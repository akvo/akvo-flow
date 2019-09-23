import React from 'react';

// eslint-disable-next-line import/no-unresolved
import DeviceSelector from 'akvo-flow/components/selectors/DeviceSelector';
/* eslint-disable jsx-a11y/label-has-associated-control */
/* eslint-disable jsx-a11y/label-has-for */
/* eslint-disable jsx-a11y/click-events-have-key-events */
// eslint-disable-next-line import/no-unresolved
require('akvo-flow/views/react-component');

FLOW.DeviceGroupSelectorView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
    this.deviceGroups = {};
    this.deviceGroupNames = {};
    this.handleChange = this.handleChange.bind(this);
    this.deviceInAssignment = this.deviceInAssignment.bind(this);
    this.renderDevices = this.renderDevices.bind(this);
  },

  didInsertElement(...args) {
    this._super(...args);
    if (FLOW.deviceGroupControl.content.isLoaded) {
      FLOW.deviceGroupControl.get('content').forEach((item) => {
        this.deviceGroupNames[item.get('keyId')] = item.get('code');
        this.deviceGroups[item.get('keyId')] = {}; // initialize array of devices per group
      });

      if (FLOW.deviceControl.content.isLoaded) {
        FLOW.selectedControl.selectedSurveyAssignment.get('devices').forEach((deviceId) => {
          // populate pre-selected devices
          const device = FLOW.Device.find(deviceId);
          if (device && device.get('keyId')) {
            FLOW.selectedControl.selectedDevices.pushObject(device);
          }
        });

        FLOW.deviceControl.get('content').forEach((device) => {
          this.deviceGroups[device.get('deviceGroup') ? device.get('deviceGroup') : 1][device.get('keyId')] = {
            name: device.get('deviceIdentifier'),
            checked: this.deviceInAssignment(device.get('keyId')),
          };
        });
      }
      this.renderDevices();
    }
  },

  deviceInAssignment(deviceId) {
    const devicesInAssignment = FLOW.selectedControl.selectedSurveyAssignment.get('devices');
    return devicesInAssignment.indexOf(deviceId) > -1;
  },

  handleChange(deviceId, checked) {
    if (checked) {
      // push device to FLOW.selectedControl.selectedDevices
      FLOW.selectedControl.selectedDevices.pushObject(FLOW.Device.find(deviceId));
    } else {
      // remove device to FLOW.selectedControl.selectedDevices
      FLOW.selectedControl.selectedDevices.removeObject(FLOW.Device.find(deviceId));
    }
  },

  renderDevices() {
    this.reactRender(
      <DeviceSelector
        deviceGroups={this.deviceGroups}
        deviceGroupNames={this.deviceGroupNames}
        onCheck={this.handleChange}
      />
    );
  },
});
