// ***********************************************//
//                      Navigation views
// ***********************************************//

require('akvo-flow/core');
require('akvo-flow/views/survey-group-views');
require('akvo-flow/views/survey-details-views');
require('akvo-flow/views/question-view');
require('akvo-flow/views/map-views');
require('akvo-flow/views/devices-views');

FLOW.ApplicationView = Ember.View.extend({
	templateName: 'application'
});

// ***********************************************//
//                      Handlebar helpers
// ***********************************************//

// localisation helper
Ember.Handlebars.registerHelper('t', function(i18nKey, options) {
  return Ember.String.loc(i18nKey);
});

// date format helper
Ember.Handlebars.registerHelper("date", function(property) {
  var d = new Date(parseInt(Ember.get(this, property),10));
  var m_names = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

  var curr_date = d.getDate();
  var curr_month = d.getMonth();
  var curr_year = d.getFullYear();
  return (curr_date + " " + m_names[curr_month] + " " + curr_year);
});

// format used in devices table
Ember.Handlebars.registerHelper("date1", function(property) {
  var d, curr_date,curr_month,curr_year,curr_hour,curr_min,monthString,dateString,hourString,minString;
  if (Ember.get(this, property)!==null){
    d = new Date(parseInt(Ember.get(this, property),10));
    curr_date = d.getDate();
    curr_month = d.getMonth()+1;
    curr_year = d.getFullYear();
    curr_hour =d.getHours();
    curr_min =d.getMinutes();

    if (curr_month<10){monthString="0"+curr_month.toString();}
    else { monthString=curr_month.toString();}

    if (curr_date<10){dateString="0"+curr_date.toString();}
    else {dateString=curr_date.toString();}

    if (curr_hour<10) {hourString="0"+curr_hour.toString();}
    else { hourString=curr_hour.toString();}

    if (curr_min<10) {minString="0"+curr_min.toString();}
    else { minString=curr_min.toString();}

    return (curr_year + "-" + monthString + "-" + dateString + "  " + hourString + ":" + minString);
  } else {return "";}
});

// ********************************************************//
//                      main navigation
// ********************************************************//

FLOW.NavigationView = Em.View.extend({
	templateName: 'navigation',
	selectedBinding: 'controller.selected',

	onLanguageChange:function(){
		this.rerender();
	}.observes('FLOW.languageControl.dashboardLanguage'),

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
FLOW.CurrentDevicesView = Ember.View.extend({ templateName: 'navDevices/devices-list-tab/devices-list'});
FLOW.AssignSurveysOverviewView = Ember.View.extend({ templateName: 'navDevices/assignment-list-tab/assignment-list'});
FLOW.EditSurveyAssignmentView = Ember.View.extend({ templateName: 'navDevices/assignment-edit-tab/assignment-edit'});


// data views
FLOW.NavDataView = Ember.View.extend({ templateName: 'navData/nav-data'});
FLOW.InspectDataView = Ember.View.extend({ templateName: 'navData/inspect-data'});
FLOW.ImportSurveyView = Ember.View.extend({ templateName: 'navData/import-survey'});
FLOW.ExcelImportView = Ember.View.extend({ templateName: 'navData/excel-import'});
FLOW.ExcelExportView = Ember.View.extend({ templateName: 'navData/excel-export'});

// reports views
FLOW.NavReportsView = Ember.View.extend({ templateName: 'navReports/nav-reports'});

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

// ********************************************************//
//             Subnavigation for the Device tabs
// ********************************************************//
FLOW.DevicesSubnavView = Em.View.extend({
	templateName: 'navDevices/devices-subnav',
	selectedBinding: 'controller.selected',
	NavItemView: Ember.View.extend({
		tagName: 'li',
		classNameBindings: 'isActive:active'.w(),

		isActive: function() {
			return this.get('item') === this.get('parentView.selected');
		}.property('item', 'parentView.selected').cacheable()
	})
});


FLOW.ColumnView = Ember.View.extend({
	tagName: 'th',
	item:null,
	type:null,
		
	classNameBindings: ['isActiveAsc:sorting_asc','isActiveDesc:sorting_desc'],
		
	isActiveAsc: function() {
		return (this.get('item') === FLOW.tableColumnControl.get('selected')) && (FLOW.tableColumnControl.get('sortAscending')===true);
	}.property('item', 'FLOW.tableColumnControl.selected','FLOW.tableColumnControl.sortAscending').cacheable(),

	isActiveDesc: function() {
		return (this.get('item') === FLOW.tableColumnControl.get('selected'))&&(FLOW.tableColumnControl.get('sortAscending')===false);
	}.property('item', 'FLOW.tableColumnControl.selected','FLOW.tableColumnControl.sortAscending').cacheable(),

	sort:function(){
		if ((this.get('isActiveAsc'))||(this.get('isActiveDesc'))) {
			FLOW.tableColumnControl.toggleProperty('sortAscending');
		} else {
			FLOW.tableColumnControl.set('sortProperties',[this.get('item')]);
			FLOW.tableColumnControl.set('selected',this.get('item'));
		}

		if (this.get('type') === 'device') {
			FLOW.deviceControl.getSortInfo();
		} else if (this.get('type') === 'assignment'){
			FLOW.surveyAssignmentControl.getSortInfo();
		}

	}
})
