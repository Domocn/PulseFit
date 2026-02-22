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
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val groupsCollection = firestore.collection("groups")
    private val uid: String? get() = auth.currentUser?.uid

    suspend fun createGroup(name: String, type: GroupType, description: String): String {
        val currentUid = uid ?: throw IllegalStateException("Not authenticated")

        // Input validation
        val trimmedName = name.trim().replace(Regex("\\s+"), " ")
        val trimmedDesc = description.trim()
        require(trimmedName.length in 1..50) { "Group name must be 1-50 characters" }
        require(trimmedDesc.length <= 500) { "Description must be under 500 characters" }

        val inviteCode = generateInviteCode()
        val maxMembers = if (type == GroupType.FAMILY) 10 else 50
        val docRef = groupsCollection.document()
        val group = WorkoutGroup(
            id = docRef.id,
            name = trimmedName,
            type = type,
            adminUid = currentUid,
            inviteCode = inviteCode,
            createdAt = System.currentTimeMillis(),
            memberCount = 1,
            maxMembers = maxMembers,
            description = trimmedDesc,
            memberUids = listOf(currentUid)
        )
        docRef.set(group).await()

        // Add creator as admin member
        val member = GroupMember(
            uid = currentUid,
            displayName = auth.currentUser?.displayName ?: "You",
            photoUrl = auth.currentUser?.photoUrl?.toString(),
            role = GroupRole.ADMIN,
            joinedAt = System.currentTimeMillis()
        )
        docRef.collection("members").document(currentUid).set(member).await()
        return docRef.id
    }

    fun getMyGroups(): Flow<List<WorkoutGroup>> = callbackFlow {
        val currentUid = uid
        if (currentUid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = groupsCollection
            .whereArrayContains("memberUids", currentUid)
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
        val currentUid = uid ?: return null
        val snapshot = groupsCollection
            .whereEqualTo("inviteCode", code.uppercase().trim())
            .limit(1)
            .get().await()

        val doc = snapshot.documents.firstOrNull() ?: return null
        val group = doc.toObject(WorkoutGroup::class.java) ?: return null

        if (group.memberCount >= group.maxMembers) return null

        val member = GroupMember(
            uid = currentUid,
            displayName = auth.currentUser?.displayName ?: "Member",
            photoUrl = auth.currentUser?.photoUrl?.toString(),
            role = GroupRole.MEMBER,
            joinedAt = System.currentTimeMillis()
        )
        doc.reference.collection("members").document(currentUid).set(member).await()
        doc.reference.update(
            mapOf(
                "memberCount" to group.memberCount + 1,
                "memberUids" to (group.memberUids + currentUid)
            )
        ).await()
        return doc.id
    }

    suspend fun leaveGroup(groupId: String) {
        val currentUid = uid ?: return
        val docRef = groupsCollection.document(groupId)
        docRef.collection("members").document(currentUid).delete().await()
        val group = docRef.get().await().toObject(WorkoutGroup::class.java) ?: return
        docRef.update(
            mapOf(
                "memberCount" to (group.memberCount - 1).coerceAtLeast(0),
                "memberUids" to group.memberUids.filter { it != currentUid }
            )
        ).await()
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
        val currentUid = uid ?: return
        val docRef = groupsCollection.document(groupId)
        val group = docRef.get().await().toObject(WorkoutGroup::class.java) ?: return

        // Only admin can remove members
        if (group.adminUid != currentUid) return

        docRef.collection("members").document(memberUid).delete().await()
        docRef.update(
            mapOf(
                "memberCount" to (group.memberCount - 1).coerceAtLeast(0),
                "memberUids" to group.memberUids.filter { it != memberUid }
            )
        ).await()
    }

    suspend fun deleteGroup(groupId: String) {
        val currentUid = uid ?: return
        val group = getGroupById(groupId) ?: return

        // Only admin can delete group
        if (group.adminUid != currentUid) return

        groupsCollection.document(groupId).delete().await()
    }

    suspend fun getGroupById(groupId: String): WorkoutGroup? {
        return try {
            val doc = groupsCollection.document(groupId).get().await()
            doc.toObject(WorkoutGroup::class.java)?.copy(id = doc.id)
        } catch (_: Exception) {
            null
        }
    }

    fun isCurrentUserAdmin(group: WorkoutGroup): Boolean = group.adminUid == uid

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val random = SecureRandom()
        return (1..8).map { chars[random.nextInt(chars.length)] }.joinToString("")
    }
}
