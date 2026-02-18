import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AppProvider, useApp } from './contexts/AppContext';
import Onboarding from './pages/Onboarding';
import Home from './pages/Home';
import Workout from './pages/Workout';
import Summary from './pages/Summary';
import History from './pages/History';
import Settings from './pages/Settings';
import Templates from './pages/Templates';
import Achievements from './pages/Achievements';
import Trends from './pages/Trends';
import './App.css';

function AppRoutes() {
  const { user, loading } = useApp();

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="pulse-loader">
          <div className="pulse-ring"></div>
          <span className="pulse-text">PulseFit</span>
        </div>
      </div>
    );
  }

  return (
    <Routes>
      <Route path="/onboarding" element={!user ? <Onboarding /> : <Navigate to="/" />} />
      <Route path="/" element={user ? <Home /> : <Navigate to="/onboarding" />} />
      <Route path="/workout" element={user ? <Workout /> : <Navigate to="/onboarding" />} />
      <Route path="/summary/:workoutId" element={user ? <Summary /> : <Navigate to="/onboarding" />} />
      <Route path="/history" element={user ? <History /> : <Navigate to="/onboarding" />} />
      <Route path="/settings" element={user ? <Settings /> : <Navigate to="/onboarding" />} />
      <Route path="/templates" element={user ? <Templates /> : <Navigate to="/onboarding" />} />
      <Route path="/achievements" element={user ? <Achievements /> : <Navigate to="/onboarding" />} />
      <Route path="/trends" element={user ? <Trends /> : <Navigate to="/onboarding" />} />
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
}

function App() {
  return (
    <AppProvider>
      <BrowserRouter>
        <div className="app-container">
          <AppRoutes />
        </div>
      </BrowserRouter>
    </AppProvider>
  );
}

export default App;
