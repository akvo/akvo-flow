import React, { useContext } from 'react';
import Folder from './Folder';
import SurveyListItem from './SurveyListItem';
import SurveysContext from './surveys-context';

export default function FolderList() {
  const {
    strings,
    currentFolder,

    // Functions
    classProperty,
    helperFunctions,
    displayContentFunctions,

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
          return helperFunctions.isProjectFolder(surveyGroup) ? (
            <Folder
              key={surveyGroup.keyId}
              strings={strings}
              surveyGroup={surveyGroup}
              // Functions
              classProperty={classProperty}
              displayContentFunctions={displayContentFunctions}
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
              helperFunctions={helperFunctions}
              displayContentFunctions={displayContentFunctions}
              // Actions
              actions={actions}
            />
          );
        })}
      </ul>
    </section>
  );
}
