FLOW.NavMapsView = FLOW.View.extend({
  templateName: 'navMaps/nav-maps-common',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,
  map: null,
  geomodel: null,
  cartodb_layer: null,
  layer_exists_check: 0,

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

    if(FLOW.Env.useCartodb && typeof cartodb != 'undefined'){
      mapContent = '<div style="width: 100%">'
      +'<div style="float: left; width: 100%">'
      +'<div style="float: left">'
      +'<label for="survey_selector">Select a survey</label>'
      +'<select class="" name="survey_selector" id="survey_selector">'
      +'<option value="">--All--</option>'
      +'</select>&nbsp;'
      +'</div>'
      +'<div style="float: left">'
      +'<label for="form_selector">Select a form</label>'
      +'<select class="" name="form_selector" id="form_selector">'
      +'<option value="">--All--</option>'
      +'</select>&nbsp;'
      +'</div>'
      +/*'<div style="float: left">'
      +'<label for="question_selector">Select a question to style the map by</label>'
      +'<select class="" name="question_selector" id="question_selector">'
      +'<option value="">--All--</option>'
      +'</select>&nbsp;'
      +'</div>'
      +'<button style="float: left" id="update_style">Update Points Style</button>'
      +*/'</div>'
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
    		app_id: 'r5lmMPKxiMeZzkTWRAJu',
    		app_code: 'W6i_Oej7Y8IdgizMp7eSyQ',
    		base: 'base',
    		maxZoom: 18
    	}).addTo(map);

      //get list of named maps
    	$.get("/rest/cartodb/named_maps", function(data, status){
    		if(data.template_ids){
    			named_map_check = 0;
    			for(var i=0; i<data['template_ids'].length; i++){
    				//check in there is a full "data_point" table named map
    				if(data['template_ids'][i] == "data_point"){
    					//named map already exists
    					named_map_check++;
    				}
    			}

    			//if named map exists
    			if(named_map_check>0){
    				//overlay named map
    				self.create_layer(map, "data_point", "");
    			}else{
    				self.named_maps(
              map,
              "data_point",
              "data_point",
              "SELECT * FROM data_point",
              ["name", "survey_id", "id", "identifier"]);
    			}
    		}
    	});

      map.on('popupclose', function(e) {
        self.hideDetailsPane();
        $("#pointDetails").html("<p class=\"noDetails\">No details</p>");
      });

      $( "#survey_selector" ).change(function() {
        $("#form_selector option[value!='']").remove();
      	$("#question_selector option[value!='']").remove();

      	if($( "#survey_selector" ).val() != ""){
      		//get list of forms in selected survey
          $.get("/rest/cartodb/forms?surveyId="+$( "#survey_selector" ).val(), function(data, status){
        		//console.log(data);
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
      				named_map_check = 0;
      				for(var i=0; i<data['template_ids'].length; i++){
      					//check if there is a selectd survey's named map
      					if(data['template_ids'][i] == "data_point_"+$( "#survey_selector" ).val()){
      						//named map already exists
      						named_map_check++;
      					}
      				}

      				//if named map exists
      				if(named_map_check>0){
      					//overlay named map
      					self.create_layer(map, "data_point_"+$( "#survey_selector" ).val(), "");
      				}else{
      					self.named_maps(
                  map,
                  "data_point_"+$( "#survey_selector" ).val(),
                  "data_point",
                  "SELECT * FROM data_point WHERE survey_id="+$( "#survey_selector" ).val(),
                  ["name", "survey_id", "id", "identifier"]);
      				}
      			}
      		});
      	}else{
      		self.create_layer(map, "data_point", "");
      	}
      });

      $( "#form_selector" ).change(function() {
        $("#question_selector option[value!='']").remove();

      	if($( "#form_selector" ).val() != ""){
      		//get named maps
      		$.get("/rest/cartodb/named_maps", function(data, status){
      			if(data.template_ids){
      				named_map_check = 0;
      				for(var i=0; i<data['template_ids'].length; i++){
      					if(data['template_ids'][i] == "raw_data_"+$( "#form_selector" ).val()){
      						//named map already exists
      						named_map_check++;
      					}
      					//console.log(data['template_ids'][i]);
      				}

      				//if named map exists
      				if(named_map_check>0){
      					//overlay named map
      					self.create_layer(map, "raw_data_"+$( "#form_selector" ).val(), "");
      				}else{
      					$.get("/rest/cartodb/columns?form_id="+$( "#form_selector" ).val(), function(columns_data) {
      						var interactivity = [];

      						if(columns_data.column_names){
      							for(var j=0; j<columns_data['column_names'].length; j++){
      								interactivity.push(columns_data['column_names'][j]['column_name']);
      							}
      						}

      						self.named_maps(
                    map,
                    "raw_data_"+$( "#form_selector" ).val(),
                    "raw_data_"+$( "#form_selector" ).val(),
                    "SELECT * FROM raw_data_"+$( "#form_selector" ).val(),
                    interactivity);
      				    });
      				}
      			}
      		});

      		self.load_questions($( "#form_selector" ).val());
      	}else{
      		self.create_layer(map, "data_point_"+$( "#survey_selector" ).val(), "");
      	}
      });

      $("#update_style").click(function(){
        if($( "#question_selector" ).val() != ""){
          $.get("/rest/cartodb/distinct?question_name="+$( "#question_selector" ).val()+"&form_id="+$( "#form_selector" ).val(), function(data, status){
      			distinct_values = data['distinct_values'];
      			cartocss = "#raw_data_"+$( "#form_selector" ).val()+"{"
      				+"marker-fill-opacity: 0.9;"
      				+"marker-line-color: #FFF;"
      				+"marker-line-width: 1.5;"
      				+"marker-line-opacity: 1;"
      				+"marker-placement: point;"
      				+"marker-type: ellipse;"
      				+"marker-width: 10;"
      				+"marker-allow-overlap: true;"
      				+"}";
      			for(var i=0; i<distinct_values.length; i++){
      				cartocss += "#raw_data_"+$( "#form_selector" ).val()+"["+$( "#question_selector" ).val()+"=\""+distinct_values[i][$( "#question_selector" ).val()]+"\"]";
      				cartocss += "{";
      				//cartocss += "marker-fill: #"+Math.random().toString(16).slice(2, 8);
      				cartocss += "marker-fill: #"+(Math.random()*0xFFFFFF<<0).toString(16)+";";
      				cartocss += "}";
      			}
            console.log(cartocss);

        		$.get("/rest/cartodb/columns?form_id="+$( "#form_selector" ).val(), function(columns_data) {
        			var interactivity = [];

        			if(columns_data.column_names){
        				for(var j=0; j<columns_data['column_names'].length; j++){
        					interactivity.push(columns_data['column_names'][j]['column_name']);
        				}
        			}

        			var config_json_data = {};
        			config_json_data['interactivity'] = interactivity;
        			config_json_data['name'] = 'raw_data_'+$( "#form_selector" ).val();
        			config_json_data['cartocss'] = cartocss;
        			config_json_data['sql'] = "SELECT * FROM raw_data_"+$( "#form_selector" ).val();

        			//console.log(JSON.stringify(config_json_data));

        			//edit named maps
              //place endpoint for editing named maps to be provided by Jonas here
              $.ajax({
                type: 'POST',
                contentType: "application/json",
                url: '/rest/cartodb/update_map',
                data: JSON.stringify(config_json_data), //turns out you need to stringify the payload before sending it
                dataType: 'json',
                success: function(named_map_data){
                  console.log(named_map_data);
                  if(named_map_data.template_id){
                    self.create_layer(map, "raw_data_"+$( "#form_selector" ).val(), "");
                  }
                }
              });
        		});
      		});
      	}else{
      		alert("No question selected to style by");
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

  openPopup: function(mapObject, popupContent, latLng){
    L.popup()
    .setLatLng(latLng)
    .setContent(popupContent)
    .openOn(mapObject);
  },

  load_questions: function(form_id){
  	$.get("/rest/cartodb/questions?form_id="+form_id, function(questions_data, status){
  		var questions_rows = [];
  		if(questions_data['questions'] && questions_data['questions'].length){
  			questions_rows = questions_data['questions'];
  			$.get("/rest/cartodb/columns?form_id="+form_id, function(columns_data, status){
  				var column_rows = [];
  				if(columns_data['column_names'] && columns_data['column_names'].length){
  					column_rows = columns_data['column_names'];
  					for(var i=0; i<column_rows.length; i++){
  						for(var j=0; j<questions_rows.length; j++){
  							//check if column name has question id
  							if(column_rows[i]['column_name'].includes(questions_rows[j]['id']) && questions_rows[j]['type'] == 'OPTION'){
  								$("#question_selector").append('<option value="'+column_rows[i]['column_name']+'">'+questions_rows[j]['display_text']+'</option>');
  							}
  						}
  					}
  				}
  			});
  		}
  	});
  },

  //create named maps
  named_maps: function(map, map_name, table, sql, interactivity){
    var self = this;

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

  	var config_json_data = {};
  	config_json_data['interactivity'] = interactivity;
  	config_json_data['name'] = map_name;
  	config_json_data['cartocss'] = cartocss;
  	config_json_data['sql'] = sql;

    console.log(JSON.stringify(config_json_data));

  	/*$.post(
  			"/rest/cartodb/named_maps",
  			config_json_data,
  			function(named_map_data, status){
  				console.log(named_map_data);
  				if(named_map_data.template_id){
  					self.create_layer(map, map_name, "");
  				}
  			});*/

    $.ajax({
      type: 'POST',
      contentType: "application/json",
      url: '/rest/cartodb/named_maps',
      data: JSON.stringify(config_json_data), //turns out you need to stringify the payload before sending it
      dataType: 'json',
      success: function(named_map_data){
        console.log(named_map_data);
        if(named_map_data.template_id){
          self.create_layer(map, map_name, "");
        }
      }
    });
  },

  create_layer: function(map, map_name, interactivity){
    var self = this, point_data_url;

  	if(self.layer_exists_check == 1){
  		map.removeLayer(self.cartodb_layer);
  	}

  	// add cartodb layer with one sublayer
  	cartodb.createLayer(map, {
  		user_name: FLOW.Env.appId, //to be replaced with dynamic cartodb username
  		type: 'namedmap',
  		named_map: {
  			name: map_name,
  			layers: [{
  				layer_name: "t",
  				interactivity: "id"
  			}]
  		}
  	},{
  		tiler_domain: FLOW.Env.cartodbHost, //to be replaced with dynamic tiler domain
  		tiler_port: "", //to be replaced with dynamic tiler port
  		tiler_protocol: "https",
  		no_cdn: true
  	})
  	.addTo(map)
  	.done(function(layer) {
  		self.layer_exists_check = 1;
  		self.cartodb_layer = layer;

  		self.addCursorInteraction(layer);

  		current_layer = layer.getSubLayer(0);
  		current_layer.setInteraction(true);

  		// on mouseover
  		/*current_layer.on('featureOver', function(e, pos, pixel, data) {
  				// print data to console log
  				console.log("Event #" + data.id);
  		});*/

  		current_layer.on('featureClick', function(e, latlng, pos, data) {
  			self.openPopup(map, "id: "+data.id, latlng);
        self.showDetailsPane();
        if($("#form_selector").val() == ""){
          point_data_url = "/rest/cartodb/answers?dataPointId="+data.id+"&surveyId="+data.survey_id;
        }else{
          point_data_url = "/rest/cartodb/raw_data?dataPointId="+data.data_point_id+"&formId="+$("#form_selector").val();
        }
        //console.log(point_data_url);
        self.getCartodbPointData(point_data_url);
  		});

  		// show infowindows on click
  		//cdb.vis.Vis.addInfowindow(map, current_layer, ['id']);
  	});
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

  getCartodbPointData: function(url){
    $("#pointDetails").html("");
    $.get(url, function(point_data, status){
      var clicked_point_content = "";
      //console.log(point_data);

      if (point_data['answers'] != null) {
        //get request for questions
        $.get(
      			"/rest/cartodb/questions?form_id="+point_data['formId'],
      			function(questions_data, status){
      				//console.log(questions_data);
              clicked_point_content += "<ul class=\"placeMarkBasicInfo floats-in\">"
              +"<li>"
              +"<span>Collected on:</span>"
              +"<div class=\"placeMarkCollectionDate\">"
              +point_data['answers']['created_at']
              +"</div></li><li></li></ul>";
              //clicked_point_content += "<table>";
              clicked_point_content += "<dl class=\"floats-in\" style=\"opacity: 1; display: inherit;\">";
              for (column in point_data['answers']){
                for(var i=0; i<questions_data['questions'].length; i++){
                  if (column.match(questions_data['questions'][i].id)) {
                    //console.log(questions_data['questions'][i].display_text);
                    clicked_point_content += "<div class=\"defListWrap\"><dt>"+questions_data['questions'][i].display_text+"&nbsp;</dt>";

                    //if question is of type, photo load a html image element
                    if(questions_data['questions'][i].type == "PHOTO"){
                      image = "<div class=\":imgContainer photoUrl:shown:hidden\">";
                      if(point_data['answers'][column] != null){
                        image_filename = FLOW.Env.photo_url_root+point_data['answers'][column].substring(point_data['answers'][column].lastIndexOf("/")+1);
                        image += "<a href=\""+image_filename+"\" target=\"_blank\">"
                        +"<img src=\""+image_filename+"\" alt=\"\"/></a>";
                      }
                      image +"</div>";
                      clicked_point_content += "<dd>"+image+"</dd></div>";
                    }else{
                      clicked_point_content += "<dd>"+point_data['answers'][column]+"</dd></div>";
                    }
                  }
                }
              }
              //clicked_point_content += "</table>";
              clicked_point_content += "</dl>";
              $("#pointDetails").html(clicked_point_content);
      			});
      }else{
        clicked_point_content += "<p class=\"noDetails\">No details</p>";
        $("#pointDetails").html(clicked_point_content);
      }
    });
  }

});


FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailView = Ember.View.extend({});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});
