/* eslint-disable no-console, import/no-extraneous-dependencies */
import webpack from 'webpack';
import path from 'path';
import { execSync } from 'child_process';
import config from '../webpack.config.prod';
import configPublic from '../webpack.config.public.prod';
import { chalkError, chalkSuccess, chalkWarning, chalkProcessing } from './chalkConfig';

process.env.NODE_ENV = 'production';
console.log(chalkProcessing('Generating bundle. This will take a moment...'));

const handler = (callback = () => {}) => (error, stats) => {
  if (error) {
    console.log(chalkError(error));
    return 1;
  }

  const jsonStats = stats.toJson();

  if (jsonStats.hasErrors) {
    return jsonStats.errors.map(error2 => console.log(chalkError(error2)));
  }

  if (jsonStats.hasWarnings) {
    console.log(chalkWarning('Webpack generated the following warnings: '));
    jsonStats.warnings.map(warning => console.log(chalkWarning(warning)));
  }

  console.log(`Webpack stats: ${stats}`);

  console.log(chalkSuccess('The app is compiled and ready for production...'));

  callback();

  return 0;
};

webpack(config).run(handler(() => {
  webpack(configPublic).run(handler());
  execSync(`node ${path.join(__dirname, './buildUsersCss.js')}`);
}));
