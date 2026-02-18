# PulseFit - Android Fitness App Plan

## 1. Concept Overview

**PulseFit** is a heart-rate-zone-based group and solo fitness app that gamifies workouts through a proprietary points system called **"Burn Points"**. Users earn Burn Points by spending time in elevated heart rate zones during any workout. The app tracks real-time heart rate data from wearables and fitness trackers, displays live zone feedback, and rewards sustained effort with points that accumulate over sessions.

> **Trademark / IP Note:** All naming, branding, scoring mechanics, and zone definitions in this document are original. No trademarked names, proprietary zone labels, or copyrighted visual designs from any existing fitness brand are used. "Burn Points" and "PulseFit" are working names — a trademark search should be conducted before launch.

---

## 2. Core Mechanics

### 2.1 Heart Rate Zones

The app uses five colour-coded heart rate zones based on each user's **estimated or tested max heart rate (MHR)**. MHR is calculated via the standard formula (`220 - age`) or entered manually from a lab/field test.

| Zone | Name         | % of MHR   | Colour  | Burn Points / min |
|------|------------- |-----------|---------|-------------------|
| 1    | Rest         | 50 – 60%  | Grey    | 0                 |
| 2    | Warm-Up      | 61 – 70%  | Blue    | 0                 |
| 3    | Active       | 71 – 83%  | Green   | 1                 |
| 4    | Push         | 84 – 91%  | Orange  | 2                 |
| 5    | Peak         | 92 – 100% | Red     | 3                 |

- Zones 1–2 are recovery / warm-up and earn no points.
- Zones 3–5 earn escalating Burn Points per full minute spent in that zone.
- A **partial minute** at the end of a workout rounds down (no fractional points).

### 2.2 Burn Points Scoring

| Metric               | Detail                                                    |
|-----------------------|-----------------------------------------------------------|
| **Daily Target**      | 12 Burn Points (configurable by user, default = 12)       |
| **Session Goal**      | Hit your daily target during a single workout              |
| **Streak Bonus**      | +2 bonus points for 3+ consecutive days hitting target     |
| **Weekly Summary**    | Total Burn Points, avg per session, time in each zone      |
| **Monthly Challenge** | Community leaderboard, opt-in, ranked by total points      |

### 2.3 Afterburn Estimate

After each session, the app shows an **Afterburn Estimate** — an estimated additional calorie burn over the next 24 hours based on time spent in Zones 4–5 (EPOC effect). This is calculated using a simplified model:

```
afterburn_kcal = (minutes_in_zone_4 * 1.5 + minutes_in_zone_5 * 2.5) * body_weight_kg * 0.05
```

This is clearly labeled as an **estimate** (not medical advice).

---

## 3. Feature Set

### 3.1 MVP (v1.0)

| # | Feature                        | Description                                                                 |
|---|-------------------------------|-----------------------------------------------------------------------------|
| 1 | **User Onboarding**            | Age, weight, height, resting HR, fitness level → auto-calculate MHR & zones |
| 2 | **Live Workout Screen**        | Real-time HR, current zone colour, Burn Points accumulator, elapsed time     |
| 3 | **Workout Summary**            | Total Burn Points, time per zone (bar chart), calories, Afterburn Estimate   |
| 4 | **History & Trends**           | Calendar view of past workouts, weekly/monthly Burn Point trends             |
| 5 | **Heart Rate Device Pairing**  | Bluetooth LE pairing for chest straps & wrist monitors                      |
| 6 | **Google Health Connect**      | Bi-directional sync — read HR, write workout summaries                      |
| 7 | **Profile & Settings**         | Edit MHR, zone thresholds, daily target, units (metric/imperial)            |
| 8 | **Notifications**              | Workout reminders, streak alerts, weekly summary push                       |

### 3.2 Post-MVP (v1.x – v2.0)

| # | Feature                          | Description                                                                  |
|---|----------------------------------|------------------------------------------------------------------------------|
| 9 | **Group Workout Mode**            | Instructor creates a session code; participants join and see a shared display |
| 10| **Instructor Dashboard**          | Tablet/TV view showing all participants' live zones and points               |
| 11| **Workout Templates**             | Pre-built interval/circuit/endurance templates with timed segments           |
| 12| **Custom Workout Builder**        | Users create their own segment-based workouts (warm-up, push, peak, cool-down)|
| 13| **Social & Leaderboards**         | Friends list, challenges, weekly leaderboard, achievement badges             |
| 14| **Wearable Companion App**        | Wear OS companion showing live zone and points on wrist                      |
| 15| **Fitbit / Garmin / Samsung**     | Direct API integration beyond Health Connect                                 |
| 16| **AI Coach Suggestions**          | Post-workout tips based on zone distribution (e.g., "Try more time in Push") |
| 17| **Apple Health (iOS)**            | iOS port with HealthKit integration                                          |

---

## 4. Technical Architecture

### 4.1 Platform & Language

| Layer        | Technology                                |
|-------------|------------------------------------------|
| Language     | **Kotlin** (100%)                        |
| UI           | **Jetpack Compose** + Material 3         |
| Min SDK      | API 26 (Android 8.0)                     |
| Target SDK   | API 35 (Android 15)                      |
| Build        | Gradle with Kotlin DSL, version catalogs |

### 4.2 App Architecture

```
┌─────────────────────────────────────────────────┐
│                    UI Layer                      │
│         Jetpack Compose + ViewModels             │
├─────────────────────────────────────────────────┤
│                  Domain Layer                    │
│     Use Cases / Interactors (pure Kotlin)        │
├─────────────────────────────────────────────────┤
│                  Data Layer                      │
│  Room DB  │  Health Connect  │  BLE Service      │
│  DataStore│  Remote API      │  WorkManager       │
└─────────────────────────────────────────────────┘
```

**Pattern:** MVVM + Clean Architecture (UI → ViewModel → UseCase → Repository → DataSource)

### 4.3 Key Libraries & APIs

| Concern               | Library / API                                        |
|-----------------------|------------------------------------------------------|
| DI                    | Hilt                                                 |
| Database              | Room                                                 |
| Preferences           | DataStore (Proto)                                    |
| Async                 | Kotlin Coroutines + Flow                             |
| Navigation            | Compose Navigation                                   |
| Charts                | Vico (Compose-native charting)                       |
| Bluetooth LE          | Android BLE API + companion library (ABLE / Kable)   |
| Health data           | **Health Connect API** (Google)                      |
| Background work       | WorkManager                                          |
| Notifications         | Firebase Cloud Messaging (FCM)                       |
| Auth (post-MVP)       | Firebase Auth                                        |
| Backend (post-MVP)    | Firebase Firestore / Cloud Functions                 |
| CI/CD                 | GitHub Actions                                       |

### 4.4 Health Connect Integration

Health Connect (formerly Google Health) is the primary hub for reading/writing health data on Android.

**Read:**
- `HeartRateRecord` — live and historical HR data from any connected wearable
- `RestingHeartRateRecord` — to refine zone calculations
- `ExerciseSessionRecord` — import workouts from other apps

**Write:**
- `ExerciseSessionRecord` — export PulseFit workouts
- `TotalCaloriesBurnedRecord` — calories including Afterburn Estimate
- `HeartRateRecord` — if captured directly via BLE

**Permissions:** The app requests only the minimum scopes needed and clearly explains each permission during onboarding.

### 4.5 Bluetooth LE Heart Rate Service

Standard Bluetooth Heart Rate Profile (UUID `0x180D`):
- Characteristic `0x2A37` — Heart Rate Measurement (notify)
- Supports chest straps (Polar, Wahoo, Garmin HRM) and most BLE wrist monitors
- Fallback: phone's own optical sensor via Health Connect if no external device

---

## 5. Data Model (Room)

### Core Entities

```kotlin
@Entity
data class UserProfile(
    @PrimaryKey val id: Long = 1,
    val age: Int,
    val weightKg: Float,
    val heightCm: Float,
    val restingHr: Int,
    val maxHr: Int,              // calculated or manual
    val dailyBurnTarget: Int,    // default 12
    val unitSystem: UnitSystem   // METRIC or IMPERIAL
)

@Entity
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Instant,
    val endTime: Instant,
    val totalBurnPoints: Int,
    val caloriesBurned: Int,
    val afterburnEstimate: Int,
    val avgHr: Int,
    val maxHr: Int,
    val notes: String? = null
)

@Entity(foreignKeys = [ForeignKey(entity = Workout::class, ...)])
data class ZoneSample(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val timestamp: Instant,
    val heartRate: Int,
    val zone: Int              // 1-5
)

@Entity(foreignKeys = [ForeignKey(entity = Workout::class, ...)])
data class ZoneSummary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val zone: Int,
    val durationSeconds: Int,
    val burnPointsEarned: Int
)
```

---

## 6. Screen Map

```
Onboarding (first launch)
  ├── Welcome
  ├── Profile Setup (age, weight, height)
  ├── HR Device Pairing (optional)
  ├── Health Connect Permission
  └── Daily Target Selection

Main App (BottomNav)
  ├── Home
  │     ├── Today's Summary Card
  │     ├── Streak Counter
  │     └── Quick Start Workout button
  ├── Workout
  │     ├── Pre-Workout (device check, template select)
  │     ├── Live Workout (HR gauge, zone bar, points counter)
  │     └── Post-Workout Summary
  ├── History
  │     ├── Calendar View
  │     ├── Workout Detail
  │     └── Trends (weekly/monthly charts)
  └── Profile
        ├── Edit Profile
        ├── Zone Settings
        ├── Connected Devices
        ├── Health Connect Settings
        └── App Preferences
```

---

## 7. Burn Points Algorithm (Detail)

```kotlin
class BurnPointsCalculator(private val maxHr: Int) {

    fun zoneFor(heartRate: Int): Int = when {
        heartRate < (maxHr * 0.50) -> 0  // below tracking
        heartRate < (maxHr * 0.61) -> 1  // Rest
        heartRate < (maxHr * 0.71) -> 2  // Warm-Up
        heartRate < (maxHr * 0.84) -> 3  // Active
        heartRate < (maxHr * 0.92) -> 4  // Push
        else                        -> 5  // Peak
    }

    fun pointsPerMinute(zone: Int): Int = when (zone) {
        3    -> 1
        4    -> 2
        5    -> 3
        else -> 0
    }

    /**
     * Given a list of (timestamp, heartRate) samples for a finished workout,
     * calculate total Burn Points.
     * Samples are expected roughly every 1 second.
     */
    fun calculate(samples: List<HrSample>): BurnResult {
        val zoneDurations = IntArray(6) // index 0-5, seconds per zone

        samples.zipWithNext { a, b ->
            val zone = zoneFor(a.heartRate)
            val durationSec = (b.timestamp - a.timestamp).inWholeSeconds.toInt()
            zoneDurations[zone] += durationSec
        }

        val totalPoints = (3..5).sumOf { zone ->
            (zoneDurations[zone] / 60) * pointsPerMinute(zone)
        }

        return BurnResult(
            totalPoints = totalPoints,
            zoneDurations = zoneDurations.toList()
        )
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
| **Free Tier**      | Full solo workout tracking, basic history, Health Connect sync  |
| **Premium ($4.99/mo)** | Group mode, advanced analytics, AI coach, custom templates  |
| **Gym/Studio License** | White-label instructor dashboard, bulk accounts             |

---

## 10. Project Structure

```
app/
├── src/main/kotlin/com/pulsefit/
│   ├── di/                     # Hilt modules
│   ├── data/
│   │   ├── local/              # Room DB, DAOs, DataStore
│   │   ├── health/             # Health Connect data source
│   │   ├── ble/                # Bluetooth LE heart rate service
│   │   └── repository/         # Repository implementations
│   ├── domain/
│   │   ├── model/              # Domain models (Workout, Zone, BurnResult)
│   │   ├── calculator/         # BurnPointsCalculator, AfterBurnEstimator
│   │   └── usecase/            # Use cases (StartWorkout, GetHistory, etc.)
│   └── ui/
│       ├── theme/              # Material 3 theme, colours, typography
│       ├── onboarding/         # Onboarding screens
│       ├── home/               # Home tab
│       ├── workout/            # Live workout, pre/post screens
│       ├── history/            # History & trends
│       ├── profile/            # Profile & settings
│       └── components/         # Shared composables (ZoneBar, HrGauge, etc.)
├── src/main/res/
│   ├── values/                 # Strings, colours, dimensions
│   └── drawable/               # Icons, illustrations
└── build.gradle.kts
```

---

## 11. Development Phases

### Phase 1 — Foundation
- Project scaffolding (Compose, Hilt, Room, Navigation)
- User profile entity + onboarding flow
- Zone calculation engine + unit tests

### Phase 2 — Core Workout Loop
- BLE heart rate connection service
- Live workout screen with real-time zone display
- Burn Points accumulator
- Workout summary screen + Room persistence

### Phase 3 — Health Connect & History
- Health Connect read/write integration
- Workout history list + calendar view
- Weekly/monthly trend charts

### Phase 4 — Polish & Launch
- Notifications (reminders, streaks)
- Settings & zone customisation
- UI polish, animations, accessibility
- Play Store listing, privacy policy, beta test

### Phase 5 — Post-Launch (v1.x)
- Group workout mode + instructor dashboard
- Workout templates & custom builder
- Social features, leaderboards, badges
- Wear OS companion
- AI coach suggestions

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

PulseFit delivers the same motivating, heart-rate-zone-driven workout experience that has proven popular in boutique fitness — but as a standalone, brand-neutral Android app. By using standard HR zone science, an original "Burn Points" scoring system, and deep integration with Health Connect and Bluetooth LE wearables, PulseFit can serve both individual users and gym/studio operators without infringing on any existing brand's intellectual property.
