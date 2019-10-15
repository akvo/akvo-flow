import React from 'react';
import PropTypes from 'prop-types';

// eslint-disable-next-line import/no-unresolved
import Checkbox from 'akvo-flow/components/Checkbox';
import DeviceAccordion from './DeviceAccordion';

export default class DeviceSelector extends React.Component {
  accordionIsActive = id => {
    return this.props.activeDeviceGroups.has(id);
  };

  render() {
    const { deviceGroups, handleDeviceCheck, deviceGroupNames } = this.props;

    return (
      <div>
        {Object.keys(deviceGroups).map(dgId => (
          <DeviceAccordion
            key={dgId}
            name={deviceGroupNames[dgId]}
            deviceGroupIsActive={this.accordionIsActive(dgId)}
          >
            {Object.keys(deviceGroups[dgId]).map(deviceId => (
              <div key={deviceId}>
                <Checkbox
                  id={deviceId}
                  name={deviceId}
                  checked={deviceGroups[dgId][deviceId].checked}
                  onChange={(...args) => handleDeviceCheck(...args, dgId)}
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
  activeDeviceGroups: PropTypes.any.isRequired,
  handleDeviceCheck: PropTypes.func.isRequired,
  onSelectAll: PropTypes.func.isRequired,
};
