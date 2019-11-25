/* eslint-disable import/no-unresolved */
import React from 'react';

export default class DevicesSection extends React.Component {
  render() {
    return (
      <div className="devices">
        <div className="device-empty">
          <i className="fa fa-mobile" />
          <p>No devices added to assignment yet</p>
        </div>
      </div>
    );
  }
}
