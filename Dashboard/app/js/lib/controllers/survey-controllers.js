
FLOW.questionTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_free_text'),
      value: 'FREE_TEXT'
    }), Ember.Object.create({
      label: Ember.String.loc('_option'),
      value: 'OPTION'
    }), Ember.Object.create({
        label: Ember.String.loc('_cascade'),
     value: 'CASCADE'
      }),Ember.Object.create({
      label: Ember.String.loc('_number'),
      value: 'NUMBER'
    }), Ember.Object.create({
      label: Ember.String.loc('_gelocation'),
      value: 'GEO'
    }), Ember.Object.create({
      label: Ember.String.loc('_photo'),
      value: 'PHOTO'
    }), Ember.Object.create({
      label: Ember.String.loc('_video'),
      value: 'VIDEO'
    }), Ember.Object.create({
      label: Ember.String.loc('_date'),
      value: 'DATE'
    }), Ember.Object.create({
      label: Ember.String.loc('_barcode'),
      value: 'SCAN'
    }), Ember.Object.create({
      label: Ember.String.loc('_geoshape'),
      value: 'GEOSHAPE'
    }), Ember.Object.create({
      label: Ember.String.loc('_signature'),
      value: 'SIGNATURE'
    }), Ember.Object.create({
        label: Ember.String.loc('_caddisfly'),
        value: 'CADDISFLY'
      })
  ]
});


FLOW.notificationOptionControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_link'),
      value: "LINK"
    }), Ember.Object.create({
      label: "attachment",
      value: "ATTACHMENT"
    })
  ]
});

FLOW.notificationTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_email'),
      value: "EMAIL"
    })
  ]
});

FLOW.notificationEventControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_raw_data_reports_nightly'),
      value: "rawDataReport"
    }), Ember.Object.create({
      label: Ember.String.loc('_survey_submission'),
      value: "surveySubmission"
    }), Ember.Object.create({
      label: Ember.String.loc('_survey_approval'),
      value: "surveyApproval"
    })
  ]
});

FLOW.languageControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: "English",
      value: "en"
    }), Ember.Object.create({
      label: "Español",
      value: "es"
    }), Ember.Object.create({
      label: "Français",
      value: "fr"
    })
  ]
});

FLOW.surveyPointTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_point'),
      value: "Point"
    }), Ember.Object.create({
      label: Ember.String.loc('_household'),
      value: "Household"
    }), Ember.Object.create({
      label: Ember.String.loc('_public_institution'),
      value: "PublicInstitution"
    })
  ]
});

FLOW.surveySectorTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: "Water and Sanitation",
      value: "WASH"
    }), Ember.Object.create({
      label: "Education",
      value: "EDUC"
    }), Ember.Object.create({
      label: "Economic development",
      value: "ECONDEV"
    }), Ember.Object.create({
      label: "Health care",
      value: "HEALTH"
    }), Ember.Object.create({
      label: "IT and Communication",
      value: "ICT"
    }), Ember.Object.create({
      label: "Food security",
      value: "FOODSEC"
    }), Ember.Object.create({
      label: "Other",
      value: "OTHER"
    })
  ]
});

FLOW.privacyLevelControl = Ember.Object.create({
  content: ["PRIVATE", "PUBLIC"]
});

FLOW.alwaysTrue = function () {
  return true;
};

FLOW.surveyGroupControl = Ember.ArrayController.create({
  sortProperties: ['code'],
  sortAscending: true,
  content: null,

  setFilteredContent: function (f) {
    this.set('content', FLOW.store.filter(FLOW.SurveyGroup, f));
  },

  // load all Survey Groups
  populate: function (f) {
    var fn = (f && $.isFunction(f) && f) || FLOW.alwaysTrue;
    FLOW.store.find(FLOW.SurveyGroup);
    this.setFilteredContent(fn);
  },

  // checks if data store contains surveys within this survey group.
  // this is also checked server side.
  containsSurveys: function () {
    var surveys, sgId;
    surveys = FLOW.store.filter(FLOW.Survey, function (data) {
      sgId = FLOW.selectedControl.selectedSurveyGroup.get('id');
      if (data.get('surveyGroupId') == sgId) {
        return true;
      }
    });
    return surveys.get('content').length > 0;
  },

  deleteSurveyGroup: function (keyId) {
    var surveyGroup;
    surveyGroup = FLOW.store.find(FLOW.SurveyGroup, keyId);
    surveyGroup.deleteRecord();
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedSurveyGroup', null);
  },

  /* return all the ancestor paths of a given path string */
  ancestorPaths: function(pathString) {
    if(!pathString) {
        return [];
    }

    var ancestors = [];
    while(pathString) {
        ancestors.push(pathString);
        pathString = pathString.slice(0, pathString.lastIndexOf("/"));
    }
    ancestors.push("/"); // add the root level folder to ancestors list
    return ancestors;
  },

  /* retrieve a survey group based on its id and check based on its
  path whether or not a user is able to delete data in the group. Used
  for monitoring groups */
  userCanDeleteData: function(surveyGroupId) {
    var ancestorIds;
    var surveyGroups = FLOW.store.filter(FLOW.SurveyGroup, function(sg){
        return sg.get('keyId') === surveyGroupId;
    });

    if(surveyGroups && surveyGroups.get('firstObject')) {
        ancestorIds = surveyGroups.get('firstObject').get('ancestorIds');
        return FLOW.permControl.canDeleteData(ancestorIds);
    } else {
        return false; // need survey group and ancestorIds, otherwise prevent delete
    }
  },
});


/**
 * The root project folder is represented as null with the keyId null
 */
FLOW.projectControl = Ember.ArrayController.create({
  content: null,
  currentProject: null,
  moveTarget: null,
  isLoading: true,

  populate: function() {
    FLOW.store.find(FLOW.SurveyGroup);
    this.set('content', FLOW.store.filter(FLOW.SurveyGroup, function(p) {
      return true;
    }));
  },

  setCurrentProject: function(project) {
    this.set('currentProject', project);
    window.scrollTo(0,0);
  },

  /* return true if the given SurveyGroup's has the data cleaning permission
   * associated with it, or if one of the ancestors or descendants of the SurveyGroup
   * has data cleaning permission associated with it.  In the case of descendants we
   * return true in order to be able to browse to the descendant */
  dataCleaningEnabled: function(surveyGroup) {
    var permissions = FLOW.currentUser.get('pathPermissions');
    var keyedSurvey;

    for (var key in permissions) {
      if(permissions[key].indexOf("DATA_CLEANING") > -1){
        // check key against survey group
        if(surveyGroup.get('keyId') === +key) {
          return true;
        }

        // check ancestors to for matching permission from higher level in hierarchy
        var ancestorIds = surveyGroup.get('ancestorIds');
        if (ancestorIds === null) {
          return false;
        } else {
          for(var i = 0; i < ancestorIds.length; i++){
            if(ancestorIds[i] === +key) {
              return true;
            }
          }
        }

        // finally check for all descendents that may have surveyGroup.keyId in their
        // ancestor list otherwise will not be able to browse to them.
        keyedSurvey = FLOW.store.find(FLOW.SurveyGroup, key);
        if (keyedSurvey) {
          var keyedAncestorIds = keyedSurvey.get('ancestorIds');
          if (keyedAncestorIds === null) {
            return false;
          } else {
            for (var j = 0; j < keyedAncestorIds.length; j++) {
              if(keyedAncestorIds[j] === surveyGroup.get('keyId')) {
                return true;
              }
            }
          }
        }
      }
    }
    return false;
  },

  /* Computed properties */
  breadCrumbs: function() {
    var result = [];
    var currentProject = this.get('currentProject');
    if (currentProject === null) {
      // current project is root
      return [];
    }
    var id = currentProject.get('keyId');
    while(id !== null && id !== 0) {
      project = FLOW.store.find(FLOW.SurveyGroup, id);
      result.push(project);
      id = project.get('parentId');
    }
    return result.reverse();
  }.property('@each', 'currentProject'),

  currentFolders: function() {
    var self = this;
    var currentProject = this.get('currentProject');
    var parentId = currentProject ? currentProject.get('keyId') : 0;
    return this.get('content').filter(function(project) {
      return project.get('parentId') === parentId;
    }).sort(function(a, b) {
      if (self.isProjectFolder(a) && self.isProject(b)) {
        return -1;
      } else if (self.isProject(a) && self.isProjectFolder(b)) {
        return 1;
      } else {
        var aCode = a.get('code') || a.get('name');
        var bCode = b.get('code') || b.get('name');
        if (aCode === bCode) return 0;
        if (aCode === 'New survey' || aCode === 'New folder') return -1;
        if (bCode === 'New survey' || bCode === 'New folder') return 1;
        return aCode.localeCompare(bCode);
      }
    });
  }.property('@each', 'currentProject', 'moveTarget'),

  formCount: function() {
    return FLOW.surveyControl.content && FLOW.surveyControl.content.get('length') || 0;
  }.property('FLOW.surveyControl.content.@each'),

  questionCount: function () {
    var questions = FLOW.questionControl.filterContent;
    return questions && questions.get('length') || 0;
  }.property('FLOW.questionControl.filterContent.@each'),

  hasForms: function() {
    return this.get('formCount') > 0;
  }.property('this.formCount'),

  currentProjectPath: function() {
    var projectList = this.get('breadCrumbs');
    if(projectList.length === 0) {
        return ""; // root project folder
    } else {
        var path = "";
        for(i = 0; i < projectList.length; i++){
            path += "/" + projectList[i].get('name');
        }
        return path;
    }
  }.property('breadCrumbs'),

  currentFolderPermissions: function() {
      var currentFolder = this.get('currentProject');
      var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
      var folderPermissions = [];

      if (!currentUserPermissions) {
        return [];
      }

      // root folder
      if (!currentFolder) {
        if (currentUserPermissions[0]) {
          currentUserPermissions[0].forEach(function(item){
            folderPermissions.push(item);
          });
        }
        return folderPermissions;
      }

      // first check current object id
      if (currentFolder.get('keyId') in currentUserPermissions) {
        currentUserPermissions[currentFolder.get('keyId')].forEach(function(item){
          folderPermissions.push(item);
        });
      }

      var ancestorIds = currentFolder.get('ancestorIds');
      if (!ancestorIds) {
        return folderPermissions;
      }

      var i;
      for(i = 0; i < ancestorIds.length; i++){
        if (ancestorIds[i] in currentUserPermissions) {
          currentUserPermissions[ancestorIds[i]].forEach(function(item){
            folderPermissions.push(item);
          });
        }
      }

      return folderPermissions;
  }.property('currentProject'),

  /* Actions */
  selectProject: function(evt) {
    var project = evt.context;
    this.setCurrentProject(evt.context);

    // User is using the breadcrumb to navigate, we could have unsaved changes
    FLOW.store.commit();

    if (this.isProject(project)) {
        // load caddisfly resources if they are not loaded
        // and only when surveys are selected
        this.loadCaddisflyResources();

        // applies to project where data approval has
        // been previously set
        if (project.get('requireDataApproval')) {
            this.loadDataApprovalGroups();
        }

        FLOW.selectedControl.set('selectedSurveyGroup', project);
    }

    this.set('newlyCreated', null);
  },

  selectRootProject: function() {
    this.setCurrentProject(null);
  },

  /*
   * Load caddisfly resources if they are not already loaded
   */
  loadCaddisflyResources: function () {
      var caddisflyResources = FLOW.caddisflyResourceControl.get('content');
      if (Ember.empty(caddisflyResources)) {
          FLOW.caddisflyResourceControl.populate();
      }
  },

  /*
   * Load the data approval resources for this survey
   */
  loadDataApprovalGroups: function (survey) {
      var approvalGroups = FLOW.router.approvalGroupListController.get('content');
      if (Ember.empty(approvalGroups)) {
          FLOW.router.approvalGroupListController.load();
      }
  },

  /* Create a new project folder. The current project must be root or a project folder */
  createProjectFolder: function() {
    this.createNewProject(true);
  },

  createProject: function() {
    this.createNewProject(false);
  },

  createNewProject: function(folder) {
    var currentFolder = this.get('currentProject');
    var currentFolderId = currentFolder ? currentFolder.get('keyId') : 0;

    var name = folder ? Ember.String.loc('_new_folder').trim() : Ember.String.loc('_new_survey').trim();
    var projectType = folder ? "PROJECT_FOLDER" : "PROJECT";
    var path = this.get('currentProjectPath') + "/" + name;

    var newRecord = FLOW.store.createRecord(FLOW.SurveyGroup, {
      "code": name,
      "name": name,
      "path": path,
      "parentId": currentFolderId,
      "projectType": projectType
    });
    FLOW.store.commit();

    this.set('newlyCreated', newRecord);
  },

  deleteProject: function(evt) {
    var project = FLOW.store.find(FLOW.SurveyGroup, evt.context.get('keyId'));
    project.deleteRecord();
    FLOW.store.commit();
  },

  beginMoveProject: function(evt) {
    this.set('newlyCreated', null);
    this.set('moveTarget', evt.context);
    this.set('moveTargetType', this.isProjectFolder(evt.context) ? "folder" : "survey");
  },

  beginCopyProject: function(evt) {
    this.set('newlyCreated', null);
    this.set('copyTarget', evt.context);
  },

  cancelMoveProject: function(evt) {
    this.set('moveTarget', null);
  },

  cancelCopyProject: function(evt) {
    this.set('copyTarget', null);
  },

  endMoveProject: function(evt) {
    var newFolderId = this.get('currentProject') ? this.get('currentProject').get('keyId') : 0;
    var project = this.get('moveTarget');
    var path = this.get('currentProjectPath') + "/" + project.get('name');
    project.set('parentId', newFolderId);
    project.set('path', path);
    FLOW.store.commit();
    this.set('moveTarget', null);
  },

  endCopyProject: function(evt) {
    var currentFolder = this.get('currentProject');

    FLOW.store.findQuery(FLOW.Action, {
      action: 'copyProject',
      targetId: this.get('copyTarget').get('keyId'),
      folderId: currentFolder ? currentFolder.get('keyId') : 0,
    });

    FLOW.store.commit();

    this.set('showCopySurveyDialogBool', false);

    FLOW.dialogControl.set('activeAction', "ignore");
    FLOW.dialogControl.set('header', Ember.String.loc('_copying_survey'));
    FLOW.dialogControl.set('message', Ember.String.loc('_copying_published_text_'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);

    this.set('copyTarget', null);
  },

  /* Helper methods */
  isProjectFolder: function(project) {
    return project === null || project.get('projectType') === 'PROJECT_FOLDER';
  },

  isProject: function(project) {
    return !this.isProjectFolder(project);
  },

  isProjectFolderEmpty: function(folder) {
    var id = folder.get('keyId');
    var children = this.get('content').filter(function(project) {
      return project.get('parentId') === id;
    });
    return children.get('length') === 0;
  },

  /*
   * A computed property to enable editing and displaying
   * the selected approval group for a survey, as well as
   * loading the appropriate approval steps depending on
   * the selected approval group
   */
  dataApprovalGroup: function (key, value, previousValue) {
      var survey = this.get('currentProject');

      // setter
      if (arguments.length > 1 && survey) {
          survey.set('dataApprovalGroupId', value && value.get('keyId'));
      }

      // getter
      var approvalGroupId = survey && survey.get('dataApprovalGroupId');
      FLOW.router.approvalStepsController.loadByGroupId(approvalGroupId);

      var groups = FLOW.router.approvalGroupListController.get('content');
      return groups && groups.filterProperty('keyId', approvalGroupId).get('firstObject');
  }.property('this.currentProject.dataApprovalGroupId'),

  /*
   * Property to dynamically load the data approval controller
   * content when needed, and otherwise return the boolean
   * value corresponding to whether the current survey has
   * data approval enabled or not
   */
  requireDataApproval: function (key, value, previousValue) {
      // setter
      if (arguments.length > 1) {
          this.currentProject.set('requireDataApproval', value);
      }

      // getter
      return this.currentProject.get('requireDataApproval');
  }.property('this.currentProject'),
});


FLOW.surveyControl = Ember.ArrayController.create({
  content: null,
  publishedContent: null,
  sortProperties: ['name'],
  sortAscending: true,

  setPublishedContent: function () {
    var sgId;
    if (FLOW.selectedControl.get('selectedSurveyGroup') && FLOW.selectedControl.selectedSurveyGroup.get('keyId') > 0) {
      sgId = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
      this.set('publishedContent', Ember.ArrayController.create({
        sortProperties: this.get('sortProperties'),
        sortAscending: this.get('sortAscending'),
        content: FLOW.store.filter(FLOW.Survey, function (item) {
          return item.get('surveyGroupId') == sgId && item.get('status') == 'PUBLISHED';
        })
      }));
    } else {
      this.set('publishedContent', null);
    }
  }.observes('FLOW.selectedControl.selectedSurveyGroup'),

  populate: function () {

    var id;
    if (FLOW.selectedControl.get('selectedSurveyGroup')) {
      id = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
      // this content is actualy not used, the data ends up in the store
      // and is accessed through the filtered content above
      this.set('content', FLOW.store.findQuery(FLOW.Survey, {
        surveyGroupId: id
      }));

    } else {
      this.set('content', null);
    }
  }.observes('FLOW.selectedControl.selectedSurveyGroup'),

  selectFirstForm: function() {
    if (this.get('content') && this.content.get('isLoaded')) {
      var form = this.content.get('firstObject');
      if (form) {
        FLOW.selectedControl.set('selectedSurvey', form);
      }
    }
  }.observes('content.isLoaded'),

  refresh: function () {
	  var sg = FLOW.selectedControl.get('selectedSurveyGroup');
	  this.set('content', FLOW.store.filter(FLOW.Survey, function (item) {
		  return item.get('surveyGroupId') === sg.get('keyId');
	  }));
  },

  newLocale: function () {
	  var newLocaleId = FLOW.selectedControl && FLOW.selectedControl.selectedSurveyGroup && FLOW.selectedControl.selectedSurveyGroup.get('newLocaleSurveyId');
	  if(!this.get('content') || !this.get('content').get('isLoaded')) { return; }
	  this.set('newLocaleSurvey', this.find(function (item) { return item.get('keyId') === newLocaleId; }));
  }.observes('content.isLoaded'),

  publishSurvey: function () {
    var surveyId;
    surveyId = FLOW.selectedControl.selectedSurvey.get('keyId');
    FLOW.store.findQuery(FLOW.Action, {
      action: 'publishSurvey',
      surveyId: surveyId
    });

    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_publishing_survey'));
    FLOW.dialogControl.set('message', Ember.String.loc('_survey_published_text_'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  createForm: function() {
    var code = Ember.String.loc('_new_form').trim();
    var path = FLOW.projectControl.get('currentProjectPath') + "/" + code;
    FLOW.store.createRecord(FLOW.Survey, {
      "name": code,
      "code": code,
      "path": path,
      "defaultLanguageCode": "en",
      "requireApproval": false,
      "status": "NOT_PUBLISHED",
      "surveyGroupId": FLOW.selectedControl.selectedSurveyGroup.get('keyId'),
      "version":"1.0"
    });
    FLOW.projectControl.get('currentProject').set('deleteDisabled', true);
    FLOW.store.commit();
    this.refresh();
  },

  deleteForm: function() {
    var keyId = FLOW.selectedControl.selectedSurvey.get('keyId');
    var survey = FLOW.store.find(FLOW.Survey, keyId);
    if (FLOW.projectControl.get('formCount') === 1) {
      FLOW.projectControl.get('currentProject').set('surveyList', null);
      FLOW.projectControl.get('currentProject').set('deleteDisabled', false);
    }
    survey.deleteRecord();

    FLOW.store.commit();
    this.refresh();
  },

  showPreview: function() {
    FLOW.previewControl.set('showPreviewPopup', true);
  },

  selectForm: function(evt) {
    FLOW.selectedControl.set('selectedSurvey', evt.context);
    //  we don't allow copying or moving between forms
    FLOW.selectedControl.set('selectedForMoveQuestionGroup',null);
    FLOW.selectedControl.set('selectedForCopyQuestionGroup',null);
    FLOW.selectedControl.set('selectedForMoveQuestion',null);
    FLOW.selectedControl.set('selectedForCopyQuestion',null);
  },

  /* retrieve a survey and check based on its path whether the user
  is allowed to delete survey instances related to the survey */
  userCanDeleteData: function(surveyId) {
    var survey;
    this.get('content').forEach(function(item){
        if(item.get('keyId') === surveyId) {
            survey = item;
        }
    });

    if(survey && survey.get('path')) {
        return FLOW.permControl.canDeleteData(survey.get('path'));
    } else {
        return false; // need survey and survey path, otherwise prevent delete
    }
  },

  /* retrieve the list of permissions associated with the currently
    active form */
  currentFormPermissions: function() {
    var currentForm = FLOW.selectedControl.get('selectedSurvey');
    var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
    var formPermissions = [];

    if (!currentForm || !currentUserPermissions) {
      return [];
    }

    var ancestorIds = currentForm.get('ancestorIds');
    if (!ancestorIds) {
      return [];
    }

    var i;
    for(i = 0; i < ancestorIds.length; i++){
      if (ancestorIds[i] in currentUserPermissions) {
        currentUserPermissions[ancestorIds[i]].forEach(function(item){
          formPermissions.push(item);
        });
      }
    }

    return formPermissions;

  }.property('FLOW.selectedControl.selectedSurvey'),
});


FLOW.questionGroupControl = Ember.ArrayController.create({
  sortProperties: ['order'],
  sortAscending: true,
  content: null,

  setFilteredContent: function () {
    var sId;
    if (FLOW.selectedControl.get('selectedSurvey')) {
      if (!Ember.empty(FLOW.selectedControl.selectedSurvey.get('keyId'))) {
        sId = FLOW.selectedControl.selectedSurvey.get('keyId');
        this.set('content', FLOW.store.filter(FLOW.QuestionGroup, function (item) {
          return item.get('surveyId') == sId;
        }));
      } else {
        // this happens when we have created a new survey, which has no id yet
        this.set('content', null);
      }
    }
  },

  populate: function () {
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      var id = FLOW.selectedControl.selectedSurvey.get('keyId');
      FLOW.store.findQuery(FLOW.QuestionGroup, {
        surveyId: id
      });
    }
    this.setFilteredContent();
  }.observes('FLOW.selectedControl.selectedSurvey'),

  getQuestionGroup: function (id) {
	  FLOW.store.findQuery(FLOW.QuestionGroup,{
		  questionGroupId: id
	  });
  },

  // true if all items have been saved
  // used in models.js
  allRecordsSaved: function () {
    var allSaved = true;
    if (Ember.none(this.get('content'))) {
      return true;
    } else {
      this.get('content').forEach(function (item) {
        if (item.get('isSaving')) {
          allSaved = false;
        }
      });
      return allSaved;
    }
  }.property('content.@each.isSaving'),

  // execute group delete
  deleteQuestionGroup: function (questionGroupId) {
    var questionGroup, questionsGroupsInSurvey, sId, qgOrder;
    sId = FLOW.selectedControl.selectedSurvey.get('keyId');
    questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);
    qgOrder = questionGroup.get('order');

    questionGroup.deleteRecord();

    // restore order of remaining groups
    questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, function (item) {
      return item.get('surveyId') == sId;
    });

    // restore order
    questionGroupsInSurvey.forEach(function (item) {
      if (item.get('order') > qgOrder) {
        item.set('order', item.get('order') - 1);
      }
    });

    // restore order in case the order has gone haywire
    FLOW.questionControl.restoreOrder(questionGroupsInSurvey);
    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
  }
});


FLOW.questionControl = Ember.ArrayController.create({
  content: null,
  OPTIONcontent: null,
  earlierOptionQuestions: null,
  QGcontent: null,
  filterContent: null,
  sortProperties: ['order'],
  sortAscending: true,
  preflightQId: null,

  populateAllQuestions: function () {
    var sId;
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.Question, {
        surveyId: sId
      }));
    }
  }.observes('FLOW.selectedControl.selectedSurvey'),

  populateQuestionGroupQuestions: function (qgId) {
        this.set('content', FLOW.store.findQuery(FLOW.Question, {
          questionGroupId: qgId
        }));
    },

  // used for surveyInstances in data edit popup
  doSurveyIdQuery: function (surveyId) {
    this.set('content', FLOW.store.findQuery(FLOW.Question, {
      surveyId: surveyId
    }));
  },

  restoreOrder: function (groups) {
    var temp, i;
    // sort them and renumber them according to logical numbering
    temp = groups.toArray();
    temp.sort(function(a,b) {
    	return a.get('order') - b.get('order');
    });
    i = 1;
    temp.forEach(function(item){
      item.set('order',i);
      i++;
    });
  },

  deleteQuestion: function (questionId) {
    qgId = this.content.get('questionGroupId');
    question = FLOW.store.find(FLOW.Question, questionId);
    qgId = question.get('questionGroupId');
    qOrder = question.get('order');
    question.deleteRecord();

    // restore order
    questionsInGroup = FLOW.store.filter(FLOW.Question, function (item) {
      return item.get('questionGroupId') == qgId;
    });

    questionsInGroup.forEach(function (item) {
      if (item.get('order') > qOrder) {
        item.set('order', item.get('order') - 1);
      }
    });
    // restore order in case the order has gone haywire
    this.restoreOrder(questionsInGroup);
    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
  },

  allQuestionsFilter: function () {
    var sId;
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('filterContent', FLOW.store.filter(FLOW.Question, function (item) {
        return item.get('surveyId') == sId;
      }));
    } else {
      this.set('filterContent', null);
    }
  }.observes('FLOW.selectedControl.selectedSurvey'),

  setQGcontent: function () {
    if (FLOW.selectedControl.get('selectedQuestionGroup') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      var qId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      this.set('content', FLOW.store.filter(FLOW.Question, function (item) {
        return item.get('questionGroupId') == qId;
      }));
    }
  }.observes('FLOW.selectedControl.selectedQuestionGroup'),

  geoshapeContent: function() {
    var selectedSurvey = FLOW.selectedControl.get('selectedSurvey');
    var surveyId = selectedSurvey ? selectedSurvey.get('keyId') : null;
    return FLOW.store.filter(FLOW.Question, function (question) {
      return question.get('type') === 'GEOSHAPE' && surveyId === question.get('surveyId');
    });
  }.property('content'),

  downloadOptionQuestions: function (surveyId) {
	  this.set('OPTIONcontent', FLOW.store.findQuery(FLOW.Question, {
	     surveyId: surveyId,
	      optionQuestionsOnly:true
	  }));
  },

  // used for display of dependencies: a question can only be dependent on earlier questions
  setEarlierOptionQuestions: function () {
    if (!Ember.none(FLOW.selectedControl.get('selectedQuestion')) && !Ember.none(FLOW.selectedControl.get('selectedQuestionGroup'))) {
      var optionQuestionList, sId, questionGroupOrder, qgOrder, qg, questionOrder, questionGroupId;
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      questionGroupId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      questionGroupOrder = FLOW.selectedControl.selectedQuestionGroup.get('order');
      questionOrder = FLOW.selectedControl.selectedQuestion.get('order');
      optionQuestionList = FLOW.store.filter(FLOW.Question, function (item) {
        qg = FLOW.store.find(FLOW.QuestionGroup, item.get('questionGroupId'));
        // no dependencies from non-repeat to repeat groups
        if (qg.get('keyId') != questionGroupId && qg.get('repeatable')) {
          return false;
        }
        qgOrder = qg.get('order');
        if (!(item.get('type') == 'OPTION' && item.get('surveyId') == sId)) return false;
        if (qgOrder > questionGroupOrder) {
          return false;
        }
        if (qgOrder < questionGroupOrder) {
          return true;
        }
        // when we arrive there qgOrder = questionGroupOrder, so we have to check question order
        return item.get('order') < questionOrder;
      });

      this.set('earlierOptionQuestions', optionQuestionList);
    }
  }.observes('FLOW.selectedControl.selectedQuestion'),



  // true if all items have been saved
  // used in models.js
  allRecordsSaved: function () {
    var allSaved = true;
    FLOW.questionControl.get('content').forEach(function (item) {
      if (item.get('isSaving')) {
        allSaved = false;
      }
    });
    return allSaved;
  }.property('content.@each.isSaving')
});

/*
 *  Note: This controller is for the option list for a question's dependencies
 */
FLOW.optionListControl = Ember.ArrayController.create({
  content: []
});

/*
 *  Controller for the list of options attached to an option question
 *
 */
FLOW.questionOptionsControl = Ember.ArrayController.create({
  content: null,
  questionId: null,

  /*
   *  Add two empty option objects to the options list.  This is used
   *  as a default setup for new option questions
   */
  loadDefaultOptions: function () {
    var c = this.content, defaultLength = 2;
    if (c && c.get('length') === 0) {
      while (defaultLength > 0) {
        c.addObject(Ember.Object.create({
          code: null,
          text: null,
          order: c.get('length') + 1,
          questionId: this.get('questionId'),
        }));
        defaultLength--;
      }
    }
  },

  /*
   *  Add a new option object to the content of this controller.  The object
   *  is not persisted to the data store.
   */
  addOption: function() {
    var c = this.content;
    c.addObject(Ember.Object.create({
        code: null,
        text: null,
        order: c.get('length') + 1,
        questionId: this.get('questionId'),
    }));
  },

  /*
   *  Persist all the newly added options to the data store.
   *  Options with empty code and empty text fields are dropped
   *  from the list.  If they were already persisted in the datastore
   *  they are deleted
   *
   */
  persistOptions: function () {
    var options = this.content, blankOptions = [];
    // remove blank options
    options.forEach(function (option) {
      var code = option.get('code') && option.get('code').trim();
      var text = option.get('text') && option.get('text').trim();
      if (!code && !text) {
        blankOptions.push(option);
        if (option.get('keyId')) {
          option.deleteRecord();
        }
      }
    });
    options.removeObjects(blankOptions);

    // reset ordering and persist
    options.forEach(function (option, index) {
      var code = option.get('code') && option.get('code').trim();
      var text = option.get('text') && option.get('text').trim();
      if (!code) {
        option.set('code', null); // do not send empty string as code
      } else {
        option.set('code', code);
      }

      // trimmed whitespace
      option.set('text', text);
      option.set('order', index);
      if (!option.get('keyId')) {
        FLOW.store.createRecord(FLOW.QuestionOption, option);
      }
    });
  },

  /*
   *  Remove an option from the list of options.
   *
   */
  deleteOption: function(event) {
    var c = this.content, option = event.view.content;
    c.removeObject(option);

    if (option.get('keyId')) { // clear persisted versions
      option.deleteRecord();
    }
  },

  /*
   *  Validate all code options and if there is invalid input
   *  return an error message.  Valid input returns null
   */
  validateOptions: function () {
    var options = this.content, error;

    if (!options) {
      return null;
    }

    error = this.validateAllTextFilled();
    if (error && error.trim().length > 0) {
      return Ember.String.htmlSafe(error);
    }

    error = this.validateAllCodesFilled();
    if (error && error.trim().length > 0) {
      return Ember.String.htmlSafe(error);
    }

    error = this.validateDuplicateCodes();
    if (error && error.trim().length > 0) {
      return Ember.String.htmlSafe(error);
    }

    error = this.validateDuplicateText();
    if (error && error.trim().length > 0) {
      return Ember.String.htmlSafe(error);
    }

    error = this.validateDisallowedCharacters();
    if (error && error.trim().length > 0) {
      return Ember.String.htmlSafe(error);
    }
    return null;
  },

  /*
   *  Return an error string of any text options are left blank
   */
  validateAllTextFilled: function () {
    var options = this.content, error = '';

    options.forEach(function (option) {
      // only take into account options with no text but with text filled in
      if (!option.get('text') || option.get('text').trim().length === 0) {
        if(option.get('code') && option.get('code').trim()) {
          error += "<li>" + option.get('code').trim() + "</li>"
        }
      }
    });

    if (error) {
      error = '<ul>' + error + '</ul>';
      error = Ember.String.loc('_missing_option_text') + "\n" + error;
      return error;
    }
    return null;
  },

  /*
   * Return an error string if codes are partially filled in
   */
  validateAllCodesFilled: function () {
    var options = this.content, error = '', hasCodes;

    options.forEach(function (option) {
      // only take into account options with text to be able to give error dialog
      if (option.get('text') && option.get('text').trim()) {
        if(option.get('code') && option.get('code').trim()) {
          hasCodes = true;
        } else {
          error += "<li>" + option.get('text').trim() + "</li>"
        }
      }
    });

    if (hasCodes && error) {
      error = '<ul>' + error + '</ul>';
      error = Ember.String.loc('_missing_option_codes') + "\n" + error;
      return error;
    }
    return null;
  },

  /*
   *  Check for duplicate codes in the created options
   */
  validateDuplicateCodes: function () {
    var options = this.content, error = '';

    var uniqCodes = [];
    options.forEach(function (option) {
      if (option.get('code') && option.get('code').trim()){
        if(uniqCodes.indexOf(option.get('code').trim()) > -1) {
          error += '<li>' + option.get('code').trim() + '</li>'
        } else {
          uniqCodes.push(option.get('code').trim());
        }
      }
    });

    if (error) {
      error = '<ul>' + error + '</ul>';
      error = Ember.String.loc('_duplicate_option_codes') + "\n" + error;
      return error;
    }

    return null;
  },

  /*
   *  Check for duplicate texts in the created options
   */
  validateDuplicateText: function () {
    var options = this.content, error = '';

    var uniqText = [];
    options.forEach(function (option) {
      if (option.get('text') && option.get('text').trim()){
        if(uniqText.indexOf(option.get('text').trim()) > -1) {
          error += '<li>' + option.get('text').trim() + '</li>'
        } else {
          uniqText.push(option.get('text').trim());
        }
      }
    });

    if (error) {
      error = '<ul>' + error + '</ul>';
      error = Ember.String.loc('_duplicate_option_text') + "\n" + error;
      return error;
    }

    return null;
  },

  /*
   *  Check for disallowed xters in option codes
   */
  validateDisallowedCharacters: function () {
    var options = this.content, error = '';

    var reservedCode = [];
    options.forEach(function (option) {
      if (option.get('code') && option.get('code').trim()){
        if(!option.get('code').trim().match(/^[A-Za-z0-9_\-]*$/)) {
          error += '<li>' + option.get('code').trim() + '</li>'
        }

        if (option.get('code').trim() === "OTHER") {
          reservedCode.push(option.get('code').trim());
        }
      }
    });

    if (error) {
      error = '<ul>' + error + '</ul>';
      error = Ember.String.loc('_disallowed_xters_in_code') + "\n" + error;
      return error;
    }

    if (reservedCode.length) {
      error = Ember.String.loc('_reserved_code');
      return error;
    }

    return null;
  },
});

FLOW.previewControl = Ember.ArrayController.create({
  changed: false,
  showPreviewPopup: false,
  // associative array for answers in the preview
  answers: {}
});


FLOW.notificationControl = Ember.ArrayController.create({
  content: null,
  filterContent: null,
  sortProperties: ['notificationDestination'],
  sortAscending: true,

  populate: function () {
    var id;
    if (FLOW.selectedControl.get('selectedSurvey')) {
      id = FLOW.selectedControl.selectedSurvey.get('keyId');
      FLOW.store.findQuery(FLOW.NotificationSubscription, {
        surveyId: id
      });
    }
  },

  doFilterContent: function () {
    var sId;
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('content', FLOW.store.filter(FLOW.NotificationSubscription, function (item) {
        return item.get('entityId') == sId;
      }));
    }
  }.observes('FLOW.selectedControl.selectedSurvey')
});


FLOW.translationControl = Ember.ArrayController.create({
  itemArray: [],
  itemDict: {},
  translations: [],
  isoLangs: null,
  questionGroups: null,
  currentTranslation: null,
  currentTranslationName: null,
  defaultLang: null,
  selectedLanguage: null,
  newSelected: false,
  noCurrentTrans: true,
  toBeDeletedTranslations: [],
  firstLoad: true,

  init: function () {
    this._super();
    this.createIsoLangs();
  },

  createIsoLangs: function () {
    var tempArray = [];
    for (var key in FLOW.isoLanguagesDict) {
      tempArray.push(Ember.Object.create({
        value: key,
        labelShort: FLOW.isoLanguagesDict[key].nativeName,
        labelLong: FLOW.isoLanguagesDict[key].nativeName + " - " + FLOW.isoLanguagesDict[key].name
      }));
    }
    this.set('isoLangs', tempArray);
  },

  blockInteraction: function () {
    return this.get('noCurrentTrans') || this.get('newSelected');
  }.property('noCurrentTrans', 'newSelected'),

  populate: function () {
    var id, questionGroupId, questionGroup;
    id = FLOW.selectedControl.selectedSurvey.get('keyId');
    questionGroupId = FLOW.questionGroupControl.get('arrangedContent')[0].get('keyId');
    questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);

    if (!Ember.none(questionGroup)){
    	FLOW.selectedControl.set('selectedQuestionGroup',questionGroup);
    }

    if (!Ember.none(id) && !Ember.none(questionGroupId)) {
      this.set('content', FLOW.store.findQuery(FLOW.Translation, {
        surveyId: id,
        questionGroupId: questionGroupId
      }));
      this.set('translations', []);
      this.set('newSelected', false);
      this.set('noCurrentTrans', true);
      this.set('selectedLanguage', null);
      this.set('currentTranslation', null);
      this.set('currentTranslationName', null);

      // this creates the internal structure that we use to display all the items for translation
      // the translation items are put in here when they arrive from the backend
      this.createItemList(id, questionGroupId);
      this.set('defaultLang', FLOW.isoLanguagesDict[FLOW.selectedControl.selectedSurvey.get('defaultLanguageCode')].name);
      this.set('firstLoad', true);
    }
  },

  loadQuestionGroup: function (questionGroupId) {
        var id;
	    id = FLOW.selectedControl.selectedSurvey.get('keyId');
	    questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);

	    if (!Ember.none(questionGroup)){
	    	FLOW.selectedControl.set('selectedQuestionGroup',questionGroup);
	    }

	    if (!Ember.none(id)) {}
	    this.set('content', FLOW.store.findQuery(FLOW.Translation, {
	      surveyId: id,
	      questionGroupId: questionGroupId
	    }));
	    this.set('firstLoad', false);

	    // this creates the internal structure that we use to display all the items for translation
	    // the translation items are put in here when they arrive from the backend
	    this.createItemList(id, questionGroupId);
  },

  //when the translations arrive, put them in the internal data structure
  initiateData: function () {
    if (this.get('firstLoad')){
	  if (this.get('content').content.length > 0) {
        this.determineAvailableTranslations();
        this.resetTranslationFields();
        if (this.get('translations').length > 0) {
          this.set('currentTranslation', this.get('translations')[0].value);
          this.set('currentTranslationName', this.get('translations')[0].label);
          this.putTranslationsInList();
          this.set('noCurrentTrans', false);
        } else {
          this.set('noCurrentTrans', true);
        }
      }
    } else {
      if (this.get('content').content.length > 0) {
    	  this.resetTranslationFields();
    	  this.putTranslationsInList();
      }
    }
  }.observes('content.isLoaded'),

  resetTranslationFields: function () {
    this.get('itemArray').forEach(function (item) {
      switch (item.get('type')) {
      case "S":
        item.set('surveyTextTrans', null);
        item.set('surveyTextTransId', null);
        item.set('sDescTextTrans', null);
        item.set('sDescTextTransId', null);
        break;

      case "QG":
        item.set('qgTextTrans', null);
        item.set('qgTextTransId', null);
        break;

      case "Q":
        item.set('qTextTrans', null);
        item.set('qTextTransId', null);
        item.set('qTipTextTrans', null);
        item.set('qTipTextTransId', null);
        break;

      case "QO":
        item.set('qoTextTrans', null);
        item.set('qoTextTransId', null);
        break;

      default:

      }
    });
  },

  // determine which languages are present in the translation objects,
  // so we can show the proper items
  determineAvailableTranslations: function () {
    var tempDict = {};
    this.get('content').forEach(function (item) {
      if (!Ember.none(item.get('langCode'))) {
        tempDict[item.get('langCode')] = item.get('langCode');
      }
    });
    for (var key in tempDict) this.translations.pushObject(Ember.Object.create({
      value: key,
      label: FLOW.isoLanguagesDict[key].name
    }));
  },

  cancelAddTranslation: function () {
    this.set('newSelected', false);
    this.set('selectedLanguage', null);
  },

  lockWhenNewLangChosen: function () {
    var selLang, found = false;
    if (!Ember.none(this.get('selectedLanguage'))) {
      if (FLOW.selectedControl.selectedSurvey.get('defaultLanguageCode') == this.selectedLanguage.get('value')) {
        // we can't select the same language as the default language
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_cant_select_lang'));
        FLOW.dialogControl.set('message', Ember.String.loc('_cant_select_lang_text'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
        this.set('selectedLanguage', null);
        this.set('newSelected', false);
        return;
      }
      selLang = this.selectedLanguage.get('value');
      this.get('translations').forEach(function (item) {
        found = found || selLang == item.value;
      });

      if (found) {
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_trans_already_present'));
        FLOW.dialogControl.set('message', Ember.String.loc('_trans_already_present_text'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
        this.set('selectedLanguage', null);
        this.set('newSelected', false);
        return;
      }
      this.set('newSelected', true);
    }
  }.observes('this.selectedLanguage'),

  addTranslation: function () {
    var found = false,
      newLang = null;
    newLang = this.get('selectedLanguage');
    if (!Ember.none(newLang)) {
      this.get('translations').forEach(function (item) {
        found = found || (newLang.value == item.value);
      });
      if (!found) {
        this.resetTranslationFields();
        this.translations.pushObject(Ember.Object.create({
          value: this.get('selectedLanguage').value,
          label: FLOW.isoLanguagesDict[this.get('selectedLanguage').value].name
        }));
        this.set('currentTranslation', this.get('selectedLanguage').value);
        this.set('currentTranslationName', FLOW.isoLanguagesDict[this.get('selectedLanguage').value].name);
        this.set('newSelected', false);
        this.set('noCurrentTrans', false);
      }
    }
  },

  switchTranslation: function (event) {
    if (event.context.value != this.get('currentTranslation')) {
      this.saveTranslations();
      this.resetTranslationFields();
      this.set('currentTranslation', event.context.value);
      this.set('currentTranslationName', FLOW.isoLanguagesDict[event.context.value].name);
      this.set('noCurrentTrans', false);
      this.putTranslationsInList();
    }
  },

  createItemList: function (id, questionGroupId) {
    var tempArray, tempHashDict, questionGroup, qgOrder;
    tempArray = [];
    tempHashDict = {};

    // put in survey stuff
    survey = FLOW.selectedControl.get('selectedSurvey');
    tempArray.push(Ember.Object.create({
      keyId: survey.get('keyId'),
      type: "S",
      order: 0,
      surveyText: survey.get('name'),
      sDescText: survey.get('description'),
      isSurvey: true
    }));

    // put in question group
    questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);
    if (!Ember.none(questionGroup)){
      tempArray.push(Ember.Object.create({
        keyId: questionGroup.get('keyId'),
        type: "QG",
        order: 1000000 * parseInt(questionGroup.get('order'), 10),
        displayOrder: questionGroup.get('order'),
        qgText: questionGroup.get('name'),
        isQG: true
      }));
    }
    // put in questions
    questions = FLOW.store.filter(FLOW.Question, function (item) {
      return item.get('questionGroupId') == questionGroupId;
    });
    questions.forEach(function (item) {
      questionGroup = FLOW.store.find(FLOW.QuestionGroup, item.get('questionGroupId'));
      qgOrder = parseInt(questionGroup.get('order'), 10);
      qId = item.get('keyId');

      tempArray.push(Ember.Object.create({
        keyId: item.get('keyId'),
        type: "Q",
        order: 1000000 * qgOrder + 1000 * parseInt(item.get('order'), 10),
        qText: item.get('text'),
        displayOrder: item.get('order'),
        qTipText: item.get('tip'),
        isQ: true,
        hasTooltip: !Ember.empty(item.get('tip'))
      }));
      // for each question, put in question options
      options = FLOW.store.filter(FLOW.QuestionOption, function (optionItem) {
        return optionItem.get('questionId') == qId;
      });

      qOrder = parseInt(item.get('order'), 10);
      options.forEach(function (item) {
        tempArray.push(Ember.Object.create({
          keyId: item.get('keyId'),
          type: "QO",
          order: 1000000 * qgOrder + 1000 * qOrder + parseInt(item.get('order'), 10),
          displayOrder: item.get('order'),
          qoText: item.get('text'),
          isQO: true
        }));
      });
    });

    // put all the items in the right order
    tempArray.sort(function (a, b) {
    	return a.get('order') - b.get('order');
    });

    i = 0;
    tempArray.forEach(function (item) {
      tempHashDict[item.get('type') + item.get('keyId')] = i;
      i++;
    });

    this.set('itemDict', tempHashDict);
    this.set('itemArray', tempArray);
  },

  // if deleteItem is true, the translation information is deleted
  putSingleTranslationInList: function (parentType, parentId, text, keyId, deleteItem) {
    var existingItemPos, itemText, itemKeyId;
    if (deleteItem) {
      itemText = text;
      itemKeyId = null;
    } else {
      itemText = text;
      itemKeyId = keyId;
    }
    switch (parentType) {
    case "SURVEY_NAME":
      existingItemPos = this.get('itemDict')["S" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('surveyTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('surveyTextTransId', itemKeyId);
      }
      break;

    case "SURVEY_DESC":
      existingItemPos = this.get('itemDict')["S" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('sDescTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('sDescTextTransId', itemKeyId);
      }
      break;

    case "QUESTION_GROUP_NAME":
      existingItemPos = this.get('itemDict')["QG" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('qgTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('qgTextTransId', itemKeyId);
      }
      break;

    case "QUESTION_TEXT":
      existingItemPos = this.get('itemDict')["Q" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('qTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('qTextTransId', itemKeyId);
      }
      break;

    case "QUESTION_TIP":
      existingItemPos = this.get('itemDict')["Q" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('qTipTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('qTipTextTransId', itemKeyId);
      }
      break;

    case "QUESTION_OPTION":
      existingItemPos = this.get('itemDict')["QO" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('qoTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('qoTextTransId', itemKeyId);
      }
      break;

    default:
    }
  },


  putTranslationsInList: function () {
    var currLang, _self;
    _self = this;
    currTrans = this.get('currentTranslation');
    // only proceed if we have a language selected
    if (!Ember.none(currTrans)) {
      // get the translations with the right surveyId and the right language code
      translations = FLOW.store.filter(FLOW.Translation, function (item) {
        return (item.get('surveyId') == FLOW.selectedControl.selectedSurvey.get('keyId') && item.get('langCode') == currTrans);
      });
      translations.forEach(function (item) {
        _self.putSingleTranslationInList(item.get('parentType'), item.get('parentId'), item.get('text'), item.get('keyId'), false);
      });
    }
  },

  // delete a translation record by its Id. Committing is done in saveTranslations method
  deleteRecord: function (transId) {
    var candidates, existingTrans;
    candidates = FLOW.store.filter(FLOW.Translation, function (item) {
      return item.get('keyId') == transId;
    });

    if (candidates.get('content').length > 0) {
      existingTrans = candidates.objectAt(0);
      existingTrans.deleteRecord();
    }
  },

  createUpdateOrDeleteRecord: function (surveyId, questionGroupId, type, parentId, origText, translationText, lan, transId, allowSideEffects) {
	  var changed = false;
	  if (!Ember.none(origText) && origText.length > 0) {
      // we have an original text
      if (!Ember.none(translationText) && translationText.length > 0) {
        // we have a translation text
        if (Ember.none(transId)) {
          // we don't have an existing translation, so create it
        	changed = true;
        	if (allowSideEffects){
        	  FLOW.store.createRecord(FLOW.Translation, {
                parentType: type,
                parentId: parentId,
                surveyId: surveyId,
                questionGroupId: questionGroupId,
                text: translationText,
                langCode: lan
              });
            }
        } else {
          // we have an existing translation, so update it, if the text has changed
          candidates = FLOW.store.filter(FLOW.Translation, function (item) {
            return item.get('keyId') == transId;
          });

          if (candidates.get('content').length > 0) {
        	 existingTrans = candidates.objectAt(0);
        	 // if the existing translation is different from the existing one, update it
        	 if (existingTrans.get('text') != translationText){
        		 changed = true;
        		 if(allowSideEffects){
            		existingTrans.set('text', translationText);
            	 }
        	 }
          }
        }
      } else {
        // we don't have a translation text. If there is an existing translation, delete it
        if (!Ember.none(transId)) {
          // add this id to the list of to be deleted items
          changed = true;
          if (allowSideEffects){
        	  this.toBeDeletedTranslations.pushObject(transId);
          }
        }
      }
    } else {
      // we don't have an original text. If there is an existing translation, delete it
      if (!Ember.none(transId)) {
        // add this to the list of to be deleted items
        changed = true;
    	if (allowSideEffects){
    	  this.toBeDeletedTranslations.pushObject(transId);
        }
      }
    }
	return changed;
  },

  // checks if unsaved translations are present, and if so, emits a warning
  unsavedTranslations: function () {
	  var type, parentId, lan, transId, _self, unsaved;
	  _self = this;
	  unsaved = false;
	  this.get('itemArray').forEach(function (item) {
		  type = item.type;
	      parentId = item.keyId;
	      surveyId = FLOW.selectedControl.selectedSurvey.get('keyId');
        if (!Ember.none(FLOW.selectedControl.get('selectedQuestionGroup'))) {
          questionGroupId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
        } else {
          questionGroupId = null;
        }
	      lan = _self.get('currentTranslation');
	      if (type == 'S') {
	        unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "SURVEY_NAME", parentId, item.surveyText, item.surveyTextTrans, lan, item.surveyTextTransId, false);
	        unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "SURVEY_DESC", parentId, item.sDescText, item.sDescTextTrans, lan, item.sDescTextTransId, false);
	      } else if (type == 'QG') {
	    	unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_GROUP_NAME", parentId, item.qgText, item.qgTextTrans, lan, item.qgTextTransId, false);
	      } else if (type == 'Q') {
	    	unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_TEXT", parentId, item.qText, item.qTextTrans, lan, item.qTextTransId, false);
	    	unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_TIP", parentId, item.qTipText, item.qTipTextTrans, lan, item.qTipTextTransId, false);
	      } else if (type == 'QO') {
	    	unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_OPTION", parentId, item.qoText, item.qoTextTrans, lan, item.qoTextTransId, false);
	      }
	  });
      if (unsaved){
    	FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_unsaved_translations_present'));
        FLOW.dialogControl.set('message', Ember.String.loc('_unsaved_translations_present_text'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
      }
      return unsaved;
  },

  // after saving is complete, records insert themselves back into the translation item list
  saveTranslations: function () {
    var type, parentId, lan, transId, _self;
    _self = this;
    FLOW.store.adapter.set('bulkCommit', true);
    this.get('itemArray').forEach(function (item) {
      type = item.type;
      parentId = item.keyId;
      surveyId = FLOW.selectedControl.selectedSurvey.get('keyId');
      if (!Ember.none(FLOW.selectedControl.get('selectedQuestionGroup'))) {
        questionGroupId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      } else {
        questionGroupId = null;
      }
      lan = _self.get('currentTranslation');
      if (type == 'S') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "SURVEY_NAME", parentId, item.surveyText, item.surveyTextTrans, lan, item.surveyTextTransId, true);
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "SURVEY_DESC", parentId, item.sDescText, item.sDescTextTrans, lan, item.sDescTextTransId, true);
      } else if (type == 'QG') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_GROUP_NAME", parentId, item.qgText, item.qgTextTrans, lan, item.qgTextTransId, true);
      } else if (type == 'Q') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_TEXT", parentId, item.qText, item.qTextTrans, lan, item.qTextTransId, true);
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_TIP", parentId, item.qTipText, item.qTipTextTrans, lan, item.qTipTextTransId, true);
      } else if (type == 'QO') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_OPTION", parentId, item.qoText, item.qoTextTrans, lan, item.qoTextTransId, true);
      }
    });
    FLOW.store.commit();
    FLOW.store.adapter.set('bulkCommit', false);

    // delete items individually, as a body in a DELETE request is not accepted by GAE
    this.get('toBeDeletedTranslations').forEach(function (item) {
      _self.deleteRecord(item);
    });

    // make survey unpublished
    survey = FLOW.store.find(FLOW.Survey,surveyId);
    if (!Ember.empty(survey)){
        survey.set('status','NOT_PUBLISHED');
    }
    FLOW.store.commit();
    this.set('toBeDeletedTranslations', []);
  }
});
