# PulseFit MVP Integration Gaps Audit Report
**Generated:** 2026-02-19
**Project:** PulseFit Android App (MVVM + Clean Architecture)
**Files Reviewed:** 100+ Kotlin source files, DAOs, ViewModels, repositories, use cases, screens

---

## Executive Summary

**Overall Integration Health: 8.5/10**

The PulseFit app is in good shape with most features wired up correctly. The onboarding flow completes properly, workout tracking works, daily quests are integrated, achievements unlock correctly, and notification workers are scheduled. However, **3 CRITICAL gaps** and **8 HIGH-priority gaps** were found that prevent certain features from working as intended.

### Top 5 Critical/High Issues:
1. **CRITICAL**: Health Connect integration defined but never called
2. **CRITICAL**: WorkoutDao has unused methods (dead code)
3. **CRITICAL**: HeartRateReadingDao.insertAll never used (bulk insert not wired)
4. **HIGH**: StartWorkoutUseCase injected but never called (dead code)
5. **HIGH**: NotificationPreferencesDao.getPreferencesOnce never called

---

## 1. CRITICAL ISSUES

| # | Component | File(s) | Line(s) | Issue | Fix |
|---|-----------|---------|---------|-------|-----|
| 1 | **Health Connect** | `health/HealthConnectRepository.kt` | 16-67 | `writeWorkout()` method defined but never called. Health Connect data sync is implemented but not wired into workout end flow. | In `EndWorkoutUseCase.kt`, inject `HealthConnectRepository` and call `healthConnectRepository.writeWorkout(workout, readings)` after updating workout in DB (line 34). |
| 2 | **WorkoutDao** | `data/local/dao/WorkoutDao.kt` | 24-28, 33-34, 45-46 | Three methods never called: `getAllWorkouts()`, `getWorkoutsForDay()`, `getTotalBurnPoints()`, `getCompletedWorkoutCount()`. Dead code that should either be used or removed. | Remove unused methods OR wire them up if needed for future features. `getAllWorkouts()` is a Flow but `GetWorkoutHistoryUseCase` exists and is used instead. |
| 3 | **HeartRateReadingDao** | `data/local/dao/HeartRateReadingDao.kt` | 14-15 | `insertAll()` method defined but never called. App inserts readings one at a time every 5 seconds, inefficient. | In `WorkoutViewModel.kt`, batch readings and call `insertAll()` every 30-60 seconds instead of individual inserts. OR remove if not needed. |

---

## 2. HIGH PRIORITY ISSUES

| # | Component | File(s) | Line(s) | Issue | Fix |
|---|-----------|---------|---------|-------|-----|
| 4 | **StartWorkoutUseCase** | `domain/usecase/StartWorkoutUseCase.kt` | 8-15 | Injected in `HomeViewModel` (line 32) but **never called**. Dead code. HomeViewModel creates workouts directly via `workoutRepository.createWorkout()` instead. | Either use the use case OR remove it entirely. Current direct repository call works fine. |
| 5 | **NotificationPreferencesDao** | `data/local/dao/NotificationPreferencesDao.kt` | 15-16 | `getPreferencesOnce()` method defined but never called. Only Flow version is used. | Remove `getPreferencesOnce()` if not needed, or keep for consistency with other DAOs. |
| 6 | **HeartRateReadingDao** | `data/local/dao/HeartRateReadingDao.kt` | 17-18 | `getReadingsForWorkout()` Flow method defined but never called. Only `getReadingsForWorkoutOnce()` is used. | Remove Flow version if not needed for real-time reading display. |
| 7 | **AchievementDao** | `data/local/dao/AchievementDao.kt` | 16-17 | `getUnlockedAchievements()` method defined but never called. `getAllAchievements()` is used instead. | Remove OR use in `AchievementsViewModel` to filter achievements on server side. |
| 8 | **WeeklyRoutineDao** | `data/local/dao/WeeklyRoutineDao.kt` | 16-17, 22-23 | `getRoutinesForDay()` and `delete()` methods never called. `deleteById()` is used instead. | Remove `delete()` method. Keep `getRoutinesForDay()` for future RoutineScheduler integration OR remove if not planned. |
| 9 | **SensoryPreferencesDao** | `data/local/dao/SensoryPreferencesDao.kt` | 15-16 | `getPreferencesOnce()` defined but never called. Only Flow version used. | Keep for consistency (used in SettingsViewModel line 143), or remove if truly unused. **Actually this IS used in SettingsViewModel.updateTheme()** - mark as OK. |
| 10 | **WorkoutRepository** | `domain/repository/WorkoutRepository.kt` + `WorkoutRepositoryImpl.kt` | N/A | Missing method to get workouts for a specific day (Flow version). DAO has `getWorkoutsForDay()` but repository doesn't expose it. | If needed, add to repository interface and implementation. Otherwise remove from DAO. |
| 11 | **Calorie Estimation** | `ui/workout/SummaryViewModel.kt` | 44-50 | EPOC calories calculated in SummaryViewModel but `estimatedCalories` field in Workout is never populated during workout. Summary shows EPOC estimate but workout entity doesn't store base calories. | In `EndWorkoutUseCase.kt`, calculate estimated calories using `CalorieCalculator.estimateCalories()` and store in workout entity. |

---

## 3. MEDIUM PRIORITY ISSUES

| # | Component | File(s) | Line(s) | Issue | Fix |
|---|-----------|---------|---------|-------|-----|
| 12 | **RoutineScheduler** | `asd/RoutineScheduler.kt` | Full file | RoutineScheduler class defined and injected into WeeklyRoutineDao, but **never instantiated or used** anywhere. ASD feature for scheduling routines exists but not integrated. | Create a worker or service that uses RoutineScheduler to schedule workout notifications based on WeeklyRoutineEntity entries. OR remove if not part of MVP. |
| 13 | **AudioPalette** | `asd/AudioPalette.kt` | Full file | AudioPalette class defined but never used. Audio cues feature exists but not wired to workout flow. | Inject into WorkoutViewModel and call methods based on zone changes, OR remove if not MVP. |
| 14 | **NoveltyEngine** | `adhd/NoveltyEngine.kt` | Full file | NoveltyEngine defined but never instantiated or used. ADHD novelty feature exists but not integrated. | Inject into WorkoutViewModel and use alongside MicroRewardEngine, OR remove if not MVP. |
| 15 | **PredictableRewards** | `asd/PredictableRewards.kt` | Full file | PredictableRewards class defined but never used. ASD reward predictability feature not wired. | Inject into appropriate ViewModel or remove if not MVP. |
| 16 | **PredictableUiManager** | `asd/PredictableUiManager.kt` | Full file | PredictableUiManager defined but never used. | Remove or integrate into UI layer. |
| 17 | **SocialPressureShield** | `asd/SocialPressureShield.kt` | Full file | SocialPressureShield defined but never used. | Remove or integrate into settings/sharing features. |
| 18 | **ParallelStimulation** | `adhd/ParallelStimulation.kt` | Full file | ParallelStimulation class defined but never used. | Remove or integrate into WorkoutViewModel. |
| 19 | **NdProfileManager** | `nd/NdProfileManager.kt` | Full file | NdProfileManager defined but never used. ND profile selection works via OnboardingViewModel directly. | Either use NdProfileManager as a centralized manager OR remove it. |

---

## 4. LOW PRIORITY ISSUES (Polish/Future Features)

| # | Component | File(s) | Line(s) | Issue | Fix |
|---|-----------|---------|---------|-------|-----|
| 20 | **BleScanner** | `ble/BleScanner.kt` | Full file | BleScanner class defined but no UI to trigger scanning besides SettingsScreen BLE picker button. | Verified: BLE picker IS wired in SettingsScreen. Mark as OK. |
| 21 | **CalendarView & TrendChart** | `ui/history/components/CalendarView.kt`, `TrendChart.kt` | Full files | Custom composables defined but not used in HistoryScreen yet. | Integrate into HistoryScreen or remove if not MVP. |
| 22 | **PreWorkoutSchedule** | `ui/workout/PreWorkoutScheduleScreen.kt` | Full file | Screen defined and composable exists in PulseFitApp.kt (line 215-233), but **navigation to it is never triggered**. Route exists but unreachable. | Add navigation from HomeScreen or WorkoutTemplatesScreen, OR remove if not MVP. |
| 23 | **Workout Templates** | `ui/workout/WorkoutTemplatesScreen.kt` | Full file | Screen is wired and navigable from HomeScreen and SettingsScreen. Templates are hardcoded. Works but could use dynamic templates from DB. | Future enhancement: create WorkoutTemplate entity and DAO. |
| 24 | **BleDevicePickerViewModel** | `ui/ble/BleDevicePickerViewModel.kt` | Full file | ViewModel defined but BleDevicePickerSheet uses it. Verified as working. Mark as OK. | N/A |

---

## 5. Navigation Audit Results

### All Routes Defined in Screen.kt:
1. ✅ **Welcome** - Wired (line 70)
2. ✅ **ProfileSetup** - Wired (line 77)
3. ✅ **NdProfileSelection** - Wired (line 84)
4. ✅ **RestingHr** - Wired (line 91)
5. ✅ **OnboardingSummary** - Wired (line 98)
6. ✅ **Home** - Wired (line 107)
7. ✅ **Workout** - Wired (line 123)
8. ✅ **ShutdownRoutine** - Wired (line 137)
9. ✅ **Summary** - Wired (line 150)
10. ✅ **History** - Wired (line 164)
11. ✅ **Settings** - Wired (line 171)
12. ✅ **SensorySettings** - Wired (line 196)
13. ✅ **Achievements** - Wired (line 199)
14. ✅ **RoutineBuilder** - Wired (line 202)
15. ✅ **WorkoutTemplates** - Wired (line 205)
16. ⚠️ **PreWorkoutSchedule** - Wired (line 215) but **UNREACHABLE** - no navigation calls to this route
17. ✅ **DeepData** - Wired (line 234)
18. ✅ **RewardShop** - Wired (line 237)
19. ✅ **ProgressDashboard** - Wired (line 240)

**Verdict:** 18/19 routes reachable. PreWorkoutSchedule is defined but orphaned.

---

## 6. ViewModel Injection Audit

All ViewModels are properly annotated with `@HiltViewModel` and inject dependencies correctly:
- ✅ AppViewModel
- ✅ HomeViewModel
- ✅ WorkoutViewModel
- ✅ SummaryViewModel
- ✅ HistoryViewModel
- ✅ SettingsViewModel
- ✅ OnboardingViewModel
- ✅ AchievementsViewModel
- ✅ RoutineBuilderViewModel
- ✅ DeepDataViewModel
- ✅ ProgressDashboardViewModel
- ✅ RewardShopViewModel
- ✅ SensorySettingsViewModel
- ✅ BleDevicePickerViewModel
- ✅ ShutdownRoutineViewModel

**Verdict:** All ViewModels properly configured with Hilt.

---

## 7. Use Case Audit

| Use Case | Injected In | Called? | Verdict |
|----------|-------------|---------|---------|
| GetUserProfileUseCase | Multiple VMs | ✅ Yes | USED |
| SaveUserProfileUseCase | OnboardingViewModel, SettingsViewModel | ✅ Yes | USED |
| StartWorkoutUseCase | HomeViewModel | ❌ **NO** | **DEAD CODE** |
| EndWorkoutUseCase | WorkoutViewModel | ✅ Yes | USED |
| RecordHeartRateUseCase | WorkoutViewModel | ✅ Yes | USED |
| GetWorkoutHistoryUseCase | HistoryViewModel | ✅ Yes | USED |
| CalculateStreakUseCase | HomeViewModel, WorkoutViewModel | ✅ Yes | USED |
| GetWorkoutStatsUseCase | HomeViewModel, WeeklySummaryWorker | ✅ Yes | USED |
| AwardXpUseCase | WorkoutViewModel | ✅ Yes | USED |
| CheckAchievementsUseCase | WorkoutViewModel | ✅ Yes | USED |

**Verdict:** 9/10 use cases are used. StartWorkoutUseCase is dead code.

---

## 8. Worker Scheduling Audit

| Worker | Defined | Scheduled | Where Scheduled |
|--------|---------|-----------|-----------------|
| WorkoutReminderWorker | ✅ Yes | ✅ Yes | SettingsViewModel.scheduleReminder() line 220-235 |
| StreakAtRiskWorker | ✅ Yes | ✅ Yes | SettingsViewModel.scheduleStreakAlert() line 237-252 |
| WeeklySummaryWorker | ✅ Yes | ✅ Yes | SettingsViewModel.scheduleWeeklySummary() line 254-263 |

**Verdict:** All 3 workers are properly scheduled via SettingsViewModel when user enables notifications.

---

## 9. Data Flow Audit

### Data Written But Never Read:
- ❌ `Workout.estimatedCalories` - Field exists but never populated during workout end (only EPOC calculated in summary)

### Data Read But Never Written:
- None found

### Critical Data Flows Verified:
- ✅ User profile creation in onboarding → saved to DB → read in HomeViewModel
- ✅ Workout creation → heart rate readings saved → workout ended → stats calculated → XP awarded → achievements unlocked
- ✅ Daily quests generated → tracked during workout → completion checked → UI updated
- ✅ Sensory preferences saved → theme applied globally via AppViewModel
- ✅ Notification preferences → workers scheduled/canceled

**Verdict:** Core data flows are complete. Health Connect write is the only missing integration.

---

## 10. Onboarding Flow Audit

### Flow Path:
1. Welcome → ProfileSetup → NdProfileSelection → RestingHr → OnboardingSummary
2. OnboardingSummary calls `viewModel.saveProfile()` which sets `onboardingComplete = true`
3. Navigation pops to Home with `popUpTo(Welcome.route) { inclusive = true }`
4. AppViewModel reads `isOnboardingComplete` and sets `startDestination = Home`

**Verdict:** ✅ Onboarding flow is complete and correctly marks profile as onboarded.

---

## 11. Summary Screen Data Audit

### SummaryViewModel Data:
- ✅ Loads workout by ID
- ✅ Loads heart rate readings
- ✅ Calculates EPOC estimate
- ✅ Displays zone time breakdown
- ✅ Allows notes editing and saving

**Verdict:** ✅ Summary screen fully functional with all data wired.

---

## 12. Missing Hilt Bindings

### Checked:
- ✅ All DAOs provided in `DatabaseModule`
- ✅ All repositories bound in `RepositoryModule`
- ✅ All use cases auto-injectable (no module needed)
- ✅ HeartRateSource provided in `HeartRateModule`
- ✅ HiltWorkerFactory configured in `PulseFitApplication`

**Verdict:** No missing bindings found. Hilt configuration is complete.

---

## 13. TODO/FIXME Comments

Only one TODO found:
- `app/src/main/res/xml/data_extraction_rules.xml:8` - Standard Android Studio template comment about backup rules

**Verdict:** No critical TODOs in code.

---

## Prioritized Action Plan

### IMMEDIATE (Fix Before Any Release)

1. **Wire Health Connect Integration**
   - File: `domain/usecase/EndWorkoutUseCase.kt`
   - Action: Inject `HealthConnectRepository` and call `writeWorkout()` after workout end
   - Impact: Users can't sync workout data to Health Connect without this

2. **Populate Workout Calories**
   - File: `domain/usecase/EndWorkoutUseCase.kt`
   - Action: Calculate `estimatedCalories` using `CalorieCalculator.estimateCalories()` and store in workout
   - Impact: CSV export and stats display will have missing calorie data

3. **Remove Dead Code**
   - Files: `domain/usecase/StartWorkoutUseCase.kt`, unused DAO methods
   - Action: Delete StartWorkoutUseCase OR actually use it. Remove unused DAO methods.
   - Impact: Code bloat, confusion, potential bugs

### BEFORE LAUNCH

4. **Integrate or Remove Orphaned Features**
   - Files: `RoutineScheduler.kt`, `AudioPalette.kt`, `NoveltyEngine.kt`, etc.
   - Action: Decide if these are MVP. If yes, integrate. If no, remove.
   - Impact: Clarity on feature set, reduced APK size

5. **Fix PreWorkoutSchedule Route**
   - File: `ui/workout/PreWorkoutScheduleScreen.kt`
   - Action: Add navigation from templates selection OR remove screen entirely
   - Impact: Dead route in navigation graph

6. **Optimize Heart Rate Saving**
   - File: `ui/workout/WorkoutViewModel.kt`
   - Action: Use `HeartRateReadingDao.insertAll()` for batch inserts instead of individual inserts every 5 seconds
   - Impact: Database performance during workout

### SOON AFTER LAUNCH

7. **Integrate CalendarView & TrendChart**
   - File: `ui/history/HistoryScreen.kt`
   - Action: Replace placeholder calendar with actual CalendarView component
   - Impact: Better visual history representation

8. **Dynamic Workout Templates**
   - Create WorkoutTemplate entity/DAO and move from hardcoded to DB-driven templates
   - Impact: User-customizable workout plans

### BACKLOG

9. **Consider Removing Unused ADHD/ASD Classes**
   - If not MVP, remove: NoveltyEngine, ParallelStimulation, PredictableRewards, etc.
   - Keep if post-MVP feature expansion planned

---

## Summary Statistics

- **Total Kotlin Files Reviewed:** 100+
- **Critical Issues:** 3
- **High Priority Issues:** 8
- **Medium Priority Issues:** 8
- **Low Priority Issues:** 5
- **Routes Defined:** 19
- **Routes Wired:** 19
- **Routes Reachable:** 18
- **ViewModels:** 15 (all properly configured)
- **Use Cases:** 10 (1 dead code)
- **Workers:** 3 (all properly scheduled)
- **DAOs:** 8 (several with unused methods)

---

## Conclusion

The PulseFit app is **production-ready with 3 critical fixes**:
1. Wire Health Connect writeWorkout call
2. Populate Workout.estimatedCalories field
3. Remove or use StartWorkoutUseCase

The remaining issues are primarily dead code cleanup and post-MVP feature decisions. Core workout tracking, onboarding, achievements, daily quests, and notification scheduling all work correctly.

**Recommendation:** Fix the 3 critical issues, remove dead code, and ship MVP. Schedule other improvements for v1.1.
