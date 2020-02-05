/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import { groupBy as _groupBy } from 'lodash';
import DeviceGroupSelectorView from 'akvo-flow/components/selectors/DeviceSelector';

import AssignmentsContext from '../assignment-context';
import DeviceEmpty from '../__partials/DeviceEmpty';

export default class AddDevice extends React.Component {
  state = {
    selectedDevicesIds: [],
  };

  getDeviceGroups() {
    // filter out selected devices
    const { devices, selectedDeviceIds } = this.context.data;

    const unSelectedDevices = devices.filter(device => !selectedDeviceIds.includes(device.id));

    return _groupBy(unSelectedDevices, device => device.deviceGroup.id);
  }

  onSelectDevice = (id, checked) => {
    // convert id to number
    // const id = parseInt(stringId, 10);

    const { selectedDevicesIds } = this.state;
    let newSelectedDevicesIds = [];

    if (checked) {
      newSelectedDevicesIds = selectedDevicesIds.concat(id);
    } else {
      newSelectedDevicesIds = selectedDevicesIds.filter(device => device !== id);
    }

    this.setState({ selectedDevicesIds: newSelectedDevicesIds });
  };

  onSelectMultipleDevices = (ids, checked) => {
    const { selectedDevicesIds } = this.state;
    let newSelectedDevicesIds = [...selectedDevicesIds];

    if (checked) {
      newSelectedDevicesIds = selectedDevicesIds.concat(ids);
    } else {
      newSelectedDevicesIds = selectedDevicesIds.filter(device => !ids.includes(device));
    }

    this.setState({ selectedDevicesIds: [...new Set(newSelectedDevicesIds)] });
  };

  addToAssignment = () => {
    const { selectedDevicesIds } = this.state;
    const { addDevicesToAssignment } = this.context.actions;

    addDevicesToAssignment(selectedDevicesIds);

    // empty selected devices
    this.setState({ selectedDevicesIds: [] });
  };

  render() {
    const { strings } = this.context;
    const deviceGroups = this.getDeviceGroups();
    const { selectedDevicesIds } = this.state;

    return (
      <div className="devices-action-page">
        <div className="header">
          <p>{strings.addDevicesToAssignment}</p>
          <i
            className="fa fa-times icon"
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
              handleSelectAllDevices={this.onSelectMultipleDevices}
              selectedDevices={selectedDevicesIds}
            />
          </div>
        </div>

        <div className="footer">
          <div className="footer-inner">
            <div>
              <p>
                {selectedDevicesIds.length} {strings.selected}
              </p>
            </div>

            <button
              type="button"
              onClick={this.addToAssignment}
              className={`btnOutline ${selectedDevicesIds.length === 0 ? 'disabled' : ''}`}
            >
              {strings.addToAssignment}
            </button>
          </div>
        </div>
      </div>
    );
  }
}

AddDevice.contextType = AssignmentsContext;
AddDevice.propTypes = {
  changeTab: PropTypes.func.isRequired,
};
