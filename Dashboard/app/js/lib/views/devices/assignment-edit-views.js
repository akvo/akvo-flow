FLOW.AssignmentEditView = Em.View.extend({
  selectedDevicesPreview:null,
  selectedSurveysPreview:null,
  assignmentName:null,
  startDate:null,
  expirationDate:null,

  addSelectedDevices: function() {
    this.set('selectedDevicesPreview',FLOW.selectedControl.selectedDevicesPreview);
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