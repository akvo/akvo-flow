var host = "http://" + window.location.host;
FLOW.store = DS.Store.create({
	revision: 10,
	adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"rest", url:host})
  //adapter: DS.FixtureAdapter
});

DS.JSONTransforms.array = {
  deserialize: function(serialized) {
    return Ember.none(serialized) ? null : serialized;
  },

  serialize: function(deserialized) {
    return Ember.none(deserialized) ? null : deserialized;
  }
};