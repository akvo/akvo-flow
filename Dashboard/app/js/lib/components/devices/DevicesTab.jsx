import React, { useState } from 'react';
import DevicesGroupList from './DevicesGroupList';
import DevicesList from './DevicesList';
import RemoveDialog from './deviceTabDialog/RemoveDialog';

export default function DevicesTab() {
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
          selectDevice={selectDevice}
          setSwitchTable={setSwitchTable}
        />
      ) : (
        <DevicesList
          devices={devices}
          devicesGroup={devicesGroup}
          selectDevice={selectDevice}
          setSwitchTable={setSwitchTable}
          mouseEnter={mouseEnter}
          mouseLeave={mouseLeave}
          mouseMove={mouseMove}
          setShowRemoveFromGroupDialogBool={setShowRemoveFromGroupDialogBool}
        />
      )}
      <RemoveDialog
        className={showRemoveFromGroupDialogBool ? `display overlay` : `overlay`}
        cancelRemoveFromGroup={() => setShowRemoveFromGroupDialogBool(false)}
        warningText="Remove devices from device group?"
      />
    </section>
  );
}
