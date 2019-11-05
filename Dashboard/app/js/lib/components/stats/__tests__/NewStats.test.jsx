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

describe('New Stats Tests', () => {
  afterEach(cleanup);

  const props = {
    strings: {
      generateStats: 'Generate form submission stats',
      formTimeFrame: 'Form submission time frame',
      startDate: 'Start date',
      toDate: 'To date',
      downloadStats: 'Download Stats',
    },
    generateReport: jest.fn(),
  };

  it('+++ renders snapshots', () => {
    const wrapper = render(<NewStats {...props} />);

    expect(wrapper.container).toMatchSnapshot();
  });

  it('+++ handles inputs and submit forms', () => {
    const wrapper = render(<NewStats {...props} />);

    // get start and end date input
    const startDate = wrapper.getByLabelText('Start date:');
    const endDate = wrapper.getByLabelText('To date:');

    // select date on inputs
    fireEvent.change(startDate, { target: { value: '2019-10-01' } });
    fireEvent.change(endDate, { target: { value: '2019-10-10' } });

    // click on submit button
    fireEvent.click(wrapper.getByText('Download Stats'));

    // expectations
    expect(props.generateReport).toHaveBeenCalledTimes(1);
    expect(props.generateReport).toHaveBeenCalledWith({
      // be called with ISO strings
      startDate: '2019-10-01T00:00:00.000Z',
      endDate: '2019-10-10T00:00:00.000Z',
    });
  });
});
