/* eslint-disable import/no-unresolved */
import React from 'react';
import NewStats from 'akvo-flow/components/stats/NewStats';

require('akvo-flow/views/react-component');

FLOW.NewStatsReactView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
    this.generateReport = this.generateReport.bind(this);
  },

  didInsertElement(...args) {
    this._super(...args);
    this.reactRender(<NewStats generateReport={this.generateReport} />);
  },

  generateReport(dates) {
    // create new 'export report'
    FLOW.selectedControl.set(
      'selectedReportExport',
      FLOW.store.createRecord(FLOW.Report, {})
    );

    const newReport = FLOW.selectedControl.get('selectedReportExport');
    newReport.set('reportType', 'STATISTICS');
    newReport.set('formId', 0);
    newReport.set('startDate', dates.startDate);
    newReport.set('endDate', dates.endDate);
    newReport.set('filename', '');
    newReport.set('state', 'QUEUED');

    FLOW.store.commit();

    this.showDialogMessage(
      Ember.String.loc('_your_report_is_being_prepared'),
      Ember.String.loc('_we_will_notify_via_email')
    );

    FLOW.router.transitionTo('navStats.index');
  },

  showDialogMessage(header, message) {
    FLOW.savingMessageControl.numLoadingChange(-1);
    FLOW.dialogControl.set('header', header);
    FLOW.dialogControl.set('message', message);
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },
});
