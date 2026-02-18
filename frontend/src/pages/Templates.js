import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { 
  Play, Clock, Flame, Zap, ChevronRight, Plus, 
  Home as HomeIcon, History as HistoryIcon, Settings as SettingsIcon,
  Timer, Mountain, Heart, Trash2
} from 'lucide-react';
import { useApp } from '../contexts/AppContext';

const DIFFICULTY_COLORS = {
  easy: '#22c55e',
  moderate: '#f97316',
  hard: '#ef4444',
  custom: '#8b5cf6'
};

const CATEGORY_ICONS = {
  hiit: Zap,
  endurance: Heart,
  interval: Timer,
  recovery: Mountain,
  custom: Play
};

export default function Templates() {
  const navigate = useNavigate();
  const { user, shouldAnimate, API_URL } = useApp();
  
  const [templates, setTemplates] = useState([]);
  const [customTemplates, setCustomTemplates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [showBuilder, setShowBuilder] = useState(false);
  const [newTemplate, setNewTemplate] = useState({
    name: '',
    description: '',
    segments: [{ name: 'Warm-Up', duration_seconds: 300, target_zone: 2 }]
  });

  useEffect(() => {
    fetchTemplates();
    if (user) fetchCustomTemplates();
  }, [user]);

  const fetchTemplates = async () => {
    try {
      const res = await fetch(`${API_URL}/api/templates`);
      if (res.ok) {
        const data = await res.json();
        setTemplates(data.templates || []);
      }
    } catch (err) {
      console.error('Failed to fetch templates:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchCustomTemplates = async () => {
    try {
      const res = await fetch(`${API_URL}/api/users/${user.id}/templates`);
      if (res.ok) {
        const data = await res.json();
        setCustomTemplates(data.templates || []);
      }
    } catch (err) {
      console.error('Failed to fetch custom templates:', err);
    }
  };

  const startTemplate = (templateId) => {
    navigate(`/workout?template=${templateId}`);
  };

  const addSegment = () => {
    setNewTemplate(prev => ({
      ...prev,
      segments: [...prev.segments, { name: 'New Segment', duration_seconds: 180, target_zone: 3 }]
    }));
  };

  const updateSegment = (index, field, value) => {
    setNewTemplate(prev => ({
      ...prev,
      segments: prev.segments.map((s, i) => 
        i === index ? { ...s, [field]: value } : s
      )
    }));
  };

  const removeSegment = (index) => {
    setNewTemplate(prev => ({
      ...prev,
      segments: prev.segments.filter((_, i) => i !== index)
    }));
  };

  const saveCustomTemplate = async () => {
    if (!newTemplate.name.trim()) {
      alert('Please enter a template name');
      return;
    }
    
    try {
      const res = await fetch(`${API_URL}/api/users/${user.id}/templates`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          user_id: user.id,
          name: newTemplate.name,
          description: newTemplate.description,
          segments: newTemplate.segments
        })
      });
      
      if (res.ok) {
        await fetchCustomTemplates();
        setShowBuilder(false);
        setNewTemplate({
          name: '',
          description: '',
          segments: [{ name: 'Warm-Up', duration_seconds: 300, target_zone: 2 }]
        });
      }
    } catch (err) {
      console.error('Failed to save template:', err);
    }
  };

  const deleteCustomTemplate = async (templateId) => {
    if (!window.confirm('Delete this template?')) return;
    
    try {
      await fetch(`${API_URL}/api/templates/${templateId}`, { method: 'DELETE' });
      await fetchCustomTemplates();
    } catch (err) {
      console.error('Failed to delete template:', err);
    }
  };

  const categories = ['all', 'hiit', 'interval', 'endurance', 'recovery', 'custom'];
  
  const filteredTemplates = selectedCategory === 'all' 
    ? [...templates, ...customTemplates]
    : selectedCategory === 'custom'
      ? customTemplates
      : templates.filter(t => t.category === selectedCategory);

  const formatDuration = (minutes) => {
    return minutes >= 60 ? `${Math.floor(minutes / 60)}h ${minutes % 60}m` : `${minutes} min`;
  };

  const ZONE_COLORS = ['#71717a', '#71717a', '#3b82f6', '#22c55e', '#f97316', '#ef4444'];

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header">
        <h1 className="page-title">Workout Templates</h1>
        <button 
          className="btn btn-primary"
          onClick={() => setShowBuilder(true)}
          data-testid="btn-create-template"
        >
          <Plus size={18} />
        </button>
      </div>

      {/* Category Filter */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', overflowX: 'auto', paddingBottom: '0.5rem' }}>
        {categories.map(cat => (
          <button
            key={cat}
            className={`btn ${selectedCategory === cat ? 'btn-primary' : 'btn-secondary'}`}
            style={{ padding: '0.5rem 1rem', fontSize: '0.875rem', textTransform: 'capitalize', whiteSpace: 'nowrap' }}
            onClick={() => setSelectedCategory(cat)}
            data-testid={`filter-${cat}`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Loading */}
      {loading && (
        <div style={{ textAlign: 'center', padding: '3rem' }}>
          <div className="pulse-ring" style={{ margin: '0 auto' }}></div>
        </div>
      )}

      {/* Template List */}
      {!loading && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {filteredTemplates.map((template, index) => {
            const Icon = CATEGORY_ICONS[template.category] || Play;
            const isCustom = template.category === 'custom';
            
            return (
              <motion.div
                key={template.id}
                initial={shouldAnimate ? { opacity: 0, y: 20 } : undefined}
                animate={shouldAnimate ? { opacity: 1, y: 0 } : undefined}
                transition={{ delay: index * 0.05 }}
                className="card"
                style={{ cursor: 'pointer' }}
                onClick={() => startTemplate(template.id)}
                data-testid={`template-${template.id}`}
              >
                <div style={{ display: 'flex', gap: '1rem' }}>
                  <div style={{
                    width: '56px',
                    height: '56px',
                    borderRadius: '12px',
                    background: `${DIFFICULTY_COLORS[template.difficulty]}20`,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    flexShrink: 0
                  }}>
                    <Icon size={28} color={DIFFICULTY_COLORS[template.difficulty]} />
                  </div>
                  
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                      <h3 style={{ fontSize: '1rem' }}>{template.name}</h3>
                      <span 
                        className="badge"
                        style={{ 
                          background: `${DIFFICULTY_COLORS[template.difficulty]}30`,
                          color: DIFFICULTY_COLORS[template.difficulty],
                          fontSize: '0.625rem',
                          textTransform: 'capitalize'
                        }}
                      >
                        {template.difficulty}
                      </span>
                    </div>
                    
                    <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '0.5rem' }}>
                      {template.description}
                    </p>
                    
                    <div style={{ display: 'flex', gap: '1rem', fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                      <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                        <Clock size={14} />
                        {formatDuration(template.duration_minutes)}
                      </span>
                      <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                        <Flame size={14} color="var(--zone-push)" />
                        ~{template.estimated_burn_points} pts
                      </span>
                    </div>
                    
                    {/* Segment preview */}
                    <div style={{ display: 'flex', gap: '2px', marginTop: '0.5rem', height: '6px', borderRadius: '3px', overflow: 'hidden' }}>
                      {template.segments?.slice(0, 8).map((seg, i) => (
                        <div
                          key={i}
                          style={{
                            flex: seg.duration_seconds,
                            background: ZONE_COLORS[seg.target_zone] || '#71717a'
                          }}
                        />
                      ))}
                    </div>
                  </div>
                  
                  {isCustom ? (
                    <button
                      className="btn btn-ghost"
                      onClick={(e) => { e.stopPropagation(); deleteCustomTemplate(template.id); }}
                      style={{ alignSelf: 'center' }}
                    >
                      <Trash2 size={18} color="var(--zone-peak)" />
                    </button>
                  ) : (
                    <ChevronRight size={24} color="var(--text-muted)" style={{ alignSelf: 'center' }} />
                  )}
                </div>
              </motion.div>
            );
          })}
          
          {filteredTemplates.length === 0 && (
            <div className="card" style={{ textAlign: 'center', padding: '2rem' }}>
              <p style={{ color: 'var(--text-muted)' }}>No templates in this category</p>
            </div>
          )}
        </div>
      )}

      {/* Custom Template Builder Modal */}
      {showBuilder && (
        <div style={{
          position: 'fixed',
          inset: 0,
          background: 'rgba(0,0,0,0.9)',
          zIndex: 1000,
          overflow: 'auto',
          padding: '1rem'
        }}>
          <div className="card" style={{ maxWidth: '500px', margin: '0 auto' }}>
            <h2 className="page-title" style={{ marginBottom: '1rem' }}>Create Template</h2>
            
            <div className="input-group">
              <label className="input-label">Template Name</label>
              <input
                type="text"
                className="input"
                placeholder="My Custom Workout"
                value={newTemplate.name}
                onChange={(e) => setNewTemplate(prev => ({ ...prev, name: e.target.value }))}
                data-testid="input-template-name"
              />
            </div>
            
            <div className="input-group">
              <label className="input-label">Description</label>
              <input
                type="text"
                className="input"
                placeholder="A challenging interval workout"
                value={newTemplate.description}
                onChange={(e) => setNewTemplate(prev => ({ ...prev, description: e.target.value }))}
              />
            </div>
            
            <h3 style={{ marginBottom: '0.75rem', marginTop: '1rem' }}>Segments</h3>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginBottom: '1rem' }}>
              {newTemplate.segments.map((seg, index) => (
                <div key={index} style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                  <input
                    type="text"
                    className="input"
                    style={{ flex: 2 }}
                    value={seg.name}
                    onChange={(e) => updateSegment(index, 'name', e.target.value)}
                    placeholder="Segment name"
                  />
                  <input
                    type="number"
                    className="input"
                    style={{ flex: 1, textAlign: 'center' }}
                    value={seg.duration_seconds / 60}
                    onChange={(e) => updateSegment(index, 'duration_seconds', parseInt(e.target.value) * 60 || 60)}
                    placeholder="min"
                  />
                  <select
                    className="input"
                    style={{ flex: 1 }}
                    value={seg.target_zone}
                    onChange={(e) => updateSegment(index, 'target_zone', parseInt(e.target.value))}
                  >
                    <option value={2}>Warm-Up</option>
                    <option value={3}>Active</option>
                    <option value={4}>Push</option>
                    <option value={5}>Peak</option>
                  </select>
                  <button
                    className="btn btn-ghost"
                    onClick={() => removeSegment(index)}
                    disabled={newTemplate.segments.length <= 1}
                    style={{ padding: '0.5rem' }}
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              ))}
            </div>
            
            <button className="btn btn-secondary" onClick={addSegment} style={{ width: '100%', marginBottom: '1rem' }}>
              <Plus size={18} /> Add Segment
            </button>
            
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <button className="btn btn-ghost" onClick={() => setShowBuilder(false)} style={{ flex: 1 }}>
                Cancel
              </button>
              <button className="btn btn-primary" onClick={saveCustomTemplate} style={{ flex: 1 }} data-testid="btn-save-template">
                Save Template
              </button>
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
        <Link to="/templates" className="nav-item active" data-testid="nav-templates">
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
