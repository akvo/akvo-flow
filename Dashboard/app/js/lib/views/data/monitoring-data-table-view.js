FLOW.MonitoringDataTableView = FLOW.View.extend({
  showingDetailsDialog: false,

  showDetailsDialog: function (evt) {
    this.toggleProperty('showingDetailsDialog');
  },

  closeDetailsDialog: function () {
    this.toggleProperty('showingDetailsDialog');
  },

  showSurveyInstanceDialog: function (evt) {
    FLOW.questionAnswerControl.doQuestionAnswerQuery(evt.context.get('keyId'));
    $('.si_details').hide();
    $('tr[data-flow-id="si_details_' + evt.context.get('keyId') + '"]').show();
  },
  hideSurveyInstanceDialog: function (evt) {
    this.toggleProperty('showingSurveyInstanceDialog');
  }
});