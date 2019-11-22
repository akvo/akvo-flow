import React from 'react';

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

    return (
      <div className="sidebar-dropdown">
        <div
          onKeyDown={this.toggleDropdown}
          onClick={this.toggleDropdown}
          className="dd-header"
        >
          <span>Partner 1</span>
          <i className={fontClass} />
        </div>

        <div style={panelStyle} className="sidebar-panel">
          <button type="button">Water mapping 2</button>
          <button type="button">Water mapping 3</button>
        </div>
      </div>
    );
  }
}
