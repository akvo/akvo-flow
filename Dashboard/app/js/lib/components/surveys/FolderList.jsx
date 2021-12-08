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
    classProperty,
    folderModifierFunction,
    displayContentFunction,

    // Actions
    actions,
    toggleEditFolderName,
    editFolderName,
    saveFolderName,
  } = useContext(SurveysContext);

  return (
    <section id="allSurvey" className="surveysList">
      <ul className={classProperty.list()}>
        {currentFolder.map(surveyGroup => {
          return folderModifierFunction.isProjectFolder(surveyGroup) ? (
            <Folder
              key={surveyGroup.keyId}
              strings={strings}
              surveyGroup={surveyGroup}
              // Functions
              classProperty={classProperty}
              displayContentFunction={displayContentFunction}
              // Actions
              toggleEditFolderName={toggleEditFolderName}
              editFolderName={editFolderName}
              saveFolderName={saveFolderName}
              actions={actions}
            />
          ) : (
            <SurveyListItem
              key={surveyGroup.keyId}
              strings={strings}
              surveyGroup={surveyGroup}
              // Function
              formatDate={formatDate}
              language={language}
              displayContentFunction={displayContentFunction}
              // Actions
              actions={actions}
            />
          );
        })}
      </ul>
    </section>
  );
}
