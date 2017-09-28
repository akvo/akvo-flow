require('akvo-flow/templ-common');
// Ember.LOG_BINDINGS = true;
// Create the application
window.FLOW = Ember.Application.create({
  VERSION: '0.0.1'
});

/* Generic FLOW view that also handles language rerenders */
FLOW.View = Ember.View.extend({});
