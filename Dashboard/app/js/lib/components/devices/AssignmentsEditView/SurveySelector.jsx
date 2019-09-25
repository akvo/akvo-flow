/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';


import FolderSurveySelectorView from 'akvo-flow/components/selectors/FolderSurveySelector';
import FormSelectorView from 'akvo-flow/components/selectors/FormSelector';

export default class SurveySelector extends React.Component {
  render() {
    const { strings } = this.props;

    return (
      <div className="formLeftPanel">
        <fieldset id="surveySelect" className="floats-in">
          <h2>
            02.
            {' '}
            {strings.selectSurvey}
            :
          </h2>
          <p className="infoText">{strings.cantFindYourSurvey}</p>

          <div className="SelectLayout">
            <label htmlFor="surveyGroup">
              {strings.selectSurvey}
              :
            </label>

            <FolderSurveySelectorView />
          </div>

          <div className="formSelectorList">
            <label htmlFor="surveys">
              {strings.selectForms}
              :
            </label>

            {<FormSelectorView />}
          </div>
        </fieldset>
      </div>
    );
  }
}

SurveySelector.propTypes = {
  strings: PropTypes.object.isRequired,
  // values: PropTypes.object.isRequired,
  // onChange: PropTypes.func.isRequired,
};
