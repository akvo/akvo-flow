/* eslint-disable import/no-unresolved */
import React from 'react';

require('akvo-flow/views/react-component');

FLOW.StatsListsView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
  },

  didInsertElement(...args) {
    this._super(...args);
    this.reactRender(<p>Stats lsting page</p>);
  },
});
