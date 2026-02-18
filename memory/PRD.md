# PulseFit - Product Requirements Document (Full Feature)

## Overview
PulseFit is a comprehensive heart-rate-zone training Progressive Web App (PWA) with neurodivergent-aware design. It features a gamified points system (Burn Points), workout templates, achievements, trends analytics, and dedicated ADHD/ASD experience modes.

## User Personas
1. **Standard User**: Fitness enthusiast wanting gamified HR training
2. **ADHD User**: Needs micro-rewards, dopamine hits, "Just 5 Minutes" mode
3. **ASD User**: Needs calm UI, sensory controls, predictable layouts
4. **Combined User**: Needs both ADHD rewards and ASD calm experience

## Implemented Features (Jan 18, 2026)

### Core Features ✅
- [x] Burn Points System (1-3 pts/min based on HR zones)
- [x] Daily Target Tracking (default 12 points)
- [x] Streak System with Streak Freeze
- [x] XP/Leveling System
- [x] 4 Experience Modes (Standard, ADHD Focus, ASD Comfort, Combined)

### Workout Templates ✅
- [x] 6 Pre-built Templates:
  - HIIT Blast (20 min, hard)
  - Endurance Builder (30 min, moderate)
  - 30-Min Interval Burner (30 min, moderate)
  - Easy Recovery (20 min, easy)
  - Peak Chaser (25 min, hard)
  - Quick 15-Min Burn (15 min, moderate)
- [x] Custom Template Builder
- [x] Segment-based structure with target zones
- [x] Audio/haptic cues for segment changes

### Achievements System ✅
- [x] 14 Achievements including:
  - First Steps (first workout)
  - Week Warrior (7-day streak)
  - Monthly Master (30-day streak)
  - Centurion (100-day streak)
  - Burn Baby Burn (1,000 lifetime points)
  - Inferno (10,000 lifetime points)
  - Peak Performer (20+ min in Peak zone)
  - Early Bird (workout before 7 AM)
  - Night Owl (workout after 9 PM)
  - Century Club (100 workouts)
  - Zone Master (all 5 zones in one workout)
  - Marathon Session (60+ min workout)
- [x] XP Rewards for each achievement

### Personal Bests ✅
- [x] Max Session Points
- [x] Longest Workout Duration
- [x] Longest Peak Zone Time
- [x] Best Streak

### Trends & Analytics ✅
- [x] Daily Burn Points Chart
- [x] Weekly Summary Bar Chart
- [x] Zone Distribution Analysis
- [x] 7/14/30/90 day view options
- [x] Interactive charts (recharts)

### Voice Coach ✅
- [x] Zone Change Callouts
- [x] Point Updates (configurable frequency)
- [x] Encouragement (frequent/occasional/off)
- [x] Interval Countdown
- [x] Pacing Warnings
- [x] Speech Rate Control (slow/normal/fast)
- [x] Volume Control
- [x] Min Gap Between Announcements

### Sensory Control Panel ✅
- [x] Animation Level (0-100%)
- [x] Sound Level (0-100%)
- [x] Haptic Level (0-100%)
- [x] Transition Warnings Toggle
- [x] Literal Labels Toggle

### Accessibility ✅
- [x] High Contrast Mode
- [x] Colorblind-Friendly Mode
- [x] Font Size Options (small/medium/large)
- [x] Full keyboard navigation
- [x] Screen reader friendly labels

### Data Management ✅
- [x] Export to JSON (full backup)
- [x] Export to CSV (workout data)
- [x] Streak Freeze Feature

### ADHD-Specific Features ✅
- [x] "Just 5 Minutes" Mode
- [x] Micro-rewards every 30 seconds
- [x] Daily Quests (5 quests)
- [x] XP leveling
- [x] Celebration animations

### ASD-Specific Features ✅
- [x] Calm UI with reduced animations
- [x] Predictable layout (frozen UI)
- [x] Literal voice coach
- [x] Zero-guilt exit messaging
- [x] Sensory control panel

## Technical Stack
- **Frontend**: React PWA with Framer Motion, Recharts
- **Backend**: FastAPI (Python)
- **Database**: MongoDB
- **Deployment**: PWA-ready for Capacitor/TWA Android conversion

## API Endpoints
### Users
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user
- `PATCH /api/users/{id}` - Update user
- `POST /api/users/{id}/use-streak-freeze` - Use streak freeze

### Settings
- `GET /api/users/{id}/settings` - Get settings
- `PUT /api/users/{id}/settings` - Update settings

### Workouts
- `POST /api/workouts` - Create workout
- `GET /api/workouts/{id}` - Get workout
- `GET /api/users/{id}/workouts` - Get workout history

### Templates
- `GET /api/templates` - Get all templates
- `GET /api/templates/{id}` - Get template
- `GET /api/users/{id}/templates` - Get user's custom templates
- `POST /api/users/{id}/templates` - Create custom template
- `DELETE /api/templates/{id}` - Delete custom template

### Analytics
- `GET /api/users/{id}/stats` - Get statistics
- `GET /api/users/{id}/trends` - Get trend data
- `GET /api/users/{id}/quests` - Get daily quests
- `GET /api/users/{id}/achievements` - Get achievements
- `GET /api/users/{id}/personal-bests` - Get personal bests

### Export
- `GET /api/users/{id}/export?format=json|csv` - Export data

### Simulation
- `POST /api/simulate-hr` - Simulate heart rate (MOCKED)
- `GET /api/zones` - Get zone configuration
- `GET /api/voice-phrases` - Get voice coach phrases

## Prioritized Backlog

### P1 - High Priority (Post-MVP)
- [ ] Real Bluetooth LE heart rate monitor pairing
- [ ] Health Connect integration
- [ ] Push notifications for streaks
- [ ] Offline service worker for full PWA

### P2 - Medium Priority
- [ ] Group workout mode
- [ ] Social features / leaderboards
- [ ] AI Coach suggestions
- [ ] Wear OS companion

### P3 - Future
- [ ] Fitbit/Garmin/Samsung direct APIs
- [ ] GPS route tracking
- [ ] Premium tier features
- [ ] Apple Watch support

## Notes
- HR simulation is MOCKED (no real sensor integration yet)
- Ready for Capacitor/TWA conversion to Android
- All ND features fully implemented
- 100% backend test coverage
