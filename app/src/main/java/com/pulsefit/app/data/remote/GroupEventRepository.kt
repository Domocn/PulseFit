package com.pulsefit.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pulsefit.app.data.remote.model.GroupEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupEventRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun eventsCollection(groupId: String) =
        firestore.collection("groups").document(groupId).collection("events")

    suspend fun createEvent(groupId: String, event: GroupEvent): String {
        val uid = auth.currentUser?.uid ?: ""
        val docRef = eventsCollection(groupId).document()
        val newEvent = event.copy(id = docRef.id, createdByUid = uid)
        docRef.set(newEvent).await()
        return docRef.id
    }

    fun getUpcomingEvents(groupId: String): Flow<List<GroupEvent>> = callbackFlow {
        val listener = eventsCollection(groupId)
            .whereIn("status", listOf("upcoming", "active"))
            .orderBy("scheduledAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val events = snapshot?.documents?.mapNotNull {
                    it.toObject(GroupEvent::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(events)
            }
        awaitClose { listener.remove() }
    }

    suspend fun rsvpEvent(groupId: String, eventId: String) {
        val uid = auth.currentUser?.uid ?: return
        // Verify user is a group member
        val memberDoc = firestore.collection("groups")
            .document(groupId).collection("members")
            .document(uid).get().await()
        if (!memberDoc.exists()) return

        val docRef = eventsCollection(groupId).document(eventId)
        val event = docRef.get().await().toObject(GroupEvent::class.java) ?: return
        if (uid !in event.rsvpList) {
            docRef.update("rsvpList", event.rsvpList + uid).await()
        }
    }

    suspend fun unrsvpEvent(groupId: String, eventId: String) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = eventsCollection(groupId).document(eventId)
        val event = docRef.get().await().toObject(GroupEvent::class.java) ?: return
        docRef.update("rsvpList", event.rsvpList - uid).await()
    }
}
