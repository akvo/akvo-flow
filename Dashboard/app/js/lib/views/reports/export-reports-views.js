/*global Ember, $, FLOW */

FLOW.ReportLoader = Ember.Object.create({
  load: function (exportType, surveyId, opts) {
    FLOW.selectedControl.set('selectedReportExport', FLOW.store.createRecord(FLOW.Report, {}));
    var newReport = FLOW.selectedControl.get('selectedReportExport');

    Ember.assert('exportType param is required', exportType !== undefined);
    Ember.assert('surveyId param is required', surveyId !== undefined);

    if (opts) {
      Ember.keys(opts).forEach(function (k) {
        newReport.set(k, opts[k])
      });
    }

    newReport.set('reportType', exportType);
    newReport.set('formId', surveyId);
    newReport.set('filename', '');
    newReport.set('state', 'QUEUED');

    FLOW.store.commit();
    this.showDialogMessage(Ember.String.loc('_your_report_is_being_prepared'), Ember.String.loc('_we_will_notify_via_email'), 'reports');
  },

  showDialogMessage: function(header, message, action) {
    FLOW.savingMessageControl.numLoadingChange(-1);
    FLOW.dialogControl.set('activeAction', action);
    FLOW.dialogControl.set('header', header);
    FLOW.dialogControl.set('message', message);
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  }
});

FLOW.ExportReportsView = Ember.View.extend({
  templateName: 'navReports/export-reports'
});

FLOW.ExportReportTypeView = Ember.View.extend({
  showRawDataReportApplet: false,
  showComprehensiveReportApplet: false,
  showGoogleEarthFileApplet: false,
  showSurveyFormApplet: false,
  showComprehensiveDialog: false,
  showRawDataImportApplet: false,
  showGoogleEarthButton: false,
  reportFromDate: undefined,
  reportToDate: undefined,
  dateRangeDisabled: false,
  rangeActive: "",
  recentActive: "background-color: transparent;",
  exportOption: "range",
  dateRangeText: Ember.String.loc('_collection_period'),
  onlyRecentText: Ember.String.loc('_only_recent_submissions'),
  tagName: 'li',
  classNames: 'trigger',

  dateRangeDisabledObserver: function () {
    this.set('rangeActive', this.get("exportOption") === "range" ? "" : "background-color: transparent;");
    this.set('recentActive', this.get("exportOption") === "recent" ? "" : "background-color: transparent;");
    this.set('dateRangeDisabled', this.get("exportOption") === "recent");
  }.observes('this.exportOption'),

  setMinDate: function () {
    if (this.get('reportFromDate')) {
      this.$(".to_date").datepicker("option", "minDate", this.get("reportFromDate"))
    }
  }.observes('this.reportFromDate'),

  setMaxDate: function () {
    if (this.get('reportToDate')) {
     this.$(".from_date").datepicker("option", "maxDate", this.get("reportToDate"))
    }
  }.observes('this.reportToDate'),

  didInsertElement: function () {
    FLOW.selectedControl.set('surveySelection', FLOW.SurveySelection.create());
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.editControl.set('useQuestionId', false);
    FLOW.uploader.registerEvents();
  },

  selectedSurvey: function () {
    if (!Ember.none(FLOW.selectedControl.get('selectedSurvey')) && !Ember.none(FLOW.selectedControl.selectedSurvey.get('keyId'))){
      return FLOW.selectedControl.selectedSurvey.get('keyId');
    } else {
      return null;
    }
  }.property('FLOW.selectedControl.selectedSurvey'),

  selectedQuestion: function () {
    if (!Ember.none(FLOW.selectedControl.get('selectedQuestion'))
        && !Ember.none(FLOW.selectedControl.selectedQuestion.get('keyId'))){
      return FLOW.selectedControl.selectedQuestion.get('keyId');
    } else {
      return null;
    }
  }.property('FLOW.selectedControl.selectedQuestion'),

  hideLastCollection: function () {
    if (!FLOW.selectedControl.selectedSurvey) {
      return true;
    }
    if (FLOW.selectedControl.selectedSurveyGroup && FLOW.selectedControl.selectedSurvey) {
      //if not a monitoring form, export should be filtered by date
      if (FLOW.selectedControl.selectedSurvey.get('keyId') == FLOW.selectedControl.selectedSurveyGroup.get('newLocaleSurveyId')) {
        $('input:radio[name=cleaning-export-option]').filter('[value=range]').prop('checked', true);
        $('input:radio[name=analysis-export-option]').filter('[value=range]').prop('checked', true);
        this.set('rangeActive', "");
        this.set('recentActive', "background-color: transparent; opacity: 0.5");
      } else {
        this.set('recentActive', "background-color: transparent;");
      }
    }
    return !(FLOW.selectedControl.selectedSurveyGroup && FLOW.selectedControl.selectedSurveyGroup.get('monitoringGroup')
      && FLOW.selectedControl.selectedSurvey.get('keyId') != FLOW.selectedControl.selectedSurveyGroup.get('newLocaleSurveyId'));
  }.property('FLOW.selectedControl.selectedSurvey'),

  showDataCleaningReport: function () {
    var opts = {startDate:this.get("reportFromDate"), endDate:this.get("reportToDate"), lastCollectionOnly: this.get('exportOption') === "recent"};
    var sId = this.get('selectedSurvey');
    if (!sId) {
      this.showWarning();
      return;
    }
    FLOW.ReportLoader.load('DATA_CLEANING', sId, opts);
  },

  showDataAnalysisReport: function () {
    var opts = {startDate:this.get("reportFromDate"), endDate:this.get("reportToDate"), lastCollectionOnly: this.get('exportOption') === "recent"};
    var sId = this.get('selectedSurvey');
    if (!sId) {
      this.showWarning();
      return;
    }
    FLOW.ReportLoader.load('DATA_ANALYSIS', sId, opts);
  },

  showComprehensiveReport: function () {
    var opts = {}, sId = this.get('selectedSurvey');
    if (!sId) {
      this.showWarning();
      return;
    }
    FLOW.ReportLoader.load('COMPREHENSIVE', sId, opts);
  },

  showGeoshapeReport: function () {
    var sId = this.get('selectedSurvey');
    var qId = this.get('selectedQuestion');
    if (!sId || !qId) {
      FLOW.ReportLoader.showDialogMessage(
        Ember.String.loc('_export_data'),
        Ember.String.loc('_select_survey_and_geoshape_question_warning'),
        'ignore'
      );
      return;
    }
    FLOW.ReportLoader.load('GEOSHAPE', sId, {"questionId": qId});
  },

  showSurveyForm: function () {
    var sId = this.get('selectedSurvey');
    if (!sId) {
      this.showWarning();
      return;
    }
    FLOW.ReportLoader.load('SURVEY_FORM', sId);
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
    FLOW.ReportLoader.showDialogMessage(Ember.String.loc('_export_data'), Ember.String.loc('_applet_select_survey'), 'ignore');
  },

  eventManager: Ember.Object.create({
    click: function(event, clickedView){
      var exportTypes = ["dataCleanExp", "dataAnalyseExp", "compReportExp", "geoShapeDataExp", "surveyFormExp"];
      if (exportTypes.indexOf(clickedView.get('export')) > -1) {
        var i, options, trigger;
        options = document.getElementsByClassName("options");
        for (i = 0; i < options.length; i++) {
          options[i].style.display = "none";
        }
        trigger = document.getElementsByClassName("trigger");
        for (i = 0; i < trigger.length; i++) {
          trigger[i].className = trigger[i].className.replace(" active", "");
        }
        document.getElementById(clickedView.get('export')).style.display = "block";
        event.currentTarget.className += " active";

        //by default select the range option
        if (clickedView.get('export') == "dataCleanExp") {
          if ($('input:radio[name=cleaning-export-option]').is(':checked') === false) {
            $('input:radio[name=cleaning-export-option]').filter('[value=range]').prop('checked', true);
          }
        } else if (clickedView.get('export') == "dataAnalyseExp") {
          if ($('input:radio[name=analysis-export-option]').is(':checked') === false) {
            $('input:radio[name=analysis-export-option]').filter('[value=range]').prop('checked', true);
          }
        }
      }
    }
  })
});

FLOW.ReportsListView = Ember.View.extend({
  templateName: 'navReports/reports-list',

  didInsertElement: function () {
    FLOW.router.reportsController.populate();
  },

  exportNewReport: function () {
    FLOW.router.transitionTo('navData.exportReports');
  }
});

FLOW.ReportListItemView = FLOW.View.extend({
  templateName: 'navReports/report',

  reportType: function(){
    var reportTypeClasses = {
      DATA_CLEANING: "dataCleanExp",
      DATA_ANALYSIS: "dataAnalyseExp",
      COMPREHENSIVE: "compReportExp",
      GEOSHAPE: "geoShapeDataExp",
      SURVEY_FORM: "surveyFormExp"
    };
    return reportTypeClasses[this.content.get('reportType')];
  }.property(this.content),

  reportStatus: function(){
    var reportStates = {
      IN_PROGRESS: "exportGenerating",
      QUEUED: "exportGenerating",
      FINISHED_SUCCESS: "",
      FINISHED_ERROR: ""
    };
    return reportStates[this.content.get('state')];
  }.property(this.content),

  reportTypeString: function(){
    var reportTypeStrings = {
      DATA_CLEANING: Ember.String.loc('_data_cleaning_export'),
      DATA_ANALYSIS: Ember.String.loc('_data_analysis_export'),
      COMPREHENSIVE: Ember.String.loc('_comprehensive_report'),
      GEOSHAPE: Ember.String.loc('_geoshape_data'),
      SURVEY_FORM: Ember.String.loc('_survey_form')
    };
    return reportTypeStrings[this.content.get('reportType')];
  }.property(this.content),

  reportFilename: function(){
    var url = this.content.get('filename');
    return FLOW.reportFilename(url);
  }.property(this.content),

  reportLink: function(){
    var url = this.content.get('filename');
    return !url ? "#" : url;
  }.property(this.content),

  surveyPath: function(){
    var formId = this.content.get('formId'), path = "";
    var form  = FLOW.Survey.find(formId);
    if (form) {
      var ancestorIds = form.get('ancestorIds');
      if (ancestorIds) {
        for (var i = 0; i < ancestorIds.length; i++) {
          if (ancestorIds[i] !== null && ancestorIds[i] !== 0) {
            var ancestor = FLOW.SurveyGroup.find(ancestorIds[i]);
            if (ancestor) {
              path += (i > 1 ? " > ": "") + ancestor.get('name');
            }
          }
        }
        path += " > " + form.get('name');
      }
    }
    return path;
  }.property(this.content),

  startDate: function(){
    return FLOW.renderTimeStamp(this.content.get('startDate'));
  }.property(this.content),

  endDate: function(){
    return FLOW.renderTimeStamp(this.content.get('endDate'));
  }.property(this.content),

  lastUpdateDateTime: function(){
    return FLOW.renderDate(this.content.get('lastUpdateDateTime'));
  }.property(this.content)
});

FLOW.DataCleaningView = Ember.View.extend({
  templateName: 'navData/data-cleaning',

  importFile: function () {
    var file, survey = FLOW.selectedControl.get('selectedSurvey');
    if (!survey.get('keyId')) {
      FLOW.ReportLoader.showDialogMessage(Ember.String.loc('_import_clean_data'), Ember.String.loc('_import_select_survey'), 'ignore');
      return;
    }

    file = $('#raw-data-import-file')[0];

    if (!file || file.files.length === 0) {
      FLOW.ReportLoader.showDialogMessage(Ember.String.loc('_import_clean_data'), Ember.String.loc('_import_select_file'), 'ignore');
      return;
    }

    FLOW.uploader.addFile(file.files[0]);
    FLOW.uploader.upload();
  }
});
