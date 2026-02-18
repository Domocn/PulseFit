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

1. **Text-to-Speech (TTS) engine** — uses Android's built-in `TextToSpeech` API
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
| **Voice selection** | System TTS voices (device-dependent — typically 5–10 options) | Device default |
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

**Technical implementation:**

```kotlin
class VoiceCoachService(
    private val tts: TextToSpeech,
    private val settings: VoiceCoachSettings
) {
    private val phraseLibrary = PhraseLibrary()
    private var lastCalloutTime: Instant = Instant.MIN

    fun onZoneChanged(oldZone: Int, newZone: Int) {
        if (!settings.zoneCallouts) return
        if (!canSpeak()) return

        val phrase = if (newZone > oldZone) {
            phraseLibrary.randomFor(PhraseType.ZONE_UP, mapOf("zone" to zoneNameFor(newZone)))
        } else {
            phraseLibrary.randomFor(PhraseType.ZONE_DOWN, mapOf("zone" to zoneNameFor(newZone)))
        }
        speak(phrase, Priority.MEDIUM)
    }

    fun onBurnPointsEarned(total: Int, target: Int) {
        if (total == target) {
            speak(phraseLibrary.randomFor(PhraseType.TARGET_HIT), Priority.HIGH)
        } else if (settings.pointUpdateFrequency.shouldAnnounce(total)) {
            speak(phraseLibrary.randomFor(
                PhraseType.POINT_UPDATE,
                mapOf("points" to total, "target" to target)
            ), Priority.LOW)
        }
    }

    fun onSegmentChange(segment: WorkoutSegment, countdown: Boolean) {
        if (!settings.intervalCountdown) return
        speak("Next up: ${segment.name}. Target zone: ${zoneNameFor(segment.targetZone)}.", Priority.HIGH)
    }

    private fun canSpeak(): Boolean {
        return Duration.between(lastCalloutTime, Instant.now()) >= settings.minGap
    }

    private fun speak(text: String, priority: Priority) {
        requestAudioFocus()
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, UUID.randomUUID().toString())
        lastCalloutTime = Instant.now()
    }
}
```

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
| P1 | F11 Group Workout | High | High |
| P1 | F12 Instructor Dashboard | High | Medium |
| P1 | F13 Workout Templates | Medium | High |
| P1 | F14 Custom Builder | Medium | Medium |
| P2 | F15 Social & Leaderboards | High | High |
| P2 | F16 Wear OS | High | Medium |
| P2 | F17 Third-Party APIs | High | Medium |
| P2 | F18 AI Coach | Medium | Medium |
| P3 | F19 Data Export | Low | Low |
| P1 | F21 Voice Coach | Medium | High |
| P3 | F20 Accessibility | Medium | High |
