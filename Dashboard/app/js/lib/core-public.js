require('akvo-flow/templ-public');
// Ember.LOG_BINDINGS = true;
// Create the application
window.FLOW = Ember.Application.create({
  VERSION: '0.0.1'
});

/* Generic FLOW view that also handles lanague rerenders*/
FLOW.View = Ember.View.extend({
  onLanguageChange: function() {
    this.rerender();
  }.observes('FLOW.dashboardLanguageControl.dashboardLanguage')
});
