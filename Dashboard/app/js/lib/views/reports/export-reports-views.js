FLOW.ExportReportsAppletView = FLOW.View.extend({
  showRawDataReportApplet:false,
  showComprehensiveReportApplet:false,
  showGoogleEarthFileApplet: false,
  showSurveyFormApplet: false,

  showRawDataReport: function () {
    this.set('showRawDataReportApplet', true);
  },

  showComprehensiveReport: function () {
    this.set('showComprehensiveReportApplet', true);
  },

  showGoogleEarthFile: function () {
    this.set('showGoogleEarthFileApplet', true);
  },

  showSurveyForm: function () {
    this.set('showSurveyFormApplet', true);
  }
});