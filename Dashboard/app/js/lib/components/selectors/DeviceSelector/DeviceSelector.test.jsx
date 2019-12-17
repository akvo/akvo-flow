import React from 'react';
import { render, cleanup, fireEvent } from '@testing-library/react';
import DeviceSelector from './index';
import '@testing-library/jest-dom/extend-expect';

// Main Tests
describe('DeviceSelector Tests', () => {
  afterEach(cleanup);

  const deviceGroups = {
    // key is the device group id
    1: [
      {
        name: 'droidxx',
        id: '150452032',
        deviceGroup: { id: '1', name: 'Device not in group' },
      },
      {
        name: 'jana',
        id: '150482013',
        deviceGroup: { id: '1', name: 'Device not in group' },
      },
    ],
  };

  it('+++ renders initial <snapshot>', () => {
    const wrapper = render(
      <DeviceSelector
        deviceGroups={deviceGroups}
        handleSelectDevice={jest.fn()}
        handleSelectAllDevices={jest.fn()}
        selectedDevices={[]}
      />
    );

    expect(wrapper.container).toMatchSnapshot();
  });

  it('+++ toggles accordion', () => {
    const wrapper = render(
      <DeviceSelector
        deviceGroups={deviceGroups}
        handleSelectDevice={jest.fn()}
        handleSelectAllDevices={jest.fn()}
        selectedDevices={[]}
      />
    );

    // find and click on accordion
    const accordion = wrapper.getByTestId('accordion');
    const panel = wrapper.getByTestId('panel');

    // close accordion
    fireEvent.click(accordion);

    // expect panel display to be closed
    expect(panel).toHaveStyle('display: none');

    // open accordion
    fireEvent.click(accordion);

    // expect panel display to be opened
    expect(panel).toHaveStyle('display: block');
  });

  test('+++ select device works', () => {
    const handleSelectDevice = jest.fn();

    const wrapper = render(
      <DeviceSelector
        deviceGroups={deviceGroups}
        handleSelectDevice={handleSelectDevice}
        handleSelectAllDevices={jest.fn()}
        selectedDevices={[]}
      />
    );

    // find input and trigger a check
    const inputNode = wrapper.getByLabelText('jana');
    fireEvent.click(inputNode);

    expect(handleSelectDevice).toHaveBeenCalledTimes(1);
    expect(handleSelectDevice).toHaveBeenCalledWith('150482013', true);
  });

  test('+++ select all device works', () => {
    const handleSelectAllDevice = jest.fn();

    const wrapper = render(
      <DeviceSelector
        deviceGroups={deviceGroups}
        handleSelectDevice={jest.fn()}
        handleSelectAllDevices={handleSelectAllDevice}
        selectedDevices={[]}
      />
    );

    // find input and trigger a check
    const inputNode = wrapper.getByLabelText('');
    fireEvent.click(inputNode);

    expect(handleSelectAllDevice).toHaveBeenCalledTimes(1);
    expect(handleSelectAllDevice).toHaveBeenCalledWith(
      ['150452032', '150482013'],
      true
    );
  });
});
