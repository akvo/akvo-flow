
require('akvo-flow/vendor/handlebars-1.0.rc.1');
require('akvo-flow/vendor/ember-1.0.0.pre-2-36');
require('akvo-flow/vendor/ember-data-rev10');

require('akvo-flow/all_locales');
require('akvo-flow/models/FLOWrest-adapter-v2');
require('akvo-flow/models/models');

require('akvo-flow/core-public');
require('akvo-flow/flowenv');

require('akvo-flow/controllers/controllers/maps-controller');
require('akvo-flow/views/views-public');
require('akvo-flow/router/router-public');

FLOW.initialize();
