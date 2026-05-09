/**
 * @format
 */

import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import App from '../App';

jest.mock('react-native-webview', () => {
  const mockReact = require('react');
  const { Text: MockText } = require('react-native');

  return {
    WebView: () => mockReact.createElement(MockText, null, 'Mock WebView'),
  };
});

test('renders correctly', async () => {
  await ReactTestRenderer.act(() => {
    ReactTestRenderer.create(<App />);
  });
});
