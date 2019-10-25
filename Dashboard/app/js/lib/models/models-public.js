// ***********************************************//
//                 models and stores
// ***********************************************//
import observe from '../mixins/observe';

require('akvo-flow/core-common');
require('akvo-flow/models/store_def-common');

FLOW.BaseModel = DS.Model.extend(observe({
  isSaving: 'anySaving',
  isDirty: 'anySaving',
}), {
  keyId: DS.attr('number'),
  savingStatus: null,

  // this method calls the checkSaving method on the savingMessageControl, which
  // checks if there are any records inflight. If yes, it sets a boolean,
  // so a saving message can be displayed. savingStatus is used to capture the
  // moment that nothing is being saved anymore, but in the previous event it was
  // so we can turn off the saving message.
  anySaving() {
    if (this.get('isSaving') || this.get('isDirty') || this.get('savingStatus')) {
      FLOW.savingMessageControl.checkSaving();
    }
    this.set('savingStatus', (this.get('isSaving') || this.get('isDirty')));
  },

});

FLOW.SurveyGroup = FLOW.BaseModel.extend({
  didDelete() {
    FLOW.surveyGroupControl.populate();
  },
  didUpdate() {
    FLOW.surveyGroupControl.populate();
  },
  didCreate() {
    FLOW.surveyGroupControl.populate();
  },

  description: DS.attr('string', {
    defaultValue: '',
  }),
  name: DS.attr('string', {
    defaultValue: '',
  }),
  createdDateTime: DS.attr('string', {
    defaultValue: '',
  }),
  lastUpdateDateTime: DS.attr('string', {
    defaultValue: '',
  }),
  // the code field is used as name
  code: DS.attr('string', {
    defaultValue: '',
  }),
});

FLOW.Placemark = FLOW.BaseModel.extend({
  latitude: DS.attr('number'),
  longitude: DS.attr('number'),
  count: DS.attr('number'),
  level: DS.attr('number'),
  collectionDate: DS.attr('number'),
});
