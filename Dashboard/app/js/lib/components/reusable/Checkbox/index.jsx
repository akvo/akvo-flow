import React from 'react';
import PropTypes from 'prop-types';
import './style.scss';

class Checkbox extends React.Component {
  render() {
    const { id, name, checked, label } = this.props;

    // Label has no id because select all checkboxes has the same id of 0
    // so if two labels has the same id, when i click on one the other one gets checked too
    return (
      <label htmlFor="no_id" className="checkbox-container">
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
