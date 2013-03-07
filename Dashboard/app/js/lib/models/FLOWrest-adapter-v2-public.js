/*global DS*/

DS.FLOWRESTAdapter = DS.RESTAdapter.extend({
  serializer: DS.RESTSerializer.extend({
    primaryKey: function (type) {
      return "keyId";
    },
    keyForAttributeName: function (type, name) {
      return name;
    }
  }),

  buildURL: function (record, suffix) {
    var url;

    url = this._super(record, suffix);
    if (record === 'placemark') {
      return  url + '?country=' + FLOW.countryController.get('countryCode');
    }
    return url;
  },

  sideload: function (store, type, json, root) {
    var msg;
    this._super(store, type, json, root);
    // only change metaControl info if there is actual meta info in the server response
    if (Object.keys(this.extractMeta(json)).length !== 0) {
      FLOW.metaControl.set('since', this.extractMeta(json).since);
      FLOW.metaControl.set('num', this.extractMeta(json).num);

      msg = this.extractMeta(json).message;
      if (msg.indexOf('_') === 0) { // Response is a translatable message
        msg = Ember.String.loc(msg);
      }
      FLOW.metaControl.set('message', msg);

      FLOW.metaControl.set('status', this.extractMeta(json).status);
      FLOW.savingMessageControl.set('areLoadingBool', false);
      FLOW.savingMessageControl.set('areSavingBool', false);

      if (this.extractMeta(json).status === 'failed' || FLOW.metaControl.get('message') !== ''){
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', '' /*Ember.String.loc('_action_failed')*/); //FIXME
        FLOW.dialogControl.set('message', FLOW.metaControl.get('message'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
      }
    }
  },

 ajax: function(url, type, hash) {
   this._super(url, type, hash);
   if (type == "GET"){
     FLOW.savingMessageControl.set('areLoadingBool',true);
   }
 },

didFindRecord: function(store, type, json, id) {
  this._super(store, type, json, id);
  FLOW.savingMessageControl.set('areLoadingBool',false);
},

didFindAll: function(store, type, json) {
  this._super(store, type, json);
  FLOW.savingMessageControl.set('areLoadingBool',false);
},

didFindQuery: function(store, type, json, recordArray) {
  this._super(store, type, json, recordArray);
  FLOW.savingMessageControl.set('areLoadingBool',false);
}


});