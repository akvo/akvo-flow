FLOW.NavMapsView = FLOW.View.extend({
  templateName: 'navMaps/nav-maps-common',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,
  map: null,
  marker: null,
  geomodel: null,
  cartodbLayer: null,
  layerExistsCheck: 0,

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
    var self = this;

    if(FLOW.Env.mapsProvider === 'google'){
      this.map = new L.Map('flowMap', {center: new L.LatLng(-0.703107, 36.765), zoom: 2});
      var roadmap = new L.Google("ROADMAP");
      var terrain = new L.Google('TERRAIN');
      var satellite = new L.Google('SATELLITE');
      this.map.addLayer(roadmap);
      this.map.addControl(new L.Control.Layers({
        'Roadmap': roadmap,
        'Satellite': satellite,
        'Terrain': terrain
      }, {}));
    }else if(FLOW.Env.mapsProvider === 'cartodb'){
      filterContent = '<select style="float: left" class="" name="survey_selector" id="survey_selector">'
      +'<option value="">--'+Ember.String.loc('_choose_a_survey') +'--</option>'
      +'</select>&nbsp;'
      +'<select style="float: left" class="" name="form_selector" id="form_selector">'
      +'<option value="">--'+Ember.String.loc('_choose_a_form') +'--</option>'
      +'</select>&nbsp;';

      $("#dropdown-holder").prepend(filterContent);
      $("#dropdown-holder").append("<div style='clear: both'></div>");

      //Define the data layer
      var data_layer;

      $.get("/rest/cartodb/surveys", function(data, status){
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
    	map = L.map('flowMap', {scrollWheelZoom: true}).setView([26.11598592533351, 1.9335937499999998], 3);

      bounds = new L.LatLngBounds(map.getBounds().getSouthWest(), map.getBounds().getNorthEast());

      map.options.maxBoundsViscosity = 1.0;
      map.options.maxBounds = bounds;
      map.options.maxZoom = 18;
      map.options.minZoom = 2;

      L.tileLayer('https://{s}.{base}.maps.cit.api.here.com/maptile/2.1/maptile/{mapID}/normal.day.transit/{z}/{x}/{y}/256/png8?app_id={app_id}&app_code={app_code}', {
    		attribution: 'Map &copy; 1987-2014 <a href="http://developer.here.com">HERE</a>',
    		subdomains: '1234',
    		mapID: 'newest',
    		app_id: 'r5lmMPKxiMeZzkTWRAJu',
    		app_code: 'W6i_Oej7Y8IdgizMp7eSyQ',
    		base: 'base',
        noWrap: true
    	}).addTo(map);

      this.map = map;

      //get list of named maps
    	$.get("/rest/cartodb/named_maps", function(data, status){
    		if(data.template_ids){
    			namedMapCheck = 0;
    			for(var i=0; i<data['template_ids'].length; i++){
    				//check in there is a full "data_point" table named map
    				if(data['template_ids'][i] == "data_point"){
    					//named map already exists
    					namedMapCheck++;
    				}
    			}

    			//if named map exists
    			if(namedMapCheck>0){
    				//overlay named map
    				self.createLayer(map, "data_point", "");
    			}else{
    				self.namedMaps(
              map,
              "data_point",
              "data_point",
              "SELECT * FROM data_point",
              ["name", "survey_id", "id", "identifier", "lat", "lon"]);
    			}
    		}
    	});

      map.on('click', function(e){
        if(self.marker != null){
          self.map.removeLayer(self.marker);
          self.hideDetailsPane();
          $("#pointDetails").html("<p class=\"noDetails\">"+Ember.String.loc('_no_details') +"</p>");
        }
      });

      $( "#survey_selector" ).change(function() {
        $("#form_selector option[value!='']").remove();
      	$("#question_selector option[value!='']").remove();

      	if($( "#survey_selector" ).val() != ""){
      		//get list of forms in selected survey
          $.get("/rest/cartodb/forms?surveyId="+$( "#survey_selector" ).val(), function(data, status){
        		var rows = [];
        		if( data["forms"] && data["forms"].length > 0){
        			rows = data["forms"];
        			rows.sort(function(el1, el2){
        				return self.compare(el1, el2, "name")
        			});

        			for(var i=0; i<rows.length; i++){
        				//append return survey list to the survey selector element
        				$("#form_selector").append('<option value="'+rows[i]["id"]+'">'+rows[i]["name"]+'</option>');
        			}
        		}
        	});

      		//get list of named maps
      		$.get("/rest/cartodb/named_maps", function(data, status){
      			if(data.template_ids){
      				namedMapCheck = 0;
      				for(var i=0; i<data['template_ids'].length; i++){
      					//check if there is a selectd survey's named map
      					if(data['template_ids'][i] == "data_point_"+$( "#survey_selector" ).val()){
      						//named map already exists
      						namedMapCheck++;
      					}
      				}

      				//if named map exists
      				if(namedMapCheck>0){
      					//overlay named map
      					self.createLayer(map, "data_point_"+$( "#survey_selector" ).val(), "");
      				}else{
                //create named map
      					self.namedMaps(
                  map,
                  "data_point_"+$( "#survey_selector" ).val(),
                  "data_point",
                  "SELECT * FROM data_point WHERE survey_id="+$( "#survey_selector" ).val(),
                  ["name", "survey_id", "id", "identifier", "lat", "lon"]);
      				}
      			}
      		});
      	}else{
      		self.createLayer(map, "data_point", "");
      	}
      });

      $( "#form_selector" ).change(function() {
        $("#question_selector option[value!='']").remove();

      	if($( "#form_selector" ).val() != ""){
      		//get named maps
      		$.get("/rest/cartodb/named_maps", function(data, status){
      			if(data.template_ids){
      				namedMapCheck = 0;
      				for(var i=0; i<data['template_ids'].length; i++){
      					if(data['template_ids'][i] == "raw_data_"+$( "#form_selector" ).val()){
      						//named map already exists
      						namedMapCheck++;
      					}
      				}

      				//if named map exists
      				if(namedMapCheck>0){
      					//overlay named map
      					self.createLayer(map, "raw_data_"+$( "#form_selector" ).val(), "");
      				}else{
                //get list of columns to be added to new named map's interactivity
      					$.get("/rest/cartodb/columns?form_id="+$( "#form_selector" ).val(), function(columnsData) {
      						var interactivity = [];

      						if(columnsData.column_names){
      							for(var j=0; j<columnsData['column_names'].length; j++){
      								interactivity.push(columnsData['column_names'][j]['column_name']);
      							}
      						}

                  //create named map
      						self.namedMaps(
                    map,
                    "raw_data_"+$( "#form_selector" ).val(),
                    "raw_data_"+$( "#form_selector" ).val(),
                    "SELECT * FROM raw_data_"+$( "#form_selector" ).val(),
                    interactivity);
      				    });
      				}
      			}
      		});
      	}else{
      		self.createLayer(map, "data_point_"+$( "#survey_selector" ).val(), "");
      	}
      });

    }else{
      // insert the map
      this.map = L.mapbox.map('flowMap', 'akvo.he30g8mm')
        .setView([-0.703107, 36.765], 2);

      L.control.layers({
        'Terrain': L.mapbox.tileLayer('akvo.he30g8mm').addTo(this.map),
        'Streets': L.mapbox.tileLayer('akvo.he2pdjhk'),
        'Satellite': L.mapbox.tileLayer('akvo.he30neh4')
      }).addTo(this.map);

      // couple listener to end of zoom or drag
      this.map.on('moveend', function (e) {
        self.redoMap();
      });

      FLOW.placemarkController.set('map', this.map);
      this.geoModel = create_geomodel();

      //load points for the visible map
      this.redoMap();
    }

    // add scale indication to map
    L.control.scale({position:'topleft', maxWidth:150}).addTo(this.map);

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

  /*Place a marker to highlight clicked point of layer on cartodb map*/
  placeMarker: function(latlng){
    markerIcon = new L.Icon({
      iconUrl: 'images/marker.svg',
      iconSize: [10, 10]
    });
    this.marker = new L.marker(latlng, {icon: markerIcon});
    this.map.addLayer(this.marker);
  },

  //create named maps
  namedMaps: function(map, mapName, table, sql, interactivity){
    var self = this;

  	//style of points for new layer
    cartocss = "#"+table+"{"
  		+"marker-fill-opacity: 0.9;"
  		+"marker-line-color: #FFF;"
  		+"marker-line-width: 1.5;"
  		+"marker-line-opacity: 1;"
  		+"marker-placement: point;"
  		+"marker-type: ellipse;"
  		+"marker-width: 10;"
  		+"marker-fill: #FF6600;"
  		+"marker-allow-overlap: true;"
  		+"}";

  	var configJsonData = {};
  	configJsonData['interactivity'] = interactivity;
  	configJsonData['name'] = mapName;
  	configJsonData['cartocss'] = cartocss;
  	configJsonData['sql'] = sql;

    $.ajax({
      type: 'POST',
      contentType: "application/json",
      url: '/rest/cartodb/named_maps',
      data: JSON.stringify(configJsonData), //turns out you need to stringify the payload before sending it
      dataType: 'json',
      success: function(namedMapData){
        if(namedMapData.template_id){
          self.createLayer(map, mapName, "");
        }
      }
    });
  },

  /*this function overlays a named map on the cartodb map*/
  createLayer: function(map, mapName, interactivity){
    var self = this, pointDataUrl;

  	if(self.layerExistsCheck == 1){
  		map.removeLayer(self.cartodbLayer);
  	}

  	// add cartodb layer with one sublayer
  	cartodb.createLayer(map, {
  		user_name: FLOW.Env.appId,
  		type: 'namedmap',
  		named_map: {
  			name: mapName,
  			layers: [{
  				layer_name: "t",
  				interactivity: "id"
  			}]
  		}
  	},{
  		tiler_domain: FLOW.Env.cartodbHost,
  		tiler_port: "", //set to empty string to stop cartodb js from appending default port
  		tiler_protocol: "https",
  		no_cdn: true
  	})
  	.addTo(map)
  	.done(function(layer) {
      self.layerExistsCheck = 1;
  		self.cartodbLayer = layer;

  		self.addCursorInteraction(layer);

  		current_layer = layer.getSubLayer(0);
  		current_layer.setInteraction(true);

  		current_layer.on('featureClick', function(e, latlng, pos, data) {
        if(self.marker != null){
          self.map.removeLayer(self.marker);
        }
        self.placeMarker([data.lat, data.lon]);

  			self.showDetailsPane();
        if($("#form_selector").val() == ""){
          pointDataUrl = "/rest/cartodb/answers?dataPointId="+data.id+"&surveyId="+data.survey_id;
          self.getCartodbPointData(pointDataUrl, data.name, data.identifier);
        }else{
          pointDataUrl = "/rest/cartodb/raw_data?dataPointId="+data.data_point_id+"&formId="+$("#form_selector").val();
          $.get("/rest/cartodb/data_point?id="+data.data_point_id, function(pointData, status){
            self.getCartodbPointData(pointDataUrl, pointData['row']['name'], pointData['row']['identifier']);
          });
        }
  		});
  	});
  },

  /*function is required to manage how the cursor appears on the cartodb map canvas*/
  addCursorInteraction: function (layer) {
    var hovers = [];

    layer.bind('featureOver', function(e, latlon, pxPos, data, layer) {
      hovers[layer] = 1;
      if(_.any(hovers)) {
        $('#flowMap').css('cursor', 'pointer');
      }
    });

    layer.bind('featureOut', function(m, layer) {
      hovers[layer] = 0;
      if(!_.any(hovers)) {
        $('#flowMap').css({"cursor":"-moz-grabbing","cursor":"-webkit-grabbing"});
      }
    });
  },

  getCartodbPointData: function(url, dataPointName, dataPointIdentifier){
    self= this;
    $("#pointDetails").html("");
    $.get(url, function(pointData, status){
      var clickedPointContent = "";

      if (pointData['answers'] != null) {
        //get request for questions
        $.get(
      			"/rest/cartodb/questions?form_id="+pointData['formId'],
      			function(questionsData, status){
              geoshapeCheck = 0;
              geoshapeCoordinates = "";
      				clickedPointContent += "<ul class=\"placeMarkBasicInfo floats-in\">"
              +"<h3>"
              +((dataPointName != "" && dataPointName != "null" && dataPointName != null) ? dataPointName : "")
              +"</h3>"
              +"<li>"
              +"<span>"+Ember.String.loc('_data_point_id') +":</span>"
              +"<div style=\"display: inline; margin: 0 0 0 5px;\">"+dataPointIdentifier+"</div>"
              +"</li>"
              +"<li>"
              +"<span>"+Ember.String.loc('_collected_on') +":</span>"
              +"<div class=\"placeMarkCollectionDate\">"
              +pointData['answers']['created_at']
              +"</div></li><li></li></ul>";
              //clickedPointContent += "<table>";
              clickedPointContent += "<dl class=\"floats-in\" style=\"opacity: 1; display: inherit;\">";
              for (column in pointData['answers']){
                for(var i=0; i<questionsData['questions'].length; i++){
                  if (column.match(questionsData['questions'][i].id)) {
                    clickedPointContent += "<div class=\"defListWrap\"><dt>"+questionsData['questions'][i].display_text+"&nbsp;</dt>";

                    //if question is of type, photo load a html image element
                    if(questionsData['questions'][i].type == "PHOTO"){
                      image = "<div class=\":imgContainer photoUrl:shown:hidden\">";
                      if(pointData['answers'][column] != null){
                        image_filename = FLOW.Env.photo_url_root+pointData['answers'][column].substring(pointData['answers'][column].lastIndexOf("/")+1);
                        image += "<a href=\""+image_filename+"\" target=\"_blank\">"
                        +"<img src=\""+image_filename+"\" alt=\"\"/></a>";
                      }
                      image +"</div>";
                      clickedPointContent += "<dd>"+image+"</dd></div>";
                    }else{
                      clickedPointContent += "<dd>"+pointData['answers'][column]+"</dd></div>";
                      /*//if point is a geoshape, draw the shape in the side window
                      if(questionsData['questions'][i].type == "GEOSHAPE"){
                        geoshapeCheck = 1;
                        geoshape_object = JSON.parse(pointData['answers'][column]);
                        if(geoshape_object['features'].length > 0){
                          geoshapeCoordinates = geoshape_object['features'][0]['geometry']['coordinates'][0];
                        }
                        clickedPointContent += "<dd><div id=\"geoShapeMap\" style=\"width:100%; height: 100px\"></div></dd></div>";
                      }else{
                        clickedPointContent += "<dd>"+pointData['answers'][column]+"</dd></div>";
                      }*/
                    }
                  }
                }
              }
              clickedPointContent += "</dl>";
              $("#pointDetails").html(clickedPointContent);

              //if there's geoshape, draw it
              if(geoshapeCheck === 1){
                //self.createGeoshape(geoshapeCoordinates);
              }
      			});
      }else{
        clickedPointContent += "<p class=\"noDetails\">"+Ember.String.loc('_no_details') +"</p>";
        $("#pointDetails").html(clickedPointContent);
      }
    });
  },

  createGeoshape: function(points){
    var getCentroid = function (arr) {
      return arr.reduce(function (x,y) {
        return [x[0] + y[0]/arr.length, x[1] + y[1]/arr.length]
      }, [0,0])
    }

    center = getCentroid(points);

    map = L.map('geoShapeMap', {scrollWheelZoom: false}).setView(center, 2);
    L.tileLayer('https://{s}.{base}.maps.cit.api.here.com/maptile/2.1/maptile/{mapID}/normal.day.transit/{z}/{x}/{y}/256/png8?app_id={app_id}&app_code={app_code}', {
      attribution: '<a href="http://developer.here.com">HERE</a>',
      subdomains: '1234',
      mapID: 'newest',
      app_id: 'r5lmMPKxiMeZzkTWRAJu',
      app_code: 'W6i_Oej7Y8IdgizMp7eSyQ',
      base: 'base',
      maxZoom: 18
    }).addTo(map);

    geoshape = L.polygon(points);

    geoshape.addTo(map);

    southWest = geoshape.getBounds().getSouthWest();
    northEast = geoshape.getBounds().getNorthEast();
    bounds = new L.LatLngBounds(southWest, northEast);

    map.fitBounds(bounds);
  }

});


FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailView = Ember.View.extend({});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});
