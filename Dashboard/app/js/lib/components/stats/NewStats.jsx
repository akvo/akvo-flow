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
    const data = {
      startDate: new Date(this.state.startDate).toISOString(),
      endDate: new Date(this.state.endDate).toISOString(),
    };

    this.props.generateReport(data);
  };

  render() {
    return (
      <div id="stats-page">
        <h2>Generate form submission stats</h2>

        <p>Form submission time frame</p>

        <div className="date-picker">
          <label htmlFor="startDate">
            <p>Start Date:</p>

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
            <p>To Date:</p>

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
          Download Stats
        </button>
      </div>
    );
  }
}

Stats.propTypes = {
  generateReport: PropTypes.func.isRequired,
};
