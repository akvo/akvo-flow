/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import PropTypes from 'prop-types';

export default function SurveyLists({
  strings,
  surveyGroup,
  surveyGroupId,

  // Functions
  formatDate,
  classNames,

  // Actions
  selectProject,
  beginMoveProject,
  beginCopyProject,
}) {
  const language = () => {
    let languageFullWord = '';
    if (surveyGroup.defaultLanguageCode === 'en') {
      languageFullWord = 'English';
    }
    if (surveyGroup.defaultLanguageCode === 'es') {
      languageFullWord = 'Español';
    }
    if (surveyGroup.defaultLanguageCode === 'fr') {
      languageFullWord = 'Français';
    }
    return languageFullWord;
  };

  return (
    <li key={surveyGroup.keyId} className={`aSurvey ${classNames(surveyGroup)}`}>
      <a
        onClick={() => !surveyGroupId && selectProject()}
        onKeyDown={() => !surveyGroupId && selectProject()}
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
          <p>{language()}</p>
        </li>
      </ul>
      <nav>
        <ul>
          <li className="editSurvey">
            <a
              onClick={() => !surveyGroupId && selectProject()}
              onKeyDown={() => !surveyGroupId && selectProject()}
            >
              {strings.edit}
            </a>
          </li>

          <li className="moveSurvey">
            <a
              onClick={() => !surveyGroupId && beginMoveProject(surveyGroup.keyId)}
              onKeyDown={() => !surveyGroupId && beginMoveProject(surveyGroup.keyId)}
            >
              {strings.move}
            </a>
          </li>

          {surveyGroup.surveyList === null && (
            <li className="deleteSurvey">
              <a>{strings.delete}</a>
            </li>
          )}

          <li className="copySurvey">
            <a
              onClick={() => !surveyGroupId && beginCopyProject(surveyGroup.keyId)}
              onKeyDown={() => !surveyGroupId && beginCopyProject(surveyGroup.keyId)}
            >
              {strings.copy}
            </a>
          </li>
        </ul>
      </nav>
    </li>
  );
}

SurveyLists.propTypes = {
  strings: PropTypes.object.isRequired,
  surveyGroup: PropTypes.object,
  surveyGroupId: PropTypes.number,
  formatDate: PropTypes.func,
  classNames: PropTypes.func,
  selectProject: PropTypes.func,
  beginMoveProject: PropTypes.func,
  beginCopyProject: PropTypes.func,
};

SurveyLists.defaultProps = {
  surveyGroup: null,
  surveyGroupId: null,
  formatDate: () => null,
  classNames: () => null,
  selectProject: () => null,
  beginMoveProject: () => null,
  beginCopyProject: () => null,
};
