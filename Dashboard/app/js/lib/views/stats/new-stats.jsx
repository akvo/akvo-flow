/* eslint-disable import/no-unresolved */
import React from 'react';
import NewStats from 'akvo-flow/components/stats/NewStats';
import { trackEvent, STATS } from 'akvo-flow/analytics';

require('akvo-flow/views/react-component');

FLOW.NewStatsReactView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
    this.generateReport = this.generateReport.bind(this);
    this.getProps = this.getProps.bind(this);
  },

  didInsertElement(...args) {
    this._super(...args);

    const props = this.getProps();
    this.reactRender(<NewStats {...props} />);
  },

  getProps() {
    return {
      generateReport: this.generateReport,
      strings: {
        generateStats: Ember.String.loc('_generate_form_submission_stats'),
        formTimeFrame: Ember.String.loc('_form_submission_time_frame'),
        startDate: Ember.String.loc('_start_date'),
        toDate: Ember.String.loc('_to_date'),
        downloadStats: Ember.String.loc('_download_stats'),
      },
    };
  },

  generateReport(dates) {
    // create new 'export report'
    FLOW.selectedControl.set('selectedReportExport', FLOW.store.createRecord(FLOW.Report, {}));

    const newReport = FLOW.selectedControl.get('selectedReportExport');
    newReport.set('reportType', 'STATISTICS');
    newReport.set('formId', 0);
    newReport.set('startDate', dates.startDate);
    newReport.set('endDate', dates.endDate);
    newReport.set('filename', '');
    newReport.set('state', 'QUEUED');

    trackEvent(STATS, 'Exported Stats in flow');
    FLOW.store.commit();

    this.showDialogMessage(
      Ember.String.loc('_your_report_is_being_prepared'),
      Ember.String.loc('_we_will_notify_via_email')
    );

    setTimeout(() => {
      FLOW.router.transitionTo('navStats.index');
    }, 1000);
  },

  showDialogMessage(header, message) {
    FLOW.savingMessageControl.numLoadingChange(-1);
    FLOW.dialogControl.set('header', header);
    FLOW.dialogControl.set('message', message);
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },
});
