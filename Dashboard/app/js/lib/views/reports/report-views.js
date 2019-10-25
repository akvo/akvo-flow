/* global deleteChart, createDoughnutChart, createHBarChart, createVBarChart */
import observe from '../../mixins/observe';

FLOW.chartView = FLOW.View.extend(observe({
  'this.selectedSurvey': 'downloadOptionQuestions',
  'FLOW.surveyQuestionSummaryControl.content.isLoaded': 'buildChart',
}), {
  noChoiceBool: false,
  noDataBool: false,
  chartType: null,
  compactSmaller: true,
  selectedSurvey: null,

  downloadOptionQuestions() {
    if (!Ember.none(this.get('selectedSurvey'))) {
      FLOW.questionControl.downloadOptionQuestions(this.selectedSurvey.get('keyId'));
    }
  },

  isDoughnut: Ember.computed(function () {
    return this.chartType.get('value') == 'doughnut';
  }).property('this.chartType'),

  init() {
    this._super();
    this.chartType = FLOW.chartTypeControl.content[0];
  },

  hideChart: Ember.computed(function () {
    return this.get('noChoiceBool') || this.get('noDataBool');
  }).property('noChoiceBool', 'noDataBool'),

  getChartData() {
    this.set('noChoiceBool', false);
    if (FLOW.selectedControl.get('selectedQuestion') !== null) {
      FLOW.surveyQuestionSummaryControl.doSurveyQuestionSummaryQuery(FLOW.selectedControl.selectedQuestion.get('keyId'));
      FLOW.chartDataControl.set('questionText', FLOW.selectedControl.selectedQuestion.get('text'));
    } else {
      this.set('noChoiceBool', true);
    }
  },

  buildChart() {
    const chartData = [];
    const smallerItems = [];
    let total = 0;
    let max = 0;
    let maxPer;
    let i;
    let tot;
    let totPerc;

    deleteChart();

    if (FLOW.surveyQuestionSummaryControl.content.get('isLoaded') === true) {
      FLOW.chartDataControl.set('total', FLOW.surveyQuestionSummaryControl.content.get('length'));
      if (FLOW.chartDataControl.get('total') == 0) {
        this.set('noDataBool', true);
        return;
      }
      this.set('noDataBool', false);


      FLOW.surveyQuestionSummaryControl.get('content').forEach((item) => {
        total += item.get('count');
        if (item.get('count') > max) max = item.get('count');
      });

      // set the maximum of the scale
      maxPer = 100.0 * max / total;

      // if type is doughnut, do doughnut things
      if (this.chartType.get('value') == 'doughnut') {
        i = -1;
        tot = 0;
        totPerc = 0;

        FLOW.surveyQuestionSummaryControl.get('content').forEach((item) => {
          const percentage = 100.0 * item.get('count') / total;
          const percString = percentage.toFixed(1);
          chartData.push({
            legendLabel: `${item.get('response')}, ${percString}% (${item.get('count')})`,
            percentage: 100.0 * item.get('count') / total,
          });
        });

        // sort smallest first
        chartData.sort((a, b) => a.percentage - b.percentage);


        if (this.get('compactSmaller')) {
          chartData.forEach((item) => {
            if ((totPerc < 5 || item.percentage < 5) && (item.percentage < 7)) {
              totPerc += item.percentage;
              i += 1;
            }
          });

          tot = 0;

          for (let ii = 0; ii <= i; ii++) {
            smallerItems.push(chartData[ii]);
            tot += chartData[ii].percentage;
          }

          // delete smallest items from chartData
          chartData.splice(0, i + 1);

          // add new item with the size of the smallest items
          chartData.splice(0, 0, {
            legendLabel: (`${Ember.String.loc('_smallest_items')}, ${tot.toFixed(1)}%`),
            percentage: tot,
          });
        }
        FLOW.chartDataControl.set('chartData', chartData);
        FLOW.chartDataControl.set('smallerItems', smallerItems);
        FLOW.chartDataControl.set('total', total);

        createDoughnutChart();

        // if type vbar, do vbar things
      } else if (this.chartType.get('value') == 'vbar') {
        FLOW.surveyQuestionSummaryControl.get('content').forEach((item) => {
          chartData.push({
            legendLabel: (item.get('response')),
            percentage: 100.0 * item.get('count') / total,
            itemCount: item.get('count'),
          });
        });

        // sort smallest first
        chartData.sort((a, b) => a.percentage - b.percentage);
        FLOW.chartDataControl.set('chartData', chartData);
        FLOW.chartDataControl.set('maxPer', maxPer);
        createVBarChart();

        // if type hbar, do hbar things
      } else if (this.chartType.get('value') == 'hbar') {
        FLOW.surveyQuestionSummaryControl.get('content').forEach((item) => {
          chartData.push({
            legendLabel: (item.get('response')),
            percentage: 100.0 * item.get('count') / total,
            itemCount: item.get('count'),
          });
        });

        // sort smallest first
        chartData.sort((a, b) => a.percentage - b.percentage);
        FLOW.chartDataControl.set('chartData', chartData);
        FLOW.chartDataControl.set('maxPer', maxPer);
        createHBarChart();
      }
    }
  },
});
