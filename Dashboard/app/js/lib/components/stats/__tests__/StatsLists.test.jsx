import React from 'react';
import { render, cleanup, fireEvent } from '@testing-library/react';
import StatsList from '../StatsLists';
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

describe('Stats Lists Tests', () => {
  afterEach(cleanup);

  const stats = [
    {
      id: 345598,
      name: 'Test export',
      startDate: '2019-10-01',
      endDate: '2019-10-11',
      status: 'Finished Error',
    },
  ];

  const goToNewExports = jest.fn();

  it('+++ renders snapshots', () => {
    const wrapper = render(
      <StatsList stats={stats} goToExport={goToNewExports} />
    );

    expect(wrapper.container).toMatchSnapshot();
  });

  it('+++ goes to export page on button press', () => {
    const wrapper = render(
      <StatsList stats={stats} goToExport={goToNewExports} />
    );

    // find and click on button
    const button = wrapper.getByTestId('newStatsBtn');
    fireEvent.click(button);

    expect(goToNewExports).toHaveBeenCalledTimes(1);
  });
});
