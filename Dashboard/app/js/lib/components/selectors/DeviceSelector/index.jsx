import React from 'react';
// import PropTypes from 'prop-types';

// eslint-disable-next-line import/no-unresolved
import Checkbox from 'akvo-flow/components/Checkbox';

export default class DeviceSelector extends React.Component {
  state = {
    deviceGroups: {},
    deviceGroupNames: {},
    isAccordionOpen: false,
  }

  componentDidMount() {
    let isAccordionOpen = false;
    const deviceGroupNames = {};
    const deviceGroups = {};

    if (FLOW.deviceGroupControl.content.isLoaded) {
      FLOW.deviceGroupControl.get('content').forEach((item) => {
        deviceGroupNames[item.get('keyId')] = item.get('code');
        deviceGroups[item.get('keyId')] = {}; // initialize array of devices per group
      });

      if (FLOW.deviceControl.content.isLoaded) {
        if (FLOW.selectedControl.selectedSurveyAssignment.get('deviceIds')) {
          FLOW.selectedControl.selectedSurveyAssignment.get('deviceIds').forEach((deviceId) => {
            // populate pre-selected devices
            const device = FLOW.Device.find(deviceId);
            if (device && device.get('keyId')) {
              FLOW.selectedControl.selectedDevices.pushObject(device);
            }
          });
        }

        FLOW.deviceControl.get('content').forEach((device) => {
          const checked = this.deviceInAssignment(device.get('keyId'));

          if (checked) {
            isAccordionOpen = true;
          }

          deviceGroups[device.get('deviceGroup') ? device.get('deviceGroup') : 1][device.get('keyId')] = {
            name: device.get('deviceIdentifier'),
            checked,
          };
        });
      }
    }

    this.setState({ deviceGroupNames, deviceGroups, isAccordionOpen });
  }

  onAccordionClick = () => {
    const { isAccordionOpen } = this.state;
    this.setState({ isAccordionOpen: !isAccordionOpen });
  }

  deviceInAssignment = (deviceId) => {
    const devicesInAssignment = FLOW.selectedControl.selectedSurveyAssignment.get('deviceIds');
    return devicesInAssignment ? devicesInAssignment.indexOf(deviceId) > -1 : false;
  }

  handleCheck = (deviceId, checked) => {
    if (checked) {
      // push device to FLOW.selectedControl.selectedDevices
      FLOW.selectedControl.selectedDevices.pushObject(FLOW.Device.find(deviceId));
    } else {
      // remove device to FLOW.selectedControl.selectedDevices
      FLOW.selectedControl.selectedDevices.removeObject(FLOW.Device.find(deviceId));
    }
  }

  render() {
    const { isAccordionOpen, deviceGroups, deviceGroupNames } = this.state;

    const accordionClass = `accordion ${isAccordionOpen && 'active'}`;
    const panelStyle = isAccordionOpen ? { display: 'block' } : { display: 'none' };

    return (
      <div>
        {Object.keys(deviceGroups).map(dgId => (
          <div key={dgId}>
            <div
              className={accordionClass}
              onClick={this.onAccordionClick}
              onKeyPress={this.onAccordionClick}
              data-testid="accordion"
            >
              {deviceGroupNames[dgId]}
            </div>

            <div className="panel" style={panelStyle} data-testid="panel">
              {Object.keys(deviceGroups[dgId]).map(deviceId => (
                <div key={deviceId}>
                  <Checkbox
                    id={deviceId}
                    name={deviceId}
                    checked={deviceGroups[dgId][deviceId].checked}
                    onChange={this.handleCheck}
                  />

                  <label id={deviceId} htmlFor={deviceId}>
                    {deviceGroups[dgId][deviceId].name}
                  </label>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    );
  }
}
