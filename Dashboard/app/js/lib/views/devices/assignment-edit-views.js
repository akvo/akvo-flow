// removes duplicate objects with a clientId from an Ember Array

function ArrNoDupe(a) {
  var templ, i, item, gotIt, tempa;
  templ = {};
  tempa = Ember.A([]);
  for(i = 0; i < a.length; i++) {
    templ[a.objectAt(i).clientId] = true;
  }
  for(item in templ) {
    gotIt = false;
    for(i = 0; i < a.length; i++) {
      if(a.objectAt(i).clientId == item && !gotIt) {
        tempa.pushObject(a.objectAt(i));
        gotIt = true;
      }
    }
  }
  return tempa;
}

FLOW.AssignmentEditView = Em.View.extend({
  devicesPreview: Ember.A([]),
  surveysPreview: Ember.A([]),
  assignmentName: null,
  startDate: null,
  endDate: null,
  language: null,

  init: function() {
    var dId, deviceIds, previewDevices, surveyIds, previewSurveys;
    previewDevices = Ember.A([]);
    previewSurveys = Ember.A([]);
    this._super();
    this.set('assignmentName', FLOW.selectedControl.selectedSurveyAssignment.get('name'));
    this.set('startDate', FLOW.selectedControl.selectedSurveyAssignment.get('startDate'));
    this.set('endDate', FLOW.selectedControl.selectedSurveyAssignment.get('endDate'));
    this.set('language', FLOW.selectedControl.selectedSurveyAssignment.get('language'));

    deviceIds = Ember.A(FLOW.selectedControl.selectedSurveyAssignment.get('devices'));

    deviceIds.forEach(function(item) {
      previewDevices.pushObjects(FLOW.store.find(FLOW.Device, item));
    });
    this.set('devicesPreview', previewDevices);

    surveyIds = Ember.A(FLOW.selectedControl.selectedSurveyAssignment.get('surveys'));

    surveyIds.forEach(function(item) {
      previewSurveys.pushObjects(FLOW.store.find(FLOW.Survey, item));
    });
    this.set('surveysPreview', previewSurveys);
  },

  saveSurveyAssignment: function() {
    var sa, devices = [],
      surveys = [];
    sa = FLOW.selectedControl.get('selectedSurveyAssignment');

    sa.set('name',this.get('assignmentName'));
    sa.set('endDate', this.get('endDate'));
    sa.set('startDate', this.get('startDate'));
    sa.set('language', 'en');

    this.get('devicesPreview').forEach(function(item) {
      devices.push(item.get('keyId'));
    });
    sa.set('devices', devices);

    this.get('surveysPreview').forEach(function(item) {
      surveys.push(item.get('keyId'));
    });
    sa.set('surveys', surveys);

    FLOW.store.commit();
  },

  cancelEditSurveyAssignment: function() {
    FLOW.router.transitionTo('navDevices.assignSurveysOverview');
  },

  addSelectedDevices: function() {
    this.devicesPreview.pushObjects(FLOW.selectedControl.get('selectedDevices'));
    // delete duplicates
    this.set('devicesPreview', ArrNoDupe(this.get('devicesPreview')));
  },

  addSelectedSurveys: function() {
    var sgName;
    sgName = FLOW.selectedControl.selectedSurveyGroup.get('code');
    FLOW.selectedControl.get('selectedSurveys').forEach(function(item) {
      item.set('surveyGroupName', sgName);
    });
    this.surveysPreview.pushObjects(FLOW.selectedControl.get('selectedSurveys'));
    // delete duplicates
    this.set('surveysPreview', ArrNoDupe(this.get('surveysPreview')));
  },

  selectAllDevices: function() {
    FLOW.selectedControl.set('selectedDevices', FLOW.devicesInGroupControl.get('content'));
  },

  deselectAllDevices: function() {
    FLOW.selectedControl.set('selectedDevices', []);
  },

  selectAllSurveys: function() {
    var selected=Ember.A([]);
    FLOW.surveyControl.get('content').forEach(function(item){
      selected.pushObject(item);
    });
    FLOW.selectedControl.set('selectedSurveys', selected);
  },

  deselectAllSurveys: function() {
    FLOW.selectedControl.set('selectedSurveys', []);
  },

  removeSingleSurvey: function(event) {
    var id, surveysPreview, i;
    id = event.context.get('clientId');
    surveysPreview = this.get('surveysPreview');
    for(i = 0; i < surveysPreview.length; i++) {
      if(surveysPreview.objectAt(i).clientId == id) {
        surveysPreview.removeAt(i);
      }
    }
    this.set('surveysPreview', surveysPreview);
  },

  removeAllSurveys: function() {
    this.set('surveysPreview', Ember.A([]));
  },

  removeSingleDevice: function(event) {
    var id, devicesPreview, i;
    id = event.context.get('clientId');
    devicesPreview = this.get('devicesPreview');
    for(i = 0; i < devicesPreview.length; i++) {
      if(devicesPreview.objectAt(i).clientId == id) {
        devicesPreview.removeAt(i);
      }
    }
    this.set('devicesPreview', devicesPreview);
  },

  removeAllDevices: function() {
    this.set('devicesPreview', Ember.A([]));
  }
});