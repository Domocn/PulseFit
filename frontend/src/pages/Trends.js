import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  TrendingUp, Calendar, Flame, Clock, Heart, BarChart3,
  Home as HomeIcon, History as HistoryIcon, Settings as SettingsIcon
} from 'lucide-react';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Area, AreaChart } from 'recharts';
import { useApp } from '../contexts/AppContext';

const ZONE_COLORS = {
  1: '#71717a',
  2: '#3b82f6', 
  3: '#22c55e',
  4: '#f97316',
  5: '#ef4444'
};

export default function Trends() {
  const { user, API_URL } = useApp();
  
  const [trends, setTrends] = useState(null);
  const [loading, setLoading] = useState(true);
  const [timeRange, setTimeRange] = useState(30);

  useEffect(() => {
    if (user) fetchTrends();
  }, [user, timeRange]);

  const fetchTrends = async () => {
    setLoading(true);
    try {
      const res = await fetch(`${API_URL}/api/users/${user.id}/trends?days=${timeRange}`);
      if (res.ok) {
        const data = await res.json();
        setTrends(data);
      }
    } catch (err) {
      console.error('Failed to fetch trends:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  };

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header">
        <h1 className="page-title">Trends</h1>
        <select
          className="input"
          style={{ width: 'auto', padding: '0.5rem 1rem' }}
          value={timeRange}
          onChange={(e) => setTimeRange(parseInt(e.target.value))}
        >
          <option value={7}>7 Days</option>
          <option value={14}>14 Days</option>
          <option value={30}>30 Days</option>
          <option value={90}>90 Days</option>
        </select>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem' }}>
          <div className="pulse-ring" style={{ margin: '0 auto' }}></div>
        </div>
      ) : trends ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', marginBottom: '5rem' }}>
          {/* Summary Stats */}
          <div className="stats-grid">
            <div className="stat-card">
              <Flame size={20} color="var(--zone-push)" />
              <div className="stat-value" style={{ fontSize: '1.5rem' }}>
                {trends.daily_points?.reduce((sum, d) => sum + d.points, 0) || 0}
              </div>
              <div className="stat-label">Total Points</div>
            </div>
            
            <div className="stat-card">
              <Calendar size={20} color="var(--primary)" />
              <div className="stat-value" style={{ fontSize: '1.5rem' }}>
                {trends.total_workouts || 0}
              </div>
              <div className="stat-label">Workouts</div>
            </div>
            
            <div className="stat-card">
              <TrendingUp size={20} color="var(--secondary)" />
              <div className="stat-value" style={{ fontSize: '1.5rem' }}>
                {trends.avg_points_per_workout || 0}
              </div>
              <div className="stat-label">Avg Points</div>
            </div>
            
            <div className="stat-card">
              <BarChart3 size={20} color="var(--accent)" />
              <div className="stat-value" style={{ fontSize: '1.5rem' }}>
                {timeRange}
              </div>
              <div className="stat-label">Days</div>
            </div>
          </div>

          {/* Daily Points Chart */}
          <div className="card">
            <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
              <Flame size={20} color="var(--zone-push)" />
              Daily Burn Points
            </h3>
            
            {trends.daily_points?.length > 0 ? (
              <ResponsiveContainer width="100%" height={200}>
                <AreaChart data={trends.daily_points}>
                  <defs>
                    <linearGradient id="colorPoints" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#f97316" stopOpacity={0.8}/>
                      <stop offset="95%" stopColor="#f97316" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#27272a" />
                  <XAxis 
                    dataKey="date" 
                    tickFormatter={formatDate}
                    stroke="#71717a"
                    fontSize={12}
                  />
                  <YAxis stroke="#71717a" fontSize={12} />
                  <Tooltip 
                    contentStyle={{ 
                      background: '#18181b', 
                      border: '1px solid #27272a',
                      borderRadius: '8px'
                    }}
                    labelFormatter={formatDate}
                  />
                  <Area 
                    type="monotone" 
                    dataKey="points" 
                    stroke="#f97316" 
                    fillOpacity={1} 
                    fill="url(#colorPoints)"
                  />
                </AreaChart>
              </ResponsiveContainer>
            ) : (
              <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>
                No workout data for this period
              </p>
            )}
          </div>

          {/* Weekly Points Bar Chart */}
          <div className="card">
            <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
              <BarChart3 size={20} color="var(--primary)" />
              Weekly Summary
            </h3>
            
            {trends.weekly_points?.length > 0 ? (
              <ResponsiveContainer width="100%" height={200}>
                <BarChart data={trends.weekly_points}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#27272a" />
                  <XAxis 
                    dataKey="label" 
                    stroke="#71717a"
                    fontSize={10}
                    angle={-45}
                    textAnchor="end"
                    height={60}
                  />
                  <YAxis stroke="#71717a" fontSize={12} />
                  <Tooltip 
                    contentStyle={{ 
                      background: '#18181b', 
                      border: '1px solid #27272a',
                      borderRadius: '8px'
                    }}
                  />
                  <Bar dataKey="points" fill="#8b5cf6" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>
                Not enough data for weekly view
              </p>
            )}
          </div>

          {/* Zone Distribution */}
          <div className="card">
            <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
              <Heart size={20} color="var(--zone-peak)" />
              Zone Distribution
            </h3>
            
            {trends.zone_distribution && Object.keys(trends.zone_distribution).length > 0 ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {['Rest', 'Warm-Up', 'Active', 'Push', 'Peak'].map((name, index) => {
                  const zoneId = index + 1;
                  const totalSeconds = Object.values(trends.zone_distribution).reduce(
                    (sum, day) => sum + (day[zoneId] || 0), 0
                  );
                  const totalAllZones = Object.values(trends.zone_distribution).reduce(
                    (sum, day) => sum + Object.values(day).reduce((s, v) => s + v, 0), 0
                  );
                  const percentage = totalAllZones > 0 ? (totalSeconds / totalAllZones) * 100 : 0;
                  const minutes = Math.round(totalSeconds / 60);
                  
                  return (
                    <div key={zoneId}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                          <div style={{ width: '12px', height: '12px', borderRadius: '3px', background: ZONE_COLORS[zoneId] }} />
                          {name}
                        </span>
                        <span style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
                          {minutes} min ({percentage.toFixed(1)}%)
                        </span>
                      </div>
                      <div className="progress-bar">
                        <div 
                          className="progress-fill"
                          style={{ width: `${percentage}%`, background: ZONE_COLORS[zoneId] }}
                        />
                      </div>
                    </div>
                  );
                })}
              </div>
            ) : (
              <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem' }}>
                No zone data available
              </p>
            )}
          </div>
        </div>
      ) : (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <TrendingUp size={48} color="var(--text-muted)" style={{ marginBottom: '1rem' }} />
          <h3 style={{ marginBottom: '0.5rem' }}>No Data Yet</h3>
          <p style={{ color: 'var(--text-muted)' }}>
            Complete some workouts to see your trends
          </p>
        </div>
      )}

      {/* Bottom Navigation */}
      <nav className="bottom-nav">
        <Link to="/" className="nav-item" data-testid="nav-home">
          <HomeIcon size={24} />
          <span>Home</span>
        </Link>
        <Link to="/trends" className="nav-item active" data-testid="nav-trends">
          <TrendingUp size={24} />
          <span>Trends</span>
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
