/**
  Controllers related to the map tab
  Definition:
    "placemark" is an FLOW object that represents a single survey point.
    "marker" is a map object that is rendered as a pin. Each marker have
      a placemark counterpart.
**/

FLOW.placemarkController = Ember.ArrayController.create({
  content: null,
  map: null,
  overlays: Ember.A(),

  populate: function () {
    this.set('content', FLOW.store.findAll(FLOW.Placemark));
  },

  populateMap: function () {
    var map;
    
    if (this.content.get('isUpdating') === false) {
      this.clearOverlays();
      map = this.get('map');
      this.get('content').forEach(function(placemark) {
        this.addMarker(placemark);
      }, this);
    }
  }.observes('this.content.isUpdating'),

  clearOverlays: function () {
    this.overlays.map(function (marker) {
      marker.setMap(null);
    });
    this.overlays.clear();
  },

  addMarker: function (placemark) {
    var marker, map, coordinate;

    coordinate = new google.maps.LatLng(placemark.get('latitude'),
                                     placemark.get('longitude'));
    map = this.get('map');

    marker = new google.maps.Marker({
      position: coordinate,
      map: map, 
      placemark: placemark,
      icon: '/images/maps/blueMarker.png'
    });

    placemark.addMarkerClickHandler = function (marker) {
      google.maps.event.addListener(marker, 'click', function () {
        marker.placemark.handleClick(marker);
      });
    },

    placemark.handleClick = function (marker) {
      var oldSelected;

      marker.placemark.toggleMarker(marker.placemark);
      oldSelected = FLOW.placemarkController.get('selected');
      if(Ember.none(oldSelected)) {
        // console.log("No previous selection");
        FLOW.placemarkController.set('selected', marker.placemark);
      } else {
        if(this.marker === oldSelected.marker) {
          // console.log("Clicked a selected marker");
          FLOW.placemarkController.set('selected', undefined);
        } else {
          // console.log("Clicked a new marker");
          oldSelected.toggleMarker(oldSelected);
          FLOW.placemarkController.set('selected', marker.placemark);
        }
      }
    };

    placemark.toggleMarker = function (placemark) {
      var coordinate, iconUrl, map, newMarker;

      map = FLOW.placemarkController.get('map');
      coordinate = new google.maps.LatLng(placemark.get('latitude'),
                                     placemark.get('longitude'));

      if(placemark.marker.icon === ('/images/maps/blueMarker.png')) {
        iconUrl = '/images/maps/redMarker.png' ;
      } else {
        iconUrl = '/images/maps/blueMarker.png';
      }
      
      newMarker = new google.maps.Marker({
        position: coordinate,
        map: map, 
        placemark: placemark,
        icon: iconUrl
      });

      placemark.addMarkerClickHandler(newMarker);
      placemark.marker.setMap(null);
      newMarker.placemark = placemark;

      placemark.set('marker', newMarker);
      FLOW.placemarkController.overlays.pushObject(newMarker);

    };

    placemark.addMarkerClickHandler(marker);
    placemark.set('marker', marker);
    this.overlays.pushObject(marker);
    return marker;
  }

});

FLOW.countryController = Ember.ArrayController.create({
  content: [],
  country: null,
  countryCode: null,

  init: function() {
    this._super();
    if ( !Ember.none(FLOW.Env) && !Ember.none(FLOW.Env.countries) ) {
      this.set('content', this.getContent(FLOW.Env.countries));
    }
  },

  handleCountrySelection: function () {
    this.resetMap();
    this.positionMap();
    this.set('countryCode', this.country.get('iso'));
    FLOW.placemarkController.populate(this.country);
  }.observes('this.country'),

  resetMap: function () {
    var selectedMarker;

    selectedMarker = FLOW.placemarkController.get('selected');
    if (!Ember.none(selectedMarker)) {
      selectedMarker.toggleMarker(selectedMarker);
      FLOW.placemarkController.set('selected', null);
    } 
  },

  positionMap: function() {
    var country, map;
  
    country = this.get('country');
    map = FLOW.placemarkController.get('map');

    if (!Ember.none(country)) {
      map.panTo(new google.maps.LatLng(
        country.get('lat'), country.get('lon'))
      );
      map.setZoom(country.get('zoom'));
    }
  },
  

  /**
    Helper function to parse backend countries to countryList
  */
  getContent: function (countries) {
    var countryList = [];

    countries.sort(function (a, b) {
      if (a.name < b.name) return -1;
      if (a.name > b.name) return 1;
      return 0;
    });

    for (var i = 0; i < countries.length; i++) {
      if ( !Ember.none(countries[i].centroidLat) && !Ember.none(countries[i].centroidLon) ) {
        var zoom = 7; // default zoom level
        if (!Ember.none(countries[i].zoomLevel)) {
          zoom = countries[i].zoomLevel;
        }

        countryList.push(
          Ember.Object.create({
            label: countries[i].name,
            iso: countries[i].isoAlpha2Code,
            lat: countries[i].centroidLat,
            lon: countries[i].centroidLon,
            zoom: zoom
          })
        );
      }
    }
    return countryList;
  }

});


FLOW.placemarkDetailController = Ember.ArrayController.create({
  content: Ember.A(),

  populate: function (placemark) {
    if (placemark && placemark.id) {
      this.set('content', FLOW.store.find(FLOW.PlacemarkDetail, {
        placemarkId: placemark.id
      }));
    } else {
      this.set('content', Ember.A());
    }
  },

  handlePlacemarkSelection: function() {
    var selected;
    
    selected = FLOW.placemarkController.get('selected');
    this.populate(selected);
  }.observes('FLOW.placemarkController.selected'),

  photoUrl: function() {
    var photoDetails, photoUrl, rawPhotoUrl;

    if(!this.get('content').get('isLoaded')) {
      return null;
    }

    // filter out details with images
    photoDetails = this.get('content').filter(function (detail) {
      return detail.get('questionType') === 'PHOTO';
    });

    if(Ember.empty(photoDetails)) {
      return null;
    }

    // We only care for the first image
    rawPhotoUrl = photoDetails[0].get('stringValue');
    // Since photos have a leading path from devices that we need to trim
    photoUrl = FLOW.Env.photo_url_root + rawPhotoUrl.split('/').pop();

    return photoUrl;
  }.property('content.isLoaded')

});
