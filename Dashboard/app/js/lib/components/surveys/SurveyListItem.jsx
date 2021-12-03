/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import PropTypes from 'prop-types';

export default function SurveyListItem({
  strings,
  surveyGroup,

  // Functions
  formatDate,
  classNames,

  // Actions
  // selectProject,
  beginMoveProject,
  beginCopyProject,
  setCurrentSurvey,
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

  const hasSurvey = Boolean(surveyGroup && surveyGroup.surveyList && surveyGroup.surveyList.length);

  return (
    <li key={surveyGroup.keyId} className={`aSurvey ${classNames(surveyGroup)}`}>
      <a
        onClick={() => hasSurvey && setCurrentSurvey(surveyGroup.surveyList[0])}
        onKeyDown={() => hasSurvey && setCurrentSurvey(surveyGroup.surveyList[0])}
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
              onClick={() => hasSurvey && setCurrentSurvey(surveyGroup.surveyList[0])}
              onKeyDown={() => hasSurvey && setCurrentSurvey(surveyGroup.surveyList[0])}
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

          {surveyGroup.surveyList === null && (
            <li className="deleteSurvey">
              <a>{strings.delete}</a>
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
  surveyGroupId: PropTypes.number,
  formatDate: PropTypes.func,
  classNames: PropTypes.func,
  selectProject: PropTypes.func,
  beginMoveProject: PropTypes.func,
  beginCopyProject: PropTypes.func,
  setCurrentSurvey: PropTypes.func,
};

SurveyListItem.defaultProps = {
  surveyGroup: null,
  surveyGroupId: null,
  formatDate: () => null,
  classNames: () => null,
  selectProject: () => null,
  beginMoveProject: () => null,
  beginCopyProject: () => null,
  setCurrentSurvey: () => null,
};
