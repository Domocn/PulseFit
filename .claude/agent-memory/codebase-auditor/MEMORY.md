# PulseFit Audit Memory

## Project Overview
- Native Android app (Kotlin + Compose)
- MVVM + Clean Architecture
- DI: Hilt with KSP
- DB: Room (version 4, 8 entities)
- Package: com.example.pulsefit
- Navigation: Jetpack Compose with 19 routes

## Audit Completed: 2026-02-19 (P0 MVP Gap Audit: PASS)

### P0 MVP Status: ✅ ALL GAPS CLOSED
- All 18 previously identified P0 features verified as implemented and wired
- Zero blocking issues found
- App is 100% integration-complete for MVP launch

### Architecture Verified
- All 15 ViewModels properly annotated with @HiltViewModel
- All 8 DAOs provided in DatabaseModule
- 2 repositories bound in RepositoryModule
- 10 use cases (9 used, 1 dead: StartWorkoutUseCase)
- 3 workers all properly scheduled via SettingsViewModel
- 19 navigation routes (18 reachable, 1 orphaned: PreWorkoutSchedule)

### Critical Integration Points Verified
1. Onboarding flow completes correctly (sets onboardingComplete=true)
2. Workout lifecycle: create → track HR → end → calculate stats → award XP → unlock achievements
3. Daily quests generated and evaluated on workout completion
4. Notification channels created in PulseFitApplication.onCreate()
5. Theme selection persisted and applied via AppViewModel

### Verified Integrations (2026-02-19)
- Health Connect: ✅ writeWorkout() called in EndWorkoutUseCase line 55
- NoveltyEngine: ✅ Weekly theme shown on HomeScreen (HomeViewModel lines 90-96)
- PreWorkoutScheduleScreen: ✅ Reachable from Templates for ASD/AUDHD + GUIDED
- CalendarView & TrendChart: ✅ Integrated in HistoryScreen lines 50-66
- BLE auto-reconnect: ✅ WorkoutViewModel line 139 uses BlePreferences
- Daily quests: ✅ Generated in HomeViewModel, evaluated in WorkoutViewModel
- Achievements: ✅ Checked in WorkoutViewModel lines 286-294
- Calorie estimation: ✅ Live counter + final in summary
- Shutdown routine: ✅ Between Workout → Summary for ASD/AUDHD
- All 7 onboarding screens: ✅ Complete flow with biologicalSex, dailyTarget, maxHR override

### Unused Methods (Not Dead Code — Defined for Future Use)
- WorkoutDao: getAllWorkouts(), getWorkoutsForDay(), getTotalBurnPoints(), getCompletedWorkoutCount()
- HeartRateReadingDao: insertAll()
- AchievementDao: getUnlockedAchievements()
- WeeklyRoutineDao: getRoutinesForDay(), delete()
- NotificationPreferencesDao: getPreferencesOnce()
- Note: StartWorkoutUseCase removed, no longer exists

### Code Quality Notes
- Only 1 TODO in entire codebase (XML template comment)
- No missing Hilt bindings
- No broken data flows (except Health Connect)
- All ViewModels properly scoped and injected
- All workers configured with HiltWorkerFactory

## Patterns Observed
- ViewModel typically loads profile in init{} block
- Use cases are thin wrappers around repository calls
- DAOs often have both Flow and suspend "Once" versions (Flow for reactive UI, Once for one-time reads)
- Entity ↔ Domain model conversion happens in repository layer
- Zone time stored as JSON string in WorkoutEntity, deserialized on read

## Comprehensive Security & Code Audit: 2026-02-21 (PRODUCTION BLOCKERS)

### Overall Score: 7.2/10
- Architecture: 9/10 (excellent MVVM + Clean)
- Security: 4/10 (CRITICAL exposures)
- Code Quality: 8/10 (zero TODOs, well-structured)
- Error Handling: 6/10 (many silent failures)
- Performance: 7/10 (some inefficient queries)

### CRITICAL Production Blockers (Must Fix Before ANY Release)
1. **S1: google-services.json in git** - Firebase API key `AIzaSy[REDACTED]` exposed
2. **S2: No Firestore security rules** - 9 collections (users, groups, etc.) have no RLS
3. **S3: ProGuard disabled** - `isMinifyEnabled = false` in release build
4. **S5: Firebase auth force-unwraps** - 3 instances of `result.user!!` will crash on null
5. **B1: Voice coach race condition** - Queue accessed from multiple threads unsafely
6. **Missing crash reporting** - No Crashlytics/Sentry for production monitoring

### Audio Asset Status (Updated from Previous Audit)
- **131 MP3 clips now present** (was 51 in VOICE_COACH_AUDIT_REPORT.md)
- All 7 engagement categories now complete (start, complete, target_hit, encourage, streak, return, progress)
- Voice coach feature is 100% asset-complete
- TTS fallback still working for edge cases

### High-Priority Bugs (Crash Risks)
- B1: VoiceCoachEngine queue race (use ConcurrentLinkedQueue)
- B2: MediaPlayer not nulled on create failure
- B3: TTS callback may fire after shutdown
- B4: Null profile on first workout (no greeting played)
- B5: TransitionWarningManager `lastZone!!` crash
- B6: WorkoutScreen `guidedState!!` crash if guided mode disabled

### Performance Issues
- P1: GroupChallengeRepository.getActiveGroupIds() fetches ALL groups (O(n) reads)
- P2: CloudProfileRepository.getSharedWorkouts() issues multiple sequential queries
- P3: WorkoutViewModel recalculates everything every second (CPU waste)
- P4: Room destructive migration drops all user data on schema change

### Security Gaps Beyond Firebase
- S6: Multiple UI screens use `!!` on nullable state (14 instances total)
- S7: GroupChallengeRepository leaks other users' group data via client-side filtering
- S8: BLE permissions missing `neverForLocation` flag (unnecessary location prompt)

### Missing Production Requirements
- GDPR: No account deletion or data export
- Testing: Zero test files in app/src/test/ or app/src/androidTest/
- Monitoring: No crash reporting configured
- Legal: No privacy policy, terms of service, or cookie consent
- Offline: No Firestore offline persistence enabled
- Backups: No Room database export mechanism

### Time to Production-Ready
- **Minimum** (6 critical blockers): 10 hours dev + 4 hours testing
- **Recommended** (all HIGH issues): 18 hours dev + 6 hours testing
- **Ideal** (+ monitoring + basic tests): 30 hours dev + 10 hours testing

### Commendations
- Zero TODOs/FIXMEs in 197 Kotlin files (exceptional discipline)
- World-class neurodivergent accommodation features (ASD/ADHD/AuDHD)
- Textbook Clean Architecture implementation
- Sophisticated voice coach with queue-based playback
- Proper AGP 9 compatibility (navigated KGP 2.2.10 bundling correctly)

## Future Audit Focus
- Check if unused ND feature classes should be removed or integrated
- Verify if batch heart rate insert optimization is needed
- Confirm PreWorkoutSchedule screen is intentionally unreachable or needs wiring
