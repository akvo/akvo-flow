/*global Ember, $, FLOW */

FLOW.ReportLoader = Ember.Object.create({
  criteria: null,
  timeout: 6000,
  requestInterval: 3000,

  payloads: {
    RAW_DATA: {
      surveyId: '75201',
      exportType: 'RAW_DATA',
      opts: {
        locale: 'en',
        exportMode: 'RAW_DATA',
        generateTabFormat: 'false'
      }
    },
    RAW_DATA_TEXT: {
        surveyId: '75201',
        exportType: 'RAW_DATA_TEXT',
        opts: {}
    },
    GRAPHICAL_SURVEY_SUMMARY: {
      surveyId: '75201',
      exportType: 'GRAPHICAL_SURVEY_SUMMARY',
      opts: {
        locale: 'en',
        performRollup: 'false',
        nocharts: 'true'
      }
    },
    SURVEY_FORM: {
      surveyId: '75201',
      exportType: 'SURVEY_FORM',
      opts: {}
    }
  },

  load: function (exportType, surveyId, opts) {
    var criteria;

    if (this.get('criteria')) {
      return;
    }

    Ember.assert('exportType param is required', exportType !== undefined);
    Ember.assert('surveyId param is required', surveyId !== undefined);

    criteria = Ember.copy(this.get('payloads')[exportType]);
    criteria.surveyId = '' + surveyId;
    criteria.baseURL = location.protocol + '//' + location.host;

    criteria.opts.imgPrefix = FLOW.Env.photo_url_root;
    criteria.opts.uploadUrl = FLOW.Env.surveyuploadurl;

    if (opts) {
      Ember.keys(opts).forEach(function (k) {
        criteria.opts[k] = opts[k];
      });
    }

    if (criteria.opts.locale && FLOW.reportLanguageControl.get('selectedLanguage')) {
      criteria.opts.locale = FLOW.reportLanguageControl.get('selectedLanguage').get('value');
    }

    this.set('criteria', criteria);
    FLOW.savingMessageControl.numLoadingChange(1);
    this.requestReport();
  },

  handleResponse: function (resp) {
    if (!resp || resp.status !== 'OK') {
      FLOW.savingMessageControl.numLoadingChange(-1);
      this.showError();
      return;
    }
    if (resp.message === 'PROCESSING') {
      this.set('processing', false);
      Ember.run.later(this, this.requestReport, this.requestInterval);
    } else if (resp.file) {
      FLOW.savingMessageControl.numLoadingChange(-1);
      this.set('processing', false);
      this.set('criteria', null);
      $('#downloader').attr('src', FLOW.Env.flowServices + '/report/' + resp.file);
    }
  },

  requestReport: function () {
    this.set('processing', true);
    $.ajax({
      url: FLOW.Env.flowServices + '/generate',
      data: {
        criteria: JSON.stringify(this.get('criteria'))
      },
      jsonpCallback: 'FLOW.ReportLoader.handleResponse',
      dataType: 'jsonp'
    });

    Ember.run.later(this, this.handleError, this.timeout);
  },

  handleError: function () {
    if (this.get('processing')) {
      FLOW.savingMessageControl.numLoadingChange(-1);
      this.showError();
    }
  },

  showError: function () {
	  FLOW.savingMessageControl.numLoadingChange(-1);
    this.set('processing', false);
    this.set('criteria', null);
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_error_generating_report'));
    FLOW.dialogControl.set('message', Ember.String.loc('_error_generating_report_try_later'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  }
});

FLOW.ExportReportsAppletView = FLOW.View.extend({
  showRawDataReportApplet: false,
  showComprehensiveReportApplet: false,
  showGoogleEarthFileApplet: false,
  showSurveyFormApplet: false,
  showComprehensiveDialog: false,
  showRawDataImportApplet: false,
  showGoogleEarthButton: false,

  didInsertElement: function () {
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.uploader.registerEvents();
  },

  selectedSurvey: function () {
    if (!Ember.none(FLOW.selectedControl.get('selectedSurvey')) && !Ember.none(FLOW.selectedControl.selectedSurvey.get('keyId'))){
      return FLOW.selectedControl.selectedSurvey.get('keyId');
    } else {
      return null;
    }
  }.property('FLOW.selectedControl.selectedSurvey'),

  showRawDataReport: function () {
	var sId = this.get('selectedSurvey');
    if (!sId) {
      this.showWarning();
      return;
    }
    FLOW.ReportLoader.load('RAW_DATA', sId);
  },
  
  showRawTextFileExport: function () {
	var sId = this.get('selectedSurvey');
    if (!sId) {
	  this.showWarning();
    return;
	}
	FLOW.ReportLoader.load('RAW_DATA_TEXT', sId);
  },

  showComprehensiveReport: function () {
    var opts = {}, sId = this.get('selectedSurvey');
    this.set('showComprehensiveDialog', false);

    opts.performRollup = '' + FLOW.editControl.summaryPerGeoArea;
    opts.nocharts = '' + FLOW.editControl.omitCharts;

    FLOW.ReportLoader.load('GRAPHICAL_SURVEY_SUMMARY', sId, opts);
  },

  showSurveyForm: function () {
	var sId = this.get('selectedSurvey');
    if (!sId) {
      this.showWarning();
      return;
    }
    FLOW.ReportLoader.load('SURVEY_FORM', sId);
  },

  importFile: function () {
    var file, sId = this.get('selectedSurvey');
    if (!sId) {
      this.showImportWarning(Ember.String.loc('_import_select_survey'));
      return;
    }

    file = $('#raw-data-import-file')[0];

    if (!file || file.files.length === 0) {
      this.showImportWarning(Ember.String.loc('_import_select_file'));
      return;
    }

    FLOW.uploader.addFile(file.files[0]);
    FLOW.uploader.upload();
  },

  showComprehensiveOptions: function () {
	var sId = this.get('selectedSurvey');
    if (!sId) {
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

  showImportWarning: function (msg) {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_import_clean_data'));
    FLOW.dialogControl.set('message', msg);
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  }
});
