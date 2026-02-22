# PulseFit — Project Brief

## What It Is

PulseFit is a **production-ready native Android fitness app** with **76+ features**, built on Kotlin + Jetpack Compose + Firebase. It gamifies heart-rate-zone training through an original **Burn Points** system — 1-3 points per minute in elevated HR zones, with a configurable daily target and streak bonuses.

What makes PulseFit unique: it is the **first fitness app with dedicated neurodivergent experience modes**. Four toggleable profiles — Standard, ADHD Focus, ASD Comfort, AuDHD Combined — reshape the entire UX around different cognitive needs. No competitor (OrangeTheory, Peloton, WHOOP, Fitbod, Apple Fitness+) offers anything like this.

## The Problem

Fitness apps assume everyone is motivated the same way.
- **ADHD users (10% of adults)** abandon apps because of friction, boredom, and time blindness
- **Autistic users (2-3% of adults)** abandon apps because of sensory overload, unpredictable UI changes, and social pressure
- The fitness app market is **$13.8B in 2026** and growing — yet neither group is served by existing products

PulseFit's neurodivergent-first design is a blue ocean opportunity.

## Current State

| Metric | Value |
|--------|-------|
| Build status | BUILD SUCCESSFUL |
| Total features | 76+ |
| Workout templates | 26 (5 categories) |
| Exercises | 20 (5 tread, 3 row, 12 floor) |
| Challenge types | 14 (single-session to month-long) |
| Navigation routes | 39 |
| ND profiles | 4 (Standard, ADHD, ASD, AuDHD) |
| Room DB version | 10 |
| Firebase collections | 10 |
| minSdk / targetSdk | 26 / 36 |

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

## Feature Highlights

### Core Workout System
- Real-time HR zone tracking via BLE heart rate monitors
- 26 workout templates across Standard, Beginner, Advanced, Specialty, and OTF-Style categories
- 20 exercises with form cues and Lottie-animated visual guides
- Equipment profiling (16 items across 4 environments)
- Weekly plan generator with calendar sync
- Health Connect bi-directional sync
- CSV and JSON data export

### Composable Voice Coaching
- Clip queue system assembling atomic audio segments into real-time coaching
- 3 voice styles: Literal (ASD), Standard, Hype (ADHD)
- ND-aware verbosity: ASD depth 2, Standard depth 4, ADHD depth 5, AuDHD depth 3
- Coaching target registry with exercise-specific speed/incline/watt targets
- Form cues, push-harder prompts, station change announcements

### Challenge System (14 Types)
- Single-session benchmarks: Trifecta, Summit Climb, Circuit Blitz, The Ladder, Outrun the Clock
- Multi-day endurance: Gauntlet Week, Overdrive Week, The Countdown
- Month-long programs: Mileage Month, Distance Expedition, Evolution Challenge
- Assessment: Pulse Check, PR Week
- Group: Tag Team

### Social & Groups (Firebase)
- Friends system with requests and activity feed
- Weekly leaderboard among friends
- Accountability contracts with weekly check-ins
- Group system with SecureRandom invite codes
- Group events, challenges, and weekly stats

### What Makes It Different

**ADHD Focus Mode** — zero-friction start, micro-rewards every minute, "Just 5 Minutes" mode to beat initiation paralysis, XP leveling (1-50), streak multipliers (1x-3x), task chunking ("Block 2 of 6"), variable reward drops, daily quests, celebration overkill, body double mode, accountability alarm.

**ASD Comfort Mode** — sensory control panel (per-channel Off/Reduced/Full), frozen UI layout that never changes, literal voice coach ("Heart rate: 155. Zone: Push." — no hype), predictable reward formula with no randomness, one-tap safe exit with zero guilt, transition warnings before every change, minimal mode (4 elements only), shutdown routine, consistent audio palette, deep data dashboard.

**AuDHD Combined Mode** — defaults to ASD calm UI with ADHD structured rewards. Every setting individually tuneable. Resolves conflict by defaulting to ASD comfort and surfacing ADHD as toggleable options.

## Competitive Positioning

| Feature | PulseFit | OTF | Peloton | WHOOP | Fitbod |
|---------|----------|-----|---------|-------|--------|
| ND experience modes | 4 profiles | None | None | None | None |
| Sensory control panel | Yes | No | No | No | No |
| ND voice coaching | 4 verbosity levels | No | No | No | No |
| HR zone training | Yes | Yes | Yes | Yes | No |
| Workout templates | 26 | Class schedule | Library | No | AI |
| Challenge system | 14 types | Monthly | Yes | No | No |
| Equipment profiling | 16 items | Gym-only | Bike/Tread | No | Yes |
| Health Connect | Full R/W | No | No | No | No |
| Price | Free/$4.99 | $12-169/mo | $13-44/mo | $30/mo | $13/mo |

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin (100%) |
| UI | Jetpack Compose + Material 3 (dark theme) |
| Build | AGP 9.0.1, Kotlin 2.2.10, KSP 2.2.10-2.0.2 |
| DI | Hilt 2.59+ |
| Database | Room (DB v10) with Gson serialization |
| Auth | Firebase Auth (email + Google) |
| Backend | Firebase Firestore (10 collections + security rules) |
| BLE | Android BLE API (HeartRateSource interface) |
| Health | Health Connect 1.1.0-alpha12 |
| Animations | Lottie (exercise guides) |
| Charts | Vico (Compose-native) |
| Crash reporting | Firebase Crashlytics |
| Architecture | MVVM + Clean Architecture |
| minSdk / targetSdk | 26 / 36 |

## Integrations

| Integration | Scope | Status |
|-------------|-------|--------|
| Health Connect | Read HR; write workout summaries | COMPLETE |
| Bluetooth LE | Direct pairing with chest straps and wrist monitors (0x180D) | COMPLETE |
| Firebase Auth | Email/password + Google Sign-In | COMPLETE |
| Firebase Firestore | Social, groups, challenges, profiles | COMPLETE |
| Fitbit / Garmin / Samsung | Direct APIs for deeper wearable integration | PLANNED |
| Wear OS | Companion app for on-wrist display | PLANNED |

## Development Phases

1. **Foundation** — Scaffolding, onboarding, zone engine **COMPLETE**
2. **Core Workout Loop** — BLE, live workout, Burn Points, safe exit, sensory control **COMPLETE**
3. **Health Connect & History** — Health Connect integration, history, trends **COMPLETE**
4. **Social & Templates** — Firebase backend, social features, 26 templates, voice coach, accessibility **COMPLETE**
5. **Advanced Features** — Challenges, equipment, weekly plans, exercise guides, security hardening **COMPLETE**
6. **Market Launch** — Play Store beta, premium tier, Wear OS, iOS port **NEXT**

## Monetisation

| Tier | Price | Includes |
|------|-------|----------|
| **Free** | $0 | Full solo tracking, all zones, Burn Points, streaks, all ND modes, 26 templates |
| **Premium** | $4.99/mo | Group mode, advanced analytics, AI coaching, premium voice packs |
| **White-Label** | Custom | Gym/studio licensing with custom branding |

## Market Opportunity

- Fitness app market: **$13.8B** (2026)
- ADHD prevalence: **10% of adults** (globally ~500M people)
- ASD prevalence: **2-3% of adults** (globally ~150M people)
- Combined addressable ND market: **12-13% of adults**
- No competitor serves this market with dedicated features
- PulseFit's ND modes serve as both product differentiator and market expansion

## What's Next

1. **Play Store Beta** — Submit to Google Play closed testing track
2. **Premium Tier** — In-app purchases for group mode, AI coach, advanced analytics
3. **Instructor Dashboard** — Tablet/TV view for gym operators
4. **Wear OS Companion** — On-wrist workout display
5. **iOS Port** — SwiftUI + HealthKit version
6. **Marketing** — "First ND-friendly fitness app" positioning

---

*Full specs: [PLAN.md](PLAN.md) | [FEATURES.md](FEATURES.md) | [NEURODIVERGENT_DESIGN.md](NEURODIVERGENT_DESIGN.md) — 76+ features detailed across 3 documents.*
