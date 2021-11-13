/* eslint-disable new-cap */
import observe from '../../mixins/observe';
import template from '../../mixins/template';

const { create_geomodel } = require('../../../plugins/geocells');

FLOW.NavMapsView = FLOW.View.extend(template('navMaps/nav-maps-common'), observe({
  'FLOW.router.mapsController.selectedMarker': 'handlePlacemarkDetails',
  'FLOW.selectedControl.selectedSurvey': 'surveySelection',
  'FLOW.selectedControl.selectedSurveyGroup': 'surveyGroupSelection',
  'this.detailsPaneVisible': 'detailsPaneShowHide',
}), {
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,
  map: null,
  polygons: [],
  mapZoomLevel: 0,
  mapCenter: null,
  mediaMarkers: {},
  selectedMediaMarker: {},
  mediaMarkerSelected: {},
  geoModel: null,
  showSurveyFilters: true, // show filters when user is logged in

  init() {
    this._super();
    this.detailsPaneElements = '#pointDetails h2'
      + ', #pointDetails dl'
      + ', #pointDetails img'
      + ', #pointDetails .imgContainer'
      + ', .placeMarkBasicInfo'
      + ', .noDetails';
  },

  /**
    Once the view is in the DOM create the map
  */
  didInsertElement() {
    const self = this;

    if (FLOW.Env.mapsProvider === 'google') {
      self.insertGoogleMap();
    } else {
      self.insertMapboxMap();
    }
    // couple listener to end of zoom or drag
    this.map.on('moveend', () => {
      self.redoMap();
    });
    FLOW.router.mapsController.set('map', this.map);
    this.geoModel = create_geomodel();

    // add scale indication to map
    L.control.scale({ position: 'topleft', maxWidth: 150 }).addTo(this.map);

    this.$('#mapDetailsHideShow').click(() => {
      self.toggleProperty('detailsPaneVisible');
    });

    self.set('detailsPaneVisible', false);

    self.detailsPanelListeners();
  },

  insertGoogleMap() {
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
  },

  insertMapboxMap() {

    L.mapbox.accessToken = 'pk.eyJ1IjoiYWt2byIsImEiOiJzUFVwR3pJIn0.8dLa4fHG19fBwwBUJMDOSQ';

    const layers = {
      Streets: L.mapbox.styleLayer('mapbox://styles/mapbox/streets-v11'),
      Outdoors: L.mapbox.styleLayer('mapbox://styles/mapbox/outdoors-v11'),
      Satellite: L.mapbox.styleLayer('mapbox://styles/mapbox/satellite-v9'),
    };

    this.map = L.mapbox.map('flowMap')
    .setView([-0.703107, 36.765], 2);

    layers.Streets.addTo(this.map);
    L.control.layers(layers).addTo(this.map);
  },

  detailsPanelListeners() {
    const self = this;
    $(document.body).on('click', '.project-geoshape', function () {
      if (self.polygons.length > 0) {
        $(this).html(Ember.String.loc('_project_onto_main_map'));
        for (let i = 0; i < self.polygons.length; i++) {
          self.map.removeLayer(self.polygons[i]);
        }
        // restore the previous zoom level and map center
        self.map.setZoom(self.mapZoomLevel);
        self.map.panTo(self.mapCenter);
        self.polygons = [];
      } else {
        $(this).html(Ember.String.loc('_clear_geoshape_from_main_map'));
        self.projectGeoshape($(this).data('geoshape-object'));
      }
    });

    $(document.body).on('mouseover', '.media', function () {
      const mediaObject = $(this).data('coordinates');
      const mediaMarkerIcon = new L.Icon({
        iconUrl: 'images/media-marker.png',
        iconSize: [11, 11],
      });
      const selectedMediaMarkerIcon = new L.Icon({
        iconUrl: 'images/media-marker-selected.png',
        iconSize: [11, 11],
      });
      if (mediaObject !== '') {
        const filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf('/') + 1).split('.')[0];
        const mediaCoordinates = [mediaObject.location.latitude, mediaObject.location.longitude];
        if (!(filename in self.mediaMarkers)) {
          self.mediaMarkers[filename] = new L.marker(mediaCoordinates, { icon: mediaMarkerIcon }).addTo(self.map); // eslint-disable-line new-cap, max-len
        } else {
          self.selectedMediaMarker[filename] = new L.marker(mediaCoordinates, { icon: selectedMediaMarkerIcon }).addTo(self.map); // eslint-disable-line new-cap, max-len
        }
      }
    });

    $(document.body).on('mouseout', '.media', function () {
      const mediaObject = $(this).data('coordinates');
      if (mediaObject !== '') {
        const filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf('/') + 1).split('.')[0];
        if (filename in self.mediaMarkers && !(filename in self.mediaMarkerSelected)) {
          self.map.removeLayer(self.mediaMarkers[filename]);
          delete self.mediaMarkers[filename];
        } else {
          self.map.removeLayer(self.selectedMediaMarker[filename]);
          delete self.selectedMediaMarker[filename];
        }
      }
    });

    $(document.body).on('click', '.media-location', function () {
      const mediaObject = $(this).data('coordinates');
      const mediaMarkerIcon = new L.Icon({
        iconUrl: 'images/media-marker.png',
        iconSize: [11, 11],
      });
      if (mediaObject !== '') {
        const filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf('/') + 1).split('.')[0];
        const mediaCoordinates = [mediaObject.location.latitude, mediaObject.location.longitude];
        if (!(filename in self.mediaMarkerSelected)) {
          $(this).html(Ember.String.loc('_hide_photo_on_map'));
          self.mediaMarkers[filename] = new L.marker(mediaCoordinates, { icon: mediaMarkerIcon }).addTo(self.map); // eslint-disable-line new-cap, max-len
          self.mediaMarkerSelected[filename] = true;
        } else {
          $(this).html(Ember.String.loc('_show_photo_on_map'));
          self.map.removeLayer(self.mediaMarkers[filename]);
          delete self.mediaMarkers[filename];
          delete self.mediaMarkerSelected[filename];
        }
      }
    });
  },

  /**
    If a placemark is selected and the details pane is hidden make sure to
    slide out
  */
  handlePlacemarkDetails() {
    this.showDetailsPane();
  },

  // function to project geoshape from details panel to main map canvas
  projectGeoshape(geoShapeObject) {
    const points = [];
    let geoShape;

    // before fitting the geoshape to map, get the current
    // zoom level and map center first and save them
    this.mapZoomLevel = this.map.getZoom();
    this.mapCenter = this.map.getCenter();

    let geoshapeCoordinatesArray;
    const geoShapeObjectType = geoShapeObject.features[0].geometry.type;
    if (geoShapeObjectType === 'Polygon') {
      geoshapeCoordinatesArray = geoShapeObject.features[0].geometry.coordinates[0];
    } else {
      geoshapeCoordinatesArray = geoShapeObject.features[0].geometry.coordinates;
    }

    for (let j = 0; j < geoshapeCoordinatesArray.length; j++) {
      points.push([geoshapeCoordinatesArray[j][1], geoshapeCoordinatesArray[j][0]]);
    }

    if (geoShapeObjectType === 'Polygon') {
      geoShape = L.polygon(points).addTo(this.map);
    } else if (geoShapeObjectType === 'MultiPoint') {
      const geoShapeMarkersArray = [];
      for (let i = 0; i < points.length; i++) {
        geoShapeMarkersArray.push(L.marker([points[i][0], points[i][1]]));
      }
      geoShape = L.featureGroup(geoShapeMarkersArray).addTo(this.map);
    } else if (geoShapeObjectType === 'LineString') {
      geoShape = L.polyline(points).addTo(this.map);
    }
    this.map.fitBounds(geoShape.getBounds());
    this.polygons.push(geoShape);
  },

  surveySelection() {
    this.clearMap('form-selection');
  },

  surveyGroupSelection() {
    this.clearMap('survey-selection');
    this.redoMap();
  },

  clearMap(trigger) {
    FLOW.router.mapsController.clearMarker();
    this.set('detailsPaneVisible', false);
    if (!Ember.empty(FLOW.router.mapsController.allPlacemarks) && trigger == 'survey-selection') {
      FLOW.router.mapsController.allPlacemarks.clearLayers();
    }
  },

  redoMap() {
    if (!FLOW.selectedControl.selectedSurveyGroup) {
      return;
    }

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

  detailsPaneShowHide() {
    const display = this.detailsPaneVisible;

    this.$('#mapDetailsHideShow').html(`&lsaquo; ${Ember.String.loc((display) ? '_hide' : '_show')}`);

    this.$('#flowMap').animate({
      width: (display) ? '75%' : '99.25%',
    }, 200);
    this.$('#pointDetails').animate({
      width: (display) ? '24.5%' : '0.25%',
    }, 200).css({
      overflow: (display) ? 'auto' : 'scroll-y',
      marginLeft: '-2px',
    });
    this.$(this.detailsPaneElements, '#pointDetails').animate({
      opacity: (display) ? '1' : '0',
      display: (display) ? 'inherit' : 'none',
    });
  },

  showDetailsPane() {
    if (!this.detailsPaneVisible) {
      this.set('detailsPaneVisible', true);
    }
  },
});

FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailsView = FLOW.View.extend({});

FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});

FLOW.GeoshapeMapView = FLOW.View.extend(template('navMaps/geoshape-map'), {
  geoshape: null,

  didInsertElement() {
    this.set('geoshape', JSON.parse(this.get('parentView.geoShapeObject')));
    if (this.get('isPolygon') || this.get('isLineString') || this.get('isMultiPoint')) {
      const containerNode = this.get('element').getElementsByClassName('geoshapeMapContainer')[0];
      containerNode.innerHTML = '';
      if (containerNode) {
        FLOW.drawGeoShape(containerNode, this.get('geoshape'));
      }
    }
  },

  length: Ember.computed(function () {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.length;
  }).property('this.geoshape'),

  area: Ember.computed(function () {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.area;
  }).property('this.geoshape'),

  pointCount: Ember.computed(function () {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.pointCount;
  }).property('this.geoshape'),

  isPolygon: Ember.computed(function () {
    const geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    }
    return geoshape.features.length > 0
        && geoshape.features[0].geometry.type === 'Polygon';
  }).property('this.geoshape'),

  isLineString: Ember.computed(function () {
    const geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    }
    return geoshape.features.length > 0
        && geoshape.features[0].geometry.type === 'LineString';
  }).property('this.geoshape'),

  isMultiPoint: Ember.computed(function () {
    const geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    }
    return geoshape.features.length > 0 && geoshape.features[0].geometry.type === 'MultiPoint';
  }).property('this.geoshape'),

  geoshapeString: Ember.computed(function () {
    return this.geoshape === null ? null : JSON.stringify(this.geoshape);
  }).property('this.geoshape'),
});
