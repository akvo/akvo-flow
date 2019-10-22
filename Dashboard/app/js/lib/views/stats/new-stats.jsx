/* eslint-disable import/no-unresolved */
import React from 'react';
import NewStats from 'akvo-flow/components/stats/new-stats';

require('akvo-flow/views/react-component');

FLOW.NewStatsView = FLOW.ReactComponentView.extend({
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
  },
});
