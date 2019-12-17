/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
// import Dropdown from 'akvo-flow/components/reusable/Dropdown';
import SearchDatapoints from './SearchDatapoints';

export default class AssignDatapoints extends React.Component {
  state = {
    currentSubTab: 'SEARCH_DATAPOINTS',
  };

  changeTab = tab => {
    this.setState({
      currentSubTab: tab,
    });
  };

  render() {
    const { selectedDevice } = this.props;

    return (
      <div className="devices-action-page assign-datapoints">
        <div>
          <div className="header">
            <div className="device-details">
              <p>Device name</p>
              <p>0 Datapoints assigned</p>
            </div>

            <button
              onClick={() => this.changeTab('SEARCH_DATAPOINTS')}
              type="button"
            >
              By datapoints name or ID
            </button>
          </div>

          <div className="body">
            <p>Body of datapoints {selectedDevice}</p>
          </div>
        </div>

        <div>
          {this.state.currentSubTab === 'SEARCH_DATAPOINTS' && (
            <SearchDatapoints changeTab={this.changeTab} />
          )}
        </div>
      </div>
    );
  }
}

AssignDatapoints.propTypes = {
  selectedDevice: PropTypes.string.isRequired,
};
