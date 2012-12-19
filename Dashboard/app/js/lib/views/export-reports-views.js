FLOW.exportreportsView = Em.View.extend({
  showRawDataReportApplet:false,
  showComprehensiveReportApplet:false,
  showGoogleEarthFileApplet: false,

  showRawDataReport: function () {
    this.set('showRawDataReportApplet', true);
  },

  showComprehensiveReport: function () {
    this.set('showComprehensiveReportApplet', true);
  },

  showGoogleEarthFile: function () {
    this.set('showGoogleEarthFileApplet', true);
  }

})