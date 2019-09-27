import observe from '../../mixins/observe';

// removes duplicate objects with a clientId from an Ember Array

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

FLOW.AssignmentEditView = FLOW.View.extend(observe({
  'this.assignmentName': 'validateAssignmentObserver',
  'FLOW.router.navigationController.selected': 'detectChangeTab',
  'FLOW.router.devicesSubnavController.selected': 'detectChangeTab',
}), {
  assignmentName: null,
  language: null,

  init() {
    let startDate = null;
    let endDate = null;
    this._super();
    this.set('assignmentName', FLOW.selectedControl.selectedSurveyAssignment.get('name'));
    FLOW.selectedControl.set('selectedDevices', []);
    FLOW.selectedControl.set('selectedSurveys', []);
    FLOW.selectedControl.set('selectedSurveyGroup', null);
    FLOW.selectedControl.set('selectedDeviceGroup', null);
    FLOW.surveyControl.set('content', null);
    FLOW.devicesInGroupControl.set('content', null);

    if (FLOW.selectedControl.selectedSurveyAssignment.get('startDate') > 0) {
      startDate = new Date(FLOW.selectedControl.selectedSurveyAssignment.get('startDate'));
    }
    if (FLOW.selectedControl.selectedSurveyAssignment.get('endDate') > 0) {
      endDate = new Date(FLOW.selectedControl.selectedSurveyAssignment.get('endDate'));
    }
    FLOW.dateControl.set('fromDate', FLOW.formatDate(startDate));
    FLOW.dateControl.set('toDate', FLOW.formatDate(endDate));

    this.set('language', FLOW.selectedControl.selectedSurveyAssignment.get('language'));
  },

  detectChangeTab() {
    if (Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))) {
      FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
    }
    FLOW.selectedControl.set('selectedSurveyAssignment', null);
  },

  assignmentNotComplete() {
    if (Ember.empty(this.get('assignmentName'))) {
      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', Ember.String.loc('_assignment_name_not_set'));
      FLOW.dialogControl.set('message', Ember.String.loc('_assignment_name_not_set_text'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
      return true;
    }
    if (Ember.none(FLOW.dateControl.get('toDate')) || Ember.none(FLOW.dateControl.get('fromDate'))) {
      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', Ember.String.loc('_date_not_set'));
      FLOW.dialogControl.set('message', Ember.String.loc('_date_not_set_text'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
      return true;
    }
    return false;
  },

  saveSurveyAssignment() {
    let endDateParse;
    let startDateParse;
    const devices = [];
    const surveys = [];
    if (this.assignmentNotComplete()) {
      return;
    }
    const sa = FLOW.selectedControl.get('selectedSurveyAssignment');
    sa.set('name', this.get('assignmentName'));

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
    setTimeout(function () {
      FLOW.router.transitionTo('navDevices.assignSurveysOverview');
    }, 500);
  },

  cancelEditSurveyAssignment() {
    if (Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))) {
      FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
    }
    FLOW.selectedControl.set('selectedSurveyAssignment', null);
    FLOW.router.transitionTo('navDevices.assignSurveysOverview');
  },

  validateAssignmentObserver() {
    this.set('assignmentValidationFailure', (
      (this.assignmentName && this.assignmentName.length > 100)
      || !this.assignmentName || this.assignmentName == ''));
    if (this.assignmentName && this.assignmentName.length > 100) {
      this.set('assignmentValidationFailureReason', Ember.String.loc('_assignment_name_over_100_chars'));
    } else if (!this.assignmentName || this.assignmentName == '') {
      this.set('assignmentValidationFailureReason', Ember.String.loc('_assignment_name_not_set'));
    }
  },
});
