FLOW.SurveyBootstrap = FLOW.View.extend({
  surveysPreview: Ember.A([]),
  includeDBInstructions: false,
  dbInstructions: '',
  init: function () {
    this._super();
    FLOW.selectedControl.set('selectedSurveys', null);
  },

  selectAllSurveys: function () {
    var selected = Ember.A([]);
    FLOW.surveyControl.get('content').forEach(function(item) {
      selected.pushObject(item);
    });
    FLOW.selectedControl.set('selectedSurveys', selected);
  },

  deselectAllSurveys: function () {
    FLOW.selectedControl.set('selectedSurveys', []);
  },

  addSelectedSurveys: function() {
    var sgName = FLOW.selectedControl.selectedSurveyGroup.get('code');

    FLOW.selectedControl.get('selectedSurveys').forEach(function(item) {
      item.set('surveyGroupName', sgName);
    });

    this.surveysPreview.pushObjects(FLOW.selectedControl.get('selectedSurveys'));
    // delete duplicates
    this.set('surveysPreview', FLOW.ArrNoDupe(this.get('surveysPreview')));
  },

  removeSingleSurvey: function(event) {
    var id, surveysPreview, i;
    id = event.context.get('clientId');
    surveysPreview = this.get('surveysPreview');
    for(i = 0; i < surveysPreview.length; i++) {
      if(surveysPreview.objectAt(i).clientId == id) {
        surveysPreview.removeAt(i);
      }
    }
    this.set('surveysPreview', surveysPreview);
  },

  removeAllSurveys: function() {
    this.set('surveysPreview', Ember.A([]));
  },

  sendSurveys: function () {
    var surveyIds = [], payload = {action: 'generateBootstrapFile'};

    if(!this.get('notificationEmail')) {
      return;
    }

    if (this.get('surveysPreview').length === 0) {
      return;
    }

    this.get('surveysPreview').forEach(function (item) {
      surveyIds.push(item.get('keyId'));
    });

    payload.surveyIds = surveyIds;

    if (this.get('includeDBInstructions')) {
      payload.dbInstructions = this.get('dbInstructions');
    }

    FLOW.store.findQuery(FLOW.Action, payload);

    this.reset();
  },

  reset: function () {
    this.deselectAllSurveys();
    this.removeAllSurveys();
    this.set('dbInstructions', '');
    this.set('includeDBInstructions', false);
  }
});