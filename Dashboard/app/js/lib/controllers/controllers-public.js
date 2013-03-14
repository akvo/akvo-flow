// ***********************************************//
//                 controllers
// ***********************************************//
// Define the main application controller. This is automatically picked up by
// the application and initialized.
require('akvo-flow/core-common');
require('akvo-flow/flowenv');
require('akvo-flow/controllers/general-controllers-common');
require('akvo-flow/controllers/maps-controllers-common');

FLOW.ApplicationController = Ember.Controller.extend({
  init: function() {
    this._super();
    Ember.STRINGS = Ember.STRINGS_EN;
  }
});

FLOW.NavMapsController = Ember.Controller.extend();
