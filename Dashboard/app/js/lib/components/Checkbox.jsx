import React from 'react';
import PropTypes from 'prop-types';

const Checkbox = ({
  type = 'checkbox',
  id,
  name,
  checked = false,
  onChange,
}) => (
  <input
    type={type}
    id={id}
    name={name}
    checked={checked}
    onChange={onChange}
    className="displayInline"
  />
);

Checkbox.defaultProps = {
  type: 'checkbox',
};

Checkbox.propTypes = {
  type: PropTypes.string,
  id: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  checked: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired,
};

export default Checkbox;
