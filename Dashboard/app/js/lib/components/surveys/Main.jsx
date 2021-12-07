/* eslint-disable no-param-reassign */
import React from 'react';
import PropTypes from 'prop-types';
import SurveysContext from './surveys-context';
import ForlderList from './FolderList';

export default class Main extends React.Component {
  state = {
    surveyGroups: this.props.surveyGroups,
    isFolderEdit: null,
    inputId: null,
    inputValue: null,
    currentProjectId: null,
    surveyGroupId: null,
    currentProject: null,
  };

  formatDate = datetime => {
    if (datetime === '') return '';
    const date = new Date(parseInt(datetime, 10));
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
  };

  isProjectFolderEmpty = folder => {
    const id = folder !== undefined && folder.keyId;

    const children = FLOW.projectControl
      .get('content')
      .filter(project => project.get('parentId') === id);

    return children.length === 0;
  };

  isProjectFolder = surveyGroup => {
    return surveyGroup === null || surveyGroup.projectType === 'PROJECT_FOLDER';
  };

  isNewProject = currentProject => {
    return currentProject && currentProject.code === 'New survey';
  };

  listClassProperty = () => {
    return FLOW.projectControl.moveTarget || FLOW.projectControl.copyTarget ? 'actionProcess' : '';
  };

  listItemClassProperty = project => {
    let classes = 'aSurvey';

    const isMoving =
      FLOW.projectControl.moveTarget && project.keyId === Number(FLOW.projectControl.moveTarget.id);
    const isCopying =
      FLOW.projectControl.copyTarget && project.keyId === Number(FLOW.projectControl.copyTarget.id);

    const isFolder = this.isProjectFolder(project);
    const isFolderEmpty = this.isProjectFolderEmpty(project);

    if (isFolder) classes += ' aFolder';

    if (isMoving || isCopying) classes += ' highLighted';

    if (isFolderEmpty) classes = 'aFolder folderEmpty';
    if (FLOW.projectControl.newlyCreated === FLOW.projectControl.get('content'))
      classes += ' newlyCreated';

    console.log(FLOW.projectControl.newlyCreated);

    return classes;
  };

  // ACTIONS

  selectProject = surveyGroupId => {
    const self = FLOW.projectControl;
    const surveyGroup = this.state.surveyGroups;
    const project = surveyGroup.find(item => item.get('keyId') === surveyGroupId);
    // the target should not be openable while being moved. Prevents moving it into itself.
    if (self.moveTarget !== null && self.moveTarget.get('keyId') === surveyGroupId) {
      return;
    }

    self.setCurrentProject(project);

    // User is using the breadcrumb to navigate, we could have unsaved changes
    FLOW.store.commit();

    if (self.isProject(project)) {
      //  load caddisfly resources if they are not loaded
      //  and only when surveys are selected
      self.loadCaddisflyResources();

      //  applies to project where data approval has
      //  been previously set
      if (project.get('requireDataApproval')) {
        self.loadDataApprovalGroups();
      }

      FLOW.selectedControl.set('selectedSurveyGroup', project);
    }

    self.set('newlyCreated', null);
    if (surveyGroupId !== this.state.surveyGroupId) {
      this.setState({ currentProjectId: surveyGroupId });
    }
  };

  currentFolders = () => {
    const self = FLOW.projectControl;
    const currentProject = FLOW.projectControl.get('currentProject');
    const parentId = currentProject ? currentProject.get('keyId') : 0;
    return FLOW.projectControl
      .get('content')
      .filter(project => project.get('parentId') === parentId)
      .sort((a, b) => {
        if (self.isProjectFolder(a) && self.isProject(b)) {
          return -1;
        }
        if (self.isProject(a) && self.isProjectFolder(b)) {
          return 1;
        }
        const aCode = a.get('code') || a.get('name');
        const bCode = b.get('code') || b.get('name');
        if (aCode === bCode) return 0;
        if (aCode === 'New survey' || aCode === 'New folder') return -1;
        if (bCode === 'New survey' || bCode === 'New folder') return 1;
        return aCode.localeCompare(bCode);
      });
  };

  setCurrentProject = project => {
    FLOW.projectControl.set('currentProject', project);
    FLOW.selectedControl.set('publishingErrors', null);
    window.scrollTo(0, 0);
  };

  beginMoveProject = surveyGroupId => {
    const surveyGroup = this.state.surveyGroups;
    const moveTarget = surveyGroup.find(item => item.get('keyId') === surveyGroupId);
    FLOW.projectControl.set('moveTarget', moveTarget);
  };

  beginCopyProject = surveyGroupId => {
    const surveyGroup = this.state.surveyGroups;
    const copyTarget = surveyGroup.find(item => item.get('keyId') === surveyGroupId);
    FLOW.projectControl.set('copyTarget', copyTarget);
  };

  deleteSurveyGroup = surveyGroupId => {
    const surveyGroup = FLOW.store.find(FLOW.SurveyGroup, surveyGroupId);
    surveyGroup.deleteRecord();
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedSurveyGroup', null);
  };

  editFolderName = (inputId, inputValue) => {
    this.setState({ inputId, inputValue });
  };

  toggleEditFolderName = surveyGroupId => {
    const surveyGroups = this.currentFolders().map(item => item._data.attributes);

    const surveyGroupToEdit = surveyGroups.find(surveyGroup => surveyGroup.keyId === surveyGroupId);

    surveyGroupToEdit.isEdit = true;

    this.setState(state => ({
      isFolderEdit: !state.isFolderEdit,
    }));
  };

  saveFolderName = surveyGroupId => {
    const surveyGroups = this.currentFolders().map(item => item._data.attributes);

    const surveyGroupToEdit = surveyGroups.find(surveyGroup => surveyGroup.keyId === surveyGroupId);

    // Delete an object property
    delete surveyGroupToEdit.isEdit;

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
  };

  render() {
    const contextData = {
      surveys: this.state.surveys,
      surveyGroups: this.state.surveyGroups,
      strings: this.props.strings,
      currentProjectId: this.state.currentProjectId,
      currentProject: this.state.currentProject,
      surveyGroupId: this.state.surveyGroupId,
      currentFolder: this.currentFolders().map(item => item._data.attributes),

      // Functions

      formatDate: this.formatDate,
      isProjectFolderEmpty: this.isProjectFolderEmpty,
      isProjectFolder: this.isProjectFolder,
      listItemClassProperty: this.listItemClassProperty,
      listClassProperty: this.listClassProperty,
      isNewProject: this.isNewProject,

      // Actions
      toggleEditFolderName: this.toggleEditFolderName,
      editFolderName: this.editFolderName,
      saveFolderName: this.saveFolderName,
      selectProject: this.selectProject,
      beginMoveProject: this.beginMoveProject,
      beginCopyProject: this.beginCopyProject,
      deleteSurveyGroup: this.deleteSurveyGroup,
    };

    return (
      <SurveysContext.Provider value={contextData}>
        <div className="floats-in">
          <div id="pageWrap" className="widthConstraint belowHeader">
            <ForlderList />
          </div>
        </div>
      </SurveysContext.Provider>
    );
  }
}

Main.propTypes = {
  strings: PropTypes.object.isRequired,
  surveyGroups: PropTypes.object,
  isFolder: PropTypes.bool,
  isFolderEdit: PropTypes.bool,
  toggleEditFolderName: PropTypes.func,
};

Main.defaultProps = {
  surveyGroups: null,
  isFolder: false,
  isFolderEdit: false,
  toggleEditFolderName: () => null,
};
