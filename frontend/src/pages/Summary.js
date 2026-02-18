import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { 
  Flame, Clock, Heart, TrendingUp, Award, Share2, 
  Home, ChevronRight, Target, Zap, CheckCircle
} from 'lucide-react';
import { useApp } from '../contexts/AppContext';

const ZONES = {
  1: { name: 'Rest', color: '#71717a' },
  2: { name: 'Warm-Up', color: '#3b82f6' },
  3: { name: 'Active', color: '#22c55e' },
  4: { name: 'Push', color: '#f97316' },
  5: { name: 'Peak', color: '#ef4444' }
};

export default function Summary() {
  const { workoutId } = useParams();
  const navigate = useNavigate();
  const { user, refreshStats, refreshUser, shouldAnimate, ndMode, API_URL } = useApp();
  
  const [workout, setWorkout] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showCelebration, setShowCelebration] = useState(false);

  useEffect(() => {
    fetchWorkout();
    refreshStats();
    refreshUser();
  }, [workoutId]);

  useEffect(() => {
    if (workout?.target_hit && shouldAnimate) {
      setShowCelebration(true);
      setTimeout(() => setShowCelebration(false), 3000);
    }
  }, [workout, shouldAnimate]);

  const fetchWorkout = async () => {
    try {
      const res = await fetch(`${API_URL}/api/workouts/${workoutId}`);
      if (res.ok) {
        const data = await res.json();
        setWorkout(data);
      } else {
        navigate('/');
      }
    } catch (err) {
      console.error('Failed to fetch workout:', err);
      navigate('/');
    } finally {
      setLoading(false);
    }
  };

  const formatDuration = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: 'PulseFit Workout',
          text: `I earned ${workout.total_burn_points} Burn Points in my ${formatDuration(workout.duration_seconds)} workout! 🔥`,
          url: window.location.href
        });
      } catch (err) {
        console.log('Share cancelled');
      }
    }
  };

  if (loading) {
    return (
      <div className="page" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <div className="pulse-loader">
          <div className="pulse-ring"></div>
          <span className="pulse-text">Loading...</span>
        </div>
      </div>
    );
  }

  if (!workout) return null;

  const totalZoneTime = workout.zones?.reduce((sum, z) => sum + z.duration_seconds, 0) || workout.duration_seconds;

  return (
    <div className="page">
      {/* Celebration Overlay */}
      {showCelebration && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(16, 185, 129, 0.2)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
            pointerEvents: 'none'
          }}
        >
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: [0, 1.2, 1] }}
            transition={{ duration: 0.5 }}
            style={{
              width: '150px',
              height: '150px',
              borderRadius: '50%',
              background: 'var(--secondary)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            <CheckCircle size={80} color="white" />
          </motion.div>
        </motion.div>
      )}

      {/* Header */}
      <div style={{ textAlign: 'center', marginBottom: '2rem', paddingTop: '1rem' }}>
        <motion.div
          initial={shouldAnimate ? { scale: 0 } : undefined}
          animate={shouldAnimate ? { scale: 1 } : undefined}
          transition={{ type: 'spring', stiffness: 200 }}
          style={{
            width: '80px',
            height: '80px',
            margin: '0 auto 1rem',
            borderRadius: '50%',
            background: workout.target_hit 
              ? 'linear-gradient(135deg, var(--secondary), var(--zone-active))'
              : 'linear-gradient(135deg, var(--primary), var(--zone-push))',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}
        >
          {workout.target_hit ? (
            <Target size={40} color="white" />
          ) : (
            <Flame size={40} color="white" />
          )}
        </motion.div>
        
        <h1 className="page-title" style={{ marginBottom: '0.5rem' }}>
          {workout.target_hit ? 'Target Hit!' : 'Workout Complete'}
        </h1>
        <p style={{ color: 'var(--text-muted)' }}>
          Great effort, {user?.name}!
        </p>
      </div>

      {/* Main Stats Card */}
      <div className="card card-highlight" style={{ textAlign: 'center', marginBottom: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
          <Flame size={32} color="var(--zone-push)" />
          <span className="font-heading" style={{ fontSize: '4rem', fontWeight: 900 }}>
            {workout.total_burn_points}
          </span>
        </div>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '1rem' }}>Burn Points Earned</p>
        
        {workout.target_hit && (
          <span className="badge badge-success" style={{ fontSize: '0.875rem' }}>
            <CheckCircle size={14} />
            Daily Target Complete!
          </span>
        )}
      </div>

      {/* XP Earned */}
      <motion.div
        initial={shouldAnimate ? { opacity: 0, y: 20 } : undefined}
        animate={shouldAnimate ? { opacity: 1, y: 0 } : undefined}
        transition={{ delay: 0.2 }}
        className="card"
        style={{ marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '1rem' }}
      >
        <div style={{
          width: '48px',
          height: '48px',
          borderRadius: '12px',
          background: 'rgba(139, 92, 246, 0.2)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}>
          <Zap size={24} color="var(--primary)" />
        </div>
        <div>
          <div className="font-heading" style={{ fontSize: '1.5rem', fontWeight: 700 }}>
            +{workout.xp_earned} XP
          </div>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Level {user?.level || 1}
          </p>
        </div>
      </motion.div>

      {/* Stats Grid */}
      <div className="stats-grid" style={{ marginBottom: '1rem' }}>
        <div className="stat-card">
          <Clock size={20} color="var(--text-muted)" />
          <div className="stat-value" style={{ fontSize: '1.5rem' }}>
            {formatDuration(workout.duration_seconds)}
          </div>
          <div className="stat-label">Duration</div>
        </div>
        
        <div className="stat-card">
          <Heart size={20} color="var(--zone-peak)" />
          <div className="stat-value" style={{ fontSize: '1.5rem' }}>
            {workout.avg_hr}
          </div>
          <div className="stat-label">Avg HR</div>
        </div>
        
        <div className="stat-card">
          <TrendingUp size={20} color="var(--zone-push)" />
          <div className="stat-value" style={{ fontSize: '1.5rem' }}>
            {workout.max_hr}
          </div>
          <div className="stat-label">Max HR</div>
        </div>
        
        <div className="stat-card">
          <Flame size={20} color="var(--zone-active)" />
          <div className="stat-value" style={{ fontSize: '1.5rem' }}>
            {workout.calories_burned}
          </div>
          <div className="stat-label">Calories</div>
        </div>
      </div>

      {/* Zone Breakdown */}
      <div className="card" style={{ marginBottom: '1rem' }}>
        <h3 style={{ marginBottom: '1rem' }}>Zone Breakdown</h3>
        
        {/* Stacked Bar */}
        <div style={{ display: 'flex', height: '24px', borderRadius: '12px', overflow: 'hidden', marginBottom: '1rem' }}>
          {workout.zones?.map((z) => {
            const percentage = totalZoneTime > 0 ? (z.duration_seconds / totalZoneTime) * 100 : 0;
            if (percentage < 1) return null;
            return (
              <div
                key={z.zone}
                style={{
                  width: `${percentage}%`,
                  background: ZONES[z.zone]?.color || '#71717a',
                  minWidth: percentage > 5 ? undefined : '8px'
                }}
              />
            );
          })}
        </div>
        
        {/* Zone Details */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          {workout.zones?.filter(z => z.duration_seconds > 0).map((z) => (
            <div key={z.zone} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <div style={{
                width: '12px',
                height: '12px',
                borderRadius: '3px',
                background: ZONES[z.zone]?.color
              }} />
              <span style={{ flex: 1 }}>{ZONES[z.zone]?.name || z.name}</span>
              <span style={{ color: 'var(--text-muted)' }}>
                {Math.floor(z.duration_seconds / 60)}:{(z.duration_seconds % 60).toString().padStart(2, '0')}
              </span>
              <span style={{ fontWeight: 600, color: z.burn_points > 0 ? 'var(--zone-push)' : 'var(--text-muted)' }}>
                +{z.burn_points} pts
              </span>
            </div>
          ))}
        </div>
      </div>

      {/* Afterburn Estimate */}
      {workout.afterburn_estimate > 0 && (
        <div className="card" style={{ marginBottom: '1rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <Flame size={24} color="var(--secondary)" />
            <div>
              <div style={{ fontWeight: 600 }}>+{workout.afterburn_estimate} kcal Afterburn</div>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                Estimated extra burn over 24h (EPOC effect)
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Action Buttons */}
      <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '2rem' }}>
        <button className="btn btn-ghost" onClick={handleShare} data-testid="btn-share">
          <Share2 size={20} />
          Share
        </button>
        
        <Link 
          to="/" 
          className="btn btn-primary"
          style={{ flex: 1, textDecoration: 'none' }}
          data-testid="btn-done"
        >
          <Home size={20} />
          Done
          <ChevronRight size={20} />
        </Link>
      </div>

      {/* ADHD Motivation for Next Workout */}
      {(ndMode === 'adhd' || ndMode === 'combined') && (
        <div className="card" style={{ background: 'rgba(139, 92, 246, 0.1)', border: '1px solid var(--primary)' }}>
          <p style={{ textAlign: 'center' }}>
            🎯 <strong>Next goal:</strong> Beat today's score with {workout.total_burn_points + 3} Burn Points!
          </p>
        </div>
      )}
    </div>
  );
}
