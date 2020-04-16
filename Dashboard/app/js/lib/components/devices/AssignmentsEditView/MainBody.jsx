/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import { groupBy as _groupBy } from 'lodash';

import FormSection from './screens/FormSection';
import AddDevices from './screens/AddDevices';
import EditDevices from './screens/EditDevices';
import AssignDatapoints from './screens/AssignDatapoints';
import EditDatapoints from './screens/EditDatapoints';
import DevicesSection from './screens/DevicesSection';

import SidebarDropdown from './__partials/SidebarDropdown';

import AssignmentsContext from './assignment-context';

export default class AssignmentMain extends React.Component {
  state = {
    currentTab: 'FORMS',
    selectedDeviceId: null,
  };

  changeTab = (tab, selectedDeviceId = null) => {
    this.setState({ currentTab: tab, selectedDeviceId }, () => {
      if (tab === 'ASSIGN_DATAPOINTS') {
        // load full details for each datapoint when viewing device datapoints
        this.context.actions.getDeviceDatapoints(selectedDeviceId);
      }
    });
  };

  getDeviceGroups = () => {
    // filter out selected devices
    const { devices, selectedDeviceIds } = this.context.data;

    const selectedDevices = devices.filter(device => selectedDeviceIds.includes(device.id));

    return _groupBy(selectedDevices, device => device.deviceGroup.id);
  };

  renderSidebar = () => {
    const { strings, data } = this.context;
    const deviceGroups = this.getDeviceGroups();
    const deviceIsSelected = data.selectedDeviceIds.length > 0;
    const { currentTab } = this.state;

    return (
      <div className="assignment-sidebar">
        <ul>
          <li className={currentTab === 'FORMS' ? 'active' : undefined}>
            <a href="#" onClick={() => this.changeTab('FORMS')}>
              {strings.forms}
            </a>
          </li>

          <li className={currentTab !== 'FORMS' ? 'active' : undefined}>
            <a
              className="disabled"
              href="#"
              onClick={deviceIsSelected ? undefined : () => this.changeTab('DEVICES')}
            >
              {`${data.selectedDeviceIds.length} ${strings.devices}`}
            </a>

            {deviceIsSelected && (
              <a
                href="#"
                className={`sub-action ${currentTab === 'EDIT_DEVICE' ? 'active' : undefined}`}
                onClick={() => this.changeTab('EDIT_DEVICE')}
              >
                {strings.edit}
              </a>
            )}

            <a
              href="#"
              className={`sub-action ${currentTab === 'ADD_DEVICE' ? 'active' : undefined}`}
              onClick={() => this.changeTab('ADD_DEVICE')}
            >
              {strings.add}
            </a>
          </li>

          <li
            className={`sidebar-dropdown-container ${
              currentTab !== 'FORMS' ? 'active' : undefined
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
    const { currentTab, selectedDeviceId } = this.state;

    return (
      <div className="assignment-body">
        {this.renderSidebar()}

        <div className="assignment-main">
          {currentTab === 'FORMS' && <FormSection changeTab={this.changeTab} />}
          {currentTab === 'ADD_DEVICE' && <AddDevices changeTab={this.changeTab} />}
          {currentTab === 'EDIT_DEVICE' && <EditDevices changeTab={this.changeTab} />}
          {currentTab === 'DEVICES' && <DevicesSection changeTab={this.changeTab} />}
          {currentTab === 'ASSIGN_DATAPOINTS' && (
            <AssignDatapoints changeTab={this.changeTab} selectedDeviceId={selectedDeviceId} />
          )}
          {currentTab === 'EDIT_DATAPOINTS' && (
            <EditDatapoints changeTab={this.changeTab} selectedDeviceId={selectedDeviceId} />
          )}
        </div>
      </div>
    );
  }
}

AssignmentMain.contextType = AssignmentsContext;
