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
  OandNcontent: null,
  Ocontent: null,

  // used for display of dependencies: a question can only be dependent on earlier questions
  earlierOptionQuestions: function() {
    var optionQuestionList, qIndex;
    optionQuestionList = this.get('Ocontent');
    qIndex = optionQuestionList.indexOf(FLOW.selectedControl.get('selectedQuestion'));

    return this.get('Ocontent').filter(function(item) {
      return(qIndex > optionQuestionList.indexOf(item));
    });
  }.property('FLOW.selectedControl.selectedQuestion'),

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
  }.property('content.@each.isSaving'),

  populate: function() {
    if(FLOW.selectedControl.get('selectedQuestionGroup')) {
      var id = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.Question, {
        questionGroupId: id
      }));
    }
  }.observes('FLOW.selectedControl.selectedQuestionGroup'),

  populateAllQuestions: function(surveyId) {
    this.set('content', FLOW.store.findQuery(FLOW.Question, {
      surveyId: surveyId
    }));
  },

  populateOPTIONandNUMBERQuestions: function() {
    if(FLOW.selectedControl.get('selectedSurveyOPTIONandNUMBERQuestions')) {
      var id = FLOW.selectedControl.selectedSurveyOPTIONandNUMBERQuestions.get('keyId');
      this.set('OandNcontent', FLOW.store.findQuery(FLOW.Question, {
        surveyId: id,
        includeOption: "true",
        includeNumber: "true"
      }));
    }
  }.observes('FLOW.selectedControl.selectedSurveyOPTIONandNUMBERQuestions'),

  // TODO make this more efficient - this is loading to many times
  populateOPTIONQuestions: function() {
    if(FLOW.selectedControl.get('selectedSurvey')) {
      var id = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('Ocontent', FLOW.store.findQuery(FLOW.Question, {
        surveyId: id,
        includeOption: "true"
      }));
    }
  }.observes('FLOW.selectedControl.selectedQuestionGroup')
});


FLOW.optionListControl = Ember.ArrayController.create({
  content: []
});