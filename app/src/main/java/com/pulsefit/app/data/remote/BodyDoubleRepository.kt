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
        val listener = collection.addSnapshotListener { snapshot, _ ->
            val count = snapshot?.documents?.count { it.id != uid } ?: 0
            trySend(count)
        }
        awaitClose { listener.remove() }
    }
}
