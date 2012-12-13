// ***********************************************//
//                      Navigation views
// ***********************************************//
/*global tooltip, makePlaceholders */


require('akvo-flow/core');
require('akvo-flow/views/survey-group-views');
require('akvo-flow/views/survey-details-views');
require('akvo-flow/views/inspect-data-table-views');
require('akvo-flow/views/question-view');
require('akvo-flow/views/map-views');
require('akvo-flow/views/devices-views');

FLOW.ApplicationView = Ember.View.extend({
	templateName: 'application/application',

  init: function () {
    var locale;

    this._super();
    
    // If available set language from local storage
    locale = localStorage.locale;
    if (typeof locale === 'undefined') {
      locale = 'en';
    }
    switch(locale) {
      case 'nl':
        Ember.STRINGS = Ember.STRINGS_NL; break;
      case 'fr':
        Ember.STRINGS = Ember.STRINGS_FR; break;
      case 'es':
        Ember.STRINGS = Ember.STRINGS_ES; break;
      default:
        Ember.STRINGS = Ember.STRINGS_EN; break;
    }
  }

  // For some reason if we rerender this view we loose current selection
  // onLanguageChange: function () {
  //   this.rerender();
  // }.observes('FLOW.languageControl.dashboardLanguage')

});

// ***********************************************//
//                      Handlebar helpers
// ***********************************************//

// localisation helper
Ember.Handlebars.registerHelper('t', function(i18nKey, options) {
  return Ember.String.loc(i18nKey);
});

// add space to vertical bar helper
Ember.Handlebars.registerHelper('addSpace', function(property) {
  return Ember.get(this,property).replace(/\|/g,' | ');
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

// Register a Handlebars helper that instantiates `view`.
// The view will have its `content` property bound to the
// helper argument.
FLOW.registerViewHelper = function(name, view) {
  Ember.Handlebars.registerHelper(name, function(property, options) {
    options.hash.contentBinding = property;
    return Ember.Handlebars.helpers.view.call(this, view, options);
  });
};

FLOW.registerViewHelper('date2', Ember.View.extend({
  tagName: 'span',

  template: Ember.Handlebars.compile('{{view.formattedContent}}'),

  formattedContent: (function() {
    var content = this.get('content');

    if (content !== null) {
        d = new Date(parseInt(content,10));
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
    } else {
    	return "";
    }
  }).property('content')
}));

// ********************************************************//
//                      main navigation
// ********************************************************//

FLOW.NavigationView = Em.View.extend({
	templateName: 'application/navigation',
	selectedBinding: 'controller.selected',

  onLanguageChange: function () {
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

// TODO check if doing this in View is not impacting performance,
// as some pages have a lot of views (all navigation elements, for example)
// one way could be use an extended copy of view, with the didInsertElement,
// for some of the elements, and not for others.
Ember.View.reopen({
    didInsertElement: function() {
        this._super();
        tooltip();
	$("nav#topnav li.current").prev("nav#topnav li").css("background", "none");
	$("nav#topnav li").hover( function (){
		$(this).prev().css("background", "none");
		});
	// Adds needed classes to survey assets as nth-child selectors don't work in ie.
    $('li.aSurvey:nth-child(4n+1)').addClass('firstOfRow');
    $('li.aSurvey:nth-child(4n)').addClass('endOfRow');
    $('table#devicesListTable tbody tr:nth-child(2n)').addClass('even');
    

	var nCount = 0;
	$(".addQuestion").click( function () {
		nCount++;
		$(".questionSetContent div.innerContent").fadeIn().css("box-shadow","0 0 3px rgba(0,0,0,0.1)");
		$(this).insertAfter("div.innerContent");
		$("#numberQuestion").text(
			function() {
				if ( nCount < 10 ) {
					$(this).text("0" + nCount);
					}
				else {
					$(this).text(nCount);
					}
				}
		);
		var nQ = parseInt($("#numberQuestion").text(), 10);
		$("h1.questionNbr span").text(
			function () {
				if (nQ < 10) {
					$(this).text("0" + nQ);
					}
				else {
					$(this).text(nQ);
					}
				}
		);
		}
	);
	
// Function displaying the options depending on question type
	$('.formElems').hide();
    // listener for QR type
    $("#questionType").change(function () {
        var selected = $("#questionType option:selected").val();
            $('.formElems').hide();
            $("." +selected).show();
    });
	
    // datepickers
	$( "#from_date" ).datepicker({
			dateFormat: 'yy/mm/dd',
			defaultDate: "+1w",
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#to_date" ).datepicker( "option", "minDate", selectedDate );
				FLOW.dateControl.set('fromDate', selectedDate);
			}
		});

	$( "#to_date" ).datepicker({
			dateFormat: 'yy/mm/dd',
			defaultDate: "+1w",
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#from_date" ).datepicker( "option", "maxDate", selectedDate );
				FLOW.dateControl.set('toDate', selectedDate);
			}
		});
   }
});
// home screen view
FLOW.NavHomeView = Ember.View.extend({templateName: 'navHome/nav-home'});

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
FLOW.NavAdminView = Ember.View.extend({
  templateName: 'navAdmin/nav-admin',
  
  onLanguageChange: function () {
    this.rerender();
  }.observes('FLOW.languageControl.dashboardLanguage')
});
 
FLOW.HeaderView = Ember.View.extend({
  templateName: 'application/header',
  
  onLanguageChange: function () {
    this.rerender();
  }.observes('FLOW.languageControl.dashboardLanguage')
});

FLOW.FooterView = Ember.View.extend({
  templateName: 'application/footer',
  
  forceObserverToggle:function(){
    FLOW.forceObserverControl.toggleProperty('forceObserverBool');
  },

  onLanguageChange: function () {
    this.rerender();
  }.observes('FLOW.languageControl.dashboardLanguage')
});

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
});
