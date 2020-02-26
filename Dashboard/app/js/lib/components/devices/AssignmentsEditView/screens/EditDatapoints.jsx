/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import Checkbox from 'akvo-flow/components/reusable/Checkbox';
import AssignmentsContext from '../assignment-context';

export default class EditDatapoints extends React.Component {
  state = {
    selectedDatapoints: [],
  };

  onSelectDatapoint = (id, checked) => {
    const { selectedDatapoints } = this.state;
    let newSelectedDatapoints = [];

    if (checked) {
      newSelectedDatapoints = selectedDatapoints.concat(id);
    } else {
      newSelectedDatapoints = selectedDatapoints.filter(device => device !== id);
    }

    this.setState({ selectedDatapoints: newSelectedDatapoints });
  };

  getDeviceData = () => {
    const { deviceId: selectedDeviceId } = this.props.routeData;
    const { devices, datapointAssignments } = this.context.data;

    const deviceData = devices.find(device => device.id === selectedDeviceId);
    const selectedDatapointAssignment = datapointAssignments.find(
      dp => dp.deviceId === parseInt(selectedDeviceId, 10)
    );
    let datapointsData = [];

    if (selectedDatapointAssignment) {
      datapointsData = selectedDatapointAssignment.datapoints;
    }

    return {
      deviceData,
      datapointsData,
    };
  };

  removeFromAssignment = () => {
    const { deviceId: selectedDeviceId } = this.props.routeData;
    const { selectedDatapoints } = this.state;
    const { removeDatapointsFromAssignments } = this.context.actions;

    removeDatapointsFromAssignments(selectedDatapoints, selectedDeviceId);

    // empty selected devices
    this.setState({ selectedDatapoints: [] });
  };

  render() {
    const { strings } = this.context;
    const { deviceData, datapointsData } = this.getDeviceData();

    return (
      <div className="devices-action-page edit-datapoints">
        <div className="header">
          <p>{strings.editDatapoints}</p>
          <i
            className="fa fa-times icon"
            onClick={() => this.props.goTo('ASSIGN_DATAPOINTS', { deviceId: deviceData.id })}
            onKeyDown={() => this.props.goTo('ASSIGN_DATAPOINTS', { deviceId: deviceData.id })}
          />
        </div>

        <div className="body">
          {datapointsData.map(dp => (
            <div key={dp.id} className="datapoint">
              <Checkbox
                id={dp.id}
                name={dp.id}
                checked={this.state.selectedDatapoints.includes(dp.id)}
                onChange={this.onSelectDatapoint}
                label=""
              />

              <label htmlFor={dp.id}>
                <p>{dp.name}</p>
                <span>{dp.id}</span>
              </label>
            </div>
          ))}
        </div>

        <div className="footer">
          <div className="footer-inner">
            <div>
              <p>
                {this.state.selectedDatapoints.length} {strings.selected}
              </p>
            </div>

            <button
              type="button"
              onClick={this.removeFromAssignment}
              className={`btnOutline ${
                this.state.selectedDatapoints.length === 0 ? 'disabled' : ''
              }`}
            >
              {strings.removeFromAssignment}
            </button>
          </div>
        </div>
      </div>
    );
  }
}

EditDatapoints.contextType = AssignmentsContext;
EditDatapoints.propTypes = {
  goTo: PropTypes.func.isRequired,
  routeData: PropTypes.object.isRequired,
};
