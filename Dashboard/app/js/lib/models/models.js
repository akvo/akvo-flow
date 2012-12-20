// ***********************************************//
//                 models and stores
// ***********************************************//
require('akvo-flow/core');
require('akvo-flow/models/store_def');

FLOW.BaseModel = DS.Model.extend({
  keyId: DS.attr('number'),
  savingStatus: null,

  // this method calls the checkSaving method on the savingMessageControl, which
  // checks if there are any records inflight. If yes, it sets a boolean,
  // so a saving message can be displayed. savingStatus is used to capture the
  // moment that nothing is being saved anymore, but in the previous event it was
  // so we can turn off the saving message.
  anySaving: function() {
    if(this.get('isSaving') || this.get('isDirty') || this.get('savingStatus')) {
      FLOW.savingMessageControl.checkSaving();
    }
    this.set('savingStatus', (this.get('isSaving') || this.get('isDirty')));
  }.observes('isSaving', 'isDirty')

});

FLOW.SurveyGroup = FLOW.BaseModel.extend({
  didDelete: function() {
    FLOW.surveyGroupControl.populate();
  },
  didUpdate: function() {
    FLOW.surveyGroupControl.populate();
  },
  didCreate: function() {
    FLOW.surveyGroupControl.populate();
  },

  description: DS.attr('string', {
    defaultValue: ''
  }),
  name: DS.attr('string', {
    defaultValue: ''
  }),
  createdDateTime: DS.attr('string', {
    defaultValue: ''
  }),
  lastUpdateDateTime: DS.attr('string', {
    defaultValue: ''
  }),
  // the code field is used as name
  code: DS.attr('string', {
    defaultValue: ''
  })
});


FLOW.Survey = FLOW.BaseModel.extend({
  didDelete: function() {
    FLOW.surveyControl.populate();
  },
  didUpdate: function() {
    FLOW.surveyControl.populate();
  },
  didCreate: function() {
    FLOW.surveyControl.populate();
  },

  defaultLanguageCode: DS.attr('string'),
  status: DS.attr('string'),
  sector: DS.attr('string'),
  code: DS.attr('string'),
  requireApproval: DS.attr('string'),
  version: DS.attr('string'),
  description: DS.attr('string'),
  name: DS.attr('string'),
  pointType: DS.attr('string'),
  surveyGroupId: DS.attr('number'),
  createdDateTime: DS.attr('number'),
  lastUpdateDateTime: DS.attr('number')
});


FLOW.QuestionGroup = FLOW.BaseModel.extend({
  didDelete: function() {
    if(FLOW.questionGroupControl.get('allRecordsSaved')) FLOW.questionGroupControl.populate();
  },
  didUpdate: function() {
    if(FLOW.questionGroupControl.get('allRecordsSaved')) FLOW.questionGroupControl.populate();
  },
  didCreate: function() {
    if(FLOW.questionGroupControl.get('allRecordsSaved')) FLOW.questionGroupControl.populate();
  },

  order: DS.attr('number'),
  description: DS.attr('string'),
  name: DS.attr('string'),
  path: DS.attr('string'),
  code: DS.attr('string'),
  surveyId: DS.attr('number')
});


FLOW.Question = FLOW.BaseModel.extend({
  didDelete: function() {
    if(FLOW.questionControl.get('allRecordsSaved')) FLOW.questionControl.populate();
  },
  didUpdate: function() {
    if(FLOW.questionControl.get('allRecordsSaved')) FLOW.questionControl.populate();
  },
  didCreate: function() {
    if(FLOW.questionControl.get('allRecordsSaved')) FLOW.questionControl.populate();
  },

  allowDecimal: DS.attr('boolean', {
    defaultValue: 0
  }),
  allowMultipleFlag: DS.attr('boolean', {
    defaultValue: 0
  }),
  allowOtherFlag: DS.attr('boolean', {
    defaultValue: 0
  }),
  allowSign: DS.attr('boolean', {
    defaultValue: 0
  }),
  collapseable: DS.attr('boolean', {
    defaultValue: 0
  }),
  immutable: DS.attr('boolean', {
    defaultValue: 0
  }),
  isName: DS.attr('boolean', {
    defaultValue: 0
  }),
  mandatoryFlag: DS.attr('boolean', {
    defaultValue: 0
  }),
  dependentFlag: DS.attr('boolean', {
    defaultValue: 0
  }),
  dependentQuestionAnswer: DS.attr('string'),
  dependentQuestionId:DS.attr('number'),
  maxVal: DS.attr('number'),
  minVal: DS.attr('number'),
  //optionContainerDto: DS.attr('string'),
  optionList:DS.attr('string'),
  order: DS.attr('number'),
  path: DS.attr('string'),
  //questionDependency: DS.attr('string'),
  questionGroupId: DS.attr('number'),
  surveyId: DS.attr('number'),
  text: DS.attr('string'),
  tip: DS.attr('string'),
  type: DS.attr('string',{defaultValue:"FREE_TEXT"})
});


FLOW.QuestionOption = FLOW.BaseModel.extend({
  questionId: DS.attr('number'),
  text: DS.attr('string')
});


FLOW.DeviceGroup = FLOW.BaseModel.extend({
  code: DS.attr('string', {
    defaultValue: ''
  })
});

FLOW.Device = FLOW.BaseModel.extend({
  esn: DS.attr('string', {
    defaultValue: ''
  }),
  phoneNumber: DS.attr('string', {
    defaultValue: ''
  }),
  deviceIdentifier: DS.attr('string', {
    defaultValue: ''
  }),
  lastKnownLat: DS.attr('number', {
    defaultValue: 0
  }),
  lastKnownLon: DS.attr('number', {
    defaultValue: 0
  }),
  lastKnownAccuracy: DS.attr('number', {
    defaultValue: 0
  }),
  lastPositionDate: DS.attr('number', {
    defaultValue: ''
  }),
  deviceGroup: DS.attr('string', {
    defaultValue: ''
  }),
  isSelected: false,
  deviceGroupName: null
});

FLOW.SurveyAssignment = FLOW.BaseModel.extend({
  name: DS.attr('string'),
  startDate: DS.attr('date'),
  endDate: DS.attr('date'),
  devices: DS.attr('array'),
  surveys: DS.attr('array')
});

FLOW.SurveyedLocale = DS.Model.extend({
  description: DS.attr('string', {
    defaultValue: ''
  }),
  keyId: DS.attr('number'),
  latitude: DS.attr('number'),
  longitude: DS.attr('number'),
  primaryKey: 'keyId',
  typeMark: DS.attr('string', {
    defaultValue: 'WATER_POINT'
  })
});

// Explicitly avoid to use belongTo and hasMany as
// Ember-Data lacks of partial loading
// https://github.com/emberjs/data/issues/51
FLOW.PlacemarkDetail = FLOW.BaseModel.extend({
  placemarkId: DS.attr('number'),
  collectionDate: DS.attr('number'),
  questionText: DS.attr('string'),
  metricName: DS.attr('string'),
  stringValue: DS.attr('string')
});

FLOW.Placemark = FLOW.BaseModel.extend({
  latitude: DS.attr('number'),
  longitude: DS.attr('number'),
  collectionDate: DS.attr('number'),
  markType: DS.attr('string', {
    defaultValue: 'WATER_POINT'
  })
});

FLOW.SurveyInstance = FLOW.BaseModel.extend({
  approvedFlag: DS.attr('string'),
  approximateLocationFlag: DS.attr('string'),
  surveyId: DS.attr('number'),
  collectionDate: DS.attr('number'),
  surveyCode: DS.attr('string'),
  submitterName: DS.attr('string'),
  deviceIdentifier: DS.attr('string')
});

FLOW.QuestionAnswer = FLOW.BaseModel.extend({
  value: DS.attr('string'),
  type: DS.attr('string'),
  oldValue: DS.attr('string'),
  surveyId: DS.attr('number'),
  collectionDate: DS.attr('number'),
  surveyInstanceId: DS.attr('number'),
  questionId: DS.attr('string'),
  questionText: DS.attr('string')
});

FLOW.SurveyQuestionSummary = FLOW.BaseModel.extend({
  response: DS.attr('string'),
  count: DS.attr('number'),
  questionId: DS.attr('string')
});
