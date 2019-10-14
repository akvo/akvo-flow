import React from 'react';
import PropTypes from 'prop-types';

export default class DeviceAccordion extends React.Component {
  state = {
    isAccordionOpen: this.props.deviceGroupIsActive,
  };

  onAccordionClick = () => {
    const { isAccordionOpen } = this.state;
    this.setState({ isAccordionOpen: !isAccordionOpen });
  };

  render() {
    const { isAccordionOpen } = this.state;
    const { name } = this.props;
    const accordionClass = `accordion ${isAccordionOpen && 'active'}`;
    const panelStyle = isAccordionOpen
      ? { display: 'block' }
      : { display: 'none' };

    return (
      <div>
        <div
          className={accordionClass}
          onClick={this.onAccordionClick}
          onKeyPress={this.onAccordionClick}
          data-testid="accordion"
        >
          {name}
        </div>

        <div className="panel" style={panelStyle} data-testid="panel">
          {this.props.children}
        </div>
      </div>
    );
  }
}

DeviceAccordion.propTypes = {
  deviceGroupIsActive: PropTypes.bool.isRequired,
  name: PropTypes.string.isRequired,
  children: PropTypes.any.isRequired,
};
