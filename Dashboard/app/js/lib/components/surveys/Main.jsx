import React from 'react';
import PropTypes from 'prop-types';
import SurveysContext from './surveys-context';
import ForlderList from './FolderList';

export default class Main extends React.Component {
  state = {
    isFolderEdit: null,
    inputValue: null,
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
      helperFunctions: this.props.helperFunctions,
      classProperty: this.props.classProperty,
      displayContentFunctions: this.props.displayContentFunctions,
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
  helperFunctions: PropTypes.object,
  classProperty: PropTypes.object,
  toggleEditFolderName: PropTypes.func,
  displayContentFunctions: PropTypes.object,
};

Main.defaultProps = {
  currentFolders: [],
  actions: null,
  helperFunctions: null,
  classProperty: null,
  toggleEditFolderName: () => null,
  displayContentFunctions: null,
};
