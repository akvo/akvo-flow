/* eslint-disable import/no-unresolved */
import { trackEvent, DATA_INSPECT } from 'akvo-flow/analytics';
import { isNaN } from 'lodash';
import observe from '../../mixins/observe';

FLOW.inspectDataTableView = FLOW.View.extend(
  observe({
    'FLOW.selectedControl.selectedSurvey': 'watchSurveySelection',
  }),
  {
    selectedSurvey: null,
    surveyInstanceId: null,
    surveyId: null,
    deviceId: null,
    submitterName: null,
    beginDate: null,
    endDate: null,
    since: null,
    alreadyLoaded: [],
    selectedCountryCode: null,
    selectedLevel1: null,
    selectedLevel2: null,
    showEditSurveyInstanceWindowBool: false,
    selectedSurveyInstanceId: null,
    selectedSurveyInstanceNum: null,
    siString: null,
    missingSurvey: false,

    form: Ember.computed(() => {
      if (FLOW.selectedControl.get('selectedSurvey')) {
        return FLOW.selectedControl.get('selectedSurvey');
      }
    }).property('FLOW.selectedControl.selectedSurvey'),

    init() {
      this._super();
      FLOW.selectedControl.set('selectedSurveyGroup', null);
      FLOW.selectedControl.set('selectedSurvey', null);
      FLOW.dateControl.set('toDate', null);
      FLOW.dateControl.set('fromDate', null);
      FLOW.surveyInstanceControl.set('pageNumber', 0);
      FLOW.surveyInstanceControl.set('currentContents', null);
      FLOW.locationControl.set('selectedLevel1', null);
      FLOW.locationControl.set('selectedLevel2', null);
    },

    // do a new query
    doFindSurveyInstances() {
      // check first that survey is selected before performing find action
      if (FLOW.selectedControl.get('selectedSurvey') === null) {
        this.set('missingSurvey', true);
        return;
      }

      trackEvent(DATA_INSPECT, 'Inspect data');

      FLOW.surveyInstanceControl.get('sinceArray').clear();
      FLOW.surveyInstanceControl.set('pageNumber', -1);
      FLOW.metaControl.set('since', null);
      this.doNextPage();
    },

    watchSurveySelection() {
      if (FLOW.selectedControl.get('selectedSurvey') !== null) {
        this.set('missingSurvey', false);
      }
    },

    doInstanceQuery() {
      this.set('beginDate', Date.parse(FLOW.dateControl.get('fromDate')));
      // we add 24 hours to the date, in order to make the date search inclusive.
      const dayInMilliseconds = 24 * 60 * 60 * 1000;
      this.set('endDate', Date.parse(FLOW.dateControl.get('toDate')) + dayInMilliseconds);

      // we shouldn't be sending NaN
      if (isNaN(this.get('beginDate'))) {
        this.set('beginDate', null);
      }
      if (isNaN(this.get('endDate'))) {
        this.set('endDate', null);
      }

      if (FLOW.selectedControl.get('selectedSurvey')) {
        this.set('surveyId', FLOW.selectedControl.selectedSurvey.get('keyId'));
      } else {
        this.set('surveyId', null);
      }

      if (!Ember.none(FLOW.locationControl.get('selectedCountry'))) {
        this.set('selectedCountryCode', FLOW.locationControl.selectedCountry.get('iso'));
      } else {
        this.set('selectedCountryCode', null);
      }

      if (!Ember.none(FLOW.locationControl.get('selectedLevel1'))) {
        this.set('selectedLevel1', FLOW.locationControl.selectedLevel1.get('name'));
      } else {
        this.set('selectedLevel1', null);
      }

      if (!Ember.none(FLOW.locationControl.get('selectedLevel2'))) {
        this.set('selectedLevel2', FLOW.locationControl.selectedLevel2.get('name'));
      } else {
        this.set('selectedLevel2', null);
      }

      FLOW.surveyInstanceControl.doInstanceQuery(
        this.get('surveyInstanceId'),
        this.get('surveyId'),
        this.get('deviceId'),
        this.get('since'),
        this.get('beginDate'),
        this.get('endDate'),
        this.get('submitterName'),
        this.get('selectedCountryCode'),
        this.get('selectedLevel1'),
        this.get('selectedLevel2')
      );
    },

    doNextPage() {
      const cursorArray = FLOW.surveyInstanceControl.get('sinceArray');
      const cursor = cursorArray.length > 0 ? cursorArray[cursorArray.length - 1] : null;
      this.set('since', cursor);
      this.doInstanceQuery();
      FLOW.surveyInstanceControl.set(
        'pageNumber',
        FLOW.surveyInstanceControl.get('pageNumber') + 1
      );
    },

    doPrevPage() {
      const cursorArray = FLOW.surveyInstanceControl.get('sinceArray');
      const cursor = cursorArray.length - 3 > -1 ? cursorArray[cursorArray.length - 3] : null;
      this.set('since', cursor);
      this.doInstanceQuery();
      FLOW.surveyInstanceControl.set(
        'pageNumber',
        FLOW.surveyInstanceControl.get('pageNumber') - 1
      );
    },

    // If the number of items in the previous call was 20 (a full page) we assume that there are more.
    // This is not foolproof, but will only lead to an empty next page in 1/20 of the cases
    hasNextPage: Ember.computed(() => FLOW.metaControl.get('numSILoaded') == 20).property(
      'FLOW.metaControl.numSILoaded'
    ),

    // not perfect yet, sometimes previous link is shown while there are no previous pages.
    hasPrevPage: Ember.computed(() => FLOW.surveyInstanceControl.get('pageNumber')).property(
      'FLOW.surveyInstanceControl.pageNumber'
    ),

    downloadQuestionsIfNeeded() {
      const si = FLOW.store.find(FLOW.SurveyInstance, this.get('selectedSurveyInstanceId'));
      if (!Ember.none(si)) {
        const surveyId = si.get('surveyId');
        // if we haven't loaded the questions of this survey yet, do so.
        if (this.get('alreadyLoaded').indexOf(surveyId) == -1) {
          FLOW.questionControl.doSurveyIdQuery(surveyId);
          this.get('alreadyLoaded').push(surveyId);
        }
      }
    },

    // Survey instance edit popup window
    // TODO solve when popup is open, no new surveyIdQuery is done
    showEditSurveyInstanceWindow(event) {
      FLOW.questionAnswerControl.doQuestionAnswerQuery(event.context);
      this.get('alreadyLoaded').push(event.context.get('surveyId'));
      this.set('selectedSurveyInstanceId', event.context.get('keyId'));
      this.set('selectedSurveyInstanceNum', event.context.clientId);
      this.set('showEditSurveyInstanceWindowBool', true);
    },

    showEditResponseLink: Ember.computed(function() {
      return FLOW.permControl.canEditResponses(this.get('form'));
    }).property('this.form'),

    doCloseEditSIWindow() {
      this.set('showEditSurveyInstanceWindowBool', false);
    },

    doPreviousSI() {
      const currentSIList = FLOW.surveyInstanceControl.content.get('content');
      const SIindex = currentSIList.indexOf(this.get('selectedSurveyInstanceNum'));

      if (SIindex === 0) {
        // if at the end of the list, go and get more data
      } else {
        const nextItem = currentSIList.objectAt(SIindex - 1);
        const filtered = FLOW.store.filter(FLOW.SurveyInstance, item => {
          if (item.clientId == nextItem) {
            return true;
          }
          return false;
        });
        const nextSI = filtered.objectAt(0);
        const nextSIkeyId = nextSI.get('keyId');
        this.set('selectedSurveyInstanceId', nextSIkeyId);
        this.set('selectedSurveyInstanceNum', nextItem);
        this.downloadQuestionsIfNeeded();
        FLOW.questionAnswerControl.doQuestionAnswerQuery(nextSI);
      }
    },

    // TODO error checking
    doNextSI() {
      const currentSIList = FLOW.surveyInstanceControl.content.get('content');
      const SIindex = currentSIList.indexOf(this.get('selectedSurveyInstanceNum'));

      if (SIindex == 19) {
        // TODO get more data
        // if at the end of the list, we should first go back and get more data
      } else {
        const nextItem = currentSIList.objectAt(SIindex + 1);
        const filtered = FLOW.store.filter(FLOW.SurveyInstance, item => {
          if (item.clientId == nextItem) {
            return true;
          }
          return false;
        });
        const nextSI = filtered.objectAt(0);
        const nextSIkeyId = nextSI.get('keyId');
        this.set('selectedSurveyInstanceId', nextSIkeyId);
        this.set('selectedSurveyInstanceNum', nextItem);
        this.downloadQuestionsIfNeeded();
        FLOW.questionAnswerControl.doQuestionAnswerQuery(nextSI);
      }
    },

    showSurveyInstanceDeleteButton: Ember.computed(() => {
      const permissions = FLOW.surveyControl.get('currentFormPermissions');
      return permissions.indexOf('DATA_DELETE') >= 0;
    }).property('FLOW.selectedControl.selectedSurvey'),

    doShowDeleteSIDialog() {
      FLOW.dialogControl.set('activeAction', 'delSI');
      FLOW.dialogControl.set('showCANCEL', true);
      FLOW.dialogControl.set('showDialog', true);
    },

    deleteSI() {
      const SIid = this.get('selectedSurveyInstanceId');
      const SI = FLOW.store.find(FLOW.SurveyInstance, SIid);
      if (SI !== null) {
        // remove from displayed content
        SI.deleteRecord();
        FLOW.store.commit();
      }
      this.set('showEditSurveyInstanceWindowBool', false);
    },

    validSurveyInstanceId: Ember.computed(function() {
      return (
        this.surveyInstanceId === null ||
        this.surveyInstanceId === '' ||
        this.surveyInstanceId.match(/^\d+$/)
      );
    }).property('this.surveyInstanceId'),

    noResults: Ember.computed(() => {
      const content = FLOW.surveyInstanceControl.get('content');
      if (content && content.get('isLoaded')) {
        return content.get('length') === 0;
      }
      return false;
    }).property(
      'FLOW.surveyInstanceControl.content',
      'FLOW.surveyInstanceControl.content.isLoaded'
    ),

    // clearing the SI records when the user navigates away from inspect-tab.
    willDestroyElement() {
      FLOW.surveyInstanceControl.set('currentContents', null);
      FLOW.metaControl.set('numSILoaded', null);
      FLOW.surveyInstanceControl.set('pageNumber', 0);
    },
  }
);

FLOW.DataItemView = FLOW.View.extend({
  tagName: 'span',
  deleteSI() {
    const SI = FLOW.store.find(FLOW.SurveyInstance, this.content.get('keyId'));
    if (SI !== null) {
      // check if we also have the data point loaded locally
      const slKey = SI.get('surveyedLocaleId');
      const SL = FLOW.store.filter(FLOW.SurveyedLocale, item => item.get('keyId') == slKey);
      // if we have found the surveyedLocale, check if there are more
      // formInstances inside it
      if (!Ember.empty(SL)) {
        // are there any other formInstances loaded for this surveyedLocale?
        // if not, we also need to not show the locale any more.
        // it will also be deleted automatically in the backend,
        // so this is just to not show it in the UI
        const SiList = FLOW.store.filter(
          FLOW.SurveyInstance,
          item => item.get('surveyedLocaleId') == slKey
        );
        if (SiList.get('content').length == 1) {
          // this is the only formInstance, so the surveyedLocale
          // will be deleted by the backend, and we need to remove
          // it from the UI
          FLOW.router.surveyedLocaleController.removeLocale(SL.objectAt(0));
        }
      }

      FLOW.surveyInstanceControl.removeInstance(SI);
      SI.deleteRecord();
      FLOW.store.commit();
    }
  },
});

FLOW.DataLocaleItemView = FLOW.View.extend({
  tagName: 'span',
  deleteSL() {
    const SL = FLOW.store.find(FLOW.SurveyedLocale, this.content.get('keyId'));
    if (SL !== null) {
      FLOW.router.surveyedLocaleController.removeLocale(SL);
      // the filled forms inside this data point will be deleted by the backend
      SL.deleteRecord();
      FLOW.store.commit();
    }
  },
});

FLOW.DataNumView = FLOW.View.extend({
  tagName: 'span',
  pageNumber: 0,
  content: null,
  rownum: Ember.computed(function() {
    return this.get('_parentView.contentIndex') + 1 + 20 * this.get('pageNumber');
  }).property(),
});
