import React from 'react';
import StatRow from './StatRow';
import TypeBadge from './TypeBadge';
import './StatsGroup.css';

export default function StatsGroup({ type, entries }) {
  return (
    <div className="stats-group">
      <div className="stats-group__header">
        <TypeBadge type={type} />
        <span className="stats-group__count">
          {entries.length} record{entries.length !== 1 ? 's' : ''}
        </span>
      </div>

      {entries.map(({ sensor, stat }, i) => (
        <StatRow
          key={`${sensor.sensorID}-${stat.statsId.timeframeStart}-${i}`}
          sensor={sensor}
          stat={stat}
        />
      ))}
    </div>
  );
}
