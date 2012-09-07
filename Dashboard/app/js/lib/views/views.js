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

// subnavigation for the data tab
FLOW.DatasubnavView = Em.View.extend({
	templateName: 'datasubnav',
	selectedBinding: 'controller.selected',
	NavItemView: Ember.View.extend({
		tagName: 'li',
		classNameBindings: 'isActive:active'.w(),

		isActive: function() {
			return this.get('item') === this.get('parentView.selected');
		}.property('item', 'parentView.selected').cacheable()
	})
});


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
	templateName: 'inspectData'
});
FLOW.ImportSurveyView = Ember.View.extend({
	templateName: 'importSurvey'
});
FLOW.ExcelImportView = Ember.View.extend({
	templateName: 'excelImport'
});
FLOW.ExcelExportView = Ember.View.extend({
	templateName: 'excelExport'
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




FLOW.testControl = Ember.Controller.create({
	data: 25,

})

FLOW.TestView = Ember.View.extend({
	dataBinding: "FLOW.testControl.data",
	change: function() {
		this.set('data', this.get('data') + 1);
	}


});

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