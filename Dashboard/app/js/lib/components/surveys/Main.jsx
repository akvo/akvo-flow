/* eslint-disable no-param-reassign */
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
    surveyGroups: this.props.surveyGroups,
    isFolderEdit: null,
    inputId: null,
    inputValue: null,
  };

  formatDate = datetime => {
    if (datetime === '') return '';
    const date = new Date(parseInt(datetime, 10));
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
  };

  toggleEditFolderName = surveyGroupKeyId => {
    const surveyGroupToEdit = this.state.surveyGroups.find(
      surveyGroup => surveyGroup.keyId === surveyGroupKeyId
    );
    surveyGroupToEdit.isEdit = true;

    this.setState(state => ({
      isFolderEdit: !state.isFolderEdit,
    }));
  };

  editFolderName = (inputId, inputValue) => {
    this.setState({ inputId, inputValue });
  };

  saveFolderName = surveyGroupKeyId => {
    // Toggle the edit button
    const surveyGroupToEdit = this.state.surveyGroups.find(
      surveyGroup => surveyGroup.keyId === surveyGroupKeyId
    );
    surveyGroupToEdit.isEdit = false;

    if (this.state.inputValue !== null) {
      const folderToEdit = FLOW.projectControl.find(
        item => item.get('keyId') === this.state.inputId
      );

      folderToEdit.set('name', this.state.inputValue);
      folderToEdit.set('code', this.state.inputValue);
      const path = `${FLOW.projectControl.get('currentProjectPath')}/${this.state.inputValue}`;
      folderToEdit.set('path', path);

      FLOW.store.commit();
    }

    this.setState(state => ({
      isFolderEdit: !state.isFolderEdit,
    }));

    this.state.surveyGroups.map(surveyGroup => {
      if (this.state.inputId === surveyGroup.keyId) {
        surveyGroup.name = this.state.inputValue;
        surveyGroup.code = this.state.inputValue;
        surveyGroup.path = this.state.inputValue;
      }
      return surveyGroup;
    });
  };

  render() {
    const contextData = {
      surveys: this.state.surveys,
      surveyGroups: this.state.surveyGroups,
      strings: this.props.strings,

      // Functions
      formatDate: this.formatDate,
      // Actions
      toggleEditFolderName: this.toggleEditFolderName,
      editFolderName: this.editFolderName,
      saveFolderName: this.saveFolderName,
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
  surveyGroups: PropTypes.array,
  isFolder: PropTypes.bool,
  isFolderEdit: PropTypes.bool,
  toggleEditFolderName: PropTypes.func,
};

Main.defaultProps = {
  surveyGroups: [],
  isFolder: false,
  isFolderEdit: false,
  toggleEditFolderName: () => null,
};
