// ***********************************************//
//                 controllers
// ***********************************************//

// Define the main application controller. This is automatically picked up by
// the application and initialized.
require('akvo-flow/core');
FLOW.ApplicationController = Ember.Controller.extend({
  init: function () {
    this._super();
    Ember.STRINGS = Ember.STRINGS_EN;
  }
});

// Navigation controllers
FLOW.NavigationController = Em.Controller.extend({ selected: null});
FLOW.NavHomeController = Ember.Controller.extend();
FLOW.NavSurveysController = Ember.Controller.extend();
FLOW.NavDevicesController = Ember.Controller.extend();
FLOW.DevicesSubnavController = Em.Controller.extend();
FLOW.DevicesTableHeaderController = Em.Controller.extend({selected: null});

FLOW.NavDataController = Ember.Controller.extend();
FLOW.DatasubnavController = Em.Controller.extend();
FLOW.InspectDataController = Ember.ArrayController.extend();
FLOW.ImportSurveyController = Ember.Controller.extend();
FLOW.ExcelImportController = Ember.Controller.extend();
FLOW.ExcelExportController = Ember.Controller.extend();

FLOW.NavReportsController = Ember.Controller.extend();
FLOW.ReportsSubnavController = Em.Controller.extend();
FLOW.ExportReportsController = Ember.ArrayController.extend();
FLOW.ChartReportsController = Ember.Controller.extend();

FLOW.NavMapsController = Ember.Controller.extend();
FLOW.NavUsersController = Ember.Controller.extend();
FLOW.NavAdminController = Ember.Controller.extend();


// ***********************************************//
//                Type controllers
// ***********************************************//
FLOW.languageControl = Ember.Object.create({
  dashboardLanguage: null,

  init: function () {
    var locale;

    this._super();
    locale = localStorage.locale;
    if (typeof locale === 'undefined') {
      this.set('dashboardLanguage', this.content.findProperty('value', 'en'));
    } else {
      this.set('dashboardLanguage', this.content.findProperty('value', locale));
    }
  },

  content: [
    Ember.Object.create({label: "English", value: "en"}),
    Ember.Object.create({label: "Dutch", value: "nl"}),
    Ember.Object.create({label: "Spanish", value: "es"}),
    Ember.Object.create({label: "French", value: "fr"})
    ],

  changeLanguage: function () {
    var locale;
    locale = this.dashboardLanguage.get("value");
    localStorage.locale = this.get('dashboardLanguage.value');

    if (locale === "nl") {Ember.STRINGS = Ember.STRINGS_NL; }
    else if (locale === "fr") {Ember.STRINGS = Ember.STRINGS_FR; }
    else if (locale === "es") {Ember.STRINGS = Ember.STRINGS_ES; }
    else {Ember.STRINGS = Ember.STRINGS_EN; }
  }.observes('this.dashboardLanguage')
});

FLOW.dataserverControl = Ember.Object.create({
  dataserver: null,

  init: function () {
    var dataserverSetting;

    this._super();
    dataserverSetting = localStorage.dataserver;
    if (typeof dataserverSetting === "undefined") {
      this.set('dataserver', this.content.findProperty('value', 'sandbox'));
    } else {
      this.set('dataserver', this.content.findProperty('value', dataserverSetting));
    }
  },

  content: [
    Ember.Object.create({label: "Akvo Sandbox", value: "sandbox"}),
    Ember.Object.create({label: "Localhost", value: "local"}),
    Ember.Object.create({label: "Localhost to Sandbox", value: "local-sandbox"}),
    Ember.Object.create({label: "Local VM", value: "vm"}),
    Ember.Object.create({label: "Fixtures", value: "fixtures"})],

  changeServer: function () {
    var host = "http://" + window.location.host,
    server = this.dataserver.get('value');
    localStorage.dataserver = server;

    if (server == "local") {
      // FLOW.selectedControl.set('dataserverControl', null);
      FLOW.store = DS.Store.create({
        revision: 10,
        adapter:DS.FLOWRESTAdapter.create({bulkCommit: false, namespace: "restlocal", url: host})
      });
    }
    else if (server == "vm") {
      // FLOW.selectedControl.set('dataserverControl',null);
      FLOW.store = DS.Store.create({
        revision: 10,
        adapter:DS.FLOWRESTAdapter.create({bulkCommit: false, namespace: "rest", url: host})
      });
    }
    else if (server == "sandbox") {
      // FLOW.selectedControl.set('dataserverControl',null);
      FLOW.store = DS.Store.create({
        revision: 10,
        adapter: DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"rest", url:host})
      });
    }
     else if (server == "local-sandbox") {
      // FLOW.selectedControl.set('dataserverControl',null);
      FLOW.store = DS.Store.create({
        revision: 10,
        adapter: DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"restsandbox", url:host})
      });
    }
    else if (server == "fixtures") {
      // FLOW.selectedControl.set('dataserverControl',null);
      FLOW.store = DS.Store.create({
        revision: 10,
        adapter: DS.fixtureAdapter
      });
    }
  }.observes('this.dataserver')
});


FLOW.questionTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({label: "Free text", value: "freeText"}),
    Ember.Object.create({label: "Option", value: "option"}),
    Ember.Object.create({label: "Number", value: "number"}),
    Ember.Object.create({label: "Geolocation", value: "geoLoc"}),
    Ember.Object.create({label: "Photo", value: "photo"}),
    Ember.Object.create({label: "Video", value: "video"}),
    Ember.Object.create({label: "Date", value: "date"}),
    Ember.Object.create({label: "Barcode", value: "barcode"})
  ]
});


FLOW.surveyPointTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({label: "Point", value: "Point"}),
    Ember.Object.create({label: "Household", value: "Household"}),
    Ember.Object.create({label: "Public institution", value: "PublicInstitution"})
  ]
});


FLOW.surveySectorTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({label: "Water and Sanitation", value: "WASH"}),
    Ember.Object.create({label: "Education", value: "EDUC"}),
    Ember.Object.create({label: "Economic development", value: "ECONDEV"}),
    Ember.Object.create({label: "Health care", value: "HEALTH"}),
    Ember.Object.create({label: "IT and Communication", value: "ICT"}),
    Ember.Object.create({label: "Food security", value: "FOODSEC"}),
    Ember.Object.create({label: "Other", value: "OTHER"})
  ]
});


FLOW.selectedControl = Ember.Controller.create({
  selectedSurveyGroup: null,
  selectedSurvey: null,
  selectedSurveyAllQuestions: null,
  selectedQuestionGroup: null,
  selectedQuestion: null,
  selectedOption: null,
  selectedForMoveQuestionGroup: null,
  selectedForCopyQuestionGroup: null,
  selectedForMoveQuestion: null,
  selectedForCopyQuestion: null,
  selectedCreateNewGroup: false,

  // when selected survey changes, deselect selected surveys and question groups
  deselectSurveyGroupChildren: function () {
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.selectedControl.set('selectedSurveyAllQuestions', null);
    FLOW.selectedControl.set('selectedQuestionGroup', null);
  }.observes('this.selectedSurveyGroup'),

   deselectSurveyChildren: function () {
    FLOW.selectedControl.set('selectedQuestionGroup', null);
  }.observes('this.selectedSurvey')
});


FLOW.dialogControl = Ember.Object.create({
  delSG:"delSG",
  delS:"delS",
  delQG:"delQG",
  delQ:"delQ",
  showDialog:false,
  message:null,
  header:null,
  activeView:null,
  activeAction:null,
  showOK:true,
  showCANCEL:true,

  confirm:function(event){
    this.set('activeView',event.view);
    this.set('activeAction',event.context);
    this.set('showOK',true);
    this.set('showCANCEL',true);

    switch (this.get('activeAction')) {
      case "delSG":
        if (FLOW.surveyGroupControl.containsSurveys()){
          this.set('activeAction',"ignore");
          this.set('header',Ember.String.loc('_SG_delete_not_possible_header'));
          this.set('message',Ember.String.loc('_SG_delete_not_possible_message'));
          this.set('showCANCEL',false);
          this.set('showDialog',true);
        } else {
          this.set('header',Ember.String.loc('_SG_delete_header'));
          this.set('message',Ember.String.loc('_SG_delete_message'));
          this.set('showDialog',true);
        }
        break;

      case "delS":
        this.set('header',Ember.String.loc('_S_delete_header'));
        this.set('message',Ember.String.loc('_S_delete_message'));
        this.set('showDialog',true);
        break;

      case "delQG":
        this.set('header',Ember.String.loc('_QG_delete_header'));
        this.set('message',Ember.String.loc('_QG_delete_message'));
        this.set('showDialog',true);
        break;

      case "delQ":
        this.set('header',Ember.String.loc('_Q_delete_header'));
        this.set('message',Ember.String.loc('_Q_delete_message'));
        this.set('showDialog',true);
        break;

      default:
    }
  },

  doOK:function(event){
    this.set('header',null);
    this.set('message',null);
    this.set('showCANCEL',true);
    this.set('showDialog',false);
    var view =  this.get('activeView');
    switch (this.get('activeAction')) {
      case "delSG":
        view.deleteSurveyGroup.apply(view,arguments);
        break;

      case "delS":
        view.deleteSurvey.apply(view,arguments);
        break;

      case "delQG":
        view.deleteQuestionGroup.apply(view,arguments);
        break;

      case "delQ":
        this.set('showDialog',false);
        view.deleteQuestion.apply(view,arguments);
        break;
      default:
    }
  },

  doCANCEL:function(event){
    this.set('showDialog',false);
  }
}),


// ***********************************************//
//                Data controllers
// ***********************************************//
FLOW.surveyGroupControl = Ember.ArrayController.create({
  content: null,

  populate: function () {
    this.set('content', FLOW.store.find(FLOW.SurveyGroup));
  },

  // checks if data store contains surveys within this survey group.
  // this is also checked server side.
  containsSurveys:function(){
    var surveys,sgId;
    surveys = FLOW.store.filter(FLOW.Survey,function(data) {
      sgId = FLOW.selectedControl.selectedSurveyGroup.get('id');
      if (data.get('surveyGroupId') == sgId) { return true; } });
    
    return (surveys.get('content').length > 0);
  }
});


FLOW.surveyControl = Ember.ArrayController.create({
  content: null,
  populate: function () {
    var id;
    if (FLOW.selectedControl.get('selectedSurveyGroup')) {
      id = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.Survey, {surveyGroupId: id}));
    }
  }.observes('FLOW.selectedControl.selectedSurveyGroup')
});


FLOW.questionGroupControl = Ember.ArrayController.create({
  sortProperties: ['order'],
  sortAscending: true,
  content: null,

  // true if all items have been saved
  // used in models.js
   allRecordsSaved: function () {
     var allSaved = true;
     FLOW.questionGroupControl.get('content').forEach(function (item) {
       if (item.get('isSaving')) {
         allSaved = false;
       }
     });
      return allSaved;
   }.property('content.@each.isSaving'),

  populate: function () {
    if (FLOW.selectedControl.get('selectedSurvey')) {
      var id = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.QuestionGroup, {surveyId: id}));
    }
  }.observes('FLOW.selectedControl.selectedSurvey')
});


FLOW.questionControl = Ember.ArrayController.create({
  content: null,

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
   }.property('content.@each.isSaving'),

  populate: function () {
    if (FLOW.selectedControl.get('selectedQuestionGroup')) {
      var id = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.Question, {questionGroupId: id}));
    }
  }.observes('FLOW.selectedControl.selectedQuestionGroup'),

  populateAllQuestions: function () {
    if (FLOW.selectedControl.get('selectedSurveyAllQuestions')) {
      var id = FLOW.selectedControl.selectedSurveyAllQuestions.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.Question, {surveyId: id,summaryOnly:"true"}));
    }
  }.observes('FLOW.selectedControl.selectedSurveyAllQuestions')
});


FLOW.placemarkControl = Ember.ArrayController.create({
  content: null,

  populate: function () {
    this.set('content', FLOW.store.findAll(FLOW.Placemark));
  }
});


FLOW.placemarkDetailControl = Ember.ArrayController.create({
  content: null,
  selectedDetailImage: null,
  selectedPointCode:null,

  populate: function (placemarkId) {
    if (typeof placemarkId === 'undefined') {
      this.set('content', null);
    } else {
      this.set('content', FLOW.store.find(FLOW.PlacemarkDetail, {"placemarkId": placemarkId}));
    }
  }
});


FLOW.optionControl = Ember.ArrayController.create({
});


FLOW.tableColumnControl = Ember.Object.create({
  sortProperties: null,
  sortAscending: true,
  selected: null,
  content: null
});


FLOW.deviceGroupControl = Ember.ArrayController.create({
  content: null,
  
  populate: function () {
    this.set('content', FLOW.store.findQuery(FLOW.DeviceGroup,{}));
  }


});


FLOW.deviceControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  selected: null,
  content: null,

  populate: function () {
    this.set('content', FLOW.store.findQuery(FLOW.Device,{}));
    this.set('sortProperties', ['phoneNumber']);
    this.set('sortAscending', true);
  },

  allAreSelected: function (key, value) {
    if (arguments.length === 2) {
      this.setEach('isSelected', value);
      return value;
    }
    else {
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }
  }.property('@each.isSelected'),

  atLeastOneSelected: function () {
    return this.filterProperty('isSelected', true).get('length');
  }.property('@each.isSelected'),

  // fired from tableColumnView.sort
  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});


FLOW.surveyAssignmentControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,

  populate: function () {
    this.set('content', FLOW.store.find(FLOW.SurveyAssignment));
    this.set('sortProperties', ['name']);
    this.set('sortAscending', true);
  },

  allAreSelected: function (key, value) {
    if (arguments.length === 2) {
      this.setEach('isSelected', value);
      return value;
    }
    else {
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }
  }.property('@each.isSelected'),

  atLeastOneSelected: function () {
    return this.filterProperty('isSelected', true).get('length');
  }.property('@each.isSelected'),

  getSortInfo:function(){
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
    this.set('selected', FLOW.tableColumnControl.get('selected'));
  }
});


// set by restadapter sideLoad meta
FLOW.metaControl = Ember.Object.create({
  since: null,
  num: null,
  message: null,
  status: null
}),


// set by javacript datepickers in views.js
FLOW.dateControl = Ember.Object.create({
  // filled by javacript datepicker defined in views.js and by inspect-data.handlebars
  // binding. This makes sure we can both pick a date with the datepicker, and enter
  // a date manually
  fromDate: null,
  toDate: null
}),


FLOW.forceObserverControl = Ember.Object.create({
  forceObserverBool:false
});


FLOW.savingMessageControl = Ember.Object.create({
  areSavingBool:false,
  areLoadingBool:false,

  checkSaving:function(){
     if (FLOW.store.defaultTransaction.buckets.inflight.list.get('length') > 0){
       this.set('areSavingBool',true);
     } else {
       this.set('areSavingBool',false);
     }
  }
}),


FLOW.surveyInstanceControl = Ember.ArrayController.create({
  sortProperties: ['collectionDate'],
  sortAscending: false,
  selectedSurvey: null,
  content: null,
  sinceArray:[],

  populate: function () {
    this.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance,{}));
  },

  doInstanceQuery:function(surveyId, deviceId, since, beginDate, endDate){
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance,{
      'surveyId':surveyId,
      'deviceId':deviceId,
      'since':since,
      'beginDate':beginDate,
      'endDate':endDate}));
  },

  allAreSelected: function (key, value) {
    if (arguments.length === 2) {
      this.setEach('isSelected', value);
      return value;
    }
    else {
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }
  }.property('@each.isSelected'),

  atLeastOneSelected: function () {
    return this.filterProperty('isSelected', true).get('length');
  }.property('@each.isSelected'),

  // fired from tableColumnView.sort
  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});


FLOW.questionAnswerControl = Ember.ArrayController.create({
  content:null,

  doQuestionAnswerQuery:function(surveyInstanceId){
    this.set('content', FLOW.store.findQuery(FLOW.QuestionAnswer,{
      'surveyInstanceId':surveyInstanceId}));
  }
});

FLOW.surveyQuestionSummaryControl = Ember.ArrayController.create({
  content:null,

  doSurveyQuestionSummaryQuery:function(questionId){
    this.set('content', FLOW.store.find(FLOW.SurveyQuestionSummary,{
      'questionId':questionId}));
  }
});

FLOW.chartDataControl = Ember.Object.create({
  questionText:"",
  chartData:[],
  total:null
});
