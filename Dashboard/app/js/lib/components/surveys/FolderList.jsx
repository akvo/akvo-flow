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
    language,
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
              // Functions
              isProjectFolderEmpty={isProjectFolderEmpty}
              listItemClassProperty={listItemClassProperty}
              hideFolderSurveyDeleteButton={hideFolderSurveyDeleteButton}
              // Actions
              toggleEditFolderName={toggleEditFolderName}
              editFolderName={editFolderName}
              saveFolderName={saveFolderName}
              selectProject={selectProject}
              beginMoveProject={beginMoveProject}
              deleteSurveyGroup={deleteSurveyGroup}
            />
          ) : (
            <SurveyListItem
              key={surveyGroup.keyId}
              strings={strings}
              surveyGroup={surveyGroup}
              // Function
              formatDate={formatDate}
              language={language}
              listItemClassProperty={listItemClassProperty}
              hideFolderSurveyDeleteButton={hideFolderSurveyDeleteButton}
              // Actions
              selectProject={selectProject}
              beginMoveProject={beginMoveProject}
              beginCopyProject={beginCopyProject}
              deleteSurveyGroup={deleteSurveyGroup}
            />
          );
        })}
      </ul>
    </section>
  );
}
