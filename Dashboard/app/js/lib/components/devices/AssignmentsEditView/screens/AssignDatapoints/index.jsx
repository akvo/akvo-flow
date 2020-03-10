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

    if (datapointAssignment) {
      datapointsData = datapointAssignment.datapoints;
    }

    return {
      deviceData,
      datapointsData,
    };
  };

  renderHeader = (deviceData, datapointsData) => {
    const { strings, data, actions } = this.context;
    const datapointsCount = datapointsData.length;

    return (
      <div className="header">
        <div className="device-details">
          <p>{deviceData.name}</p>
          <p>
            <span>
              {datapointsCount} {strings.datapointAssigned}
            </span>
            {!data.allDataPointsAssigned && (
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

        <Dropdown disabled={data.allDataPointsAssigned} title={strings.assignDatapoints}>
          {closeMenu => (
            <React.Fragment>
              <DropdownItem
                closeMenu={closeMenu}
                onClick={() => this.changeTab('SEARCH_DATAPOINTS')}
              >
                {strings.assignDatapointByNameOrId}
              </DropdownItem>

              <DropdownItem closeMenu={closeMenu} onClick={actions.assignAllDatapointsToDevice}>
                {strings.assignAllDatapoint}
              </DropdownItem>
            </React.Fragment>
          )}
        </Dropdown>
      </div>
    );
  };

  render() {
    const { data } = this.context;
    const { deviceData, datapointsData } = this.getAssignmentData();

    return (
      <div className="devices-action-page assign-datapoints">
        <div>
          {this.renderHeader(deviceData, datapointsData)}

          <div className="body">
            {data.allDataPointsAssigned ? (
              <AllDatapoints />
            ) : (
              <DatapointList datapointsData={datapointsData} />
            )}
          </div>
        </div>

        <div>
          {this.state.currentSubTab === 'SEARCH_DATAPOINTS' && (
            <SearchDatapoints deviceId={this.props.selectedDeviceId} changeTab={this.changeTab} />
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
