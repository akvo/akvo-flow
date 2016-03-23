FLOW.dashboardLanguageControl = Ember.Object.create({
  dashboardLanguage: FLOW.Env.locale,

  content: [{ label: "English (Default)", value: "en"},
            { label: "Español", value: "es" },
            { label: "Français", value: "fr" },
            { label: "Português", value: "pt" }],

  languageChanged: function () {
	var current = localStorage.locale,
        changed = this.get('dashboardLanguage').value;

    if (current !== changed) {
      localStorage.locale = changed;
      window.location = window.location;
    }
  }.observes('dashboardLanguage')
});

FLOW.reportLanguageControl = Ember.ArrayController.create({
  content: [
    Ember.Object.create({
	  label: "English (Default)",
	  value: "en"
	}),
	Ember.Object.create({
	  label: "Español",
	  value: "es"
	})]
});


FLOW.selectedControl = Ember.Controller.create({
  selectedSurveyGroup: null,
  selectedSurvey: null,
  selectedSurveys: [],
  selectedSurveyAllQuestions: null,
  selectedSurveyAssignment: null,
  dependentQuestion: null,
  selectedQuestionGroup: null,
  selectedQuestion: null,
  selectedOption: null,
  selectedDevice: null,
  selectedDevices: [],
  selectedDevicesPreview: [],
  selectedSurveysPreview: [],
  selectedForMoveQuestionGroup: null,
  selectedForCopyQuestionGroup: null,
  selectedForMoveQuestion: null,
  selectedForCopyQuestion: null,
  selectedCreateNewGroup: false,
  selectedSurveyOPTIONQuestions: null,
  selectedCascadeResource:null,
  radioOptions: "",
  cascadeImportNumLevels: null,
  cascadeImportIncludeCodes: null,

  // OptionQuestions:function (){
  //   console.log('optionquestions 1');
  // }.observes('this.selectedSurveyOPTIONQuestions'),

  // when selected survey changes, deselect selected surveys and question groups
  deselectSurveyGroupChildren: function () {
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.selectedControl.set('selectedSurveyAllQuestions', null);
    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedQuestion', null);
  }.observes('this.selectedSurveyGroup'),

  deselectSurveyChildren: function () {
    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedQuestion', null);
  }.observes('this.selectedSurvey')
});


// used in user tab
FLOW.editControl = Ember.Controller.create({
  newPermissionLevel: null,
  newUserName: null,
  newEmailAddress: null,
  editPermissionLevel: null,
  editUserName: null,
  editEmailAddress: null,
  editUserId: null,
  editAttributeName: null,
  editAttributeGroup: null,
  editAttributeType: null,
  editAttributeId: null
});


FLOW.tableColumnControl = Ember.Object.create({
  sortProperties: null,
  sortAscending: true,
  selected: null,
  content: null
});


// set by restadapter sideLoad meta
FLOW.metaControl = Ember.Object.create({
  numSILoaded: null, // used by data tab nextPage method
  numSLLoaded: null, // used by monitored data tab nextPage method
  since: null,
  num: null,
  message: null,
  status: null,
  cursorType: null,
}),


// set by javacript datepickers in views.js
FLOW.dateControl = Ember.Object.create({
  // filled by javacript datepicker defined in views.js and by inspect-data.handlebars
  // binding. This makes sure we can both pick a date with the datepicker, and enter
  // a date manually
  fromDate: null,
  toDate: null
});


FLOW.savingMessageControl = Ember.Object.create({
  areSavingBool: false,
  areLoadingBool: false,
  numberLoading: 0,

  numLoadingChange: function (delta) {
	  this.set('numberLoading',this.get('numberLoading') + delta);
	  if (this.get('numberLoading') < 0){
		  this.set('numberLoading', 0);
	  }
	  if (this.get('numberLoading') > 0) {
		  this.set('areLoadingBool', true);
	  } else {
		  this.set('areLoadingBool', false);
	  }
  },

  checkSaving: function () {
    if (FLOW.store.defaultTransaction.buckets.inflight.list.get('length') > 0) {
      this.set('areSavingBool', true);
    } else {
      this.set('areSavingBool', false);
    }
  }
});
