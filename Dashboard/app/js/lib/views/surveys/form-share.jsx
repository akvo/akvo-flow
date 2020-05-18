/* eslint-disable import/no-unresolved */
import React from 'react';
import WebFormShare from 'akvo-flow/components/forms/form-share';

require('akvo-flow/views/react-component');

FLOW.WebFormShareView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
  },

  didInsertElement(...args) {
    this._super(...args);

    // const props = this.getProps();
    this.reactRender(<WebFormShare />);
  },
});
