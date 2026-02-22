#!/usr/bin/env python3
"""
Pre-generate ElevenLabs voice coach clips for PulseFit offline playback.

Generates ~324 MP3 files:
  - 15 zone-change clips (3 styles x 5 zones)
  - 36 time-update clips (3 styles x 12 intervals: 5..60 min)
  - 9 workout start greetings (3 styles x 3 types)
  - 9 workout complete clips (3 styles x 3 types)
  - 3 daily target hit clips (3 styles x 1)
  - 15 mid-workout encouragement clips (3 styles x 5)
  - 24 streak celebration clips (3 styles x 8 milestones)
  - 9 comeback/return clips (3 styles x 3 types)
  - 9 progress callout clips (3 styles x 3 types)
  - 9 station change clips (3 styles x 3 stations)
  - 60 exercise announcement clips (3 styles x 20 exercises)
  --- NEW composable coaching clips ---
  - 33 target clips (3 styles x 11 targets)
  - 30 duration clips (3 styles x 10 durations)
  - 18 motivation clips (3 styles x 6 connectors)
  - 18 push-harder clips (3 styles x 6 variants)
  - 18 form cue clips (3 styles x 6 cues)
  - 12 prefix clips (3 styles x 4 prefixes)

Usage:
    pip install -r requirements.txt
    export ELEVENLABS_API_KEY=sk-...
    python generate_voice_clips.py
"""

import os
import sys
from pathlib import Path

from dotenv import load_dotenv
from elevenlabs import ElevenLabs, VoiceSettings

load_dotenv()

API_KEY = os.environ.get("ELEVENLABS_API_KEY")
if not API_KEY:
    print("ERROR: Set ELEVENLABS_API_KEY env var or add it to .env")
    sys.exit(1)

client = ElevenLabs(api_key=API_KEY)

# Output directory — Android raw resources
OUTPUT_DIR = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "res" / "raw"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

# Voice IDs (from backend/server.py VOICE_PRESETS)
VOICES = {
    "literal":  "EXAVITQu4vr4xnSDxMaL",  # Sarah — calm, clear (ASD-friendly)
    "standard": "21m00Tcm4TlvDq8ikWAM",  # Rachel — professional
    "hype":     "pNInz6obpgDQGcFmaJgB",  # Adam — energetic
}

# Voice settings per style
VOICE_SETTINGS = {
    "literal":  VoiceSettings(stability=0.8, similarity_boost=0.5, style=0.0, use_speaker_boost=True),
    "standard": VoiceSettings(stability=0.7, similarity_boost=0.6, style=0.0, use_speaker_boost=True),
    "hype":     VoiceSettings(stability=0.5, similarity_boost=0.75, style=0.0, use_speaker_boost=True),
}

# ---------------------------------------------------------------------------
# Clip definitions — text must match VoiceCoachEngine.kt exactly
# ---------------------------------------------------------------------------

ZONES = ["rest", "warm_up", "active", "push", "peak"]

ZONE_TEXTS = {
    "literal": {
        "rest":    "Zone changed to Rest. Points per minute: 0.",
        "warm_up": "Zone changed to Warm-Up. Points per minute: 0.",
        "active":  "Zone changed to Active. Points per minute: 1.",
        "push":    "Zone changed to Push. Points per minute: 2.",
        "peak":    "Zone changed to Peak. Points per minute: 3.",
    },
    "standard": {
        "rest":    "Take it easy.",
        "warm_up": "Warming up nicely.",
        "active":  "Good pace. Earning points.",
        "push":    "Pushing hard. Great effort!",
        "peak":    "You're on fire! Maximum points!",
    },
    "hype": {
        "rest":    "Chill mode. You got this!",
        "warm_up": "Let's warm it up! Getting started!",
        "active":  "YES! Active zone! Points are rolling in!",
        "push":    "PUSH IT! Double points baby!",
        "peak":    "PEAK ZONE! MAXIMUM OVERDRIVE! Triple points!",
    },
}

TIME_INTERVALS = list(range(5, 65, 5))  # 5, 10, 15 ... 60

TIME_TEXTS = {
    "literal":  lambda m: f"{m} minutes elapsed.",
    "standard": lambda m: f"{m} minutes in.",
    "hype":     lambda m: f"{m} minutes! Keep that energy up!",
}

# ---------------------------------------------------------------------------
# Workout start greetings (3 types)
# ---------------------------------------------------------------------------

START_TEXTS = {
    "literal": {
        "start_first":   "This is your first workout. Let's begin.",
        "start_welcome": "Workout ready. Starting now.",
        "start_streak":  "Your streak is active. Let's keep it going.",
    },
    "standard": {
        "start_first":   "Welcome to your very first workout! Let's make it count.",
        "start_welcome": "Good to see you. Let's get started.",
        "start_streak":  "Your streak is on fire. Let's keep building.",
    },
    "hype": {
        "start_first":   "FIRST WORKOUT! Let's make it legendary!",
        "start_welcome": "Let's GO! Time to crush it!",
        "start_streak":  "STREAK MODE! You are UNSTOPPABLE!",
    },
}

# ---------------------------------------------------------------------------
# Workout complete (3 types)
# ---------------------------------------------------------------------------

COMPLETE_TEXTS = {
    "literal": {
        "complete_good":   "Workout complete. Well done.",
        "complete_target": "Workout complete. You hit your daily target.",
        "complete_pb":     "Workout complete. That was a new personal best.",
    },
    "standard": {
        "complete_good":   "Great workout! You should be proud.",
        "complete_target": "You smashed your daily target. Amazing work!",
        "complete_pb":     "New personal best! That's incredible progress!",
    },
    "hype": {
        "complete_good":   "WORKOUT DONE! You are a BEAST!",
        "complete_target": "TARGET SMASHED! You are on ANOTHER LEVEL!",
        "complete_pb":     "NEW PERSONAL BEST! ABSOLUTELY INSANE!",
    },
}

# ---------------------------------------------------------------------------
# Daily target hit (1 type)
# ---------------------------------------------------------------------------

TARGET_HIT_TEXTS = {
    "literal": {
        "target_hit": "You have reached your daily burn point target.",
    },
    "standard": {
        "target_hit": "Daily target reached! Everything from here is bonus.",
    },
    "hype": {
        "target_hit": "TARGET HIT! Now let's see how far BEYOND you can go!",
    },
}

# ---------------------------------------------------------------------------
# Mid-workout encouragement (5 variations)
# ---------------------------------------------------------------------------

ENCOURAGE_TEXTS = {
    "literal": {
        "encourage_1": "You are doing well. Keep going.",
        "encourage_2": "Good effort. Stay consistent.",
        "encourage_3": "Your heart rate is in a good zone.",
        "encourage_4": "You are making progress. Keep it up.",
        "encourage_5": "Steady pace. Points are accumulating.",
    },
    "standard": {
        "encourage_1": "Looking strong! Keep pushing.",
        "encourage_2": "You're in the groove. Great rhythm.",
        "encourage_3": "This is where the magic happens. Stay with it.",
        "encourage_4": "Fantastic effort! You've got this.",
        "encourage_5": "Every second counts. Keep going!",
    },
    "hype": {
        "encourage_1": "YES! You are KILLING IT right now!",
        "encourage_2": "Don't stop! The ENERGY is UNREAL!",
        "encourage_3": "LOOK AT YOU GO! Absolutely SMASHING it!",
        "encourage_4": "You are a MACHINE! Keep that pace!",
        "encourage_5": "THIS is what CHAMPIONS do! KEEP GOING!",
    },
}

# ---------------------------------------------------------------------------
# Streak celebrations (8 milestones)
# ---------------------------------------------------------------------------

STREAK_TEXTS = {
    "literal": {
        "streak_3":   "You have a 3 day streak.",
        "streak_5":   "You have a 5 day streak.",
        "streak_7":   "You have a 7 day streak. That is one full week.",
        "streak_10":  "You have a 10 day streak.",
        "streak_14":  "You have a 14 day streak. That is two weeks.",
        "streak_21":  "You have a 21 day streak. That is three weeks.",
        "streak_30":  "You have a 30 day streak. That is one month.",
        "streak_100": "You have a 100 day streak.",
    },
    "standard": {
        "streak_3":   "3 days in a row! You're building a habit.",
        "streak_5":   "5 day streak! Consistency is your superpower.",
        "streak_7":   "One full week! You're on a roll.",
        "streak_10":  "10 days strong! Double digits!",
        "streak_14":  "Two weeks straight! That's dedication.",
        "streak_21":  "21 days! They say that's what it takes to build a habit.",
        "streak_30":  "30 day streak! A full month of commitment!",
        "streak_100": "100 days! You are truly extraordinary.",
    },
    "hype": {
        "streak_3":   "THREE DAYS! The streak is ALIVE!",
        "streak_5":   "FIVE DAYS! You are on FIRE!",
        "streak_7":   "ONE WEEK STRAIGHT! UNSTOPPABLE!",
        "streak_10":  "TEN DAYS! DOUBLE DIGITS BABY!",
        "streak_14":  "TWO WEEKS! You are a WARRIOR!",
        "streak_21":  "TWENTY ONE DAYS! HABIT FORMED! LEGEND!",
        "streak_30":  "THIRTY DAYS! ONE MONTH! You are ELITE!",
        "streak_100": "ONE HUNDRED DAYS! You are ABSOLUTELY LEGENDARY!",
    },
}

# ---------------------------------------------------------------------------
# Comeback / Return (3 types)
# ---------------------------------------------------------------------------

RETURN_TEXTS = {
    "literal": {
        "return_1day": "Welcome back. Ready to begin.",
        "return_few":  "Welcome back. Starting fresh.",
        "return_long": "Welcome back. Every workout counts.",
    },
    "standard": {
        "return_1day": "Good to see you! Let's get moving.",
        "return_few":  "Welcome back! Ready when you are.",
        "return_long": "Great to have you back. Let's do this.",
    },
    "hype": {
        "return_1day": "You're BACK! Let's make today count!",
        "return_few":  "WELCOME BACK! Time to get after it!",
        "return_long": "The RETURN! Let's show up TODAY!",
    },
}

# ---------------------------------------------------------------------------
# Progress callouts (3 types)
# ---------------------------------------------------------------------------

PROGRESS_TEXTS = {
    "literal": {
        "progress_week_great": "You have done 5 or more workouts this week.",
        "progress_total_10":   "You have completed 10 workouts total.",
        "progress_total_50":   "You have completed 50 workouts total.",
    },
    "standard": {
        "progress_week_great": "What a week! 5 plus workouts and counting.",
        "progress_total_10":   "10 workouts in the books! You're committed.",
        "progress_total_50":   "50 workouts! That is a serious milestone.",
    },
    "hype": {
        "progress_week_great": "FIVE WORKOUTS THIS WEEK! You are a MACHINE!",
        "progress_total_10":   "TEN WORKOUTS! Double digits! THE GRIND IS REAL!",
        "progress_total_50":   "FIFTY WORKOUTS! HALF A HUNDRED! ABSOLUTE LEGEND!",
    },
}

# ---------------------------------------------------------------------------
# Station change (3 stations)
# ---------------------------------------------------------------------------

STATION_TEXTS = {
    "literal": {
        "station_tread": "Moving to the tread station.",
        "station_row":   "Moving to the row station.",
        "station_floor": "Moving to the floor station.",
    },
    "standard": {
        "station_tread": "Time to switch! Heading to the tread.",
        "station_row":   "Time to switch! Heading to the row.",
        "station_floor": "Time to switch! Heading to the floor.",
    },
    "hype": {
        "station_tread": "TREAD TIME! Let's GET AFTER IT!",
        "station_row":   "ROW TIME! Let's GET AFTER IT!",
        "station_floor": "FLOOR TIME! Let's GET AFTER IT!",
    },
}

# ---------------------------------------------------------------------------
# Exercise announcements (20 exercises)
# ---------------------------------------------------------------------------

EXERCISE_TEXTS = {
    "literal": {
        "exercise_base_pace":        "Next: Base Pace.",
        "exercise_push_pace":        "Next: Push Pace.",
        "exercise_all_out":          "Next: All-Out Sprint.",
        "exercise_power_walk":       "Next: Power Walk.",
        "exercise_incline":          "Next: Incline Walk.",
        "exercise_steady_row":       "Next: Steady Row.",
        "exercise_power_row":        "Next: Power Row.",
        "exercise_all_out_row":      "Next: All-Out Row.",
        "exercise_squats":           "Next: Squats.",
        "exercise_lunges":           "Next: Lunges.",
        "exercise_deadlifts":        "Next: Deadlifts.",
        "exercise_chest_press":      "Next: Chest Press.",
        "exercise_shoulder_press":   "Next: Shoulder Press.",
        "exercise_bicep_curls":      "Next: Bicep Curls.",
        "exercise_tricep_extensions":"Next: Tricep Extensions.",
        "exercise_push_ups":         "Next: Push-Ups.",
        "exercise_plank":            "Next: Plank.",
        "exercise_trx_rows":         "Next: TRX Rows.",
        "exercise_bench_hop_overs":  "Next: Bench Hop-Overs.",
        "exercise_pop_squats":       "Next: Pop Squats.",
    },
    "standard": {
        "exercise_base_pace":        "Base Pace is next. Let's go.",
        "exercise_push_pace":        "Push Pace is next. Let's go.",
        "exercise_all_out":          "All-Out Sprint is next. Let's go.",
        "exercise_power_walk":       "Power Walk is next. Let's go.",
        "exercise_incline":          "Incline Walk is next. Let's go.",
        "exercise_steady_row":       "Steady Row is next. Let's go.",
        "exercise_power_row":        "Power Row is next. Let's go.",
        "exercise_all_out_row":      "All-Out Row is next. Let's go.",
        "exercise_squats":           "Squats are next. Let's go.",
        "exercise_lunges":           "Lunges are next. Let's go.",
        "exercise_deadlifts":        "Deadlifts are next. Let's go.",
        "exercise_chest_press":      "Chest Press is next. Let's go.",
        "exercise_shoulder_press":   "Shoulder Press is next. Let's go.",
        "exercise_bicep_curls":      "Bicep Curls are next. Let's go.",
        "exercise_tricep_extensions":"Tricep Extensions are next. Let's go.",
        "exercise_push_ups":         "Push-Ups are next. Let's go.",
        "exercise_plank":            "Plank is next. Let's go.",
        "exercise_trx_rows":         "TRX Rows are next. Let's go.",
        "exercise_bench_hop_overs":  "Bench Hop-Overs are next. Let's go.",
        "exercise_pop_squats":       "Pop Squats are next. Let's go.",
    },
    "hype": {
        "exercise_base_pace":        "BASE PACE! LET'S DO THIS!",
        "exercise_push_pace":        "PUSH PACE! LET'S DO THIS!",
        "exercise_all_out":          "ALL-OUT SPRINT! LET'S DO THIS!",
        "exercise_power_walk":       "POWER WALK! LET'S DO THIS!",
        "exercise_incline":          "INCLINE WALK! LET'S DO THIS!",
        "exercise_steady_row":       "STEADY ROW! LET'S DO THIS!",
        "exercise_power_row":        "POWER ROW! LET'S DO THIS!",
        "exercise_all_out_row":      "ALL-OUT ROW! LET'S DO THIS!",
        "exercise_squats":           "SQUATS! LET'S DO THIS!",
        "exercise_lunges":           "LUNGES! LET'S DO THIS!",
        "exercise_deadlifts":        "DEADLIFTS! LET'S DO THIS!",
        "exercise_chest_press":      "CHEST PRESS! LET'S DO THIS!",
        "exercise_shoulder_press":   "SHOULDER PRESS! LET'S DO THIS!",
        "exercise_bicep_curls":      "BICEP CURLS! LET'S DO THIS!",
        "exercise_tricep_extensions":"TRICEP EXTENSIONS! LET'S DO THIS!",
        "exercise_push_ups":         "PUSH-UPS! LET'S DO THIS!",
        "exercise_plank":            "PLANK! LET'S DO THIS!",
        "exercise_trx_rows":         "TRX ROWS! LET'S DO THIS!",
        "exercise_bench_hop_overs":  "BENCH HOP-OVERS! LET'S DO THIS!",
        "exercise_pop_squats":       "POP SQUATS! LET'S DO THIS!",
    },
}

# ===========================================================================
# NEW: Composable coaching clips
# ===========================================================================

# ---------------------------------------------------------------------------
# Speed/incline/watt target fragments
# ---------------------------------------------------------------------------

COACHING_TARGET_TEXTS = {
    "literal": {
        "target_5_mph":       "Minimum 5 miles per hour.",
        "target_6_mph":       "Minimum 6 miles per hour.",
        "target_7_mph":       "Minimum 7 miles per hour.",
        "target_8_mph":       "Minimum 8 miles per hour.",
        "target_9_mph":       "Minimum 9 miles per hour.",
        "target_incline_4":   "Incline to 4 percent.",
        "target_incline_8":   "Incline to 8 percent.",
        "target_incline_10":  "Incline to 10 percent.",
        "target_incline_12":  "Incline to 12 percent.",
        "target_100_watts":   "100 watts.",
        "target_150_watts":   "150 watts.",
        "target_200_watts":   "200 watts.",
    },
    "standard": {
        "target_5_mph":       "Minimum 5 miles per hour.",
        "target_6_mph":       "Minimum 6 miles per hour.",
        "target_7_mph":       "At least 7 miles per hour.",
        "target_8_mph":       "At least 8 miles per hour.",
        "target_9_mph":       "At least 9 miles per hour.",
        "target_incline_4":   "Incline to 4 percent.",
        "target_incline_8":   "Incline to 8 percent.",
        "target_incline_10":  "Incline to 10 percent.",
        "target_incline_12":  "Incline to 12 percent.",
        "target_100_watts":   "100 watts on the rower.",
        "target_150_watts":   "150 watts on the rower.",
        "target_200_watts":   "200 watts, push that power.",
    },
    "hype": {
        "target_5_mph":       "FIVE MILES PER HOUR minimum!",
        "target_6_mph":       "SIX MILES PER HOUR! Let's move!",
        "target_7_mph":       "SEVEN MPH! PICK IT UP!",
        "target_8_mph":       "EIGHT MPH! DIG DEEP!",
        "target_9_mph":       "NINE MPH! LEAVE IT ALL OUT THERE!",
        "target_incline_4":   "INCLINE TO 4! Let's climb!",
        "target_incline_8":   "EIGHT PERCENT INCLINE! FEEL THE BURN!",
        "target_incline_10":  "TEN PERCENT! YOU'VE GOT THIS!",
        "target_incline_12":  "TWELVE PERCENT INCLINE! BEAST MODE!",
        "target_100_watts":   "100 WATTS on that rower!",
        "target_150_watts":   "150 WATTS! POWER!",
        "target_200_watts":   "200 WATTS! EMPTY THE TANK!",
    },
}

# ---------------------------------------------------------------------------
# Duration fragments
# ---------------------------------------------------------------------------

DURATION_TEXTS = {
    "literal": {
        "duration_20_seconds":  "20 seconds.",
        "duration_30_seconds":  "30 seconds.",
        "duration_45_seconds":  "45 seconds.",
        "duration_1_minute":    "1 minute.",
        "duration_2_minutes":   "2 minutes.",
        "duration_3_minutes":   "3 minutes.",
        "duration_4_minutes":   "4 minutes.",
        "duration_5_minutes":   "5 minutes.",
        "duration_10_minutes":  "10 minutes.",
        "duration_15_minutes":  "15 minutes.",
    },
    "standard": {
        "duration_20_seconds":  "20 seconds.",
        "duration_30_seconds":  "30 seconds, you can do anything for 30 seconds.",
        "duration_45_seconds":  "45 seconds.",
        "duration_1_minute":    "1 minute.",
        "duration_2_minutes":   "2 minutes.",
        "duration_3_minutes":   "3 minutes.",
        "duration_4_minutes":   "4 minutes.",
        "duration_5_minutes":   "5 minutes.",
        "duration_10_minutes":  "10 minutes.",
        "duration_15_minutes":  "15 minutes.",
    },
    "hype": {
        "duration_20_seconds":  "TWENTY SECONDS! That's nothing!",
        "duration_30_seconds":  "THIRTY SECONDS! You can do ANYTHING for 30 seconds!",
        "duration_45_seconds":  "45 SECONDS! DIG IN!",
        "duration_1_minute":    "ONE MINUTE! OWN IT!",
        "duration_2_minutes":   "TWO MINUTES! STAY STRONG!",
        "duration_3_minutes":   "THREE MINUTES! LOCK IN!",
        "duration_4_minutes":   "FOUR MINUTES! HOLD THAT PACE!",
        "duration_5_minutes":   "FIVE MINUTES! THIS IS YOUR TIME!",
        "duration_10_minutes":  "TEN MINUTES! SETTLE IN AND GRIND!",
        "duration_15_minutes":  "FIFTEEN MINUTES! BUILD THAT ENGINE!",
    },
}

# ---------------------------------------------------------------------------
# Motivation connectors
# ---------------------------------------------------------------------------

MOTIVATION_TEXTS = {
    "literal": {
        "motivation_lets_go":       "Let's go.",
        "motivation_you_got_this":  "You have got this.",
        "motivation_strong_finish": "Finish strong.",
        "motivation_almost_there":  "Almost there.",
        "motivation_keep_pushing":  "Keep pushing.",
        "motivation_last_one":      "This is the last exercise.",
    },
    "standard": {
        "motivation_lets_go":       "Let's go!",
        "motivation_you_got_this":  "You've got this!",
        "motivation_strong_finish": "Strong finish!",
        "motivation_almost_there":  "Almost there!",
        "motivation_keep_pushing":  "Keep pushing!",
        "motivation_last_one":      "Last one! Make it count!",
    },
    "hype": {
        "motivation_lets_go":       "LET'S GOOO!",
        "motivation_you_got_this":  "YOU'VE GOT THIS!",
        "motivation_strong_finish": "STRONG FINISH! LEAVE IT ALL OUT THERE!",
        "motivation_almost_there":  "ALMOST THERE! DON'T YOU DARE QUIT!",
        "motivation_keep_pushing":  "KEEP PUSHING! THIS IS YOUR MOMENT!",
        "motivation_last_one":      "LAST ONE! GIVE IT EVERYTHING YOU'VE GOT!",
    },
}

# ---------------------------------------------------------------------------
# Push-harder mid-exercise clips
# ---------------------------------------------------------------------------

PUSH_HARDER_TEXTS = {
    "literal": {
        "push_harder_add_1_mph":       "Add 1 mile per hour.",
        "push_harder_add_half_mph":    "Add half a mile per hour.",
        "push_harder_raise_incline_2": "Raise your incline 2 percent.",
        "push_harder_raise_incline_4": "Raise your incline 4 percent.",
        "push_harder_add_20_watts":    "Add 20 watts.",
        "push_harder_add_50_watts":    "Add 50 watts.",
    },
    "standard": {
        "push_harder_add_1_mph":       "Push it, add 1 mile per hour!",
        "push_harder_add_half_mph":    "Add half a mile per hour, you can do it!",
        "push_harder_raise_incline_2": "Raise that incline 2 percent!",
        "push_harder_raise_incline_4": "Raise that incline 4 percent! Dig deep!",
        "push_harder_add_20_watts":    "Add 20 watts, you've got this!",
        "push_harder_add_50_watts":    "Add 50 watts, push that power!",
    },
    "hype": {
        "push_harder_add_1_mph":       "ADD ONE MPH! LET'S GO!",
        "push_harder_add_half_mph":    "HALF A MILE MORE! YOU CAN DO IT!",
        "push_harder_raise_incline_2": "RAISE THAT INCLINE! TWO MORE PERCENT!",
        "push_harder_raise_incline_4": "FOUR MORE PERCENT! FEEL THE CLIMB!",
        "push_harder_add_20_watts":    "TWENTY MORE WATTS! PULL HARDER!",
        "push_harder_add_50_watts":    "FIFTY MORE WATTS! UNLEASH THE POWER!",
    },
}

# ---------------------------------------------------------------------------
# Form cue clips
# ---------------------------------------------------------------------------

FORM_CUE_TEXTS = {
    "literal": {
        "form_chest_up":       "Chest up, drive through your heels.",
        "form_drive_heels":    "Front knee over ankle, control the drop.",
        "form_full_range":     "Core tight, full range of motion.",
        "form_control_tempo":  "Control the tempo.",
        "form_squeeze_top":    "Squeeze at the top.",
        "form_breathe":        "Remember to breathe.",
    },
    "standard": {
        "form_chest_up":       "Chest up, drive through your heels.",
        "form_drive_heels":    "Keep that front knee over your ankle.",
        "form_full_range":     "Core tight, full range of motion!",
        "form_control_tempo":  "Control the tempo, slow and controlled.",
        "form_squeeze_top":    "Squeeze at the top!",
        "form_breathe":        "Don't forget to breathe!",
    },
    "hype": {
        "form_chest_up":       "CHEST UP! Drive through those HEELS!",
        "form_drive_heels":    "KNEE OVER ANKLE! CONTROL IT!",
        "form_full_range":     "CORE TIGHT! FULL RANGE! EVERY REP COUNTS!",
        "form_control_tempo":  "CONTROL THAT TEMPO! SLOW AND POWERFUL!",
        "form_squeeze_top":    "SQUEEZE IT AT THE TOP!",
        "form_breathe":        "BREATHE! POWER THROUGH!",
    },
}

# ---------------------------------------------------------------------------
# Prefix / connector clips
# ---------------------------------------------------------------------------

PREFIX_TEXTS = {
    "literal": {
        "prefix_minimum":   "Minimum.",
        "prefix_next_up":   "Next up.",
        "prefix_moving_to": "Moving to.",
        "prefix_for":       "For.",
    },
    "standard": {
        "prefix_minimum":   "Minimum.",
        "prefix_next_up":   "Next up!",
        "prefix_moving_to": "Moving to.",
        "prefix_for":       "For.",
    },
    "hype": {
        "prefix_minimum":   "MINIMUM!",
        "prefix_next_up":   "NEXT UP!",
        "prefix_moving_to": "MOVING TO!",
        "prefix_for":       "FOR!",
    },
}

# Collect all keyed clip categories for iteration
KEYED_CATEGORIES = [
    START_TEXTS,
    COMPLETE_TEXTS,
    TARGET_HIT_TEXTS,
    ENCOURAGE_TEXTS,
    STREAK_TEXTS,
    RETURN_TEXTS,
    PROGRESS_TEXTS,
    STATION_TEXTS,
    EXERCISE_TEXTS,
    # New composable coaching categories
    COACHING_TARGET_TEXTS,
    DURATION_TEXTS,
    MOTIVATION_TEXTS,
    PUSH_HARDER_TEXTS,
    FORM_CUE_TEXTS,
    PREFIX_TEXTS,
]


def generate_clip(text: str, voice_id: str, settings: VoiceSettings, out_path: Path):
    """Generate a single MP3 clip via ElevenLabs API."""
    if out_path.exists() and out_path.stat().st_size > 0:
        print(f"  SKIP (exists): {out_path.name}")
        return

    print(f"  Generating: {out_path.name}  \"{text}\"")
    audio_gen = client.text_to_speech.convert(
        text=text,
        voice_id=voice_id,
        model_id="eleven_multilingual_v2",
        voice_settings=settings,
    )
    audio_data = b"".join(audio_gen)

    out_path.write_bytes(audio_data)
    size_kb = len(audio_data) / 1024
    print(f"    -> {size_kb:.1f} KB")


def main():
    total = 0
    skipped = 0

    for style in ("literal", "standard", "hype"):
        voice_id = VOICES[style]
        settings = VOICE_SETTINGS[style]
        print(f"\n=== Style: {style} (voice {voice_id}) ===")

        # Zone clips
        for zone in ZONES:
            text = ZONE_TEXTS[style][zone]
            fname = f"voice_{style}_zone_{zone}.mp3"
            out_path = OUTPUT_DIR / fname
            if out_path.exists() and out_path.stat().st_size > 0:
                skipped += 1
            generate_clip(text, voice_id, settings, out_path)
            total += 1

        # Time clips
        for minutes in TIME_INTERVALS:
            text = TIME_TEXTS[style](minutes)
            fname = f"voice_{style}_time_{minutes}.mp3"
            out_path = OUTPUT_DIR / fname
            if out_path.exists() and out_path.stat().st_size > 0:
                skipped += 1
            generate_clip(text, voice_id, settings, out_path)
            total += 1

        # Keyed categories (all dictionaries)
        for category in KEYED_CATEGORIES:
            for clip_key, text in category[style].items():
                fname = f"voice_{style}_{clip_key}.mp3"
                out_path = OUTPUT_DIR / fname
                if out_path.exists() and out_path.stat().st_size > 0:
                    skipped += 1
                generate_clip(text, voice_id, settings, out_path)
                total += 1

    print(f"\nDone! {total} clips processed ({skipped} already existed).")
    print(f"Output: {OUTPUT_DIR}")


if __name__ == "__main__":
    main()
