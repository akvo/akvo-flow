import React from 'react';
import dayjs from 'dayjs';

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
        <a className="btnOutline" onClick={() => this.editAssignment("new")} style={{float: 'right'}}>Create new assignment</a>
      </div>
      <div id="devicesListTable_length" className="dataTables_length"></div>
      {!assignments.get('length') && (<div style={{marginTop: '40px'}}>No assignments</div>)}
      {assignments.get('length') > 0 && (
        <table className="dataTable"><tbody>{assignments.map(assignment => (
          <tr key={assignment.get('keyId')}>
            <td className="name">{assignment.get('name')}</td>
            <td className="action"><a style={{cursor: 'pointer'}} onClick={() => this.editAssignment("edit", assignment)}>Edit</a></td>
          </tr>
        ))}</tbody></table>
      )}
      </div> //move styling to css
      //<div style={{ visibility: 'hidden' }}>React {moment().seconds()}</div>
    );
  },

  editAssignment (action, assignment) {
    var self = this;
    this.reactRender(
      <div>
        <div className="deviceControls">
          <a className="btnOutline" onClick={() => self.assignmentsList()} style={{float: 'left'}}>back</a>{/* TODO: button should reload assignments*/}
          <label style={{float: 'left'}}>{action == "edit" ? assignment.get('name') : "new ass"}</label>
          <a className="btnOutline" style={{float: 'right'}}>save</a>
        </div>
        <div id="devicesListTable_length" className="dataTables_length"></div>
        <div style={{marginTop: '40px'}}>
          <div>duration</div>
          <div>{/*should cascade*/}
            folder/survey selectors
          </div>
          <div>forms list</div>
        </div>
      </div>
    );
    //alert(action+" "+assignment.get("name"));
  }
});
