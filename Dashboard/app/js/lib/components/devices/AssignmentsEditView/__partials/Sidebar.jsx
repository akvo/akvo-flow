/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import PropTypes from 'prop-types';
import SidebarDropdown from './SidebarDropdown';

export default class Sidebar extends React.Component {
  render() {
    const { strings, data, deviceGroups, currentTab, changeTab } = this.props;
    const deviceIsSelected = data.selectedDeviceIds.length > 0;

    return (
      <div className="assignment-sidebar">
        <ul>
          <li className={currentTab === 'FORMS' ? 'active' : ''}>
            <a href="#" onClick={() => changeTab('FORMS')}>
              {strings.forms}
            </a>
          </li>

          <li className={currentTab !== 'FORMS' ? 'active' : ''}>
            <a
              className={deviceIsSelected ? 'disabled' : undefined}
              href="#"
              onClick={deviceIsSelected ? undefined : () => changeTab('DEVICES')}
            >
              {strings.devices}
            </a>

            {deviceIsSelected && (
              <a href="#" className="sub-action" onClick={() => changeTab('EDIT_DEVICE')}>
                {strings.edit}
              </a>
            )}

            <a href="#" className="sub-action" onClick={() => changeTab('ADD_DEVICE')}>
              {strings.add}
            </a>
          </li>

          <li className={`sidebar-dropdown-container ${currentTab !== 'FORMS' ? 'active' : ''}`}>
            {Object.keys(deviceGroups).map(dgId => (
              <SidebarDropdown key={dgId} devices={deviceGroups[dgId]} changeTab={changeTab} />
            ))}
          </li>
        </ul>
      </div>
    );
  }
}

Sidebar.propTypes = {
  strings: PropTypes.object.isRequired,
  data: PropTypes.object.isRequired,
  deviceGroups: PropTypes.object.isRequired,
  currentTab: PropTypes.string.isRequired,
  changeTab: PropTypes.func.isRequired,
};
