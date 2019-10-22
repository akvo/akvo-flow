import React from 'react';
import PropTypes from 'prop-types';

export default class StatsList extends React.Component {
  renderNoStats = () => {
    return (
      <div className="no-stats">
        <p>No stats generated yet</p>
        <p>Click {'"Export stats"'} to get started</p>
      </div>
    );
  };

  renderStat = stat => {
    return (
      <div key={stat.id} className="generated-stat">
        <div className="stats-details">
          <span className="stat-icon" />
          <div>
            <a href="/">{stat.name}</a>
            <span className="stat-date">
              {stat.startDate} - {stat.endDate} Submissions
            </span>
          </div>
        </div>

        <p className="stats-status">{stat.status}</p>
      </div>
    );
  };

  render() {
    const { stats } = this.props;

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
            {stats.length ? stats.map(this.renderStat) : this.renderNoStats()}
          </div>
        </div>
      </div>
    );
  }
}

StatsList.propTypes = {
  stats: PropTypes.array.isRequired,
};
