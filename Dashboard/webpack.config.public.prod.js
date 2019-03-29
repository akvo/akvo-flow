import path from 'path';
import webpack from 'webpack';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import { execSync } from 'child_process';

import prodConfig, { HTML_CONFIG } from './webpack.config.prod';

export default {
  ...prodConfig,
  plugins: [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('development'), // Tells React to build in either dev or prod modes. https://facebook.github.io/react/downloads.html (See bottom)
      __DEV__: true,
      __VERSION__: JSON.stringify(execSync('git describe').toString()),
    }),
    new HtmlWebpackPlugin({
      ...HTML_CONFIG,
      template: 'app/public.ejs',
      chunks: ['pub'],
      filename: '../index.html',
    }),
  ],
  entry: {
    publicmap: [
      './app/js/lib/webpack-public-path', // must be first entry to properly set public path
      path.resolve(__dirname, 'app/js/lib/main-public.js'),
    ],
  },
  output: {
    path: path.resolve(__dirname, '../GAE/target/akvo-flow/publicmap'), // Note: Physical files are only output by the production build task `npm run build`.
    filename: '[name].bundle.js',
    publicPath: '/publicmap/',
  },
};
