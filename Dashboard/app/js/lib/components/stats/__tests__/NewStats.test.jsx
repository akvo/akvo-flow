import React from 'react';
import { render, cleanup, fireEvent } from '@testing-library/react';
import NewStats from '../NewStats';
import '@testing-library/jest-dom/extend-expect';

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

describe('News Stats Tests', () => {
  afterEach(cleanup);

  const generateReport = jest.fn();

  it('+++ renders snapshots', () => {
    const wrapper = render(<NewStats generateReport={generateReport} />);

    expect(wrapper.container).toMatchSnapshot();
  });

  it('+++ handles inputs and submit forms', () => {
    const wrapper = render(<NewStats generateReport={generateReport} />);

    // get start and end date input
    const startDate = wrapper.getByLabelText('Start Date:');
    const endDate = wrapper.getByLabelText('To Date:');

    // select date on inputs
    fireEvent.change(startDate, { target: { value: '2019-10-01' } });
    fireEvent.change(endDate, { target: { value: '2019-10-10' } });

    // click on submit button
    fireEvent.click(wrapper.getByText('Download Stats'));

    // expectations
    expect(generateReport).toHaveBeenCalledTimes(1);
    expect(generateReport).toHaveBeenCalledWith({
      // be called with ISO strings
      startDate: '2019-10-01T00:00:00.000Z',
      endDate: '2019-10-10T00:00:00.000Z',
    });
  });
});
