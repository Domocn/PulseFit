# PulseFit — Project Brief for Emergent

## What It Is

PulseFit is a heart-rate-zone training app with an original points system called **Burn Points**. Users earn 1-3 points per minute spent in elevated HR zones (Active / Push / Peak), with a default daily target of 12 points and streak bonuses for consistency. The app stands apart by offering dedicated **ADHD** and **ASD** experience modes — making it the first neurodivergent-aware fitness app on the market.

## The Problem

Fitness apps assume everyone is motivated the same way. ADHD users (10% of adults) abandon apps because of friction, boredom, and time blindness. Autistic users (2-3% of adults) abandon apps because of sensory overload, unpredictable UI changes, and social pressure. Neither group is served by existing products.

## Zones & Scoring

| Zone | % Max HR | Burn Points / Min |
|------|----------|-------------------|
| Rest | < 50% | 0 |
| Warm-Up | 50-59% | 0 |
| Active | 60-69% | 1 |
| Push | 70-84% | 2 |
| Peak | 85-100% | 3 |

Daily target: **12 Burn Points** (default, adjustable). Streak bonuses for consecutive days.

## IP-Safe Naming

- **App:** PulseFit (working name — trademark search recommended before launch)
- **Points:** Burn Points — generic fitness vocabulary
- **Zones:** Rest / Warm-Up / Active / Push / Peak — standard HR terminology
- None of the naming, zone labels, colours, or scoring formulas are derived from any existing brand

## Core Screens

| Screen | Function |
|--------|----------|
| **Onboarding** | Name, age, HR monitor pairing, optional ND profile selection (Standard / ADHD / ASD / Both) |
| **Home** | One-tap "GO" button, today's workout, streak counter, daily target progress, daily quests |
| **Live Workout** | Real-time heart rate, current zone (colour + pattern), Burn Points counter, elapsed time, voice coach |
| **Summary** | Duration, points earned, zone breakdown, XP progress bar |
| **History** | Past workouts with stats, filterable by date |
| **Settings** | Profile, HR zones, ND mode toggles, Sensory Control Panel (animation/sound/haptic sliders) |

## What Makes It Different

**ADHD Focus Mode** — zero-friction start, micro-rewards every 30 seconds, "Just 5 Minutes" mode to beat initiation paralysis, XP leveling, streak multipliers, task chunking (30 min = six 5-min blocks), celebration animations on every milestone.

**ASD Comfort Mode** — sensory control panel (dim animations, quiet sounds, gentle haptics), frozen UI layout that never changes, literal voice coach ("Heart rate: 155. Zone: Push." — no hype), predictable point formula with no random rewards, one-tap safe exit with zero guilt messaging, transition warnings before every screen or phase change.

**Combined Mode** — for users with both ADHD and ASD. Defaults to calm UI (ASD) with structured micro-rewards (ADHD). Every setting individually tuneable.

## Tech Stack

Kotlin, Jetpack Compose, Material 3, MVVM + Clean Architecture, Room, Hilt, Coroutines/Flow

## Integrations

| Integration | Scope |
|-------------|-------|
| **Health Connect (Google)** | Read HR data from any connected wearable; write workout summaries back |
| **Bluetooth LE** | Direct pairing with chest straps and wrist monitors (standard HR profile 0x180D) |
| **Post-MVP:** Fitbit, Garmin, Samsung | Direct APIs for deeper wearable integration |
| **Post-MVP:** Wear OS | Companion app for on-wrist workout display |

## Build Priority

**Phase 1 — Foundation:** Scaffolding, onboarding, zone engine, BLE + Health Connect pairing, Burn Points, quick start, safe exit, sensory control panel
**Phase 2 — ADHD Core:** Micro-rewards, Just 5 Min, XP/leveling, streaks, daily quests, celebrations
**Phase 3 — ASD Core:** Routine builder, literal voice coach, UI lock, social shield, minimal mode, transition warnings
**Phase 4 — Social & Data:** Leaderboards, deep data dashboard, reward shop, Fitbit/Garmin/Samsung/Wear OS integrations

## Target

Android first. Single user, local data. No backend required for MVP.

---

*Full specs: [PLAN.md](PLAN.md) · [FEATURES.md](FEATURES.md) · [NEURODIVERGENT_DESIGN.md](NEURODIVERGENT_DESIGN.md) — 57 features detailed across 3 documents.*
