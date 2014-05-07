/*global deleteChart, createDoughnutChart, createHBarChart, createVBarChart*/


FLOW.StatisticsMetricView = FLOW.View.extend({
  answers: null,

  fillAnswers: function(){
    var qId, total, percentage;
    qId = this.content.get('questionId');
    SQS = FLOW.store.filter(FLOW.SurveyQuestionSummary,function(item){
      return (item.get('questionId') == qId);
    });
    this.set('answers',SQS);
    total = 0;
    SQS.forEach(function(item){
      total += item.get('count');
    });
    SQS.forEach(function(item){
      percentage = 100 * item.get('count')/total
      item.set('percentage',percentage.toFixed(1));
    });
    FLOW.statisticsControl.get('totalsSurveys').push(total);
    FLOW.statisticsControl.computeTotal();
  }.observes('FLOW.statisticsControl.QAcontent.content.isLoaded')
}),


FLOW.chartView = FLOW.View.extend({
  noChoiceBool: false,
  noDataBool: false,
  chartType: null,
  compactSmaller: true,
  selectedSurvey: null,
  
  downloadOptionQuestions: function () {
	  if (!Ember.none(this.get('selectedSurvey'))) {
		  FLOW.questionControl.downloadOptionQuestions(this.selectedSurvey.get('keyId'));
	  }
  }.observes('this.selectedSurvey'),

  isDoughnut: function () {
    return this.chartType.get('value') == 'doughnut';
  }.property('this.chartType'),

  init: function () {
    this._super();
    this.chartType = FLOW.chartTypeControl.content[0];
  },

  hideChart: function () {
	  return this.get('noChoiceBool') || this.get('noDataBool');
  }.property('noChoiceBool', 'noDataBool'),

  getChartData: function () {
    this.set('noChoiceBool', false);
    if (FLOW.selectedControl.get('selectedQuestion') !== null) {
      FLOW.surveyQuestionSummaryControl.doSurveyQuestionSummaryQuery(FLOW.selectedControl.selectedQuestion.get('keyId'));
      FLOW.chartDataControl.set('questionText', FLOW.selectedControl.selectedQuestion.get('text'));
    } else {
      this.set('noChoiceBool', true);
    }
  },

  buildChart: function () {
    var chartData = [],
      smallerItems = [],
      total = 0,
      max = 0,
      maxPer, i, tot, totPerc;

    deleteChart();

    if (FLOW.surveyQuestionSummaryControl.content.get('isLoaded') === true) {
      FLOW.chartDataControl.set('total', FLOW.surveyQuestionSummaryControl.content.get('length'));
      if (FLOW.chartDataControl.get('total') == 0) {
          this.set('noDataBool', true);
    	  return;
      } else {
    	  this.set('noDataBool', false);
      }

      FLOW.surveyQuestionSummaryControl.get('content').forEach(function (item) {
        total = total + item.get('count');
        if (item.get('count') > max) max = item.get('count');
      });

      // set the maximum of the scale
      maxPer = 100.0 * max / total;

      // if type is doughnut, do doughnut things
      if (this.chartType.get('value') == 'doughnut') {
        i = -1;
        tot = 0;
        totPerc = 0;

        FLOW.surveyQuestionSummaryControl.get('content').forEach(function (item) {
          var percentage = 100.0 * item.get('count') / total,
            percString = percentage.toFixed(1);
          chartData.push({
            "legendLabel": (item.get('response') + ", " + percString + "%"),
            "percentage": 100.0 * item.get('count') / total
          });
        });

        // sort smallest first
        chartData.sort(function (a, b) {
        	return a.percentage - b.percentage;
        });


        if (this.get('compactSmaller')) {
          chartData.forEach(function (item) {
            if ((totPerc < 5 || item.percentage < 5) && (item.percentage < 7)) {
              totPerc = totPerc + item.percentage;
              i = i + 1;
            }
          });

          tot = 0;

          for (var ii = 0; ii <= i; ii++) {
            smallerItems.push(chartData[ii]);
            tot = tot + chartData[ii].percentage;
          }

          // delete smallest items from chartData
          chartData.splice(0, i + 1);

          // add new item with the size of the smallest items
          chartData.splice(0, 0, {
            "legendLabel": (Ember.String.loc('_smallest_items') + ", " + tot.toFixed(1) + "%"),
            "percentage": tot
          });
        }
        FLOW.chartDataControl.set('chartData', chartData);
        FLOW.chartDataControl.set('smallerItems', smallerItems);
        FLOW.chartDataControl.set('total', total);

        createDoughnutChart();

        // if type vbar, do vbar things
      } else if (this.chartType.get('value') == 'vbar') {
        FLOW.surveyQuestionSummaryControl.get('content').forEach(function (item) {
          chartData.push({
            "legendLabel": (item.get('response')),
            "percentage": 100.0 * item.get('count') / total
          });
        });

        // sort smallest first
        chartData.sort(function (a, b) {
        	return a.percentage - b.percentage;
        });
        FLOW.chartDataControl.set('chartData', chartData);
        FLOW.chartDataControl.set('maxPer', maxPer);
        createVBarChart();

        // if type hbar, do hbar things
      } else if (this.chartType.get('value') == 'hbar') {

        FLOW.surveyQuestionSummaryControl.get('content').forEach(function (item) {
          chartData.push({
            "legendLabel": (item.get('response')),
            "percentage": 100.0 * item.get('count') / total
          });
        });

        // sort smallest first
        chartData.sort(function (a, b) {
        	return a.percentage - b.percentage;
        });
        FLOW.chartDataControl.set('chartData', chartData);
        FLOW.chartDataControl.set('maxPer', maxPer);
        createHBarChart();
      }
    }
  }.observes('FLOW.surveyQuestionSummaryControl.content.isLoaded')
});
