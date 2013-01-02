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