/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';

import FolderSurveySelectorView from 'akvo-flow/components/selectors/FolderSurveySelector';
import FormSelectorView from 'akvo-flow/components/selectors/FormSelector';
import DeviceGroupSelectorView from 'akvo-flow/components/selectors/DeviceSelector';
import AssignmentDetails from './AssignmentDetails';

export default class AssignmentsEditView extends React.Component {
  state = {
    assignmentName: this.props.inputValues.assignmentName,
    startDate: this.props.inputValues.startDate,
    expireDate: this.props.inputValues.toDate,
    nameValidationMsg: '',
    expireDateMsg: '',
    startDateMsg: '',
  };

  // event handlers
  onChangeState = (key, value) => {
    this.setState({ [key]: value });
  };

  onSubmit = () => {
    const { assignmentName, startDate, expireDate } = this.state;

    if (this.validateData()) {
      this.props.actions.onSubmit({ assignmentName, startDate, expireDate });
    }
  };

  // helpers
  validateData = () => {
    const { assignmentName, startDate, expireDate } = this.state;
    let isValid = true;

    // validate assignment name
    if (!assignmentName || assignmentName == '') {
      this.setState({
        nameValidationMsg: Ember.String.loc('_assignment_name_not_set'),
      });
      isValid = false;
    }

    if (assignmentName.length > 100) {
      this.setState({
        nameValidationMsg: Ember.String.loc('_assignment_name_over_100_chars'),
      });
      isValid = false;
    }

    // remove validation message incase it was set
    if (isValid) {
      this.setState({ nameValidationMsg: '' });
    }

    // validate dates === start date
    if (!startDate || !startDate.length) {
      this.setState({ startDateMsg: Ember.String.loc('_date_not_set_text') });
      isValid = false;
    }

    if (isValid) {
      this.setState({ startDateMsg: '' });
    }

    // validate date === expire date
    if (!expireDate || !expireDate.length) {
      this.setState({ expireDateMsg: Ember.String.loc('_date_not_set_text') });
      isValid = false;
    }

    if (isValid) {
      this.setState({ expireDateMsg: '' });
    }

    return isValid;
  };

  formatStateForComponents = () => {
    const assignmentDetailsState = {
      assignmentName: this.state.assignmentName,
      startDate: this.state.startDate,
      expireDate: this.state.expireDate,
    };

    return {
      assignmentDetailsState,
    };
  };

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

  // render
  render() {
    const { strings, actions, data } = this.props;
    const { assignmentDetailsState } = this.formatStateForComponents();
    const selectedDevices = this.getNumberOfSelectedDevices();

    return (
      <div>
        <button
          onKeyPress={actions.cancelEditSurveyAssignment}
          onClick={actions.cancelEditSurveyAssignment}
          className="stepBack"
          id="float-right"
          type="button"
        >
          {strings.backToAssignmentList}
        </button>

        <form>
          <AssignmentDetails
            strings={{
              ...strings,
              nameValidationMsg: this.state.nameValidationMsg,
              expireDateMsg: this.state.expireDateMsg,
              startDateMsg: this.state.startDateMsg,
            }}
            values={assignmentDetailsState}
            onChange={this.onChangeState}
          />

          <div className="fieldSetWrap floats-in">
            <div className="formLeftPanel">
              <fieldset id="surveySelect" className="floats-in">
                <h2>{strings.selectSurvey}:</h2>

                <div className="SelectLayout">
                  <FolderSurveySelectorView
                    initialSurveyGroup={data.initialSurveyGroup}
                    surveyGroups={data.surveyGroups}
                    onSelectSurvey={actions.handleSurveySelect}
                    strings={strings}
                  />
                </div>

                <div className="formSelectorList">
                  <label htmlFor="surveys">{strings.selectForms}:</label>
                  <span className="infoText">{strings.selectFormNote}</span>
                  <br />

                  <FormSelectorView
                    forms={data.forms}
                    onCheck={actions.handleFormCheck}
                  />
                </div>
              </fieldset>
            </div>

            <div className="formRightPanel">
              <fieldset id="surveySelect" className="floats-in">
                <h2>
                  {strings.selectDevices}:{' '}
                  <span className="infoText">
                    {selectedDevices} {strings.selectedDevices}
                  </span>
                </h2>

                <DeviceGroupSelectorView
                  deviceGroupNames={data.deviceGroupNames}
                  deviceGroups={data.deviceGroups}
                  activeDeviceGroups={data.activeDeviceGroups}
                  handleDeviceCheck={actions.handleDeviceCheck}
                  onSelectAll={actions.handleSelectAllDevice}
                />
              </fieldset>
            </div>
          </div>

          <div className="fieldSetWrap makeWhite">
            <div className="formLeftPanel" />
            <div className="formRightPanel">
              {/* Data points list will come here */}
            </div>
          </div>

          <div className="menuConfirm">
            <ul>
              <li>
                <button
                  onClick={this.onSubmit}
                  onKeyPress={this.onSubmit}
                  className="standardBtn"
                  type="button"
                >
                  {strings.saveAssignment}
                </button>
              </li>

              <li>
                <button
                  onClick={actions.cancelEditSurveyAssignment}
                  onKeyPress={actions.cancelEditSurveyAssignment}
                  type="button"
                >
                  {strings.cancel}
                </button>
              </li>
            </ul>
          </div>
        </form>
      </div>
    );
  }
}

AssignmentsEditView.propTypes = {
  strings: PropTypes.object.isRequired,
  actions: PropTypes.object.isRequired,
  inputValues: PropTypes.object.isRequired,
  data: PropTypes.object.isRequired,
};
