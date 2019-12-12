import React from 'react';
import { render, cleanup } from '@testing-library/react';
import FormSelector from './index';
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
