package com.pulsefit.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class FriendRequest(
    val id: String = "",
    val fromUid: String = "",
    val toUid: String = "",
    val fromDisplayName: String = "",
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis()
)

data class FriendEntry(
    val uid: String = "",
    val displayName: String = "",
    val addedAt: Long = System.currentTimeMillis(),
    val photoUrl: String? = null
)

@Singleton
class FriendsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val cloudProfileRepository: CloudProfileRepository
) {
    private val friendRequestsCollection = firestore.collection("friendRequests")

    private val currentUid: String?
        get() = firebaseAuth.currentUser?.uid

    private fun friendsCollection(uid: String) =
        firestore.collection("users").document(uid).collection("friends")

    suspend fun searchUsers(query: String): List<PublicProfile> {
        if (query.length < 4) return emptyList()
        val snapshot = firestore.collection("users")
            .orderBy("displayName")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .limit(20)
            .get()
            .await()

        val uid = currentUid ?: return emptyList()
        return snapshot.documents
            .filter { it.id != uid }
            .mapNotNull { doc ->
                val visibility = doc.getString("profileVisibility") ?: "friends"
                if (visibility == "private") return@mapNotNull null
                PublicProfile(
                    uid = doc.id,
                    displayName = doc.getString("displayName") ?: "",
                    photoUrl = doc.getString("photoUrl"),
                    xpLevel = (doc.getLong("xpLevel") ?: 1).toInt(),
                    totalXp = doc.getLong("totalXp") ?: 0,
                    totalWorkouts = (doc.getLong("totalWorkouts") ?: 0).toInt(),
                    weeklyBurnPoints = (doc.getLong("weeklyBurnPoints") ?: 0).toInt(),
                    currentStreak = (doc.getLong("currentStreak") ?: 0).toInt()
                )
            }
    }

    suspend fun sendFriendRequest(toUid: String) {
        val uid = currentUid ?: return
        // Check for existing pending request
        val existing = friendRequestsCollection
            .whereEqualTo("fromUid", uid)
            .whereEqualTo("toUid", toUid)
            .whereEqualTo("status", "pending")
            .limit(1)
            .get().await()
        if (!existing.isEmpty) return // Already sent

        val displayName = firebaseAuth.currentUser?.displayName ?: "User"
        val request = FriendRequest(
            fromUid = uid,
            toUid = toUid,
            fromDisplayName = displayName
        )
        friendRequestsCollection.add(request).await()
    }

    suspend fun acceptFriendRequest(requestId: String) {
        val uid = currentUid ?: return
        val doc = friendRequestsCollection.document(requestId).get().await()
        val fromUid = doc.getString("fromUid") ?: return
        val fromName = doc.getString("fromDisplayName") ?: ""

        // Update request status
        friendRequestsCollection.document(requestId)
            .update("status", "accepted")
            .await()

        // Add friend to both users
        val myName = firebaseAuth.currentUser?.displayName ?: "User"
        val myPhoto = firebaseAuth.currentUser?.photoUrl?.toString()

        friendsCollection(uid).document(fromUid)
            .set(FriendEntry(uid = fromUid, displayName = fromName))
            .await()

        friendsCollection(fromUid).document(uid)
            .set(FriendEntry(uid = uid, displayName = myName, photoUrl = myPhoto))
            .await()
    }

    suspend fun declineFriendRequest(requestId: String) {
        friendRequestsCollection.document(requestId)
            .update("status", "declined")
            .await()
    }

    fun getPendingRequests(): Flow<List<FriendRequest>> = callbackFlow {
        val uid = currentUid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = friendRequestsCollection
            .whereEqualTo("toUid", uid)
            .whereEqualTo("status", "pending")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val requests = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FriendRequest::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(requests)
            }
        awaitClose { listener.remove() }
    }

    fun getFriends(): Flow<List<PublicProfile>> = callbackFlow {
        val uid = currentUid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = friendsCollection(uid)
            .addSnapshotListener { snapshot, _ ->
                val friends = snapshot?.documents?.mapNotNull { doc ->
                    val entry = doc.toObject(FriendEntry::class.java)
                    entry?.let {
                        PublicProfile(
                            uid = it.uid,
                            displayName = it.displayName,
                            photoUrl = it.photoUrl
                        )
                    }
                } ?: emptyList()
                trySend(friends)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getFriendUids(): List<String> {
        val uid = currentUid ?: return emptyList()
        val snapshot = friendsCollection(uid).get().await()
        return snapshot.documents.map { it.id }
    }

    suspend fun removeFriend(friendUid: String) {
        val uid = currentUid ?: return
        friendsCollection(uid).document(friendUid).delete().await()
        friendsCollection(friendUid).document(uid).delete().await()
    }
}
