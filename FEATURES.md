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

---

## Competitive Edge Features (F200–F220)

> Features inspired by researching Orangetheory, Peloton, Strava, WHOOP, Fitbit, Noom, Apple Fitness+, and SuperBetter — then reimagined through PulseFit's neurodivergent-first, zone-reactive lens. Every feature below passes the **anti-generic filter**: it either doesn't exist in any competitor, or it's been enhanced with at least one PulseFit differentiator (ND modes, zone-reactivity, sensory control, or voice coaching).

---

### F201. Adaptive Max Heart Rate Engine

**Inspired by:** OTF (auto-adjusts MHR after 5+ classes)

**Goal:** Automatically refine each user's Max HR using their real workout data, making zones more accurate over time — transparently and with user control.

**How it works:**
1. After 10+ workouts, PulseFit analyses peak HR values from the last 30 sessions
2. Uses the 95th percentile of recorded peak HRs as a candidate MHR
3. Suggests adjustment: "Your Max HR was adjusted from 185 to 189 based on your last 10 workouts. Tap to review."
4. User can accept, reject, or override manually at any time
5. Recalculates every 15 workouts or on demand

**Why it's better than OTF:**
- OTF adjusts silently — PulseFit is fully transparent with the formula visible
- OTF requires studio visits — PulseFit works with any workout, anywhere
- OTF has no ND consideration — PulseFit handles it per mode

**ND mode behaviour:**
- **ADHD:** Notification with "Your zones just levelled up!" framing + 25 XP reward for reaching 10 workouts
- **ASD:** Shows exact formula, old vs new values side-by-side, requires explicit confirmation, never auto-applies
- **AuDHD:** Shows formula + XP reward, requires confirmation

---

### F202. Burn Point Benchmark Challenges

**Inspired by:** OTF (1 Mile Run, 500m Row — annual benchmark events)

**Goal:** Monthly timed challenges that create long-term progress markers and personal records.

**Benchmark types:**
- **20-Minute Burn** — How many Burn Points can you earn in exactly 20 minutes?
- **Zone Climber** — How quickly can you reach Zone 5 from rest and hold it for 2 minutes?
- **Endurance Test** — Maximum consecutive minutes in Zone 3+ without dropping below
- **Recovery Race** — Fastest time to drop from Zone 5 to Zone 2 (measures fitness)
- **Consistency King** — Most BP earned across 5 workouts in a calendar week

**Tracking:**
- Personal records stored per benchmark type with date and conditions
- Historical chart showing benchmark performance over months
- "You vs. You" comparison: never ranked against others (Anti-Comparison Mode compatible)

**ND mode behaviour:**
- **ADHD:** Benchmark unlocks special animated badge + 100 XP. New benchmark every month (novelty). "Beat your record!" framing
- **ASD:** Same benchmark available every month (predictability). Exact numbers shown. No time pressure messaging — "Complete when ready"
- **AuDHD:** Monthly availability + exact numbers + badge reward

---

### F203. Zone Chase Game Mode

**Inspired by:** Peloton Lanebreak (rhythm-game cycling)

**Goal:** A visual mini-game where your real heart rate controls gameplay. A target zone moves on screen and you earn bonus points for keeping your HR inside it.

**Gameplay:**
- A target zone indicator moves up and down on screen (Zone 2 → 4 → 3 → 5 → 3, etc.)
- Your actual HR zone is shown as a tracker
- When your zone matches the target: **+1 bonus BP per minute** on top of normal earning
- Accuracy score at end: "You matched the target zone 73% of the time"
- Difficulty levels: Easy (slow changes, wide zones), Medium, Hard (fast changes, narrow targets)

**Why no competitor has this:**
- Peloton Lanebreak uses resistance/cadence, not heart rate zones
- No app gamifies the act of controlling your heart rate in real time

**ND mode behaviour:**
- **ADHD:** Dynamic, unpredictable target pattern. Combo counter for consecutive matches. Sound effects on hit/miss. Maximum visual feedback
- **ASD:** Predictable, repeating target pattern shown in advance (Pre-Workout Visual Schedule). No sound effects by default. Pattern displayed as timeline before workout starts
- **AuDHD:** Predictable pattern + combo counter. Reduced sound. Timeline preview available

---

### F204. Pulse Encouragement

**Inspired by:** Peloton High-Fives

**Goal:** One-tap encouragement sent to workout buddies or Body Double partners during live workouts.

**How it works:**
- During a workout, if a friend or Body Double partner is also active, a small avatar appears in corner
- Tap to send a "Pulse" — a brief haptic + visual glow on their screen
- No text, no chat, no comparison data — just presence acknowledgement
- Receiving a Pulse triggers a micro-dopamine hit (small animation + haptic)

**ND mode behaviour:**
- **ADHD:** Sending/receiving Pulses grants 5 XP each. Animation is a satisfying ripple effect. "You received 3 Pulses today!" on summary
- **ASD:** Incoming Pulses can be set to silent (no interruption). Outgoing only. No animation by default — just a subtle icon change. Fully disableable
- **AuDHD:** Receive as icon-only (no animation), send with XP reward

**Social Pressure Shield interaction:** When shield is ON, Pulse feature is completely hidden.

---

### F205. AI Workout Intelligence

**Inspired by:** Strava Athlete Intelligence + WHOOP Coach (persistent memory AI)

**Goal:** Natural language post-workout insights and a conversational AI coach with memory across sessions.

**Post-workout insights (rule-based, free tier):**
- "Your Push Zone efficiency improved 12% this week — you're spending less time in Warm-Up"
- "You've done 5 Push-heavy sessions in a row. An Active Recovery day could boost your next performance"
- "Your average HR for the same effort dropped 4 BPM this month — a sign of improving fitness"
- "Morning workouts earn you 18% more BP on average than evening ones"

**Conversational AI Coach (LLM-powered, Premium):**
- Ask questions: "Why was today's workout harder?" → analyses HR patterns, sleep data, recent history
- Persistent memory: remembers your goals, preferences, struggles across sessions
- Proactive suggestions: "Based on your recovery score and tomorrow's schedule, I'd suggest a 20-min Endurance Builder"

**ND mode behaviour:**
- **ADHD:** Insights framed as achievements: "New discovery: your best zone is Push!" Conversational coach uses energetic tone
- **ASD:** Insights are data-first with exact numbers. No interpretive language. Coach uses literal, factual communication. All suggestions include the reasoning/data behind them
- **AuDHD:** Data-first framing + achievement badges for insights

**Why it's better than competitors:**
- Strava's Athlete Intelligence has no ND-aware communication
- WHOOP Coach has no heart-rate-zone gamification context
- Neither adjusts communication style based on neurodivergent profile

---

### F206. Recurring Workout Comparison

**Inspired by:** Strava Segments (location-based leaderboards)

**Goal:** Automatically detect when you repeat the same type of workout and show personal progress — like Strava segments but for indoor/any workouts.

**How it works:**
- PulseFit clusters workouts by: template used, duration (±5 min), time of day (±2 hrs), workout type tag
- When a cluster has 3+ workouts, it becomes a "Recurring Workout"
- After each matching workout, shows comparison: "This Tuesday HIIT: 24 BP. Last Tuesday: 18 BP. +33%"
- Charts recurring workout performance over time

**Why no competitor has this for indoor workouts:**
- Strava segments are GPS/location-based — useless indoors
- No app clusters indoor workouts by pattern and shows progress

**ND mode behaviour:**
- **ADHD:** Improvement shown as levelling-up animation. "Your Tuesday HIIT is now Level 4!" Progress badge unlocked
- **ASD:** Exact numbers, percentage change, side-by-side data table. "Session 12 of this workout type. Trend: +2.1 BP/session average improvement"
- **AuDHD:** Data table + level badge

---

### F207. Pulse Readiness Score

**Inspired by:** WHOOP Recovery Score + Fitbit Daily Readiness Score

**Goal:** A daily readiness score (0–100) combining HRV, resting HR, and sleep quality that dynamically adjusts your Burn Point target.

**Inputs:**
- **HRV** (Heart Rate Variability) — from wearable or morning measurement via camera-based detection
- **Resting HR** — from overnight/morning wearable data or manual entry
- **Sleep quality** — duration + self-reported quality (1–5) or wearable sleep data
- **Recent training load** — last 7 days of BP earned vs. target

**Score interpretation:**
- 80–100 (Green): Full capacity. Suggested target: standard or +20%
- 50–79 (Yellow): Moderate. Suggested target: standard
- 20–49 (Orange): Recovery needed. Suggested target: -30%
- 0–19 (Red): Rest day recommended. Suggested target: 0 (gentle movement only)

**Dynamic target adjustment:**
- "Your Pulse Readiness is 62 today. Suggested target: 10 BP (instead of 12). Tap to accept or keep 12."
- Adjustments are always suggestions — never forced

**ND mode behaviour:**
- **ADHD:** Readiness shown as a battery/fuel gauge. Green = "Fully charged!" If low: "Low battery day — a short Just 5 Minutes workout still counts!" Never demotivating
- **ASD:** Exact score + all input values + formula visible. Adjustment reasoning shown. Predictable: same inputs = same score always
- **AuDHD:** Exact score + battery visual. Formula available on tap

**Spoon Theory integration (F212):** Users who set daily spoons see Readiness AND spoons together — whichever is lower drives the suggestion.

---

### F208. Sleep-Workout Planner

**Inspired by:** WHOOP Sleep Planner

**Goal:** Recommend a bedtime based on tomorrow's planned workout intensity, so users wake up recovered.

**How it works:**
- Checks tomorrow's Routine Builder schedule (F132) or manually set workout plan
- Calculates recommended sleep duration based on: planned intensity, today's training load, current recovery trend
- Suggests bedtime: "For tomorrow's Push workout at 7 AM, aim for bed by 10:30 PM (8.5 hours)"
- Evening notification at suggested bedtime minus 30 min: "Start winding down for tomorrow's workout"

**ND mode behaviour:**
- **ADHD:** Framed as "Level up your sleep to unlock better performance tomorrow." Time blindness helper: "That's in 2 hours and 15 minutes from now"
- **ASD:** Exact bedtime, exact duration, exact wake time. Same notification timing every night. No motivational framing — just data
- **AuDHD:** Exact times + "2 hours from now" helper

---

### F209. Zone Science Micro-Lessons

**Inspired by:** Noom (daily CBT-based micro-lessons + quizzes)

**Goal:** 30-second daily training science tips that teach users about HR zones, fitness adaptation, and the science behind Burn Points. Earning XP makes learning feel like part of the game.

**Content categories:**
- **Zone Science** — "Why does Zone 4 burn more fat post-workout? The EPOC effect explained in 30 seconds"
- **Recovery Science** — "Why rest days make you stronger: the supercompensation principle"
- **Heart Rate Literacy** — "What your resting HR trend tells you about your fitness"
- **Burn Points Mastery** — "Strategy: 20 min in Zone 4 vs. 10 min in Zone 5 — which earns more BP?"
- **ND & Exercise** — "Why exercise helps ADHD: the dopamine-norepinephrine connection"

**Format:**
- 30-second read or listen (voice coach reads aloud if enabled)
- Optional 1-question quiz: "Which zone has the highest EPOC effect?" → correct = 10 XP
- New lesson available daily, archive of past lessons accessible anytime

**ND mode behaviour:**
- **ADHD:** Lesson appears as a "Daily Discovery" with mystery XP reward reveal. Swipeable card format. Quiz framed as "Challenge"
- **ASD:** Full text, no time pressure, no gamified framing. Just "Today's Lesson" with factual content. Quiz optional, no pressure
- **AuDHD:** Factual content + XP reveal

---

### F210. Wellness Radar

**Inspired by:** SuperBetter (4-dimension resilience tracking)

**Goal:** A 4-axis radar chart on the home screen showing holistic progress beyond just Burn Points.

**Four axes:**
1. **Training** — Weekly BP earned vs. target (0–100%)
2. **Recovery** — Pulse Readiness Score average this week (0–100)
3. **Consistency** — Days active this week / 7 × 100 (0–100%)
4. **Growth** — Benchmark improvement + new personal bests this month (0–100%)

**Display:**
- Radar/spider chart on home screen (compact, expandable)
- Each axis colour-coded and labelled
- Week-over-week overlay showing trend
- Tap any axis for detailed breakdown

**ND mode behaviour:**
- **ADHD:** Radar pulses/glows when an axis improves. "Your Consistency just hit 85% — that's a new weekly best!" Gamified labels: "Training Warrior", "Recovery Master"
- **ASD:** Clean lines, no animation, exact percentages displayed on each axis. No gamified labels — just "Training: 72%, Recovery: 88%, Consistency: 57%, Growth: 45%"
- **AuDHD:** Exact percentages + glow on improvement

---

### F211. Zone Accuracy Training (Body Awareness Calibration)

**No competitor has this.**

**Goal:** Teach users to feel their heart rate zones without looking at the screen — building interoceptive awareness over time.

**How it works:**
1. After each workout, user rates perceived exertion per segment: "How hard did that feel?" (1–10 RPE scale)
2. PulseFit compares RPE rating against actual HR zone data
3. Shows calibration score: "Your body awareness accuracy: 78% — you correctly identified your zone 78% of the time"
4. Over weeks, charts calibration improvement
5. Unlock "Zone Sense" badge at 90%+ accuracy across 10 workouts

**Why this matters:**
- No fitness app teaches body awareness — they all make you screen-dependent
- Interoception (sensing internal body states) is often atypical in neurodivergent people
- This feature helps users gradually reduce screen dependency during workouts

**ND mode behaviour:**
- **ADHD:** Gamified as a "Body Sense Level" (1–10). Each level unlocks at higher accuracy. "You're a Level 6 Body Scanner!" + XP rewards
- **ASD:** Data-driven: scatter plot of RPE vs. actual zone. Statistical correlation shown. "Your RPE-to-zone correlation coefficient: 0.82"
- **AuDHD:** Scatter plot + level system

---

### F212. Spoon Theory Integration

**No competitor has this.**

**Goal:** For users with chronic fatigue, disability, or energy-limited conditions (common in neurodivergent populations), let them set daily "spoons" (energy units) that auto-adjust targets.

**How it works:**
- Optional feature in Settings → Energy Management
- Morning check-in: "How many spoons do you have today?" (1–10 scale, or custom labels)
- Burn Point target auto-adjusts: 10 spoons = full target, 5 spoons = 50% target, 2 spoons = gentle movement only
- "Low spoon day" earns full streak credit at the adjusted target — no penalty for low energy
- Voice coach adjusts tone: low spoons = "Every movement counts today. No pressure."

**Why this matters:**
- Spoon theory is widely used in disability and neurodivergent communities
- No fitness app acknowledges that daily capacity varies dramatically
- Forcing the same target on a 2-spoon day as a 10-spoon day guarantees abandonment

**ND mode behaviour:**
- **ADHD:** Quick emoji-based spoon check (tap energy level in 1 second). Target adjusts instantly with encouraging message
- **ASD:** Numeric scale with exact mapping shown: "5 spoons → target: 6 BP, streak maintained at 6+ BP"
- **AuDHD:** Numeric scale + encouraging message

---

### F213. Stim-Friendly Haptic Library

**No competitor has this.**

**Goal:** A library of haptic patterns that serve sensory-seeking (ADHD) or sensory-regulating (ASD) needs during workouts.

**Pattern categories:**

**ADHD Stimulating Patterns (sensory-seeking):**
- Rapid pulse (fast rhythmic vibration during low-intensity segments)
- Heartbeat sync (vibration matches your actual heart rate)
- Random burst (unpredictable short pulses for novelty)
- Victory rumble (deep satisfying vibration on point earned)

**ASD Regulating Patterns (sensory-grounding):**
- Steady metronome (consistent, predictable rhythm)
- Breathing guide (inhale-hold-exhale pattern via haptics)
- Gentle wave (slow crescendo-decrescendo)
- Zone anchor (fixed pattern per zone — always the same)

**Customisation:**
- Users can assign any pattern to any workout event (zone change, point earned, segment transition)
- Intensity slider per pattern
- Preview before enabling
- Create custom patterns by combining base vibrations

---

### F214. Context-Aware Voice Personality

**No competitor has this.**

**Goal:** The ElevenLabs voice coach dynamically adjusts personality based on contextual signals — not just ND mode, but time of day, recovery state, and streak status.

**Context signals → voice adjustments:**

| Context | Voice Adjustment |
|---------|-----------------|
| Morning + low recovery | Gentler tone, slower pace: "Let's ease into this one" |
| Day 7+ of streak | Pride tone: "Seven days strong. You've built something real" |
| First workout after 3+ day gap | Warm welcome, zero guilt: "Welcome back. Let's pick up where you left off" |
| Post-benchmark PR | Maximum excitement: "NEW PERSONAL BEST! That was incredible!" |
| Low spoon day (F212) | Extra gentle: "Every minute counts today. You showed up — that's what matters" |
| Evening workout | Calmer energy: "Nice way to end the day" |
| Approaching target | Building intensity: "Two more points — you're almost there!" |

**ND mode interaction:**
- Context adjustments layer ON TOP of ND voice profile
- ADHD energetic voice + low recovery context = energetic but gentler
- ASD literal voice + streak context = "This is day 7 of your streak. Heart rate: 145. Push zone."

---

### F215. Transition Ritual Builder

**No competitor has this.**

**Goal:** Customisable pre-workout and post-workout routines that the app guides users through — addressing executive function challenges (ADHD) and routine needs (ASD).

**Pre-workout ritual (configurable sequence):**
1. Breathing exercise (30s–2min, box breathing or custom)
2. Equipment check ("Do you have water? Heart rate monitor on?")
3. Intention setting ("What's your focus today?" — optional free text or preset)
4. Dynamic stretch guide (optional, 2–5 min)
5. Music start (auto-play linked playlist via Spotify/Apple Music intent)
6. "3… 2… 1… Let's go" countdown

**Post-workout ritual (configurable sequence):**
1. Cool-down breathing (guided by voice coach)
2. Static stretch guide (optional, 3–5 min)
3. Hydration reminder
4. Workout summary (F5)
5. Journal prompt ("How did that feel?" — optional free text)
6. Shutdown confirmation ("Workout saved. See you tomorrow at [scheduled time]")

**ND mode behaviour:**
- **ADHD:** Ritual is optional and skippable per step. Default: minimal (breathing + countdown only). "Skip ritual" always visible. Purpose: reduce decision friction at workout start
- **ASD:** Ritual runs the same way every time. Full sequence by default. Same order, same timing, same voice. Purpose: predictable routine reduces anxiety. Deviation warning if user tries to skip a step: "You usually do breathing first. Skip today?"
- **AuDHD:** Same order every time + skip buttons visible

---

### F216. Anti-Comparison Mode

**No competitor has this.**

**Goal:** Go beyond Social Pressure Shield (F135) — actively reframe ALL metrics as personal progress. Never show ranking, percentiles, or comparative language.

**How it works:**
- One toggle in Settings: "Show only personal progress"
- When ON, all screens use self-referential language only:
  - ❌ "You ranked 47th this week"
  - ✅ "You earned 15 BP today — 3 more than your Tuesday average"
  - ❌ "Top 20% of users"
  - ✅ "Your best week this month"
  - ❌ "Beat 156 people in this challenge"
  - ✅ "You completed the challenge — your 4th this month"
- Group workout displays show only your own data (others' tiles hidden)
- Challenge completion shows personal achievement, not ranking
- Even AI Coach insights avoid comparative language

**Difference from Social Pressure Shield (F135):**
- F135 hides social features entirely
- F216 keeps social features visible but rewrites all language to be self-referential
- They can be used independently or together

---

### F217. Workout Compatibility Score

**No competitor has this.**

**Goal:** Before starting a workout, show a compatibility score based on current energy, time available, and recovery — suggesting the best-fit template.

**How it works:**
1. User taps "Start Workout" → sees template library as usual
2. Each template now shows a compatibility badge: "92% match" / "67% match" / "41% match"
3. Score calculated from:
   - **Time available** (user sets or app infers from calendar)
   - **Energy level** (Spoon Theory F212 or Pulse Readiness F207)
   - **Recent training** (what zones you've hit this week — suggests what you need)
   - **Template difficulty** vs. current capacity
4. Top suggestion highlighted: "Best for you right now: Endurance Builder (est. 14 BP, 25 min)"

**ND mode behaviour:**
- **ADHD:** Shows top 1 suggestion prominently with "Perfect for right now!" label. One-tap start. Reduces decision paralysis
- **ASD:** Shows all templates with exact scores and reasoning: "92%: matches your time (30 min), energy (7/10), and recovery (green). Missing zones this week: Push"
- **AuDHD:** Top suggestion + full reasoning on tap

---

### F218. Flow State Guardian

**No competitor has this.**

**Goal:** Detect when the user enters a flow state and protect it by silencing all interruptions until flow naturally breaks.

**Flow state detection:**
- Steady Zone 3–4 heart rate (low HR variance, coefficient of variation < 5%)
- Duration 15+ unbroken minutes in scoring zones
- No pause events
- Consistent cadence (if detectable)

**When flow is detected:**
- ALL voice callouts stop
- ALL micro-reward animations stop
- ALL notifications suppressed
- Screen dims slightly (less visual distraction)
- A subtle "Flow" indicator appears (small icon, no animation)
- Timer continues, points still accumulate silently

**When flow breaks (HR drops to Zone 1–2 for 60+ seconds or user pauses):**
- Accumulated rewards delivered in a satisfying burst: "While you were in flow: 14 BP earned, 2 badges unlocked, 1 quest completed!"
- Voice coach: "That was 23 minutes of unbroken flow. Incredible focus."
- Flow Duration tracked as a personal stat and personal best

**Why this matters for ADHD:**
- ADHD hyperfocus during exercise is rare and precious
- Most apps INTERRUPT flow with celebrations, callouts, and notifications
- PulseFit is the only app that recognizes flow and actively protects it

**ND mode behaviour:**
- **ADHD:** Flow state detection ON by default. Post-flow burst is maximum celebration. "FLOW STATE ACHIEVED!" badge + 100 XP
- **ASD:** Flow detection ON by default. Post-flow: data only, no burst animation. "Flow duration: 23:14. Points earned during flow: 14"
- **AuDHD:** Flow detection ON. Post-flow: data + 100 XP, reduced animation

---

### F219. Heart Rate Story Mode

**No competitor has this.**

**Goal:** Narrative workouts where your heart rate literally controls the story — your zones drive the plot forward.

**How it works:**
- User selects a Story Workout (e.g., "The Mountain Escape", "Space Station Emergency", "Dragon Chase")
- Story has chapters mapped to zone targets:
  - Chapter 1 (Zone 2): "You're hiking through the forest. Keep a steady pace…"
  - Chapter 2 (Zone 3): "You hear something behind you. Pick up the pace…"
  - Chapter 3 (Zone 4): "It's gaining on you! Run!"
  - Chapter 4 (Zone 5): "Sprint to the bridge! Everything you've got!"
  - Chapter 5 (Zone 3): "You made it across. Catch your breath…"
- Voice coach narrates the story using ElevenLabs with dramatic delivery
- Story only advances when you reach the target zone — your HR is the controller
- Completion unlocks the next chapter/story + XP + unique story badge

**Why this is groundbreaking:**
- Peloton Lanebreak gamifies cadence, not HR
- No app uses narrative + heart rate as a game mechanic
- Combines fitness, storytelling, and biofeedback in a way that's never been done

**ND mode behaviour:**
- **ADHD:** Maximum novelty — different stories rotate. Voice is dramatic and engaging. "What happens next?!" cliffhangers between sessions. This is the ultimate ADHD engagement tool
- **ASD:** Story structure shown before starting (all chapters, zone targets, estimated duration). Same story replayable for comfort. Voice is calm narrator, not dramatic. Literal descriptions: "Chapter 3 requires Zone 4 for approximately 4 minutes"
- **AuDHD:** Story preview available + calm narration + cliffhanger unlocks

---

### F220. Community Template Marketplace

**Inspired by:** No direct competitor — but extends Strava's route sharing concept to structured workouts

**Goal:** A marketplace where users share, discover, rate, and remix workout templates — creating a self-sustaining content ecosystem.

**How it works:**
- Users can publish Custom Workout Builder (F14) templates to the marketplace
- Each template shows: creator, average BP earned, average rating, completion count, zone distribution chart
- Users can: browse by category/duration/difficulty, try templates, rate (1–5 stars), save favourites
- **Remix:** Take any public template and modify it → publishes as "Based on [original]" with attribution
- **Creator rewards:** Earn 5 XP every time someone completes your template. Top creators get "Template Master" badge

**Curation:**
- Staff picks (manually curated monthly)
- "Trending this week" (by completion count)
- "Best for your level" (algorithmic match based on history)
- "ND Recommended" tag for templates tested/approved for specific ND modes

**ND mode behaviour:**
- **ADHD:** "New this week" prominently featured (novelty). One-tap try. Creator XP notifications are extra satisfying
- **ASD:** Filter by "Most Consistent" (templates with predictable structure). Preview full segment timeline before downloading. "ND Recommended" filter available
- **AuDHD:** "ND Recommended" filter + one-tap try

**Social Pressure Shield interaction:** When shield is ON, marketplace shows templates anonymously (no creator names, no completion counts, no ratings — just the workout structure).

---

## Updated Feature Priority Matrix (Including Competitive Edge Features)

| Priority | Feature | Effort | Impact | Inspiration |
|----------|---------|--------|--------|-------------|
| P1 | F201 Adaptive Max HR Engine | Medium | High | OTF |
| P1 | F218 Flow State Guardian | Medium | High | Original |
| P1 | F212 Spoon Theory Integration | Low | High | Original |
| P1 | F214 Context-Aware Voice | Medium | High | Original |
| P1 | F216 Anti-Comparison Mode | Low | High | Original |
| P2 | F202 Benchmark Challenges | Medium | High | OTF |
| P2 | F205 AI Workout Intelligence | High | High | Strava/WHOOP |
| P2 | F206 Recurring Workout Comparison | Medium | Medium | Strava |
| P2 | F207 Pulse Readiness Score | High | High | WHOOP/Fitbit |
| P2 | F209 Zone Science Micro-Lessons | Medium | Medium | Noom |
| P2 | F210 Wellness Radar | Medium | Medium | SuperBetter |
| P2 | F211 Zone Accuracy Training | Low | Medium | Original |
| P2 | F217 Workout Compatibility Score | Medium | Medium | Original |
| P2 | F219 Heart Rate Story Mode | High | High | Original |
| P3 | F203 Zone Chase Game Mode | High | Medium | Peloton |
| P3 | F204 Pulse Encouragement | Low | Low | Peloton |
| P3 | F208 Sleep-Workout Planner | Medium | Medium | WHOOP |
| P3 | F213 Stim-Friendly Haptic Library | Medium | Medium | Original |
| P3 | F215 Transition Ritual Builder | Medium | Medium | Original |
| P3 | F220 Community Template Marketplace | High | High | Original |
