import React, { useMemo, useState } from 'react';
import SensorCard from './SensorCard';
import SensorDetail from './SensorDetail';
import ThresholdsForm from './ThresholdsForm';
import { computeStatus } from '../utils/sensorStatus';
import './SensorsTab.css';

const STATUS_ORDER = { NOT_ACTIVE: -1, NEEDS_CHECK: 2, WARNING: 1, ACTIVE: 0 };

export default function SensorsTab({ sensors, statsMap }) {
  const [selectedId, setSelectedId] = useState(null);
  const [showThresholds, setShowThresholds] = useState(false);
  const [thresholdVersion, setThresholdVersion] = useState(0);

  // get latest stat per sensor (first after sorting newest-first)
  function getLatestStat(sensorID) {
    const stats = statsMap?.[sensorID] || [];
    if (!stats.length) return null;
    return [...stats].sort(
      (a, b) => new Date(b.statsId.timeframeStart) - new Date(a.statsId.timeframeStart)
    )[0];
  }

  const sorted = useMemo(() => {
    return [...sensors].sort((a, b) => {
      const statusA = STATUS_ORDER[computeStatus(a, getLatestStat(a.sensorID))] ?? 0;
      const statusB = STATUS_ORDER[computeStatus(b, getLatestStat(b.sensorID))] ?? 0;
      return statusB - statusA;
    });
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sensors, statsMap, thresholdVersion]);

  const sensorTypes = [...new Set(sensors.map(s => s.type?.toLowerCase()).filter(Boolean))];

  if (selectedId) {
    const sensor = sensors.find(s => s.sensorID === selectedId);
    const stats = statsMap?.[selectedId] || [];
    return (
      <SensorDetail
        sensor={sensor}
        stats={stats}
        onBack={() => setSelectedId(null)}
      />
    );
  }

  if (!sorted.length) return <p className="tab-empty">No sensors found.</p>;

  const counts = { ACTIVE: 0, WARNING: 0, NEEDS_CHECK: 0, NOT_ACTIVE: 0 };
  sensors.forEach(s => {
    const status = computeStatus(s, getLatestStat(s.sensorID));
    counts[status] = (counts[status] || 0) + 1;
  });

  return (
    <div>
      <div className="sensors-tab__topbar">
        <div className="sensors-tab__summary">
          {counts.ACTIVE      > 0 && <span className="summary__active">● {counts.ACTIVE} active</span>}
          {counts.WARNING     > 0 && <span className="summary__warning">● {counts.WARNING} warning</span>}
          {counts.NEEDS_CHECK > 0 && <span className="summary__needs-check">● {counts.NEEDS_CHECK} needs check</span>}
          {counts.NOT_ACTIVE  > 0 && <span className="summary__not-active">● {counts.NOT_ACTIVE} not active</span>}
        </div>
        <button
          className="sensors-tab__thresholds-btn"
          onClick={() => setShowThresholds(true)}
        >
          ⚙ set thresholds
        </button>
      </div>

      <div className="sensors-grid">
        {sorted.map(sensor => (
          <SensorCard
            key={sensor.sensorID}
            sensor={sensor}
            latestStat={getLatestStat(sensor.sensorID)}
            onClick={() => setSelectedId(sensor.sensorID)}
          />
        ))}
      </div>

      {showThresholds && (
        <ThresholdsForm
          sensorTypes={sensorTypes}
          onClose={() => {
            setShowThresholds(false);
            setThresholdVersion(v => v + 1); // recompute statuses after save
          }}
        />
      )}
    </div>
  );
}