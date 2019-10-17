import React from 'react';
import PropTypes from 'prop-types';

class Checkbox extends React.Component {
  render() {
    const { id, name, checked } = this.props;

    return (
      <input
        type="checkbox"
        id={id}
        name={name}
        checked={checked}
        onChange={() => this.props.onChange(name, !checked)}
        className="displayInline"
      />
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
};

export default Checkbox;
