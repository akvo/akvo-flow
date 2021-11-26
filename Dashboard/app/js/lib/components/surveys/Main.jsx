import React from 'react';
import PropTypes from 'prop-types';
import SurveysContext from './surveys-context';
import Folders from './Folders';
import SurveyLists from './SurveyLists';

export default class Main extends React.Component {
  state = {
    surveys: [
      {
        keyId: 6614936830607360,
        name: 'New form',
        code: 'New form',
        version: '1.0',
        description: null,
        status: 'NOT_PUBLISHED',
        questionGroupList: null,
        path: '/Folder with a few large data sets/New survey/New form',
        surveyGroupId: 6051986877186048,
        defaultLanguageCode: 'en',
        requireApproval: false,
        createdDateTime: 1636102031681,
        lastUpdateDateTime: 1636102031681,
        sourceId: null,
        ancestorIds: [0, 153142013, 6051986877186048],
        alias: null,
        translationMap: null,
      },
      {
        keyId: 5550609574920192,
        name: 'New form',
        code: 'New form',
        version: '1.0',
        description: null,
        status: 'NOT_PUBLISHED',
        questionGroupList: null,
        path: "/Folder with a few large data sets/New folder/Daniel's survey copy/New form",
        surveyGroupId: 6535771993407488,
        defaultLanguageCode: 'en',
        requireApproval: false,
        createdDateTime: 1637300972815,
        lastUpdateDateTime: 1637300972815,
        sourceId: null,
        ancestorIds: [0, 153142013, 4583039342477312, 6535771993407488],
        alias: null,
        translationMap: null,
      },
    ],
    surveyGroups: [
      {
        keyId: 4583039342477312,
        description: '',
        name: 'New folder',
        code: 'New folder',
        path: '/Folder with a few large data sets/New folder',
        monitoringGroup: false,
        newLocaleSurveyId: null,
        createdDateTime: 1636780642802,
        lastUpdateDateTime: 1636780642802,
        projectType: 'PROJECT_FOLDER',
        parentId: 153142013,
        defaultLanguageCode: 'en',
        published: false,
        requireDataApproval: false,
        dataApprovalGroupId: null,
        ancestorIds: [0, 153142013],
        surveyList: null,
        template: false,
      },
      {
        keyId: 152342023,
        description: '',
        name: 'BAR-handpump',
        code: 'BAR-handpump',
        path: '/Folder with a few large data sets/BAR-handpump',
        monitoringGroup: true,
        newLocaleSurveyId: 146532016,
        createdDateTime: 1490604443830,
        lastUpdateDateTime: 1490604833184,
        projectType: 'PROJECT',
        parentId: 153142013,
        defaultLanguageCode: 'en',
        published: false,
        requireDataApproval: false,
        dataApprovalGroupId: null,
        ancestorIds: [0, 153142013],
        surveyList: [4574243249455104],
        template: null,
      },
      {
        keyId: 148412306,
        description: '',
        name: 'NR-handpump',
        code: 'NR-handpump',
        path: '/Folder with a few large data sets/NR-handpump',
        monitoringGroup: true,
        newLocaleSurveyId: 145492013,
        createdDateTime: 1490605551743,
        lastUpdateDateTime: 1490605647506,
        projectType: 'PROJECT',
        parentId: 153142013,
        defaultLanguageCode: 'en',
        published: false,
        requireDataApproval: false,
        dataApprovalGroupId: null,
        ancestorIds: [0, 153142013],
        surveyList: [145492013],
        template: null,
      },
      {
        keyId: 6051986877186048,
        description: '',
        name: "Daniel's survey",
        code: "Daniel's survey",
        path: "/Folder with a few large data sets/Daniel's survey",
        monitoringGroup: true,
        newLocaleSurveyId: 6614936830607360,
        createdDateTime: 1636090277753,
        lastUpdateDateTime: 1637736537837,
        projectType: 'PROJECT',
        parentId: 153142013,
        defaultLanguageCode: 'en',
        published: false,
        requireDataApproval: true,
        dataApprovalGroupId: null,
        ancestorIds: [0, 153142013],
        surveyList: [6614936830607360],
        template: true,
      },
      {
        keyId: 6535771993407488,
        description: '',
        name: "Daniel's survey copy",
        code: "Daniel's survey copy",
        path: "/Folder with a few large data sets/New folder/Daniel's survey copy",
        monitoringGroup: true,
        newLocaleSurveyId: 4706184644788224,
        createdDateTime: 1637237380255,
        lastUpdateDateTime: 1637237380837,
        projectType: 'PROJECT',
        parentId: 4583039342477312,
        defaultLanguageCode: 'en',
        published: false,
        requireDataApproval: true,
        dataApprovalGroupId: null,
        ancestorIds: [0, 153142013, 4583039342477312],
        surveyList: [5550609574920192],
        template: false,
      },
      {
        keyId: 153142013,
        description: '',
        name: 'Folder with a few large data sets',
        code: 'Folder with a few large data sets',
        path: '/Folder with a few large data sets',
        monitoringGroup: false,
        newLocaleSurveyId: null,
        createdDateTime: 1490599366674,
        lastUpdateDateTime: 1635150012263,
        projectType: 'PROJECT_FOLDER',
        parentId: 0,
        defaultLanguageCode: 'en',
        published: false,
        requireDataApproval: false,
        dataApprovalGroupId: null,
        ancestorIds: [0],
        surveyList: null,
        template: false,
      },
    ],
    folderEdit: false,
  };

  toggleEditFolderName = id => {
    const surveyGroupToEdit = this.state.surveyGroups.find(surveyGroup => surveyGroup.keyId === id);
    if (!surveyGroupToEdit.isEdit) {
      surveyGroupToEdit.isEdit = true;
    } else {
      surveyGroupToEdit.isEdit = false;
    }
    this.setState(state => ({
      folderEdit: !state.folderEdit,
    }));
  };

  render() {
    const contextData = {
      surveys: this.state.surveys,
      surveyGroups: this.state.surveyGroups,
      strings: this.props.strings,

      // Functions
      formatDate: this.props.formatDate,
      // Actions
      toggleEditFolderName: this.toggleEditFolderName,
    };

    return (
      <SurveysContext.Provider value={contextData}>
        <div className="floats-in">
          <div id="pageWrap" className="widthConstraint belowHeader">
            <section id="allSurvey" className="surveysList">
              <ul>{this.props.isFolder ? <Folders /> : <SurveyLists />}</ul>
            </section>
          </div>
        </div>
      </SurveysContext.Provider>
    );
  }
}

Main.propTypes = {
  strings: PropTypes.object.isRequired,
  isFolder: PropTypes.bool,
  folderEdit: PropTypes.bool,
  formatDate: PropTypes.func,
  toggleEditFolderName: PropTypes.func,
};

Main.defaultProps = {
  isFolder: false,
  folderEdit: false,
  formatDate: () => null,
  toggleEditFolderName: () => null,
};
