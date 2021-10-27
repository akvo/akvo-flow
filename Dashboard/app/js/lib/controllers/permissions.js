FLOW.permControl = Ember.Controller.create({
  /* Given an entity, process the permissions settings for the current user
    and return the permissions associated with that entity.  Entity is an Ember object */
  permissions(entity) {
    let permissions = [];
    const currentUserPermissions = FLOW.currentUser.get('pathPermissions');

    if (!currentUserPermissions || !entity) {
      return [];
    }

    // first check current object id
    const keyId = entity.get('keyId');
    if (keyId in currentUserPermissions) {
      permissions = currentUserPermissions[keyId];
    }

    // check ancestor permissions
    const ancestorIds = entity.get('ancestorIds');
    if (!ancestorIds) {
      return permissions;
    }

    for (let i = 0; i < ancestorIds.length; i++) {
      if (ancestorIds[i] in currentUserPermissions) {
        if (currentUserPermissions[ancestorIds[i]]) {
          currentUserPermissions[ancestorIds[i]].forEach(item => {
            if (permissions.indexOf(item) < 0) {
              permissions.push(item);
            }
          });
        }
      }
    }

    return permissions;
  },

  /* query based on survey (group) ancestorIds whether a user has
  permissions for data deletion */
  canDeleteData(ancestorIds) {
    const pathPermissions = FLOW.currentUser.get('pathPermissions');
    let canDelete = false;
    ancestorIds.forEach(id => {
      if (id in pathPermissions && pathPermissions[id].indexOf('DATA_DELETE') > -1) {
        canDelete = true;
      }
    });
    return canDelete;
  },

  /* takes a survey (ember object) and checks whether the current user
    has edit permissions for the survey */
  canEditSurvey(survey) {
    let permissions;
    if (!Ember.none(survey)) {
      permissions = this.permissions(survey);
    }
    return permissions && permissions.indexOf('PROJECT_FOLDER_UPDATE') > -1;
  },

  /* takes a form (ember object) and checks with user permissions
  whether the current user has edit permissions for the form */
  canEditForm(form) {
    let permissions;
    if (!Ember.none(form)) {
      permissions = this.permissions(form);
    }
    return permissions && permissions.indexOf('FORM_UPDATE') > -1;
  },

  canEditResponses(form) {
    let permissions;
    if (!Ember.none(form)) {
      permissions = this.permissions(form);
    }
    return permissions && permissions.indexOf('DATA_UPDATE') > -1;
  },

  canManageDevices: Ember.computed(() => FLOW.hasPermission('DEVICE_MANAGE')).property(),

  canManageCascadeResources: Ember.computed(() => FLOW.hasPermission('CASCADE_MANAGE')).property(),

  canCleanData: Ember.computed(() => FLOW.hasPermission('DATA_CLEANING')).property(),

  canManageDataAppoval: Ember.computed(() => FLOW.hasPermission('DATA_APPROVE_MANAGE')).property(),

  userCanViewData(entity) {
    let permissions;
    if (!Ember.none(entity)) {
      permissions = this.permissions(entity);
    }
    return permissions && permissions.indexOf('DATA_READ') > -1;
  },
});

FLOW.dialogControl = Ember.Object.create({
  delS: 'delS',
  delQG: 'delQG',
  delQ: 'delQ',
  delUser: 'delUser',
  delAssignment: 'delAssignment',
  delDeviceGroup: 'delDeviceGroup',
  delSI: 'delSI',
  delSI2: 'delSI2',
  delCR: 'delCR',
  delForm: 'delForm',
  showDialog: false,
  message: null,
  header: null,
  activeView: null,
  activeAction: null,
  showOK: true,
  showCANCEL: true,
  showSpin: false,
  showTick: false,
  showError: false,
  showOKDisabled: false,

  confirm(event) {
    this.set('activeView', event.view);
    this.set('activeAction', event.context);
    this.set('showOK', true);
    this.set('showCANCEL', true);
    this.set('delAssignmentConfirm', false);
    this.set('delGroupConfirm', false);

    switch (this.get('activeAction')) {
      case 'delS':
        this.set('header', Ember.String.loc('_s_delete_header'));
        this.set('message', Ember.String.loc('_this_cant_be_undo'));
        this.set('showDialog', true);
        break;

      case 'delQG':
        this.set('header', Ember.String.loc('_qg_delete_header'));
        this.set('message', Ember.String.loc('_this_cant_be_undo'));
        this.set('showDialog', true);
        break;

      case 'delQ':
        this.set('header', Ember.String.loc('_q_delete_header'));
        this.set('message', Ember.String.loc('_this_cant_be_undo'));
        this.set('showDialog', true);
        break;

      case 'delUser':
        this.set('header', Ember.String.loc('_are_you_sure_you_want_to_delete_this_user'));
        this.set('message', Ember.String.loc('_this_cant_be_undo'));
        this.set('showDialog', true);
        break;

      case 'delAssignment':
        this.set('header', Ember.String.loc('_assignment_delete_header'));
        this.set('message', Ember.String.loc('_this_cant_be_undo'));
        this.set('delAssignmentId', event.assignmentId);
        this.set('showDialog', true);
        break;

      case 'delDeviceGroup':
        this.set('header', Ember.String.loc('_device_group_delete_header'));
        this.set('message', Ember.String.loc('_this_cant_be_undo'));
        this.set('delGroupId', event.groupId);
        this.set('showDialog', true);
        break;

      case 'delSI':
        this.set('header', Ember.String.loc('_delete_record_header'));
        this.set('message', Ember.String.loc('_are_you_sure_delete_this_data_record'));
        this.set('showDialog', true);
        break;

      case 'delSI2':
        this.set('header', Ember.String.loc('_delete_record_header'));
        this.set('message', undefined);

        if (this.isRegistrationFormInstance(event.contexts[1])) {
          this.set('showOKDisabled', true);
          const self = this;
          const instanceDeleted = event.contexts[1];
          const submissions = FLOW.SurveyInstance.find({
            surveyedLocaleId: instanceDeleted.get('surveyedLocaleId'),
          });
          submissions.on('didLoad', () => {
            this.set('showOKDisabled', false);
            const numberOfSubmissions = submissions.get('content').length;
            const numberMonitoringSubmissions = numberOfSubmissions - 1;
            if (numberMonitoringSubmissions > 0) {
              self.set(
                'message',
                Ember.String.loc('_delete_all_monitoring_forms', [numberMonitoringSubmissions])
              );
            }
          });
        }
        this.set('showDialog', true);
        break;

      case 'delForm':
        this.set('header', Ember.String.loc('_delete_form'));
        this.set('message', Ember.String.loc('_are_you_sure_delete_this_form'));
        this.set('showDialog', true);
        break;

      case 'delCR':
        this.set('header', Ember.String.loc('_delete_cascade_resource_header'));
        this.set('message', Ember.String.loc('_delete_cascade_resource_text'));
        this.set('showDialog', true);
        break;

      default:
    }
  },

  isRegistrationFormInstance(instanceDeleted) {
    const registrationFormId =
      FLOW.selectedControl.selectedSurvey &&
      FLOW.selectedControl.selectedSurveyGroup.get('newLocaleSurveyId');
    const instanceFormId = instanceDeleted && instanceDeleted.get('surveyId');
    return registrationFormId && instanceFormId && registrationFormId === instanceFormId;
  },

  doOK() {
    this.set('header', null);
    this.set('message', null);
    this.set('showCANCEL', true);
    this.set('showDialog', false);
    this.set('showSpin', false);
    this.set('showTick', false);
    this.set('showError', false);

    const view = this.get('activeView');
    switch (this.get('activeAction')) {
      case 'delS':
        view.deleteSurvey.apply(view, arguments);
        break;

      case 'delQG':
        view.deleteQuestionGroup.apply(view, arguments);
        break;

      case 'delQ':
        this.set('showDialog', false);
        view.deleteQuestion.apply(view, arguments);
        break;

      case 'delUser':
        this.set('showDialog', false);
        view.deleteUser.apply(view, arguments);
        break;

      case 'delAssignment':
        this.set('showDialog', false);
        this.set('delAssignmentConfirm', true);
        break;

      case 'delDeviceGroup':
        this.set('showDialog', false);
        this.set('delGroupConfirm', true);
        break;

      case 'delSI':
        this.set('showDialog', false);
        view.deleteSI.apply(view, arguments);
        break;

      case 'delSI2':
        this.set('showDialog', false);
        view.deleteSI.apply(view, arguments);
        break;

      case 'delForm':
        this.set('showDialog', false);
        FLOW.surveyControl.deleteForm();
        break;

      case 'delCR':
        this.set('showDialog', false);
        view.deleteResource(view, arguments);
        break;

      case 'reports':
        FLOW.router.transitionTo('navData.reportsList');
        break;

      default:
    }
  },

  doCANCEL() {
    this.set('showDialog', false);
    this.set('showSpin', false);
    this.set('showTick', false);
    this.set('showError', false);
  },
});
