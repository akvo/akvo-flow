
FLOW.permissionLevelControl = Ember.Object.create({
  content: [
  Ember.Object.create({
    label: "User",
    value: "USER"
  }), Ember.Object.create({
    label: "Project Editor",
    value: "PROJECT_EDITOR"
  }), Ember.Object.create({
    label: "Administrator",
    value: "ADMIN"
  })]
});


FLOW.permControl = Ember.Controller.create({
  perms: [],

  init: function() {
    this._super();
    this.initPermissions();
    this.setUserPermissions();
    this.setCurrentPermissions();
  },

  initPermissions: function() {
    this.perms.push(Ember.Object.create({perm: 'createSurvey',value: false}));
    this.perms.push(Ember.Object.create({perm: 'editSurvey',value: false}));
    this.perms.push(Ember.Object.create({perm: 'uploadSurveyZipData',value: false}));
    this.perms.push(Ember.Object.create({perm: 'importDataReport',value: false}));
    this.perms.push(Ember.Object.create({perm: 'viewMessages',value: false}));
    this.perms.push(Ember.Object.create({perm: 'publishSurvey',value: false}));
    this.perms.push(Ember.Object.create({perm: 'mapData',value: false}));
    this.perms.push(Ember.Object.create({perm: 'setDataPrivacy',value: false}));
    this.perms.push(Ember.Object.create({perm: 'editRawData',value: false}));
    this.perms.push(Ember.Object.create({perm: 'deleteRawData',value: false}));
    this.perms.push(Ember.Object.create({perm: 'runReport',value: false}));
    this.perms.push(Ember.Object.create({perm: 'deleteSurvey',value: false}));
    this.perms.push(Ember.Object.create({perm: 'deleteSurveyGroup',value: false}));
    this.perms.push(Ember.Object.create({perm: 'addUser',value: false}));
    this.perms.push(Ember.Object.create({perm: 'editUser',value: false}));
    this.perms.push(Ember.Object.create({perm: 'deleteUser',value: false}));
    this.perms.push(Ember.Object.create({perm: 'approveSurvey',value: false}));
    this.perms.push(Ember.Object.create({perm: 'editEditorialContent',value: false}));
  },

  setUserPermissions: function() {
    var user=true;
    if(user === true) {
      this.perms.findProperty('perm','createSurvey').value = true;
      this.perms.findProperty('perm','editSurvey').value = true;
      this.perms.findProperty('perm','uploadSurveyZipData').value = true;
      this.perms.findProperty('perm','viewMessages').value = true;
      this.perms.findProperty('perm','publishSurvey').value = true;
      this.perms.findProperty('perm','mapData').value = true;
      this.perms.findProperty('perm','setDataPrivacy').value = true;
      this.perms.findProperty('perm','runReport').value = true;
    }
    
    if(user === true) {
      this.perms.findProperty('perm','createSurvey').value = true;
      this.perms.findProperty('perm','editSurvey').value = true;
      this.perms.findProperty('perm','uploadSurveyZipData').value = true;
      this.perms.findProperty('perm','importDataReport').value = true;
      this.perms.findProperty('perm','viewMessages').value = true;
      this.perms.findProperty('perm','publishSurvey').value = true;
      this.perms.findProperty('perm','mapData').value = true;
      this.perms.findProperty('perm','setDataPrivacy').value = true;
      this.perms.findProperty('perm','runReport').value = true;
      this.perms.findProperty('perm','editRawData').value = true;
      this.perms.findProperty('perm','deleteRawData').value = true;
      this.perms.findProperty('perm','approveSurvey').value = true;
    }
    
    if(user === true) {
      this.perms.findProperty('perm','createSurvey').value = true;
      this.perms.findProperty('perm','editSurvey').value = true;
      this.perms.findProperty('perm','uploadSurveyZipData').value = true;
      this.perms.findProperty('perm','importDataReport').value = true;
      this.perms.findProperty('perm','viewMessages').value = true;
      this.perms.findProperty('perm','publishSurvey').value = true;
      this.perms.findProperty('perm','mapData').value = true;
      this.perms.findProperty('perm','setDataPrivacy').value = true;
      this.perms.findProperty('perm','runReport').value = true;
      this.perms.findProperty('perm','editRawData').value = true;
      this.perms.findProperty('perm','deleteRawData').value = true;
      this.perms.findProperty('perm','approveSurvey').value = true;
      this.perms.findProperty('perm','deleteSurvey').value = true;
      this.perms.findProperty('perm','deleteSurveyGroup').value = true;
      this.perms.findProperty('perm','addUser').value = true;
      this.perms.findProperty('perm','editUser').value = true;
      this.perms.findProperty('perm','deleteUser').value = true;
    }

  },

  setCurrentPermissions:function () {
    this.perms.forEach(function(item){
      //this.set(item.perm,item.value);
    });
  }
});


FLOW.dialogControl = Ember.Object.create({
  delSG: "delSG",
  delS: "delS",
  delQG: "delQG",
  delQ: "delQ",
  delUser: "delUser",
  delAttr: "delAttr",
  delAssignment: "delAssignment",
  showDialog: false,
  message: null,
  header: null,
  activeView: null,
  activeAction: null,
  showOK: true,
  showCANCEL: true,

  confirm: function(event) {
    this.set('activeView', event.view);
    this.set('activeAction', event.context);
    this.set('showOK', true);
    this.set('showCANCEL', true);

    switch(this.get('activeAction')) {
    case "delSG":
      if(FLOW.surveyGroupControl.containsSurveys()) {
        this.set('activeAction', "ignore");
        this.set('header', Ember.String.loc('_SG_delete_not_possible_header'));
        this.set('message', Ember.String.loc('_SG_delete_not_possible_message'));
        this.set('showCANCEL', false);
        this.set('showDialog', true);
      } else {
        this.set('header', Ember.String.loc('_SG_delete_header'));
        this.set('message', Ember.String.loc('_SG_delete_message'));
        this.set('showDialog', true);
      }
      break;

    case "delS":
      this.set('header', Ember.String.loc('_S_delete_header'));
      this.set('message', Ember.String.loc('_S_delete_message'));
      this.set('showDialog', true);
      break;

    case "delQG":
      this.set('header', Ember.String.loc('_QG_delete_header'));
      this.set('message', Ember.String.loc('_QG_delete_message'));
      this.set('showDialog', true);
      break;

    case "delQ":
      this.set('header', Ember.String.loc('_Q_delete_header'));
      this.set('message', Ember.String.loc('_Q_delete_message'));
      this.set('showDialog', true);
      break;

    case "delUser":
      this.set('header', Ember.String.loc('_User_delete_header'));
      this.set('message', Ember.String.loc('_User_delete_message'));
      this.set('showDialog', true);
      break;

    case "delAttr":
      this.set('header', Ember.String.loc('_Attr_delete_header'));
      this.set('message', Ember.String.loc('_Attr_delete_message'));
      this.set('showDialog', true);
      break;

    case "delAssignment":
      this.set('header', Ember.String.loc('_Assignment_delete_header'));
      this.set('message', Ember.String.loc('_Assignment_delete_message'));
      this.set('showDialog', true);
      break;

    default:
    }
  },

  doOK: function(event) {
    this.set('header', null);
    this.set('message', null);
    this.set('showCANCEL', true);
    this.set('showDialog', false);
    var view = this.get('activeView');
    switch(this.get('activeAction')) {
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

    default:
    }
  },

  doCANCEL: function(event) {
    this.set('showDialog', false);
  }
});