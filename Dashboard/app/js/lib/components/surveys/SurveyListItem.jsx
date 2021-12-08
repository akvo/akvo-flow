/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import PropTypes from 'prop-types';

export default function SurveyListItem({
  strings,
  surveyGroup,
  actions,
  formatDate,
  language,
  displayContentFunction,
}) {
  return (
    <li key={surveyGroup.keyId} className="aSurvey">
      <a
        onClick={() => actions.selectProject(surveyGroup.keyId)}
        onKeyDown={() => actions.selectProject(surveyGroup.keyId)}
      >
        <h2>{surveyGroup.code}</h2>
      </a>
      <ul className="surveyInfo floats-in">
        <li className="dateCreated">
          <span>{strings.created}</span>
          <p>{formatDate(surveyGroup.createdDateTime)}</p>
        </li>
        <li className="responseNumber">
          <span>{strings.responses}</span>
        </li>
        <li className="dateModified">
          <span>{strings.modified}</span>
          <p>{formatDate(surveyGroup.lastUpdateDateTime)}</p>
        </li>
        <li className="surveyLanguage">
          <span>{strings.language}</span>
          <p>{language(surveyGroup)}</p>
        </li>
      </ul>
      <nav>
        <ul>
          {displayContentFunction.showSurveyEditButton && (
            <li className="editSurvey">
              <a
                onClick={() => actions.selectProject(surveyGroup.keyId)}
                onKeyDown={() => actions.selectProject(surveyGroup.keyId)}
              >
                {strings.edit}
              </a>
            </li>
          )}
          {displayContentFunction.showSurveyMoveButton && (
            <li className="moveSurvey">
              <a
                onClick={() => actions.beginMoveProject(surveyGroup.keyId)}
                onKeyDown={() => actions.beginMoveProject(surveyGroup.keyId)}
              >
                {strings.move}
              </a>
            </li>
          )}

          {!displayContentFunction.hideFolderSurveyDeleteButton(surveyGroup) && (
            <li className="deleteSurvey">
              <a
                onClick={() => actions.deleteSurveyGroup(surveyGroup.keyId)}
                onKeyDown={() => actions.deleteSurveyGroup(surveyGroup.keyId)}
              >
                {strings.delete}
              </a>
            </li>
          )}
          {displayContentFunction.showSurveyCopyButton && (
            <li className="copySurvey">
              <a
                onClick={() => actions.beginCopyProject(surveyGroup.keyId)}
                onKeyDown={() => actions.beginCopyProject(surveyGroup.keyId)}
              >
                {strings.copy}
              </a>
            </li>
          )}
        </ul>
      </nav>
    </li>
  );
}

SurveyListItem.propTypes = {
  strings: PropTypes.object.isRequired,
  surveyGroup: PropTypes.object,
  actions: PropTypes.object,
  formatDate: PropTypes.func,
  language: PropTypes.func,
  displayContentFunction: PropTypes.object,
};

SurveyListItem.defaultProps = {
  surveyGroup: null,
  actions: null,
  formatDate: () => null,
  language: () => null,
  displayContentFunction: null,
};
