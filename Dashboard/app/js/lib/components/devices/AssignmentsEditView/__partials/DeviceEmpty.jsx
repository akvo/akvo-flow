import React from 'react';
import PropTypes from 'prop-types';

export default class DeviceEmpty extends React.Component {
  render() {
    return (
      <div className="device-empty">
        <i className="fa fa-mobile" />
        <p>{this.props.warningText}</p>
      </div>
    );
  }
}

DeviceEmpty.propTypes = {
  warningText: PropTypes.string.isRequired,
};
