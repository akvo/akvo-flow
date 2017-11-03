/**
  Controllers related to the map tab
  Definition:
    "placemark" is an FLOW object that represents a single survey point.
    "marker" is a map object that is rendered as a pin. Each marker have
      a placemark counterpart.
**/

FLOW.mapsController = Ember.ArrayController.create({
    content: null,
    map: null,
    marker: null,
    markerCoordinates: null,
    cartodbLayer: null,
    layerExistsCheck: false,
    questions: null,
    detailsPaneVisible: null,
    geocellCache: [],
    currentGcLevel: null,
    allPlacemarks: null,
    selectedMarker:null,
    selectedSI: null,
    questionAnswers: null,

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
        if (zoomlevel < 4) {
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
        for (var i = 0; i < bestBB.length; i++) {
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
        if (!Ember.empty(bbString)) {
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
            if (!Ember.none(FLOW.mapsController.get('selectedMarker'))) {
                if (FLOW.mapsController.selectedMarker.target.options.placemarkId != marker.target.options.placemarkId) {
                    FLOW.mapsController.selectedMarker.target.options.selected = false;
                    FLOW.mapsController.selectedMarker.target.setStyle({
                        color:'#d46f12',
                        fillColor:'#edb660'});
                    FLOW.mapsController.set('selectedMarker',null);
                }
            }

            // now toggle this one
            if (marker.target.options.selected) {
                marker.target.setStyle({
                    color:'#d46f12',
                    fillColor:'#edb660'});
                marker.target.options.selected = false;
                FLOW.mapsController.set('selectedMarker',null);
            } else {
                marker.target.setStyle({
                    color:'#d46f12',
                    fillColor:'#433ec9'});
                marker.target.options.selected = true;
                FLOW.mapsController.set('selectedMarker',marker);
            }
        }
    },

    loadNamedMap: function(formId){
        var self = this;
        //TODO Clear map
        this.loadQuestions(formId); //Load questions
        var namedMapObject = {};
        namedMapObject['mapName'] = 'raw_data_'+formId;
        namedMapObject['tableName'] = 'raw_data_'+formId;
        namedMapObject['interactivity'] = ['lat','lon','id'];
        namedMapObject['query'] = 'SELECT * FROM raw_data_'+formId;

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
                FLOW.mapsController.set('markerCoordinates', [data.lat, data.lon]);
                
                //get survey instance
                FLOW.placemarkDetailController.set( 'si', FLOW.store.find(FLOW.SurveyInstance, data.id));

                //get questions answers for clicked survey instance
                FLOW.placemarkDetailController.set('content', FLOW.store.findQuery(FLOW.QuestionAnswer, {
                    'surveyInstanceId' : data.id
                }));
            });
        });
    },

    loadQuestions: function(formId){
        this.set('questions', FLOW.store.findQuery(FLOW.Question, {
            'surveyId' : formId
        }));
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
  si: null,

  populate: function (placemarkId) {
	  if (placemarkId) {
		  this.set('content', FLOW.store.findQuery(FLOW.PlacemarkDetail, {
			  placemarkId: placemarkId
		  }));
	  } else {
		  this.set('content', Ember.A());
	  }
  },

  siDetails: function() {
      //add display name and identifier here
      this.set('collectionDate', this.si.get('collectionDate'));
  }.observes('si.isLoaded'),

  handlePlacemarkSelection: function () {
    var selectedPlacemarkId = null;
    if (!Ember.none(FLOW.mapsController.get('selectedMarker'))) {
    	selectedPlacemarkId = FLOW.mapsController.selectedMarker.target.options.placemarkId;
    	this.set('collectionDate',FLOW.mapsController.selectedMarker.target.options.collectionDate);
    }
    this.populate(selectedPlacemarkId);
  }.observes('FLOW.mapsController.selectedMarker'),

  photoUrl: function () {
    var photoDetails, photoUrls = [],
      rawPhotoUrl, photoJson;

    if (!this.get('content').get('isLoaded')) {
      return null;
    }

    // filter out details with images
    photoDetails = this.get('content').filter(function (detail) {
        if (FLOW.Env.mapsProvider === 'cartodb') {
            return detail.get('type') === 'IMAGE';
        }
        return detail.get('questionType') === 'PHOTO';
    });

    if (Ember.empty(photoDetails)) {
      return null;
    }

    photoDetails.forEach(function (photo) {
      rawPhotoUrl = photo.get(FLOW.Env.mapsProvider === 'cartodb' ? 'value' : 'stringValue') || '';
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
