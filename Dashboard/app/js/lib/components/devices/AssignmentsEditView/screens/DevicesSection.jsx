/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import AssignmentsContext from '../assignment-context';

export default class DevicesSection extends React.Component {
  componentDidMount() {
    // if devices are available and monitoring survey is seleected, go to the first device page
    const { selectedDeviceIds, datapointsEnabled } = this.context.data;

    if (!selectedDeviceIds.length) {
      return;
    }

    if (!datapointsEnabled) {
      // TODO:
      return;
    }

    this.props.changeTab('ASSIGN_DATAPOINTS', selectedDeviceIds[0]);
  }

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
DevicesSection.propTypes = {
  changeTab: PropTypes.func.isRequired,
};
