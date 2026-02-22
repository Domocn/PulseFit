#!/usr/bin/env python3
"""
Pre-generate ElevenLabs voice coach clips for PulseFit offline playback.

Generates 129 MP3 files:
  - 15 zone-change clips (3 styles x 5 zones)
  - 36 time-update clips (3 styles x 12 intervals: 5..60 min)
  - 9 workout start greetings (3 styles x 3 types)
  - 9 workout complete clips (3 styles x 3 types)
  - 3 daily target hit clips (3 styles x 1)
  - 15 mid-workout encouragement clips (3 styles x 5)
  - 24 streak celebration clips (3 styles x 8 milestones)
  - 9 comeback/return clips (3 styles x 3 types)
  - 9 progress callout clips (3 styles x 3 types)

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

# Collect all keyed clip categories for iteration
KEYED_CATEGORIES = [
    START_TEXTS,
    COMPLETE_TEXTS,
    TARGET_HIT_TEXTS,
    ENCOURAGE_TEXTS,
    STREAK_TEXTS,
    RETURN_TEXTS,
    PROGRESS_TEXTS,
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

        # Keyed categories (start, complete, target, encourage, streak, return, progress)
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
