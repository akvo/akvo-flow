// ***********************************************//
//                      views
// ***********************************************//

require('akvo-flow/core');
require('akvo-flow/views/survey-group-views');

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
			return this.get('item');
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
	content: null, // question group content comes through binding in handlebars file
	zeroItem: false,
	renderView:false,

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
		return this.get('amVisible') ? 'Hide questions' : 'Show questions';
	}.property('amVisible').cacheable(),

	doQGroupNameEdit:function(){
		console.log("TODO - group name edit");
	},

	// true if one question group has been selected for Move
	oneSelectedForMove: function() {
		var selectedForMove = FLOW.selectedControl.get('selectedForMoveQuestionGroup');
		if (selectedForMove) {
			return true;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedForMoveQuestionGroup'),

	// true if one question group has been selected for Copy
	oneSelectedForCopy: function() {
		var selectedForCopy = FLOW.selectedControl.get('selectedForCopyQuestionGroup');
		if (selectedForCopy) {
			return true;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedForCopyQuestionGroup'),


	doQGroupDelete:function(){
		console.log("TODO - group delete");
	},

	// prepare for group copy. Shows 'copy to here' buttons
	doQGroupCopy:function(){
		FLOW.selectedControl.set('selectedForCopyQuestionGroup', this.content);
	},

	// cancel group copy
	doQGroupCopyCancel:function(){
		FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
	},

	// prepare for group move. Shows 'move here' buttons
	doQGroupMove:function(){
		FLOW.selectedControl.set('selectedForMoveQuestionGroup', this.content);
	},

	// cancel group move
	doQGroupMoveCancel:function(){
		FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
	},

	// execture group move to selected location
	doQGroupMoveHere:function(){
		
		var selectedOrder = FLOW.selectedControl.selectedForMoveQuestionGroup.get('order');
		var insertAfterOrder;
		var movingUp=false;

		if (this.get('zeroItem')) {insertAfterOrder=0;} else {insertAfterOrder=this.content.get('order');}

		FLOW.questionGroupControl.propertyWillChange('content');

		// moving to the same place => do nothing
		if ((selectedOrder==insertAfterOrder)||(selectedOrder==(insertAfterOrder+1))){}
		else {
			// determine if the item is moving up or down
			movingUp = (selectedOrder<insertAfterOrder);
		
			FLOW.questionGroupControl.get('content').forEach(function(item){
				var currentOrder=item.get('order');

				// item moving up
				if (movingUp) {
					// if outside of change region, do not move
					if ((currentOrder<selectedOrder) || (currentOrder>insertAfterOrder)){ }

					// move moving item to right location
					else if (currentOrder==selectedOrder) {	item.set('order',insertAfterOrder); }
					
					// move rest down
					else { item.set('order',item.get('order')-1); }
				}

				// item moving down
				else {
					if ((currentOrder<=insertAfterOrder) || (currentOrder>selectedOrder)){ }

					else if (currentOrder==selectedOrder) {	item.set('order',insertAfterOrder+1); }

					else {	item.set('order',item.get('order')+1); }
				}
			}); // end of forEach
		} // end of top else
		
		FLOW.store.commit();
		FLOW.questionGroupControl.propertyDidChange('content');
		FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
	},

	// execure group copy to selected location
	// *************************** DOES NOT WORK ************************
	// error: Cannot call method 'destroy' of undefined
	doQGroupCopyHere:function(){
		
		var selectedOrder = FLOW.selectedControl.selectedForCopyQuestionGroup.get('order');
		var insertAfterOrder = this.get('order');
		
		//FLOW.questionGroupControl.propertyWillChange('content');
		
		// move up to make space
		FLOW.questionGroupControl.get('content').forEach(function(item){
			var currentOrder=item.get('order');
			if (currentOrder>=insertAfterOrder) {item.set('order',item.get('order')+1);
				console.log("upping "+currentOrder);
			}
		}); // end of forEach

		FLOW.store.commit();
		
		// create copy of QuestionGroup item in the store
		var newRec = FLOW.store.createRecord(FLOW.QuestionGroup,{
			"description": FLOW.selectedControl.selectedForCopyQuestionGroup.get('description'),
			"order":insertAfterOrder,
			"name":FLOW.selectedControl.selectedForCopyQuestionGroup.get('name'),
			"surveyId":FLOW.selectedControl.selectedForCopyQuestionGroup.get('surveyId'),
			"displayName":FLOW.selectedControl.selectedForCopyQuestionGroup.get('displayName')});

		console.log("displayName: " + newRec.get('displayName'));
		FLOW.store.commit();

		// TODO implement commit to persistence layer
		// TODO create copy of questions contained in QuestionGroup and insert them in the store
		var sId=FLOW.selectedControl.selectedSurvey.get('keyId');
		console.log("keyId: "+ sId);

		FLOW.questionGroupControl.set('content',FLOW.store.find(FLOW.QuestionGroup, {surveyId:sId})); // only shows original 5. perhaps because of fixtures?
		//FLOW.questionGroupControl.set('content',null); // works, deletes content
		//FLOW.questionGroupControl.propertyDidChange('content');

		//console.log("going to print names now!");
		//FLOW.questionGroupControl.get('content').forEach(function(item){console.log(item.get('displayName'));});

		FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
	}
	// *************************** END DOES NOT WORK *************************



}); // end QuestionGroupItemView



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

			var isOpen = (this.content.get('keyId') == FLOW.selectedControl.selectedQuestionType.get('keyId'));
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
		console.log('canceling edit');
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

