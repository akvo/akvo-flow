FLOW.MonitoringDataTableView = FLOW.View.extend({
  showingDetailsDialog: false,
  showingSurveyInstanceDialog: false,

  showDetailsDialog: function (evt) {
    this.toggleProperty('showingDetailsDialog');
  },

  closeDetailsDialog: function () {
    this.toggleProperty('showingDetailsDialog');
  },

  showSurveyInstanceDialog: function (evt) {
    this.toggleProperty('showingSurveyInstanceDialog');
  },
  hideSurveyInstanceDialog: function (evt) {
    this.toggleProperty('showingSurveyInstanceDialog');
  }
});