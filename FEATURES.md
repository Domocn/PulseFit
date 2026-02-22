# PulseFit — Feature Specification

> **76+ features | 26 workout templates | 14 challenge types | 39 navigation routes | 4 ND profiles**
>
> PulseFit is a production-ready native Android fitness app with the most comprehensive neurodivergent-first design in the fitness market. No competitor — OrangeTheory, Peloton, WHOOP, Fitbod, or Apple Fitness+ — offers dedicated ADHD, ASD, or AuDHD experience modes.

### Status Legend

| Badge | Meaning |
|-------|---------|
| **COMPLETE** | Fully implemented and building |
| **IN PROGRESS** | Partially implemented |
| **PLANNED** | Designed, not yet coded |

---

## MVP Features (v1.0)

---

### F1. User Onboarding & Profile Setup — COMPLETE

**Goal:** Collect the minimum data needed to calculate personalised HR zones and start tracking.

**Flow:**
1. **Welcome screen** — app overview, value proposition, "Get Started" CTA
2. **Basic info** — age, weight, height, biological sex (optional, improves calorie estimate)
3. **Resting heart rate** — manual entry or auto-detect via connected device
4. **Max heart rate** — auto-calculated (`220 - age`) with option to override manually (lab test / field test result)
5. **Daily Burn Point target** — slider (range 8-30, default 12) with plain-language guidance ("12 = moderate 30-min session")
6. **Device pairing** — scan for Bluetooth LE HR monitors; skip option if using phone/watch sensor
7. **Health Connect permissions** — explain what data is read/written; request scopes
8. **Confirmation** — summary card of profile + zones; "Start First Workout" button

**Data stored:** `UserProfileEntity` in Room (age, weight, height, restingHr, maxHr, dailyBurnTarget, unitSystem, ndProfile, treadMode, equipmentProfileJson)

**Neurodivergent personalisation (optional step between steps 5 and 6):**
- "Personalise your experience" screen offering profiles:
  - **Standard** — balanced gamification (default)
  - **ADHD Focus Mode** — dopamine-first, micro-rewards, novelty rotation, zero-friction starts
  - **ASD Comfort Mode** — predictable UI, sensory controls, literal communication, routine structure
  - **Both (AuDHD)** — combined defaults, fully customisable
- Profile changeable anytime in Settings
- ADHD mode reduces remaining onboarding to 1 tap ("set up later" for optional steps)
- ASD mode shows full step map: "Step 3 of 7" with all steps listed

**Navigation routes:** welcome, profile_setup, nd_profile_selection, ble_onboarding, resting_hr, onboarding_summary

---

### F2. Heart Rate Zone Engine — COMPLETE

**Goal:** Classify any heart rate reading into one of five zones in real time.

| Zone | Name     | % of Max HR | Colour | Burn Points/min |
|------|----------|-------------|--------|-----------------|
| 1    | Rest     | < 50%       | Grey   | 0               |
| 2    | Warm-Up  | 50-59%      | Blue   | 0               |
| 3    | Active   | 60-69%      | Green  | 1               |
| 4    | Push     | 70-84%      | Orange | 2               |
| 5    | Peak     | 85-100%     | Red    | 3               |

**Implementation:** `ZoneCalculator` with configurable `ZoneThresholds` (defaults: warmUp=50, active=60, push=70, peak=85). `HeartRateZone` enum with `pointsPerMinute` property.

**Customisation:**
- Users can adjust zone boundaries in Settings (e.g., move the Active/Push threshold from 70% to 72%)
- Users can override max HR at any time
- Resting HR can be used for a Karvonen-based formula (optional advanced setting)

---

### F3. Bluetooth LE Heart Rate Pairing — COMPLETE

**Goal:** Connect to any standard Bluetooth LE heart rate monitor for real-time HR data.

**Supported devices (standard HR profile `0x180D`):**
- Chest straps — Polar H10/H9, Wahoo TICKR, Garmin HRM-Pro/Dual, Coospo, etc.
- Arm bands — Polar Verity Sense, Wahoo TICKR Fit, Scosche Rhythm+
- Most BLE-enabled fitness watches broadcasting HR

**Implementation:** `HeartRateSource` interface with `RealBleHeartRateSource` and `SimulatedHeartRateSource` implementations. BLE logs wrapped in `BuildConfig.DEBUG`.

**Behaviour:**
- Scan & pair screen with device name + signal strength
- Auto-reconnect to last paired device on workout start
- Connection status indicator (connected / searching / lost)
- If signal lost mid-workout, show banner alert and buffer gap; resume when reconnected
- Support multiple saved devices (user picks active device before each workout)

**Fallback:** If no external device, the app reads HR from Health Connect (wrist-based watch sensor).

---

### F4. Live Workout Screen — COMPLETE

**Goal:** The core experience — a real-time dashboard showing HR, zone, and Burn Points as the user exercises.

**Layout:**

```
+----------------------------------+
|  [Zone colour fills background]  |
|                                  |
|         HR 156 BPM               |
|        Zone: PUSH                |
|                                  |
|  +----+----+----+----+----+      |
|  | Z1 | Z2 | Z3 | Z4 | Z5 |    |  <- zone time bar (fills live)
|  +----+----+----+----+----+      |
|                                  |
|     8 Burn Points                |
|     Target: 12                   |
|     progress bar 67%             |  <- progress bar to daily target
|                                  |
|     22:45 elapsed                |
|     ~187 kcal                    |
|                                  |
|  [ PAUSE ]         [ END ]      |
+----------------------------------+
```

**Behaviour:**
- Background colour smoothly transitions to match current zone colour
- HR updates every 1 second (or as fast as the BLE device sends)
- Burn Points increment in real time (awarded per completed minute in zone)
- Calorie counter updates continuously (HR-based formula)
- Pause freezes timer and point accumulation; resume continues
- End triggers the shutdown routine / summary screen
- Screen stays awake (wake lock) during active workout
- Works in landscape mode for gym-mounted phones/tablets

**ADHD mode enhancements:**
- Micro-reward animation every completed minute in Zone 3-5 (confetti burst + point pop + sound)
- Task chunking overlay for templates: "Block 2 of 6 — Push Zone" with mini progress bar
- Giant time blindness timer: colour-filling circle making elapsed time concrete
- Streak multiplier badge visible in corner (1.5x / 2x / 3x)
- "Just 5 Minutes" check-in at 5:00 mark for low-commitment starts

**ASD mode enhancements:**
- Fixed layout — elements never move or resize during workout
- Reduced/no animations per Sensory Control Panel (F131)
- Exact time display: "14:37" not "Almost 15 minutes"
- Zone shown with triple redundancy: text label + colour + pattern (F141)
- Transition warnings for template segments: "Next segment in 30 seconds" (F137)
- Minimal Mode option: HR, zone, points, time — nothing else (F142)

---

### F5. Workout Summary — COMPLETE

**Goal:** Post-workout recap showing performance breakdown and Burn Points earned.

**Content:**
- **Total Burn Points** earned (large, prominent number)
- **Target hit?** — visual indicator (checkmark + celebration animation if target met)
- **Time in each zone** — horizontal stacked bar chart (colour-coded)
- **Zone detail table** — minutes and Burn Points per zone
- **Heart rate graph** — line chart of HR over time with zone colour bands behind it
- **Stats row** — total time, avg HR, max HR, total calories
- **Afterburn Estimate** — estimated extra calories over next 24h (EPOC), clearly labeled as estimate
- **Notes field** — optional free-text (e.g., "Treadmill intervals" or "Outdoor run")
- **Share button** — export summary as image for social media
- **Save** — auto-saved to Room + written to Health Connect

**ADHD mode:** Celebration Overkill (F130) plays before stats — confetti, sound, XP animation. Stats shown after 3-second celebration.

**ASD mode:** Calm Celebration (F138) — subtle checkmark + soft tone. Data-first layout with stats table immediately. Same format every time.

**Shutdown Routine (F145):** Post-workout wind-down sequence — stats, breathing exercise (optional), stretching prompt (optional), "Workout saved", home. Each step skippable, always offered.

---

### F6. Burn Points System & Streaks — COMPLETE

**Goal:** Gamify consistency with a simple, motivating points and streak system.

**Mechanics:**
- Points accumulate per completed minute in Zones 3-5 (see zone table above)
- **Daily target** — configurable (default 12); hit = day counts as "active"
- **Streak counter** — consecutive days hitting target
- **Streak bonus** — +2 bonus Burn Points added to any session on day 3+ of a streak
- **Streak freeze** (1 per week) — skip a day without breaking your streak (user activates manually or auto on rest day)
- **Personal bests** tracked: highest single-session points, longest streak, most points in a week

**Home screen widgets:**
- Today's Burn Points (0 if no workout yet, with "Start Workout" prompt)
- Current streak count + flame icon
- Weekly points progress bar

**ADHD mode additions:**
- Variable Reward Drops (F113): ~15% chance per minute of mystery bonus during workout
- Dopamine Streak Multiplier (F114): visual multiplier 1x -> 1.5x -> 2x -> 2.5x -> 3x (applies to XP, not actual BP)
- XP & Leveling System (F120): every Burn Point grants 10 XP; Level 1-50 with cosmetic unlocks
- Daily Quests (F121): 3 fresh micro-challenges each morning for bonus XP

**ASD mode additions:**
- Predictable Reward Schedule (F139): fixed visible formula, no randomness
- Clear formula display: "12 min x Zone 4 x 2 pts/min = 24 Burn Points"

---

### F7. Workout History & Trends — COMPLETE

**Goal:** Let users review past performance and see progress over time.

**History list:**
- Reverse-chronological list of all workouts
- Each card shows: date, duration, total Burn Points, zone colour breakdown mini-bar
- Tap to expand into full workout summary (same as F5)

**Calendar view:**
- Monthly calendar with colour-coded dots per day (green = target hit, yellow = workout but missed target, empty = rest day)
- Streak highlight bar across consecutive green days

**Trend charts (Vico):**
- **Weekly Burn Points** — bar chart, last 8 weeks
- **Avg HR per session** — line chart
- **Time in Zone distribution** — stacked area chart over time
- **Streak history** — timeline showing streak lengths

---

### F8. Health Connect Integration — COMPLETE

**Goal:** Bi-directional sync with Android's Health Connect platform so data flows between PulseFit and other health/fitness apps.

**Implementation note:** Uses `@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")` for `Metadata()` no-arg constructor compatibility with Health Connect 1.1.0-alpha12.

**Reads from Health Connect:**
- Heart rate records (from Wear OS watches, Samsung Health, Fitbit, etc.)
- Resting heart rate (to refine zone boundaries)
- Exercise sessions from other apps (import and apply Burn Points retroactively)

**Writes to Health Connect:**
- Exercise session record (type, duration, start/end time)
- Heart rate samples captured via BLE
- Total calories burned (active + Afterburn Estimate)

**Sync behaviour:**
- Auto-sync after each workout (writes)
- Background sync every 6 hours for imported data (reads, via WorkManager)
- Manual "Sync Now" button in Settings
- Clear permission explanations with per-scope toggles

---

### F9. Notifications & Reminders — COMPLETE

**Goal:** Keep users engaged without being annoying.

| Notification          | Trigger                                    | Default  |
|-----------------------|--------------------------------------------|----------|
| Workout reminder      | User-set time of day (e.g., 6:00 AM)       | Off      |
| Streak at risk        | 8 PM if no workout logged today + active streak | On   |
| Streak milestone      | Hit 7, 14, 30, 60, 100 day streak          | On       |
| Weekly summary        | Sunday evening — total points, time, comparison to last week | On |
| Personal best         | New record for single-session or weekly points | On    |
| Inactivity nudge      | No workout in 3+ days                       | Off      |

All notifications are individually toggleable in Settings. Stored in `NotificationPreferencesEntity`.

**ADHD mode additions:**
- "Just 5 Minutes" nudge: low-commitment prompt with one-tap Quick Start (F119)
- Daily quest notification each morning with 3 micro-challenges
- Accountability Alarm (F124): opt-in escalating alarm at user-set "latest start time"

**ASD mode additions:**
- All notifications sent at exact same time each day (predictable)
- Literal language: "You have not recorded a workout today" (not "Don't break your streak!")
- Every notification type requires explicit opt-in

---

### F10. Profile & Settings — COMPLETE

**Goal:** Full control over personal data, zones, preferences, and connected services.

**Sections:**
- **Personal info** — edit age, weight, height, resting HR, max HR
- **Zone settings** — adjust zone boundary percentages, toggle Karvonen formula
- **Daily target** — change Burn Point target
- **Units** — metric / imperial toggle
- **Connected devices** — manage paired BLE monitors
- **Health Connect** — permission status, sync settings, manual sync
- **Notifications** — per-notification toggles + reminder time picker
- **Data export** — export all workouts as CSV or JSON
- **Delete account / data** — wipe all local data with confirmation
- **About** — version, licenses, privacy policy link
- **Neurodivergent profile** — select Standard / ADHD / ASD / AuDHD; fine-tune individual settings
- **Sensory Control Panel** (F131, ASD mode) — granular control over animations, sounds, haptics, colours
- **Reward Shop** (F126, ADHD mode) — spend Burn Points on cosmetic unlocks

---

## Post-MVP Features (v1.x - v2.0)

---

### F11. Group Workout Mode — COMPLETE (Firebase)

**Goal:** Replicate the energy of group fitness classes — an instructor runs a session and all participants see a shared live display.

**How it works:**
1. Admin creates a **Group** in the app -> gets an 8-character invite code (SecureRandom)
2. Participants enter the code to join
3. Group system supports events, challenges, and member management
4. Post-session: everyone gets their individual summary + group leaderboard

**Implementation:** `GroupRepository`, `GroupEventRepository`, `GroupChallengeRepository` backed by Firestore subcollections. Admin auth checks enforce role-based access. `memberUids` array queries for efficient membership lookups.

**Firebase collections:** `groups` (with subcollections: `members`, `events`, `challenges`, `stats`)

---

### F12. Instructor Dashboard — PLANNED

**Goal:** A tablet/TV-optimised view for gym instructors to monitor the class.

**Features:**
- Grid of participant tiles (name, HR, zone colour, points)
- Sorting options (by zone, by points, alphabetical)
- Class-wide stats: avg zone, total class Burn Points, % of class in Push/Peak
- Timer controls (interval timer with work/rest segments)
- Chromecast / screen mirror support for gym TVs
- Session history for the instructor (attendance, avg performance)

---

### F13. Workout Templates — COMPLETE (26 templates)

**Goal:** Pre-built structured workouts that guide users through timed segments.

**Template categories and counts:**

| Category | Count | Templates |
|----------|-------|-----------|
| **Standard** | 5 | Free Run, Quick 15, Steady State, HIIT Intervals, Endurance |
| **Beginner** | 3 | Gentle Start (10m), Walk & Jog (20m), Easy Active (15m) |
| **Advanced** | 3 | Tabata (20m), Hill Climb (30m), Sprint Intervals (25m) |
| **Specialty** | 3 | Recovery (15m), Stress Relief (20m), Morning Energizer (10m) |
| **OTF-Style** | 12 | See F22 below |

**Template structure example:**
```
Template: "Steady State"
+-- Warm-Up    - 5 min  (target: Zone 2)
+-- Push Block  - 4 min  (target: Zone 4)
+-- Active Recovery - 2 min (target: Zone 3)
+-- Push Block  - 4 min  (target: Zone 4)
+-- Active Recovery - 2 min (target: Zone 3)
+-- Peak Effort - 2 min  (target: Zone 5)
+-- Active Recovery - 2 min (target: Zone 3)
+-- Push Block  - 4 min  (target: Zone 4)
+-- Peak Effort - 1 min  (target: Zone 5)
+-- Cool Down   - 4 min  (target: Zone 2)
```

**Behaviour:**
- Audio/haptic cues when segments change
- On-screen target zone indicator ("Get to PUSH!")
- Live feedback on whether you're hitting the target zone
- Templates filterable by duration and difficulty via Template Picker (F23)

**Implementation:** `TemplateRegistry` singleton providing all 26 templates with segment definitions.

---

### F14. Custom Workout Builder — COMPLETE

**Goal:** Let users design their own segment-based workouts.

**Builder UI (Routine Builder):**
- Add segments with: name, duration, target zone
- Drag to reorder segments
- Duplicate / delete segments
- Preview total duration and estimated Burn Points
- Save as personal template (reusable)
- Share template via link or code (post-MVP social feature)

**Navigation route:** routine_builder

---

### F15. Social Features & Leaderboards — COMPLETE (Firebase)

**Goal:** Community motivation through friendly competition.

**Features:**
- **Friends list** — add by username or invite link (`FriendsRepository`)
- **Weekly leaderboard** — ranked by total Burn Points among friends
- **Challenges** — 14 challenge types from single-session to month-long (F26)
- **Achievement badges** — 14 achievements tracked in Room (`AchievementEntity`)
- **Activity feed** — see friends' workout completions (opt-in, `sharedWorkouts` collection)
- **Privacy controls** — choose what's visible (points only, full summary, nothing)
- **Social Pressure Shield** (F135) — one toggle hides all social features

**Firebase collections:** `friendRequests`, `users/{uid}/friends`, `sharedWorkouts`

**Navigation routes:** social_hub, friends, add_friend, leaderboard, feed, accountability

---

### F16. Wear OS Companion App — PLANNED

**Goal:** See live workout data on your wrist without pulling out your phone.

---

### F17. Third-Party Fitness Platform Integration — PLANNED

**Goal:** Go beyond Health Connect with direct API integrations for richer data.

---

### F18. AI Coach Suggestions — PLANNED

**Goal:** Personalised post-workout tips powered by on-device analysis of workout patterns.

---

### F19. Data Export & Backup — COMPLETE

**Goal:** Users own their data and can take it anywhere.

**Export formats:**
- **CSV** — one row per workout with summary stats
- **JSON** — full structured export of all workouts, zone data, profile

---

### F20. Accessibility & Inclusivity — COMPLETE

**Goal:** Ensure PulseFit is usable by everyone.

**Features:**
- Full TalkBack / screen reader support with semantic descriptions and live regions
- High-contrast mode for outdoor visibility
- Haptic feedback for zone transitions (useful when screen isn't visible)
- Audio zone announcements — toggleable
- Configurable font sizes
- Colour-blind-friendly zone palette option (patterns + labels supplement colours)
- Animation preferences respected (reduced motion support)
- Form error announcements for accessibility
- Content descriptions on all interactive elements

---

### F21. Real-Time Voice Coach — COMPLETE (Composable Clip System)

**Goal:** An in-workout audio coach that gives spoken feedback, encouragement, and cues so users stay motivated without needing to look at the screen.

**Implementation:** `VoiceCoachEngine` with composable clip queue system. Clips are assembled from atomic audio segments and played sequentially through a `ConcurrentLinkedQueue` with TTS fallback for missing audio files. `@Volatile` thread safety on engine state.

**Voice Coach Styles:**

| Style | Character | Usage |
|-------|-----------|-------|
| LITERAL | Direct, factual, no hype | ASD Comfort Mode |
| STANDARD | Encouraging, balanced | Standard profile |
| HYPE | High-energy, motivational | ADHD Focus Mode |

**Composable clip categories:**
- Exercise announcements (exercise name clips)
- Target clips (speed/incline/watts, mode-dependent)
- Duration clips (20s, 30s, 45s, 1min through 15min)
- Motivation connectors (lets_go, you_got_this, strong_finish)
- Form cue clips (chest_up, drive_heels, full_range, control_tempo, squeeze_top, breathe)
- Push harder clips (add_1_mph, add_half_mph, raise_incline_2/4, add_20/50_watts)
- Station change clips (tread, row, floor)
- Zone change clips (rest, warm_up, active, push, peak)
- Time interval clips (5min through 60min)
- Milestone clips (start_first, complete_target, complete_pb, streak milestones)

**ND Profile Verbosity:**

| Profile | Max Queue Depth | Motivation Connectors | Push Cues | Mid-Exercise Interrupts |
|---------|-----------------|----------------------|-----------|------------------------|
| ASD | 2 | No | No | No |
| Standard | 4 | Yes | All | Yes |
| ADHD | 5 | Yes | All | Yes |
| AuDHD | 3 | No | All-out only | No |

---

## New Features (Implemented)

---

### F22. OTF-Style Class Format Templates — COMPLETE

**Goal:** Replicate the structure of Orangetheory-style multi-station class formats with timed blocks for tread, rower, and floor work.

**12 OTF-Style templates:**

| Template | Duration | Difficulty | Description |
|----------|----------|------------|-------------|
| 2G Power | 60 min | Hard | 2-group power format |
| 3G Endurance | 60 min | Moderate | 3-group endurance focus |
| 3G Strength | 60 min | Hard | 3-group strength focus |
| ESP | 60 min | Hard | Endurance/Strength/Power mixed |
| Tornado | 45 min | Hard | Rapid station rotations |
| Inferno | 45 min | Very Hard | High-intensity all stations |
| Lift 45 Upper | 45 min | Moderate | Upper body focus |
| Lift 45 Lower | 45 min | Moderate | Lower body focus |
| 23-Min Burn | 23 min | Moderate | Quick efficient session |
| 90-Min Marathon | 90 min | Very Hard | Extended endurance |
| Partner Throwdown | 60 min | Hard | Partner-format workout |
| Benchmark Day | 60 min | Hard | Benchmark assessment |

**Implementation:** Part of `TemplateRegistry` with full segment definitions including station assignments and coaching targets.

---

### F23. Template Picker — COMPLETE

**Goal:** Easy template discovery and selection via bottom sheet UI.

**Features:**
- Bottom sheet presentation with category grouping (Standard, Beginner, Advanced, Specialty, OTF-Style)
- Template cards showing name, duration, difficulty, station indicators
- Quick-start from template selection
- Integrates with Pre-Workout Visual Schedule (F143) for preview before starting

---

### F24. Equipment Profiling & Environment System — COMPLETE

**Goal:** Let users define their available equipment and workout environment so templates and plans can adapt.

**Workout environments (4):** GYM, HOME, OUTDOOR, HOTEL

**Equipment (16 items across 5 categories):**

| Category | Equipment |
|----------|-----------|
| Cardio | Treadmill, Rower, Bike, Elliptical |
| Free Weights | Dumbbells, Barbell, Kettlebell |
| Resistance | Resistance Bands, TRX, Cable Machine |
| Floor | Bench, Yoga Mat, Pull-Up Bar, Ab Roller, Bosu Ball |
| Bodyweight | Bodyweight (always available) |

**Default sets:** Gym = 12 items, Home = 4 items (Dumbbells, Resistance Bands, Yoga Mat, Bodyweight)

**Implementation:** `Equipment` enum, `WorkoutEnvironment` enum, `EquipmentProfile` stored as JSON in Room (`equipmentProfileJson` column in `UserProfileEntity`).

**Navigation route:** equipment_setup

---

### F25. Weekly Plan Generator — COMPLETE

**Goal:** Auto-generate a balanced weekly workout schedule based on available equipment and preferred training days.

**Features:**
- Creates plans based on equipment profile and schedule preferences
- Intelligently assigns rest days
- Calendar picker for selecting training days
- Remove-day support for schedule adjustments
- Calendar sync utility for exporting plan to device calendar

**Implementation:** `WeeklyPlanGenerator` produces plans stored in `WeeklyRoutineEntity`. `CalendarSync` utility handles Android calendar integration.

**Navigation route:** weekly_plan

---

### F26. Challenge System — COMPLETE (14 types)

**Goal:** Structured fitness challenges ranging from single-session benchmarks to month-long programs.

**Challenge categories and types:**

| Category | Challenge | Duration | Difficulty |
|----------|-----------|----------|------------|
| **Multi-Day Endurance** | Gauntlet Week | 8 workouts in 8 days | 5/5 |
| | Overdrive Week | 5 themed in 7 days | 4/5 |
| | The Countdown | 12 in 12 days | 4/5 |
| **Single-Session Benchmark** | Trifecta | 2000m row + 5km run + 300 floor reps | 5/5 |
| | Summit Climb | Progressive incline | 4/5 |
| | Outrun the Clock | Pace-based tread | 4/5 |
| | Circuit Blitz | 3-min rapid rotations | 3/5 |
| | The Ladder | Progressive intervals | 4/5 |
| **Month-Long** | Mileage Month | Accumulate tread miles | 3/5 |
| | Distance Expedition | Accumulate rowing meters | 3/5 |
| **Benchmark / Assessment** | Pulse Check | Quarterly benchmark | 3/5 |
| | PR Week | 5 PR days in 7 days | 3/5 |
| | Evolution Challenge | 30-day program, 16 workouts | 3/5 |
| **Group** | Tag Team | Collective group goal | 2/5 |

**Implementation:** `ChallengeRegistry` with challenge definitions. `UserChallengeRepository` tracks individual progress in Firestore (`users/{uid}/challenges`). Calendar sync for challenge events via `CalendarSync`.

**Navigation routes:** challenges, challenge_detail/{challengeType}

---

### F27. Exercise Registry — COMPLETE (20 exercises)

**Goal:** Comprehensive exercise database with form cues, station assignments, and coaching targets.

**20 exercises across 3 stations:**

| Station | Exercise | ID |
|---------|----------|----|
| **Tread** (5) | Base Pace, Push Pace, All-Out Sprint, Power Walk, Incline Walk | tread_* |
| **Row** (3) | Steady Row, Power Row, All-Out Row | row_* |
| **Floor** (12) | Squats, Lunges, Deadlifts, Chest Press, Shoulder Press, Bicep Curls, Tricep Extensions, Push-Ups, Plank, TRX Rows, Bench Hop-Overs, Pop Squats | floor_* |

**Implementation:** `ExerciseRegistry` singleton. Each exercise has form cues integrated into the voice coaching system.

---

### F28. Visual Exercise Guides — COMPLETE

**Goal:** Animated demonstrations of exercises so users can see proper form.

**Implementation:** `GuidedWorkoutManager` + `ExerciseGuideOverlay` composable with Lottie animation dependency for smooth, vector-based exercise demonstrations.

---

### F29. Composable Voice Coaching — COMPLETE

**Goal:** A modular audio system that assembles real-time coaching from atomic clip segments, with ND-aware verbosity control.

See F21 for full details. Key additions over basic voice coaching:
- **Clip queue system** — atomic audio segments composed into full sentences
- **ND verbosity profiles** — ASD: minimal (depth 2), ADHD: maximum (depth 5), AuDHD: balanced (depth 3), Standard: full (depth 4)
- **CoachingTargetRegistry** — OTF-accurate speed/incline/watt targets per exercise and mode
- **Form cue system** — 6 form cue clips integrated into exercise coaching
- **Push harder cues** — contextual cues to increase intensity (speed, incline, watts)
- **Station change announcements** — automatic callouts when switching between tread/row/floor

---

### F30. Group System — COMPLETE (Firebase)

**Goal:** Social workout groups with shared events, challenges, and member management.

**Features:**
- Create groups with admin controls
- 8-character SecureRandom invite codes for joining
- Group events (scheduled workouts, meetups)
- Group challenges (collective goals)
- Member management with role-based access
- Weekly stats tracking per group

**Firebase architecture:** `groups` collection with `members`, `events`, `challenges`, `stats` subcollections.

**Navigation routes:** groups, create_group, group_detail/{groupId}, join_group

---

### F31. Tread Mode Intelligence — COMPLETE

**Goal:** Differentiate coaching targets between runners and power walkers on the treadmill.

**Modes:**
- **Runner** — speed-based targets (5-9 mph), incline secondary
- **Power Walker** — incline-based targets (4-12%), speed secondary

**Implementation:** `treadMode` column in Room database. `CoachingTargetRegistry` provides mode-specific targets for each exercise. Voice coaching adapts clip selection based on active tread mode.

---

## Neurodivergent Features — ADHD Focus Mode (F111-F130) — COMPLETE

> These features activate when ADHD Focus Mode is enabled, or can be toggled individually. See `NEURODIVERGENT_DESIGN.md` for full specs.

---

### F111. Instant Micro-Rewards — COMPLETE
Continuous dopamine: confetti burst + point pop + sound + haptic every completed minute in Zone 3-5. Visual style varies (novelty). Configurable frequency.

### F112. "Just 5 Minutes" Start Mode — COMPLETE
Eliminate task initiation paralysis. One-tap 5-min workout; at 5:00 asks "Keep going?" All points count fully.

### F113. Variable Reward Drops — COMPLETE
~15% chance per minute of mystery bonus (extra BP, XP multiplier, cosmetic, streak freeze). Disabled in ASD mode (F139).

### F114. Dopamine Streak Multiplier — COMPLETE
Visual multiplier grows with streak: 1x -> 1.5x -> 2x -> 2.5x -> 3x. Applies to XP display, not actual BP.

### F115. Time Blindness Timer — COMPLETE
Colour-filling circle, haptic pulses every 5 min, voice time markers. ASD variant uses exact numbers.

### F116. Novelty Rotation Engine — COMPLETE
Rotates: UI colour (weekly), badge art (monthly), voice phrases (per session), micro-reward visuals. Disabled in ASD mode (F134).

### F117. Body Double Mode — COMPLETE (Firebase)
Opt-in match with simultaneous user. Minimal display (avatar, zone, "still going"). No chat/comparison. Firebase `activeSessions` collection.

### F118. Hyperfocus Capture Badge — COMPLETE
15+ unbroken minutes in Zone 3-5 earns special badge + 50 XP.

### F119. Zero-Friction Quick Start — COMPLETE
One-tap from: widget, notification, lock screen, Wear OS, app launch. Auto-connects last device.

### F120. XP & Leveling System — COMPLETE
Level 1-50, logarithmic XP. Sources: BP (1:10), streaks, badges, quests, drops. Cosmetic unlocks per level.

### F121. Daily Quests Board — COMPLETE
3 random micro-challenges daily from 50+ types. 25-50 XP each; all 3 = 100 XP bonus. Expire at midnight. Stored in `DailyQuestEntity`.

### F122. Progress Visualisation — COMPLETE
Garden/Pet/City grows with Burn Points. Visible on home screen. Withers (not dies) after 3+ inactive days.

### F123. Fidget Haptics — COMPLETE
Rhythmic vibration during Zone 1-2 rest phases. Faster/stimulating patterns (vs ASD calming haptics).

### F124. Accountability Alarm — COMPLETE
Escalating reminders at user-set time: gentle -> persistent -> alarm -> "Just 5 min?" -> silence. Never punitive.

### F125. Task Chunking Display — COMPLETE
"Block 2 of 6 — Push Zone" + progress bar + celebration per block. Free workouts auto-chunk in 5-min blocks.

### F126. Reward Shop — COMPLETE
Spend BP on cosmetics (separate balance). Themes, animations, voice packs, frames. New items monthly. Navigation route: reward_shop.

### F127. Anti-Burnout Detection — COMPLETE
Detect overtraining (7+ days at 150%+, rising HR, declining BP/min). Gentle suggestion + free streak freeze.

### F128. Social Accountability Contracts — COMPLETE (Firebase)
Pair with friend, set weekly goal, see completion. Optional consequence. Weekly check-in. Firebase `accountabilityContracts` collection with `weekly` subcollection.

### F129. Parallel Stimulation Mode — COMPLETE
PiP for video, podcast title on screen, split layout, music BPM sync.

### F130. Celebration Overkill Mode — COMPLETE
Max celebration on target: confetti, vibration, fanfare, voice, XP animation. Replaced by F138 in ASD mode.

---

## Neurodivergent Features — ASD Comfort Mode (F131-F145) — COMPLETE

> These features activate when ASD Comfort Mode is enabled, or can be toggled individually. See `NEURODIVERGENT_DESIGN.md` for full specs.

---

### F131. Sensory Control Panel — COMPLETE
Granular control: animations, transitions, sounds, haptics, colour intensity, confetti, screen shake, contrast. Each Off/Reduced/Full. Stored in `SensoryPreferencesEntity`. Navigation route: sensory_settings.

### F132. Routine Builder & Scheduler — COMPLETE
Fixed weekly schedule: assign templates to days with exact times. One-tap start. No judgement on deviation. Navigation route: routine_builder.

### F133. Literal Voice Coach Mode — COMPLETE
Factual, calm voice style. "Heart rate: 148. Active zone." No exclamations, metaphors, or slang. Implemented as LITERAL style in `VoiceCoachEngine`.

### F134. Predictable UI Lock — COMPLETE
Locks nav, layouts, colours, fonts, buttons. No A/B testing, no surprise popups, update previews.

### F135. Social Pressure Shield — COMPLETE
One toggle hides: leaderboards, feeds, comparative stats, sharing prompts, group invitations.

### F136. Deep Data Dashboard — COMPLETE
Per-second HR data, zone time to the second, mean/median/std dev, session comparison, CSV/JSON export. Navigation route: deep_data.

### F137. Transition Warnings — COMPLETE
Advance notice: 30s -> 10s -> 3-2-1 for segments. 2 min -> 1 min for workout end. Each channel toggleable.

### F138. Calm Celebration Mode — COMPLETE
Subtle checkmark + soft chime + "Daily target reached." No confetti/shake/fanfare. Replaces F130 in ASD mode.

### F139. Predictable Reward Schedule — COMPLETE
Fixed formula, no randomness, transparent XP. Replaces F113 in ASD mode.

### F140. Safe Exit Protocol — COMPLETE
One tap, no confirmation. "Workout saved. Well done." No streak warnings, no guilt. Silent streak freeze.

### F141. Texture & Pattern Zones — COMPLETE
Zone patterns beyond colour: horizontal lines, dots, diagonal stripes, crosshatch, solid fill.

### F142. Minimal Mode — COMPLETE
Essential-only UI: HR, zone name, points/target, time. Nothing else on screen.

### F143. Pre-Workout Visual Schedule — COMPLETE
Full workout structure shown before starting. Segments with name, duration, target zone. Navigation route: pre_workout_schedule/{workoutId}.

### F144. Consistent Audio Palette — COMPLETE
Fixed sounds per event (zone up/down, point, target, start, end). Never randomised.

### F145. Shutdown Routine — COMPLETE
Same post-workout sequence every time: stats -> breathing -> stretching -> saved -> home. Each skippable. Navigation route: shutdown_routine/{workoutId}.

---

## Competitive Positioning

| Feature | PulseFit | OrangeTheory | Peloton | WHOOP | Fitbod | Apple Fitness+ |
|---------|----------|-------------|---------|-------|--------|---------------|
| HR zone training | Yes | Yes | Yes | Yes | No | Yes |
| Original points system | Burn Points | Splat Points* | No | Strain Score | No | No |
| ADHD Focus Mode | Yes | No | No | No | No | No |
| ASD Comfort Mode | Yes | No | No | No | No | No |
| AuDHD Combined Mode | Yes | No | No | No | No | No |
| Sensory Control Panel | Yes | No | No | No | No | No |
| Composable voice coaching | Yes | In-class only | Instructor-led | No | No | Instructor-led |
| ND-aware verbosity | 4 profiles | No | No | No | No | No |
| Custom zone thresholds | Yes | No | No | Yes | No | No |
| Workout templates | 26 | Class schedule | Library | No | AI-generated | Library |
| Challenge system | 14 types | Monthly | Challenges | No | No | Activity rings |
| Equipment profiling | 16 items | Gym-only | Bike/Tread | No | Yes | No |
| Weekly plan generator | Yes | Class booking | Programs | No | Yes | No |
| Health Connect | Full R/W | No | No | No | No | No |
| Price | Free / $4.99 | $12-169/mo | $12.99-44/mo | $30/mo | $12.99/mo | $9.99/mo |

*Splat Points is a trademark of Orangetheory Fitness. PulseFit's Burn Points system is an original, independently designed scoring mechanic.

---

## Feature Priority Matrix (Updated)

| Priority | Feature | Status | Effort | Impact |
|----------|---------|--------|--------|--------|
| P0 (MVP) | F1 Onboarding | **COMPLETE** | Medium | High |
| P0 (MVP) | F2 Zone Engine | **COMPLETE** | Low | High |
| P0 (MVP) | F3 BLE Pairing | **COMPLETE** | Medium | High |
| P0 (MVP) | F4 Live Workout | **COMPLETE** | High | High |
| P0 (MVP) | F5 Workout Summary | **COMPLETE** | Medium | High |
| P0 (MVP) | F6 Burn Points & Streaks | **COMPLETE** | Medium | High |
| P0 (MVP) | F7 History & Trends | **COMPLETE** | Medium | Medium |
| P0 (MVP) | F8 Health Connect | **COMPLETE** | Medium | High |
| P0 (MVP) | F9 Notifications | **COMPLETE** | Low | Medium |
| P0 (MVP) | F10 Profile & Settings | **COMPLETE** | Low | Medium |
| P0 (MVP) | F111 Micro-Rewards | **COMPLETE** | Low | High |
| P0 (MVP) | F112 "Just 5 Minutes" | **COMPLETE** | Low | High |
| P0 (MVP) | F119 Quick Start | **COMPLETE** | Low | High |
| P0 (MVP) | F131 Sensory Control Panel | **COMPLETE** | Medium | High |
| P0 (MVP) | F134 Predictable UI Lock | **COMPLETE** | Low | High |
| P0 (MVP) | F140 Safe Exit Protocol | **COMPLETE** | Low | High |
| P1 | F11 Group Workout | **COMPLETE** | High | High |
| P1 | F13 Workout Templates | **COMPLETE** | Medium | High |
| P1 | F14 Custom Builder | **COMPLETE** | Medium | Medium |
| P1 | F21 Voice Coach | **COMPLETE** | Medium | High |
| P1 | F22 OTF-Style Templates | **COMPLETE** | Medium | High |
| P1 | F23 Template Picker | **COMPLETE** | Low | Medium |
| P1 | F24 Equipment Profiling | **COMPLETE** | Medium | Medium |
| P1 | F25 Weekly Plan Generator | **COMPLETE** | Medium | High |
| P1 | F26 Challenge System | **COMPLETE** | High | High |
| P1 | F27 Exercise Registry | **COMPLETE** | Medium | Medium |
| P1 | F28 Visual Exercise Guides | **COMPLETE** | Medium | High |
| P1 | F29 Composable Voice Coaching | **COMPLETE** | High | High |
| P1 | F30 Group System | **COMPLETE** | High | High |
| P1 | F31 Tread Mode Intelligence | **COMPLETE** | Medium | Medium |
| P1 | F113-F130 ADHD Features | **COMPLETE** | Medium | High |
| P1 | F131-F145 ASD Features | **COMPLETE** | Medium | High |
| P2 | F12 Instructor Dashboard | PLANNED | High | Medium |
| P2 | F15 Social & Leaderboards | **COMPLETE** | High | High |
| P2 | F16 Wear OS | PLANNED | High | Medium |
| P2 | F17 Third-Party APIs | PLANNED | High | Medium |
| P2 | F18 AI Coach | PLANNED | Medium | Medium |
| P3 | F19 Data Export | **COMPLETE** | Low | Low |
| P3 | F20 Accessibility | **COMPLETE** | Medium | High |
