/*jshint browser:true, jquery:true */
/*global Ember, FLOW */


FLOW.placemarkControl = Ember.ArrayController.create({
  content: null,

  populate: function () {
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
  
  init: function() {
    this._super();
    this.set('content', this.getContent(Ember.ENV.countries));
  },

  getContent: function (countries) {
    var countryList = [];

    countries.sort(function (a, b) {
      if (a.label < b.label) return -1;
      if (a.label > b.label) return 1;
      return 0;
    });

    for (var i = 0; i < countries.length; i++) {
      countryList.push(
        Ember.Object.create({
          label: countries[i].label,
          lat: countries[i].lat,
          lon: countries[i].lon,
          zoom: countries[i].zoom
        })
      );
    }
    return countryList;
  }

});