import React from 'react';
import PropTypes from 'prop-types';
import AssignmentContext from '../../assignment-context';

export default class AllDatapoints extends React.Component {
  render() {
    const { actions, strings } = this.context;
    const { deviceId } = this.props;

    return (
      <div className="all-dp-assigned">
        <div className="dp-info">
          <p>{strings.allDatapointsAssigned}</p>
          <p className="info">{strings.unassignNote}</p>
        </div>

        <div className="unassign-dp">
          <button onClick={() => actions.unassignAllDatapointsToDevice(deviceId)} type="button">
            {strings.unassign}
          </button>
        </div>
      </div>
    );
  }
}

AllDatapoints.contextType = AssignmentContext;
AllDatapoints.propTypes = {
  deviceId: PropTypes.number.isRequired,
};
