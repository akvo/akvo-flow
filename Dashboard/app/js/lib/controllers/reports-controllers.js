
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
  maxPer:null,
  chartData: [],
  smallerItems:[],
  total: null
});

FLOW.chartTypeControl = Ember.Object.create({
  content: [
  Ember.Object.create({
    label: "Doughnut chart",
    value: "doughnut"
  }), Ember.Object.create({
    label: "Vertical bar chart",
    value: "vbar"
  }),
  Ember.Object.create({
    label: "Horizontal bar chart",
    value: "hbar"
  })]
});