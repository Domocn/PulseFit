"""
PulseFit Backend - Heart Rate Zone Training API
Full Feature Implementation with ElevenLabs Voice
"""
from fastapi import FastAPI, HTTPException, Response
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import StreamingResponse
from pydantic import BaseModel, Field
from typing import Optional, List, Dict, Any
from datetime import datetime, timezone, timedelta
from motor.motor_asyncio import AsyncIOMotorClient
from bson import ObjectId
from elevenlabs import ElevenLabs, VoiceSettings
from dotenv import load_dotenv
import os
import uuid
import random
import json
import io
import csv
import base64

# Load environment variables
load_dotenv()

app = FastAPI(title="PulseFit API", version="2.1.0")

# ElevenLabs client
ELEVENLABS_API_KEY = os.environ.get("ELEVENLABS_API_KEY")
eleven_client = ElevenLabs(api_key=ELEVENLABS_API_KEY) if ELEVENLABS_API_KEY else None

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# MongoDB connection
MONGO_URL = os.environ.get("MONGO_URL")
DB_NAME = os.environ.get("DB_NAME", "pulsefit")
client = AsyncIOMotorClient(MONGO_URL)
db = client[DB_NAME]

# Collections
users_collection = db["users"]
workouts_collection = db["workouts"]
settings_collection = db["settings"]
templates_collection = db["templates"]
achievements_collection = db["achievements"]
personal_bests_collection = db["personal_bests"]


# ===================== Models =====================

class UserCreate(BaseModel):
    name: str
    age: int
    weight_kg: float
    height_cm: float
    resting_hr: int = 60
    max_hr: Optional[int] = None
    daily_burn_target: int = 12
    nd_mode: str = "standard"


class UserResponse(BaseModel):
    id: str
    name: str
    age: int
    weight_kg: float
    height_cm: float
    resting_hr: int
    max_hr: int
    daily_burn_target: int
    nd_mode: str
    xp: int
    level: int
    streak_days: int
    streak_freezes_available: int
    last_workout_date: Optional[str]
    total_burn_points: int
    total_workouts: int
    created_at: str


class UserUpdate(BaseModel):
    name: Optional[str] = None
    age: Optional[int] = None
    weight_kg: Optional[float] = None
    height_cm: Optional[float] = None
    resting_hr: Optional[int] = None
    max_hr: Optional[int] = None
    daily_burn_target: Optional[int] = None
    nd_mode: Optional[str] = None


class SensorySettings(BaseModel):
    animation_level: int = Field(default=100, ge=0, le=100)
    sound_level: int = Field(default=100, ge=0, le=100)
    haptic_level: int = Field(default=100, ge=0, le=100)
    transition_warnings: bool = True
    literal_labels: bool = False


class VoiceCoachSettings(BaseModel):
    enabled: bool = True
    voice_selection: str = "default"
    speech_rate: str = "normal"  # slow, normal, fast
    volume: int = 80
    min_gap_seconds: int = 15
    zone_callouts: bool = True
    point_updates: str = "every_3"  # every, every_3, target_only, off
    interval_countdown: bool = True
    encouragement: str = "occasional"  # frequent, occasional, off
    pacing_warnings: bool = True


class SettingsUpdate(BaseModel):
    sensory: Optional[SensorySettings] = None
    voice_coach: Optional[VoiceCoachSettings] = None
    dark_mode: bool = True
    high_contrast: bool = False
    colorblind_mode: bool = False
    font_size: str = "medium"  # small, medium, large


class ZoneSettings(BaseModel):
    zone_1_max: int = 60  # Rest
    zone_2_max: int = 70  # Warm-Up
    zone_3_max: int = 84  # Active
    zone_4_max: int = 92  # Push
    use_karvonen: bool = False


class WorkoutSegment(BaseModel):
    name: str
    duration_seconds: int
    target_zone: int
    description: Optional[str] = None


class WorkoutTemplate(BaseModel):
    name: str
    description: str
    difficulty: str  # easy, moderate, hard
    duration_minutes: int
    segments: List[WorkoutSegment]
    estimated_burn_points: int
    category: str  # hiit, endurance, recovery, interval


class WorkoutCreate(BaseModel):
    user_id: str
    hr_samples: List[dict]
    duration_seconds: int
    notes: Optional[str] = None
    template_id: Optional[str] = None


class ZoneSummary(BaseModel):
    zone: int
    name: str
    duration_seconds: int
    burn_points: int
    color: str


class WorkoutResponse(BaseModel):
    id: str
    user_id: str
    start_time: str
    end_time: str
    duration_seconds: int
    total_burn_points: int
    zones: List[ZoneSummary]
    avg_hr: int
    max_hr: int
    calories_burned: int
    afterburn_estimate: int
    target_hit: bool
    xp_earned: int
    notes: Optional[str]
    template_name: Optional[str] = None


class Achievement(BaseModel):
    id: str
    name: str
    description: str
    icon: str
    category: str
    requirement: Dict[str, Any]
    xp_reward: int
    unlocked: bool = False
    unlocked_at: Optional[str] = None


class HRSimulationRequest(BaseModel):
    user_id: str
    intensity: str = "moderate"
    target_zone: Optional[int] = None


class CustomTemplateCreate(BaseModel):
    user_id: str
    name: str
    description: Optional[str] = ""
    segments: List[WorkoutSegment]


# ===================== Zone Engine =====================

ZONES = {
    0: {"name": "Below", "min_pct": 0, "max_pct": 50, "points": 0, "color": "#71717a"},
    1: {"name": "Rest", "min_pct": 50, "max_pct": 60, "points": 0, "color": "#71717a"},
    2: {"name": "Warm-Up", "min_pct": 60, "max_pct": 70, "points": 0, "color": "#3b82f6"},
    3: {"name": "Active", "min_pct": 70, "max_pct": 84, "points": 1, "color": "#22c55e"},
    4: {"name": "Push", "min_pct": 84, "max_pct": 92, "points": 2, "color": "#f97316"},
    5: {"name": "Peak", "min_pct": 92, "max_pct": 100, "points": 3, "color": "#ef4444"},
}

# Pre-built Workout Templates
BUILT_IN_TEMPLATES = [
    {
        "id": "hiit_blast",
        "name": "HIIT Blast",
        "description": "High-intensity intervals for maximum calorie burn",
        "difficulty": "hard",
        "duration_minutes": 20,
        "category": "hiit",
        "estimated_burn_points": 25,
        "segments": [
            {"name": "Warm-Up", "duration_seconds": 180, "target_zone": 2, "description": "Light movement"},
            {"name": "Sprint 1", "duration_seconds": 60, "target_zone": 5, "description": "All out effort!"},
            {"name": "Recover", "duration_seconds": 90, "target_zone": 3, "description": "Active recovery"},
            {"name": "Sprint 2", "duration_seconds": 60, "target_zone": 5, "description": "Push hard!"},
            {"name": "Recover", "duration_seconds": 90, "target_zone": 3, "description": "Keep moving"},
            {"name": "Sprint 3", "duration_seconds": 60, "target_zone": 5, "description": "You got this!"},
            {"name": "Recover", "duration_seconds": 90, "target_zone": 3, "description": "Breathe"},
            {"name": "Sprint 4", "duration_seconds": 60, "target_zone": 5, "description": "Final push!"},
            {"name": "Cool Down", "duration_seconds": 240, "target_zone": 2, "description": "Slow it down"}
        ]
    },
    {
        "id": "endurance_builder",
        "name": "Endurance Builder",
        "description": "Steady-state cardio to build aerobic base",
        "difficulty": "moderate",
        "duration_minutes": 30,
        "category": "endurance",
        "estimated_burn_points": 20,
        "segments": [
            {"name": "Warm-Up", "duration_seconds": 300, "target_zone": 2, "description": "Easy pace"},
            {"name": "Build", "duration_seconds": 300, "target_zone": 3, "description": "Find your rhythm"},
            {"name": "Sustain", "duration_seconds": 600, "target_zone": 4, "description": "Hold this pace"},
            {"name": "Push", "duration_seconds": 300, "target_zone": 4, "description": "Slight increase"},
            {"name": "Cool Down", "duration_seconds": 300, "target_zone": 2, "description": "Wind down"}
        ]
    },
    {
        "id": "interval_burner",
        "name": "30-Min Interval Burner",
        "description": "Classic interval training with push and recovery phases",
        "difficulty": "moderate",
        "duration_minutes": 30,
        "category": "interval",
        "estimated_burn_points": 22,
        "segments": [
            {"name": "Warm-Up", "duration_seconds": 300, "target_zone": 2, "description": "Get ready"},
            {"name": "Push Block", "duration_seconds": 240, "target_zone": 4, "description": "Push pace"},
            {"name": "Active Recovery", "duration_seconds": 120, "target_zone": 3, "description": "Recover"},
            {"name": "Push Block", "duration_seconds": 240, "target_zone": 4, "description": "Push again"},
            {"name": "Active Recovery", "duration_seconds": 120, "target_zone": 3, "description": "Recover"},
            {"name": "Peak Effort", "duration_seconds": 120, "target_zone": 5, "description": "Max effort!"},
            {"name": "Active Recovery", "duration_seconds": 120, "target_zone": 3, "description": "Breathe"},
            {"name": "Push Block", "duration_seconds": 240, "target_zone": 4, "description": "Final push block"},
            {"name": "Peak Effort", "duration_seconds": 60, "target_zone": 5, "description": "Finish strong!"},
            {"name": "Cool Down", "duration_seconds": 240, "target_zone": 2, "description": "Great job!"}
        ]
    },
    {
        "id": "easy_recovery",
        "name": "Easy Recovery",
        "description": "Light movement for active recovery days",
        "difficulty": "easy",
        "duration_minutes": 20,
        "category": "recovery",
        "estimated_burn_points": 8,
        "segments": [
            {"name": "Gentle Start", "duration_seconds": 300, "target_zone": 1, "description": "Very easy"},
            {"name": "Light Movement", "duration_seconds": 600, "target_zone": 2, "description": "Stay relaxed"},
            {"name": "Easy Pace", "duration_seconds": 300, "target_zone": 3, "description": "Comfortable effort"},
            {"name": "Wind Down", "duration_seconds": 300, "target_zone": 2, "description": "Slow down"}
        ]
    },
    {
        "id": "peak_chaser",
        "name": "Peak Chaser",
        "description": "Push your limits with sustained high-intensity work",
        "difficulty": "hard",
        "duration_minutes": 25,
        "category": "hiit",
        "estimated_burn_points": 30,
        "segments": [
            {"name": "Warm-Up", "duration_seconds": 300, "target_zone": 2, "description": "Prepare yourself"},
            {"name": "Build Up", "duration_seconds": 180, "target_zone": 3, "description": "Building intensity"},
            {"name": "Push", "duration_seconds": 180, "target_zone": 4, "description": "Getting hot"},
            {"name": "Peak Zone", "duration_seconds": 120, "target_zone": 5, "description": "Max effort!"},
            {"name": "Brief Recovery", "duration_seconds": 60, "target_zone": 3, "description": "Quick breath"},
            {"name": "Peak Zone", "duration_seconds": 120, "target_zone": 5, "description": "Push through!"},
            {"name": "Brief Recovery", "duration_seconds": 60, "target_zone": 3, "description": "Almost there"},
            {"name": "Final Peak", "duration_seconds": 180, "target_zone": 5, "description": "Give it all!"},
            {"name": "Cool Down", "duration_seconds": 300, "target_zone": 2, "description": "You crushed it!"}
        ]
    },
    {
        "id": "quick_burn",
        "name": "Quick 15-Min Burn",
        "description": "Short but effective workout when time is limited",
        "difficulty": "moderate",
        "duration_minutes": 15,
        "category": "interval",
        "estimated_burn_points": 12,
        "segments": [
            {"name": "Quick Warm-Up", "duration_seconds": 120, "target_zone": 2, "description": "Get moving"},
            {"name": "Push", "duration_seconds": 180, "target_zone": 4, "description": "Work hard"},
            {"name": "Recover", "duration_seconds": 60, "target_zone": 3, "description": "Brief rest"},
            {"name": "Push", "duration_seconds": 180, "target_zone": 4, "description": "Keep going"},
            {"name": "Peak", "duration_seconds": 60, "target_zone": 5, "description": "All out!"},
            {"name": "Recover", "duration_seconds": 60, "target_zone": 3, "description": "Breathe"},
            {"name": "Final Push", "duration_seconds": 120, "target_zone": 4, "description": "Finish strong"},
            {"name": "Cool Down", "duration_seconds": 120, "target_zone": 2, "description": "Done!"}
        ]
    }
]

# Achievement Definitions
ACHIEVEMENTS = [
    {"id": "first_workout", "name": "First Steps", "description": "Complete your first workout", "icon": "trophy", "category": "milestone", "requirement": {"workouts": 1}, "xp_reward": 50},
    {"id": "streak_7", "name": "Week Warrior", "description": "Maintain a 7-day streak", "icon": "flame", "category": "streak", "requirement": {"streak": 7}, "xp_reward": 100},
    {"id": "streak_30", "name": "Monthly Master", "description": "Maintain a 30-day streak", "icon": "fire", "category": "streak", "requirement": {"streak": 30}, "xp_reward": 300},
    {"id": "streak_100", "name": "Centurion", "description": "Maintain a 100-day streak", "icon": "crown", "category": "streak", "requirement": {"streak": 100}, "xp_reward": 1000},
    {"id": "points_1000", "name": "Burn Baby Burn", "description": "Earn 1,000 lifetime Burn Points", "icon": "zap", "category": "points", "requirement": {"total_points": 1000}, "xp_reward": 200},
    {"id": "points_10000", "name": "Inferno", "description": "Earn 10,000 lifetime Burn Points", "icon": "flame", "category": "points", "requirement": {"total_points": 10000}, "xp_reward": 500},
    {"id": "points_50000", "name": "Legendary Burner", "description": "Earn 50,000 lifetime Burn Points", "icon": "star", "category": "points", "requirement": {"total_points": 50000}, "xp_reward": 2000},
    {"id": "peak_20", "name": "Peak Performer", "description": "Spend 20+ minutes in Peak zone in one session", "icon": "mountain", "category": "workout", "requirement": {"peak_minutes": 20}, "xp_reward": 150},
    {"id": "early_bird", "name": "Early Bird", "description": "Complete a workout before 7 AM", "icon": "sunrise", "category": "time", "requirement": {"before_hour": 7}, "xp_reward": 75},
    {"id": "night_owl", "name": "Night Owl", "description": "Complete a workout after 9 PM", "icon": "moon", "category": "time", "requirement": {"after_hour": 21}, "xp_reward": 75},
    {"id": "century_club", "name": "Century Club", "description": "Log 100 workouts", "icon": "medal", "category": "milestone", "requirement": {"workouts": 100}, "xp_reward": 500},
    {"id": "perfect_week", "name": "Perfect Week", "description": "Hit your daily target 7 days in a row", "icon": "check-circle", "category": "consistency", "requirement": {"target_streak": 7}, "xp_reward": 200},
    {"id": "zone_master", "name": "Zone Master", "description": "Spend time in all 5 zones in one workout", "icon": "layers", "category": "workout", "requirement": {"all_zones": True}, "xp_reward": 100},
    {"id": "marathon_session", "name": "Marathon Session", "description": "Complete a 60+ minute workout", "icon": "clock", "category": "workout", "requirement": {"duration_minutes": 60}, "xp_reward": 150},
]


def get_zone(heart_rate: int, max_hr: int) -> int:
    if max_hr <= 0:
        return 0
    pct = (heart_rate / max_hr) * 100
    if pct < 50:
        return 0
    elif pct < 60:
        return 1
    elif pct < 70:
        return 2
    elif pct < 84:
        return 3
    elif pct < 92:
        return 4
    else:
        return 5


def calculate_burn_points(hr_samples: List[dict], max_hr: int) -> tuple:
    zone_seconds = {i: 0 for i in range(6)}
    total_hr = 0
    peak_hr = 0
    
    for i, sample in enumerate(hr_samples):
        hr = sample.get("heart_rate", 0)
        total_hr += hr
        peak_hr = max(peak_hr, hr)
        zone = get_zone(hr, max_hr)
        
        duration = 1
        if i < len(hr_samples) - 1:
            try:
                t1 = datetime.fromisoformat(sample["timestamp"].replace("Z", "+00:00"))
                t2 = datetime.fromisoformat(hr_samples[i+1]["timestamp"].replace("Z", "+00:00"))
                duration = max(1, int((t2 - t1).total_seconds()))
            except:
                duration = 1
        zone_seconds[zone] += duration
    
    total_points = 0
    zone_summaries = []
    
    for zone_id in range(1, 6):
        zone_info = ZONES[zone_id]
        minutes = zone_seconds[zone_id] // 60
        points = minutes * zone_info["points"]
        total_points += points
        
        zone_summaries.append({
            "zone": zone_id,
            "name": zone_info["name"],
            "duration_seconds": zone_seconds[zone_id],
            "burn_points": points,
            "color": zone_info["color"]
        })
    
    avg_hr = total_hr // len(hr_samples) if hr_samples else 0
    return total_points, zone_summaries, avg_hr, peak_hr


def calculate_calories(duration_seconds: int, avg_hr: int, weight_kg: float, age: int) -> int:
    duration_min = duration_seconds / 60
    calories = ((age * 0.2017) + (weight_kg * 0.09036) + (avg_hr * 0.6309) - 55.0969) * duration_min / 4.184
    return max(0, int(calories))


def calculate_afterburn(zone_summaries: List[dict], weight_kg: float) -> int:
    zone_4_min = 0
    zone_5_min = 0
    for z in zone_summaries:
        if z["zone"] == 4:
            zone_4_min = z["duration_seconds"] / 60
        elif z["zone"] == 5:
            zone_5_min = z["duration_seconds"] / 60
    
    afterburn = (zone_4_min * 1.5 + zone_5_min * 2.5) * weight_kg * 0.05
    return int(afterburn)


def calculate_level(xp: int) -> int:
    return (xp // 100) + 1


async def check_achievements(user_id: str, workout: dict = None):
    """Check and unlock achievements for a user"""
    user = await users_collection.find_one({"_id": ObjectId(user_id)})
    if not user:
        return []
    
    unlocked = []
    user_achievements = await achievements_collection.find({"user_id": user_id}).to_list(None)
    unlocked_ids = {a["achievement_id"] for a in user_achievements}
    
    for achievement in ACHIEVEMENTS:
        if achievement["id"] in unlocked_ids:
            continue
            
        req = achievement["requirement"]
        earned = False
        
        # Check different requirement types
        if "workouts" in req and user.get("total_workouts", 0) >= req["workouts"]:
            earned = True
        elif "streak" in req and user.get("streak_days", 0) >= req["streak"]:
            earned = True
        elif "total_points" in req and user.get("total_burn_points", 0) >= req["total_points"]:
            earned = True
        elif workout:
            if "peak_minutes" in req:
                peak_zone = next((z for z in workout.get("zones", []) if z["zone"] == 5), None)
                if peak_zone and peak_zone["duration_seconds"] >= req["peak_minutes"] * 60:
                    earned = True
            elif "duration_minutes" in req and workout.get("duration_seconds", 0) >= req["duration_minutes"] * 60:
                earned = True
            elif "all_zones" in req:
                zones_hit = [z for z in workout.get("zones", []) if z["duration_seconds"] > 0]
                if len(zones_hit) >= 5:
                    earned = True
            elif "before_hour" in req:
                workout_hour = datetime.fromisoformat(workout["end_time"].replace("Z", "+00:00")).hour
                if workout_hour < req["before_hour"]:
                    earned = True
            elif "after_hour" in req:
                workout_hour = datetime.fromisoformat(workout["end_time"].replace("Z", "+00:00")).hour
                if workout_hour >= req["after_hour"]:
                    earned = True
        
        if earned:
            await achievements_collection.insert_one({
                "user_id": user_id,
                "achievement_id": achievement["id"],
                "unlocked_at": datetime.now(timezone.utc).isoformat()
            })
            # Add XP
            await users_collection.update_one(
                {"_id": ObjectId(user_id)},
                {"$inc": {"xp": achievement["xp_reward"]}}
            )
            unlocked.append(achievement)
    
    return unlocked


async def update_personal_bests(user_id: str, workout: dict):
    """Update personal bests after a workout"""
    pb = await personal_bests_collection.find_one({"user_id": user_id})
    
    updates = {}
    workout_points = workout.get("total_burn_points", 0)
    workout_duration = workout.get("duration_seconds", 0)
    peak_seconds = next((z["duration_seconds"] for z in workout.get("zones", []) if z["zone"] == 5), 0)
    
    if not pb:
        pb = {"user_id": user_id}
    
    if workout_points > pb.get("max_session_points", 0):
        updates["max_session_points"] = workout_points
        updates["max_session_points_date"] = workout["end_time"]
    
    if workout_duration > pb.get("longest_workout_seconds", 0):
        updates["longest_workout_seconds"] = workout_duration
        updates["longest_workout_date"] = workout["end_time"]
    
    if peak_seconds > pb.get("longest_peak_seconds", 0):
        updates["longest_peak_seconds"] = peak_seconds
        updates["longest_peak_date"] = workout["end_time"]
    
    if updates:
        await personal_bests_collection.update_one(
            {"user_id": user_id},
            {"$set": updates},
            upsert=True
        )
    
    return updates


# ===================== API Endpoints =====================

@app.get("/api/health")
async def health_check():
    return {"status": "healthy", "app": "PulseFit", "version": "2.0.0"}


# ----- User Endpoints -----

@app.post("/api/users", response_model=UserResponse)
async def create_user(user: UserCreate):
    max_hr = user.max_hr or (220 - user.age)
    
    user_doc = {
        "name": user.name,
        "age": user.age,
        "weight_kg": user.weight_kg,
        "height_cm": user.height_cm,
        "resting_hr": user.resting_hr,
        "max_hr": max_hr,
        "daily_burn_target": user.daily_burn_target,
        "nd_mode": user.nd_mode,
        "xp": 0,
        "level": 1,
        "streak_days": 0,
        "streak_freezes_available": 1,
        "last_workout_date": None,
        "total_burn_points": 0,
        "total_workouts": 0,
        "created_at": datetime.now(timezone.utc).isoformat()
    }
    
    result = await users_collection.insert_one(user_doc)
    user_doc["id"] = str(result.inserted_id)
    return UserResponse(**user_doc)


@app.get("/api/users/{user_id}", response_model=UserResponse)
async def get_user(user_id: str):
    try:
        user = await users_collection.find_one({"_id": ObjectId(user_id)})
    except:
        raise HTTPException(status_code=400, detail="Invalid user ID format")
    
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    user["id"] = str(user["_id"])
    # Ensure all fields exist with defaults
    user.setdefault("streak_freezes_available", 1)
    user.setdefault("total_burn_points", 0)
    user.setdefault("total_workouts", 0)
    return UserResponse(**user)


@app.patch("/api/users/{user_id}", response_model=UserResponse)
async def update_user(user_id: str, update: UserUpdate):
    update_data = {k: v for k, v in update.dict().items() if v is not None}
    
    if not update_data:
        raise HTTPException(status_code=400, detail="No fields to update")
    
    if "age" in update_data and "max_hr" not in update_data:
        update_data["max_hr"] = 220 - update_data["age"]
    
    try:
        result = await users_collection.update_one(
            {"_id": ObjectId(user_id)},
            {"$set": update_data}
        )
    except:
        raise HTTPException(status_code=400, detail="Invalid user ID format")
    
    if result.modified_count == 0:
        raise HTTPException(status_code=404, detail="User not found")
    
    return await get_user(user_id)


@app.post("/api/users/{user_id}/use-streak-freeze")
async def use_streak_freeze(user_id: str):
    """Use a streak freeze to preserve streak"""
    user = await users_collection.find_one({"_id": ObjectId(user_id)})
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    freezes = user.get("streak_freezes_available", 0)
    if freezes <= 0:
        raise HTTPException(status_code=400, detail="No streak freezes available")
    
    await users_collection.update_one(
        {"_id": ObjectId(user_id)},
        {
            "$inc": {"streak_freezes_available": -1},
            "$set": {"last_workout_date": datetime.now(timezone.utc).strftime("%Y-%m-%d")}
        }
    )
    
    return {"success": True, "freezes_remaining": freezes - 1}


# ----- Settings Endpoints -----

@app.get("/api/users/{user_id}/settings")
async def get_settings(user_id: str):
    settings = await settings_collection.find_one({"user_id": user_id})
    
    if not settings:
        return {
            "user_id": user_id,
            "sensory": {
                "animation_level": 100,
                "sound_level": 100,
                "haptic_level": 100,
                "transition_warnings": True,
                "literal_labels": False
            },
            "voice_coach": {
                "enabled": True,
                "voice_selection": "default",
                "speech_rate": "normal",
                "volume": 80,
                "min_gap_seconds": 15,
                "zone_callouts": True,
                "point_updates": "every_3",
                "interval_countdown": True,
                "encouragement": "occasional",
                "pacing_warnings": True
            },
            "zone_settings": {
                "zone_1_max": 60,
                "zone_2_max": 70,
                "zone_3_max": 84,
                "zone_4_max": 92,
                "use_karvonen": False
            },
            "dark_mode": True,
            "high_contrast": False,
            "colorblind_mode": False,
            "font_size": "medium"
        }
    
    settings["id"] = str(settings.pop("_id"))
    return settings


@app.put("/api/users/{user_id}/settings")
async def update_settings(user_id: str, settings: dict):
    settings["user_id"] = user_id
    settings["updated_at"] = datetime.now(timezone.utc).isoformat()
    
    await settings_collection.update_one(
        {"user_id": user_id},
        {"$set": settings},
        upsert=True
    )
    
    return await get_settings(user_id)


# ----- Workout Endpoints -----

@app.post("/api/workouts", response_model=WorkoutResponse)
async def create_workout(workout: WorkoutCreate):
    try:
        user = await users_collection.find_one({"_id": ObjectId(workout.user_id)})
    except:
        raise HTTPException(status_code=400, detail="Invalid user ID")
    
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    total_points, zone_summaries, avg_hr, max_hr = calculate_burn_points(
        workout.hr_samples, user["max_hr"]
    )
    
    calories = calculate_calories(
        workout.duration_seconds, avg_hr, user["weight_kg"], user["age"]
    )
    
    afterburn = calculate_afterburn(zone_summaries, user["weight_kg"])
    
    target_hit = total_points >= user["daily_burn_target"]
    
    # XP calculation with bonuses
    xp_earned = 10 + (total_points * 5) + (20 if target_hit else 0)
    
    # Streak bonus
    if user.get("streak_days", 0) >= 3:
        xp_earned += 10
    
    end_time = datetime.now(timezone.utc)
    start_time = end_time - timedelta(seconds=workout.duration_seconds)
    
    # Get template name if using one
    template_name = None
    if workout.template_id:
        template = next((t for t in BUILT_IN_TEMPLATES if t["id"] == workout.template_id), None)
        if template:
            template_name = template["name"]
        else:
            custom_template = await templates_collection.find_one({"_id": ObjectId(workout.template_id)})
            if custom_template:
                template_name = custom_template["name"]
    
    workout_doc = {
        "user_id": workout.user_id,
        "start_time": start_time.isoformat(),
        "end_time": end_time.isoformat(),
        "duration_seconds": workout.duration_seconds,
        "total_burn_points": total_points,
        "zones": zone_summaries,
        "avg_hr": avg_hr,
        "max_hr": max_hr,
        "calories_burned": calories,
        "afterburn_estimate": afterburn,
        "target_hit": target_hit,
        "xp_earned": xp_earned,
        "notes": workout.notes,
        "template_id": workout.template_id,
        "template_name": template_name,
        "hr_samples": workout.hr_samples
    }
    
    result = await workouts_collection.insert_one(workout_doc)
    
    # Update user stats
    today = datetime.now(timezone.utc).strftime("%Y-%m-%d")
    last_date = user.get("last_workout_date")
    
    new_streak = user.get("streak_days", 0)
    if last_date:
        last = datetime.strptime(last_date, "%Y-%m-%d")
        diff = (datetime.strptime(today, "%Y-%m-%d") - last).days
        if diff == 1 and target_hit:
            new_streak += 1
        elif diff > 1:
            new_streak = 1 if target_hit else 0
        elif diff == 0:
            pass  # Same day, keep streak
    else:
        new_streak = 1 if target_hit else 0
    
    new_xp = user.get("xp", 0) + xp_earned
    
    await users_collection.update_one(
        {"_id": ObjectId(workout.user_id)},
        {"$set": {
            "xp": new_xp,
            "level": calculate_level(new_xp),
            "streak_days": new_streak,
            "last_workout_date": today
        },
        "$inc": {
            "total_burn_points": total_points,
            "total_workouts": 1
        }}
    )
    
    # Check achievements
    workout_doc["id"] = str(result.inserted_id)
    await check_achievements(workout.user_id, workout_doc)
    await update_personal_bests(workout.user_id, workout_doc)
    
    return WorkoutResponse(**workout_doc)


@app.get("/api/users/{user_id}/workouts")
async def get_user_workouts(user_id: str, limit: int = 20, offset: int = 0):
    cursor = workouts_collection.find({"user_id": user_id}).sort("end_time", -1).skip(offset).limit(limit)
    
    workouts = []
    async for w in cursor:
        w["id"] = str(w.pop("_id"))
        w.pop("hr_samples", None)
        workouts.append(w)
    
    total = await workouts_collection.count_documents({"user_id": user_id})
    
    return {"workouts": workouts, "total": total}


@app.get("/api/workouts/{workout_id}")
async def get_workout(workout_id: str):
    try:
        workout = await workouts_collection.find_one({"_id": ObjectId(workout_id)})
    except:
        raise HTTPException(status_code=400, detail="Invalid workout ID")
    
    if not workout:
        raise HTTPException(status_code=404, detail="Workout not found")
    
    workout["id"] = str(workout.pop("_id"))
    return workout


# ----- Stats & Trends Endpoints -----

@app.get("/api/users/{user_id}/stats")
async def get_user_stats(user_id: str):
    user = await users_collection.find_one({"_id": ObjectId(user_id)})
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    today = datetime.now(timezone.utc).strftime("%Y-%m-%d")
    today_workouts = await workouts_collection.find({
        "user_id": user_id,
        "end_time": {"$regex": f"^{today}"}
    }).to_list(None)
    
    today_points = sum(w.get("total_burn_points", 0) for w in today_workouts)
    today_target_hit = today_points >= user["daily_burn_target"]
    
    week_ago = (datetime.now(timezone.utc) - timedelta(days=7)).isoformat()
    week_workouts = await workouts_collection.find({
        "user_id": user_id,
        "end_time": {"$gte": week_ago}
    }).to_list(None)
    
    week_points = sum(w.get("total_burn_points", 0) for w in week_workouts)
    week_workout_count = len(week_workouts)
    
    return {
        "user_id": user_id,
        "today": {
            "burn_points": today_points,
            "target": user["daily_burn_target"],
            "target_hit": today_target_hit,
            "workout_count": len(today_workouts)
        },
        "week": {
            "burn_points": week_points,
            "workout_count": week_workout_count,
            "avg_points_per_workout": week_points // week_workout_count if week_workout_count > 0 else 0
        },
        "streak": {
            "current": user.get("streak_days", 0),
            "bonus_active": user.get("streak_days", 0) >= 3,
            "freezes_available": user.get("streak_freezes_available", 1)
        },
        "progress": {
            "xp": user.get("xp", 0),
            "level": user.get("level", 1),
            "xp_to_next_level": 100 - (user.get("xp", 0) % 100)
        },
        "lifetime": {
            "total_burn_points": user.get("total_burn_points", 0),
            "total_workouts": user.get("total_workouts", 0)
        }
    }


@app.get("/api/users/{user_id}/trends")
async def get_user_trends(user_id: str, days: int = 30):
    """Get trend data for charts"""
    user = await users_collection.find_one({"_id": ObjectId(user_id)})
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    start_date = (datetime.now(timezone.utc) - timedelta(days=days)).isoformat()
    workouts = await workouts_collection.find({
        "user_id": user_id,
        "end_time": {"$gte": start_date}
    }).sort("end_time", 1).to_list(None)
    
    # Daily points
    daily_points = {}
    daily_zone_time = {}
    
    for w in workouts:
        date = w["end_time"][:10]
        daily_points[date] = daily_points.get(date, 0) + w.get("total_burn_points", 0)
        
        if date not in daily_zone_time:
            daily_zone_time[date] = {1: 0, 2: 0, 3: 0, 4: 0, 5: 0}
        
        for z in w.get("zones", []):
            daily_zone_time[date][z["zone"]] += z["duration_seconds"]
    
    # Weekly aggregates
    weekly_points = []
    for i in range(min(8, days // 7)):
        week_start = datetime.now(timezone.utc) - timedelta(days=(i + 1) * 7)
        week_end = datetime.now(timezone.utc) - timedelta(days=i * 7)
        
        week_total = sum(
            w.get("total_burn_points", 0) 
            for w in workouts 
            if week_start.isoformat() <= w["end_time"] < week_end.isoformat()
        )
        weekly_points.append({
            "week": i,
            "label": f"{(i * 7)} - {((i + 1) * 7)} days ago" if i > 0 else "This week",
            "points": week_total
        })
    
    return {
        "daily_points": [{"date": k, "points": v} for k, v in sorted(daily_points.items())],
        "weekly_points": list(reversed(weekly_points)),
        "zone_distribution": daily_zone_time,
        "total_workouts": len(workouts),
        "avg_points_per_workout": sum(w.get("total_burn_points", 0) for w in workouts) // len(workouts) if workouts else 0
    }


# ----- Quests Endpoints -----

@app.get("/api/users/{user_id}/quests")
async def get_daily_quests(user_id: str):
    user = await users_collection.find_one({"_id": ObjectId(user_id)})
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    quests = [
        {
            "id": "hit_target",
            "title": "Daily Burner",
            "description": f"Earn {user['daily_burn_target']} Burn Points today",
            "xp_reward": 25,
            "progress": 0,
            "target": user["daily_burn_target"],
            "completed": False
        },
        {
            "id": "push_zone",
            "title": "Push It",
            "description": "Spend 5 minutes in Push zone",
            "xp_reward": 15,
            "progress": 0,
            "target": 300,
            "completed": False
        },
        {
            "id": "peak_zone",
            "title": "Peak Performance",
            "description": "Reach Peak zone at least once",
            "xp_reward": 10,
            "progress": 0,
            "target": 1,
            "completed": False
        },
        {
            "id": "active_time",
            "title": "Stay Active",
            "description": "Spend 15 minutes in Active zone or above",
            "xp_reward": 20,
            "progress": 0,
            "target": 900,
            "completed": False
        },
        {
            "id": "calorie_burn",
            "title": "Calorie Crusher",
            "description": "Burn 200 calories",
            "xp_reward": 15,
            "progress": 0,
            "target": 200,
            "completed": False
        }
    ]
    
    today = datetime.now(timezone.utc).strftime("%Y-%m-%d")
    today_workouts = await workouts_collection.find({
        "user_id": user_id,
        "end_time": {"$regex": f"^{today}"}
    }).to_list(None)
    
    total_points = 0
    push_seconds = 0
    peak_reached = False
    active_plus_seconds = 0
    total_calories = 0
    
    for w in today_workouts:
        total_points += w.get("total_burn_points", 0)
        total_calories += w.get("calories_burned", 0)
        for z in w.get("zones", []):
            if z["zone"] == 4:
                push_seconds += z["duration_seconds"]
            if z["zone"] == 5 and z["duration_seconds"] > 0:
                peak_reached = True
            if z["zone"] >= 3:
                active_plus_seconds += z["duration_seconds"]
    
    quests[0]["progress"] = total_points
    quests[0]["completed"] = total_points >= user["daily_burn_target"]
    
    quests[1]["progress"] = push_seconds
    quests[1]["completed"] = push_seconds >= 300
    
    quests[2]["progress"] = 1 if peak_reached else 0
    quests[2]["completed"] = peak_reached
    
    quests[3]["progress"] = active_plus_seconds
    quests[3]["completed"] = active_plus_seconds >= 900
    
    quests[4]["progress"] = total_calories
    quests[4]["completed"] = total_calories >= 200
    
    return {"quests": quests, "date": today}


# ----- Templates Endpoints -----

@app.get("/api/templates")
async def get_templates():
    """Get all built-in workout templates"""
    return {"templates": BUILT_IN_TEMPLATES}


@app.get("/api/templates/{template_id}")
async def get_template(template_id: str):
    """Get a specific template"""
    template = next((t for t in BUILT_IN_TEMPLATES if t["id"] == template_id), None)
    if template:
        return template
    
    # Check custom templates
    try:
        custom = await templates_collection.find_one({"_id": ObjectId(template_id)})
        if custom:
            custom["id"] = str(custom.pop("_id"))
            return custom
    except:
        pass
    
    raise HTTPException(status_code=404, detail="Template not found")


@app.get("/api/users/{user_id}/templates")
async def get_user_custom_templates(user_id: str):
    """Get user's custom templates"""
    templates = await templates_collection.find({"user_id": user_id}).to_list(None)
    for t in templates:
        t["id"] = str(t.pop("_id"))
    return {"templates": templates}


@app.post("/api/users/{user_id}/templates")
async def create_custom_template(user_id: str, template: CustomTemplateCreate):
    """Create a custom workout template"""
    total_duration = sum(s.duration_seconds for s in template.segments)
    estimated_points = sum(
        (s.duration_seconds // 60) * ZONES.get(s.target_zone, {}).get("points", 0)
        for s in template.segments
    )
    
    template_doc = {
        "user_id": user_id,
        "name": template.name,
        "description": template.description,
        "segments": [s.dict() for s in template.segments],
        "duration_minutes": total_duration // 60,
        "estimated_burn_points": estimated_points,
        "difficulty": "custom",
        "category": "custom",
        "created_at": datetime.now(timezone.utc).isoformat()
    }
    
    result = await templates_collection.insert_one(template_doc)
    template_doc["id"] = str(result.inserted_id)
    return template_doc


@app.delete("/api/templates/{template_id}")
async def delete_custom_template(template_id: str):
    """Delete a custom template"""
    try:
        result = await templates_collection.delete_one({"_id": ObjectId(template_id)})
        if result.deleted_count == 0:
            raise HTTPException(status_code=404, detail="Template not found")
        return {"success": True}
    except:
        raise HTTPException(status_code=400, detail="Invalid template ID")


# ----- Achievements Endpoints -----

@app.get("/api/users/{user_id}/achievements")
async def get_user_achievements(user_id: str):
    """Get all achievements with unlock status for user"""
    user_achievements = await achievements_collection.find({"user_id": user_id}).to_list(None)
    unlocked_map = {a["achievement_id"]: a["unlocked_at"] for a in user_achievements}
    
    result = []
    for achievement in ACHIEVEMENTS:
        a = achievement.copy()
        a["unlocked"] = achievement["id"] in unlocked_map
        a["unlocked_at"] = unlocked_map.get(achievement["id"])
        result.append(a)
    
    return {"achievements": result}


# ----- Personal Bests Endpoints -----

@app.get("/api/users/{user_id}/personal-bests")
async def get_personal_bests(user_id: str):
    """Get user's personal bests"""
    pb = await personal_bests_collection.find_one({"user_id": user_id})
    
    if not pb:
        return {
            "user_id": user_id,
            "max_session_points": 0,
            "max_session_points_date": None,
            "longest_workout_seconds": 0,
            "longest_workout_date": None,
            "longest_peak_seconds": 0,
            "longest_peak_date": None,
            "longest_streak": 0
        }
    
    pb.pop("_id", None)
    return pb


# ----- Export Endpoints -----

@app.get("/api/users/{user_id}/export")
async def export_user_data(user_id: str, format: str = "json"):
    """Export all user data"""
    user = await users_collection.find_one({"_id": ObjectId(user_id)})
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    user["id"] = str(user.pop("_id"))
    
    workouts = await workouts_collection.find({"user_id": user_id}).to_list(None)
    for w in workouts:
        w["id"] = str(w.pop("_id"))
    
    settings = await settings_collection.find_one({"user_id": user_id})
    if settings:
        settings.pop("_id", None)
    
    achievements = await achievements_collection.find({"user_id": user_id}).to_list(None)
    for a in achievements:
        a.pop("_id", None)
    
    pb = await personal_bests_collection.find_one({"user_id": user_id})
    if pb:
        pb.pop("_id", None)
    
    export_data = {
        "exported_at": datetime.now(timezone.utc).isoformat(),
        "user": user,
        "settings": settings,
        "workouts": workouts,
        "achievements": achievements,
        "personal_bests": pb
    }
    
    if format == "json":
        return Response(
            content=json.dumps(export_data, indent=2, default=str),
            media_type="application/json",
            headers={"Content-Disposition": f"attachment; filename=pulsefit_export_{user_id}.json"}
        )
    elif format == "csv":
        output = io.StringIO()
        writer = csv.writer(output)
        
        # Workouts CSV
        writer.writerow(["Workout ID", "Date", "Duration (min)", "Burn Points", "Avg HR", "Max HR", "Calories", "Target Hit"])
        for w in workouts:
            writer.writerow([
                w["id"],
                w["end_time"][:10],
                w["duration_seconds"] // 60,
                w["total_burn_points"],
                w["avg_hr"],
                w["max_hr"],
                w["calories_burned"],
                w["target_hit"]
            ])
        
        return Response(
            content=output.getvalue(),
            media_type="text/csv",
            headers={"Content-Disposition": f"attachment; filename=pulsefit_workouts_{user_id}.csv"}
        )
    else:
        raise HTTPException(status_code=400, detail="Invalid format. Use 'json' or 'csv'")


# ----- HR Simulation -----

@app.post("/api/simulate-hr")
async def simulate_heart_rate(req: HRSimulationRequest):
    try:
        user = await users_collection.find_one({"_id": ObjectId(req.user_id)})
    except:
        raise HTTPException(status_code=400, detail="Invalid user ID")
    
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    max_hr = user["max_hr"]
    resting_hr = user["resting_hr"]
    
    # If target zone specified, aim for that zone
    if req.target_zone:
        zone_info = ZONES.get(req.target_zone, ZONES[3])
        min_pct = zone_info["min_pct"] / 100
        max_pct = zone_info["max_pct"] / 100
    else:
        intensity_ranges = {
            "easy": (0.50, 0.70),
            "moderate": (0.60, 0.85),
            "hard": (0.75, 0.95),
            "interval": (0.55, 0.95)
        }
        min_pct, max_pct = intensity_ranges.get(req.intensity, (0.60, 0.85))
    
    base_hr = int(max_hr * ((min_pct + max_pct) / 2))
    variation = int(max_hr * (max_pct - min_pct) / 4)
    hr = base_hr + random.randint(-variation, variation)
    hr = max(resting_hr, min(max_hr, hr))
    
    zone = get_zone(hr, max_hr)
    zone_info = ZONES[zone]
    
    return {
        "heart_rate": hr,
        "zone": zone,
        "zone_name": zone_info["name"],
        "zone_color": zone_info["color"],
        "points_per_minute": zone_info["points"],
        "max_hr": max_hr,
        "hr_percentage": round((hr / max_hr) * 100, 1)
    }


@app.get("/api/zones")
async def get_zones():
    return {"zones": ZONES}


# ----- Voice Coach Phrases -----

@app.get("/api/voice-phrases")
async def get_voice_phrases():
    """Get voice coach phrase library"""
    return {
        "zone_up": [
            "You're now in {zone} zone. {points} points per minute.",
            "Moving up to {zone}! Keep it going!",
            "Entering {zone} zone - great work!",
            "{zone} zone activated!"
        ],
        "zone_down": [
            "Dropped to {zone} zone.",
            "Now in {zone} - push a bit harder if you can.",
            "Back to {zone} zone."
        ],
        "target_hit": [
            "Congratulations! You've hit your daily target!",
            "Target achieved! Amazing work!",
            "Daily goal complete! You're crushing it!"
        ],
        "point_update": [
            "{points} Burn Points earned. {remaining} to go.",
            "You're at {points} points. Keep pushing!",
            "{points} points and counting!"
        ],
        "encouragement": [
            "You're doing great!",
            "Keep it up!",
            "Strong effort!",
            "Stay in the zone!",
            "You've got this!"
        ],
        "segment_change": [
            "Next up: {segment}. Target zone: {zone}.",
            "Moving to {segment}. Aim for {zone} zone.",
            "{segment} starting now!"
        ],
        "countdown": [
            "3... 2... 1... switch!",
            "Segment change in 3, 2, 1!",
            "Here we go!"
        ],
        "pacing": [
            "You've been in Peak for {minutes} minutes. Consider easing back to Push.",
            "Great sustained effort in {zone}!",
            "Pace yourself - you're doing well."
        ]
    }


# ----- ElevenLabs Voice Endpoints -----

# Available voices for the voice coach
VOICE_PRESETS = {
    "energetic_coach": {
        "voice_id": "pNInz6obpgDQGcFmaJgB",  # Adam - energetic male
        "name": "Energetic Coach",
        "description": "High-energy motivational voice",
        "stability": 0.5,
        "similarity_boost": 0.75
    },
    "calm_guide": {
        "voice_id": "EXAVITQu4vr4xnSDxMaL",  # Sarah - calm female
        "name": "Calm Guide", 
        "description": "Soothing, relaxed voice for ASD mode",
        "stability": 0.8,
        "similarity_boost": 0.5
    },
    "friendly_trainer": {
        "voice_id": "ErXwobaYiN019PkySvjV",  # Antoni - friendly male
        "name": "Friendly Trainer",
        "description": "Warm, encouraging voice",
        "stability": 0.6,
        "similarity_boost": 0.7
    },
    "professional": {
        "voice_id": "21m00Tcm4TlvDq8ikWAM",  # Rachel - professional female
        "name": "Professional",
        "description": "Clear, professional announcements",
        "stability": 0.7,
        "similarity_boost": 0.6
    }
}


class TTSRequest(BaseModel):
    text: str
    voice_preset: str = "energetic_coach"
    stability: Optional[float] = None
    similarity_boost: Optional[float] = None


class TTSResponse(BaseModel):
    audio_base64: str
    text: str
    voice_id: str
    format: str = "mp3"


@app.get("/api/voices")
async def get_available_voices():
    """Get available voice presets for the voice coach"""
    return {"voices": VOICE_PRESETS}


@app.get("/api/voices/elevenlabs")
async def get_elevenlabs_voices():
    """Get all available ElevenLabs voices"""
    if not eleven_client:
        raise HTTPException(status_code=503, detail="ElevenLabs not configured")
    
    try:
        voices_response = eleven_client.voices.get_all()
        voices = [
            {
                "voice_id": v.voice_id,
                "name": v.name,
                "category": getattr(v, 'category', 'premade'),
                "labels": getattr(v, 'labels', {})
            }
            for v in voices_response.voices
        ]
        return {"voices": voices}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to fetch voices: {str(e)}")


@app.post("/api/tts/generate", response_model=TTSResponse)
async def generate_tts(request: TTSRequest):
    """Generate text-to-speech audio using ElevenLabs"""
    if not eleven_client:
        raise HTTPException(status_code=503, detail="ElevenLabs not configured")
    
    try:
        # Get voice preset
        preset = VOICE_PRESETS.get(request.voice_preset, VOICE_PRESETS["energetic_coach"])
        
        # Use request overrides if provided
        stability = request.stability if request.stability is not None else preset["stability"]
        similarity = request.similarity_boost if request.similarity_boost is not None else preset["similarity_boost"]
        
        voice_settings = VoiceSettings(
            stability=stability,
            similarity_boost=similarity,
            style=0.0,
            use_speaker_boost=True
        )
        
        # Generate audio
        audio_generator = eleven_client.text_to_speech.convert(
            text=request.text,
            voice_id=preset["voice_id"],
            model_id="eleven_multilingual_v2",
            voice_settings=voice_settings
        )
        
        # Collect audio data
        audio_data = b""
        for chunk in audio_generator:
            audio_data += chunk
        
        # Convert to base64
        audio_b64 = base64.b64encode(audio_data).decode()
        
        return TTSResponse(
            audio_base64=audio_b64,
            text=request.text,
            voice_id=preset["voice_id"],
            format="mp3"
        )
        
    except Exception as e:
        error_msg = str(e).lower()
        # Handle ElevenLabs API authentication errors
        if "401" in error_msg or "unauthorized" in error_msg or "authentication" in error_msg:
            print(f"ElevenLabs API authentication failed: {str(e)}")
            raise HTTPException(status_code=401, detail="ElevenLabs API key is invalid or restricted. Using browser TTS fallback.")
        # Handle quota/rate limit errors
        elif "429" in error_msg or "quota" in error_msg or "limit" in error_msg:
            print(f"ElevenLabs API quota exceeded: {str(e)}")
            raise HTTPException(status_code=429, detail="ElevenLabs API quota exceeded. Using browser TTS fallback.")
        else:
            print(f"ElevenLabs TTS generation failed: {str(e)}")
            raise HTTPException(status_code=500, detail=f"TTS generation failed: {str(e)}")


@app.post("/api/tts/workout-announcement")
async def generate_workout_announcement(
    announcement_type: str,
    zone: Optional[str] = None,
    points: Optional[int] = None,
    segment: Optional[str] = None,
    voice_preset: str = "energetic_coach",
    nd_mode: str = "standard"
):
    """Generate a workout announcement with appropriate phrasing"""
    if not eleven_client:
        raise HTTPException(status_code=503, detail="ElevenLabs not configured")
    
    # Select voice based on ND mode
    if nd_mode in ["asd", "combined"]:
        voice_preset = "calm_guide"
    
    # Generate appropriate text
    phrases = {
        "zone_up": f"You're now in {zone} zone. Keep it going!",
        "zone_down": f"Dropping to {zone} zone. Push a bit harder if you can.",
        "target_hit": "Congratulations! You've hit your daily target! Amazing work!",
        "point_earned": f"You've earned {points} Burn Points so far.",
        "encouragement": random.choice([
            "You're doing great!",
            "Keep it up!",
            "Strong effort!",
            "Stay in the zone!",
            "You've got this!"
        ]),
        "segment_change": f"Next up: {segment}. Get ready!",
        "workout_start": "Workout started. Let's go!",
        "workout_complete": "Workout complete. Great job today!"
    }
    
    # Literal mode for ASD
    if nd_mode in ["asd", "combined"]:
        phrases = {
            "zone_up": f"Heart rate zone changed to {zone}.",
            "zone_down": f"Heart rate zone is now {zone}.",
            "target_hit": f"Daily target of {points} Burn Points has been reached.",
            "point_earned": f"Current Burn Points: {points}.",
            "encouragement": "Continuing workout.",
            "segment_change": f"Segment: {segment}.",
            "workout_start": "Workout has started.",
            "workout_complete": "Workout has ended."
        }
    
    text = phrases.get(announcement_type, "")
    if not text:
        raise HTTPException(status_code=400, detail="Invalid announcement type")
    
    # Generate TTS
    return await generate_tts(TTSRequest(text=text, voice_preset=voice_preset))


@app.get("/api/tts/status")
async def get_tts_status():
    """Check if ElevenLabs TTS is configured and working"""
    return {
        "enabled": eleven_client is not None,
        "provider": "elevenlabs",
        "presets_available": list(VOICE_PRESETS.keys())
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)
