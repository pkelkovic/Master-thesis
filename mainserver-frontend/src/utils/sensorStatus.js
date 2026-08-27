import { loadThresholds } from '../components/ThresholdsForm';

// Priority: NOT_ACTIVE > NEEDS_CHECK > WARNING > ACTIVE
// WARNING only overrides ACTIVE

export function computeStatus(sensor, latestStat) {
  const backendStatus = sensor.status ?? 'ACTIVE';

  if (backendStatus === 'NOT_ACTIVE') return 'NOT_ACTIVE';
  if (backendStatus === 'NEEDS_CHECK') return 'NEEDS_CHECK';

  if (backendStatus === 'ACTIVE' && latestStat) {
    const thresholds = loadThresholds();
    const t = thresholds[sensor.type?.toLowerCase()];

    if (t) {
      const avg = Number(latestStat.average);
      const belowMin = t.min !== null && avg < t.min;
      const aboveMax = t.max !== null && avg > t.max;

      if (belowMin || aboveMax) return 'WARNING';
    }
  }

  return 'ACTIVE';
}

export const STATUS_LABELS = {
  ACTIVE:      'active',
  WARNING:     'warning',
  NEEDS_CHECK: 'needs check',
  NOT_ACTIVE:  'not active',
};

export const STATUS_CSS = {
  ACTIVE:      'status--active',
  WARNING:     'status--warning',
  NEEDS_CHECK: 'status--needs-check',
  NOT_ACTIVE:  'status--not-active',
};

export const CARD_CSS = {
  ACTIVE:      'sensor-card--active',
  WARNING:     'sensor-card--warning',
  NEEDS_CHECK: 'sensor-card--needs-check',
  NOT_ACTIVE:  'sensor-card--not-active',
};
