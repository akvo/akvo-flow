import React, { useContext } from 'react';
import PropTypes from 'prop-types';
import DevicesTabContext from './device-context';

export default function DevicesList({
  setSwitchTable,
  // devicesGroup,
  mouseEnter,
  mouseLeave,
  mouseMove,
}) {
  const {
    devices,
    showRemoveFromGroupDialog,
    strings,
    onSortDevices,
    sortProperties,
    selectDevice,
    selectedDeviceIds,
    tableHeaderClass,
  } = useContext(DevicesTabContext);

  return (
    <>
      <div className="deviceControls">
        <button type="button" className="btnOutline" onClick={() => setSwitchTable(true)}>
          {strings.navText.manageDeviceGroups}
        </button>
        <nav className="dataTabMenu">
          <ul>
            <li>
              <button
                type="button"
                className={selectedDeviceIds.length !== 0 ? '' : 'disabled'}
                onClick={() => selectedDeviceIds.length !== 0 && alert('Added to device group')}
              >
                {strings.navText.addToDeviceGroup}
              </button>
            </li>
            <li>
              <button
                type="button"
                className={selectedDeviceIds.length === 0 ? 'disabled' : ''}
                onClick={showRemoveFromGroupDialog}
              >
                {strings.navText.removeFromDeviceGroup}
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
              className={sortProperties.column == 'esn' ? tableHeaderClass() : ''}
              id="device_table_header"
              onClick={() => onSortDevices('esn')}
              onKeyDown={() => onSortDevices('esn')}
            >
              <div
                onMouseEnter={mouseEnter}
                onMouseMove={mouseMove}
                onMouseLeave={mouseLeave}
                className="helpIcon tooltip"
                data-title={strings.imeiTooltip}
              >
                ?
              </div>
              <span>{strings.IMEI}</span>
            </th>
            <th
              className={sortProperties.column == 'deviceIdentifier' ? tableHeaderClass() : ''}
              id="device_table_header"
              onClick={() => onSortDevices('deviceIdentifier')}
              onKeyDown={() => onSortDevices('deviceIdentifier')}
            >
              {strings.deviceID}
            </th>
            <th
              className={sortProperties.column == 'deviceGroupName' ? tableHeaderClass() : ''}
              id="device_table_header"
              onClick={() => onSortDevices('deviceGroupName')}
              onKeyDown={() => onSortDevices('deviceGroupName')}
            >
              {strings.deviceGroup}
            </th>
            <th
              className={sortProperties.column == 'lastPositionDate' ? tableHeaderClass() : ''}
              id="device_table_header"
              onClick={() => onSortDevices('lastPositionDate')}
              onKeyDown={() => onSortDevices('lastPositionDate')}
            >
              {strings.lastContact}
            </th>
            <th
              className={
                sortProperties.column == 'gallatinSoftwareManifest' ? tableHeaderClass() : ''
              }
              id="device_table_header"
              onClick={() => onSortDevices('gallatinSoftwareManifest')}
              onKeyDown={() => onSortDevices('gallatinSoftwareManifest')}
            >
              {strings.version}
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

DevicesList.propTypes = {
  devices: PropTypes.array,
  devicesGroup: PropTypes.array,
  setSwitchTable: PropTypes.func,
  mouseEnter: PropTypes.func,
  mouseLeave: PropTypes.func,
  mouseMove: PropTypes.func,
  showRemoveFromGroupDialog: PropTypes.func,
  strings: PropTypes.object,
};

DevicesList.defaultProps = {
  devices: [],
  devicesGroup: [],
  setSwitchTable: () => null,
  mouseEnter: () => null,
  mouseLeave: () => null,
  mouseMove: () => null,
  showRemoveFromGroupDialog: () => null,
  strings: {},
};
