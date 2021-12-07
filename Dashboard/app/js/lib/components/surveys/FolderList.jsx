import React, { useContext } from 'react';
import Folder from './Folder';
import SurveyListItem from './SurveyListItem';
import SurveysContext from './surveys-context';

export default function FolderList() {
  const {
    strings,
    currentFolder,
    surveyGroupId,

    // Functions
    formatDate,
    isProjectFolderEmpty,
    isProjectFolder,
    listItemClassProperty,
    listClassProperty,
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
              surveyGroupId={surveyGroupId}
              listItemClassProperty={listItemClassProperty}
              isProjectFolderEmpty={isProjectFolderEmpty}
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
              surveyGroupId={surveyGroupId}
              listItemClassProperty={listItemClassProperty}
              formatDate={formatDate}
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
