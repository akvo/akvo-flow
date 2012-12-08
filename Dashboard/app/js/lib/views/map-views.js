/*global mxn:false*/

/**
  View that handles map page.
  Definition:
    "placemark" is an FLOW object that represents a single survey point.
    "marker" is a map object that is rendered as a pin. Each marker have
      a placemark counterpart.
**/
FLOW.NavMapsView = Ember.View.extend({
  templateName: "navMaps/nav-maps",
  showDetailsBool: false,
  imageURL: 'flow15/images/invisible.png',

  init: function () {
    this._super();
    FLOW.placemarkControl.populate();
  },

  /**
    ...
  **/
  didInsertElement: function () {
    var map = new mxn.Mapstraction("flowMap", "google", true),
        latLon = new mxn.LatLonPoint(-0.703107, 36.765747);

    map.addControls({
        pan: true,
        zoom: 'small',
        map_type: true
    });

    map.setCenterAndZoom(latLon, 8);
    map.enableScrollWheelZoom();
    FLOW.placemarkControl.set('map', map);
  },

  /**
    Populate the map with markers
  **/
  populateMap: function () {
    var map;
    if (FLOW.placemarkControl.content.get('isUpdating') === false) {
      map = FLOW.placemarkControl.get('map');
      FLOW.placemarkControl.get('content').forEach(function (placemark) {
        map.addMarker(this.createMarker(placemark));
      }, this);
    }
  }.observes('FLOW.placemarkControl.content.isUpdating'),

  /**
    ...
  **/
  handlePlacemarkSelection: function () {
    var selected = FLOW.placemarkControl.get('selected');
    if (typeof selected !== 'undefined') {
      FLOW.placemarkDetailControl.populate(selected.id);
    } else {
      FLOW.placemarkDetailControl.populate(selected);
    }
    this.set('imageURL', 'flow15/images/invisible.png');
  }.observes('FLOW.placemarkControl.selected'),

  /**
    ...
  **/
  handlePlacemarkDetails: function () {
    var content, imageURL, selectedPlacemarkDetail, stringVal;

    content = FLOW.placemarkDetailControl.get('content');

    if ((typeof content !== 'undefined') && (content !== null) && (content.get('isLoaded') === true)) {
      this.set('showDetailsBool', true);
      selectedPlacemarkDetail = FLOW.placemarkDetailControl.get('content');

      selectedPlacemarkDetail.forEach(function (item) {
        stringVal = item.get('stringValue');
        if (stringVal.indexOf('wfpPhoto') != -1) {
          imageURL = 'http://flowdemo.s3.amazonaws.com/images/' + stringVal.slice(stringVal.indexOf('wfpPhoto'));
          this.set('imageURL', imageURL);
        }
      }, this);
    }
  }.observes('FLOW.placemarkDetailControl.content.isLoaded'),

  /**
     Returns a marker(pin on the map) to represent the placemarker
  **/
  createMarker: function (placemark) {
    // Create a marker
    var point = new mxn.LatLonPoint(placemark.get('latitude'), placemark.get('longitude')),
      marker = new mxn.Marker(point);

    marker.setIcon('flow15/images/maps/blueMarker.png');
    marker.placemark = placemark;
    // Add a click handler that handles what happens when marker is clicked
    
    /**
      Adds a custom clickHandler to the markers
    **/
    placemark.addMarkerClickHandler = function (marker, placemark) {
      var clickHandler = function (event_name, event_source, event_args) {
        event_source.placemark.handleClick(event_source.placemark.marker);
      };
      marker.click.addHandler(clickHandler);
    };

    /**
      When a marker is clicked we do different thing depending on
      the state of the map. E.g. if the same marker is clicked we deselect
      that marker and no marker is selected.
    **/
    placemark.handleClick = function (marker) {
      var oldSelected;
      marker.placemark.toggleMarker(marker.placemark);

      // If there was a marker already selected deselect
      oldSelected = FLOW.placemarkControl.get('selected');
      if (typeof oldSelected === 'undefined') {
        // console.log('No old selection');
        FLOW.placemarkControl.set('selected', placemark);
      } else {
        if (this.marker === oldSelected.marker) {
          // console.log('Clicking the same marker');
          FLOW.placemarkControl.set('selected', undefined);
        } else {
          // console.log('Clicking new marker');
          oldSelected.toggleMarker(oldSelected);
          FLOW.placemarkControl.set('selected', placemark);
        }
      }
    };

    /**
      Toggle between selected and deselected marker.
      In reality there is no toggle but delete and create
    **/
    placemark.toggleMarker = function (placemark) {
      var map = FLOW.placemarkControl.get('map');
      var point = new mxn.LatLonPoint(placemark.get('latitude'), placemark.get('longitude')),
        newMarker = new mxn.Marker(point);

      if (placemark.marker.iconUrl === 'flow15/images/maps/blueMarker.png') {
        newMarker.iconUrl = 'flow15/images/maps/redMarker.png';
      } else {
        newMarker.iconUrl = 'flow15/images/maps/blueMarker.png';
      }
    
      placemark.addMarkerClickHandler(newMarker, placemark);
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