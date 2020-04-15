import React from 'react';
import propTypes from 'prop-types';

export default function DatapointList({ datapointsData }) {
  return (
    <React.Fragment>
      {datapointsData.map(dp => (
        <div key={dp.id} className="datapoint">
          <p>{dp.name}</p>
          <span>{dp.identifier}</span>
        </div>
      ))}
    </React.Fragment>
  );
}

DatapointList.propTypes = {
  datapointsData: propTypes.any.isRequired,
};
