/* eslint-disable import/no-unresolved */
import React from 'react';
import Stats from 'akvo-flow/components/stats';

require('akvo-flow/views/react-component');

FLOW.StatsView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
  },

  didInsertElement(...args) {
    this._super(...args);
    this.reactRender(<Stats />);
  },
});
