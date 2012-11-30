FLOW.NavMapsView = Ember.View.extend({
  templateName: "navMaps/nav-maps",
  showDetailsBool: false,

  PlacemarkDetailsView:Ember.View.extend({

  }),

  init: function () {
    this._super();
    FLOW.placemarkControl.populate();
  },


  didInsertElement: function () {
    var map = new mxn.Mapstraction("flowMap", "google", true),
        latlon = new mxn.LatLonPoint(-0.703107, 36.765747);

    map.addControls({
        pan: true,
        zoom: 'small',
        map_type: true
    });

    map.setCenterAndZoom(latlon, 8);
    map.enableScrollWheelZoom();
    FLOW.placemarkControl.set('map', map);
  },

  // Returns a marker(pin on the map) to represent the placemarker
  createMarker: function (placemark) {
    // Create a marker
    var point = new mxn.LatLonPoint(placemark.get('latitude'), placemark.get('longitude')),
      marker = new mxn.Marker(point);

    marker.setIcon('flow15/images/maps/blueMarker.png');
    marker.placemark = placemark;
    // Add a click handler that handles what happens when marker is clicked
    
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

    placemark.manageClick = function (marker) {
      // Handle new click
      marker.placemark.toggleMarker(marker.placemark);

      // If there was a marker already selected deselect
      var oldSelected = FLOW.placemarkControl.get('selected');
      if (typeof oldSelected === 'undefined') {
        console.log('No old selection');
        FLOW.placemarkControl.set('selected', placemark);
      } else {
        if (this.marker === oldSelected.marker) {
          console.log('Clicking the same marker');
          FLOW.placemarkControl.set('selected', undefined);
        } else {
          console.log('Clicking new marker');
          oldSelected.toggleMarker(oldSelected);
          FLOW.placemarkControl.set('selected', placemark);
        }
      }

    };

    placemark.addMarkerClickHandler = function (marker, placemark) {
      var clickHandler = function (event_name, event_source, event_args) {
        event_source.placemark.manageClick(event_source.placemark.marker);
      };
      marker.click.addHandler(clickHandler);
    };

    placemark.addMarkerClickHandler(marker, placemark);
    // Attach the new marker to the placemarker object
    placemark.set('marker', marker);

    return marker;
  },


  putPlacemarksOnMap: function () {
    var map = FLOW.placemarkControl.get('map');
    FLOW.placemarkControl.get('content').forEach(function (placemark) {
      var marker = this.createMarker(placemark);
      map.addMarker(marker);
    }, this);
  },

  triggerPlacemarks: function () {
    if (FLOW.placemarkControl.content.get('isUpdating') === false) {this.putPlacemarksOnMap();}
  }.observes('FLOW.placemarkControl.content.isUpdating'),

  getDetails: function () {
    // console.log('getting details');
    var selected = FLOW.placemarkControl.get('selected');
    if (typeof selected !== 'undefined') {
      FLOW.placemarkDetailControl.populate(selected.id);
    } else {
      FLOW.placemarkDetailControl.populate(selected);
    }
  }.observes('FLOW.placemarkControl.selected'),

  showDetails: function () {
    // console.log('details loaded');
    var content = FLOW.placemarkDetailControl.get('content');
    if ((typeof content !== 'undefined') && (content !== null)) {
      if (content.get('isLoaded') === true) {
        this.set('showDetailsBool', true);
      }

      var imageString=null;
      if (FLOW.placemarkDetailControl.content.get('isLoaded') === true) {
        this.set('showDetailsBool', true);

        var containsImage = FLOW.store.filter(FLOW.PlacemarkDetail,function(data){
        var stringVal=data.get('stringValue');
        if (stringVal.indexOf('wfpPhoto') != -1) {return true;} else {return false;}
        });

        imageObj = containsImage.get('firstObject');
        imageString = imageObj.get('stringValue'); 
        imageURL = 'http://flowdemo.s3.amazonaws.com/images/' + imageString.slice(imageString.indexOf('wfpPhoto'));
  
        console.log(imageURL);
        // over to you Daniel!
      }
    }
  }.observes('FLOW.placemarkDetailControl.content.isLoaded')

});