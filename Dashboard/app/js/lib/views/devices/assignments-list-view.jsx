import React from 'react';

import AssignmentsList from 'akvo-flow/components/devices/AssignmentsList';
import EditAssignment from 'akvo-flow/components/devices/EditAssignment';
import observe from '../../mixins/observe';

require('akvo-flow/views/react-component');

FLOW.AssignmentsListView = FLOW.ReactComponentView.extend(observe({
  'FLOW.surveyAssignmentControl.content': 'assignmentsList',
}), {
  didInsertElement(...args) {
    this._super(...args);
    setTimeout(() => {
      this.assignmentsList();
    }, 1000);
  },

  assignmentsList () {
    if (!FLOW.surveyAssignmentControl.content.isLoaded) return;

    this.reactRender(
      <AssignmentsList
        assignments={FLOW.surveyAssignmentControl.get('content')}
        onEdit={this.editAssignment.bind(this)}
      />
    );
  },

  editAssignment (action, assignment) {
    this.reactRender(
      <EditAssignment
        action={action}
        assignment={assignment}
        backToAssignmentsList={this.assignmentsList.bind(this)}
      />
    );
  }
});
