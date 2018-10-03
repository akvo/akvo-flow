/**
  Controllers related to the map tab
  Definition:
    "placemark" is an FLOW object that represents a single survey point.
    "marker" is a map object that is rendered as a pin. Each marker have
      a placemark counterpart.
**/

FLOW.MapsController = Ember.ArrayController.extend({
    content: null,
    map: null,
    marker: null,
    markerCoordinates: null,
    questions: null,
    geocellCache: [],
    currentGcLevel: null,
    allPlacemarks: null,
    selectedMarker:null,
    selectedSI: null,
    questionAnswers: null,
    namedMap: null,
    surveyDataLayer: null,

    populateMap: function () {
        var gcLevel, placemarks, placemarkArray=[];

        if (this.content.get('isLoaded') && FLOW.selectedControl.selectedSurveyGroup) {
            var surveyId = FLOW.selectedControl.selectedSurveyGroup.get('keyId');

            placemarks = FLOW.store.filter(FLOW.Placemark, function(item){
                return item.get('surveyId') === surveyId;
            });

            if (!this.allPlacemarks) {
                this.set('allPlacemarks', L.layerGroup());
                this.allPlacemarks.addTo(this.map);
            }

            placemarks.forEach(function (placemark) {
                marker = this.addMarker(placemark);
                this.allPlacemarks.addLayer(marker);
            }, this);
        }
    }.observes('this.content.isLoaded'),

    adaptMap: function(bestBB, zoomlevel){
        var bbString = "", gcLevel = 0, listToRetrieve = [], surveyId;

        this.set('currentGcLevel',gcLevel);
        // on zoomlevel 2, the map repeats itself, leading to wrong results
        // therefore, we force to download the highest level on all the world.
        if (zoomlevel == 2) {
            bestBB = "0123456789abcdef".split("");
        }

        for (var i = 0; i < bestBB.length; i++) {
            listToRetrieve.push(bestBB[i]);
        }

        // pack best bounding box values in a string for sending to the server
        bbString = listToRetrieve.join(',');

        // go get it in the datastore
        // when the points come in, populateMap will trigger and place the points
        if (!Ember.empty(bbString)) {
            var requestParams = { bbString: bbString, gcLevel: gcLevel };
            if (FLOW.selectedControl.selectedSurveyGroup) {
                requestParams.surveyId = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
            }
            this.set('content',FLOW.store.findQuery(FLOW.Placemark, requestParams));
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
                    placemarkId: placemark.get('keyId'),
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
                placemarkId: placemark.get('keyId'),
                collectionDate:placemark.get('collectionDate')});
            marker.on('click', onMarkerClick);
            return marker;
        }

        function onMarkerClick(marker){
            // first deselect others
            if (!Ember.none(FLOW.router.mapsController.get('selectedMarker'))) {
                if (FLOW.router.mapsController.selectedMarker.target.options.placemarkId != marker.target.options.placemarkId) {
                    FLOW.router.mapsController.selectedMarker.target.options.selected = false;
                    FLOW.router.mapsController.selectedMarker.target.setStyle({
                        color:'#d46f12',
                        fillColor:'#edb660'});
                    FLOW.router.mapsController.set('selectedMarker',null);
                    FLOW.questionAnswerControl.set('content', null); //clear answers from side bar
                }
            }

            // now toggle this one
            marker.target.setStyle({
                color:'#d46f12',
                fillColor:'#433ec9'});
            marker.target.options.selected = true;
            FLOW.router.mapsController.set('selectedMarker',marker);
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
                        self.set('namedMap', namedMapObject.mapName);
                        break;
                    }
                }

                if (!mapExists) {
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
                    self.set('namedMap', namedMapData.template_id);
                }
            }
        });
    },

    /*this function overlays a named map on the cartodb map*/
    createLayer: function(formId){
        if (this.namedMap) {
            var self = this;

            // add cartodb layer with one sublayer
            cartodb.createLayer(self.map, {
                user_name: FLOW.Env.appId,
                type: 'namedmap',
                named_map: {
                    name: this.namedMap,
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
                self.set('surveyDataLayer', layer);

                self.addCursorInteraction(layer);

                var dataLayer = layer.getSubLayer(0);
                dataLayer.setInteraction(true);

                dataLayer.on('featureClick', function(e, latlng, pos, data) {
                    self.set('markerCoordinates', [data.lat, data.lon]);

                    //get survey instance
                    FLOW.placemarkDetailController.set( 'si', FLOW.store.find(FLOW.SurveyInstance, data.id));

                    //get questions answers for clicked survey instance
                    FLOW.placemarkDetailController.set('content', FLOW.store.findQuery(FLOW.QuestionAnswer, {
                        'surveyInstanceId' : data.id
                    }));
                });
            });
        }
    }.observes('this.namedMap'),

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

    clearSurveyDataLayer: function(){
        if (this.surveyDataLayer) {
            this.map.removeLayer(this.surveyDataLayer);
            this.set('surveyDataLayer', null);
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

  dataPoint: null,

  dataPointCollectionDate: null,

  dataPointDisplayName: function () {
      return this.dataPoint && this.dataPoint.get('displayName')
  }.property('this.dataPoint.isLoaded'),

  dataPointIdentifier: function () {
      return this.dataPoint && this.dataPoint.get('identifier')
  }.property('this.dataPoint.isLoaded'),

  /*
  * Observer that loads a datapoint and its associated details when clicked
  */
  mapPointClickHandler: function () {
      var mapsController = FLOW.router.get('mapsController');
      if (mapsController && mapsController.get('selectedMarker')) {
          var selectedPlacemarkId = mapsController.selectedMarker.target.options.placemarkId;
          this.set('dataPointCollectionDate', mapsController.selectedMarker.target.options.collectionDate);
          this.set('dataPoint', FLOW.store.find(FLOW.SurveyedLocale, selectedPlacemarkId));
          FLOW.surveyInstanceControl.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {
                'surveyedLocaleId': selectedPlacemarkId,
          }));
      }
  }.observes('FLOW.router.mapsController.selectedMarker'),

  /*
  * Observer that retrieves question answers associated with a datapoint
  * when it is clicked on.
  *
  * !!! Only data from the REGISTRATION FORMS is loaded at the moment !!!
  */
  mapPointRetrieveDetailsHandler: function () {
      var formInstances = FLOW.surveyInstanceControl.content;
      var mapsController = FLOW.router.get('mapsController');

      if (Ember.empty(formInstances)
        || !formInstances.isLoaded
        || !mapsController.get('selectedMarker')) {
            return;
        }

      var survey = FLOW.projectControl.content.filterProperty('keyId', this.dataPoint.get('surveyGroupId')).get('firstObject');
      var registrationFormId, registrationFormInstance;
      if (survey) {
          registrationFormId = survey.get('newLocaleSurveyId');
          registrationFormInstance = formInstances.filterProperty('surveyId', registrationFormId).get('firstObject');
      }

      if (registrationFormInstance) {
          FLOW.questionAnswerControl.doQuestionAnswerQuery(registrationFormInstance);
      }
  }.observes('FLOW.surveyInstanceControl.content.isLoaded'),
});
