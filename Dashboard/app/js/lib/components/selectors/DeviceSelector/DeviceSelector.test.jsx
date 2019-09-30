import React from 'react';
import { render, cleanup, fireEvent } from '@testing-library/react';
import DeviceSelector from './index';
import '@testing-library/jest-dom/extend-expect';
import flowMock from './FLOW.mock';

// https://github.com/testing-library/react-testing-library#suppressing-unnecessary-warnings-on-react-dom-168
// eslint-disable-next-line no-console
const originalError = console.error;
beforeAll(() => {
  // eslint-disable-next-line no-console
  console.error = (...args) => {
    if (/supports * the "act"/.test(args[0])) {
      return;
    }
    originalError.call(console, ...args);
  };
});

afterAll(() => {
  // eslint-disable-next-line no-console
  console.error = originalError;
});


// Main Tests
describe('DeviceSelector Tests', () => {
  afterEach(cleanup);

  it('+++ renders <snapshot>', () => {
    const wrapper = render(
      <DeviceSelector />
    );

    expect(wrapper.container).toMatchSnapshot();
  });

  it('+++ toggles accordion', () => {
    const wrapper = render(
      <DeviceSelector />
    );

    // find and click on accordion
    const accordion = wrapper.getByTestId('accordion');
    fireEvent.click(accordion);

    // expect panel display to be block
    const panel = wrapper.getByTestId('panel');
    expect(panel).toHaveStyle('display: none'); // open by default when one item was checked
    expect(wrapper.container).toMatchSnapshot();
  });

  test('+++ checkbox works', () => {
    const wrapper = render(
      <DeviceSelector />
    );

    // find input and trigger a check
    // checked by default
    const inputNode = wrapper.getByLabelText('jana');
    fireEvent.click(inputNode);

    expect(flowMock.FLOW.selectedControl.selectedDevices.removeObject).toHaveBeenCalledTimes(1);
  });
});
