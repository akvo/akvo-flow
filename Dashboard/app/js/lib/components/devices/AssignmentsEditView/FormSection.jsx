/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import FolderSurveySelectorView from 'akvo-flow/components/selectors/FolderSurveySelector';
import FormSelectorView from 'akvo-flow/components/selectors/FormSelector';

export default class FormSection extends React.Component {
  formatMomentDate = date => moment(date, 'YYYY/MM/DD').format('YYYY-MM-DD');

  render() {
    const { strings, actions, data, inputValues, onInputChange } = this.props;

    return (
      <div className="settings">
        <div className="assignment-date">
          <p className="heading">
            <span className="title">{strings.duration}</span>
          </p>

          <div className="date-picker">
            <div className="date">
              <i className="fa fa-calendar" />
              <input
                type="date"
                id="startDate"
                value={this.formatMomentDate(inputValues.startDate)}
                onChange={onInputChange}
              />
            </div>
            <span> - </span>
            <div className="date">
              <i className="fa fa-calendar" />
              <input
                type="date"
                id="endDate"
                min={this.formatMomentDate(inputValues.startDate)}
                value={this.formatMomentDate(inputValues.endDate)}
                onChange={onInputChange}
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

          <div className="form-selector">
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
              <FormSelectorView
                forms={data.forms}
                onCheck={actions.handleFormCheck}
              />
            ) : (
              <p className="noForms">{strings.noForms}</p>
            )}
          </div>
        </div>
      </div>
    );
  }
}

FormSection.propTypes = {
  strings: PropTypes.object.isRequired,
  actions: PropTypes.object.isRequired,
  data: PropTypes.object.isRequired,
  inputValues: PropTypes.object.isRequired,
  onInputChange: PropTypes.func.isRequired,
};
