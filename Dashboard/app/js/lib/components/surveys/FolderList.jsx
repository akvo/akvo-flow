import React, { useContext } from 'react';
import Folder from './Folder';
import SurveyList from './SurveyList';
import SurveysContext from './surveys-context';

export default function FolderList() {
  const {
    strings,
    surveyGroups,
    currentLevel,
    projectToMoveId,

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
    moveProject,
  } = useContext(SurveysContext);

  return sortAscending(surveyGroups)
    .filter(surveyGroup => surveyGroup.parentId === currentLevel)
    .map(surveyGroup => {
      return isProjectFolder(surveyGroup) ? (
        <Folder
          key={surveyGroup.keyId}
          strings={strings}
          surveyGroups={surveyGroups}
          surveyGroup={surveyGroup}
          projectToMoveId={projectToMoveId}
          classNames={classNames}
          isProjectFolderEmpty={isProjectFolderEmpty}
          toggleEditFolderName={toggleEditFolderName}
          editFolderName={editFolderName}
          saveFolderName={saveFolderName}
          selectProject={selectProject}
          moveProject={moveProject}
        />
      ) : (
        <SurveyList
          key={surveyGroup.keyId}
          strings={strings}
          surveyGroup={surveyGroup}
          projectToMoveId={projectToMoveId}
          classNames={classNames}
          formatDate={formatDate}
          moveProject={moveProject}
        />
      );
    });
}
