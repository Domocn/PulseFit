package com.pulsefit.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pulsefit.app.data.remote.model.GroupMember
import com.pulsefit.app.data.remote.model.GroupRole
import com.pulsefit.app.data.remote.model.GroupType
import com.pulsefit.app.data.remote.model.WorkoutGroup
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val groupsCollection = firestore.collection("groups")
    private val uid: String get() = auth.currentUser?.uid ?: ""

    suspend fun createGroup(name: String, type: GroupType, description: String): String {
        val inviteCode = generateInviteCode()
        val maxMembers = if (type == GroupType.FAMILY) 10 else 50
        val docRef = groupsCollection.document()
        val group = WorkoutGroup(
            id = docRef.id,
            name = name,
            type = type,
            adminUid = uid,
            inviteCode = inviteCode,
            createdAt = System.currentTimeMillis(),
            memberCount = 1,
            maxMembers = maxMembers,
            description = description
        )
        docRef.set(group).await()

        // Add creator as admin member
        val member = GroupMember(
            uid = uid,
            displayName = auth.currentUser?.displayName ?: "You",
            photoUrl = auth.currentUser?.photoUrl?.toString(),
            role = GroupRole.ADMIN,
            joinedAt = System.currentTimeMillis()
        )
        docRef.collection("members").document(uid).set(member).await()
        return docRef.id
    }

    fun getMyGroups(): Flow<List<WorkoutGroup>> = callbackFlow {
        val listener = groupsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val groups = snapshot?.documents?.mapNotNull {
                    it.toObject(WorkoutGroup::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(groups)
            }
        awaitClose { listener.remove() }
    }

    suspend fun joinGroupByCode(code: String): String? {
        val snapshot = groupsCollection
            .whereEqualTo("inviteCode", code)
            .limit(1)
            .get().await()

        val doc = snapshot.documents.firstOrNull() ?: return null
        val group = doc.toObject(WorkoutGroup::class.java) ?: return null

        if (group.memberCount >= group.maxMembers) return null

        val member = GroupMember(
            uid = uid,
            displayName = auth.currentUser?.displayName ?: "Member",
            photoUrl = auth.currentUser?.photoUrl?.toString(),
            role = GroupRole.MEMBER,
            joinedAt = System.currentTimeMillis()
        )
        doc.reference.collection("members").document(uid).set(member).await()
        doc.reference.update("memberCount", group.memberCount + 1).await()
        return doc.id
    }

    suspend fun leaveGroup(groupId: String) {
        val docRef = groupsCollection.document(groupId)
        docRef.collection("members").document(uid).delete().await()
        val group = docRef.get().await().toObject(WorkoutGroup::class.java) ?: return
        docRef.update("memberCount", (group.memberCount - 1).coerceAtLeast(0)).await()
    }

    fun getMembers(groupId: String): Flow<List<GroupMember>> = callbackFlow {
        val listener = groupsCollection.document(groupId).collection("members")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val members = snapshot?.documents?.mapNotNull {
                    it.toObject(GroupMember::class.java)
                } ?: emptyList()
                trySend(members)
            }
        awaitClose { listener.remove() }
    }

    suspend fun removeMember(groupId: String, memberUid: String) {
        val docRef = groupsCollection.document(groupId)
        docRef.collection("members").document(memberUid).delete().await()
        val group = docRef.get().await().toObject(WorkoutGroup::class.java) ?: return
        docRef.update("memberCount", (group.memberCount - 1).coerceAtLeast(0)).await()
    }

    suspend fun deleteGroup(groupId: String) {
        groupsCollection.document(groupId).delete().await()
    }

    suspend fun getGroupById(groupId: String): WorkoutGroup? {
        val doc = groupsCollection.document(groupId).get().await()
        return doc.toObject(WorkoutGroup::class.java)?.copy(id = doc.id)
    }

    fun isCurrentUserAdmin(group: WorkoutGroup): Boolean = group.adminUid == uid

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
}
