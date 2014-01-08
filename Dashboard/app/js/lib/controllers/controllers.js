// ***********************************************//
//                 controllers
// ***********************************************//
// Define the main application controller. This is automatically picked up by
// the application and initialized.
require('akvo-flow/core-common');
require('akvo-flow/flowenv');
require('akvo-flow/controllers/languages');
require('akvo-flow/currentuser');
require('akvo-flow/controllers/permissions');
require('akvo-flow/controllers/general-controllers-common');
require('akvo-flow/controllers/survey-controllers');
require('akvo-flow/controllers/device-controllers');
require('akvo-flow/controllers/data-controllers');
require('akvo-flow/controllers/reports-controllers');
require('akvo-flow/controllers/maps-controllers-common');
require('akvo-flow/controllers/messages-controllers');
require('akvo-flow/controllers/user-controllers');

FLOW.ApplicationController = Ember.Controller.extend({
  init: function () {
    this._super();
    Ember.STRINGS = Ember.STRINGS_EN;
  }
});

FLOW.role = Ember.Object.create({
	SUPER_ADMIN: function () {
		return FLOW.currentUser && FLOW.currentUser.permissionList === 0;
	}.property(),

	ADMIN: function () {
		return FLOW.currentUser && FLOW.currentUser.permissionList <= 10;
	}.property(),

	USER: function () {
		return FLOW.currentUser && FLOW.currentUser.permissionList <= 20;
	}.property()
});

//require('akvo-flow/currentuser');

// Navigation controllers
FLOW.NavigationController = Em.Controller.extend({
  selected: null
});
FLOW.NavHomeController = Ember.Controller.extend();
FLOW.NavSurveysController = Ember.Controller.extend();
FLOW.NavSurveysEditController = Ember.Controller.extend();
FLOW.NavDevicesController = Ember.Controller.extend();
FLOW.DevicesSubnavController = Em.Controller.extend();
FLOW.DevicesTableHeaderController = Em.Controller.extend({
  selected: null
});

FLOW.NavDataController = Ember.Controller.extend();
FLOW.DatasubnavController = Em.Controller.extend();
FLOW.InspectDataController = Ember.ArrayController.extend();
FLOW.BulkUploadController = Ember.Controller.extend();
FLOW.DataCleaningController = Ember.Controller.extend();

FLOW.NavReportsController = Ember.Controller.extend();
FLOW.ReportsSubnavController = Em.Controller.extend();
FLOW.ExportReportsController = Ember.ArrayController.extend();
FLOW.ChartReportsController = Ember.Controller.extend();
FLOW.StatisticsController = Ember.Controller.extend();

FLOW.NavMapsController = Ember.Controller.extend();
FLOW.NavUsersController = Ember.Controller.extend();
FLOW.NavMessagesController = Ember.Controller.extend();
FLOW.NavAdminController = Ember.Controller.extend();
