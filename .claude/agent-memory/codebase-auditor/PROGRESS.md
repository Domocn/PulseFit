# Comprehensive Codebase Audit Progress

## Audit Started: 2026-02-21
## Last Updated: 2026-02-24

### Files Reviewed: 285 / 285 Kotlin files (full ND feature audit complete)

## Phase 1: Discovery - COMPLETE
- Total Kotlin files: 285 (up from 197 in Feb-21 audit)
- Total entities: 15 in DB version 13
- Package structure confirmed: com.pulsefit.app
- New packages: adhd/, asd/, nd/, voice/, util/, caregiver/, gym/, gear/, ritual/, recovery/

## Phase 2: Security Audit - COMPLETE (see Feb-21 audit for prior findings)

## Phase 3: Code Review - COMPLETE (2026-02-24)

### 18 ND Features Status:
1. Sensory Gym Map (F1): IMPLEMENTED - GymProfileScreen/ViewModel/GymProfileRepository/GymProfileEntity
2. Decide for Me (F2): IMPLEMENTED - DecisionEngine + HomeViewModel.decideForMe() + HomeScreen
3. Ritual Builder (F3): IMPLEMENTED - RitualScreen/ViewModel/RitualRepository/RitualStepEntity
4. Body Double Sessions (F4): IMPLEMENTED - BodyDoubleSessionsScreen/ViewModel + ScheduledBodyDoubleRepository
5. PDA Mode (F5): IMPLEMENTED - PdaLanguage.transform() used in HomeScreen + WorkoutScreen
6. Recovery Workouts (F6): IMPLEMENTED - RecoveryContentScreen/ViewModel/RecoveryContentRegistry
7. Spoon Budget (F7): IMPLEMENTED - SpoonBudgetCard + SpoonBudgetRepository + SpoonBudgetEntity
8. Music Zones (F8): IMPLEMENTED - MusicSuggestionSheet + MusicSuggestionEngine (FAB in WorkoutScreen)
9. Form Tips (F9): IMPLEMENTED - ExerciseGuideOverlay shows formTips from Exercise model
10. Quick Launch (F10): IMPLEMENTED AS WORKER - QuickLaunchWorker exists but NOT SCHEDULED anywhere
11. Calendar Block (F11): PARTIALLY IMPLEMENTED - Screen exists but missing runtime permission request
12. Recovery Content (F12): IMPLEMENTED - RecoveryContentScreen (same as F6)
13. Buddy Match (F13): IMPLEMENTED - BuddyMatchScreen/ViewModel/BuddyMatchingRepository
14. Micro Workouts (F14): IMPLEMENTED - MicroWorkoutEngine + MicroWorkoutCard on HomeScreen
15. Gym Busyness (F15): IMPLEMENTED - GymBusyPredictor used in HomeViewModel + GymProfileViewModel
16. Caregiver Link (F16): PARTIALLY IMPLEMENTED - CaregiverSetupScreen + CaregiverDashboardScreen
    ISSUE: shareReadiness() on CaregiverRepository is NEVER called (athlete never pushes data)
    ISSUE: CaregiverLinkEntity in Room is unused dead code (Firestore-only)
17. Hyperfocus Guard (F17): IMPLEMENTED - HyperfocusGuard + WorkoutViewModel + WorkoutScreen banner
18. Gear Guide (F18): IMPLEMENTED - GearGuideScreen/ViewModel/GearGuideRegistry

### Critical Bugs Found (2026-02-24):
1. QuickLaunchWorker and MicroWorkoutReminderWorker are NEVER scheduled anywhere
2. CalendarBlockScreen: No runtime permission request for READ_CALENDAR/WRITE_CALENDAR
3. CaregiverRepository.shareReadiness() is never called - caregiver dashboard shows no data
4. CaregiverLinkEntity/CaregiverLinkDao: dead Room entity (Firestore used instead)
5. MusicSuggestionSheet: instantiates MusicSuggestionEngine() directly (not injected, not a problem in production but inconsistent)
6. BodyDoubleSessionsScreen: scheduledAt hardcoded to System.currentTimeMillis() + 3600_000 (no time picker)
7. BuddyMatchScreen: ndProfile hardcoded to "ASD" (line 58: submitRequest("ASD", ...))
8. AdMob test IDs in both Manifest and RewardedAdManager.kt (ca-app-pub-3940256099942544)
9. AdMob App ID in Manifest is test ID (ca-app-pub-3940256099942544~3347511713)

## Phase 4: Integration Audit - COMPLETE

## AUDIT COMPLETE (2026-02-24)
