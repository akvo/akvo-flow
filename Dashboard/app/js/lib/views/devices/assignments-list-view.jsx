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
        self.assignmentsList();
        clearInterval(render);
      }
    }, 500);
  },

  assignmentsList () {
    const assignments = FLOW.surveyAssignmentControl.get('content');
    this.reactRender(
      <div><div className="deviceControls">
        <a className="btnOutline" onClick={() => this.editAssignment("new")}>Create new assignment</a>
      </div>
      <div id="devicesListTable_length" className="dataTables_length"></div>
      <table className="dataTable"><tbody>{assignments.map(assignment => (
        <tr key={assignment.get('keyId')}>
          <td className="name">{assignment.get('name')}</td>
          <td className="action"><a style={{cursor: 'pointer'}} onClick={() => this.editAssignment("edit", assignment)}>Edit</a></td>
        </tr>
      ))}</tbody></table></div> //move styling to css
      //<div style={{ visibility: 'hidden' }}>React {moment().seconds()}</div>
    );
  },

  editAssignment (action, assignment) {
    var self = this;
    this.reactRender(
      <div><div className="deviceControls">
        <a className="btnOutline" onClick={() => self.assignmentsList()}>back</a>
      </div>
      <div id="devicesListTable_length" className="dataTables_length"></div>
      <div>{action == "edit" ? assignment.get('name') : "new ass"}</div></div>
    );
    //alert(action+" "+assignment.get("name"));
  }
});
