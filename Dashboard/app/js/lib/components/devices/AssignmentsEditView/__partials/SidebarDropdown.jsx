import React from 'react';
import PropTypes from 'prop-types';

export default class SidebarDropdow extends React.Component {
  state = {
    isOpen: true,
  };

  toggleDropdown = () => {
    this.setState(state => ({
      isOpen: !state.isOpen,
    }));
  };

  getStyleProps = () => {
    const { isOpen } = this.state;
    const fontClass = `fa fa-chevron-${isOpen ? 'up' : 'down'}`;
    const panelStyle = isOpen ? { display: 'block' } : { display: 'none' };

    return {
      fontClass,
      panelStyle,
    };
  };

  render() {
    const { fontClass, panelStyle } = this.getStyleProps();
    const { devices } = this.props;

    return (
      <div className="sidebar-dropdown">
        <div
          onKeyDown={this.toggleDropdown}
          onClick={this.toggleDropdown}
          className="dd-header"
        >
          <span>{devices[0].deviceGroup.name}</span>
          <i className={fontClass} />
        </div>

        <div style={panelStyle} className="sidebar-panel">
          {devices.map(device => (
            <button type="button">{device.name}</button>
          ))}
        </div>
      </div>
    );
  }
}

SidebarDropdow.propTypes = {
  devices: PropTypes.array.isRequired,
};
