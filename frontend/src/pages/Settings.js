import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  User, Heart, Target, Volume2, Vibrate, Sparkles, Eye, Type,
  Home as HomeIcon, History as HistoryIcon, Settings as SettingsIcon,
  LogOut, Shield, Zap, Brain, Timer, Mic, ChevronDown, ChevronUp
} from 'lucide-react';
import { useApp } from '../contexts/AppContext';

const ND_MODES = [
  { id: 'standard', title: 'Standard', icon: Heart, color: '#8b5cf6' },
  { id: 'adhd', title: 'ADHD Focus', icon: Zap, color: '#f97316' },
  { id: 'asd', title: 'ASD Comfort', icon: Shield, color: '#3b82f6' },
  { id: 'combined', title: 'Combined', icon: Brain, color: '#10b981' }
];

export default function Settings() {
  const navigate = useNavigate();
  const { user, settings, ndMode, updateUser, updateSettings, logout } = useApp();
  
  const [editProfile, setEditProfile] = useState(false);
  const [expandedSection, setExpandedSection] = useState(null);
  const [profileForm, setProfileForm] = useState({
    name: user?.name || '',
    age: user?.age || 30,
    weight_kg: user?.weight_kg || 70,
    height_cm: user?.height_cm || 170,
    resting_hr: user?.resting_hr || 60,
    daily_burn_target: user?.daily_burn_target || 12
  });
  
  const [sensory, setSensory] = useState(settings?.sensory || {
    animation_level: 100,
    sound_level: 100,
    haptic_level: 100,
    transition_warnings: true,
    literal_labels: false
  });

  const [voiceCoach, setVoiceCoach] = useState(settings?.voice_coach || {
    enabled: true,
    voice_selection: 'auto',
    speech_rate: 'normal',
    volume: 80,
    min_gap_seconds: 15,
    zone_callouts: true,
    point_updates: 'every_3',
    interval_countdown: true,
    encouragement: 'occasional',
    pacing_warnings: true
  });

  const [accessibility, setAccessibility] = useState({
    high_contrast: settings?.high_contrast || false,
    colorblind_mode: settings?.colorblind_mode || false,
    font_size: settings?.font_size || 'medium'
  });

  const handleProfileSave = async () => {
    await updateUser(profileForm);
    setEditProfile(false);
  };

  const handleNdModeChange = async (mode) => {
    await updateUser({ nd_mode: mode });
    
    if (mode === 'asd' || mode === 'combined') {
      const newSensory = {
        ...sensory,
        animation_level: 0,
        literal_labels: true,
        transition_warnings: true
      };
      setSensory(newSensory);
      await updateSettings({ ...settings, sensory: newSensory });
    }
  };

  const handleSensorySave = async () => {
    await updateSettings({ ...settings, sensory });
  };

  const handleVoiceCoachSave = async () => {
    await updateSettings({ ...settings, voice_coach: voiceCoach });
  };

  const handleAccessibilitySave = async () => {
    await updateSettings({ 
      ...settings, 
      high_contrast: accessibility.high_contrast,
      colorblind_mode: accessibility.colorblind_mode,
      font_size: accessibility.font_size
    });
  };

  const handleLogout = () => {
    if (window.confirm('Are you sure you want to log out?')) {
      logout();
      navigate('/onboarding');
    }
  };

  const toggleSection = (section) => {
    setExpandedSection(expandedSection === section ? null : section);
  };

  const SectionHeader = ({ title, icon: Icon, section, color }) => (
    <button
      onClick={() => toggleSection(section)}
      style={{
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '1rem',
        background: 'var(--bg-card)',
        border: '1px solid rgba(255,255,255,0.1)',
        borderRadius: expandedSection === section ? '1rem 1rem 0 0' : '1rem',
        cursor: 'pointer',
        color: 'var(--text-primary)'
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        <Icon size={20} color={color || 'var(--primary)'} />
        <span style={{ fontWeight: 600 }}>{title}</span>
      </div>
      {expandedSection === section ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
    </button>
  );

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header">
        <h1 className="page-title">Settings</h1>
      </div>

      {/* Profile Section */}
      <section style={{ marginBottom: '1rem' }}>
        <div className="card">
          {!editProfile ? (
            <>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1rem' }}>
                <div style={{
                  width: '56px',
                  height: '56px',
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg, var(--primary), var(--accent))',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }}>
                  <User size={28} color="white" />
                </div>
                <div>
                  <h3 style={{ marginBottom: '0.25rem' }}>{user?.name}</h3>
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
                    Level {user?.level} • {user?.xp} XP • {user?.total_workouts || 0} workouts
                  </p>
                </div>
              </div>
              
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem', fontSize: '0.875rem', marginBottom: '1rem' }}>
                <span style={{ color: 'var(--text-muted)' }}>Age</span>
                <span>{user?.age}</span>
                <span style={{ color: 'var(--text-muted)' }}>Max HR</span>
                <span>{user?.max_hr} BPM</span>
                <span style={{ color: 'var(--text-muted)' }}>Daily Target</span>
                <span>{user?.daily_burn_target} Burn Points</span>
                <span style={{ color: 'var(--text-muted)' }}>Lifetime Points</span>
                <span>{user?.total_burn_points || 0}</span>
              </div>
              
              <button 
                className="btn btn-secondary" 
                style={{ width: '100%' }}
                onClick={() => setEditProfile(true)}
                data-testid="btn-edit-profile"
              >
                Edit Profile
              </button>
            </>
          ) : (
            <>
              <div className="input-group">
                <label className="input-label">Name</label>
                <input
                  type="text"
                  className="input"
                  value={profileForm.name}
                  onChange={(e) => setProfileForm({ ...profileForm, name: e.target.value })}
                />
              </div>
              
              <div className="input-group">
                <label className="input-label">Age: {profileForm.age} (Max HR: {220 - profileForm.age})</label>
                <input
                  type="range"
                  className="slider"
                  min="16"
                  max="80"
                  value={profileForm.age}
                  onChange={(e) => setProfileForm({ ...profileForm, age: parseInt(e.target.value) })}
                />
              </div>
              
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="input-group">
                  <label className="input-label">Weight (kg)</label>
                  <input
                    type="number"
                    className="input"
                    value={profileForm.weight_kg}
                    onChange={(e) => setProfileForm({ ...profileForm, weight_kg: parseFloat(e.target.value) })}
                  />
                </div>
                <div className="input-group">
                  <label className="input-label">Height (cm)</label>
                  <input
                    type="number"
                    className="input"
                    value={profileForm.height_cm}
                    onChange={(e) => setProfileForm({ ...profileForm, height_cm: parseFloat(e.target.value) })}
                  />
                </div>
              </div>
              
              <div className="input-group">
                <label className="input-label">Daily Target: {profileForm.daily_burn_target} pts</label>
                <input
                  type="range"
                  className="slider"
                  min="8"
                  max="30"
                  value={profileForm.daily_burn_target}
                  onChange={(e) => setProfileForm({ ...profileForm, daily_burn_target: parseInt(e.target.value) })}
                />
              </div>
              
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button className="btn btn-ghost" style={{ flex: 1 }} onClick={() => setEditProfile(false)}>
                  Cancel
                </button>
                <button className="btn btn-primary" style={{ flex: 1 }} onClick={handleProfileSave}>
                  Save
                </button>
              </div>
            </>
          )}
        </div>
      </section>

      {/* Experience Mode */}
      <section style={{ marginBottom: '1rem' }}>
        <h2 style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
          Experience Mode
        </h2>
        
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
          {ND_MODES.map((mode) => {
            const Icon = mode.icon;
            const isSelected = ndMode === mode.id;
            
            return (
              <button
                key={mode.id}
                className="card"
                onClick={() => handleNdModeChange(mode.id)}
                data-testid={`settings-nd-${mode.id}`}
                style={{
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: '0.5rem',
                  padding: '1rem',
                  cursor: 'pointer',
                  border: isSelected ? `2px solid ${mode.color}` : undefined,
                  background: isSelected ? `${mode.color}10` : undefined
                }}
              >
                <Icon size={24} color={mode.color} />
                <span style={{ fontSize: '0.875rem', fontWeight: isSelected ? 600 : 400 }}>
                  {mode.title}
                </span>
              </button>
            );
          })}
        </div>
      </section>

      {/* Sensory Control Panel */}
      <section style={{ marginBottom: '0.5rem' }}>
        <SectionHeader title="Sensory Control Panel" icon={Shield} section="sensory" color="#3b82f6" />
        
        {expandedSection === 'sensory' && (
          <div className="card" style={{ borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
            <div className="input-group">
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                <Sparkles size={16} color="var(--primary)" />
                <label className="input-label" style={{ margin: 0 }}>
                  Animations: {sensory.animation_level}%
                </label>
              </div>
              <input
                type="range"
                className="slider"
                min="0"
                max="100"
                value={sensory.animation_level}
                onChange={(e) => setSensory({ ...sensory, animation_level: parseInt(e.target.value) })}
              />
            </div>
            
            <div className="input-group">
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                <Volume2 size={16} color="var(--secondary)" />
                <label className="input-label" style={{ margin: 0 }}>
                  Sounds: {sensory.sound_level}%
                </label>
              </div>
              <input
                type="range"
                className="slider"
                min="0"
                max="100"
                value={sensory.sound_level}
                onChange={(e) => setSensory({ ...sensory, sound_level: parseInt(e.target.value) })}
              />
            </div>
            
            <div className="input-group">
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                <Vibrate size={16} color="var(--zone-push)" />
                <label className="input-label" style={{ margin: 0 }}>
                  Haptics: {sensory.haptic_level}%
                </label>
              </div>
              <input
                type="range"
                className="slider"
                min="0"
                max="100"
                value={sensory.haptic_level}
                onChange={(e) => setSensory({ ...sensory, haptic_level: parseInt(e.target.value) })}
              />
            </div>
            
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginTop: '0.5rem' }}>
              <input
                type="checkbox"
                checked={sensory.transition_warnings}
                onChange={(e) => setSensory({ ...sensory, transition_warnings: e.target.checked })}
                style={{ width: '20px', height: '20px', accentColor: 'var(--primary)' }}
              />
              <span>Transition Warnings</span>
            </label>
            
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginTop: '0.5rem' }}>
              <input
                type="checkbox"
                checked={sensory.literal_labels}
                onChange={(e) => setSensory({ ...sensory, literal_labels: e.target.checked })}
                style={{ width: '20px', height: '20px', accentColor: 'var(--primary)' }}
              />
              <span>Literal Labels (e.g., "Start Workout" vs "GO")</span>
            </label>
            
            <button className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} onClick={handleSensorySave}>
              Save Sensory Settings
            </button>
          </div>
        )}
      </section>

      {/* Voice Coach Settings */}
      <section style={{ marginBottom: '0.5rem' }}>
        <SectionHeader title="Voice Coach (ElevenLabs)" icon={Mic} section="voice" color="#22c55e" />
        
        {expandedSection === 'voice' && (
          <div className="card" style={{ borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
            <div style={{ 
              background: 'rgba(34, 197, 94, 0.1)', 
              border: '1px solid rgba(34, 197, 94, 0.3)',
              borderRadius: '0.75rem',
              padding: '0.75rem',
              marginBottom: '1rem',
              fontSize: '0.875rem'
            }}>
              <span style={{ color: 'var(--secondary)' }}>✨ Powered by ElevenLabs AI Voice</span>
            </div>
            
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
              <input
                type="checkbox"
                checked={voiceCoach.enabled}
                onChange={(e) => setVoiceCoach({ ...voiceCoach, enabled: e.target.checked })}
                style={{ width: '20px', height: '20px', accentColor: 'var(--primary)' }}
              />
              <span style={{ fontWeight: 600 }}>Enable Voice Coach</span>
            </label>
            
            {voiceCoach.enabled && (
              <>
                <div className="input-group">
                  <label className="input-label">Voice Style</label>
                  <select
                    className="input"
                    value={voiceCoach.voice_selection}
                    onChange={(e) => setVoiceCoach({ ...voiceCoach, voice_selection: e.target.value })}
                    data-testid="select-voice"
                  >
                    <option value="auto">Auto (based on ND mode)</option>
                    <option value="energetic_coach">Energetic Coach (motivational)</option>
                    <option value="calm_guide">Calm Guide (ASD-friendly)</option>
                    <option value="friendly_trainer">Friendly Trainer (warm)</option>
                    <option value="professional">Professional (clear)</option>
                  </select>
                  <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                    {voiceCoach.voice_selection === 'auto' && 'Automatically selects Calm Guide for ASD mode'}
                    {voiceCoach.voice_selection === 'energetic_coach' && 'High-energy voice for maximum motivation'}
                    {voiceCoach.voice_selection === 'calm_guide' && 'Soothing voice, perfect for sensory-sensitive users'}
                    {voiceCoach.voice_selection === 'friendly_trainer' && 'Warm, encouraging voice like a supportive friend'}
                    {voiceCoach.voice_selection === 'professional' && 'Clear, professional announcements'}
                  </p>
                </div>
                
                <div className="input-group">
                  <label className="input-label">Volume: {voiceCoach.volume}%</label>
                  <input
                    type="range"
                    className="slider"
                    min="0"
                    max="100"
                    value={voiceCoach.volume}
                    onChange={(e) => setVoiceCoach({ ...voiceCoach, volume: parseInt(e.target.value) })}
                  />
                </div>
                
                <div className="input-group">
                  <label className="input-label">Min Gap Between Announcements: {voiceCoach.min_gap_seconds}s</label>
                  <input
                    type="range"
                    className="slider"
                    min="5"
                    max="60"
                    value={voiceCoach.min_gap_seconds}
                    onChange={(e) => setVoiceCoach({ ...voiceCoach, min_gap_seconds: parseInt(e.target.value) })}
                  />
                </div>
                
                <div className="input-group">
                  <label className="input-label">Point Updates</label>
                  <select
                    className="input"
                    value={voiceCoach.point_updates}
                    onChange={(e) => setVoiceCoach({ ...voiceCoach, point_updates: e.target.value })}
                  >
                    <option value="every">Every point</option>
                    <option value="every_3">Every 3 points</option>
                    <option value="target_only">Target hit only</option>
                    <option value="off">Off</option>
                  </select>
                </div>
                
                <div className="input-group">
                  <label className="input-label">Encouragement</label>
                  <select
                    className="input"
                    value={voiceCoach.encouragement}
                    onChange={(e) => setVoiceCoach({ ...voiceCoach, encouragement: e.target.value })}
                  >
                    <option value="frequent">Frequent</option>
                    <option value="occasional">Occasional</option>
                    <option value="off">Off</option>
                  </select>
                </div>
                
                <label style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginTop: '0.5rem' }}>
                  <input
                    type="checkbox"
                    checked={voiceCoach.zone_callouts}
                    onChange={(e) => setVoiceCoach({ ...voiceCoach, zone_callouts: e.target.checked })}
                    style={{ width: '20px', height: '20px', accentColor: 'var(--primary)' }}
                  />
                  <span>Zone Change Callouts</span>
                </label>
                
                <label style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginTop: '0.5rem' }}>
                  <input
                    type="checkbox"
                    checked={voiceCoach.interval_countdown}
                    onChange={(e) => setVoiceCoach({ ...voiceCoach, interval_countdown: e.target.checked })}
                    style={{ width: '20px', height: '20px', accentColor: 'var(--primary)' }}
                  />
                  <span>Interval/Segment Countdown</span>
                </label>
              </>
            )}
            
            <button className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} onClick={handleVoiceCoachSave}>
              Save Voice Settings
            </button>
          </div>
        )}
      </section>

      {/* Accessibility */}
      <section style={{ marginBottom: '0.5rem' }}>
        <SectionHeader title="Accessibility" icon={Eye} section="accessibility" color="#8b5cf6" />
        
        {expandedSection === 'accessibility' && (
          <div className="card" style={{ borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.75rem' }}>
              <input
                type="checkbox"
                checked={accessibility.high_contrast}
                onChange={(e) => setAccessibility({ ...accessibility, high_contrast: e.target.checked })}
                style={{ width: '20px', height: '20px', accentColor: 'var(--primary)' }}
              />
              <div>
                <span style={{ fontWeight: 500 }}>High Contrast Mode</span>
                <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Better visibility outdoors</p>
              </div>
            </label>
            
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.75rem' }}>
              <input
                type="checkbox"
                checked={accessibility.colorblind_mode}
                onChange={(e) => setAccessibility({ ...accessibility, colorblind_mode: e.target.checked })}
                style={{ width: '20px', height: '20px', accentColor: 'var(--primary)' }}
              />
              <div>
                <span style={{ fontWeight: 500 }}>Colorblind-Friendly Zones</span>
                <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Patterns + labels supplement colors</p>
              </div>
            </label>
            
            <div className="input-group">
              <label className="input-label">
                <Type size={16} style={{ display: 'inline', marginRight: '0.5rem' }} />
                Font Size
              </label>
              <select
                className="input"
                value={accessibility.font_size}
                onChange={(e) => setAccessibility({ ...accessibility, font_size: e.target.value })}
              >
                <option value="small">Small</option>
                <option value="medium">Medium (Default)</option>
                <option value="large">Large</option>
              </select>
            </div>
            
            <button className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} onClick={handleAccessibilitySave}>
              Save Accessibility Settings
            </button>
          </div>
        )}
      </section>

      {/* Account Section */}
      <section style={{ marginBottom: '6rem', marginTop: '1rem' }}>
        <button
          className="btn btn-ghost"
          style={{ 
            width: '100%', 
            justifyContent: 'flex-start', 
            color: 'var(--zone-peak)',
            padding: '1rem',
            background: 'var(--bg-card)',
            borderRadius: '1rem'
          }}
          onClick={handleLogout}
          data-testid="btn-logout"
        >
          <LogOut size={20} />
          Log Out
        </button>
      </section>

      {/* Bottom Navigation */}
      <nav className="bottom-nav">
        <Link to="/" className="nav-item" data-testid="nav-home">
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
        <Link to="/settings" className="nav-item active" data-testid="nav-settings">
          <SettingsIcon size={24} />
          <span>Settings</span>
        </Link>
      </nav>
    </div>
  );
}
