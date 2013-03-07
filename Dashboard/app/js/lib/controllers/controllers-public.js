// ***********************************************//
//                 controllers
// ***********************************************//
// Define the main application controller. This is automatically picked up by
// the application and initialized.
require('akvo-flow/core-public');
require('akvo-flow/flowenv');
require('akvo-flow/controllers/maps-controllers-public');
require('akvo-flow/controllers/general-controllers-public');

FLOW.ApplicationController = Ember.Controller.extend({
  init: function() {
    this._super();
    Ember.STRINGS = Ember.STRINGS_EN;
  }
});


// Navigation controllers
FLOW.NavigationController = Em.Controller.extend({
  selected: null
});

FLOW.NavMapsController = Ember.Controller.extend();
