import React from 'react';
import { render, cleanup, fireEvent } from '@testing-library/react';
import Checkbox from './index';

// Main Tests
describe('Checkbox Tests', () => {
  afterEach(cleanup);

  const props = {
    id: 'fake-id',
    name: 'fake-id',
    checked: false,
    label: 'Fake checkbox',
    onChange: jest.fn(),
  };

  it('+++ renders <snapshot>', () => {
    const wrapper = render(<Checkbox {...props} />);

    expect(wrapper.container).toMatchSnapshot();
  });

  test('+++ checkbox works', () => {
    const wrapper = render(<Checkbox {...props} />);

    // find input and trigger a check
    const inputNode = wrapper.getByLabelText('Fake checkbox');
    fireEvent.click(inputNode);

    expect(props.onChange).toHaveBeenCalledTimes(1);
    expect(props.onChange).toHaveBeenCalledWith('fake-id', true);
  });
});
