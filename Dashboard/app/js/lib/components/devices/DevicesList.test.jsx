import React from 'react';
import { render, cleanup, fireEvent } from '@testing-library/react';
import DevicesTabContext from './devices-context';
import Deviceslist from './DevicesList';

// Main Tests
describe('Deviceslist Tests', () => {
  afterEach(cleanup);

  const devices = [
    {
      keyId: 261243005,
      phoneNumber: '',
      esn: '351859083835250',
      lastKnownLat: null,
      lastKnownLon: null,
      lastKnownAccuracy: null,
      lastPositionDate: 1624877130200,
      gallatinSoftwareManifest: '2.10.1',
      deviceGroup: '1689003',
      deviceGroupName: 'caetie',
      deviceIdentifier: 'valeria_moto5',
    },
  ];

  const strings = {
    deviceGroup: 'Device group',
    delete: 'Delete',
    action: 'Action',
    imeiTooltip: 'imei tooltip',
    IMEI: 'IMEI',
    deviceID: 'device id',
    lastContact: 'last contact',
    version: 'version',
    navText: {
      manageDevices: 'manage device',
      addToDeviceGroup: 'Add to device group',
      removeFromDeviceGroup: 'Remove from deviceGroup',
    },
  };

  const tableHeaderClass = () => 'sorting_asc';

  const sortProperties = {
    column: 'code',
  };

  let selectedDeviceIds = [];

  const selectDevice = () => {
    if (selectedDeviceIds.some(deviceId => deviceId === 261243005)) {
      const filterDevice = selectedDeviceIds.filter(deviceId => deviceId !== 261243005);
      selectedDeviceIds = [...filterDevice];
    } else {
      selectedDeviceIds = [...selectedDeviceIds, 261243005];
    }
    return selectedDeviceIds;
  };

  const { showAddToGroupDialog, showRemoveFromGroupDialog } = () => jest.fn();

  it('+++ renders <snapshot>', () => {
    const wrapper = render(
      <DevicesTabContext.Provider
        value={{
          strings,
          devices,
          tableHeaderClass,
          selectedDeviceIds,
          sortProperties,
          selectDevice,
          showAddToGroupDialog,
          showRemoveFromGroupDialog,
        }}
      >
        <Deviceslist />
      </DevicesTabContext.Provider>
    );

    const showAddToGroupButton = wrapper.getByText('Add to device group');
    fireEvent.click(showAddToGroupButton);

    const showRemoveFromGroupButton = wrapper.getByText('Remove from deviceGroup');
    fireEvent.click(showRemoveFromGroupButton);

    const selectDeviceCheckbox = document.querySelector('input');

    expect(selectedDeviceIds.length).toBe(0);
    fireEvent.click(selectDeviceCheckbox);
    expect(selectedDeviceIds.length).toBe(1);
    fireEvent.click(selectDeviceCheckbox);
    expect(selectedDeviceIds.length).toBe(0);

    expect(wrapper.container).toMatchSnapshot();
  });
});
