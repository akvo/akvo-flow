function capitaliseFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

if (!String.prototype.trim) {
  String.prototype.trim = function(){
    return this.replace(/^\s+|\s+$/g, '');
  };
}

FLOW.ProjectListView = FLOW.View.extend({
  templateName: 'navSurveys/project-list'
});

FLOW.ProjectView = FLOW.View.extend({
  templateName: 'navSurveys/project',
});

FLOW.Project = FLOW.View.extend({

  showProjectDetails: false,
  showAdvancedSettings: false,
  selectedLanguage: null,
  monitoringGroupEnabled: false,
  currentRegistrationForm: null,
  showDataApprovalDetails: false,

  /* computer property for setting / getting the value of the current
  registration form */
  selectedRegistrationForm: function(key, value, previousValue){
    var registrationForm, formId;

    if(arguments.length > 1) {
        this.set('currentRegistrationForm', value);
    }

    registrationForm = this.get('currentRegistrationForm');
    if(!registrationForm) {
        formId = FLOW.projectControl.currentProject.get('newLocaleSurveyId');
        registrationForm = FLOW.surveyControl.content.filter(function(item){
                return item.get('keyId') === formId;
        })[0];
        this.set('currentRegistrationForm', registrationForm);
    }
    return registrationForm;
  }.property('FLOW.projectControl.currentProject'),

  /*
   * property for setting the currently selected approval group
   */
  selectedApprovalGroup: function () {
      var approvalGroupId = FLOW.projectControl.currentProject.get('dataApprovalGroupId');
      var approvalGroupList = FLOW.router.approvalGroupListController.get('content');
      var approvalGroup = approvalGroupList &&
                              approvalGroupList.filterProperty('keyId', approvalGroupId).get('firstObject');
      return approvalGroup;
  }.property('FLOW.projectControl.currentProject'),

  project: function() {
    return FLOW.projectControl.get('currentProject');
  }.property(),

  toggleShowProjectDetails: function() {
    this.set('showProjectDetails', !this.get('showProjectDetails'));
  },

  /*
   * Toggle advanced settings and load data approval
   * groups if data approval is enabled on the instance
   */
  toggleShowAdvancedSettings: function() {
      var approvalGroupListController = FLOW.router.get('approvalGroupListController');
      if(FLOW.Env.enableDataApproval && !approvalGroupListController.content) {
          var self = this;

          var groups = FLOW.ApprovalGroup.find({});
          approvalGroupListController.set('content', groups);

          // only toggle the property after approval groups are retrieved
          groups.on('didLoad', function () {
              self.toggleProperty('showAdvancedSettings');
          });
      } else {
          this.toggleProperty('showAdvancedSettings');
      }
  },

  isNewProject: function() {
    var currentProject = FLOW.projectControl.get('currentProject');
    return currentProject && currentProject.get('code') == "New survey";
  }.property('FLOW.projectControl.currentProject'),

  visibleProjectBasics: function() {
    return this.get('isNewProject') || this.get('showProjectDetails');
  }.property('showProjectDetails'),

  updateSelectedLanguage: function() {
    var currentProject = FLOW.projectControl.get('currentProject');
    if (currentProject)
      currentProject.set('defaultLanguageCode', this.selectedLanguage.get('value'));
  }.observes('this.selectedLanguage'),

  showMonitoringGroupCheckbox: function() {
    return FLOW.projectControl.get('formCount') < 2;
  }.property("FLOW.projectControl.formCount"),

  updateSelectedRegistrationForm: function() {
    if (!this.get('currentRegistrationForm')) return;
    FLOW.projectControl.currentProject.set('newLocaleSurveyId', this.currentRegistrationForm.get('keyId'));
  }.observes('currentRegistrationForm'),

  isPublished: function() {
    var form = FLOW.selectedControl.get('selectedSurvey');
    return form.get('status') === 'PUBLISHED'
  }.property('FLOW.selectedControl.selectedSurvey.status'),

  disableFolderSurveyInputField: function() {
    var permissions = FLOW.projectControl.get('currentFolderPermissions');
    return permissions.indexOf("PROJECT_FOLDER_UPDATE") < 0;
  }.property('FLOW.projectControl.currentProjectPath'),

  showAddNewFormButton: function () {
    var survey = FLOW.projectControl.get('currentProject');
    return FLOW.permControl.canEditSurvey(survey);
  }.property(),

  showDataApproval: function () {
      return FLOW.Env.enableDataApproval;
  }.property(),

  showDataApprovalList: function () {
      return FLOW.projectControl.currentProject.get('requireDataApproval');
  }.property('FLOW.projectControl.currentProject.requireDataApproval'),

  toggleShowDataApprovalDetails: function () {
      this.set('showDataApprovalDetails', !this.get('showDataApprovalDetails'));
  },
});

FLOW.SurveyApprovalView = FLOW.View.extend({});

FLOW.SurveyApprovalStepView = FLOW.View.extend({
    step: null,

    showResponsibleUsers: false,


    toggleShowResponsibleUsers: function () {
        this.toggleProperty('showResponsibleUsers');
        this.loadUsers();
    },

    /*
     * load the users list if not present
     */
    loadUsers: function() {
        var users = FLOW.router.userListController.get('content');
        if(Ember.empty(users)) {
            FLOW.router.userListController.set('content', FLOW.User.find());
        }
    },
});

FLOW.ApprovalResponsibleUserView = FLOW.View.extend({
    user: null,

    step: null,

    isResponsibleUser: function (key, isCheckedValue, previousCheckedValue) {
        var step = this.get('step');
        var user = this.get('user');

        if (!step || !user) {
            return false;
        }

        // create a new list to force enabling of 'Save' button for surveys
        // when a user is added or removed from approver list
        var approverUserList = Ember.A();
        if(!Ember.empty(step.get('approverUserList'))) {
            approverUserList.pushObjects(step.get('approverUserList'));
        }

        // setter
        if(arguments.length > 1) {
            if (isCheckedValue) {
                approverUserList.addObject(user.get('keyId'));
            } else {
                approverUserList.removeObject(user.get('keyId'));
            }
            step.set('approverUserList', approverUserList);
        }

        // getter
        return approverUserList.contains(user.get('keyId'));
    }.property('this.step.approverUserList'),
});

FLOW.ProjectMainView = FLOW.View.extend({

  doSave: function() {
    var currentProject = FLOW.projectControl.get('currentProject');
    var currentForm = FLOW.selectedControl.get('selectedSurvey');

    if (currentProject && currentProject.get('isDirty')) {
      var name = currentProject.get('name').trim();
      currentProject.set('name', name);
      currentProject.set('code', name);
      currentProject.set('path', FLOW.projectControl.get('currentProjectPath'));
    }

    if (currentForm && currentForm.get('isDirty')) {
      var name = currentForm.get('name').trim();
      currentForm.set('name', name);
      currentForm.set('code', name);
      var path = FLOW.projectControl.get('currentProjectPath') + "/" + name;
      currentForm.set('path', path);
    }

    FLOW.store.commit();
  },

  hasUnsavedChanges: function() {
    var selectedProject = FLOW.projectControl.get('currentProject');
    var isProjectDirty = selectedProject ? selectedProject.get('isDirty') : false;

    var selectedForm = FLOW.selectedControl.get('selectedSurvey');
    var isFormDirty = selectedForm ? selectedForm.get('isDirty') : false;

    var approvalSteps = FLOW.router.approvalStepsController.get('content');
    var isApprovalStepDirty = false;

    if (approvalSteps) {
        approvalSteps.forEach(function (step) {
            if (!isApprovalStepDirty && step.get('isDirty')) {
                isApprovalStepDirty = true;
            }
        });
    }

    return isProjectDirty || isFormDirty || isApprovalStepDirty;

  }.property('FLOW.projectControl.currentProject.isDirty',
              'FLOW.selectedControl.selectedSurvey.isDirty',
              'FLOW.router.approvalStepsController.content.@each.approverUserList'),

  projectView: function() {
    return FLOW.projectControl.isProject(FLOW.projectControl.get('currentProject'));
  }.property('FLOW.projectControl.currentProject'),

  projectListView: function() {
    return FLOW.projectControl.isProjectFolder(FLOW.projectControl.get('currentProject'));
  }.property('FLOW.projectControl.currentProject'),

  disableAddFolderButton: function() {
    var permissions = FLOW.projectControl.get('currentFolderPermissions');
    return permissions.indexOf("PROJECT_FOLDER_CREATE") < 0;
  }.property('FLOW.projectControl.currentProjectPath'),

  disableAddSurveyButtonInRoot: function() {
    return FLOW.projectControl.get('currentProjectPath').length == 0;
  }.property('FLOW.projectControl.currentProjectPath'),

  disableAddSurveyButton: function() {
    var permissions = FLOW.projectControl.get('currentFolderPermissions');
    return permissions.indexOf("PROJECT_FOLDER_CREATE") < 0;
  }.property('FLOW.projectControl.currentProjectPath'),
});


FLOW.ProjectList = FLOW.View.extend({
  tagName: 'ul',
  classNameBindings: ['classProperty'],
  classProperty: function() {
    return FLOW.projectControl.moveTarget || FLOW.projectControl.copyTarget ? 'actionProcess' : '';
  }.property('FLOW.projectControl.moveTarget', 'FLOW.projectControl.copyTarget')
});

FLOW.ProjectItemView = FLOW.View.extend({
  tagName: 'li',
  content: null,
  classNameBindings: ['classProperty'],
  folderEdit: false,

  classProperty: function() {
    var isFolder = FLOW.projectControl.isProjectFolder(this.content);
    var isFolderEmpty = FLOW.projectControl.isProjectFolderEmpty(this.content);
    var isMoving = this.content === FLOW.projectControl.get('moveTarget');
    var isCopying = this.content === FLOW.projectControl.get('copyTarget');

    var classes = "aSurvey";
    if (isFolder) classes += " aFolder";
    if (isFolderEmpty) classes += " folderEmpty";
    if (isMoving || isCopying) classes += " highLighted";
    if (FLOW.projectControl.get('newlyCreated') === this.get('content')) classes += " newlyCreated";

    return classes;
  }.property('FLOW.projectControl.moveTarget', 'FLOW.projectControl.copyTarget', 'FLOW.projectControl.currentProject'),

  toggleEditFolderName: function(evt) {
    this.set('folderEdit', !this.get('folderEdit'));
  },

  isFolder: function() {
    return FLOW.projectControl.isProjectFolder(this.content);
  }.property(),

  formatDate: function(datetime) {
    if (datetime === "") return "";
    var date = new Date(parseInt(datetime, 10));
    return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
  },

  created: function() {
    return this.formatDate(this.content.get('createdDateTime'));
  }.property('this.content.createdDateTime'),

  modified: function() {
    return this.formatDate(this.content.get('lastUpdateDateTime'));
  }.property('this.content.lastUpdateDateTime'),

  isPrivate: function() {
    return this.content.get('privacyLevel') === "PRIVATE";
  }.property(),

  language: function() {
    var langs = {en: "English", es: "Español", fr: "Français"};
    return langs[this.content.get('defaultLanguageCode')];
  }.property(),

  hideFolderSurveyDeleteButton: function () {
    var c = this.get('content');
    var permissions = FLOW.projectControl.get('currentFolderPermissions');
    return permissions.indexOf("PROJECT_FOLDER_DELETE") < 0 || !Ember.empty(c.get('surveyList'));
  }.property(),

  showSurveyEditButton: function() {
    var survey = this.get('content');
    return FLOW.permControl.canEditSurvey(survey);
  }.property(),

  showSurveyMoveButton: function() {
    var survey = this.get('content');
    return FLOW.permControl.canEditSurvey(survey);
  }.property(),

  showSurveyCopyButton: function () {
    var survey = this.get('content');
    return FLOW.permControl.canEditSurvey(survey);
  }.property()
});

FLOW.FolderEditView = Ember.TextField.extend({
  content: null,
  path: null,

  saveFolderName: function() {
    var name = this.content.get('code').trim();
    this.content.set('name', name);
    this.content.set('code', name);
    var path = FLOW.projectControl.get('currentProjectPath') + "/" + name;
    this.content.set('path', path);
    FLOW.store.commit();
  },

  focusOut: function() {
    this.get('parentView').set('folderEdit', false);
    this.saveFolderName();
  },

  insertNewline: function() {
    this.get('parentView').set('folderEdit', false);
  }
});

FLOW.FormTabView = Ember.View.extend({
  tagName: 'li',
  content: null,
  classNameBindings: ['classProperty'],

  classProperty: function() {

    var form = this.get('content');
    var currentProject = FLOW.projectControl.get('currentProject');
    var classString = 'aFormTab';

    if (form === null || currentProject === null) return classString;

    // Return "aFormTab" "current" and/or "registrationForm"
    var isActive = form === FLOW.selectedControl.get('selectedSurvey');
    var isRegistrationForm = currentProject.get('monitoringGroup') && form.get('keyId') === currentProject.get('newLocaleSurveyId');
    var isPublished = form.get('status') === 'PUBLISHED';

    if (isActive) classString += ' current';
    if (isRegistrationForm) classString += ' registrationForm';
    if (isPublished) classString += ' published'

    return classString;
  }.property('FLOW.selectedControl.selectedSurvey', 'FLOW.projectControl.currentProject.newLocaleSurveyId', 'content.status' ),
});
