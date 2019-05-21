FLOW.store = DS.Store.create({
  revision: 10,
  adapter: DS.FLOWRESTAdapter.create({
    bulkCommit: false,
    namespace: 'rest',
    url: `${window.location.protocol}//${window.location.hostname
    }${window.location.port ? `:${window.location.port}` : ''}`,
  }),
});

DS.JSONTransforms.array = {
  deserialize(serialized) {
    return Ember.none(serialized) ? null : serialized;
  },

  serialize(deserialized) {
    return Ember.none(deserialized) ? null : deserialized;
  },
};
