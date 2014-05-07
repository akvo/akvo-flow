// removes duplicate objects with a clientId from an Ember Array

FLOW.ArrNoDupe = function (a) {
  var templ, i, item = null,
    gotIt, tempa;
  templ = {};
  tempa = Ember.A([]);
  for (i = 0; i < a.length; i++) {
    templ[a.objectAt(i).clientId] = true;
  }
  for (item in templ) {
    gotIt = false;
    for (i = 0; i < a.length; i++) {
      if (a.objectAt(i).clientId == item && !gotIt) {
        tempa.pushObject(a.objectAt(i));
        gotIt = true;
      }
    }
  }
  return tempa;
};

FLOW.formatDate = function (value) {
  if (!Ember.none(value)) {
    return value.getFullYear() + "/" + (value.getMonth() + 1) + "/" + value.getDate();
  } else return null;
};

FLOW.AssignmentEditView = FLOW.View.extend({
  devicesPreview: Ember.A([]),
  surveysPreview: Ember.A([]),
  assignmentName: null,
  language: null,

  init: function () {
    var deviceIds, previewDevices, surveyIds, previewSurveys, startDate = null,
      endDate = null;
    previewDevices = Ember.A([]);
    previewSurveys = Ember.A([]);
    this._super();
    this.set('assignmentName', FLOW.selectedControl.selectedSurveyAssignment.get('name'));
    FLOW.selectedControl.set('selectedDevices', null);
    FLOW.selectedControl.set('selectedSurveys', null);
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

    deviceIds = Ember.A(FLOW.selectedControl.selectedSurveyAssignment.get('devices'));

    deviceIds.forEach(function (item) {
      previewDevices.pushObjects(FLOW.store.find(FLOW.Device, item));
    });
    this.set('devicesPreview', previewDevices);

    surveyIds = Ember.A(FLOW.selectedControl.selectedSurveyAssignment.get('surveys'));

    surveyIds.forEach(function (item) {
      if (item !== null) {
        previewSurveys.pushObjects(FLOW.store.find(FLOW.Survey, item));
      }
    });
    this.set('surveysPreview', previewSurveys);
  },

  detectChangeTab: function () {
    if (Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))) {
      FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
    }
    FLOW.selectedControl.set('selectedSurveyAssignment', null);
  }.observes('FLOW.router.navigationController.selected', 'FLOW.router.devicesSubnavController.selected'),

  assignmentNotComplete: function () {
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

  saveSurveyAssignment: function () {
    var sa, endDateParse, startDateParse, devices = [],
      surveys = [];
    if (this.assignmentNotComplete()){
		return;
	}
    sa = FLOW.selectedControl.get('selectedSurveyAssignment');
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

    this.get('devicesPreview').forEach(function (item) {
      devices.push(item.get('keyId'));
    });
    sa.set('devices', devices);

    this.get('surveysPreview').forEach(function (item) {
      surveys.push(item.get('keyId'));
    });
    sa.set('surveys', surveys);

    FLOW.store.commit();
    FLOW.router.transitionTo('navDevices.assignSurveysOverview');
  },

  cancelEditSurveyAssignment: function () {
    if (Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))) {
      FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
    }
    FLOW.selectedControl.set('selectedSurveyAssignment', null);
    FLOW.router.transitionTo('navDevices.assignSurveysOverview');
  },

  addSelectedDevices: function () {
    this.devicesPreview.pushObjects(FLOW.selectedControl.get('selectedDevices'));
    // delete duplicates
    this.set('devicesPreview', FLOW.ArrNoDupe(this.get('devicesPreview')));
  },

  addSelectedSurveys: function () {
    var sgName;
    sgName = FLOW.selectedControl.selectedSurveyGroup.get('code');
    FLOW.selectedControl.get('selectedSurveys').forEach(function (item) {
      item.set('surveyGroupName', sgName);
    });
    this.surveysPreview.pushObjects(FLOW.selectedControl.get('selectedSurveys'));
    // delete duplicates
    this.set('surveysPreview', FLOW.ArrNoDupe(this.get('surveysPreview')));
  },

  selectAllDevices: function () {
    var selected = Ember.A([]);
    FLOW.devicesInGroupControl.get('content').forEach(function (item) {
      selected.pushObject(item);
    });
    FLOW.selectedControl.set('selectedDevices', selected);
  },

  deselectAllDevices: function () {
    FLOW.selectedControl.set('selectedDevices', []);
  },

  selectAllSurveys: function () {
	var selected = FLOW.surveyControl.get('content').filter(function (item) {
	    return item.get('status') === "PUBLISHED";
	});
    FLOW.selectedControl.set('selectedSurveys', selected);
  },

  deselectAllSurveys: function () {
    FLOW.selectedControl.set('selectedSurveys', []);
  },

  removeSingleSurvey: function (event) {
    var id, surveysPreview, i;
    id = event.context.get('clientId');
    surveysPreview = this.get('surveysPreview');
    for (i = 0; i < surveysPreview.length; i++) {
      if (surveysPreview.objectAt(i).clientId == id) {
        surveysPreview.removeAt(i);
      }
    }
    this.set('surveysPreview', surveysPreview);
  },

  removeAllSurveys: function () {
    this.set('surveysPreview', Ember.A([]));
  },

  removeSingleDevice: function (event) {
    var id, devicesPreview, i;
    id = event.context.get('clientId');
    devicesPreview = this.get('devicesPreview');
    for (i = 0; i < devicesPreview.length; i++) {
      if (devicesPreview.objectAt(i).clientId == id) {
        devicesPreview.removeAt(i);
      }
    }
    this.set('devicesPreview', devicesPreview);
  },

  removeAllDevices: function () {
    this.set('devicesPreview', Ember.A([]));
  }
});
