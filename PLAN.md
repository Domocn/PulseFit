# PulseFit - Android Fitness App Plan

## 1. Concept Overview

**PulseFit** is a heart-rate-zone-based group and solo fitness app that gamifies workouts through a proprietary points system called **"Burn Points"**. Users earn Burn Points by spending time in elevated heart rate zones during any workout. The app tracks real-time heart rate data from wearables and fitness trackers, displays live zone feedback, and rewards sustained effort with points that accumulate over sessions.

PulseFit is the first fitness app designed with neurodivergent users in mind. A toggleable **Neurodivergent Profile System** offers four modes — Standard, ADHD Focus Mode (dopamine-first, zero-friction, novelty-driven), ASD Comfort Mode (predictable, calm, sensory-controlled), and AuDHD Combined Mode — that reshape the entire UX. See `NEURODIVERGENT_DESIGN.md` for the full design philosophy and feature specifications.

> **Current Status:** Production-ready native Android app. BUILD SUCCESSFUL. 76+ features implemented. Firebase backend operational. Beta-ready for Play Store submission.

> **Trademark / IP Note:** All naming, branding, scoring mechanics, and zone definitions in this document are original. No trademarked names, proprietary zone labels, or copyrighted visual designs from any existing fitness brand are used. "Burn Points" and "PulseFit" are working names — a trademark search should be conducted before launch.

---

## 2. Core Mechanics

### 2.1 Heart Rate Zones

The app uses five colour-coded heart rate zones based on each user's **estimated or tested max heart rate (MHR)**. MHR is calculated via the standard formula (`220 - age`) or entered manually from a lab/field test.

| Zone | Name         | % of MHR    | Colour  | Burn Points / min |
|------|------------- |-------------|---------|-------------------|
| 1    | Rest         | < 50%       | Grey    | 0                 |
| 2    | Warm-Up      | 50-59%      | Blue    | 0                 |
| 3    | Active       | 60-69%      | Green   | 1                 |
| 4    | Push         | 70-84%      | Orange  | 2                 |
| 5    | Peak         | 85-100%     | Red     | 3                 |

- Zones 1-2 are recovery / warm-up and earn no points.
- Zones 3-5 earn escalating Burn Points per full minute spent in that zone.
- A **partial minute** at the end of a workout rounds down (no fractional points).

**Implementation:** `ZoneCalculator` with configurable `ZoneThresholds` (defaults: warmUp=50, active=60, push=70, peak=85). `HeartRateZone` enum with `pointsPerMinute` property.

### 2.2 Burn Points Scoring

| Metric               | Detail                                                    |
|-----------------------|-----------------------------------------------------------|
| **Daily Target**      | 12 Burn Points (configurable by user, default = 12)       |
| **Session Goal**      | Hit your daily target during a single workout              |
| **Streak Bonus**      | +2 bonus points for 3+ consecutive days hitting target     |
| **Weekly Summary**    | Total Burn Points, avg per session, time in each zone      |
| **Monthly Challenge** | Community leaderboard, opt-in, ranked by total points      |

### 2.3 Afterburn Estimate

After each session, the app shows an **Afterburn Estimate** — an estimated additional calorie burn over the next 24 hours based on time spent in Zones 4-5 (EPOC effect). This is calculated using a simplified model:

```
afterburn_kcal = (minutes_in_zone_4 * 1.5 + minutes_in_zone_5 * 2.5) * body_weight_kg * 0.05
```

This is clearly labeled as an **estimate** (not medical advice).

---

## 3. Feature Set

### 3.1 Implemented (v1.0) — 76+ Features

| # | Feature | Status |
|---|---------|--------|
| 1 | **User Onboarding** — Age, weight, height, resting HR, ND profile -> auto-calculate MHR & zones | COMPLETE |
| 2 | **Live Workout Screen** — Real-time HR, current zone colour, Burn Points accumulator, elapsed time | COMPLETE |
| 3 | **Workout Summary** — Total Burn Points, time per zone, calories, Afterburn Estimate | COMPLETE |
| 4 | **History & Trends** — Calendar view, weekly/monthly Burn Point trends | COMPLETE |
| 5 | **Heart Rate Device Pairing** — Bluetooth LE pairing for chest straps & wrist monitors | COMPLETE |
| 6 | **Google Health Connect** — Bi-directional sync — read HR, write workout summaries | COMPLETE |
| 7 | **Profile & Settings** — Edit MHR, zone thresholds, daily target, units, ND profile | COMPLETE |
| 8 | **Notifications** — Workout reminders, streak alerts, weekly summary push | COMPLETE |
| 9 | **Workout Templates** — 26 templates across 5 categories (Standard, Beginner, Advanced, Specialty, OTF-Style) | COMPLETE |
| 10 | **Custom Workout Builder** — Segment-based routine builder with drag-to-reorder | COMPLETE |
| 11 | **Social & Leaderboards** — Friends, leaderboard, feed, accountability contracts | COMPLETE |
| 12 | **Group System** — Firebase groups with invite codes, events, challenges | COMPLETE |
| 13 | **Challenge System** — 14 challenge types from single-session to month-long | COMPLETE |
| 14 | **Voice Coach** — Composable clip queue with 3 styles, ND verbosity, coaching targets | COMPLETE |
| 15 | **Exercise Registry** — 20 exercises across tread/row/floor with form cues | COMPLETE |
| 16 | **Equipment Profiling** — 16 equipment items, 4 environments, JSON profiles | COMPLETE |
| 17 | **Weekly Plan Generator** — Auto-generates plans with calendar sync | COMPLETE |
| 18 | **Tread Mode Intelligence** — Runner vs Power Walker coaching targets | COMPLETE |
| 19 | **Visual Exercise Guides** — Lottie-animated demonstrations | COMPLETE |
| 20 | **Template Picker** — Bottom sheet with category grouping | COMPLETE |
| 21 | **Data Export** — CSV and JSON export | COMPLETE |
| 22 | **Accessibility** — TalkBack, semantics, live regions, form errors, reduced motion | COMPLETE |
| 23 | **ADHD Features (F111-F130)** — 20 features: micro-rewards, XP, quests, body double, etc. | COMPLETE |
| 24 | **ASD Features (F131-F145)** — 15 features: sensory control, literal voice, safe exit, etc. | COMPLETE |

### 3.2 Planned (v2.0)

| # | Feature | Status |
|---|---------|--------|
| 25 | **Instructor Dashboard** — Tablet/TV view showing all participants' live zones and points | PLANNED |
| 26 | **Wearable Companion App** — Wear OS companion showing live zone and points on wrist | PLANNED |
| 27 | **Fitbit / Garmin / Samsung** — Direct API integration beyond Health Connect | PLANNED |
| 28 | **AI Coach Suggestions** — Post-workout tips based on zone distribution | PLANNED |
| 29 | **Apple Health (iOS)** — iOS port with HealthKit integration | PLANNED |

---

## 4. Technical Architecture

### 4.1 Platform & Language

| Layer        | Technology                                |
|-------------|------------------------------------------|
| Language     | **Kotlin** (100%)                        |
| UI           | **Jetpack Compose** + Material 3         |
| Min SDK      | API 26 (Android 8.0)                     |
| Target SDK   | API 36 (Android 16)                      |
| Compile SDK  | API 36                                   |
| Build        | AGP 9.0.1, Gradle with Kotlin DSL, version catalogs |
| Kotlin       | 2.2.10 (bundled with AGP 9)             |

### 4.2 App Architecture

```
+-------------------------------------------------------+
|                      UI Layer                          |
|            Jetpack Compose + ViewModels                |
|  39 navigation routes across 7 sections               |
+-------------------------------------------------------+
|                    Domain Layer                        |
|  ZoneCalculator     | VoiceCoachEngine                |
|  BurnPointsCalc     | WeeklyPlanGenerator             |
|  TemplateRegistry   | ExerciseRegistry                |
|  ChallengeRegistry  | CoachingTargetRegistry          |
|  GuidedWorkoutMgr   | CalendarSync                    |
+-------------------------------------------------------+
|                    Data Layer                          |
|  Room DB (v10)      | Health Connect                  |
|  Firebase Auth      | Firestore (10 collections)      |
|  BLE Service        | DataStore                       |
|  WorkManager        |                                 |
+-------------------------------------------------------+
```

**Pattern:** MVVM + Clean Architecture (UI -> ViewModel -> UseCase -> Repository -> DataSource)

### 4.3 Key Libraries & APIs

| Concern               | Library / API                                        | Version |
|-----------------------|------------------------------------------------------|---------|
| Build                 | Android Gradle Plugin                                | 9.0.1   |
| Kotlin                | Kotlin (bundled with AGP 9)                          | 2.2.10  |
| KSP                   | Kotlin Symbol Processing                             | 2.2.10-2.0.2 |
| DI                    | Hilt                                                 | 2.59+   |
| Database              | Room                                                 | Latest  |
| Preferences           | DataStore (Proto)                                    | Latest  |
| Async                 | Kotlin Coroutines + Flow                             | Latest  |
| Navigation            | Compose Navigation                                   | Latest  |
| Charts                | Vico (Compose-native charting)                       | Latest  |
| Animations            | Lottie (exercise guides)                             | Latest  |
| Bluetooth LE          | Android BLE API                                      | Native  |
| Health data           | Health Connect API                                   | 1.1.0-alpha12 |
| Background work       | WorkManager                                          | Latest  |
| Auth                  | Firebase Auth (email + Google)                       | Latest  |
| Backend               | Firebase Firestore                                   | Latest  |
| Crash reporting       | Firebase Crashlytics                                 | Latest  |
| Serialization         | Gson (JSON zone maps, equipment profiles)            | Latest  |
| Voice Coach           | Android TTS + composable clip queue                  | Native  |
| Compose compiler      | kotlin.plugin.compose                                | Bundled |

### 4.4 Firebase Architecture

**Authentication:** Firebase Auth with email/password and Google Sign-In. Email/password validation enforced.

**Firestore Collections (10):**

| Collection | Purpose | Security |
|------------|---------|----------|
| `users` | User profiles, public profiles | Owner read/write |
| `sharedWorkouts` | Activity feed (shared workout data) | Friends read, owner write |
| `friendRequests` | Friend request management | Sender/receiver access |
| `activeSessions` | Body double live workout sessions | Participant access |
| `accountabilityContracts` | Accountability partnerships | Partner access |
| `accountabilityContracts/{id}/weekly` | Weekly progress tracking | Partner access |
| `groups` | Workout groups | Member read, admin write |
| `groups/{id}/members` | Group membership | Member access |
| `groups/{id}/events` | Group events | Member read, admin write |
| `groups/{id}/challenges` | Group challenges | Member access |
| `groups/{id}/stats` | Weekly group stats | Member read |
| `users/{uid}/challenges` | Individual challenge progress | Owner access |
| `users/{uid}/friends` | Friend lists | Owner access |

**Security:** Firestore rules created for all collections. Admin auth checks on group operations. SecureRandom 8-char invite codes. `memberUids` array queries for efficient membership lookups.

### 4.5 Health Connect Integration

Health Connect is the primary hub for reading/writing health data on Android.

**Implementation note:** Uses `@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")` for `Metadata()` no-arg constructor compatibility with Health Connect 1.1.0-alpha12. Fully qualified `androidx.health.connect.client.records.metadata.Metadata()` to avoid conflict with `kotlin.Metadata`.

**Read:** HeartRateRecord, RestingHeartRateRecord, ExerciseSessionRecord
**Write:** ExerciseSessionRecord, TotalCaloriesBurnedRecord, HeartRateRecord

### 4.6 Bluetooth LE Heart Rate Service

Standard Bluetooth Heart Rate Profile (UUID `0x180D`):
- Characteristic `0x2A37` — Heart Rate Measurement (notify)
- `HeartRateSource` interface with `RealBleHeartRateSource` and `SimulatedHeartRateSource`
- BLE logs wrapped in `BuildConfig.DEBUG` (requires `buildConfig = true` in buildFeatures)

### 4.7 Build & Security

| Concern | Implementation |
|---------|---------------|
| ProGuard | Enabled for release builds |
| Crashlytics | Firebase Crashlytics integrated |
| Backup rules | Exclude Room database from auto-backup |
| Network security | Cleartext traffic disabled via network security config |
| KSP | `android.disallowKotlinSourceSets=false` in gradle.properties |
| Compose | `kotlin.plugin.compose` plugin explicitly applied |
| Migration | `fallbackToDestructiveMigration(dropAllTables = true)` |

---

## 5. Data Model (Room — DB Version 10)

### Core Entities

```kotlin
@Entity
data class UserProfileEntity(
    @PrimaryKey val id: Long = 1,
    val age: Int,
    val weightKg: Float,
    val heightCm: Float,
    val restingHr: Int,
    val maxHr: Int,
    val dailyBurnTarget: Int,
    val unitSystem: UnitSystem,
    val ndProfile: NdProfile,
    val adhdFocusMode: Boolean,
    val asdComfortMode: Boolean,
    val xpLevel: Int,
    val totalXp: Long,
    val sensoryLevel: SensoryLevel,
    val treadMode: String?,             // v10: Runner or PowerWalker
    val equipmentProfileJson: String?   // v10: JSON-serialized EquipmentProfile
)

@Entity
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,      // epoch millis
    val endTime: Long,
    val totalBurnPoints: Int,
    val caloriesBurned: Int,
    val afterburnEstimate: Int,
    val avgHr: Int,
    val maxHr: Int,
    val notes: String? = null,
    val zoneMapJson: String?,           // JSON zone durations via Gson
    val treadMode: String?              // v10: Runner or PowerWalker
)

@Entity
data class HeartRateReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val timestamp: Long,
    val heartRate: Int,
    val zone: Int
)

@Entity
data class SensoryPreferencesEntity(
    @PrimaryKey val id: Long = 1,
    val animations: String,      // OFF, REDUCED, FULL
    val sounds: String,
    val haptics: String,
    val colourIntensity: String,
    val confetti: String,
    val screenShake: Boolean
)

@Entity
data class WeeklyRoutineEntity(
    @PrimaryKey val dayOfWeek: Int,
    val workoutTemplateId: Long?,
    val scheduledTime: String?,
    val isRestDay: Boolean = false
)

@Entity
data class DailyQuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val questType: String,
    val description: String,
    val xpReward: Int,
    val completed: Boolean = false
)

@Entity
data class AchievementEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val xpReward: Int,
    val unlocked: Boolean = false,
    val unlockedAt: Long? = null
)

@Entity
data class NotificationPreferencesEntity(
    @PrimaryKey val id: Long = 1,
    val workoutReminder: Boolean,
    val streakAtRisk: Boolean,
    val streakMilestone: Boolean,
    val weeklySummary: Boolean,
    val personalBest: Boolean,
    val inactivityNudge: Boolean
)
```

### Enums

```kotlin
enum class NdProfile { STANDARD, ADHD, ASD, AUDHD }
enum class SensoryLevel { MUTED, STANDARD, VIVID }
enum class UnitSystem { METRIC, IMPERIAL }
enum class HeartRateZone { REST, WARM_UP, ACTIVE, PUSH, PEAK }
enum class WorkoutEnvironment { GYM, HOME, OUTDOOR, HOTEL }
```

---

## 6. Screen Map (39 Navigation Routes)

```
Auth
  +-- login
  +-- sign_up

Onboarding (first launch)
  +-- welcome
  +-- profile_setup
  +-- nd_profile_selection
  +-- ble_onboarding
  +-- resting_hr
  +-- onboarding_summary

Main App (BottomNav: Home | Social | History | Settings)
  +-- Home
  |     +-- home (Today's Summary, Streak, Quick Start, Daily Quests)
  |     +-- progress_dashboard
  |
  +-- Workout
  |     +-- workout/{workoutId} (Live Workout Screen)
  |     +-- shutdown_routine/{workoutId}
  |     +-- summary/{workoutId}
  |     +-- pre_workout_schedule/{workoutId}
  |     +-- workout_templates (Template Picker)
  |
  +-- Social
  |     +-- social_hub (Social Hub)
  |     +-- friends
  |     +-- add_friend
  |     +-- leaderboard
  |     +-- feed
  |     +-- accountability
  |
  +-- Groups
  |     +-- groups
  |     +-- create_group
  |     +-- group_detail/{groupId}
  |     +-- join_group
  |
  +-- Challenges
  |     +-- challenges
  |     +-- challenge_detail/{challengeType}
  |
  +-- Plans
  |     +-- weekly_plan
  |     +-- equipment_setup
  |
  +-- History
  |     +-- history (Calendar View, Workout Detail, Trends)
  |
  +-- Settings
        +-- settings
        +-- sensory_settings
        +-- achievements
        +-- routine_builder
        +-- deep_data
        +-- reward_shop
```

---

## 7. Burn Points Algorithm (Detail)

```kotlin
class BurnPointsCalculator(private val maxHr: Int) {

    fun zoneFor(heartRate: Int): Int = when {
        heartRate < (maxHr * 0.50) -> 0  // below tracking
        heartRate < (maxHr * 0.60) -> 1  // Rest
        heartRate < (maxHr * 0.70) -> 2  // Warm-Up
        heartRate < (maxHr * 0.85) -> 3  // Active
        heartRate < (maxHr * 1.00) -> 4  // Push
        else                        -> 5  // Peak
    }

    fun pointsPerMinute(zone: Int): Int = when (zone) {
        3    -> 1
        4    -> 2
        5    -> 3
        else -> 0
    }
}
```

---

## 8. IP & Legal Safeguards

| Risk Area              | Mitigation                                                                               |
|------------------------|------------------------------------------------------------------------------------------|
| **App Name**           | "PulseFit" — run trademark search via USPTO TESS before launch                           |
| **Points System Name** | "Burn Points" — generic fitness terminology, not derived from any brand name              |
| **Zone Names**         | Rest / Warm-Up / Active / Push / Peak — common fitness vocabulary                        |
| **Zone Colours**       | Grey / Blue / Green / Orange / Red — standard HR zone palette used industry-wide          |
| **Scoring Formula**    | Original points-per-minute-per-zone model, not copied from any proprietary algorithm      |
| **UI/UX**              | Original screen layouts; do not replicate any specific brand's dashboard design           |
| **Marketing**          | Never reference competitors by name; position as "heart-rate-zone training" generically   |

---

## 9. Monetisation Options

| Model              | Detail                                                          |
|--------------------|-----------------------------------------------------------------|
| **Free Tier**      | Full solo workout tracking, basic history, Health Connect sync, all ND modes |
| **Premium ($4.99/mo)** | Group mode, advanced analytics, AI coach, custom templates, premium voice packs |
| **Gym/Studio License** | White-label instructor dashboard, bulk accounts             |

---

## 10. Project Structure

```
app/
+-- src/main/kotlin/com/pulsefit/app/
|   +-- di/                       # Hilt modules
|   +-- data/
|   |   +-- local/                # Room DB (v10), DAOs, DataStore
|   |   +-- health/               # Health Connect data source
|   |   +-- ble/                  # Bluetooth LE heart rate service
|   |   +-- voice/                # Voice clip playback, TTS
|   |   +-- firebase/             # Firebase Auth + Firestore repositories
|   |   +-- repository/           # Repository implementations
|   |   +-- challenge/            # Challenge tracking repositories
|   |   +-- plan/                 # Weekly plan repositories
|   |   +-- group/                # Group, event, challenge repositories
|   +-- domain/
|   |   +-- model/                # Domain models (Workout, Zone, BurnResult, Equipment)
|   |   +-- calculator/           # BurnPointsCalculator, AfterBurnEstimator, ZoneCalculator
|   |   +-- voice/                # VoiceCoachEngine, CoachingTargetRegistry
|   |   +-- nd/                   # ND profile logic, sensory preferences
|   |   +-- challenge/            # ChallengeRegistry, challenge definitions
|   |   +-- plan/                 # WeeklyPlanGenerator
|   |   +-- usecase/              # Use cases (StartWorkout, GetHistory, etc.)
|   +-- ui/
|   |   +-- theme/                # Material 3 dark theme, colours, typography
|   |   +-- onboarding/           # Onboarding screens (6 routes)
|   |   +-- auth/                 # Login, Sign Up
|   |   +-- home/                 # Home tab + Progress Dashboard
|   |   +-- workout/              # Live workout, pre/post, shutdown routine, templates
|   |   +-- history/              # History & trends
|   |   +-- settings/             # Profile & settings, sensory settings
|   |   +-- social/               # Social hub, friends, leaderboard, feed, accountability
|   |   +-- group/                # Groups, create, detail, join
|   |   +-- challenge/            # Challenge list, detail
|   |   +-- plan/                 # Weekly plan, equipment setup
|   |   +-- nd/                   # ND profile settings & controls
|   |   +-- adhd/                 # ADHD-specific UI components
|   |   +-- asd/                  # ASD-specific UI components
|   |   +-- rewards/              # XP, leveling, reward shop, daily quests, achievements
|   |   +-- components/           # Shared composables (ZoneBar, HrGauge, ExerciseGuideOverlay)
|   +-- util/                     # CalendarSync, GuidedWorkoutManager
+-- src/main/res/
|   +-- values/                   # Strings, colours, dimensions
|   +-- xml/                      # Network security config, backup rules
|   +-- drawable/                 # Icons, illustrations
+-- build.gradle.kts
```

---

## 11. Development Phases

### Phase 1 — Foundation (COMPLETE)
- Project scaffolding (Compose, Hilt, Room, Navigation)
- User profile entity + onboarding flow (6 screens)
- Zone calculation engine + unit tests
- Firebase Auth (email + Google Sign-In)

### Phase 2 — Core Workout Loop (COMPLETE)
- BLE heart rate connection service (`HeartRateSource` interface)
- Live workout screen with real-time zone display
- Burn Points accumulator
- Workout summary screen + Room persistence
- ADHD Focus Mode core: micro-rewards, zero-friction quick start, time blindness timer, task chunking

### Phase 3 — Health Connect & History (COMPLETE)
- Health Connect read/write integration (with Metadata workaround)
- Workout history list + calendar view
- Weekly/monthly trend charts (Vico)
- ASD Comfort Mode core: sensory control panel, predictable UI lock, literal voice coach, safe exit protocol

### Phase 4 — Social & Templates (COMPLETE)
- Firebase Firestore backend (10 collections, security rules)
- Social features: friends, leaderboard, feed, accountability
- Group system: groups, events, challenges, invite codes
- 26 workout templates (TemplateRegistry) across 5 categories
- Template picker with category grouping
- Custom workout builder (Routine Builder)
- Voice coach (composable clip queue with ND verbosity)
- Notifications + settings
- Full accessibility pass (semantics, live regions, form errors)

### Phase 5 — Advanced Features (COMPLETE)
- Exercise Registry (20 exercises with form cues)
- Visual exercise guides (Lottie animations)
- Coaching target registry (OTF-accurate targets)
- Tread Mode intelligence (Runner vs Power Walker)
- Challenge system (14 types: ChallengeRegistry)
- Equipment profiling (16 items, 4 environments)
- Weekly plan generator with calendar sync
- Body double mode (Firebase activeSessions)
- Deep data dashboard
- Reward shop + progress dashboard
- ProGuard + Crashlytics + network security hardening
- Comprehensive audit fixes (security, crashes, accessibility)

### Phase 6 — Market Launch (NEXT)
- Play Store beta submission
- Privacy policy and Terms of Service
- Performance profiling and optimization
- Premium tier implementation (in-app purchases)
- Instructor Dashboard (tablet-optimised view)
- Marketing assets and App Store listing
- Wear OS companion app
- iOS port planning

---

## 12. Testing Strategy

| Layer      | Approach                                           |
|-----------|---------------------------------------------------|
| Unit       | JUnit 5 + Turbine (Flow testing) — domain logic   |
| Integration| Room in-memory DB tests, Health Connect test shims |
| UI         | Compose UI tests (createComposeRule)               |
| E2E        | Manual + Firebase Test Lab on real devices          |

---

## 13. Summary

PulseFit is a production-ready native Android fitness app with **76+ features**, **26 workout templates**, **14 challenge types**, **20 exercises**, and **39 navigation routes**. It delivers the same motivating, heart-rate-zone-driven workout experience that has proven popular in boutique fitness — but as a standalone, brand-neutral app with the industry's first dedicated neurodivergent experience modes.

Built on AGP 9.0.1 with Kotlin 2.2.10, Jetpack Compose, Room (DB v10), and Firebase (Auth + Firestore with 10 collections), PulseFit serves both individual users and gym/studio operators. The composable voice coaching system with ND-aware verbosity, the comprehensive challenge system, and the equipment-aware weekly plan generator position PulseFit as a market-leading fitness app that no competitor matches in neurodivergent support.

**Key differentiator:** PulseFit is the only fitness app with dedicated ADHD Focus Mode, ASD Comfort Mode, and AuDHD Combined Mode — serving 10-13% of adults whose needs are ignored by every other fitness app on the market.
