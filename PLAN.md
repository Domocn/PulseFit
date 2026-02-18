# PulseFit — Project Plan

> A heart-rate zone fitness app that gamifies workouts through real-time Burn Points, voice coaching, and the first neurodivergent-aware fitness experience.

---

## Table of Contents

1. [Concept Overview](#1-concept-overview)
2. [Core Mechanics](#2-core-mechanics)
3. [Data Model](#3-data-model)
4. [Project Structure](#4-project-structure)
5. [Development Phases](#5-development-phases)
6. [Summary](#6-summary)

---

## 1. Concept Overview

### What PulseFit Does

PulseFit connects to Bluetooth Low Energy (BLE) heart rate monitors and turns every workout into a real-time game. Users earn **Burn Points** by spending time in their target heart rate zones. A voice coach provides live feedback, and a gamification layer (streaks, XP, achievements, leaderboards) drives long-term engagement.

### Key Market Differentiators

1. **Zone-Based Gamification** — Burn Points tied directly to physiological effort via HR zones, not just step counts or duration
2. **Real-Time Voice Coach** — Adaptive coaching that responds to live heart rate data
3. **Neurodivergent Profiles** — Industry-first ADHD Focus Mode, ASD Comfort Mode, and AuDHD Combined Mode. See [NEURODIVERGENT_DESIGN.md](NEURODIVERGENT_DESIGN.md) for full design philosophy and feature specs (F111-F145)
4. **Sensor Agnostic** — Works with any BLE heart rate monitor (chest strap, arm band, wearable)
5. **Privacy First** — All workout data stored locally; cloud sync optional and user-controlled

### Target Users

| Segment | Need | PulseFit Answer |
|---------|------|-----------------|
| General fitness | Motivation to stay consistent | Streaks, XP, leaderboards |
| ADHD users | Task initiation, dopamine, novelty | ADHD Focus Mode (F111-F130) |
| ASD users | Predictability, calm, control | ASD Comfort Mode (F131-F145) |
| AuDHD users | Structure + reward | Combined Mode with cherry-picked defaults |
| Data enthusiasts | Deep performance analytics | Deep Data Dashboard (F136) |

---

## 2. Core Mechanics

### Heart Rate Zones

| Zone | Name | % Max HR | Burn Points / Min |
|------|------|----------|-------------------|
| 1 | Rest | < 60% | 0 |
| 2 | Light | 60-69% | 1 |
| 3 | Moderate | 70-79% | 2 |
| 4 | Push | 80-89% | 3 |
| 5 | Max | 90-100% | 4 |

Max HR calculated as `220 - age` (configurable with manual override).

### Burn Points

- Earned in real-time based on current HR zone
- Accumulated per-workout and lifetime
- ADHD mode: points trigger micro-reward animations (F111) and can earn XP (F120)
- ASD mode: points follow a fixed, predictable formula (F139) with no random bonuses

### Voice Coach

- Real-time audio feedback based on HR zone, elapsed time, and points
- Three styles: Standard, Hype (ADHD), Literal (ASD/F133)
- Three verbosity levels: Minimal, Standard, Detailed
- Controlled by Sensory Control Panel (F131) in ASD mode

---

## 3. Data Model

### UserProfile

```kotlin
@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: String,
    val displayName: String,
    val age: Int,
    val maxHeartRate: Int,              // 220 - age, or manual override
    val restingHeartRate: Int?,
    val weight: Float?,                 // kg, optional
    val height: Float?,                 // cm, optional

    // Neurodivergent profile
    val ndProfile: NdProfile = NdProfile.STANDARD,
    val adhdFocusMode: Boolean = false,
    val asdComfortMode: Boolean = false,

    // Gamification
    val xpLevel: Int = 1,
    val totalXp: Long = 0L,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalBurnPoints: Long = 0L,
    val totalWorkouts: Int = 0,

    // Sensory
    val sensoryLevel: SensoryLevel = SensoryLevel.STANDARD,

    // Timestamps
    val createdAt: Instant,
    val lastWorkoutAt: Instant?
)

enum class NdProfile { STANDARD, ADHD, ASD, AUDHD }
enum class SensoryLevel { MUTED, STANDARD, VIVID }
```

### Workout

```kotlin
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey val id: String,
    val userId: String,
    val startedAt: Instant,
    val endedAt: Instant?,
    val durationSeconds: Int,
    val burnPoints: Int,
    val xpEarned: Int,
    val averageHeartRate: Int,
    val maxHeartRate: Int,
    val zoneTimeSeconds: Map<HeartRateZone, Int>,  // time in each zone
    val workoutType: WorkoutType,
    val isQuickStart: Boolean = false,              // F119
    val isJustFiveMin: Boolean = false              // F112
)
```

### ND-Specific Entities

See [NEURODIVERGENT_DESIGN.md - Data Model](NEURODIVERGENT_DESIGN.md#6-data-model-additions) for:
- `DailyQuest` — daily quest system (ADHD, F121)
- `RewardShopItem` — reward shop inventory (ADHD, F126)
- `ProgressVisualisation` — progress viz data (ADHD, F122)
- `WeeklyRoutine` — weekly schedule (ASD, F132)
- `SensoryPreferences` — sensory control settings (ASD, F131)

### HeartRateReading

```kotlin
@Entity(tableName = "heart_rate_readings")
data class HeartRateReading(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: String,
    val timestamp: Instant,
    val heartRate: Int,
    val zone: HeartRateZone,
    val burnPointsEarned: Int        // points earned this reading
)
```

---

## 4. Project Structure

```
app/src/main/java/com/example/pulsefit/
    |
    +-- data/
    |   +-- local/
    |   |   +-- dao/                  # Room DAOs
    |   |   +-- entity/               # Room entities
    |   |   +-- PulseFitDatabase.kt
    |   +-- repository/               # Repository implementations
    |   +-- model/                    # Domain models & enums
    |
    +-- domain/
    |   +-- usecase/                  # Business logic use cases
    |   +-- repository/               # Repository interfaces
    |
    +-- ui/
    |   +-- onboarding/               # F1 Onboarding + ND profile selection
    |   +-- home/                     # Home screen, quick start (F119)
    |   +-- workout/                  # Active workout screen
    |   +-- history/                  # Workout history & review
    |   +-- settings/                 # F10 Settings
    |   +-- leaderboard/              # F20 Leaderboards
    |   +-- social/                   # F21 Activity feed, friend system
    |   +-- theme/                    # App theming, colour palettes
    |   +-- components/               # Shared UI components
    |
    +-- voice/                        # Voice coach system
    |   +-- VoiceCoachEngine.kt       # Core TTS engine
    |   +-- StandardCoach.kt          # Standard voice style
    |   +-- HypeCoach.kt              # ADHD hype voice style
    |   +-- LiteralCoach.kt           # ASD literal voice style (F133)
    |
    +-- adhd/                         # ADHD Focus Mode features
    |   +-- MicroRewardEngine.kt      # F111 Micro-rewards
    |   +-- JustFiveMinManager.kt     # F112 "Just 5 Min"
    |   +-- VariableDropEngine.kt     # F113 Variable drops
    |   +-- StreakMultiplier.kt       # F114 Streak multiplier
    |   +-- TimeBlindnessTimer.kt     # F115 Time blindness timer
    |   +-- NoveltyEngine.kt          # F116 Novelty rotation
    |   +-- BodyDoubleManager.kt      # F117 Body double
    |   +-- XpLevelingSystem.kt       # F120 XP & leveling
    |   +-- DailyQuestManager.kt      # F121 Daily quests
    |   +-- RewardShopManager.kt      # F126 Reward shop
    |   +-- AntiBurnoutSystem.kt      # F127 Anti-burnout
    |   +-- CelebrationEngine.kt      # F130 Celebration overkill
    |
    +-- asd/                          # ASD Comfort Mode features
    |   +-- SensoryControlPanel.kt    # F131 Sensory controls
    |   +-- RoutineBuilder.kt         # F132 Routine builder
    |   +-- PredictableUiLock.kt      # F134 UI lock
    |   +-- SocialPressureShield.kt   # F135 Social shield
    |   +-- DeepDataDashboard.kt      # F136 Deep data
    |   +-- TransitionWarnings.kt     # F137 Transition warnings
    |   +-- CalmCelebration.kt        # F138 Calm celebrations
    |   +-- PredictableRewards.kt     # F139 Predictable rewards
    |   +-- SafeExitProtocol.kt       # F140 Safe exit
    |   +-- MinimalMode.kt            # F142 Minimal mode
    |   +-- ShutdownRoutine.kt        # F145 Shutdown routine
    |
    +-- nd/                           # Shared neurodivergent infrastructure
    |   +-- NdProfileManager.kt       # Profile selection & management
    |   +-- NdSettingsApplier.kt      # Apply profile defaults to settings
    |   +-- AuDhdConflictResolver.kt  # Resolve ADHD/ASD setting conflicts
    |
    +-- ble/                          # Bluetooth LE heart rate monitor
    |   +-- BleScanner.kt
    |   +-- HeartRateMonitorService.kt
    |   +-- BleConnectionManager.kt
    |
    +-- util/                         # Shared utilities
        +-- ZoneCalculator.kt
        +-- BurnPointsCalculator.kt
        +-- TimeFormatter.kt
```

---

## 5. Development Phases

### Phase 1 — MVP Core (Weeks 1-4)

**Goal:** Minimal working app — connect HR monitor, show zones, earn points.

| Feature | Description |
|---------|-------------|
| F2 | BLE HR Monitor pairing |
| F3 | Real-time HR display |
| F4 | HR Zone Engine (5 zones) |
| F5 | Burn Points calculation |
| F8 | Basic workout history |
| F119 | Quick Start (ADHD P0) |
| F140 | Safe Exit Protocol (ASD P0) |
| Basic UI | Home, workout, summary screens |

**ND in Phase 1:** Quick Start and Safe Exit are P0 features that benefit all users, not just ND users. They ship in MVP as core UX.

### Phase 2 — ADHD Core + Gamification (Weeks 5-8)

**Goal:** ADHD Focus Mode operational. Core gamification live.

| Feature | Description |
|---------|-------------|
| F1 | Onboarding with ND profile selection |
| F7 | Streak system |
| F10 | Settings (including ND settings) |
| F111 | Micro-Reward Engine (P0) |
| F112 | "Just 5 Min" Mode (P0) |
| F114 | Streak Multiplier (P1) |
| F115 | Time Blindness Timer (P1) |
| F119 | Quick Start refinement |
| F120 | XP & Leveling (P1) |
| F121 | Daily Quests (P1) |
| F125 | Task Chunking (P1) |
| F130 | Celebration Overkill (P1) |
| ND infra | NdProfileManager, NdSettingsApplier |

### Phase 3 — ASD Core + Voice Coach (Weeks 9-12)

**Goal:** ASD Comfort Mode operational. Voice coach with all styles.

| Feature | Description |
|---------|-------------|
| F6 | Voice Coach (Standard + Hype + Literal) |
| F9 | Haptic Feedback system |
| F131 | Sensory Control Panel (P0) |
| F132 | Routine Builder & Scheduler (P1) |
| F133 | Literal Voice Coach Mode (P1) |
| F134 | Predictable UI Lock (P0) |
| F135 | Social Pressure Shield (P1) |
| F137 | Transition Warnings (P1) |
| F138 | Calm Celebration Mode (P1) |
| F139 | Predictable Reward Schedule (P1) |
| F142 | Minimal Mode (P1) |
| AuDHD | AuDhdConflictResolver, combined defaults |

### Phase 4 — Social & Polish (Weeks 13-16)

**Goal:** Social features, leaderboards, full settings.

| Feature | Description |
|---------|-------------|
| F11 | Workout Timer (advanced) |
| F13 | Post-Workout Summary |
| F14 | Achievement Badges |
| F15 | Weekly Goals |
| F20 | Leaderboards (respects F135 shield) |
| F21 | Activity Feed (respects F135 shield) |
| F22 | Friend System |
| F113 | Variable Reward Drops (P1) |
| F127 | Anti-Burnout System (P2) |

### Phase 5 — Advanced ND + Data (Weeks 17-20)

**Goal:** Deep ND features, data analytics, polish.

| Feature | Description |
|---------|-------------|
| F116 | Novelty Engine (P2) |
| F117 | Body Double Mode (P2) |
| F122 | Progress Visualisation (P2) |
| F126 | Reward Shop (P2) |
| F129 | Parallel Stimulation (P2) |
| F136 | Deep Data Dashboard (P2) |
| F141 | Texture & Pattern Zones (P2) |
| F143 | Pre-Workout Visual Schedule (P2) |
| F144 | Consistent Audio Palette (P2) |
| F145 | Shutdown Routine (P2) |

### Phase 6 — Advanced & Niche (Weeks 21+)

**Goal:** Remaining P3 features, optimisation, accessibility audit.

| Feature | Description |
|---------|-------------|
| F118 | Hyperfocus Badge (P3) |
| F123 | Fidget Haptics (P3) |
| F124 | Accountability Alarm (P3) |
| F128 | Social Contracts (P3) |
| F12 | Cool-Down Detection |
| F16 | Calorie Estimation |
| F17 | Workout Types / Templates |
| F18 | Rest Timer |
| F19 | Progress Dashboard (general) |
| | Accessibility audit, performance optimisation |

---

## 6. Summary

PulseFit is a **heart-rate zone fitness app** that combines real-time physiological data with gamification to motivate users across all neurotypes.

**What makes PulseFit different:**

- **Zone-based Burn Points** tie rewards to real effort, not vanity metrics
- **ADHD Focus Mode** (F111-F130) provides the dopamine, novelty, and low friction that ADHD brains need to initiate and sustain exercise
- **ASD Comfort Mode** (F131-F145) provides the predictability, sensory control, and literal communication that autistic users need to feel safe and in control
- **AuDHD Combined Mode** carefully resolves conflicts between ADHD and ASD needs with thoughtful defaults
- **Every ND setting is individually tuneable** — profiles set defaults, users set preferences

PulseFit is positioned as the **first neurodivergent-aware fitness app** — not as a niche product, but as a better product for everyone. Sensory controls, predictable rewards, low-friction starts, and honest communication benefit all users. The ND modes simply make these benefits the default for users who need them most.

---

*See also:*
- [FEATURES.md](FEATURES.md) — Complete feature list with priorities and ND annotations
- [NEURODIVERGENT_DESIGN.md](NEURODIVERGENT_DESIGN.md) — Full ND design philosophy, all features F111-F145, data model
