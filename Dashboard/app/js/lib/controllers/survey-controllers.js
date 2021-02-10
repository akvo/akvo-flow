import observe from '../mixins/observe';

FLOW.questionTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_free_text'),
      value: 'FREE_TEXT',
    }), Ember.Object.create({
      label: Ember.String.loc('_option'),
      value: 'OPTION',
    }), Ember.Object.create({
      label: Ember.String.loc('_cascade'),
      value: 'CASCADE',
    }), Ember.Object.create({
      label: Ember.String.loc('_number'),
      value: 'NUMBER',
    }), Ember.Object.create({
      label: Ember.String.loc('_gelocation'),
      value: 'GEO',
    }), Ember.Object.create({
      label: Ember.String.loc('_photo'),
      value: 'PHOTO',
    }), Ember.Object.create({
      label: Ember.String.loc('_video'),
      value: 'VIDEO',
    }), Ember.Object.create({
      label: Ember.String.loc('_date'),
      value: 'DATE',
    }), Ember.Object.create({
      label: Ember.String.loc('_barcode'),
      value: 'SCAN',
    }), Ember.Object.create({
      label: Ember.String.loc('_geoshape'),
      value: 'GEOSHAPE',
    }), Ember.Object.create({
      label: Ember.String.loc('_signature'),
      value: 'SIGNATURE',
    }), Ember.Object.create({
      label: Ember.String.loc('_caddisfly'),
      value: 'CADDISFLY',
    }),
  ],
});


FLOW.notificationOptionControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_link'),
      value: 'LINK',
    }), Ember.Object.create({
      label: 'attachment',
      value: 'ATTACHMENT',
    }),
  ],
});

FLOW.notificationTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_email'),
      value: 'EMAIL',
    }),
  ],
});

FLOW.notificationEventControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_raw_data_reports_nightly'),
      value: 'rawDataReport',
    }), Ember.Object.create({
      label: Ember.String.loc('_survey_submission'),
      value: 'surveySubmission',
    }), Ember.Object.create({
      label: Ember.String.loc('_survey_approval'),
      value: 'surveyApproval',
    }),
  ],
});

FLOW.languageControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: 'English',
      value: 'en',
    }), Ember.Object.create({
      label: 'Español',
      value: 'es',
    }), Ember.Object.create({
      label: 'Français',
      value: 'fr',
    }),
  ],
});

FLOW.alwaysTrue = function () {
  return true;
};

FLOW.surveyGroupControl = Ember.ArrayController.create({
  sortProperties: ['code'],
  sortAscending: true,
  content: null,

  setFilteredContent(f) {
    this.set('content', FLOW.store.filter(FLOW.SurveyGroup, f));
  },

  // load all Survey Groups
  populate(f) {
    const fn = (f && $.isFunction(f) && f) || FLOW.alwaysTrue;
    FLOW.store.find(FLOW.SurveyGroup);
    this.setFilteredContent(fn);
  },

  // checks if data store contains surveys within this survey group.
  // this is also checked server side.
  containsSurveys() {
    const surveys = FLOW.store.filter(FLOW.Survey, (data) => {
      const sgId = FLOW.selectedControl.selectedSurveyGroup.get('id');
      if (data.get('surveyGroupId') == sgId) {
        return true;
      }
    });
    return surveys.get('content').length > 0;
  },

  deleteSurveyGroup(keyId) {
    const surveyGroup = FLOW.store.find(FLOW.SurveyGroup, keyId);
    surveyGroup.deleteRecord();
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedSurveyGroup', null);
  },

  /* return all the ancestor paths of a given path string */
  ancestorPaths(pathString) {
    if (!pathString) {
      return [];
    }

    const ancestors = [];
    while (pathString) {
      ancestors.push(pathString);
      pathString = pathString.slice(0, pathString.lastIndexOf('/'));
    }
    ancestors.push('/'); // add the root level folder to ancestors list
    return ancestors;
  },

  /* retrieve a survey group based on its id and check based on its
  path whether or not a user is able to delete data in the group. Used
  for monitoring groups */
  userCanDeleteData(surveyGroupId) {
    const surveyGroups = FLOW.store.filter(FLOW.SurveyGroup, sg => sg.get('keyId') === surveyGroupId);

    if (surveyGroups && surveyGroups.get('firstObject')) {
      const ancestorIds = surveyGroups.get('firstObject').get('ancestorIds');
      return FLOW.permControl.canDeleteData(ancestorIds);
    }
    return false; // need survey group and ancestorIds, otherwise prevent delete
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

  populate() {
    FLOW.store.find(FLOW.SurveyGroup);
    this.set('content', FLOW.store.filter(FLOW.SurveyGroup, () => true));
  },

  setCurrentProject(project) {
    this.set('currentProject', project);
    FLOW.selectedControl.set('publishingErrors', null);
    window.scrollTo(0, 0);
  },

  /* return true if the given SurveyGroup's has the data cleaning permission
   * associated with it, or if one of the ancestors or descendants of the SurveyGroup
   * has data cleaning permission associated with it.  In the case of descendants we
   * return true in order to be able to browse to the descendant */
  dataCleaningEnabled(surveyGroup) {
    const permissions = FLOW.currentUser.get('pathPermissions');

    const keys = Object.keys(permissions);
    for (let i = 0; i < keys.length; i++) {
      if (permissions[keys[i]].indexOf('DATA_CLEANING') > -1) {
        // check key against survey group
        if (surveyGroup.get('keyId') === +keys[i]) {
          return true;
        }

        // check ancestors to for matching permission from higher level in hierarchy
        const ancestorIds = surveyGroup.get('ancestorIds');
        if (ancestorIds === null) {
          return false;
        }
        for (let j = 0; j < ancestorIds.length; j++) {
          if (ancestorIds[j] === +keys[i]) {
            return true;
          }
        }


        // finally check for all descendents that may have surveyGroup.keyId in their
        // ancestor list otherwise will not be able to browse to them.
        const keyedSurvey = FLOW.store.find(FLOW.SurveyGroup, keys[i]);
        if (keyedSurvey) {
          const keyedAncestorIds = keyedSurvey.get('ancestorIds');
          if (keyedAncestorIds === null) {
            return false;
          }
          for (let j = 0; j < keyedAncestorIds.length; j++) {
            if (keyedAncestorIds[j] === surveyGroup.get('keyId')) {
              return true;
            }
          }
        }
      }
    }

    return false;
  },

  /* Computed properties */
  breadCrumbs: Ember.computed(function () {
    const result = [];
    const currentProject = this.get('currentProject');
    if (currentProject === null) {
      // current project is root
      return [];
    }
    let project;
    let id = currentProject.get('keyId');
    while (id !== null && id !== 0) {
      project = FLOW.store.find(FLOW.SurveyGroup, id);
      result.push(project);
      id = project.get('parentId');
    }
    return result.reverse();
  }).property('@each', 'currentProject'),

  currentFolders: Ember.computed(function () {
    const self = this;
    const currentProject = this.get('currentProject');
    const parentId = currentProject ? currentProject.get('keyId') : 0;
    return this.get('content').filter(project => project.get('parentId') === parentId).sort((a, b) => {
      if (self.isProjectFolder(a) && self.isProject(b)) {
        return -1;
      } if (self.isProject(a) && self.isProjectFolder(b)) {
        return 1;
      }
      const aCode = a.get('code') || a.get('name');
      const bCode = b.get('code') || b.get('name');
      if (aCode === bCode) return 0;
      if (aCode === 'New survey' || aCode === 'New folder') return -1;
      if (bCode === 'New survey' || bCode === 'New folder') return 1;
      return aCode.localeCompare(bCode);
    });
  }).property('@each', 'currentProject', 'moveTarget'),

  formCount: Ember.computed(() => (FLOW.surveyControl.content ? FLOW.surveyControl.content.get('length') : 0)).property('FLOW.surveyControl.content.@each'),

  questionCount: Ember.computed(() => {
    const questions = FLOW.questionControl.filterContent;
    return questions ? questions.get('length') : 0;
  }).property('FLOW.questionControl.filterContent.@each'),

  hasForms: Ember.computed(function () {
    return this.get('formCount') > 0;
  }).property('this.formCount'),

  currentProjectPath: Ember.computed(function () {
    const projectList = this.get('breadCrumbs');
    if (projectList.length === 0) {
      return ''; // root project folder
    }
    let path = '';
    for (let i = 0; i < projectList.length; i++) {
      path += `/${projectList[i].get('name')}`;
    }
    return path;
  }).property('breadCrumbs'),

  currentFolderPermissions: Ember.computed(function () {
    const currentFolder = this.get('currentProject');
    const currentUserPermissions = FLOW.currentUser.get('pathPermissions');
    const folderPermissions = [];

    if (!currentUserPermissions) {
      return [];
    }

    // root folder
    if (!currentFolder) {
      if (currentUserPermissions[0]) {
        currentUserPermissions[0].forEach((item) => {
          folderPermissions.push(item);
        });
      }
      return folderPermissions;
    }

    // first check current object id
    if (currentFolder.get('keyId') in currentUserPermissions) {
      currentUserPermissions[currentFolder.get('keyId')].forEach((item) => {
        folderPermissions.push(item);
      });
    }

    const ancestorIds = currentFolder.get('ancestorIds');
    if (!ancestorIds) {
      return folderPermissions;
    }

    for (let i = 0; i < ancestorIds.length; i++) {
      if (ancestorIds[i] in currentUserPermissions) {
        currentUserPermissions[ancestorIds[i]].forEach((item) => {
          folderPermissions.push(item);
        });
      }
    }

    return folderPermissions;
  }).property('currentProject'),

  /* Actions */
  selectProject(evt) {
    const project = evt.context;
    // the target should not be openable while being moved. Prevents moving it into itself.
    if (this.moveTarget == project) {
      return;
    }

    this.setCurrentProject(project);

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

  selectRootProject() {
    this.setCurrentProject(null);
  },

  /*
   * Load caddisfly resources if they are not already loaded
   */
  loadCaddisflyResources() {
    const caddisflyResourceController = FLOW.router.get('caddisflyResourceController');
    if (Ember.empty(caddisflyResourceController.get('content'))) {
      caddisflyResourceController.load();
    }
  },

  /*
   * Load the data approval resources for this survey
   */
  loadDataApprovalGroups() {
    const approvalGroups = FLOW.router.approvalGroupListController.get('content');
    if (Ember.empty(approvalGroups)) {
      FLOW.router.approvalGroupListController.load();
    }
  },

  /* Create a new project folder. The current project must be root or a project folder */
  createProjectFolder() {
    this.createNewProject(true);
  },

  createProject() {
    this.createNewProject(false);
  },

  displayTemplateList() {
    console.log(FLOW.Env.templateIds)
  },

  createNewProject(folder) {
    const currentFolder = this.get('currentProject');
    const currentFolderId = currentFolder ? currentFolder.get('keyId') : 0;

    const name = folder ? Ember.String.loc('_new_folder').trim() : Ember.String.loc('_new_survey').trim();
    const projectType = folder ? 'PROJECT_FOLDER' : 'PROJECT';
    const path = `${this.get('currentProjectPath')}/${name}`;

    const newRecord = FLOW.store.createRecord(FLOW.SurveyGroup, {
      code: name,
      name,
      path,
      parentId: currentFolderId,
      projectType,
    });
    FLOW.store.commit();

    this.set('newlyCreated', newRecord);
  },

  deleteProject(evt) {
    const project = FLOW.store.find(FLOW.SurveyGroup, evt.context.get('keyId'));
    project.deleteRecord();
    FLOW.store.commit();
  },

  /* start moving a folder. Confusingly, the target is what will move */
  beginMoveProject(evt) {
    this.set('newlyCreated', null);
    this.set('moveTarget', evt.context);
    this.set('moveTargetType', this.isProjectFolder(evt.context) ? 'folder' : 'survey');
  },

  beginCopyProject(evt) {
    this.set('newlyCreated', null);
    this.set('copyTarget', evt.context);
  },

  cancelMoveProject() {
    this.set('moveTarget', null);
  },

  cancelCopyProject() {
    this.set('copyTarget', null);
  },

  endMoveProject() {
    const newFolderId = this.get('currentProject') ? this.get('currentProject').get('keyId') : 0;
    const project = this.get('moveTarget');
    const path = `${this.get('currentProjectPath')}/${project.get('name')}`;
    project.set('parentId', newFolderId);
    project.set('path', path);
    FLOW.store.commit();
    this.set('moveTarget', null);
  },

  endCopyProject() {
    const currentFolder = this.get('currentProject');

    FLOW.store.findQuery(FLOW.Action, {
      action: 'copyProject',
      targetId: this.get('copyTarget').get('keyId'),
      folderId: currentFolder ? currentFolder.get('keyId') : 0,
    });

    FLOW.store.commit();

    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_copying_survey'));
    FLOW.dialogControl.set('message', Ember.String.loc('_copying_published_text_'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);

    this.set('copyTarget', null);
  },

  /* Helper methods */
  isProjectFolder(project) {
    return project === null || project.get('projectType') === 'PROJECT_FOLDER';
  },

  isProject(project) {
    return !this.isProjectFolder(project);
  },

  isProjectFolderEmpty(folder) {
    const id = folder.get('keyId');
    const children = this.get('content').filter(project => project.get('parentId') === id);
    return children.get('length') === 0;
  },

  /*
   * A computed property to enable editing and displaying
   * the selected approval group for a survey, as well as
   * loading the appropriate approval steps depending on
   * the selected approval group
   */
  dataApprovalGroup: Ember.computed(function (key, value) {
    const survey = this.get('currentProject');

    // setter
    if (arguments.length > 1 && survey) {
      survey.set('dataApprovalGroupId', value && value.get('keyId'));
    }

    // getter
    const approvalGroupId = survey && survey.get('dataApprovalGroupId');
    FLOW.router.approvalStepsController.loadByGroupId(approvalGroupId);

    const groups = FLOW.router.approvalGroupListController.get('content');
    return groups && groups.filterProperty('keyId', approvalGroupId).get('firstObject');
  }).property('this.currentProject.dataApprovalGroupId'),

  saveProject() {
    const currentProject = this.get('currentProject');
    const currentForm = FLOW.selectedControl.get('selectedSurvey');

    if (currentProject && currentProject.get('isDirty')) {
      const name = currentProject.get('name').trim();
      currentProject.set('name', name);
      currentProject.set('code', name);
      currentProject.set('path', this.get('currentProjectPath'));
    }

    if (currentForm && currentForm.get('isDirty')) {
      const name = currentForm.get('name').trim();
      currentForm.set('name', name);
      currentForm.set('code', name);
      const path = `${this.get('currentProjectPath')}/${name}`;
      currentForm.set('path', path);
      currentForm.set('status', 'NOT_PUBLISHED');
    }

    FLOW.store.commit();
  },
});


FLOW.surveyControl = Ember.ArrayController.create(observe({
  'FLOW.selectedControl.selectedSurveyGroup': [
    'setPublishedContent',
    'orderForms',
    'populate',
  ],
  'content.isLoaded': [
    'orderForms',
    'selectFirstForm',
    'newLocale',
  ],
}), {
  content: null,
  publishedContent: null,
  sortProperties: ['name'],
  sortAscending: true,
  orderedForms: null,
  webformId: null,

  setPublishedContent() {
    if (FLOW.selectedControl.get('selectedSurveyGroup') && FLOW.selectedControl.selectedSurveyGroup.get('keyId') > 0) {
      const sgId = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
      this.set('publishedContent', Ember.ArrayController.create({
        sortProperties: this.get('sortProperties'),
        sortAscending: this.get('sortAscending'),
        content: FLOW.store.filter(FLOW.Survey, item => item.get('surveyGroupId') == sgId && item.get('status') == 'PUBLISHED'),
      }));
    } else {
      this.set('publishedContent', null);
    }
  },

  orderForms() {
    if (FLOW.selectedControl.get('selectedSurveyGroup') && FLOW.selectedControl.selectedSurveyGroup.get('keyId') > 0) {
      const sgId = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
      const self = this;
      const forms = FLOW.store.filter(FLOW.Survey, item => item.get('surveyGroupId') == sgId);
      self.set('orderedForms', []);

      if (forms.get('length') > 1 && FLOW.selectedControl.selectedSurveyGroup.get('monitoringGroup')) {
        // find registration form if set
        let regFormId;
        const regForm = forms.find(item => item.get('keyId') == FLOW.selectedControl.selectedSurveyGroup.get('newLocaleSurveyId'));
        if (regForm) {
          regFormId = regForm.get('keyId');
        } else {
          regFormId = forms.get('firstObject').get('keyId'); // registration form not defined so assume first form is registration
        }

        self.orderedForms.push(forms.find(form => form.get('keyId') == regFormId));

        forms.filter(form => form.get('keyId') != regFormId).sort((a, b) => {
          const nameA = a.get('name').toUpperCase();
          const nameB = b.get('name').toUpperCase();
          if (nameA < nameB) {
            return -1;
          }
          if (nameA > nameB) {
            return 1;
          }
          return 0;
        }).forEach((form) => {
          self.orderedForms.push(form);
        });
      } else {
        self.orderedForms.push(forms.find(form => form.get('surveyGroupId') == sgId));
      }
    }
  },

  populateAll() {
    FLOW.store.find(FLOW.Survey);
  },

  populate() {
    if (FLOW.selectedControl.get('selectedSurveyGroup')) {
      const id = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
      // this content is actualy not used, the data ends up in the store
      // and is accessed through the filtered content above
      this.set('content', FLOW.store.findQuery(FLOW.Survey, {
        surveyGroupId: id,
      }));
    } else {
      this.set('content', null);
    }
  },

  selectFirstForm() {
    if (FLOW.selectedControl.selectedSurvey) return; // ignore if form is already selected
    if (this.get('content') && this.content.get('isLoaded')) {
      const form = this.content.get('firstObject');
      if (form) {
        FLOW.selectedControl.set('selectedSurvey', form);
      }
      this.viewDataForms();
    }
  },

  refresh() {
    const sg = FLOW.selectedControl.get('selectedSurveyGroup');
    this.set('content', FLOW.store.filter(FLOW.Survey, item => item.get('surveyGroupId') === sg.get('keyId')));
  },

  newLocale() {
    const newLocaleId = FLOW.selectedControl && FLOW.selectedControl.selectedSurveyGroup && FLOW.selectedControl.selectedSurveyGroup.get('newLocaleSurveyId');
    if (!this.get('content') || !this.get('content').get('isLoaded')) { return; }
    this.set('newLocaleSurvey', this.find(item => item.get('keyId') === newLocaleId));
  },

  validateSurveyToBePublished(surveyId) {
    if (!surveyId && FLOW.selectedControl.get('publishingErrors')) {
      const id = FLOW.selectedControl.selectedSurvey.get('keyId');
      const validationResult = FLOW.surveyControl.validateSurveyToBePublished(id);
      FLOW.selectedControl.set('publishingErrors', validationResult);
      return;
    }

    const questions = FLOW.store.filter(FLOW.Question, q => q.get('surveyId') == surveyId);

    const findQuestionOption = (questionId, text) => FLOW.store.filter(FLOW.QuestionOption, qo => qo.get('questionId') === questionId && qo.get('text') === text).map(x => x)[0];


    const checkQuestionAnswer = (q) => q.get('dependentQuestionAnswer').split('|')
          .map(v => findQuestionOption(q.get('dependentQuestionId'), v)).every(x => Boolean(x));

    const fQuestion = (l, id) => l.find(o => o.id == id);

    const dependentQuestionsNotFound = questions.filter(o => o.get('dependentFlag')).filter(o => !o.get('dependentQuestionId') || !fQuestion(questions, o.get('dependentQuestionId')) || !o.get('dependentQuestionAnswer') || !checkQuestionAnswer(o));

    const questionGroups = FLOW.store.filter(FLOW.QuestionGroup, qg => qg.get('surveyId') == surveyId);

    const questionGroupsNoName = questionGroups.filter(o => !o.get('name'));

    const data = dependentQuestionsNotFound.reduce(function (r, q) {
      r[q.get('questionGroupId')] = r[q.get('questionGroupId')] || [];
      r[q.get('questionGroupId')].push(q.get('keyId'));
        return r;
    }, Object.create(null));

    return questionGroupsNoName.reduce((c, o) => { if(!c[o.get('keyId')]) { c[o.get('keyId')] = [];} return c;} , data);
  },

  publishSurvey() {
    const surveyId = FLOW.selectedControl.selectedSurvey.get('keyId');
    const validationResult = this.validateSurveyToBePublished(surveyId);
    if(Object.keys(validationResult).length){
      FLOW.selectedControl.set('publishingErrors', validationResult);
      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', Ember.String.loc('_cannot_publish'));
      FLOW.dialogControl.set('message', Ember.String.loc('_publishing_questions_incomplete'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
    } else {
      FLOW.store.findQuery(FLOW.Action, {
        action: 'publishSurvey',
        surveyId,
      });

      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', Ember.String.loc('_publishing_survey'));
      FLOW.dialogControl.set('message', Ember.String.loc('_survey_published_text_'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
    }
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

  deleteForm() {
    const keyId = FLOW.selectedControl.selectedSurvey.get('keyId');
    const survey = FLOW.store.find(FLOW.Survey, keyId);
    if (FLOW.projectControl.get('formCount') === 1) {
      FLOW.projectControl.get('currentProject').set('surveyList', null);
      FLOW.projectControl.get('currentProject').set('deleteDisabled', false);
    }
    survey.deleteRecord();
    FLOW.selectedControl.set('selectedSurvey', null);

    FLOW.store.commit();
    this.refresh();
  },

  showPreview() {
    FLOW.previewControl.set('showPreviewPopup', true);
  },

  selectForm(evt) {
    FLOW.selectedControl.set('selectedSurvey', evt.context);
    //  we don't allow copying or moving between forms
    FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
    FLOW.selectedControl.set('selectedForMoveQuestion', null);
    FLOW.selectedControl.set('selectedForCopyQuestion', null);
  },

  /* retrieve a survey and check based on its path whether the user
  is allowed to delete survey instances related to the survey */
  userCanDeleteData(surveyId) {
    let survey;
    this.get('content').forEach((item) => {
      if (item.get('keyId') === surveyId) {
        survey = item;
      }
    });

    if (survey && survey.get('path')) {
      return FLOW.permControl.canDeleteData(survey.get('path'));
    }
    return false; // need survey and survey path, otherwise prevent delete
  },

  /* retrieve the list of permissions associated with the currently
    active form */
  currentFormPermissions: Ember.computed(() => {
    const currentForm = FLOW.selectedControl.get('selectedSurvey');
    const currentUserPermissions = FLOW.currentUser.get('pathPermissions');
    const formPermissions = [];

    if (!currentForm || !currentUserPermissions) {
      return [];
    }

    const ancestorIds = currentForm.get('ancestorIds');
    if (!ancestorIds) {
      return [];
    }

    for (let i = 0; i < ancestorIds.length; i++) {
      if (ancestorIds[i] in currentUserPermissions) {
        currentUserPermissions[ancestorIds[i]].forEach((item) => {
          formPermissions.push(item);
        });
      }
    }

    return formPermissions;
  }).property('FLOW.selectedControl.selectedSurvey'),

  viewDataForms() {
    const forms = [];

    this.content.forEach((item) => {
      if (FLOW.permControl.userCanViewData(item)) {
        forms.push(item);
      }
    });

    this.set('readDataContent', forms);

    if (forms.length === 0) {
      FLOW.selectedControl.set('selectedSurvey', null);
    }
  },

  webformUrl() {
    const currentForm = FLOW.selectedControl.get('selectedSurvey');
    const url = `${FLOW.store.adapter.buildURL('survey', currentForm.get('keyId'))}/webform_id`;

    $.ajax({
      url,
      success: (data) =>{
        this.set('webformId', data.webformId);
      },
      error: function(xhr) {
        console.error(xhr)
      },
    });
    },
});


FLOW.questionGroupControl = Ember.ArrayController.create(observe({
  'FLOW.selectedControl.selectedSurvey': 'populate',
}), {
  sortProperties: ['order'],
  sortAscending: true,
  content: null,

  setFilteredContent() {
    if (FLOW.selectedControl.get('selectedSurvey')) {
      if (!Ember.empty(FLOW.selectedControl.selectedSurvey.get('keyId'))) {
        const sId = FLOW.selectedControl.selectedSurvey.get('keyId');
        this.set('content', FLOW.store.filter(FLOW.QuestionGroup, item => item.get('surveyId') == sId));
      } else {
        // this happens when we have created a new survey, which has no id yet
        this.set('content', null);
      }
    }
  },

  populate() {
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      const id = FLOW.selectedControl.selectedSurvey.get('keyId');
      FLOW.store.findQuery(FLOW.QuestionGroup, {
        surveyId: id,
      });
    }
    this.setFilteredContent();
  },

  // true if all items have been saved
  // used in models.js
  allRecordsSaved: Ember.computed(function () {
    let allSaved = true;
    if (Ember.none(this.get('content'))) {
      return true;
    }
    this.get('content').forEach((item) => {
      if (item.get('isSaving')) {
        allSaved = false;
      }
    });
    return allSaved;
  }).property('content.@each.isSaving'),

  // execute group delete
  deleteQuestionGroup(questionGroupId) {
    const sId = FLOW.selectedControl.selectedSurvey.get('keyId');
    const questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);
    const qgOrder = questionGroup.get('order');

    questionGroup.deleteRecord();

    // reorder the rest of the question groups
    this.reorderQuestionGroups(sId, qgOrder, 'decrement');
    this.submitBulkQuestionGroupsReorder(sId);

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
  },

  reorderQuestionGroups(surveyId, reorderPoint, reorderOperation) {
    const questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, item => item.get('surveyId') == surveyId);

    // move items up to make space
    questionGroupsInSurvey.forEach((item) => {
      if (reorderOperation == 'increment') {
        if (item.get('order') > reorderPoint) {
          item.set('order', item.get('order') + 1);
        }
      } else if (reorderOperation == 'decrement') {
        if (item.get('order') > reorderPoint) {
          item.set('order', item.get('order') - 1);
        }
      }
    });
  },

  submitBulkQuestionGroupsReorder(surveyId) {
    FLOW.questionControl.set('bulkCommit', true);
    const questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, item => item.get('surveyId') == surveyId);
    // restore order in case the order has gone haywire
    FLOW.questionControl.restoreOrder(questionGroupsInSurvey);
    FLOW.store.commit();
    FLOW.store.adapter.set('bulkCommit', false);
    FLOW.questionControl.set('bulkCommit', false);
  },
});


FLOW.questionControl = Ember.ArrayController.create(observe({
  'FLOW.selectedControl.selectedSurvey': ['populateAllQuestions', 'allQuestionsFilter'],
  'FLOW.selectedControl.selectedQuestionGroup': 'setQGcontent',
  'FLOW.selectedControl.selectedQuestion': 'setEarlierOptionQuestions',
}), {
  content: null,
  OPTIONcontent: null,
  earlierOptionQuestions: null,
  QGcontent: null,
  filterContent: null,
  sortProperties: ['order'],
  sortAscending: true,
  preflightQId: null,
  bulkCommit: false,

  populateAllQuestions() {
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      const sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.Question, {
        surveyId: sId,
      }));
    }
  },

  populateQuestionGroupQuestions(qgId) {
    this.set('content', FLOW.store.findQuery(FLOW.Question, {
      questionGroupId: qgId,
    }));
  },

  // used for surveyInstances in data edit popup
  doSurveyIdQuery(surveyId) {
    this.set('content', FLOW.store.findQuery(FLOW.Question, {
      surveyId,
    }));
  },

  restoreOrder(groups) {
    let i = 1;
    // sort them and renumber them according to logical numbering
    const temp = groups.toArray();
    temp.sort((a, b) => a.get('order') - b.get('order'));
    temp.forEach((item) => {
      item.set('order', i);
      i++;
    });
  },

  deleteQuestion(questionId) {
    const question = FLOW.store.find(FLOW.Question, questionId);
    const qgId = question.get('questionGroupId');
    const qOrder = question.get('order');
    question.deleteRecord();

    // reorder the rest of the questions
    this.reorderQuestions(qgId, qOrder, 'decrement');
    this.submitBulkQuestionsReorder([qgId]);

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
  },

  allQuestionsFilter() {
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      const sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('filterContent', FLOW.store.filter(FLOW.Question, item => item.get('surveyId') == sId));
    } else {
      this.set('filterContent', null);
    }
  },

  setQGcontent() {
    if (FLOW.selectedControl.get('selectedQuestionGroup') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      const qId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      this.set('content', FLOW.store.filter(FLOW.Question, item => item.get('questionGroupId') == qId));
    }
  },

  geoshapeContent: Ember.computed(() => {
    const selectedSurvey = FLOW.selectedControl.get('selectedSurvey');
    const surveyId = selectedSurvey ? selectedSurvey.get('keyId') : null;
    return FLOW.store.filter(FLOW.Question, question => question.get('type') === 'GEOSHAPE' && surveyId === question.get('surveyId'));
  }).property('content'),

  downloadOptionQuestions(surveyId) {
    this.set('OPTIONcontent', FLOW.store.findQuery(FLOW.Question, {
      surveyId,
      optionQuestionsOnly: true,
    }));
  },

  // used for display of dependencies: a question can only be dependent on earlier questions
  setEarlierOptionQuestions() {
    if (!Ember.none(FLOW.selectedControl.get('selectedQuestion')) && !Ember.none(FLOW.selectedControl.get('selectedQuestionGroup'))) {
      const sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      const questionGroupId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      const questionGroupOrder = FLOW.selectedControl.selectedQuestionGroup.get('order');
      const questionOrder = FLOW.selectedControl.selectedQuestion.get('order');
      const optionQuestionList = FLOW.store.filter(FLOW.Question, (item) => {
        const qg = FLOW.store.find(FLOW.QuestionGroup, item.get('questionGroupId'));
        // no dependencies from non-repeat to repeat groups
        if (qg.get('keyId') != questionGroupId && qg.get('repeatable')) {
          return false;
        }
        const qgOrder = qg.get('order');
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
  },

  reorderQuestions(qgId, reorderPoint, reorderOperation) {
    const questionsInGroup = FLOW.store.filter(FLOW.Question, item => item.get('questionGroupId') == qgId);

    // move items up to make space
    questionsInGroup.forEach((item) => {
      if (reorderOperation == 'increment') {
        if (item.get('order') > reorderPoint) {
          item.set('order', item.get('order') + 1);
        }
      } else if (reorderOperation == 'decrement') {
        if (item.get('order') > reorderPoint) {
          item.set('order', item.get('order') - 1);
        }
      }
    });
  },

  submitBulkQuestionsReorder(qgIds) {
    this.set('bulkCommit', true);
    for (let i = 0; i < qgIds.length; i++) {
      const questionsInGroup = FLOW.store.filter(FLOW.Question, item => item.get('questionGroupId') == qgIds[i]);
      // restore order in case the order has gone haywire
      FLOW.questionControl.restoreOrder(questionsInGroup);
    }
    FLOW.store.commit();
    FLOW.store.adapter.set('bulkCommit', false);
    this.set('bulkCommit', false);
  },


  // true if all items have been saved
  // used in models.js
  allRecordsSaved: Ember.computed(() => {
    let allSaved = true;
    FLOW.questionControl.get('content').forEach((item) => {
      if (item.get('isSaving')) {
        allSaved = false;
      }
    });
    return allSaved;
  }).property('content.@each.isSaving'),
});

/*
 *  Note: This controller is for the option list for a question's dependencies
 */
FLOW.optionListControl = Ember.ArrayController.create({
  content: [],
});

/*
 *  Controller for the list of options attached to an option question
 *
 */
FLOW.questionOptionsControl = Ember.ArrayController.create({
  sortProperties: ['order'],
  content: null,
  questionId: null,
  emptyOptions: Ember.computed(function () {
    const c = this.content;
    return !(c.get('length') > 0);
  }).property('content.length'),

  duplicateOptionNames: Ember.computed(function () {
    const c = this.content;
    const uniqueText = [];
    let emptyText = 0;
    c.forEach((option) => {
      if (option.get('text') && option.get('text').trim()) {
        if (!(uniqueText.indexOf(option.get('text').trim()) > -1)) {
          uniqueText.push(option.get('text').trim());
        }
      } else {
        emptyText++;
      }
    });
    return c.length > uniqueText.length && emptyText === 0;
  }).property('@each.text', 'content.length'),

  emptyOptionNames: Ember.computed(function () {
    const c = this.content;
    let emptyText = 0;
    c.forEach((option) => {
      // only take into account options with no text but with code filled in
      if ((!option.get('text') || option.get('text').trim().length === 0) && option.get('code') && option.get('code').trim()) {
        emptyText++;
      }
    });
    return emptyText > 0;
  }).property('@each.text', 'content.length'),

  partialOptionCodes: Ember.computed(function () {
    const c = this.content;
    const codes = c.filter(option => option.get('code'));
    return codes.length && codes.length < c.length;
  }).property('@each.code', 'content.length'),

  duplicateOptionCodes: Ember.computed(function () {
    const c = this.content;
    const uniqueCode = [];
    let emptyCode = 0;
    c.forEach((option) => {
      if (option.get('code') && option.get('code').trim()) {
        if (!(uniqueCode.indexOf(option.get('code').trim()) > -1)) {
          uniqueCode.push(option.get('code').trim());
        }
      } else {
        emptyCode++;
      }
    });
    return c.length > uniqueCode.length && emptyCode === 0;
  }).property('@each.code', 'content.length'),

  disallowedCharacters: Ember.computed(function () {
    const c = this.content;
    let disallowedCharacters = 0;
    c.forEach((option) => {
      if (option.get('code') && option.get('code').trim()) {
        if (!option.get('code').trim().match(/^[A-Za-z0-9_-]*$/)) {
          disallowedCharacters++;
        }
      }
    });

    return disallowedCharacters > 0;
  }).property('@each.code', 'content.length'),

  reservedCode: Ember.computed(function () {
    const c = this.content;
    const reservedCode = [];
    c.forEach((option) => {
      if (option.get('code') && option.get('code').trim()) {
        if (option.get('code').trim() === 'OTHER') {
          reservedCode.push(option.get('code').trim());
        }
      }
    });

    return reservedCode.length > 0;
  }).property('@each.code', 'content.length'),

  maxLength: Ember.computed(function () {
    const c = this.content;
    const longXters = c.find((option) => {
      if (option.get('text') && option.get('text').trim()) {
        const optionText = option.get('text');
        return optionText.length > 500;
      }
      return false;
    });
    return longXters;
  }).property('@each.text', 'content.length'),

  /*
   *  Add two empty option objects to the options list.  This is used
   *  as a default setup for new option questions
   */
  loadDefaultOptions() {
    const c = this.content;
    let defaultLength = 2;
    if (c && c.get('length') === 0) {
      while (defaultLength > 0) {
        c.addObject(Ember.Object.create({
          code: null,
          text: Ember.String.loc('_new_option'),
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
  addOption() {
    const c = this.content;
    const currentLength = c.get('length');
    c.addObject(Ember.Object.create({
      code: null,
      text: Ember.String.loc('_new_option'),
      order: currentLength + 1,
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
  persistOptions() {
    const options = this.content;
    const blankOptions = [];
    // remove blank options
    options.forEach((option) => {
      const code = option.get('code') && option.get('code').trim();
      const text = option.get('text') && option.get('text').trim();
      if (!code && !text) {
        blankOptions.push(option);
        if (option.get('keyId')) {
          option.deleteRecord();
        }
      }
    });
    options.removeObjects(blankOptions);

    // reset ordering and persist
    options.forEach((option) => {
      const code = option.get('code') && option.get('code').trim();
      const text = option.get('text') && option.get('text').trim();
      if (!code) {
        option.set('code', null); // do not send empty string as code
      } else {
        option.set('code', code);
      }

      // trimmed whitespace
      option.set('text', text);

      if (!option.get('keyId')) {
        FLOW.store.createRecord(FLOW.QuestionOption, option);
      }
    });
  },

  /*
   *  Remove an option from the list of options.
   *
   */
  deleteOption(event) {
    const c = this.content;
    const option = event.view.content;
    c.removeObject(option);

    if (option.get('keyId')) { // clear persisted versions
      option.deleteRecord();
    }

    // reorder all options
    c.forEach((item, index) => {
      item.set('order', index + 1);
    });
  },

  moveOptionUp(event) {
    const options = this.content;
    const currentOption = event.view.content;

    if (currentOption && currentOption.get('order') > 0) {
      const previousOption = options.find(option => option.get('order') == (currentOption.get('order') - 1));
      const previousOptionOrder = previousOption.get('order');
      const currentOptionOrder = currentOption.get('order');
      previousOption.set('order', currentOptionOrder);
      currentOption.set('order', previousOptionOrder);
    }
  },

  moveOptionDown(event) {
    const options = this.content;
    const currentOption = event.view.content;

    if (currentOption && currentOption.get('order') < options.get('length')) {
      const nextOption = options.find(option => option.get('order') == (currentOption.get('order') + 1));
      const nextOptionOrder = nextOption.get('order');
      const currentOptionOrder = currentOption.get('order');
      nextOption.set('order', currentOptionOrder);
      currentOption.set('order', nextOptionOrder);
    }
  },
});

FLOW.previewControl = Ember.ArrayController.create({
  changed: false,
  showPreviewPopup: false,
  // associative array for answers in the preview
  answers: {},
});


FLOW.notificationControl = Ember.ArrayController.create(observe({
  'FLOW.selectedControl.selectedSurvey': 'doFilterContent',
}), {
  content: null,
  filterContent: null,
  sortProperties: ['notificationDestination'],
  sortAscending: true,

  populate() {
    let id;
    if (FLOW.selectedControl.get('selectedSurvey')) {
      id = FLOW.selectedControl.selectedSurvey.get('keyId');
      FLOW.store.findQuery(FLOW.NotificationSubscription, {
        surveyId: id,
      });
    }
  },

  doFilterContent() {
    let sId;
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('content', FLOW.store.filter(FLOW.NotificationSubscription, item => item.get('entityId') == sId));
    }
  },
});


FLOW.translationControl = Ember.ArrayController.create(observe({
  'content.isLoaded': 'initiateData',
  'this.selectedLanguage': 'lockWhenNewLangChosen',
}), {
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

  init() {
    this._super();
    this.createIsoLangs();
  },

  createIsoLangs() {
    const tempArray = [];
    const keys = Object.keys(FLOW.isoLanguagesDict);
    for (let i = 0; i < keys.length; i++) {
      tempArray.push(Ember.Object.create({
        value: keys[i],
        labelShort: FLOW.isoLanguagesDict[keys[i]].nativeName,
        labelLong: `${FLOW.isoLanguagesDict[keys[i]].nativeName} - ${FLOW.isoLanguagesDict[keys[i]].name}`,
      }));
    }
    this.set('isoLangs', tempArray);
  },

  blockInteraction: Ember.computed(function () {
    return this.get('noCurrentTrans') || this.get('newSelected');
  }).property('noCurrentTrans', 'newSelected'),

  populate() {
    const id = FLOW.selectedControl.selectedSurvey.get('keyId');
    const questionGroupId = FLOW.questionGroupControl.get('arrangedContent')[0].get('keyId');
    const questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);

    if (!Ember.none(questionGroup)) {
      FLOW.selectedControl.set('selectedQuestionGroup', questionGroup);
    }

    if (!Ember.none(id) && !Ember.none(questionGroupId)) {
      this.set('content', FLOW.store.findQuery(FLOW.Translation, {
        surveyId: id,
        questionGroupId,
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

  loadQuestionGroup(questionGroupId) {
    const id = FLOW.selectedControl.selectedSurvey.get('keyId');
    const questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);

    if (!Ember.none(questionGroup)) {
      FLOW.selectedControl.set('selectedQuestionGroup', questionGroup);
    }

    this.set('content', FLOW.store.findQuery(FLOW.Translation, {
      surveyId: id,
      questionGroupId,
    }));
    this.set('firstLoad', false);

    // this creates the internal structure that we use to display all the items for translation
    // the translation items are put in here when they arrive from the backend
    this.createItemList(id, questionGroupId);
  },

  // when the translations arrive, put them in the internal data structure
  initiateData() {
    if (this.get('firstLoad')) {
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
    } else if (this.get('content').content.length > 0) {
      this.resetTranslationFields();
      this.putTranslationsInList();
    }
  },

  resetTranslationFields() {
    this.get('itemArray').forEach((item) => {
      switch (item.get('type')) {
        case 'S':
          item.set('surveyTextTrans', null);
          item.set('surveyTextTransId', null);
          item.set('sDescTextTrans', null);
          item.set('sDescTextTransId', null);
          break;

        case 'QG':
          item.set('qgTextTrans', null);
          item.set('qgTextTransId', null);
          break;

        case 'Q':
          item.set('qTextTrans', null);
          item.set('qTextTransId', null);
          item.set('qTipTextTrans', null);
          item.set('qTipTextTransId', null);
          break;

        case 'QO':
          item.set('qoTextTrans', null);
          item.set('qoTextTransId', null);
          break;

        default:
      }
    });
  },

  // determine which languages are present in the translation objects,
  // so we can show the proper items
  determineAvailableTranslations() {
    const tempDict = {};
    this.get('content').forEach((item) => {
      if (!Ember.none(item.get('langCode'))) {
        tempDict[item.get('langCode')] = item.get('langCode');
      }
    });
    const keys = Object.keys(tempDict);
    for (let i = 0; i < keys.length; i++) {
      this.translations.pushObject(Ember.Object.create({
        value: keys[i],
        label: FLOW.isoLanguagesDict[keys[i]].name,
      }));
    }
  },

  cancelAddTranslation() {
    this.set('newSelected', false);
    this.set('selectedLanguage', null);
  },

  lockWhenNewLangChosen() {
    let found = false;
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
      const selLang = this.selectedLanguage.get('value');
      this.get('translations').forEach((item) => {
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
  },

  addTranslation() {
    let found = false;
    const newLang = this.get('selectedLanguage');
    if (!Ember.none(newLang)) {
      this.get('translations').forEach((item) => {
        found = found || (newLang.value == item.value);
      });
      if (!found) {
        this.resetTranslationFields();
        this.translations.pushObject(Ember.Object.create({
          value: this.get('selectedLanguage').value,
          label: FLOW.isoLanguagesDict[this.get('selectedLanguage').value].name,
        }));
        this.set('currentTranslation', this.get('selectedLanguage').value);
        this.set('currentTranslationName', FLOW.isoLanguagesDict[this.get('selectedLanguage').value].name);
        this.set('newSelected', false);
        this.set('noCurrentTrans', false);
      }
    }
  },

  switchTranslation(event) {
    if (event.context.value != this.get('currentTranslation')) {
      this.saveTranslations();
      this.resetTranslationFields();
      this.set('currentTranslation', event.context.value);
      this.set('currentTranslationName', FLOW.isoLanguagesDict[event.context.value].name);
      this.set('noCurrentTrans', false);
      this.putTranslationsInList();
    }
  },

  createItemList(id, questionGroupId) {
    const tempArray = [];
    const tempHashDict = {};

    // put in survey stuff
    const survey = FLOW.selectedControl.get('selectedSurvey');
    tempArray.push(Ember.Object.create({
      keyId: survey.get('keyId'),
      type: 'S',
      order: 0,
      surveyText: survey.get('name'),
      sDescText: survey.get('description'),
      isSurvey: true,
    }));

    // put in question group
    let questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);
    if (!Ember.none(questionGroup)) {
      tempArray.push(Ember.Object.create({
        keyId: questionGroup.get('keyId'),
        type: 'QG',
        order: 1000000 * parseInt(questionGroup.get('order'), 10),
        displayOrder: questionGroup.get('order'),
        qgText: questionGroup.get('name'),
        isQG: true,
      }));
    }
    // put in questions
    const questions = FLOW.store.filter(FLOW.Question, item => item.get('questionGroupId') == questionGroupId);
    questions.forEach((item) => {
      questionGroup = FLOW.store.find(FLOW.QuestionGroup, item.get('questionGroupId'));
      const qgOrder = parseInt(questionGroup.get('order'), 10);
      const qId = item.get('keyId');

      tempArray.push(Ember.Object.create({
        keyId: item.get('keyId'),
        type: 'Q',
        order: 1000000 * qgOrder + 1000 * parseInt(item.get('order'), 10),
        qText: item.get('text'),
        displayOrder: item.get('order'),
        qTipText: item.get('tip'),
        isQ: true,
        hasTooltip: !Ember.empty(item.get('tip')),
      }));
      // for each question, put in question options
      const options = FLOW.store.filter(FLOW.QuestionOption, optionItem => optionItem.get('questionId') == qId);

      const qOrder = parseInt(item.get('order'), 10);
      options.forEach((_item) => {
        tempArray.push(Ember.Object.create({
          keyId: _item.get('keyId'),
          type: 'QO',
          order: 1000000 * qgOrder + 1000 * qOrder + parseInt(_item.get('order'), 10) + 1,
          displayOrder: _item.get('order') + 1,
          qoText: _item.get('text'),
          isQO: true,
        }));
      });
    });

    // put all the items in the right order
    tempArray.sort((a, b) => a.get('order') - b.get('order'));

    let i = 0;
    tempArray.forEach((item) => {
      tempHashDict[item.get('type') + item.get('keyId')] = i;
      i++;
    });

    this.set('itemDict', tempHashDict);
    this.set('itemArray', tempArray);
  },

  // if deleteItem is true, the translation information is deleted
  putSingleTranslationInList(parentType, parentId, text, keyId, deleteItem) {
    let existingItemPos;
    let itemText;
    let itemKeyId;
    if (deleteItem) {
      itemText = text;
      itemKeyId = null;
    } else {
      itemText = text;
      itemKeyId = keyId;
    }
    switch (parentType) {
      case 'SURVEY_NAME':
        existingItemPos = this.get('itemDict')[`S${parentId}`];
        if (!Ember.none(existingItemPos)) {
          this.get('itemArray')[existingItemPos].set('surveyTextTrans', itemText);
          this.get('itemArray')[existingItemPos].set('surveyTextTransId', itemKeyId);
        }
        break;

      case 'SURVEY_DESC':
        existingItemPos = this.get('itemDict')[`S${parentId}`];
        if (!Ember.none(existingItemPos)) {
          this.get('itemArray')[existingItemPos].set('sDescTextTrans', itemText);
          this.get('itemArray')[existingItemPos].set('sDescTextTransId', itemKeyId);
        }
        break;

      case 'QUESTION_GROUP_NAME':
        existingItemPos = this.get('itemDict')[`QG${parentId}`];
        if (!Ember.none(existingItemPos)) {
          this.get('itemArray')[existingItemPos].set('qgTextTrans', itemText);
          this.get('itemArray')[existingItemPos].set('qgTextTransId', itemKeyId);
        }
        break;

      case 'QUESTION_TEXT':
        existingItemPos = this.get('itemDict')[`Q${parentId}`];
        if (!Ember.none(existingItemPos)) {
          this.get('itemArray')[existingItemPos].set('qTextTrans', itemText);
          this.get('itemArray')[existingItemPos].set('qTextTransId', itemKeyId);
        }
        break;

      case 'QUESTION_TIP':
        existingItemPos = this.get('itemDict')[`Q${parentId}`];
        if (!Ember.none(existingItemPos)) {
          this.get('itemArray')[existingItemPos].set('qTipTextTrans', itemText);
          this.get('itemArray')[existingItemPos].set('qTipTextTransId', itemKeyId);
        }
        break;

      case 'QUESTION_OPTION':
        existingItemPos = this.get('itemDict')[`QO${parentId}`];
        if (!Ember.none(existingItemPos)) {
          this.get('itemArray')[existingItemPos].set('qoTextTrans', itemText);
          this.get('itemArray')[existingItemPos].set('qoTextTransId', itemKeyId);
        }
        break;

      default:
    }
  },


  putTranslationsInList() {
    const _self = this;
    const currTrans = this.get('currentTranslation');
    // only proceed if we have a language selected
    if (!Ember.none(currTrans)) {
      // get the translations with the right surveyId and the right language code
      const translations = FLOW.store.filter(FLOW.Translation, item => (item.get('surveyId') == FLOW.selectedControl.selectedSurvey.get('keyId') && item.get('langCode') == currTrans));
      translations.forEach((item) => {
        _self.putSingleTranslationInList(item.get('parentType'), item.get('parentId'), item.get('text'), item.get('keyId'), false);
      });
    }
  },

  // delete a translation record by its Id. Committing is done in saveTranslations method
  deleteRecord(transId) {
    const candidates = FLOW.store.filter(FLOW.Translation, item => item.get('keyId') == transId);

    if (candidates.get('content').length > 0) {
      const existingTrans = candidates.objectAt(0);
      existingTrans.deleteRecord();
    }
  },

  createUpdateOrDeleteRecord(
    surveyId,
    questionGroupId,
    type,
    parentId,
    origText,
    translationText,
    lan,
    transId,
    allowSideEffects
  ) {
    let changed = false;
    if (!Ember.none(origText) && origText.length > 0) {
      // we have an original text
      if (!Ember.none(translationText) && translationText.length > 0) {
        // we have a translation text
        if (Ember.none(transId)) {
          // we don't have an existing translation, so create it
          changed = true;
          if (allowSideEffects) {
            FLOW.store.createRecord(FLOW.Translation, {
              parentType: type,
              parentId,
              surveyId,
              questionGroupId,
              text: translationText,
              langCode: lan,
            });
          }
        } else {
          // we have an existing translation, so update it, if the text has changed
          const candidates = FLOW.store.filter(FLOW.Translation, item => item.get('keyId') == transId);

          if (candidates.get('content').length > 0) {
            const existingTrans = candidates.objectAt(0);
            // if the existing translation is different from the existing one, update it
            if (existingTrans.get('text') != translationText) {
              changed = true;
              if (allowSideEffects) {
                existingTrans.set('text', translationText);
              }
            }
          }
        }
      // we don't have a translation text. If there is an existing translation, delete it
      } else if (!Ember.none(transId)) {
        // add this id to the list of to be deleted items
        changed = true;
        if (allowSideEffects) {
          this.toBeDeletedTranslations.pushObject(transId);
        }
      }
    // we don't have an original text. If there is an existing translation, delete it
    } else if (!Ember.none(transId)) {
      // add this to the list of to be deleted items
      changed = true;
      if (allowSideEffects) {
        this.toBeDeletedTranslations.pushObject(transId);
      }
    }
    return changed;
  },

  // checks if unsaved translations are present, and if so, emits a warning
  unsavedTranslations() {
    let unsaved = false;
    const _self = this;
    this.get('itemArray').forEach((item) => {
      let questionGroupId;
      const { type } = item;
      const parentId = item.keyId;
      const surveyId = FLOW.selectedControl.selectedSurvey.get('keyId');
      if (!Ember.none(FLOW.selectedControl.get('selectedQuestionGroup'))) {
        questionGroupId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      } else {
        questionGroupId = null;
      }
      const lan = _self.get('currentTranslation');
      if (type == 'S') {
        unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'SURVEY_NAME', parentId, item.surveyText, item.surveyTextTrans, lan, item.surveyTextTransId, false);
        unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'SURVEY_DESC', parentId, item.sDescText, item.sDescTextTrans, lan, item.sDescTextTransId, false);
      } else if (type == 'QG') {
        unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'QUESTION_GROUP_NAME', parentId, item.qgText, item.qgTextTrans, lan, item.qgTextTransId, false);
      } else if (type == 'Q') {
        unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'QUESTION_TEXT', parentId, item.qText, item.qTextTrans, lan, item.qTextTransId, false);
        unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'QUESTION_TIP', parentId, item.qTipText, item.qTipTextTrans, lan, item.qTipTextTransId, false);
      } else if (type == 'QO') {
        unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'QUESTION_OPTION', parentId, item.qoText, item.qoTextTrans, lan, item.qoTextTransId, false);
      }
    });
    if (unsaved) {
      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', Ember.String.loc('_unsaved_translations_present'));
      FLOW.dialogControl.set('message', Ember.String.loc('_unsaved_translations_present_text'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
    }
    return unsaved;
  },

  // after saving is complete, records insert themselves back into the translation item list
  saveTranslations() {
    const _self = this;
    const surveyId = FLOW.selectedControl.selectedSurvey.get('keyId');
    FLOW.store.adapter.set('bulkCommit', true);
    this.get('itemArray').forEach((item) => {
      let questionGroupId;
      const { type } = item;
      const parentId = item.keyId;
      if (!Ember.none(FLOW.selectedControl.get('selectedQuestionGroup'))) {
        questionGroupId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      } else {
        questionGroupId = null;
      }
      const lan = _self.get('currentTranslation');
      if (type == 'S') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'SURVEY_NAME', parentId, item.surveyText, item.surveyTextTrans, lan, item.surveyTextTransId, true);
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'SURVEY_DESC', parentId, item.sDescText, item.sDescTextTrans, lan, item.sDescTextTransId, true);
      } else if (type == 'QG') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'QUESTION_GROUP_NAME', parentId, item.qgText, item.qgTextTrans, lan, item.qgTextTransId, true);
      } else if (type == 'Q') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'QUESTION_TEXT', parentId, item.qText, item.qTextTrans, lan, item.qTextTransId, true);
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'QUESTION_TIP', parentId, item.qTipText, item.qTipTextTrans, lan, item.qTipTextTransId, true);
      } else if (type == 'QO') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, 'QUESTION_OPTION', parentId, item.qoText, item.qoTextTrans, lan, item.qoTextTransId, true);
      }
    });
    FLOW.store.commit();
    FLOW.store.adapter.set('bulkCommit', false);

    // delete items individually, as a body in a DELETE request is not accepted by GAE
    this.get('toBeDeletedTranslations').forEach((item) => {
      _self.deleteRecord(item);
    });

    // make survey unpublished
    const survey = FLOW.store.find(FLOW.Survey, surveyId);
    if (!Ember.empty(survey)) {
      survey.set('status', 'NOT_PUBLISHED');
    }
    FLOW.store.commit();
    this.set('toBeDeletedTranslations', []);
  },
});

FLOW.CaddisflyResourceController = Ember.ArrayController.extend({
  sortProperties: ['name'],
  sortAscending: true,
  caddisflyTestsFileUrl: FLOW.Env.caddisflyTestsFileUrl,
  testsFileLoaded: false,

  load() {
    const self = this;
    $.getJSON(this.get('caddisflyTestsFileUrl'), (caddisflyTestsFileContent) => {
      self.set('content', self.parseCaddisflyTestsFile(caddisflyTestsFileContent));
      self.set('testsFileLoaded', true);
    }).fail(() => {
      self.set('content', []);
    });
  },

  parseCaddisflyTestsFile(caddisflyTestsFileContent) {
    const caddisflyTests = Ember.A();
    caddisflyTestsFileContent.tests.forEach((test) => {
      caddisflyTests.push(FLOW.CaddisflyTestDefinition.create({
        name: test.name,
        brand: test.brand,
        uuid: test.uuid,
        multiParameter: test.multiParameter,
        sample: test.sample,
        device: test.device,
        model: test.model,
        results: test.results,
        reagents: 'reagents' in test ? test.reagents : [],
      }));
    });

    return caddisflyTests;
  },
});
