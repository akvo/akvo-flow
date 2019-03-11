/* eslint-disable global-require */
import path from 'path';
import devConfig from './webpack.config.dev';

export default {
  ...devConfig,
  watch: false,
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
