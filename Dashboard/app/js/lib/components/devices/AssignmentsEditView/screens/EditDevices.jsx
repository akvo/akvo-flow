/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import { groupBy as _groupBy } from 'lodash';
import DeviceGroupSelectorView from 'akvo-flow/components/selectors/DeviceSelector';

import AssignmentsContext from '../assignment-context';
import DeviceEmpty from '../__partials/DeviceEmpty';

export default class EditDevices extends React.Component {
  state = {
    selectedDevices: [],
  };

  getDeviceGroups() {
    // filter out selected devices
    const { devices, selectedDevices } = this.context.data;

    const filteredDevices = devices.filter(device =>
      selectedDevices.includes(device.id)
    );

    return _groupBy(filteredDevices, device => device.deviceGroup.id);
  }

  onSelectDevice = (id, checked) => {
    const { selectedDevices } = this.state;

    if (checked) {
      this.setState({
        selectedDevices: selectedDevices.concat(id),
      });
    } else {
      this.setState({
        selectedDevices: selectedDevices.filter(device => device !== id),
      });
    }
  };

  removeFromAssignment = () => {
    const { selectedDevices } = this.state;
    const { removeDevicesFromAssignment } = this.context.actions;

    removeDevicesFromAssignment(selectedDevices);

    // empty selected devices
    this.setState({ selectedDevices: [] });
  };

  render() {
    const deviceGroups = this.getDeviceGroups();
    const { selectedDevices } = this.state;

    return (
      <div className="devices-action-page">
        <div className="header">
          <p>Remove devices from assignment</p>
          <i
            className="fa fa-times"
            onClick={() => this.props.changeTab('DEVICES')}
            onKeyDown={() => this.props.changeTab('DEVICES')}
          />
        </div>

        <div className="body">
          {Object.keys(deviceGroups).length === 0 && (
            <DeviceEmpty warningText="No device to be added to assignment" />
          )}

          <div className="assignment-device-selector">
            <DeviceGroupSelectorView
              deviceGroups={deviceGroups}
              handleSelectDevice={this.onSelectDevice}
              selectedDevices={selectedDevices}
            />
          </div>
        </div>

        <div className="footer">
          <div className="footer-inner">
            <div>
              <p>{selectedDevices.length} selected</p>
            </div>

            <button
              type="button"
              onClick={this.removeFromAssignment}
              className={`btnOutline ${
                selectedDevices.length === 0 ? 'disabled' : ''
              }`}
            >
              Remove from assignment
            </button>
          </div>
        </div>
      </div>
    );
  }
}

EditDevices.contextType = AssignmentsContext;
EditDevices.propTypes = {
  changeTab: PropTypes.func.isRequired,
};
