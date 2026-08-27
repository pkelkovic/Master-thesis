import React, { useState } from 'react';
import { useSensors, useSensorStats, useErrors } from './hooks/useSensors';
import SensorsTab from './components/SensorsTab';
import StatsTab from './components/StatsTab';
import ErrorsTab from './components/ErrorsTab';
import './App.css';

const TABS = ['sensors', 'recent stats', 'errors'];

export default function App() {
  const [activeTab, setActiveTab] = useState(0);

  const { sensors, loading: sensorsLoading, error: sensorsError, refresh: refreshSensors } = useSensors();
  const { statsMap, loading: statsLoading, error: statsError, refresh: refreshStats } = useSensorStats(sensors);
  const { errors, loading: errorsLoading, error: errorsError, refresh: refreshErrors } = useErrors();

  const loading = sensorsLoading || statsLoading || errorsLoading;
  const error = sensorsError || statsError || errorsError;

  function handleRefresh() {
    refreshSensors();
    refreshStats();
    refreshErrors();
  }

  return (
    <div className="app">
      <header className="app__header">
        <div className="app__header-left">
          <span className="app__status-dot" />
          <span className="app__title">Sensor Monitor</span>
        </div>
        <button className="app__refresh-btn" onClick={handleRefresh} disabled={loading}>
          {loading ? '↻ loading…' : '↻ refresh'}
        </button>
      </header>

      {error && (
        <div className="app__error">⚠ {error}</div>
      )}

      <nav className="app__tabs">
        {TABS.map((tab, i) => (
          <button
            key={tab}
            className={`app__tab ${activeTab === i ? 'app__tab--active' : ''} ${tab === 'errors' && errors.length > 0 ? 'app__tab--has-errors' : ''}`}
            onClick={() => setActiveTab(i)}
          >
            {tab}
            {tab === 'errors' && errors.length > 0 && (
              <span className="app__tab-badge">{errors.length}</span>
            )}
          </button>
        ))}
      </nav>

      <main className="app__content">
        {loading && !sensors.length ? (
          <div className="app__loading">
            <span className="app__spinner" />
            loading…
          </div>
        ) : (
          <>
            {activeTab === 0 && <SensorsTab sensors={sensors} statsMap={statsMap} />}
            {activeTab === 1 && <StatsTab sensors={sensors} statsMap={statsMap} />}
            {activeTab === 2 && <ErrorsTab errors={errors} />}
          </>
        )}
      </main>
    </div>
  );
}
