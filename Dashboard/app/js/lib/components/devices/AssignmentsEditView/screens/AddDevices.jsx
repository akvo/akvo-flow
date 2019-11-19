/* eslint-disable import/no-unresolved */
import React from 'react';
import DeviceGroupSelectorView from 'akvo-flow/components/selectors/DeviceSelector';

import AssignmentsContext from '../assignment-context';

export default class AddDevice extends React.Component {
  render() {
    const { actions, data } = this.context;

    return (
      <div className="add-devices">
        <div className="header">
          <p>Add devices to assignment</p>
          <i className="fa fa-times" />
        </div>

        <div className="body">
          <div className="assignment-device-selector">
            <DeviceGroupSelectorView
              deviceGroupNames={data.deviceGroupNames}
              deviceGroups={data.deviceGroups}
              activeDeviceGroups={data.activeDeviceGroups}
              handleDeviceCheck={actions.handleDeviceCheck}
              onSelectAll={actions.handleSelectAllDevice}
            />
          </div>
        </div>

        <div className="footer">
          <div className="footer-inner">
            <div>
              <p>{data.numberOfDevices} selected</p>
            </div>

            <button type="button">Add to assignment</button>
          </div>
        </div>
      </div>
    );
  }
}

AddDevice.contextType = AssignmentsContext;
