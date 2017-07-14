/**
  Controllers related to the map tab
  Definition:
    "placemark" is an FLOW object that represents a single survey point.
    "marker" is a map object that is rendered as a pin. Each marker have
      a placemark counterpart.
**/

FLOW.placemarkController = Ember.ArrayController.create({
  content: null,
  map: null,
  geocellCache: [],
  currentGcLevel: null,
  allPlacemarks: null,
  selectedMarker:null,

  populateMap: function () {
    var gcLevel, placemarks, placemarkArray=[];
    if (this.content.get('isLoaded') === true) {
      gcLevel = this.get('currentGcLevel');
      // filter placemarks
      placemarks = FLOW.store.filter(FLOW.Placemark, function(item){
        return item.get('level') == gcLevel;
      });

      placemarks.forEach(function (placemark) {
        marker = this.addMarker(placemark);
        placemarkArray.push(marker);
      }, this);

      if (!Ember.none(this.allPlacemarks)){
        this.allPlacemarks.clearLayers();
      }

      this.allPlacemarks = L.layerGroup(placemarkArray);
      this.allPlacemarks.addTo(this.map);
    }
  }.observes('this.content.isLoaded'),

  adaptMap: function(bestBB, zoomlevel){
    var bbString = "", gcLevel, listToRetrieve = [];

    // determine the geocell cluster level we want to show
    if (zoomlevel < 4){
      gcLevel = 2;
    } else if (zoomlevel < 6) {
      gcLevel = 3;
    } else if (zoomlevel < 8) {
      gcLevel = 4;
    } else if (zoomlevel < 11) {
      gcLevel = 5;
    } else {
      gcLevel = 0;
    }
    this.set('currentGcLevel',gcLevel);
    // on zoomlevel 2, the map repeats itself, leading to wrong results
    // therefore, we force to download the highest level on all the world.
    if (zoomlevel == 2) {
    	bestBB = "0123456789abcdef".split("");
    }

    // see if we already have it in the cache
    // in the cache, we use a combination of geocell and gcLevel requested as the key:
    // for example "af-3", "4ee-5", etc.
    // TODO this is not optimal at high zoom levels, as we will already have loaded the same points on a level before
    for (var i = 0; i < bestBB.length; i++){
      if (this.get('geocellCache').indexOf(bestBB[i]+"-"+gcLevel) < 0 ) {
        // if we don't have it in the cache add it to the list of items to be loaded
    	listToRetrieve.push(bestBB[i]);

    	// now add this key to cache
        this.get('geocellCache').push(bestBB[i] + "-" + gcLevel);
      }
    }

    // pack best bounding box values in a string for sending to the server
    bbString = listToRetrieve.join(',');

    // go get it in the datastore
    // when the points come in, populateMap will trigger and place the points
    if (!Ember.empty(bbString)){
      this.set('content',FLOW.store.findQuery(FLOW.Placemark,
        {bbString: bbString, gcLevel: gcLevel}));
    } else {
    	// we might have stuff in cache, so draw anyway
    	this.populateMap();
    }
  },

  addMarker: function (placemark) {
    var marker;
    if (placemark.get('level') > 0){
    	count = placemark.get('count');
    	if (count == 1) {
    		marker = L.circleMarker([placemark.get('latitude'),placemark.get('longitude')],{
    			radius:7,
    			color:'#d46f12',
    			fillColor:'#edb660',
    			opacity:0.9,
    			fillOpacity:0.7,
    			placemarkId: placemark.get('detailsId'),
    			collectionDate:placemark.get('collectionDate')});
    		marker.on('click', onMarkerClick);
    		return marker;
    	}

      myIcon = L.divIcon({
    	  html: '<div><span>' + count + '</span></div>',
    	  className: 'marker-cluster',
    	  iconSize: new L.Point(40, 40)});

      marker = L.marker([placemark.get('latitude'),placemark.get('longitude')], {
    	  icon: myIcon,
    	  placemarkId: placemark.get('keyId')});
      return marker;
    } else {
    	// if we are here, we are at level 0, and we have normal placemark icons.
    	marker = L.circleMarker([placemark.get('latitude'),placemark.get('longitude')],{
    		radius:7,
    		color:'#d46f12',
    		fillColor:'#edb660',
    		placemarkId: placemark.get('detailsId'),
    		collectionDate:placemark.get('collectionDate')});
		marker.on('click', onMarkerClick);
    	return marker;
    }

    function onMarkerClick(marker){
    	// first deselect others
    	if (!Ember.none(FLOW.placemarkController.get('selectedMarker'))){
    		if (FLOW.placemarkController.selectedMarker.target.options.placemarkId != marker.target.options.placemarkId){
    			FLOW.placemarkController.selectedMarker.target.options.selected = false;
                FLOW.placemarkController.selectedMarker.target.setStyle({color:'#d46f12',
    	    		fillColor:'#edb660'});
    			FLOW.placemarkController.set('selectedMarker',null);
    		}
    	}

    	// now toggle this one
    	if (marker.target.options.selected) {
    		marker.target.setStyle({color:'#d46f12',
    	    		fillColor:'#edb660'});
    		marker.target.options.selected = false;
    		FLOW.placemarkController.set('selectedMarker',null);
    	} else {
    		marker.target.setStyle({color:'#d46f12',
	    		fillColor:'#433ec9'});
    		marker.target.options.selected = true;
    		FLOW.placemarkController.set('selectedMarker',marker);
    	}
    }
  }

});

FLOW.cartoController = Ember.ArrayController.create({
    content: null,
    map: null,
    marker: null,
    markerCoordinates: null,
    cartodbLayer: null,
    layerExistsCheck: false,
    questions: [],
    detailsPaneVisible: null,
    loadNamedMap: function(formId){
        var self = this;
        //TODO Clear map
        this.loadQuestions(formId); //Load questions
        var namedMapObject = {};
        namedMapObject['mapName'] = 'raw_data_'+formId;
        namedMapObject['tableName'] = 'raw_data_'+formId;
        namedMapObject['interactivity'] = ['lat','lon','data_point_id'];
        namedMapObject['query'] = 'SELECT * FROM raw_data_'+formId;

        for (var i=0; i<self.questions.length; i++) {
            for (var qg=0; qg<self.questions[i]['questions'].length; qg++) {
                namedMapObject['interactivity'].push("q"+self.questions[i]['questions'][qg]['keyId']);
            }
        }
        this.namedMapCheck(namedMapObject, formId);
    },

    /*Check if a named map exists. If one exists, call function to overlay it
    else call function to create a new one*/
    namedMapCheck: function(namedMapObject, formId){
        var self = this;
        $.get("/rest/cartodb/named_maps", function(data, status) {
            if (data.template_ids) {
                var mapExists = false;
                for (var i=0; i<data['template_ids'].length; i++) {
                    if (data['template_ids'][i] === namedMapObject.mapName) {
                        //named map already exists
                        mapExists = true;
                        break;
                    }
                }

                if (mapExists) {
                    //overlay named map
                    self.createLayer(formId);
                } else {
                    //create new named map
                    self.createNamedMap(namedMapObject, formId);
                }
            }
        });
    },

    //create named map
    createNamedMap: function(namedMapObject, formId){
        var self = this;

        //style of points for new layer
        var cartocss = "#"+namedMapObject.tableName+"{"
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
        configJsonData['interactivity'] = namedMapObject.interactivity;
        configJsonData['name'] = namedMapObject.mapName;
        configJsonData['cartocss'] = cartocss;
        configJsonData['sql'] = namedMapObject.query;

        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: '/rest/cartodb/named_maps',
            data: JSON.stringify(configJsonData), //stringify the payload before sending it
            dataType: 'json',
            success: function(namedMapData){
                if (namedMapData.template_id) {
                    self.createLayer(formId);
                }
            }
        });
    },

    /*this function overlays a named map on the cartodb map*/
    createLayer: function(formId){
        var self = this, pointDataUrl;

        //first clear any currently overlayed cartodb layer
        self.clearCartodbLayer();

        // add cartodb layer with one sublayer
        cartodb.createLayer(self.map, {
            user_name: FLOW.Env.appId,
            type: 'namedmap',
            named_map: {
                name: "raw_data_"+formId,
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
        .addTo(self.map)
        .done(function(layer) {
            layer.setZIndex(1000); //required to ensure that the cartodb layer is not obscured by the here maps base layers
            self.layerExistsCheck = true;
            self.cartodbLayer = layer;

            self.addCursorInteraction(layer);

            var dataLayer = layer.getSubLayer(0);
            dataLayer.setInteraction(true);

            dataLayer.on('featureClick', function(e, latlng, pos, data) {
                if (self.marker) {
                    self.map.removeLayer(self.marker);
                }

                FLOW.cartoController.set('markerCoordinates', [data.lat, data.lon]);
                FLOW.cartoController.set('detailsPaneVisible', true);

                //TODO: Replace with logic that pulls point data from Flow instead of CartoDB
                if ($.active > 0) {
                    var refreshIntervalId = setInterval(function () {
                        //keep checking if there are any pending ajax requests
                        if ($.active > 0) {
                            //keep displaying loading icon
                        } else { //if no pending ajax requests
                            // call function to load the clicked point details
                            pointDataUrl = '/rest/cartodb/raw_data?dataPointId='+data.data_point_id+'&formId='+formId;
                            $.get('/rest/cartodb/data_point?id='+data.data_point_id, function(pointData, status){
                                self.getCartodbPointData(pointDataUrl, pointData['row']['name'], pointData['row']['identifier']);
                            });
                            clearInterval(refreshIntervalId);
                        }
                    }, 500);
                } else {
                    // call function to load the clicked point details
                    pointDataUrl = '/rest/cartodb/raw_data?dataPointId='+data.data_point_id+'&formId='+formId;
                    $.get('/rest/cartodb/data_point?id='+data.data_point_id, function(pointData, status){
                        self.getCartodbPointData(pointDataUrl, pointData['row']['name'], pointData['row']['identifier']);
                    });
                }
            });
        });
    },

    getCartodbPointData: function(url, dataPointName, dataPointIdentifier){
        var self = this;

        $("#pointDetails").html("");

        $.get(url, function(pointData, status){
            if (pointData['answers'] != null) {
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
                if (self.questions.length > 0) {
                    self.geoshapeCoordinates = null;

                    //sort question groups by their order
                    self.questions.sort(function(a, b) {
                        return parseFloat(a.order) - parseFloat(b.order);
                    });

                    clickedPointContent += '<div class="mapInfoDetail" style="opacity: 1; display: inherit;">';

                    for (var qg=0; qg<self.questions.length; qg++) {
                        for (var i=0; i<self.questions[qg]['questions'].length; i++) {
                            for (column in pointData['answers']) {
                                var questionAnswer = pointData['answers'][column];
                                if (column.match(self.questions[qg]['questions'][i].keyId)) {
                                    clickedPointContent += '<h4>'+self.questions[qg]['questions'][i].text+'&nbsp;</h4>'
                                    +'<div style="float: left; width: 100%">';

                                    if (questionAnswer) {
                                        clickedPointContent += self.loadQuestionAnswer(self.questions[qg]['questions'][i].questionType, questionAnswer);
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
                    $('.geoshape-map').each(function(index){
                        FLOW.drawGeoShape($('.geoshape-map')[index], $(this).data('geoshape-object'));
                    });
                }
            } else {
                $('#pointDetails').html('<p class="noDetails">'+Ember.String.loc('_no_details') +'</p>');
            }
        });
    },

    loadQuestionAnswer: function(questionType, questionAnswer){
        var clickedPointContent = "", self = this;
        switch (questionType) {
            case "PHOTO":
            case "VIDEO":
                var mediaString = "", mediaJson = "", mediaFilename = "", mediaObject = {}, mediaOutput = "";
                if (questionAnswer.charAt(0) === '{') {
                    mediaJson = JSON.parse(questionAnswer);
                    mediaString = mediaJson.filename;
                } else {
                    mediaString = questionAnswer;
                }

                var mediaFileURL = FLOW.Env.photo_url_root+mediaString.substring(mediaString.lastIndexOf("/")+1);
                if (questionType == "PHOTO") {
                    mediaOutput = '<div class=":imgContainer photoUrl:shown:hidden">'
                    +'<a class="media" data-coordinates=\''
                    +((mediaJson.location) ? questionAnswer : '' )+'\' href="'
                    +mediaFileURL+'" target="_blank"><img src="'+mediaFileURL+'" alt=""/></a><br>'
                    +((mediaJson.location) ? '<a class="media-location" data-coordinates=\''+questionAnswer+'\'>'+Ember.String.loc('_show_photo_on_map')+'</a>' : '')
                    +'</div>';
                } else if (questionType == "VIDEO") {
                    mediaOutput = '<div><div class="media" data-coordinates=\''
                    +((mediaJson.location) ? questionAnswer : '' )+'\'>'+mediaFileURL+'</div><br>'
                    +'<a href="'+mediaFileURL+'" target="_blank">'+Ember.String.loc('_open_video')+'</a>'
                    +((mediaJson.location) ? '&nbsp;|&nbsp;<a class="media-location" data-coordinates=\''+questionAnswer+'\'>'+Ember.String.loc('_show_photo_on_map')+'</a>' : '')
                    +'</div>';
                }
                clickedPointContent += mediaOutput;
                break;
            case "GEOSHAPE":
                var geoshapeObject = FLOW.parseJSON(questionAnswer, "features");
                self.geoshapeCoordinates = geoshapeObject;

                if (geoshapeObject) {
                    clickedPointContent += '<div class="geoshape-map" data-geoshape-object=\''+questionAnswer+'\' style="width:100%; height: 100px; float: left"></div>'
                    +'<a style="float: left" class="project-geoshape" data-geoshape-object=\''+questionAnswer+'\'>'+Ember.String.loc('_project_onto_main_map')+'</a>'

                    if (geoshapeObject['features'][0]['geometry']['type'] === "Polygon"
                        || geoshapeObject['features'][0]['geometry']['type'] === "LineString"
                            || geoshapeObject['features'][0]['geometry']['type'] === "MultiPoint") {
                        clickedPointContent += '<div style="float: left; width: 100%">'+ Ember.String.loc('_points') +': '+geoshapeObject['features'][0]['properties']['pointCount']+'</div>';
                    }

                    if (geoshapeObject['features'][0]['geometry']['type'] === "Polygon"
                        || geoshapeObject['features'][0]['geometry']['type'] === "LineString") {
                        clickedPointContent += '<div style="float: left; width: 100%">'+ Ember.String.loc('_length') +': '+geoshapeObject['features'][0]['properties']['length']+'m</div>';
                    }

                    if (geoshapeObject['features'][0]['geometry']['type'] === "Polygon") {
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
                        return (questionType == "CASCADE") ? item.name : item.text;
                    }).join("|");
                } else {
                    cascadeString = questionAnswer;
                }
                clickedPointContent += cascadeString;
                break;
            default:
                clickedPointContent += questionAnswer
        }
        return clickedPointContent;
    },

    loadQuestions: function(formId){
        var self = this;
        var qGroups = FLOW.store.filter(FLOW.QuestionGroup, function (qgItem) {
            return qgItem.get('surveyId') == formId;
        });
        qGroups.forEach(function (qgItem) {
            var questionGroup = {};
            questionGroup['id'] = qgItem.get('keyId');
            questionGroup['order'] = qgItem.get('order');
            questionGroup['questions'] = [];

            var qs = FLOW.store.filter(FLOW.Question, function (qItem) {
                return qItem.get('questionGroupId') == qgItem.get('keyId');
            });
            qs.forEach(function (qItem) {
                var question = {};
                question['keyId'] = qItem.get('keyId');
                question['order'] = qItem.get('order');
                question['questionType'] = qItem.get('type');
                question['text'] = qItem.get('text');
                questionGroup['questions'].push(question);
            });
            questionGroup['questions'].sort(function(a, b) {
                return parseFloat(a.order) - parseFloat(b.order);
            });
            self.questions.push(questionGroup);
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

    clearCartodbLayer: function(){
      //check to confirm that there are no layers displayed on the map
      if (this.layerExistsCheck) {
        this.map.removeLayer(this.cartodbLayer);
        this.layerExistsCheck = false;
      }
    },

    formatDate: function(date) {
      if (date && !isNaN(date.getTime())) {
        return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
      }
      return null;
    }
});

FLOW.placemarkDetailController = Ember.ArrayController.create({
  content: Ember.A(),
  sortProperties: ['order'],
  sortAscending: true,
  collectionDate: null,

  populate: function (placemarkId) {
	  if (placemarkId) {
		  this.set('content', FLOW.store.findQuery(FLOW.PlacemarkDetail, {
			  placemarkId: placemarkId
		  }));
	  } else {
		  this.set('content', Ember.A());
	  }
  },

  handlePlacemarkSelection: function () {
    var selectedPlacemarkId = null;
    if (!Ember.none(FLOW.placemarkController.get('selectedMarker'))) {
    	selectedPlacemarkId = FLOW.placemarkController.selectedMarker.target.options.placemarkId;
    	this.set('collectionDate',FLOW.placemarkController.selectedMarker.target.options.collectionDate);
    }
    this.populate(selectedPlacemarkId);
  }.observes('FLOW.placemarkController.selectedMarker'),

  photoUrl: function () {
    var photoDetails, photoUrls = [],
      rawPhotoUrl, photoJson;

    if (!this.get('content').get('isLoaded')) {
      return null;
    }

    // filter out details with images
    photoDetails = this.get('content').filter(function (detail) {
      return detail.get('questionType') === 'PHOTO';
    });

    if (Ember.empty(photoDetails)) {
      return null;
    }

    photoDetails.forEach(function (photo) {
      rawPhotoUrl = photo.get('stringValue') || '';
      if (rawPhotoUrl.charAt(0) === '{') {
        photoJson = JSON.parse(rawPhotoUrl);
        rawPhotoUrl = photoJson.filename;
      }
      // Since photos have a leading path from devices that we need to trim
      photoUrls.push(FLOW.Env.photo_url_root + rawPhotoUrl.split('/').pop());
    });

    return Ember.ArrayController.create({
      content: photoUrls
    });
  }.property('content.isLoaded')

});
