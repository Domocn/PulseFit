# PulseFit PRD — DEPRECATED

> **This file is deprecated.** It described a React PWA + FastAPI + MongoDB prototype that has been superseded by the production native Android app.

## Current Documentation

The real PulseFit documentation lives in the project root:

- **[FEATURES.md](../FEATURES.md)** — Complete feature specification (76+ features)
- **[PLAN.md](../PLAN.md)** — Architecture, data model, development phases
- **[NEURODIVERGENT_DESIGN.md](../NEURODIVERGENT_DESIGN.md)** — ND design philosophy and feature specs
- **[EMERGENT_BRIEF.md](../EMERGENT_BRIEF.md)** — Executive summary and market positioning

## What Changed

PulseFit was rebuilt from the ground up as a **native Android app** using:
- Kotlin + Jetpack Compose + Material 3
- Room (local DB, version 10) + Firebase Auth + Firestore
- Hilt (DI) + KSP + AGP 9.0.1
- BLE heart rate + Health Connect integration

The React/FastAPI/MongoDB stack described in the original PRD is no longer in use.
