/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import PropTypes from 'prop-types';

export default function Folder({
  strings,
  surveyGroup,
  surveyGroupId,

  // Functions
  listItemClassProperty,

  // Actions
  toggleEditFolderName,
  editFolderName,
  saveFolderName,
  selectProject,
  beginMoveProject,
  deleteSurveyGroup,
}) {
  return (
    <li key={surveyGroup.keyId} className={listItemClassProperty(surveyGroup)}>
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

      {surveyGroup.isEdit ? (
        <input
          type="text"
          defaultValue={surveyGroup.code}
          onChange={e => editFolderName(surveyGroup.keyId, e.target.value)}
        />
      ) : (
        <a
          onClick={() => selectProject(surveyGroup.keyId)}
          onKeyDown={() => selectProject(surveyGroup.keyId)}
        >
          <h2>{surveyGroup.code}</h2>
        </a>
      )}

      <nav>
        <ul>
          {surveyGroup.surveyList === null && (
            <li className="deleteSurvey">
              <a
                onClick={() => deleteSurveyGroup(surveyGroup.keyId)}
                onKeyDown={() => deleteSurveyGroup(surveyGroup.keyId)}
              >
                {strings.delete}
              </a>
            </li>
          )}

          <li className="moveSurvey">
            <a
              onClick={() => !surveyGroupId && beginMoveProject(surveyGroup.keyId)}
              onKeyDown={() => !surveyGroupId && beginMoveProject(surveyGroup.keyId)}
            >
              {strings.move}
            </a>
          </li>
        </ul>
      </nav>
    </li>
  );
}

Folder.propTypes = {
  strings: PropTypes.object.isRequired,
  surveyGroup: PropTypes.object,
  surveyGroupId: PropTypes.number,
  listItemClassProperty: PropTypes.func,
  toggleEditFolderName: PropTypes.func,
  editFolderName: PropTypes.func,
  saveFolderName: PropTypes.func,
  selectProject: PropTypes.func,
  isProjectFolderEmpty: PropTypes.func,
  beginMoveProject: PropTypes.func,
  deleteSurveyGroup: PropTypes.func,
};

Folder.defaultProps = {
  surveyGroup: null,
  surveyGroupId: null,
  listItemClassProperty: () => null,
  toggleEditFolderName: () => null,
  editFolderName: () => null,
  saveFolderName: () => null,
  selectProject: () => null,
  isProjectFolderEmpty: () => null,
  beginMoveProject: () => null,
  deleteSurveyGroup: () => null,
};
