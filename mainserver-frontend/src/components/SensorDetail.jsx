import React, { useMemo } from 'react';
import TypeBadge from './TypeBadge';
import './SensorDetail.css';
import SensorValueChart from './SensorValueChart';

function formatDateTime(iso) {
  try {
    const d = new Date(iso);
    return d.toLocaleString([], {
      month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit', second: '2-digit'
    });
  } catch { return iso; }
}

function fmt(val) {
  const n = parseFloat(val);
  return isNaN(n) ? '—' : n.toFixed(2);
}

export default function SensorDetail({ sensor, stats, onBack }) {
  const sortedStats = useMemo(() => {
    return [...(stats || [])].sort(
      (a, b) => new Date(b.statsId.timeframeStart) - new Date(a.statsId.timeframeStart)
    );
  }, [stats]);

  const chartUnit = sortedStats[0]?.unit || sensor.readingUnit || '';

  return (
    <div className="sensor-detail">

      {/* Back button */}
      <button className="sensor-detail__back" onClick={onBack}>
        ← back to sensors
      </button>

       <SensorValueChart stats={sortedStats} unit={chartUnit} />

      <div className="sensor-detail__layout">

        {/* LEFT — sensor info */}
        <div className="sensor-detail__info">
          <div className="sensor-detail__info-header">
            <TypeBadge type={sensor.type} />
            <span className={`sensor-detail__status ${sensor.available ? 'status--active' : 'status--unavailable'}`}>
              <span className="status-dot" />
              {sensor.available ? 'active' : 'unavailable'}
            </span>
          </div>

          <h2 className="sensor-detail__model">{sensor.model}</h2>

          <div className="sensor-detail__fields">
            <div className="detail-field">
              <span className="detail-field__label">sensor id</span>
              <span className="detail-field__value detail-field__value--mono">{sensor.sensorID}</span>
            </div>
            <div className="detail-field">
              <span className="detail-field__label">type</span>
              <span className="detail-field__value">{sensor.type}</span>
            </div>
            <div className="detail-field">
              <span className="detail-field__label">model</span>
              <span className="detail-field__value">{sensor.model}</span>
            </div>
            <div className="detail-field">
              <span className="detail-field__label">zone</span>
              <span className="detail-field__value">{sensor.location?.zone || '—'}</span>
            </div>
            <div className="detail-field">
              <span className="detail-field__label">aisle</span>
              <span className="detail-field__value">{sensor.location?.aisle || '—'}</span>
            </div>
            <div className="detail-field">
              <span className="detail-field__label">shelf</span>
              <span className="detail-field__value">{sensor.location?.shelf || '—'}</span>
            </div>
          </div>

          <div className="sensor-detail__stats-summary">
            <span className="stats-summary__label">total records</span>
            <span className="stats-summary__value">{sortedStats.length}</span>
          </div>
        </div>

        {/* RIGHT — stats list */}
        <div className="sensor-detail__stats">
          <div className="sensor-detail__stats-header">
            <span className="sensor-detail__stats-title">stats history</span>
            <span className="sensor-detail__stats-count">{sortedStats.length} records</span>
          </div>

          {sortedStats.length === 0 ? (
            <p className="sensor-detail__empty">No stats available for this sensor.</p>
          ) : (
            <div className="stats-list">
              {sortedStats.map((stat, i) => (
                <div className="stats-list__item" key={i}>
                  <div className="stats-list__item-header">
                    <span className="stats-list__index">#{String(i + 1).padStart(3, '0')}</span>
                    <span className="stats-list__timeframe">
                      {formatDateTime(stat.statsId.timeframeStart)} → {formatDateTime(stat.statsId.timeframeEnd)}
                    </span>
                  </div>
                  <div className="stats-list__metrics">
                    <div className="metric">
                      <span className="metric__label">avg</span>
                      <span className="metric__value metric__value--avg">{fmt(stat.average) + " " + stat.unit}</span>
                    </div>
                    <div className="metric">
                      <span className="metric__label">std dev</span>
                      <span className="metric__value metric__value--std">{fmt(stat.stdDev) + " " + stat.unit}</span>
                    </div>
                    <div className="metric">
                      <span className="metric__label">min</span>
                      <span className="metric__value metric__value--min">{fmt(stat.min) + " " + stat.unit}</span>
                    </div>
                    <div className="metric">
                      <span className="metric__label">max</span>
                      <span className="metric__value metric__value--max">{fmt(stat.max) + " " + stat.unit}</span>
                    </div>
                    <div className="metric">
                      <span className="metric__label">count</span>
                      <span className="metric__value">{stat.count}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

      </div>
    </div>
  );
}
