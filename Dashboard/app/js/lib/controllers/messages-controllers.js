FLOW.messageControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,

  populate: function() {
    this.set('content', FLOW.store.find(FLOW.Message));
    this.set('sortProperties', ['lastUpdateDateTime']);
    this.set('sortAscending', true);
  },

  getSortInfo: function() {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
    this.set('selected', FLOW.tableColumnControl.get('selected'));
  }
});