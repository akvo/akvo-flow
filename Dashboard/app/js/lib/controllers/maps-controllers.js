FLOW.placemarkControl = Ember.ArrayController.create({
  content: null,

  populate: function() {
    this.set('content', FLOW.store.findAll(FLOW.Placemark));
  }
});

FLOW.placemarkDetailControl = Ember.ArrayController.create({
  content: null,
  selectedDetailImage: null,
  selectedPointCode: null,

  populate: function(placemarkId) {
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
  content: [
  Ember.Object.create({
    label: "Kenya",
    lat:0.010986,
    lon:37.901123
  }), Ember.Object.create({
    label: "Netherlands",
    lat:52.24462,
    lon:5.651611
  })]
});