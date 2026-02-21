package com.pulsefit.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pulsefit.app.data.remote.model.AccountabilityContract
import com.pulsefit.app.data.remote.model.WeeklyProgress
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountabilityContractRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val contractsCollection = firestore.collection("accountabilityContracts")

    private val currentUid: String?
        get() = firebaseAuth.currentUser?.uid

    private fun currentWeekKey(): String {
        val monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return monday.toString()
    }

    private fun currentWeekStartMillis(): Long {
        val monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return monday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    suspend fun createContract(friendUid: String, friendName: String, weeklyGoal: Int) {
        val uid = currentUid ?: return
        val myName = firebaseAuth.currentUser?.displayName ?: "User"
        contractsCollection.add(
            mapOf(
                "participants" to listOf(uid, friendUid),
                "participantNames" to mapOf(uid to myName, friendUid to friendName),
                "weeklyGoal" to weeklyGoal,
                "createdAt" to System.currentTimeMillis(),
                "status" to "active"
            )
        ).await()
    }

    fun getActiveContracts(): Flow<List<AccountabilityContract>> = callbackFlow {
        val uid = currentUid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = contractsCollection
            .whereArrayContains("participants", uid)
            .whereEqualTo("status", "active")
            .addSnapshotListener { snapshot, _ ->
                val contracts = snapshot?.documents?.mapNotNull { doc ->
                    val participants = (doc.get("participants") as? List<*>)?.filterIsInstance<String>() ?: return@mapNotNull null
                    val partnerUid = participants.firstOrNull { it != uid } ?: return@mapNotNull null
                    val names = doc.get("participantNames") as? Map<*, *>
                    val partnerName = names?.get(partnerUid)?.toString() ?: "Partner"
                    AccountabilityContract(
                        id = doc.id,
                        participants = participants,
                        weeklyGoal = (doc.getLong("weeklyGoal") ?: 3).toInt(),
                        createdAt = doc.getLong("createdAt") ?: 0,
                        status = doc.getString("status") ?: "active",
                        partnerName = partnerName
                    )
                } ?: emptyList()
                trySend(contracts)
            }
        awaitClose { listener.remove() }
    }

    fun getWeeklyProgress(contractId: String): Flow<WeeklyProgress> = callbackFlow {
        val uid = currentUid ?: run {
            trySend(WeeklyProgress())
            close()
            return@callbackFlow
        }
        val weekKey = currentWeekKey()
        val progressRef = contractsCollection.document(contractId)
            .collection("weekly").document(weekKey)

        val listener = progressRef.addSnapshotListener { doc, _ ->
            if (doc != null && doc.exists()) {
                val contractDoc = doc
                val myCount = (contractDoc.getLong("${uid}_count") ?: 0).toInt()
                // Find partner count
                val allFields = contractDoc.data ?: emptyMap()
                val partnerCount = allFields.entries
                    .firstOrNull { it.key.endsWith("_count") && !it.key.startsWith(uid) }
                    ?.let { (it.value as? Long)?.toInt() ?: 0 } ?: 0
                val goal = (contractDoc.getLong("weeklyGoal") ?: 3).toInt()
                trySend(WeeklyProgress(myCount, partnerCount, goal, currentWeekStartMillis()))
            } else {
                trySend(WeeklyProgress(weekStart = currentWeekStartMillis()))
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun recordWorkout(contractId: String) {
        val uid = currentUid ?: return
        val weekKey = currentWeekKey()
        val progressRef = contractsCollection.document(contractId)
            .collection("weekly").document(weekKey)

        firestore.runTransaction { transaction ->
            val doc = transaction.get(progressRef)
            val currentCount = (doc.getLong("${uid}_count") ?: 0) + 1
            val data = mutableMapOf<String, Any>(
                "${uid}_count" to currentCount,
                "weekStart" to currentWeekStartMillis()
            )
            // Get contract goal
            val contractRef = contractsCollection.document(contractId)
            val contractDoc = transaction.get(contractRef)
            data["weeklyGoal"] = contractDoc.getLong("weeklyGoal") ?: 3
            transaction.set(progressRef, data, com.google.firebase.firestore.SetOptions.merge())
        }.await()
    }

    suspend fun cancelContract(contractId: String) {
        contractsCollection.document(contractId)
            .update("status", "cancelled")
            .await()
    }

    suspend fun getActiveContractIds(): List<String> {
        val uid = currentUid ?: return emptyList()
        val snapshot = contractsCollection
            .whereArrayContains("participants", uid)
            .whereEqualTo("status", "active")
            .get()
            .await()
        return snapshot.documents.map { it.id }
    }
}
