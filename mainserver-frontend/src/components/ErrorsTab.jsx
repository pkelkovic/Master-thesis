import React from 'react';
import './ErrorsTab.css';

export default function ErrorsTab({ errors }) {
  if (!errors.length) {
    return <p className="tab-empty">No errors recorded.</p>;
  }

  return (
    <div className="errors-tab">
      <div className="errors-tab__header">
        <span className="errors-tab__title">History of Errors</span>
        <span className="errors-tab__count">{errors.length} record{errors.length !== 1 ? 's' : ''}</span>
      </div>

      <div className="errors-list">
        {errors.map((msg, i) => (
          <div className="error-item" key={i}>
            <span className="error-item__index">#{String(i + 1).padStart(3, '0')}</span>
            <span className="error-item__icon">⚠</span>
            <span className="error-item__message">{msg}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
