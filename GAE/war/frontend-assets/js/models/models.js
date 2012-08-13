// ***********************************************//
//                 stores                    
// ***********************************************//

DS.Model.reopen({
  namingConvention: {
    keyToJSONKey: function(key) {
      return key;
    },

    foreignKey: function(key) {
      return key;
    }
  }
});

FLOW.store = DS.Store.create({
  revision: 4,
  adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"REST"})
});

FLOW.SurveyGroup = DS.Model.extend({
    surveyList: DS.attr('string'),
    description: DS.attr('string'),
    name: DS.attr('string'),
    createdDateTime: DS.attr('string'),
    class: DS.attr('string'),
    lastUpdateDateTime: DS.attr('string'),
    code: DS.attr('string'),
    displayName: DS.attr('string'),
    keyId: DS.attr('number'),
    primaryKey: 'keyId',
    
});
