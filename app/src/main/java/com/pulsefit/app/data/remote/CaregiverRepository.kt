package com.pulsefit.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaregiverRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val links = firestore.collection("caregiver_links")
    private val sharedReadiness = firestore.collection("shared_readiness")

    private val currentUid: String?
        get() = firebaseAuth.currentUser?.uid

    data class CaregiverLink(
        val linkId: String = "",
        val caregiverUid: String = "",
        val caregiverName: String = "",
        val linkedAt: Long = 0
    )

    data class SharedReadiness(
        val readinessScore: Int = 0,
        val streakDays: Int = 0,
        val lastWorkoutAt: Long = 0,
        val updatedAt: Long = 0
    )

    suspend fun generateLinkCode(): String {
        val uid = currentUid ?: return ""
        val code = buildString {
            val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
            val random = SecureRandom()
            repeat(8) { append(chars[random.nextInt(chars.length)]) }
        }
        links.document(code).set(
            mapOf("athleteUid" to uid, "code" to code, "createdAt" to System.currentTimeMillis(), "claimed" to false)
        ).await()
        return code
    }

    suspend fun claimLinkCode(code: String): Boolean {
        val uid = currentUid ?: return false
        val doc = links.document(code).get().await()
        if (!doc.exists() || doc.getBoolean("claimed") == true) return false

        links.document(code).update(
            mapOf("caregiverUid" to uid, "claimed" to true, "claimedAt" to System.currentTimeMillis())
        ).await()
        return true
    }

    suspend fun shareReadiness(readinessScore: Int, streakDays: Int, lastWorkoutAt: Long) {
        val uid = currentUid ?: return
        sharedReadiness.document(uid).set(
            mapOf(
                "readinessScore" to readinessScore,
                "streakDays" to streakDays,
                "lastWorkoutAt" to lastWorkoutAt,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    fun getLinkedAthletes(): Flow<List<CaregiverLink>> = callbackFlow {
        val uid = currentUid
        if (uid == null) { trySend(emptyList()); close(); return@callbackFlow }

        val listener = links.whereEqualTo("caregiverUid", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(emptyList()); return@addSnapshotListener }
                val result = snapshot?.documents?.map { doc ->
                    CaregiverLink(
                        linkId = doc.id,
                        caregiverUid = uid,
                        caregiverName = doc.getString("athleteName") ?: "Athlete",
                        linkedAt = doc.getLong("claimedAt") ?: 0
                    )
                } ?: emptyList()
                trySend(result)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getAthleteReadiness(athleteUid: String): SharedReadiness? {
        val doc = sharedReadiness.document(athleteUid).get().await()
        if (!doc.exists()) return null
        return SharedReadiness(
            readinessScore = doc.getLong("readinessScore")?.toInt() ?: 0,
            streakDays = doc.getLong("streakDays")?.toInt() ?: 0,
            lastWorkoutAt = doc.getLong("lastWorkoutAt") ?: 0,
            updatedAt = doc.getLong("updatedAt") ?: 0
        )
    }
}
