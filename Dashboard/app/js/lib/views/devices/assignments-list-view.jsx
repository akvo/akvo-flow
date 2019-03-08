import React from 'react';
import moment from 'moment';
require('akvo-flow/views/react-component');

FLOW.AssignmentsListView = FLOW.ReactComponentView.extend({
  didInsertElement(...args) {
    this._super(...args);
    this.reactRender(
      <div style={{ visibility: 'hidden' }}>React {moment().seconds()}</div>
    );
  }
});
