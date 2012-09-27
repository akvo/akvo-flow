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


FLOW.NavHomeController = Ember.Controller.extend();

FLOW.NavSurveysController = Ember.Controller.extend();

FLOW.NavDevicesController = Ember.Controller.extend();
FLOW.DevicesSubnavController = Em.Controller.extend();


FLOW.NavDataController = Ember.Controller.extend();
FLOW.DatasubnavController = Em.Controller.extend();
FLOW.InspectDataController = Ember.ArrayController.extend();
FLOW.ImportSurveyController = Ember.Controller.extend();
FLOW.ExcelImportController = Ember.Controller.extend();
FLOW.ExcelExportController = Ember.Controller.extend();

FLOW.NavReportsController = Ember.Controller.extend();

FLOW.NavMapsController = Ember.ArrayController.extend();

FLOW.NavUsersController = Ember.Controller.extend();

FLOW.NavAdminController = Ember.Controller.extend();


// Data controllers
FLOW.questionTypeControl = Ember.Object.create({
content:[
	Ember.Object.create({label: "Free text", value: "freeText"}),
    Ember.Object.create({label: "Option", value: "option"}),
   Ember.Object.create({label: "Number", value: "number"}),
   Ember.Object.create({label: "Geolocation", value: "geoLoc"}),
   Ember.Object.create({label: "Photo", value: "photo"}),
   Ember.Object.create({label: "Video", value: "video"}),
   Ember.Object.create({label: "Date", value: "date"}),
   Ember.Object.create({label: "Barcode", value: "barcode"})
]
});

FLOW.selectedControl = Ember.Controller.create({
	selectedSurveyGroup: null,
	selectedSurvey: null,
	selectedQuestionGroup: null,
	selectedForMoveQuestionGroup:null,
	selectedForCopyQuestionGroup:null,
	selectedQuestion: null,
	selectedOption: null,
	selectedCreateNewGroup:false
});

FLOW.selectedControl.addObserver('selectedSurveyGroup', function() {
	FLOW.selectedControl.set('selectedSurvey', null);
	FLOW.selectedControl.set('selectedQuestionGroup', null);

});


FLOW.DeviceController = Ember.ArrayController.extend({
	sortProperties:['phoneNumber'],
	sortAscending:true
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
			return FLOW.store.find(FLOW.Survey, {surveyGroupId: id});
		} else {
			FLOW.selectedControl.set('selectedSurvey', null);
			FLOW.selectedControl.set('selectedQuestionGroup', null);
			return null;
		}
	}.property('FLOW.selectedControl.selectedSurveyGroup').cacheable()
});

FLOW.questionGroupControl = Ember.ArrayController.create({
	sortProperties:['order'],
	sortAscending:true,
	reSort:false,
	
	// not used at the moment in survey tab
	active: function() {
		if (FLOW.selectedControl.get('selectedSurvey')) {
			var id = FLOW.selectedControl.selectedSurvey.get('keyId');
			return FLOW.store.find(FLOW.QuestionGroup, {surveyId: id});
		} else {
			FLOW.selectedControl.set('selectedQuestionGroup', null);
			return null;
		}
	}.property('FLOW.selectedControl.selectedSurvey').cacheable(),


	setContent:function(){
		var id = FLOW.selectedControl.selectedSurvey.get('keyId');
		this.set('content',FLOW.store.find(FLOW.QuestionGroup, {surveyId: id}));
	}.observes('this.reSort','reSort')
});

FLOW.questionControl = Ember.ArrayController.create({
	active: function() {
		if (FLOW.selectedControl.get('selectedQuestionGroup')) {
			var id = FLOW.selectedControl.selectedQuestionGroup.get('keyId');

			return FLOW.store.find(FLOW.Question, {questionGroupId: id});
		} else {
			return null;
		}
	}.property('FLOW.selectedControl.selectedQuestionGroup', 'FLOW.selectedControl.selectedSurvey').cacheable()
});

FLOW.optionControl = Ember.ArrayController.create({
	





	//editCopy:null,
	
	//questionOptions: function() {
	//	if (FLOW.selectedControl.get('selectedQuestion')) {
	//		var id = FLOW.selectedControl.selectedQuestion.get('keyId');
	//		return FLOW.store.find(FLOW.QuestionOption, {questionId: id});
	//	} else {
	//		return null;
	//}
	//}.property('FLOW.selectedControl.selectedQuestion').cacheable(),
	
	//questionOptionsList: function(){
	//	var opList = this.get('questionOptions').mapProperty('text').join("\n");
	//	return opList;
	//	}.property('this.questionOptions','this.questionOptions.isLoaded').cacheable(),
			
	//makeEditCopy: function(){
	//	this.set('editCopy',this.get('questionOptionsList'));
	//}.observes('questionOptionsList')
});



