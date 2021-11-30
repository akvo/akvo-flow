import React, { useContext } from 'react';
import Folder from './Folder';
import SurveyList from './SurveyList';
import SurveysContext from './surveys-context';

export default function FolderList() {
  const {
    surveyGroups,
    strings,

    // Functions
    formatDate,

    // Actions
    toggleEditFolderName,
    editFolderName,
    saveFolderName,
    selectProject,
    sortAscending,
  } = useContext(SurveysContext);

  return sortAscending(surveyGroups).map(surveyGroup => {
    return surveyGroup.projectType === 'PROJECT' ? (
      <SurveyList
        key={surveyGroup.keyId}
        surveyGroup={surveyGroup}
        strings={strings}
        formatDate={formatDate}
      />
    ) : (
      <Folder
        key={surveyGroup.keyId}
        surveyGroup={surveyGroup}
        strings={strings}
        toggleEditFolderName={toggleEditFolderName}
        editFolderName={editFolderName}
        saveFolderName={saveFolderName}
        selectProject={selectProject}
      />
    );
  });
}
