/* eslint-disable no-param-reassign */
import React from 'react';
import PropTypes from 'prop-types';
import SurveysContext from './surveys-context';
import ForlderList from './FolderList';

export default class Main extends React.Component {
  state = {
    surveys: this.props.surveys,
    surveyGroups: this.props.surveyGroups,
    surveysInFolder: [],
    isFolder: true,
    isFolderEdit: null,
    inputId: null,
    inputValue: null,
    currentLevel: 0,
    projectToMoveId: null,
  };

  formatDate = datetime => {
    if (datetime === '') return '';
    const date = new Date(parseInt(datetime, 10));
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
  };

  isProjectFolderEmpty = folder => {
    const id = folder.keyId;
    const children = this.state.surveyGroups.filter(project => project.parentId === id);
    return children.length === 0;
  };

  isProjectFolder = surveyGroup => {
    return surveyGroup === null || surveyGroup.projectType === 'PROJECT_FOLDER';
  };

  sortAscending = surveyGroups => {
    const sortByProjectName = surveyGroups.sort((a, b) => a.code.localeCompare(b.code));
    return sortByProjectName.sort(surveyGroup =>
      surveyGroup.projectType === 'PROJECT_FOLDER' ? -1 : 1
    );
  };

  classNames = project => {
    if (this.state.projectToMoveId === project.keyId) {
      if (this.state.projectToMoveId) {
        return 'highLighted';
      }
      if (this.isProjectFolderEmpty(project)) {
        return 'folderEmpty';
      }
    }
    return '';
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

  selectProject = folderKeyId => {
    this.setState({ currentLevel: folderKeyId });
  };

  moveProject = surveyGroupKeyId => {
    this.setState({ projectToMoveId: surveyGroupKeyId });
  };

  render() {
    const contextData = {
      surveys: this.state.surveys,
      surveyGroups: this.state.surveyGroups,
      strings: this.props.strings,
      surveysInFolder: this.state.surveysInFolder,
      isFolder: this.state.isFolder,
      currentLevel: this.state.currentLevel,
      projectToMoveId: this.state.projectToMoveId,

      // Functions
      formatDate: this.formatDate,
      sortAscending: this.sortAscending,
      isProjectFolderEmpty: this.isProjectFolderEmpty,
      isProjectFolder: this.isProjectFolder,
      classNames: this.classNames,

      // Actions
      toggleEditFolderName: this.toggleEditFolderName,
      editFolderName: this.editFolderName,
      saveFolderName: this.saveFolderName,
      selectProject: this.selectProject,
      moveProject: this.moveProject,
    };

    return (
      <SurveysContext.Provider value={contextData}>
        <div className="floats-in">
          <div id="pageWrap" className="widthConstraint belowHeader">
            <section id="allSurvey" className="surveysList">
              <ul>
                <ForlderList />
              </ul>
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
  surveys: PropTypes.array,
  isFolder: PropTypes.bool,
  isFolderEdit: PropTypes.bool,
  toggleEditFolderName: PropTypes.func,
};

Main.defaultProps = {
  surveyGroups: [],
  surveys: [],
  isFolder: false,
  isFolderEdit: false,
  toggleEditFolderName: () => null,
};
