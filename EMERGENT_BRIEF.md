# PulseFit — Project Brief for Emergent

## What It Is

PulseFit is a heart-rate zone fitness app that connects to Bluetooth heart rate monitors and turns workouts into a real-time points game. Users earn **Burn Points** by exercising in their target heart rate zones. The app stands apart by offering dedicated **ADHD** and **ASD** experience modes — making it the first neurodivergent-aware fitness app on the market.

## The Problem

Fitness apps assume everyone is motivated the same way. ADHD users (10% of adults) abandon apps because of friction, boredom, and time blindness. Autistic users (2-3% of adults) abandon apps because of sensory overload, unpredictable UI changes, and social pressure. Neither group is served by existing products.

## Core Screens & Logic

| Screen | Function |
|--------|----------|
| **Onboarding** | Name, age, HR monitor pairing, optional ND profile selection (Standard / ADHD / ASD / Both) |
| **Home** | One-tap "GO" button, today's workout (from routine or quick start), streak counter, daily quests |
| **Live Workout** | Real-time heart rate, current zone (colour + pattern), Burn Points counter, elapsed time, voice coach |
| **Summary** | Duration, points earned, zone breakdown, XP progress bar |
| **History** | List of past workouts with stats, filterable by date |
| **Settings** | Profile, HR zones, ND mode toggles, Sensory Control Panel (animation/sound/haptic intensity sliders) |

## Key Data

- **UserProfile**: name, age, max HR, ND profile, XP level, streak count, sensory preferences
- **Workout**: start/end time, duration, Burn Points, avg/max HR, zone time breakdown
- **HR Readings**: per-second heart rate with zone and points (from BLE sensor)
- **Weekly Routine** (ASD): fixed schedule — day, time, workout structure, target zone
- **Daily Quests** (ADHD): 3 daily goals with XP rewards, auto-generated each morning

## What Makes It Different

**ADHD Focus Mode** — zero-friction start, micro-rewards every 30 seconds, "Just 5 Minutes" mode to beat initiation paralysis, XP leveling, streak multipliers, task chunking (30 min = six 5-min blocks), celebration animations on every milestone.

**ASD Comfort Mode** — sensory control panel (dim animations, quiet sounds, gentle haptics), frozen UI layout that never changes, literal voice coach ("Heart rate: 155. Zone: Push." — no hype), predictable point formula with no random rewards, one-tap safe exit with zero guilt messaging, transition warnings before every screen or phase change.

**Combined Mode** — for users with both ADHD and ASD. Defaults to calm UI (ASD) with structured micro-rewards (ADHD). Every setting individually tuneable.

## Technical Needs

| Capability | Required For |
|------------|-------------|
| **Bluetooth LE** | Heart rate monitor connection (Heart Rate Service UUID 0x180D) |
| **Real-time data** | 1Hz heart rate updates driving zone calculation and point accumulation |
| **Text-to-speech** | Voice coach with 3 styles (standard, hype, literal) |
| **Haptic engine** | Zone changes, point feedback, fidget/calming patterns |
| **Local database** | Workout history, HR readings, user preferences, routines |
| **Scheduled notifications** | Daily workout reminders, quest generation |
| **Background processing** | HR tracking continues when screen off or app backgrounded |

## Build Priority

**Phase 1 (MVP):** BLE pairing, live HR + zones, Burn Points, quick start, safe exit, sensory control panel
**Phase 2:** ADHD mode (micro-rewards, Just 5 Min, XP, streaks, quests, celebrations)
**Phase 3:** ASD mode (routine builder, literal voice coach, UI lock, social shield, minimal mode, transition warnings)
**Phase 4:** Social features, leaderboards, deep data dashboard, reward shop

## Target

Android first. Single user, local data. No backend required for MVP.

---

*Full specs: [PLAN.md](PLAN.md) · [FEATURES.md](FEATURES.md) · [NEURODIVERGENT_DESIGN.md](NEURODIVERGENT_DESIGN.md) — 57 features detailed across 3 documents.*
