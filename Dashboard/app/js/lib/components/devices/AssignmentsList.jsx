import React from 'react';
import PropTypes from 'prop-types';

const AssignmentsList = ({ assignments, onEdit, onDelete, onSort }) => (
  <div>
    <div className="deviceControls">
      <a
        className="btnOutline"
        onClick={() => onEdit('new')}
        style={{ float: 'right' }}
      >
        {Ember.String.loc('_create_new_assignment')}
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
        {/*TABLE HEADER*/}
        <thead>
          <tr>
            <th
              className={FLOW.tableColumnControl.get('selected') == 'name' ? (FLOW.tableColumnControl.get('sortAscending') ? 'sorting_asc' : 'sorting_desc') : ''}
            >
              <a
                onClick={() => onSort('name')}
              >
                {Ember.String.loc('_name')}
              </a>
            </th>
            <th
              className={FLOW.tableColumnControl.get('selected') == 'startDate' ? (FLOW.tableColumnControl.get('sortAscending') ? 'sorting_asc' : 'sorting_desc') : ''}
            >
              <a
                onClick={() => onSort('startDate')}
              >
                {Ember.String.loc('_start_date')}
              </a>
            </th>
            <th
              className={FLOW.tableColumnControl.get('selected') == 'endDate' ? (FLOW.tableColumnControl.get('sortAscending') ? 'sorting_asc' : 'sorting_desc') : ''}
            >
              <a
                onClick={() => onSort('endDate')}
              >
                {Ember.String.loc('_end_date')}
              </a>
            </th>
            <th className="noArrows">
              <a>
                {Ember.String.loc('_action')}
              </a>
            </th>
          </tr>
        </thead>
        {/*TABLE BODY: MAIN CONTENT*/}
        <tbody>
          {assignments.map(assignment => (
            <tr key={assignment.get('keyId')}>
              <td className="name">{assignment.get('name')}</td>
              <td>{FLOW.date3(assignment.get('startDate'))}</td>
              <td>{FLOW.date3(assignment.get('endDate'))}</td>
              <td className="action">
                <a
                  style={{ cursor: 'pointer' }}
                  onClick={() => onEdit('edit', assignment)}
                >
                  {Ember.String.loc('_edit')}
                </a>
                <span>
                  <a
                    className="remove"
                    onClick={() => onDelete(assignment)}
                  >
                    {Ember.String.loc('_delete')}
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
  onEdit: PropTypes.func.isRequired,
};

export default AssignmentsList;
