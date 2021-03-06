import observe from '../mixins/observe';

FLOW.dashboardLanguageControl = Ember.Object.create(observe({ dashboardLanguage: 'languageChanged' }), {
  dashboardLanguage: FLOW.Env.locale,

  content: [{ label: 'English (Default)', value: 'en' },
    { label: 'Español', value: 'es' },
    { label: 'Français', value: 'fr' },
    { label: 'Bahasa Indonesia', value: 'id' },
    { label: 'Português', value: 'pt' },
    { label: 'Tiếng Việt', value: 'vi' }],

  languageChanged() {
    const localeUrl = `/ui-strings.js?locale=${this.dashboardLanguage}`;
    $.ajax({
      url: localeUrl,
      complete() {
        window.location.reload(false);
      },
    });
  },
});

FLOW.selectedControl = Ember.Controller.create(observe({
  'this.selectedSurveyGroup': 'deselectSurveyGroupChildren',
  'this.selectedSurvey': 'deselectSurveyChildren',
}), {
  selectedSurveyGroup: null,
  selectedSurvey: null,
  selectedSurveys: [],
  selectedSurveyAllQuestions: null,
  selectedSurveyAssignment: null,
  dependentQuestion: null,
  selectedQuestionGroup: null,
  publishingErrors: null,
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
  selectedCascadeResource: null,
  selectedCaddisflyResource: null,
  radioOptions: '',
  cascadeImportNumLevels: null,
  cascadeImportIncludeCodes: null,

  // when selected survey changes, deselect selected surveys and question groups
  deselectSurveyGroupChildren() {
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.selectedControl.set('selectedSurveyAllQuestions', null);
    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedQuestion', null);
  },

  deselectSurveyChildren() {
    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedQuestion', null);
    FLOW.selectedControl.set('publishingErrors', null);
  },
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
  editAttributeId: null,
});


FLOW.tableColumnControl = Ember.Object.create({
  sortProperties: null,
  sortAscending: true,
  selected: null,
  content: null,
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
});

// set by javacript datepickers in views.js
FLOW.dateControl = Ember.Object.create({
  // filled by javacript datepicker defined in views.js and by inspect-data.handlebars
  // binding. This makes sure we can both pick a date with the datepicker, and enter
  // a date manually
  fromDate: null,
  toDate: null,
});


FLOW.savingMessageControl = Ember.Object.create({
  areSavingBool: false,
  areLoadingBool: false,
  numberLoading: 0,

  numLoadingChange(delta) {
    this.set('numberLoading', this.get('numberLoading') + delta);
    if (this.get('numberLoading') < 0) {
      this.set('numberLoading', 0);
    }
    if (this.get('numberLoading') > 0) {
      this.set('areLoadingBool', true);
    } else {
      this.set('areLoadingBool', false);
    }
  },

  checkSaving() {
    if (FLOW.store.defaultTransaction.buckets.inflight.list.get('length') > 0) {
      this.set('areSavingBool', true);
    } else {
      this.set('areSavingBool', false);
    }
  },
});
