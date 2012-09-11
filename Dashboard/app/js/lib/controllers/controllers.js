// ***********************************************//
//                 controllers                    
// ***********************************************//

// Define the main application controller. This is automatically picked up by
// the application and initialized.
require('akvo-flow/core');
FLOW.ApplicationController = Ember.Controller.extend();

// Navigation controllers  
FLOW.NavigationController = Em.Controller.extend({
	selected: null
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

FLOW.selectedControl = Ember.Controller.create({
	selectedSurveyGroup: null,
	selectedSurvey: null,
	selectedQuestionGroup: null,
	selectedQuestion: null,
});

FLOW.selectedControl.addObserver('selectedSurveyGroup', function() {
	FLOW.selectedControl.set('selectedSurvey', null);
	FLOW.selectedControl.set('selectedQuestionGroup', null);

});

FLOW.SurveyGroupController = Ember.ArrayController.extend({});

FLOW.surveyGroupControl = Ember.ArrayController.create({
	active: function() {
			//TODO find out how items are being loaded, difference find and findAll
			return FLOW.store.findAll(FLOW.SurveyGroup);
	}.property('').cacheable()
});


FLOW.surveyControl = Ember.ArrayController.create({
	active: function() {
		if (FLOW.selectedControl.get('selectedSurveyGroup')) {
			var id = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
			return FLOW.store.find(FLOW.Survey, {surveyGroupId: id})
		} else {
			FLOW.selectedControl.set('selectedSurvey', null);
			FLOW.selectedControl.set('selectedQuestionGroup', null);
			return null;
		}
	}.property('FLOW.selectedControl.selectedSurveyGroup').cacheable()
});

FLOW.questionGroupControl = Ember.ArrayController.create({
	active: function() {
		if (FLOW.selectedControl.get('selectedSurvey')) {
			var id = FLOW.selectedControl.selectedSurvey.get('keyId');
			return FLOW.store.find(FLOW.QuestionGroup, {surveyId: id})
		} else {
			FLOW.selectedControl.set('selectedQuestionGroup', null);
			return null;
		}
	}.property('FLOW.selectedControl.selectedSurvey', 'FLOW.selectedControl.selectedSurveyGroup').cacheable()
});

FLOW.questionControl = Ember.ArrayController.create({
	active: function() {
		if (FLOW.selectedControl.get('selectedQuestionGroup')) {
			var id = FLOW.selectedControl.selectedQuestionGroup.get('keyId');

			return FLOW.store.find(FLOW.Question, {questionGroupId: id})
		} else {
			return null;
		}
	}.property('FLOW.selectedControl.selectedQuestionGroup', 'FLOW.selectedControl.selectedSurvey').cacheable()
});
