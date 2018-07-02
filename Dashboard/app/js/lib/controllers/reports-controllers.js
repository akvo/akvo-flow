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

FLOW.ReportsController = Ember.ArrayController.extend({
  sortProperties: ["createdDateTime"],
  sortAscending: false,
  content: null,
  reportsAvailable: false,

  populate: function () {
    this.set('content', FLOW.store.find(FLOW.Report));
  },

  reportsObserver: function () {
    var reports = this.get('content');
    if (reports && !reports.isUpdating) {
      this.set('reportsAvailable', reports.content.length > 0);

      var generatingReports = reports.filter(function(report) {
        return report.get('state') === "IN_PROGRESS" || report.get('state') === "QUEUED";
      });
      //if reports are still generating, wait 5s and then reload
      if (generatingReports.length > 0) {
        setTimeout(function(){
          FLOW.router.reportsController.set('content', FLOW.store.find(FLOW.Report));
        }, 5000);
      }
    }
  }.observes('content', 'content.isUpdating')
});
