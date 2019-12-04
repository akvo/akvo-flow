/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import Dropdown from 'akvo-flow/components/reusable/Dropdown';

export default class AssignDatapoints extends React.Component {
  render() {
    const { selectedDevice } = this.props;
    return (
      <div className="devices-action-page assign-datapoints">
        <div className="header">
          <div className="device-details">
            <p>Device name</p>
            <p>0 Datapoints assigned</p>
          </div>

          <Dropdown title="Assign Datapoints">
            <button type="button">By datapoints name or ID</button>
            <button type="button">Assign all datapoints</button>
          </Dropdown>
        </div>

        <div className="body">
          <p>Body of datapoints {selectedDevice}</p>
        </div>
      </div>
    );
  }
}

AssignDatapoints.propTypes = {
  selectedDevice: PropTypes.string.isRequired,
};
