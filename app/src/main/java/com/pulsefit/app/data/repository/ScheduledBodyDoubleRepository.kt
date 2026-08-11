package com.pulsefit.app.data.repository

import com.pulsefit.app.data.local.dao.ScheduledBodyDoubleDao
import com.pulsefit.app.data.local.entity.ScheduledBodyDoubleEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduledBodyDoubleRepository @Inject constructor(
    private val dao: ScheduledBodyDoubleDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val collection = firestore.collection("scheduledBodyDoubles")

    private val currentUid: String?
        get() = firebaseAuth.currentUser?.uid

    fun getUpcoming(): Flow<List<ScheduledBodyDoubleEntity>> =
        dao.getUpcoming(System.currentTimeMillis())

    suspend fun createSession(title: String, scheduledAt: Long, durationMinutes: Int): Long {
        val uid = currentUid ?: return -1
        val doc = collection.document()
        val data = mapOf(
            "title" to title,
            "scheduledAt" to scheduledAt,
            "durationMinutes" to durationMinutes,
            "createdByUid" to uid,
            "participants" to listOf(uid)
        )
        doc.set(data).await()

        val entity = ScheduledBodyDoubleEntity(
            firestoreSessionId = doc.id,
            scheduledAt = scheduledAt,
            durationMinutes = durationMinutes,
            title = title,
            createdByUid = uid,
            isJoined = true
        )
        return dao.insert(entity)
    }

    suspend fun joinSession(firestoreId: String) {
        val uid = currentUid ?: return
        collection.document(firestoreId).update(
            "participants", com.google.firebase.firestore.FieldValue.arrayUnion(uid)
        ).await()
    }

    suspend fun deleteLocal(id: Long) = dao.deleteById(id)
}
