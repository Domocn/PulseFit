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
class BodyDoubleRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val collection = firestore.collection("activeSessions")

    private val currentUid: String?
        get() = firebaseAuth.currentUser?.uid

    suspend fun joinSession(zone: String = "REST") {
        val uid = currentUid ?: return
        collection.document(uid).set(
            mapOf(
                "uid" to uid,
                "startedAt" to System.currentTimeMillis(),
                "zone" to zone
            )
        ).await()
    }

    suspend fun leaveSession() {
        val uid = currentUid ?: return
        collection.document(uid).delete().await()
    }

    suspend fun updateZone(zone: String) {
        val uid = currentUid ?: return
        collection.document(uid).update("zone", zone).await()
    }

    fun getActiveCount(): Flow<Int> = callbackFlow {
        val uid = currentUid
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(0)
                return@addSnapshotListener
            }
            val count = snapshot?.documents?.count { it.id != uid } ?: 0
            trySend(count)
        }
        awaitClose { listener.remove() }
    }

    private val scheduledCollection = firestore.collection("scheduledBodyDoubles")

    suspend fun createScheduledSession(title: String, scheduledAt: Long, durationMinutes: Int): String? {
        val uid = currentUid ?: return null
        val doc = scheduledCollection.document()
        doc.set(
            mapOf(
                "title" to title,
                "scheduledAt" to scheduledAt,
                "durationMinutes" to durationMinutes,
                "createdByUid" to uid,
                "participants" to listOf(uid)
            )
        ).await()
        return doc.id
    }

    suspend fun joinScheduledSession(sessionId: String) {
        val uid = currentUid ?: return
        scheduledCollection.document(sessionId).update(
            "participants", com.google.firebase.firestore.FieldValue.arrayUnion(uid)
        ).await()
    }

    fun getScheduledSessions(): Flow<List<Map<String, Any>>> = callbackFlow {
        val listener = scheduledCollection
            .whereGreaterThan("scheduledAt", System.currentTimeMillis())
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(emptyList()); return@addSnapshotListener }
                val sessions = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.plus("id" to doc.id)
                } ?: emptyList()
                trySend(sessions)
            }
        awaitClose { listener.remove() }
    }
}
