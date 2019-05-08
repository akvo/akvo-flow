import observe from '../../mixins/observe';
import template from '../../mixins/template';

const { create_geomodel } = require('../../../plugins/geocells');

FLOW.NavMapsView = FLOW.View.extend(template('navMaps/nav-maps-common'), observe({
  'FLOW.placemarkDetailController.content.isLoaded': 'handlePlacemarkDetails',
}), {
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,
  map: null,
  geoModel: null,
  showSurveyFilters: false,

  init() {
    this._super();
    this.detailsPaneElements = '#pointDetails h2'
      + ', #pointDetails dl'
      + ', #pointDetails img'
      + ', #pointDetails .imgContainer'
      + ', .placeMarkBasicInfo'
      + ', .noDetails';
    this.detailsPaneVisible = false;
  },

  redoMap() {
    const mapBounds = this.map.getBounds();
    // get current bounding box of the visible map
    const n = mapBounds.getNorthEast().lat;
    let e = mapBounds.getNorthEast().lng;
    const s = mapBounds.getSouthWest().lat;
    let w = mapBounds.getSouthWest().lng;

    // bound east and west
    e = ((e + 3 * 180.0) % (2 * 180.0)) - 180.0;
    w = ((w + 3 * 180.0) % (2 * 180.0)) - 180.0;

    // create bounding box object
    const bb = this.geoModel.create_bounding_box(n, e, s, w);

    // create the best set of geocell box cells which covers
    // the current viewport
    const bestBB = this.geoModel.best_bbox_search_cells(bb);

    // adapt the points shown on the map
    FLOW.router.mapsController.adaptMap(bestBB, this.map.getZoom());
  },

  /**
    Once the view is in the DOM create the map
  */
  didInsertElement() {
    const self = this;

    if (FLOW.Env.mapsProvider === 'google') {
      this.map = new L.Map('flowMap', { center: new L.LatLng(-0.703107, 36.765), zoom: 2 });
      const roadmap = new L.Google('ROADMAP');
      const terrain = new L.Google('TERRAIN');
      const satellite = new L.Google('SATELLITE');
      this.map.addLayer(roadmap);
      this.map.addControl(new L.Control.Layers({
        Roadmap: roadmap,
        Satellite: satellite,
        Terrain: terrain,
      }, {}));
    } else {
      // insert the map for the mapbox
      const options = {
        minZoom: 2,
        maxZoom: 18,
      };
      L.mapbox.accessToken = 'pk.eyJ1IjoiYWt2byIsImEiOiJzUFVwR3pJIn0.8dLa4fHG19fBwwBUJMDOSQ';
      const baseLayers = {
        Terrain: L.mapbox.tileLayer('akvo.he30g8mm'),
        Streets: L.mapbox.tileLayer('akvo.he2pdjhk'),
        Satellite: L.mapbox.tileLayer('akvo.he30neh4'),
      };

      this.map = L.mapbox.map('flowMap', 'akvo.he30g8mm', options).setView([-0.703107, 36.765], 2);
      L.control.layers(baseLayers).addTo(this.map);
    }

    // add scale indication to map
    L.control.scale({ position: 'topleft', maxWidth: 150 }).addTo(this.map);

    // couple listener to end of zoom or drag
    this.map.on('moveend', () => {
      self.redoMap();
    });

    FLOW.router.mapsController.set('map', this.map);
    this.geoModel = create_geomodel();

    // load points for the visible map
    this.redoMap();

    this.$('#mapDetailsHideShow').click(() => {
      self.handleShowHideDetails();
    });

    // Slide in detailspane after 1 sec
    this.hideDetailsPane(1000);
  },

  /**
    Helper function to dispatch to either hide or show details pane
  */
  handleShowHideDetails() {
    if (this.detailsPaneVisible) {
      this.hideDetailsPane();
    } else {
      this.showDetailsPane();
    }
  },

  /**
    Slide in the details pane
  */
  showDetailsPane() {
    const button = this.$('#mapDetailsHideShow');
    button.html('Hide &rsaquo;');
    this.set('detailsPaneVisible', true);

    this.$('#flowMap').animate({
      width: '75%',
    }, 200);
    this.$('#pointDetails').animate({
      width: '24.5%',
    }, 200).css({
      overflow: 'auto',
      marginLeft: '-2px',
    });
    this.$(this.detailsPaneElements, '#pointDetails').animate({
      opacity: '1',
    }, 200).css({
      display: 'inherit',
    });
  },


  /**
    Slide out details pane
  */
  hideDetailsPane(delay = 0) {
    const button = this.$('#mapDetailsHideShow');

    this.set('detailsPaneVisible', false);
    button.html('');

    this.$('#flowMap').delay(delay).animate({
      width: '99.25%',
    }, 200);
    this.$('#pointDetails').delay(delay).animate({
      width: '0.25%',
    }, 200).css({
      overflow: 'scroll-y',
      marginLeft: '-2px',
    });
    this.$(this.detailsPaneElements, '#pointDetails').delay(delay).animate({
      opacity: '0',
      display: 'none',
    });
  },


  /**
    If a placemark is selected and the details pane is hidden make sure to
    slide out
  */
  handlePlacemarkDetails() {
    const details = FLOW.placemarkDetailController.get('content');

    if (!this.detailsPaneVisible) {
      this.showDetailsPane();
    }
    if (!Ember.empty(details) && details.get('isLoaded')) {
      this.populateDetailsPane(details);
    }
  },


  /**
    Populates the details pane with data from a placemark
  */
  populateDetailsPane(details) {
    this.set('showDetailsBool', true);
    details.forEach((item) => {
      const rawImagePath = item.get('stringValue') || '';
      const verticalBars = rawImagePath.split('|');
      if (verticalBars.length === 4) {
        FLOW.placemarkDetailController.set('selectedPointCode', verticalBars[3]);
      }
    }, this);
  },

});


FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailsView = Ember.View.extend({});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});
