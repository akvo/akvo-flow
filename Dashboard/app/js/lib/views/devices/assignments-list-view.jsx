import React from 'react';

import AssignmentsList from 'akvo-flow/components/devices/AssignmentsList';
// import EditAssignment from 'akvo-flow/components/devices/EditAssignment';
import observe from '../../mixins/observe';

require('akvo-flow/views/react-component');

FLOW.AssignmentsListView = FLOW.ReactComponentView.extend(observe({
  'FLOW.surveyAssignmentControl.content.isLoaded': 'assignmentsList',
  'FLOW.surveyAssignmentControl.content.length': 'assignmentsList',
  'FLOW.dialogControl.delAssignmentConfirm': 'assignmentDelete',
}), {
  init() {
    this._super();
    this.assignmentsList = this.assignmentsList.bind(this);
    this.assignmentEdit = this.assignmentEdit.bind(this);
    this.assignmentDeleteConfirm = this.assignmentDeleteConfirm.bind(this);
    this.assignmentSort = this.assignmentSort.bind(this);
    this.sortAscending = true;
    this.selectedColumn = null;
  },

  didInsertElement(...args) {
    this._super(...args);
    const interval = setInterval(() => {
      if (FLOW.surveyAssignmentControl.content.isLoaded) {
        this.assignmentsList();
        clearInterval(interval);
      }
    }, 500);
  },

  assignmentsList() {
    if (!FLOW.surveyAssignmentControl.content.isLoaded) return;

    let assignments = this.sortedAssignments();
    const strings = {
      delete: Ember.String.loc('_delete'),
      edit: Ember.String.loc('_edit'),
      name: Ember.String.loc('_name'),
      newAssignment: Ember.String.loc('_create_new_assignment'),
      noAssignments: Ember.String.loc('_no_assignments'),
      startDate: Ember.String.loc('_start_date'),
      endDate: Ember.String.loc('_end_date'),
      action: Ember.String.loc('_action'),
    };

    this.reactRender(
      <AssignmentsList
        assignments={assignments}
        strings={strings}
        sortProperties={{
          column: this.selectedColumn,
          ascending: this.sortAscending,
        }}
        onEdit={this.assignmentEdit}
        onDelete={this.assignmentDeleteConfirm}
        onSort={this.assignmentSort}
      />
    );
  },

  assignmentEdit(action, assignmentId) {
    switch (action) {
      case 'new': {
        const newAssignment = FLOW.store.createRecord(FLOW.SurveyAssignment, {});
        FLOW.selectedControl.set('selectedSurveyAssignment', newAssignment);
        break;
      }
      default: {
        let selectedSurveyAssignment = FLOW.store.find(FLOW.SurveyAssignment, assignmentId);
        if (selectedSurveyAssignment) {
          FLOW.selectedControl.set('selectedSurveyAssignment', selectedSurveyAssignment);
        }
      }
    }
    FLOW.router.transitionTo('navDevices.editSurveysAssignment');
  },

  assignmentDeleteConfirm(assignmentId) {
    FLOW.dialogControl.confirm({
      context: FLOW.dialogControl.delAssignment,
      assignmentId: assignmentId,
    });
  },

  assignmentDelete() {
    if (FLOW.dialogControl.delAssignmentConfirm) {
      let assignment = FLOW.store.find(FLOW.SurveyAssignment, FLOW.dialogControl.get('delAssignmentId'));
      if (assignment) {
        assignment.deleteRecord();
        FLOW.store.commit();
      }
    }
  },

  assignmentSort(item) {
    this.sortAscending = !this.sortAscending;
    this.selectedColumn = item;
    this.assignmentsList();
  },

  sortedAssignments() {
    let assignments = Ember.A();
    if (!this.selectedColumn) {
      this.selectedColumn = 'name';
    }
    FLOW.surveyAssignmentControl.get('content').forEach((item) => {
      assignments.push({
        keyId: item.get('keyId'),
        name: item.get('name'),
        startDate: FLOW.date3(item.get('startDate')),
        endDate: FLOW.date3(item.get('endDate')),
      });
    });
    return assignments.sort((a, b) => {
      if (this.sortAscending) {
        if (a[this.selectedColumn] < b[this.selectedColumn]) {
          return -1;
        }
        if (a[this.selectedColumn] > b[this.selectedColumn]) {
          return 1;
        }
      } else {
        if (b[this.selectedColumn] < a[this.selectedColumn]) {
          return -1;
        }
        if (b[this.selectedColumn] > a[this.selectedColumn]) {
          return 1;
        }
      }
      return 0;
    });
  },
});
