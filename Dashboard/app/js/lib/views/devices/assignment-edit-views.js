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

function formatDate(value) {
  if(value > 0) {
    return value.getFullYear() + "/" + value.getMonth() + 1 + "/" + value.getDate();
  } else return null;
}

FLOW.AssignmentEditView = Em.View.extend({
  devicesPreview: Ember.A([]),
  surveysPreview: Ember.A([]),
  assignmentName: null,
  language: null,

  init: function() {
    var dId, deviceIds, previewDevices, surveyIds, previewSurveys, startDate = null,
      endDate = null;
    previewDevices = Ember.A([]);
    previewSurveys = Ember.A([]);
    this._super();
    this.set('assignmentName', FLOW.selectedControl.selectedSurveyAssignment.get('name'));
    FLOW.selectedControl.set('selectedDevices', null);
    FLOW.selectedControl.set('selectedSurveys', null);
    if(FLOW.selectedControl.selectedSurveyAssignment.get('startDate') > 0) {
      startDate = new Date(FLOW.selectedControl.selectedSurveyAssignment.get('startDate'));
    }
    if(FLOW.selectedControl.selectedSurveyAssignment.get('endDate') > 0) {
      endDate = new Date(FLOW.selectedControl.selectedSurveyAssignment.get('endDate'));
    }
    FLOW.dateControl.set('fromDate', formatDate(startDate));
    FLOW.dateControl.set('toDate', formatDate(endDate));

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
    var sa, endDateParse, startDateParse, devices = [],
      surveys = [];
    sa = FLOW.selectedControl.get('selectedSurveyAssignment');

    sa.set('name', this.get('assignmentName'));

    if(!Ember.none(FLOW.dateControl.get('toDate'))) {
      startDateParse = Date.parse(FLOW.dateControl.get('toDate'));
    } else {
      startDateParse = null;
    }

    if(!Ember.none(FLOW.dateControl.get('fromDate'))) {
      endDateParse = Date.parse(FLOW.dateControl.get('fromDate'));
    } else {
      endDateParse = null;
    }

    sa.set('endDate', endDateParse);
    sa.set('startDate', startDateParse);
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
    FLOW.router.transitionTo('navDevices.assignSurveysOverview');
  },

  cancelEditSurveyAssignment: function() {
    if(Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))) {
      FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
    }
    FLOW.selectedControl.set('selectedSurveyAssignment', null);
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
    var selected = Ember.A([]);
    FLOW.devicesInGroupControl.get('content').forEach(function(item) {
      selected.pushObject(item);
    });
    FLOW.selectedControl.set('selectedDevices', selected);
  },

  deselectAllDevices: function() {
    FLOW.selectedControl.set('selectedDevices', []);
  },

  selectAllSurveys: function() {
    var selected = Ember.A([]);
    FLOW.surveyControl.get('content').forEach(function(item) {
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