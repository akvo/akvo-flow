import React from 'react';
import { render, cleanup, fireEvent } from '@testing-library/react';
import SwitchBox from './index';

// Main Tests
describe('SwitchBox Tests', () => {
  afterEach(cleanup);

  const props = {
    id: 'fake-id',
    name: 'fake-id',
    checked: false,
    onChange: jest.fn(),
  };

  it('+++ renders <snapshot>', () => {
    const wrapper = render(<SwitchBox {...props} />);

    expect(wrapper.container).toMatchSnapshot();
  });

  test('+++ SwitchBox works', () => {
    const wrapper = render(<SwitchBox {...props} />);

    // find input and trigger a check
    const inputNode = wrapper.getByTestId('switch-box');
    fireEvent.click(inputNode);

    expect(props.onChange).toHaveBeenCalledTimes(1);
    expect(props.onChange).toHaveBeenCalledWith('fake-id', true);
  });
});
