/*jshint browser:true, jquery:true */
/*global Ember, FLOW */


FLOW.placemarkController = Ember.ArrayController.create({
  content: null,

  // We might be able to remove the buildURL in the REST adapter
  // and use this populate().
  // populate: function (country) {
  //   this.set('content', FLOW.store.findQuery(FLOW.Placemark,
  //     {country: country.get('iso')}));
  // }

  populate: function (country) {
    FLOW.countryController.set('countryCode', country.get('iso'));
    this.set('content', FLOW.store.findAll(FLOW.Placemark));
  }
});


FLOW.placemarkDetailController = Ember.ArrayController.create({
  content: Ember.A(),
  selectedPointCode: null,
  photoVisible: 'hidden',

  populate: function (placemarkId) {
    if(typeof placemarkId === 'undefined') {
      this.set('content', Ember.A());
    } else {
      this.set('content', FLOW.store.find(FLOW.PlacemarkDetail, {
        placemarkId: placemarkId
      }));
    }
  },

  photoUrl: function() {
    var photoDetails, photoUrl, rawPhotoUrl;


    if(!this.get('content').get('isLoaded')) {
      // this.set('photoVisible', 'hidden');
      return 'images/invisible.png';
    } else {
      this.set('photoVisible', 'shown');
    }
    
    // filter out details with images
    photoDetails = this.get('content').filter(function (detail) {
      return detail.get('questionType') === 'PHOTO';
    });

    if(Ember.empty(photoDetails)) {
      this.set('photoVisible', 'hidden');
      return 'images/invisible.png';
    }
    
    // We only care for the first image
    rawPhotoUrl = photoDetails[0].get('stringValue');
    // Since photos have a leading path from devices that we need to trim
    photoUrl = FLOW.Env.photo_url_root + rawPhotoUrl.split('/').pop();

    return photoUrl;
  }.property('content.isLoaded')

});

FLOW.placemarkDetailPhotoController = Ember.ObjectController.create({
  photo: null
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


  /**

  */
  handleCountrySelection: function () {
    FLOW.placemarkController.populate(this.country);
  }.observes('this.country'),


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