FLOW.ExportReportsAppletView = FLOW.View.extend({
  showRawDataReportApplet:false,
  showComprehensiveReportApplet:false,
  showGoogleEarthFileApplet: false,
  showSurveyFormApplet: false,
  showWarning: false,

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
      this.set('showWarning', true);
      return;
    }
    this.set(prop, true);
  },
  onSurveySelection: function () {
    this.set('showWarning', false);
  }.observes('FLOW.selectedControl.selectedSurvey')
});