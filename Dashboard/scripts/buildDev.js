// More info on Webpack's Node API here: https://webpack.js.org/api/node/
// Allowing console calls below since this is a build file.
/* eslint-disable no-console */
import webpack from 'webpack';
import config from '../webpack.config.dev';
import publicConfig from '../webpack.config.public.dev';
import {
  chalkError, chalkSuccess, chalkWarning, chalkProcessing,
} from './chalkConfig';

process.env.NODE_ENV = 'development'; // this assures React is built in prod mode and that the Babel dev config doesn't apply.
console.log(chalkProcessing('Generating bundle. This will take a moment...'));

const publicCompiler = webpack(publicConfig);

publicCompiler.watch({
  // Example watchOptions
  aggregateTimeout: 300,
  poll: undefined,
}, (error, stats) => {
  if (error) { // so a fatal error occurred. Stop here.
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

  // if we got this far, the build succeeded.
  console.log(chalkSuccess('Public app is compiled. Now compiling dashboard app...'));

  return 0;
});

const compiler = webpack(config);

compiler.watch({
  // Example watchOptions
  aggregateTimeout: 300,
  poll: undefined,
}, (error, stats) => {
  if (error) { // so a fatal error occurred. Stop here.
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

  // if we got this far, the build succeeded.
  console.log(chalkSuccess('Your app is compiled and ready to roll!'));

  return 0;
});
