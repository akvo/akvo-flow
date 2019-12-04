/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import { groupBy as _groupBy } from 'lodash';

import FormSection from './screens/FormSection';
import AddDevices from './screens/AddDevices';
import EditDevices from './screens/EditDevices';
import AssignDatapoints from './screens/AssignDatapoints';

import DevicesSection from './DevicesSection';
import SidebarDropdown from './__partials/SidebarDropdown';

import AssignmentsContext from './assignment-context';

export default class AssignmentMain extends React.Component {
  state = {
    currentTab: 'ASSIGN_DATAPOINTS',
    selectedDevice: '150482013',
  };

  changeTab = (tab, selectedDevice = null) => {
    this.setState({ currentTab: tab, selectedDevice });
  };

  getDeviceGroups = () => {
    // filter out selected devices
    const { devices, selectedDeviceIds } = this.context.data;

    const filteredDevices = devices.filter(device =>
      selectedDeviceIds.includes(device.id)
    );

    return _groupBy(filteredDevices, device => device.deviceGroup.id);
  };

  renderSidebar = () => {
    const deviceGroups = this.getDeviceGroups();
    const { selectedDeviceIds } = this.context.data;
    const deviceIsSelected = selectedDeviceIds.length > 0;

    return (
      <div className="assignment-sidebar">
        <ul>
          <li className={this.state.currentTab === 'FORMS' ? 'active' : ''}>
            <a href="#" onClick={() => this.changeTab('FORMS')}>
              Forms
            </a>
          </li>

          <li className={this.state.currentTab !== 'FORMS' ? 'active' : ''}>
            <a
              className={deviceIsSelected ? 'disabled' : undefined}
              href="#"
              onClick={
                deviceIsSelected ? undefined : () => this.changeTab('DEVICES')
              }
            >
              Devices
            </a>

            {deviceIsSelected && (
              <a
                href="#"
                className="sub-action"
                onClick={() => this.changeTab('EDIT_DEVICE')}
              >
                Edit
              </a>
            )}

            <a
              href="#"
              className="sub-action"
              onClick={() => this.changeTab('ADD_DEVICE')}
            >
              Add
            </a>
          </li>

          <li
            className={`sidebar-dropdown-container ${
              this.state.currentTab !== 'FORMS' ? 'active' : ''
            }`}
          >
            {Object.keys(deviceGroups).map(dgId => (
              <SidebarDropdown
                key={dgId}
                devices={deviceGroups[dgId]}
                changeTab={this.changeTab}
              />
            ))}
          </li>
        </ul>
      </div>
    );
  };

  render() {
    const { currentTab, selectedDevice } = this.state;
    return (
      <div className="assignment-body">
        {this.renderSidebar()}

        <div className="assignment-main">
          {currentTab === 'FORMS' && <FormSection changeTab={this.changeTab} />}
          {currentTab === 'ADD_DEVICE' && (
            <AddDevices changeTab={this.changeTab} />
          )}
          {currentTab === 'EDIT_DEVICE' && (
            <EditDevices changeTab={this.changeTab} />
          )}
          {currentTab === 'DEVICES' && (
            <DevicesSection changeTab={this.changeTab} />
          )}
          {currentTab === 'ASSIGN_DATAPOINTS' && (
            <AssignDatapoints selectedDevice={selectedDevice} />
          )}
        </div>
      </div>
    );
  }
}

AssignmentMain.contextType = AssignmentsContext;
