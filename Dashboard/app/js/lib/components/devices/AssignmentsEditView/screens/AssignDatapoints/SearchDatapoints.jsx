/* eslint-disable import/no-unresolved */
import React from 'react';
import Checkbox from 'akvo-flow/components/reusable/Checkbox';

export default class SearchDatapoints extends React.Component {
  onSearch = e => {
    e.preventDefault();

    const searchKey = e.target.searchDatapoints.value;
    console.log(searchKey);
  };

  render() {
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
          <form className="search-bar" onSubmit={this.onSearch}>
            <i className="fa fa-search" />
            <input
              type="search"
              id="searchDatapoints"
              placeholder="Search datapoint by name or ID"
            />
          </form>

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
  }
}
