FLOW.NavMapsView = FLOW.View.extend({
  templateName: 'navMaps/nav-maps-common',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,


  init: function () {
    this._super();
    this.detailsPaneElements = "#pointDetails h2" +
      ", #pointDetails dl" +
      ", #pointDetails img" +
      ", #pointDetails .imgContainer" +
      ", .placeMarkBasicInfo" +
      ", .noDetails";
    this.detailsPaneVisible = false;
  },


  /**
    Once the view is in the DOM create the map
  */
  didInsertElement: function () {
	var map, self, geoModel;

	// insert the map
	map = L.mapbox.map('flowMap', 'akvo.he30g8mm')
    .setView([-0.703107, 36.765], 2);

	L.control.layers({
		 'Terrain': L.mapbox.tileLayer('akvo.he30g8mm').addTo(map),
		'Streets': L.mapbox.tileLayer('akvo.he2pdjhk'),
	    'Satellite': L.mapbox.tileLayer('akvo.he30neh4'),
	}).addTo(map);

	// couple listener to end of zoom or drag
	map.on('moveend', function(e) {
	  redoMap();
	});
	FLOW.placemarkController.set('map', map);
	geoModel = create_geomodel();

	//load points for the visible map
	redoMap();

	function redoMap () {
	  // get current bounding box of the visible map
	  n = map.getBounds().getNorthEast().lat;
	  e = map.getBounds().getNorthEast().lng;
	  s = map.getBounds().getSouthWest().lat;
	  w = map.getBounds().getSouthWest().lng;

	  // bound east and west
	  e = (e + 3*180.0) % (2*180.0) - 180.0;
	  w = (w + 3*180.0) % (2*180.0) - 180.0;

	  // create bounding box object
	  var bb = geoModel.create_bounding_box(n,e,s,w);

	  // create the best set of geocell box cells which covers
	  // the current viewport
	  var bestBB = geoModel.best_bbox_search_cells(bb);

	  // adapt the points shown on the map
	  FLOW.placemarkController.adaptMap(bestBB,map.getZoom());
	}

    self = this;
    this.$('#mapDetailsHideShow').click(function () {
      self.handleShowHideDetails();
    });
    // Slide in detailspane after 1 sec
    this.hideDetailsPane(1000);
  },


  /**
    Helper function to dispatch to either hide or show details pane
  */
  handleShowHideDetails: function () {
    if (this.detailsPaneVisible) {
      this.hideDetailsPane();
    } else {
      this.showDetailsPane();
    }
  },

  /**
    Slide in the details pane
  */
  showDetailsPane: function () {
    var button;

    button = this.$('#mapDetailsHideShow');
    button.html('Hide &rsaquo;');
    this.set('detailsPaneVisible', true);

    this.$('#flowMap').animate({
      width: '75%'
    }, 200);
    this.$('#pointDetails').animate({
      width: '24.5%'
    }, 200).css({
      overflow: 'auto',
      marginLeft: '-2px'
    });
    this.$(this.detailsPaneElements, '#pointDetails').animate({
      opacity: '1'
    }, 200).css({
      display: 'inherit'
    });
  },


  /**
    Slide out details pane
  */
  hideDetailsPane: function (delay) {
    var button;

    delay = typeof delay !== 'undefined' ? delay : 0;
    button = this.$('#mapDetailsHideShow');

    this.set('detailsPaneVisible', false);
    button.html('&lsaquo; Show');

    this.$('#flowMap').delay(delay).animate({
      width: '99.25%'
    }, 200);
    this.$('#pointDetails').delay(delay).animate({
      width: '0.25%'
    }, 200).css({
      overflow: 'scroll-y',
      marginLeft: '-2px'
    });
    this.$(this.detailsPaneElements, '#pointDetails').delay(delay).animate({
      opacity: '0',
      display: 'none'
    });
  },


  /**
    If a placemark is selected and the details pane is hidden make sure to
    slide out
  */
  handlePlacemarkDetails: function () {
    var details;

    details = FLOW.placemarkDetailController.get('content');

    if (!this.detailsPaneVisible) {
      this.showDetailsPane();
    }
    if (!Ember.empty(details) && details.get('isLoaded')) {
      this.populateDetailsPane(details);
    }
  }.observes('FLOW.placemarkDetailController.content.isLoaded'),


  /**
    Populates the details pane with data from a placemark
  */
  populateDetailsPane: function (details) {
    var rawImagePath, verticalBars;

    this.set('showDetailsBool', true);
    details.forEach(function (item) {
      rawImagePath = item.get('stringValue');
      verticalBars = rawImagePath.split('|');
      if (verticalBars.length === 4) {
        FLOW.placemarkDetailController.set('selectedPointCode',
          verticalBars[3]);
      }
    }, this);
  }

});


FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailView = Ember.View.extend({});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});
