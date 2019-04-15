import React from 'react';
import dayjs from 'dayjs';

require('akvo-flow/views/react-component');

FLOW.AssignmentsListView = FLOW.ReactComponentView.extend({
  didInsertElement(...args) {
    this._super(...args);
    this.reactRender(
      <div style={{ visibility: 'hidden' }}>
        React
        {' '}
        {dayjs().seconds()}
      </div>
    );
  },
});
