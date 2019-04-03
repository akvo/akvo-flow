import React from 'react';
import moment from 'moment';
require('akvo-flow/views/react-component');

FLOW.AssignmentsListView = FLOW.ReactComponentView.extend({
  didInsertElement(...args) {
    const self = this;
    this._super(...args);
    //tmp solution because observe not working
    let render = setInterval(() => {
      if (FLOW.surveyAssignmentControl.content.isLoaded) {
        self.renderView();
        clearInterval(render);
      }
    }, 500);
  },

  renderView () {
    let assignments = FLOW.surveyAssignmentControl.get('content');
    this.reactRender(
      <table className="dataTable"><tbody>{assignments.map(assignment => (
        <tr key={assignment.get('keyId')}>
          <td className="name">{assignment.get('name')}</td>
        </tr>
      ))}</tbody></table> //move styling to css
      //<div style={{ visibility: 'hidden' }}>React {moment().seconds()}</div>
    );
  }
});
