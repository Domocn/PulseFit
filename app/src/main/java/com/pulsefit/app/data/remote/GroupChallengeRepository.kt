package com.pulsefit.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pulsefit.app.data.remote.model.GroupChallenge
import com.pulsefit.app.data.remote.model.GroupWeeklyStats
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupChallengeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun challengesCollection(groupId: String) =
        firestore.collection("groups").document(groupId).collection("challenges")

    private fun statsDoc(groupId: String) =
        firestore.collection("groups").document(groupId).collection("stats").document("weekly")

    suspend fun createChallenge(groupId: String, challenge: GroupChallenge): String {
        val docRef = challengesCollection(groupId).document()
        val newChallenge = challenge.copy(id = docRef.id)
        docRef.set(newChallenge).await()
        return docRef.id
    }

    fun getActiveChallenges(groupId: String): Flow<List<GroupChallenge>> = callbackFlow {
        val listener = challengesCollection(groupId)
            .whereEqualTo("status", "active")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val challenges = snapshot?.documents?.mapNotNull {
                    it.toObject(GroupChallenge::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(challenges)
            }
        awaitClose { listener.remove() }
    }

    suspend fun recordWorkoutForGroup(groupId: String) {
        try {
            // Increment active challenges progress
            val snapshot = challengesCollection(groupId)
                .whereEqualTo("status", "active")
                .get().await()
            for (doc in snapshot.documents) {
                val challenge = doc.toObject(GroupChallenge::class.java) ?: continue
                val newProgress = challenge.currentProgress + 1
                val updates = mutableMapOf<String, Any>("currentProgress" to newProgress)
                if (newProgress >= challenge.goalTarget) {
                    updates["status"] = "completed"
                }
                doc.reference.update(updates).await()
            }

            // Update weekly stats
            val ref = statsDoc(groupId)
            try {
                ref.update("totalWorkouts", FieldValue.increment(1)).await()
            } catch (_: Exception) {
                // Stats doc may not exist yet -- create it
                ref.set(GroupWeeklyStats(totalWorkouts = 1)).await()
            }
        } catch (_: Exception) {
            // Silently fail - group challenge sync is non-critical
        }
    }

    fun getWeeklyStats(groupId: String): Flow<GroupWeeklyStats?> = callbackFlow {
        val listener = statsDoc(groupId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val stats = snapshot?.toObject(GroupWeeklyStats::class.java)
                trySend(stats)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getActiveGroupIds(): List<String> {
        val currentUid = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = firestore.collection("groups")
                .whereArrayContains("memberUids", currentUid)
                .get().await()
            snapshot.documents.map { it.id }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
