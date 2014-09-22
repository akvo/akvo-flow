FLOW.attributeTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: "text",
      value: "String"
    }), Ember.Object.create({
      label: "number",
      value: "Double"
    })
  ]
});

FLOW.attributeControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,

  setFilteredContent: function () {
    this.set('content', FLOW.store.filter(FLOW.Metric, function (item) {
      return true;
    }));
  },

  // load all Survey Groups
  populate: function () {
    FLOW.store.find(FLOW.Metric);
    this.setFilteredContent();
    this.set('sortProperties', ['name']);
    this.set('sortAscending', true);
  },

  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});

FLOW.surveyInstanceControl = Ember.ArrayController.create({
  sortProperties: ['collectionDate'],
  sortAscending: false,
  selectedSurvey: null,
  content: null,
  sinceArray: [],
  pageNumber: 0,

  populate: function () {
    this.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {}));
  },

  doInstanceQuery: function (surveyId, deviceId, since, beginDate, endDate, submitterName, countryCode, level1, level2) {
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {
      'surveyId': surveyId,
      'deviceId': deviceId,
      'since': since,
      'beginDate': beginDate,
      'endDate': endDate,
      'submitterName': submitterName,
      'countryCode': countryCode,
      'level1': level1,
      'level2': level2
    }));
  },

  contentChanged: function() {
    var mutableContents = [];

    this.get('arrangedContent').forEach(function(item) {
        mutableContents.pushObject(item);
    });

    this.set('currentContents', mutableContents);
  }.observes('content', 'content.isLoaded'),

  removeInstance: function(instance) {
    this.get('currentContents').forEach(function(item, i, currentContents) {
        if (item.get('id') == instance.get('id')) {
            currentContents.removeAt(i, 1);
        }
    });
  },

  allAreSelected: function (key, value) {
    if (arguments.length === 2) {
      this.setEach('isSelected', value);
      return value;
    } else {
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }
  }.property('@each.isSelected'),

  atLeastOneSelected: function () {
    return this.filterProperty('isSelected', true).get('length');
  }.property('@each.isSelected'),

  // fired from tableColumnView.sort
  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});

FLOW.surveyedLocaleControl = Ember.ArrayController.create({
  sortProperties: ['collectionDate'],
  sortAscending: false,
  selectedSurvey: null,
  content: null,
  sinceArray: [],
  pageNumber: 0,

  populate: function () {
    this.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.set('content', FLOW.store.findQuery(FLOW.SurveyedLocale, {}));
  }
});

FLOW.questionAnswerControl = Ember.ArrayController.create({
  content: null,

  doQuestionAnswerQuery: function (surveyInstanceId) {
    this.set('content', FLOW.store.findQuery(FLOW.QuestionAnswer, {
      'surveyInstanceId': surveyInstanceId
    }));
  }
});

FLOW.locationControl = Ember.ArrayController.create({
  selectedCountry: null,
  content:null,
  level1Content:null,
  level2Content:null,
  selectedLevel1: null,
  selectedLevel2: null,

  populateLevel1: function(){
    if (!Ember.none(this.get('selectedCountry')) && this.selectedCountry.get('iso').length > 0){
    this.set('level1Content',FLOW.store.findQuery(FLOW.SubCountry,{
      countryCode:this.selectedCountry.get('iso'),
      level:1,
      parentId:null
      }));
    }
  }.observes('this.selectedCountry'),

 populateLevel2: function(){
    if (!Ember.none(this.get('selectedLevel1')) && this.selectedLevel1.get('name').length > 0){
    this.set('level2Content',FLOW.store.findQuery(FLOW.SubCountry,{
      countryCode:this.selectedCountry.get('iso'),
      level:2,
      parentId:this.selectedLevel1.get('keyId')
      }));
    }
  }.observes('this.selectedLevel1')

});
