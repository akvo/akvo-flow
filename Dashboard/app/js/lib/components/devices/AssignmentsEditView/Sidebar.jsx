import React from 'react';
import FormSection from './screens/FormSection';
import AddDevices from './screens/AddDevices';
import EditDevices from './screens/EditDevices';
import DevicesSection from './DevicesSection';
import SidebarDropdown from './__partials/SidebarDropdown';

export default class Sidebar extends React.Component {
  state = {
    currentTab: 'EDIT_DEVICE',
  };

  changeTab = tab => {
    this.setState({ currentTab: tab });
  };

  render() {
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

            <li className="sidebar-dropdown-container active">
              <SidebarDropdown />
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
