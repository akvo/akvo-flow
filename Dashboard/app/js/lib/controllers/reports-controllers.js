FLOW.surveyQuestionSummaryControl = Ember.ArrayController.create({
  content: null,

  doSurveyQuestionSummaryQuery: function (questionId) {
    this.set('content', FLOW.store.find(FLOW.SurveyQuestionSummary, {
      'questionId': questionId
    }));
  }
});

FLOW.chartDataControl = Ember.Object.create({
  questionText: "",
  maxPer: null,
  chartData: [],
  smallerItems: [],
  total: null
});

FLOW.chartTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_doughnut_chart'),
      value: "doughnut"
    }), Ember.Object.create({
      label: Ember.String.loc('_vertical_bar_chart'),
      value: "vbar"
    }),
    Ember.Object.create({
      label: Ember.String.loc('_horizontal_bar_chart'),
      value: "hbar"
    })
  ]
});

FLOW.reportsControl = Ember.ArrayController.create({
  sortProperties: ["lastModifiedDate"],
  sortAscending: true,
  content: null,

  populate: function () {
    this.set('content', FLOW.store.find(FLOW.Report));
  }
});
