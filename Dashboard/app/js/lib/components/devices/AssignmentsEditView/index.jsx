import React from 'react';
import './styles.scss';

export default class AssignmentsEdit extends React.Component {
  render() {
    return (
      <div className="assignments-edit">
        {/* topbar */}
        <div className="assignment-topbar">
          <div className="assignment-name">
            <i className="fa fa-arrow-left" />
            <h3>Unnamed assignment</h3>
          </div>

          <button type="button">Save</button>
        </div>
        {/* <p>Assignments Edit Page</p> */}
      </div>
    );
  }
}
