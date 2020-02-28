/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import AssignmentsContext from '../assignment-context';

export default class DevicesSection extends React.Component {
  state = {
    errorMessage: '',
  };

  componentDidMount() {
    const { strings } = this.context;

    // if devices are available and monitoring survey is seleected, go to the first device page
    const { selectedDeviceIds, datapointsEnabled } = this.context.data;

    if (!selectedDeviceIds.length) {
      return this.setState({ errorMessage: strings.noDeviceInAssignment });
    }

    if (!datapointsEnabled) {
      return this.setState({ errorMessage: strings.selectAMonitoringSurveyMessage });
    }

    this.props.changeTab('ASSIGN_DATAPOINTS', selectedDeviceIds[0]);
  }

  render() {
    return (
      <div className="devices">
        <div className="device-empty">
          <i className="fa fa-mobile" />
          <p>{this.state.errorMessage}</p>
        </div>
      </div>
    );
  }
}

DevicesSection.contextType = AssignmentsContext;
DevicesSection.propTypes = {
  changeTab: PropTypes.func.isRequired,
};
