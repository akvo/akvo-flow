FLOW.inspectDataTableView = FLOW.View.extend({
  selectedSurvey: null,
  surveyId: null,
  deviceId: null,
  submitterName: null,
  beginDate: null,
  endDate: null,
  since: null,
  showEditSurveyInstanceWindowBool: false,
  selectedSurveyInstanceId: null,
  selectedSurveyInstanceNum: null,
  siString: null,

  init: function(){
     this._super();
     FLOW.selectedControl.set('selectedSurveyGroup',null);
     FLOW.selectedControl.set('selectedSurvey',null);
     FLOW.dateControl.set('toDate',null);
     FLOW.dateControl.set('fromDate',null);
     FLOW.locationControl.set('selectedLevel1',null);
     FLOW.locationControl.set('selectedLevel2',null);
   },

  // do a new query
  doFindSurveyInstances: function() {
    FLOW.surveyInstanceControl.get('sinceArray').clear();
    FLOW.surveyInstanceControl.set('pageNumber',-1);
    FLOW.metaControl.set('since', null);
    this.doNextPage();
  },

  doInstanceQuery: function() {
    this.set('beginDate', Date.parse(FLOW.dateControl.get('fromDate')));
    this.set('endDate', Date.parse(FLOW.dateControl.get('toDate')));

    // we shouldn't be sending NaN
    if(isNaN(this.get('beginDate'))) {
      this.set('beginDate', null);
    }
    if(isNaN(this.get('endDate'))) {
      this.set('endDate', null);
    }

    if(FLOW.selectedControl.get('selectedSurvey')) {
      this.set('surveyId', FLOW.selectedControl.selectedSurvey.get('keyId'));
    } else {
      this.set('surveyId',null);
    }

    this.set('since', FLOW.metaControl.get('since'));
    if(FLOW.selectedControl.get('selectedSurvey')) {
      FLOW.questionControl.populateAllQuestions(FLOW.selectedControl.selectedSurvey.get('keyId'));
    }
    FLOW.surveyInstanceControl.doInstanceQuery(this.get('surveyId'), this.get('deviceId'), this.get('since'), this.get('beginDate'), this.get('endDate'), this.get('submitterName'));
  },

  doNextPage: function() {
    FLOW.surveyInstanceControl.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.doInstanceQuery();
    FLOW.surveyInstanceControl.set('pageNumber',FLOW.surveyInstanceControl.get('pageNumber') + 1);
  },

  doPrevPage: function() {
    FLOW.surveyInstanceControl.get('sinceArray').popObject();
    FLOW.metaControl.set('since', FLOW.surveyInstanceControl.get('sinceArray')[FLOW.surveyInstanceControl.get('sinceArray').length - 1]);
    this.doInstanceQuery();
    FLOW.surveyInstanceControl.set('pageNumber',FLOW.surveyInstanceControl.get('pageNumber') - 1);
  },

  // If the number of items in the previous call was 20 (a full page) we assume that there are more.
  // This is not foolproof, but will only lead to an empty next page in 1/20 of the cases
  hasNextPage: function() {
    if(FLOW.metaControl.get('numSILoaded') == 20) {
      return true;
    } else {
      return false;
    }
  }.property('FLOW.metaControl.numSILoaded'),

  // not perfect yet, sometimes previous link is shown while there are no previous pages.
  hasPrevPage: function() {
    if(FLOW.surveyInstanceControl.get('sinceArray').length === 1) {
      return false;
    } else {
      return true;
    }
  }.property('FLOW.surveyInstanceControl.sinceArray.length'),

  createSurveyInstanceString: function() {
    var si;
    si = FLOW.store.find(FLOW.SurveyInstance, this.get('selectedSurveyInstanceId'));
    this.set('siString', si.get('surveyCode') + "/" + si.get('keyId') + "/" + si.get('submitterName'));
  },

  // Survey instance edit popup window
  // TODO solve when popup is open, no new surveyIdQuery is done
  showEditSurveyInstanceWindow: function(event) {
    FLOW.questionAnswerControl.doQuestionAnswerQuery(event.context.get('keyId'));
    FLOW.questionControl.doSurveyIdQuery(event.context.get('surveyId'));
    this.set('selectedSurveyInstanceId', event.context.get('keyId'));
    this.set('selectedSurveyInstanceNum', event.context.clientId);
    this.set('showEditSurveyInstanceWindowBool', true);
    this.createSurveyInstanceString();
  },

  doCloseEditSIWindow: function(event) {
    this.set('showEditSurveyInstanceWindowBool', false);
  },

  doPreviousSI: function(event) {
    var currentSIList, SIindex, nextItem, filtered, nextSIkeyId;
    currentSIList = FLOW.surveyInstanceControl.content.get('content');
    SIindex = currentSIList.indexOf(this.get('selectedSurveyInstanceNum'));

    if(SIindex === 0) {
      // if at the end of the list, go and get more data
    } else {
      nextItem = currentSIList.objectAt(SIindex - 1);
      filtered = FLOW.store.filter(FLOW.SurveyInstance, function(item) {
        if(item.clientId == nextItem) {
          return true;
        } else {
          return false;
        }
      });
      nextSIkeyId = filtered.objectAt(0).get('keyId');
      this.set('selectedSurveyInstanceId', nextSIkeyId);
      this.set('selectedSurveyInstanceNum', nextItem);
      this.createSurveyInstanceString();
      FLOW.questionAnswerControl.doQuestionAnswerQuery(nextSIkeyId);
    }
  },

  // TODO error checking
  doNextSI: function(event) {
    var currentSIList, SIindex, nextItem, filtered, nextSIkeyId;
    currentSIList = FLOW.surveyInstanceControl.content.get('content');
    SIindex = currentSIList.indexOf(this.get('selectedSurveyInstanceNum'));

    if(SIindex == 19) {
      // TODO get more data 
      // if at the end of the list, we should first go back and get more data
    } else {
      nextItem = currentSIList.objectAt(SIindex + 1);
      filtered = FLOW.store.filter(FLOW.SurveyInstance, function(item) {
        if(item.clientId == nextItem) {
          return true;
        } else {
          return false;
        }
      });
      nextSIkeyId = filtered.objectAt(0).get('keyId');
      this.set('selectedSurveyInstanceId', nextSIkeyId);
      this.set('selectedSurveyInstanceNum', nextItem);
      this.createSurveyInstanceString();
      FLOW.questionAnswerControl.doQuestionAnswerQuery(nextSIkeyId);
    }
  },

  doShowDeleteSIDialog: function(event) {
    FLOW.dialogControl.set('activeAction', 'delSI');
    FLOW.dialogControl.set('showCANCEL', true);
    FLOW.dialogControl.set('showDialog', true);
  },

  deleteSI: function(event) {
    var SI,SIid;
    SIid = this.get('selectedSurveyInstanceId');
    SI = FLOW.store.find(FLOW.SurveyInstance, SIid);
    if(SI !== null) {
      // remove from displayed content
      SI.deleteRecord();
      FLOW.store.commit();
    }
    this.set('showEditSurveyInstanceWindowBool', false);
  }
});

FLOW.DataItemView = FLOW.View.extend({
  tagName: 'span',
  deleteSI: function() {
    var SI;
    SI = FLOW.store.find(FLOW.SurveyInstance, this.content.get('keyId'));
    if(SI !== null) {
      SI.deleteRecord();
      FLOW.store.commit();
    }
  }
});

FLOW.DataNumView = FLOW.View.extend({
  tagName: 'span',
  content:null,
  rownum:null,

  init: function(){
    var index;
    index = FLOW.surveyInstanceControl.content.indexOf(this.get('content'));
    this.set('rownum',index + 1 + 20 * FLOW.surveyInstanceControl.get('pageNumber'));
  }
});
