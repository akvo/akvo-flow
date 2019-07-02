import React from 'react';
import PropTypes from 'prop-types';
/* eslint-disable no-nested-ternary */
const ChildOption = ({
  name,
  value,
}) => (
  <option value={value}>{name}</option>
);

ChildOption.propTypes = {
  name: PropTypes.string.isRequired,
  value: PropTypes.number.isRequired,
};

export default ChildOption;
