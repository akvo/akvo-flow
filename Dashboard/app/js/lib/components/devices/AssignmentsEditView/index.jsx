/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import FormSection from './FormSection';
// import DeviceGroupSelectorView from 'akvo-flow/components/selectors/DeviceSelector';

import './styles.scss';

export default class AssignmentsEdit extends React.Component {
  state = {
    data: {
      assignmentName: this.props.inputValues.assignmentName,
      startDate: this.props.inputValues.startDate,
      endDate: this.props.inputValues.toDate,
    },
    currentTab: 'FORMS',
  };

  // event handlers
  onChangeState = e => {
    this.setState(state => ({
      data: { [e.target.id]: e.target.value, ...state.data },
    }));
  };

  onChangeTab = tab => {
    this.setState({ currentTab: tab });
  };

  onSubmit = () => {
    const { assignmentName, startDate, endDate } = this.state;

    this.props.actions.onSubmit({
      assignmentName,
      startDate,
      endDate,
    });
  };

  // helpers
  formatMomentDate = date => moment(date, 'YYYY/MM/DD').format('YYYY-MM-DD');

  getNumberOfSelectedDevices = () => {
    let selectedDevices = 0;
    const { deviceGroups } = this.props.data;

    Object.keys(deviceGroups).forEach(dgId => {
      const noOfSelectedDevicesInThisGroup = Object.keys(
        deviceGroups[dgId]
      ).filter(
        deviceId => deviceId != 0 && deviceGroups[dgId][deviceId].checked
      ).length;

      selectedDevices += noOfSelectedDevicesInThisGroup;
    });

    return selectedDevices;
  };

  render() {
    const { strings, actions, data } = this.props;
    // const numberOfDevices = this.getNumberOfSelectedDevices();

    return (
      <div className="assignments-edit">
        {/* topbar */}
        <div className="assignment-topbar">
          <div className="assignment-name">
            <button
              type="button"
              className="go-back"
              onClick={actions.cancelEditSurveyAssignment}
            >
              <i className="fa fa-arrow-left" />
            </button>

            <h3>
              <input
                type="text"
                id="assignmentName"
                placeholder={strings.assignmentNamePlaceholder}
                value={this.state.data.assignmentName}
                onChange={this.onChangeState}
              />
              {/* <span className="infoText">0 datapoints / 20k assigned</span> */}
            </h3>
          </div>

          <button onClick={this.onSubmit} type="button" className="standardBtn">
            {strings.saveAssignment}
          </button>
        </div>

        <div className="assignment-body">
          <div className="assignment-sidebar">
            <ul>
              <li className={this.state.currentTab === 'FORMS' ? 'active' : ''}>
                <a href="/">Forms</a>
              </li>

              <li
                className={this.state.currentTab === 'DEVICES' ? 'active' : ''}
              >
                <a href="/">Devices</a>
              </li>
            </ul>
          </div>

          <div className="assignment-main">
            <FormSection
              actions={actions}
              strings={strings}
              data={data}
              inputValues={this.state.data}
              onInputChange={this.onChangeState}
            />
          </div>
        </div>
        {/* <div className="assignment-body">

          <div className="devices">
            <div className="heading">
              <h3>{strings.devices}</h3>
              <span className="info">
                {numberOfDevices}{' '}
                {numberOfDevices === 1 ? strings.device : strings.devices}{' '}
                {strings.selected}
              </span>
            </div>

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
        </div> */}
      </div>
    );
  }
}

AssignmentsEdit.propTypes = {
  strings: PropTypes.object.isRequired,
  actions: PropTypes.object.isRequired,
  inputValues: PropTypes.object.isRequired,
  data: PropTypes.object.isRequired,
};
