import React from 'react';
import PropTypes from 'prop-types';

export default function DevicesGroupList({ devices, selectDevice, setSwitchTable }) {
  return (
    <>
      <div className="deviceControls">
        <button type="button" className="btnOutline" onClick={() => setSwitchTable(false)}>
          Manage devices
        </button>
        <nav className="dataTabMenu">
          <ul>
            <li>
              <button type="button" onClick={() => alert('add a group')}>
                add a group
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
            <th id="device_table_header">Device Group</th>
            <th id="device_table_header" className="noArrows">
              Action
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
                  Delete
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
