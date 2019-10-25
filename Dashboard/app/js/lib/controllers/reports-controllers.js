import observe from '../mixins/observe';

FLOW.surveyQuestionSummaryControl = Ember.ArrayController.create({
  content: null,

  doSurveyQuestionSummaryQuery(questionId) {
    this.set('content', FLOW.store.find(FLOW.SurveyQuestionSummary, {
      questionId,
    }));
  },
});

FLOW.chartDataControl = Ember.Object.create({
  questionText: '',
  maxPer: null,
  chartData: [],
  smallerItems: [],
  total: null,
});

FLOW.chartTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_doughnut_chart'),
      value: 'doughnut',
    }), Ember.Object.create({
      label: Ember.String.loc('_vertical_bar_chart'),
      value: 'vbar',
    }),
    Ember.Object.create({
      label: Ember.String.loc('_horizontal_bar_chart'),
      value: 'hbar',
    }),
  ],
});

FLOW.ReportsController = Ember.ArrayController.extend(observe({
  content: 'reportsObserver',
  'content.isUpdating': 'reportsObserver',
}), {
  sortProperties: ['createdDateTime'],
  sortAscending: false,
  content: null,
  reportsAvailable: false,
  reportsCheckScheduled: false,

  populate() {
    this.set('content', FLOW.store.find(FLOW.Report));
  },

  reportsObserver() {
    const reports = this.get('content');
    let reportsQuery = null;
    const self = this;
    if (reports && !reports.isUpdating) {
      this.set('reportsAvailable', reports.content.length > 0);

      if (!this.get('reportsCheckScheduled')) {
        this.set('reportsCheckScheduled', true);
        // if reports are still generating, wait 5s and then recheck
        reportsQuery = setInterval(() => {
          const stillGeneratingReports = self.get('content').filter(report => report.get('state') === 'IN_PROGRESS' || report.get('state') === 'QUEUED');

          if (stillGeneratingReports.length > 0) {
            self.set('content', FLOW.store.find(FLOW.Report));
            self.refreshList();
          } else {
            self.set('reportsCheckScheduled', false);
            clearInterval(reportsQuery);
            self.refreshList();
          }
        }, 10000);
      }
    }
  },

  refreshList() {
    const reports = this.get('content');
    reports.forEach((report) => {
      const reportState = report.get('state');
      if (reportState != 'IN_PROGRESS' && reportState != 'QUEUED') {
        $(`#list-${report.get('keyId')}`).removeClass('exportGenerating');
        $(`#link-${report.get('keyId')}`).attr('href', report.get('filename') !== '' ? report.get('filename') : '#');
        $(`#filename-${report.get('keyId')}`).html(report.get('filename') !== '' ? FLOW.reportFilename(report.get('filename')) : '');
      }
    });
  },
});
