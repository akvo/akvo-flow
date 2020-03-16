import React from 'react';
import PropTypes from 'prop-types';
import DeviceAccordion from './DeviceAccordion';

export default class DeviceSelector extends React.Component {
  render() {
    const {
      deviceGroups,
      handleSelectDevice,
      selectedDevices,
      handleSelectAllDevices,
    } = this.props;

    return (
      <div>
        {deviceGroups.map(devices => (
          <DeviceAccordion
            key={devices[0].deviceGroup.id}
            id={devices[0].deviceGroup.id}
            name={devices[0].deviceGroup.name}
            devices={devices}
            handleSelectDevice={handleSelectDevice}
            handleSelectAllDevices={handleSelectAllDevices}
            selectedDevices={selectedDevices}
          />
        ))}
      </div>
    );
  }
}

DeviceSelector.propTypes = {
  deviceGroups: PropTypes.array.isRequired,
  handleSelectDevice: PropTypes.func.isRequired,
  handleSelectAllDevices: PropTypes.func.isRequired,
  selectedDevices: PropTypes.array.isRequired,
};
