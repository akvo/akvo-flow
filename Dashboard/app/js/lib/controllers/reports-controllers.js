
FLOW.surveyQuestionSummaryControl = Ember.ArrayController.create({
  content: null,

  doSurveyQuestionSummaryQuery: function(questionId) {
    this.set('content', FLOW.store.find(FLOW.SurveyQuestionSummary, {
      'questionId': questionId
    }));
  }
});

FLOW.chartDataControl = Ember.Object.create({
  questionText: "",
  chartData: [],
  total: null
});