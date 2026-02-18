import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Play, Pause, Square, Heart, Flame, Clock, 
  AlertTriangle, Volume2, VolumeX, Target, Timer,
  ChevronLeft, SkipForward, Mic
} from 'lucide-react';
import { useApp } from '../contexts/AppContext';
import { useElevenLabsVoice } from '../hooks/useElevenLabsVoice';

const ZONES = {
  0: { name: 'Below', color: '#71717a', points: 0 },
  1: { name: 'Rest', color: '#71717a', points: 0 },
  2: { name: 'Warm-Up', color: '#3b82f6', points: 0 },
  3: { name: 'Active', color: '#22c55e', points: 1 },
  4: { name: 'Push', color: '#f97316', points: 2 },
  5: { name: 'Peak', color: '#ef4444', points: 3 }
};

export default function Workout() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { user, ndMode, shouldAnimate, useLiteralLabels, settings, API_URL } = useApp();
  
  // ElevenLabs Voice Hook
  const {
    isEnabled: elevenLabsEnabled,
    isLoading: voiceLoading,
    announceZoneChange,
    announceTargetHit,
    announcePoints,
    announceSegmentChange,
    announceWorkoutStart,
    announceWorkoutComplete,
    announceEncouragement,
    stop: stopVoice
  } = useElevenLabsVoice();
  
  const [status, setStatus] = useState('ready');
  const [heartRate, setHeartRate] = useState(70);
  const [zone, setZone] = useState(1);
  const [elapsedSeconds, setElapsedSeconds] = useState(0);
  const [burnPoints, setBurnPoints] = useState(0);
  const [zoneSeconds, setZoneSeconds] = useState({ 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 });
  const [hrSamples, setHrSamples] = useState([]);
  const [showExitConfirm, setShowExitConfirm] = useState(false);
  const [intensity, setIntensity] = useState('moderate');
  const [voiceEnabled, setVoiceEnabled] = useState(true);
  const [lastAnnouncement, setLastAnnouncement] = useState(0);
  const [microReward, setMicroReward] = useState(null);
  const [calories, setCalories] = useState(0);
  
  // Template state
  const [template, setTemplate] = useState(null);
  const [currentSegmentIndex, setCurrentSegmentIndex] = useState(0);
  const [segmentElapsed, setSegmentElapsed] = useState(0);
  
  const timerRef = useRef(null);
  const hrIntervalRef = useRef(null);
  const pointsRef = useRef(burnPoints);
  
  const isJust5Mode = searchParams.get('mode') === 'just5';
  const templateId = searchParams.get('template');
  const maxTime = isJust5Mode ? 300 : null;
  
  const maxHr = user?.max_hr || 190;
  const dailyTarget = user?.daily_burn_target || 12;

  // Load template if specified
  useEffect(() => {
    if (templateId) {
      fetchTemplate(templateId);
    }
  }, [templateId]);

  const fetchTemplate = async (id) => {
    try {
      const res = await fetch(`${API_URL}/api/templates/${id}`);
      if (res.ok) {
        const data = await res.json();
        setTemplate(data);
      }
    } catch (err) {
      console.error('Failed to fetch template:', err);
    }
  };

  const currentSegment = template?.segments?.[currentSegmentIndex];
  const totalTemplateTime = template?.segments?.reduce((sum, s) => sum + s.duration_seconds, 0) || 0;

  // ADHD Micro-rewards
  const triggerMicroReward = useCallback((message) => {
    if (ndMode !== 'adhd' && ndMode !== 'combined') return;
    
    setMicroReward(message);
    setTimeout(() => setMicroReward(null), 2000);
    
    if (navigator.vibrate && (settings?.sensory?.haptic_level || 100) > 0) {
      navigator.vibrate(100);
    }
  }, [ndMode, settings]);

  // Fetch simulated HR
  const fetchHeartRate = useCallback(async () => {
    if (status !== 'active') return;
    
    try {
      const targetZone = currentSegment?.target_zone;
      const res = await fetch(`${API_URL}/api/simulate-hr`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          user_id: user.id, 
          intensity,
          target_zone: targetZone
        })
      });
      
      if (res.ok) {
        const data = await res.json();
        setHeartRate(data.heart_rate);
        
        const prevZone = zone;
        setZone(data.zone);
        
        // Zone change announcement using ElevenLabs
        if (data.zone !== prevZone && data.zone > 0 && voiceEnabled) {
          const zoneCallouts = settings?.voice_coach?.zone_callouts !== false;
          if (zoneCallouts) {
            announceZoneChange(data.zone_name, data.points_per_minute, data.zone > prevZone);
          }
          
          // Haptic for zone change
          if (navigator.vibrate && (settings?.sensory?.haptic_level || 100) > 0) {
            navigator.vibrate(data.zone > prevZone ? [100, 50, 100] : [50]);
          }
        }
        
        // Record sample
        setHrSamples(prev => [...prev, {
          timestamp: new Date().toISOString(),
          heart_rate: data.heart_rate
        }]);
        
        // Update zone time
        setZoneSeconds(prev => ({
          ...prev,
          [data.zone]: (prev[data.zone] || 0) + 1
        }));
        
        // Update calories (simplified)
        setCalories(prev => prev + (data.heart_rate > 100 ? 0.15 : 0.08));
      }
    } catch (err) {
      console.error('HR fetch error:', err);
    }
  }, [status, user, intensity, zone, voiceEnabled, settings, currentSegment, API_URL, announceZoneChange]);

  // Update burn points every minute
  useEffect(() => {
    const updatePoints = () => {
      let newPoints = 0;
      Object.entries(zoneSeconds).forEach(([z, seconds]) => {
        const zoneNum = parseInt(z);
        const minutes = Math.floor(seconds / 60);
        newPoints += minutes * (ZONES[zoneNum]?.points || 0);
      });
      
      const prevPoints = pointsRef.current;
      if (newPoints > prevPoints) {
        setBurnPoints(newPoints);
        pointsRef.current = newPoints;
        
        triggerMicroReward(`+${newPoints - prevPoints} Burn Point${newPoints - prevPoints > 1 ? 's' : ''}!`);
        
        // Point update announcement using ElevenLabs
        if (voiceEnabled) {
          const pointUpdates = settings?.voice_coach?.point_updates || 'every_3';
          if (pointUpdates === 'every' || 
              (pointUpdates === 'every_3' && newPoints % 3 === 0) ||
              (pointUpdates === 'target_only' && newPoints === dailyTarget)) {
            announcePoints(newPoints, dailyTarget - newPoints);
          }
        }
        
        // Target hit announcement
        if (newPoints >= dailyTarget && prevPoints < dailyTarget) {
          if (voiceEnabled) {
            announceTargetHit(dailyTarget);
          }
          triggerMicroReward('TARGET HIT! 🎯');
        }
      }
    };
    
    if (status === 'active') {
      updatePoints();
    }
  }, [zoneSeconds, status, dailyTarget, voiceEnabled, triggerMicroReward, settings, announcePoints, announceTargetHit]);

  // Template segment management
  useEffect(() => {
    if (!template || status !== 'active') return;
    
    const segment = template.segments[currentSegmentIndex];
    if (!segment) return;
    
    if (segmentElapsed >= segment.duration_seconds) {
      // Move to next segment
      if (currentSegmentIndex < template.segments.length - 1) {
        const nextSegment = template.segments[currentSegmentIndex + 1];
        
        // Segment change announcement using ElevenLabs
        if (voiceEnabled && settings?.voice_coach?.interval_countdown !== false) {
          announceSegmentChange(nextSegment.name, ZONES[nextSegment.target_zone]?.name);
        }
        
        setCurrentSegmentIndex(prev => prev + 1);
        setSegmentElapsed(0);
        
        // Haptic for segment change
        if (navigator.vibrate && (settings?.sensory?.haptic_level || 100) > 0) {
          navigator.vibrate([100, 50, 100, 50, 100]);
        }
      } else {
        // Template complete
        endWorkout();
      }
    }
  }, [segmentElapsed, currentSegmentIndex, template, status, settings, voiceEnabled, announceSegmentChange]);

  // Timer
  useEffect(() => {
    if (status === 'active') {
      timerRef.current = setInterval(() => {
        setElapsedSeconds(prev => {
          const next = prev + 1;
          
          if (isJust5Mode && next >= maxTime) {
            endWorkout();
          }
          
          // ADHD micro-rewards every 30 seconds
          if ((ndMode === 'adhd' || ndMode === 'combined') && next % 30 === 0 && next > 0) {
            const messages = [
              "Keep it up! 💪",
              "You're crushing it!",
              "Stay in the zone!",
              "Great effort!",
              `${Math.floor(next / 60)} min strong!`
            ];
            triggerMicroReward(messages[Math.floor(Math.random() * messages.length)]);
          }
          
          // Encouragement using ElevenLabs every 2 minutes
          if (voiceEnabled && next % 120 === 0 && next > 0) {
            const encouragement = settings?.voice_coach?.encouragement || 'occasional';
            if (encouragement !== 'off') {
              if (encouragement === 'frequent' || Math.random() > 0.5) {
                announceEncouragement();
              }
            }
          }
          
          return next;
        });
        
        // Update segment elapsed for templates
        if (template) {
          setSegmentElapsed(prev => prev + 1);
        }
      }, 1000);
      
      hrIntervalRef.current = setInterval(fetchHeartRate, 1000);
    } else {
      clearInterval(timerRef.current);
      clearInterval(hrIntervalRef.current);
    }
    
    return () => {
      clearInterval(timerRef.current);
      clearInterval(hrIntervalRef.current);
    };
  }, [status, fetchHeartRate, isJust5Mode, maxTime, ndMode, triggerMicroReward, template, settings, voiceEnabled, announceEncouragement]);

  const startWorkout = () => {
    setStatus('active');
    if (voiceEnabled) {
      announceWorkoutStart(template?.name);
    }
  };

  const pauseWorkout = () => {
    setStatus('paused');
    stopVoice();
  };

  const resumeWorkout = () => {
    setStatus('active');
  };

  const endWorkout = async () => {
    setStatus('ending');
    stopVoice();
    
    try {
      const res = await fetch(`${API_URL}/api/workouts`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          user_id: user.id,
          hr_samples: hrSamples,
          duration_seconds: elapsedSeconds,
          notes: isJust5Mode ? 'Just 5 Minutes session' : template?.name ? `Template: ${template.name}` : null,
          template_id: templateId
        })
      });
      
      if (res.ok) {
        const workout = await res.json();
        navigate(`/summary/${workout.id}`);
      } else {
        throw new Error('Failed to save workout');
      }
    } catch (err) {
      console.error('Save workout error:', err);
      alert('Failed to save workout. Please try again.');
      setStatus('paused');
    }
  };

  const skipSegment = () => {
    if (!template || currentSegmentIndex >= template.segments.length - 1) return;
    setCurrentSegmentIndex(prev => prev + 1);
    setSegmentElapsed(0);
  };

  const handleExit = () => {
    if (elapsedSeconds < 60) {
      navigate('/');
    } else {
      setShowExitConfirm(true);
    }
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const zoneInfo = ZONES[zone] || ZONES[1];
  const hrPercentage = Math.round((heartRate / maxHr) * 100);
  const targetProgress = Math.min((burnPoints / dailyTarget) * 100, 100);
  
  // Check if current zone matches template target
  const isOnTarget = currentSegment ? zone === currentSegment.target_zone : true;

  return (
    <div 
      className="page"
      style={{ 
        background: status === 'active' ? `${zoneInfo.color}15` : undefined,
        transition: 'background 0.5s ease'
      }}
    >
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <button 
          className="btn btn-ghost" 
          onClick={handleExit}
          data-testid="btn-exit"
        >
          <ChevronLeft size={20} />
          {useLiteralLabels ? 'Exit Workout' : 'Exit'}
        </button>
        
        <button
          className="btn btn-ghost"
          onClick={() => setVoiceEnabled(!voiceEnabled)}
          data-testid="btn-voice-toggle"
        >
          {voiceEnabled ? <Volume2 size={20} /> : <VolumeX size={20} />}
        </button>
      </div>

      {/* Template Segment Display */}
      {template && currentSegment && (
        <div 
          className="card"
          style={{ 
            marginBottom: '1rem',
            background: isOnTarget ? `${ZONES[currentSegment.target_zone].color}20` : 'var(--zone-peak)20',
            borderColor: isOnTarget ? ZONES[currentSegment.target_zone].color : 'var(--zone-peak)'
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Timer size={18} />
              <span className="font-heading" style={{ fontWeight: 700 }}>{currentSegment.name}</span>
            </div>
            <span style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
              {currentSegmentIndex + 1} / {template.segments.length}
            </span>
          </div>
          
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
            <Target size={16} color={ZONES[currentSegment.target_zone].color} />
            <span style={{ fontSize: '0.875rem' }}>
              Target: <strong style={{ color: ZONES[currentSegment.target_zone].color }}>
                {ZONES[currentSegment.target_zone].name}
              </strong>
            </span>
            {!isOnTarget && (
              <span style={{ color: 'var(--zone-peak)', fontSize: '0.75rem' }}>
                (Push harder!)
              </span>
            )}
          </div>
          
          {/* Segment progress */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <div className="progress-bar" style={{ flex: 1 }}>
              <div 
                className="progress-fill"
                style={{ 
                  width: `${(segmentElapsed / currentSegment.duration_seconds) * 100}%`,
                  background: ZONES[currentSegment.target_zone].color
                }}
              />
            </div>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
              {formatTime(currentSegment.duration_seconds - segmentElapsed)}
            </span>
            {status === 'active' && currentSegmentIndex < template.segments.length - 1 && (
              <button 
                className="btn btn-ghost" 
                style={{ padding: '0.25rem' }}
                onClick={skipSegment}
                title="Skip to next segment"
              >
                <SkipForward size={16} />
              </button>
            )}
          </div>
        </div>
      )}

      {/* Just 5 Minutes Banner */}
      {isJust5Mode && (
        <div 
          className="card"
          style={{ 
            background: 'var(--primary)', 
            color: 'white', 
            textAlign: 'center',
            marginBottom: '1rem',
            padding: '0.75rem'
          }}
        >
          <Clock size={16} style={{ display: 'inline', marginRight: '0.5rem' }} />
          Just 5 Minutes Mode • {formatTime(maxTime - elapsedSeconds)} remaining
        </div>
      )}

      {/* Main Display */}
      <div className="card card-highlight" style={{ textAlign: 'center', marginBottom: '1rem' }}>
        {/* Heart Rate */}
        <div style={{ marginBottom: '1.5rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
            <Heart 
              size={32} 
              color={zoneInfo.color}
              style={{ animation: status === 'active' ? 'pulse-ring 1s ease-in-out infinite' : undefined }}
            />
            <span 
              className="font-heading"
              style={{ fontSize: '4rem', fontWeight: 900, color: zoneInfo.color }}
            >
              {heartRate}
            </span>
            <span style={{ color: 'var(--text-muted)', alignSelf: 'flex-end', marginBottom: '0.75rem' }}>BPM</span>
          </div>
          <p style={{ color: 'var(--text-muted)' }}>{hrPercentage}% of max HR</p>
        </div>

        {/* Zone Display */}
        <div 
          style={{ 
            padding: '1rem',
            borderRadius: '1rem',
            background: `${zoneInfo.color}30`,
            marginBottom: '1.5rem'
          }}
        >
          <div 
            className="font-heading"
            style={{ 
              fontSize: '2rem', 
              fontWeight: 900, 
              color: zoneInfo.color,
              textTransform: 'uppercase'
            }}
          >
            {zoneInfo.name}
          </div>
          <p style={{ color: 'var(--text-secondary)' }}>
            {zoneInfo.points > 0 ? `+${zoneInfo.points} pts/min` : 'No points'}
          </p>
        </div>

        {/* Zone Bar */}
        <div style={{ display: 'flex', gap: '4px', marginBottom: '1.5rem' }}>
          {[1, 2, 3, 4, 5].map((z) => (
            <div
              key={z}
              style={{
                flex: 1,
                height: '8px',
                borderRadius: '4px',
                background: zone >= z ? ZONES[z].color : 'var(--bg-subtle)',
                transition: 'background 0.3s'
              }}
            />
          ))}
        </div>

        {/* Burn Points */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
          <Flame size={28} color="var(--zone-push)" />
          <span className="font-heading" style={{ fontSize: '3rem', fontWeight: 900 }}>
            {burnPoints}
          </span>
          <span style={{ color: 'var(--text-muted)' }}>/ {dailyTarget}</span>
        </div>
        
        <div className="progress-bar" style={{ marginBottom: '1rem' }}>
          <div 
            className="progress-fill"
            style={{ 
              width: `${targetProgress}%`,
              background: targetProgress >= 100 
                ? 'var(--secondary)' 
                : 'linear-gradient(90deg, var(--zone-active), var(--zone-push))'
            }}
          />
        </div>

        {/* Timer and Calories */}
        <div style={{ display: 'flex', justifyContent: 'center', gap: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Clock size={20} color="var(--text-muted)" />
            <span className="font-mono" style={{ fontSize: '1.5rem' }}>
              {formatTime(elapsedSeconds)}
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Flame size={20} color="var(--text-muted)" />
            <span style={{ fontSize: '1rem', color: 'var(--text-secondary)' }}>
              ~{Math.round(calories)} kcal
            </span>
          </div>
        </div>
      </div>

      {/* Intensity Selector (hide when using template) */}
      {!template && (
        <div className="card" style={{ marginBottom: '1rem' }}>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>
            Simulate workout intensity:
          </p>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            {['easy', 'moderate', 'hard', 'interval'].map((level) => (
              <button
                key={level}
                className={`btn ${intensity === level ? 'btn-primary' : 'btn-secondary'}`}
                style={{ flex: 1, padding: '0.5rem', fontSize: '0.75rem', textTransform: 'capitalize' }}
                onClick={() => setIntensity(level)}
                data-testid={`intensity-${level}`}
              >
                {level}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Control Buttons */}
      <div style={{ display: 'flex', gap: '1rem' }}>
        {status === 'ready' && (
          <button
            className="btn btn-primary btn-large"
            style={{ flex: 1 }}
            onClick={startWorkout}
            data-testid="btn-start-workout"
          >
            <Play size={24} />
            {useLiteralLabels ? 'Start Workout' : 'Start'}
          </button>
        )}
        
        {status === 'active' && (
          <>
            <button
              className="btn btn-secondary btn-large"
              style={{ flex: 1 }}
              onClick={pauseWorkout}
              data-testid="btn-pause"
            >
              <Pause size={24} />
              Pause
            </button>
            <button
              className="btn btn-danger btn-large"
              onClick={() => setShowExitConfirm(true)}
              data-testid="btn-end"
            >
              <Square size={24} />
            </button>
          </>
        )}
        
        {status === 'paused' && (
          <>
            <button
              className="btn btn-primary btn-large"
              style={{ flex: 1 }}
              onClick={resumeWorkout}
              data-testid="btn-resume"
            >
              <Play size={24} />
              Resume
            </button>
            <button
              className="btn btn-danger btn-large"
              onClick={() => setShowExitConfirm(true)}
              data-testid="btn-end-paused"
            >
              <Square size={24} />
            </button>
          </>
        )}
      </div>

      {/* Micro-Reward Toast */}
      <AnimatePresence>
        {microReward && shouldAnimate && (
          <motion.div
            initial={{ opacity: 0, y: 50, scale: 0.8 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -20, scale: 0.8 }}
            style={{
              position: 'fixed',
              bottom: '150px',
              left: '50%',
              transform: 'translateX(-50%)',
              background: 'var(--primary)',
              color: 'white',
              padding: '0.75rem 1.5rem',
              borderRadius: '9999px',
              fontWeight: 700,
              boxShadow: '0 0 30px rgba(139, 92, 246, 0.5)',
              zIndex: 1000
            }}
          >
            {microReward}
          </motion.div>
        )}
      </AnimatePresence>

      {/* Exit Confirmation Modal */}
      {showExitConfirm && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(0,0,0,0.8)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '1rem',
            zIndex: 1000
          }}
        >
          <div className="card" style={{ maxWidth: '320px', textAlign: 'center' }}>
            <AlertTriangle size={48} color="var(--zone-push)" style={{ marginBottom: '1rem' }} />
            <h3 style={{ marginBottom: '0.5rem' }}>
              {useLiteralLabels ? 'End Workout Session?' : 'End Workout?'}
            </h3>
            <p style={{ color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
              {elapsedSeconds >= 60 
                ? `You've earned ${burnPoints} Burn Points. Save your progress?`
                : 'You haven\'t started yet. Exit without saving?'
              }
            </p>
            
            {(ndMode === 'asd' || ndMode === 'combined') && (
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '1rem' }}>
                It's okay to stop. Any movement is good movement.
              </p>
            )}
            
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <button
                className="btn btn-secondary"
                style={{ flex: 1 }}
                onClick={() => setShowExitConfirm(false)}
                data-testid="btn-cancel-exit"
              >
                Continue
              </button>
              <button
                className="btn btn-primary"
                style={{ flex: 1 }}
                onClick={elapsedSeconds >= 60 ? endWorkout : () => navigate('/')}
                data-testid="btn-confirm-exit"
              >
                {elapsedSeconds >= 60 ? 'Save & Exit' : 'Exit'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
