import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { 
  Trophy, Flame, Star, Crown, Medal, Zap, 
  Clock, Sunrise, Moon, CheckCircle, Mountain, Layers,
  Home as HomeIcon, History as HistoryIcon, Settings as SettingsIcon, Award
} from 'lucide-react';
import { useApp } from '../contexts/AppContext';

const ACHIEVEMENT_ICONS = {
  trophy: Trophy,
  flame: Flame,
  fire: Flame,
  star: Star,
  crown: Crown,
  medal: Medal,
  zap: Zap,
  clock: Clock,
  sunrise: Sunrise,
  moon: Moon,
  'check-circle': CheckCircle,
  mountain: Mountain,
  layers: Layers
};

const CATEGORY_COLORS = {
  milestone: '#8b5cf6',
  streak: '#f97316',
  points: '#ef4444',
  workout: '#22c55e',
  time: '#3b82f6',
  consistency: '#10b981'
};

export default function Achievements() {
  const { user, shouldAnimate, API_URL } = useApp();
  
  const [achievements, setAchievements] = useState([]);
  const [personalBests, setPersonalBests] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState('all');

  useEffect(() => {
    if (user) {
      fetchAchievements();
      fetchPersonalBests();
    }
  }, [user]);

  const fetchAchievements = async () => {
    try {
      const res = await fetch(`${API_URL}/api/users/${user.id}/achievements`);
      if (res.ok) {
        const data = await res.json();
        setAchievements(data.achievements || []);
      }
    } catch (err) {
      console.error('Failed to fetch achievements:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchPersonalBests = async () => {
    try {
      const res = await fetch(`${API_URL}/api/users/${user.id}/personal-bests`);
      if (res.ok) {
        const data = await res.json();
        setPersonalBests(data);
      }
    } catch (err) {
      console.error('Failed to fetch personal bests:', err);
    }
  };

  const categories = ['all', 'milestone', 'streak', 'points', 'workout', 'time'];
  
  const filteredAchievements = selectedCategory === 'all'
    ? achievements
    : achievements.filter(a => a.category === selectedCategory);
  
  const unlockedCount = achievements.filter(a => a.unlocked).length;
  const totalCount = achievements.length;

  const formatDuration = (seconds) => {
    const mins = Math.floor(seconds / 60);
    return mins >= 60 ? `${Math.floor(mins / 60)}h ${mins % 60}m` : `${mins} min`;
  };

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header">
        <h1 className="page-title">Achievements</h1>
        <span className="badge badge-primary" style={{ fontSize: '0.875rem' }}>
          {unlockedCount} / {totalCount}
        </span>
      </div>

      {/* Progress Card */}
      <div className="card card-highlight" style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{
            width: '64px',
            height: '64px',
            borderRadius: '50%',
            background: 'linear-gradient(135deg, var(--primary), var(--accent))',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
            <Trophy size={32} color="white" />
          </div>
          <div style={{ flex: 1 }}>
            <div className="font-heading" style={{ fontSize: '1.5rem', fontWeight: 700 }}>
              {Math.round((unlockedCount / totalCount) * 100)}% Complete
            </div>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
              {unlockedCount} achievements unlocked
            </p>
          </div>
        </div>
        
        <div className="progress-bar" style={{ marginTop: '1rem' }}>
          <div 
            className="progress-fill"
            style={{ 
              width: `${(unlockedCount / totalCount) * 100}%`,
              background: 'linear-gradient(90deg, var(--primary), var(--secondary))'
            }}
          />
        </div>
      </div>

      {/* Personal Bests */}
      {personalBests && (
        <div style={{ marginBottom: '1.5rem' }}>
          <h2 style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
            Personal Bests
          </h2>
          
          <div className="stats-grid">
            <div className="stat-card">
              <Flame size={20} color="var(--zone-push)" />
              <div className="stat-value" style={{ fontSize: '1.25rem' }}>
                {personalBests.max_session_points || 0}
              </div>
              <div className="stat-label">Best Session</div>
            </div>
            
            <div className="stat-card">
              <Clock size={20} color="var(--primary)" />
              <div className="stat-value" style={{ fontSize: '1.25rem' }}>
                {personalBests.longest_workout_seconds ? formatDuration(personalBests.longest_workout_seconds) : '0 min'}
              </div>
              <div className="stat-label">Longest Workout</div>
            </div>
            
            <div className="stat-card">
              <Zap size={20} color="var(--zone-peak)" />
              <div className="stat-value" style={{ fontSize: '1.25rem' }}>
                {personalBests.longest_peak_seconds ? formatDuration(personalBests.longest_peak_seconds) : '0 min'}
              </div>
              <div className="stat-label">Longest Peak</div>
            </div>
            
            <div className="stat-card">
              <Flame size={20} color="var(--secondary)" />
              <div className="stat-value" style={{ fontSize: '1.25rem' }}>
                {user?.streak_days || 0}
              </div>
              <div className="stat-label">Best Streak</div>
            </div>
          </div>
        </div>
      )}

      {/* Category Filter */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem', overflowX: 'auto', paddingBottom: '0.5rem' }}>
        {categories.map(cat => (
          <button
            key={cat}
            className={`btn ${selectedCategory === cat ? 'btn-primary' : 'btn-secondary'}`}
            style={{ padding: '0.5rem 1rem', fontSize: '0.875rem', textTransform: 'capitalize', whiteSpace: 'nowrap' }}
            onClick={() => setSelectedCategory(cat)}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Achievements List */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem' }}>
          <div className="pulse-ring" style={{ margin: '0 auto' }}></div>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginBottom: '5rem' }}>
          {filteredAchievements.map((achievement, index) => {
            const Icon = ACHIEVEMENT_ICONS[achievement.icon] || Trophy;
            const color = CATEGORY_COLORS[achievement.category] || '#8b5cf6';
            
            return (
              <motion.div
                key={achievement.id}
                initial={shouldAnimate ? { opacity: 0, x: -20 } : undefined}
                animate={shouldAnimate ? { opacity: 1, x: 0 } : undefined}
                transition={{ delay: index * 0.03 }}
                className="card"
                style={{
                  opacity: achievement.unlocked ? 1 : 0.5,
                  border: achievement.unlocked ? `1px solid ${color}50` : undefined
                }}
                data-testid={`achievement-${achievement.id}`}
              >
                <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                  <div style={{
                    width: '48px',
                    height: '48px',
                    borderRadius: '12px',
                    background: achievement.unlocked ? `${color}30` : 'var(--bg-subtle)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}>
                    <Icon size={24} color={achievement.unlocked ? color : 'var(--text-muted)'} />
                  </div>
                  
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                      <h3 style={{ fontSize: '1rem' }}>{achievement.name}</h3>
                      {achievement.unlocked && (
                        <CheckCircle size={16} color={color} />
                      )}
                    </div>
                    <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
                      {achievement.description}
                    </p>
                    {achievement.unlocked && achievement.unlocked_at && (
                      <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                        Unlocked {new Date(achievement.unlocked_at).toLocaleDateString()}
                      </p>
                    )}
                  </div>
                  
                  <span className="badge" style={{ background: `${color}20`, color }}>
                    +{achievement.xp_reward} XP
                  </span>
                </div>
              </motion.div>
            );
          })}
        </div>
      )}

      {/* Bottom Navigation */}
      <nav className="bottom-nav">
        <Link to="/" className="nav-item" data-testid="nav-home">
          <HomeIcon size={24} />
          <span>Home</span>
        </Link>
        <Link to="/achievements" className="nav-item active" data-testid="nav-achievements">
          <Award size={24} />
          <span>Awards</span>
        </Link>
        <Link to="/history" className="nav-item" data-testid="nav-history">
          <HistoryIcon size={24} />
          <span>History</span>
        </Link>
        <Link to="/settings" className="nav-item" data-testid="nav-settings">
          <SettingsIcon size={24} />
          <span>Settings</span>
        </Link>
      </nav>
    </div>
  );
}
