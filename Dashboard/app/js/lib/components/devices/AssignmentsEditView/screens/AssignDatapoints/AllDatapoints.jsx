import React from 'react';
import AssignmentContext from '../../assignment-context';

export default class AllDatapoints extends React.Component {
  render() {
    const { actions, strings } = this.context;

    return (
      <div className="all-dp-assigned">
        <div className="dp-info">
          <p>All 20k datapoints assigned</p>
          <p className="info">{strings.unassignNote}</p>
        </div>

        <div className="unassign-dp">
          <button onClick={actions.unassignAllDatapointsToDevice} type="button">
            {strings.unassign}
          </button>
        </div>
      </div>
    );
  }
}

AllDatapoints.contextType = AssignmentContext;
