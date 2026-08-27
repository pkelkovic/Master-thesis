import React, { useMemo, useState } from 'react';
import StatsGroup from './StatsGroup';
import './StatsTab.css';

export default function StatsTab({ sensors, statsMap }) {
  const groups = useMemo(() => {
    const map = {};

    sensors.forEach(sensor => {
      const type = sensor.type || 'unknown';
      const stats = statsMap[sensor.sensorID] || [];

      const sorted = [...stats].sort(
        (a, b) => new Date(b.statsId.timeframeStart) - new Date(a.statsId.timeframeStart)
      );

      sorted.forEach(stat => {
        if (!map[type]) map[type] = [];
        map[type].push({ sensor, stat });
      });
    });

    return Object.entries(map).sort(([a], [b]) => a.localeCompare(b));
  }, [sensors, statsMap]);

  const [selectedType, setSelectedType] = useState(null);

  if (!groups.length) {
    return <p className="tab-empty">No stats available.</p>;
  }

  const activeType =
    selectedType && groups.some(([type]) => type === selectedType)
      ? selectedType
      : groups[0][0];

  const activeGroup = groups.find(([type]) => type === activeType);
  const activeEntries = activeGroup ? activeGroup[1] : [];

  return (
    <div className="stats-tab">
      <div className="stats-tab__type-tabs">
        {groups.map(([type, entries]) => (
          <button
            key={type}
            className={`stats-tab__type-tab ${
              activeType === type ? 'stats-tab__type-tab--active' : ''
            }`}
            onClick={() => setSelectedType(type)}
          >
            {type}

          </button>
        ))}
      </div>

      <StatsGroup type={activeType} entries={activeEntries} />
    </div>
  );
}