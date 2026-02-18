# PulseFit — Feature Specification

> Complete feature list for PulseFit, including core features (F1-F22), ADHD Focus Mode features (F111-F130), and ASD Comfort Mode features (F131-F145).

---

## Table of Contents

1. [Core Features (F1 - F22)](#1-core-features-f1---f22)
2. [ADHD Focus Mode Features (F111 - F130)](#2-adhd-focus-mode-features-f111---f130)
3. [ASD Comfort Mode Features (F131 - F145)](#3-asd-comfort-mode-features-f131---f145)
4. [Feature Priority Matrix](#4-feature-priority-matrix)

---

## 1. Core Features (F1 - F22)

### F1 Onboarding & Profile Setup
**Priority:** P0 | **ND Impact:** ADHD + ASD

User registration and initial profile configuration.

- Collect: name, age (for max HR calculation), optional weight/height
- BLE heart rate monitor pairing walkthrough
- **[ND] Optional "Personalise your experience" step** — select Standard, ADHD Focus, ASD Comfort, or AuDHD profile (see [NEURODIVERGENT_DESIGN.md - Profile System](NEURODIVERGENT_DESIGN.md#2-neurodivergent-profile-system))
- Profile selection uses approachable, non-clinical language
- Skippable — defaults to Standard if skipped
- Minimal steps to reduce drop-off (especially important for ADHD users)

### F2 BLE Heart Rate Monitor Pairing
**Priority:** P0

Connect to any Bluetooth Low Energy heart rate monitor.

- Scan for nearby BLE devices advertising Heart Rate Service (UUID 0x180D)
- Display discovered devices with signal strength
- One-tap pairing
- Auto-reconnect to last paired device
- Support for multiple saved devices
- Connection status indicator on workout screen

### F3 Real-Time Heart Rate Display
**Priority:** P0

Show current heart rate with zone indicator.

- Large, readable HR number (BPM)
- Colour-coded zone indicator (Zone 1-5)
- HR graph/trend line (last 60 seconds)
- Connection quality indicator
- Updates at sensor reporting rate (typically 1Hz)

### F4 Heart Rate Zone Engine
**Priority:** P0 | **ND Impact:** ADHD + ASD

Calculate and manage HR zones in real-time.

- Five zones based on % of max HR (see [PLAN.md - Zones](PLAN.md#heart-rate-zones))
- Max HR = 220 - age (with manual override)
- Zone change detection with configurable hysteresis (avoid zone flickering)
- Zone change events trigger:
  - **[ND-ADHD]** Micro-reward animations (F111)
  - **[ND-ASD]** Texture/pattern zone indicators (F141)
  - Voice coach announcement
  - Haptic feedback (respects F131 sensory settings)

### F5 Burn Points System
**Priority:** P0 | **ND Impact:** ADHD + ASD

Core gamification currency earned through effort.

- Points per minute based on current zone (0/0/1/2/3 for Rest/Warm-Up/Active/Push/Peak)
- Daily target: 12 Burn Points (default, adjustable)
- Accumulated in real-time during workout
- Running total visible on workout screen
- **[ND-ADHD]** Points trigger micro-reward pop animations (F111), contribute to XP (F120)
- **[ND-ASD]** Fixed, predictable formula with no random bonuses (F139); estimated total shown before workout starts
- Lifetime accumulation tracked in UserProfile

### F6 Voice Coach
**Priority:** P1 | **ND Impact:** ADHD + ASD

Real-time audio coaching using text-to-speech.

- Zone change announcements
- Periodic time and progress updates
- Encouragement at milestones
- Three voice styles:
  - **Standard:** Balanced encouragement and information
  - **[ND-ADHD] Hype:** High energy, exclamation-heavy, "You're crushing it!" (see HypeCoach.kt)
  - **[ND-ASD] Literal:** Factual, calm, no slang or metaphors (F133, see LiteralCoach.kt)
- Three verbosity levels: Minimal / Standard / Detailed
- Controlled by Sensory Control Panel (F131) when ASD mode active
- Mute button always accessible during workout

### F7 Streak & Consistency Tracking
**Priority:** P1 | **ND Impact:** ADHD + ASD

Track consecutive workout days.

- Current streak count on home screen
- Longest streak record
- Calendar view of workout days
- **[ND-ADHD]** Streak multiplier on Burn Points (F114); Streak Shield (one free miss/week); streak milestones trigger Celebration Overkill (F130)
- **[ND-ASD]** No guilt messaging for broken streaks; Safe Exit (F140) never warns about streak loss; streak display can be hidden via Social Pressure Shield (F135)

### F8 Workout History & Review
**Priority:** P1

Browse and review past workouts.

- Chronological list of all workouts
- Per-workout detail: duration, Burn Points, avg/max HR, zone breakdown
- Filterable by date range, workout type, duration
- Delete/edit workout entries
- Summary statistics (weekly, monthly)

### F9 Haptic Feedback System
**Priority:** P1 | **ND Impact:** ADHD + ASD

Vibration patterns for workout events.

- Zone change haptic pattern
- Point earned pulse
- Workout milestone vibration
- **[ND-ADHD]** Fidget haptics during rest periods — stimulating, varied patterns (F123)
- **[ND-ASD]** Calming, predictable rhythmic patterns; full control via Sensory Control Panel (F131); can be disabled entirely
- Respects system-level haptic settings

### F10 Settings & Preferences
**Priority:** P1 | **ND Impact:** ADHD + ASD

App configuration screen.

- Profile editing (name, age, max HR override)
- HR monitor management
- Notification preferences
- Data export options
- **[ND] Neurodivergent Profile section:**
  - Change ND profile (Standard / ADHD / ASD / AuDHD)
  - **[ND-ADHD]** ADHD settings: micro-rewards, streak multiplier, novelty, celebrations, etc.
  - **[ND-ASD]** ASD settings: Sensory Control Panel (F131), Predictable UI Lock (F134), Social Shield (F135), voice coach style, etc.
  - Individual setting overrides regardless of profile

### F11 Workout Timer
**Priority:** P1

Countdown and elapsed time display.

- Elapsed time (always visible)
- Countdown timer for timed workouts
- Phase timer for interval workouts (warm-up, work, rest, cool-down)
- Large, readable format
- ASD mode: time display never changes position (F134)

### F12 Cool-Down Detection
**Priority:** P2

Detect when user is cooling down post-workout.

- Monitor HR decline rate
- Suggest cool-down start when HR drops below Active zone for 2+ minutes
- Optional automatic cool-down phase trigger
- ASD mode: transition warning before cool-down suggestion (F137)

### F13 Post-Workout Summary
**Priority:** P1

Detailed workout recap screen.

- Total duration, Burn Points, XP earned
- Zone time breakdown (bar chart)
- HR graph over full workout
- Personal records highlighted
- ADHD mode: celebratory animation, level progress shown
- ASD mode: clean data layout, no auto-sharing prompts, Shutdown Routine (F145) before summary

### F14 Achievement Badges
**Priority:** P2

Unlock badges for milestones.

- First workout, 10 workouts, 100 workouts
- Zone-specific badges (30 min in Push zone, etc.)
- Streak milestones (7, 30, 100 days)
- Total Burn Points milestones
- ADHD mode: Hyperfocus Badge (F118), badges trigger Celebration Overkill (F130)
- ASD mode: Calm Celebration (F138), badges displayed in predictable grid

### F15 Weekly Goals
**Priority:** P2

Set and track weekly workout targets.

- Target: workouts per week, total minutes, total Burn Points
- Progress bar on home screen
- Weekly reset with summary
- ADHD mode: quest-like framing via Daily Quests (F121)
- ASD mode: same goals every week unless manually changed

### F16 Calorie Estimation
**Priority:** P2

Estimate calories burned using HR data.

- Formula based on age, weight, sex, HR
- Displayed on workout and summary screens
- Clearly labelled as estimate
- ASD Deep Data mode (F136): show formula and confidence interval

### F17 Workout Types / Templates
**Priority:** P2

Predefined and custom workout structures.

- Templates: Free Run, Interval, HIIT, Steady State, Cool-Down
- Custom template creation
- Template sets target zones and phase durations
- ASD mode: templates provide the predictable structure users need
- ADHD mode: "Surprise Me" random template option (F116)

### F18 Rest Timer
**Priority:** P2

Countdown timer for rest periods between intervals.

- Configurable rest duration
- Audio and haptic alert at end of rest
- ADHD mode: fidget haptics (F123) available during rest
- ASD mode: transition warning before rest ends (F137)

### F19 Progress Dashboard
**Priority:** P2

Overview of fitness progress over time.

- Weekly/monthly/yearly views
- Trend lines for: workout frequency, avg HR, total Burn Points, avg duration
- ADHD mode: Progress Visualisation (F122) — heat maps, mountain, garden
- ASD mode: Deep Data Dashboard (F136) — raw numbers, tables, CSV export

### F20 Leaderboards
**Priority:** P2 | **ND Impact:** ASD

Compare performance with other users.

- Weekly Burn Points leaderboard
- Friend leaderboard
- Global leaderboard
- Opt-in only
- **[ND-ASD]** Hidden entirely when Social Pressure Shield (F135) is enabled
- **[ND-ADHD]** Enhanced with Social Contracts (F128) — promise a friend you'll work out

### F21 Activity Feed
**Priority:** P2 | **ND Impact:** ASD

Social feed of friend activity.

- "X completed a workout" updates
- Achievement unlocks from friends
- Streak milestones
- Like / high-five reactions
- **[ND-ASD]** Hidden entirely when Social Pressure Shield (F135) is enabled
- **[ND-ADHD]** Shows Body Double count ("12 people working out now") (F117)

### F22 Friend System
**Priority:** P2

Add and manage friends.

- Search by username
- Friend request / accept / decline
- Friend list with online status
- Privacy controls (what friends can see)
- ASD mode: profile defaults to private (F135)

---

## 2. ADHD Focus Mode Features (F111 - F130)

> Full design details, principles, and rationale in [NEURODIVERGENT_DESIGN.md - ADHD Focus Mode](NEURODIVERGENT_DESIGN.md#3-adhd-focus-mode)

### F111 Micro-Reward Engine
**Priority:** P0 | **Depends on:** F5

Small, frequent dopamine hits throughout the workout. Points earned every 30 seconds in-zone with visual pop animation, haptic pulse, and sound effect on milestones. Running total always visible.

### F112 "Just 5 Minutes" Mode
**Priority:** P0 | **Depends on:** F4, F5

One-tap 5-minute workout from home screen. Eliminates initiation barrier. At 5 minutes, gentle prompt to continue with rolling "+1 min" counter. Full celebration if user stops at 5 minutes.

### F113 Variable Reward Drops
**Priority:** P1 | **Depends on:** F111

Random loot drops during workout: bonus XP, cosmetic unlocks, achievement fragments. Drop probability increases with time in-zone. Visual and haptic surprise effect. Never the same drop twice per session.

### F114 Streak Multiplier
**Priority:** P1 | **Depends on:** F7

Escalating Burn Point multiplier for consecutive days. Day 1: 1x through Day 30: 5x. Streak Shield allows one free miss per week. Streak freeze earnable through Reward Shop (F126).

### F115 Time Blindness Timer
**Priority:** P1 | **Depends on:** F11

Large colour-coded timer with visual progress bar. Periodic voice announcements of elapsed time. Vibration pattern changes every 5 minutes. Optional "time chunks" view dividing workout into 5-min blocks.

### F116 Novelty Engine
**Priority:** P2

Rotate UX elements to prevent habituation. Weekly colour theme rotation, randomised celebration animations (pool of 20+), daily challenge flavour rotation, seasonal events. "Surprise me" workout mode.

### F117 Body Double Mode
**Priority:** P2

Simulate presence of another person working out. Ambient gym sounds, ghost avatar, optional live friend sync, anonymous count of active PulseFit users. Purely presence-based, no competition.

### F118 Hyperfocus Badge
**Priority:** P3 | **Depends on:** F4, F14

Detect sustained high-zone effort (15+ min in Push/Peak zones without pause). Award special badge with unique animation. Track hyperfocus sessions in history. Weekly hyperfocus minutes stat.

### F119 Quick Start
**Priority:** P0

One tap from app open to heart rate tracking. Large "GO" button on home screen. Uses last workout settings. HR monitor auto-connects if previously paired. Zero configuration required.

### F120 XP & Leveling System
**Priority:** P1 | **Depends on:** F5

Every Burn Point earns XP. XP accumulates across all workouts with scaling level curve. Each level unlocks something (theme, badge, shop item). Level-up celebration with special animation.

### F121 Daily Quests
**Priority:** P1 | **Depends on:** F5

Three daily quests generated each morning. Mix of Easy/Medium/Hard. Each awards bonus XP. Quests rotate themes. Completing all three awards "Quest Master" bonus.

### F122 Progress Visualisation
**Priority:** P2 | **Depends on:** F8

Weekly heat map, monthly "mountain" visualisation, "garden" metaphor. Before/after stat comparisons. Exportable progress images.

### F123 Fidget Haptics
**Priority:** P3 | **Depends on:** F9

Selectable stimulating haptic patterns during rest periods: heartbeat, metronome, wave, random. Intensity adjustable. Can be combined with music tempo. Different from ASD calming haptics.

### F124 Accountability Alarm
**Priority:** P3

Escalating reminders: gentle -> persistent -> alarm. "Your workout buddy is waiting" framing. 5-min snooze only. Smart timing learns user patterns. Linkable to Body Double Mode (F117).

### F125 Task Chunking
**Priority:** P1 | **Depends on:** F11

Display workouts as 5-minute chunks. Each chunk has mini-goal and mini-celebration. "Just one more chunk" prompting. Current chunk highlighted, future dimmed. Visible progress blocks.

### F126 Reward Shop
**Priority:** P2 | **Depends on:** F120

Spend XP on themes, celebration animations, avatar items, streak shields. Rotating inventory for novelty. Daily deal featured item. Earn-only prestige items.

### F127 Anti-Burnout System
**Priority:** P2 | **Depends on:** F8

Track frequency and intensity over 7-day rolling window. Suggest rest days when trending toward overtraining. Rest days earn small XP bonus. No streak penalty for system-recommended rest.

### F128 Social Contracts
**Priority:** P3 | **Depends on:** F22

Promise a friend you'll work out today. Friend gets notified on completion. Two-way contracts available. Gentle, not punitive. Maximum 3 active contracts.

### F129 Parallel Stimulation
**Priority:** P2

Podcast/music integration, picture-in-picture video, minimal overlay mode, background mode with notification tracking, audio-only coaching when screen off.

### F130 Celebration Overkill
**Priority:** P1 | **Depends on:** F9

Full-screen animation + sound + haptic burst on completion. Extra-long level-up sequence. Special themed streak milestone celebrations. Unique PR animation. Trophy Room for revisiting.

---

## 3. ASD Comfort Mode Features (F131 - F145)

> Full design details, principles, and rationale in [NEURODIVERGENT_DESIGN.md - ASD Comfort Mode](NEURODIVERGENT_DESIGN.md#4-asd-comfort-mode)

### F131 Sensory Control Panel
**Priority:** P0 | **ND Profile:** ASD, AUDHD

Master controls for all sensory output. Animations (Off/Reduced/Full), Sounds (Off/Quiet/Normal), Haptics (Off/Gentle/Strong), Colour Intensity (Muted/Standard/Vivid). Accessible from Settings and during workout. Individual overrides per feature. Changes apply immediately with no transition animation.

### F132 Routine Builder & Scheduler
**Priority:** P1 | **ND Profile:** ASD, AUDHD

Fixed weekly workout schedule. Define workout per day with warm-up, main, cool-down durations and target zone. Remind at exact same time daily. "Today's workout" on home screen. Deviation requires explicit confirmation. Full-week visual grid.

### F133 Literal Voice Coach Mode
**Priority:** P1 | **Depends on:** F6 | **ND Profile:** ASD, AUDHD

Factual, calm voice coaching. No exclamation marks, slang, colloquialisms, rhetorical questions, or metaphors. Numbers and facts only. Calm TTS voice. Configurable verbosity: Minimal (zone changes) / Standard (periodic) / Detailed (every 30s).

### F134 Predictable UI Lock
**Priority:** P0 | **ND Profile:** ASD, AUDHD

Freeze interface layout. No UI element changes position. No rotating banners, tip cards, dynamic content, or "what's new" pop-ups. No A/B tests applied. New features only in dedicated Settings section.

### F135 Social Pressure Shield
**Priority:** P1 | **ND Profile:** ASD, AUDHD

One toggle to remove all social and comparative elements. Hides leaderboards (F20), activity feed (F21), friend notifications, comparative language. Profile set to private. Individual social features can be re-enabled selectively.

### F136 Deep Data Dashboard
**Priority:** P2 | **ND Profile:** ASD

Raw HR data table (per-second), zone time breakdown (exact seconds), statistical analysis (mean, median, std dev, min, max), HRV if supported, per-workout comparison graphs, exportable CSV, customisable dashboard, trend analysis. No interpretation unless requested.

### F137 Transition Warnings
**Priority:** P1 | **ND Profile:** ASD, AUDHD

Advance notice before any change. "Changing to cool-down in 30 seconds." Countdown overlay for phase transitions. Audio announcement in Literal Voice Coach style. Warning intervals configurable: 10s/30s/60s. No sudden screen changes.

### F138 Calm Celebration Mode
**Priority:** P1 | **ND Profile:** ASD, AUDHD

Quiet success indicators. Gentle checkmark (no confetti), subtle badge glow with soft tone, progress bar fill with quiet chime. All celebrations under 2 seconds. No full-screen takeovers. Option to disable all celebration animations.

### F139 Predictable Reward Schedule
**Priority:** P1 | **ND Profile:** ASD, AUDHD

Fixed formula: earn exactly X points for exactly Y effort. No random bonuses, no variable multipliers (unless user opts in). Formula explained in persistent tooltip. Estimated points shown before workout. End-of-workout total matches running total exactly.

### F140 Safe Exit Protocol
**Priority:** P0 | **ND Profile:** ASD, AUDHD

Single "End Workout" button, always visible, one tap to end. No confirmation dialog, no streak warnings, no "you were so close" messages. End message always: "Workout saved. Well done." Partial workout saved and earns proportional points.

### F141 Texture & Pattern Zones
**Priority:** P2 | **ND Profile:** ASD

Zone indicators use distinct patterns: Rest (dotted), Warm-Up (horizontal lines), Active (diagonal hatching), Push (cross-hatching), Peak (solid fill). Visible on all zone indicators. Supports colour vision differences.

### F142 Minimal Mode
**Priority:** P1 | **ND Profile:** ASD

Stripped UI showing only: current HR (large, centre), zone name, elapsed time, Burn Points. No zone bar, graphs, avatar, animations, or secondary stats. Black/dark grey background. Maximum contrast. Largest font sizes.

### F143 Pre-Workout Visual Schedule
**Priority:** P2 | **ND Profile:** ASD

Full workout timeline upfront showing all phases with duration, target zone, expected effort. Scrollable and zoomable. "Start" only appears after review. During workout, current position highlighted. No deviations from shown schedule.

### F144 Consistent Audio Palette
**Priority:** P2 | **ND Profile:** ASD

Fixed sound mapping: zone up = rising tone, zone down = falling tone, point = soft click, start = chime, end = double chime, transition = triple chime. No randomised sounds. No seasonal changes. Sound map documented in Settings.

### F145 Shutdown Routine
**Priority:** P2 | **ND Profile:** ASD

Guided post-workout wind-down: cool-down text (3s) -> breathing guide (1 min, 4-4-4) -> stretch prompt (30s, skippable) -> summary screen -> "Session complete." Same sequence every time. Each step has visible countdown. No share/rate/review prompts.

---

## 4. Feature Priority Matrix

### P0 — MVP (Must Ship)

| ID | Feature | Category |
|----|---------|----------|
| F2 | BLE HR Monitor Pairing | Core |
| F3 | Real-Time HR Display | Core |
| F4 | HR Zone Engine | Core |
| F5 | Burn Points System | Core |
| F1 | Onboarding & Profile Setup | Core |
| F119 | Quick Start | ADHD |
| F112 | "Just 5 Minutes" Mode | ADHD |
| F111 | Micro-Reward Engine | ADHD |
| F131 | Sensory Control Panel | ASD |
| F134 | Predictable UI Lock | ASD |
| F140 | Safe Exit Protocol | ASD |

### P1 — Core Experience

| ID | Feature | Category |
|----|---------|----------|
| F6 | Voice Coach | Core |
| F7 | Streak & Consistency | Core |
| F8 | Workout History | Core |
| F9 | Haptic Feedback | Core |
| F10 | Settings & Preferences | Core |
| F11 | Workout Timer | Core |
| F13 | Post-Workout Summary | Core |
| F113 | Variable Reward Drops | ADHD |
| F114 | Streak Multiplier | ADHD |
| F115 | Time Blindness Timer | ADHD |
| F120 | XP & Leveling | ADHD |
| F121 | Daily Quests | ADHD |
| F125 | Task Chunking | ADHD |
| F130 | Celebration Overkill | ADHD |
| F132 | Routine Builder & Scheduler | ASD |
| F133 | Literal Voice Coach Mode | ASD |
| F135 | Social Pressure Shield | ASD |
| F137 | Transition Warnings | ASD |
| F138 | Calm Celebration Mode | ASD |
| F139 | Predictable Reward Schedule | ASD |
| F142 | Minimal Mode | ASD |

### P2 — Enhanced Experience

| ID | Feature | Category |
|----|---------|----------|
| F12 | Cool-Down Detection | Core |
| F14 | Achievement Badges | Core |
| F15 | Weekly Goals | Core |
| F16 | Calorie Estimation | Core |
| F17 | Workout Types / Templates | Core |
| F18 | Rest Timer | Core |
| F19 | Progress Dashboard | Core |
| F20 | Leaderboards | Core |
| F21 | Activity Feed | Core |
| F22 | Friend System | Core |
| F116 | Novelty Engine | ADHD |
| F117 | Body Double Mode | ADHD |
| F122 | Progress Visualisation | ADHD |
| F126 | Reward Shop | ADHD |
| F127 | Anti-Burnout System | ADHD |
| F129 | Parallel Stimulation | ADHD |
| F136 | Deep Data Dashboard | ASD |
| F141 | Texture & Pattern Zones | ASD |
| F143 | Pre-Workout Visual Schedule | ASD |
| F144 | Consistent Audio Palette | ASD |
| F145 | Shutdown Routine | ASD |

### P3 — Nice to Have

| ID | Feature | Category |
|----|---------|----------|
| F118 | Hyperfocus Badge | ADHD |
| F123 | Fidget Haptics | ADHD |
| F124 | Accountability Alarm | ADHD |
| F128 | Social Contracts | ADHD |

---

### Feature Count Summary

| Category | Count |
|----------|-------|
| Core Features (F1-F22) | 22 |
| ADHD Features (F111-F130) | 20 |
| ASD Features (F131-F145) | 15 |
| **Total** | **57** |

| Priority | Count |
|----------|-------|
| P0 (MVP) | 11 |
| P1 (Core) | 21 |
| P2 (Enhanced) | 21 |
| P3 (Nice to Have) | 4 |

---

*See also:*
- [PLAN.md](PLAN.md) — Project plan, data model, phases, architecture
- [NEURODIVERGENT_DESIGN.md](NEURODIVERGENT_DESIGN.md) — Full ND design philosophy, all ND features detailed
