/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import PropTypes from 'prop-types';

export default function Folder({
  surveyGroup,
  strings,

  // Functions
  isProjectFolderEmpty,

  // Actions
  toggleEditFolderName,
  editFolderName,
  saveFolderName,
  selectProject,
}) {
  return (
    <li
      key={surveyGroup.keyId}
      className={`aSurvey aFolder ${isProjectFolderEmpty(surveyGroup) && 'folderEmpty'}`}
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
              <a>{strings.delete}</a>
            </li>
          )}

          <li className="moveSurvey">
            <a>{strings.move}</a>
          </li>
        </ul>
      </nav>
    </li>
  );
}

Folder.propTypes = {
  strings: PropTypes.object.isRequired,
  surveyGroup: PropTypes.object,
  toggleEditFolderName: PropTypes.func,
  editFolderName: PropTypes.func,
  saveFolderName: PropTypes.func,
  selectProject: PropTypes.func,
  isProjectFolderEmpty: PropTypes.func,
};

Folder.defaultProps = {
  surveyGroup: null,
  toggleEditFolderName: () => null,
  editFolderName: () => null,
  saveFolderName: () => null,
  selectProject: () => null,
  isProjectFolderEmpty: () => null,
};
