/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import SearchDatapoints from './SearchDatapoints';
import AssignmentContext from '../../assignment-context';

export default class AssignDatapoints extends React.Component {
  state = {
    currentSubTab: 'SEARCH_DATAPOINTS',
  };

  changeTab = tab => {
    this.setState({
      currentSubTab: tab,
    });
  };

  renderDatapoint = dp => {
    return (
      <div key={dp.id} className="datapoint">
        <p>{dp.name}</p>
        <span>{dp.id}</span>
      </div>
    );
  };

  getDeviceData = () => {
    const { selectedDevice } = this.props;
    const { devices, selectedDatapoints } = this.context.data;

    const deviceData = devices.find(device => device.id === selectedDevice);
    const selectedDatapoint = selectedDatapoints.find(
      dp => dp.deviceId === selectedDevice
    );
    let datapointsData = [];

    if (selectedDatapoint) {
      datapointsData = selectedDatapoint.datapoints;
    }

    return {
      deviceData,
      datapointsData,
    };
  };

  render() {
    const { deviceData, datapointsData } = this.getDeviceData();

    return (
      <div className="devices-action-page assign-datapoints">
        <div>
          <div className="header">
            <div className="device-details">
              <p>{deviceData.name}</p>
              <p>{datapointsData.length} Datapoints assigned</p>
            </div>

            <button
              onClick={() => this.changeTab('SEARCH_DATAPOINTS')}
              type="button"
            >
              By datapoints name or ID
            </button>
          </div>

          <div className="body">{datapointsData.map(this.renderDatapoint)}</div>
        </div>

        <div>
          {this.state.currentSubTab === 'SEARCH_DATAPOINTS' && (
            <SearchDatapoints
              deviceId={this.props.selectedDevice}
              changeTab={this.changeTab}
            />
          )}
        </div>
      </div>
    );
  }
}

AssignDatapoints.contextType = AssignmentContext;
AssignDatapoints.propTypes = {
  selectedDevice: PropTypes.string.isRequired,
};
