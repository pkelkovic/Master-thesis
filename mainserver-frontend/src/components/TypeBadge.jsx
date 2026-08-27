import React from 'react';
import './TypeBadge.css';

const TYPE_CLASSES = {
  temperature: 'badge--temperature',
  humidity:    'badge--humidity',
  weight:      'badge--weight',
  pressure:    'badge--pressure',
};

export default function TypeBadge({ type }) {
  const key = (type || '').toLowerCase();
  const cls = Object.keys(TYPE_CLASSES).find(k => key.includes(k));
  return (
    <span className={`type-badge ${TYPE_CLASSES[cls] || 'badge--default'}`}>
      {type}
    </span>
  );
}
