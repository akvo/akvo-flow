import React from 'react';
import PropTypes from 'prop-types';

class Checkbox extends React.Component {
  state = {
    isChecked: this.props.checked,
  }

  onCheck = () => {
    const { isChecked } = this.state;
    this.setState({ isChecked: !isChecked }, () => {
      this.props.onChange(this.props.name, this.state.isChecked);
    });
  }

  render() {
    const { id, name } = this.props;

    return (
      <input
        type="checkbox"
        id={id}
        name={name}
        checked={this.state.isChecked}
        onChange={this.onCheck}
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
