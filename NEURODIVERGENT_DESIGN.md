# PulseFit Neurodivergent Design Philosophy & Features

> PulseFit is the first heart-rate zone fitness app designed from the ground up to support neurodivergent users. This document defines two dedicated UX modes — **ADHD Focus Mode** and **ASD Comfort Mode** — plus a combined **AuDHD Mode** for users who identify with both profiles.

---

## Table of Contents

1. [Why Neurodivergent Design](#1-why-neurodivergent-design)
2. [Neurodivergent Profile System](#2-neurodivergent-profile-system)
3. [ADHD Focus Mode](#3-adhd-focus-mode)
4. [ASD Comfort Mode](#4-asd-comfort-mode)
5. [AuDHD Combined Mode](#5-audhd-combined-mode)
6. [Data Model Additions](#6-data-model-additions)
7. [Cross-References](#7-cross-references)

---

## 1. Why Neurodivergent Design

Standard gamification assumes neurotypical motivation patterns: steady progress, social comparison, and moderate reward schedules. Two large, underserved groups need fundamentally different UX approaches:

### ADHD Users

- Struggle with **task initiation**, **time blindness**, **consistency**, and **boredom**
- Need **dopamine**, **novelty**, **instant rewards**, and **low friction**
- Respond well to variable reward schedules, urgency cues, and micro-goals

### ASD Users

- Struggle with **sensory overload**, **unpredictability**, **social pressure**, and **ambiguous communication**
- Need **predictability**, **calm UI**, **literal language**, **deep data**, and **routine structure**
- Respond well to fixed schedules, detailed information, and controlled sensory environments

### The Conflict

These needs often directly conflict:

| Dimension | ADHD Wants | ASD Wants |
|-----------|-----------|-----------|
| Rewards | Surprise, variable drops | Predictable, fixed schedule |
| UI | Novelty rotation, fresh layouts | Frozen layout, zero change |
| Sound | Exciting, varied | Consistent, learnable |
| Social | Accountability, body doubling | Privacy, no comparisons |
| Celebrations | Overkill, confetti, fanfare | Subtle, calm, quiet |
| Communication | Hype, encouragement, exclamation | Factual, literal, no slang |

**Solution:** Separate toggleable modes rather than one-size-fits-all. Every setting remains individually tuneable regardless of profile.

---

## 2. Neurodivergent Profile System

### 2.1 Profile Selection

During onboarding (F1), users are offered an optional **"Personalise your experience"** step. This is:

- **Completely optional** — skipping defaults to Standard mode
- **Non-clinical** — no diagnosis required; framed as preference, not label
- **Changeable anytime** — accessible from Settings (F10)

### 2.2 Available Profiles

| Profile | Code | Description |
|---------|------|-------------|
| **Standard** | `STANDARD` | Default experience, no ND-specific adjustments |
| **ADHD Focus Mode** | `ADHD` | Dopamine-first, low friction, novelty, instant rewards |
| **ASD Comfort Mode** | `ASD` | Predictable, calm, literal, sensory-controlled |
| **AuDHD Combined** | `AUDHD` | Cherry-picked defaults from both modes |

### 2.3 How Profiles Work

- Selecting a profile changes **defaults** across the entire app
- Every individual setting can still be overridden manually
- Profiles are not mutually exclusive at the setting level — AuDHD mode proves this
- Profile selection is stored in `UserProfile.ndProfile`

### 2.4 Onboarding Language

The profile selector uses approachable, non-clinical language:

> **How do you like your apps?**
>
> - **Keep it exciting** — Fresh rewards, quick starts, lots of feedback *(ADHD defaults)*
> - **Keep it calm** — Predictable layout, clear info, quiet celebrations *(ASD defaults)*
> - **Mix of both** — Structured but rewarding *(AuDHD defaults)*
> - **Standard** — The default PulseFit experience

---

## 3. ADHD Focus Mode

### 3.1 Design Principles

1. **Zero Friction** — Reduce steps to start. Every tap is a barrier. One-tap workout start.
2. **Dopamine First** — Reward early and often. Don't wait until workout end.
3. **Novelty Rotation** — Rotate UI elements, rewards, and challenges to prevent habituation.
4. **Chunking** — Break workouts into tiny, completable segments. Never show the full mountain.
5. **Time Externalisation** — Make time visible. ADHD brains don't feel time passing.
6. **Hyperfocus Leverage** — When flow state hits, don't interrupt it. Ride the wave.
7. **Guilt-Free Flexibility** — Missed a day? No punishment. Just a fresh start.

### 3.2 ADHD Features (F111 - F130)

#### F111 Micro-Reward Engine
**Priority:** P0 (MVP)

Deliver small, frequent dopamine hits throughout the workout — not just at the end.

- Earn Burn Points every 30 seconds in-zone (not just at workout end)
- Visual "point pop" animation on each earn
- Haptic pulse on each point
- Sound effect (toggleable) on milestone points (every 5th point)
- Running total always visible on workout screen

#### F112 "Just 5 Minutes" Mode
**Priority:** P0 (MVP)

Eliminate the initiation barrier by reframing commitment.

- Prominent button on home screen: **"Just 5 Min"**
- Starts a 5-minute workout with no further configuration
- At 5 minutes, gentle prompt: "Nice! Keep going?" with one-tap continue
- If user continues, shows "+1 min" rolling counter instead of a daunting total
- If user stops, celebrates the 5 minutes fully — no "you could have done more" messaging

#### F113 Variable Reward Drops
**Priority:** P1

Unpredictable bonus rewards to maintain novelty.

- Random "loot drops" during workout: bonus XP, cosmetic unlocks, achievement fragments
- Drop probability increases with time in-zone (rewarding effort, not luck)
- Visual and haptic "surprise" effect when a drop occurs
- Drop log viewable after workout
- Never the same drop twice in a session

#### F114 Streak Multiplier
**Priority:** P1

Turn consistency into escalating rewards.

- Day 1: 1x multiplier on all Burn Points
- Day 2: 1.2x, Day 3: 1.5x, Day 7: 2x, Day 14: 3x, Day 30: 5x
- Multiplier shown prominently on home screen
- "Streak Shield" — one free miss per week that doesn't break the streak
- Streak freeze can be earned through the Reward Shop (F126)

#### F115 Time Blindness Timer
**Priority:** P1

Make time tangible for users who don't feel it passing.

- Large, colour-coded countdown/countup timer always visible
- Fills a visual progress bar (spatial representation of time)
- Periodic time announcements via voice coach: "You're 10 minutes in"
- Vibration pattern changes every 5 minutes as a physical time anchor
- Optional "time chunks" view: workout divided into 5-min blocks, filling one at a time

#### F116 Novelty Engine
**Priority:** P2

Prevent habituation by rotating UX elements.

- Rotate workout screen colour themes weekly
- Randomise celebration animations (pool of 20+)
- Daily rotating "challenge flavour" on daily quests
- Seasonal event overlays (Halloween, summer, etc.)
- "Surprise me" workout mode — random workout type and duration

#### F117 Body Double Mode
**Priority:** P2

Simulate the presence of another person working out alongside you.

- Ambient "gym sounds" audio layer (toggleable)
- Ghost avatar on screen doing the same workout in real-time
- Optional live sync with a friend (both see each other's progress)
- "Someone is working out with you right now" — anonymous count of active PulseFit users
- No competitive element — purely presence-based

#### F118 Hyperfocus Badge
**Priority:** P3

Recognise and celebrate flow states.

- Detect sustained high-zone effort (15+ minutes in Push/Peak zones without pause)
- Award "Hyperfocus" badge with special animation
- Track hyperfocus sessions in history
- "You found your flow" voice coach message
- Weekly hyperfocus minutes stat

#### F119 Quick Start
**Priority:** P0 (MVP)

One tap from app open to heart rate tracking.

- Home screen shows one large "GO" button
- Uses last workout settings (or defaults for first-timers)
- Heart rate monitor auto-connects if previously paired
- No configuration required — just tap and move
- Settings accessible via small gear icon, not blocking the start flow

#### F120 XP & Leveling System
**Priority:** P1

Long-term progression through experience points.

- Every Burn Point also earns XP
- XP accumulates across all workouts
- Level up every N XP (scaling curve)
- Level number displayed on profile
- Each level unlocks something: theme, badge, reward shop item, feature
- Level-up celebration with special animation and sound

#### F121 Daily Quests
**Priority:** P1

Fresh goals every day to provide structure and novelty.

- Three daily quests generated each morning
- Mix of difficulty: Easy (5 min workout), Medium (15 min), Hard (30 min or specific zone target)
- Each quest awards bonus XP on completion
- Quests rotate themes: "Cardio Day", "Zone Chaser", "Burn Builder"
- Completing all three awards a "Quest Master" bonus

#### F122 Progress Visualisation
**Priority:** P2

Make abstract progress concrete and visible.

- Weekly heat map of workout days
- Monthly "mountain" visualisation — altitude = total Burn Points
- "Garden" metaphor — workouts grow virtual plants
- Before/after stat comparisons
- Exportable progress images for social sharing

#### F123 Fidget Haptics
**Priority:** P3

Rhythmic vibration patterns for sensory stimulation during rest periods.

- Selectable haptic patterns: heartbeat, metronome, wave, random
- Activates during rest periods or low-intensity phases
- Intensity adjustable (light, medium, strong)
- Can be combined with music tempo
- Different from ASD calming haptics — these are stimulating and varied

#### F124 Accountability Alarm
**Priority:** P3

Smart reminders that work with ADHD time blindness.

- Reminders escalate: gentle notification -> persistent notification -> alarm sound
- "Your workout buddy is waiting" social framing
- Snooze = 5 minutes (not configurable — prevents infinite snoozing)
- Smart timing: learns when user actually works out and reminds at that time
- Can be linked to Body Double Mode (F117)

#### F125 Task Chunking
**Priority:** P1

Break workouts into digestible micro-segments.

- 30-minute workout displayed as six 5-minute chunks
- Each chunk has its own mini-goal and mini-celebration
- "Just one more chunk" prompting at each boundary
- Current chunk highlighted, future chunks dimmed
- Chunk completion creates visible progress (filling blocks)

#### F126 Reward Shop
**Priority:** P2

Spend earned currency on meaningful unlocks.

- XP can be spent on: themes, celebration animations, avatar items, streak shields
- Rotating shop inventory (novelty)
- "Daily deal" featured item at discount
- Earn-only items (not purchasable with real money) for achievement prestige
- Window-shopping mode — browse without spending

#### F127 Anti-Burnout System
**Priority:** P2

Detect and prevent overtraining spirals.

- Track workout frequency and intensity over rolling 7-day window
- If trending toward overtraining: suggest rest day, reduce intensity targets
- "Rest days make you stronger" messaging
- Rest day still earns a small XP bonus for opening the app
- No streak penalty for system-recommended rest days

#### F128 Social Contracts
**Priority:** P3

Peer accountability with bite-sized commitments.

- "Promise" a friend you'll work out today
- Friend gets notified when you complete (or don't)
- Two-way contracts: both commit to the same day
- Gentle, not punitive — "Your friend completed theirs! Your turn?"
- Maximum 3 active contracts (prevents overwhelm)

#### F129 Parallel Stimulation
**Priority:** P2

Support doing multiple things at once (common ADHD need).

- Built-in podcast/music player integration
- Picture-in-picture mode for video during workout
- Minimal workout overlay mode for use with other apps
- "Background mode" — workout tracks in notification bar while user does other things
- Audio-only workout coaching when screen is off

#### F130 Celebration Overkill
**Priority:** P1

Make achievements feel momentous.

- Workout completion: full-screen animation, sound, haptic burst
- Level up: extra-long animation sequence with fireworks
- Streak milestone: special themed celebration (7-day, 30-day, 100-day)
- PR (personal record): unique "record breaker" animation
- All celebrations logged in a "Trophy Room" for revisiting

---

## 4. ASD Comfort Mode

### 4.1 Design Principles

1. **Sensory Control** — User controls all visual, audio, and haptic intensity. Nothing is forced.
2. **Predictable UI** — The layout never changes. Same screens, same positions, always.
3. **Routine Structure** — Fixed weekly schedules. The app guides the same structure each time.
4. **Literal Communication** — Clear, direct language. No slang, metaphors, or excessive punctuation.
5. **Social Pressure Removal** — No leaderboards, no comparisons, no unsolicited social features.
6. **Deep Data** — Detailed stats for special-interest engagement. More data, not less.
7. **Transition Preparation** — Advance warning before any change. Never surprise the user.
8. **Safe Exit** — End anytime with zero guilt. No "are you sure?" manipulation.

### 4.2 ASD Features (F131 - F145)

#### F131 Sensory Control Panel
**Priority:** P0 (MVP)

Master controls for all sensory output.

- **Animations:** Off / Reduced / Full
- **Sounds:** Off / Quiet / Normal
- **Haptics:** Off / Gentle / Strong
- **Colour Intensity:** Muted / Standard / Vivid
- **Screen Brightness Override:** Dim / Auto / Bright
- Accessible from Settings (F10) and quick-access during workout
- Settings persist across sessions
- Changes apply immediately with no transition animation
- Individual overrides per feature (e.g., haptics off for celebrations but on for zone changes)

#### F132 Routine Builder & Scheduler
**Priority:** P1

Set a fixed weekly workout schedule for predictable structure.

- Define workout for each day of the week (or mark as rest day)
- Each workout has a fixed structure: warm-up duration, main duration, cool-down duration, target zone
- App reminds at the exact same time every day (user-configured)
- "Today's workout" shown on home screen matching the schedule
- Deviation from schedule requires explicit confirmation
- Schedule viewable as a full-week visual grid
- Copy a day's workout to other days for consistency

#### F133 Literal Voice Coach Mode
**Priority:** P1

Factual, calm, unambiguous voice coaching.

**Standard voice coach says:**
> "Awesome job! You're absolutely CRUSHING it! Keep pushing, you've got this!!!"

**Literal voice coach says:**
> "Heart rate: 162. Zone: Push. Burn Points earned: 8 of 12 target. Time remaining: 14 minutes."

Principles:
- No exclamation marks in TTS text
- No slang or colloquialisms ("crushing it", "beast mode", "let's go")
- No rhetorical questions ("Ready to push harder?")
- No metaphors ("climb the mountain", "dig deep")
- Numbers and facts only
- Calm, even tone (TTS voice selection: lowest energy option)
- Configurable verbosity: Minimal (zone changes only) / Standard (periodic updates) / Detailed (every 30 seconds)

#### F134 Predictable UI Lock
**Priority:** P0 (MVP)

Freeze the interface layout.

- When enabled, no UI element ever changes position
- No rotating banners, tip cards, or dynamic content
- No "what's new" pop-ups or feature announcements
- Home screen is always the same arrangement
- Workout screen elements in fixed positions
- No A/B tests or UI experiments applied to this user
- New features appear in a dedicated "Updates" section in Settings, never as pop-ups

#### F135 Social Pressure Shield
**Priority:** P1

One toggle to remove all social and comparative elements.

When enabled:
- Leaderboards (F20) hidden from all screens
- Activity feed (F21) hidden
- Friend workout notifications silenced
- "X people are working out now" hidden
- Comparative language removed ("You beat 80% of users" -> removed)
- Social sharing prompts never shown
- Profile set to private by default
- Individual social features can be re-enabled selectively

#### F136 Deep Data Dashboard
**Priority:** P2

Detailed statistics for data-oriented users.

- Raw heart rate data table (per-second readings)
- Zone time breakdown with exact seconds
- Statistical analysis: mean HR, median HR, standard deviation, min, max
- Heart rate variability (HRV) if sensor supports it
- Per-workout comparison graphs (overlay two workouts)
- Exportable CSV with all raw data
- Customisable dashboard — choose which metrics to display
- Trend analysis over 7/30/90 day windows
- No interpretation or "insights" unless requested — just the numbers

#### F137 Transition Warnings
**Priority:** P1

Advance notice before any change occurs.

- "Changing to cool-down phase in 30 seconds"
- "Workout will end in 2 minutes"
- "Screen will change to summary in 10 seconds"
- Countdown overlay for phase transitions
- Audio announcement of upcoming changes (in Literal Voice Coach style)
- Warning intervals configurable: 10s / 30s / 60s before transition
- No sudden screen changes — all transitions use a brief, predictable fade

#### F138 Calm Celebration Mode
**Priority:** P1

Quiet, understated success indicators.

- Workout complete: gentle checkmark animation (no confetti, no fireworks)
- Achievement earned: subtle badge glow with soft tone
- Level up: progress bar fills, quiet chime
- Streak milestone: text acknowledgement, no fanfare
- All celebrations complete in under 2 seconds
- No full-screen takeovers
- Option to disable all celebration animations entirely

#### F139 Predictable Reward Schedule
**Priority:** P1

Earn exactly X points for exactly Y effort. No randomness.

- Fixed formula: 1-3 Burn Points per minute based on zone (Active=1, Push=2, Peak=3)
- No random bonus drops
- No variable multipliers (unless user explicitly enables streak multiplier)
- Points calculation explained in a persistent tooltip
- Estimated points shown before workout starts
- No "surprise" rewards or loot boxes
- End-of-workout points match the running total exactly (no hidden bonuses)

#### F140 Safe Exit Protocol
**Priority:** P0 (MVP)

End a workout at any time with zero guilt or friction.

- Single "End Workout" button, always visible
- One tap to end — no "Are you sure?" confirmation dialog
- No "You'll lose your streak!" warnings
- No "You were so close to..." messages
- End message: **"Workout saved. Well done."** (always the same)
- Partial workout saved and counted normally
- Partial workout earns proportional Burn Points (no penalty for short sessions)
- No post-workout screen asking "Why did you stop early?"

#### F141 Texture & Pattern Zones
**Priority:** P2

Zone indicators use distinct patterns and textures, not just colour.

- Zone 1 (Rest): Dotted pattern
- Zone 2 (Warm-Up): Horizontal lines
- Zone 3 (Active): Diagonal hatching
- Zone 4 (Push): Cross-hatching
- Zone 5 (Peak): Solid fill
- Patterns visible on zone bars, heart rate display, and workout timeline
- Supports users with colour vision differences or sensory processing that doesn't rely on colour alone
- Patterns consistent everywhere zones appear (never colour-only)

#### F142 Minimal Mode
**Priority:** P1

Strip the workout UI to absolute essentials.

Screen shows only:
- Current heart rate (large number, centre)
- Current zone name (text below HR)
- Elapsed time
- Burn Points earned

Nothing else:
- No zone bar
- No graphs
- No avatar
- No animations
- No secondary stats
- Black or dark grey background
- Maximum contrast text
- Largest possible font sizes

#### F143 Pre-Workout Visual Schedule
**Priority:** P2

Show the entire workout structure upfront before starting.

- Visual timeline showing all phases: warm-up, intervals, cool-down
- Each phase shows: duration, target zone, expected effort
- Timeline is scrollable and zoomable
- "Start" button only appears after user has reviewed the schedule
- During workout, current position is highlighted on the same timeline
- No deviations from the shown schedule (unless user manually overrides)

#### F144 Consistent Audio Palette
**Priority:** P2

Same sounds always mean the same thing.

- Zone change up: rising tone (always the same tone)
- Zone change down: falling tone (always the same tone)
- Point earned: soft click (always the same click)
- Workout start: single chime
- Workout end: double chime
- Phase transition: triple chime
- No randomised sound effects, ever
- No seasonal or themed sound changes
- Sound mapping documented in Settings so user can learn and predict every sound
- Sounds never change in pitch, volume, or character

#### F145 Shutdown Routine
**Priority:** P2

Guided, predictable post-workout wind-down.

1. Workout ends -> "Cool-down complete" text (3 seconds)
2. Breathing guide: 4 seconds in, 4 seconds hold, 4 seconds out (1 minute)
3. Stretching prompt: "Take a moment to stretch" (30 seconds, optional skip)
4. Summary screen: stats displayed in fixed layout
5. "Session complete" — app returns to home screen

- Same sequence every single time
- Each step has a visible countdown
- No prompts to share, rate, or review
- No upsell or suggestion screens
- User can enable/disable individual steps but the order never changes

---

## 5. AuDHD Combined Mode

### 5.1 Overview

ADHD and ASD commonly co-occur ("AuDHD"). These users need elements from both modes but with careful defaults since some ADHD features conflict with ASD needs.

### 5.2 AuDHD Default Settings

The following defaults are suggested when AuDHD profile is selected. All remain individually configurable.

| Setting | Source | AuDHD Default | Rationale |
|---------|--------|---------------|-----------|
| Micro-rewards (F111) | ADHD | **On** | Dopamine without sensory overload |
| Just 5 Min (F112) | ADHD | **On** | Reduces initiation barrier |
| Variable drops (F113) | ADHD | **Off** | Unpredictability conflicts with ASD needs |
| Streak multiplier (F114) | ADHD | **On** (visible, predictable formula) | Motivation + predictability |
| Time blindness timer (F115) | ADHD | **On** | Useful for both profiles |
| Novelty engine (F116) | ADHD | **Off** | UI changes conflict with ASD needs |
| Celebration overkill (F130) | ADHD | **Off** | Sensory overload risk |
| Sensory control (F131) | ASD | **On** (Reduced level) | Essential for comfort |
| Routine builder (F132) | ASD | **On** | Structure helps both ADHD and ASD |
| Literal voice coach (F133) | ASD | **On** | Clear communication benefits both |
| Predictable UI lock (F134) | ASD | **On** | Reduces overwhelm |
| Social pressure shield (F135) | ASD | **On** | Removes anxiety source |
| Calm celebration (F138) | ASD | **On** | Replaces celebration overkill |
| Predictable rewards (F139) | ASD | **Partial** — fixed base + visible streak multiplier | Structure + motivation |
| Safe exit (F140) | ASD | **On** | Essential for both profiles |
| XP & Leveling (F120) | ADHD | **On** | Long-term motivation with predictable progression |
| Daily quests (F121) | ADHD | **On** (same 3 quest types daily) | Structure + freshness compromise |
| Quick start (F119) | ADHD | **On** | Reduces friction for both |
| Task chunking (F125) | ADHD | **On** | Manageable segments benefit both |
| Minimal mode (F142) | ASD | **Off** (available) | Some AuDHD users need more stimulation |
| Transition warnings (F137) | ASD | **On** | Prevents surprise for both |

### 5.3 Conflict Resolution

When ADHD and ASD settings directly conflict, AuDHD mode defaults to the **ASD setting** (comfort over stimulation) but surfaces the ADHD alternative as a clearly labelled toggle:

> **Celebrations:** Calm mode is on. *Want more excitement? Turn on Celebration Overkill in ADHD settings.*

This ensures the user is never overwhelmed by default but can opt into more stimulation.

---

## 6. Data Model Additions

### 6.1 UserProfile Extensions

```kotlin
// Added to existing UserProfile entity
data class UserProfile(
    // ... existing fields ...
    val ndProfile: NdProfile = NdProfile.STANDARD,
    val adhdFocusMode: Boolean = false,
    val asdComfortMode: Boolean = false,
    val xpLevel: Int = 1,
    val totalXp: Long = 0L,
    val sensoryLevel: SensoryLevel = SensoryLevel.STANDARD
)

enum class NdProfile {
    STANDARD,  // Default experience
    ADHD,      // ADHD Focus Mode
    ASD,       // ASD Comfort Mode
    AUDHD      // Combined mode
}

enum class SensoryLevel {
    MUTED,     // Reduced colours, no animations, quiet sounds
    STANDARD,  // Default sensory output
    VIVID      // Enhanced colours, full animations, louder sounds
}
```

### 6.2 New Entities

```kotlin
// Daily quest system (ADHD)
@Entity(tableName = "daily_quests")
data class DailyQuest(
    @PrimaryKey val id: String,
    val date: LocalDate,
    val title: String,
    val description: String,
    val type: QuestType,           // DURATION, ZONE_TARGET, BURN_POINTS
    val targetValue: Int,
    val currentValue: Int = 0,
    val completed: Boolean = false,
    val xpReward: Int
)

// Reward shop items (ADHD)
@Entity(tableName = "reward_shop_items")
data class RewardShopItem(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: RewardCategory,  // THEME, ANIMATION, AVATAR, STREAK_SHIELD
    val xpCost: Int,
    val owned: Boolean = false,
    val equipped: Boolean = false,
    val availableFrom: LocalDate?,
    val availableUntil: LocalDate?
)

// Progress visualisation entries (ADHD)
@Entity(tableName = "progress_visualisations")
data class ProgressVisualisation(
    @PrimaryKey val id: String,
    val userId: String,
    val date: LocalDate,
    val type: VisualisationType,   // HEAT_MAP, MOUNTAIN, GARDEN
    val dataJson: String,          // Serialised visualisation data
    val totalBurnPoints: Int,
    val totalMinutes: Int
)

// Weekly routine schedule (ASD)
@Entity(tableName = "weekly_routines")
data class WeeklyRoutine(
    @PrimaryKey val id: String,
    val userId: String,
    val dayOfWeek: DayOfWeek,
    val isRestDay: Boolean = false,
    val warmUpMinutes: Int = 5,
    val mainMinutes: Int = 20,
    val coolDownMinutes: Int = 5,
    val targetZone: HeartRateZone = HeartRateZone.ACTIVE,
    val scheduledTime: LocalTime?,
    val reminderEnabled: Boolean = true
)

// Sensory preferences (ASD)
@Entity(tableName = "sensory_preferences")
data class SensoryPreferences(
    @PrimaryKey val userId: String,
    val animationLevel: AnimationLevel = AnimationLevel.FULL,    // OFF, REDUCED, FULL
    val soundLevel: SoundLevel = SoundLevel.NORMAL,              // OFF, QUIET, NORMAL
    val hapticLevel: HapticLevel = HapticLevel.STRONG,           // OFF, GENTLE, STRONG
    val colourIntensity: ColourIntensity = ColourIntensity.STANDARD, // MUTED, STANDARD, VIVID
    val celebrationStyle: CelebrationStyle = CelebrationStyle.STANDARD, // CALM, STANDARD, OVERKILL
    val voiceCoachStyle: VoiceCoachStyle = VoiceCoachStyle.STANDARD,    // LITERAL, STANDARD, HYPE
    val voiceCoachVerbosity: Verbosity = Verbosity.STANDARD      // MINIMAL, STANDARD, DETAILED
)
```

### 6.3 Enums

```kotlin
enum class AnimationLevel { OFF, REDUCED, FULL }
enum class SoundLevel { OFF, QUIET, NORMAL }
enum class HapticLevel { OFF, GENTLE, STRONG }
enum class ColourIntensity { MUTED, STANDARD, VIVID }
enum class CelebrationStyle { CALM, STANDARD, OVERKILL }
enum class VoiceCoachStyle { LITERAL, STANDARD, HYPE }
enum class Verbosity { MINIMAL, STANDARD, DETAILED }
enum class QuestType { DURATION, ZONE_TARGET, BURN_POINTS }
enum class RewardCategory { THEME, ANIMATION, AVATAR, STREAK_SHIELD }
enum class VisualisationType { HEAT_MAP, MOUNTAIN, GARDEN }
```

---

## 7. Cross-References

### Features Modified by ND Profiles

| Base Feature | ADHD Impact | ASD Impact |
|-------------|-------------|------------|
| F1 Onboarding | Add profile selection step | Add profile selection step |
| F4 HR Zone Engine | Zone changes trigger micro-rewards (F111) | Zone changes use texture patterns (F141) |
| F5 Burn Points | Points trigger dopamine animations | Points use predictable fixed formula (F139) |
| F6 Voice Coach | Hype mode option | Literal mode option (F133) |
| F7 Streak System | Streak multiplier (F114), streak shield | No streak-loss guilt messaging (F140) |
| F9 Haptic Feedback | Fidget haptics option (F123) | Calming haptics, full control (F131) |
| F10 Settings | ADHD settings section | ASD settings section, sensory panel (F131) |
| F20 Leaderboards | Social contracts (F128) | Hidden by shield (F135) |
| F21 Activity Feed | Body double count (F117) | Hidden by shield (F135) |

### Sensory Control Panel (F131) Integration Points

F131 must be referenced and respected by every feature that produces:
- Visual output: F111 (micro-rewards), F113 (variable drops), F116 (novelty), F122 (progress viz), F130 (celebrations)
- Audio output: F6 (voice coach), F133 (literal voice coach), F111 (point sounds), F113 (drop sounds), F130 (celebration sounds), F144 (audio palette)
- Haptic output: F9 (haptics), F111 (point pulses), F123 (fidget), F145 (shutdown routine)

### Profile Selection Access Points

- F1 Onboarding: Initial profile selection
- F10 Settings: Change profile anytime
- Quick-access: Sensory Control Panel (F131) available during workout via overlay
