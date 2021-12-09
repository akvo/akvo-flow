/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import PropTypes from 'prop-types';

export default function Folder({
  strings,
  surveyGroup,

  // Functions
  classProperty,
  displayContentFunctions,

  // Actions
  actions,
  toggleEditFolderName,
  editFolderName,
  saveFolderName,
}) {
  return (
    <li key={surveyGroup.keyId} className={classProperty.listItem(surveyGroup)}>
      {/* Buttons to toggle */}
      {surveyGroup.isEdit ? (
        <a
          onClick={() => saveFolderName(surveyGroup.keyId)}
          onKeyDown={() => saveFolderName(surveyGroup.keyId)}
          className="editingFolderName"
        >
          {strings.editFolderName}
        </a>
      ) : (
        displayContentFunctions.showSurveyEditButton && (
          <a
            onClick={() => toggleEditFolderName(surveyGroup.keyId)}
            onKeyDown={() => toggleEditFolderName(surveyGroup.keyId)}
            className="editFolderName"
          >
            {strings.editFolderName}
          </a>
        )
      )}

      {/* Show input when edit button is clicked */}
      {surveyGroup.isEdit ? (
        <input
          type="text"
          defaultValue={surveyGroup.code}
          onChange={e => editFolderName(e.target.value)}
        />
      ) : (
        <a
          onClick={() => actions.selectProject(surveyGroup.keyId)}
          onKeyDown={() => actions.selectProject(surveyGroup.keyId)}
        >
          <h2>{surveyGroup.code}</h2>
        </a>
      )}

      <nav>
        <ul>
          {!displayContentFunctions.hideFolderSurveyDeleteButton(surveyGroup) && (
            <li className="deleteSurvey">
              <a
                onClick={() => actions.deleteSurveyGroup(surveyGroup.keyId)}
                onKeyDown={() => actions.deleteSurveyGroup(surveyGroup.keyId)}
              >
                {strings.delete}
              </a>
            </li>
          )}
          {displayContentFunctions.showSurveyMoveButton(surveyGroup) &&
            !classProperty.listItem(surveyGroup).includes('newlyCreated') && (
              <li className="moveSurvey">
                <a
                  onClick={() => actions.beginMoveProject(surveyGroup.keyId)}
                  onKeyDown={() => actions.beginMoveProject(surveyGroup.keyId)}
                >
                  {strings.move}
                </a>
              </li>
            )}
        </ul>
      </nav>
    </li>
  );
}

Folder.propTypes = {
  strings: PropTypes.object.isRequired,
  surveyGroup: PropTypes.object.isRequired,

  // Functions
  classProperty: PropTypes.object,
  displayContentFunctions: PropTypes.object,

  // Actions
  actions: PropTypes.object,
  saveFolderName: PropTypes.func,
  toggleEditFolderName: PropTypes.func,
  editFolderName: PropTypes.func,
};

Folder.defaultProps = {
  displayContentFunctions: null,
  classProperty: null,
  // Actions
  actions: null,
  saveFolderName: () => null,
  toggleEditFolderName: () => null,
  editFolderName: () => null,
};
