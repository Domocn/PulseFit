# Competitive reference — Gym WP 10.25.0 (`pps.pedro.gym`)

**Source:** unpacked release APK, kept locally at `reference/apks/gym-wp-10.25.0/` (git-ignored, never committed — repo is public and this app is proprietary).
**Use:** feature/UX reference only. Do **not** copy their code, assets, strings, or drawables into PulseFit. Reimplement anything worth having from your own design.

## Tech stack (from packaged library manifests)

- Kotlin 2.1.20, Gradle 8.9, Java 17 target
- **Hybrid UI**: Jetpack Compose *and* 614 legacy XML layouts — a partially-migrated View codebase. PulseFit is Compose-only, which is a genuine advantage; don't imitate this.
- Material 3, ConstraintLayout (+ Compose variant)
- Firebase: Auth, Firestore, Analytics, Crashlytics, Cloud Messaging, Performance
- Play Billing + Play App Update + Play Integrity, AdMob, Facebook SDK, Picasso, OkHttp

Localisation is narrow: resource variants exist only for **en / es / pt**.

## Feature map (from `AndroidManifest.xml` — not obfuscated)

Their app decomposes into these areas:

| Area | Screens |
|---|---|
| Onboarding | Splash → LetsStart → UseAppAtGym → PersonalizedWorkout → CreateCustomPlan → CustomPlanLoading → WorkoutNotification |
| Discover | AllWorkouts, WorkoutCategory, WorkoutDetails |
| Exercises | ExerciseDetails, **ExerciseDetails3D**, ExerciseInfo, **ExerciseFilter**, AddExerciseBottomSheet |
| Custom workouts | CustomWorkout, CustomPlanInfo, CustomWorkoutPlanOverview, PlannerResume, **WorkoutGeneratorVisualization** |
| Training | WorkoutDayList, WorkoutExerciseList, **FreestyleWorkout**, **WorkoutAssistant** (+ real-time background service), **1RM calculator** |
| Body & progress | **MyBody** (measurements), **MuscleFatigue**, Retrospective, StatisticsRetrospective |
| Widgets | **4 home-screen widgets** — 3 muscle-fatigue sizes + a general one |
| Retention | Reminders, AlarmReceiver, DeviceBootReceiver, **Inactivity** re-engagement, UpdateRating |
| Monetisation | 11 paywall/sale screens: SubsPlans, PremiumBenefits, OfferAnnualPlan, CustomizablePromotion, CustomizableRenewal (v1/v2), ThanksUpSellPurchase, PurchaseConfirmation, LandingPage, PlansTest (A/B), PremiumDisable |
| Account | Email login/create/recover, MyProfile, SyncBackupService |

## Gaps — things they have that PulseFit doesn't

Ranked by what I'd judge as actual value to your users, not by their prominence:

1. **Home-screen widgets** (4 of them). PulseFit ships none. For an ADHD-focused app this is arguably the single highest-value item here — an at-a-glance widget is an external memory aid that removes the "remember to open the app" step entirely. Strong fit with the time-blindness goals.
2. **Muscle fatigue model** — tracks per-muscle-group recovery state and surfaces it as its own screen *and* widgets. Feeds "what should I train today?" without requiring a decision from the user.
3. **Body measurements** (`MyBody`) — PulseFit tracks workouts but has no body-metric history.
4. **1RM calculator** — small, self-contained, expected by gym users.
5. **Exercise filtering** — you have a large `ExerciseRegistry`; a filter screen is table stakes once the catalogue is big.
6. **Retrospective / year-in-review** — a recap moment. Pairs well with your existing achievements and reward-shop loop.
7. **Real-time workout assistant** as a foreground service — survives screen-off mid-workout. Worth checking how PulseFit's active workout behaves when the screen locks.
8. **Inactivity re-engagement flow** — a dedicated screen for lapsed users. Handle carefully: done badly this is shame-inducing, which is exactly wrong for your audience.

## Where PulseFit is already ahead

Don't lose these while chasing the list above — they're the differentiators Gym WP has no answer to:

- Sensory settings, ND profile selection, animation/sound guards
- Body-double sessions, accountability, caregiver dashboard
- Social layer: friends, groups, challenges, leaderboard, feed
- BLE heart-rate + resting HR
- Rituals, shutdown routine, pre-workout scheduling
- Compose-only codebase (vs. their 614-layout hybrid)

## What is NOT extractable

`assets/` is 22 encrypted/obfuscated blobs with randomised names (~3 MB total) — the exercise database and media are in there and are not readable. `classes*.dex` is minified. I did not attempt to defeat either, and you shouldn't: their exercise content is their product, and reproducing it is the line between competitive analysis and infringement.

## Ground rules

- Reference the *ideas*; write your own implementation.
- Never copy strings, drawables, layouts, or exercise data.
- Keep `reference/apks/` git-ignored. It is 115 MB of proprietary binaries and the repo is public.
