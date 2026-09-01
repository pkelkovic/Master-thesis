import { useState, useEffect, useCallback } from 'react';
import { fetchSensors, fetchSensorStats, fetchErrors, fetchSensorStatus } from '../api';

const POLL_INTERVAL = 30000;

// ─── MOCK DATA ───────────────────────────────────────────────────────────────

const MOCK_SENSORS = [
  { sensorID: 'a1b2c3d4-e5f6-1a2b-8c9d-0e1f2a3b4c5d', type: 'temperature', model: 'TE-74', location: { zone: 'Z7', aisle: 'A11', shelf: 'S07' }, readingUnit: '°C', available: true },
  { sensorID: 'b2c3d4e5-f6a7-2b3c-9d0e-1f2a3b4c5d6e', type: 'temperature', model: 'TE-38', location: { zone: 'Z4', aisle: 'A03', shelf: 'S29' }, readingUnit: '°C', available: true },
  { sensorID: 'c3d4e5f6-a7b8-3c4d-ab0e-2f3a4b5c6d7e', type: 'humidity',    model: 'HU-07', location: { zone: 'Z1', aisle: 'A06', shelf: 'S50' }, readingUnit: '%',  available: true },
  { sensorID: 'd4e5f6a7-b8c9-4d5e-bb1f-3a4b5c6d7e8f', type: 'humidity',    model: 'HU-98', location: { zone: 'Z7', aisle: 'A12', shelf: 'S46' }, readingUnit: '%',  available: true },
  { sensorID: 'e5f6a7b8-c9d0-1e2f-8b2a-4b5c6d7e8f9a', type: 'weight',     model: 'WE-61', location: { zone: 'Z5', aisle: 'A12', shelf: 'S77' }, readingUnit: 'kg', available: true },
  { sensorID: 'f6a7b8c9-d0e1-2f3a-9c3b-5c6d7e8f9a0b', type: 'weight',     model: 'WE-92', location: { zone: 'Z5', aisle: 'A19', shelf: 'S26' }, readingUnit: 'kg', available: false },
  { sensorID: 'a7b8c9d0-e1f2-3a4b-8d0c-6d7e8f9a0b1c', type: 'temperature', model: 'TE-11', location: { zone: 'Z8', aisle: 'A04', shelf: 'S53' }, readingUnit: '°C', available: false },
];

const MOCK_STATS = {
  'a1b2c3d4-e5f6-1a2b-8c9d-0e1f2a3b4c5d': [
    { statsId: { sensorId: 'a1b2c3d4-e5f6-1a2b-8c9d-0e1f2a3b4c5d', timeframeStart: '2026-06-17T08:00:00Z', timeframeEnd: '2026-06-17T09:00:00Z' }, average: 22.4, stdDev: 0.8, min: 21.1, max: 23.9, count: 360 },
    { statsId: { sensorId: 'a1b2c3d4-e5f6-1a2b-8c9d-0e1f2a3b4c5d', timeframeStart: '2026-06-17T07:00:00Z', timeframeEnd: '2026-06-17T08:00:00Z' }, average: 21.8, stdDev: 0.5, min: 21.0, max: 22.6, count: 360 },
    { statsId: { sensorId: 'a1b2c3d4-e5f6-1a2b-8c9d-0e1f2a3b4c5d', timeframeStart: '2026-06-17T06:00:00Z', timeframeEnd: '2026-06-17T07:00:00Z' }, average: 20.1, stdDev: 1.2, min: 18.5, max: 22.0, count: 360 },
    { statsId: { sensorId: 'a1b2c3d4-e5f6-1a2b-8c9d-0e1f2a3b4c5d', timeframeStart: '2026-06-17T05:00:00Z', timeframeEnd: '2026-06-17T06:00:00Z' }, average: 19.3, stdDev: 0.9, min: 18.0, max: 20.8, count: 360 },
  ],
  'b2c3d4e5-f6a7-2b3c-9d0e-1f2a3b4c5d6e': [
    { statsId: { sensorId: 'b2c3d4e5-f6a7-2b3c-9d0e-1f2a3b4c5d6e', timeframeStart: '2026-06-17T08:00:00Z', timeframeEnd: '2026-06-17T09:00:00Z' }, average: 19.8, stdDev: 0.5, min: 19.1, max: 20.6, count: 360 },
    { statsId: { sensorId: 'b2c3d4e5-f6a7-2b3c-9d0e-1f2a3b4c5d6e', timeframeStart: '2026-06-17T07:00:00Z', timeframeEnd: '2026-06-17T08:00:00Z' }, average: 18.9, stdDev: 0.7, min: 18.0, max: 20.1, count: 360 },
  ],
  'c3d4e5f6-a7b8-3c4d-ab0e-2f3a4b5c6d7e': [
    { statsId: { sensorId: 'c3d4e5f6-a7b8-3c4d-ab0e-2f3a4b5c6d7e', timeframeStart: '2026-06-17T08:00:00Z', timeframeEnd: '2026-06-17T09:00:00Z' }, average: 64.2, stdDev: 2.1, min: 60.1, max: 68.5, count: 360 },
    { statsId: { sensorId: 'c3d4e5f6-a7b8-3c4d-ab0e-2f3a4b5c6d7e', timeframeStart: '2026-06-17T07:00:00Z', timeframeEnd: '2026-06-17T08:00:00Z' }, average: 61.5, stdDev: 1.8, min: 58.0, max: 65.2, count: 360 },
    { statsId: { sensorId: 'c3d4e5f6-a7b8-3c4d-ab0e-2f3a4b5c6d7e', timeframeStart: '2026-06-17T06:00:00Z', timeframeEnd: '2026-06-17T07:00:00Z' }, average: 59.8, stdDev: 2.5, min: 55.0, max: 64.0, count: 360 },
  ],
  'd4e5f6a7-b8c9-4d5e-bb1f-3a4b5c6d7e8f': [
    { statsId: { sensorId: 'd4e5f6a7-b8c9-4d5e-bb1f-3a4b5c6d7e8f', timeframeStart: '2026-06-17T08:00:00Z', timeframeEnd: '2026-06-17T09:00:00Z' }, average: 71.5, stdDev: 3.4, min: 65.0, max: 77.2, count: 360 },
  ],
  'e5f6a7b8-c9d0-1e2f-8b2a-4b5c6d7e8f9a': [
    { statsId: { sensorId: 'e5f6a7b8-c9d0-1e2f-8b2a-4b5c6d7e8f9a', timeframeStart: '2026-06-17T08:00:00Z', timeframeEnd: '2026-06-17T09:00:00Z' }, average: 15.33, stdDev: 0.22, min: 15.0, max: 15.7, count: 180 },
    { statsId: { sensorId: 'e5f6a7b8-c9d0-1e2f-8b2a-4b5c6d7e8f9a', timeframeStart: '2026-06-17T07:00:00Z', timeframeEnd: '2026-06-17T08:00:00Z' }, average: 14.9,  stdDev: 0.31, min: 14.5, max: 15.4, count: 180 },
  ],
  'f6a7b8c9-d0e1-2f3a-9c3b-5c6d7e8f9a0b': [],
  'a7b8c9d0-e1f2-3a4b-8d0c-6d7e8f9a0b1c': [
    { statsId: { sensorId: 'a7b8c9d0-e1f2-3a4b-8d0c-6d7e8f9a0b1c', timeframeStart: '2026-06-16T10:00:00Z', timeframeEnd: '2026-06-16T11:00:00Z' }, average: 25.1, stdDev: 1.1, min: 23.5, max: 27.0, count: 360, unit: "C" },
  ],
};

const MOCK_ERRORS = [
  '[2026-06-17 08:42:11] Sensor f6a7b8c9-d0e1-2f3a-9c3b-5c6d7e8f9a0b (WE-92) removed from database — no readings for 30 minutes',
  '[2026-06-17 07:15:03] Sensor a7b8c9d0-e1f2-3a4b-8d0c-6d7e8f9a0b1c (TE-11) removed from database — connection lost',
  '[2026-06-17 06:03:55] Received reading from unknown sensor: 99999999-0000-1111-2222-333333333333',
  '[2026-06-16 22:11:40] Kafka consumer retry exhausted for partition sensor.stats-0@44',
  '[2026-06-16T19:30:12] Stats calculation failed for sensor b2c3d4e5 — insufficient readings (count < 2)',
];

const MOCK_SENSOR_STATUSES = {
  'a1b2c3d4-e5f6-1a2b-8c9d-0e1f2a3b4c5d': '',
  'b2c3d4e5-f6a7-2b3c-9d0e-1f2a3b4c5d6e': 'ACTIVE',
  'c3d4e5f6-a7b8-3c4d-ab0e-2f3a4b5c6d7e': 'ACTIVE',
  'd4e5f6a7-b8c9-4d5e-bb1f-3a4b5c6d7e8f': 'NEEDS_CHECK',
  'e5f6a7b8-c9d0-1e2f-8b2a-4b5c6d7e8f9a': 'ACTIVE',
  'f6a7b8c9-d0e1-2f3a-9c3b-5c6d7e8f9a0b': '',
  'a7b8c9d0-e1f2-3a4b-8d0c-6d7e8f9a0b1c': 'NOT_ACTIVE',
};

async function attachStatusesToSensors(sensors, getStatus) {
  return Promise.all(
    sensors.map(async (sensor) => {
      const status = await getStatus(sensor.sensorID);

      return {
        ...sensor,
        status,
        available: status !== 'NOT_ACTIVE',
      };
    })
  );
}

const USE_MOCK = false; 

// ─── HOOKS ───────────────────────────────────────────────────────────────────

export function useSensors() {
  const [knownSensors, setKnownSensors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setError(null);
    try {
      if (USE_MOCK) {
        const withStatuses = await attachStatusesToSensors(
          MOCK_SENSORS,
          async (sensorId) => MOCK_SENSOR_STATUSES[sensorId] || 'ACTIVE'
        );

        setKnownSensors(withStatuses);
        setLoading(false);
        return;
      }

      const current = await fetchSensors();

      const withStatuses = await Promise.all(
        current.map(async (sensor) => {
          let status;
          try {
            status = await fetchSensorStatus(sensor.sensorID);
          } catch (err) {
            console.warn(`Failed to fetch status for sensor ${sensor.sensorID}`, err);
            status = null;
          }
          status = status || 'ACTIVE'; 

          return {
            ...sensor,
            status,
            available: status !== 'NOT_ACTIVE',
          };
        })
      );

      const currentIds = new Set(withStatuses.map(s => s.sensorID));

      setKnownSensors(prev => {
        const prevIds = new Set(prev.map(s => s.sensorID));

        const updated = prev.map(oldSensor => {
          const freshSensor = withStatuses.find(s => s.sensorID === oldSensor.sensorID);

          if (!freshSensor) {
            return {
              ...oldSensor,
              status: 'NOT_ACTIVE',
              available: false,
            };
          }

          return freshSensor;
        });

        const newSensors = withStatuses.filter(s => !prevIds.has(s.sensorID));

        return [...updated, ...newSensors];
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  useEffect(() => {
    if (USE_MOCK) return;
    const interval = setInterval(load, POLL_INTERVAL);
    return () => clearInterval(interval);
  }, [load]);

  return { sensors: knownSensors, loading, error, refresh: load };
}

export function useSensorStats(sensors) {
  const [statsMap, setStatsMap] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    if (!sensors.length) return;
    setLoading(true);
    setError(null);
    try {
      if (USE_MOCK) {
        setStatsMap(MOCK_STATS);
        setLoading(false);
        return;
      }

      const entries = await Promise.all(
        sensors.map(async (s) => {
          let stats;
          try {
            stats = await fetchSensorStats(s.sensorID);
          } catch (err) {
            console.warn(`Failed to fetch stats for sensor ${s.sensorID}`, err);
            stats = [];
          }
          return [s.sensorID, stats];
        })
      );
      setStatsMap(Object.fromEntries(entries));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [sensors]);

  useEffect(() => { load(); }, [load]);

  return { statsMap, loading, error, refresh: load };
}

export function useErrors() {
  const [errors, setErrors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      if (USE_MOCK) {
        setErrors(MOCK_ERRORS);
        setLoading(false);
        return;
      }

      const data = await fetchErrors();
      setErrors(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  return { errors, loading, error, refresh: load };
}

