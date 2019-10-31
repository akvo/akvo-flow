/* eslint-disable import/no-unresolved */
import React from 'react';
import StatsList from 'akvo-flow/components/stats/StatsLists';
import observe from '../../mixins/observe';

require('akvo-flow/views/react-component');

FLOW.StatsListsView = FLOW.ReactComponentView.extend(
  observe({
    'FLOW.router.reportsController.content.isLoaded': 'getStatsLists',
    'FLOW.router.reportsController.content.isUpdating': 'getStatsLists',
  }),
  {
    init() {
      this._super();
      this.getStatsLists = this.getStatsLists.bind(this);
      this.newExport = this.newExport.bind(this);
    },

    didInsertElement(...args) {
      this._super(...args);
    },

    getStatsLists() {
      const { isLoaded, isUpdating } = FLOW.router.reportsController.content;
      if (!isLoaded || isUpdating) {
        return;
      }

      const stats = [];

      FLOW.router.reportsController
        .get('content')
        .filter(item => item.get('reportType') === 'STATISTICS')
        .forEach(item => {
          stats.push({
            id: item.get('keyId'),
            url: item.get('filename').length ? item.get('filename') : '#',
            name: item.get('filename').length
              ? item.get('filename')
              : 'No file generated',
            startDate: FLOW.renderTimeStamp(item.get('startDate')) || 'N/A',
            endDate: FLOW.renderTimeStamp(item.get('endDate')) || 'N/A',
            status: item.get('state'),
          });
        });

      this.reactRender(<StatsList stats={stats} goToExport={this.newExport} />);
    },

    newExport() {
      FLOW.router.transitionTo('navStats.newStats');
    },
  }
);
