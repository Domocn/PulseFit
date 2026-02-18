import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Heart, ChevronRight, ChevronLeft, Zap, Brain, Sparkles, Shield } from 'lucide-react';
import { useApp } from '../contexts/AppContext';

const STEPS = ['welcome', 'profile', 'nd_mode', 'target', 'complete'];

const ND_MODES = [
  {
    id: 'standard',
    title: 'Standard',
    icon: Heart,
    description: 'Classic experience with full animations and features',
    color: '#8b5cf6'
  },
  {
    id: 'adhd',
    title: 'ADHD Focus',
    icon: Zap,
    description: 'Micro-rewards, XP leveling, celebration animations, "Just 5 Minutes" mode',
    color: '#f97316'
  },
  {
    id: 'asd',
    title: 'ASD Comfort',
    icon: Shield,
    description: 'Calm UI, predictable layout, sensory controls, literal labels',
    color: '#3b82f6'
  },
  {
    id: 'combined',
    title: 'Combined',
    icon: Brain,
    description: 'Calm UI with structured micro-rewards. Best of both.',
    color: '#10b981'
  }
];

export default function Onboarding() {
  const navigate = useNavigate();
  const { createUser } = useApp();
  const [step, setStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    age: 30,
    weight_kg: 70,
    height_cm: 170,
    resting_hr: 60,
    daily_burn_target: 12,
    nd_mode: 'standard'
  });

  const currentStep = STEPS[step];

  const handleNext = async () => {
    if (step < STEPS.length - 2) {
      setStep(step + 1);
    } else if (currentStep === 'target') {
      setLoading(true);
      try {
        await createUser(formData);
        setStep(step + 1);
      } catch (err) {
        alert('Failed to create profile. Please try again.');
      } finally {
        setLoading(false);
      }
    }
  };

  const handleBack = () => {
    if (step > 0) setStep(step - 1);
  };

  const updateForm = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const pageVariants = {
    initial: { opacity: 0, x: 50 },
    animate: { opacity: 1, x: 0 },
    exit: { opacity: 0, x: -50 }
  };

  return (
    <div className="page" style={{ padding: '2rem 1rem' }}>
      {/* Progress Indicator */}
      {step < STEPS.length - 1 && (
        <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '2rem' }}>
          {STEPS.slice(0, -1).map((_, i) => (
            <div
              key={i}
              style={{
                flex: 1,
                height: '4px',
                borderRadius: '2px',
                background: i <= step ? 'var(--primary)' : 'var(--bg-subtle)'
              }}
            />
          ))}
        </div>
      )}

      <AnimatePresence mode="wait">
        <motion.div
          key={step}
          variants={pageVariants}
          initial="initial"
          animate="animate"
          exit="exit"
          transition={{ duration: 0.3 }}
        >
          {/* Welcome Step */}
          {currentStep === 'welcome' && (
            <div style={{ textAlign: 'center', paddingTop: '3rem' }}>
              <div
                style={{
                  width: '120px',
                  height: '120px',
                  margin: '0 auto 2rem',
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg, var(--primary), var(--accent))',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }}
              >
                <Heart size={60} color="white" />
              </div>
              
              <h1 className="font-heading" style={{ fontSize: '2.5rem', fontWeight: 900, marginBottom: '1rem' }}>
                PulseFit
              </h1>
              
              <p style={{ color: 'var(--text-secondary)', fontSize: '1.125rem', marginBottom: '0.5rem' }}>
                Heart Rate Zone Training
              </p>
              
              <p style={{ color: 'var(--text-muted)', marginBottom: '3rem' }}>
                The first neurodivergent-aware fitness app
              </p>

              <div className="card" style={{ textAlign: 'left', marginBottom: '2rem' }}>
                <h3 style={{ marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <Sparkles size={20} color="var(--primary)" />
                  What you'll get
                </h3>
                <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  <li style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <div style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--zone-active)' }} />
                    Earn Burn Points in elevated HR zones
                  </li>
                  <li style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <div style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--zone-push)' }} />
                    Track streaks and level up with XP
                  </li>
                  <li style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <div style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--zone-peak)' }} />
                    ADHD & ASD experience modes
                  </li>
                </ul>
              </div>
            </div>
          )}

          {/* Profile Step */}
          {currentStep === 'profile' && (
            <div>
              <h2 className="page-title" style={{ marginBottom: '0.5rem' }}>Your Profile</h2>
              <p style={{ color: 'var(--text-muted)', marginBottom: '2rem' }}>
                We'll use this to calculate your heart rate zones
              </p>

              <div className="input-group">
                <label className="input-label">Your Name</label>
                <input
                  type="text"
                  className="input"
                  placeholder="Enter your name"
                  value={formData.name}
                  onChange={(e) => updateForm('name', e.target.value)}
                  data-testid="input-name"
                />
              </div>

              <div className="input-group">
                <label className="input-label">Age: {formData.age}</label>
                <input
                  type="range"
                  className="slider"
                  min="16"
                  max="80"
                  value={formData.age}
                  onChange={(e) => updateForm('age', parseInt(e.target.value))}
                  data-testid="slider-age"
                />
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  <span>16</span>
                  <span>Max HR: {220 - formData.age} BPM</span>
                  <span>80</span>
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="input-group">
                  <label className="input-label">Weight (kg)</label>
                  <input
                    type="number"
                    className="input"
                    value={formData.weight_kg}
                    onChange={(e) => updateForm('weight_kg', parseFloat(e.target.value))}
                    data-testid="input-weight"
                  />
                </div>

                <div className="input-group">
                  <label className="input-label">Height (cm)</label>
                  <input
                    type="number"
                    className="input"
                    value={formData.height_cm}
                    onChange={(e) => updateForm('height_cm', parseFloat(e.target.value))}
                    data-testid="input-height"
                  />
                </div>
              </div>

              <div className="input-group">
                <label className="input-label">Resting Heart Rate: {formData.resting_hr} BPM</label>
                <input
                  type="range"
                  className="slider"
                  min="40"
                  max="100"
                  value={formData.resting_hr}
                  onChange={(e) => updateForm('resting_hr', parseInt(e.target.value))}
                  data-testid="slider-resting-hr"
                />
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  <span>40 (Athletic)</span>
                  <span>100 (Sedentary)</span>
                </div>
              </div>
            </div>
          )}

          {/* ND Mode Step */}
          {currentStep === 'nd_mode' && (
            <div>
              <h2 className="page-title" style={{ marginBottom: '0.5rem' }}>Experience Mode</h2>
              <p style={{ color: 'var(--text-muted)', marginBottom: '2rem' }}>
                Choose how you want to experience PulseFit
              </p>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {ND_MODES.map((mode) => {
                  const Icon = mode.icon;
                  const isSelected = formData.nd_mode === mode.id;
                  
                  return (
                    <button
                      key={mode.id}
                      onClick={() => updateForm('nd_mode', mode.id)}
                      className="card"
                      data-testid={`nd-mode-${mode.id}`}
                      style={{
                        border: isSelected ? `2px solid ${mode.color}` : undefined,
                        background: isSelected ? `${mode.color}10` : undefined,
                        textAlign: 'left',
                        cursor: 'pointer',
                        display: 'flex',
                        gap: '1rem',
                        alignItems: 'flex-start'
                      }}
                    >
                      <div
                        style={{
                          width: '48px',
                          height: '48px',
                          borderRadius: '12px',
                          background: `${mode.color}20`,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          flexShrink: 0
                        }}
                      >
                        <Icon size={24} color={mode.color} />
                      </div>
                      <div>
                        <h3 style={{ marginBottom: '0.25rem' }}>{mode.title}</h3>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
                          {mode.description}
                        </p>
                      </div>
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          {/* Target Step */}
          {currentStep === 'target' && (
            <div>
              <h2 className="page-title" style={{ marginBottom: '0.5rem' }}>Daily Target</h2>
              <p style={{ color: 'var(--text-muted)', marginBottom: '2rem' }}>
                Set your daily Burn Points goal
              </p>

              <div className="card card-highlight" style={{ textAlign: 'center', marginBottom: '2rem' }}>
                <div
                  className="font-heading"
                  style={{ fontSize: '4rem', fontWeight: 900, color: 'var(--primary)' }}
                >
                  {formData.daily_burn_target}
                </div>
                <p style={{ color: 'var(--text-secondary)' }}>Burn Points per day</p>
              </div>

              <div className="input-group">
                <input
                  type="range"
                  className="slider"
                  min="8"
                  max="30"
                  value={formData.daily_burn_target}
                  onChange={(e) => updateForm('daily_burn_target', parseInt(e.target.value))}
                  data-testid="slider-target"
                />
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.875rem', color: 'var(--text-muted)' }}>
                  <span>8 (Light)</span>
                  <span>30 (Intense)</span>
                </div>
              </div>

              <div className="card" style={{ marginTop: '2rem' }}>
                <h4 style={{ marginBottom: '0.5rem' }}>How it works</h4>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '1rem' }}>
                  Earn 1-3 Burn Points per minute based on your heart rate zone:
                </p>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <div style={{ width: '12px', height: '12px', borderRadius: '3px', background: 'var(--zone-active)' }} />
                    <span>Active (60-69% MHR)</span>
                    <span style={{ marginLeft: 'auto', fontWeight: 600 }}>1 pt/min</span>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <div style={{ width: '12px', height: '12px', borderRadius: '3px', background: 'var(--zone-push)' }} />
                    <span>Push (70-84% MHR)</span>
                    <span style={{ marginLeft: 'auto', fontWeight: 600 }}>2 pts/min</span>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <div style={{ width: '12px', height: '12px', borderRadius: '3px', background: 'var(--zone-peak)' }} />
                    <span>Peak (85-100% MHR)</span>
                    <span style={{ marginLeft: 'auto', fontWeight: 600 }}>3 pts/min</span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Complete Step */}
          {currentStep === 'complete' && (
            <div style={{ textAlign: 'center', paddingTop: '3rem' }}>
              <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                transition={{ type: 'spring', stiffness: 200 }}
                style={{
                  width: '100px',
                  height: '100px',
                  margin: '0 auto 2rem',
                  borderRadius: '50%',
                  background: 'var(--secondary)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }}
              >
                <Sparkles size={50} color="white" />
              </motion.div>

              <h2 className="page-title" style={{ marginBottom: '1rem' }}>You're All Set!</h2>
              
              <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>
                Welcome to PulseFit, {formData.name}!
              </p>

              <div className="card" style={{ marginBottom: '2rem', textAlign: 'left' }}>
                <h4 style={{ marginBottom: '1rem' }}>Your Profile</h4>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem', fontSize: '0.875rem' }}>
                  <span style={{ color: 'var(--text-muted)' }}>Max HR:</span>
                  <span>{220 - formData.age} BPM</span>
                  <span style={{ color: 'var(--text-muted)' }}>Daily Target:</span>
                  <span>{formData.daily_burn_target} Burn Points</span>
                  <span style={{ color: 'var(--text-muted)' }}>Experience:</span>
                  <span>{ND_MODES.find(m => m.id === formData.nd_mode)?.title}</span>
                </div>
              </div>

              <button
                className="btn btn-primary btn-large"
                style={{ width: '100%' }}
                onClick={() => navigate('/')}
                data-testid="btn-start"
              >
                Start Your First Workout
                <ChevronRight size={20} />
              </button>
            </div>
          )}
        </motion.div>
      </AnimatePresence>

      {/* Navigation Buttons */}
      {currentStep !== 'complete' && (
        <div
          style={{
            position: 'fixed',
            bottom: 0,
            left: 0,
            right: 0,
            padding: '1rem',
            paddingBottom: 'calc(1rem + env(safe-area-inset-bottom, 0))',
            background: 'linear-gradient(transparent, var(--bg-default))',
            display: 'flex',
            gap: '1rem'
          }}
        >
          {step > 0 && (
            <button className="btn btn-ghost" onClick={handleBack} data-testid="btn-back">
              <ChevronLeft size={20} />
              Back
            </button>
          )}
          
          <button
            className="btn btn-primary"
            style={{ flex: 1 }}
            onClick={handleNext}
            disabled={currentStep === 'profile' && !formData.name.trim()}
            data-testid="btn-next"
          >
            {loading ? 'Creating...' : currentStep === 'target' ? 'Create Profile' : 'Continue'}
            {!loading && <ChevronRight size={20} />}
          </button>
        </div>
      )}
    </div>
  );
}
