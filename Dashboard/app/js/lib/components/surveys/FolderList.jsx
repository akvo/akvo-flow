import React, { useContext } from 'react';
import Folder from './Folder';
import SurveyListItem from './SurveyListItem';
import SurveysContext from './surveys-context';

export default function FolderList() {
  const {
    strings,
    surveyGroups,
    currentProjectId,
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
    setCurrentSurvey,
    selectProject,
    beginMoveProject,
    beginCopyProject,
  } = useContext(SurveysContext);

  // Get all projects on each level
  const surveyGroupToDisplay = sortAscending(surveyGroups).filter(
    surveyGroup => surveyGroup.parentId === currentProjectId
  );

  return (
    <section id="allSurvey" className="surveysList">
      <ul className={surveyGroupId && 'actionProcess'}>
        {surveyGroupToDisplay.map(surveyGroup => {
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
            <SurveyListItem
              key={surveyGroup.keyId}
              strings={strings}
              surveyGroup={surveyGroup}
              surveyGroupId={surveyGroupId}
              classNames={classNames}
              formatDate={formatDate}
              selectProject={selectProject}
              setCurrentSurvey={setCurrentSurvey}
              beginMoveProject={beginMoveProject}
              beginCopyProject={beginCopyProject}
            />
          );
        })}
      </ul>
    </section>
  );
}
