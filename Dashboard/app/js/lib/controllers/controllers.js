// ***********************************************//
//                 controllers
// ***********************************************//
// Define the main application controller. This is automatically picked up by
// the application and initialized.
loader.require('akvo-flow/flowenv');
loader.require('akvo-flow/currentuser');
require('akvo-flow/core-common');
require('akvo-flow/controllers/languages');
require('akvo-flow/controllers/permissions');
require('akvo-flow/controllers/general-controllers-common');
require('akvo-flow/controllers/survey-controllers');
require('akvo-flow/controllers/device-controllers');
require('akvo-flow/controllers/data-controllers');
require('akvo-flow/controllers/reports-controllers');
require('akvo-flow/controllers/maps-controllers-common');
require('akvo-flow/controllers/messages-controllers');
require('akvo-flow/controllers/user-controllers');
require('akvo-flow/controllers/survey-selection');

FLOW.ApplicationController = Ember.Controller.extend({});

// Navigation controllers
FLOW.NavigationController = Ember.Controller.extend({ selected: null });
FLOW.NavHomeController = Ember.Controller.extend();
FLOW.NavSurveysController = Ember.Controller.extend();
FLOW.NavSurveysEditController = Ember.Controller.extend();
FLOW.NavDevicesController = Ember.Controller.extend();
FLOW.DevicesSubnavController = Ember.Controller.extend();
FLOW.DevicesTableHeaderController = Ember.Controller.extend({ selected: null });

FLOW.NavDataController = Ember.Controller.extend();
FLOW.DatasubnavController = Ember.Controller.extend();
FLOW.InspectDataController = Ember.ArrayController.extend();
FLOW.BulkUploadController = Ember.Controller.extend();
FLOW.DataCleaningController = Ember.Controller.extend();

FLOW.NavResourcesController = Ember.Controller.extend();
FLOW.ResourcesSubnavController = Ember.Controller.extend();

FLOW.NavMapsController = Ember.Controller.extend();
FLOW.NavUsersController = Ember.Controller.extend();
FLOW.NavMessagesController = Ember.Controller.extend();
FLOW.NavStatsController = Ember.Controller.extend();
FLOW.NavAdminController = Ember.Controller.extend();

Ember.ENV.RAISE_ON_DEPRECATION = true;
