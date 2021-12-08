import React from 'react';
import PropTypes from 'prop-types';
import SurveysContext from './surveys-context';
import ForlderList from './FolderList';

export default class Main extends React.Component {
  state = {
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
    if (
      FLOW.projectControl.newlyCreated &&
      Number(FLOW.projectControl.newlyCreated.id) === surveyGroup.keyId
    )
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
      strings: this.props.strings,
      currentFolder: this.props.currentFolders,

      formatDate: this.formatDate,
      language: this.language,

      folderModifierFunction: {
        isProjectFolderEmpty: this.isProjectFolderEmpty,
        isProjectFolder: this.isProjectFolder,
        isNewProject: this.isNewProject,
      },

      classProperty: {
        list: this.listClassProperty,
        listItem: this.listItemClassProperty,
      },

      displayContentFunction: this.props.displayContentFunction,
      actions: this.props.actions,
      toggleEditFolderName: this.toggleEditFolderName,
      editFolderName: this.editFolderName,
      saveFolderName: this.saveFolderName,
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
  currentFolders: PropTypes.array,
  actions: PropTypes.object,
  toggleEditFolderName: PropTypes.func,
  displayContentFunction: PropTypes.object,
};

Main.defaultProps = {
  currentFolders: [],
  actions: null,
  toggleEditFolderName: () => null,
  displayContentFunction: null,
};
