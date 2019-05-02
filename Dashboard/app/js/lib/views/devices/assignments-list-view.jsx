import React from 'react';

import AssignmentsList from 'akvo-flow/components/devices/AssignmentsList';
import observe from '../../mixins/observe';

require('akvo-flow/views/react-component');

FLOW.AssignmentsListView = FLOW.ReactComponentView.extend(observe({
  'FLOW.surveyAssignmentControl.content.isLoaded': 'assignmentsList',
  'FLOW.surveyAssignmentControl.content.length': 'assignmentsList',
}), {
  init() {
    this._super();
    this.assignmentsList = this.assignmentsList.bind(this);
    this.assignmentEdit = this.assignmentEdit.bind(this);
  },

  didInsertElement(...args) {
    this._super(...args);
    this.assignmentsList();
  },

  assignmentsList() {
    if (!FLOW.surveyAssignmentControl.content.isLoaded) return;

    const assignments = FLOW.surveyAssignmentControl.get('content');

    this.reactRender(
      <AssignmentsList
        assignments={assignments}
        onEdit={this.assignmentEdit}
      />
    );
  },

  assignmentEdit(action, assignment) {
    this.reactRender(
      <div>
        <div className="deviceControls">
          <a
            className="btnOutline"
            onClick={this.assignmentsList}
            style={{ float: 'left' }}
          >
            back
          </a>
          {/* TODO: button should reload assignments */}
          <label style={{ float: 'left' }}>
            {action == 'edit' ? assignment.get('name') : 'new ass'}
          </label>
          <a className="btnOutline" style={{ float: 'right' }}>
            save
          </a>
        </div>
        <div id="devicesListTable_length" className="dataTables_length" />
        <div style={{ marginTop: '40px' }}>
          <div>duration</div>
          <div>
            {/* should cascade */}
            folder/survey selectors
          </div>
          <div>forms list</div>
        </div>
      </div>
    );
  },
});
