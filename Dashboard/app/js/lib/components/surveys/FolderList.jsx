import React, { useContext } from 'react';
import Folder from './Folder';
import SurveyListItem from './SurveyListItem';
import SurveysContext from './surveys-context';

export default function FolderList() {
  const {
    strings,
    currentFolder,

    // Functions
    formatDate,
    isProjectFolderEmpty,
    isProjectFolder,
    listItemClassProperty,
    listClassProperty,
    hideFolderSurveyDeleteButton,

    // Actions
    toggleEditFolderName,
    editFolderName,
    saveFolderName,
    selectProject,
    beginMoveProject,
    beginCopyProject,
    deleteSurveyGroup,
  } = useContext(SurveysContext);

  return (
    <section id="allSurvey" className="surveysList">
      <ul className={listClassProperty()}>
        {currentFolder.map(surveyGroup => {
          return isProjectFolder(surveyGroup) ? (
            <Folder
              key={surveyGroup.keyId}
              strings={strings}
              surveyGroup={surveyGroup}
              listItemClassProperty={listItemClassProperty}
              isProjectFolderEmpty={isProjectFolderEmpty}
              toggleEditFolderName={toggleEditFolderName}
              editFolderName={editFolderName}
              saveFolderName={saveFolderName}
              selectProject={selectProject}
              beginMoveProject={beginMoveProject}
              deleteSurveyGroup={deleteSurveyGroup}
              hideFolderSurveyDeleteButton={hideFolderSurveyDeleteButton}
            />
          ) : (
            <SurveyListItem
              key={surveyGroup.keyId}
              strings={strings}
              surveyGroup={surveyGroup}
              listItemClassProperty={listItemClassProperty}
              formatDate={formatDate}
              selectProject={selectProject}
              beginMoveProject={beginMoveProject}
              beginCopyProject={beginCopyProject}
              deleteSurveyGroup={deleteSurveyGroup}
              hideFolderSurveyDeleteButton={hideFolderSurveyDeleteButton}
            />
          );
        })}
      </ul>
    </section>
  );
}
