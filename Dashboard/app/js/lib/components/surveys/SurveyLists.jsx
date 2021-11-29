import React, { useContext } from 'react';
import SurveysContext from './surveys-context';

export default function SurveyLists() {
  const { surveysInFolder, surveyGroups, strings, formatDate } = useContext(SurveysContext);

  return surveysInFolder.map(survey => {
    const getSurveyGroup = surveyGroups.find(
      surveyGroup => surveyGroup.keyId === survey.surveyGroupId
    );

    const language = () => {
      if (survey.defaultLanguageCode === 'en') {
        return 'English';
      }
      if (survey.defaultLanguageCode === 'es') {
        return 'Español';
      }
      if (survey.defaultLanguageCode === 'fr') {
        return 'Français';
      }
    };

    return (
      <li key={survey.keyId} className="aSurvey folderEmpty">
        <a>
          {/* {{action "selectProject" sg target="FLOW.projectControl"}} */}
          <h2>{getSurveyGroup.code}</h2>
        </a>
        <ul className="surveyInfo floats-in">
          <li className="dateCreated">
            <span>{strings.created}</span>
            <p>{formatDate(survey.createdDateTime)}</p>
          </li>
          <li className="responseNumber">
            <span>{strings.responses}</span>
          </li>
          <li className="dateModified">
            <span>{strings.modified}</span>
            <p>{formatDate(survey.lastUpdateDateTime)}</p>
          </li>
          <li className="surveyLanguage">
            <span>{strings.language}</span>
            <p>{language()}</p>
          </li>
        </ul>
        <nav>
          <ul>
            {/* {{#if view.showSurveyEditButton}} */}
            <li className="editSurvey">
              <a>{strings.edit}</a>
              {/* {{action "selectProject" sg target="FLOW.projectControl"}} */}
            </li>
            {/* {{/if}} */}
            {/* {{#if view.showSurveyMoveButton}} */}
            <li className="moveSurvey">
              <a>{strings.move}</a>
              {/* {{action "beginMoveProject" sg target="FLOW.projectControl"}} */}
            </li>
            {/* {{/if}} */}
            {/* {{#unless view.hideFolderSurveyDeleteButton}} */}
            <li className="deleteSurvey">
              <a>{strings.delete}</a>
              {/* {{action "deleteProject" sg target="FLOW.projectControl"}} */}
            </li>
            {/* {{/unless}} */}
            {/* {{#if view.showSurveyCopyButton}} */}
            <li
              className="copySurvey"
              // {{action "beginCopyProject" sg target="FLOW.projectControl"}}
            >
              <a>{strings.copy}</a>
            </li>
            {/* {{/if}} */}
          </ul>
        </nav>
      </li>
    );
  });
}
