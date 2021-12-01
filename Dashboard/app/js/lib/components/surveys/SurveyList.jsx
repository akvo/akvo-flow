/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import PropTypes from 'prop-types';

export default function SurveyLists({
  strings,
  surveyGroup,
  projectToMoveId,
  formatDate,
  classNames,
  moveProject,
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
      <a>
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
            <a>{strings.edit}</a>
          </li>

          <li className="moveSurvey">
            <a
              onClick={() => !projectToMoveId && moveProject(surveyGroup.keyId)}
              onKeyDown={() => !projectToMoveId && moveProject(surveyGroup.keyId)}
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
            <a>{strings.copy}</a>
          </li>
        </ul>
      </nav>
    </li>
  );
}

SurveyLists.propTypes = {
  strings: PropTypes.object.isRequired,
  surveyGroup: PropTypes.object,
  projectToMoveId: PropTypes.number,
  formatDate: PropTypes.func,
  classNames: PropTypes.func,
  moveProject: PropTypes.func,
};

SurveyLists.defaultProps = {
  surveyGroup: null,
  projectToMoveId: null,
  formatDate: () => null,
  classNames: () => null,
  moveProject: () => null,
};
