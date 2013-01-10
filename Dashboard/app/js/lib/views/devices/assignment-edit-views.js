function ArrNoDupe(a) {
  var templ, i, item, gotIt, tempa;
  templ = {};
  tempa = [];
  for(i = 0; i < a.length; i++) {
    templ[a[i].clientId] = true;
  }
  for(item in templ) {
    gotIt = false;
    for(i = 0; i < a.length; i++) {
      if(a[i].clientId == item && !gotIt) {
        tempa.push(a[i]);
        gotIt = true;
      }
    }
  }
  return tempa;
}

FLOW.AssignmentEditView = Em.View.extend({
  selectedDevicesPreview: [],
  selectedSurveysPreview: [],
  assignmentName: null,
  startDate: null,
  expirationDate: null,

  addSelectedDevices: function() {
    this.set('selectedDevicesPreview', this.get('selectedDevicesPreview').concat(FLOW.selectedControl.get('selectedDevices')));
    // delete duplicates
    this.set('selectedDevicesPreview',ArrNoDupe(this.get('selectedDevicesPreview')));

  },

  addSelectedSurveys: function() {

  },

  selectAllDevices: function() {

  },

  deselectAllDevices: function() {

  },

  selectAllSurveys: function() {

  },

  deselectAllSurveys: function() {

  },

  removeSingleSurvey: function() {

  },

  removeAllSurveys: function() {

  },

  removeSingleDevice: function() {

  },

  removeAllDevices: function() {

  },

  saveSurveyAssignment: function() {

  },

  cancelSurveyAssignment: function() {

  }

});