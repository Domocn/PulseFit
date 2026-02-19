package com.example.pulsefit.ui.settings

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pulsefit.data.local.dao.NotificationPreferencesDao
import com.example.pulsefit.data.local.entity.NotificationPreferencesEntity
import com.example.pulsefit.data.model.AppTheme
import com.example.pulsefit.data.repository.SensoryPreferencesRepository
import com.example.pulsefit.domain.model.Workout
import com.example.pulsefit.domain.repository.WorkoutRepository
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.example.pulsefit.domain.usecase.SaveUserProfileUseCase
import com.example.pulsefit.util.ZoneCalculator
import com.example.pulsefit.util.ZoneThresholds
import com.example.pulsefit.worker.StreakAtRiskWorker
import com.example.pulsefit.worker.WeeklySummaryWorker
import com.example.pulsefit.worker.WorkoutReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val saveUserProfile: SaveUserProfileUseCase,
    private val sensoryPreferencesRepository: SensoryPreferencesRepository,
    private val workoutRepository: WorkoutRepository,
    private val notificationPreferencesDao: NotificationPreferencesDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _profile = MutableStateFlow<com.example.pulsefit.domain.model.UserProfile?>(null)
    val profile: StateFlow<com.example.pulsefit.domain.model.UserProfile?> = _profile

    private val _notifPrefs = MutableStateFlow(NotificationPreferencesEntity())
    val notifPrefs: StateFlow<NotificationPreferencesEntity> = _notifPrefs

    private val _useSimulatedHr = MutableStateFlow(false)
    val useSimulatedHr: StateFlow<Boolean> = _useSimulatedHr

    private val _appTheme = MutableStateFlow(AppTheme.MIDNIGHT)
    val appTheme: StateFlow<AppTheme> = _appTheme

    private val _zoneThresholds = MutableStateFlow(ZoneThresholds())
    val zoneThresholds: StateFlow<ZoneThresholds> = _zoneThresholds

    private val _snackbar = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val snackbar: SharedFlow<String> = _snackbar

    // Stash for undo delete
    private var deletedWorkouts: List<Workout>? = null

    private val workManager = WorkManager.getInstance(context)

    init {
        viewModelScope.launch {
            getUserProfile().collect { p ->
                _profile.value = p
                p?.customZoneThresholds?.let { json ->
                    ZoneCalculator.parseThresholds(json)?.let {
                        _zoneThresholds.value = it
                    }
                }
            }
        }
        viewModelScope.launch {
            sensoryPreferencesRepository.getPreferences()
                .map { it?.appTheme ?: AppTheme.MIDNIGHT }
                .collect { _appTheme.value = it }
        }
        viewModelScope.launch {
            notificationPreferencesDao.getPreferences().collect { prefs ->
                _notifPrefs.value = prefs ?: NotificationPreferencesEntity()
            }
        }
    }

    fun updateName(name: String) {
        _profile.value?.let { p -> _profile.value = p.copy(name = name) }
    }

    fun updateAge(age: String) {
        val ageInt = age.toIntOrNull() ?: return
        _profile.value?.let { p ->
            _profile.value = p.copy(age = ageInt, maxHeartRate = 220 - ageInt)
        }
    }

    fun updateWeight(weight: String) {
        _profile.value?.let { p ->
            _profile.value = p.copy(weight = weight.toFloatOrNull())
        }
    }

    fun updateHeight(height: String) {
        _profile.value?.let { p ->
            _profile.value = p.copy(height = height.toFloatOrNull())
        }
    }

    fun updateRestingHr(hr: String) {
        _profile.value?.let { p ->
            _profile.value = p.copy(restingHeartRate = hr.toIntOrNull())
        }
    }

    fun updateDailyTarget(target: Int) {
        _profile.value?.let { p -> _profile.value = p.copy(dailyTarget = target) }
    }

    fun toggleUnits() {
        _profile.value?.let { p ->
            val newUnits = if (p.units == "metric") "imperial" else "metric"
            _profile.value = p.copy(units = newUnits)
        }
    }

    fun toggleSimulatedHr() {
        _useSimulatedHr.value = !_useSimulatedHr.value
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            val current = sensoryPreferencesRepository.getPreferencesOnce()
            sensoryPreferencesRepository.save(current.copy(appTheme = theme))
        }
    }

    fun updateZoneThreshold(field: String, value: Int) {
        val t = _zoneThresholds.value
        _zoneThresholds.value = when (field) {
            "warmUp" -> t.copy(warmUp = value)
            "active" -> t.copy(active = value)
            "push" -> t.copy(push = value)
            "peak" -> t.copy(peak = value)
            else -> t
        }
    }

    fun save() {
        viewModelScope.launch {
            _profile.value?.let { p ->
                val thresholdsJson = ZoneCalculator.toJson(_zoneThresholds.value)
                saveUserProfile(p.copy(customZoneThresholds = thresholdsJson))
                _snackbar.emit("Settings saved")
            }
        }
    }

    // --- Notification scheduling ---

    fun toggleReminderEnabled() {
        viewModelScope.launch {
            val current = _notifPrefs.value
            val updated = current.copy(reminderEnabled = !current.reminderEnabled)
            notificationPreferencesDao.insertOrUpdate(updated)
            if (updated.reminderEnabled) {
                scheduleReminder(updated.reminderHour, updated.reminderMinute)
            } else {
                workManager.cancelUniqueWork("workout_reminder")
            }
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val updated = _notifPrefs.value.copy(reminderHour = hour, reminderMinute = minute)
            notificationPreferencesDao.insertOrUpdate(updated)
            if (updated.reminderEnabled) {
                scheduleReminder(hour, minute)
            }
        }
    }

    fun toggleStreakAlert() {
        viewModelScope.launch {
            val current = _notifPrefs.value
            val updated = current.copy(streakAlertEnabled = !current.streakAlertEnabled)
            notificationPreferencesDao.insertOrUpdate(updated)
            if (updated.streakAlertEnabled) {
                scheduleStreakAlert()
            } else {
                workManager.cancelUniqueWork("streak_at_risk")
            }
        }
    }

    fun toggleWeeklySummary() {
        viewModelScope.launch {
            val current = _notifPrefs.value
            val updated = current.copy(weeklySummaryEnabled = !current.weeklySummaryEnabled)
            notificationPreferencesDao.insertOrUpdate(updated)
            if (updated.weeklySummaryEnabled) {
                scheduleWeeklySummary()
            } else {
                workManager.cancelUniqueWork("weekly_summary")
            }
        }
    }

    private fun scheduleReminder(hour: Int, minute: Int) {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        var target = now.with(LocalTime.of(hour, minute))
        if (target.isBefore(now)) target = target.plusDays(1)
        val initialDelay = Duration.between(now, target).toMillis()

        val request = PeriodicWorkRequestBuilder<WorkoutReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "workout_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun scheduleStreakAlert() {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        var target = now.with(LocalTime.of(20, 0)) // 8 PM
        if (target.isBefore(now)) target = target.plusDays(1)
        val initialDelay = Duration.between(now, target).toMillis()

        val request = PeriodicWorkRequestBuilder<StreakAtRiskWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "streak_at_risk",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun scheduleWeeklySummary() {
        val request = PeriodicWorkRequestBuilder<WeeklySummaryWorker>(7, TimeUnit.DAYS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "weekly_summary",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun exportCsv() {
        viewModelScope.launch {
            try {
                val workouts = workoutRepository.getCompletedWorkouts()
                if (workouts.isEmpty()) {
                    _snackbar.emit("No workouts to export")
                    return@launch
                }

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withZone(ZoneId.systemDefault())
                val sb = StringBuilder()
                sb.appendLine("Date,Duration (min),Burn Points,Avg HR,Max HR,XP,Calories,Notes")
                workouts.forEach { w ->
                    val date = formatter.format(w.startTime)
                    val dur = w.durationSeconds / 60
                    val notes = w.notes?.replace(",", ";")?.replace("\n", " ") ?: ""
                    sb.appendLine("$date,$dur,${w.burnPoints},${w.averageHeartRate},${w.maxHeartRate},${w.xpEarned},${w.estimatedCalories ?: 0},$notes")
                }

                val filename = "pulsefit_export_${System.currentTimeMillis()}.csv"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, filename)
                    put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { os ->
                        os.write(sb.toString().toByteArray())
                    }
                    _snackbar.emit("Exported ${workouts.size} workouts to Downloads")
                } ?: _snackbar.emit("Export failed")
            } catch (e: Exception) {
                _snackbar.emit("Export failed: ${e.message}")
            }
        }
    }

    fun deleteAllWorkouts() {
        viewModelScope.launch {
            val workouts = workoutRepository.getCompletedWorkouts()
            deletedWorkouts = workouts
            workouts.forEach { workoutRepository.deleteWorkout(it.id) }
            _snackbar.emit("All workouts deleted. Tap to undo.")
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            deletedWorkouts?.forEach { workoutRepository.createWorkout(it) }
            deletedWorkouts = null
            _snackbar.emit("Workouts restored")
        }
    }
}
