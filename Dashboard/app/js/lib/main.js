require('../plugins/flowDashboard');
require('../plugins/geocells');
require('../plugins/tooltip');

require('akvo-flow/flowenv');
require('akvo-flow/models/FLOWrest-adapter-v2-common');
require('akvo-flow/models/models');
require('akvo-flow/controllers/controllers');
require('akvo-flow/views/views');
require('akvo-flow/router/router');
require('akvo-flow/version');
require('akvo-flow/analytics');
require('../vendor/Google');

require('../../css/screen.scss');

if (FLOW.Env.mapsProvider == 'google') {
  var regionBias = FLOW.Env.googleMapsRegionBias;
  var region = regionBias ? '&region=' + regionBias : '';
  document.write('<script src="https://maps.google.com/maps/api/js?key=AIzaSyBZU7kLJ75VlTlC5Qrfi1n1N-5hJYProuQ' + region + '"><\/script>');
}

FLOW.initialize();
