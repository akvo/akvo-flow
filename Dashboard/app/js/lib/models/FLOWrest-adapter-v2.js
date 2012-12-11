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
    var url = this._super(record, suffix);
    // temporal hack
    return url + (record === 'placemark' ? '?country=KE' : '');
  },

  sideload: function (store, type, json, root) {
    this._super(store, type, json, root);
    
    // only change metaControl info if there is actual meta info in the server response
    if (Object.keys(this.extractMeta(json)).length !== 0) {
      FLOW.metaControl.set('since', this.extractMeta(json).since);
      FLOW.metaControl.set('num', this.extractMeta(json).num);
      FLOW.metaControl.set('message', this.extractMeta(json).message);
      FLOW.metaControl.set('status', this.extractMeta(json).status);
    }
  }
});