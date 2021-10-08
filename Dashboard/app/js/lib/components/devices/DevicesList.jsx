import React, { useState } from 'react';
import PropTypes from 'prop-types';
import RemoveDialog from './deviceTabDialog/RemoveDialog';

export default function DevicesList() {
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

  return (
    <>
      <div className="deviceControls">
        {/* <a {{action "showManageDeviceGroupsDialog" target="this"}} className="btnOutline">{{t _manage_device_groups}}</a> */}
        <button type="button" className="btnOutline" onClick={() => setSwitchTable(!switchTable)}>
          Manage device groups
        </button>
        <nav className="dataTabMenu">
          <ul>
            <li>
              <button
                type="button"
                className={devicesGroup.length === 0 ? 'disabled' : ''}
                // {{action "showAddToGroupDialog" target="this"}}
              >
                {!switchTable ? 'Add to device group' : 'add a group'}
                {/* {{t _add_to_device_group}} */}
              </button>
            </li>
            <li>
              <button
                type="button"
                className={devicesGroup.length === 0 ? 'disabled' : ''}
                onClick={() => devicesGroup.length !== 0 && setShowRemoveFromGroupDialogBool(true)}
                // {{action "showRemoveFromGroupDialog" target="this"}}
              >
                {!switchTable ? '  Remove from device group' : 'Remove a group'}
                {/* {{t _remove_from_device_group}} */}
              </button>
            </li>
          </ul>
        </nav>
      </div>
      <table className="dataTable" id="surveyDataTable">
        <thead>
          {!switchTable ? (
            <tr>
              <th className="noArrows ember-view" />
              <th className="ember-view" id="device_table_header">
                <div
                  className="ember-view helpIcon tooltip"
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
              <th id="device_table_header">Action</th>
              {/* {{#view FLOW.ColumnView item="select" className="noArrows"}}{{/view}}
            {{#view FLOW.ColumnView item="IMEI" type="device"}} <a {{action "sort" target="this"}}>{{t _imei}} {{tooltip _imei_tooltip}}</a> {{/view}} */}
              {/* {{#view FLOW.ColumnView item="deviceIdentifier" type="device"}} <a {{action "sort" target="this"}}>{{t _device_id}}</a> {{/view}}
            {{#view FLOW.ColumnView item="deviceGroup" type="device"}} <a {{action "sort" target="this"}}>{{t _device_group}}</a> {{/view}}
            {{#view FLOW.ColumnView item="lastPositionDate" type="device"}} <a {{action "sort" target="this"}}>{{t _last_contact}}</a> {{/view}}
            {{#view FLOW.ColumnView item="lastPositionDate" type="device"}} <a {{action "sort" target="this"}}>{{t _version}}</a> {{/view}} </tr> */}
            </tr>
          ) : (
            <tr>
              <th className="noArrows ember-view" />
              <th id="device_table_header">Device Group</th>
              <th id="device_table_header">Action</th>
            </tr>
          )}
        </thead>
        <tbody>
          {!switchTable
            ? devices.map(device => (
                <tr key={device.keyId}>
                  {/* <td class="selection"> {{view Ember.Checkbox checkedBinding="isSelected"}}</td> */}
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
              ))
            : devices.map(device => (
                <tr key={device.keyId}>
                  {/* <td class="selection"> {{view Ember.Checkbox checkedBinding="isSelected"}}</td> */}
                  <td className="selection">
                    <input type="checkBox" onChange={() => selectDevice(device.keyId)} />
                  </td>
                  <td className="deviceGroup">{device.deviceGroupName}</td>
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
      <RemoveDialog
        className={showRemoveFromGroupDialogBool ? `display overlay` : `overlay`}
        cancelRemoveFromGroup={() => setShowRemoveFromGroupDialogBool(false)}
        warningText={!switchTable ? 'Remove devices from device group?' : 'Remove group?'}
      />
    </>
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
