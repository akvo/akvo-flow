/* eslint-disable no-param-reassign */
import React from 'react';
import PropTypes from 'prop-types';
import SurveysContext from './surveys-context';
import ForlderList from './FolderList';
import Survey from './Survey';

export default class Main extends React.Component {
  state = {
    surveys: this.props.surveys,
    surveyGroups: this.props.surveyGroups,
    surveysInFolder: [],
    isFolder: true,
    isFolderEdit: null,
    inputId: null,
    inputValue: null,
    currentProjectId: 0,
    surveyGroupId: null,
    surveyToDisplay: null,
    currentProject: null,
    showProjectDetails: false,
    showDataApproval: FLOW.Env.enableDataApproval,
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

  isNewProject = currentProject => {
    return currentProject && currentProject.code === 'New survey';
  };

  visibleProjectBasics = () => {
    return this.isNewProject() || this.state.showProjectDetails;
  };

  sortAscending = surveyGroups => {
    const sortByProjectName = surveyGroups.sort((a, b) => a.code.localeCompare(b.code));
    return sortByProjectName.sort((a, b) => (a.projectType === b.projectType ? 1 : -1));
  };

  classNames = project => {
    if (this.state.surveyGroupId === project.keyId) {
      if (this.state.surveyGroupId) {
        return 'highLighted';
      }
      if (this.isProjectFolderEmpty(project)) {
        return 'folderEmpty';
      }
    }
    return '';
  };

  toggleShowProjectDetails = () => {
    this.setState(state => ({
      showProjectDetails: !state.showProjectDetails,
    }));
  };

  toggleEditFolderName = surveyGroupId => {
    const surveyGroupToEdit = this.state.surveyGroups.find(
      surveyGroup => surveyGroup.keyId === surveyGroupId
    );
    surveyGroupToEdit.isEdit = true;

    this.setState(state => ({
      isFolderEdit: !state.isFolderEdit,
    }));
  };

  forms = () => {
    if (this.state.currentProject && this.state.currentProject.projectType !== 'PROJECT_FOLDER') {
      return FLOW.store.filter(
        FLOW.Survey,
        form =>
          this.state.currentProject && form.get('surveyGroupId') === this.state.currentProject.keyId
      );
    }
  };

  formCount = () => {
    return this.forms().content ? this.forms().content.get('length') : 0;
  };

  hasForms = () => {
    return this.formCount() > 0;
  };

  editFolderName = (inputId, inputValue) => {
    this.setState({ inputId, inputValue });
  };

  saveFolderName = surveyGroupId => {
    // Toggle the edit button
    const surveyGroupToEdit = this.state.surveyGroups.find(
      surveyGroup => surveyGroup.keyId === surveyGroupId
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

  selectProject = surveyGroupId => {
    // the project should not be openable while being moved. Prevents moving it into itself.
    if (surveyGroupId !== this.state.surveyGroupId) {
      this.setState({ currentProjectId: surveyGroupId });
    }

    // selectProject(evt) {
    //   const project = evt.context;
    //   // the target should not be openable while being moved. Prevents moving it into itself.
    //   if (this.moveTarget == project) {
    //     return;
    //   }

    //   this.setCurrentProject(project);

    //   // User is using the breadcrumb to navigate, we could have unsaved changes
    //   FLOW.store.commit();

    //   if (this.isProject(project)) {
    //     // load caddisfly resources if they are not loaded
    //     // and only when surveys are selected
    //     this.loadCaddisflyResources();

    //     // applies to project where data approval has
    //     // been previously set
    //     if (project.get('requireDataApproval')) {
    //       this.loadDataApprovalGroups();
    //     }

    //     FLOW.selectedControl.set('selectedSurveyGroup', project);
    //   }

    //   this.set('newlyCreated', null);
    // },
  };

  setCurrentSurvey = surveyId => {
    const getSurvey = this.state.surveys.find(survey => survey.keyId === surveyId);

    this.setState(state => ({
      currentProject: state.surveyGroups.find(
        surveyGroup => surveyGroup.surveyList !== null && surveyGroup.surveyList.includes(surveyId)
      ),
    }));

    this.setState({ surveyToDisplay: { ...getSurvey } });
  };

  beginMoveProject = surveyGroupId => {
    this.setState({ surveyGroupId });
  };

  beginCopyProject = surveyGroupId => {
    this.setState({ surveyGroupId });
  };

  orderForms = () => {
    if (this.state.currentProject && this.state.currentProject.keyId > 0) {
      const sgId = this.state.currentProject.keyId;
      const self = FLOW.surveyControl;
      const forms = FLOW.store.filter(FLOW.Survey, item => item.get('surveyGroupId') === sgId);
      self.set('orderedForms', []);

      if (forms.get('length') > 1 && this.state.currentProject.monitoringGroup) {
        // find registration form if set
        let regFormId;
        const regForm = forms.find(
          item => item.get('keyId') === this.state.currentProject.newLocaleSurveyId
        );
        if (regForm) {
          regFormId = regForm.get('keyId');
        } else {
          regFormId = forms.get('firstObject').get('keyId'); // registration form not defined so assume first form is registration
        }

        self.orderedForms.push(forms.find(form => form.get('keyId') === regFormId));

        return forms
          .filter(form => form.get('keyId') !== regFormId)
          .sort((a, b) => {
            const nameA = a.get('name').toUpperCase();
            const nameB = b.get('name').toUpperCase();
            if (nameA < nameB) {
              return -1;
            }
            if (nameA > nameB) {
              return 1;
            }
            return 0;
          })
          .forEach(form => {
            self.orderedForms.push(form);
          });
      }
      self.orderedForms.push(forms.find(form => form.get('surveyGroupId') === sgId));
    }
  };

  render() {
    const contextData = {
      surveys: this.state.surveys,
      surveyGroups: this.state.surveyGroups,
      strings: this.props.strings,
      surveysInFolder: this.state.surveysInFolder,
      isFolder: this.state.isFolder,
      currentProjectId: this.state.currentProjectId,
      currentProject: this.state.currentProject,
      surveyGroupId: this.state.surveyGroupId,
      surveyToDisplay: this.state.surveyToDisplay,
      showDataApproval: this.state.showDataApproval,

      // Functions
      formatDate: this.formatDate,
      sortAscending: this.sortAscending,
      isProjectFolderEmpty: this.isProjectFolderEmpty,
      isProjectFolder: this.isProjectFolder,
      classNames: this.classNames,
      isNewProject: this.isNewProject,
      visibleProjectBasics: this.visibleProjectBasics,
      orderForms: this.orderForms,
      formCount: this.formCount,
      hasForms: this.hasForms,
      forms: this.forms,

      // Actions
      toggleEditFolderName: this.toggleEditFolderName,
      toggleShowProjectDetails: this.toggleShowProjectDetails,
      editFolderName: this.editFolderName,
      saveFolderName: this.saveFolderName,
      selectProject: this.selectProject,
      setCurrentSurvey: this.setCurrentSurvey,
      beginMoveProject: this.beginMoveProject,
      beginCopyProject: this.beginCopyProject,
    };

    return (
      <SurveysContext.Provider value={contextData}>
        <div className="floats-in">
          <div id="pageWrap" className="widthConstraint belowHeader">
            {this.state.surveyToDisplay !== null ? <Survey /> : <ForlderList />}
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
