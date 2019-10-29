import React from 'react';
import PropTypes from 'prop-types';

// eslint-disable-next-line import/no-unresolved
import Checkbox from 'akvo-flow/components/reusable/Checkbox';
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
            selectAllCheckbox={() => (
              <Checkbox
                id="0"
                name="0"
                checked={deviceGroups[dgId][0].checked}
                onChange={(...args) => handleDeviceCheck(...args, dgId)}
                label=""
              />
            )}
          >
            {Object.keys(deviceGroups[dgId])
              .filter(deviceId => deviceId != 0)
              .map(deviceId => (
                <div key={deviceId}>
                  <Checkbox
                    id={deviceId}
                    name={deviceId}
                    checked={deviceGroups[dgId][deviceId].checked}
                    onChange={(...args) => handleDeviceCheck(...args, dgId)}
                    label={deviceGroups[dgId][deviceId].name}
                  />
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
