FLOW.MonitoringDataTableView = FLOW.View.extend({
  showingDetailsDialog: false,

  showDetailsDialog: function (evt) {
	FLOW.surveyInstanceControl.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {
		surveyedLocaleId: evt.context.get('keyId')
	}));
    this.toggleProperty('showingDetailsDialog');
  },

  closeDetailsDialog: function () {
    this.toggleProperty('showingDetailsDialog');
  },

  showSurveyInstanceDetails: function (evt) {
    FLOW.questionAnswerControl.doQuestionAnswerQuery(evt.context.get('keyId'));
    $('.si_details').hide();
    $('tr[data-flow-id="si_details_' + evt.context.get('keyId') + '"]').show();
  },
  findSurveyedLocale: function (evt) {
	  var ident = this.get('identifier'),
	      displayName = this.get('displayName'),
	      sgId = this.get('selectedSurveyGroup'),
	      criteria = {};

	  if (ident) {
		  criteria.identifier = ident;
	  }

	  if (displayName) {
		  criteria.displayName = displayName;
	  }

	  if (sgId) {
		  criteria.surveyGroupId = sgId.get('keyId');
	  }

	  FLOW.surveyedLocaleControl.set('content', FLOW.store.findQuery(FLOW.SurveyedLocale, criteria));
  }
});