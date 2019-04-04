require('akvo-flow/models/FLOWrest-adapter-v2-common');
require('akvo-flow/models/models-public');
require('akvo-flow/controllers/controllers-public');
require('akvo-flow/views/views-public');
require('akvo-flow/router/router-public');
require('akvo-flow/version');
require('akvo-flow/analytics');
require('../vendor/Google');

require('../../css/screen.scss');

const regionBias = FLOW.Env.googleMapsRegionBias;
const region = regionBias ? `&region=${regionBias}` : '';
document.write(`<script src="https://maps.google.com/maps/api/js?v=3.2&sensor=false${region}"><\/script>`);

FLOW.initialize();
