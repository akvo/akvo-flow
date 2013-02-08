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
    switch (prop) {
      case 'showRawDataReportApplet':
        this.set('showRawDataReportApplet', true);
        this.set('showComprehensiveReportApplet', false);
        this.set('showGoogleEarthFileApplet', false);
        this.set('showSurveyFormApplet', false);
        break;
      case 'showComprehensiveReportApplet':
        this.set('showRawDataReportApplet', false);
        this.set('showComprehensiveReportApplet', true);
        this.set('showGoogleEarthFileApplet', false);
        this.set('showSurveyFormApplet', false);
        break;
      case 'showGoogleEarthFileApplet':
        this.set('showRawDataReportApplet', false);
        this.set('showComprehensiveReportApplet', false);
        this.set('showGoogleEarthFileApplet', true);
        this.set('showSurveyFormApplet', false);
        break;
      case 'showSurveyFormApplet':
        this.set('showRawDataReportApplet', false);
        this.set('showComprehensiveReportApplet', false);
        this.set('showGoogleEarthFileApplet', false);
        this.set('showSurveyFormApplet', true);
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