import React, { useContext } from 'react';
import Folder from './Folder';
import SurveyList from './SurveyList';
import SurveysContext from './surveys-context';

export default function FolderList() {
  const {
    strings,
    surveyGroups,
    currentLevel,
    surveyGroupId,

    // Functions
    formatDate,
    isProjectFolderEmpty,
    isProjectFolder,
    sortAscending,
    classNames,

    // Actions
    toggleEditFolderName,
    editFolderName,
    saveFolderName,
    selectProject,
    beginMoveProject,
    beginCopyProject,
  } = useContext(SurveysContext);

  // Get all projects on each level
  const surveyGroupToDisplay = sortAscending(surveyGroups).filter(
    surveyGroup => surveyGroup.parentId === currentLevel
  );

  return surveyGroupToDisplay.map(surveyGroup => {
    return isProjectFolder(surveyGroup) ? (
      <Folder
        key={surveyGroup.keyId}
        strings={strings}
        surveyGroups={surveyGroups}
        surveyGroup={surveyGroup}
        surveyGroupId={surveyGroupId}
        classNames={classNames}
        isProjectFolderEmpty={isProjectFolderEmpty}
        toggleEditFolderName={toggleEditFolderName}
        editFolderName={editFolderName}
        saveFolderName={saveFolderName}
        selectProject={selectProject}
        beginMoveProject={beginMoveProject}
      />
    ) : (
      <SurveyList
        key={surveyGroup.keyId}
        strings={strings}
        surveyGroup={surveyGroup}
        surveyGroupId={surveyGroupId}
        classNames={classNames}
        formatDate={formatDate}
        beginMoveProject={beginMoveProject}
        beginCopyProject={beginCopyProject}
      />
    );
  });
}
