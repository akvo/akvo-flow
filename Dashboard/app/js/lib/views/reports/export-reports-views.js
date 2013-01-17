FLOW.ExportReportsAppletView = Em.View.extend({
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
});