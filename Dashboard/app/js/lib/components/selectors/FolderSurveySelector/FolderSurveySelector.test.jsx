import React from 'react';
import { render, cleanup, fireEvent } from '@testing-library/react';
import FolderSurveySelector from './index';
import '@testing-library/jest-dom/extend-expect';

// https://github.com/testing-library/react-testing-library#suppressing-unnecessary-warnings-on-react-dom-168
const originalError = console.error;
beforeAll(() => {
  console.error = (...args) => {
    if (/supports * the "act"/.test(args[0])) {
      return;
    }
    originalError.call(console, ...args);
  };
});

afterAll(() => {
  console.error = originalError;
});


// Main Tests
describe('FolderSurveySelector Tests', () => {
  afterEach(cleanup);


  const initialLevels = [[
    { keyId: 0, name: 'Choose a folder or survey', parentId: null },
    { keyId: 153142013, name: 'Folder with few large data sets', parentId: 0 },
  ]];

  it('+++ renders <snapshot>', () => {
    const wrapper = render(<FolderSurveySelector onChange={jest.fn} levels={initialLevels} />);
    expect(wrapper.container).toMatchSnapshot();
  });

  it('+++ calls for new levels and update state when option is selected', () => {
    // create mock function and make it return new levels when called
    const onChangeFn = jest.fn();
    onChangeFn.mockReturnValue([[
      { keyId: 0, parentId: null, name: 'Choose a folder or survey' },
      { keyId: 152342023, parentId: 153142013, name: 'BAR-handpump' },
    ]]);

    const wrapper = render(<FolderSurveySelector onChange={onChangeFn} levels={initialLevels} />);

    // select a select form and trigger a value change
    const select = wrapper.getByTestId('folder-survey-select-0');
    fireEvent.change(select, { target: { value: 153142013 } });

    // expect the onChange props to have been called once
    expect(onChangeFn).toHaveBeenCalledTimes(1);

    // expect the onChange props to be called with the right value
    expect(onChangeFn).toHaveBeenCalledWith('153142013');

    // expect a new select form to be available
    expect(wrapper.getByTestId('folder-survey-select-1')).toBeTruthy();

    // save snapshot of the current result
    expect(wrapper.container).toMatchSnapshot();
  });

  it('+++ doesn\'t update state when no new value comes in after option is selected', () => {
    // create mock function and make it return null when called
    const onChangeFn = jest.fn();
    onChangeFn.mockReturnValue(null);

    const wrapper = render(<FolderSurveySelector onChange={onChangeFn} levels={initialLevels} />);

    // select a select form and trigger a value change
    const select = wrapper.getByTestId('folder-survey-select-0');
    fireEvent.change(select, { target: { value: 153142013 } });

    // expect the onChange props to have been called once
    expect(onChangeFn).toHaveBeenCalledTimes(1);

    // expect the onChange props to be called with the right value
    expect(onChangeFn).toHaveBeenCalledWith('153142013');

    // expect no new select form to be available
    expect(wrapper.queryByTestId('folder-survey-select-1')).toBeFalsy();
  });
});
