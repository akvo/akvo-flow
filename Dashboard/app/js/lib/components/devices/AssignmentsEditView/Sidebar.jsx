import React from 'react';
import { groupBy as _groupBy } from 'lodash';
import FormSection from './screens/FormSection';
import AddDevices from './screens/AddDevices';
import EditDevices from './screens/EditDevices';
import DevicesSection from './DevicesSection';
import SidebarDropdown from './__partials/SidebarDropdown';

import AssignmentsContext from './assignment-context';

export default class Sidebar extends React.Component {
  state = {
    currentTab: 'ADD_DEVICE',
  };

  changeTab = tab => {
    this.setState({ currentTab: tab });
  };

  getDeviceGroups = () => {
    // filter out selected devices
    const { devices, selectedDevices } = this.context.data;

    const filteredDevices = devices.filter(device =>
      selectedDevices.includes(device.id)
    );

    return _groupBy(filteredDevices, device => device.deviceGroup.id);
  };

  render() {
    const deviceGroups = this.getDeviceGroups();

    return (
      <React.Fragment>
        <div className="assignment-sidebar">
          <ul>
            <li className={this.state.currentTab === 'FORMS' ? 'active' : ''}>
              <button type="button" onClick={() => this.changeTab('FORMS')}>
                Forms
              </button>
            </li>

            <li className={this.state.currentTab !== 'FORMS' ? 'active' : ''}>
              <button type="button" onClick={() => this.changeTab('DEVICES')}>
                Devices
              </button>

              <button
                className="sub-action"
                type="button"
                onClick={() => this.changeTab('EDIT_DEVICE')}
              >
                Edit
              </button>

              <button
                className="sub-action"
                type="button"
                onClick={() => this.changeTab('ADD_DEVICE')}
              >
                Add
              </button>
            </li>

            <li
              className={`sidebar-dropdown-container ${
                this.state.currentTab !== 'FORMS' ? 'active' : ''
              }`}
            >
              {Object.keys(deviceGroups).map(dgId => (
                <SidebarDropdown devices={deviceGroups[dgId]} />
              ))}
            </li>
          </ul>
        </div>

        <div className="assignment-main">
          {this.state.currentTab === 'FORMS' && (
            <FormSection changeTab={this.changeTab} />
          )}
          {this.state.currentTab === 'ADD_DEVICE' && (
            <AddDevices changeTab={this.changeTab} />
          )}
          {this.state.currentTab === 'EDIT_DEVICE' && (
            <EditDevices changeTab={this.changeTab} />
          )}
          {this.state.currentTab === 'DEVICES' && (
            <DevicesSection changeTab={this.changeTab} />
          )}
        </div>
      </React.Fragment>
    );
  }
}

Sidebar.contextType = AssignmentsContext;
