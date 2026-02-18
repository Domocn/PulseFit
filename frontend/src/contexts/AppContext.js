import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';

const API_URL = process.env.REACT_APP_BACKEND_URL || '';

const AppContext = createContext(null);

export function AppProvider({ children }) {
  const [user, setUser] = useState(null);
  const [settings, setSettings] = useState({
    sensory: {
      animation_level: 100,
      sound_level: 100,
      haptic_level: 100,
      transition_warnings: true,
      literal_labels: false
    },
    voice_coach_enabled: true,
    dark_mode: true
  });
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [ndMode, setNdMode] = useState('standard'); // standard, adhd, asd, combined

  // Load user from localStorage
  useEffect(() => {
    const savedUserId = localStorage.getItem('pulsefit_user_id');
    if (savedUserId) {
      fetchUser(savedUserId);
    } else {
      setLoading(false);
    }
  }, []);

  // Update ndMode when user changes
  useEffect(() => {
    if (user?.nd_mode) {
      setNdMode(user.nd_mode);
    }
  }, [user]);

  // Apply ASD mode class to body
  useEffect(() => {
    if (ndMode === 'asd' || ndMode === 'combined') {
      document.body.classList.add('asd-mode');
    } else {
      document.body.classList.remove('asd-mode');
    }
  }, [ndMode]);

  const fetchUser = async (userId) => {
    try {
      const res = await fetch(`${API_URL}/api/users/${userId}`);
      if (res.ok) {
        const userData = await res.json();
        setUser(userData);
        fetchSettings(userId);
        fetchStats(userId);
      } else {
        localStorage.removeItem('pulsefit_user_id');
      }
    } catch (err) {
      console.error('Failed to fetch user:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchSettings = async (userId) => {
    try {
      const res = await fetch(`${API_URL}/api/users/${userId}/settings`);
      if (res.ok) {
        const data = await res.json();
        setSettings(data);
      }
    } catch (err) {
      console.error('Failed to fetch settings:', err);
    }
  };

  const fetchStats = async (userId) => {
    try {
      const res = await fetch(`${API_URL}/api/users/${userId}/stats`);
      if (res.ok) {
        const data = await res.json();
        setStats(data);
      }
    } catch (err) {
      console.error('Failed to fetch stats:', err);
    }
  };

  const createUser = async (userData) => {
    try {
      const res = await fetch(`${API_URL}/api/users`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData)
      });
      if (res.ok) {
        const newUser = await res.json();
        setUser(newUser);
        setNdMode(newUser.nd_mode);
        localStorage.setItem('pulsefit_user_id', newUser.id);
        return newUser;
      }
      throw new Error('Failed to create user');
    } catch (err) {
      console.error('Create user error:', err);
      throw err;
    }
  };

  const updateUser = async (updates) => {
    if (!user) return;
    try {
      const res = await fetch(`${API_URL}/api/users/${user.id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updates)
      });
      if (res.ok) {
        const updated = await res.json();
        setUser(updated);
        if (updates.nd_mode) {
          setNdMode(updates.nd_mode);
        }
        return updated;
      }
    } catch (err) {
      console.error('Update user error:', err);
    }
  };

  const updateSettings = async (newSettings) => {
    if (!user) return;
    try {
      const res = await fetch(`${API_URL}/api/users/${user.id}/settings`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newSettings)
      });
      if (res.ok) {
        const data = await res.json();
        setSettings(data);
        return data;
      }
    } catch (err) {
      console.error('Update settings error:', err);
    }
  };

  const refreshStats = useCallback(async () => {
    if (user) {
      await fetchStats(user.id);
    }
  }, [user]);

  const refreshUser = useCallback(async () => {
    if (user) {
      await fetchUser(user.id);
    }
  }, [user]);

  const logout = () => {
    localStorage.removeItem('pulsefit_user_id');
    setUser(null);
    setStats(null);
    setNdMode('standard');
  };

  const shouldAnimate = settings.sensory.animation_level > 0 && ndMode !== 'asd';
  const shouldPlaySound = settings.sensory.sound_level > 0;
  const useLiteralLabels = settings.sensory.literal_labels || ndMode === 'asd';

  return (
    <AppContext.Provider value={{
      user,
      settings,
      stats,
      loading,
      ndMode,
      createUser,
      updateUser,
      updateSettings,
      refreshStats,
      refreshUser,
      logout,
      shouldAnimate,
      shouldPlaySound,
      useLiteralLabels,
      API_URL
    }}>
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within AppProvider');
  }
  return context;
}
