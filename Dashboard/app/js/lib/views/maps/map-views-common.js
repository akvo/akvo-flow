FLOW.NavMapsView = FLOW.View.extend({
  templateName: 'navMaps/nav-maps-common',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,
  map: null,
  marker: null,
  geoShape: null,
  geoshapeCoordinates: null,
  polygons: [],
  mapZoomLevel: 0,
  mapCenter: null,
  hierarchyObject: [],
  lastSelectedElement: 0,
  geomodel: null,
  cartodbLayer: null,
  layerExistsCheck: false,

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

    if (FLOW.Env.mapsProvider === 'cartodb') {
      self.insertCartodbMap();
    } else {
      if (FLOW.Env.mapsProvider === 'google') {
        self.insertGoogleMap();
      } else {
        self.insertMapboxMap();
      }
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

  insertGoogleMap: function () {
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
  },

  insertMapboxMap: function() {
    this.map = L.mapbox.map('flowMap', 'akvo.he30g8mm').setView([-0.703107, 36.765], 2);
    L.control.layers({
      'Terrain': L.mapbox.tileLayer('akvo.he30g8mm').addTo(this.map),
      'Streets': L.mapbox.tileLayer('akvo.he2pdjhk'),
      'Satellite': L.mapbox.tileLayer('akvo.he30neh4')
    }).addTo(this.map);
  },

  insertCartodbMap: function() {
    var self = this;

    $.ajaxSetup({
    	beforeSend: function(){
    		FLOW.savingMessageControl.numLoadingChange(1);
        },
    	complete: function(){
    		FLOW.savingMessageControl.numLoadingChange(-1);
        }
    });

    var filterContent = '<div id="survey_hierarchy" style="float: left"></div>&nbsp;';

    $('#dropdown-holder').prepend(filterContent);
    $('#dropdown-holder').append('<div style="clear: both"></div>');

    //Define the data layer
    var data_layer;

    // create leaflet map
    var map = L.map('flowMap', {scrollWheelZoom: true}).setView([26.11598592533351, 1.9335937499999998], 2);

    var bounds = new L.LatLngBounds(map.getBounds().getSouthWest(), map.getBounds().getNorthEast());

    map.options.maxBoundsViscosity = 1.0;
    map.options.maxBounds = bounds;
    map.options.maxZoom = 18;
    map.options.minZoom = 2;

    var mbAttr = 'Map &copy; 1987-2014 <a href="http://developer.here.com">HERE</a>',
			mbUrl = 'https://{s}.{base}.maps.cit.api.here.com/maptile/2.1/maptile/{mapID}/{scheme}/{z}/{x}/{y}/256/{format}?app_id={app_id}&app_code={app_code}';

    var normal = L.tileLayer(mbUrl, {
      scheme: 'normal.day.transit',
      format: 'png8',
      attribution: mbAttr,
      subdomains: '1234',
      mapID: 'newest',
      app_id: FLOW.Env.hereMapsAppId,
      app_code: FLOW.Env.hereMapsAppCode,
      base: 'base'
    }).addTo(map),
    satellite  = L.tileLayer(mbUrl, {
      scheme: 'hybrid.day',
      format: 'jpg',
      attribution: mbAttr,
      subdomains: '1234',
      mapID: 'newest',
      app_id: FLOW.Env.hereMapsAppId,
      app_code: FLOW.Env.hereMapsAppCode,
      base: 'aerial'
    });

    var baseLayers = {
			"Normal": normal,
			"Satellite": satellite
		};

    L.control.layers(baseLayers).addTo(map);

    this.map = map;

    map.on('click', function(e) {
      if(self.marker != null){
        self.map.removeLayer(self.marker);
        self.hideDetailsPane();
        $('#pointDetails').html('<p class="noDetails">'+Ember.String.loc('_no_details') +'</p>');
      }

      if(self.polygons.length > 0){
        for(var i=0; i<self.polygons.length; i++){
          self.map.removeLayer(self.polygons[i]);
        }
        //restore the previous zoom level and map center
        self.map.setZoom(self.mapZoomLevel);
        self.map.panTo(self.mapCenter);
        self.polygons = [];
      }
    });

    map.on('zoomend', function() {
      $('body, html, #flowMap').scrollTop(0);
    });

    //manage folder and/or survey selection hierarchy
    self.checkHierarchy(0);

    $(document).off('change', '.folder_survey_selector').on('change', '.folder_survey_selector',function(e) {

      $('#form_selector option[value!=""]').remove();

      //remove all 'folder_survey_selector's after current
      self.cleanHierarchy($(this));

      //first remove previously created form selector elements
      $(".form_selector").remove();

      if($(this).val() !== ""){
        var keyId = $(this).val();
        //if a survey is selected, load forms to form selector element.
        if($(this).find("option:selected").data('type') === 'PROJECT'){
          $.get('/rest/cartodb/forms?surveyId='+keyId, function(data, status) {
            var rows = [];
            if(data['forms'] && data['forms'].length > 0) {
              rows = data['forms'];
              rows.sort(function(el1, el2) {
                return self.compare(el1, el2, 'name')
              });

              var hierarchyObject = self.hierarchyObject;

              //create folder and/or survey select element
              var form_selector = $("<select></select>").attr("data-survey-id", keyId).attr("class", "form_selector");
              form_selector.append('<option value="">--' + Ember.String.loc('_choose_a_form') + '--</option>');

              for(var i=0; i<rows.length; i++) {
                //append returned forms list to the firm selector element
                form_selector.append(
                  $('<option></option>').val(rows[i]["id"]).html(rows[i]["name"]));
              }
              $("#survey_hierarchy").append(form_selector);
            }
          });

          var namedMapObject = {};
          namedMapObject['mapObject'] = map;
          namedMapObject['mapName'] = 'data_point_'+keyId;
          namedMapObject['tableName'] = 'data_point';
          namedMapObject['interactivity'] = ["name", "survey_id", "id", "identifier", "lat", "lon"];
          namedMapObject['query'] = 'SELECT * FROM data_point WHERE survey_id='+keyId;

          self.namedMapCheck(namedMapObject);
        }else{ //if a folder is selected, load the folder's children on a new 'folder_survey_selector'
          //first clear any currently overlayed cartodb layer (if any)
          self.clearCartodbLayer();

          var hierarchyObject = self.hierarchyObject;

          for(var i=0; i<hierarchyObject.length; i++){
            if(hierarchyObject[i].keyId === parseInt(keyId) && self.lastSelectedElement !== parseInt(keyId)){
              self.checkHierarchy(keyId);
              self.lastSelectedElement = parseInt(keyId);
            }
          }
        }
      }else{ //if nothing is selected, delete all children 'folder_survey_selector's and clear form selector
        self.clearCartodbLayer();
      }

    });

    $(document).off('change', '.form_selector').on('change', '.form_selector',function(e) {
      //remove all 'folder_survey_selector's after current
      self.cleanHierarchy($(this));

      if ($(this).val() !== "") {
        var formId = $(this).val();
        //get list of columns to be added to new named map's interactivity
        $.get("/rest/cartodb/columns?form_id="+formId, function(columnsData) {
          var namedMapObject = {};
          namedMapObject['mapObject'] = map;
          namedMapObject['mapName'] = "raw_data_"+formId;
          namedMapObject['tableName'] = "raw_data_"+formId;
          namedMapObject['interactivity'] = [];
          namedMapObject['query'] = "SELECT * FROM raw_data_" + formId;

          if (columnsData.column_names) {
            for (var j=0; j<columnsData['column_names'].length; j++) {
              namedMapObject['interactivity'].push(columnsData['column_names'][j]['column_name']);
            }
          }

          self.namedMapCheck(namedMapObject);
        });
      } else {
        self.createLayer(map, "data_point_"+$(this).data('survey-id'), "");
      }
    });

    $(document.body).on('click', '.projectGeoshape', function(){
      if(self.polygons.length > 0){
        $(this).html(Ember.String.loc('_project_geoshape_onto_main_map'));
        for(var i=0; i<self.polygons.length; i++){
          self.map.removeLayer(self.polygons[i]);
        }
        //restore the previous zoom level and map center
        self.map.setZoom(self.mapZoomLevel);
        self.map.panTo(self.mapCenter);
        self.polygons = [];
      }else{
        $(this).html(Ember.String.loc('_clear_geoshape_from_main_map'));
        self.projectGeoshape(self.geoshapeCoordinates);
      }
    });
  },

  /*Check if a named map exists. If one exists, call function to overlay it
  else call function to create a new one*/
  namedMapCheck: function(namedMapObject){
    var self = this;
    $.get("/rest/cartodb/named_maps", function(data, status) {
      if (data.template_ids) {
        var mapExists = false;
        for (var i=0; i<data['template_ids'].length; i++) {
          if(data['template_ids'][i] === namedMapObject.mapName) {
            //named map already exists
            mapExists = true;
            break;
          }
        }

        if (mapExists) {
          //overlay named map
          self.createLayer(namedMapObject.mapObject, namedMapObject.mapName, "");
        }else{
          //create new named map
          self.namedMaps(
            namedMapObject.mapObject,
            namedMapObject.mapName,
            namedMapObject.tableName,
            namedMapObject.query,
            namedMapObject.interactivity);
        }
      }
    });
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
      rawImagePath = item.get('stringValue') || '';
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
    var markerIcon = new L.Icon({
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
    var cartocss = "#"+table+"{"
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

    //first clear any currently overlayed cartodb layer
    self.clearCartodbLayer();

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
      layer.setZIndex(1000); //required to ensure that the cartodb layer is not obscured by the here maps base layers
      self.layerExistsCheck = true;
      self.cartodbLayer = layer;

      self.addCursorInteraction(layer);

      var current_layer = layer.getSubLayer(0);
      current_layer.setInteraction(true);

      current_layer.on('featureClick', function(e, latlng, pos, data) {
        if(self.marker != null){
          self.map.removeLayer(self.marker);
        }
        self.placeMarker([data.lat, data.lon]);

        self.showDetailsPane();
        if($('.form_selector').length && $('.form_selector').val() !== ""){
          pointDataUrl = '/rest/cartodb/raw_data?dataPointId='+data.data_point_id+'&formId='+$('.form_selector').val();
          $.get('/rest/cartodb/data_point?id='+data.data_point_id, function(pointData, status){
            self.getCartodbPointData(pointDataUrl, pointData['row']['name'], pointData['row']['identifier']);
          });
        }else{
          pointDataUrl = '/rest/cartodb/answers?dataPointId='+data.id+'&surveyId='+data.survey_id;
          self.getCartodbPointData(pointDataUrl, data.name, data.identifier);
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
        $('#flowMap').css({"cursor":"-moz-grab","cursor":"-webkit-grab"});
      }
    });
  },

  getCartodbPointData: function(url, dataPointName, dataPointIdentifier){
    var self = this;
    $("#pointDetails").html("");
    $.get(url, function(pointData, status){
      var clickedPointContent = "";

      if (pointData['answers'] != null) {
        //get request for questions
        $.get(
            "/rest/cartodb/questions?form_id="+pointData['formId'],
            function(questionsData, status){
              var geoshapeObject, geoshapeCheck = false;
              self.geoshapeCoordinates = null;

              var dataCollectionDate = pointData['answers']['created_at'];
              var date = new Date(dataCollectionDate);

              clickedPointContent += '<ul class="placeMarkBasicInfo floats-in">'
              +'<h3>'
              +((dataPointName != "" && dataPointName != "null" && dataPointName != null) ? dataPointName : "")
              +'</h3>'
              +'<li>'
              +'<span>'+Ember.String.loc('_data_point_id') +':</span>'
              +'<div style="display: inline; margin: 0 0 0 5px;">'+dataPointIdentifier+'</div>'
              +'</li>'
              +'<li>'
              +'<span>'+Ember.String.loc('_collected_on') +':</span>'
              +'<div class="placeMarkCollectionDate">'
              +date.toUTCString()
              +'</div></li><li></li></ul>';

              clickedPointContent += '<div class="mapInfoDetail" style="opacity: 1; display: inherit;">';
              for (column in pointData['answers']){
                var questionAnswer = pointData['answers'][column];
                for(var i=0; i<questionsData['questions'].length; i++){
                  if (column.match(questionsData['questions'][i].id)) {
                    if(questionsData['questions'][i].type === "GEOSHAPE" && questionAnswer !== null){
                      var geoshapeObject = FLOW.parseGeoshape(questionAnswer);
                      if(geoshapeObject !== null){
                        clickedPointContent += '<h4><div style="float: left">'
                        +questionsData['questions'][i].display_text
                        +'</div>&nbsp;<a style="float: right" class="projectGeoshape">'+Ember.String.loc('_project_geoshape_onto_main_map') +'</a></h4>';
                      }
                    } else {
                      clickedPointContent += '<h4>'+questionsData['questions'][i].display_text+'&nbsp;</h4>';
                    }

                    clickedPointContent += '<div style="float: left; width: 100%">';

                    if(questionAnswer !== "" && questionAnswer !== null && questionAnswer !== "null"){
                      switch (questionsData['questions'][i].type) {
                        case "PHOTO":
                          var image = '<div class=":imgContainer photoUrl:shown:hidden">';
                          var image_filename = FLOW.Env.photo_url_root+questionAnswer.substring(questionAnswer.lastIndexOf("/")+1);
                          image += '<a href="'+image_filename+'" target="_blank">'
                          +'<img src="'+image_filename+'" alt=""/></a>';

                          image += '</div>';
                          clickedPointContent += image;
                          break;
                        case "GEOSHAPE":
                          geoshapeObject = FLOW.parseGeoshape(questionAnswer);
                          self.geoshapeCoordinates = geoshapeObject;

                          if(geoshapeObject !== null){
                            geoshapeCheck = true;
                            //create a container for each feature in geoshape object
                            for(var j=0; j<geoshapeObject['features'].length; j++){
                              clickedPointContent += '<div id="geoShapeMap'+j+'" style="width:100%; height: 100px; float: left"></div>';

                              if(geoshapeObject['features'][j]['geometry']['type'] === "Polygon"
                               || geoshapeObject['features'][j]['geometry']['type'] === "LineString"
                                || geoshapeObject['features'][j]['geometry']['type'] === "MultiPoint"){
                                clickedPointContent += '<div style="float: left; width: 100%">'+ Ember.String.loc('_points') +': '+geoshapeObject['features'][j]['properties']['pointCount']+'</div>';
                              }

                              if(geoshapeObject['features'][j]['geometry']['type'] === "Polygon"
                               || geoshapeObject['features'][j]['geometry']['type'] === "LineString"){
                                clickedPointContent += '<div style="float: left; width: 100%">'+ Ember.String.loc('_length') +': '+geoshapeObject['features'][j]['properties']['length']+'m</div>';
                              }

                              if(geoshapeObject['features'][j]['geometry']['type'] === "Polygon"){
                                clickedPointContent += '<div style="float: left; width: 100%">'+ Ember.String.loc('_area') +': '+geoshapeObject['features'][j]['properties']['area']+'m&sup2;</div>';
                              }
                            }
                          }
                          break;
                        case "DATE":
                          var dateQuestion = new Date((isNaN(questionAnswer) === false) ? parseInt(questionAnswer) : questionAnswer);
                          clickedPointContent += dateQuestion.toUTCString().slice(0, -13); //remove last 13 x-ters so only date displays
                          break;
                        case "CASCADE":
                        case "OPTION":
                          var cascadeString = "", cascadeJson;
                          if (questionAnswer.charAt(0) === '[') {
                            cascadeJson = JSON.parse(questionAnswer);
                            cascadeString = cascadeJson.map(function(item){
                              return (questionsData['questions'][i].type == "CASCADE") ? item.name : item.text;
                            }).join("|");
                          } else {
                            cascadeString = questionAnswer;
                          }
                          clickedPointContent += cascadeString;
                          break;
                        case "SIGNATURE":
                          var tmpSig = {"image":"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","name":"xvxbx"};
                          clickedPointContent += tmpSig.image;
                        default:
                          clickedPointContent += questionAnswer;
                      }
                    }
                    clickedPointContent += "&nbsp;</div><hr>";
                  }
                }
              }
              clickedPointContent += '</div>';
              $('#pointDetails').html(clickedPointContent);
              $('hr').show();

              //if there's geoshape, draw it
              if(geoshapeCheck){
                for(var j=0; j<geoshapeObject['features'].length; j++){
                  //pass  container node, object type, and object coordinates to drawGeoShape function
                  FLOW.drawGeoShape($("#geoShapeMap"+j)[0], geoshapeObject['features'][j]['geometry']['type'], geoshapeObject['features'][j]['geometry']['coordinates']);
                }
              }
            });
      } else {
        clickedPointContent += '<p class="noDetails">'+Ember.String.loc('_no_details') +'</p>';
        $('#pointDetails').html(clickedPointContent);
      }
    });
  },

  //function to project geoshape from details panel to main map canvas
  projectGeoshape: function(geoShapeObject){
    //before fitting the geoshape to map, get the current
    //zoom level and map center first and save them
    this.mapZoomLevel = this.map.getZoom();
    this.mapCenter = this.map.getCenter();

    //create a leaflet featureGroup to hold all object features
    var featureGroup = new L.featureGroup;
    for(var i=0; i<geoShapeObject['features'].length; i++){
      var points = [], geoShape;
      var geoshapeCoordinatesArray, geoShapeObjectType = geoShapeObject['features'][i]['geometry']['type'];
      if(geoShapeObjectType === "Polygon"){
        geoshapeCoordinatesArray = geoShapeObject['features'][i]['geometry']['coordinates'][0];
      } else {
        geoshapeCoordinatesArray = geoShapeObject['features'][i]['geometry']['coordinates'];
      }

      for(var j=0; j<geoshapeCoordinatesArray.length; j++){
        points.push([geoshapeCoordinatesArray[j][1], geoshapeCoordinatesArray[j][0]]);
      }

      //add object to featureGroup
      if(geoShapeObjectType === "Polygon"){
        geoShape = L.polygon(points).addTo(this.map);
      }else if (geoShapeObjectType === "MultiPoint") {
        var geoShapeMarkersArray = [];
        for (var k = 0; k < points.length; k++) {
          geoShapeMarkersArray.push(L.marker([points[k][0],points[k][1]]));
        }
        geoShape = L.featureGroup(geoShapeMarkersArray).addTo(this.map);
      }else if (geoShapeObjectType === "LineString") {
        geoShape = L.polyline(points).addTo(this.map);
      }
      featureGroup.addLayer(geoShape);
      this.polygons.push(geoShape);
    }
    this.map.fitBounds(featureGroup.getBounds()); //fit featureGroup to map bounds
  },

  checkHierarchy: function(parentFolderId){
    var self = this;

    //if survey hierarchy object has previously been retrieved, no need to pull it anew
    if(self.hierarchyObject.length > 0){
      self.manageHierarchy(parentFolderId);
    }else{
      $.get('/rest/survey_groups'/*place survey_groups endpoint here*/
      , function(data, status){
        if(data['survey_groups'].length > 0){
          self.hierarchyObject = data['survey_groups'];
          self.manageHierarchy(parentFolderId);
        }
      });
    }
  },

  manageHierarchy: function(parentFolderId){
    var self = this;

    rows = self.hierarchyObject;
    rows.sort(function(el1, el2) {
      return self.compare(el1, el2, 'name');
    });

    //create folder and/or survey select element
    var folder_survey_selector = $("<select></select>").attr("class", "folder_survey_selector");
    folder_survey_selector.append('<option value="">--' + Ember.String.loc('_choose_folder_or_survey') + '--</option>');

    for (var i=0; i<rows.length; i++) {
      //append return survey list to the survey selector element
      var surveyGroup = rows[i];

      //if a subfolder, only load folders and surveys from parent folder
      if(surveyGroup.parentId == parentFolderId){
        folder_survey_selector.append('<option value="'
          + surveyGroup.keyId + '"'
          +'data-type="'+surveyGroup.projectType+'">'
          + surveyGroup.name
          + '</option>');
      }
    }
    $("#survey_hierarchy").append(folder_survey_selector);
  },

  cleanHierarchy: function(element){
    $(element).nextAll().remove();
  },

  clearCartodbLayer: function(){
    //check to confirm that there are no layers displayed on the map
    if(this.layerExistsCheck){
      this.map.removeLayer(this.cartodbLayer);
      this.layerExistsCheck = false;
    }
  }
});


FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailView = Ember.View.extend({});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});

FLOW.GeoshapeMapView = FLOW.View.extend({
  templateName: 'navMaps/geoshape-map',
  geoshape: null,

  didInsertElement: function() {
    this.set('geoshape', JSON.parse(this.get('parentView.geoShapeObject')));
    if (this.get('isPolygon') || this.get('isLineString') || this.get('isMultiPoint')) {
      var containerNode = this.get('element').getElementsByClassName('geoshapeMapContainer')[0];
      if (containerNode) {
        FLOW.drawGeoShape(containerNode, this.get('geoshape'));
      }
    }
  },

  length: function() {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.length
  }.property('this.geoshape'),

  area: function() {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.area
  }.property('this.geoshape'),

  pointCount: function() {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.pointCount
  }.property('this.geoshape'),

  isPolygon: function() {
    var geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    } else {
      return geoshape['features'].length > 0 &&
        geoshape['features'][0]["geometry"]["type"] === "Polygon"
    }
  }.property('this.geoshape'),

  isLineString: function() {
    var geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    } else {
      return geoshape['features'].length > 0 &&
        geoshape['features'][0]["geometry"]["type"] === "LineString"
    }
  }.property('this.geoshape'),

  isMultiPoint: function() {
    var geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    } else {
      return geoshape['features'].length > 0 &&
        geoshape['features'][0]["geometry"]["type"] === "MultiPoint"
    }
  }.property('this.geoshape'),

  geoshapeString: function() {
    return this.geoshape === null ? null : JSON.stringify(this.geoshape);
  }.property('this.geoshape')
});
