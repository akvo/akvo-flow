import React from 'react';
import FormSection from './FormSection';
import DevicesSection from './DevicesSection';

export default class Sidebar extends React.Component {
  state = {
    currentTab: 'FORMS',
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

            <li className={this.state.currentTab === 'DEVICES' ? 'active' : ''}>
              <button type="button" onClick={() => this.changeTab('DEVICES')}>
                Devices
              </button>

              <button className="sub-action" type="button" onClick={() => null}>
                Add
              </button>
            </li>
          </ul>
        </div>

        <div className="assignment-main">
          {this.state.currentTab === 'FORMS' && <FormSection />}

          {this.state.currentTab === 'DEVICES' && <DevicesSection />}
        </div>
      </React.Fragment>
    );
  }
}
