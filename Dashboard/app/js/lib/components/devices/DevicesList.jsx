import React, { useContext } from 'react';
import DevicesTabContext from './devices-context';
import TABLE_NAMES from './constants';

export default function DevicesList() {
  const {
    strings,
    devices,
    devicesGroup,
    sortProperties,
    selectedDeviceIds,
    deviceToBlockIds,

    // Functions
    tableHeaderClass,

    // Event handlers
    setCurrentTable,
    onSortDevices,
    selectDevice,
    blockDevice,
    showAddToGroupDialog,
    showRemoveFromGroupDialog,
  } = useContext(DevicesTabContext);

  return (
    <>
      <div className="deviceControls">
        <button
          type="button"
          className="btnOutline"
          onClick={() => setCurrentTable(TABLE_NAMES.DEVICES_GROUP)}
        >
          {strings.navText.manageDeviceGroups}
        </button>
        <nav className="dataTabMenu">
          <ul>
            <li>
              <button
                type="button"
                className={selectedDeviceIds.length !== 0 ? '' : 'disabled'}
                onClick={() => selectedDeviceIds.length !== 0 && showAddToGroupDialog()}
              >
                {strings.navText.addToDeviceGroup}
              </button>
            </li>
            <li>
              <button
                type="button"
                className={selectedDeviceIds.length !== 0 ? '' : 'disabled'}
                onClick={() => selectedDeviceIds.length !== 0 && showRemoveFromGroupDialog()}
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
              className={sortProperties.column === 'esn' ? tableHeaderClass() : ''}
              id="device_table_header"
              onClick={() => onSortDevices('esn')}
              onKeyDown={() => onSortDevices('esn')}
            >
              <div className="helpIcon tooltip" data-title={strings.imeiTooltip}>
                ?
              </div>
              <span>{strings.IMEI}</span>
            </th>
            <th
              className={sortProperties.column === 'deviceIdentifier' ? tableHeaderClass() : ''}
              id="device_table_header"
              onClick={() => onSortDevices('deviceIdentifier')}
              onKeyDown={() => onSortDevices('deviceIdentifier')}
            >
              {strings.deviceID}
            </th>
            <th
              className={sortProperties.column === 'deviceGroupName' ? tableHeaderClass() : ''}
              id="device_table_header"
              onClick={() => onSortDevices('deviceGroupName')}
              onKeyDown={() => onSortDevices('deviceGroupName')}
            >
              {strings.deviceGroup}
            </th>
            <th
              className={sortProperties.column === 'lastPositionDate' ? tableHeaderClass() : ''}
              id="device_table_header"
              onClick={() => onSortDevices('lastPositionDate')}
              onKeyDown={() => onSortDevices('lastPositionDate')}
            >
              {strings.lastContact}
            </th>
            <th
              className={
                sortProperties.column === 'gallatinSoftwareManifest' ? tableHeaderClass() : ''
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
          {devices.map(device => {
            const deviceGroupToDisplay = devicesGroup.find(
              group => group.keyId === Number(device.deviceGroup)
            );

            return (
              <tr key={device.keyId}>
                <td className="selection">
                  <input
                    type="checkBox"
                    data-keyid={device.keyId}
                    checked={selectedDeviceIds.includes(device.keyId)}
                    onChange={() => selectDevice(device.keyId, selectedDeviceIds)}
                  />
                </td>
                <td className="IMEI">{device.esn}</td>
                <td className="deviceId">{device.deviceIdentifier}</td>
                <td className="deviceGroup">{deviceGroupToDisplay && deviceGroupToDisplay.code}</td>
                <td className="lastBeacon">{(device.date1, device.lastPositionDate)}</td>
                <td className="version">{device.gallatinSoftwareManifest}</td>
                <td>
                  <div
                    id={device.keyId}
                    onClick={() => blockDevice(device.keyId, deviceToBlockIds)}
                    onKeyDown={() => blockDevice(device.keyId, deviceToBlockIds)}
                  >
                    {deviceToBlockIds.includes(device.keyId) ? 'Unblock' : 'Block'}
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </>
  );
}
