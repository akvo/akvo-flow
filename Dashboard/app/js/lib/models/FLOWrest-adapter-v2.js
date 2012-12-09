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

  sideload: function(store, type, json, root) {
    var sideloadedType, mappings, loaded = {};

    loaded[root] = true;

    for (var prop in json) {
      if (!json.hasOwnProperty(prop)) { continue; }
      if (prop === root) { continue; }
      
      if (prop === 'meta') {
        metaContent=json['meta'];
        FLOW.metaControl.set('since',metaContent['since']);
        FLOW.metaControl.set('num',metaContent['num']);
        FLOW.metaControl.set('message',metaContent['message']);
        FLOW.metaControl.set('status',metaContent['status']);
        continue;
      }

      sideloadedType = type.typeForAssociation(prop);

      if (!sideloadedType) {
        mappings = get(this, 'mappings');
        Ember.assert("Your server returned a hash with the key " + prop + " but you have no mappings", !!mappings);

        sideloadedType = get(mappings, prop);

        if (typeof sideloadedType === 'string') {
          sideloadedType = get(window, sideloadedType);
        }

        Ember.assert("Your server returned a hash with the key " + prop + " but you have no mapping for it", !!sideloadedType);
      }

      this.sideloadAssociations(store, sideloadedType, json, prop, loaded);
    }
  },

});