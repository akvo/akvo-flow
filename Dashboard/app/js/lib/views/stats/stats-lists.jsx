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
      this.getProps = this.getProps.bind(this);

      this.stats = [];
    },

    didInsertElement(...args) {
      this._super(...args);
    },

    renderReactSide() {
      const props = this.getProps();
      this.reactRender(<StatsList {...props} />);
    },

    getStatsLists() {
      const { isLoaded, isUpdating } = FLOW.router.reportsController.content;
      if (!isLoaded || isUpdating) {
        return;
      }

      // empty stats and add new result from API
      this.stats = [];

      FLOW.router.reportsController
        .get('content')
        .filter(item => item.get('reportType') === 'STATISTICS')
        .forEach(item => {
          this.stats.push({
            id: item.get('keyId'),
            url: item.get('filename').length ? item.get('filename') : '#',
            name: item.get('filename').length
              ? item.get('filename')
              : Ember.String.loc('_no_file_generated'),
            startDate: FLOW.renderTimeStamp(item.get('startDate')) || 'N/A',
            endDate: FLOW.renderTimeStamp(item.get('endDate')) || 'N/A',
            status: item.get('state'),
          });
        });

      this.renderReactSide();
    },

    newExport() {
      FLOW.router.transitionTo('navStats.newStats');
    },

    getProps() {
      return {
        strings: {
          noStats: Ember.String.loc('_no_stats_generated'),
          clickToExport: Ember.String.loc('_click_new_export'),
          submissions: Ember.String.loc('_submissions'),
          generatedStats: Ember.String.loc('_generated_stats'),
          exportStats: Ember.String.loc('_export_stats'),
        },
        goToExport: this.newExport,
        stats: this.stats,
      };
    },
  }
);
