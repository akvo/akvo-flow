import React, { useContext } from 'react';
import Folder from './Folder';
import SurveyList from './SurveyList';
import SurveysContext from './surveys-context';

export default function FolderList() {
  const {
    surveyGroups,
    strings,
    currentLevel,

    // Functions
    formatDate,
    isProjectFolderEmpty,
    isProjectFolder,
    // Actions
    toggleEditFolderName,
    editFolderName,
    saveFolderName,
    selectProject,
    sortAscending,
  } = useContext(SurveysContext);

  return sortAscending(surveyGroups)
    .filter(surveyGroup => surveyGroup.parentId === currentLevel)
    .map(surveyGroup => {
      return isProjectFolder(surveyGroup) ? (
        <Folder
          key={surveyGroup.keyId}
          surveyGroups={surveyGroups}
          surveyGroup={surveyGroup}
          strings={strings}
          isProjectFolderEmpty={isProjectFolderEmpty}
          toggleEditFolderName={toggleEditFolderName}
          editFolderName={editFolderName}
          saveFolderName={saveFolderName}
          selectProject={selectProject}
        />
      ) : (
        <SurveyList
          key={surveyGroup.keyId}
          surveyGroup={surveyGroup}
          strings={strings}
          formatDate={formatDate}
        />
      );
    });
}
