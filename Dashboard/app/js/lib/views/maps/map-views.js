/*jshint browser:true, jquery:true, laxbreak:true */
/*global Ember, mxn, FLOW*/
/**
  View that handles map page.
  Definition:
    "placemark" is an FLOW object that represents a single survey point.
    "marker" is a map object that is rendered as a pin. Each marker have
      a placemark counterpart.
**/

FLOW.NavMapsView = Ember.View.extend({
  templateName: 'navMaps/nav-maps',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,


  init: function() {
    this._super();
    this.detailsPaneElements = "#pointDetails h2" +
      ", #pointDetails dl" +
      ", #pointDetails img" +
      ", #pointDetails .imgContainer" +
      ", .placeMarkBasicInfo" +
      ", .noDetails";
    this.detailsPaneVisible = true;
  },


  /**
    Create the map once in DOM
  */
  didInsertElement: function() {
    var map = new mxn.Mapstraction('flowMap', 'google', true),
      latLon = new mxn.LatLonPoint(-0.703107, 36.765747),
      self;

    map.addControls({
      pan: true,
      zoom: 'small',
      map_type: true
    });

    map.setCenterAndZoom(latLon, 2);
    map.enableScrollWheelZoom();
    FLOW.placemarkController.set('map', map);

    self = this;
    this.$('#mapDetailsHideShow').click(function () {
      self.handleShowHideDetails();
    });
    // Slide in detailspane after 1 sec
    this.hideDetailsPane(1000);
  },


  /**

  */
  positionMap: function() {
    var country, latLon, map;

    country = FLOW.countryController.get('country');
    map = FLOW.placemarkController.get('map');
    if (!Ember.none(country)) {
      latLon = new mxn.LatLonPoint(country.get('lat'), country.get('lon'));
      map.getMap().clearOverlays();
      map.setCenterAndZoom(latLon, country.get('zoom'));
    }
  }.observes('FLOW.countryController.country'),


  /**
    Populate the map with markers
  */
  populateMap: function() {
    var map;

    if(FLOW.placemarkController.content.get('isUpdating') === false) {
      map = FLOW.placemarkController.get('map');
      FLOW.placemarkController.get('content').forEach(function(placemark) {
        map.addMarker(this.createMarker(placemark));
      }, this);
    }
  }.observes('FLOW.placemarkController.content.isUpdating'),


  /**

  */
  handleShowHideDetails: function () {
    if (this.detailsPaneVisible) {
      this.hideDetailsPane();
    } else {
      this.showDetailsPane();
    }
  },


  /**
    Handle placemark selection
  */
  handlePlacemarkDetails: function() {
    var details;

    details = FLOW.placemarkDetailController.get('content');

    if (!this.detailsPaneVisible) {
      this.showDetailsPane();
    }
    if (!Ember.empty(details) && details.get('isLoaded')) {
      this.populateDetailsPane(details);
    }
  }.observes('FLOW.placemarkDetailController.content.isLoaded'),


  /**
    Slide in the details pane
  */
  showDetailsPane: function() {
    var button;

    button = this.$('#mapDetailsHideShow');
    button.html('Hide &rsaquo;');
    this.set('detailsPaneVisible', true);

    this.$('#flowMap').animate({
      width: '75%'
    }, 200);
    this.$('#pointDetails').animate({
      width: '24.5%'
    }, 200).css({
      overflow: 'auto',
      marginLeft: '-2px'
    });
    this.$(this.detailsPaneElements, '#pointDetails').animate({
      opacity: '1'
    }, 200).css({
      display: 'inherit'
    });
  },


  /**
    Populates the details pane with data from a placemark
  */
  populateDetailsPane: function (details) {
    var rawImagePath, verticalBars;

    this.set('showDetailsBool', true);
    details.forEach(function(item) {
      rawImagePath = item.get('stringValue');
      verticalBars = rawImagePath.split('|');
      if (verticalBars.length === 4) {
        FLOW.placemarkDetailController.set('selectedPointCode', verticalBars[3]);
      }
    }, this);
  },


  /**
    Slide out details pane
  */
  hideDetailsPane: function(delay) {
    var button;

    delay = typeof delay !== 'undefined' ? delay : 0;
    button = this.$('#mapDetailsHideShow');

    this.set('detailsPaneVisible', false);
    button.html('&lsaquo; Show');

    this.$('#flowMap').delay(delay).animate({
      width: '99.25%'
    }, 200);
    this.$('#pointDetails').delay(delay).animate({
      width: '0.25%'
    }, 200).css({
      overflow: 'scroll-y',
      marginLeft: '-2px'
    });
    this.$(this.detailsPaneElements, '#pointDetails').delay(delay).animate({
      opacity: '0',
      display: 'none'
    });//.css({

    //});
  },


  /**
       Returns a marker(pin on the map) to represent the placemarker
    **/
  createMarker: function(placemark) {
    // Create a marker
    var point = new mxn.LatLonPoint(placemark.get('latitude'),
                                    placemark.get('longitude')),
      marker = new mxn.Marker(point);

    marker.setIcon('/images/maps/blueMarker.png');
    marker.placemark = placemark;

    // Add a click handler that handles what happens when marker is clicked
    placemark.addMarkerClickHandler = function(marker) {
      var clickHandler = function(event_name, event_source, event_args) {
        /*jshint unused: true*/
        event_source.placemark.handleClick(event_source.placemark.marker);
        void(event_args); // Until unused:true is honored by JSHint
      };
      marker.click.addHandler(clickHandler);
    };


    /**
        When a marker is clicked we do different thing depending on
        the state of the map. E.g. if the same marker is clicked we deselect
        that marker and no marker is selected.
      **/
    placemark.handleClick = function(marker) {
      var oldSelected;

      marker.placemark.toggleMarker(marker.placemark);

      oldSelected = FLOW.placemarkController.get('selected');
      if(Ember.none(oldSelected)) {
        FLOW.placemarkController.set('selected', placemark);
      } else {
        if(this.marker === oldSelected.marker) {
          FLOW.placemarkController.set('selected', undefined);
        } else {
          oldSelected.toggleMarker(oldSelected);
          FLOW.placemarkController.set('selected', placemark);
        }
      }
    };


    /**
        Toggle between selected and deselected marker.
        In reality there is no toggle but delete and create
      **/
    placemark.toggleMarker = function(placemark) {
      var map = FLOW.placemarkController.get('map');
      var point = new mxn.LatLonPoint(placemark.get('latitude'),
                                      placemark.get('longitude')),
        newMarker = new mxn.Marker(point);

      if(placemark.marker.iconUrl === ('/images/maps/blueMarker.png')) {
        newMarker.iconUrl = '/images/maps/redMarker.png' ;
      } else {
        newMarker.iconUrl = '/images/maps/blueMarker.png';
      }

      placemark.addMarkerClickHandler(newMarker);
      map.addMarker(newMarker);
      map.removeMarker(placemark.marker);
      newMarker.placemark = placemark;

      placemark.set('marker', newMarker);
    },

    placemark.addMarkerClickHandler(marker, placemark);
    // Attach the new marker to the placemarker object
    placemark.set('marker', marker);
    return marker;
  }

});

FLOW.countryView = Ember.View.extend({
  // country: null
});

FLOW.PlacemarkDetailView = Ember.View.extend({});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});

