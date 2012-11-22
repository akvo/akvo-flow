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
  }
});