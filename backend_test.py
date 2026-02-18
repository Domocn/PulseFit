#!/usr/bin/env python3
"""
PulseFit Backend API Testing Suite
Tests all backend functionality including user management, workouts, and settings.
"""
import requests
import json
import sys
from datetime import datetime
import time

class PulseFitAPITester:
    def __init__(self, base_url="https://pulsefit-preview.preview.emergentagent.com"):
        self.base_url = base_url
        self.user_id = None
        self.workout_id = None
        self.tests_run = 0
        self.tests_passed = 0
        self.failed_tests = []
        
    def log_test(self, test_name, success, details=""):
        """Log test results"""
        self.tests_run += 1
        if success:
            self.tests_passed += 1
            print(f"✅ {test_name}: PASS")
        else:
            print(f"❌ {test_name}: FAIL - {details}")
            self.failed_tests.append(f"{test_name}: {details}")
        return success
    
    def run_test(self, name, method, endpoint, expected_status, data=None, headers=None):
        """Run a single API test"""
        url = f"{self.base_url}/{endpoint}"
        if headers is None:
            headers = {'Content-Type': 'application/json'}
        
        try:
            if method == 'GET':
                response = requests.get(url, headers=headers, timeout=10)
            elif method == 'POST':
                response = requests.post(url, json=data, headers=headers, timeout=10)
            elif method == 'PATCH':
                response = requests.patch(url, json=data, headers=headers, timeout=10)
            elif method == 'PUT':
                response = requests.put(url, json=data, headers=headers, timeout=10)
            else:
                return self.log_test(name, False, f"Unsupported method: {method}")
            
            success = response.status_code == expected_status
            
            if success:
                try:
                    response_data = response.json()
                    self.log_test(name, True, f"Status: {response.status_code}")
                    return True, response_data
                except:
                    # Response might not be JSON
                    self.log_test(name, True, f"Status: {response.status_code}")
                    return True, {}
            else:
                try:
                    error_data = response.json()
                    self.log_test(name, False, f"Expected {expected_status}, got {response.status_code} - {error_data}")
                except:
                    self.log_test(name, False, f"Expected {expected_status}, got {response.status_code} - {response.text}")
                return False, {}
                
        except Exception as e:
            self.log_test(name, False, f"Request failed: {str(e)}")
            return False, {}

    def test_health_check(self):
        """Test /api/health endpoint"""
        success, data = self.run_test("Health Check", "GET", "api/health", 200)
        if success and data.get("status") == "healthy":
            return True
        return False
    
    def test_create_user(self):
        """Test user creation"""
        user_data = {
            "name": "Test User",
            "age": 30,
            "weight_kg": 70,
            "height_cm": 175,
            "resting_hr": 60,
            "daily_burn_target": 12,
            "nd_mode": "standard"
        }
        
        success, data = self.run_test("Create User", "POST", "api/users", 200, user_data)
        if success and data.get("id"):
            self.user_id = data["id"]
            print(f"   Created user ID: {self.user_id}")
            
            # Verify required fields
            required_fields = ["name", "age", "weight_kg", "height_cm", "max_hr", "xp", "level"]
            missing_fields = [field for field in required_fields if field not in data]
            if missing_fields:
                self.log_test("User Response Fields", False, f"Missing fields: {missing_fields}")
                return False
            else:
                self.log_test("User Response Fields", True)
            
            return True
        return False
    
    def test_get_user(self):
        """Test get user by ID"""
        if not self.user_id:
            return self.log_test("Get User", False, "No user ID available")
        
        success, data = self.run_test("Get User", "GET", f"api/users/{self.user_id}", 200)
        return success and data.get("id") == self.user_id
    
    def test_update_user(self):
        """Test user profile update"""
        if not self.user_id:
            return self.log_test("Update User", False, "No user ID available")
        
        update_data = {
            "name": "Updated Test User",
            "daily_burn_target": 15,
            "nd_mode": "adhd"
        }
        
        success, data = self.run_test("Update User", "PATCH", f"api/users/{self.user_id}", 200, update_data)
        if success:
            # Verify updates were applied
            if data.get("name") == update_data["name"] and data.get("daily_burn_target") == 15:
                return True
            else:
                return self.log_test("Update User Verification", False, "Updates not reflected in response")
        return False
    
    def test_get_user_stats(self):
        """Test get user statistics"""
        if not self.user_id:
            return self.log_test("Get User Stats", False, "No user ID available")
        
        success, data = self.run_test("Get User Stats", "GET", f"api/users/{self.user_id}/stats", 200)
        if success:
            # Check required stats structure
            required_keys = ["today", "week", "streak", "progress"]
            missing_keys = [key for key in required_keys if key not in data]
            if missing_keys:
                return self.log_test("Stats Structure", False, f"Missing keys: {missing_keys}")
            else:
                self.log_test("Stats Structure", True)
                return True
        return False
    
    def test_get_settings(self):
        """Test get user settings (should return defaults)"""
        if not self.user_id:
            return self.log_test("Get Settings", False, "No user ID available")
        
        success, data = self.run_test("Get Settings", "GET", f"api/users/{self.user_id}/settings", 200)
        if success:
            # Check sensory settings structure
            if "sensory" in data and "animation_level" in data["sensory"]:
                self.log_test("Settings Structure", True)
                return True
            else:
                return self.log_test("Settings Structure", False, "Missing sensory settings")
        return False
    
    def test_update_settings(self):
        """Test update user settings"""
        if not self.user_id:
            return self.log_test("Update Settings", False, "No user ID available")
        
        settings_data = {
            "sensory": {
                "animation_level": 50,
                "sound_level": 75,
                "haptic_level": 100,
                "transition_warnings": True,
                "literal_labels": False
            },
            "voice_coach_enabled": True,
            "dark_mode": True
        }
        
        success, data = self.run_test("Update Settings", "PUT", f"api/users/{self.user_id}/settings", 200, settings_data)
        return success
    
    def test_heart_rate_simulation(self):
        """Test HR simulation endpoint"""
        if not self.user_id:
            return self.log_test("HR Simulation", False, "No user ID available")
        
        hr_request = {
            "user_id": self.user_id,
            "intensity": "moderate"
        }
        
        success, data = self.run_test("HR Simulation", "POST", "api/simulate-hr", 200, hr_request)
        if success:
            # Verify HR data structure
            required_fields = ["heart_rate", "zone", "zone_name", "zone_color", "points_per_minute"]
            missing_fields = [field for field in required_fields if field not in data]
            if missing_fields:
                return self.log_test("HR Response Structure", False, f"Missing fields: {missing_fields}")
            else:
                self.log_test("HR Response Structure", True)
                print(f"   HR: {data.get('heart_rate')} BPM, Zone: {data.get('zone_name')}")
                return True
        return False
    
    def test_zones_endpoint(self):
        """Test get zones configuration"""
        success, data = self.run_test("Get Zones", "GET", "api/zones", 200)
        if success and "zones" in data:
            zones = data["zones"]
            # Check if we have the expected zones (1-5)
            expected_zones = [1, 2, 3, 4, 5]
            missing_zones = [z for z in expected_zones if str(z) not in zones]
            if missing_zones:
                return self.log_test("Zones Structure", False, f"Missing zones: {missing_zones}")
            else:
                self.log_test("Zones Structure", True)
                return True
        return False
    
    def test_daily_quests(self):
        """Test daily quests endpoint"""
        if not self.user_id:
            return self.log_test("Daily Quests", False, "No user ID available")
        
        success, data = self.run_test("Daily Quests", "GET", f"api/users/{self.user_id}/quests", 200)
        if success and "quests" in data:
            quests = data["quests"]
            if len(quests) > 0:
                # Check quest structure
                first_quest = quests[0]
                required_fields = ["id", "title", "description", "xp_reward", "progress", "target", "completed"]
                missing_fields = [field for field in required_fields if field not in first_quest]
                if missing_fields:
                    return self.log_test("Quest Structure", False, f"Missing fields: {missing_fields}")
                else:
                    self.log_test("Quest Structure", True)
                    return True
            else:
                return self.log_test("Quest Count", False, "No quests returned")
        return False
    
    def test_create_workout(self):
        """Test workout creation and saving"""
        if not self.user_id:
            return self.log_test("Create Workout", False, "No user ID available")
        
        # Generate sample HR data (simulating 5 minutes of workout)
        hr_samples = []
        base_time = datetime.now()
        for i in range(60):  # 1 minute of data (1 sample per second)
            hr_samples.append({
                "timestamp": base_time.replace(second=i).isoformat() + "Z",
                "heart_rate": 120 + (i % 20)  # Varying HR between 120-140
            })
        
        workout_data = {
            "user_id": self.user_id,
            "hr_samples": hr_samples,
            "duration_seconds": 60,
            "notes": "Test workout session"
        }
        
        success, data = self.run_test("Create Workout", "POST", "api/workouts", 200, workout_data)
        if success and data.get("id"):
            self.workout_id = data["id"]
            print(f"   Created workout ID: {self.workout_id}")
            
            # Verify workout response structure
            required_fields = ["total_burn_points", "zones", "avg_hr", "max_hr", "calories_burned", "xp_earned"]
            missing_fields = [field for field in required_fields if field not in data]
            if missing_fields:
                return self.log_test("Workout Response Structure", False, f"Missing fields: {missing_fields}")
            else:
                self.log_test("Workout Response Structure", True)
                print(f"   Burn Points: {data.get('total_burn_points')}, XP: {data.get('xp_earned')}")
                return True
        return False
    
    def test_get_workout(self):
        """Test get single workout details"""
        if not self.workout_id:
            return self.log_test("Get Workout", False, "No workout ID available")
        
        success, data = self.run_test("Get Workout", "GET", f"api/workouts/{self.workout_id}", 200)
        return success and data.get("id") == self.workout_id
    
    def test_get_user_workouts(self):
        """Test get user workout history"""
        if not self.user_id:
            return self.log_test("Get User Workouts", False, "No user ID available")
        
        success, data = self.run_test("Get User Workouts", "GET", f"api/users/{self.user_id}/workouts", 200)
        if success and "workouts" in data:
            workouts = data["workouts"]
            if len(workouts) > 0:
                # Verify we have our created workout
                workout_ids = [w.get("id") for w in workouts]
                if self.workout_id in workout_ids:
                    self.log_test("Workout in History", True)
                    return True
                else:
                    return self.log_test("Workout in History", False, "Created workout not in history")
            else:
                return self.log_test("Workout History", False, "No workouts in history")
        return False
    
    def test_get_templates(self):
        """Test GET /api/templates - should return 6 built-in templates"""
        success, data = self.run_test("Get Templates", "GET", "api/templates", 200)
        if success and "templates" in data:
            templates = data["templates"]
            if len(templates) == 6:
                self.log_test("Template Count", True, f"Found {len(templates)} templates")
                
                # Verify template structure
                first_template = templates[0]
                required_fields = ["id", "name", "description", "difficulty", "duration_minutes", "segments", "estimated_burn_points", "category"]
                missing_fields = [field for field in required_fields if field not in first_template]
                if missing_fields:
                    return self.log_test("Template Structure", False, f"Missing fields: {missing_fields}")
                else:
                    self.log_test("Template Structure", True)
                    return True
            else:
                return self.log_test("Template Count", False, f"Expected 6 templates, got {len(templates)}")
        return False

    def test_get_achievements(self):
        """Test GET /api/users/{id}/achievements"""
        if not self.user_id:
            return self.log_test("Get Achievements", False, "No user ID available")
        
        success, data = self.run_test("Get Achievements", "GET", f"api/users/{self.user_id}/achievements", 200)
        if success and "achievements" in data:
            achievements = data["achievements"]
            if len(achievements) >= 14:  # Should have 14 achievements
                self.log_test("Achievement Count", True, f"Found {len(achievements)} achievements")
                
                # Verify achievement structure
                first_achievement = achievements[0]
                required_fields = ["id", "name", "description", "icon", "category", "requirement", "xp_reward", "unlocked"]
                missing_fields = [field for field in required_fields if field not in first_achievement]
                if missing_fields:
                    return self.log_test("Achievement Structure", False, f"Missing fields: {missing_fields}")
                else:
                    self.log_test("Achievement Structure", True)
                    return True
            else:
                return self.log_test("Achievement Count", False, f"Expected at least 14 achievements, got {len(achievements)}")
        return False

    def test_get_trends(self):
        """Test GET /api/users/{id}/trends - returns daily/weekly data"""
        if not self.user_id:
            return self.log_test("Get Trends", False, "No user ID available")
        
        success, data = self.run_test("Get Trends", "GET", f"api/users/{self.user_id}/trends", 200)
        if success:
            # Check required trends structure
            required_keys = ["daily_points", "weekly_points", "zone_distribution", "total_workouts", "avg_points_per_workout"]
            missing_keys = [key for key in required_keys if key not in data]
            if missing_keys:
                return self.log_test("Trends Structure", False, f"Missing keys: {missing_keys}")
            else:
                self.log_test("Trends Structure", True)
                return True
        return False

    def test_get_personal_bests(self):
        """Test GET /api/users/{id}/personal-bests"""
        if not self.user_id:
            return self.log_test("Get Personal Bests", False, "No user ID available")
        
        success, data = self.run_test("Get Personal Bests", "GET", f"api/users/{self.user_id}/personal-bests", 200)
        if success:
            # Check personal bests structure
            expected_fields = ["user_id", "max_session_points", "longest_workout_seconds", "longest_peak_seconds"]
            missing_fields = [field for field in expected_fields if field not in data]
            if missing_fields:
                return self.log_test("Personal Bests Structure", False, f"Missing fields: {missing_fields}")
            else:
                self.log_test("Personal Bests Structure", True)
                return True
        return False

    def test_export_data(self):
        """Test GET /api/users/{id}/export?format=json"""
        if not self.user_id:
            return self.log_test("Export Data JSON", False, "No user ID available")
        
        # Test JSON export
        success, _ = self.run_test("Export Data JSON", "GET", f"api/users/{self.user_id}/export?format=json", 200)
        if not success:
            return False
            
        # Test CSV export
        success2, _ = self.run_test("Export Data CSV", "GET", f"api/users/{self.user_id}/export?format=csv", 200)
        
        return success and success2

    def test_template_based_workout(self):
        """Test creating a workout with template_id"""
        if not self.user_id:
            return self.log_test("Template Workout", False, "No user ID available")
        
        # First get a template ID
        success, templates_data = self.run_test("Get Template for Workout", "GET", "api/templates", 200)
        if not success or not templates_data.get("templates"):
            return self.log_test("Template Workout", False, "No templates available")
        
        template_id = templates_data["templates"][0]["id"]
        
        # Generate sample HR data
        hr_samples = []
        base_time = datetime.now()
        for i in range(60):  # 1 minute of data
            hr_samples.append({
                "timestamp": base_time.replace(second=i).isoformat() + "Z",
                "heart_rate": 140 + (i % 15)  # Varying HR between 140-155
            })
        
        workout_data = {
            "user_id": self.user_id,
            "hr_samples": hr_samples,
            "duration_seconds": 60,
            "notes": "Template-based test workout",
            "template_id": template_id
        }
        
        success, data = self.run_test("Template Workout", "POST", "api/workouts", 200, workout_data)
        if success and data.get("template_name"):
            print(f"   Template: {data.get('template_name')}")
            return True
        return False

    def test_invalid_endpoints(self):
        """Test error handling for invalid requests"""
        # Test invalid user ID
        success, _ = self.run_test("Invalid User ID", "GET", "api/users/invalid-id", 400)
        
        # Test invalid workout ID  
        success2, _ = self.run_test("Invalid Workout ID", "GET", "api/workouts/invalid-id", 400)
        
        # Test missing required fields in user creation
        success3, _ = self.run_test("Missing User Fields", "POST", "api/users", 422, {})
        
        return success and success2 and success3
    
    def run_all_tests(self):
        """Run all backend tests"""
        print("🚀 Starting PulseFit Backend API Tests")
        print("=" * 50)
        
        # Basic connectivity
        if not self.test_health_check():
            print("❌ Health check failed - backend might be down")
            return False
        
        # Zone configuration
        self.test_zones_endpoint()
        
        # Templates API - NEW FEATURE
        self.test_get_templates()
        
        # User management flow
        if self.test_create_user():
            self.test_get_user()
            self.test_update_user()
            self.test_get_user_stats()
            
            # Settings management
            self.test_get_settings()
            self.test_update_settings()
            
            # HR simulation
            self.test_heart_rate_simulation()
            
            # Quests
            self.test_daily_quests()
            
            # NEW FEATURES - Achievements, Trends, Personal Bests, Export
            self.test_get_achievements()
            self.test_get_trends()
            self.test_get_personal_bests()
            self.test_export_data()
            
            # Workout flow
            if self.test_create_workout():
                self.test_get_workout()
                self.test_get_user_workouts()
                
            # Template-based workout - NEW FEATURE
            self.test_template_based_workout()
        
        # Error handling
        self.test_invalid_endpoints()
        
        # Print final results
        print("\n" + "=" * 50)
        print(f"📊 Backend Tests Complete: {self.tests_passed}/{self.tests_run} passed")
        
        if self.failed_tests:
            print("\n❌ Failed Tests:")
            for failure in self.failed_tests:
                print(f"   • {failure}")
        else:
            print("\n✅ All backend tests passed!")
        
        return self.tests_passed == self.tests_run

def main():
    """Main test runner"""
    tester = PulseFitAPITester()
    
    try:
        all_passed = tester.run_all_tests()
        return 0 if all_passed else 1
    except KeyboardInterrupt:
        print("\n🛑 Tests interrupted")
        return 1
    except Exception as e:
        print(f"\n💥 Test runner error: {e}")
        return 1

if __name__ == "__main__":
    sys.exit(main())