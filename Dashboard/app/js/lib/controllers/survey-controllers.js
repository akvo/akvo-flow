FLOW.questionTypeControl = Ember.Object.create({
  content: [
  Ember.Object.create({
    label: "Free text",
    value: "FREE_TEXT"
  }), Ember.Object.create({
    label: "Option",
    value: "OPTION"
  }), Ember.Object.create({
    label: "Number",
    value: "NUMBER"
  }), Ember.Object.create({
    label: "Geolocation",
    value: "GEO"
  }), Ember.Object.create({
    label: "Photo",
    value: "PHOTO"
  }), Ember.Object.create({
    label: "Video",
    value: "VIDEO"
  }), Ember.Object.create({
    label: "Date",
    value: "DATE"
  }), Ember.Object.create({
    label: "Barcode",
    value: "BARCODE"
  })]
});


FLOW.surveyPointTypeControl = Ember.Object.create({
  content: [
  Ember.Object.create({
    label: "Point",
    value: "Point"
  }), Ember.Object.create({
    label: "Household",
    value: "Household"
  }), Ember.Object.create({
    label: "Public institution",
    value: "PublicInstitution"
  })]
});

FLOW.surveySectorTypeControl = Ember.Object.create({
  content: [
  Ember.Object.create({
    label: "Water and Sanitation",
    value: "WASH"
  }), Ember.Object.create({
    label: "Education",
    value: "EDUC"
  }), Ember.Object.create({
    label: "Economic development",
    value: "ECONDEV"
  }), Ember.Object.create({
    label: "Health care",
    value: "HEALTH"
  }), Ember.Object.create({
    label: "IT and Communication",
    value: "ICT"
  }), Ember.Object.create({
    label: "Food security",
    value: "FOODSEC"
  }), Ember.Object.create({
    label: "Other",
    value: "OTHER"
  })]
});


FLOW.surveyGroupControl = Ember.ArrayController.create({
  sortProperties: ['code'],
  sortAscending: true,
  content: null,

  populate: function() {
    this.set('content', FLOW.store.find(FLOW.SurveyGroup));
  },

  // checks if data store contains surveys within this survey group.
  // this is also checked server side.
  containsSurveys: function() {
    var surveys, sgId;
    surveys = FLOW.store.filter(FLOW.Survey, function(data) {
      sgId = FLOW.selectedControl.selectedSurveyGroup.get('id');
      if(data.get('surveyGroupId') == sgId) {
        return true;
      }
    });

    return(surveys.get('content').length > 0);
  }
});


FLOW.surveyControl = Ember.ArrayController.create({
  content: null,

  populate: function() {
    var id;
    if(FLOW.selectedControl.get('selectedSurveyGroup')) {
      id = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.Survey, {
        surveyGroupId: id
      }));
    }
  }.observes('FLOW.selectedControl.selectedSurveyGroup')
});


FLOW.questionGroupControl = Ember.ArrayController.create({
  sortProperties: ['order'],
  sortAscending: true,
  content: null,

  // true if all items have been saved
  // used in models.js
  allRecordsSaved: function() {
    var allSaved = true;
    FLOW.questionGroupControl.get('content').forEach(function(item) {
      if(item.get('isSaving')) {
        allSaved = false;
      }
    });
    return allSaved;
  }.property('content.@each.isSaving'),

  populate: function() {
    if(FLOW.selectedControl.get('selectedSurvey')) {
      var id = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.QuestionGroup, {
        surveyId: id
      }));
    }
  }.observes('FLOW.selectedControl.selectedSurvey')
});


FLOW.questionControl = Ember.ArrayController.create({
  content: null,
  OPTIONcontent: null,
  earlierOptionQuestions: null,
  QGcontent: null,

  populate: function() {
    if(FLOW.selectedControl.get('selectedQuestionGroup')) {
      var id = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.Question, {
        questionGroupId: id
      }));
    }
  }.observes('FLOW.selectedControl.selectedQuestionGroup'),

  populateAllQuestions: function() {
    var sId;
    if(FLOW.selectedControl.get('selectedSurvey')) {
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.Question, {
        surveyId: sId
      }));
    }
  }.observes('FLOW.selectedControl.selectedSurvey'),

  setSGcontent: function() {
    if(FLOW.selectedControl.get('selectedQuestionGroup')) {
      this.set('QGcontent', FLOW.store.filter(FLOW.Question, function(item) {
        return(item.get('questionGroupId') == FLOW.selectedControl.selectedQuestionGroup.get('keyId'));
      }));
    }
  }.observes('FLOW.selectedControl.selectedQuestionGroup'),

  setOPTIONcontent: function() {
    if(FLOW.selectedControl.get('selectedSurvey')) {
      this.set('OPTIONcontent', FLOW.store.filter(FLOW.Question, function(item) {
        return(item.get('type') == 'OPTION');
      }));
    }
  }.observes('FLOW.selectedControl.selectedSurvey'),

  // used for display of dependencies: a question can only be dependent on earlier questions
  setEarlierOptionQuestions: function() {
    var QuestionList, qIndex;
    QuestionList = this.get('content');
    qIndex = QuestionList.indexOf(FLOW.selectedControl.get('selectedQuestion'));
    this.set('earlierOptionQuestions', FLOW.store.filter(FLOW.Question, function(item) {
      return(qIndex > QuestionList.indexOf(item)) && (item.get('type') == 'OPTION');
    }));
  }.observes('FLOW.selectedControl.selectedQuestion'),

  // true if all items have been saved
  // used in models.js
  allRecordsSaved: function() {
    var allSaved = true;
    FLOW.questionControl.get('content').forEach(function(item) {
      if(item.get('isSaving')) {
        allSaved = false;
      }
    });
    return allSaved;
  }.property('content.@each.isSaving')
});

// TODO turn this into radio buttons
FLOW.optionListControl = Ember.ArrayController.create({
  content: []
  // changes:function(){return true;}.property('content.@each.isSelected'),
  // changed:function(){console.log('hier dan?');}.observes('this.changes')
});