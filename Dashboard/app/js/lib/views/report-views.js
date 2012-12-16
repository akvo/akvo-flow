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
        chartData.push({"legendLabel":item.get('response'), "magnitude":item.get('count')});
        total = total + item.get('count');
      });
      FLOW.chartDataControl.set('chartData',chartData);
      FLOW.chartDataControl.set('total',total);
      deleteChart();
      createChart();
    }
  }.observes('FLOW.surveyQuestionSummaryControl.content.isLoaded')

});