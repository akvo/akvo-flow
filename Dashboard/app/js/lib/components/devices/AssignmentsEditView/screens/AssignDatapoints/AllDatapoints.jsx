import React from 'react';

export default function AllDatapoints() {
  return (
    <div className="all-dp-assigned">
      <div className="dp-info">
        <p>All 20k datapoints assigned</p>
        <p className="info">Unassign all datapoints to assign by other options</p>
      </div>

      <div className="unassign-dp">
        <button type="button">Unassign</button>
      </div>
    </div>
  );
}
