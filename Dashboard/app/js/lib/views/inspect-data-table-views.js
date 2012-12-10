FLOW.inspectDataTableView = Em.View.extend({
  selectedSurvey: null,
  surveyId: null,
  deviceId: null,
  beginDate: null,
  endDate: null,
  since: null,
  sinceArray:[],

  // do a new query
  doFindSurveyInstances: function(){
    this.get('sinceArray').clear();
    FLOW.metaControl.set('since',null);
    this.doNextPage();
  },

  doInstanceQuery:function(){
    this.set('beginDate',FLOW.dateControl.get('fromDate'));
    this.set('endDate',FLOW.dateControl.get('toDate'));
	if (FLOW.selectedControl.get('selectedSurvey')){
      this.set('surveyId',FLOW.selectedControl.selectedSurvey.get('keyId'));
    }
    this.set('since', FLOW.metaControl.get('since'));
    FLOW.surveyInstanceControl.doInstanceQuery(this.get('surveyId'),this.get('deviceId'),this.get('since'),this.get('beginDate'),this.get('endDate'));
},

  doNextPage: function(){
    this.sinceArray.pushObject(FLOW.metaControl.get('since'));
    this.doInstanceQuery();
  },

  doPrevPage: function(){
    this.sinceArray.popObject();
    FLOW.metaControl.set('since',this.sinceArray[this.sinceArray.length-1]);
    this.doInstanceQuery();
  },

  // If the number of items in the previous call was 20 (a full page) we assume that there are more.
  // This is not foolproof, but will only lead to an empty next page in 1/20 of the cases
  hasNextPage: function(){
    if (FLOW.metaControl.get('num') == 20) {return true;} else {return false;}
  }.property('FLOW.metaControl.num'),

  // not perfect yet, sometimes previous link is shown while there are no previous pages.
  hasPrevPage:function(){
    if (this.get('sinceArray').length === 0){return false;} else {return true;}
   }.property('this.sinceArray.length')
});
