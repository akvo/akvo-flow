import React from 'react';
import PropTypes from 'prop-types';
import SurveysContext from './surveys-context';
import ForlderList from './FolderList';

export default class Main extends React.Component {
  state = {
    inputValue: null,
  };

  editFolderName = inputValue => {
    this.setState({ inputValue });
  };

  toggleEditFolderName = project => {
    project.isEdit = true;

    this.setState({
      inputValue: null,
    });
  };

  saveFolderName = project => {
    // Deletes an object property
    delete project.isEdit;

    if (this.state.inputValue !== null) {
      const folderToEdit = FLOW.projectControl.find(item => item.get('keyId') === project.keyId);

      folderToEdit.set('name', this.state.inputValue);
      folderToEdit.set('code', this.state.inputValue);
      const path = `${FLOW.projectControl.get('currentProjectPath')}/${this.state.inputValue}`;
      folderToEdit.set('path', path);
      FLOW.store.commit();
    }

    // Resets input value to prevent it from modifying the name of all selected folder
    this.setState({
      inputValue: null,
    });
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
      focusOut: this.focusOut,
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
