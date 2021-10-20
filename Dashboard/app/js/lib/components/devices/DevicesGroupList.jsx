import React, { useContext } from 'react';
import PropTypes from 'prop-types';
import DevicesTabContext from './device-context';

export default function DevicesGroupList({ selectDevice, setSwitchTable }) {
  const { devices, strings, onSort, sortProperties } = useContext(DevicesTabContext);
  return (
    <>
      <div className="deviceControls">
        <button type="button" className="btnOutline" onClick={() => setSwitchTable(false)}>
          {strings.navText.manageDevices}
        </button>
        <nav className="dataTabMenu">
          <ul>
            <li>
              <button type="button" onClick={() => alert('add a group')}>
                {strings.navText.newGroup}
              </button>
            </li>
          </ul>
        </nav>
      </div>

      {/* TABLE CONTENTS */}
      <table className="dataTable" id="surveyDataTable">
        <thead>
          <tr>
            <th className="noArrows" />
            <th
              id="device_table_header"
              className={
                sortProperties.column == 'deviceGroupName'
                  ? sortProperties.ascending
                    ? 'sorting_asc'
                    : 'sorting_desc'
                  : ''
              }
              onClick={() => onSort('deviceGroupName')}
              onKeyDown={() => onSort('deviceGroupName')}
            >
              {strings.deviceGroup}
            </th>
            <th id="device_table_header" className="noArrows">
              {strings.action}
            </th>
          </tr>
        </thead>
        <tbody>
          {devices.map(device => (
            <tr key={device.keyId}>
              <td className="selection">
                <input type="checkBox" onChange={() => selectDevice(device.keyId)} />
              </td>
              <td className="deviceGroup">{device.deviceGroupName}</td>
              <td>
                <div
                  onClick={() => alert(`${device.deviceGroupName} is deleted`)}
                  onKeyDown={() => alert(`${device.deviceGroupName} is deleted`)}
                >
                  {strings.delete}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}

DevicesGroupList.propTypes = {
  devices: PropTypes.array,
  selectDevice: PropTypes.func,
  setSwitchTable: PropTypes.func,
};

DevicesGroupList.defaultProps = {
  devices: [],
  selectDevice: () => null,
  setSwitchTable: () => null,
};
