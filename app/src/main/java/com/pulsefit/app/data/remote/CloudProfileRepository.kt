package com.pulsefit.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.data.model.TreadMode
import com.pulsefit.app.domain.model.UserProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class CloudProfile(
    val displayName: String = "",
    val photoUrl: String? = null,
    val xpLevel: Int = 1,
    val totalXp: Long = 0,
    val totalWorkouts: Int = 0,
    val totalBurnPoints: Long = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val weeklyBurnPoints: Int = 0,
    val lastWorkoutAt: Long? = null,
    val profileVisibility: String = "friends",
    val createdAt: Long = System.currentTimeMillis(),
    // Personal profile fields
    val name: String = "",
    val age: Int = 25,
    val maxHeartRate: Int = 195,
    val weight: Float? = null,
    val height: Float? = null,
    val restingHeartRate: Int? = null,
    val biologicalSex: String = "male",
    val ndProfile: String = "STANDARD",
    val dailyTarget: Int = 12,
    val units: String = "metric",
    val customZoneThresholds: String? = null,
    val treadMode: String = "RUNNER",
    val equipmentProfileJson: String? = null,
    val onboardingComplete: Boolean = false
) {
    companion object {
        fun fromUserProfile(profile: UserProfile): CloudProfile = CloudProfile(
            displayName = profile.displayName ?: profile.name,
            photoUrl = profile.photoUrl,
            xpLevel = profile.xpLevel,
            totalXp = profile.totalXp,
            totalWorkouts = profile.totalWorkouts,
            totalBurnPoints = profile.totalBurnPoints,
            currentStreak = profile.currentStreak,
            longestStreak = profile.longestStreak,
            weeklyBurnPoints = 0,
            lastWorkoutAt = profile.lastWorkoutAt,
            profileVisibility = profile.profileVisibility,
            createdAt = profile.createdAt,
            name = profile.name,
            age = profile.age,
            maxHeartRate = profile.maxHeartRate,
            weight = profile.weight,
            height = profile.height,
            restingHeartRate = profile.restingHeartRate,
            biologicalSex = profile.biologicalSex,
            ndProfile = profile.ndProfile.name,
            dailyTarget = profile.dailyTarget,
            units = profile.units,
            customZoneThresholds = profile.customZoneThresholds,
            treadMode = profile.treadMode.name,
            equipmentProfileJson = profile.equipmentProfileJson,
            onboardingComplete = profile.onboardingComplete
        )

        fun CloudProfile.toDomainProfile(firebaseUid: String?): UserProfile = UserProfile(
            name = name,
            age = age,
            maxHeartRate = maxHeartRate,
            weight = weight,
            height = height,
            restingHeartRate = restingHeartRate,
            biologicalSex = biologicalSex,
            ndProfile = try { NdProfile.valueOf(ndProfile) } catch (_: Exception) { NdProfile.STANDARD },
            dailyTarget = dailyTarget,
            onboardingComplete = onboardingComplete,
            xpLevel = xpLevel,
            totalXp = totalXp,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalBurnPoints = totalBurnPoints,
            totalWorkouts = totalWorkouts,
            createdAt = createdAt,
            lastWorkoutAt = lastWorkoutAt,
            customZoneThresholds = customZoneThresholds,
            units = units,
            displayName = displayName,
            photoUrl = photoUrl,
            profileVisibility = profileVisibility,
            firebaseUid = firebaseUid,
            treadMode = try { TreadMode.valueOf(treadMode) } catch (_: Exception) { TreadMode.RUNNER },
            equipmentProfileJson = equipmentProfileJson
        )
    }
}

data class PublicProfile(
    val uid: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val xpLevel: Int = 1,
    val totalXp: Long = 0,
    val totalWorkouts: Int = 0,
    val weeklyBurnPoints: Int = 0,
    val currentStreak: Int = 0
)

data class SharedWorkout(
    val id: String = "",
    val uid: String = "",
    val displayName: String = "",
    val durationSeconds: Int = 0,
    val burnPoints: Int = 0,
    val averageHeartRate: Int = 0,
    val maxHeartRate: Int = 0,
    val xpEarned: Int = 0,
    val zoneTimeSummary: Map<String, Long> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis(),
    val visibility: String = "friends"
)

@Singleton
class CloudProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val usersCollection = firestore.collection("users")
    private val sharedWorkoutsCollection = firestore.collection("sharedWorkouts")

    private val currentUid: String?
        get() = firebaseAuth.currentUser?.uid

    suspend fun pushProfile(profile: CloudProfile) {
        val uid = currentUid ?: return
        usersCollection.document(uid)
            .set(profile, SetOptions.merge())
            .await()
    }

    suspend fun pullProfile(): CloudProfile? {
        val uid = currentUid ?: return null
        val doc = usersCollection.document(uid).get().await()
        return if (doc.exists()) doc.toObject(CloudProfile::class.java) else null
    }

    suspend fun syncAfterWorkout(
        displayName: String,
        xpLevel: Int,
        totalXp: Long,
        totalWorkouts: Int,
        totalBurnPoints: Long,
        currentStreak: Int,
        longestStreak: Int,
        weeklyBurnPoints: Int,
        lastWorkoutAt: Long
    ) {
        val uid = currentUid ?: return
        val updates = mapOf(
            "displayName" to displayName,
            "xpLevel" to xpLevel,
            "totalXp" to totalXp,
            "totalWorkouts" to totalWorkouts,
            "totalBurnPoints" to totalBurnPoints,
            "currentStreak" to currentStreak,
            "longestStreak" to longestStreak,
            "weeklyBurnPoints" to weeklyBurnPoints,
            "lastWorkoutAt" to lastWorkoutAt
        )
        usersCollection.document(uid)
            .set(updates, SetOptions.merge())
            .await()
    }

    suspend fun shareWorkout(workout: SharedWorkout) {
        val uid = currentUid ?: return
        val data = workout.copy(uid = uid)
        sharedWorkoutsCollection.add(data).await()
    }

    suspend fun getSharedWorkouts(friendUids: List<String>, limit: Int = 50): List<SharedWorkout> {
        if (friendUids.isEmpty()) return emptyList()
        // Firestore 'in' queries limited to 30 elements
        val chunks = friendUids.chunked(30)
        val results = mutableListOf<SharedWorkout>()
        for (chunk in chunks) {
            val snapshot = sharedWorkoutsCollection
                .whereIn("uid", chunk)
                .whereEqualTo("visibility", "friends")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            results.addAll(snapshot.documents.mapNotNull { doc ->
                doc.toObject(SharedWorkout::class.java)?.copy(id = doc.id)
            })
        }
        return results.sortedByDescending { it.timestamp }.take(limit)
    }

    suspend fun getPublicProfile(uid: String): PublicProfile? {
        val doc = usersCollection.document(uid).get().await()
        if (!doc.exists()) return null
        val data = doc.data ?: return null
        return PublicProfile(
            uid = uid,
            displayName = data["displayName"] as? String ?: "",
            photoUrl = data["photoUrl"] as? String,
            xpLevel = (data["xpLevel"] as? Long)?.toInt() ?: 1,
            totalXp = data["totalXp"] as? Long ?: 0,
            totalWorkouts = (data["totalWorkouts"] as? Long)?.toInt() ?: 0,
            weeklyBurnPoints = (data["weeklyBurnPoints"] as? Long)?.toInt() ?: 0,
            currentStreak = (data["currentStreak"] as? Long)?.toInt() ?: 0
        )
    }
}
