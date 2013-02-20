/*jshint browser:true, jquery:true */
/*global Ember, FLOW */


FLOW.placemarkController = Ember.ArrayController.create({
  content: null,
  countryCode: null,

  populate: function (country) {
    FLOW.countryController.set('countryCode', country.get('iso'));
    this.set('content', FLOW.store.findAll(FLOW.Placemark));
  }

});


FLOW.placemarkDetailController = Ember.ArrayController.create({
  content: null,
  selectedPointCode: null,
  photo: null,

  populate: function (placemarkId) {
    if(typeof placemarkId === 'undefined') {
      this.set('content', null);
    } else {
      this.set('content', FLOW.store.find(FLOW.PlacemarkDetail, {
        "placemarkId": placemarkId
      }));
    }
  },

  contentDidChange: function() {
    if (this.get('content') && this.get('content').isLoaded) {
      if (Ember.empty(this.get('content'))) {
        this.set('photo', null);
      } else {
        this.set('photo', this.getPhotoUrl());
      }
    }
  }.observes('content.isLoaded'),

  getPhotoUrl: function() {
    var photoDetails, photoUrl, rawPhotoUrl;

    // filter out details with images
    photoDetails = this.get('content').filter(function (detail) {
      return detail.get('stringValue').match( /(.jpeg|.JPEG|.jpg|.JPG|.png|.PNG|.gif|.GIF)/ );
    });
    // We can only handle one image
    rawPhotoUrl = photoDetails[0].get('stringValue');
    // Since photos have a leading path from devices that we need to trim
    photoUrl = FLOW.Env.photo_url_root +
      rawPhotoUrl.slice(rawPhotoUrl.indexOf('wfpPhoto'));

    return photoUrl;
  }

});


FLOW.countryController = Ember.Object.create({
  content: null,
  country: null,
  
  init: function() {
    this._super();
    if ( !Ember.none(FLOW.Env) && !Ember.none(FLOW.Env.countries) ) {
      this.set('content', this.getContent(FLOW.Env.countries));
    }
  },

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