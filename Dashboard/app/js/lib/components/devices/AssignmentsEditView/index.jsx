/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import FolderSurveySelectorView from 'akvo-flow/components/selectors/FolderSurveySelector';
import FormSelectorView from 'akvo-flow/components/selectors/FormSelector';
import DeviceGroupSelectorView from 'akvo-flow/components/selectors/DeviceSelector';

import './styles.scss';

export default class AssignmentsEdit extends React.Component {
  render() {
    const { strings, actions, data } = this.props;

    return (
      <div className="assignments-edit">
        {/* topbar */}
        <div className="assignment-topbar">
          <div className="assignment-name">
            <i className="fa fa-arrow-left" />
            <h3>
              <span>Unnamed assignment</span>
              <span className="infoText">0 datapoints / 20k assigned</span>
            </h3>
          </div>

          <button type="button">Save</button>
        </div>

        <div className="assignment-body">
          <div className="settings">
            <p>Settings</p>

            <div className="assignment-date">
              <p className="heading">
                <span className="title">Duration</span>
                <span className="info">1 Day</span>
              </p>

              {/* date picker */}
              <div className="date-picker">
                <div className="startDate">
                  <i className="fa fa-calendar" />
                  <span>21 Aug 2019</span>
                </div>
                <span> - </span>
                <div className="startDate">
                  <i className="fa fa-calendar" />
                  <span>21 Aug 2019</span>
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
            <p>Devices</p>

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
