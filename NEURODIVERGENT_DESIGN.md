# PulseFit — Neurodivergent Design Philosophy & Features

## Overview

PulseFit is the first fitness app designed with neurodivergent users in mind. Rather than bolting accessibility onto a neurotypical design, PulseFit embeds ND-friendly principles into its core UX through toggleable profiles.

**Profiles** (combinable):
- **Standard** — default balanced gamification
- **ADHD Focus Mode** — dopamine-first, zero friction, novelty, instant rewards
- **ASD Comfort Mode** — predictable, calm, literal, sensory-controlled, deep data
- **AuDHD Combined** — cherry-picked defaults from both (ADHD+ASD overlap)

Profiles are offered during onboarding and changeable in Settings. Each profile sets defaults; every setting remains manually tuneable.

---

## Part 1: ADHD Focus Mode

### Design Principles

| Principle | In PulseFit |
|-----------|------------|
| **Zero friction** | Max 1 tap to primary action; one-tap start from widget/notification/lock screen |
| **Dopamine-first** | Instant visual/audio/haptic reward on every point earned |
| **Novelty engine** | Rotate UI colours, badge art, voice phrases to prevent habituation |
| **Chunk everything** | "Block 2 of 6" not "28 min remaining" |
| **Externalise time** | Giant colour-filling timer, haptic pulses, voice time markers |
| **Reduce decisions** | Smart defaults, auto-suggestions, "just do this" prompts |

### Modifications to Existing Features

**F1 Onboarding:** Reduce to 3 taps (age → target → start). Optional steps deferred. ADHD toggle on step 2.

**F4 Live Workout:** Micro-reward animation every minute in Zone 3-5. Task chunking overlay ("Block 2 of 6"). Giant time blindness timer. Streak multiplier badge in corner.

**F5 Summary:** Celebration Overkill plays BEFORE stats (confetti, sound, voice). XP bar prominent.

**F6 Burn Points:** Variable reward drops. Visual streak multiplier (1x→3x). XP layer (1 BP = 10 XP).

**F7 History:** Simplified win/not-yet view. Progress visualisation (garden/pet/city) default.

**F9 Notifications:** "Just 5 Minutes" nudge with one-tap start. Daily quest notification.

**F21 Voice Coach:** Hybrid ElevenLabs — energetic voice profile, frequent encouragement (every 2-3 min), time blindness callouts.

---

### F111. Instant Micro-Rewards

**Goal:** Continuous dopamine during workouts.

- Every completed minute in Zone 3-5: confetti burst + point pop-up + sound + haptic double-tap
- Visual style varies slightly each time (novelty)
- Settings: frequency (every min / 2 min / off), sound (on/off), haptic (on/off)

---

### F112. "Just 5 Minutes" Start Mode

**Goal:** Eliminate task initiation paralysis.

- Notification/widget: "Just 5 minutes. That's it." → one tap starts
- At 5:00: "Nice! Keep going?" → "Yes!" / "Done" (saves session, full credit)
- Next check at 10 min, then no more interruptions
- All points count — no penalty for short workouts

---

### F113. Variable Reward Drops

**Goal:** Variable reinforcement for the ADHD brain.

- ~15% chance per minute of "mystery drop" during workout
- Types: bonus BP (1-3), XP multiplier (2 min), cosmetic unlock, streak freeze
- Glowing orb animation, tap to reveal
- More frequent in longer workouts
- **Disabled in ASD mode** (replaced by F139)

---

### F114. Dopamine Streak Multiplier

**Goal:** Make streaks feel increasingly rewarding.

| Days | Multiplier | Visual |
|------|-----------|--------|
| 1-2 | 1x | Standard |
| 3-6 | 1.5x | Flame icon |
| 7-13 | 2x | Pulsing counter |
| 14-29 | 2.5x | Golden flame |
| 30+ | 3x | Full fire animation |

Applies to XP display, not actual Burn Points (leaderboard fairness).

---

### F115. Time Blindness Timer

**Goal:** Make time concrete and visible.

- **Colour-filling circle:** large arc fills clockwise, bands every 5 min
- **Haptic pulses:** every 5 min (configurable: 2/5/10)
- **Voice markers:** ElevenLabs says "10 minutes in" / "Halfway"
- **ASD variant:** exact numbers ("14 minutes 37 seconds")

---

### F116. Novelty Rotation Engine

**Goal:** Prevent habituation and app abandonment.

Rotates: UI accent colour (weekly), badge art (monthly), voice phrases (per session), micro-reward visuals, daily quest combos, home screen stat.

**Disabled in ASD mode** (F134 Predictable UI Lock).

---

### F117. Body Double Mode

**Goal:** "Working alongside someone" presence for task initiation.

- Opt-in match with simultaneous user
- Minimal display: avatar, zone colour, "still going" indicator
- No chat, no comparison, anonymous by default
- Optional mutual haptic nudge

---

### F118. Hyperfocus Capture Badge

**Goal:** Reward the ADHD superpower.

- Trigger: 15+ unbroken minutes in Zone 3-5
- Reward: special badge, 50 XP, tracked as personal best

---

### F119. Zero-Friction Quick Start

**Goal:** Minimum taps from intention to workout.

Entry points: home widget "GO", notification action, lock screen shortcut, Wear OS complication, app-launch prompt. Auto-connects last device; starts in 5 sec if none found.

---

### F120. XP & Leveling System

**Goal:** Long-term progression for ADHD brains.

- Level 1-50, logarithmic XP scaling
- Sources: BP (1:10 XP), streaks, badges, quests, drops
- Each level unlocks cosmetics

| Level | XP | Unlock |
|-------|-----|--------|
| 5 | 500 | "Ember" theme |
| 10 | 1,500 | Custom zone animation |
| 15 | 3,500 | Premium voice pack |
| 20 | 6,500 | "Neon" theme |
| 25 | 10,000 | Animated profile badge |
| 30 | 15,000 | Custom celebration |
| 40 | 25,000 | "Galaxy" theme |
| 50 | 40,000 | "Legendary" frame + all unlocked |

---

### F121. Daily Quests Board

**Goal:** Fresh micro-challenges answering "what should I do?"

- 3 quests from 50+ types, generated at midnight
- Examples: "5 min in Peak", "15 BP", "Start before 9 AM"
- 25-50 XP per quest; all 3 = 100 XP bonus
- Expire at midnight — fresh tomorrow

---

### F122. Progress Visualisation

**Goal:** Turn abstract points into something alive.

Options: **Garden** (flowers bloom), **Virtual Pet** (evolves at milestones), **City Builder** (buildings grow). Visible on home screen. Withers/dims (not dies) after 3+ inactive days.

---

### F123. Fidget Haptics

**Goal:** Keep engagement during boring rest phases.

- Active in Zone 1-2
- Patterns: steady pulse, heartbeat sync, wave, random tap
- Faster/more stimulating than ASD calming haptics

---

### F124. Accountability Alarm

**Goal:** Opt-in escalating start pressure.

User sets "latest start time". Escalation: T-60 gentle → T-30 persistent + Start Now → T-0 alarm → T+15 "Just 5 min?" → T+30 final then silence. Always snoozable, never punitive.

---

### F125. Task Chunking Display

**Goal:** Small completable pieces instead of overwhelming duration.

- Templates: "Block 2 of 6 — Push Zone" + progress bar + celebration per block
- Free workouts: auto-chunk into 5-min blocks

---

### F126. Reward Shop

**Goal:** Give Burn Points a spending purpose.

Separate "spendable" balance (not deducted from stats). Items: themes, animations, celebrations, voice packs, frames, viz skins. 50-500 BP; new items monthly.

---

### F127. Anti-Burnout Detection

**Goal:** Catch overtraining before crash.

Triggers: 7+ days at 150%+ target, rising HR at same effort, declining BP/min. Gentle suggestion + free streak freeze.

---

### F128. Social Accountability Contracts

**Goal:** Structured external accountability.

Pair with friend, set weekly commitment, see completion (not details). Optional consequence. Weekly check-in.

---

### F129. Parallel Stimulation Mode

**Goal:** Combine workout with other stimulation.

PiP for video, podcast title on screen, split-screen layout, music BPM sync.

---

### F130. Celebration Overkill Mode

**Goal:** Maximum dopamine on target hit.

Full-screen confetti (3 sec), vibration, fanfare, screen pulse, ElevenLabs voice, XP animation. Default ON in ADHD mode. Replaced by F138 in ASD mode.

---

## Part 2: ASD Comfort Mode

### Design Principles

| Principle | In PulseFit |
|-----------|------------|
| **Sensory control** | User controls every animation, sound, haptic, colour independently |
| **Predictability** | UI never changes unexpectedly; same layout every time |
| **Routine structure** | Fixed weekly schedule; same workout structure repeating |
| **Literal communication** | Clear, direct, factual; no slang, metaphors, exclamations |
| **Social safety** | No unsolicited comparisons or social pressure |
| **Deep data** | Detailed stats, raw numbers, exportable data |
| **Transition preparation** | Advance warning before any change |
| **Safe exit** | End anything anytime with zero guilt |

### Modifications to Existing Features

**F1 Onboarding:** "Step 2 of 6" with full map visible. Plain language for every request. Review all before confirm.

**F4 Live Workout:** Fixed layout (never moves). Exact numbers ("14:37"). Zone = text + colour + pattern. Transition warnings.

**F5 Summary:** Calm celebration (checkmark + soft tone). Data-first. Same format every time.

**F6 Burn Points:** Predictable schedule. No drops/surprises. Formula shown: "12 min x 2 pt/min = 24 BP".

**F7 History:** Deep data default. Consistent charts. Stats: mean, median, std dev.

**F9 Notifications:** Same time daily. Explicit opt-in. Literal: "You have not recorded a workout today."

**F10 Settings:** Sensory Control at top. Plain explanations. Change previews.

**F20 Accessibility:** Texture zones default. High contrast. Reduced motion.

**F21 Voice Coach:** Calm ElevenLabs voice. Literal: "Heart rate: 162. Zone: Push. Burn Points: 8 of 12." No exclamations. Consistent cadence. Consistent audio cues.

---

### F131. Sensory Control Panel

**Goal:** Granular control over every sensory output.

| Channel | Options | ASD Default |
|---------|---------|-------------|
| Animations | Off / Reduced / Full | Reduced |
| Screen transitions | Instant / Fade / Slide | Instant |
| Sounds | Off / Quiet / Normal | Quiet |
| Haptics | Off / Gentle / Strong | Gentle |
| Colour intensity | Muted / Standard / Vivid | Muted |
| Background zone colour | Off / Subtle / Full | Subtle |
| Confetti/particles | Off / Reduced / Full | Off |
| Screen shake | Off / On | Off |
| Contrast | Standard / High / Maximum | High |

Accessible from Settings top and quick-settings during workout.

---

### F132. Routine Builder & Scheduler

**Goal:** Fixed, repeating weekly schedule.

- Assign templates to days with exact times
- "Today's workout: [name] at [time]" — one tap starts
- No judgement on deviation; repeats weekly

---

### F133. Literal Voice Coach Mode

**Goal:** Factual, calm, unambiguous feedback.

Standard: "You're crushing it!" → Literal: "Heart rate: 148. Active zone. Push zone requires above 160."

No exclamation marks, metaphors, slang, assumed emotions. Calm ElevenLabs voice (Stability: 0.8, Style: 0.2).

---

### F134. Predictable UI Lock

**Goal:** UI never changes unexpectedly.

Locks: nav position, layouts, colours, fonts, buttons, card order. No A/B testing, no surprise popups. App updates preview UI changes.

---

### F135. Social Pressure Shield

**Goal:** Remove all social comparison.

One toggle hides: leaderboards, activity feeds, comparative stats, sharing prompts, group invitations, challenge notifications. Only personal data remains.

---

### F136. Deep Data Dashboard

**Goal:** Detailed analytics for data-focused users.

Per-second HR data, zone time to the second, mean/median/std dev, session comparison, CSV/JSON export, custom date filtering, correlation analysis.

---

### F137. Transition Warnings

**Goal:** Advance notice before any change.

- Segments: 30s → 10s → 3-2-1
- Workout end: 2 min → 1 min → complete
- Screen changes: text before transition
- Each channel (visual/audio/haptic) independently toggleable

---

### F138. Calm Celebration Mode

**Goal:** Acknowledge achievement without sensory overload.

On target hit: subtle checkmark + soft chime + "Daily target reached." No confetti, shake, fanfare. **Replaces F130 in ASD mode.**

---

### F139. Predictable Reward Schedule

**Goal:** Eliminate randomness.

Fixed visible formula. No variable drops. Transparent XP. Fixed quest rewards. Fixed streak bonus. **Replaces F113 in ASD mode.**

---

### F140. Safe Exit Protocol

**Goal:** Zero-guilt workout exit.

One tap, no confirmation, "Workout saved. Well done." No streak warnings, no guilt. Silent streak freeze.

---

### F141. Texture & Pattern Zones

**Goal:** Zone identification beyond colour.

| Zone | Colour | Pattern |
|------|--------|---------|
| 1 Rest | Grey | Horizontal lines |
| 2 Warm-Up | Blue | Dots |
| 3 Active | Green | Diagonal stripes |
| 4 Push | Orange | Crosshatch |
| 5 Peak | Red | Solid fill |

---

### F142. Minimal Mode

**Goal:** Essential-only workout UI.

Shows only: HR number, zone name, points/target, time. No bars, icons, animations, background changes.

---

### F143. Pre-Workout Visual Schedule

**Goal:** Complete structure shown before starting.

Template workouts: numbered segments with name, duration, target zone, intensity bar. Free: "Free workout — no set structure. End anytime."

---

### F144. Consistent Audio Palette

**Goal:** Learnable, predictable audio cues.

| Event | Sound |
|-------|-------|
| Zone up | Rising two-tone chime |
| Zone down | Falling two-tone chime |
| Point earned | Soft click |
| Target reached | Warm bell |
| Segment change | Double tap |
| Workout start | Three ascending notes |
| Workout end | Three descending notes |

Never randomised. Novelty engine never touches audio in ASD mode.

---

### F145. Shutdown Routine

**Goal:** Predictable post-workout wind-down.

Same sequence every time: 1) Stats screen → 2) Breathing exercise (optional) → 3) Stretching prompt (optional) → 4) "Workout saved" → 5) Home. Each step skippable, always offered.

---

## Part 3: AuDHD Combined Mode

For users with both ADHD and ASD traits (30-80% overlap):

| Setting | AuDHD Default | Reasoning |
|---------|--------------|-----------|
| Micro-rewards | ON, reduced animation | Dopamine (ADHD) without overload (ASD) |
| Variable drops | OFF | Unpredictability more distressing than rewarding |
| Novelty rotation | OFF for UI, ON for quests | Stable layout + fresh challenges |
| Voice coach | Literal + frequent | Factual (ASD) + regular engagement (ADHD) |
| Celebrations | 1-sec confetti | Brief reward that ends quickly |
| Sensory | Per-channel control | User decides per channel |
| Task chunking | ON | Benefits both |
| XP/Leveling | ON | Structured progression satisfies both |
| Social | Body double only | 1:1 presence without pressure |
| Time timer | ON, exact numbers | Concrete (ADHD) + precise (ASD) |
| Routine builder | ON | Structure reduces decision fatigue |
| Safe exit | ON | Both benefit |
| Transitions | ON | Preparation + time awareness |

Every setting individually toggleable.

---

## Part 4: Data Model Additions

### Updated UserProfile

```kotlin
@Entity
data class UserProfile(
    @PrimaryKey val id: Long = 1,
    val age: Int,
    val weightKg: Float,
    val heightCm: Float,
    val restingHr: Int,
    val maxHr: Int,
    val dailyBurnTarget: Int,
    val unitSystem: UnitSystem,
    val ndProfile: NdProfile,          // STANDARD, ADHD, ASD, AUDHD
    val adhdFocusMode: Boolean,
    val asdComfortMode: Boolean,
    val xpLevel: Int,
    val totalXp: Long,
    val sensoryLevel: SensoryLevel     // MUTED, STANDARD, VIVID
)

enum class NdProfile { STANDARD, ADHD, ASD, AUDHD }
enum class SensoryLevel { MUTED, STANDARD, VIVID }
```

### New Entities

```kotlin
@Entity
data class DailyQuest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,
    val questType: String,
    val description: String,
    val xpReward: Int,
    val completed: Boolean = false
)

@Entity
data class RewardShopItem(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,       // THEME, ANIMATION, VOICE_PACK, BADGE
    val costBurnPoints: Int,
    val purchased: Boolean = false,
    val equipped: Boolean = false
)

@Entity
data class WeeklyRoutine(
    @PrimaryKey val dayOfWeek: Int,
    val workoutTemplateId: Long?,
    val scheduledTime: LocalTime?,
    val isRestDay: Boolean = false
)

@Entity
data class SensoryPreferences(
    @PrimaryKey val id: Long = 1,
    val animations: SensoryOption,
    val screenTransitions: TransitionStyle,
    val sounds: SensoryOption,
    val haptics: SensoryOption,
    val colourIntensity: SensoryLevel,
    val backgroundZoneColour: SensoryOption,
    val confetti: SensoryOption,
    val screenShake: Boolean,
    val contrast: ContrastLevel
)

@Entity
data class VariableRewardDrop(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val timestamp: Instant,
    val dropType: String,
    val value: Int,
    val claimed: Boolean = true
)

@Entity
data class LevelUnlock(
    @PrimaryKey val level: Int,
    val rewardType: String,
    val rewardId: String,
    val rewardName: String,
    val unlocked: Boolean = false
)

enum class SensoryOption { OFF, REDUCED, FULL }
enum class TransitionStyle { INSTANT, FADE, SLIDE }
enum class ContrastLevel { STANDARD, HIGH, MAXIMUM }
```

---

## Part 5: Voice Coach — Hybrid ElevenLabs Strategy

### Why ElevenLabs

| Aspect | Android TTS | ElevenLabs |
|--------|------------|------------|
| Voice quality | Robotic, flat | Natural, human-like |
| Emotional range | Monotone | Calm (ASD) or energetic (ADHD) |
| Voice variety | 5-10 system voices | 100+ voices |
| Consistency | Varies by device | Same everywhere |
| Languages | Device-dependent | 29+ languages |
| Latency | Instant | ~200-500ms (API) |
| Offline | Yes | No (needs caching) |
| Cost | Free | Per-character |

### Three-Tier Hybrid Approach

**Tier 1: Pre-generated ElevenLabs clips (bundled with APK)**
- All fixed phrases generated at build time using ElevenLabs API
- ~200 phrases x 3 voice profiles (Standard/ADHD/ASD) = ~600 audio clips
- Bundled as OGG in APK assets folder
- Zero runtime cost, instant playback, works offline
- Covers ~90% of voice coach usage (zone transitions, encouragement, countdowns, milestones)

**Tier 2: Runtime ElevenLabs API (Premium only, dynamic text)**
- Personalised: "You earned 24 Burn Points, Sarah — 3 more than yesterday"
- AI Coach suggestions (F18), custom user phrases
- Uses `eleven_turbo_v2_5` model for speed, cached locally for reuse
- ~$0.01-0.03 per workout session

**Tier 3: Android TTS (offline fallback)**
- When no cached audio and no network connectivity
- Dynamic text on free tier
- Lower quality but functional

### Voice Profiles

| Profile | Stability | Style | Character |
|---------|-----------|-------|-----------|
| Standard | 0.6 | 0.4 | Friendly, balanced |
| ADHD | 0.5 | 0.7 | Energetic, varied, enthusiastic |
| ASD | 0.8 | 0.2 | Calm, steady, measured, predictable |

### Implementation

```kotlin
class HybridVoiceCoach(
    private val assetPlayer: AssetAudioPlayer,
    private val elevenLabsApi: ElevenLabsApi,
    private val androidTts: TextToSpeech,
    private val cache: VoiceCache,
    private val settings: VoiceCoachSettings
) {
    private var lastCalloutTime: Instant = Instant.MIN

    suspend fun speak(phraseKey: String?, dynamicText: String?, priority: Priority) {
        if (!canSpeak()) return

        // Tier 1: Pre-generated assets
        if (phraseKey != null) {
            val path = "voice/${settings.voiceProfile}/${phraseKey}.ogg"
            if (assetPlayer.hasAsset(path)) {
                requestAudioFocus()
                assetPlayer.play(path, priority)
                lastCalloutTime = Instant.now()
                return
            }
        }

        val text = dynamicText ?: return

        // Tier 2: ElevenLabs API (Premium)
        if (settings.isPremium) {
            val cached = cache.get(text, settings.voiceId)
            if (cached != null) {
                requestAudioFocus()
                audioPlayer.play(cached, priority)
                lastCalloutTime = Instant.now()
                return
            }
            try {
                val audio = elevenLabsApi.textToSpeech(
                    text = text,
                    voiceId = settings.voiceId,
                    modelId = "eleven_turbo_v2_5",
                    voiceSettings = VoiceSettings(
                        stability = settings.voiceProfile.stability,
                        similarityBoost = 0.75f,
                        style = settings.voiceProfile.style
                    )
                )
                cache.store(text, settings.voiceId, audio)
                requestAudioFocus()
                audioPlayer.play(audio, priority)
                lastCalloutTime = Instant.now()
                return
            } catch (_: Exception) { /* fall through to Tier 3 */ }
        }

        // Tier 3: Android TTS fallback
        requestAudioFocus()
        androidTts.speak(text, TextToSpeech.QUEUE_ADD, null, UUID.randomUUID().toString())
        lastCalloutTime = Instant.now()
    }

    private fun canSpeak(): Boolean =
        Duration.between(lastCalloutTime, Instant.now()) >= settings.minGap
}
```

### Cost Analysis

| Tier | Usage | Cost |
|------|-------|------|
| Tier 1 (pre-generated) | ~90% of callouts | One-time ~$5-10; $0 ongoing |
| Tier 2 (runtime API) | ~10%, Premium only | ~$0.01-0.03 per session |
| Tier 3 (Android TTS) | Fallback only | Free |

Result: 90%+ of voice usage is free after initial generation.
