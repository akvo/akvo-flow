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
  clickedPointCoordinates: [],
  mediaMarkers: {},
  selectedMediaMarker: {},
  mediaMarkerSelected: {},
  hierarchyObject: [],
  lastSelectedElement: 0,
  geomodel: null,
  cartodbLayer: null,
  layerExistsCheck: false,
  questionGroups: [],
  refreshIntervalId: null,

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
      var options = {
          minZoom: 2,
          maxZoom: 18
      };
    this.map = L.mapbox.map('flowMap', 'akvo.he30g8mm', options).setView([-0.703107, 36.765], 2);
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

    var hereAttr = 'Map &copy; 1987-2014 <a href="http://developer.here.com">HERE</a>',
			hereUrl = 'https://{s}.{base}.maps.cit.api.here.com/maptile/2.1/maptile/{mapID}/{scheme}/{z}/{x}/{y}/256/{format}?app_id={app_id}&app_code={app_code}',
      mbAttr = 'Map &copy; <a href="http://openstreetmap.org">OSM</a>',
      mbUrl = 'http://{s}.tiles.mapbox.com/v3/akvo.he30g8mm/{z}/{x}/{y}.png';

    var normal = L.tileLayer(hereUrl, {
      scheme: 'normal.day.transit',
      format: 'png8',
      attribution: hereAttr,
      subdomains: '1234',
      mapID: 'newest',
      app_id: FLOW.Env.hereMapsAppId,
      app_code: FLOW.Env.hereMapsAppCode,
      base: 'base'
    }).addTo(map),
    terrain  = L.tileLayer(mbUrl, {
      attribution: mbAttr,
      subdomains: 'abc'
    }),
    satellite  = L.tileLayer(hereUrl, {
      scheme: 'hybrid.day',
      format: 'jpg',
      attribution: hereAttr,
      subdomains: '1234',
      mapID: 'newest',
      app_id: FLOW.Env.hereMapsAppId,
      app_code: FLOW.Env.hereMapsAppCode,
      base: 'aerial'
    });

    var baseLayers = {
			"Normal": normal,
      "Terrain": terrain,
			"Satellite": satellite
		};

    FLOW.addExtraMapBoxTileLayer(baseLayers);

    L.control.layers(baseLayers).addTo(map);

    this.map = map;

    map.on('click', function(e) {
      self.clearMap(); //remove any previously loaded point data
    });

    map.on('zoomend', function() {
      $('body, html, #flowMap').scrollTop(0);
    });

    //manage folder and/or survey selection hierarchy
    self.checkHierarchy(0);

    $(document).off('change', '.folder_survey_selector').on('change', '.folder_survey_selector',function(e) {
      self.clearMap(); //remove any previously loaded point data
      $('#form_selector option[value!=""]').remove();

      self.cleanHierarchy($(this)); //remove all 'folder_survey_selector's after current

      //first remove previously created form selector elements
      $(".form_selector").remove();

      if($(this).val() !== ""){
        var keyId = $(this).val();
        //if a survey is selected, load forms to form selector element.
        if($(this).find("option:selected").data('type') === 'PROJECT'){
          $.get(
            '/rest/surveys?surveyGroupId='+keyId,
            function(data, status) {
              var rows = [];
              if(data['surveys'] && data['surveys'].length > 0) {
                rows = data['surveys'];
                rows.sort(function(el1, el2) {
                  return self.compare(el1, el2, 'name')
                });

                var hierarchyObject = self.hierarchyObject;

                //create folder and/or survey select element
                var form_selector = $("<select></select>").attr("data-survey-id", keyId).attr("class", "form_selector");
                form_selector.append('<option value="">--' + Ember.String.loc('_choose_a_form') + '--</option>');

                var formIds = [];
                for(var i=0; i<rows.length; i++) {
                  //append returned forms list to the firm selector element
                  form_selector.append(
                    $('<option></option>').val(rows[i]["keyId"]).html(rows[i]["name"]));
                  formIds.push(rows[i]["keyId"]);
                }
                $("#survey_hierarchy").append(form_selector);

                self.questionGroups = [];
                for(var i=0; i<formIds.length; i++){
                  self.loadQuestions(formIds[i]);
                }
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
      self.clearMap(); //remove any previously loaded point data
      self.cleanHierarchy($(this)); //remove all 'folder_survey_selector's after current

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

    $(document.body).on('click', '.project-geoshape', function(){
      if(self.polygons.length > 0){
        $(this).html(Ember.String.loc('_project_onto_main_map'));
        for(var i=0; i<self.polygons.length; i++){
          self.map.removeLayer(self.polygons[i]);
        }
        //restore the previous zoom level and map center
        self.map.setZoom(self.mapZoomLevel);
        self.map.panTo(self.mapCenter);
        self.polygons = [];
      }else{
        $(this).html(Ember.String.loc('_clear_geoshape_from_main_map'));
        self.projectGeoshape($(this).data('geoshape-object'));
      }
    });

    $(document.body).on('mouseover', '.media', function(){
      var mediaObject = $(this).data('coordinates');
      var mediaMarkerIcon = new L.Icon({
        iconUrl: 'images/media-marker.png',
        iconSize: [11, 11]
      }), selectedMediaMarkerIcon = new L.Icon({
        iconUrl: 'images/media-marker-selected.png',
        iconSize: [11, 11]
      });
      if(mediaObject !== '') {
        var filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf("/")+1).split(".")[0];
        var mediaCoordinates = [mediaObject['location']['latitude'], mediaObject['location']['longitude']];
        if(!(filename in self.mediaMarkers)) {
          self.mediaMarkers[filename] = new L.marker(mediaCoordinates, {icon: mediaMarkerIcon}).addTo(self.map);
        } else {
          self.selectedMediaMarker[filename] = new L.marker(mediaCoordinates, {icon: selectedMediaMarkerIcon}).addTo(self.map);
        }
      }
    });

    $(document.body).on('mouseout', '.media', function(){
      var mediaObject = $(this).data('coordinates');
      if(mediaObject !== '') {
        var filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf("/")+1).split(".")[0];
        if(filename in self.mediaMarkers && !(filename in self.mediaMarkerSelected)) {
          self.map.removeLayer(self.mediaMarkers[filename]);
          delete self.mediaMarkers[filename];
        } else {
          self.map.removeLayer(self.selectedMediaMarker[filename]);
          delete self.selectedMediaMarker[filename];
        }
      }
    });

    $(document.body).on('click', '.media-location', function(){
      var mediaObject = $(this).data('coordinates');
      var mediaMarkerIcon = new L.Icon({
        iconUrl: 'images/media-marker.png',
        iconSize: [11, 11]
      });
      if(mediaObject !== '') {
        var filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf("/")+1).split(".")[0];
        var mediaCoordinates = [mediaObject['location']['latitude'], mediaObject['location']['longitude']];
        if(!(filename in self.mediaMarkerSelected)) {
          $(this).html(Ember.String.loc('_hide_photo_on_map'));
          self.mediaMarkers[filename] = new L.marker(mediaCoordinates, {icon: mediaMarkerIcon}).addTo(self.map);
          self.mediaMarkerSelected[filename] = true;
        } else {
          $(this).html(Ember.String.loc('_show_photo_on_map'));
          self.map.removeLayer(self.mediaMarkers[filename]);
          delete self.mediaMarkers[filename];
          delete self.mediaMarkerSelected[filename];
        }
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
    button.html(Ember.String.loc('_hide') + ' &rsaquo;');
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
    button.html('&lsaquo; ' + Ember.String.loc('_show') );

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
        self.clickedPointCoordinates = [data.lat, data.lon];
        self.placeMarker([data.lat, data.lon]);

        self.showDetailsPane();

        if($.active > 0){
            self.refreshIntervalId = setInterval(function () {
                //keep checking if there are any pending ajax requests
                if($.active > 0){
                    //keep displaying loading icon
                } else { //if no pending ajax requests
                    // call function to load the clicked point details
                  if ($('.form_selector').length && $('.form_selector').val() !== ""){
                    pointDataUrl = '/rest/cartodb/raw_data?dataPointId='+data.data_point_id+'&formId='+$('.form_selector').val();
                    $.get('/rest/cartodb/data_point?id='+data.data_point_id, function(pointData, status){
                      self.getCartodbPointData(pointDataUrl, pointData['row']['name'], pointData['row']['identifier']);
                    });
                  } else {
                    pointDataUrl = '/rest/cartodb/answers?dataPointId='+data.id+'&surveyId='+data.survey_id;
                    self.getCartodbPointData(pointDataUrl, data.name, data.identifier);
                  }
                }
            }, 500);
        } else {
            // call function to load the clicked point details
            if($('.form_selector').length && $('.form_selector').val() !== ""){
                pointDataUrl = '/rest/cartodb/raw_data?dataPointId='+data.data_point_id+'&formId='+$('.form_selector').val();
                $.get('/rest/cartodb/data_point?id='+data.data_point_id, function(pointData, status){
                    self.getCartodbPointData(pointDataUrl, pointData['row']['name'], pointData['row']['identifier']);
                });
            } else {
                pointDataUrl = '/rest/cartodb/answers?dataPointId='+data.id+'&surveyId='+data.survey_id;
                self.getCartodbPointData(pointDataUrl, data.name, data.identifier);
            }
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
    clearInterval(self.refreshIntervalId); //stop the interval if running

    $("#pointDetails").html("");

    $.get(url, function(pointData, status){
      if (pointData['answers'] != null) {
        var geoshapeObject, geoshapeQuestionsCount = 0, mediaResponses = [];

        var dataCollectionDate = pointData['answers']['created_at'];
        var date = new Date(dataCollectionDate);

        var pointDetailsHeader = '<ul class="placeMarkBasicInfo floats-in">'
        +'<h3>'
        +((dataPointName != "" && dataPointName != "null" && dataPointName != null) ? dataPointName : "")
        +'</h3>'
        +'<li>'
        +'<span>'+Ember.String.loc('_data_point_id') +':</span>'
        +'<div style="display: inline; margin: 0 0 0 5px;">'+dataPointIdentifier+'</div>'
        +'</li>'
        +'<br><li>'
        +'<span>'+Ember.String.loc('_collected_on') +':</span>'
        +'<div class="placeMarkCollectionDate">'
        +date.toISOString().slice(0,-8).replace("T", " ")
        +'</div></li><li></li></ul>';

        $("#pointDetails").append(pointDetailsHeader);

        var clickedPointContent = "";
        //create a questions array with the correct order of questions as in the survey
        if(self.questionGroups.length > 0) {
          var geoshapeObject, geoshapeCheck = false;
          self.geoshapeCoordinates = null;

          //sort question groups by their order
          self.questionGroups.sort(function(a, b) {
            return parseFloat(a.order) - parseFloat(b.order);
          });

          clickedPointContent += '<div class="mapInfoDetail" style="opacity: 1; display: inherit;">';

          for(var qg=0; qg<self.questionGroups.length; qg++) {
            for(var i=0; i<self.questionGroups[qg]['questions'].length; i++) {
              for (column in pointData['answers']) {
                var questionAnswer = pointData['answers'][column];
                if (column.match(self.questionGroups[qg]['questions'][i].keyId)) {
                  clickedPointContent += '<h4>'+self.questionGroups[qg]['questions'][i].text+'&nbsp;</h4>'
                    +'<div style="float: left; width: 100%">';

                  if(questionAnswer){
                    switch (self.questionGroups[qg]['questions'][i].type) {
                      case "PHOTO":
                      case "VIDEO":
                        var mediaString = "", mediaJson = "", mediaFilename = "", mediaObject = {};
                        if (questionAnswer.charAt(0) === '{') {
                          mediaJson = JSON.parse(questionAnswer);
                          mediaString = mediaJson.filename;
                        } else {
                          mediaString = questionAnswer;
                        }

                        var mediaFileURL = FLOW.Env.photo_url_root+mediaString.substring(mediaString.lastIndexOf("/")+1);
                        if(self.questionGroups[qg]['questions'][i].type == "PHOTO") {
                          mediaOutput = '<div class=":imgContainer photoUrl:shown:hidden">'
                          +'<a class="media" data-coordinates=\''
                          +((mediaJson.location) ? questionAnswer : '' )+'\' href="'
                          +mediaFileURL+'" target="_blank"><img src="'+mediaFileURL+'" alt=""/></a><br>'
                          +((mediaJson.location) ? '<a class="media-location" data-coordinates=\''+questionAnswer+'\'>'+Ember.String.loc('_show_photo_on_map')+'</a>' : '')
                          +'</div>';
                        } else if (self.questionGroups[qg]['questions'][i].type == "VIDEO") {
                          mediaOutput = '<div><div class="media" data-coordinates=\''
                          +((mediaJson.location) ? questionAnswer : '' )+'\'>'+mediaFileURL+'</div><br>'
                          +'<a href="'+mediaFileURL+'" target="_blank">'+Ember.String.loc('_open_video')+'</a>'
                          +((mediaJson.location) ? '&nbsp;|&nbsp;<a class="media-location" data-coordinates=\''+questionAnswer+'\'>'+Ember.String.loc('_show_photo_on_map')+'</a>' : '')
                          +'</div>';
                        }
                        clickedPointContent += mediaOutput;
                        break;
                      case "GEOSHAPE":
                        geoshapeObject = FLOW.parseJSON(questionAnswer, "features");
                        self.geoshapeCoordinates = geoshapeObject;

                        if(geoshapeObject){
                          geoshapeCheck = true;
                          geoshapeQuestionsCount++;
                          clickedPointContent += '<div class="geoshape-map" data-geoshape-object=\''+questionAnswer+'\' style="width:100%; height: 100px; float: left"></div>'
                            +'<a style="float: left" class="project-geoshape" data-geoshape-object=\''+questionAnswer+'\'>'+Ember.String.loc('_project_onto_main_map')+'</a>'

                          if(geoshapeObject['features'][0]['geometry']['type'] === "Polygon"
                           || geoshapeObject['features'][0]['geometry']['type'] === "LineString"
                            || geoshapeObject['features'][0]['geometry']['type'] === "MultiPoint"){
                            clickedPointContent += '<div style="float: left; width: 100%">'+ Ember.String.loc('_points') +': '+geoshapeObject['features'][0]['properties']['pointCount']+'</div>';
                          }

                          if(geoshapeObject['features'][0]['geometry']['type'] === "Polygon"
                           || geoshapeObject['features'][0]['geometry']['type'] === "LineString"){
                            clickedPointContent += '<div style="float: left; width: 100%">'+ Ember.String.loc('_length') +': '+geoshapeObject['features'][0]['properties']['length']+'m</div>';
                          }

                          if(geoshapeObject['features'][0]['geometry']['type'] === "Polygon"){
                            clickedPointContent += '<div style="float: left; width: 100%">'+ Ember.String.loc('_area') +': '+geoshapeObject['features'][0]['properties']['area']+'m&sup2;</div>';
                          }
                        }
                        break;
                      case "DATE":
                        var dateQuestion = new Date((!isNaN(questionAnswer)) ? parseInt(questionAnswer) : questionAnswer);
                        clickedPointContent += self.formatDate(dateQuestion);
                        break;
                      case "SIGNATURE":
                        clickedPointContent += '<div class="signatureImage"><img src="';
                        var srcAttr = 'data:image/png;base64,', signatureJson;
                        signatureJson = JSON.parse(questionAnswer);
                        clickedPointContent += srcAttr + signatureJson.image +'"/></div>';
                        clickedPointContent += '<div class="signedBySection">'+Ember.String.loc('_signed_by') +': '+signatureJson.name+'</div>';
                        break;
                      case "CADDISFLY":
                        clickedPointContent += FLOW.renderCaddisflyAnswer(questionAnswer);
                        break;
                      case "CASCADE":
                      case "OPTION":
                        var cascadeString = "", cascadeJson;
                        if (questionAnswer.charAt(0) === '[') {
                          cascadeJson = JSON.parse(questionAnswer);
                          cascadeString = cascadeJson.map(function(item){
                            return (self.questionGroups[qg]['questions'][i].type == "CASCADE") ? item.name : item.text;
                          }).join("|");
                        } else {
                          cascadeString = questionAnswer;
                        }
                        clickedPointContent += cascadeString;
                        break;
                      default:
                        clickedPointContent += questionAnswer
                    }
                  }
                  clickedPointContent += "&nbsp;</div><hr>";
                }
              }
            }
          }
          clickedPointContent += '</div>';
          $('#pointDetails').append(clickedPointContent);
          $('hr').show();

          //if there's geoshape, draw it
          if(geoshapeQuestionsCount > 0){
            $('.geoshape-map').each(function(index){
              FLOW.drawGeoShape($('.geoshape-map')[index], $(this).data('geoshape-object'));
            });
          }
        }
      } else {
        $('#pointDetails').html('<p class="noDetails">'+Ember.String.loc('_no_details') +'</p>');
      }
    });
  },

  loadQuestions: function(formId){
    var self = this;

    //first get the question groups for this formId
    var questionGroupsAjaxObject = {};
    questionGroupsAjaxObject['call'] = 'GET';
    questionGroupsAjaxObject['url'] = '/rest/question_groups?surveyId='+formId;
    questionGroupsAjaxObject['data'] = '';

    FLOW.ajaxCall(function(questionGroupsResponse){
      if(questionGroupsResponse.question_groups){
        //for every question group pull a list of associated questions
        for(var g=0; g<questionGroupsResponse.question_groups.length; g++){
          var questionGroup = {};
          questionGroup['id'] = questionGroupsResponse.question_groups[g].keyId;
          questionGroup['order'] = questionGroupsResponse.question_groups[g].order;
          questionGroup['questions'] = [];
          self.questionGroups.push(questionGroup);

          var questionsAjaxObject = {};
          questionsAjaxObject['call'] = 'GET';
          questionsAjaxObject['url'] = '/rest/questions?surveyId='+formId+'&questionGroupId='+questionGroupsResponse.question_groups[g].keyId;
          questionsAjaxObject['data'] = '';
          questionsAjaxObject['index'] = g;

          FLOW.ajaxCall(function(questionsResponse, qObj){
            if(questionsResponse.questions){
              for(var j=0; j<questionsResponse.questions.length; j++) {
                self.questionGroups[qObj.index]['questions'].push(questionsResponse.questions[j]);
              }

              //sort questions by order
              self.questionGroups[questionsAjaxObject['index']]['questions'].sort(function(a, b) {
                return parseFloat(a.order) - parseFloat(b.order);
              });
            }
          }, questionsAjaxObject);
        }
      }
    }, questionGroupsAjaxObject);
  },

  //function to project geoshape from details panel to main map canvas
  projectGeoshape: function(geoShapeObject){
    var points = [], geoShape;

    //before fitting the geoshape to map, get the current
    //zoom level and map center first and save them
    this.mapZoomLevel = this.map.getZoom();
    this.mapCenter = this.map.getCenter();

    var geoshapeCoordinatesArray, geoShapeObjectType = geoShapeObject['features'][0]['geometry']['type'];
    if(geoShapeObjectType === "Polygon"){
      geoshapeCoordinatesArray = geoShapeObject['features'][0]['geometry']['coordinates'][0];
    } else {
      geoshapeCoordinatesArray = geoShapeObject['features'][0]['geometry']['coordinates'];
    }

    for(var j=0; j<geoshapeCoordinatesArray.length; j++){
      points.push([geoshapeCoordinatesArray[j][1], geoshapeCoordinatesArray[j][0]]);
    }

    if(geoShapeObjectType === "Polygon"){
      geoShape = L.polygon(points).addTo(this.map);
    }else if (geoShapeObjectType === "MultiPoint") {
      var geoShapeMarkersArray = [];
      for (var i = 0; i < points.length; i++) {
        geoShapeMarkersArray.push(L.marker([points[i][0],points[i][1]]));
      }
      geoShape = L.featureGroup(geoShapeMarkersArray).addTo(this.map);
    }else if (geoShapeObjectType === "LineString") {
      geoShape = L.polyline(points).addTo(this.map);
    }
    this.map.fitBounds(geoShape.getBounds());
    this.polygons.push(geoShape);
  },

  checkHierarchy: function(parentFolderId){
    var self = this;

    //if survey hierarchy object has previously been retrieved, no need to pull it anew
    if(self.hierarchyObject.length > 0){
      self.manageHierarchy(parentFolderId);
    }else{
      $.get(
        '/rest/survey_groups', /*place survey_groups endpoint here*/
        function(data, status){
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
  },

  clearMap: function() {
    var self = this;
    if(self.marker != null){
      self.map.removeLayer(self.marker);
      self.hideDetailsPane();
      $('#pointDetails').html('<p class="noDetails">'+Ember.String.loc('_no_details') +'</p>');
    }

    if(!$.isEmptyObject(self.mediaMarkers)) {
      for(mediaMarker in self.mediaMarkers) {
        self.map.removeLayer(self.mediaMarkers[mediaMarker]);
      }
    }

    if(self.polygons.length > 0){
      for(var i=0; i<self.polygons.length; i++){
        self.map.removeLayer(self.polygons[i])
      }
      //restore the previous zoom level and map center
      self.map.setView(self.mapCenter, self.mapZoomLevel);
      self.polygons = [];
    }
  },

  formatDate: function(date) {
    if (date && !isNaN(date.getTime())) {
      return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
    }
    return null;
  }
});

FLOW.ajaxCall = function(callback, ajaxObject){
  $.ajax({
    type: ajaxObject.call,
    contentType: "application/json",
    url: ajaxObject.url,
    data: ajaxObject.data, //turns out you need to stringify the payload before sending it
    dataType: 'json',
    success: function(responseData){
      callback(responseData, ajaxObject);
    }
  });
};

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
