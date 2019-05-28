import React from 'react';
import PropTypes from 'prop-types';
/* eslint-disable no-nested-ternary */
const AssignmentsList = ({
  assignments,
  strings,
  sortProperties,
  onEdit,
  onDelete,
  onSort,
}) => (
  <div>
    <div className="deviceControls">
      <a
        className="btnOutline newAssignment"
        onClick={() => onEdit('new')}
        onKeyDown={() => onEdit('new')}
      >
        {strings.newAssignment}
      </a>
    </div>
    <div
      className="dataTables_length"
    />
    {!assignments.length && (
      <div
        className="noAssignments"
      >
        {strings.noAssignments}
      </div>
    )}
    {assignments.length > 0 && (
      <table className="dataTable">
        {/* TABLE HEADER */}
        <thead>
          <tr>
            <th
              className={sortProperties.column == 'name' ? (sortProperties.ascending ? 'sorting_asc' : 'sorting_desc') : ''}
            >
              <a
                onClick={() => onSort('name')}
                onKeyDown={() => onSort('name')}
              >
                {strings.name}
              </a>
            </th>
            <th
              className={sortProperties.column == 'startDate' ? (sortProperties.ascending ? 'sorting_asc' : 'sorting_desc') : ''}
            >
              <a
                onClick={() => onSort('startDate')}
                onKeyDown={() => onSort('startDate')}
              >
                {strings.startDate}
              </a>
            </th>
            <th
              className={sortProperties.column == 'endDate' ? (sortProperties.ascending ? 'sorting_asc' : 'sorting_desc') : ''}
            >
              <a
                onClick={() => onSort('endDate')}
                onKeyDown={() => onSort('endDate')}
              >
                {strings.endDate}
              </a>
            </th>
            <th className="noArrows cursorStyle">
              <a>
                {strings.action}
              </a>
            </th>
          </tr>
        </thead>
        {/* TABLE BODY: MAIN CONTENT */}
        <tbody>
          {assignments.map(assignment => (
            <tr key={assignment.keyId}>
              <td className="name">{assignment.name}</td>
              <td>{assignment.startDate}</td>
              <td>{assignment.endDate}</td>
              <td className="action">
                <a
                  onClick={() => onEdit('edit', assignment.keyId)}
                  onKeyDown={() => onEdit('edit', assignment.keyId)}
                >
                  {strings.edit}
                </a>
                <span>
                  <a
                    className="remove"
                    onClick={() => onDelete(assignment.keyId)}
                    onKeyDown={() => onDelete(assignment.keyId)}
                  >
                    {strings.delete}
                  </a>
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    )}
  </div> // move styling to css
);

AssignmentsList.propTypes = {
  assignments: PropTypes.array.isRequired,
  strings: PropTypes.object.isRequired,
  sortProperties: PropTypes.object.isRequired,
  onEdit: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onSort: PropTypes.func.isRequired,
};

export default AssignmentsList;
