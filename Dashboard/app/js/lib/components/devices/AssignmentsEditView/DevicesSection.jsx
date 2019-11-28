/* eslint-disable import/no-unresolved */
import React from 'react';
// import DeviceGroupSelectorView from 'akvo-flow/components/selectors/DeviceSelector';

export default class DevicesSection extends React.Component {
  render() {
    return (
      <div className="devices">
        <div className="device-empty">
          <i className="fa fa-mobile" />
          <p>No devices added to assignment yet</p>
        </div>

        {/* <div className="heading">
          <h3>{strings.devices}</h3>
          <span className="info">
            {data.numberOfDevices}{' '}
            {data.numberOfDevices === 1 ? strings.device : strings.devices}{' '}
            {strings.selected}
          </span>
        </div> */}

        {/* <div className="assignment-device-selector">
          <DeviceGroupSelectorView
            deviceGroupNames={data.deviceGroupNames}
            deviceGroups={data.deviceGroups}
            activeDeviceGroups={data.activeDeviceGroups}
            handleDeviceCheck={actions.handleDeviceCheck}
            onSelectAll={actions.handleSelectAllDevice}
          />
        </div> */}
      </div>
    );
  }
}
