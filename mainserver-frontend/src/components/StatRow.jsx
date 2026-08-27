import React from 'react';
import './StatRow.css';

function fmt(val) {
  const n = parseFloat(val);
  return isNaN(n) ? '—' : n.toFixed(2);
}

function shortId(id) {
  return id ? id.slice(0, 8) + '…' : '—';
}

function formatTime(iso) {
  try {
    return new Date(iso).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  } catch { return iso; }
}

function formatDate(iso) {
  try {
    return new Date(iso).toLocaleDateString([], { month: 'short', day: 'numeric' });
  } catch { return ''; }
}

function formatUnit(unit) {
  if (unit != null) return " " + unit;
  else return "";
}

export default function StatRow({ sensor, stat }) {
  const { sensorID, model } = sensor;
  const { statsId, unit, average, stdDev, min, max, count } = stat;

  return (
    <div className="stat-row">
      <div className="stat-row__header">
        <span className="stat-row__sensor-id" title={sensorID}>
          ◈ {sensorID} · {model} 
        </span>
        <span className="stat-row__timeframe">
          {formatDate(statsId.timeframeStart)}&nbsp;&nbsp;
          {formatTime(statsId.timeframeStart)} – {formatTime(statsId.timeframeEnd)}
          &nbsp;·&nbsp; {count} readings
        </span>
      </div>

      <div className="stat-row__metrics">
        <div className="metric">
          <span className="metric__label">avg</span>
          <span className="metric__value metric__value--avg">{fmt(average) + formatUnit(unit)}</span>
        </div>
        <div className="metric">
          <span className="metric__label">std dev</span>
          <span className="metric__value metric__value--std">{fmt(stdDev)+ formatUnit(unit)}</span>
        </div>
        <div className="metric">
          <span className="metric__label">min</span>
          <span className="metric__value metric__value--min">{fmt(min)+ formatUnit(unit)}</span>
        </div>
        <div className="metric">
          <span className="metric__label">max</span>
          <span className="metric__value metric__value--max">{fmt(max)+ formatUnit(unit)}</span>
        </div>
        <div className="metric">
          <span className="metric__label">count</span>
          <span className="metric__value">{count}</span>
        </div>
      </div>
    </div>
  );
}
