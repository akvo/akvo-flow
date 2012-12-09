FLOW.inspectDataTableView = Em.View.extend({

  doFindSurveyInstances: function(){
		FLOW.surveyInstanceControl.doNewInstanceQuery();
  }
});