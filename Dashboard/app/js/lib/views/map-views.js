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

        mark.click.addHandler(function(event_name, event_source, event_args) {
          event_source.setInfoBubble('YAY');
          console.log(event_source);
          var pmDetails = FLOW.store.find(FLOW.Placemark, event_source.infoBubble);
          // console.log(pmDetails);

          // var pmDetails = FLOW.store.find(FLOW.PlaceMark, pm.get('id'));
          // var details = FLOW.store.find(FLOW.SurveyedLocale, {communityCode: event_source.infoBubble});
          // var details = FLOW.store.find(FLOW.PlaceMark, event_source.infoBubble);
          // console.log(details);
          // mark.setInfoBubble('YAY');
        });

        map.addMarker(mark, true);

        return this;
      }
    });
  }
});