import React, { useMemo, useState } from 'react';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from 'recharts';
import './SensorValueChart.css';

function formatShortDateTime(iso) {
  try {
    const d = new Date(iso);
    return d.toLocaleString([], {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return iso;
  }
}

function formatUnit(unit) {
  return unit ? ` ${unit}` : '';
}

export default function SensorValueChart({ stats, unit }) {
  const [expanded, setExpanded] = useState(false);

  const chartData = useMemo(() => {
    return [...(stats || [])]
      .sort((a, b) => new Date(a.statsId.timeframeStart) - new Date(b.statsId.timeframeStart))
      .map((stat) => ({
        time: formatShortDateTime(stat.statsId.timeframeStart),
        average: Number(stat.average),
        min: Number(stat.min),
        max: Number(stat.max),
      }))
      .filter(row => !Number.isNaN(row.average));
  }, [stats]);

  if (!chartData.length) {
    return (
      <div className="sensor-chart sensor-chart--empty">
        No chart data available.
      </div>
    );
  }

  return (
    <div className={`sensor-chart ${expanded ? 'sensor-chart--expanded' : ''}`}>
      <div className="sensor-chart__header">
        <div>
          <h3 className="sensor-chart__title">Value over time</h3>
          <p className="sensor-chart__subtitle">
            Average readings across recorded stats windows
          </p>
        </div>

        <button
          className="sensor-chart__toggle"
          onClick={() => setExpanded(prev => !prev)}
        >
          {expanded ? 'collapse' : 'expand'}
        </button>
      </div>

      <div className="sensor-chart__body">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis
              dataKey="time"
              tick={{ fontSize: 10 }}
              minTickGap={20}
            />
            <YAxis
              tick={{ fontSize: 10 }}
              width={45}
            />
            <Tooltip
              formatter={(value, name) => [
                `${Number(value).toFixed(2)}${formatUnit(unit)}`,
                name,
              ]}
            />
            <Line
              type="monotone"
              dataKey="average"
              name="Average"
              strokeWidth={2}
              dot={{ r: 3 }}
              activeDot={{ r: 5 }}
            />
            {expanded && (
              <>
                <Line
                  type="monotone"
                  dataKey="min"
                  name="Min"
                  strokeWidth={1.5}
                  dot={false}
                />
                <Line
                  type="monotone"
                  dataKey="max"
                  name="Max"
                  strokeWidth={1.5}
                  dot={false}
                />
              </>
            )}
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}