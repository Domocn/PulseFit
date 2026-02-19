# PulseFit — Feature Specification

## MVP Features (v1.0)

---

### F1. User Onboarding & Profile Setup

**Goal:** Collect the minimum data needed to calculate personalised HR zones and start tracking.

**Flow:**
1. **Welcome screen** — app overview, value proposition, "Get Started" CTA
2. **Basic info** — age, weight, height, biological sex (optional, improves calorie estimate)
3. **Resting heart rate** — manual entry or auto-detect via connected device
4. **Max heart rate** — auto-calculated (`220 - age`) with option to override manually (lab test / field test result)
5. **Daily Burn Point target** — slider (range 8–30, default 12) with plain-language guidance ("12 = moderate 30-min session")
6. **Device pairing** — scan for Bluetooth LE HR monitors; skip option if using phone/watch sensor
7. **Health Connect permissions** — explain what data is read/written; request scopes
8. **Confirmation** — summary card of profile + zones; "Start First Workout" button

**Data stored:** `UserProfile` entity in Room (age, weight, height, restingHr, maxHr, dailyBurnTarget, unitSystem)

**Neurodivergent personalisation (optional step between steps 5 and 6):**
- "Personalise your experience" screen offering profiles:
  - **Standard** — balanced gamification (default)
  - **ADHD Focus Mode** — dopamine-first, micro-rewards, novelty rotation, zero-friction starts
  - **ASD Comfort Mode** — predictable UI, sensory controls, literal communication, routine structure
  - **Both (AuDHD)** — combined defaults, fully customisable
- Profile changeable anytime in Settings
- ADHD mode reduces remaining onboarding to 1 tap ("set up later" for optional steps)
- ASD mode shows full step map: "Step 3 of 7" with all steps listed

---

### F2. Heart Rate Zone Engine

**Goal:** Classify any heart rate reading into one of five zones in real time.

| Zone | Name     | % of Max HR | Colour | Burn Points/min |
|------|----------|-------------|--------|-----------------|
| 1    | Rest     | 50–60%      | Grey   | 0               |
| 2    | Warm-Up  | 61–70%      | Blue   | 0               |
| 3    | Active   | 71–83%      | Green  | 1               |
| 4    | Push     | 84–91%      | Orange | 2               |
| 5    | Peak     | 92–100%     | Red    | 3               |

**Customisation:**
- Users can adjust zone boundaries in Settings (e.g., move the Active/Push threshold from 84% to 82%)
- Users can override max HR at any time
- Resting HR can be used for a Karvonen-based formula (optional advanced setting)

---

### F3. Bluetooth LE Heart Rate Pairing

**Goal:** Connect to any standard Bluetooth LE heart rate monitor for real-time HR data.

**Supported devices (standard HR profile `0x180D`):**
- Chest straps — Polar H10/H9, Wahoo TICKR, Garmin HRM-Pro/Dual, Coospo, etc.
- Arm bands — Polar Verity Sense, Wahoo TICKR Fit, Scosche Rhythm+
- Most BLE-enabled fitness watches broadcasting HR

**Behaviour:**
- Scan & pair screen with device name + signal strength
- Auto-reconnect to last paired device on workout start
- Connection status indicator (connected / searching / lost)
- If signal lost mid-workout, show banner alert and buffer gap; resume when reconnected
- Support multiple saved devices (user picks active device before each workout)

**Fallback:** If no external device, the app reads HR from Health Connect (wrist-based watch sensor).

---

### F4. Live Workout Screen

**Goal:** The core experience — a real-time dashboard showing HR, zone, and Burn Points as the user exercises.

**Layout:**

```
┌──────────────────────────────────┐
│  [Zone colour fills background]  │
│                                  │
│         ♥ 156 BPM               │
│        Zone: PUSH               │
│                                  │
│  ┌────┬────┬────┬────┬────┐     │
│  │ Z1 │ Z2 │ Z3 │ Z4 │ Z5 │     │  ← zone time bar (fills live)
│  └────┴────┴────┴────┴────┘     │
│                                  │
│     🔥 8 Burn Points            │
│     Target: 12                   │
│     ██████████░░░░  67%          │  ← progress bar to daily target
│                                  │
│     ⏱ 22:45 elapsed             │
│     🔥 ~187 kcal                │
│                                  │
│  [ PAUSE ]         [ END ]      │
└──────────────────────────────────┘
```

**Behaviour:**
- Background colour smoothly transitions to match current zone colour
- HR updates every 1 second (or as fast as the BLE device sends)
- Burn Points increment in real time (awarded per completed minute in zone)
- Calorie counter updates continuously (HR-based formula)
- Pause freezes timer and point accumulation; resume continues
- End triggers the summary screen
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

### F5. Workout Summary

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

**ADHD mode:** Celebration Overkill (F130) plays before stats — confetti, sound, ElevenLabs voice, XP animation. Stats shown after 3-second celebration.

**ASD mode:** Calm Celebration (F138) — subtle checkmark + soft tone. Data-first layout with stats table immediately. Same format every time.

---

### F6. Burn Points System & Streaks

**Goal:** Gamify consistency with a simple, motivating points and streak system.

**Mechanics:**
- Points accumulate per completed minute in Zones 3–5 (see zone table above)
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
- Dopamine Streak Multiplier (F114): visual multiplier 1x → 1.5x → 2x → 2.5x → 3x (applies to XP, not actual BP)
- XP & Leveling System (F120): every Burn Point grants 10 XP; Level 1-50 with cosmetic unlocks
- Daily Quests (F121): 3 fresh micro-challenges each morning for bonus XP

**ASD mode additions:**
- Predictable Reward Schedule (F139): fixed visible formula, no randomness
- Clear formula display: "12 min x Zone 4 x 2 pts/min = 24 Burn Points"

---

### F7. Workout History & Trends

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

### F8. Health Connect Integration

**Goal:** Bi-directional sync with Android's Health Connect platform so data flows between PulseFit and other health/fitness apps.

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

### F9. Notifications & Reminders

**Goal:** Keep users engaged without being annoying.

| Notification          | Trigger                                    | Default  |
|-----------------------|--------------------------------------------|----------|
| Workout reminder      | User-set time of day (e.g., 6:00 AM)       | Off      |
| Streak at risk        | 8 PM if no workout logged today + active streak | On   |
| Streak milestone      | Hit 7, 14, 30, 60, 100 day streak          | On       |
| Weekly summary        | Sunday evening — total points, time, comparison to last week | On |
| Personal best         | New record for single-session or weekly points | On    |
| Inactivity nudge      | No workout in 3+ days                       | Off      |

All notifications are individually toggleable in Settings.

**ADHD mode additions:**
- "Just 5 Minutes" nudge: low-commitment prompt with one-tap Quick Start (F119)
- Daily quest notification each morning with 3 micro-challenges
- Accountability Alarm (F124): opt-in escalating alarm at user-set "latest start time"

**ASD mode additions:**
- All notifications sent at exact same time each day (predictable)
- Literal language: "You have not recorded a workout today" (not "Don't break your streak!")
- Every notification type requires explicit opt-in

---

### F10. Profile & Settings

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

## Post-MVP Features (v1.x – v2.0)

---

### F11. Group Workout Mode

**Goal:** Replicate the energy of group fitness classes — an instructor runs a session and all participants see a shared live display.

**How it works:**
1. Instructor creates a **Session** in the app → gets a 6-digit join code
2. Participants enter the code (or scan QR) to join
3. During workout, a **shared display** (TV/tablet via Chromecast or companion web view) shows all participants:
   - Name / alias
   - Current zone (colour tile)
   - Burn Points accumulated
   - Percentage of daily target reached
4. Instructor can start/pause/end the session for all participants
5. Post-session: everyone gets their individual summary + group leaderboard

**Tech:** Firebase Realtime Database or Firestore for low-latency sync between devices.

---

### F12. Instructor Dashboard

**Goal:** A tablet/TV-optimised view for gym instructors to monitor the class.

**Features:**
- Grid of participant tiles (name, HR, zone colour, points)
- Sorting options (by zone, by points, alphabetical)
- Class-wide stats: avg zone, total class Burn Points, % of class in Push/Peak
- Timer controls (interval timer with work/rest segments)
- Chromecast / screen mirror support for gym TVs
- Session history for the instructor (attendance, avg performance)

---

### F13. Workout Templates

**Goal:** Pre-built structured workouts that guide users through timed segments.

**Template structure:**
```
Template: "30-Min Interval Burner"
├── Warm-Up    — 5 min  (target: Zone 2)
├── Push Block  — 4 min  (target: Zone 4)
├── Active Recovery — 2 min (target: Zone 3)
├── Push Block  — 4 min  (target: Zone 4)
├── Active Recovery — 2 min (target: Zone 3)
├── Peak Effort — 2 min  (target: Zone 5)
├── Active Recovery — 2 min (target: Zone 3)
├── Push Block  — 4 min  (target: Zone 4)
├── Peak Effort — 1 min  (target: Zone 5)
└── Cool Down   — 4 min  (target: Zone 2)
```

**Behaviour:**
- Audio/haptic cues when segments change
- On-screen target zone indicator ("Get to PUSH!")
- Live feedback on whether you're hitting the target zone
- Built-in library: Interval Burner, Endurance Builder, Peak Chaser, Easy Recovery, HIIT Blast
- Templates filterable by duration (15/30/45/60 min) and difficulty

---

### F14. Custom Workout Builder

**Goal:** Let users design their own segment-based workouts.

**Builder UI:**
- Add segments with: name, duration, target zone
- Drag to reorder segments
- Duplicate / delete segments
- Preview total duration and estimated Burn Points
- Save as personal template (reusable)
- Share template via link or code (post-MVP social feature)

---

### F15. Social Features & Leaderboards

**Goal:** Community motivation through friendly competition.

**Features:**
- **Friends list** — add by username or invite link
- **Weekly leaderboard** — ranked by total Burn Points among friends
- **Challenges** — e.g., "Earn 100 Burn Points this week", "7-day streak challenge"
- **Achievement badges:**
  - First Workout
  - 7-Day Streak / 30-Day Streak / 100-Day Streak
  - 1,000 Lifetime Burn Points / 10,000 / 50,000
  - Peak Performer (20+ min in Zone 5 in one session)
  - Early Bird (workout before 7 AM)
  - Night Owl (workout after 9 PM)
  - Century Club (100 workouts logged)
- **Activity feed** — see friends' workout completions (opt-in)
- **Privacy controls** — choose what's visible (points only, full summary, nothing)

---

### F16. Wear OS Companion App

**Goal:** See live workout data on your wrist without pulling out your phone.

**Watch face during workout:**
- Current HR + zone colour background
- Burn Points counter
- Elapsed time
- Haptic buzz on zone transitions

**Standalone capability:**
- Start/stop workout from watch
- Use watch's built-in HR sensor (no chest strap needed)
- Sync data to phone app when reconnected

---

### F17. Third-Party Fitness Platform Integration

**Goal:** Go beyond Health Connect with direct API integrations for richer data.

| Platform        | Data In                        | Data Out                     |
|----------------|--------------------------------|------------------------------|
| **Fitbit**      | HR, resting HR, sleep, steps  | Workout session              |
| **Garmin Connect** | HR, VO2 max, training load | Workout session              |
| **Samsung Health** | HR, exercise sessions       | Workout session, calories    |
| **Strava**      | GPS routes, exercise sessions  | Workout session + Burn Points in description |
| **Apple Health** | (iOS version) HR, workouts    | Workout session, calories    |

Each integration is opt-in with OAuth-based authorisation flows.

---

### F18. AI Coach Suggestions

**Goal:** Personalised post-workout tips powered by on-device analysis of workout patterns.

**Example suggestions:**
- "You spent 80% of today's session in Zone 3. Try adding 2-minute Zone 5 intervals to boost your Burn Points."
- "Your average HR has dropped 5 BPM over the last month at the same effort — your fitness is improving!"
- "You've done 5 Push-heavy workouts in a row. Consider an Active Recovery session tomorrow."
- "You hit your target in 22 minutes today — try increasing your daily target to 15 for a bigger challenge."

**Implementation:**
- Rule-based engine in v1.x (pattern matching on zone distributions, trends, streaks)
- Optional LLM-powered coaching in v2.0 (on-device or API-based, premium feature)

---

### F19. Data Export & Backup

**Goal:** Users own their data and can take it anywhere.

**Export formats:**
- **CSV** — one row per workout with summary stats; separate file for HR samples
- **JSON** — full structured export of all workouts, zone data, profile
- **GPX** (future, if GPS tracking added) — route + HR data for mapping apps

**Backup:**
- Auto-backup to Google Drive (encrypted, opt-in)
- Manual export to local storage
- Import from backup file to restore on new device

---

### F20. Accessibility & Inclusivity

**Goal:** Ensure PulseFit is usable by everyone.

**Features:**
- Full TalkBack / screen reader support with descriptive labels
- High-contrast mode for outdoor visibility
- Haptic feedback for zone transitions (useful when screen isn't visible)
- Audio zone announcements ("You're now in the Push zone!") — toggleable
- Configurable font sizes
- Colour-blind-friendly zone palette option (patterns + labels supplement colours)
- Support for seated / wheelchair workouts (adjusted calorie formulas)

---

### F21. Real-Time Voice Coach

**Goal:** An in-workout audio coach that gives spoken feedback, encouragement, and cues so users stay motivated without needing to look at the screen.

**Voice Event Types:**

| Category | Example Callouts |
|----------|-----------------|
| **Zone transitions** | "You've entered the Push zone — keep it up!" / "You've dropped to Warm-Up — pick up the pace!" |
| **Target progress** | "6 Burn Points earned — you're halfway to your target." / "One more point to hit your daily goal!" |
| **Target hit** | "You've hit your Burn Point target — amazing work! Keep going for bonus points." |
| **Streak motivation** | "Day 5 of your streak — don't stop now!" |
| **Interval cues** (templates) | "Push block starting in 3… 2… 1… GO!" / "10 seconds left in this interval." / "Recovery — bring your heart rate down." |
| **Milestone alerts** | "That's 20 minutes in the Push zone — a new personal best!" |
| **Pacing guidance** | "You've been in Peak for 3 minutes — consider easing back to Push to sustain your effort." |
| **Warm-up / cool-down** | "Let's start with a 5-minute warm-up — keep it easy in Zone 2." / "Great session. Cool down and bring your heart rate below 120." |
| **Encouragement** | "You're crushing it!" / "Strong effort — stay in this zone." / "Almost there, finish strong!" |

**How it works:**

1. **Hybrid voice engine** — three-tier system: (a) pre-generated ElevenLabs audio clips bundled with app for fixed phrases (~90% of usage, zero latency, works offline), (b) ElevenLabs runtime API for dynamic/personalised text (Premium), (c) Android TTS as offline fallback
2. **Event-driven architecture** — a `VoiceCoachService` listens to the live workout state (zone changes, points earned, timer events) and triggers speech from a phrase library
3. **Phrase library** — each event type has multiple phrase variants to avoid repetition; phrases are randomly rotated
4. **Cooldown timer** — minimum 15-second gap between callouts to avoid overwhelming the user (configurable: 10s / 15s / 30s / 60s)
5. **Priority queue** — if multiple events fire simultaneously, highest priority speaks first (target hit > zone change > encouragement)

**Audio behaviour:**

- **Audio ducking** — when the voice coach speaks, any playing music (Spotify, YouTube Music, etc.) is temporarily lowered via `AudioFocus` with `AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK`
- Music volume ducks ~70% during speech, then restores
- Works with Bluetooth headphones, phone speaker, or watch speaker
- Voice plays over any media — no need to pause music manually

**Voice settings (in Settings > Voice Coach):**

| Setting | Options | Default |
|---------|---------|---------|
| **Voice Coach toggle** | On / Off | On |
| **Voice profile** | Standard / ADHD Energetic / ASD Calm | Standard |
| **Voice selection** | ElevenLabs voices (Premium) or system TTS | ElevenLabs default |
| **Speech rate** | Slow / Normal / Fast | Normal |
| **Volume** | Independent slider (0–100%) | 80% |
| **Minimum gap between callouts** | 10s / 15s / 30s / 60s | 15s |
| **Zone transition callouts** | On / Off | On |
| **Burn Point updates** | Every point / Every 3 points / Target only / Off | Every 3 points |
| **Interval countdown** | On / Off (only relevant with templates) | On |
| **Encouragement** | Frequent / Occasional / Off | Occasional |
| **Pacing warnings** | On / Off | On |

**Template integration:**
- When running a Workout Template (F13), the voice coach reads out each segment transition: "Next up: 4-minute Push block. Get ready!"
- Countdown at end of each segment: "3… 2… 1… switch!"
- Target zone reminders mid-segment: "You should be in the Push zone right now — you're in Active, push a bit harder."

**Custom phrases (Premium):**
- Users can replace default phrases with custom text per event type
- e.g., change "You've entered the Push zone" to "Time to send it!"
- Useful for personal motivation or language preferences

**Group workout interaction (F11):**
- In group mode, the instructor can trigger voice announcements to all participants: "Let's go, class! Push zone NOW!"
- Individual voice coach still runs locally for personal stats

**ADHD mode voice behaviour:**
- More frequent callouts (every 2-3 minutes)
- Energetic ElevenLabs voice profile (Stability: 0.5, Style: 0.7)
- Time blindness callouts: "You've been going 10 minutes — nice!"
- Extra encouragement variety (novelty rotation applies to phrase selection)

**ASD mode voice behaviour (Literal Voice Coach — F133):**
- Calm ElevenLabs voice profile (Stability: 0.8, Style: 0.2)
- Factual, measured delivery: "Heart rate: 162. Zone: Push. Burn Points: 8 of 12."
- No exclamation marks, metaphors, slang, or assumed emotions
- Predictable cadence: same information at same intervals every workout
- Consistent Audio Palette (F144): same sounds always mean the same thing

**Technical implementation:**

```kotlin
class HybridVoiceCoach(
    private val assetPlayer: AssetAudioPlayer,
    private val elevenLabsApi: ElevenLabsApi,
    private val androidTts: TextToSpeech,
    private val cache: VoiceCache,
    private val settings: VoiceCoachSettings
) {
    private var lastCalloutTime: Instant = Instant.MIN

    suspend fun speak(phraseKey: String?, dynamicText: String?, priority: Priority) {
        if (!canSpeak()) return

        // Tier 1: Pre-generated ElevenLabs assets (bundled with APK)
        if (phraseKey != null) {
            val path = "voice/${settings.voiceProfile}/${phraseKey}.ogg"
            if (assetPlayer.hasAsset(path)) {
                requestAudioFocus()
                assetPlayer.play(path, priority)
                lastCalloutTime = Instant.now()
                return
            }
        }

        val text = dynamicText ?: return

        // Tier 2: ElevenLabs runtime API (Premium, dynamic text)
        if (settings.isPremium) {
            val cached = cache.get(text, settings.voiceId)
            if (cached != null) {
                requestAudioFocus()
                audioPlayer.play(cached, priority)
                lastCalloutTime = Instant.now()
                return
            }
            try {
                val audio = elevenLabsApi.textToSpeech(
                    text = text,
                    voiceId = settings.voiceId,
                    modelId = "eleven_turbo_v2_5"
                )
                cache.store(text, settings.voiceId, audio)
                requestAudioFocus()
                audioPlayer.play(audio, priority)
                lastCalloutTime = Instant.now()
                return
            } catch (_: Exception) { /* fall through */ }
        }

        // Tier 3: Android TTS fallback
        requestAudioFocus()
        androidTts.speak(text, TextToSpeech.QUEUE_ADD, null, UUID.randomUUID().toString())
        lastCalloutTime = Instant.now()
    }

    private fun canSpeak(): Boolean =
        Duration.between(lastCalloutTime, Instant.now()) >= settings.minGap
}
```

---

## Neurodivergent Features — ADHD Focus Mode (F111-F130)

> These features activate when ADHD Focus Mode is enabled, or can be toggled individually. See `NEURODIVERGENT_DESIGN.md` for full specs.

---

### F111. Instant Micro-Rewards
Continuous dopamine: confetti burst + point pop + sound + haptic every completed minute in Zone 3-5. Visual style varies (novelty). Configurable frequency.

### F112. "Just 5 Minutes" Start Mode
Eliminate task initiation paralysis. One-tap 5-min workout; at 5:00 asks "Keep going?" All points count fully.

### F113. Variable Reward Drops
~15% chance per minute of mystery bonus (extra BP, XP multiplier, cosmetic, streak freeze). Disabled in ASD mode (F139).

### F114. Dopamine Streak Multiplier
Visual multiplier grows with streak: 1x → 1.5x → 2x → 2.5x → 3x. Applies to XP display, not actual BP.

### F115. Time Blindness Timer
Colour-filling circle, haptic pulses every 5 min, ElevenLabs voice time markers. ASD variant uses exact numbers.

### F116. Novelty Rotation Engine
Rotates: UI colour (weekly), badge art (monthly), voice phrases (per session), micro-reward visuals. Disabled in ASD mode (F134).

### F117. Body Double Mode
Opt-in match with simultaneous user. Minimal display (avatar, zone, "still going"). No chat/comparison.

### F118. Hyperfocus Capture Badge
15+ unbroken minutes in Zone 3-5 earns special badge + 50 XP.

### F119. Zero-Friction Quick Start
One-tap from: widget, notification, lock screen, Wear OS, app launch. Auto-connects last device.

### F120. XP & Leveling System
Level 1-50, logarithmic XP. Sources: BP (1:10), streaks, badges, quests, drops. Cosmetic unlocks per level.

### F121. Daily Quests Board
3 random micro-challenges daily from 50+ types. 25-50 XP each; all 3 = 100 XP bonus. Expire at midnight.

### F122. Progress Visualisation
Garden/Pet/City grows with Burn Points. Visible on home screen. Withers (not dies) after 3+ inactive days.

### F123. Fidget Haptics
Rhythmic vibration during Zone 1-2 rest phases. Faster/stimulating patterns (vs ASD calming haptics).

### F124. Accountability Alarm
Escalating reminders at user-set time: gentle → persistent → alarm → "Just 5 min?" → silence. Never punitive.

### F125. Task Chunking Display
"Block 2 of 6 — Push Zone" + progress bar + celebration per block. Free workouts auto-chunk in 5-min blocks.

### F126. Reward Shop
Spend BP on cosmetics (separate balance). Themes, animations, voice packs, frames. New items monthly.

### F127. Anti-Burnout Detection
Detect overtraining (7+ days at 150%+, rising HR, declining BP/min). Gentle suggestion + free streak freeze.

### F128. Social Accountability Contracts
Pair with friend, set weekly goal, see completion. Optional consequence. Weekly check-in.

### F129. Parallel Stimulation Mode
PiP for video, podcast title on screen, split layout, music BPM sync.

### F130. Celebration Overkill Mode
Max celebration on target: confetti, vibration, fanfare, ElevenLabs voice, XP animation. Replaced by F138 in ASD mode.

---

## Neurodivergent Features — ASD Comfort Mode (F131-F145)

> These features activate when ASD Comfort Mode is enabled, or can be toggled individually. See `NEURODIVERGENT_DESIGN.md` for full specs.

---

### F131. Sensory Control Panel
Granular control: animations, transitions, sounds, haptics, colour intensity, confetti, screen shake, contrast. Each Off/Reduced/Full.

### F132. Routine Builder & Scheduler
Fixed weekly schedule: assign templates to days with exact times. One-tap start. No judgement on deviation.

### F133. Literal Voice Coach Mode
Factual, calm ElevenLabs voice. "Heart rate: 148. Active zone." No exclamations, metaphors, or slang.

### F134. Predictable UI Lock
Locks nav, layouts, colours, fonts, buttons. No A/B testing, no surprise popups, update previews.

### F135. Social Pressure Shield
One toggle hides: leaderboards, feeds, comparative stats, sharing prompts, group invitations.

### F136. Deep Data Dashboard
Per-second HR data, zone time to the second, mean/median/std dev, session comparison, CSV/JSON export.

### F137. Transition Warnings
Advance notice: 30s → 10s → 3-2-1 for segments. 2 min → 1 min for workout end. Each channel toggleable.

### F138. Calm Celebration Mode
Subtle checkmark + soft chime + "Daily target reached." No confetti/shake/fanfare. Replaces F130 in ASD mode.

### F139. Predictable Reward Schedule
Fixed formula, no randomness, transparent XP. Replaces F113 in ASD mode.

### F140. Safe Exit Protocol
One tap, no confirmation. "Workout saved. Well done." No streak warnings, no guilt. Silent streak freeze.

### F141. Texture & Pattern Zones
Zone patterns beyond colour: horizontal lines, dots, diagonal stripes, crosshatch, solid fill.

### F142. Minimal Mode
Essential-only UI: HR, zone name, points/target, time. Nothing else on screen.

### F143. Pre-Workout Visual Schedule
Full workout structure shown before starting. Segments with name, duration, target zone.

### F144. Consistent Audio Palette
Fixed sounds per event (zone up/down, point, target, start, end). Never randomised.

### F145. Shutdown Routine
Same post-workout sequence every time: stats → breathing → stretching → saved → home. Each skippable.

---

## Feature Priority Matrix

| Priority | Feature | Effort | Impact |
|----------|---------|--------|--------|
| P0 (MVP) | F1 Onboarding | Medium | High |
| P0 (MVP) | F2 Zone Engine | Low | High |
| P0 (MVP) | F3 BLE Pairing | Medium | High |
| P0 (MVP) | F4 Live Workout | High | High |
| P0 (MVP) | F5 Workout Summary | Medium | High |
| P0 (MVP) | F6 Burn Points & Streaks | Medium | High |
| P0 (MVP) | F7 History & Trends | Medium | Medium |
| P0 (MVP) | F8 Health Connect | Medium | High |
| P0 (MVP) | F9 Notifications | Low | Medium |
| P0 (MVP) | F10 Profile & Settings | Low | Medium |
| P0 (MVP) | F111 Micro-Rewards | Low | High |
| P0 (MVP) | F112 "Just 5 Minutes" | Low | High |
| P0 (MVP) | F119 Quick Start | Low | High |
| P0 (MVP) | F131 Sensory Control Panel | Medium | High |
| P0 (MVP) | F134 Predictable UI Lock | Low | High |
| P0 (MVP) | F140 Safe Exit Protocol | Low | High |
| P1 | F11 Group Workout | High | High |
| P1 | F12 Instructor Dashboard | High | Medium |
| P1 | F13 Workout Templates | Medium | High |
| P1 | F14 Custom Builder | Medium | Medium |
| P1 | F21 Voice Coach | Medium | High |
| P1 | F113 Variable Reward Drops | Low | Medium |
| P1 | F114 Streak Multiplier | Low | Medium |
| P1 | F115 Time Blindness Timer | Low | High |
| P1 | F120 XP & Leveling | Medium | High |
| P1 | F121 Daily Quests | Medium | Medium |
| P1 | F125 Task Chunking | Low | Medium |
| P1 | F130 Celebration Overkill | Low | Medium |
| P1 | F132 Routine Builder | Medium | High |
| P1 | F133 Literal Voice Coach | Low | High |
| P1 | F135 Social Pressure Shield | Low | High |
| P1 | F137 Transition Warnings | Low | Medium |
| P1 | F138 Calm Celebration | Low | Medium |
| P1 | F139 Predictable Rewards | Low | Medium |
| P1 | F142 Minimal Mode | Low | Medium |
| P2 | F15 Social & Leaderboards | High | High |
| P2 | F16 Wear OS | High | Medium |
| P2 | F17 Third-Party APIs | High | Medium |
| P2 | F18 AI Coach | Medium | Medium |
| P2 | F116 Novelty Engine | Medium | Medium |
| P2 | F117 Body Double | Medium | Medium |
| P2 | F122 Progress Visualisation | Medium | High |
| P2 | F126 Reward Shop | Medium | Medium |
| P2 | F129 Parallel Stimulation | Low | Low |
| P2 | F136 Deep Data Dashboard | Medium | Medium |
| P2 | F141 Texture & Pattern Zones | Low | Medium |
| P2 | F143 Pre-Workout Schedule | Low | Medium |
| P2 | F144 Consistent Audio | Low | Medium |
| P2 | F145 Shutdown Routine | Low | Medium |
| P3 | F19 Data Export | Low | Low |
| P3 | F20 Accessibility | Medium | High |
| P3 | F118 Hyperfocus Badge | Low | Low |
| P3 | F123 Fidget Haptics | Low | Low |
| P3 | F124 Accountability Alarm | Low | Medium |
| P3 | F127 Anti-Burnout | Low | Medium |
| P3 | F128 Social Contracts | Medium | Medium |
