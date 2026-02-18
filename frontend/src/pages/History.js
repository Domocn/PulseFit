import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Calendar, Flame, Clock, ChevronRight, TrendingUp,
  Home as HomeIcon, History as HistoryIcon, Settings as SettingsIcon
} from 'lucide-react';
import { useApp } from '../contexts/AppContext';

const ZONES = {
  1: { name: 'Rest', color: '#71717a' },
  2: { name: 'Warm-Up', color: '#3b82f6' },
  3: { name: 'Active', color: '#22c55e' },
  4: { name: 'Push', color: '#f97316' },
  5: { name: 'Peak', color: '#ef4444' }
};

export default function History() {
  const { user, API_URL } = useApp();
  const [workouts, setWorkouts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [viewMode, setViewMode] = useState('list'); // list, calendar

  useEffect(() => {
    fetchWorkouts();
  }, []);

  const fetchWorkouts = async () => {
    if (!user) return;
    
    try {
      const res = await fetch(`${API_URL}/api/users/${user.id}/workouts?limit=50`);
      if (res.ok) {
        const data = await res.json();
        setWorkouts(data.workouts || []);
      }
    } catch (err) {
      console.error('Failed to fetch workouts:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    
    if (date.toDateString() === today.toDateString()) {
      return 'Today';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return 'Yesterday';
    }
    
    return date.toLocaleDateString('en-US', { 
      weekday: 'short',
      month: 'short', 
      day: 'numeric' 
    });
  };

  const formatTime = (dateStr) => {
    return new Date(dateStr).toLocaleTimeString('en-US', {
      hour: 'numeric',
      minute: '2-digit'
    });
  };

  const formatDuration = (seconds) => {
    const mins = Math.floor(seconds / 60);
    return `${mins} min`;
  };

  // Group workouts by date
  const groupedWorkouts = workouts.reduce((groups, workout) => {
    const date = new Date(workout.end_time).toDateString();
    if (!groups[date]) {
      groups[date] = [];
    }
    groups[date].push(workout);
    return groups;
  }, {});

  // Calculate week stats
  const weekTotal = workouts.reduce((sum, w) => sum + (w.total_burn_points || 0), 0);
  const weekWorkouts = workouts.length;
  const avgPoints = weekWorkouts > 0 ? Math.round(weekTotal / weekWorkouts) : 0;

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header">
        <h1 className="page-title">History</h1>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            className={`btn ${viewMode === 'list' ? 'btn-primary' : 'btn-ghost'}`}
            style={{ padding: '0.5rem 0.75rem' }}
            onClick={() => setViewMode('list')}
            data-testid="btn-view-list"
          >
            <HistoryIcon size={18} />
          </button>
          <button
            className={`btn ${viewMode === 'calendar' ? 'btn-primary' : 'btn-ghost'}`}
            style={{ padding: '0.5rem 0.75rem' }}
            onClick={() => setViewMode('calendar')}
            data-testid="btn-view-calendar"
          >
            <Calendar size={18} />
          </button>
        </div>
      </div>

      {/* Summary Card */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Recent Activity</p>
            <div className="font-heading" style={{ fontSize: '2rem', fontWeight: 900 }}>
              {weekTotal} <span style={{ fontSize: '1rem', color: 'var(--text-muted)' }}>pts</span>
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)' }}>
              <TrendingUp size={16} />
              <span>{weekWorkouts} workouts</span>
            </div>
            <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
              ~{avgPoints} pts avg
            </p>
          </div>
        </div>
      </div>

      {/* Loading State */}
      {loading && (
        <div style={{ textAlign: 'center', padding: '3rem' }}>
          <div className="pulse-ring" style={{ margin: '0 auto' }}></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-muted)' }}>Loading history...</p>
        </div>
      )}

      {/* Empty State */}
      {!loading && workouts.length === 0 && (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <Flame size={48} color="var(--text-muted)" style={{ marginBottom: '1rem' }} />
          <h3 style={{ marginBottom: '0.5rem' }}>No workouts yet</h3>
          <p style={{ color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
            Complete your first workout to see it here
          </p>
          <Link to="/" className="btn btn-primary" style={{ textDecoration: 'none' }}>
            Start Workout
          </Link>
        </div>
      )}

      {/* Workout List */}
      {!loading && workouts.length > 0 && viewMode === 'list' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {Object.entries(groupedWorkouts).map(([date, dayWorkouts]) => (
            <div key={date}>
              <h3 style={{ 
                fontSize: '0.875rem', 
                color: 'var(--text-muted)', 
                marginBottom: '0.75rem',
                textTransform: 'uppercase',
                letterSpacing: '0.05em'
              }}>
                {formatDate(dayWorkouts[0].end_time)}
              </h3>
              
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {dayWorkouts.map((workout) => (
                  <Link
                    key={workout.id}
                    to={`/summary/${workout.id}`}
                    className="card"
                    style={{ textDecoration: 'none', color: 'inherit' }}
                    data-testid={`workout-${workout.id}`}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                      {/* Zone Mini-bar */}
                      <div style={{ 
                        width: '8px', 
                        height: '48px', 
                        borderRadius: '4px',
                        background: 'var(--bg-subtle)',
                        overflow: 'hidden',
                        display: 'flex',
                        flexDirection: 'column-reverse'
                      }}>
                        {workout.zones?.map((z) => {
                          const total = workout.duration_seconds || 1;
                          const pct = (z.duration_seconds / total) * 100;
                          if (pct < 5) return null;
                          return (
                            <div
                              key={z.zone}
                              style={{
                                height: `${pct}%`,
                                background: ZONES[z.zone]?.color
                              }}
                            />
                          );
                        })}
                      </div>
                      
                      {/* Workout Info */}
                      <div style={{ flex: 1 }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                          <Flame size={18} color="var(--zone-push)" />
                          <span className="font-heading" style={{ fontSize: '1.25rem', fontWeight: 700 }}>
                            {workout.total_burn_points}
                          </span>
                          <span style={{ color: 'var(--text-muted)' }}>pts</span>
                          {workout.target_hit && (
                            <span className="badge badge-success" style={{ fontSize: '0.625rem' }}>
                              Target
                            </span>
                          )}
                        </div>
                        
                        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', fontSize: '0.875rem', color: 'var(--text-muted)' }}>
                          <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                            <Clock size={14} />
                            {formatDuration(workout.duration_seconds)}
                          </span>
                          <span>{formatTime(workout.end_time)}</span>
                        </div>
                      </div>
                      
                      <ChevronRight size={20} color="var(--text-muted)" />
                    </div>
                  </Link>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Calendar View (Simple) */}
      {!loading && workouts.length > 0 && viewMode === 'calendar' && (
        <div className="card">
          <p style={{ color: 'var(--text-muted)', textAlign: 'center', marginBottom: '1rem' }}>
            Last 30 days
          </p>
          <div style={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(7, 1fr)', 
            gap: '4px' 
          }}>
            {['S', 'M', 'T', 'W', 'T', 'F', 'S'].map((day, i) => (
              <div 
                key={i} 
                style={{ 
                  textAlign: 'center', 
                  fontSize: '0.75rem', 
                  color: 'var(--text-muted)',
                  padding: '0.5rem 0'
                }}
              >
                {day}
              </div>
            ))}
            
            {Array.from({ length: 30 }, (_, i) => {
              const date = new Date();
              date.setDate(date.getDate() - (29 - i));
              const dateStr = date.toDateString();
              const dayWorkouts = groupedWorkouts[dateStr] || [];
              const hasWorkout = dayWorkouts.length > 0;
              const hitTarget = dayWorkouts.some(w => w.target_hit);
              
              return (
                <div
                  key={i}
                  style={{
                    aspectRatio: '1',
                    borderRadius: '8px',
                    background: hasWorkout 
                      ? hitTarget ? 'var(--secondary)' : 'var(--zone-push)'
                      : 'var(--bg-subtle)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '0.75rem',
                    color: hasWorkout ? 'white' : 'var(--text-muted)'
                  }}
                >
                  {date.getDate()}
                </div>
              );
            })}
          </div>
          
          <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', marginTop: '1rem', fontSize: '0.75rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <div style={{ width: '12px', height: '12px', borderRadius: '3px', background: 'var(--secondary)' }} />
              Target hit
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <div style={{ width: '12px', height: '12px', borderRadius: '3px', background: 'var(--zone-push)' }} />
              Workout
            </div>
          </div>
        </div>
      )}

      {/* Bottom Navigation */}
      <nav className="bottom-nav">
        <Link to="/" className="nav-item" data-testid="nav-home">
          <HomeIcon size={24} />
          <span>Home</span>
        </Link>
        <Link to="/history" className="nav-item active" data-testid="nav-history">
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
