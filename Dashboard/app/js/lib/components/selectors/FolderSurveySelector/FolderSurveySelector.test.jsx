import React from 'react';
import { render, cleanup, fireEvent } from '@testing-library/react';
import FolderSurveySelector from './index';

// Main Tests
describe('FolderSurveySelector Tests', () => {
  afterEach(cleanup);

  const props = {
    surveyGroups: [
      {
        keyId: 153142013,
        parentId: 0,
        name: 'Folder with a few large data sets',
        published: false,
        projectType: 'PROJECT_FOLDER',
        monitoringGroup: false,
        ancestorIds: [0],
      },
      {
        keyId: 152342023,
        parentId: 153142013,
        name: 'BAR-handpump',
        published: false,
        projectType: 'PROJECT',
        monitoringGroup: true,
        ancestorIds: [0, 153142013],
      },
      {
        keyId: 148412306,
        parentId: 153142013,
        name: 'NR-handpump',
        published: false,
        projectType: 'PROJECT',
        monitoringGroup: true,
        ancestorIds: [0, 153142013],
      },
    ],
    strings: { chooseFolderOrSurvey: 'Choose folder or survey' },
  };

  it('+++ renders <snapshot>', () => {
    const wrapper = render(
      <FolderSurveySelector {...props} onSelectSurvey={jest.fn()} />
    );
    expect(wrapper.container).toMatchSnapshot();
  });

  it('+++ checks if a survey is selected when options is clicked', () => {
    // create mock function and make it return new levels when called
    const onChangeFn = jest.fn();
    onChangeFn.mockReturnValue(true);

    const wrapper = render(
      <FolderSurveySelector {...props} onSelectSurvey={onChangeFn} />
    );

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

  it("+++ doesn't update state when a survey is selected", () => {
    // create mock function and make it return null when called
    const onChangeFn = jest.fn();
    onChangeFn.mockReturnValue(false);

    const wrapper = render(
      <FolderSurveySelector {...props} onSelectSurvey={onChangeFn} />
    );

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
