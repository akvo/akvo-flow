/*jshint browser:true, jquery:true */
/*global Ember, FLOW */


FLOW.placemarkControl = Ember.ArrayController.create({
  content: null,
  countryCode: null,

  populate: function (country) {
    FLOW.countryControl.set('countryCode', country.get('iso'));
    this.set('content', FLOW.store.findAll(FLOW.Placemark));
  }

});


FLOW.placemarkDetailControl = Ember.ArrayController.create({
  content: null,
  selectedDetailImage: null,
  selectedPointCode: null,

  populate: function (placemarkId) {
    if(typeof placemarkId === 'undefined') {
      this.set('content', null);
    } else {
      this.set('content', FLOW.store.find(FLOW.PlacemarkDetail, {
        "placemarkId": placemarkId
      }));
    }
  }

});


FLOW.countryControl = Ember.Object.create({
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

    // -------- Fixture start
    console.log('Adding fixture countries for coutry selector');
    countryList.push(
      Ember.Object.create({
        label: "Holland",
        iso: "NL",
        lat: 52.2500,
        lon: 4.6670,
        zoom: 7
      })
    );

    countryList.push(
      Ember.Object.create({
        label: "Kenya",
        iso: "KE",
        lat: -1.26103461,
        lon: 36.74724467,
        zoom: 7
      })
    );

    return countryList;
    // -------- Fixture end


    // countries.sort(function (a, b) {
    //   if (a.name < b.name) return -1;
    //   if (a.name > b.name) return 1;
    //   return 0;
    // });

    // for (var i = 0; i < countries.length; i++) {
    //   if ( !Ember.none(countries[i].centroidLat) && !Ember.none(countries[i].centroidLon) ) {
    //     var zoom = 8; // default zoom level
    //     if (Ember.none(countries[i].zoomLevel)) {
    //       zoom = countries[i].zoomLevel;
    //     }
        
    //     countryList.push(
    //       Ember.Object.create({
    //         label: countries[i].name,
    //         iso: countries[i].isoAlpha2Code,
    //         lat: countries[i].centroidLat,
    //         lon: countries[i].centroidLon,
    //         zoom: zoom
    //       })
    //     );
    //   }
    // }
    // return countryList;
  }

});