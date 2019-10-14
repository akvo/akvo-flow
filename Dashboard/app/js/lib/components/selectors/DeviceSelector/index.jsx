import React from 'react';
import PropTypes from 'prop-types';

// eslint-disable-next-line import/no-unresolved
import Checkbox from 'akvo-flow/components/Checkbox';
import DeviceAccordion from './DeviceAccordion';

export default class DeviceSelector extends React.Component {
  render() {
    const { deviceGroups, handleDeviceCheck, deviceGroupNames } = this.props;

    return (
      <div>
        {Object.keys(deviceGroups).map(dgId => (
          <DeviceAccordion name={deviceGroupNames[dgId]}>
            {Object.keys(deviceGroups[dgId]).map(deviceId => (
              <div key={deviceId}>
                <Checkbox
                  id={deviceId}
                  name={deviceId}
                  checked={deviceGroups[dgId][deviceId].checked}
                  onChange={handleDeviceCheck}
                />

                <label id={deviceId} htmlFor={deviceId}>
                  {deviceGroups[dgId][deviceId].name}
                </label>
              </div>
            ))}
          </DeviceAccordion>
        ))}
      </div>
    );
  }
}

DeviceSelector.propTypes = {
  deviceGroups: PropTypes.object.isRequired,
  deviceGroupNames: PropTypes.object.isRequired,
  deviceGroupIsActive: PropTypes.bool.isRequired,
  handleDeviceCheck: PropTypes.func.isRequired,
};
