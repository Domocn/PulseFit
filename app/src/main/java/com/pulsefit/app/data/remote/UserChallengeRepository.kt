package com.pulsefit.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pulsefit.app.data.model.ChallengeDefinition
import com.pulsefit.app.data.model.ChallengeMetric
import com.pulsefit.app.data.remote.model.ChallengeStatus
import com.pulsefit.app.data.remote.model.UserChallenge
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing individual user challenge instances in Firestore.
 * Stub — core CRUD operations; progression logic to be wired later.
 */
@Singleton
class UserChallengeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun challengesRef() =
        auth.currentUser?.uid?.let {
            firestore.collection("users").document(it).collection("challenges")
        }

    /**
     * Start a new challenge for the current user.
     */
    suspend fun startChallenge(definition: ChallengeDefinition): String? {
        val ref = challengesRef() ?: return null
        val docRef = ref.document()
        val now = System.currentTimeMillis()
        val endDate = now + (definition.durationDays.toLong() * 24 * 60 * 60 * 1000)

        val challenge = UserChallenge(
            id = docRef.id,
            challengeType = definition.type,
            name = definition.name,
            startDate = now,
            endDate = endDate,
            metric = definition.metric,
            goalTarget = definition.defaultGoal,
            status = ChallengeStatus.ACTIVE
        )
        docRef.set(challenge).await()
        return docRef.id
    }

    /**
     * Get all active challenges for the current user.
     */
    fun getActiveChallenges(): Flow<List<UserChallenge>> = callbackFlow {
        val ref = challengesRef()
        if (ref == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = ref
            .whereEqualTo("status", ChallengeStatus.ACTIVE.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val challenges = snapshot?.documents?.mapNotNull {
                    it.toObject(UserChallenge::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(challenges)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Get all challenges (active + completed) for history.
     */
    fun getAllChallenges(): Flow<List<UserChallenge>> = callbackFlow {
        val ref = challengesRef()
        if (ref == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = ref
            .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val challenges = snapshot?.documents?.mapNotNull {
                    it.toObject(UserChallenge::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(challenges)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Record progress toward active challenges matching the given metric.
     * Called after each workout completes.
     */
    suspend fun recordProgress(metric: ChallengeMetric, amount: Int) {
        val ref = challengesRef() ?: return
        try {
            val snapshot = ref
                .whereEqualTo("status", ChallengeStatus.ACTIVE.name)
                .whereEqualTo("metric", metric.name)
                .get().await()

            for (doc in snapshot.documents) {
                val challenge = doc.toObject(UserChallenge::class.java) ?: continue
                val newProgress = challenge.currentProgress + amount
                val updates = mutableMapOf<String, Any>("currentProgress" to newProgress)

                if (newProgress >= challenge.goalTarget) {
                    updates["status"] = ChallengeStatus.COMPLETED.name
                    updates["completedAt"] = System.currentTimeMillis()
                    updates["badgeAwarded"] = true
                    updates["xpAwarded"] = challenge.goalTarget // Placeholder XP
                }
                doc.reference.update(updates).await()
            }
        } catch (_: Exception) {
            // Non-critical — challenge progress sync can fail silently
        }
    }

    /**
     * Abandon a challenge.
     */
    suspend fun abandonChallenge(challengeId: String) {
        val ref = challengesRef() ?: return
        try {
            ref.document(challengeId)
                .update("status", ChallengeStatus.ABANDONED.name)
                .await()
        } catch (_: Exception) {
            // Non-critical
        }
    }

    /**
     * Store calendar event ID for syncing.
     */
    suspend fun setCalendarEventId(challengeId: String, calendarEventId: String) {
        val ref = challengesRef() ?: return
        try {
            ref.document(challengeId)
                .update("calendarEventId", calendarEventId)
                .await()
        } catch (_: Exception) {
            // Non-critical
        }
    }
}
