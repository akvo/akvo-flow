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
}) {
  const { devices, showRemoveFromGroupDialog, strings, onSort, sortProperties } = useContext(
    DevicesTabContext
  );

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
                className={devicesGroup.length !== 0 ? '' : 'disabled'}
                onClick={() => devicesGroup.length !== 0 && alert('Added to device group')}
              >
                {strings.navText.addToDeviceGroup}
              </button>
            </li>
            <li>
              <button
                type="button"
                className={devicesGroup.length == 0 ? 'disabled' : ''}
                onClick={devicesGroup.length == 0 && showRemoveFromGroupDialog}
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
              className={
                sortProperties.column == 'esn'
                  ? sortProperties.ascending
                    ? 'sorting_asc'
                    : 'sorting_desc'
                  : ''
              }
              id="device_table_header"
              onClick={() => onSort('esn')}
              onKeyDown={() => onSort('esn')}
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
              className={
                sortProperties.column == 'deviceIdentifier'
                  ? sortProperties.ascending
                    ? 'sorting_asc'
                    : 'sorting_desc'
                  : ''
              }
              id="device_table_header"
              onClick={() => onSort('deviceIdentifier')}
              onKeyDown={() => onSort('deviceIdentifier')}
            >
              {strings.deviceID}
            </th>
            <th
              className={
                sortProperties.column == 'deviceGroupName'
                  ? sortProperties.ascending
                    ? 'sorting_asc'
                    : 'sorting_desc'
                  : ''
              }
              id="device_table_header"
              onClick={() => onSort('deviceGroupName')}
              onKeyDown={() => onSort('deviceGroupName')}
            >
              {strings.deviceGroup}
            </th>
            <th
              className={
                sortProperties.column == 'lastPositionDate'
                  ? sortProperties.ascending
                    ? 'sorting_asc'
                    : 'sorting_desc'
                  : ''
              }
              id="device_table_header"
              onClick={() => onSort('lastPositionDate')}
              onKeyDown={() => onSort('lastPositionDate')}
            >
              {strings.lastContact}
            </th>
            <th
              className={
                sortProperties.column == 'gallatinSoftwareManifest'
                  ? sortProperties.ascending
                    ? 'sorting_asc'
                    : 'sorting_desc'
                  : ''
              }
              id="device_table_header"
              onClick={() => onSort('gallatinSoftwareManifest')}
              onKeyDown={() => onSort('gallatinSoftwareManifest')}
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
  selectDevice: PropTypes.func,
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
  selectDevice: () => null,
  setSwitchTable: () => null,
  mouseEnter: () => null,
  mouseLeave: () => null,
  mouseMove: () => null,
  showRemoveFromGroupDialog: () => null,
  strings: {},
};
