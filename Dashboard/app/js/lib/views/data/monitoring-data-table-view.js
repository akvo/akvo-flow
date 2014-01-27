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
    FLOW.questionAnswerControl.doQuestionAnswerQuery(evt.context.get('keyId'));
    this.toggleProperty('showingSurveyInstanceDialog');
  },
  hideSurveyInstanceDialog: function (evt) {
    this.toggleProperty('showingSurveyInstanceDialog');
  }
});