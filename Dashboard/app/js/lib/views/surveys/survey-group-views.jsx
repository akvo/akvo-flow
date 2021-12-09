/* eslint-disable no-extend-native */

import React from 'react';
import Main from '../../components/surveys/Main';

import observe from '../../mixins/observe';

require('akvo-flow/views/react-component');

if (!String.prototype.trim) {
  String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, '');
  };
}

FLOW.Project = FLOW.View.extend(
  observe({
    'this.selectedLanguage': 'updateSelectedLanguage',
    currentRegistrationForm: 'updateSelectedRegistrationForm',
  }),
  {
    showProjectDetails: false,
    showAdvancedSettings: false,
    selectedLanguage: null,
    monitoringGroupEnabled: false,
    currentRegistrationForm: null,
    showDataApprovalDetails: false,

    /* computer property for setting / getting the value of the current
  registration form */
    selectedRegistrationForm: Ember.computed(function(key, value) {
      if (arguments.length > 1) {
        this.set('currentRegistrationForm', value);
      }

      let registrationForm = this.get('currentRegistrationForm');
      if (!registrationForm) {
        const formId = FLOW.projectControl.currentProject.get('newLocaleSurveyId');
        registrationForm = FLOW.surveyControl.content.filter(
          item => item.get('keyId') === formId
        )[0];
        this.set('currentRegistrationForm', registrationForm);
      }
      return registrationForm;
    }).property('FLOW.projectControl.currentProject'),

    /*
     * property for setting the currently selected approval group
     */
    selectedApprovalGroup: Ember.computed(() => {
      const approvalGroupId = FLOW.projectControl.currentProject.get('dataApprovalGroupId');
      const approvalGroupList = FLOW.router.approvalGroupListController.get('content');
      const approvalGroup =
        approvalGroupList &&
        approvalGroupList.filterProperty('keyId', approvalGroupId).get('firstObject');
      return approvalGroup;
    }).property('FLOW.projectControl.currentProject'),

    project: Ember.computed(() => FLOW.projectControl.get('currentProject')).property(),

    toggleShowProjectDetails() {
      this.set('showProjectDetails', !this.get('showProjectDetails'));
    },

    /*
     * Toggle advanced settings and load data approval
     * groups if data approval is enabled on the instance
     */
    toggleShowAdvancedSettings() {
      const approvalGroupListController = FLOW.router.get('approvalGroupListController');
      if (FLOW.Env.enableDataApproval && !approvalGroupListController.content) {
        const self = this;

        const groups = FLOW.ApprovalGroup.find({});
        approvalGroupListController.set('content', groups);

        // only toggle the property after approval groups are retrieved
        groups.on('didLoad', () => {
          self.toggleProperty('showAdvancedSettings');
        });
      } else {
        this.toggleProperty('showAdvancedSettings');
      }
    },

    isNewProject: Ember.computed(() => {
      const currentProject = FLOW.projectControl.get('currentProject');
      return currentProject && currentProject.get('code') == 'New survey';
    }).property('FLOW.projectControl.currentProject'),

    visibleProjectBasics: Ember.computed(function() {
      return this.get('isNewProject') || this.get('showProjectDetails');
    }).property('showProjectDetails'),

    updateSelectedLanguage() {
      const currentProject = FLOW.projectControl.get('currentProject');
      if (currentProject)
        currentProject.set('defaultLanguageCode', this.selectedLanguage.get('value'));
    },

    showMonitoringGroupCheckbox: Ember.computed(
      () => FLOW.projectControl.get('formCount') < 2
    ).property('FLOW.projectControl.formCount'),

    updateSelectedRegistrationForm() {
      if (!this.get('currentRegistrationForm')) return;
      FLOW.projectControl.currentProject.set(
        'newLocaleSurveyId',
        this.currentRegistrationForm.get('keyId')
      );
    },

    isPublished: Ember.computed(() => {
      let form;
      if (!Ember.none(FLOW.selectedControl.get('selectedSurvey'))) {
        form = FLOW.selectedControl.get('selectedSurvey');
      } else if (FLOW.surveyControl.content.get('isLoaded')) {
        form = FLOW.surveyControl.content.get('firstObject');
        FLOW.selectedControl.set('selectedSurvey', form);
      }
      return form && form.get('status') === 'PUBLISHED';
    }).property('FLOW.selectedControl.selectedSurvey.status'),

    disableFolderSurveyInputField: Ember.computed(() => {
      const permissions = FLOW.projectControl.get('currentFolderPermissions');
      return permissions.indexOf('PROJECT_FOLDER_UPDATE') < 0;
    }).property('FLOW.projectControl.currentProjectPath'),

    showAddNewFormButton: Ember.computed(() => {
      const survey = FLOW.projectControl.get('currentProject');
      return FLOW.permControl.canEditSurvey(survey);
    }).property(),

    showDataApproval: Ember.computed(() => FLOW.Env.enableDataApproval).property(),

    showDataApprovalList: Ember.computed(() =>
      FLOW.projectControl.currentProject.get('requireDataApproval')
    ).property('FLOW.projectControl.currentProject.requireDataApproval'),

    toggleShowDataApprovalDetails() {
      this.set('showDataApprovalDetails', !this.get('showDataApprovalDetails'));
    },
  }
);

FLOW.SurveyApprovalView = FLOW.View.extend({});

FLOW.SurveyApprovalStepView = FLOW.View.extend({
  step: null,

  showResponsibleUsers: false,

  toggleShowResponsibleUsers() {
    this.toggleProperty('showResponsibleUsers');
    this.loadUsers();
  },

  /*
   * load the users list if not present
   */
  loadUsers() {
    const users = FLOW.router.userListController.get('content');
    if (Ember.empty(users)) {
      FLOW.router.userListController.set('content', FLOW.User.find());
    }
  },
});

FLOW.ApprovalResponsibleUserView = FLOW.View.extend({
  user: null,

  step: null,

  isResponsibleUser: Ember.computed(function(key, isCheckedValue) {
    const step = this.get('step');
    const user = this.get('user');

    if (!step || !user) {
      return false;
    }

    // create a new list to force enabling of 'Save' button for surveys
    // when a user is added or removed from approver list
    const approverUserList = Ember.A();
    if (!Ember.empty(step.get('approverUserList'))) {
      approverUserList.pushObjects(step.get('approverUserList'));
    }

    // setter
    if (arguments.length > 1) {
      if (isCheckedValue) {
        approverUserList.addObject(user.get('keyId'));
      } else {
        approverUserList.removeObject(user.get('keyId'));
      }
      step.set('approverUserList', approverUserList);
    }

    // getter
    return approverUserList.contains(user.get('keyId'));
  }).property('this.step.approverUserList'),
});

FLOW.ProjectMainView = FLOW.View.extend({
  surveyTemplatesList: Ember.computed(() => {
    const projects = FLOW.projectControl.get('content');
    return projects.filter(project => project.get('template'));
  }).property('FLOW.projectControl.content.@each'),

  createSurveyFromTemplate() {
    this.set('showTemplates', false);
    FLOW.projectControl.copyTemplate(this.get('selectedSurveyTemplate'));
  },

  hideTemplateList() {
    this.set('showTemplates', false);
  },

  displayTemplateList() {
    this.set('showTemplates', true);
  },

  showTemplates: false,

  selectedSurveyTemplate: null,

  hasUnsavedChanges: Ember.computed(() => {
    const selectedProject = FLOW.projectControl.get('currentProject');
    const isProjectDirty = selectedProject ? selectedProject.get('isDirty') : false;

    const selectedForm = FLOW.selectedControl.get('selectedSurvey');
    const isFormDirty = selectedForm ? selectedForm.get('isDirty') : false;

    const approvalSteps = FLOW.router.approvalStepsController.get('content');
    let isApprovalStepDirty = false;

    if (approvalSteps) {
      approvalSteps.forEach(step => {
        if (!isApprovalStepDirty && step.get('isDirty')) {
          isApprovalStepDirty = true;
        }
      });
    }

    return isProjectDirty || isFormDirty || isApprovalStepDirty;
  }).property(
    'FLOW.projectControl.currentProject.isDirty',
    'FLOW.selectedControl.selectedSurvey.isDirty',
    'FLOW.router.approvalStepsController.content.@each.approverUserList'
  ),

  projectView: Ember.computed(() =>
    FLOW.projectControl.isProject(FLOW.projectControl.get('currentProject'))
  ).property('FLOW.projectControl.currentProject'),

  projectListView: Ember.computed(() =>
    FLOW.projectControl.isProjectFolder(FLOW.projectControl.get('currentProject'))
  ).property('FLOW.projectControl.currentProject'),

  disableAddFolderButton: Ember.computed(() => {
    const permissions = FLOW.projectControl.get('currentFolderPermissions');
    return permissions.indexOf('PROJECT_FOLDER_CREATE') < 0;
  }).property('FLOW.projectControl.currentProjectPath'),

  disableAddSurveyButtonInRoot: Ember.computed(
    () => FLOW.projectControl.get('currentProjectPath').length == 0
  ).property('FLOW.projectControl.currentProjectPath'),

  disableAddSurveyButton: Ember.computed(() => {
    const permissions = FLOW.projectControl.get('currentFolderPermissions');
    return permissions.indexOf('PROJECT_FOLDER_CREATE') < 0;
  }).property('FLOW.projectControl.currentProjectPath'),
});

FLOW.ProjectItemView = FLOW.ReactComponentView.extend(
  observe({
    'FLOW.projectControl.content.isLoaded': 'renderReactSide',
    'FLOW.projectControl.currentProject': 'renderReactSide',
    'FLOW.projectControl.content.@each.keyId': 'renderReactSide',

    // Add "highLight" className when copying or moving a surveyGroup
    'FLOW.projectControl.moveTarget': 'renderReactSide',
    'FLOW.projectControl.copyTarget': 'renderReactSide',
  }),
  {
    init() {
      this._super();
      this.getProps = this.getProps.bind(this);
      this.listItemClassProperty = this.listItemClassProperty.bind(this);
      this.selectProject = this.selectProject.bind(this);
      this.beginMoveProject = this.beginMoveProject.bind(this);
      this.beginCopyProject = this.beginCopyProject.bind(this);
      this.deleteSurveyGroup = this.deleteSurveyGroup.bind(this);
      this.setCurrentProject = this.setCurrentProject.bind(this);
      this.renderReactSide = this.renderReactSide.bind(this);
    },

    didInsertElement(...args) {
      this._super(...args);
      this.renderReactSide();
    },

    renderReactSide() {
      const props = this.getProps();
      this.reactRender(<Main {...props} />);
    },

    getProps() {
      return {
        currentFolders: this.currentFolders().map(item => item._data.attributes),

        classProperty: {
          list: this.listClassProperty,
          listItem: this.listItemClassProperty,
        },

        helperFunctions: {
          language: this.language,
          formatDate: this.formatDate,
          isProjectFolder: this.isProjectFolder,
          isNewProject: this.isNewProject,
          isProjectFolderEmpty: this.isProjectFolderEmpty,
        },

        displayContentFunctions: {
          showSurveyCopyButton: this.showSurveyCopyButton,
          showSurveyMoveButton: this.showSurveyMoveButton,
          showSurveyEditButton: this.showSurveyEditButton,
          hideFolderSurveyDeleteButton: this.hideFolderSurveyDeleteButton,
        },

        strings: {
          editFolderName: Ember.String.loc('_edit_folder_name'),
          created: Ember.String.loc('_created'),
          modified: Ember.String.loc('_modified'),
          language: Ember.String.loc('_language'),
          edit: Ember.String.loc('_edit'),
          move: Ember.String.loc('_move'),
          delete: Ember.String.loc('_delete'),
          copy: Ember.String.loc('_copy'),
        },

        actions: {
          selectProject: this.selectProject,
          beginMoveProject: this.beginMoveProject,
          beginCopyProject: this.beginCopyProject,
          deleteSurveyGroup: this.deleteSurveyGroup,
        },
      };
    },

    surveyGroups: null,

    formatDate(datetime) {
      if (datetime === '') return '';
      const date = new Date(parseInt(datetime, 10));
      return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
    },

    language(surveyGroup) {
      const langs = { en: 'English', es: 'Español', fr: 'Français' };
      return langs[surveyGroup.defaultLanguageCode];
    },

    hideFolderSurveyDeleteButton(surveyGroup) {
      const permissions = FLOW.projectControl.get('currentFolderPermissions');
      return permissions.indexOf('PROJECT_FOLDER_DELETE') < 0 || surveyGroup.surveyList !== null;
    },

    showSurveyEditButton(surveyGroup) {
      return (
        FLOW.permControl.canEditSurvey(surveyGroup) ||
        FLOW.projectControl.get('newlyCreated') === surveyGroup
      );
    },

    showSurveyMoveButton(surveyGroup) {
      return FLOW.permControl.canEditSurvey(surveyGroup);
    },

    showSurveyCopyButton(surveyGroup) {
      return FLOW.permControl.canEditSurvey(surveyGroup);
    },

    isProjectFolderEmpty(folder) {
      const id = folder !== undefined && folder.keyId;

      const children = this.surveyGroups.filter(project => project.get('parentId') === id);

      return children.length === 0;
    },

    isProjectFolder(surveyGroup) {
      return surveyGroup === null || surveyGroup.projectType === 'PROJECT_FOLDER';
    },

    isNewProject(currentProject) {
      return currentProject && currentProject.code === 'New survey';
    },

    listClassProperty() {
      return FLOW.projectControl.moveTarget || FLOW.projectControl.copyTarget
        ? 'actionProcess'
        : '';
    },

    listItemClassProperty(surveyGroup) {
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
    },

    selectProject(surveyGroupId) {
      const self = FLOW.projectControl;
      const project = this.surveyGroups.find(item => item.get('keyId') === surveyGroupId);
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
    },

    setCurrentProject(project) {
      FLOW.projectControl.set('currentProject', project);
      FLOW.selectedControl.set('publishingErrors', null);
      window.scrollTo(0, 0);
    },

    currentFolders() {
      const self = FLOW.projectControl;
      const currentProject = self.get('currentProject');
      const parentId = currentProject ? currentProject.get('keyId') : 0;
      this.set('surveyGroups', self.get('content'));
      return self
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
    },

    beginMoveProject(surveyGroupId) {
      const moveTarget = this.surveyGroups.find(item => item.get('keyId') === surveyGroupId);
      FLOW.projectControl.set('moveTarget', moveTarget);
    },

    beginCopyProject(surveyGroupId) {
      const copyTarget = this.surveyGroups.find(item => item.get('keyId') === surveyGroupId);
      FLOW.projectControl.set('copyTarget', copyTarget);
    },

    deleteSurveyGroup(surveyGroupId) {
      const surveyGroup = FLOW.store.find(FLOW.SurveyGroup, surveyGroupId);
      surveyGroup.deleteRecord();
      FLOW.store.commit();
      FLOW.selectedControl.set('selectedSurveyGroup', null);
    },
  }
);

FLOW.FolderEditView = Ember.TextField.extend({
  content: null,
  path: null,

  saveFolderName() {
    const name = this.content.get('code').trim();
    this.content.set('name', name);
    this.content.set('code', name);
    const path = `${FLOW.projectControl.get('currentProjectPath')}/${name}`;
    this.content.set('path', path);
    FLOW.store.commit();
  },

  focusOut() {
    this.get('parentView').set('folderEdit', false);
    this.saveFolderName();
  },

  insertNewline() {
    this.get('parentView').set('folderEdit', false);
    this.saveFolderName();
  },
});

FLOW.FormTabView = Ember.View.extend({
  tagName: 'li',
  content: null,
  classNameBindings: ['classProperty'],

  classProperty: Ember.computed(function() {
    const form = this.get('content');
    const currentProject = FLOW.projectControl.get('currentProject');
    let classString = 'aFormTab';

    if (form === null || currentProject === null) return classString;

    // Return "aFormTab" "current" and/or "registrationForm"
    const isActive = form === FLOW.selectedControl.get('selectedSurvey');
    const isRegistrationForm =
      currentProject.get('monitoringGroup') &&
      form.get('keyId') === currentProject.get('newLocaleSurveyId');

    if (isActive) classString += ' current';
    if (isRegistrationForm) classString += ' registrationForm';

    return classString;
  }).property(
    'FLOW.selectedControl.selectedSurvey',
    'FLOW.projectControl.currentProject.newLocaleSurveyId',
    'content.status'
  ),
});
