FLOW.messageControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,
  sinceArray: [],

  populate() {
    this.get('sinceArray').clear();
    FLOW.metaControl.set('since', null);
    // put null in as the first item
    this.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.set('content', FLOW.store.findQuery(FLOW.Message, {
      since: null,
    }));
    this.set('sortProperties', ['lastUpdateDateTime']);
    this.set('sortAscending', false);
  },

  doInstanceQuery(since) {
    this.set('content', FLOW.store.findQuery(FLOW.Message, {
      since,
    }));
  },

  getSortInfo() {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
    this.set('selected', FLOW.tableColumnControl.get('selected'));
  },
});
