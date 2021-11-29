/* eslint-disable jsx-a11y/anchor-is-valid */
import React, { useContext } from 'react';
import SurveysContext from './surveys-context';

export default function Folders() {
  const {
    surveyGroups,
    strings,

    // Actions
    toggleEditFolderName,
    editFolderName,
    saveFolderName,
  } = useContext(SurveysContext);

  return surveyGroups.map(surveyGroup => (
    <li
      key={surveyGroup.keyId}
      className={`aSurvey aFolder ${surveyGroup.surveyList === null && 'folderEmpty'}`}
    >
      {surveyGroup.isEdit ? (
        <a
          onClick={() => saveFolderName(surveyGroup.keyId)}
          onKeyDown={() => saveFolderName(surveyGroup.keyId)}
          className="editingFolderName"
        >
          {strings.editFolderName}
        </a>
      ) : (
        <a
          onClick={() => toggleEditFolderName(surveyGroup.keyId)}
          onKeyDown={() => toggleEditFolderName(surveyGroup.keyId)}
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
        <input
          type="text"
          defaultValue={surveyGroup.code}
          onChange={e => editFolderName(surveyGroup.keyId, e.target.value)}
        />
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
