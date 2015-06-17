FLOW.NavMapsView = FLOW.View.extend({
  templateName: 'navMaps/nav-maps-common',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,
  map: null,
  geomodel: null,

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

  redoMap: function() {
      var n, e, s, w, mapBounds;
      mapBounds = this.map.getBounds();
      // get current bounding box of the visible map
      n = mapBounds.getNorthEast().lat;
      e = mapBounds.getNorthEast().lng;
      s = mapBounds.getSouthWest().lat;
      w = mapBounds.getSouthWest().lng;

      // bound east and west
      e = (e + 3 * 180.0) % (2 * 180.0) - 180.0;
      w = (w + 3 * 180.0) % (2 * 180.0) - 180.0;

      // create bounding box object
      var bb = this.geoModel.create_bounding_box(n, e, s, w);

      // create the best set of geocell box cells which covers
      // the current viewport
      var bestBB = this.geoModel.best_bbox_search_cells(bb);

      // adapt the points shown on the map
      FLOW.placemarkController.adaptMap(bestBB, this.map.getZoom());
    },

  /**
    Once the view is in the DOM create the map
  */
  didInsertElement: function () {
    //console.log(FLOW.Env);

    var self = this;

    if(FLOW.Env.useCartodb){
      mapContent = '<div style="width: 100%">'
      +'<div style="float: left; width: 100%">'
      +'<div style="float: left">'
      +'<label for="survey_selector">Select a survey</label>'
      +'<select class="" name="survey_selector" id="survey_selector">'
      +'<option value="">--All--</option>'
      +'</select>'
      +'</div>'
      +'<!-- <div style="width: 150px; float: left">'
      +'<label for="form_selector">Select a form</label>'
      +'<select class="" name="form_selector" id="form_selector">'
      +'<option value="">--All--</option>'
      +'</select>'
      +'</div> -->'
      +'</div>'
      +'<div style="float: left; width:100%; height: 550px" id="cartodbd_flowMap"></div>'
      +'</div>';

      $("#flowMap").html(mapContent);

      //Define the data layer
      var data_layer;

      $.get("/rest/cartodb/surveys", function(data, status){
    		//console.log(data);
        var rows = [];
        if(data["surveys"].length > 0){
          rows = data["surveys"];
          rows.sort(function(el1, el2){
        		return self.compare(el1, el2, "name")
        	});

          for(var i=0; i<rows.length; i++){
            //append return survey list to the survey selector element
            $("#survey_selector").append('<option value="'+rows[i]["id"]+'">'+rows[i]["name"]+'</option>');
          }
        }
    	});

      // create leaflet map
    	var map = L.map('cartodbd_flowMap', {scrollWheelZoom: false}).setView([-0.703107, 36.765], 2);
    	L.tileLayer('http://{s}.{base}.maps.cit.api.here.com/maptile/2.1/maptile/{mapID}/normal.day.transit/{z}/{x}/{y}/256/png8?app_id={app_id}&app_code={app_code}', {
    		attribution: 'Map &copy; 1987-2014 <a href="http://developer.here.com">HERE</a>',
    		subdomains: '1234',
    		mapID: 'newest',
    		app_id: 'Y8m9dK2brESDPGJPdrvs',
    		app_code: 'dq2MYIvjAotR8tHvY8Q_Dg',
    		base: 'base',
    		maxZoom: 18
    	}).addTo(map);

    	// add cartodb layer with one sublayer
    	cartodb.createLayer(map, {
    			user_name: 'flowaglimmerofhope-hrd',
          type: 'cartodb',
    			sublayers: [{
    		    sql: "SELECT * FROM data_point",
    		    cartocss: '/** simple visualization */#data_point{marker-fill-opacity: 0.9;marker-line-color: #FFF;marker-line-width: 1.5;marker-line-opacity: 1;marker-placement: point;marker-type: ellipse;marker-width: 10;marker-fill: #FF6600;marker-allow-overlap: true;}',
            interactivity: "name, survey_id, id, identifier"
    		  }]
    	},{
    		tiler_domain: "cartodb.akvo.org",
    		tiler_port: "8181",
    		tiler_protocol: "http",
    		no_cdn: true
    	})
    	.addTo(map)
    	.done(function(layer) {
          data_layer = layer.getSubLayer(0);

    			layer.getSubLayer(0).setInteraction(true);
          self.addCursorInteraction(layer);

    			// on mouseover
    			layer.getSubLayer(0).on('featureOver', function(e, pos, pixel, data) {
    					// print data to console log
    					//console.log("Event #" + data.cartodb_id + ", name " + data.name + ", identifier: " + data.identifier);
    			});
          layer.getSubLayer(0).on('featureClick', function(e, latlng, pos, data) {
            //open a popup and pass some clicked point data
            var clicked_point_content = "<table>";

            self.openPopup(map, "identifier: "+data.identifier, latlng);

            self.showDetailsPane();

            /*if($("#form_selector").val() != ""){
              //get all clicked point data
              self.getCartodbPointData("/rest/cartodb/point_data?dataPointId="+data.id+"&formId="+$("#form_selector").val());
            }else{
              //get all clicked point data
              self.getCartodbPointData("/rest/cartodb/answers?dataPointId="+data.id+"&surveyId="+data.survey_id);
            }*/

            self.getCartodbPointData("/rest/cartodb/answers?dataPointId="+data.id+"&surveyId="+data.survey_id);

          });
    	});

      map.on('popupclose', function(e) {
        self.hideDetailsPane();
        $("#pointDetails").html("");
      });

      $( "#survey_selector" ).change(function() {
        if($( "#survey_selector" ).val() == ""){
          LayerActions['all']();
        }else{
          //get a list of forms on a survey
          /*$.get("/rest/cartodb/forms?surveyId="+$( "#survey_selector" ).val(), function(data, status){
        		//console.log(data);
            $("#form_selector option[value!='']").remove();
            var rows = [];
            if(data["forms"].length > 0){
              console.log(data);
              rows = data["forms"];
              rows.sort(function(el1, el2){
            		return self.compare(el1, el2, "name")
            	});

              for(var i=0; i<rows.length; i++){
                //append return survey list to the survey selector element
                $("#form_selector").append('<option value="'+rows[i]["id"]+'">'+rows[i]["name"]+'</option>');
              }
            }
        	});*/
          LayerActions['survey']();
        }
      });

      $( "#form_selector" ).change(function() {
        if($( "#form_selector" ).val() == ""){
          LayerActions['survey']();
        }else{
          //get a list of forms on a survey
          LayerActions['form']();
        }
      });

      var LayerActions = {
      		all: function(){
      			data_layer.setSQL("SELECT * FROM data_point");
      			return true;
      		},
      		survey: function(){
            data_layer.setSQL("SELECT * FROM data_point WHERE survey_id = '"+$( "#survey_selector option:selected" ).val()+"'");
      			return true;
      		},
          form : function(){
            data_layer.setSQL("SELECT data_point.* FROM data_point LEFT JOIN raw_data_"+$( "#form_selector option:selected" ).val()+" ON data_point.id = raw_data_"+$( "#form_selector option:selected" ).val()+".data_point_id");
            return true;
          }
      }
    }else{
      // insert the map
      this.map = L.mapbox.map('flowMap', 'akvo.he30g8mm')
        .setView([-0.703107, 36.765], 2);

      L.control.layers({
        'Terrain': L.mapbox.tileLayer('akvo.he30g8mm').addTo(this.map),
        'Streets': L.mapbox.tileLayer('akvo.he2pdjhk'),
        'Satellite': L.mapbox.tileLayer('akvo.he30neh4')
      }).addTo(this.map);

      // add scale indication to map
      L.control.scale({position:'topleft', maxWidth:150}).addTo(this.map);

      // couple listener to end of zoom or drag
      this.map.on('moveend', function (e) {
        self.redoMap();
      });

      FLOW.placemarkController.set('map', this.map);
      this.geoModel = create_geomodel();

      //load points for the visible map
      this.redoMap();
    }

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
  },

  compare: function (el1, el2, index) {
  	return el1[index] == el2[index] ? 0 : (el1[index] < el2[index] ? -1 : 1);
  },

  addCursorInteraction: function (layer) {
    var hovers = [];

    layer.bind('featureOver', function(e, latlon, pxPos, data, layer) {
      hovers[layer] = 1;
      if(_.any(hovers)) {
        $('#cartodbd_flowMap').css('cursor', 'pointer');
      }
    });

    layer.bind('featureOut', function(m, layer) {
      hovers[layer] = 0;
      if(!_.any(hovers)) {
        $('#cartodbd_flowMap').css({"cursor":"-moz-grabbing","cursor":"-webkit-grabbing"});
      }
    });
  },

  openPopup: function(mapObject, popupContent, latLng){
    L.popup()
    .setLatLng(latLng)
    .setContent(popupContent)
    .openOn(mapObject);
  },

  getCartodbPointData: function(url){
    $("#pointDetails").html("");
    $.get(url, function(point_data, status){
      var clicked_point_content = "";
      console.log(point_data);

      if (point_data['answers'] != null) {
        clicked_point_content += "<table>";
        for (column in point_data['answers']){
          clicked_point_content += "<tr><td><b>"+column+": </b></td>";
          clicked_point_content += "<td>"+point_data['answers'][column]+"</td></tr>";
        }
        clicked_point_content += "</table>";
      }else{
        clicked_point_content += "No details";
      }

      $("#pointDetails").html(clicked_point_content);
    });
  }

});


FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailView = Ember.View.extend({});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});
