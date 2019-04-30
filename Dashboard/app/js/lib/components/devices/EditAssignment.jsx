import React from 'react';
import PropTypes from 'prop-types';

const EditAssignment = ({ action, assignment, backToAssignmentsList }) => (
  <div>
    <div className="deviceControls">
      <a
        className="btnOutline"
        onClick={() => backToAssignmentsList()}
        style={{float: 'left'}}
      >
        back
      </a>{/* TODO: button should reload assignments*/}
      <label style={{float: 'left'}}>
        {action == "edit" ? assignment.get('name') : "new ass"}
      </label>
      <a
        className="btnOutline"
        style={{float: 'right'}}
      >
        save
      </a>
    </div>
    <div
      id="devicesListTable_length"
      className="dataTables_length"
    >
    </div>
    <div style={{marginTop: '40px'}}>
      <div>
        duration
      </div>
      <div>{/*should cascade*/}
        folder/survey selectors
      </div>
      <div>
        forms list
      </div>
    </div>
  </div>
);

EditAssignment.propTypes = {
  assignment: PropTypes.object,
  action: PropTypes.string.isRequired,
  backToAssignmentsList: PropTypes.func.isRequired,
};
export default EditAssignment;
