import React from 'react';
import TypeBadge from './TypeBadge';
import { computeStatus, STATUS_LABELS, STATUS_CSS, CARD_CSS } from '../utils/sensorStatus';
import './SensorCard.css';

export default function SensorCard({ sensor, latestStat, onClick }) {
  const status = computeStatus(sensor, latestStat);

  return (
    <div
      className={`sensor-card ${CARD_CSS[status]} sensor-card--clickable`}
      onClick={onClick}
    >
      <div className="sensor-card__top">
        <TypeBadge type={sensor.type} />
        <span className={`sensor-card__status ${STATUS_CSS[status]}`}>
          <span className="sensor-card__status-dot" />
          {STATUS_LABELS[status]}
        </span>
      </div>

      <div className="sensor-card__id" title={sensor.sensorID}>{sensor.sensorID}</div>
      <div className="sensor-card__model">{sensor.model}</div>

      <div className="sensor-card__location">
        <span className="location__icon">⌖</span>
        <span>Zone <strong>{sensor.location?.zone || '—'}</strong></span>
        <span className="location__sep">/</span>
        <span>Aisle <strong>{sensor.location?.aisle || '—'}</strong></span>
        <span className="location__sep">/</span>
        <span>Shelf <strong>{sensor.location?.shelf || '—'}</strong></span>
      </div>
    </div>
  );
}
