// I18N
// Ember.String.loc('_request_submitted_email_will_be_sent');
import { ArrNoDupe } from '../../utils';

FLOW.SurveyBootstrap = FLOW.View.extend({
  surveysPreview: Ember.A([]),
  includeDBInstructions: false,
  dbInstructions: '',
  notificationEmail: '',

  init() {
    this._super();
    FLOW.selectedControl.set('selectedSurveys', null);
  },

  selectAllSurveys() {
    const selected = FLOW.surveyControl
      .get('content')
      .filter(item => item.get('status') === 'PUBLISHED');
    FLOW.selectedControl.set('selectedSurveys', selected);
  },

  deselectAllSurveys() {
    FLOW.selectedControl.set('selectedSurveys', []);
  },

  addSelectedSurveys() {
    const sgName = FLOW.selectedControl.selectedSurveyGroup.get('code');

    FLOW.selectedControl.get('selectedSurveys').forEach(item => {
      item.set('surveyGroupName', sgName);
    });

    this.surveysPreview.pushObjects(
      FLOW.selectedControl.get('selectedSurveys')
    );
    // delete duplicates
    this.set('surveysPreview', ArrNoDupe(this.get('surveysPreview')));
  },

  removeSingleSurvey(event) {
    const id = event.context.get('clientId');
    const surveysPreview = this.get('surveysPreview');
    for (let i = 0; i < surveysPreview.length; i++) {
      if (surveysPreview.objectAt(i).clientId == id) {
        surveysPreview.removeAt(i);
      }
    }
    this.set('surveysPreview', surveysPreview);
  },

  removeAllSurveys() {
    this.set('surveysPreview', Ember.A([]));
  },

  sendSurveys() {
    if (
      this.get('surveysPreview').length === 0 &&
      !this.get('includeDBInstructions')
    ) {
      this.showMessage(Ember.String.loc('_survey_or_db_instructions_required'));
      return;
    }

    if (
      this.get('includeDBInstructions') &&
      this.get('dbInstructions') === ''
    ) {
      this.showMessage(Ember.String.loc('_missing_db_instructions'));
      return;
    }

    if (!this.get('notificationEmail')) {
      this.showMessage(Ember.String.loc('_notification_email_required'));
      return;
    }

    const payload = {
      action: 'generateBootstrapFile',
      email: this.get('notificationEmail'),
    };

    const surveyIds = [];

    this.get('surveysPreview').forEach(item => {
      surveyIds.push(item.get('keyId'));
    });

    payload.surveyIds = surveyIds;

    if (this.get('includeDBInstructions')) {
      payload.dbInstructions = this.get('dbInstructions');
    }

    FLOW.store.findQuery(FLOW.Action, payload);

    this.reset();
  },

  showMessage(msg) {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set(
      'header',
      Ember.String.loc('_manual_survey_transfer')
    );
    FLOW.dialogControl.set('message', msg);
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  reset() {
    this.removeAllSurveys();
    this.deselectAllSurveys();
    this.set('dbInstructions', '');
    this.set('includeDBInstructions', false);
    this.set('notificationEmail', '');
  },
});
