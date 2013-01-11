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
  expirationDate: null,

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

  },

  deselectAllDevices: function() {

  },

  selectAllSurveys: function() {

  },

  deselectAllSurveys: function() {

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
  },

  saveSurveyAssignment: function() {

  },

  cancelSurveyAssignment: function() {

  }

});