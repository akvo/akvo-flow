import React from 'react';

import AssignmentsList from 'akvo-flow/components/devices/AssignmentsList';
import EditAssignment from 'akvo-flow/components/devices/EditAssignment';
import observe from '../../mixins/observe';

require('akvo-flow/views/react-component');

FLOW.AssignmentsListView = FLOW.ReactComponentView.extend(observe({
  'FLOW.surveyAssignmentControl.content.isLoaded': 'assignmentsList',
  'FLOW.surveyAssignmentControl.content.length': 'assignmentsList',
  'FLOW.dialogControl.delAssignmentConfirm' : 'assignmentDelete'
}), {
  init() {
    this._super();
    this.assignmentsList = this.assignmentsList.bind(this);
    this.assignmentEdit = this.assignmentEdit.bind(this);
    this.assignmentDeleteConfirm = this.assignmentDeleteConfirm.bind(this);
  },

  didInsertElement(...args) {
    this._super(...args);
    const self = this;
    const interval = setInterval(() => {
      if (FLOW.surveyAssignmentControl.content.isLoaded) {
        self.assignmentsList();
        clearInterval(interval);
      }
    }, 500);
  },

  assignmentsList() {
    if (!FLOW.surveyAssignmentControl.content.isLoaded) return;

    this.reactRender(
      <AssignmentsList
        assignments={FLOW.surveyAssignmentControl.get('content')}
        onEdit={this.assignmentEdit}
        onDelete={this.assignmentDeleteConfirm}
      />
    );
  },

  assignmentEdit (action, assignment) {
    FLOW.selectedControl.set('selectedSurveyAssignment', assignment);
    FLOW.router.transitionTo('navDevices.editSurveysAssignment');
    // this.reactRender(
    //   <EditAssignment
    //     action={action}
    //     assignment={assignment}
    //     backToAssignmentsList={this.assignmentsList.bind(this)}
    //   />
    // );
  },

  assignmentDeleteConfirm (assignment) {
    FLOW.dialogControl.confirm({context: FLOW.dialogControl.delAssignment, assignmentId: assignment.get('keyId')});
  },

  assignmentDelete () {
    if (FLOW.dialogControl.delAssignmentConfirm) {
      var assignment = FLOW.store.find(FLOW.SurveyAssignment, FLOW.dialogControl.get('delAssignmentId'));
      if (assignment) {
        console.log(assignment);
        // assignment.deleteRecord();
        // FLOW.store.commit();
      }
    }
  }
});
