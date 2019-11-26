import React from 'react';
import FormSection from './screens/FormSection';
import AddDevices from './screens/AddDevices';
import EditDevices from './screens/EditDevices';
import DevicesSection from './DevicesSection';

export default class AssignmentMain extends React.Component {
  state = {
    currentTab: 'EDIT_DEVICE',
  };

  changeTab = tab => {
    this.setState({ currentTab: tab });
  };

  render() {
    return (
      <div className="assignment-body">
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
      </div>
    );
  }
}
