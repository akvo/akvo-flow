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
    const { selectedDevice } = this.props;
    const { devices, selectedDatapoints } = this.context.data;

    const deviceData = devices.find(device => device.id === selectedDevice);
    const selectedDatapoint = selectedDatapoints.find(dp => dp.deviceId === selectedDevice);
    let datapointsData = [];

    if (selectedDatapoint) {
      datapointsData = selectedDatapoint.datapoints;
    }

    return {
      deviceData,
      datapointsData,
    };
  };

  removeFromAssignment = () => {
    const { selectedDatapoints } = this.state;
    const { removeDatapointsFromAssignments } = this.context.actions;

    removeDatapointsFromAssignments(selectedDatapoints, this.props.selectedDevice);

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
            onClick={() => this.props.changeTab('ASSIGN_DATAPOINTS', deviceData.id)}
            onKeyDown={() => this.props.changeTab('ASSIGN_DATAPOINTS', deviceData.id)}
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
  changeTab: PropTypes.func.isRequired,
  selectedDevice: PropTypes.number.isRequired,
};
