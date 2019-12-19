/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import Checkbox from 'akvo-flow/components/reusable/Checkbox';
import AssignmentContext from '../../assignment-context';

export default class SearchDatapoints extends React.Component {
  state = {
    selectedDatapoints: [],
  };

  onSearch = e => {
    e.preventDefault();

    const searchKey = e.target.searchDatapoints.value;
    this.context.actions.findDatapoints(searchKey);
  };

  onSelectDatapoint = (id, checked) => {
    const { selectedDatapoints } = this.state;
    let newSelectedDatapoints = [];

    if (checked) {
      newSelectedDatapoints = selectedDatapoints.concat(id);
    } else {
      newSelectedDatapoints = selectedDatapoints.filter(
        device => device !== id
      );
    }

    this.setState({ selectedDatapoints: newSelectedDatapoints });
  };

  addToAssignment = () => {
    const { selectedDatapoints } = this.state;
    const { datapoints } = this.context.data;
    const { addDatapointsToAssignment } = this.context.actions;

    const formattedSelectedDps = selectedDatapoints.map(sDp =>
      datapoints.find(dp => dp.id === sDp)
    );

    addDatapointsToAssignment(formattedSelectedDps, this.props.deviceId);

    // empty selected devices
    this.setState({ selectedDatapoints: [] });
  };

  render() {
    const { datapoints } = this.context.data;
    const { selectedDatapoints } = this.state;

    return (
      <div className="search-datapoints">
        <div className="header">
          <p>Assign datapoints by name</p>

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
              type="search"
              id="searchDatapoints"
              placeholder="Search datapoint by name"
            />
          </form>

          <div className="search-results">
            {datapoints.map(dp => (
              <Checkbox
                key={dp.id}
                id={dp.id}
                name={dp.id}
                checked={selectedDatapoints.includes(dp.id)}
                onChange={this.onSelectDatapoint}
                label={dp.name}
              />
            ))}
          </div>
        </div>

        <div className="footer">
          <div className="footer-inner">
            <div>
              <p>{selectedDatapoints.length} selected</p>
            </div>

            <button
              type="button"
              onClick={this.addToAssignment}
              className="btnOutline"
            >
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
  deviceId: PropTypes.string.isRequired,
  changeTab: PropTypes.func.isRequired,
};
