


// ***********************************************//
//                 controllers                    
// ***********************************************//


  // Define the main application controller. This is automatically picked up by
  // the application and initialized.
  FLOW.ApplicationController = Ember.Controller.extend({
  });

  // Navigation controllers  
  FLOW.NavigationController = Em.Controller.extend();
  
  FLOW.DatasubnavController = Em.Controller.extend();
  
  FLOW.NavHomeController = Ember.Controller.extend();
  
  FLOW.NavSurveysController = Ember.Controller.extend();
  
  FLOW.NavDevicesController = Ember.Controller.extend();
  
  FLOW.NavDataController = Ember.Controller.extend();
  
  FLOW.InspectDataController = Ember.ArrayController.extend();

  FLOW.ImportSurveyController = Ember.Controller.extend();

  FLOW.ExcelImportController = Ember.Controller.extend();

  FLOW.ExcelExportController = Ember.Controller.extend();
  
  FLOW.NavReportsController = Ember.Controller.extend();

  FLOW.NavMapsController = Ember.ArrayController.extend();

  FLOW.NavUsersController = Ember.Controller.extend();
 
  FLOW.NavAdminController = Ember.Controller.extend();


	// Data controllers

	FLOW.SelectedSurveyGroupController = Ember.Controller.extend({
			selectedSurveyGroup:null
		}); 
	
	FLOW.SelectedSurveyController =  Ember.Controller.extend({
			selectedSurvey:null
		});

  FLOW.SurveyGroupController = Ember.ArrayController.extend();
  	
  FLOW.SurveyController = Ember.ArrayController.extend({
//  	selectedSurveyBinding:"FLOW.selectedSurveyController.selectedSurvey",
//  	active_text:function() {
//  		console.log(this.get('controllers.selectedSurveyGroupController.selectedSurveyGroup'));
//  		if (this.get('controllers.selectedSurveyGroupController.selectedSurveyGroup')){	
//  			return "got one!"+this.get('controllers.selectedSurveyGroupController.selectedSurveyGroup.keyId');
//  		}	
//  	}.property('controllers.selectedSurveyGroupController.selectedSurveyGroup'),
  	
  	active:function() {
  		console.log(this.get('controllers.selectedSurveyGroupController.selectedSurveyGroup'));
  		if (this.get('controllers.selectedSurveyGroupController.selectedSurveyGroup')){	
  			console.log("inside active now");
  			var id=this.get('controllers.selectedSurveyGroupController.selectedSurveyGroup.keyId');
  			return FLOW.store.find(FLOW.Survey,{surveyGroupId:id})
  		}	
  		else {
  			return null;
  		}
  	}.property('controllers.selectedSurveyGroupController.selectedSurveyGroup')
  		
  	});
  
  
	FLOW.QuestionSetController = Ember.ArrayController.extend();

	FLOW.QuestionController = Ember.ArrayController.extend();
		 	

