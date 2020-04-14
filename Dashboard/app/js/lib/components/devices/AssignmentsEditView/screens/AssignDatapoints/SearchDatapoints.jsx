/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import Checkbox from 'akvo-flow/components/reusable/Checkbox';
import AssignmentContext from '../../assignment-context';

export default class SearchDatapoints extends React.Component {
  state = {
    selectedDatapointsIds: [],
  };

  componentWillUnmount() {
    this.context.actions.clearSearchedDatapoints();
  }

  onSearch = e => {
    e.preventDefault();

    const searchKey = e.target.searchDatapoints.value;
    this.context.actions.findDatapoints(searchKey);
  };

  onSelectDatapoint = (id, checked) => {
    const { selectedDatapointsIds } = this.state;
    let newSelectedDatapointsIds = [];

    if (checked) {
      newSelectedDatapointsIds = selectedDatapointsIds.concat(id);
    } else {
      newSelectedDatapointsIds = selectedDatapointsIds.filter(datapointId => datapointId !== id);
    }

    this.setState({ selectedDatapointsIds: newSelectedDatapointsIds });
  };

  addToAssignment = () => {
    const { selectedDatapointsIds } = this.state;
    const { datapointsResults } = this.context.data;
    const { assignDataPointsToDevice } = this.context.actions;

    // format datapoints to datapoints object when adding to assignment
    assignDataPointsToDevice(
      selectedDatapointsIds.map(sDp => datapointsResults.find(dp => dp.id === sDp)),
      this.props.deviceId
    );

    // empty selected devices
    this.setState({ selectedDatapointsIds: [] });
  };

  render() {
    const { datapointsResults } = this.context.data;
    const { strings } = this.context;
    const { selectedDatapointsIds } = this.state;

    return (
      <div className="search-datapoints">
        <div className="header">
          <p>{strings.assignDatapointByNameOrId}</p>

          <i
            className="fa fa-times icon"
            onClick={() => this.props.changeTab('')}
            onKeyDown={() => this.props.changeTab('')}
          />
        </div>

        <div className="body">
          {/* search bar */}
          <form className="search-bar" onSubmit={this.onSearch}>
            <i className="fa fa-search" />
            <input
              type="text"
              id="searchDatapoints"
              placeholder={strings.searchDatapointByNameOrId}
            />
          </form>

          <div className="search-results">
            {datapointsResults.map(dp => (
              <div key={dp.id} className="search-result">
                <Checkbox
                  key={dp.id}
                  id={dp.id}
                  name={dp.id}
                  checked={selectedDatapointsIds.includes(dp.id)}
                  onChange={this.onSelectDatapoint}
                  label=""
                />

                <label htmlFor={dp.id}>
                  <p>{dp.name}</p>
                  <span>{dp.identifier}</span>
                </label>
              </div>
            ))}
          </div>
        </div>

        <div className="footer">
          <div className="footer-inner">
            <div>
              <p>{selectedDatapointsIds.length} selected</p>
            </div>

            <button type="button" onClick={this.addToAssignment} className="btnOutline">
              Assign
            </button>
          </div>
        </div>
      </div>
    );
  }
}

SearchDatapoints.contextType = AssignmentContext;

SearchDatapoints.propTypes = {
  deviceId: PropTypes.number.isRequired,
  changeTab: PropTypes.func.isRequired,
};
