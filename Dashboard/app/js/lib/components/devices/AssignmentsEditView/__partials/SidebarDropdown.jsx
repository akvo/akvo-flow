/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import PropTypes from 'prop-types';
import AssignmentsContext from '../assignment-context';

export default class SidebarDropdown extends React.Component {
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
    const { data } = this.context;
    const { fontClass, panelStyle } = this.getStyleProps();
    const { devices, changeTab } = this.props;

    return (
      <div className="sidebar-dropdown">
        <div onKeyDown={this.toggleDropdown} onClick={this.toggleDropdown} className="dd-header">
          <span>{devices[0].deviceGroup.name}</span>
          <i className={fontClass} />
        </div>

        <div style={panelStyle} className="sidebar-panel">
          {devices.map(device => (
            <a
              key={device.id}
              href="#"
              onClick={
                data.datapointsEnabled ? () => changeTab('ASSIGN_DATAPOINTS', device.id) : undefined
              }
              className={data.datapointsEnabled ? undefined : 'disabled'}
            >
              {device.name}
            </a>
          ))}
        </div>
      </div>
    );
  }
}

SidebarDropdown.contextType = AssignmentsContext;
SidebarDropdown.propTypes = {
  devices: PropTypes.array.isRequired,
  changeTab: PropTypes.func.isRequired,
};
