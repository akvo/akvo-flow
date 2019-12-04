/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import Dropdown from 'akvo-flow/components/reusable/Dropdown';
import Checkbox from 'akvo-flow/components/reusable/Checkbox';

export default class AssignDatapoints extends React.Component {
  state = {
    currentSubTab: 'SEARCH_DATAPOINTS',
  };

  changeTab = tab => {
    this.setState({
      currentSubTab: tab,
    });
  };

  renderDatapointsSearch = () => {
    return (
      <div className="search-datapoints">
        <div className="header">
          <p>Assign datapoints by name of ID</p>

          <i
            className="fa fa-times icon"
            onClick={() => this.changeTab('')}
            onKeyDown={() => this.changeTab('')}
          />
        </div>

        <div className="body">
          {/* search bar */}
          <div className="search-bar">
            <i className="fa fa-search" />
            <input
              type="search"
              id="searchDatapoints"
              placeholder="Search datapoint by name or ID"
            />
          </div>

          <div className="search-results">
            <Checkbox
              id="001"
              name="001"
              checked={false}
              onChange={() => null}
              label="Type A built borehole Street 1"
            />

            <Checkbox
              id="002"
              name="002"
              checked
              onChange={() => null}
              label="Type A built borehole Street 1"
            />
          </div>
        </div>

        <div className="footer">
          <div className="footer-inner">
            <div>
              <p>0 selected</p>
            </div>

            <button type="button" onClick={() => null} className="btnOutline">
              Assign
            </button>
          </div>
        </div>
      </div>
    );
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

            <Dropdown title="Assign Datapoints">
              <button type="button">By datapoints name or ID</button>
              <button type="button">Assign all datapoints</button>
            </Dropdown>
          </div>

          <div className="body">
            <p>Body of datapoints {selectedDevice}</p>
          </div>
        </div>

        <div>
          {this.state.currentSubTab === 'SEARCH_DATAPOINTS' &&
            this.renderDatapointsSearch()}
        </div>
      </div>
    );
  }
}

AssignDatapoints.propTypes = {
  selectedDevice: PropTypes.string.isRequired,
};
