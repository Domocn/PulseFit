package com.pulsefit.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuddyMatchingRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val requests = firestore.collection("buddy_requests")
    private val matches = firestore.collection("buddy_matches")

    private val currentUid: String?
        get() = firebaseAuth.currentUser?.uid

    data class BuddyRequest(
        val uid: String = "",
        val ndProfile: String = "",
        val preferredTimes: List<String> = emptyList(),
        val parallelPlayOnly: Boolean = false,
        val createdAt: Long = 0
    )

    data class BuddyMatch(
        val matchId: String = "",
        val partnerUid: String = "",
        val partnerName: String = "",
        val matchedAt: Long = 0
    )

    suspend fun submitRequest(ndProfile: String, preferredTimes: List<String>, parallelPlayOnly: Boolean) {
        val uid = currentUid ?: return
        val data = mapOf(
            "uid" to uid,
            "ndProfile" to ndProfile,
            "preferredTimes" to preferredTimes,
            "parallelPlayOnly" to parallelPlayOnly,
            "createdAt" to System.currentTimeMillis()
        )
        requests.document(uid).set(data).await()
    }

    suspend fun cancelRequest() {
        val uid = currentUid ?: return
        requests.document(uid).delete().await()
    }

    fun getMatches(): Flow<List<BuddyMatch>> = callbackFlow {
        val uid = currentUid
        if (uid == null) { trySend(emptyList()); close(); return@callbackFlow }

        val listener = matches.whereArrayContains("participants", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(emptyList()); return@addSnapshotListener }
                val result = snapshot?.documents?.mapNotNull { doc ->
                    val participants = doc.get("participants") as? List<*> ?: return@mapNotNull null
                    val partnerUid = participants.firstOrNull { it != uid } as? String ?: return@mapNotNull null
                    BuddyMatch(
                        matchId = doc.id,
                        partnerUid = partnerUid,
                        partnerName = doc.getString("partnerName_$partnerUid") ?: "Workout Buddy",
                        matchedAt = doc.getLong("matchedAt") ?: 0
                    )
                } ?: emptyList()
                trySend(result)
            }
        awaitClose { listener.remove() }
    }

    suspend fun hasActiveRequest(): Boolean {
        val uid = currentUid ?: return false
        val doc = requests.document(uid).get().await()
        return doc.exists()
    }
}
