


// ***********************************************//
//                 controllers                    
// ***********************************************//


  // Define the main application controller. This is automatically picked up by
  // the application and initialized.
  FLOW.ApplicationController = Ember.Controller.extend({
  });
  
  FLOW.NavigationController = Em.Controller.extend();
  
  FLOW.DatasubnavController = Em.Controller.extend();
  
  FLOW.NavHomeController = Ember.Controller.extend();
  
  FLOW.NavSurveysController = Ember.Controller.extend();
  
  FLOW.NavDevicesController = Ember.ArrayController.extend();
  
  FLOW.NavDataController = Ember.Controller.extend();
  
  FLOW.NavReportsController = Ember.Controller.extend();

  FLOW.NavMapsController = Ember.ArrayController.extend();

  FLOW.NavUsersController = Ember.Controller.extend();
 
  FLOW.NavAdminController = Ember.Controller.extend();


