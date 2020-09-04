/* eslint-disable import/no-unresolved */
/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import PropTypes from 'prop-types';
import { Dropdown, DropdownItem } from 'akvo-flow/components/reusable/Dropdown';
import AllDatapoints from './AllDatapoints';
import DatapointList from './DatapointList';
import SearchDatapoints from './SearchDatapoints';
import AssignmentContext from '../../assignment-context';

export default class AssignDatapoints extends React.Component {
  state = {
    currentSubTab: '',
  };

  changeTab = tab => {
    this.setState({
      currentSubTab: tab,
    });
  };

  getAssignmentData = () => {
    const { selectedDeviceId } = this.props;
    const { devices, datapointAssignments } = this.context.data;

    const deviceData = devices.find(device => device.id === selectedDeviceId);
    const datapointAssignment = datapointAssignments.find(
      dp => dp.deviceId === parseInt(selectedDeviceId, 10)
    );

    let datapointsData = [];
    let allDPAssigned = false;
    let hasMore = false;

    if (datapointAssignment) {
      datapointsData = datapointAssignment.datapoints;
      allDPAssigned = datapointAssignment.allDataPointsAssigned;
      hasMore = datapointAssignment.hasMoreRawIds;
    }

    return {
      deviceData,
      datapointsData,
      allDPAssigned,
      hasMore,
    };
  };

  renderHeader = (deviceData, datapointsData, allDPAssigned) => {
    const { strings, actions } = this.context;
    const datapointsCount = datapointsData.length;
    const allAssignedLabel = strings.allDatapointsAssigned.replace(/{}/, '');

    return (
      <div className="header">
        <div className="device-details">
          <p>{deviceData.name}</p>
          <p>
            <span>
              {allDPAssigned ? allAssignedLabel : `${datapointsCount} ${strings.datapointAssigned}`}
            </span>
            {!allDPAssigned && (
              <>
                <span className="divider">.</span>
                <a
                  className={datapointsCount ? undefined : 'disabled'}
                  href="#"
                  onClick={
                    datapointsCount
                      ? () => this.props.changeTab('EDIT_DATAPOINTS', deviceData.id)
                      : undefined
                  }
                >
                  {strings.edit}
                </a>
              </>
            )}
          </p>
        </div>

        <Dropdown disabled={allDPAssigned} title={strings.assignDatapoints}>
          {closeMenu => (
            <React.Fragment>
              <DropdownItem
                closeMenu={closeMenu}
                onClick={() => this.changeTab('SEARCH_DATAPOINTS')}
              >
                {strings.assignDatapointByNameOrId}
              </DropdownItem>

              <DropdownItem
                closeMenu={closeMenu}
                onClick={() => actions.assignAllDatapointsToDevice(deviceData.id)}
              >
                {strings.assignAllDatapoint}
              </DropdownItem>
            </React.Fragment>
          )}
        </Dropdown>
      </div>
    );
  };

  render() {
    const { loadMoreDatapoints } = this.context.actions;
    const { deviceData, datapointsData, allDPAssigned, hasMore } = this.getAssignmentData();

    const assignedDataPointIds = datapointsData.reduce((acc, current) => {
      acc[current.id] = true;
      return acc;
    }, {});

    return (
      <div className="devices-action-page assign-datapoints">
        <div>
          {this.renderHeader(deviceData, datapointsData, allDPAssigned)}

          <div className="body">
            {allDPAssigned ? (
              <AllDatapoints deviceId={deviceData.id} />
            ) : (
              <DatapointList datapointsData={datapointsData} />
            )}

            {!allDPAssigned && hasMore && (
              <button
                onClick={() => loadMoreDatapoints(deviceData.id)}
                className="btnOutline"
                type="button"
              >
                Load more
              </button>
            )}
          </div>
        </div>

        <div>
          {this.state.currentSubTab === 'SEARCH_DATAPOINTS' && (
            <SearchDatapoints
              deviceId={this.props.selectedDeviceId}
              changeTab={this.changeTab}
              assignedDataPointIds={assignedDataPointIds}
            />
          )}
        </div>
      </div>
    );
  }
}

AssignDatapoints.contextType = AssignmentContext;
AssignDatapoints.propTypes = {
  selectedDeviceId: PropTypes.number.isRequired,
  changeTab: PropTypes.func.isRequired,
};
