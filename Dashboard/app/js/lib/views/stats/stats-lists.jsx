/* eslint-disable import/no-unresolved */
import React from 'react';
import StatsList from 'akvo-flow/components/stats/stats-lists';

require('akvo-flow/views/react-component');

FLOW.StatsListsView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
  },

  didInsertElement(...args) {
    this._super(...args);
    this.reactRender(<StatsList />);
  },
});
