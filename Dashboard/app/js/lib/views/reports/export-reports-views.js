FLOW.ExportReportsAppletView = FLOW.View.extend({
  showRawDataReportApplet:false,
  showComprehensiveReportApplet:false,
  showGoogleEarthFileApplet: false,
  showSurveyFormApplet: false,

  showRawDataReport: function () {
    this.renderApplet('showRawDataReportApplet');
  },

  showComprehensiveReport: function () {
    this.renderApplet('showComprehensiveReportApplet');
  },

  showGoogleEarthFile: function () {
    this.renderApplet('showGoogleEarthFileApplet');
  },

  showSurveyForm: function () {
    this.renderApplet('showSurveyFormApplet');
  },
  renderApplet: function (prop) {
    if(!FLOW.selectedControl.selectedSurvey) {
      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', Ember.String.loc('_export_data'));
      FLOW.dialogControl.set('message', Ember.String.loc('_applet_select_survey'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
      return;
    }
    this.set(prop, true);
  }
});