import React from 'react';
import { render, cleanup } from '@testing-library/react';
import FormSelector from './index';

// Main Tests
describe('FormSelector Tests', () => {
  afterEach(cleanup);

  it('+++ renders <snapshot> correctly', () => {
    const wrapper = render(
      <FormSelector
        forms={{ 145492013: { name: 'Handpump', checked: false } }}
        onCheck={jest.fn()}
      />
    );
    expect(wrapper.container).toMatchSnapshot();
  });
});
