FLOW.dashboardLanguageControl = Ember.Object.create({
  dashboardLanguage: null,

  init: function() {
    var locale;

    this._super();
    locale = localStorage.locale;
    if(typeof locale === 'undefined') {
      this.set('dashboardLanguage', this.content.findProperty('value', 'en'));
    } else {
      this.set('dashboardLanguage', this.content.findProperty('value', locale));
    }
  },

  content: [
    Ember.Object.create({
      label: "English (Default)",
      value: "en"
    }), Ember.Object.create({
      label: "Espanol",
      value: "es"
    }), Ember.Object.create({
      label: "Français",
      value: "fr"
    })
  ],

  changeLanguage: function() {
    var locale;
    locale = this.dashboardLanguage.get("value");
    localStorage.locale = this.get('dashboardLanguage.value');

    if (locale === 'fr') {
      Ember.set('Ember.STRINGS', Ember.STRINGS_FR);
    } else if (locale === 'es') {
      Ember.set('Ember.STRINGS', Ember.STRINGS_ES);
    } else {
      Ember.set('Ember.STRINGS', Ember.STRINGS_EN);
    }

    // if(locale === "fr") {
    //   Ember.STRINGS = Ember.STRINGS_FR;
    // } else if(locale === "es") {
    //   Ember.STRINGS = Ember.STRINGS_ES;
    // } else {
    //   Ember.STRINGS = Ember.STRINGS_EN;
    // }
  }.observes('this.dashboardLanguage')
});


FLOW.selectedControl = Ember.Controller.create({
  selectedSurveyGroup: null,
  selectedSurvey: null,
  selectedSurveys:[],
  selectedSurveyAllQuestions: null,
  selectedSurveyAssignment: null,
  dependentQuestion: null,
  selectedQuestionGroup: null,
  selectedQuestion: null,
  selectedOption: null,
  selectedDevice:null,
  selectedDevices:[],
  selectedDevicesPreview: [],
  selectedSurveysPreview: [],
  selectedForMoveQuestionGroup: null,
  selectedForCopyQuestionGroup: null,
  selectedForMoveQuestion: null,
  selectedForCopyQuestion: null,
  selectedCreateNewGroup: false,
  selectedSurveyOPTIONQuestions: null,
  radioOptions: "",

  // OptionQuestions:function (){
  //   console.log('optionquestions 1');
  // }.observes('this.selectedSurveyOPTIONQuestions'),

  // when selected survey changes, deselect selected surveys and question groups
  deselectSurveyGroupChildren: function() {
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.selectedControl.set('selectedSurveyAllQuestions', null);
    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedQuestion', null);
  }.observes('this.selectedSurveyGroup'),

  deselectSurveyChildren: function() {
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
  since: null,
  num: null,
  message: null,
  status: null
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

  checkSaving: function() {
    if(FLOW.store.defaultTransaction.buckets.inflight.list.get('length') > 0) {
      this.set('areSavingBool', true);
    } else {
      this.set('areSavingBool', false);
    }
  }
});

