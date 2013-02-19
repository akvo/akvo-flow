FLOW.MessagesListView = FLOW.View.extend({
  
   doInstanceQuery: function() {
    this.set('since', FLOW.metaControl.get('since'));
    FLOW.messageControl.doInstanceQuery(this.get('since'));
  },
  
  doNextPage: function() {
    FLOW.messageControl.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.doInstanceQuery();
  },

  doPrevPage: function() {
    FLOW.messageControl.get('sinceArray').popObject();
    FLOW.metaControl.set('since', FLOW.messageControl.get('sinceArray')[FLOW.messageControl.get('sinceArray').length - 1]);
    this.doInstanceQuery();
  },

  // If the number of items in the previous call was 20 (a full page) we assume that there are more.
  // This is not foolproof, but will only lead to an empty next page in 1/20 of the cases
  hasNextPage: function() {
    if(FLOW.metaControl.get('num') == 20) {
      return true;
    } else {
      return false;
    }
  }.property('FLOW.metaControl.num'),

  // not perfect yet, sometimes previous link is shown while there are no previous pages.
  hasPrevPage: function() {
    if(FLOW.messageControl.get('sinceArray').length === 1) {
      return false;
    } else {
      return true;
    }
  }.property('FLOW.messageControl.sinceArray.length')

});