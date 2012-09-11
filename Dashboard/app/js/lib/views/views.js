// ***********************************************//
//                      views                    
// ***********************************************//

require('akvo-flow/core');


FLOW.ApplicationView = Ember.View.extend({
	templateName: 'application'
});

// main Navigation with 'active' indication  
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

// standard views
FLOW.NavHomeView = Ember.View.extend({
	templateName: 'navHome'
});
FLOW.NavSurveysView = Ember.View.extend({
	templateName: 'navSurveys'	
});

FLOW.NavDevicesView = Ember.View.extend({
	templateName: 'navDevices'
});
FLOW.NavDataView = Ember.View.extend({
	templateName: 'navData'
});
FLOW.InspectDataView = Ember.View.extend({
	templateName: 'navData/inspectData'
});
FLOW.ImportSurveyView = Ember.View.extend({
	templateName: 'navData/importSurvey'
});
FLOW.ExcelImportView = Ember.View.extend({
	templateName: 'navData/excelImport'
});
FLOW.ExcelExportView = Ember.View.extend({
	templateName: 'navData/excelExport'
});
FLOW.NavReportsView = Ember.View.extend({
	templateName: 'navReports'
});
FLOW.NavMapsView = Ember.View.extend({
	templateName: 'navMaps'
});
FLOW.NavUsersView = Ember.View.extend({
	templateName: 'navUsers'
});
FLOW.NavAdminView = Ember.View.extend({
	templateName: 'navAdmin'
});


// subnavigation for the data tab
FLOW.DatasubnavView = Em.View.extend({
	templateName: 'navData/datasubnav',
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
	}.property('FLOW.selectedControl.selectedQuestionGroup', 'content.keyId'),

	toggleVisibility: function() {
		if (this.get('amVisible')) {
			FLOW.selectedControl.set('selectedQuestionGroup', null);
		} else {
			FLOW.selectedControl.set('selectedQuestionGroup', this.content);
		}
	},

	showHideText: function() {
		return this.get('amVisible') ? 'Close question group' : 'Open question group';
	}.property('amVisible')

});

FLOW.QuestionView = Ember.View.extend({
	content:null,
	
	doEdit: function() {
		console.log("doing doEdit");
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


FLOW.SurveyGroupMenuItemView = Ember.View.extend({
	content: null,
	tagName: 'li',
	classNameBindings: 'amSelected:current'.w(),

	amSelected: function() {
		var selected = FLOW.selectedControl.get('selectedSurveyGroup');
		if (selected) {
			var amSelected = (this.content.get('keyId') === FLOW.selectedControl.selectedSurveyGroup.get('keyId'));
			return amSelected;
		} else {
			return null;
		}
	}.property('FLOW.selectedControl.selectedSurveyGroup', 'content'),

	makeSelected: function() {
			console.log("selecting survey Group: "+this.content.get('keyId'));
			FLOW.selectedControl.set('selectedSurveyGroup', this.content);
	}
});

FLOW.SurveyGroupTopBarView = Ember.View.extend({
	
	showEditField: false,
	showNewGroupField:false,
	surveyGroupName:null,
	
	oneSelected: function() {
		var selected = FLOW.selectedControl.get('selectedSurveyGroup');
		if (selected) {
			return true;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedSurveyGroup'),

	editSurveyInfo: function() {
			console.log("Edit survey info");
			this.set('surveyGroupName',FLOW.selectedControl.selectedSurveyGroup.get('displayName'));
			this.set('showEditField',true);			
	},
	
	saveSurveyGroupNameEdit: function() {
			var sgId=FLOW.selectedControl.selectedSurveyGroup.get('id');
			var surveyGroup=FLOW.store.find(FLOW.SurveyGroup, sgId);
			surveyGroup.set('displayName',this.get('surveyGroupName'));	
			this.set('showEditField',false);			
	},
	
	cancelSurveyGroupNameEdit: function() {
			console.log("cancel survey group name edit info");
			this.set('surveyGroupName',FLOW.selectedControl.selectedSurveyGroup.get('displayName'));
			this.set('showEditField',false);			
	},
	
	addGroup: function() {
			console.log("create group");
			FLOW.selectedControl.set('selectedSurveyGroup',null);
			this.set('surveyGroupName',null);
			this.set('showNewGroupField',true);	
	},
	
	saveNewSurveyGroupName: function() {
			console.log("saving new survey group name");
			var newSG = FLOW.store.createRecord(FLOW.SurveyGroup,{"keyId":"","name":this.get('surveyGroupName'),"displayName":this.get('surveyGroupName'),"code":this.get('surveyGroupName')});

			this.set('showNewGroupField',false);	
	},
	
	cancelNewSurveyGroupName: function() {
			console.log("cancel new survey group name");
			this.set('surveyGroupName',null);
			this.set('showNewGroupField',false);	
	},
		
	createSurvey: function() {
			console.log("TODO create Survey");			
	},
	editSurvey: function() {
			console.log("TODO edit Survey");			
	},
	previewSurvey: function() {
			console.log("TODO preview Survey");			
	},
	deleteSurvey: function() {
			console.log("TODO delete Survey");			
	},
	inspectData: function() {
			console.log("TODO inspect Data");			
	}
});