import React, { useState } from 'react';
import PropTypes from 'prop-types';
import RemoveDialog from './deviceTabDialog/RemoveDialog';
import DevicesGroupList from './DevicesGroupList';

export default function DevicesList() {
  const xOffset = 10;
  const yOffset = 20;

  const [showRemoveFromGroupDialogBool, setShowRemoveFromGroupDialogBool] = useState(false);
  const [switchTable, setSwitchTable] = useState(false);
  const [devicesGroup, setDevicesGroup] = useState([]);
  const devices = [
    {
      keyId: 150452032,
      phoneNumber: '',
      esn: '358848047655824',
      lastKnownLat: null,
      lastKnownLon: null,
      lastKnownAccuracy: null,
      lastPositionDate: 1491224092773,
      gallatinSoftwareManifest: '2.3.0',
      deviceGroup: '5630049290027008',
      deviceGroupName: 'NEWS',
      deviceIdentifier: 'droidxx',
    },
    {
      keyId: 150482013,
      phoneNumber: '54:40:AD:2B:95:8F',
      esn: '353768076605331',
      lastKnownLat: null,
      lastKnownLon: null,
      lastKnownAccuracy: null,
      lastPositionDate: 1491301178481,
      gallatinSoftwareManifest: '2.3.0',
      deviceGroup: '5067099336605696',
      deviceGroupName: 'MY NEW GROUP',
      deviceIdentifier: 'jana',
    },
  ];
  
  function selectDevice(id) {
    const selectedDevice = devices.find(device => device.keyId === id);
    if (!devicesGroup.some(device => selectedDevice.deviceGroup === device)) {
      setDevicesGroup([...devicesGroup, selectedDevice.deviceGroup]);
    } else {
      const filterDevice = devicesGroup.filter(device => device !== selectedDevice.deviceGroup);
      setDevicesGroup([...filterDevice]);
    }
  }

  const mouseEnter = function(e) {
    const tooltipText = $(e.target).attr('data-title');
    $('body').append(`<p id='tooltip'>${tooltipText}</p>`);
    $('#tooltip')
      .css('top', `${e.pageY - xOffset}px`)
      .css('left', `${e.pageX + yOffset}px`)
      .fadeIn('fast');
  };

  const mouseLeave = function() {
    $('#tooltip').remove();
  };

  const mouseMove = function(e) {
    $('#tooltip')
      .css('top', `${e.pageY - xOffset}px`)
      .css('left', `${e.pageX + yOffset}px`);
  };

  return (
    <section id="devicesList">
      {switchTable ? (
        <DevicesGroupList
          devices={devices}
          switchTable={switchTable}
          setSwitchTable={setSwitchTable}
          selectDevice={selectDevice}
          showRemoveFromGroupDialogBool={showRemoveFromGroupDialogBool}
        />
      ) : (
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
                    className={devicesGroup.length === 0 ? 'disabled' : ''}
                    onClick={() =>
                      devicesGroup.length !== 0 && setShowRemoveFromGroupDialogBool(true)
                    }
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
      )}
      <RemoveDialog
        className={showRemoveFromGroupDialogBool ? `display overlay` : `overlay`}
        cancelRemoveFromGroup={() => setShowRemoveFromGroupDialogBool(false)}
        warningText={!switchTable ? 'Remove devices from device group?' : 'Remove group?'}
      />
    </section>
  );
}

DevicesList.propTypes = {
  showRemoveFromGroupDialog: PropTypes.func,
  showDeleteDevicesDialogBool: PropTypes.bool,
  showAddToGroupDialogBool: PropTypes.bool,
  showRemoveFromGroupDialogBool: PropTypes.bool,
  doAddToGroup: PropTypes.func,
  doRemoveFromGroup: PropTypes.func,
};

DevicesList.defaultProps = {
  showRemoveFromGroupDialog: () => null,
  showDeleteDevicesDialogBool: false,
  showAddToGroupDialogBool: false,
  showRemoveFromGroupDialogBool: false,
  doAddToGroup: () => null,
  doRemoveFromGroup: () => null,
};
