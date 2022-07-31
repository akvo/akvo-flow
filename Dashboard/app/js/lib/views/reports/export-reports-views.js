/* eslint-disable import/no-unresolved */
import { trackEvent, DATA_EXPORTS } from 'akvo-flow/analytics';
import observe from '../../mixins/observe';
import template from '../../mixins/template';
/* global Ember, $, FLOW */

FLOW.ReportLoader = Ember.Object.create({
  selectedSurveyId: Ember.computed(() => {
    if (
      !Ember.none(FLOW.selectedControl.get('selectedSurvey')) &&
      !Ember.none(FLOW.selectedControl.selectedSurvey.get('keyId'))
    ) {
      return FLOW.selectedControl.selectedSurvey.get('keyId');
    }
    return null;
  }).property('FLOW.selectedControl.selectedSurvey'),

  load(exportType, surveyId, opts) {
    FLOW.selectedControl.set('selectedReportExport', FLOW.store.createRecord(FLOW.Report, {}));
    const newReport = FLOW.selectedControl.get('selectedReportExport');

    Ember.assert('exportType param is required', exportType !== undefined);
    Ember.assert('surveyId param is required', surveyId !== undefined);

    if (opts) {
      Ember.keys(opts).forEach(k => {
        newReport.set(k, opts[k]);
      });
    }

    newReport.set('reportType', exportType);
    newReport.set('formId', surveyId);
    newReport.set('filename', '');
    newReport.set('state', 'QUEUED');

    FLOW.store.commit();
    this.showDialogMessage(
      Ember.String.loc('_your_report_is_being_prepared'),
      Ember.String.loc('_we_will_notify_via_email'),
      'reports'
    );

    trackEvent(DATA_EXPORTS, `Export Type`, exportType);
  },

  showDialogMessage(header, message, action) {
    FLOW.savingMessageControl.numLoadingChange(-1);
    FLOW.dialogControl.set('activeAction', action);
    FLOW.dialogControl.set('header', header);
    FLOW.dialogControl.set('message', message);
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },
});

FLOW.ExportReportsView = Ember.View.extend(template('navReports/export-reports'), {
  missingSurvey: false,
  updateSurveyStatus(surveyStatus) {
    this.set('missingSurvey', surveyStatus !== 'survey-selected');
    Ember.$('body, html ,#navExportSelect').scrollTop(0);
  },
});

FLOW.ExportReportTypeView = Ember.View.extend(
  observe({
    'this.exportOption': 'dateRangeDisabledObserver',
    'this.reportFromDate': 'setMinDate',
    'this.reportToDate': 'setMaxDate',
    'FLOW.selectedControl.selectedSurvey': 'watchSurveySelection',
  }),
  {
    showComprehensiveDialog: false,
    reportFromDate: undefined,
    reportToDate: undefined,
    dateRangeDisabled: false,
    rangeActive: '',
    recentActive: 'background-color: transparent;',
    exportOption: 'range',
    dateRangeText: Ember.String.loc('_collection_period'),
    onlyRecentText: Ember.String.loc('_only_recent_submissions'),
    tagName: 'li',
    classNames: 'trigger',
    missingQuestion: false,

    dateRangeDisabledObserver() {
      this.set(
        'rangeActive',
        this.get('exportOption') === 'range' ? '' : 'background-color: transparent;'
      );
      this.set(
        'recentActive',
        this.get('exportOption') === 'recent' ? '' : 'background-color: transparent;'
      );
      this.set('dateRangeDisabled', this.get('exportOption') === 'recent');
    },

    setMinDate() {
      if (this.get('reportFromDate')) {
        this.$('.to_date').datepicker('option', 'minDate', this.get('reportFromDate'));
      }
    },

    setMaxDate() {
      if (this.get('reportToDate')) {
        this.$('.from_date').datepicker('option', 'maxDate', this.get('reportToDate'));
      }
    },

    didInsertElement() {
      FLOW.selectedControl.set('surveySelection', FLOW.SurveySelection.create());
      FLOW.selectedControl.set('selectedSurvey', null);
      FLOW.editControl.set('useQuestionId', false);
      FLOW.uploader.registerEvents();
    },

    selectedQuestion: Ember.computed(() => {
      if (
        !Ember.none(FLOW.selectedControl.get('selectedQuestion')) &&
        !Ember.none(FLOW.selectedControl.selectedQuestion.get('keyId'))
      ) {
        return FLOW.selectedControl.selectedQuestion.get('keyId');
      }
      return null;
    }).property('FLOW.selectedControl.selectedQuestion'),

    watchSurveySelection() {
      if (FLOW.selectedControl.get('selectedSurvey') !== null) {
        this.get('parentView').updateSurveyStatus('survey-selected');
      }
    },

    hideLastCollection: Ember.computed(function() {
      if (!FLOW.selectedControl.selectedSurvey) {
        return true;
      }
      if (FLOW.selectedControl.selectedSurveyGroup && FLOW.selectedControl.selectedSurvey) {
        // if not a monitoring form, export should be filtered by date
        if (
          FLOW.selectedControl.selectedSurvey.get('keyId') ==
          FLOW.selectedControl.selectedSurveyGroup.get('newLocaleSurveyId')
        ) {
          $('input:radio[name=cleaning-export-option]')
            .filter('[value=range]')
            .prop('checked', true);
          $('input:radio[name=analysis-export-option]')
            .filter('[value=range]')
            .prop('checked', true);
          this.set('rangeActive', '');
          this.set('recentActive', 'background-color: transparent; opacity: 0.5');
        } else {
          this.set('recentActive', 'background-color: transparent;');
        }
      }
      return !(
        FLOW.selectedControl.selectedSurveyGroup &&
        FLOW.selectedControl.selectedSurveyGroup.get('monitoringGroup') &&
        FLOW.selectedControl.selectedSurvey.get('keyId') !=
          FLOW.selectedControl.selectedSurveyGroup.get('newLocaleSurveyId')
      );
    }).property('FLOW.selectedControl.selectedSurvey'),

    showDataCleaningReport() {
      const opts = {
        startDate: this.get('reportFromDate'),
        endDate: this.get('reportToDate'),
        lastCollectionOnly: this.get('exportOption') === 'recent',
      };
      const sId = FLOW.ReportLoader.get('selectedSurveyId');
      if (!sId) {
        this.get('parentView').updateSurveyStatus('not-selected');
        return;
      }
      FLOW.ReportLoader.load('DATA_CLEANING', sId, opts);
    },

    showDataAnalysisReport() {
      const opts = {
        startDate: this.get('reportFromDate'),
        endDate: this.get('reportToDate'),
        lastCollectionOnly: this.get('exportOption') === 'recent',
      };
      const sId = FLOW.ReportLoader.get('selectedSurveyId');
      if (!sId) {
        this.get('parentView').updateSurveyStatus('not-selected');
        return;
      }
      FLOW.ReportLoader.load('DATA_ANALYSIS', sId, opts);
    },

    showComprehensiveReport() {
      const opts = {};
      const sId = FLOW.ReportLoader.get('selectedSurveyId');
      if (!sId) {
        this.get('parentView').updateSurveyStatus('not-selected');
        return;
      }
      FLOW.ReportLoader.load('COMPREHENSIVE', sId, opts);
    },

    showGeoshapeReport() {
      const sId = FLOW.ReportLoader.get('selectedSurveyId');
      const qId = this.get('selectedQuestion');
      if (!sId) {
        this.get('parentView').updateSurveyStatus('not-selected');
        return;
      }
      if (!qId) {
        this.set('missingQuestion', true);
        return;
      }
      FLOW.ReportLoader.load('GEOSHAPE', sId, { questionId: qId });
    },

    showSurveyForm() {
      const sId = FLOW.ReportLoader.get('selectedSurveyId');
      if (!sId) {
        this.get('parentView').updateSurveyStatus('not-selected');
        return;
      }
      FLOW.ReportLoader.load('SURVEY_FORM', sId);
    },

    showComprehensiveOptions() {
      const sId = FLOW.ReportLoader.get('selectedSurveyId');
      if (!sId) {
        this.get('parentView').updateSurveyStatus('not-selected');
        return;
      }

      FLOW.editControl.set('summaryPerGeoArea', true);
      FLOW.editControl.set('omitCharts', false);
      this.set('showComprehensiveDialog', true);
    },

    showWarning() {
      FLOW.ReportLoader.showDialogMessage(
        Ember.String.loc('_export_data'),
        Ember.String.loc('_applet_select_survey'),
        'ignore'
      );
    },

    eventManager: Ember.Object.create({
      click(event, clickedView) {
        const exportTypes = [
          'dataCleanExp',
          'dataAnalyseExp',
          'compReportExp',
          'geoShapeDataExp',
          'surveyFormExp',
        ];
        if (exportTypes.indexOf(clickedView.get('export')) > -1) {
          const options = document.getElementsByClassName('options');
          for (let i = 0; i < options.length; i++) {
            options[i].style.display = 'none';
          }
          const trigger = document.getElementsByClassName('trigger');
          for (let i = 0; i < trigger.length; i++) {
            trigger[i].className = trigger[i].className.replace(' active', '');
          }
          document.getElementById(clickedView.get('export')).style.display = 'block';
          event.currentTarget.className += ' active';

          // by default select the range option
          if (clickedView.get('export') == 'dataCleanExp') {
            if ($('input:radio[name=cleaning-export-option]').is(':checked') === false) {
              $('input:radio[name=cleaning-export-option]')
                .filter('[value=range]')
                .prop('checked', true);
            }
          } else if (clickedView.get('export') == 'dataAnalyseExp') {
            if ($('input:radio[name=analysis-export-option]').is(':checked') === false) {
              $('input:radio[name=analysis-export-option]')
                .filter('[value=range]')
                .prop('checked', true);
            }
          }
        }
      },
    }),
  }
);

FLOW.ReportsListView = Ember.View.extend(template('navReports/reports-list'), {
  didInsertElement() {
    FLOW.router.reportsController.populate();
  },

  exportNewReport() {
    FLOW.router.transitionTo('navData.exportReports');
  },
});

FLOW.ReportListItemView = FLOW.View.extend(template('navReports/report'), {
  reportType: Ember.computed(function() {
    const reportTypeClasses = {
      DATA_CLEANING: 'dataCleanExp',
      DATA_ANALYSIS: 'dataAnalyseExp',
      COMPREHENSIVE: 'compReportExp',
      GEOSHAPE: 'geoShapeDataExp',
      SURVEY_FORM: 'surveyFormExp',
    };
    return reportTypeClasses[this.content.get('reportType')];
  }).property('content'),

  reportStatus: Ember.computed(function() {
    const reportStates = {
      IN_PROGRESS: 'exportGenerating',
      QUEUED: 'exportGenerating',
      FINISHED_SUCCESS: '',
      FINISHED_ERROR: '',
    };
    return reportStates[this.content.get('state')];
  }).property('content'),

  reportTypeString: Ember.computed(function() {
    const reportTypeStrings = {
      DATA_CLEANING: Ember.String.loc('_data_cleaning_export'),
      DATA_ANALYSIS: Ember.String.loc('_data_analysis_export'),
      COMPREHENSIVE: Ember.String.loc('_comprehensive_report'),
      GEOSHAPE: Ember.String.loc('_geoshape_data'),
      SURVEY_FORM: Ember.String.loc('_survey_form'),
    };
    return reportTypeStrings[this.content.get('reportType')];
  }).property('content'),

  reportFilename: Ember.computed(function() {
    const url = this.content.get('filename');
    return FLOW.reportFilename(url);
  }).property('content'),

  reportLink: Ember.computed(function() {
    const url = this.content.get('filename');
    return !url ? '#' : url;
  }).property('content'),

  reportSuccess: Ember.computed(function() {
    return this.content.get('state') === 'FINISHED_SUCCESS';
  }).property('content'),

  surveyPath: Ember.computed(function() {
    const formId = this.content.get('formId');
    let path = '';
    const form = FLOW.Survey.find(formId);
    if (form) {
      const ancestorIds = form.get('ancestorIds');
      if (ancestorIds) {
        for (let i = 0; i < ancestorIds.length; i++) {
          if (ancestorIds[i] !== null && ancestorIds[i] !== 0) {
            try {
              const ancestor = FLOW.SurveyGroup.find(ancestorIds[i]);
              if (ancestor && ancestor.get('name')) {
                path += (i > 1 ? ' > ' : '') + ancestor.get('name');
              }
            } catch (e) {
              path += Ember.String.loc('_invalid_path');
            }
          }
        }
        path += ` > ${form.get('name')}`;
      }
    }
    return path;
  }).property('content'),

  startDate: Ember.computed(function() {
    return FLOW.renderTimeStamp(this.content.get('startDate'));
  }).property('content'),

  endDate: Ember.computed(function() {
    return FLOW.renderTimeStamp(this.content.get('endDate'));
  }).property('content'),

  lastUpdateDateTime: Ember.computed(function() {
    return FLOW.renderDate(this.content.get('lastUpdateDateTime'));
  }).property('content'),

  isNotStats: Ember.computed(function() {
    return this.content.get('reportType') !== 'STATISTICS';
  }).property('content'),
});

FLOW.DataCleaningView = Ember.View.extend(
  template('navData/data-cleaning'),
  observe({
    'FLOW.selectedControl.selectedSurvey': 'watchSurveySelection',
  }),
  {
    missingSurvey: false,

    didInsertElement() {
      FLOW.uploader.registerEvents();
    },

    importFile() {
      const survey = FLOW.selectedControl.get('selectedSurvey');

      if (survey === null) {
        this.set('missingSurvey', true);
        return;
      }

      const file = $('#raw-data-import-file')[0];

      if (!file || file.files.length === 0) {
        FLOW.ReportLoader.showDialogMessage(
          Ember.String.loc('_import_clean_data'),
          Ember.String.loc('_import_select_file'),
          'ignore'
        );
        return;
      }

      FLOW.uploader.addFile(file.files[0]);
      FLOW.uploader.upload();
    },
    watchSurveySelection() {
      // remove the highlight around the dropdown if survey is selected
      if (FLOW.selectedControl.get('selectedSurvey') !== null) {
        this.set('missingSurvey', false);
      }
    },
  }
);
