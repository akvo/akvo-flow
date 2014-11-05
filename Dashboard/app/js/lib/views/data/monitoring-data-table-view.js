FLOW.MonitoringDataTableView = FLOW.View.extend({
  showingDetailsDialog: false,
  since: null,

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
        since = FLOW.metaControl.get('since');
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

    if (since) {
      criteria.since = since;
    }

	  FLOW.surveyedLocaleControl.set('content', FLOW.store.findQuery(FLOW.SurveyedLocale, criteria));
  },

  doNextPage: function () {
    FLOW.surveyedLocaleControl.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.findSurveyedLocale();
    FLOW.surveyedLocaleControl.set('pageNumber', FLOW.surveyedLocaleControl.get('pageNumber') + 1);
  },

  doPrevPage: function () {
    FLOW.surveyedLocaleControl.get('sinceArray').popObject();
    FLOW.metaControl.set('since', FLOW.surveyedLocaleControl.get('sinceArray')[FLOW.surveyedLocaleControl.get('sinceArray').length - 1]);
    this.findSurveyedLocale();
    FLOW.surveyedLocaleControl.set('pageNumber', FLOW.surveyedLocaleControl.get('pageNumber') - 1);
  },

  hasNextPage: function () {
    return FLOW.metaControl.get('numSLLoaded') == 20;
  }.property('FLOW.metaControl.numSLLoaded'),

  hasPrevPage: function () {
    return FLOW.surveyedLocaleControl.get('sinceArray').length != 0;
  }.property('FLOW.surveyedLocaleControl.sinceArray.length'),
});
