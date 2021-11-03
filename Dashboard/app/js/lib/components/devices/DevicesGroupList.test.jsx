import React from 'react';
import { render, cleanup, fireEvent } from '@testing-library/react';
import DevicesTabContext from './devices-context';
import DevicesGroupList from './DevicesGroupList';

// Main Tests
describe('DevicesGroupList Tests', () => {
  afterEach(cleanup);

  let devicesGroup = [
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

  const newGroup = {
    keyId: 5562979080732672,
    description: null,
    name: null,
    code: 'fwfwfwdsaddsf',
    createdDateTime: 1635914912204,
    lastUpdateDateTime: 1635915246764,
    deviceList: null,
    displayName: 'fwfwfwdsaddsf',
  };

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

  const addNewGroup = () => {
    devicesGroup = [...devicesGroup, newGroup];
  };

  const onDeleteGroup = () => {
    const filterDevicesGroup = devicesGroup.filter(group => group.keyId !== 1689003);
    devicesGroup = [...filterDevicesGroup];
    return devicesGroup;
  };

  it('+++ renders <snapshot>', () => {
    const wrapper = render(
      <DevicesTabContext.Provider
        value={{
          devicesGroup,
          strings,
          sortProperties,
          tableHeaderClass,
          selectedDeviceGroupIds,
          addNewGroup,
          onDeleteGroup,
        }}
      >
        <DevicesGroupList />
      </DevicesTabContext.Provider>
    );

    const addNewGroupButton = wrapper.getByText('New group', { selector: 'button' });

    // Test adding a new group
    expect(devicesGroup.length).toEqual(1);
    fireEvent.click(addNewGroupButton);
    expect(devicesGroup.length).toEqual(2);

    // Test delete button
    const deleteGroup = wrapper.getByText('Delete');
    fireEvent.click(deleteGroup);
    expect(devicesGroup.length).toEqual(1);

    expect(wrapper.container).toMatchSnapshot();
  });
});
