import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { 
  Play, Flame, Target, TrendingUp, Clock, Award, 
  Home as HomeIcon, History as HistoryIcon, Settings as SettingsIcon,
  Zap, CheckCircle, Circle, Timer, Trophy, ChevronRight,
  Download, Snowflake
} from 'lucide-react';
import { useApp } from '../contexts/AppContext';

export default function Home() {
  const navigate = useNavigate();
  const { user, stats, ndMode, shouldAnimate, useLiteralLabels, refreshStats, refreshUser, API_URL } = useApp();
  const [quests, setQuests] = useState([]);
  const [showStreakFreeze, setShowStreakFreeze] = useState(false);

  useEffect(() => {
    refreshStats();
    fetchQuests();
  }, [refreshStats]);

  const fetchQuests = async () => {
    if (!user) return;
    try {
      const res = await fetch(`${API_URL}/api/users/${user.id}/quests`);
      if (res.ok) {
        const data = await res.json();
        setQuests(data.quests || []);
      }
    } catch (err) {
      console.error('Failed to fetch quests:', err);
    }
  };

  const useStreakFreeze = async () => {
    try {
      const res = await fetch(`${API_URL}/api/users/${user.id}/use-streak-freeze`, {
        method: 'POST'
      });
      if (res.ok) {
        setShowStreakFreeze(false);
        refreshUser();
        refreshStats();
      }
    } catch (err) {
      console.error('Failed to use streak freeze:', err);
    }
  };

  const exportData = async (format) => {
    try {
      const res = await fetch(`${API_URL}/api/users/${user.id}/export?format=${format}`);
      if (res.ok) {
        const blob = await res.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `pulsefit_export_${user.id}.${format}`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
      }
    } catch (err) {
      console.error('Export failed:', err);
    }
  };

  const todayPoints = stats?.today?.burn_points || 0;
  const dailyTarget = user?.daily_burn_target || 12;
  const targetProgress = Math.min((todayPoints / dailyTarget) * 100, 100);
  const streakDays = user?.streak_days || 0;
  const xpProgress = user?.xp ? (user.xp % 100) : 0;
  const freezesAvailable = stats?.streak?.freezes_available || 0;

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: { staggerChildren: 0.1 }
    }
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0 }
  };

  const Wrapper = shouldAnimate ? motion.div : 'div';

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header">
        <div>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Welcome back,</p>
          <h1 className="page-title" style={{ fontSize: '1.5rem' }}>{user?.name}</h1>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Link to="/achievements" className="badge badge-primary" style={{ textDecoration: 'none' }}>
            <Award size={14} />
            Lvl {user?.level || 1}
          </Link>
        </div>
      </div>

      <Wrapper
        variants={shouldAnimate ? containerVariants : undefined}
        initial={shouldAnimate ? "hidden" : undefined}
        animate={shouldAnimate ? "visible" : undefined}
        style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}
      >
        {/* XP Progress */}
        <Wrapper variants={shouldAnimate ? itemVariants : undefined}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
            <Zap size={16} color="var(--primary)" />
            <span style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
              {user?.xp || 0} XP • {100 - xpProgress} to next level
            </span>
          </div>
          <div className="xp-bar">
            <div className="xp-fill" style={{ width: `${xpProgress}%` }} />
          </div>
        </Wrapper>

        {/* Main CTA - GO Button */}
        <Wrapper variants={shouldAnimate ? itemVariants : undefined}>
          <div className="card card-highlight" style={{ textAlign: 'center', padding: '2rem' }}>
            <button
              className="btn btn-primary btn-xl"
              onClick={() => navigate('/workout')}
              data-testid="btn-go"
              style={{
                margin: '0 auto',
                boxShadow: '0 0 40px rgba(139, 92, 246, 0.5)'
              }}
            >
              {useLiteralLabels ? 'START' : 'GO'}
            </button>
            <p style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>
              {useLiteralLabels ? 'Start Free Workout' : 'Tap to start your workout'}
            </p>
            
            <div style={{ display: 'flex', justifyContent: 'center', gap: '0.5rem', marginTop: '0.75rem' }}>
              {ndMode === 'adhd' || ndMode === 'combined' ? (
                <button
                  className="btn btn-ghost"
                  style={{ fontSize: '0.875rem' }}
                  onClick={() => navigate('/workout?mode=just5')}
                  data-testid="btn-just5"
                >
                  <Clock size={16} />
                  Just 5 Min
                </button>
              ) : null}
              
              <button
                className="btn btn-ghost"
                style={{ fontSize: '0.875rem' }}
                onClick={() => navigate('/templates')}
                data-testid="btn-templates"
              >
                <Timer size={16} />
                Templates
              </button>
            </div>
          </div>
        </Wrapper>

        {/* Today's Progress */}
        <Wrapper variants={shouldAnimate ? itemVariants : undefined}>
          <div className="card">
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
              <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Flame size={20} color="var(--zone-push)" />
                Today's Burn
              </h3>
              {stats?.today?.target_hit && (
                <span className="badge badge-success">
                  <CheckCircle size={14} />
                  Target Hit!
                </span>
              )}
            </div>
            
            <div style={{ display: 'flex', alignItems: 'baseline', gap: '0.5rem', marginBottom: '0.5rem' }}>
              <span className="font-heading" style={{ fontSize: '3rem', fontWeight: 900 }}>
                {todayPoints}
              </span>
              <span style={{ color: 'var(--text-muted)' }}>/ {dailyTarget} pts</span>
            </div>
            
            <div className="progress-bar">
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
          </div>
        </Wrapper>

        {/* Stats Grid */}
        <Wrapper variants={shouldAnimate ? itemVariants : undefined}>
          <div className="stats-grid">
            <div 
              className="stat-card" 
              style={{ cursor: 'pointer' }}
              onClick={() => streakDays > 0 && setShowStreakFreeze(true)}
            >
              <Flame size={24} color="var(--zone-push)" style={{ marginBottom: '0.5rem' }} />
              <div className="stat-value">{streakDays}</div>
              <div className="stat-label">Day Streak</div>
              {freezesAvailable > 0 && (
                <span className="badge" style={{ marginTop: '0.25rem', background: 'rgba(59, 130, 246, 0.2)', color: '#3b82f6', fontSize: '0.625rem' }}>
                  <Snowflake size={10} />
                  {freezesAvailable} freeze
                </span>
              )}
            </div>
            
            <Link to="/trends" className="stat-card" style={{ textDecoration: 'none', color: 'inherit' }}>
              <TrendingUp size={24} color="var(--secondary)" style={{ marginBottom: '0.5rem' }} />
              <div className="stat-value">{stats?.week?.burn_points || 0}</div>
              <div className="stat-label">This Week</div>
            </Link>
            
            <div className="stat-card">
              <Target size={24} color="var(--primary)" style={{ marginBottom: '0.5rem' }} />
              <div className="stat-value">{stats?.week?.workout_count || 0}</div>
              <div className="stat-label">Workouts</div>
            </div>
            
            <Link to="/achievements" className="stat-card" style={{ textDecoration: 'none', color: 'inherit' }}>
              <Award size={24} color="var(--accent)" style={{ marginBottom: '0.5rem' }} />
              <div className="stat-value">{user?.xp || 0}</div>
              <div className="stat-label">Total XP</div>
            </Link>
          </div>
        </Wrapper>

        {/* Quick Actions */}
        <Wrapper variants={shouldAnimate ? itemVariants : undefined}>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <Link 
              to="/templates" 
              className="card" 
              style={{ flex: 1, textDecoration: 'none', color: 'inherit', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <Timer size={20} color="var(--primary)" />
                <span>Workout Templates</span>
              </div>
              <ChevronRight size={18} color="var(--text-muted)" />
            </Link>
          </div>
          
          <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
            <Link 
              to="/achievements" 
              className="card" 
              style={{ flex: 1, textDecoration: 'none', color: 'inherit', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <Trophy size={20} color="var(--accent)" />
                <span>Achievements</span>
              </div>
              <ChevronRight size={18} color="var(--text-muted)" />
            </Link>
          </div>
        </Wrapper>

        {/* Daily Quests (ADHD Mode) */}
        {(ndMode === 'adhd' || ndMode === 'combined') && quests.length > 0 && (
          <Wrapper variants={shouldAnimate ? itemVariants : undefined}>
            <div className="card">
              <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
                <Target size={20} color="var(--primary)" />
                Daily Quests
              </h3>
              
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {quests.slice(0, 3).map((quest) => (
                  <div
                    key={quest.id}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '0.75rem',
                      padding: '0.75rem',
                      background: quest.completed ? 'rgba(16, 185, 129, 0.1)' : 'var(--bg-subtle)',
                      borderRadius: '0.75rem',
                      opacity: quest.completed ? 0.7 : 1
                    }}
                    data-testid={`quest-${quest.id}`}
                  >
                    {quest.completed ? (
                      <CheckCircle size={24} color="var(--secondary)" />
                    ) : (
                      <Circle size={24} color="var(--text-muted)" />
                    )}
                    <div style={{ flex: 1 }}>
                      <div style={{ fontWeight: 600 }}>{quest.title}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        {quest.description}
                      </div>
                    </div>
                    <span className="badge badge-primary">
                      +{quest.xp_reward} XP
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </Wrapper>
        )}

        {/* Lifetime Stats */}
        <Wrapper variants={shouldAnimate ? itemVariants : undefined}>
          <div className="card" style={{ marginBottom: '1rem' }}>
            <h3 style={{ marginBottom: '1rem', fontSize: '0.875rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>
              Lifetime Stats
            </h3>
            <div style={{ display: 'flex', justifyContent: 'space-around', textAlign: 'center' }}>
              <div>
                <div className="font-heading" style={{ fontSize: '1.5rem', fontWeight: 700 }}>
                  {stats?.lifetime?.total_burn_points || user?.total_burn_points || 0}
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Total Points</div>
              </div>
              <div>
                <div className="font-heading" style={{ fontSize: '1.5rem', fontWeight: 700 }}>
                  {stats?.lifetime?.total_workouts || user?.total_workouts || 0}
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Workouts</div>
              </div>
              <div>
                <div className="font-heading" style={{ fontSize: '1.5rem', fontWeight: 700 }}>
                  {user?.level || 1}
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Level</div>
              </div>
            </div>
            
            {/* Export Button */}
            <div style={{ marginTop: '1rem', display: 'flex', gap: '0.5rem' }}>
              <button 
                className="btn btn-ghost" 
                style={{ flex: 1, fontSize: '0.75rem' }}
                onClick={() => exportData('json')}
              >
                <Download size={14} /> Export JSON
              </button>
              <button 
                className="btn btn-ghost" 
                style={{ flex: 1, fontSize: '0.75rem' }}
                onClick={() => exportData('csv')}
              >
                <Download size={14} /> Export CSV
              </button>
            </div>
          </div>
        </Wrapper>
      </Wrapper>

      {/* Streak Freeze Modal */}
      {showStreakFreeze && freezesAvailable > 0 && (
        <div style={{
          position: 'fixed',
          inset: 0,
          background: 'rgba(0,0,0,0.8)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '1rem',
          zIndex: 1000
        }}>
          <div className="card" style={{ maxWidth: '320px', textAlign: 'center' }}>
            <Snowflake size={48} color="#3b82f6" style={{ marginBottom: '1rem' }} />
            <h3 style={{ marginBottom: '0.5rem' }}>Streak Freeze</h3>
            <p style={{ color: 'var(--text-muted)', marginBottom: '1rem' }}>
              Use a streak freeze to preserve your {streakDays}-day streak without working out today?
            </p>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '1.5rem' }}>
              You have {freezesAvailable} freeze{freezesAvailable > 1 ? 's' : ''} available.
            </p>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <button className="btn btn-ghost" style={{ flex: 1 }} onClick={() => setShowStreakFreeze(false)}>
                Cancel
              </button>
              <button className="btn btn-primary" style={{ flex: 1 }} onClick={useStreakFreeze}>
                Use Freeze
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Bottom Navigation */}
      <nav className="bottom-nav">
        <Link to="/" className="nav-item active" data-testid="nav-home">
          <HomeIcon size={24} />
          <span>Home</span>
        </Link>
        <Link to="/templates" className="nav-item" data-testid="nav-templates">
          <Timer size={24} />
          <span>Templates</span>
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
