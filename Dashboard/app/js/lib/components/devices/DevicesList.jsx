import React, { useContext } from 'react';
import PropTypes from 'prop-types';
import DevicesTabContext from './device-context';

export default function DevicesList({
  selectDevice,
  setSwitchTable,
  devicesGroup,
  mouseEnter,
  mouseLeave,
  mouseMove,
  // setShowRemoveFromGroupDialogBool,
}) {
  const { devices, showRemoveFromGroupDialog } = useContext(
    DevicesTabContext
  );
  return (
    <>
      <div className="deviceControls">
        <button type="button" className="btnOutline" onClick={() => setSwitchTable(true)}>
          Manage device groups
        </button>
        <nav className="dataTabMenu">
          <ul>
            <li>
              <button
                type="button"
                className={devicesGroup.length !== 0 ? '' : 'disabled'}
                onClick={() => devicesGroup.length !== 0 && alert('Added to device group')}
              >
                Add to device group
              </button>
            </li>
            <li>
              <button
                type="button"
                // className={devicesGroup.length === 0 ? 'disabled' : ''}
                onClick={showRemoveFromGroupDialog}
              >
                Remove from device group
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
            <th id="device_table_header">
              <div
                onMouseEnter={mouseEnter}
                onMouseMove={mouseMove}
                onMouseLeave={mouseLeave}
                className="helpIcon tooltip"
                data-title="The IMEI is the identifying number unique to each device that helps to identify it in our Akvo database. IMEI stands for International Mobile Station Equipment Identity number."
              >
                ?
              </div>
              <span>IMEI</span>
            </th>
            <th id="device_table_header">Device ID</th>
            <th id="device_table_header">Device Group</th>
            <th id="device_table_header">Last contact</th>
            <th id="device_table_header">Version</th>
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
              <td className="EMEI">{device.esn}</td>
              <td className="deviceId">{device.deviceIdentifier}</td>
              <td className="deviceGroup">{device.deviceGroupName}</td>
              <td className="lastBeacon">{(device.date1, device.lastPositionDate)}</td>
              <td className="version">{device.gallatinSoftwareManifest}</td>
              <td>
                <div
                  onClick={() => alert(`${device.deviceIdentifier} is deleted`)}
                  onKeyDown={() => alert(`${device.deviceIdentifier} is deleted`)}
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

DevicesList.propTypes = {
  devices: PropTypes.array,
  devicesGroup: PropTypes.array,
  selectDevice: PropTypes.func,
  setSwitchTable: PropTypes.func,
  mouseEnter: PropTypes.func,
  mouseLeave: PropTypes.func,
  mouseMove: PropTypes.func,
  showRemoveFromGroupDialog: PropTypes.func,
};

DevicesList.defaultProps = {
  devices: [],
  devicesGroup: [],
  selectDevice: () => null,
  setSwitchTable: () => null,
  mouseEnter: () => null,
  mouseLeave: () => null,
  mouseMove: () => null,
  showRemoveFromGroupDialog: () => null,
};
