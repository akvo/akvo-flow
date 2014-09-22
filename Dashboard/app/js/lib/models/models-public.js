// ***********************************************//
//                 models and stores
// ***********************************************//
require('akvo-flow/core-common');
require('akvo-flow/models/store_def-common');

FLOW.BaseModel = DS.Model.extend({
  keyId: DS.attr('number'),
  savingStatus: null,

  // this method calls the checkSaving method on the savingMessageControl, which
  // checks if there are any records inflight. If yes, it sets a boolean,
  // so a saving message can be displayed. savingStatus is used to capture the
  // moment that nothing is being saved anymore, but in the previous event it was
  // so we can turn off the saving message.
  anySaving: function () {
    if (this.get('isSaving') || this.get('isDirty') || this.get('savingStatus')) {
      FLOW.savingMessageControl.checkSaving();
    }
    this.set('savingStatus', (this.get('isSaving') || this.get('isDirty')));
  }.observes('isSaving', 'isDirty')

});

FLOW.SurveyGroup = FLOW.BaseModel.extend({
  didDelete: function () {
    FLOW.surveyGroupControl.populate();
  },
  didUpdate: function () {
    FLOW.surveyGroupControl.populate();
  },
  didCreate: function () {
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


// Explicitly avoid to use belongTo and hasMany as
// Ember-Data lacks of partial loading
// https://github.com/emberjs/data/issues/51
FLOW.PlacemarkDetail = FLOW.BaseModel.extend({
  placemarkId: DS.attr('number'),
  collectionDate: DS.attr('number'),
  order: DS.attr('number'),
  questionText: DS.attr('string'),
  metricName: DS.attr('string'),
  stringValue: DS.attr('string'),
  questionType: DS.attr('string')
});

FLOW.Placemark = FLOW.BaseModel.extend({
  latitude: DS.attr('number'),
  longitude: DS.attr('number'),
  count: DS.attr('number'),
  level: DS.attr('number'),
  surveyId: DS.attr('number'),
  detailsId: DS.attr('number'),
  collectionDate: DS.attr('number')
});
