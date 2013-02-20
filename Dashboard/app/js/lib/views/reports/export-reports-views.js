FLOW.ExportReportsAppletView = FLOW.View.extend({
  showRawDataReportApplet:false,
  showComprehensiveReportApplet:false,
  showGoogleEarthFileApplet: false,
  showSurveyFormApplet: false,
  showComprehensiveDialog: false,
  showRawDataImportApplet: false,
  showGoogleEarthButton: false,

  showRawDataReport: function () {
    this.renderApplet('showRawDataReportApplet');
  },

  showComprehensiveReport: function () {
    this.set('showComprehensiveDialog', false);
    this.renderApplet('showComprehensiveReportApplet');
  },

  showGoogleEarthFile: function () {
    this.renderApplet('showGoogleEarthFileApplet', true);
  },

  showSurveyForm: function () {
    this.renderApplet('showSurveyFormApplet');
  },

  showImportApplet: function () {
    this.renderApplet('showRawDataImportApplet', true);
  },

  showComprehensiveOptions: function () {
    if(!FLOW.selectedControl.selectedSurvey) {
      this.showWarning();
      return;
    }

    FLOW.editControl.set('summaryPerGeoArea', true);
    FLOW.editControl.set('omitCharts', false);
    this.set('showComprehensiveDialog', true);
  },

  showWarning: function () {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_export_data'));
    FLOW.dialogControl.set('message', Ember.String.loc('_applet_select_survey'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  renderApplet: function (prop, skipSurveyCheck) {
    if(!skipSurveyCheck && !FLOW.selectedControl.selectedSurvey) {
      this.showWarning();
      return;
    }
    switch (prop) {
      case 'showRawDataReportApplet':
        this.set('showRawDataReportApplet', true);
        this.set('showComprehensiveReportApplet', false);
        this.set('showGoogleEarthFileApplet', false);
        this.set('showSurveyFormApplet', false);
        this.set('showRawDataImportApplet', false);
        break;
      case 'showComprehensiveReportApplet':
        this.set('showRawDataReportApplet', false);
        this.set('showComprehensiveReportApplet', true);
        this.set('showGoogleEarthFileApplet', false);
        this.set('showSurveyFormApplet', false);
        this.set('showRawDataImportApplet', false);
        break;
      case 'showGoogleEarthFileApplet':
        this.set('showRawDataReportApplet', false);
        this.set('showComprehensiveReportApplet', false);
        this.set('showGoogleEarthFileApplet', true);
        this.set('showSurveyFormApplet', false);
        this.set('showRawDataImportApplet', false);
        break;
      case 'showSurveyFormApplet':
        this.set('showRawDataReportApplet', false);
        this.set('showComprehensiveReportApplet', false);
        this.set('showGoogleEarthFileApplet', false);
        this.set('showSurveyFormApplet', true);
        this.set('showRawDataImportApplet', false);
        break;
      case 'showRawDataImportApplet':
        this.set('showRawDataReportApplet', false);
        this.set('showComprehensiveReportApplet', false);
        this.set('showGoogleEarthFileApplet', false);
        this.set('showSurveyFormApplet', true);
        this.set('showRawDataImportApplet', true);
        break;
    }

    this.get('childViews').forEach(function(v) {
      if(v.get('childViews') && v.get('childViews').length > 0) {
        return; // skip initial select items
      }

      if(v.state === 'inDOM') {
        v.rerender();
      }
    });
  }
});