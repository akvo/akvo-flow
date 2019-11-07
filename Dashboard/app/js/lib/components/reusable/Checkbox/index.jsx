import React from 'react';
import PropTypes from 'prop-types';
import './style.scss';

class Checkbox extends React.Component {
  render() {
    const { id, name, checked, label } = this.props;

    // using name because id is used to control the label and input
    return (
      <label htmlFor={id} className="checkbox-container">
        {label}
        <input
          type="checkbox"
          id={id}
          name={name}
          checked={checked}
          onChange={() => this.props.onChange(name, !checked)}
          className="displayInline"
        />
        <span className="checkmark" />
      </label>
    );
  }
}

Checkbox.defaultProps = {
  checked: false,
};

Checkbox.propTypes = {
  checked: PropTypes.bool,
  id: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  label: PropTypes.string.isRequired,
};

export default Checkbox;
