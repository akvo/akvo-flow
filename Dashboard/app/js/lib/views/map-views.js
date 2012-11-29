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

  putPlacemarksOnMap: function () {
    console.log('ready to put placemarks on map');
    var mapHandler = FLOW.placemarkControl.get('map');
    var htmlContent, marker, pm, point, mark;

    FLOW.placemarkControl.get('content').forEach(function (item) {
        var mapHandler = FLOW.placemarkControl.get('map');
        point = new mxn.LatLonPoint(item.get('latitude'), item.get('longitude'));
        marker = new mxn.Marker(point);
        item.set('marker', marker);
        marker.setLabel('label');
        marker.setIcon('flow15/images/maps/blueMarker.png');

        marker.click.addHandler(function (event_name, event_source, event_args) {
          // Deselect marker
          
          var toggleMarker = function (placeMark, map) {
            // console.log("About to toogle:");
            var newMark = new mxn.Marker(placeMark.marker.location);
            newMark.setLabel('label');
            if (placeMark.marker.iconUrl === 'flow15/images/maps/blueMarker.png') {
              newMark.setIcon('flow15/images/maps/redMarker.png');
            } else {
              newMark.setIcon('flow15/images/maps/blueMarker.png');
            }
            map.removeMarker(placeMark.marker);
            map.addMarker(newMark);
            placeMark.set('marker', newMark);
          };

          var oldSelected = FLOW.placemarkControl.get('selected');
          
          if (typeof oldSelected != 'undefined') {
            toggleMarker(oldSelected, mapHandler);
          }

          toggleMarker(item, mapHandler);
          FLOW.placemarkControl.set('selected', item);
        });
        
        mapHandler.addMarker(marker, false);
    });
  },

  triggerPlacemarks: function () {
    if (FLOW.placemarkControl.content.get('isUpdating') === false) {this.putPlacemarksOnMap();}
  }.observes('FLOW.placemarkControl.content.isUpdating'),

  getDetails: function () {
    var pmId = FLOW.placemarkControl.selected.get('keyId');
    FLOW.placemarkDetailControl.populate(pmId);
    console.log('getting details');
  }.observes('FLOW.placemarkControl.selected'),

  showDetails: function () {
    if (FLOW.placemarkDetailControl.content.get('isLoaded') === true) {
      console.log('details loaded');
      this.set('showDetailsBool', true);
    }
  }.observes('FLOW.placemarkDetailControl.content.isLoaded')

});