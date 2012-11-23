FLOW.NavMapsView = Ember.View.extend({
  templateName: "navMaps/nav-maps",

  init: function () {
    this._super();
    FLOW.NavMapsDetailsView = Ember.View.extend({
      templateName: 'navMaps/nav-maps-marker',

      click: function(evt) {
        if (FLOW.activeMarker.get('marker') !== null) {
          console.log(FLOW.activeMarker.marker.id);
        }
      }

    });
  },

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
    FLOW.placemarksController = Ember.ArrayController.create({
      content: FLOW.store.findAll(FLOW.Placemark),
      selected: null,

      contentArrayDidChange: function (model, index) {
        var htmlContent, marker, pm, point, mark;

        pm = this.objectAt(index);
        point = new mxn.LatLonPoint(pm.get('latitude'), pm.get('longitude'));
        marker = new mxn.Marker(point);
        pm.set('marker', marker);
        
        marker.setLabel(pm.get('collectionDate').toString());
        marker.setInfoBubble(pm.get('id')); // Use the NavMapsDetailsView

        marker.click.addHandler(function(event_name, event_source, event_args) {
          FLOW.placemarksController.set('selected', pm);
        });
        
        map.addMarker(marker, true);
        // return this;
      }
    });


    FLOW.activeMarker = Ember.Object.create({
      marker: null,
      
      init: function () {
        this._super();
        this.set("marker", FLOW.placemarksController.get('selected')); // obviously this does not change aything
      },

      updateMarker: function() {
        // Swap this code to use a new find Placemark detail call
        // var pmDetails = FLOW.store.find(FLOW.Placemark, event_source.infoBubble);
        
        this.set('marker', FLOW.placemarksController.get('selected'));
        if (this.get('marker') !== null) {
          console.log('FLOW.activeMarker: ' + this.get('marker').id);
        }
      }.observes('FLOW.placemarksController.selected')
    });

  }
});