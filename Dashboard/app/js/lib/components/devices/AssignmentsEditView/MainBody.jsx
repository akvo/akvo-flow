/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import { groupBy as _groupBy } from 'lodash';

import FormSection from './screens/FormSection';
import AddDevices from './screens/AddDevices';
import EditDevices from './screens/EditDevices';
import AssignDatapoints from './screens/AssignDatapoints';
import EditDatapoints from './screens/EditDatapoints';

import DevicesSection from './DevicesSection';
import SidebarDropdown from './__partials/SidebarDropdown';

import AssignmentsContext from './assignment-context';

export default class AssignmentMain extends React.Component {
  state = {
    currentTab: 'FORMS',
    selectedDevice: '', // 150482013
  };

  changeTab = (tab, selectedDevice = null) => {
    this.setState({ currentTab: tab, selectedDevice }, () => {
      if (tab === 'ASSIGN_DATAPOINTS') {
        // load full details for each datapoint when viewing device datapoints
        this.context.actions.getDeviceDatapoints(selectedDevice);
      }
    });
  };

  getDeviceGroups = () => {
    // filter out selected devices
    const { devices, selectedDeviceIds } = this.context.data;

    const filteredDevices = devices.filter(device => selectedDeviceIds.includes(device.id));

    return _groupBy(filteredDevices, device => device.deviceGroup.id);
  };

  renderSidebar = () => {
    const { strings, data } = this.context;
    const deviceGroups = this.getDeviceGroups();
    const deviceIsSelected = data.selectedDeviceIds.length > 0;

    return (
      <div className="assignment-sidebar">
        <ul>
          <li className={this.state.currentTab === 'FORMS' ? 'active' : ''}>
            <a href="#" onClick={() => this.changeTab('FORMS')}>
              {strings.forms}
            </a>
          </li>

          <li className={this.state.currentTab !== 'FORMS' ? 'active' : ''}>
            <a
              className={deviceIsSelected ? 'disabled' : undefined}
              href="#"
              onClick={deviceIsSelected ? undefined : () => this.changeTab('DEVICES')}
            >
              {strings.devices}
            </a>

            {deviceIsSelected && (
              <a href="#" className="sub-action" onClick={() => this.changeTab('EDIT_DEVICE')}>
                {strings.edit}
              </a>
            )}

            <a href="#" className="sub-action" onClick={() => this.changeTab('ADD_DEVICE')}>
              {strings.add}
            </a>
          </li>

          <li
            className={`sidebar-dropdown-container ${
              this.state.currentTab !== 'FORMS' ? 'active' : ''
            }`}
          >
            {Object.keys(deviceGroups).map(dgId => (
              <SidebarDropdown key={dgId} devices={deviceGroups[dgId]} changeTab={this.changeTab} />
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
          {currentTab === 'ADD_DEVICE' && <AddDevices changeTab={this.changeTab} />}
          {currentTab === 'EDIT_DEVICE' && <EditDevices changeTab={this.changeTab} />}
          {currentTab === 'DEVICES' && <DevicesSection changeTab={this.changeTab} />}
          {currentTab === 'ASSIGN_DATAPOINTS' && (
            <AssignDatapoints changeTab={this.changeTab} selectedDevice={selectedDevice} />
          )}
          {currentTab === 'EDIT_DATAPOINTS' && (
            <EditDatapoints changeTab={this.changeTab} selectedDevice={selectedDevice} />
          )}
        </div>
      </div>
    );
  }
}

AssignmentMain.contextType = AssignmentsContext;
