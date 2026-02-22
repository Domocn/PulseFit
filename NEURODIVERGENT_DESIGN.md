# PulseFit — Neurodivergent Design Philosophy & Features

> **Implementation Status: COMPLETE** — All 35 ND features (F111-F145) are implemented and building. PulseFit is the first fitness app with dedicated neurodivergent experience modes. No competitor offers ADHD, ASD, or AuDHD-specific features.

## Overview

PulseFit is the first fitness app designed with neurodivergent users in mind. Rather than bolting accessibility onto a neurotypical design, PulseFit embeds ND-friendly principles into its core UX through toggleable profiles.

**Profiles** (combinable):
- **Standard** — default balanced gamification
- **ADHD Focus Mode** — dopamine-first, zero friction, novelty, instant rewards
- **ASD Comfort Mode** — predictable, calm, literal, sensory-controlled, deep data
- **AuDHD Combined** — cherry-picked defaults from both (ADHD+ASD overlap)

Profiles are offered during onboarding and changeable in Settings. Each profile sets defaults; every setting remains manually tuneable.

---

## User Personas & Journey Scenarios

### Jamie — ADHD Focus Mode

**Profile:** 28, diagnosed ADHD-C at 22. Personal trainer suggested heart rate training but Jamie abandoned three fitness apps within 2 weeks. Main barriers: forgetting to work out, can't start even when motivated ("initiation paralysis"), gets bored mid-workout, loses track of time.

**Journey with PulseFit:**
1. **Onboarding:** Selects ADHD Focus Mode. Onboarding reduces to 3 taps — age, target, start. Everything else deferred.
2. **Day 1:** Gets "Just 5 Minutes" notification at set time. One tap starts workout. At 5:00 mark: "Nice! Keep going?" — Jamie says yes, runs 25 minutes. Gets Celebration Overkill with confetti and XP animation. Unlocks "First Steps" achievement.
3. **Week 1:** Daily quests provide fresh micro-goals each morning. Variable reward drops keep workouts surprising. Streak multiplier hits 1.5x at day 3. Body double mode makes Jamie feel less alone during evening workouts.
4. **Month 1:** Level 12, unlocked "Ember" theme. Accountability alarm catches missed days. Task chunking makes 60-min OTF-style templates feel doable ("Block 2 of 6"). Longest streak: 14 days.
5. **Key insight:** Jamie hasn't abandoned the app because every session feels slightly different (novelty engine) and starting requires zero decisions (Quick Start).

### Alex — ASD Comfort Mode

**Profile:** 34, diagnosed autistic at 30. Loves data and routine. Tried OrangeTheory but the loud music, unpredictable class format, and social pressure caused shutdowns. Wants structured, predictable workouts with full control over sensory output.

**Journey with PulseFit:**
1. **Onboarding:** Selects ASD Comfort Mode. Sees "Step 2 of 6" with full step map. Reviews all settings before confirming. Sensory Control Panel immediately available.
2. **Day 1:** Configures sensory panel: animations OFF, sounds QUIET, haptics GENTLE, contrast HIGH. Picks "Steady State" template. Pre-Workout Visual Schedule shows exactly what's coming. Literal voice coach: "Heart rate: 148. Active zone. Push zone requires above 160." Workout ends with calm checkmark and soft tone.
3. **Week 1:** Routine Builder assigns same templates to same days. "Today's workout: Steady State at 6:00 AM" — one tap starts. Transition warnings give 30-second advance notice. Safe Exit available at any time with zero guilt.
4. **Month 1:** Deep Data Dashboard shows per-second HR data with mean, median, std dev. Exports CSV for personal spreadsheet. Social Pressure Shield hides all leaderboards and comparison features. Consistent Audio Palette means zone-up chime always sounds the same.
5. **Key insight:** Alex hasn't had a single shutdown during workouts because every sensory output is controlled, every session is predictable, and there's no social pressure.

### Taylor — AuDHD Combined Mode

**Profile:** 26, diagnosed ADHD at 16 and autistic at 24. Needs the dopamine hits of ADHD mode but the sensory safety of ASD mode. Previous fitness apps were either too stimulating (ADHD features with flashing animations) or too boring (minimal apps with no rewards).

**Journey with PulseFit:**
1. **Onboarding:** Selects AuDHD Combined Mode. Defaults: calm UI (ASD) with structured rewards (ADHD). Variable drops OFF (unpredictability is more distressing than rewarding). Novelty rotation OFF for UI, ON for quests.
2. **Day 1:** Micro-rewards ON but with reduced animation (1-second confetti, not 3-second). Literal voice coach with frequent check-ins. Task chunking active. Safe exit always available.
3. **Week 1:** Routine builder provides structure (ASD need). Daily quests provide novelty (ADHD need). XP/leveling provides long-term progression (both). Body double mode provides presence without social pressure.
4. **Month 1:** Fine-tunes individual settings — turns fidget haptics ON during rest phases (ADHD stimulation need), keeps transition warnings ON (ASD preparation need). Time blindness timer shows exact numbers (satisfies both: concrete for ADHD, precise for ASD).
5. **Key insight:** Taylor's AuDHD settings aren't a compromise — they're a synthesis. PulseFit resolves the ADHD-ASD conflict by defaulting to ASD comfort settings and surfacing ADHD features as toggleable options.

---

## Part 1: ADHD Focus Mode — COMPLETE

### Design Principles

| Principle | In PulseFit |
|-----------|------------|
| **Zero friction** | Max 1 tap to primary action; one-tap start from widget/notification/lock screen |
| **Dopamine-first** | Instant visual/audio/haptic reward on every point earned |
| **Novelty engine** | Rotate UI colours, badge art, voice phrases to prevent habituation |
| **Chunk everything** | "Block 2 of 6" not "28 min remaining" |
| **Externalise time** | Giant colour-filling timer, haptic pulses, voice time markers |
| **Reduce decisions** | Smart defaults, auto-suggestions, "just do this" prompts |

### Modifications to Existing Features

**F1 Onboarding:** Reduce to 3 taps (age -> target -> start). Optional steps deferred. ADHD toggle on step 2.

**F4 Live Workout:** Micro-reward animation every minute in Zone 3-5. Task chunking overlay ("Block 2 of 6"). Giant time blindness timer. Streak multiplier badge in corner.

**F5 Summary:** Celebration Overkill plays BEFORE stats (confetti, sound, voice). XP bar prominent.

**F6 Burn Points:** Variable reward drops. Visual streak multiplier (1x->3x). XP layer (1 BP = 10 XP).

**F7 History:** Simplified win/not-yet view. Progress visualisation (garden/pet/city) default.

**F9 Notifications:** "Just 5 Minutes" nudge with one-tap start. Daily quest notification.

**F21 Voice Coach:** HYPE style — energetic, frequent encouragement (every 2-3 min), time blindness callouts. Max queue depth 5. All push-harder cues and motivation connectors enabled.

---

### F111. Instant Micro-Rewards — COMPLETE

**Goal:** Continuous dopamine during workouts.

- Every completed minute in Zone 3-5: confetti burst + point pop-up + sound + haptic double-tap
- Visual style varies slightly each time (novelty)
- Settings: frequency (every min / 2 min / off), sound (on/off), haptic (on/off)

---

### F112. "Just 5 Minutes" Start Mode — COMPLETE

**Goal:** Eliminate task initiation paralysis.

- Notification/widget: "Just 5 minutes. That's it." -> one tap starts
- At 5:00: "Nice! Keep going?" -> "Yes!" / "Done" (saves session, full credit)
- Next check at 10 min, then no more interruptions
- All points count — no penalty for short workouts

---

### F113. Variable Reward Drops — COMPLETE

**Goal:** Variable reinforcement for the ADHD brain.

- ~15% chance per minute of "mystery drop" during workout
- Types: bonus BP (1-3), XP multiplier (2 min), cosmetic unlock, streak freeze
- Glowing orb animation, tap to reveal
- More frequent in longer workouts
- **Disabled in ASD mode** (replaced by F139)

---

### F114. Dopamine Streak Multiplier — COMPLETE

**Goal:** Make streaks feel increasingly rewarding.

| Days | Multiplier | Visual |
|------|-----------|--------|
| 1-2 | 1x | Standard |
| 3-6 | 1.5x | Flame icon |
| 7-13 | 2x | Pulsing counter |
| 14-29 | 2.5x | Golden flame |
| 30+ | 3x | Full fire animation |

Applies to XP display, not actual Burn Points (leaderboard fairness).

---

### F115. Time Blindness Timer — COMPLETE

**Goal:** Make time concrete and visible.

- **Colour-filling circle:** large arc fills clockwise, bands every 5 min
- **Haptic pulses:** every 5 min (configurable: 2/5/10)
- **Voice markers:** voice says "10 minutes in" / "Halfway"
- **ASD variant:** exact numbers ("14 minutes 37 seconds")

---

### F116. Novelty Rotation Engine — COMPLETE

**Goal:** Prevent habituation and app abandonment.

Rotates: UI accent colour (weekly), badge art (monthly), voice phrases (per session), micro-reward visuals, daily quest combos, home screen stat.

**Disabled in ASD mode** (F134 Predictable UI Lock).

---

### F117. Body Double Mode — COMPLETE (Firebase)

**Goal:** "Working alongside someone" presence for task initiation.

- Opt-in match with simultaneous user (Firebase `activeSessions` collection)
- Minimal display: avatar, zone colour, "still going" indicator
- No chat, no comparison, anonymous by default
- Optional mutual haptic nudge

---

### F118. Hyperfocus Capture Badge — COMPLETE

**Goal:** Reward the ADHD superpower.

- Trigger: 15+ unbroken minutes in Zone 3-5
- Reward: special badge, 50 XP, tracked as personal best

---

### F119. Zero-Friction Quick Start — COMPLETE

**Goal:** Minimum taps from intention to workout.

Entry points: home widget "GO", notification action, lock screen shortcut, Wear OS complication, app-launch prompt. Auto-connects last device; starts in 5 sec if none found.

---

### F120. XP & Leveling System — COMPLETE

**Goal:** Long-term progression for ADHD brains.

- Level 1-50, logarithmic XP scaling
- Sources: BP (1:10 XP), streaks, badges, quests, drops
- Each level unlocks cosmetics

| Level | XP | Unlock |
|-------|-----|--------|
| 5 | 500 | "Ember" theme |
| 10 | 1,500 | Custom zone animation |
| 15 | 3,500 | Premium voice pack |
| 20 | 6,500 | "Neon" theme |
| 25 | 10,000 | Animated profile badge |
| 30 | 15,000 | Custom celebration |
| 40 | 25,000 | "Galaxy" theme |
| 50 | 40,000 | "Legendary" frame + all unlocked |

---

### F121. Daily Quests Board — COMPLETE

**Goal:** Fresh micro-challenges answering "what should I do?"

- 3 quests from 50+ types, generated at midnight
- Examples: "5 min in Peak", "15 BP", "Start before 9 AM"
- 25-50 XP per quest; all 3 = 100 XP bonus
- Expire at midnight — fresh tomorrow
- Stored in `DailyQuestEntity` (Room)

---

### F122. Progress Visualisation — COMPLETE

**Goal:** Turn abstract points into something alive.

Options: **Garden** (flowers bloom), **Virtual Pet** (evolves at milestones), **City Builder** (buildings grow). Visible on home screen. Withers/dims (not dies) after 3+ inactive days.

---

### F123. Fidget Haptics — COMPLETE

**Goal:** Keep engagement during boring rest phases.

- Active in Zone 1-2
- Patterns: steady pulse, heartbeat sync, wave, random tap
- Faster/more stimulating than ASD calming haptics

---

### F124. Accountability Alarm — COMPLETE

**Goal:** Opt-in escalating start pressure.

User sets "latest start time". Escalation: T-60 gentle -> T-30 persistent + Start Now -> T-0 alarm -> T+15 "Just 5 min?" -> T+30 final then silence. Always snoozable, never punitive.

---

### F125. Task Chunking Display — COMPLETE

**Goal:** Small completable pieces instead of overwhelming duration.

- Templates: "Block 2 of 6 — Push Zone" + progress bar + celebration per block
- Free workouts: auto-chunk into 5-min blocks

---

### F126. Reward Shop — COMPLETE

**Goal:** Give Burn Points a spending purpose.

Separate "spendable" balance (not deducted from stats). Items: themes, animations, celebrations, voice packs, frames, viz skins. 50-500 BP; new items monthly. Navigation route: reward_shop.

---

### F127. Anti-Burnout Detection — COMPLETE

**Goal:** Catch overtraining before crash.

Triggers: 7+ days at 150%+ target, rising HR at same effort, declining BP/min. Gentle suggestion + free streak freeze.

---

### F128. Social Accountability Contracts — COMPLETE (Firebase)

**Goal:** Structured external accountability.

Pair with friend, set weekly commitment, see completion (not details). Optional consequence. Weekly check-in. Firebase `accountabilityContracts` collection with `weekly` subcollection.

---

### F129. Parallel Stimulation Mode — COMPLETE

**Goal:** Combine workout with other stimulation.

PiP for video, podcast title on screen, split-screen layout, music BPM sync.

---

### F130. Celebration Overkill Mode — COMPLETE

**Goal:** Maximum dopamine on target hit.

Full-screen confetti (3 sec), vibration, fanfare, screen pulse, voice, XP animation. Default ON in ADHD mode. Replaced by F138 in ASD mode.

---

## Part 2: ASD Comfort Mode — COMPLETE

### Design Principles

| Principle | In PulseFit |
|-----------|------------|
| **Sensory control** | User controls every animation, sound, haptic, colour independently |
| **Predictability** | UI never changes unexpectedly; same layout every time |
| **Routine structure** | Fixed weekly schedule; same workout structure repeating |
| **Literal communication** | Clear, direct, factual; no slang, metaphors, exclamations |
| **Social safety** | No unsolicited comparisons or social pressure |
| **Deep data** | Detailed stats, raw numbers, exportable data |
| **Transition preparation** | Advance warning before any change |
| **Safe exit** | End anything anytime with zero guilt |

### Modifications to Existing Features

**F1 Onboarding:** "Step 2 of 6" with full map visible. Plain language for every request. Review all before confirm.

**F4 Live Workout:** Fixed layout (never moves). Exact numbers ("14:37"). Zone = text + colour + pattern. Transition warnings.

**F5 Summary:** Calm celebration (checkmark + soft tone). Data-first. Same format every time.

**F6 Burn Points:** Predictable schedule. No drops/surprises. Formula shown: "12 min x 2 pt/min = 24 BP".

**F7 History:** Deep data default. Consistent charts. Stats: mean, median, std dev.

**F9 Notifications:** Same time daily. Explicit opt-in. Literal: "You have not recorded a workout today."

**F10 Settings:** Sensory Control at top. Plain explanations. Change previews.

**F20 Accessibility:** Texture zones default. High contrast. Reduced motion.

**F21 Voice Coach:** LITERAL style — calm, factual. "Heart rate: 162. Zone: Push. Burn Points: 8 of 12." No exclamations. Consistent cadence. Max queue depth 2. No mid-exercise interruptions. No motivation connectors.

---

### F131. Sensory Control Panel — COMPLETE

**Goal:** Granular control over every sensory output.

| Channel | Options | ASD Default | Standard Default | ADHD Default | AuDHD Default |
|---------|---------|-------------|-----------------|-------------|--------------|
| Animations | Off / Reduced / Full | Reduced | Full | Full | Reduced |
| Screen transitions | Instant / Fade / Slide | Instant | Fade | Slide | Instant |
| Sounds | Off / Quiet / Normal | Quiet | Normal | Normal | Quiet |
| Haptics | Off / Gentle / Strong | Gentle | Strong | Strong | Gentle |
| Colour intensity | Muted / Standard / Vivid | Muted | Standard | Vivid | Muted |
| Background zone colour | Off / Subtle / Full | Subtle | Full | Full | Subtle |
| Confetti/particles | Off / Reduced / Full | Off | Reduced | Full | Off |
| Screen shake | Off / On | Off | On | On | Off |
| Contrast | Standard / High / Maximum | High | Standard | Standard | High |

Stored in `SensoryPreferencesEntity` (Room). Accessible from Settings top (sensory_settings route) and quick-settings during workout.

---

### F132. Routine Builder & Scheduler — COMPLETE

**Goal:** Fixed, repeating weekly schedule.

- Assign templates to days with exact times
- "Today's workout: [name] at [time]" — one tap starts
- No judgement on deviation; repeats weekly
- Stored in `WeeklyRoutineEntity` (Room)
- Navigation route: routine_builder

---

### F133. Literal Voice Coach Mode — COMPLETE

**Goal:** Factual, calm, unambiguous feedback.

Standard: "You're crushing it!" -> Literal: "Heart rate: 148. Active zone. Push zone requires above 160."

No exclamation marks, metaphors, slang, assumed emotions. Implemented as LITERAL style in `VoiceCoachEngine`.

---

### F134. Predictable UI Lock — COMPLETE

**Goal:** UI never changes unexpectedly.

Locks: nav position, layouts, colours, fonts, buttons, card order. No A/B testing, no surprise popups. App updates preview UI changes.

---

### F135. Social Pressure Shield — COMPLETE

**Goal:** Remove all social comparison.

One toggle hides: leaderboards, activity feeds, comparative stats, sharing prompts, group invitations, challenge notifications. Only personal data remains.

---

### F136. Deep Data Dashboard — COMPLETE

**Goal:** Detailed analytics for data-focused users.

Per-second HR data, zone time to the second, mean/median/std dev, session comparison, CSV/JSON export, custom date filtering, correlation analysis.

Navigation route: deep_data

---

### F137. Transition Warnings — COMPLETE

**Goal:** Advance notice before any change.

- Segments: 30s -> 10s -> 3-2-1
- Workout end: 2 min -> 1 min -> complete
- Screen changes: text before transition
- Each channel (visual/audio/haptic) independently toggleable

---

### F138. Calm Celebration Mode — COMPLETE

**Goal:** Acknowledge achievement without sensory overload.

On target hit: subtle checkmark + soft chime + "Daily target reached." No confetti, shake, fanfare. **Replaces F130 in ASD mode.**

---

### F139. Predictable Reward Schedule — COMPLETE

**Goal:** Eliminate randomness.

Fixed visible formula. No variable drops. Transparent XP. Fixed quest rewards. Fixed streak bonus. **Replaces F113 in ASD mode.**

---

### F140. Safe Exit Protocol — COMPLETE

**Goal:** Zero-guilt workout exit.

One tap, no confirmation, "Workout saved. Well done." No streak warnings, no guilt. Silent streak freeze. No confirmation dialogs anywhere in the app (ASD-friendly design decision).

---

### F141. Texture & Pattern Zones — COMPLETE

**Goal:** Zone identification beyond colour.

| Zone | Colour | Pattern |
|------|--------|---------|
| 1 Rest | Grey | Horizontal lines |
| 2 Warm-Up | Blue | Dots |
| 3 Active | Green | Diagonal stripes |
| 4 Push | Orange | Crosshatch |
| 5 Peak | Red | Solid fill |

---

### F142. Minimal Mode — COMPLETE

**Goal:** Essential-only workout UI.

Shows only: HR number, zone name, points/target, time. No bars, icons, animations, background changes.

---

### F143. Pre-Workout Visual Schedule — COMPLETE

**Goal:** Complete structure shown before starting.

Template workouts: numbered segments with name, duration, target zone, intensity bar. Free: "Free workout — no set structure. End anytime."

Navigation route: pre_workout_schedule/{workoutId}

---

### F144. Consistent Audio Palette — COMPLETE

**Goal:** Learnable, predictable audio cues.

| Event | Sound |
|-------|-------|
| Zone up | Rising two-tone chime |
| Zone down | Falling two-tone chime |
| Point earned | Soft click |
| Target reached | Warm bell |
| Segment change | Double tap |
| Workout start | Three ascending notes |
| Workout end | Three descending notes |

Never randomised. Novelty engine never touches audio in ASD mode.

---

### F145. Shutdown Routine — COMPLETE

**Goal:** Predictable post-workout wind-down.

Same sequence every time: 1) Stats screen -> 2) Breathing exercise (optional) -> 3) Stretching prompt (optional) -> 4) "Workout saved" -> 5) Home. Each step skippable, always offered.

Navigation route: shutdown_routine/{workoutId}

---

## Part 3: AuDHD Combined Mode — COMPLETE

For users with both ADHD and ASD traits (30-80% overlap):

| Setting | AuDHD Default | Reasoning |
|---------|--------------|-----------|
| Micro-rewards | ON, reduced animation | Dopamine (ADHD) without overload (ASD) |
| Variable drops | OFF | Unpredictability more distressing than rewarding |
| Novelty rotation | OFF for UI, ON for quests | Stable layout + fresh challenges |
| Voice coach | Literal + frequent | Factual (ASD) + regular engagement (ADHD) |
| Celebrations | 1-sec confetti | Brief reward that ends quickly |
| Sensory | Per-channel control | User decides per channel |
| Task chunking | ON | Benefits both |
| XP/Leveling | ON | Structured progression satisfies both |
| Social | Body double only | 1:1 presence without pressure |
| Time timer | ON, exact numbers | Concrete (ADHD) + precise (ASD) |
| Routine builder | ON | Structure reduces decision fatigue |
| Safe exit | ON | Both benefit |
| Transitions | ON | Preparation + time awareness |

**AuDHD conflict resolution principle:** Default to ASD comfort settings, surface ADHD features as toggleable options. This reflects the clinical reality that sensory overwhelm (ASD) is typically more distressing than missing dopamine hits (ADHD), so safety takes precedence.

Every setting individually toggleable.

---

## Part 4: Feature Interaction Matrix

### Conflict Resolution

When ADHD and ASD features conflict, PulseFit resolves them according to these rules:

| ADHD Feature | ASD Feature | Conflict | Resolution |
|-------------|-------------|----------|------------|
| F113 Variable Drops | F139 Predictable Rewards | Randomness vs predictability | ASD wins: drops disabled, predictable formula shown |
| F116 Novelty Rotation | F134 Predictable UI Lock | Change vs stability | ASD wins for UI; ADHD wins for quest content |
| F130 Celebration Overkill | F138 Calm Celebration | Sensory intensity | ASD wins: calm celebration replaces overkill |
| F111 Micro-Rewards | F131 Sensory Control | Reward frequency vs overload | Both active: rewards fire but animation/sound respect sensory settings |
| F123 Fidget Haptics | F131 Sensory Haptics | Stimulating vs calming | User selects per preference; defaults to calming (ASD) |
| F114 Streak Multiplier | F139 Predictable Rewards | Visual excitement vs consistency | Both: multiplier shown but with consistent visual treatment |

### Complementary Features (Always Compatible)

| Feature A | Feature B | Why they work together |
|-----------|-----------|----------------------|
| F112 Just 5 Minutes | F140 Safe Exit | Low-commitment start + zero-guilt exit = zero pressure |
| F125 Task Chunking | F137 Transition Warnings | Chunks provide structure + warnings prepare for change |
| F115 Time Blindness Timer | F142 Minimal Mode | Timer replaces clock + minimal mode removes clutter |
| F120 XP/Leveling | F139 Predictable Rewards | Structured progression with transparent formula |
| F117 Body Double | F135 Social Shield | Presence without comparison or pressure |
| F132 Routine Builder | F143 Pre-Workout Schedule | Weekly structure + session preview = full predictability |

---

## Part 5: Composable Voice Coaching System — COMPLETE

### Architecture

The voice coaching system uses a **composable clip queue** architecture. Instead of pre-recording full sentences, atomic audio segments are assembled in real time:

```
[exercise_name] + [target] + [duration] + [motivation_connector]
"Push Pace" + "7 miles per hour" + "for 2 minutes" + "lets go"
```

**Implementation:** `VoiceCoachEngine` manages a `ConcurrentLinkedQueue` of clip segments. Clips play sequentially with TTS fallback for missing audio files. `@Volatile` thread safety on engine state.

### Clip Categories

| Category | Examples | Count |
|----------|---------|-------|
| Exercise | "Base Pace", "Power Row", "Squats" | 20 clips |
| Target (speed) | "5 mph", "6 mph"... "9 mph" | ~10 clips |
| Target (incline) | "4 percent", "6 percent"... "12 percent" | ~9 clips |
| Target (watts) | "100 watts", "150 watts", "200 watts" | ~5 clips |
| Duration | "20 seconds", "30 seconds"... "15 minutes" | 10 clips |
| Motivation | "lets go", "you got this", "strong finish" | 3 clips |
| Form cue | "chest up", "drive heels", "full range", "control tempo", "squeeze top", "breathe" | 6 clips |
| Push harder | "add 1 mph", "raise incline 2", "add 20 watts" | ~6 clips |
| Station change | "tread", "row", "floor" | 3 clips |
| Zone change | "rest", "warm up", "active", "push", "peak" | 5 clips |
| Time interval | "5 minutes", "10 minutes"... "60 minutes" | ~12 clips |
| Milestone | "start first", "complete target", "complete PB", streak milestones | ~14 clips |

### ND Profile Verbosity Control

| Profile | Max Queue Depth | Mid-Exercise Interrupts | Motivation Connectors | Push Harder Cues |
|---------|-----------------|------------------------|----------------------|-----------------|
| **ASD** | 2 | No | No | No |
| **Standard** | 4 | Yes | Yes | All |
| **ADHD** | 5 | Yes | Yes | All |
| **AuDHD** | 3 | No | No | All-out only |

**What "max queue depth" means:** ASD mode caps at 2 clip segments per announcement (e.g., "Push Pace. 2 minutes.") while ADHD mode allows 5 (e.g., "Push Pace. 7 miles per hour. For 2 minutes. Chest up. Lets go!").

### Coaching Target Registry

`CoachingTargetRegistry` provides OTF-accurate targets per exercise and tread mode:

| Exercise | Runner Target | Power Walker Target |
|----------|--------------|-------------------|
| Base Pace | 5-6 mph | 3.5-4 mph, 4-6% incline |
| Push Pace | 7-8 mph | 3.5-4 mph, 8-10% incline |
| All-Out Sprint | 9+ mph | 4+ mph, 12-15% incline |
| Steady Row | 150-180 watts | Same |
| Power Row | 200+ watts | Same |

### Voice Coach Styles

| Style | Voice Character | Stability | Enthusiasm |
|-------|----------------|-----------|------------|
| LITERAL | Direct, factual, no hype | 0.8 | 0.2 |
| STANDARD | Encouraging, balanced | 0.6 | 0.4 |
| HYPE | High-energy, motivational | 0.5 | 0.7 |

---

## Part 6: Data Model Additions — COMPLETE

### Updated UserProfile

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
    val ndProfile: NdProfile,          // STANDARD, ADHD, ASD, AUDHD
    val adhdFocusMode: Boolean,
    val asdComfortMode: Boolean,
    val xpLevel: Int,
    val totalXp: Long,
    val sensoryLevel: SensoryLevel,    // MUTED, STANDARD, VIVID
    val treadMode: String?,            // "Runner" or "PowerWalker"
    val equipmentProfileJson: String?  // JSON EquipmentProfile
)

enum class NdProfile { STANDARD, ADHD, ASD, AUDHD }
enum class SensoryLevel { MUTED, STANDARD, VIVID }
```

### Room Entities (DB v10)

8 entities: `UserProfileEntity`, `WorkoutEntity`, `HeartRateReadingEntity`, `SensoryPreferencesEntity`, `WeeklyRoutineEntity`, `DailyQuestEntity`, `AchievementEntity`, `NotificationPreferencesEntity`

Migration strategy: `fallbackToDestructiveMigration(dropAllTables = true)`

---

## Part 7: Accessibility Integration

### How TalkBack Works with ND Features

PulseFit's accessibility system integrates with ND features at every level:

| ND Feature | TalkBack Behaviour |
|------------|-------------------|
| Sensory Control Panel | Each slider has semantic description: "Animations: Reduced. Double tap to change." |
| Zone display (colour + pattern) | Content description: "Zone 4, Push, Orange, Crosshatch pattern" |
| Micro-rewards | Announced via live region: "Bonus! 2 extra Burn Points" |
| Task chunking | Announced: "Block 2 of 6. Push zone. 4 minutes remaining." |
| Transition warnings | Announced: "Next segment in 30 seconds: Active Recovery" |
| Safe exit | Button labeled: "End workout. Your progress will be saved." |
| Calm celebration | Announced: "Daily target reached." (no fanfare) |
| Deep data | All charts have content descriptions with data summaries |

### Semantic Descriptions

All interactive elements have semantic descriptions. Live regions announce dynamic content changes. Form errors are announced immediately. Animation preferences are respected via the system `prefers-reduced-motion` setting.

### Colour and Contrast

- Zone colours have pattern overlays for colour-blind users (F141)
- High contrast mode available in Sensory Control Panel
- All text meets WCAG AA contrast ratios
- Zone information is always communicated through text label + colour + pattern (triple redundancy)

---

## Summary

PulseFit's neurodivergent design is not an accessibility add-on — it is the core product differentiator. With 35 ND-specific features across 4 profiles, a composable voice coaching system with ND-aware verbosity, and deep integration between ND features and accessibility standards, PulseFit serves the 10-13% of adults with ADHD, ASD, or both who are completely ignored by every other fitness app on the market.
