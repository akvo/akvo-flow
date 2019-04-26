import React from 'react';
import PropTypes from 'prop-types';

const AssignmentsList = ({ assignments, onEdit }) => (
  <div>
    <div className="deviceControls">
      <a
        className="btnOutline"
        onClick={() => onEdit('new')}
        style={{ float: 'right' }}
      >
        Create new assignment
      </a>
    </div>
    <div
      id="devicesListTable_length"
      className="dataTables_length"
    />
    {!assignments.get('length') && (
      <div style={{ marginTop: '40px' }}>No assignments</div>
    )}
    {assignments.get('length') > 0 && (
      <table className="dataTable">
        <tbody>
          {assignments.map(assignment => (
            <tr key={assignment.get('keyId')}>
              <td className="name">{assignment.get('name')}</td>
              <td className="action">
                <a
                  style={{ cursor: 'pointer' }}
                  onClick={() => onEdit('edit', assignment)}
                >
                  Edit
                </a>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    )}
  </div> // move styling to css
);

AssignmentsList.propTypes = {
  assignments: PropTypes.object.isRequired,
  onEdit: PropTypes.func.isRequired,
};

export default AssignmentsList;
