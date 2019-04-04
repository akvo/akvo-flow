/* eslint-disable no-console, import/no-extraneous-dependencies */
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
  aggregateTimeout: 300,
  poll: undefined,
}, (error, stats) => {
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

  console.log(chalkSuccess('Public app is compiled. Now compiling dashboard app...'));

  return 0;
});

const compiler = webpack(config);

compiler.watch({
  aggregateTimeout: 300,
  poll: undefined,
}, (error, stats) => {
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

  console.log(chalkSuccess('The app is compiled and ready for development...'));

  return 0;
});
