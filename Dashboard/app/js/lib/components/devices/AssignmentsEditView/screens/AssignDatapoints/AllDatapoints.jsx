import React from 'react';
import PropTypes from 'prop-types';
import AssignmentContext from '../../assignment-context';

export default class AllDatapoints extends React.Component {
  render() {
    const { actions, strings, data } = this.context;
    const { deviceId } = this.props;
    const label = strings.allDatapointsAssigned.replace(/{}/, data.datapointsCount.get('count'));

    return (
      <div className="all-dp-assigned">
        <div className="dp-info">
          <p>{label}</p>
        </div>

        <div className="unassign-dp">
          <button
            className="link-button"
            onClick={() => actions.unassignAllDatapointsToDevice(deviceId)}
            type="button"
          >
            {strings.undo}
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
