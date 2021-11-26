import React, { useContext } from 'react';
import SurveysContext from './surveys-context';

export default function Folders() {
  const { surveyGroups, strings, toggleEditFolderName } = useContext(SurveysContext);

  return surveyGroups.map(surveyGroup => (
    <li
      key={surveyGroup.keyId}
      className={`aSurvey aFolder ${surveyGroup.surveyList === null && 'folderEmpty'}`}
    >
      {surveyGroup.isEdit ? (
        <a
          onClick={() => toggleEditFolderName(surveyGroup.keyId)}
          onKeyDown={() => toggleEditFolderName(surveyGroup.keyId)}
          // {{action "toggleEditFolderName" sg target="this"}}
          className="editingFolderName"
        >
          {strings.editFolderName}
        </a>
      ) : (
        <a
          onClick={() => toggleEditFolderName(surveyGroup.keyId)}
          onKeyDown={() => toggleEditFolderName(surveyGroup.keyId)}
          //  {{action "toggleEditFolderName" sg target="this"}}
          className="editFolderName"
        >
          {strings.editFolderName}
        </a>
      )}

      {/* {{view FLOW.FolderEditView valueBinding="sg.code" contentBinding="sg"}}
              {{else}}
                  {{#if view.showSurveyEditButton}} */}

      {/* {{/if}} */}
      {surveyGroup.isEdit ? (
        <input type="text" defaultValue={surveyGroup.code} />
      ) : (
        <a>
          {/* {{action "selectProject" sg target="FLOW.projectControl"}} */}
          <h2>{surveyGroup.code}</h2>
        </a>
      )}

      <nav>
        <ul>
          {/* {{#unless view.hideFolderSurveyDeleteButton}} */}

          {surveyGroup.surveyList === null && (
            <li className="deleteSurvey">
              <a>
                {/* {{action "deleteProject" sg target="FLOW.projectControl"}} */}
                {strings.delete}
              </a>
            </li>
          )}

          <li className="moveSurvey">
            <a>{strings.move}</a>
            {/* {action "beginMoveProject" sg target="FLOW.projectControl"}} */}
          </li>
          {/* {{/unless}} */}
          {/* {{#if view.showSurveyMoveButton}} */}

          {/* {{/if}} */}
        </ul>
      </nav>
    </li>
  ));
}
