package com.pulsefit.app.voice

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import com.pulsefit.app.data.model.ExerciseStation
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.data.model.TreadMode
import com.pulsefit.app.data.model.VoiceCoachStyle
import com.pulsefit.app.data.repository.SensoryPreferencesRepository
import com.pulsefit.app.domain.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceCoachEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sensoryPreferencesRepository: SensoryPreferencesRepository
) {
    private var tts: TextToSpeech? = null
    @Volatile private var isInitialized = false
    @Volatile private var isTtsReady = false
    private var mediaPlayer: MediaPlayer? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private var currentStyle = VoiceCoachStyle.STANDARD
    private var lastZone: HeartRateZone? = null
    private var lastSpokenMinute = -1

    /** Map of clip key -> raw resource ID, built once at init. */
    private val clipResourceMap = mutableMapOf<String, Int>()

    /** Playback queue for sequential clip playback. */
    private val playbackQueue = ConcurrentLinkedQueue<Pair<String, String>>()
    @Volatile private var isQueuePlaying = false

    /** Round-robin index for encouragement clips. */
    private var encouragementIndex = 0

    /** ND-profile verbosity: controls max queue depth and feature flags. */
    private var ndProfile = NdProfile.STANDARD
    private var maxQueueDepth = 4

    fun initialize() {
        buildResourceMap()
        isInitialized = true
        playbackQueue.clear()
        isQueuePlaying = false
        encouragementIndex = 0

        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS && isInitialized) {
                tts?.language = Locale.getDefault()
                isTtsReady = true
            }
        }
    }

    suspend fun updateStyle() {
        currentStyle = sensoryPreferencesRepository.getPreferencesOnce().voiceCoachStyle
    }

    /** Set the ND profile for verbosity control. Call after loading user profile. */
    fun setNdProfile(profile: NdProfile) {
        ndProfile = profile
        maxQueueDepth = when (profile) {
            NdProfile.ASD -> 2
            NdProfile.ADHD -> 5
            NdProfile.AUDHD -> 3
            NdProfile.STANDARD -> 4
        }
    }

    fun onZoneChange(newZone: HeartRateZone) {
        if (!isInitialized || newZone == lastZone) return
        lastZone = newZone

        val zoneKey = when (newZone) {
            HeartRateZone.REST -> "rest"
            HeartRateZone.WARM_UP -> "warm_up"
            HeartRateZone.ACTIVE -> "active"
            HeartRateZone.PUSH -> "push"
            HeartRateZone.PEAK -> "peak"
        }
        val styleKey = currentStyle.name.lowercase()
        val clipKey = "voice_${styleKey}_zone_$zoneKey"

        val fallbackText = when (currentStyle) {
            VoiceCoachStyle.LITERAL -> "Zone changed to ${newZone.label}. Points per minute: ${newZone.pointsPerMinute}."
            VoiceCoachStyle.STANDARD -> when (newZone) {
                HeartRateZone.REST -> "Take it easy."
                HeartRateZone.WARM_UP -> "Warming up nicely."
                HeartRateZone.ACTIVE -> "Good pace. Earning points."
                HeartRateZone.PUSH -> "Pushing hard. Great effort!"
                HeartRateZone.PEAK -> "You're on fire! Maximum points!"
            }
            VoiceCoachStyle.HYPE -> when (newZone) {
                HeartRateZone.REST -> "Chill mode. You got this!"
                HeartRateZone.WARM_UP -> "Let's warm it up! Getting started!"
                HeartRateZone.ACTIVE -> "YES! Active zone! Points are rolling in!"
                HeartRateZone.PUSH -> "PUSH IT! Double points baby!"
                HeartRateZone.PEAK -> "PEAK ZONE! MAXIMUM OVERDRIVE! Triple points!"
            }
        }
        playClip(clipKey, fallbackText)
    }

    fun onTimeUpdate(elapsedSeconds: Int) {
        if (!isInitialized) return
        val minutes = elapsedSeconds / 60
        if (minutes > 0 && minutes != lastSpokenMinute && minutes % 5 == 0) {
            lastSpokenMinute = minutes

            val fallbackText = when (currentStyle) {
                VoiceCoachStyle.LITERAL -> "$minutes minutes elapsed."
                VoiceCoachStyle.STANDARD -> "$minutes minutes in."
                VoiceCoachStyle.HYPE -> "$minutes minutes! Keep that energy up!"
            }

            if (minutes in 5..60) {
                val styleKey = currentStyle.name.lowercase()
                val clipKey = "voice_${styleKey}_time_$minutes"
                playClip(clipKey, fallbackText)
            } else {
                speak(fallbackText)
            }
        }
    }

    fun onMilestone(message: String) {
        if (!isInitialized) return
        speak(message)
    }

    // ---- Composable exercise announcements ----------------------------------

    /**
     * Composes a multi-clip announcement for an exercise change:
     * 1. Exercise name clip
     * 2. Target clip (speed/incline/watts) — based on tread mode
     * 3. Duration clip
     * 4. Motivation connector (optional, based on ND profile)
     * 5. Form cue (for floor exercises)
     *
     * Each queued clip plays sequentially. TTS fills gaps if clip is missing.
     */
    fun announceExerciseComposite(
        exerciseName: String,
        exerciseId: String,
        station: ExerciseStation,
        durationSeconds: Int,
        isLast: Boolean,
        treadMode: TreadMode,
        coachingTargetRegistry: CoachingTargetRegistry
    ) {
        if (!isInitialized) return
        val styleKey = currentStyle.name.lowercase()

        // Enforce queue depth limit
        if (playbackQueue.size >= maxQueueDepth) return

        // 1. Exercise name
        val exerciseKey = exerciseName.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
        val exerciseClipKey = "voice_${styleKey}_exercise_$exerciseKey"
        val exerciseFallback = when (currentStyle) {
            VoiceCoachStyle.LITERAL -> "Next: $exerciseName."
            VoiceCoachStyle.STANDARD -> "$exerciseName is next."
            VoiceCoachStyle.HYPE -> "${exerciseName.uppercase()}!"
        }
        queueClip(exerciseClipKey, exerciseFallback)

        // 2. Target (speed/incline/watts) — skip for ASD literal (they just get name + duration)
        val targetClipKey = coachingTargetRegistry.getTargetClipKey(exerciseId, treadMode, station)
        val targetText = coachingTargetRegistry.getTargetText(exerciseId, treadMode, station)
        if (targetClipKey != null && targetText != null && playbackQueue.size < maxQueueDepth) {
            queueClip("voice_${styleKey}_$targetClipKey", targetText)
        }

        // 3. Duration
        if (playbackQueue.size < maxQueueDepth) {
            val durClipKey = getDurationClipKey(durationSeconds)
            val durText = getDurationText(durationSeconds)
            if (durClipKey != null) {
                queueClip("voice_${styleKey}_$durClipKey", durText)
            }
        }

        // 4. Motivation connector — only for ADHD/STANDARD/HYPE styles, not ASD
        if (shouldAddMotivation() && playbackQueue.size < maxQueueDepth) {
            val motivationKeys = listOf("motivation_lets_go", "motivation_you_got_this", "motivation_strong_finish")
            val key = motivationKeys[encouragementIndex % motivationKeys.size]
            val fallback = when (key) {
                "motivation_lets_go" -> "Let's go!"
                "motivation_you_got_this" -> "You've got this!"
                else -> "Strong finish!"
            }
            queueClip("voice_${styleKey}_$key", fallback)
        }

        // 5. Form cue for floor exercises
        if (station == ExerciseStation.FLOOR && playbackQueue.size < maxQueueDepth) {
            val formClipKey = coachingTargetRegistry.getFormCueClipKey(exerciseId)
            val formText = coachingTargetRegistry.getTarget(exerciseId)?.formCue
            if (formClipKey != null && formText != null) {
                queueClip("voice_${styleKey}_$formClipKey", formText)
            }
        }

        // 6. Last exercise callout
        if (isLast && playbackQueue.size < maxQueueDepth) {
            val lastText = when (currentStyle) {
                VoiceCoachStyle.LITERAL -> "This is the last exercise."
                VoiceCoachStyle.STANDARD -> "Last one!"
                VoiceCoachStyle.HYPE -> "FINAL EXERCISE! GIVE IT EVERYTHING!"
            }
            queueClip("voice_${styleKey}_motivation_last_one", lastText)
        }

        startQueueIfNeeded()
    }

    /**
     * Mid-exercise push-harder encouragement. Called at exercise midpoint
     * for push/all-out exercises. Respects ND profile verbosity.
     */
    fun encouragePushHarder(
        treadMode: TreadMode,
        station: ExerciseStation,
        exerciseId: String,
        coachingTargetRegistry: CoachingTargetRegistry
    ) {
        if (!isInitialized) return
        if (!shouldPushHarder(exerciseId)) return

        val styleKey = currentStyle.name.lowercase()
        val clipKey = coachingTargetRegistry.getPushHarderClipKey(treadMode, station)
        val fallbackText = coachingTargetRegistry.getPushHarderText(treadMode, station)

        if (clipKey != null && fallbackText != null) {
            queueClip("voice_${styleKey}_$clipKey", fallbackText)
            startQueueIfNeeded()
        }
    }

    /** Called when the guided workout transitions to a new station. */
    fun onStationChange(stationName: String) {
        if (!isInitialized) return
        val text = when (currentStyle) {
            VoiceCoachStyle.LITERAL -> "Moving to the ${stationName.lowercase()} station."
            VoiceCoachStyle.STANDARD -> "Time to switch! Heading to the ${stationName.lowercase()}."
            VoiceCoachStyle.HYPE -> "${stationName.uppercase()} TIME! Let's GET AFTER IT!"
        }
        val stationKey = stationName.lowercase()
        val styleKey = currentStyle.name.lowercase()
        val clipKey = "voice_${styleKey}_station_$stationKey"
        playClip(clipKey, text)
    }

    // ---- Engagement methods -------------------------------------------------

    fun onWorkoutStart(profile: UserProfile) {
        if (!isInitialized) return
        val styleKey = currentStyle.name.lowercase()

        val totalWorkouts = profile.totalWorkouts.coerceAtLeast(0)
        val currentStreak = profile.currentStreak.coerceAtLeast(0)
        val lastWorkoutAt = profile.lastWorkoutAt?.takeIf { it <= System.currentTimeMillis() }

        val daysSince = if (lastWorkoutAt != null) {
            TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastWorkoutAt)
                .coerceAtLeast(0)
        } else {
            Long.MAX_VALUE
        }

        when {
            totalWorkouts == 0 -> {
                queueClip("voice_${styleKey}_start_first", startFallback("start_first"))
            }
            daysSince > 7 -> {
                queueClip("voice_${styleKey}_return_long", returnFallback("return_long"))
                queueClip("voice_${styleKey}_start_welcome", startFallback("start_welcome"))
            }
            daysSince in 3..7 -> {
                queueClip("voice_${styleKey}_return_few", returnFallback("return_few"))
                queueClip("voice_${styleKey}_start_welcome", startFallback("start_welcome"))
            }
            daysSince == 2L -> {
                queueClip("voice_${styleKey}_return_1day", returnFallback("return_1day"))
                queueClip("voice_${styleKey}_start_welcome", startFallback("start_welcome"))
            }
            currentStreak >= 3 -> {
                queueClip("voice_${styleKey}_start_streak", startFallback("start_streak"))
            }
            else -> {
                queueClip("voice_${styleKey}_start_welcome", startFallback("start_welcome"))
            }
        }
        startQueueIfNeeded()
    }

    fun onWorkoutComplete(hitTarget: Boolean, isPersonalBest: Boolean) {
        if (!isInitialized) return
        val styleKey = currentStyle.name.lowercase()

        val key = when {
            isPersonalBest -> "complete_pb"
            hitTarget -> "complete_target"
            else -> "complete_good"
        }
        queueClip("voice_${styleKey}_$key", completeFallback(key))
        startQueueIfNeeded()
    }

    fun onTargetHit() {
        if (!isInitialized) return
        val styleKey = currentStyle.name.lowercase()
        playClip("voice_${styleKey}_target_hit", targetHitFallback())
    }

    fun onStreakMilestone(streakDays: Int) {
        if (!isInitialized) return
        val validMilestones = setOf(3, 5, 7, 10, 14, 21, 30, 100)
        if (streakDays !in validMilestones) return

        val styleKey = currentStyle.name.lowercase()
        queueClip("voice_${styleKey}_streak_$streakDays", streakFallback(streakDays))
        startQueueIfNeeded()
    }

    fun onEncouragement() {
        if (!isInitialized) return
        val styleKey = currentStyle.name.lowercase()
        val idx = (encouragementIndex % 5) + 1
        encouragementIndex++
        playClip("voice_${styleKey}_encourage_$idx", encourageFallback(idx))
    }

    fun onProgressCallout(key: String) {
        if (!isInitialized) return
        val styleKey = currentStyle.name.lowercase()
        queueClip("voice_${styleKey}_$key", progressFallback(key))
        startQueueIfNeeded()
    }

    // ---- ND-profile verbosity helpers ---------------------------------------

    private fun shouldAddMotivation(): Boolean = when (ndProfile) {
        NdProfile.ASD -> false
        NdProfile.AUDHD -> false
        NdProfile.ADHD -> true
        NdProfile.STANDARD -> true
    }

    private fun shouldPushHarder(exerciseId: String): Boolean = when (ndProfile) {
        NdProfile.ASD -> false  // No mid-exercise interruptions
        NdProfile.AUDHD -> exerciseId in setOf("tread_all_out", "row_all_out")  // All-out only
        NdProfile.ADHD -> true  // All push/all-out exercises
        NdProfile.STANDARD -> true  // All push/all-out exercises
    }

    // ---- Duration helpers ---------------------------------------------------

    private fun getDurationClipKey(seconds: Int): String? {
        return when (seconds) {
            20 -> "duration_20_seconds"
            30 -> "duration_30_seconds"
            45 -> "duration_45_seconds"
            60 -> "duration_1_minute"
            120 -> "duration_2_minutes"
            180 -> "duration_3_minutes"
            240 -> "duration_4_minutes"
            300 -> "duration_5_minutes"
            600 -> "duration_10_minutes"
            900 -> "duration_15_minutes"
            else -> null  // TTS will handle non-standard durations
        }
    }

    private fun getDurationText(seconds: Int): String {
        return if (seconds >= 60) {
            val min = seconds / 60
            val sec = seconds % 60
            if (sec == 0) "$min minute${if (min > 1) "s" else ""}"
            else "$min minute${if (min > 1) "s" else ""} $sec seconds"
        } else {
            "$seconds seconds"
        }
    }

    // ---- Playback queue -----------------------------------------------------

    private fun queueClip(key: String, fallbackText: String) {
        if (playbackQueue.size < maxQueueDepth) {
            playbackQueue.offer(key to fallbackText)
        }
    }

    private fun startQueueIfNeeded() {
        if (!isQueuePlaying && playbackQueue.isNotEmpty()) {
            playNext()
        }
    }

    private fun playNext() {
        val next = playbackQueue.poll()
        if (next == null) {
            isQueuePlaying = false
            return
        }
        isQueuePlaying = true
        val (key, fallback) = next
        playClipQueued(key, fallback)
    }

    private fun playClipQueued(key: String, fallbackText: String) {
        val resId = clipResourceMap[key]
        if (resId != null && resId != 0) {
            try {
                playResourceQueued(resId)
                return
            } catch (_: Exception) {
                // fall through to TTS
            }
        }
        speak(fallbackText)
        playNext()
    }

    private fun playResourceQueued(resId: Int) {
        mediaPlayer?.release()
        mediaPlayer = null
        val player = MediaPlayer.create(context, resId)
        if (player == null) {
            _isSpeaking.value = false
            playNext()
            return
        }
        mediaPlayer = player.apply {
            setOnCompletionListener { mp ->
                mp.release()
                if (mediaPlayer === mp) mediaPlayer = null
                _isSpeaking.value = false
                playNext()
            }
            setOnErrorListener { mp, _, _ ->
                mp.release()
                if (mediaPlayer === mp) mediaPlayer = null
                _isSpeaking.value = false
                playNext()
                true
            }
            _isSpeaking.value = true
            start()
        }
    }

    // ---- Playback helpers ---------------------------------------------------

    private fun playClip(key: String, fallbackText: String) {
        val resId = clipResourceMap[key]
        if (resId != null && resId != 0) {
            try {
                playResource(resId)
                return
            } catch (_: Exception) {
                // fall through to TTS
            }
        }
        speak(fallbackText)
    }

    private fun playResource(resId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, resId)?.apply {
            setOnCompletionListener { mp ->
                mp.release()
                if (mediaPlayer === mp) mediaPlayer = null
                _isSpeaking.value = false
            }
            setOnErrorListener { mp, _, _ ->
                mp.release()
                if (mediaPlayer === mp) mediaPlayer = null
                _isSpeaking.value = false
                true
            }
            _isSpeaking.value = true
            start()
        }
    }

    private fun speak(text: String) {
        if (!isTtsReady) return
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, text.hashCode().toString())
    }

    // ---- Fallback text generators -------------------------------------------

    private fun startFallback(key: String): String = when (currentStyle) {
        VoiceCoachStyle.LITERAL -> when (key) {
            "start_first" -> "This is your first workout. Let's begin."
            "start_streak" -> "Your streak is active. Let's keep it going."
            else -> "Workout ready. Starting now."
        }
        VoiceCoachStyle.STANDARD -> when (key) {
            "start_first" -> "Welcome to your very first workout! Let's make it count."
            "start_streak" -> "Your streak is on fire. Let's keep building."
            else -> "Good to see you. Let's get started."
        }
        VoiceCoachStyle.HYPE -> when (key) {
            "start_first" -> "FIRST WORKOUT! Let's make it legendary!"
            "start_streak" -> "STREAK MODE! You are UNSTOPPABLE!"
            else -> "Let's GO! Time to crush it!"
        }
    }

    private fun returnFallback(key: String): String = when (currentStyle) {
        VoiceCoachStyle.LITERAL -> when (key) {
            "return_1day" -> "Welcome back. Ready to begin."
            "return_few" -> "Welcome back. Starting fresh."
            else -> "Welcome back. Every workout counts."
        }
        VoiceCoachStyle.STANDARD -> when (key) {
            "return_1day" -> "Good to see you! Let's get moving."
            "return_few" -> "Welcome back! Ready when you are."
            else -> "Great to have you back. Let's do this."
        }
        VoiceCoachStyle.HYPE -> when (key) {
            "return_1day" -> "You're BACK! Let's make today count!"
            "return_few" -> "WELCOME BACK! Time to get after it!"
            else -> "The RETURN! Let's show up TODAY!"
        }
    }

    private fun completeFallback(key: String): String = when (currentStyle) {
        VoiceCoachStyle.LITERAL -> when (key) {
            "complete_target" -> "Workout complete. You hit your daily target."
            "complete_pb" -> "Workout complete. That was a new personal best."
            else -> "Workout complete. Well done."
        }
        VoiceCoachStyle.STANDARD -> when (key) {
            "complete_target" -> "You smashed your daily target. Amazing work!"
            "complete_pb" -> "New personal best! That's incredible progress!"
            else -> "Great workout! You should be proud."
        }
        VoiceCoachStyle.HYPE -> when (key) {
            "complete_target" -> "TARGET SMASHED! You are on ANOTHER LEVEL!"
            "complete_pb" -> "NEW PERSONAL BEST! ABSOLUTELY INSANE!"
            else -> "WORKOUT DONE! You are a BEAST!"
        }
    }

    private fun targetHitFallback(): String = when (currentStyle) {
        VoiceCoachStyle.LITERAL -> "You have reached your daily burn point target."
        VoiceCoachStyle.STANDARD -> "Daily target reached! Everything from here is bonus."
        VoiceCoachStyle.HYPE -> "TARGET HIT! Now let's see how far BEYOND you can go!"
    }

    private fun streakFallback(days: Int): String = when (currentStyle) {
        VoiceCoachStyle.LITERAL -> when (days) {
            3 -> "You have a 3 day streak."
            5 -> "You have a 5 day streak."
            7 -> "You have a 7 day streak. That is one full week."
            10 -> "You have a 10 day streak."
            14 -> "You have a 14 day streak. That is two weeks."
            21 -> "You have a 21 day streak. That is three weeks."
            30 -> "You have a 30 day streak. That is one month."
            100 -> "You have a 100 day streak."
            else -> "You have a $days day streak."
        }
        VoiceCoachStyle.STANDARD -> when (days) {
            3 -> "3 days in a row! You're building a habit."
            5 -> "5 day streak! Consistency is your superpower."
            7 -> "One full week! You're on a roll."
            10 -> "10 days strong! Double digits!"
            14 -> "Two weeks straight! That's dedication."
            21 -> "21 days! They say that's what it takes to build a habit."
            30 -> "30 day streak! A full month of commitment!"
            100 -> "100 days! You are truly extraordinary."
            else -> "$days day streak! Keep going!"
        }
        VoiceCoachStyle.HYPE -> when (days) {
            3 -> "THREE DAYS! The streak is ALIVE!"
            5 -> "FIVE DAYS! You are on FIRE!"
            7 -> "ONE WEEK STRAIGHT! UNSTOPPABLE!"
            10 -> "TEN DAYS! DOUBLE DIGITS BABY!"
            14 -> "TWO WEEKS! You are a WARRIOR!"
            21 -> "TWENTY ONE DAYS! HABIT FORMED! LEGEND!"
            30 -> "THIRTY DAYS! ONE MONTH! You are ELITE!"
            100 -> "ONE HUNDRED DAYS! You are ABSOLUTELY LEGENDARY!"
            else -> "$days DAYS! INCREDIBLE!"
        }
    }

    private fun encourageFallback(idx: Int): String = when (currentStyle) {
        VoiceCoachStyle.LITERAL -> when (idx) {
            1 -> "You are doing well. Keep going."
            2 -> "Good effort. Stay consistent."
            3 -> "Your heart rate is in a good zone."
            4 -> "You are making progress. Keep it up."
            else -> "Steady pace. Points are accumulating."
        }
        VoiceCoachStyle.STANDARD -> when (idx) {
            1 -> "Looking strong! Keep pushing."
            2 -> "You're in the groove. Great rhythm."
            3 -> "This is where the magic happens. Stay with it."
            4 -> "Fantastic effort! You've got this."
            else -> "Every second counts. Keep going!"
        }
        VoiceCoachStyle.HYPE -> when (idx) {
            1 -> "YES! You are KILLING IT right now!"
            2 -> "Don't stop! The ENERGY is UNREAL!"
            3 -> "LOOK AT YOU GO! Absolutely SMASHING it!"
            4 -> "You are a MACHINE! Keep that pace!"
            else -> "THIS is what CHAMPIONS do! KEEP GOING!"
        }
    }

    private fun progressFallback(key: String): String = when (currentStyle) {
        VoiceCoachStyle.LITERAL -> when (key) {
            "progress_week_great" -> "You have done 5 or more workouts this week."
            "progress_total_10" -> "You have completed 10 workouts total."
            "progress_total_50" -> "You have completed 50 workouts total."
            else -> "You are making great progress."
        }
        VoiceCoachStyle.STANDARD -> when (key) {
            "progress_week_great" -> "What a week! 5 plus workouts and counting."
            "progress_total_10" -> "10 workouts in the books! You're committed."
            "progress_total_50" -> "50 workouts! That is a serious milestone."
            else -> "Great progress! Keep it up."
        }
        VoiceCoachStyle.HYPE -> when (key) {
            "progress_week_great" -> "FIVE WORKOUTS THIS WEEK! You are a MACHINE!"
            "progress_total_10" -> "TEN WORKOUTS! Double digits! THE GRIND IS REAL!"
            "progress_total_50" -> "FIFTY WORKOUTS! HALF A HUNDRED! ABSOLUTE LEGEND!"
            else -> "INCREDIBLE PROGRESS!"
        }
    }

    // ---- Resource map -------------------------------------------------------

    private fun buildResourceMap() {
        clipResourceMap.clear()
        val packageName = context.packageName
        val res = context.resources

        val styles = listOf("literal", "standard", "hype")
        val zones = listOf("rest", "warm_up", "active", "push", "peak")
        val timeIntervals = (5..60 step 5).toList()

        val keyedClips = listOf(
            "start_first", "start_welcome", "start_streak",
            "complete_good", "complete_target", "complete_pb",
            "target_hit",
            "encourage_1", "encourage_2", "encourage_3", "encourage_4", "encourage_5",
            "streak_3", "streak_5", "streak_7", "streak_10", "streak_14", "streak_21", "streak_30", "streak_100",
            "return_1day", "return_few", "return_long",
            "progress_week_great", "progress_total_10", "progress_total_50"
        )

        val exercises = listOf(
            "base_pace", "push_pace", "all_out", "power_walk", "incline",
            "steady_row", "power_row", "all_out_row",
            "squats", "lunges", "deadlifts", "chest_press", "shoulder_press",
            "bicep_curls", "tricep_extensions", "push_ups", "plank",
            "trx_rows", "bench_hop_overs", "pop_squats"
        )

        val stations = listOf("tread", "row", "floor")

        // New composable coaching clips
        val targetClips = listOf(
            "target_5_mph", "target_6_mph", "target_7_mph", "target_8_mph", "target_9_mph",
            "target_incline_4", "target_incline_8", "target_incline_10", "target_incline_12",
            "target_100_watts", "target_150_watts", "target_200_watts"
        )

        val durationClips = listOf(
            "duration_20_seconds", "duration_30_seconds", "duration_45_seconds",
            "duration_1_minute", "duration_2_minutes", "duration_3_minutes",
            "duration_4_minutes", "duration_5_minutes",
            "duration_10_minutes", "duration_15_minutes"
        )

        val motivationClips = listOf(
            "motivation_lets_go", "motivation_you_got_this", "motivation_strong_finish",
            "motivation_almost_there", "motivation_keep_pushing", "motivation_last_one"
        )

        val pushHarderClips = listOf(
            "push_harder_add_1_mph", "push_harder_add_half_mph",
            "push_harder_raise_incline_2", "push_harder_raise_incline_4",
            "push_harder_add_20_watts", "push_harder_add_50_watts"
        )

        val formCueClips = listOf(
            "form_chest_up", "form_drive_heels", "form_full_range",
            "form_control_tempo", "form_squeeze_top", "form_breathe"
        )

        val prefixClips = listOf(
            "prefix_minimum", "prefix_next_up", "prefix_moving_to", "prefix_for"
        )

        for (style in styles) {
            for (zone in zones) {
                val key = "voice_${style}_zone_$zone"
                val id = res.getIdentifier(key, "raw", packageName)
                if (id != 0) clipResourceMap[key] = id
            }
            for (min in timeIntervals) {
                val key = "voice_${style}_time_$min"
                val id = res.getIdentifier(key, "raw", packageName)
                if (id != 0) clipResourceMap[key] = id
            }
            for (clip in keyedClips) {
                val key = "voice_${style}_$clip"
                val id = res.getIdentifier(key, "raw", packageName)
                if (id != 0) clipResourceMap[key] = id
            }
            for (exercise in exercises) {
                val key = "voice_${style}_exercise_$exercise"
                val id = res.getIdentifier(key, "raw", packageName)
                if (id != 0) clipResourceMap[key] = id
            }
            for (station in stations) {
                val key = "voice_${style}_station_$station"
                val id = res.getIdentifier(key, "raw", packageName)
                if (id != 0) clipResourceMap[key] = id
            }
            // New composable clip categories
            val newClipLists = listOf(
                targetClips, durationClips, motivationClips,
                pushHarderClips, formCueClips, prefixClips
            )
            for (clipList in newClipLists) {
                for (clip in clipList) {
                    val key = "voice_${style}_$clip"
                    val id = res.getIdentifier(key, "raw", packageName)
                    if (id != 0) clipResourceMap[key] = id
                }
            }
        }
    }

    // ---- Lifecycle ----------------------------------------------------------

    fun shutdown() {
        mediaPlayer?.release()
        mediaPlayer = null
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        isTtsReady = false
        lastZone = null
        lastSpokenMinute = -1
        clipResourceMap.clear()
        playbackQueue.clear()
        isQueuePlaying = false
        encouragementIndex = 0
    }
}
