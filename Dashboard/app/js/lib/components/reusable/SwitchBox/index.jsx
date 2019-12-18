import React from 'react';
import PropTypes from 'prop-types';
import './style.scss';

export default class SwitchBox extends React.Component {
  render() {
    const { id, name, checked } = this.props;

    return (
      <label htmlFor={id} className="switch">
        <input
          type="checkbox"
          id={id}
          name={name}
          checked={checked}
          onChange={() => this.props.onChange(name, !checked)}
          data-testid="switch-box"
        />
        <span className="slider round" />
      </label>
    );
  }
}

SwitchBox.defaultProps = {
  checked: false,
};

SwitchBox.propTypes = {
  checked: PropTypes.bool,
  id: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
};
