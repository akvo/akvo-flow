import React from 'react';
import PropTypes from 'prop-types';

export default class Stats extends React.Component {
  state = {
    startDate: '',
    endDate: '',
  };

  onChange = e => {
    this.setState({ [e.target.id]: e.target.value });
  };

  onSubmit = () => {
    const { startDate, endDate } = this.state;

    const data = {
      startDate: startDate.length ? new Date(startDate).toISOString() : null,
      endDate: endDate.length
        ? new Date(endDate).toISOString()
        : new Date().toISOString(),
    };

    this.props.generateReport(data);
  };

  render() {
    const { strings } = this.props;
    return (
      <div id="stats-page">
        <h2>{strings.generateStats}</h2>

        <p>{strings.formTimeFrame}</p>

        <div className="date-picker">
          <label htmlFor="startDate">
            <p>{strings.startDate}:</p>

            <input
              type="date"
              id="startDate"
              value={this.state.startDate}
              onChange={this.onChange}
              className="datePicker"
              style={{ width: '95%' }}
            />
          </label>

          <label htmlFor="endDate">
            <p>{strings.toDate}:</p>

            <input
              type="date"
              id="endDate"
              value={this.state.endDate}
              onChange={this.onChange}
              className="datePicker"
              style={{ width: '95%' }}
              min={this.state.startDate}
            />
          </label>
        </div>

        <button onClick={this.onSubmit} className="standardBtn" type="button">
          {strings.downloadStats}
        </button>
      </div>
    );
  }
}

Stats.propTypes = {
  generateReport: PropTypes.func.isRequired,
  strings: PropTypes.object.isRequired,
};
