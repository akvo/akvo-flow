import React from 'react';
import PropTypes from 'prop-types';
import { groupBy as _groupBy } from 'lodash';
import assignmentContext from '../assignment-context';
import SidebarDropdown from './SidebarDropdown';
import { withTabRouter } from './TabRouter';

class Sidebar extends React.Component {
  getDeviceGroups = () => {
    // filter out selected devices
    const { devices, selectedDeviceIds } = this.context.data;

    const selectedDevices = devices.filter(device => selectedDeviceIds.includes(device.id));

    return _groupBy(selectedDevices, device => device.deviceGroup.id);
  };

  render() {
    const { strings, data } = this.context;
    const { currentPath, changeTab } = this.props.routerContext;
    const deviceGroups = this.getDeviceGroups();
    const deviceIsSelected = data.selectedDeviceIds.length > 0;

    return (
      <div className="assignment-sidebar">
        <ul>
          <li className={currentPath === '' ? 'active' : ''}>
            <a href="#" onClick={() => changeTab('')}>
              {strings.forms}
            </a>
          </li>

          <li className={currentPath.length ? 'active' : ''}>
            <a
              className={deviceIsSelected ? 'disabled' : undefined}
              href="#"
              onClick={deviceIsSelected ? undefined : () => changeTab('DEVICE')}
            >
              {strings.devices}
            </a>

            {deviceIsSelected && (
              <a href="#" className="sub-action" onClick={() => changeTab('EDIT_DEVICES')}>
                {strings.edit}
              </a>
            )}

            <a href="#" className="sub-action" onClick={() => changeTab('ADD_DEVICES')}>
              {strings.add}
            </a>
          </li>

          <li className={`sidebar-dropdown-container ${currentPath.length ? 'active' : ''}`}>
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
  routerContext: PropTypes.object.isRequired,
};
Sidebar.contextType = assignmentContext;
export default withTabRouter(Sidebar);
