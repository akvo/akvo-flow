/* eslint-disable no-extend-native */
import React from 'react';
import Surveys from '../../components/surveys/Surveys';

import observe from '../../mixins/observe';
import template from '../../mixins/template';

require('akvo-flow/views/react-component');

if (!String.prototype.trim) {
  String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, '');
  };
}

FLOW.ProjectListView = FLOW.View.extend(template('navSurveys/project-list'));

FLOW.ProjectView = FLOW.ReactComponentView.extend(
  observe({
    'this.selectedLanguage': 'updateSelectedLanguage',
    'this.currentRegistrationForm': 'updateSelectedRegistrationForm',
    'this.showProjectDetails': 'renderReactSide',
    'this.showDataApprovalDetails': 'renderReactSide',
    'this.showResponsibleUsers': 'renderReactSide',
    'FLOW.surveyControl.content.@each.keyId': 'renderReactSide',
    'FLOW.selectedControl.selectedSurvey': 'renderReactSide',
    'FLOW.projectControl.currentProject.newLocaleSurveyId': 'renderReactSide',
    'FLOW.projectControl.currentProject.monitoringGroup': 'renderReactSide',
    'FLOW.projectControl.currentProject.requireDataApproval': 'renderReactSide',
    'FLOW.projectControl.currentProject.template': 'renderReactSide',
  }),
  {
    init() {
      this._super();
      this.getProps = this.getProps.bind(this);
      this.createForm = this.createForm.bind(this);
      this.selectedRegistrationForm = this.selectedRegistrationForm.bind(this);
      this.toggleShowProjectDetails = this.toggleShowProjectDetails.bind(this);
      this.toggleShowDataApprovalDetails = this.toggleShowDataApprovalDetails.bind(this);
      this.toggleShowResponsibleUsers = this.toggleShowResponsibleUsers.bind(this);
      this.visibleProjectBasics = this.visibleProjectBasics.bind(this);
      this.updateSelectedLanguage = this.updateSelectedLanguage.bind(this);
      this.isResponsibleUser = this.isResponsibleUser.bind(this);

      // FORM
      this.form = this.form.bind(this);
      this.showFormDeleteButton = this.showFormDeleteButton.bind(this);
      this.visibleFormBasics = this.visibleFormBasics.bind(this);
      this.renderReactSide = this.renderReactSide.bind(this);
    },

    didInsertElement(...args) {
      this._super(...args);
      this.renderReactSide();
    },

    renderReactSide() {
      console.log(this.disableFormPublishButton());
      const props = this.getProps();
      this.reactRender(<Surveys {...props} />);
    },

    getProps() {
      return {
        currentProject: this.project()._data.attributes,
        arrangedContent: FLOW.surveyControl.get('arrangedContent'),
        dataApprovalGroup:
          FLOW.projectControl.get('dataApprovalGroup') &&
          FLOW.projectControl
            .get('dataApprovalGroup')
            .getEach('_data')
            .getEach('attributes'),
        orderedForms: FLOW.surveyControl.content,
        selectedSurvey:
          FLOW.selectedControl.selectedSurvey &&
          FLOW.selectedControl.selectedSurvey._data.attributes,
        languages: FLOW.languageControl.content,
        approvalGroups: FLOW.router.approvalGroupController.content,
        approvalSteps: FLOW.router.approvalStepsController.content,
        showDataApproval: FLOW.Env.enableDataApproval,
        step: this.step,
        userList: FLOW.router.userListController.content,
        showProjectDetails: this.showProjectDetails,
        showDataApprovalDetails: this.showDataApprovalDetails,
        showResponsibleUsers: this.showResponsibleUsers,

        helperFunctions: {
          isPublished: this.isPublished,
          formCount: this.formCount,
          hasForms: this.hasForms,
          classProperty: this.classProperty,
          isNewProject: this.isNewProject,
          visibleProjectBasics: this.visibleProjectBasics,
          showAddNewFormButton: this.showAddNewFormButton,
          showDataApprovalList: this.showDataApprovalList,
          disableFolderSurveyInputField: this.disableFolderSurveyInputField,
          // Form helper functiions
          showFormPublishButton: this.showFormPublishButton,
          showFormDeleteButton: this.showFormDeleteButton,
          visibleFormBasics: this.visibleFormBasics,
          showFormTranslationsButton: this.showFormTranslationsButton,
          isNewForm: this.isNewForm,
          disableFormFields: this.disableFormFields,
          disableFormPublishButton: this.disableFormPublishButton,
        },

        actions: {
          toggleShowProjectDetails: this.toggleShowProjectDetails,
          toggleShowDataApprovalDetails: this.toggleShowDataApprovalDetails,
          toggleShowResponsibleUsers: this.toggleShowResponsibleUsers,
          toggleMonitoringGroup: this.toggleMonitoringGroup,
          toggleDataApproval: this.toggleDataApproval,
          toggleTemplate: this.toggleTemplate,
          isResponsibleUser: this.isResponsibleUser,
          createForm: this.createForm,
          selectForm: this.selectForm,
          selectedRegistrationForm: this.selectedRegistrationForm,
          updateSelectedLanguage: this.updateSelectedLanguage,
          getSurveyTitle: this.getSurveyTitle,
          saveProject: FLOW.projectControl.saveProject,
        },

        strings: {
          addNewForm: Ember.String.loc('_add_new_form'),
          chooseDataApprovalGroup: Ember.String.loc('_choose_data_approval_group'),
          chooseRegistrationForm: Ember.String.loc('_choose_the_registration_form'),
          collapse: Ember.String.loc('_collapse'),
          enabled: Ember.String.loc('_enabled'),
          enableDataApproval: Ember.String.loc('_enable_data_approval'),
          enableMonitoringFeatures: Ember.String.loc('_enable_monitoring_features'),
          form: Ember.String.loc('_forms'),
          hideApproval: Ember.String.loc('_hide_approval'),
          id: Ember.String.loc('_id'),
          language: Ember.String.loc('_language'),
          markAsTemplate: Ember.String.loc('_mark_as_template'),
          monitoring: Ember.String.loc('_monitoring'),
          noForm: Ember.String.loc('_no_forms_in_this_survey'),
          notEnabled: Ember.String.loc('_not_enabled'),
          orderedApproval: Ember.String.loc('_ordered_approval'),
          responsibleUser: Ember.String.loc('_responsible_users'),
          show: Ember.String.loc('_show'),
          showApproval: Ember.String.loc('_show_approval'),
          surveyBasics: Ember.String.loc('_survey_basics'),
          surveyTitle: Ember.String.loc('_survey_title'),
          unorderedApproval: Ember.String.loc('_unordered_approval'),

          // Form strings
          publish: Ember.String.loc('_publish'),
          preview: Ember.String.loc('_preview'),
          delete: Ember.String.loc('_delete'),
          version: Ember.String.loc('_version'),

          questions: Ember.String.loc('_questions'),
          formBasic: Ember.String.loc('_form_basics'),

          formTitle: Ember.String.loc('_form_title'),
          formApiUrl: Ember.String.loc('_form_api_url'),
          manageTranslations: Ember.String.loc('_manage_translations'),
          manageNotifications: Ember.String.loc('_manage_notifications'),

          tooltip: {
            chooseRegistrationForm: Ember.String.loc('_choose_the_registration_form_tooltip'),
            enableDataApproval: Ember.String.loc('_enable_data_approval_tooltip'),
            markAsTemplate: Ember.String.loc('_mark_as_template_tooltip'),

            // Form tooltip strings
            formApiUrl: Ember.String.loc('_form_api_url_tooltip'),
          },
        },
      };
    },

    // FORM

    showFormBasics: false,

    form() {
      return FLOW.selectedControl.get('selectedSurvey');
    },

    showFormDeleteButton() {
      const form = FLOW.selectedControl.get('selectedSurvey');
      return FLOW.permControl.canEditForm(form);
    },

    showFormPublishButton() {
      const form = FLOW.selectedControl.selectedSurvey;
      return FLOW.permControl.canEditForm(form);
    },

    isNewForm() {
      const form = FLOW.selectedControl.get('selectedSurvey');
      return form && form.get('code').toLowerCase() === 'new form';
    },

    visibleFormBasics() {
      console.log(this.showFormBasics, 'showFormBasic');
      return this.isNewForm() || this.showFormBasics;
    },

    showFormTranslationsButton() {
      const form = FLOW.selectedControl.selectedSurvey;
      return FLOW.permControl.canEditForm(form);
    },

    disableFormFields() {
      const form = FLOW.selectedControl.selectedSurvey;
      return !FLOW.permControl.canEditForm(form);
    },

    disableFormPublishButton() {
      const form = FLOW.selectedControl.selectedSurvey;
      console.log(form.get('status'), 'state');
      const questionsLoading =
        FLOW.questionControl.content && !FLOW.questionControl.content.isLoaded;
      return questionsLoading || form.get('status') === 'PUBLISHED';
    },

    // FORM
    step: null,
    user: null,
    showProjectDetails: false,
    showAdvancedSettings: false,
    selectedLanguage: null,
    monitoringGroupEnabled: false,
    currentRegistrationForm: null,
    showDataApprovalDetails: false,
    showResponsibleUsers: false,

    project() {
      return FLOW.projectControl.get('currentProject');
    },

    classProperty(project) {
      const form = project;
      const currentProject = FLOW.projectControl.get('currentProject');
      let classString = 'aFormTab';

      if (form === null || currentProject === null) return classString;

      // Return "aFormTab" "current" and/or "registrationForm"
      const isActive =
        project && form.keyId === Number(FLOW.selectedControl.get('selectedSurvey').get('id'));
      const isRegistrationForm = form.keyId === currentProject.get('newLocaleSurveyId');

      if (isActive) classString += ' current';
      if (isRegistrationForm) classString += ' registrationForm';

      return classString;
    },

    formCount() {
      return FLOW.surveyControl.content ? FLOW.surveyControl.content.get('length') : 0;
    },

    hasForms() {
      return FLOW.projectControl.get('formCount') > 0;
    },

    isNewProject() {
      const currentProject = FLOW.projectControl.get('currentProject');
      return currentProject && currentProject.get('code') === 'New survey';
    },

    visibleProjectBasics() {
      return this.isNewProject() || this.showProjectDetails;
    },

    isPublished() {
      let form;
      if (!Ember.none(FLOW.selectedControl.get('selectedSurvey'))) {
        form = FLOW.selectedControl.get('selectedSurvey');
      } else if (FLOW.surveyControl.content.get('isLoaded')) {
        form = FLOW.surveyControl.content.get('firstObject');
        FLOW.selectedControl.set('selectedSurvey', form);
      }
      return form && form.get('status') === 'PUBLISHED';
    },

    disableFolderSurveyInputField() {
      const permissions = FLOW.projectControl.get('currentFolderPermissions');
      return permissions.indexOf('PROJECT_FOLDER_UPDATE') < 0;
    },

    showAddNewFormButton() {
      const survey = FLOW.projectControl.currentProject;
      return FLOW.permControl.canEditSurvey(survey);
    },

    showDataApprovalList() {
      return FLOW.projectControl.currentProject.get('requireDataApproval');
    },

    // Approval responsible user
    isResponsibleUser(key, isCheckedValue) {
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
    },

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

    toggleMonitoringGroup() {
      FLOW.projectControl.currentProject.set(
        'monitoringGroup',
        !FLOW.projectControl.currentProject.get('monitoringGroup')
      );
    },

    toggleDataApproval() {
      FLOW.projectControl.currentProject.set(
        'requireDataApproval',
        !FLOW.projectControl.currentProject.get('requireDataApproval')
      );
    },

    toggleTemplate() {
      FLOW.projectControl.currentProject.set(
        'template',
        !FLOW.projectControl.currentProject.get('template')
      );
    },

    /* computer property for setting / getting the value of the current
  registration form */
    selectedRegistrationForm(key, value) {
      const selectedSurvey = FLOW.surveyControl
        .get('arrangedContent')
        .filter(item => item.get('keyId') === value)[0]._data.attributes;

      if (arguments.length > 1) {
        this.set('currentRegistrationForm', selectedSurvey);
      }

      let registrationForm = this.currentRegistrationForm;
      if (!registrationForm) {
        const formId = FLOW.projectControl.currentProject.get('newLocaleSurveyId');
        registrationForm = FLOW.surveyControl.content.filter(item => item.get('keyId') === formId);
        this.set('currentRegistrationForm', registrationForm[0]);
      }
      return registrationForm;
    },

    /*
     * property for setting the currently selected approval group
     */
    selectedApprovalGroup() {
      const approvalGroupId = FLOW.projectControl.currentProject.get('dataApprovalGroupId');
      const approvalGroupList = FLOW.router.approvalGroupListController.get('content');
      const approvalGroup =
        approvalGroupList &&
        approvalGroupList.filterProperty('keyId', approvalGroupId).get('firstObject');
      return approvalGroup;
    },

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

    toggleShowDataApprovalDetails() {
      this.set('showDataApprovalDetails', !this.get('showDataApprovalDetails'));
    },

    refresh() {
      const sg = FLOW.selectedControl.get('selectedSurveyGroup');
      this.set(
        'content',
        FLOW.store.filter(FLOW.Survey, item => item.get('surveyGroupId') === sg.get('keyId'))
      );
    },

    createForm() {
      const code = Ember.String.loc('_new_form').trim();
      const path = `${FLOW.projectControl.get('currentProjectPath')}/${code}`;
      const ancestorIds = FLOW.selectedControl.selectedSurveyGroup.get('ancestorIds');
      ancestorIds.push(FLOW.selectedControl.selectedSurveyGroup.get('keyId'));
      const newForm = FLOW.store.createRecord(FLOW.Survey, {
        name: code,
        code,
        path,
        defaultLanguageCode: 'en',
        requireApproval: false,
        status: 'NOT_PUBLISHED',
        surveyGroupId: FLOW.selectedControl.selectedSurveyGroup.get('keyId'),
        version: '1.0',
        ancestorIds,
      });
      FLOW.projectControl.get('currentProject').set('deleteDisabled', true);
      FLOW.selectedControl.set('selectedSurvey', newForm);
      FLOW.store.commit();
      this.refresh();
    },

    selectForm(project) {
      FLOW.selectedControl.set('selectedSurvey', project);
      //  we don't allow copying or moving between forms
      FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
      FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
      FLOW.selectedControl.set('selectedForMoveQuestion', null);
      FLOW.selectedControl.set('selectedForCopyQuestion', null);
    },

    getSurveyTitle(inputValue) {
      if (inputValue.length > 0) {
        FLOW.projectControl.currentProject.set('name', inputValue);
        FLOW.projectControl.currentProject.set('code', inputValue);
        const path = `${FLOW.projectControl.get('currentProjectPath')}/${inputValue}`;
        FLOW.projectControl.currentProject.set('path', path);
      }
    },

    updateSelectedLanguage(value) {
      const getLanguage = FLOW.languageControl.content.filter(
        language => language.get('value') === value
      );

      if (getLanguage.length !== 0) this.set('selectedLanguage', getLanguage[0]);

      const currentProject = FLOW.projectControl.get('currentProject');

      if (currentProject) currentProject.set('defaultLanguageCode', this.selectedLanguage.value);
    },

    showMonitoringGroupCheckbox() {
      return FLOW.projectControl.get('formCount') < 2;
    },

    updateSelectedRegistrationForm() {
      if (!this.currentRegistrationForm) return;
      FLOW.projectControl.currentProject.set(
        'newLocaleSurveyId',
        this.currentRegistrationForm.keyId
      );
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

FLOW.ProjectList = FLOW.View.extend({
  tagName: 'ul',
  classNameBindings: ['classProperty'],
  classProperty: Ember.computed(() =>
    FLOW.projectControl.moveTarget || FLOW.projectControl.copyTarget ? 'actionProcess' : ''
  ).property('FLOW.projectControl.moveTarget', 'FLOW.projectControl.copyTarget'),
});

FLOW.ProjectItemView = FLOW.View.extend({
  tagName: 'li',
  content: null,
  classNameBindings: ['classProperty'],
  folderEdit: false,

  classProperty: Ember.computed(function() {
    const isFolder = FLOW.projectControl.isProjectFolder(this.content);
    const isFolderEmpty = FLOW.projectControl.isProjectFolderEmpty(this.content);
    const isMoving = this.content === FLOW.projectControl.get('moveTarget');
    const isCopying = this.content === FLOW.projectControl.get('copyTarget');

    let classes = 'aSurvey';
    if (isFolder) classes += ' aFolder';
    if (isFolderEmpty) classes += ' folderEmpty';
    if (isMoving || isCopying) classes += ' highLighted';
    if (FLOW.projectControl.get('newlyCreated') === this.get('content')) classes += ' newlyCreated';

    return classes;
  }).property(
    'FLOW.projectControl.moveTarget',
    'FLOW.projectControl.copyTarget',
    'FLOW.projectControl.currentProject'
  ),

  toggleEditFolderName() {
    this.set('folderEdit', !this.get('folderEdit'));
  },

  isFolder: Ember.computed(function() {
    return FLOW.projectControl.isProjectFolder(this.content);
  }).property(),

  formatDate(datetime) {
    if (datetime === '') return '';
    const date = new Date(parseInt(datetime, 10));
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
  },

  created: Ember.computed(function() {
    return this.formatDate(this.content.get('createdDateTime'));
  }).property('this.content.createdDateTime'),

  modified: Ember.computed(function() {
    return this.formatDate(this.content.get('lastUpdateDateTime'));
  }).property('this.content.lastUpdateDateTime'),

  language: Ember.computed(function() {
    const langs = { en: 'English', es: 'Español', fr: 'Français' };
    return langs[this.content.get('defaultLanguageCode')];
  }).property(),

  hideFolderSurveyDeleteButton: Ember.computed(function() {
    const c = this.get('content');
    const permissions = FLOW.projectControl.get('currentFolderPermissions');
    return permissions.indexOf('PROJECT_FOLDER_DELETE') < 0 || !Ember.empty(c.get('surveyList'));
  }).property(),

  showSurveyEditButton: Ember.computed(function() {
    const survey = this.get('content');
    return (
      FLOW.permControl.canEditSurvey(survey) || FLOW.projectControl.get('newlyCreated') === survey
    );
  }).property(),

  showSurveyMoveButton: Ember.computed(function() {
    const survey = this.get('content');
    return FLOW.permControl.canEditSurvey(survey);
  }).property(),

  showSurveyCopyButton: Ember.computed(function() {
    const survey = this.get('content');
    return FLOW.permControl.canEditSurvey(survey);
  }).property(),
});

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
