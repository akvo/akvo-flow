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
        {Object.keys(deviceGroups).map(dgId => (
          <DeviceAccordion
            key={dgId}
            id={dgId}
            name={deviceGroups[dgId][0].deviceGroup.name}
            devices={deviceGroups[dgId]}
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
  deviceGroups: PropTypes.object.isRequired,
  handleSelectDevice: PropTypes.func.isRequired,
  handleSelectAllDevices: PropTypes.func.isRequired,
  selectedDevices: PropTypes.array.isRequired,
};
