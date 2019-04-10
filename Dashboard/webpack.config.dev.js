/* eslint-disable global-require */
import webpack from 'webpack';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import path from 'path';
import CopyPlugin from 'copy-webpack-plugin';
import { execSync } from 'child_process';

export const HTML_CONFIG = {
  minify: {
    removeComments: true,
    collapseWhitespace: true,
  },
  inject: 'body',
};

export default {
  mode: 'development',
  resolve: {
    extensions: ['*', '.js', '.jsx', '.json', '.handlebars', '.scss'],
    alias: {
      'akvo-flow': path.resolve(__dirname, 'app/js/lib/'),
      templates: path.resolve(__dirname, 'app/js/templates/'),
    },
  },
  externals: {
    'akvo-flow/flowenv': 'FLOW.Env',
    'akvo-flow/currentuser': 'FLOW.currentUser',
  },
  devtool: 'cheap-module-eval-source-map', // more info:https://webpack.js.org/guides/development/#using-source-maps and https://webpack.js.org/configuration/devtool/
  entry: {
    admin: [
      './app/js/lib/webpack-public-path', // must be first entry to properly set public path
      path.resolve(__dirname, 'app/js/lib/main.js'), // Defining path seems necessary for this to work consistently on Windows machines.
    ],
  },
  target: 'web',
  output: {
    path: path.resolve(__dirname, '../GAE/target/akvo-flow/admin'), // Note: Physical files are only output by the production build task `npm run build`.
    filename: '[name].bundle.js',
    publicPath: '/admin/',
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('development'), // Tells React to build in either dev or prod modes. https://facebook.github.io/react/downloads.html (See bottom)
      __DEV__: true,
      __VERSION__: JSON.stringify(execSync('git describe').toString()),
    }),

    new HtmlWebpackPlugin({
      ...HTML_CONFIG,
      template: 'app/dashboard.ejs',
      chunks: ['admin'],
    }),

    new CopyPlugin([
      { from: 'app/js/plugins', to: 'js' },
      { from: 'app/static/images', to: 'images' },
      { from: 'app/js/vendor', to: '../vendorjs/js/vendor' },
    ]),
  ],
  module: {
    rules: [
      {
        test: /\.jsx?$/,
        exclude: /node_modules/,
        use: ['babel-loader'],
      },
      {
        test: /\.eot(\?v=\d+.\d+.\d+)?$/,
        use: ['file-loader'],
      },
      {
        test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 10000,
              mimetype: 'application/font-woff',
            },
          },
        ],
      },
      {
        test: /\.[ot]tf(\?v=\d+.\d+.\d+)?$/,
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 10000,
              mimetype: 'application/octet-stream',
            },
          },
        ],
      },
      {
        test: /\.svg(\?v=\d+\.\d+\.\d+)?$/,
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 10000,
              mimetype: 'image/svg+xml',
              outputPath: 'images',
            },
          },
        ],
      },
      {
        test: /\.(html)$/i,
        use: [
          {
            loader: 'file-loader',
            options: {
              name: '[name].[ext]',
            },
          },
        ],
      },
      {
        test: /\.(jpe?g|png|gif|ico)$/i,
        use: [
          {
            loader: 'file-loader',
            options: {
              name: '[name].[ext]',
              outputPath: 'images',
            },
          },
        ],
      },
      {
        test: /(\.css|\.scss|\.sass)$/,
        use: [
          'style-loader',
          {
            loader: 'css-loader',
            options: {
              sourceMap: true,
            },
          },
          {
            loader: 'postcss-loader',
            options: {
              plugins: () => [
                require('autoprefixer'),
              ],
              sourceMap: true,
            },
          },
          {
            loader: 'sass-loader',
            options: {
              includePaths: [path.resolve(__dirname, 'src', 'scss')],
              sourceMap: true,
            },
          },
        ],
      },
      {
        test: /\.handlebars$/,
        use: ['text-loader'],
      },
    ],
  },
};
