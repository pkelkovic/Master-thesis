import React, { useState, useEffect } from 'react';
import './ThresholdsForm.css';

const DEFAULT_THRESHOLDS = {};

export function loadThresholds() {
  try {
    const stored = localStorage.getItem('sensor_thresholds');
    return stored ? JSON.parse(stored) : DEFAULT_THRESHOLDS;
  } catch {
    return DEFAULT_THRESHOLDS;
  }
}

function saveThresholds(thresholds) {
  localStorage.setItem('sensor_thresholds', JSON.stringify(thresholds));
}

export default function ThresholdsForm({ sensorTypes, onClose }) {
  const [thresholds, setThresholds] = useState(loadThresholds);
  const [saved, setSaved] = useState(false);

  // initialize missing types with empty values
  useEffect(() => {
    setThresholds(prev => {
      const updated = { ...prev };
      sensorTypes.forEach(type => {
        if (!updated[type]) updated[type] = { min: '', max: '' };
      });
      return updated;
    });
  }, [sensorTypes]);

  function handleChange(type, field, value) {
    setThresholds(prev => ({
      ...prev,
      [type]: { ...prev[type], [field]: value }
    }));
    setSaved(false);
  }

  function handleSave() {
    // convert to numbers, remove empty entries
    const cleaned = {};
    Object.entries(thresholds).forEach(([type, { min, max }]) => {
      const hasMin = min !== '' && !isNaN(parseFloat(min));
      const hasMax = max !== '' && !isNaN(parseFloat(max));
      if (hasMin || hasMax) {
        cleaned[type] = {
          min: hasMin ? parseFloat(min) : null,
          max: hasMax ? parseFloat(max) : null,
        };
      }
    });
    saveThresholds(cleaned);
    setSaved(true);
    setTimeout(() => onClose(), 800);
  }

  function handleClear(type) {
    setThresholds(prev => ({ ...prev, [type]: { min: '', max: '' } }));
    setSaved(false);
  }

  return (
    <div className="thresholds-overlay" onClick={onClose}>
      <div className="thresholds-form" onClick={e => e.stopPropagation()}>
        <div className="thresholds-form__header">
          <span className="thresholds-form__title">warning thresholds</span>
          <button className="thresholds-form__close" onClick={onClose}>✕</button>
        </div>

        <p className="thresholds-form__desc">
          Set acceptable average value ranges per sensor type. Active sensors whose latest average falls outside these ranges will show as <strong>WARNING</strong>.
        </p>

        <div className="thresholds-form__fields">
          {sensorTypes.map(type => (
            <div className="threshold-row" key={type}>
              <span className="threshold-row__type">{type}</span>
              <div className="threshold-row__inputs">
                <label>
                  <span>min</span>
                  <input
                    type="number"
                    value={thresholds[type]?.min ?? ''}
                    onChange={e => handleChange(type, 'min', e.target.value)}
                    placeholder="—"
                  />
                </label>
                <label>
                  <span>max</span>
                  <input
                    type="number"
                    value={thresholds[type]?.max ?? ''}
                    onChange={e => handleChange(type, 'max', e.target.value)}
                    placeholder="—"
                  />
                </label>
                <button className="threshold-row__clear" onClick={() => handleClear(type)}>clear</button>
              </div>
            </div>
          ))}
        </div>

        <div className="thresholds-form__footer">
          <button className="thresholds-form__save" onClick={handleSave}>
            {saved ? '✓ saved' : 'save thresholds'}
          </button>
        </div>
      </div>
    </div>
  );
}
