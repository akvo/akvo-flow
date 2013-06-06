
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

FLOW.statisticsControl = Ember.ArrayController.create({
  selectedSurvey: null,
  allreadyTriggered: false,
  content:null,
  QAcontent:null,
  sortProperties: ['name'],
  sortAscending: true,
  totalsSurveys:[],
  total:null,

  computeTotal: function(){
    this.set('total',Math.max.apply(Math, this.get('totalsSurveys')));
  },

  getMetrics: function(){
    if (!Ember.none(this.get('selectedSurvey'))){
      this.set('totalsSurveys',[]);
      this.set('total',null);
      this.set('alreadyTriggered',false);
      this.set('content',FLOW.store.findQuery(FLOW.Metric,{
        surveyId: this.selectedSurvey.get('keyId')
      }));
    }
  }.observes('this.selectedSurvey'),

  resetSurvey: function(){
    this.set('selectedSurvey',null);
  }.observes('FLOW.selectedControl.selectedSurveyGroup'),

  getQA: function(){
    if (!Ember.none(this.get('content') && !this.get('allreadyTriggered'))){
      this.set('totalsSurveys',[]);
      this.set('total',null);
      // for each metric, get all the QuestionAnswerSummery objects of the questions
      // this could be a single call: give me all he QA summ for questions with a metric.
      this.set('QAcontent',FLOW.store.findQuery(FLOW.SurveyQuestionSummary,{
        surveyId:this.selectedSurvey.get('keyId'),
        metricOnly:"true"
      }));
      allreadyTriggered = true;
    }
  }.observes('this.content.isLoaded')

});