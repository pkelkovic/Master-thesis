const BASE = window.ENV?.API_BASE ?? '';

async function safeJson(res, fallback) {
  const text = await res.text();
  if (!text) return fallback;

  try {
    return JSON.parse(text);
  } catch (err) {
    console.warn('Malformed JSON response, using fallback', err);
    return fallback;
  }
}

export async function fetchSensors() {
  const res = await fetch(`${BASE}/api/sensors/info`);
  if (!res.ok) throw new Error(`Failed to fetch sensors: ${res.status}`);
  return safeJson(res, []);
}

export async function fetchSensorStats(sensorId) {
  const res = await fetch(`${BASE}/api/sensors/${sensorId}/stats`);
  if (!res.ok) throw new Error(`Failed to fetch stats for ${sensorId}: ${res.status}`);
  return safeJson(res, []);
}

export async function fetchErrors() {
  const res = await fetch(`${BASE}/api/sensors/errors`);
  if (!res.ok) throw new Error(`Failed to fetch errors: ${res.status}`);
  return res.json();
}

export async function fetchSensorStatus(sensorId) {
  const res = await fetch(`${BASE}/api/sensors/${sensorId}/status`);
  if (!res.ok) throw new Error(`Failed to fetch status for ${sensorId}: ${res.status}`);

  const parsed = await safeJson(res, null);
  return parsed || 'ACTIVE';
}
