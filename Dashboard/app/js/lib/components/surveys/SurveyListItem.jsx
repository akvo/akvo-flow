/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import PropTypes from 'prop-types';

export default function SurveyListItem({
  strings,
  surveyGroup,

  // Functions
  formatDate,
  language,
  hideFolderSurveyDeleteButton,

  // Actions
  selectProject,
  beginMoveProject,
  beginCopyProject,
  deleteSurveyGroup,
}) {
  return (
    <li key={surveyGroup.keyId} className="aSurvey">
      <a
        onClick={() => selectProject(surveyGroup.keyId)}
        onKeyDown={() => selectProject(surveyGroup.keyId)}
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
          <li className="editSurvey">
            <a
              onClick={() => selectProject(surveyGroup.keyId)}
              onKeyDown={() => selectProject(surveyGroup.keyId)}
            >
              {strings.edit}
            </a>
          </li>

          <li className="moveSurvey">
            <a
              onClick={() => beginMoveProject(surveyGroup.keyId)}
              onKeyDown={() => beginMoveProject(surveyGroup.keyId)}
            >
              {strings.move}
            </a>
          </li>

          {!hideFolderSurveyDeleteButton(surveyGroup) && (
            <li className="deleteSurvey">
              <a
                onClick={() => deleteSurveyGroup(surveyGroup.keyId)}
                onKeyDown={() => deleteSurveyGroup(surveyGroup.keyId)}
              >
                {strings.delete}
              </a>
            </li>
          )}

          <li className="copySurvey">
            <a
              onClick={() => beginCopyProject(surveyGroup.keyId)}
              onKeyDown={() => beginCopyProject(surveyGroup.keyId)}
            >
              {strings.copy}
            </a>
          </li>
        </ul>
      </nav>
    </li>
  );
}

SurveyListItem.propTypes = {
  strings: PropTypes.object.isRequired,
  surveyGroup: PropTypes.object,

  // Functions
  formatDate: PropTypes.func,
  language: PropTypes.func,
  listItemClassProperty: PropTypes.func,
  hideFolderSurveyDeleteButton: PropTypes.func,

  // Actions
  selectProject: PropTypes.func,
  beginMoveProject: PropTypes.func,
  beginCopyProject: PropTypes.func,
  deleteSurveyGroup: PropTypes.func,
};

SurveyListItem.defaultProps = {
  surveyGroup: null,

  // Functions
  formatDate: () => null,
  language: () => null,
  listItemClassProperty: () => null,
  hideFolderSurveyDeleteButton: () => null,

  // Actions
  selectProject: () => null,
  beginMoveProject: () => null,
  beginCopyProject: () => null,
  deleteSurveyGroup: () => null,
};
