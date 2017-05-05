FLOW.permControl = Ember.Controller.create({
  perms: [],

  init: function () {
    this._super();
    this.initPermissions();
    this.setUserPermissions();
    this.setCurrentPermissions();
  },

  initPermissions: function () {
    this.perms.push(Ember.Object.create({
      perm: 'createSurvey',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'editSurvey',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'uploadSurveyZipData',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'importDataReport',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'viewMessages',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'publishSurvey',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'mapData',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'setDataPrivacy',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'editRawData',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'deleteRawData',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'runReport',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'deleteSurvey',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'deleteSurveyGroup',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'addUser',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'editUser',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'deleteUser',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'approveSurvey',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'editEditorialContent',
      value: false
    }));
  },

  setUserPermissions: function () {
    var user = true;
    if (user === true) {
      this.perms.findProperty('perm', 'createSurvey').value = true;
      this.perms.findProperty('perm', 'editSurvey').value = true;
      this.perms.findProperty('perm', 'uploadSurveyZipData').value = true;
      this.perms.findProperty('perm', 'viewMessages').value = true;
      this.perms.findProperty('perm', 'publishSurvey').value = true;
      this.perms.findProperty('perm', 'mapData').value = true;
      this.perms.findProperty('perm', 'setDataPrivacy').value = true;
      this.perms.findProperty('perm', 'runReport').value = true;
    }

    if (user === true) {
      this.perms.findProperty('perm', 'createSurvey').value = true;
      this.perms.findProperty('perm', 'editSurvey').value = true;
      this.perms.findProperty('perm', 'uploadSurveyZipData').value = true;
      this.perms.findProperty('perm', 'importDataReport').value = true;
      this.perms.findProperty('perm', 'viewMessages').value = true;
      this.perms.findProperty('perm', 'publishSurvey').value = true;
      this.perms.findProperty('perm', 'mapData').value = true;
      this.perms.findProperty('perm', 'setDataPrivacy').value = true;
      this.perms.findProperty('perm', 'runReport').value = true;
      this.perms.findProperty('perm', 'editRawData').value = true;
      this.perms.findProperty('perm', 'deleteRawData').value = true;
      this.perms.findProperty('perm', 'approveSurvey').value = true;
    }

    if (user === true) {
      this.perms.findProperty('perm', 'createSurvey').value = true;
      this.perms.findProperty('perm', 'editSurvey').value = true;
      this.perms.findProperty('perm', 'uploadSurveyZipData').value = true;
      this.perms.findProperty('perm', 'importDataReport').value = true;
      this.perms.findProperty('perm', 'viewMessages').value = true;
      this.perms.findProperty('perm', 'publishSurvey').value = true;
      this.perms.findProperty('perm', 'mapData').value = true;
      this.perms.findProperty('perm', 'setDataPrivacy').value = true;
      this.perms.findProperty('perm', 'runReport').value = true;
      this.perms.findProperty('perm', 'editRawData').value = true;
      this.perms.findProperty('perm', 'deleteRawData').value = true;
      this.perms.findProperty('perm', 'approveSurvey').value = true;
      this.perms.findProperty('perm', 'deleteSurvey').value = true;
      this.perms.findProperty('perm', 'deleteSurveyGroup').value = true;
      this.perms.findProperty('perm', 'addUser').value = true;
      this.perms.findProperty('perm', 'editUser').value = true;
      this.perms.findProperty('perm', 'deleteUser').value = true;
    }

  },

  setCurrentPermissions: function () {
    this.perms.forEach(function (item) {
      //this.set(item.perm,item.value);
    });
  },

  /* Given an entity, process the permissions settings for the current user
    and return the permissions associated with that entity.  Entity is an Ember object*/
  permissions: function(entity) {
    var keyId, ancestorIds, permissions = [], currentUserPermissions = FLOW.currentUser.get('pathPermissions');

    if (!currentUserPermissions || !entity) { return []; }

    // return superAdmin permissions
    if ("0" in currentUserPermissions){
      return currentUserPermissions["0"];
    }

    // first check current object id
    keyId = entity.get('keyId');
    if (keyId in currentUserPermissions) {
      permissions = currentUserPermissions[keyId];
    }

    // check ancestor permissions
    ancestorIds = entity.get('ancestorIds');
    if (!ancestorIds) {
      return permissions;
    }

    var i;
    for(i = 0; i < ancestorIds.length; i++){
      if (ancestorIds[i] in currentUserPermissions) {
        if (currentUserPermissions[ancestorIds[i]]) {
          currentUserPermissions[ancestorIds[i]].forEach(function(item){
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
  canDeleteData: function(ancestorIds) {
      var pathPermissions = FLOW.currentUser.get('pathPermissions');
      var canDelete = false;
      ancestorIds.forEach(function(id){
          if(id in pathPermissions && pathPermissions[id].indexOf("DATA_DELETE") > -1) {
              canDelete = true;
          }
      });
      return canDelete;
    },

  /* takes a survey (ember object) and checks whether the current user
    has edit permissions for the survey */
  canEditSurvey: function(survey) {
    var permissions;
    if (!Ember.none(survey)) {
      permissions = this.permissions(survey);
    }
    return permissions && permissions.indexOf("PROJECT_FOLDER_UPDATE") > -1;
  },

  /* takes a form (ember object) and checks with user permissions
  whether the current user has edit permissions for the form */
  canEditForm: function(form) {
    var permissions;
    if (!Ember.none(form)) {
      permissions = this.permissions(form);
    }
    return permissions && permissions.indexOf("FORM_UPDATE") > -1;
  },

  canEditResponses: function (form) {
    var permissions;
    if (!Ember.none(form)) {
      permissions = this.permissions(form);
    }
    return permissions && permissions.indexOf("DATA_UPDATE") > -1;
  },

  canManageDevices: function () {
    var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
    for (var perms in currentUserPermissions) {
      if (currentUserPermissions[perms].indexOf("DEVICE_MANAGE") > -1) {
        return true;
      }
    }
    return false;
  }.property(),

  canManageCascadeResources: function () {
    var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
    for (var perms in currentUserPermissions) {
      if (currentUserPermissions[perms].indexOf("CASCADE_MANAGE") > -1) {
          return true;
      }
    }
    return false;
  }.property(),

  canCleanData: function () {
      var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
      for (var perms in currentUserPermissions) {
          if (currentUserPermissions[perms].indexOf("DATA_CLEANING") > -1) {
              return true;
          }
      }
      return false;
  }.property(),

  canManageDataAppoval: function () {
      var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
      for (var perms in currentUserPermissions) {
          if (currentUserPermissions[perms].indexOf("DATA_APPROVE_MANAGE") > -1) {
              return true;
          }
      }
      return false;
  }.property(),
});


FLOW.dialogControl = Ember.Object.create({
  delSG: "delSG",
  delS: "delS",
  delQG: "delQG",
  delQ: "delQ",
  delUser: "delUser",
  delAttr: "delAttr",
  delAssignment: "delAssignment",
  delDeviceGroup: "delDeviceGroup",
  delSI: "delSI",
  delSI2: "delSI2",
  delSL: "delSL",
  delCR: "delCR",
  makeMonitor: "makeMonitor",
  delForm: "delForm",
  showDialog: false,
  message: null,
  header: null,
  activeView: null,
  activeAction: null,
  showOK: true,
  showCANCEL: true,

  confirm: function (event) {
    this.set('activeView', event.view);
    this.set('activeAction', event.context);
    this.set('showOK', true);
    this.set('showCANCEL', true);

    switch (this.get('activeAction')) {
    case "delSG":
      this.set('header', Ember.String.loc('_sg_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delS":
      this.set('header', Ember.String.loc('_s_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delQG":
      this.set('header', Ember.String.loc('_qg_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delQ":
      this.set('header', Ember.String.loc('_q_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delUser":
      this.set('header', Ember.String.loc('_are_you_sure_you_want_to_delete_this_user'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delAttr":
      this.set('header', Ember.String.loc('_attr_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delAssignment":
      this.set('header', Ember.String.loc('_assignment_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delDeviceGroup":
      this.set('header', Ember.String.loc('_device_group_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delSI":
      this.set('header', Ember.String.loc('_delete_record_header'));
      this.set('message', Ember.String.loc('_are_you_sure_delete_this_data_record'));
      this.set('showDialog', true);
      break;

    case "delSI2":
      this.set('header', Ember.String.loc('_delete_record_header'));
      this.set('message', Ember.String.loc('_are_you_sure_delete_this_data_record'));
      this.set('showDialog', true);
      break;

    case "delSL":
        this.set('header', Ember.String.loc('_delete_data_point_header'));
        this.set('message', Ember.String.loc('_are_you_sure_delete_this_data_point'));
        this.set('showDialog', true);
        break;

    case "makeMonitor":
    	this.set('header', Ember.String.loc('_make_monitor_group_header'));
        this.set('message', Ember.String.loc('_make_monitor_group_text'));
        this.set('showDialog', true);
      break;

    case "delForm":
      this.set('header', "Delete form");
      this.set('message', "Are you sure you want to delete this form?");
      this.set('showDialog', true);
      break;

    case "delCR":
        this.set('header', Ember.String.loc('_delete_cascade_resource_header'));
        this.set('message', Ember.String.loc('_delete_cascade_resource_text'));
        this.set('showDialog', true);
      break;

    default:
    }
  },

  doOK: function (event) {
    this.set('header', null);
    this.set('message', null);
    this.set('showCANCEL', true);
    this.set('showDialog', false);
    var view = this.get('activeView');
    switch (this.get('activeAction')) {
    case "delSG":
      view.deleteSurveyGroup.apply(view, arguments);
      break;

    case "delS":
      view.deleteSurvey.apply(view, arguments);
      break;

    case "delQG":
      view.deleteQuestionGroup.apply(view, arguments);
      break;

    case "delQ":
      this.set('showDialog', false);
      view.deleteQuestion.apply(view, arguments);
      break;

    case "delUser":
      this.set('showDialog', false);
      view.deleteUser.apply(view, arguments);
      break;

    case "delAttr":
      this.set('showDialog', false);
      view.deleteAttribute.apply(view, arguments);
      break;

    case "delAssignment":
      this.set('showDialog', false);
      view.deleteSurveyAssignment.apply(view, arguments);
      break;

    case "delDeviceGroup":
      this.set('showDialog', false);
      view.deleteDeviceGroup.apply(view, arguments);
      break;

    case "delSI":
      this.set('showDialog', false);
      view.deleteSI.apply(view, arguments);
      break;

    case "delSI2":
      this.set('showDialog', false);
      view.deleteSI.apply(view, arguments);
      break;

    case "delSL":
        this.set('showDialog', false);
        view.deleteSL.apply(view, arguments);
        break;

    case "makeMonitor":
      this.set('showDialog', false);
      view.makeMonitorGroup.apply(view, arguments);
      break;

    case "delForm":
      this.set('showDialog', false);
      FLOW.surveyControl.deleteForm();
      break;
      
    case "delCR":
        this.set('showDialog', false);
        view.deleteResource(view, arguments);
        break;

    default:
    }
  },

  doCANCEL: function (event) {
    this.set('showDialog', false);
  }
});
