/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import MainBody from './MainBody';

import AssignmentContext from './assignment-context';
import './styles.scss';

export default class AssignmentsEdit extends React.Component {
  state = {
    data: {
      assignmentName: this.props.inputValues.assignmentName,
      startDate: this.props.inputValues.startDate,
      endDate: this.props.inputValues.toDate,
    },
  };

  // event handlers
  onChangeState = ({ target }) => {
    this.setState(state => ({
      data: { ...state.data, [target.id]: target.value },
    }));
  };

  onChangeDate = (date, id) => {
    this.setState(state => ({
      data: {
        ...state.data,
        [id]: date,
      },
    }));
  };

  onSubmit = () => {
    const { assignmentName, startDate, endDate } = this.state.data;

    this.props.actions.onSubmit({
      assignmentName,
      startDate,
      endDate,
    });
  };

  // validation
  canSubmitAssignment = () => {
    const { assignmentName, startDate, endDate } = this.state.data;
    const { selectedDeviceIds, numberOfForms } = this.props.data;

    // validate assignment name
    if (!assignmentName || assignmentName == '') {
      return false;
    }

    if (assignmentName.length > 100) {
      return false;
    }

    // validate dates [start date]
    if (!startDate) {
      return false;
    }

    // validate date [expire date]
    if (!endDate) {
      return false;
    }

    if (!selectedDeviceIds.length) {
      return false;
    }

    if (!numberOfForms) {
      return false;
    }

    return true;
  };

  // renders
  renderTopBar() {
    const { strings, actions } = this.props;
    const canSubmitAssignment = this.canSubmitAssignment();
    const submitBtnProps = {
      onClick: this.onSubmit,
      className: `${canSubmitAssignment ? '' : 'disabled'} standardBtn`,
      disabled: !canSubmitAssignment,
    };

    return (
      <div className="assignment-topbar">
        <div className="assignment-name">
          <button type="button" className="go-back" onClick={actions.cancelEditSurveyAssignment}>
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
          </h3>
        </div>

        <button type="button" {...submitBtnProps}>
          {strings.saveAssignment}
        </button>
      </div>
    );
  }

  render() {
    const contextData = {
      strings: this.props.strings,
      actions: {
        ...this.props.actions,
        onInputChange: this.onChangeState,
        onDateChange: this.onChangeDate,
      },
      data: this.props.data,
      inputValues: this.state.data,
    };

    return (
      <div className="assignments-edit">
        {/* topbar */}
        {this.renderTopBar()}

        <AssignmentContext.Provider value={contextData}>
          <MainBody />
        </AssignmentContext.Provider>
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
