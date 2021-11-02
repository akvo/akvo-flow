import React from 'react';
import { render, cleanup, fireEvent } from '@testing-library/react';
import DevicesTabContext from './devices-context';
import DevicesGroupList from './DevicesGroupList';

// Main Tests
describe('DevicesGroupList Tests', () => {
  afterEach(cleanup);

  const devicesGroup = [
    {
      keyId: 1689003,
      description: null,
      name: null,
      code: 'caetie',
      createdDateTime: 1363622757899,
      lastUpdateDateTime: 1363622757899,
      deviceList: null,
      displayName: 'caetie',
    },
  ];

  const strings = {
    deviceGroup: 'Device group',
    delete: 'Delete',
    action: 'Action',
    navText: {
      manageDevices: 'manage device',
      newGroup: 'New group',
    },
  };

  const sortProperties = {
    column: 'code',
  };

  const tableHeaderClass = () => 'sorting_asc';

  const selectedDeviceGroupIds = [];

  it('+++ renders <snapshot>', () => {
    const wrapper = render(
      <DevicesTabContext.Provider
        value={{ devicesGroup, strings, sortProperties, tableHeaderClass, selectedDeviceGroupIds }}
      >
        <DevicesGroupList strings={strings} />
      </DevicesTabContext.Provider>
    );

    const addNewGroupButton = wrapper.getByText('New group', { selector: 'button' });
    fireEvent.click(addNewGroupButton);

    expect(wrapper.container).toMatchSnapshot();
  });
});
