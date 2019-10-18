import React from 'react';

export default class Stats extends React.Component {
  state = {
    startDate: '',
    endDate: '',
  };

  onChange = e => {
    this.setState({ [e.target.id]: e.target.value });
  };

  onSubmit = () => {
    console.log('Im going to be sending these inputs to the api', this.state);
  };

  render() {
    return (
      <div id="stats-page">
        <h2>Download form submission stats</h2>

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
