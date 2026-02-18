import { useState, useCallback, useRef, useEffect } from 'react';
import { useApp } from '../contexts/AppContext';

const VOICE_PRESETS = {
  energetic_coach: { name: 'Energetic Coach', description: 'High-energy motivational voice' },
  calm_guide: { name: 'Calm Guide', description: 'Soothing voice for ASD mode' },
  friendly_trainer: { name: 'Friendly Trainer', description: 'Warm, encouraging voice' },
  professional: { name: 'Professional', description: 'Clear, professional announcements' }
};

export function useElevenLabsVoice() {
  const { settings, ndMode, API_URL } = useApp();
  const [isLoading, setIsLoading] = useState(false);
  const [elevenLabsEnabled, setElevenLabsEnabled] = useState(false);
  const [error, setError] = useState(null);
  const audioRef = useRef(null);
  const queueRef = useRef([]);
  const isPlayingRef = useRef(false);
  const lastAnnouncementRef = useRef(0);
  const failCountRef = useRef(0);

  // Check if ElevenLabs is available
  useEffect(() => {
    checkTTSStatus();
  }, []);

  const checkTTSStatus = async () => {
    try {
      const res = await fetch(`${API_URL}/api/tts/status`);
      if (res.ok) {
        const data = await res.json();
        setElevenLabsEnabled(data.enabled);
      }
    } catch (err) {
      console.log('TTS status check failed, using browser TTS');
      setElevenLabsEnabled(false);
    }
  };

  // Get voice preset based on ND mode
  const getVoicePreset = useCallback(() => {
    const voiceSetting = settings?.voice_coach?.voice_selection || 'auto';
    
    if (voiceSetting === 'auto') {
      if (ndMode === 'asd' || ndMode === 'combined') {
        return 'calm_guide';
      }
      return 'energetic_coach';
    }
    
    return voiceSetting;
  }, [settings, ndMode]);

  // Browser TTS fallback
  const speakWithBrowser = useCallback((text) => {
    if (!window.speechSynthesis) return;
    
    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.volume = (settings?.voice_coach?.volume || 80) / 100;
    utterance.rate = settings?.voice_coach?.speech_rate === 'slow' ? 0.8 : 
                     settings?.voice_coach?.speech_rate === 'fast' ? 1.2 : 1;
    window.speechSynthesis.speak(utterance);
    lastAnnouncementRef.current = Date.now();
  }, [settings]);

  // Generate and play TTS (ElevenLabs or fallback)
  const speak = useCallback(async (text, options = {}) => {
    if (settings?.voice_coach?.enabled === false) return;

    // Check minimum gap between announcements
    const now = Date.now();
    const minGap = (settings?.voice_coach?.min_gap_seconds || 15) * 1000;
    if (!options.force && now - lastAnnouncementRef.current < minGap) {
      return;
    }

    // If ElevenLabs has failed too many times, use browser TTS directly
    if (failCountRef.current >= 3 || !elevenLabsEnabled) {
      speakWithBrowser(text);
      return;
    }

    // Add to queue
    queueRef.current.push({ text, options });
    processQueue();
  }, [elevenLabsEnabled, settings, speakWithBrowser, API_URL]);

  const processQueue = async () => {
    if (isPlayingRef.current || queueRef.current.length === 0) {
      return;
    }

    isPlayingRef.current = true;
    const { text, options } = queueRef.current.shift();

    try {
      setIsLoading(true);
      setError(null);

      const res = await fetch(`${API_URL}/api/tts/generate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          text,
          voice_preset: options.voicePreset || getVoicePreset(),
          stability: options.stability,
          similarity_boost: options.similarity
        })
      });

      if (!res.ok) {
        // Handle specific error codes for faster fallback
        if (res.status === 401 || res.status === 429 || res.status === 503) {
          // API key issues, quota exceeded, or not configured - disable ElevenLabs permanently
          setElevenLabsEnabled(false);
          failCountRef.current = 3; // Force fallback mode
          console.log('ElevenLabs unavailable, switching to browser TTS permanently');
        }
        const errorData = await res.json().catch(() => ({}));
        throw new Error(errorData.detail || 'TTS generation failed');
      }

      const data = await res.json();
      
      // Create audio from base64
      const audioData = atob(data.audio_base64);
      const audioArray = new Uint8Array(audioData.length);
      for (let i = 0; i < audioData.length; i++) {
        audioArray[i] = audioData.charCodeAt(i);
      }
      const audioBlob = new Blob([audioArray], { type: 'audio/mp3' });
      const audioUrl = URL.createObjectURL(audioBlob);

      // Play audio
      if (audioRef.current) {
        audioRef.current.pause();
      }
      
      const audio = new Audio(audioUrl);
      audio.volume = (settings?.voice_coach?.volume || 80) / 100;
      audioRef.current = audio;
      
      audio.onended = () => {
        URL.revokeObjectURL(audioUrl);
        isPlayingRef.current = false;
        lastAnnouncementRef.current = Date.now();
        failCountRef.current = 0; // Reset fail count on success
        processQueue();
      };
      
      audio.onerror = () => {
        URL.revokeObjectURL(audioUrl);
        isPlayingRef.current = false;
        processQueue();
      };

      await audio.play();
      lastAnnouncementRef.current = Date.now();

    } catch (err) {
      console.log('ElevenLabs TTS failed, using browser fallback:', err.message);
      setError(err.message);
      failCountRef.current++;
      isPlayingRef.current = false;
      
      // Fallback to browser TTS immediately
      speakWithBrowser(text);
      
      processQueue();
    } finally {
      setIsLoading(false);
    }
  };

  // Pre-defined announcements
  const announceZoneChange = useCallback(async (zoneName, pointsPerMin, isUp = true) => {
    const text = isUp 
      ? `You're now in ${zoneName} zone. ${pointsPerMin} points per minute.`
      : `Dropped to ${zoneName} zone.`;
    
    if (ndMode === 'asd' || (ndMode === 'combined' && settings?.sensory?.literal_labels)) {
      const literalText = isUp
        ? `Heart rate zone: ${zoneName}. Points per minute: ${pointsPerMin}.`
        : `Heart rate zone: ${zoneName}.`;
      await speak(literalText);
    } else {
      await speak(text);
    }
  }, [speak, ndMode, settings]);

  const announceTargetHit = useCallback(async (targetPoints) => {
    const text = ndMode === 'asd'
      ? `Daily target of ${targetPoints} Burn Points reached.`
      : `Congratulations! You've hit your daily target of ${targetPoints} Burn Points! Amazing work!`;
    await speak(text, { force: true });
  }, [speak, ndMode]);

  const announcePoints = useCallback(async (points, remaining) => {
    const text = ndMode === 'asd'
      ? `Current Burn Points: ${points}. Remaining: ${remaining}.`
      : `${points} Burn Points earned. ${remaining} to go!`;
    await speak(text);
  }, [speak, ndMode]);

  const announceSegmentChange = useCallback(async (segmentName, targetZone) => {
    const text = ndMode === 'asd'
      ? `Segment: ${segmentName}. Target zone: ${targetZone}.`
      : `Next up: ${segmentName}. Aim for ${targetZone} zone. Let's go!`;
    await speak(text, { force: true });
  }, [speak, ndMode]);

  const announceWorkoutStart = useCallback(async (templateName = null) => {
    let text = ndMode === 'asd'
      ? 'Workout started.'
      : 'Workout started. Let\'s go!';
    
    if (templateName) {
      text = ndMode === 'asd'
        ? `Starting ${templateName} workout.`
        : `Starting ${templateName}. Let's crush it!`;
    }
    await speak(text, { force: true });
  }, [speak, ndMode]);

  const announceWorkoutComplete = useCallback(async (points, duration) => {
    const mins = Math.floor(duration / 60);
    const text = ndMode === 'asd'
      ? `Workout complete. Duration: ${mins} minutes. Burn Points: ${points}.`
      : `Workout complete! You earned ${points} Burn Points in ${mins} minutes. Great job!`;
    await speak(text, { force: true });
  }, [speak, ndMode]);

  const announceEncouragement = useCallback(async () => {
    const phrases = ndMode === 'asd'
      ? ['Continuing workout.', 'Keep going.', 'Good progress.']
      : [
          "You're doing great!",
          "Keep it up!",
          "Strong effort!",
          "Stay in the zone!",
          "You've got this!",
          "Push through!",
          "Excellent work!"
        ];
    const text = phrases[Math.floor(Math.random() * phrases.length)];
    await speak(text);
  }, [speak, ndMode]);

  const stop = useCallback(() => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current = null;
    }
    queueRef.current = [];
    isPlayingRef.current = false;
    window.speechSynthesis?.cancel();
  }, []);

  return {
    speak,
    stop,
    isLoading,
    isEnabled: true, // Always enabled (with browser fallback)
    elevenLabsActive: elevenLabsEnabled && failCountRef.current < 3,
    error,
    voicePresets: VOICE_PRESETS,
    currentPreset: getVoicePreset(),
    // Convenience methods
    announceZoneChange,
    announceTargetHit,
    announcePoints,
    announceSegmentChange,
    announceWorkoutStart,
    announceWorkoutComplete,
    announceEncouragement
  };
}
