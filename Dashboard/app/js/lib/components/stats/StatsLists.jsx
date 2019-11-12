import React from 'react';
import PropTypes from 'prop-types';

export default class StatsList extends React.Component {
  renderNoStats = () => {
    return (
      <div className="no-stats">
        <p>{this.props.strings.noStats}</p>
        <p>{this.props.strings.clickToExport}</p>
      </div>
    );
  };

  renderStat = stat => {
    return (
      <a href={stat.url} key={stat.id}>
        <div className="generated-stat">
          <div className="stats-details">
            <span className="stat-icon" />
            <div>
              <span className="date">
                {stat.startDate} - {stat.endDate}{' '}
                {this.props.strings.submissions}
              </span>
              <span className="filename">{stat.name}</span>
            </div>
          </div>

          <p className="stats-status">{stat.status}</p>
        </div>
      </a>
    );
  };

  render() {
    const { stats, strings, goToExport } = this.props;

    return (
      <div id="stats-listing-page">
        <div className="page-header">
          <h2>{strings.generatedStats}</h2>
          <button
            className="standardBtn newStats"
            type="button"
            data-testid="newStatsBtn"
            onClick={goToExport}
          >
            {strings.exportStats}
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
  goToExport: PropTypes.func.isRequired,
  strings: PropTypes.object.isRequired,
};
