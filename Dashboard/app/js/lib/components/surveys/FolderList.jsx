import React, { useContext } from 'react';
import Folder from './Folder';
import SurveyList from './SurveyList';
import SurveysContext from './surveys-context';

export default function FolderList() {
  const {
    strings,
    surveyGroups,
    currentProject,
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
    surveyGroup => surveyGroup.parentId === currentProject
  );

  // selectProject = surveyGroupKeyId => {
  //   const surveyGroups = this.state.surveyGroups.find(
  //     surveyGroup => surveyGroup.keyId === surveyGroupKeyId
  //   );

  //   if (surveyGroups.surveyList !== null) {
  //     const getSurveys = this.state.surveys.filter(survey => {
  //       if (surveyGroups.surveyList.includes(survey.keyId)) {
  //         return true;
  //       }
  //       return false;
  //     });
  //     this.setState({ surveysInFolder: [...getSurveys] });
  //   } else {
  //     this.setState({ surveysInFolder: [] });
  //   }

  //   this.setState({ isFolder: false });
  // };

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
        selectProject={selectProject}
        beginMoveProject={beginMoveProject}
        beginCopyProject={beginCopyProject}
      />
    );
  });
}
