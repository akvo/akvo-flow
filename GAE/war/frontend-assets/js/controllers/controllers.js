// ***********************************************//
//                 controllers                    
// ***********************************************//


// Define the main application controller. This is automatically picked up by
// the application and initialized.
FLOW.ApplicationController = Ember.Controller.extend();

// Navigation controllers  
FLOW.NavigationController = Em.Controller.extend({
	selected:null
	});
	
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

FLOW.SelectedQuestionGroupController =  Ember.Controller.extend({
	selectedQuestionGroup:null,
});

FLOW.SelectedQuestionController =  Ember.Controller.extend({
	selectedQuestion:null
});

FLOW.SurveyGroupController = Ember.ArrayController.extend();
  	
FLOW.SurveyController = Ember.ArrayController.extend({  	
	active:function() {
  	if (this.get('controllers.selectedSurveyGroupController.selectedSurveyGroup')){	
  		var id=this.get('controllers.selectedSurveyGroupController.selectedSurveyGroup.keyId');
  		return FLOW.store.find(FLOW.Survey,{surveyGroupId:id})
  	}	
  	else {
  		return null;
  	}
 	}.property('controllers.selectedSurveyGroupController.selectedSurveyGroup')		
});
  
FLOW.QuestionGroupController = Ember.ArrayController.extend({
	active:function() {
  	if (this.get('controllers.selectedSurveyController.selectedSurvey')){	
  		var id=this.get('controllers.selectedSurveyController.selectedSurvey.keyId');
  		console.log("selectedsurvey: "+id);
  		return FLOW.store.find(FLOW.QuestionGroup,{surveyId:id})
  	}	
  	else {
  		return null;
  	}
 	}.property('controllers.selectedSurveyController.selectedSurvey')			
});

//FLOW.QuestionController = Ember.ArrayController.extend({
//		
//	active:function() {
//  	if (this.get('controllers.selectedQuestionGroupController.selectedQuestionGroup')){	
//  		var id=this.get('controllers.selectedQuestionGroupController.selectedQuestionGroup.keyId');
//  		return FLOW.store.find(FLOW.Question,{questionGroupId:id})
//  	}	
//  	else {
//  		return null;
//  	}
// 	}.property('controllers.selectedQuestionGroupController.selectedQuestionGroup')		
//});
//		 	

