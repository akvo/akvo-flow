/*global deleteChart, createChart */

FLOW.chartView = Em.View.extend({

  getChartData:function () {
    if (FLOW.selectedControl.get('selectedQuestion') !== null){
      FLOW.surveyQuestionSummaryControl.doSurveyQuestionSummaryQuery(FLOW.selectedControl.selectedQuestion.get('keyId'));
      FLOW.chartDataControl.set('questionText',FLOW.selectedControl.selectedQuestion.get('text'));
    }
  },

  buildChart:function () {
    var chartData = [], total = 0;
    if (FLOW.surveyQuestionSummaryControl.content.get('isLoaded') === true){
      FLOW.chartDataControl.set('total',FLOW.surveyQuestionSummaryControl.content.get('length'));
      FLOW.surveyQuestionSummaryControl.get('content').forEach(function(item) {
        total = total + item.get('count');
      });

      FLOW.surveyQuestionSummaryControl.get('content').forEach(function(item) {
        var percentage = 100*item.get('count')/total,
        percString = percentage.toFixed(1);
        chartData.push({"legendLabel":(item.get('response') + "," + percString + "%"), "magnitude":item.get('count')});
        total = total + item.get('count');
      });
      chartData.sort(function(a,b){
        return a.magnitude - b.magnitude;
      });
      FLOW.chartDataControl.set('chartData',chartData);
      FLOW.chartDataControl.set('total',total);
      deleteChart();
      createChart();
    }
  }.observes('FLOW.surveyQuestionSummaryControl.content.isLoaded'),

  showRawDataReport: function () {
    if (!FLOW.selectedControl.selectedSurveyAllQuestions) {
      return;
    }
    FLOW.set('showRawDataReportApplet', true);
  },

  showComprehensiveReport: function () {
    if (!FLOW.selectedControl.selectedSurveyAllQuestions) {
      return;
    }
    FLOW.set('showComprehensiveReportApplet', true);
  },

  showGoogleEarthFile: function () {
    FLOW.set('showGoogleEarthFileApplet', true);
  }
});