/* eslint-disable import/no-unresolved */
import React from 'react';
import FolderSurveySelectorView from 'akvo-flow/components/selectors/FolderSurveySelector';
import FormSelectorView from 'akvo-flow/components/selectors/FormSelector';
import Datepicker from 'akvo-flow/components/reusable/DatePicker';

import AssignmentsContext from '../assignment-context';

export default class FormSection extends React.Component {
  render() {
    const { strings, actions, data, inputValues } = this.context;

    return (
      <div className="settings">
        <div className="assignment-date">
          <p className="heading">
            <span className="title">{strings.duration}</span>
          </p>

          <div className="date-picker">
            <div className="date">
              <i className="fa fa-calendar" />
              <Datepicker
                id="startDate"
                value={inputValues.startDate}
                onChange={actions.onDateChange}
              />
            </div>
            <span> - </span>
            <div className="date">
              <i className="fa fa-calendar" />
              <Datepicker
                id="endDate"
                value={inputValues.endDate}
                onChange={actions.onDateChange}
              />
            </div>
          </div>
        </div>

        <div className="assignment-form-selector">
          <p className="heading">
            <span className="title">{strings.forms}</span>
            <span className="info">
              {data.numberOfForms} {strings.enabled}
            </span>
          </p>

          <div>
            <br />

            <div className="folder-selector">
              <FolderSurveySelectorView
                initialSurveyGroup={data.initialSurveyGroup}
                surveyGroups={data.surveyGroups}
                onSelectSurvey={actions.handleSurveySelect}
                strings={strings}
              />
            </div>
            <br />

            {Object.keys(data.forms).length ? (
              <FormSelectorView forms={data.forms} onCheck={actions.handleFormCheck} />
            ) : (
              <p className="noForms">{strings.noForms}</p>
            )}
          </div>
        </div>
      </div>
    );
  }
}

FormSection.contextType = AssignmentsContext;
