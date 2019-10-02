import React from 'react';
// eslint-disable-next-line import/no-unresolved
import AssignmentsEditView from 'akvo-flow/components/devices/AssignmentsEditView';
import observe from '../../mixins/observe';

// eslint-disable-next-line import/no-unresolved
require('akvo-flow/views/react-component');

// utils
FLOW.ArrNoDupe = function (a) {
  let gotIt;
  const templ = {};
  const tempa = Ember.A([]);
  for (let i = 0; i < a.length; i++) {
    templ[a.objectAt(i).clientId] = true;
  }
  const keys = Object.keys(templ);
  for (let j = 0; j < keys.length; j++) {
    gotIt = false;
    for (let i = 0; i < a.length; i++) {
      if (a.objectAt(i).clientId == keys[j] && !gotIt) {
        tempa.pushObject(a.objectAt(i));
        gotIt = true;
      }
    }
  }
  return tempa;
};

FLOW.formatDate = function (value) {
  if (!Ember.none(value)) {
    return `${value.getFullYear()}/${value.getMonth() + 1}/${value.getDate()}`;
  } return null;
};

FLOW.AssignmentEditView = FLOW.ReactComponentView.extend(
  observe({
    'FLOW.router.navigationController.selected': 'detectChangeTab',
    'FLOW.router.devicesSubnavController.selected': 'detectChangeTab',
    'FLOW.surveyControl.content.isLoaded': 'detectSurveyLoaded',
  }),
  {
    init() {
      this._super();
      this.setupControls();

      this.getProps = this.getProps.bind(this);
      this.cancelEditSurveyAssignment = this.cancelEditSurveyAssignment.bind(this);
      this.detectSurveyLoaded = this.detectSurveyLoaded.bind(this);
      this.renderReactSide = this.renderReactSide.bind(this);
      this.handleFormCheck = this.handleFormCheck.bind(this);
      this.canAddFormsToAssignment = this.canAddFormsToAssignment.bind(this);
      this.validateAssignment = this.validateAssignment.bind(this);
      this.saveSurveyAssignment = this.saveSurveyAssignment.bind(this);
      this.setupForms = this.setupForms.bind(this);

      // object wide varaibles
      this.forms = {};
    },

    didInsertElement(...args) {
      this._super(...args);

      this.setupForms();

      // react render
      this.renderReactSide();
    },

    renderReactSide() {
      const props = this.getProps();
      this.reactRender(<AssignmentsEditView {...props} />);
    },

    getProps() {
      const strings = {
        backToAssignmentList: Ember.String.loc('_go_back_to_assignment_list'),
        assignmentDetails: Ember.String.loc('_assignment_details'),
        assignmentName: Ember.String.loc('_assignment_name'),
        assignmentNamePlaceholder: Ember.String.loc('_enter_a_name_for_this_assignment'),
        startDate: Ember.String.loc('_start_date'),
        expireDate: Ember.String.loc('_expiration_date'),
        selectSurvey: Ember.String.loc('_select_survey'),
        cantFindYourSurvey: Ember.String.loc('_cant_find_your_survey_'),
        selectForms: Ember.String.loc('_select_forms'),
        selectDevices: Ember.String.loc('_select_devices'),
        selectDeviceGroup: Ember.String.loc('_select_device_group'),
        saveAssignment: Ember.String.loc('_save_assignment'),
        cancel: Ember.String.loc('_cancel'),
      };

      const inputValues = {
        assignmentName: FLOW.selectedControl.selectedSurveyAssignment.get('name'),
        startDate: FLOW.dateControl.fromDate,
        toDate: FLOW.dateControl.toDate,
      };

      const actions = {
        cancelEditSurveyAssignment: this.cancelEditSurveyAssignment,
        handleFormCheck: this.handleFormCheck,
        validateAssignment: this.validateAssignment,
        onSubmit: this.saveSurveyAssignment,
      };

      const data = {
        forms: this.forms,
      };

      return {
        strings, actions, inputValues, data,
      };
    },

    cancelEditSurveyAssignment() {
      if (Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))) {
        FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
      }
      FLOW.selectedControl.set('selectedSurveyAssignment', null);
      FLOW.router.transitionTo('navDevices.assignSurveysOverview');
    },

    saveSurveyAssignment(data) {
      let endDateParse;
      let startDateParse;
      const devices = [];
      const surveys = [];

      // set Ember Data
      FLOW.dateControl.set('fromDate', FLOW.formatDate(new Date(data.startDate)));
      FLOW.dateControl.set('toDate', FLOW.formatDate(new Date(data.expireDate)));

      // get assignment
      const sa = FLOW.selectedControl.get('selectedSurveyAssignment');

      // set assignment name
      sa.set('name', data.assignmentName);

      // parse date
      if (!Ember.none(FLOW.dateControl.get('toDate'))) {
        endDateParse = Date.parse(FLOW.dateControl.get('toDate'));
      } else {
        endDateParse = null;
      }

      if (!Ember.none(FLOW.dateControl.get('fromDate'))) {
        startDateParse = Date.parse(FLOW.dateControl.get('fromDate'));
      } else {
        startDateParse = null;
      }

      // set data and language
      sa.set('endDate', endDateParse);
      sa.set('startDate', startDateParse);
      sa.set('language', 'en');

      FLOW.selectedControl.get('selectedDevices').forEach((item) => {
        devices.push(item.get('keyId'));
      });
      sa.set('deviceIds', devices);

      FLOW.selectedControl.get('selectedSurveys').forEach((item) => {
        surveys.push(item.get('keyId'));
      });

      sa.set('formIds', surveys);

      FLOW.store.commit();

      // wait half a second before transitioning back to the assignments list
      setTimeout(() => {
        FLOW.router.transitionTo('navDevices.assignSurveysOverview');
      }, 500);
    },

    // setups
    setupControls() {
      FLOW.selectedControl.set('selectedDevices', []);
      FLOW.selectedControl.set('selectedSurveys', []);
      FLOW.selectedControl.set('selectedSurveyGroup', null);
      FLOW.selectedControl.set('selectedDeviceGroup', null);
      FLOW.surveyControl.set('content', null);
      FLOW.devicesInGroupControl.set('content', null);

      let startDate = null;
      let endDate = null;

      if (FLOW.selectedControl.selectedSurveyAssignment.get('startDate') > 0) {
        startDate = new Date(FLOW.selectedControl.selectedSurveyAssignment.get('startDate'));
      }
      if (FLOW.selectedControl.selectedSurveyAssignment.get('endDate') > 0) {
        endDate = new Date(FLOW.selectedControl.selectedSurveyAssignment.get('endDate'));
      }
      FLOW.dateControl.set('fromDate', FLOW.formatDate(startDate));
      FLOW.dateControl.set('toDate', FLOW.formatDate(endDate));
    },

    setupForms() {
      if (!FLOW.selectedControl.selectedSurveyAssignment.get('formIds')) {
        return;
      }

      FLOW.selectedControl.selectedSurveyAssignment.get('formIds').forEach((formId) => {
        const form = FLOW.Survey.find(formId);
        if (form && form.get('keyId')) {
          FLOW.selectedControl.selectedSurveys.pushObject(form);
          this.forms[form.get('keyId')] = { // also load pre-selected forms
            name: form.get('name'),
            checked: true,
          };
        }
      });
    },

    // listeners
    detectSurveyLoaded() {
      this.forms = {};

      if (!FLOW.surveyControl.content) return;

      FLOW.surveyControl.content.forEach((form) => {
        this.forms[form.get('keyId')] = {
          name: form.get('name'),
          checked: this.formInAssignment(form.get('keyId')),
        };
      });

      this.renderReactSide();
    },

    detectChangeTab() {
      if (Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))) {
        FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
      }
      FLOW.selectedControl.set('selectedSurveyAssignment', null);
    },

    // helpers
    formInAssignment(formId) {
      const formsInAssignment = FLOW.selectedControl.selectedSurveyAssignment.get('formIds');
      return formsInAssignment ? formsInAssignment.indexOf(formId) > -1 : false;
    },

    canAddFormsToAssignment() {
      // only allow if form qualifies
      const formsInAssignment = FLOW.selectedControl.selectedSurveyAssignment.get('formIds');
      const selectedSurveyGroupId = FLOW.selectedControl.selectedSurveyGroup.get('keyId');

      if (formsInAssignment && formsInAssignment.length > 0) {
        // get survey id of first form currently in assignment
        const preSelectedSurvey = FLOW.Survey.find(formsInAssignment[0]);
        if (preSelectedSurvey && preSelectedSurvey.get('keyId')) {
          return preSelectedSurvey.get('surveyGroupId') == selectedSurveyGroupId;
        }
      }

      return true; // no forms are currently added to the assignment
    },

    validateAssignment(assignmentName) {
      this.set('assignmentValidationFailure', (
        (assignmentName && assignmentName.length > 100)
        || !assignmentName || assignmentName == ''));
      if (assignmentName && assignmentName.length > 100) {
        this.set('assignmentValidationFailureReason', Ember.String.loc('_assignment_name_over_100_chars'));
      } else if (!assignmentName || assignmentName == '') {
        this.set('assignmentValidationFailureReason', Ember.String.loc('_assignment_name_not_set'));
      }
    },

    // handlers
    handleFormCheck(e) {
      // only allow a form to be checked if a different survey isn't already selected
      const formId = e.target.name;
      if (this.canAddFormsToAssignment()) {
        this.forms[formId].checked = !this.forms[formId].checked;
      } else {
        // TODO: display error that form cannot be added unless currently added forms are removed
      }

      this.renderReactSide();

      // add/remove form to/from assignment
      if (this.forms[formId].checked) {
        // push survey to FLOW.selectedControl.selectedSurveys
        FLOW.selectedControl.selectedSurveys.pushObject(FLOW.Survey.find(formId));
      } else {
        FLOW.selectedControl.selectedSurveys.removeObject(FLOW.Survey.find(formId));
      }

      // TODO: load data points in selected form
    },
  }
);
