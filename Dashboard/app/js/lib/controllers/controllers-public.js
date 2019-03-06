// ***********************************************//
//                 controllers
// ***********************************************//
// Define the main application controller. This is automatically picked up by
// the application and initialized.
loader.require('akvo-flow/flowenv');
require('akvo-flow/core-common');
require('akvo-flow/controllers/general-controllers-common');
require('akvo-flow/controllers/maps-controllers-common');

FLOW.ApplicationController = Ember.Controller.extend({});

FLOW.NavMapsController = Ember.Controller.extend();
