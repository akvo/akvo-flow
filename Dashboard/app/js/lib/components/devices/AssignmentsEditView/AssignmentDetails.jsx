import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';

export default class AssignmentDetails extends React.Component {
  formatMomentDate = date => moment(date, 'YYYY/MM/DD').format('YYYY-MM-DD')

  onInputChange = (e) => {
    this.props.onChange(e.target.id, e.target.value);
  }

  render() {
    const { strings, values } = this.props;

    return (
      <fieldset id="assignmentDetails">
        <h2>
          0.1
          {' '}
          {strings.assignmentDetails}
        </h2>

        {/* Assignment Name */}
        <label htmlFor="assignmentName">
          {strings.assignmentName}
          :
          {/* TODO:: Add failure message for  */}
          <span style={{ color: 'red' }}>
            {' '}
            {strings.nameValidationMsg}
          </span>
        </label>

        <input
          type="text"
          id="assignmentName"
          value={values.assignmentName}
          onChange={this.onInputChange}
          placeholder={strings.assignmentNamePlaceholder}
          size="30"
        />

        {/* Date range */}
        <div className="dateRange">
          <div className="activeDate">
            <label htmlFor="startDate">
              {strings.startDate}
              :
            </label>
            <input
              type="date"
              id="startDate"
              value={this.formatMomentDate(values.startDate)}
              onChange={this.onInputChange}
              style={{ width: '95%' }}
              className="datePicker"
            />
          </div>

          <div className="expireDate">
            <label htmlFor="expireDate">
              {strings.expireDate}
              :
            </label>
            <input
              type="date"
              id="expireDate"
              value={this.formatMomentDate(values.expireDate)}
              onChange={this.onInputChange}
              style={{ width: '95%' }}
              className="datePicker"
              min={this.formatMomentDate(values.startDate)}
            />
          </div>
        </div>
      </fieldset>
    );
  }
}

AssignmentDetails.propTypes = {
  strings: PropTypes.object.isRequired,
  values: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
};
