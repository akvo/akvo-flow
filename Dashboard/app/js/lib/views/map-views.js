FLOW.NavMapsView = Ember.View.extend({
  templateName: "navMaps/nav-maps",
  didInsertElement: function() {
    var map = new mxn.Mapstraction("flowMap", "google"),
        latlon = new mxn.LatLonPoint(-0.703107, 36.765747);

    map.addControls({
        pan: true,
        zoom: 'small',
        map_type: true
    });

    map.setCenterAndZoom(latlon, 8);
    map.enableScrollWheelZoom();

    // Markers
    Ember.ArrayController.create({
      content: FLOW.store.findAll(FLOW.Placemark),
      contentArrayDidChange: function (model, index) {
        var htmlContent, marker, pm, point, mark;

        pm = this.objectAt(index);
        point = new mxn.LatLonPoint(pm.get('latitude'), pm.get('longitude'));
        mark = new mxn.Marker(point);

        mark.setLabel(pm.get('collectionDate').toString());
        mark.setInfoBubble(pm.get('id'));

        map.addMarker(mark, true);

        return this;
      }
    });
  }
});