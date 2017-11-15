FLOW.NavMapsView = FLOW.View.extend({
  templateName: 'navMaps/nav-maps-common',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,
  map: null,
  marker: null,
  polygons: [],
  mapZoomLevel: 0,
  mapCenter: null,
  mediaMarkers: {},
  selectedMediaMarker: {},
  mediaMarkerSelected: {},
  geoModel: null,
  selectedSurvey: null,
  allowFilters: FLOW.Env.mapsProvider && FLOW.Env.mapsProvider === 'cartodb',

  init: function () {
    this._super();
    this.detailsPaneElements = "#pointDetails h2" +
      ", #pointDetails dl" +
      ", #pointDetails img" +
      ", #pointDetails .imgContainer" +
      ", .placeMarkBasicInfo" +
      ", .noDetails";
  },

  redoMap: function() {
      var n, e, s, w, mapBounds;
      mapBounds = this.map.getBounds();
      // get current bounding box of the visible map
      n = mapBounds.getNorthEast().lat;
      e = mapBounds.getNorthEast().lng;
      s = mapBounds.getSouthWest().lat;
      w = mapBounds.getSouthWest().lng;

      // bound east and west
      e = (e + 3 * 180.0) % (2 * 180.0) - 180.0;
      w = (w + 3 * 180.0) % (2 * 180.0) - 180.0;

      // create bounding box object
      var bb = this.geoModel.create_bounding_box(n, e, s, w);

      // create the best set of geocell box cells which covers
      // the current viewport
      var bestBB = this.geoModel.best_bbox_search_cells(bb);

      // adapt the points shown on the map
      FLOW.mapsController.adaptMap(bestBB, this.map.getZoom());
    },

  /**
    Once the view is in the DOM create the map
  */
  didInsertElement: function () {
    var self = this;

    if (FLOW.Env.mapsProvider === 'cartodb') {
      self.insertCartodbMap();
    } else {
      if (FLOW.Env.mapsProvider === 'google') {
        self.insertGoogleMap();
      } else {
        self.insertMapboxMap();
      }
      // couple listener to end of zoom or drag
      this.map.on('moveend', function (e) {
        self.redoMap();
      });
      FLOW.mapsController.set('map', this.map);
      this.geoModel = create_geomodel();
      //load points for the visible map
      this.redoMap();
    }

    // add scale indication to map
    L.control.scale({position:'topleft', maxWidth:150}).addTo(this.map);

    this.$('#mapDetailsHideShow').click(function () {
      self.toggleProperty('detailsPaneVisible');
    });

    self.set('detailsPaneVisible', false);

    self.detailsPanelListeners();
  },

  insertGoogleMap: function () {
    this.map = new L.Map('flowMap', {center: new L.LatLng(-0.703107, 36.765), zoom: 2});
    var roadmap = new L.Google("ROADMAP");
    var terrain = new L.Google('TERRAIN');
    var satellite = new L.Google('SATELLITE');
    this.map.addLayer(roadmap);
    this.map.addControl(new L.Control.Layers({
      'Roadmap': roadmap,
      'Satellite': satellite,
      'Terrain': terrain
    }, {}));
  },

  insertMapboxMap: function() {
      var options = {
          minZoom: 2,
          maxZoom: 18
      };
    this.map = L.mapbox.map('flowMap', 'akvo.he30g8mm', options).setView([-0.703107, 36.765], 2);
    L.control.layers({
      'Terrain': L.mapbox.tileLayer('akvo.he30g8mm').addTo(this.map),
      'Streets': L.mapbox.tileLayer('akvo.he2pdjhk'),
      'Satellite': L.mapbox.tileLayer('akvo.he30neh4')
    }).addTo(this.map);
  },

  insertCartodbMap: function() {
    var self = this;

    $.ajaxSetup({
    	beforeSend: function(){
    		FLOW.savingMessageControl.numLoadingChange(1);
        },
    	complete: function(){
    		FLOW.savingMessageControl.numLoadingChange(-1);
        }
    });

    this.map = L.map('flowMap', {scrollWheelZoom: true}).setView([26.11598592533351, 1.9335937499999998], 2);

    var bounds = new L.LatLngBounds(this.map.getBounds().getSouthWest(), this.map.getBounds().getNorthEast());

    this.map.options.maxBoundsViscosity = 1.0;
    this.map.options.maxBounds = bounds;
    this.map.options.maxZoom = 18;
    this.map.options.minZoom = 2;

    var hereAttr = 'Map &copy; 1987-2014 <a href="http://developer.here.com">HERE</a>',
			hereUrl = 'https://{s}.{base}.maps.cit.api.here.com/maptile/2.1/maptile/{mapID}/{scheme}/{z}/{x}/{y}/256/{format}?app_id={app_id}&app_code={app_code}',
      mbAttr = 'Map &copy; <a href="http://openstreetmap.org">OSM</a>',
      mbUrl = 'http://{s}.tiles.mapbox.com/v3/akvo.he30g8mm/{z}/{x}/{y}.png';

    var normal = L.tileLayer(hereUrl, {
      scheme: 'normal.day.transit',
      format: 'png8',
      attribution: hereAttr,
      subdomains: '1234',
      mapID: 'newest',
      app_id: FLOW.Env.hereMapsAppId,
      app_code: FLOW.Env.hereMapsAppCode,
      base: 'base'
    }).addTo(this.map),
    terrain  = L.tileLayer(mbUrl, {
      attribution: mbAttr,
      subdomains: 'abc'
    }),
    satellite  = L.tileLayer(hereUrl, {
      scheme: 'hybrid.day',
      format: 'jpg',
      attribution: hereAttr,
      subdomains: '1234',
      mapID: 'newest',
      app_id: FLOW.Env.hereMapsAppId,
      app_code: FLOW.Env.hereMapsAppCode,
      base: 'aerial'
    });

    var baseLayers = {
			"Normal": normal,
            "Terrain": terrain,
			"Satellite": satellite
		};

    FLOW.addExtraMapBoxTileLayer(baseLayers);

    L.control.layers(baseLayers).addTo(this.map);

    FLOW.mapsController.set('map', this.map);

    this.map.on('click', function(e) {
      self.clearMap(); //remove any previously loaded point data
    });

    this.map.on('zoomend', function() {
      $('body, html, #flowMap').scrollTop(0);
    });
  },

  detailsPanelListeners: function(){
      var self = this;
      $(document.body).on('click', '.project-geoshape', function(){
        if(self.polygons.length > 0){
          $(this).html(Ember.String.loc('_project_onto_main_map'));
          for(var i=0; i<self.polygons.length; i++){
            self.map.removeLayer(self.polygons[i]);
          }
          //restore the previous zoom level and map center
          self.map.setZoom(self.mapZoomLevel);
          self.map.panTo(self.mapCenter);
          self.polygons = [];
        }else{
          $(this).html(Ember.String.loc('_clear_geoshape_from_main_map'));
          self.projectGeoshape($(this).data('geoshape-object'));
        }
      });

      $(document.body).on('mouseover', '.media', function(){
        var mediaObject = $(this).data('coordinates');
        var mediaMarkerIcon = new L.Icon({
          iconUrl: 'images/media-marker.png',
          iconSize: [11, 11]
        }), selectedMediaMarkerIcon = new L.Icon({
          iconUrl: 'images/media-marker-selected.png',
          iconSize: [11, 11]
        });
        if(mediaObject !== '') {
          var filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf("/")+1).split(".")[0];
          var mediaCoordinates = [mediaObject['location']['latitude'], mediaObject['location']['longitude']];
          if(!(filename in self.mediaMarkers)) {
            self.mediaMarkers[filename] = new L.marker(mediaCoordinates, {icon: mediaMarkerIcon}).addTo(self.map);
          } else {
            self.selectedMediaMarker[filename] = new L.marker(mediaCoordinates, {icon: selectedMediaMarkerIcon}).addTo(self.map);
          }
        }
      });

      $(document.body).on('mouseout', '.media', function(){
        var mediaObject = $(this).data('coordinates');
        if(mediaObject !== '') {
          var filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf("/")+1).split(".")[0];
          if(filename in self.mediaMarkers && !(filename in self.mediaMarkerSelected)) {
            self.map.removeLayer(self.mediaMarkers[filename]);
            delete self.mediaMarkers[filename];
          } else {
            self.map.removeLayer(self.selectedMediaMarker[filename]);
            delete self.selectedMediaMarker[filename];
          }
        }
      });

      $(document.body).on('click', '.media-location', function(){
        var mediaObject = $(this).data('coordinates');
        var mediaMarkerIcon = new L.Icon({
          iconUrl: 'images/media-marker.png',
          iconSize: [11, 11]
        });
        if(mediaObject !== '') {
          var filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf("/")+1).split(".")[0];
          var mediaCoordinates = [mediaObject['location']['latitude'], mediaObject['location']['longitude']];
          if(!(filename in self.mediaMarkerSelected)) {
            $(this).html(Ember.String.loc('_hide_photo_on_map'));
            self.mediaMarkers[filename] = new L.marker(mediaCoordinates, {icon: mediaMarkerIcon}).addTo(self.map);
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

  surveySelection: function () {
      this.clearMap();
      FLOW.mapsController.clearSurveyDataLayer();
      if (!Ember.none(this.get('selectedSurvey'))) {
          FLOW.mapsController.loadNamedMap(this.selectedSurvey.get('keyId'));
      }
  }.observes('this.selectedSurvey'),

  surveyGroupSelection: function () {
      this.clearMap();
      FLOW.mapsController.clearSurveyDataLayer();
  }.observes('FLOW.selectedControl.selectedSurveyGroup'),

  /**
    If a placemark is selected and the details pane is hidden make sure to
    slide out
  */
  handlePlacemarkDetails: function () {
    var details = FLOW.placemarkDetailController.get('content');

    this.showDetailsPane();
    if (!Ember.empty(details) && details.get('isLoaded')) {
      this.populateDetailsPane(details);
    }
  }.observes('FLOW.placemarkDetailController.content.isLoaded'),

  /**
    Populates the details pane with data from a placemark
  */
  populateDetailsPane: function (details) {
    var rawImagePath, verticalBars;

    this.set('showDetailsBool', true);
    details.forEach(function (item) {
      rawImagePath = item.get('stringValue') || '';
      verticalBars = rawImagePath.split('|');
      if (verticalBars.length === 4) {
        FLOW.placemarkDetailController.set('selectedPointCode',
          verticalBars[3]);
      }
    }, this);
  },

  //function to project geoshape from details panel to main map canvas
  projectGeoshape: function(geoShapeObject){
    var points = [], geoShape;

    //before fitting the geoshape to map, get the current
    //zoom level and map center first and save them
    this.mapZoomLevel = this.map.getZoom();
    this.mapCenter = this.map.getCenter();

    var geoshapeCoordinatesArray, geoShapeObjectType = geoShapeObject['features'][0]['geometry']['type'];
    if(geoShapeObjectType === "Polygon"){
      geoshapeCoordinatesArray = geoShapeObject['features'][0]['geometry']['coordinates'][0];
    } else {
      geoshapeCoordinatesArray = geoShapeObject['features'][0]['geometry']['coordinates'];
    }

    for(var j=0; j<geoshapeCoordinatesArray.length; j++){
      points.push([geoshapeCoordinatesArray[j][1], geoshapeCoordinatesArray[j][0]]);
    }

    if(geoShapeObjectType === "Polygon"){
      geoShape = L.polygon(points).addTo(this.map);
    }else if (geoShapeObjectType === "MultiPoint") {
      var geoShapeMarkersArray = [];
      for (var i = 0; i < points.length; i++) {
        geoShapeMarkersArray.push(L.marker([points[i][0],points[i][1]]));
      }
      geoShape = L.featureGroup(geoShapeMarkersArray).addTo(this.map);
    }else if (geoShapeObjectType === "LineString") {
      geoShape = L.polyline(points).addTo(this.map);
    }
    this.map.fitBounds(geoShape.getBounds());
    this.polygons.push(geoShape);
  },

  clearMap: function() {
    var self = this;
    self.set('detailsPaneVisible', false);
    if (self.marker) {
      self.map.removeLayer(self.marker);
    }

    if (!$.isEmptyObject(self.mediaMarkers)) {
      for (mediaMarker in self.mediaMarkers) {
        self.map.removeLayer(self.mediaMarkers[mediaMarker]);
      }
    }

    if (self.polygons.length > 0) {
      for (var i=0; i<self.polygons.length; i++) {
        self.map.removeLayer(self.polygons[i])
      }
      //restore the previous zoom level and map center
      self.map.setView(self.mapCenter, self.mapZoomLevel);
      self.polygons = [];
    }
  },

  /*Place a marker to highlight clicked point of layer on cartodb map*/
  placeMarker: function(latlng){
      //if there's a previously loaded marker, first remove it
      if (this.marker) {
          this.map.removeLayer(this.marker);
      }

      var markerIcon = new L.Icon({
          iconUrl: 'images/marker.svg',
          iconSize: [10, 10]
      });
      this.marker = new L.marker(FLOW.mapsController.get('markerCoordinates'), {icon: markerIcon});
      this.map.addLayer(this.marker);

      this.showDetailsPane();
  }.observes('FLOW.mapsController.markerCoordinates'),

  detailsPaneShowHide: function(){
      var button = this.$('#mapDetailsHideShow');
      var display = this.detailsPaneVisible;

      button.html('&lsaquo; ' + Ember.String.loc((display) ? '_hide' : '_show'));

      this.$('#flowMap').animate({
        width: (display) ? '75%' : '99.25%'
      }, 200);
      this.$('#pointDetails').animate({
        width: (display) ? '24.5%' : '0.25%'
      }, 200).css({
        overflow: (display) ? 'auto' : 'scroll-y',
        marginLeft: '-2px'
      });
      this.$(this.detailsPaneElements, '#pointDetails').animate({
        opacity: (display) ? '1' : '0',
        display: (display) ? 'inherit' : 'none'
      });
  }.observes('this.detailsPaneVisible'),

  showDetailsPane: function(){
      if (!this.detailsPaneVisible) {
        this.set('detailsPaneVisible', true);
      }
  }
});

FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailView = Ember.View.extend({
    cartoMaps: FLOW.Env.mapsProvider && FLOW.Env.mapsProvider === 'cartodb'
});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});

FLOW.GeoshapeMapView = FLOW.View.extend({
  templateName: 'navMaps/geoshape-map',
  geoshape: null,

  didInsertElement: function() {
    this.set('geoshape', JSON.parse(this.get('parentView.geoShapeObject')));
    if (this.get('isPolygon') || this.get('isLineString') || this.get('isMultiPoint')) {
      var containerNode = this.get('element').getElementsByClassName('geoshapeMapContainer')[0];
      containerNode.innerHTML = "";
      if (containerNode) {
        FLOW.drawGeoShape(containerNode, this.get('geoshape'));
      }
    }
  },

  length: function() {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.length
  }.property('this.geoshape'),

  area: function() {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.area
  }.property('this.geoshape'),

  pointCount: function() {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.pointCount
  }.property('this.geoshape'),

  isPolygon: function() {
    var geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    } else {
      return geoshape['features'].length > 0 &&
        geoshape['features'][0]["geometry"]["type"] === "Polygon"
    }
  }.property('this.geoshape'),

  isLineString: function() {
    var geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    } else {
      return geoshape['features'].length > 0 &&
        geoshape['features'][0]["geometry"]["type"] === "LineString"
    }
  }.property('this.geoshape'),

  isMultiPoint: function() {
    var geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    } else {
      return geoshape['features'].length > 0 &&
        geoshape['features'][0]["geometry"]["type"] === "MultiPoint"
    }
  }.property('this.geoshape'),

  geoshapeString: function() {
    return this.geoshape === null ? null : JSON.stringify(this.geoshape);
  }.property('this.geoshape')
});
