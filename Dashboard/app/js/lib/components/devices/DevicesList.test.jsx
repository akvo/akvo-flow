import React from 'react';
import { render, cleanup } from '@testing-library/react';
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
    },
  };

  const tableHeaderClass = () => 'sorting_asc';

  const selectedDeviceIds = [];

  const sortProperties = {
    column: 'code',
  };

  it('+++ renders <snapshot>', () => {
    const wrapper = render(
      <DevicesTabContext.Provider
        value={{ strings, devices, tableHeaderClass, selectedDeviceIds, sortProperties }}
      >
        <Deviceslist />
      </DevicesTabContext.Provider>
    );

    expect(wrapper.container).toMatchSnapshot();
  });
});
