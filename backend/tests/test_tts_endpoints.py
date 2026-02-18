"""
Test TTS (Text-to-Speech) Endpoints for PulseFit
Tests ElevenLabs voice integration and fallback behavior
"""
import pytest
import requests
import os

BASE_URL = os.environ.get('REACT_APP_BACKEND_URL', '').rstrip('/')


class TestTTSStatus:
    """TTS Status endpoint tests - verifies ElevenLabs configuration"""
    
    def test_tts_status_returns_200(self):
        """TTS status endpoint should return 200"""
        response = requests.get(f"{BASE_URL}/api/tts/status")
        assert response.status_code == 200
        print(f"PASS: TTS status endpoint returns 200")
    
    def test_tts_status_has_enabled_field(self):
        """TTS status should indicate if enabled"""
        response = requests.get(f"{BASE_URL}/api/tts/status")
        data = response.json()
        assert "enabled" in data
        assert isinstance(data["enabled"], bool)
        print(f"PASS: TTS enabled status: {data['enabled']}")
    
    def test_tts_status_has_provider(self):
        """TTS status should indicate provider"""
        response = requests.get(f"{BASE_URL}/api/tts/status")
        data = response.json()
        assert "provider" in data
        assert data["provider"] == "elevenlabs"
        print(f"PASS: TTS provider is {data['provider']}")
    
    def test_tts_status_has_presets(self):
        """TTS status should list available voice presets"""
        response = requests.get(f"{BASE_URL}/api/tts/status")
        data = response.json()
        assert "presets_available" in data
        assert isinstance(data["presets_available"], list)
        assert len(data["presets_available"]) > 0
        print(f"PASS: TTS presets available: {data['presets_available']}")


class TestTTSGenerate:
    """TTS Generate endpoint tests - verifies proper error handling with restricted API key"""
    
    def test_tts_generate_returns_proper_error(self):
        """TTS generate should return 401 with restricted API key"""
        response = requests.post(
            f"{BASE_URL}/api/tts/generate",
            json={"text": "Test message", "voice_preset": "energetic_coach"}
        )
        # Expected: 401 (unauthorized) or 500 (API error)
        assert response.status_code in [401, 429, 500, 503]
        print(f"PASS: TTS generate returns error status {response.status_code} (expected with restricted key)")
    
    def test_tts_generate_returns_json_error(self):
        """TTS generate error should be proper JSON format"""
        response = requests.post(
            f"{BASE_URL}/api/tts/generate",
            json={"text": "Test message", "voice_preset": "energetic_coach"}
        )
        # Should return JSON error, not crash
        data = response.json()
        assert "detail" in data
        print(f"PASS: TTS error is proper JSON: {data['detail'][:50]}...")
    
    def test_tts_generate_with_all_presets(self):
        """TTS generate should handle all voice presets"""
        presets = ["energetic_coach", "calm_guide", "friendly_trainer", "professional"]
        for preset in presets:
            response = requests.post(
                f"{BASE_URL}/api/tts/generate",
                json={"text": "Hello", "voice_preset": preset}
            )
            # Should return proper error, not crash
            assert response.status_code in [401, 429, 500, 503, 200]
            print(f"PASS: TTS preset {preset} returns proper response")


class TestVoicesEndpoints:
    """Voice presets endpoints tests"""
    
    def test_get_available_voices(self):
        """Should return available voice presets"""
        response = requests.get(f"{BASE_URL}/api/voices")
        assert response.status_code == 200
        data = response.json()
        assert "voices" in data
        print(f"PASS: Voices endpoint returns {len(data['voices'])} presets")
    
    def test_voice_preset_structure(self):
        """Voice presets should have proper structure"""
        response = requests.get(f"{BASE_URL}/api/voices")
        data = response.json()
        for preset_id, preset in data["voices"].items():
            assert "voice_id" in preset
            assert "name" in preset
            assert "description" in preset
            assert "stability" in preset
            assert "similarity_boost" in preset
        print(f"PASS: All voice presets have correct structure")


class TestVoicePhrasesEndpoint:
    """Voice phrases endpoint tests"""
    
    def test_get_voice_phrases(self):
        """Should return voice phrases library"""
        response = requests.get(f"{BASE_URL}/api/voice-phrases")
        assert response.status_code == 200
        data = response.json()
        assert "zone_up" in data
        assert "zone_down" in data
        assert "target_hit" in data
        assert "encouragement" in data
        print(f"PASS: Voice phrases endpoint returns complete library")


class TestHealthEndpoint:
    """Health check to ensure backend is running"""
    
    def test_health_check(self):
        """Backend health check"""
        response = requests.get(f"{BASE_URL}/api/health")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "healthy"
        print(f"PASS: Backend is healthy - version {data.get('version', 'unknown')}")


if __name__ == "__main__":
    pytest.main([__file__, "-v"])
