import React from 'react';

export default class StatsList extends React.Component {
  renderNoStats = () => {
    return (
      <div className="no-stats">
        <p>No stats generated yet</p>
        <p>Click {'"Export stats"'} to get started</p>
      </div>
    );
  };

  render() {
    return (
      <div id="stats-listing-page">
        <div className="page-header">
          <h2>Generated stats</h2>
          <button className="standardBtn newStats" type="button">
            Export stats
          </button>
        </div>

        <div className="main-page">
          <div className="generated-stats">
            <div className="generated-stat">
              <div className="stats-details">
                <span className="stat-icon" />
                <div>
                  <a href="/">Form submission stats - 231509125.xls</a>
                  <span className="stat-date">
                    13 Mar 2016 - 31 Oct 2019 Submissions
                  </span>
                </div>
              </div>

              <p className="stats-status">Generating...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }
}
