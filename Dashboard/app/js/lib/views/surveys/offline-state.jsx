/* eslint-disable import/no-unresolved */
import React from 'react';
import OfflineIndicator from 'akvo-flow/components/reusable/OfflineIndicator';

require('akvo-flow/views/react-component');

FLOW.OfflineIndicatorView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
  },

  didInsertElement(...args) {
    this._super(...args);
    this.reactRender(<OfflineIndicator />);
  },
});
