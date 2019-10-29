/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import FolderSurveySelectorView from 'akvo-flow/components/selectors/FolderSurveySelector';
import FormSelectorView from 'akvo-flow/components/selectors/FormSelector';
import DeviceGroupSelectorView from 'akvo-flow/components/selectors/DeviceSelector';

import './styles.scss';

export default class AssignmentsEdit extends React.Component {
  state = {
    assignmentName: this.props.inputValues.assignmentName,
    startDate: this.props.inputValues.startDate,
    endDate: this.props.inputValues.toDate,
  };

  // event handlers
  onChangeState = e => {
    this.setState({ [e.target.id]: e.target.value });
  };

  onSubmit = () => {
    const { assignmentName, startDate, endDate } = this.state;

    this.props.actions.onSubmit({ assignmentName, startDate, endDate });
  };

  // helpers
  formatMomentDate = date => moment(date, 'YYYY/MM/DD').format('YYYY-MM-DD');

  render() {
    const { strings, actions, data } = this.props;

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
                placeholder="Untitled Assignment"
                value={this.state.assignmentName}
                onChange={this.onChangeState}
              />
              {/* <span className="infoText">0 datapoints / 20k assigned</span> */}
            </h3>
          </div>

          <button onClick={this.onSubmit} type="button" className="standardBtn">
            Save
          </button>
        </div>

        <div className="assignment-body">
          <div className="settings">
            <h3>Settings</h3>

            <div className="assignment-date">
              <p className="heading">
                <span className="title">Duration</span>
                <span className="info">1 Day</span>
              </p>

              {/* date picker */}
              <div className="date-picker">
                <div className="startDate">
                  <i className="fa fa-calendar" />
                  <input
                    type="date"
                    id="startDate"
                    value={this.formatMomentDate(this.state.startDate)}
                    onChange={this.onChangeState}
                  />
                </div>
                <span> - </span>
                <div className="startDate">
                  <i className="fa fa-calendar" />
                  <input
                    type="date"
                    id="endDate"
                    min={this.formatMomentDate(this.state.startDate)}
                    value={this.formatMomentDate(this.state.endDate)}
                    onChange={this.onChangeState}
                  />
                </div>
              </div>

              <span className="infoText">
                Data will only be submittable within this timeframe
              </span>
            </div>

            <div className="assignment-survery-folder">
              <p className="heading">
                <span className="title">Survey</span>
              </p>

              <div className="folder-selector">
                <FolderSurveySelectorView
                  initialSurveyGroup={data.initialSurveyGroup}
                  surveyGroups={data.surveyGroups}
                  onSelectSurvey={actions.handleSurveySelect}
                  strings={strings}
                />
              </div>
            </div>

            <div className="assignment-form-selector">
              <p className="heading">
                <span className="title">Forms</span>
                <span className="info">1 Enabled</span>
              </p>

              <div className="form-selector">
                <span className="infoText">
                  Only enabled forms can be submitted
                </span>
                <br />

                {Object.keys(data.forms).length ? (
                  <FormSelectorView
                    forms={data.forms}
                    onCheck={actions.handleFormCheck}
                  />
                ) : (
                  <p className="noForms">
                    No forms available. Please select a survey with forms
                  </p>
                )}
              </div>
            </div>
          </div>

          <div className="devices">
            <h3>Devices</h3>

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
        </div>
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
