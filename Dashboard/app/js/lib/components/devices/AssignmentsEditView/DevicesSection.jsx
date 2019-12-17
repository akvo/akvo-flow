/* eslint-disable import/no-unresolved */
import React from 'react';
import AssignmentsContext from './assignment-context';

export default class DevicesSection extends React.Component {
  render() {
    const { strings } = this.context;

    return (
      <div className="devices">
        <div className="device-empty">
          <i className="fa fa-mobile" />
          <p>{strings.noDeviceInAssignment}</p>
        </div>
      </div>
    );
  }
}

DevicesSection.contextType = AssignmentsContext;
