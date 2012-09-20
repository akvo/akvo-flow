// ***********************************************//
//                      views                    
// ***********************************************//

require('akvo-flow/core');
require('akvo-flow/views/survey-group-views')

FLOW.ApplicationView = Ember.View.extend({
	templateName: 'application'
});

// ********************************************************//
//                      main navigation                    
// ********************************************************//

FLOW.NavigationView = Em.View.extend({
	templateName: 'navigation',
	selectedBinding: 'controller.selected',
	NavItemView: Ember.View.extend({
		tagName: 'li',
		classNameBindings: 'isActive:current navItem'.w(),

		navItem: function() {
			return this.get('item')
		}.property('item').cacheable(),

		isActive: function() {
			return this.get('item') === this.get('parentView.selected');
		}.property('item', 'parentView.selected').cacheable()
	})
});

// ********************************************************//
//                      standard views                    
// ********************************************************//

// home screen view
FLOW.NavHomeView = Ember.View.extend({ templateName: 'navHome/nav-home'});

// surveys views
FLOW.NavSurveysView = Ember.View.extend({ templateName: 'navSurveys/nav-surveys'});
FLOW.NavSurveysMainView = Ember.View.extend({ templateName: 'navSurveys/nav-surveys-main'});
FLOW.NavSurveysEditView = Ember.View.extend({ templateName: 'navSurveys/nav-surveys-edit'});

// devices views
FLOW.NavDevicesView = Ember.View.extend({ templateName: 'navDevices/nav-devices'});

// data views
FLOW.NavDataView = Ember.View.extend({ templateName: 'navData/nav-data'});
FLOW.InspectDataView = Ember.View.extend({ templateName: 'navData/inspect-data'});
FLOW.ImportSurveyView = Ember.View.extend({ templateName: 'navData/import-survey'});
FLOW.ExcelImportView = Ember.View.extend({ templateName: 'navData/excel-import'});
FLOW.ExcelExportView = Ember.View.extend({ templateName: 'navData/excel-export'});

// reports views
FLOW.NavReportsView = Ember.View.extend({ templateName: 'navReports/nav-reports'});

// maps views
FLOW.NavMapsView = Ember.View.extend({ templateName: 'navMaps/nav-maps'});

// users views
FLOW.NavUsersView = Ember.View.extend({	templateName: 'navUsers/nav-users'});

// admin views
FLOW.NavAdminView = Ember.View.extend({	templateName: 'navAdmin/nav-admin'});


// ********************************************************//
//             Subnavigation for the Data tabs                
// ********************************************************//
FLOW.DatasubnavView = Em.View.extend({
	templateName: 'navData/data-subnav',
	selectedBinding: 'controller.selected',
	NavItemView: Ember.View.extend({
		tagName: 'li',
		classNameBindings: 'isActive:active'.w(),

		isActive: function() {
			return this.get('item') === this.get('parentView.selected');
		}.property('item', 'parentView.selected').cacheable()
	})
});


// ************************ Surveys *************************
FLOW.QuestionGroupItemView = Ember.View.extend({
	content: null,

	amVisible: function() {
		var selected = FLOW.selectedControl.get('selectedQuestionGroup');
		if (selected) {

			var isVis = (this.content.get('keyId') === FLOW.selectedControl.selectedQuestionGroup.get('keyId'));
			return isVis;
		} else {
			return null;
		}
	}.property('FLOW.selectedControl.selectedQuestionGroup', 'content.keyId').cacheable(),

	toggleVisibility: function() {
		if (this.get('amVisible')) {
			FLOW.selectedControl.set('selectedQuestionGroup', null);
		} else {
			FLOW.selectedControl.set('selectedQuestionGroup', this.content);
		}
	},

	showHideText: function() {
		return this.get('amVisible') ? 'Close' : 'Open';
	}.property('amVisible').cacheable()

});

FLOW.QuestionView = Ember.View.extend({
	content:null,
	questionName:null,
	checkedMandatory: false,
	checkedDependent: false,
	checkedOptionMultiple:false,
	checkedOptionOther:false,
	selectedQuestionType:null,
	selectedOptionEdit:null,
	
	amOpenQuestion: function() {
		var selected = FLOW.selectedControl.get('selectedQuestion');
		if (selected) {

			var isOpen = (this.content.get('keyId') == FLOW.selectedControl.selectedQuestion.get('keyId'));
			return isOpen;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedQuestion', 'content.keyId').cacheable(),

	
	amOptionType:function() {
		if (this.selectedQuestionType){ return (this.selectedQuestionType.get('value')=='option') ? true : false;}
		else {return false;}
	}.property('this.selectedQuestionType').cacheable(),
	
	amNumberType:function() {
		if (this.selectedQuestionType){ return (this.selectedQuestionType.get('value')=='number') ? true : false;}
		else {return false;}
	}.property('this.selectedQuestionType').cacheable(),
		
	doEdit: function() {
		FLOW.selectedControl.set('selectedQuestion', this.content);
		this.set('questionName',FLOW.selectedControl.selectedQuestion.get('displayName'));
		FLOW.optionControl.set('editCopy',FLOW.optionControl.get('questionOptionsList'));
	
		//TODO populate selected question type
		//TODO populate tooltip
		//TODO populate question options
		//TODO populate help
		//TODO populate translations	
	},
	
	doCancelEditQuestion: function() {
		FLOW.selectedControl.set('selectedQuestion', null);
	},
	
	doSaveEditQuestion: function() {
	},
	
	doCopy: function() {
		console.log("doing doDuplicate");	
	},
	
	doMove: function() {
			console.log("doing doMove");
	},
	
	doDelete: function() {
			console.log("doing doDelete");
	}
});


