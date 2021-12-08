import React from 'react';
import PropTypes from 'prop-types';
import SurveysContext from './surveys-context';
import ForlderList from './FolderList';

export default class Main extends React.Component {
  state = {
    surveyGroups: this.props.surveyGroups,
    isFolderEdit: null,
    inputValue: null,
  };

  formatDate = datetime => {
    if (datetime === '') return '';
    const date = new Date(parseInt(datetime, 10));
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
  };

  language = surveyGroup => {
    const langs = { en: 'English', es: 'Español', fr: 'Français' };
    return langs[surveyGroup.defaultLanguageCode];
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

  listItemClassProperty = surveyGroup => {
    let classes = 'aSurvey';

    const isMoving =
      FLOW.projectControl.moveTarget &&
      surveyGroup.keyId === Number(FLOW.projectControl.moveTarget.id);
    const isCopying =
      FLOW.projectControl.copyTarget &&
      surveyGroup.keyId === Number(FLOW.projectControl.copyTarget.id);

    const isFolder = this.isProjectFolder(surveyGroup);
    const isFolderEmpty = this.isProjectFolderEmpty(surveyGroup);

    if (isFolder) classes += ' aFolder';

    if (isMoving || isCopying) classes += ' highLighted';

    if (isFolderEmpty) classes = 'aFolder folderEmpty';
    if (FLOW.projectControl.newlyCreated === FLOW.projectControl.get('content'))
      classes += ' newlyCreated';

    return classes;
  };

  editFolderName = inputValue => {
    this.setState({ inputValue });
  };

  toggleEditFolderName = surveyGroupId => {
    const surveyGroupToEdit = this.props.currentFolders.find(
      surveyGroup => surveyGroup.keyId === surveyGroupId
    );
    surveyGroupToEdit.isEdit = true;

    this.setState(state => ({
      isFolderEdit: !state.isFolderEdit,
    }));
  };

  saveFolderName = surveyGroupId => {
    const surveyGroupToEdit = this.props.currentFolders.find(
      surveyGroup => surveyGroup.keyId === surveyGroupId
    );

    // Delete an object property
    delete surveyGroupToEdit.isEdit;

    if (this.state.inputValue !== null) {
      const folderToEdit = FLOW.projectControl.find(item => item.get('keyId') === surveyGroupId);

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
      currentFolder: this.props.currentFolders,

      // Functions
      formatDate: this.formatDate,
      language: this.language,
      isProjectFolderEmpty: this.isProjectFolderEmpty,
      isProjectFolder: this.isProjectFolder,
      listItemClassProperty: this.listItemClassProperty,
      listClassProperty: this.listClassProperty,
      isNewProject: this.isNewProject,
      hideFolderSurveyDeleteButton: this.props.hideFolderSurveyDeleteButton,

      // Actions
      toggleEditFolderName: this.toggleEditFolderName,
      editFolderName: this.editFolderName,
      saveFolderName: this.saveFolderName,
      selectProject: this.props.selectProject,
      beginMoveProject: this.props.beginMoveProject,
      beginCopyProject: this.props.beginCopyProject,
      deleteSurveyGroup: this.props.deleteSurveyGroup,
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
  currentFolders: PropTypes.array,
  beginMoveProject: PropTypes.func,
  beginCopyProject: PropTypes.func,
  deleteSurveyGroup: PropTypes.func,
  selectProject: PropTypes.func,
  toggleEditFolderName: PropTypes.func,
  hideFolderSurveyDeleteButton: PropTypes.func,
};

Main.defaultProps = {
  surveyGroups: null,
  isFolder: false,
  isFolderEdit: false,
  currentFolders: [],
  beginMoveProject: () => null,
  beginCopyProject: () => null,
  deleteSurveyGroup: () => null,
  selectProject: () => null,
  toggleEditFolderName: () => null,
  hideFolderSurveyDeleteButton: () => null,
};
