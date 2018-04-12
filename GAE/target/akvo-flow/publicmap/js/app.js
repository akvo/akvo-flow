
loader.register('akvo-flow/templates/application/application-public', function(require) {

return Ember.Handlebars.compile("<div class=\"loadSave\">\n      {{#if FLOW.savingMessageControl.areSavingBool}}<div class=\"isSaving\">{{t _saving}}</div>\n      {{else}}\n        {{#if FLOW.savingMessageControl.areLoadingBool}}<div class=\"isLoading\">{{t _loading}}</div>\n        {{/if}}\n      {{/if}}\n  </div>\n  <header class=\"floats-in top\" id=\"header\" role=\"banner\">\n      <div class=\"widthConstraint\">\n            <h1>Akvo Flow</h1>\n            <ul>\n               <li class=\"logIn\"><a href=\"/admin/\"  class=\"smallButton\">{{t _log_in}}</a></li>\n            </ul>\n      </div>\n  </header>\n    <div id=\"pageWrap\" class=\"widthConstraint belowHeader public\">\n        {{outlet}}\n</div>\n\n   {{view FLOW.FooterView}}");

});

loader.register('akvo-flow/templates/application/footer-public', function(require) {

return Ember.Handlebars.compile(" <footer class=\"floats-in bottomPage smaller\" role=\"contentinfo\">\n    <div class=\"widthConstraint\">\n\t  <nav id=\"footerNav\" class=\"floats-in footItems\">\n\t\t<ul>\n\t\t\t<li class=\"footLink\"><a href=\"https://github.com/akvo/akvo-flow/releases\" title=\"Go to Software Updates\" target=\"_blank\" > {{t _software_updates}}</a></li>\n\t\t\t<li class=\"footLink\"><a href=\"http://flowsupport.akvo.org/\" title=\"Help and Support\" target=\"_blank\" > {{t _help_support}}</a></li>\n\t\t\t<li class=\"footLink\"><a href=\"http://akvo.org/help/akvo-policies-and-terms-2/akvo-flow-terms-of-use/\" title=\"Terms of Service\" target=\"_blank\" >{{t _terms_of_service}}</a></li>\n\t\t</ul>\n\t</nav>\n  </div>\n  <div><small>{{t _copyright}} &copy; 2012-2018 <a href=\"http://www.akvo.org\" title=\"akvo.org\" target=\"_blank\">akvo.org</a></small></div>\n</footer>\n");

});

loader.register('akvo-flow/templates/application/header-common', function(require) {

return Ember.Handlebars.compile("<li class=\"logOut\"><a href=\"/admin/logout.html\" class=\"smallButton\">{{t _log_out}}</a></li>\n");

});

loader.register('akvo-flow/templates/navMaps/nav-maps-common', function(require) {

return Ember.Handlebars.compile("<section id=\"main\" class=\"mapFlow floats-in middleSection\" role=\"main\">\n  {{#if view.allowFilters}}\n      {{#unless FLOW.projectControl.isLoading}}\n        {{view FLOW.SurveySelectionView}}\n      {{/unless}}\n      {{#if FLOW.selectedControl.selectedSurveyGroup}}\n        {{view Ember.Select\n            contentBinding=\"FLOW.surveyControl.arrangedContent\"\n            selectionBinding=\"view.selectedSurvey\"\n            optionLabelPath=\"content.code\"\n            optionValuePath=\"content.keyId\"\n            prompt=\"\"\n            promptBinding=\"Ember.STRINGS._select_form\"\n            classNames=\"form-selector\"\n        }}\n      {{/if}}\n  {{/if}}\n  <div id=\"dropdown-holder\">\n    <div id=\"mapDetailsHideShow\" class=\"drawHandle hideMapD\"></div>\n  </div>\n  <div id=\"flowMap\"></div>\n  {{#view FLOW.PlacemarkDetailView controllerBinding=\"FLOW.placemarkDetailController\"}}\n    <div id=\"pointDetails\">\n      {{#if content}}\n        <ul class=\"placeMarkBasicInfo floats-in\">\n            {{#if view.cartoMaps}}\n            <h3>{{surveyedLocaleDisplayName}}</h3>\n            <li>\n                <span>{{t _data_point_id}}:</span>\n                <label style=\"display: inline; margin: 0 0 0 5px;\">{{surveyedLocaleIdentifier}}</label>\n            </li><br>\n            {{/if}}\n          <li>\n            <span>{{t _collected_on}}:</span>\n            <div class=\"placeMarkCollectionDate\">\n              {{date2 collectionDate}}\n            </div>\n          </li>\n          <li></li>\n        </ul>\n        <div class=\"mapInfoDetail\">\n          {{#each arrangedContent}}\n            <p>{{placemarkDetail}}</p>\n            {{drawGeoshapes}}\n          {{else}}\n            <p class=\"noDetails\">{{t _no_details}}</p>\n          {{/each}}\n        </div>\n      {{else}}\n        <p class=\"noDetails\">{{t _no_details}}</p>\n      {{/if}}\n\n    </div>\n  {{/view}}\n  <div id=\"flowMapLegend\">\n    <h1>{{t _legend}}</h1>\n  </div>\n</section>\n\n<style>\n  #pointDetails > dl > div.defListWrap:nth-child(odd) {\n    background-color: rgb(204,214,214);\n  }\n</style>\n<script type=\"text/javascript\">\n(function(){\n\n  var dropDown = document.querySelector('#dropdown-holder');\n  var header = document.querySelector('header');\n  var footer = document.querySelector('footer');\n\n  function resizeMap() {\n\n    if (!document.querySelector('#flowMap')) {\n      // If we can't find the map element, assume we have changed tabs and remove listener\n      window.removeEventListener('resize', resizeMap);\n      return;\n    }\n\n    var totalHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);\n    var newHeight = totalHeight;\n\n    newHeight -= dropDown.offsetHeight;\n    newHeight -= header.offsetHeight;\n    newHeight -= footer.offsetHeight;\n    newHeight = newHeight * 0.95;\n\n    document.querySelector('#flowMap').style.height = newHeight + 'px';\n    document.querySelector('#pointDetails').style.height = newHeight + 'px';\n  }\n\n  window.addEventListener('resize', resizeMap);\n  resizeMap();\n})()\n</script>\n");

});

loader.register('akvo-flow/controllers/general-controllers-common', function(require) {
FLOW.dashboardLanguageControl = Ember.Object.create({
  dashboardLanguage: FLOW.Env.locale,

  content: [{ label: "English (Default)", value: "en"},
            { label: "Español", value: "es" },
            { label: "Français", value: "fr" },
            { label: "Bahasa Indonesia", value: "id"},
            { label: "Português", value: "pt" },
            { label: "Tiếng Việt", value: "vi"}],

  languageChanged: function () {
    var localeUrl = '/ui-strings.js?locale=' + this.dashboardLanguage;
    $.ajax({
      url: localeUrl,
      complete: function () {
        location.reload(false);
      },
    });
  }.observes('dashboardLanguage'),
});
 
 
FLOW.selectedControl = Ember.Controller.create({
  selectedSurveyGroup: null,
  selectedSurvey: null,
  selectedSurveys: [],
  selectedSurveyAllQuestions: null,
  selectedSurveyAssignment: null,
  dependentQuestion: null,
  selectedQuestionGroup: null,
  selectedQuestion: null,
  selectedOption: null,
  selectedDevice: null,
  selectedDevices: [],
  selectedDevicesPreview: [],
  selectedSurveysPreview: [],
  selectedForMoveQuestionGroup: null,
  selectedForCopyQuestionGroup: null,
  selectedForMoveQuestion: null,
  selectedForCopyQuestion: null,
  selectedCreateNewGroup: false,
  selectedSurveyOPTIONQuestions: null,
  selectedCascadeResource:null,
  selectedCaddisflyResource:null,
  radioOptions: "",
  cascadeImportNumLevels: null,
  cascadeImportIncludeCodes: null,

  // OptionQuestions:function (){
  //   console.log('optionquestions 1');
  // }.observes('this.selectedSurveyOPTIONQuestions'),

  // when selected survey changes, deselect selected surveys and question groups
  deselectSurveyGroupChildren: function () {
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.selectedControl.set('selectedSurveyAllQuestions', null);
    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedQuestion', null);
  }.observes('this.selectedSurveyGroup'),

  deselectSurveyChildren: function () {
    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedQuestion', null);
  }.observes('this.selectedSurvey')
});


// used in user tab
FLOW.editControl = Ember.Controller.create({
  newPermissionLevel: null,
  newUserName: null,
  newEmailAddress: null,
  editPermissionLevel: null,
  editUserName: null,
  editEmailAddress: null,
  editUserId: null,
  editAttributeName: null,
  editAttributeGroup: null,
  editAttributeType: null,
  editAttributeId: null
});


FLOW.tableColumnControl = Ember.Object.create({
  sortProperties: null,
  sortAscending: true,
  selected: null,
  content: null
});


// set by restadapter sideLoad meta
FLOW.metaControl = Ember.Object.create({
  numSILoaded: null, // used by data tab nextPage method
  numSLLoaded: null, // used by monitored data tab nextPage method
  since: null,
  num: null,
  message: null,
  status: null,
  cursorType: null,
}),


// set by javacript datepickers in views.js
FLOW.dateControl = Ember.Object.create({
  // filled by javacript datepicker defined in views.js and by inspect-data.handlebars
  // binding. This makes sure we can both pick a date with the datepicker, and enter
  // a date manually
  fromDate: null,
  toDate: null
});


FLOW.savingMessageControl = Ember.Object.create({
  areSavingBool: false,
  areLoadingBool: false,
  numberLoading: 0,

  numLoadingChange: function (delta) {
	  this.set('numberLoading',this.get('numberLoading') + delta);
	  if (this.get('numberLoading') < 0){
		  this.set('numberLoading', 0);
	  }
	  if (this.get('numberLoading') > 0) {
		  this.set('areLoadingBool', true);
	  } else {
		  this.set('areLoadingBool', false);
	  }
  },

  checkSaving: function () {
    if (FLOW.store.defaultTransaction.buckets.inflight.list.get('length') > 0) {
      this.set('areSavingBool', true);
    } else {
      this.set('areSavingBool', false);
    }
  }
});

});

loader.register('akvo-flow/controllers/maps-controllers-common', function(require) {
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
            if (!Ember.none(FLOW.router.mapsController.get('selectedMarker'))) {
                if (FLOW.router.mapsController.selectedMarker.target.options.placemarkId != marker.target.options.placemarkId) {
                    FLOW.router.mapsController.selectedMarker.target.options.selected = false;
                    FLOW.router.mapsController.selectedMarker.target.setStyle({
                        color:'#d46f12',
                        fillColor:'#edb660'});
                    FLOW.router.mapsController.set('selectedMarker',null);
                }
            }

            // now toggle this one
            if (marker.target.options.selected) {
                marker.target.setStyle({
                    color:'#d46f12',
                    fillColor:'#edb660'});
                marker.target.options.selected = false;
                FLOW.router.mapsController.set('selectedMarker',null);
            } else {
                marker.target.setStyle({
                    color:'#d46f12',
                    fillColor:'#433ec9'});
                marker.target.options.selected = true;
                FLOW.router.mapsController.set('selectedMarker',marker);
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
  sortProperties: ['order'],
  sortAscending: true,
  collectionDate: null,
  surveyedLocaleDisplayName: null,
  surveyedLocaleIdentifier: null,
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
      if (FLOW.Env.mapsProvider === 'cartodb') {
          this.set('surveyedLocaleDisplayName', this.si.get('surveyedLocaleDisplayName'));
          this.set('surveyedLocaleIdentifier', this.si.get('surveyedLocaleIdentifier'));
      }
      this.set('collectionDate', this.si.get('collectionDate'));
  }.observes('si.isLoaded'),

  handlePlacemarkSelection: function () {
    var selectedPlacemarkId = null;
    if (!Ember.none(FLOW.router.get('mapsController'))) {
      var mapsController = FLOW.router.get('mapsController');
      if (!Ember.none(mapsController.get('selectedMarker'))) {
      	selectedPlacemarkId = mapsController.selectedMarker.target.options.placemarkId;
      	this.set('collectionDate',mapsController.selectedMarker.target.options.collectionDate);
      }
      this.populate(selectedPlacemarkId);
    }
  }.observes('FLOW.router.mapsController.selectedMarker')

});

});

loader.register('akvo-flow/core-common', function(require) {
require('akvo-flow/templ-common');
// Ember.LOG_BINDINGS = true;
// Create the application
window.FLOW = Ember.Application.create({
  VERSION: '0.0.1'
});

/* Generic FLOW view that also handles language rerenders */
FLOW.View = Ember.View.extend({});

});

loader.register('akvo-flow/models/FLOWrest-adapter-v2-common', function(require) {
/*global DS*/
var get = Ember.get,
  set = Ember.set;

DS.FLOWRESTAdapter = DS.RESTAdapter.extend({
  serializer: DS.RESTSerializer.extend({
    primaryKey: function (type) {
      return "keyId";
    },
    keyForAttributeName: function (type, name) {
      return name;
    }
  }),

  sideload: function (store, type, json, root) {
    var msg, status, metaObj;
    this._super(store, type, json, root);

    this.setQueryCursor(type, json);

    // only change metaControl info if there is actual meta info in the server response
    // and if it does not come from a delete action. We detect this by looking if num == null
    metaObj = this.extractMeta(json);
    if (metaObj && !Ember.none(metaObj.message)) {

      if (type == FLOW.SurveyInstance
          || type == FLOW.SurveyedLocale
          && !Ember.none(this.extractMeta(json).num)) {
        FLOW.metaControl.set(type == FLOW.SurveyInstance ? 'numSILoaded' : 'numSLLoaded', this.extractMeta(json).num);
        FLOW.metaControl.set('since', this.extractMeta(json).since);
        FLOW.metaControl.set('num', this.extractMeta(json).num);
        FLOW.metaControl.set('cursorType', type);
      }
      msg = this.extractMeta(json).message;
      status = this.extractMeta(json).status;
      keyId = this.extractMeta(json).keyId;

      if (msg.indexOf('_') === 0) { // Response is a translatable message
        msg = Ember.String.loc(msg);
      }
      FLOW.metaControl.set('message', msg);
      FLOW.metaControl.set('status', status);
      FLOW.metaControl.set('keyId', keyId);
      FLOW.savingMessageControl.numLoadingChange(-1);
      FLOW.savingMessageControl.set('areSavingBool', false);

      if (status === 'preflight-delete-question') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.questionControl.deleteQuestion(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_question'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_question_text'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
        }
        return;
      }

      if (status === 'preflight-delete-questiongroup') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.questionGroupControl.deleteQuestionGroup(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_questiongroup'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_questiongroup_text'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
        }
        return;
      }

      if (status === 'preflight-delete-survey') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.surveyControl.deleteSurvey(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_survey'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_survey_text'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
        }
        return;
      }

      if (status === 'preflight-delete-surveygroup') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.surveyGroupControl.deleteSurveyGroup(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_surveygroup'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_surveygroup_text'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
        }
        return;
      }

      if (this.extractMeta(json).status === 'failed' || FLOW.metaControl.get('message') !== '') {
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', '' /*Ember.String.loc('_action_failed')*/ ); //FIXME
        FLOW.dialogControl.set('message', FLOW.metaControl.get('message'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
      }
    }
  },

  /*  Process the cursor returned by the query. The cursor is used for pagination requests
      and is based on the type of entities queried */
  setQueryCursor: function(type, json) {
    var cursorArray, cursorStart, cursorIndex;
    if (type === FLOW.SurveyedLocale) {
      cursorArray = FLOW.router.surveyedLocaleController.get('sinceArray');
    } else if (type === FLOW.SurveyInstance) {
      cursorArray = FLOW.surveyInstanceControl.get('sinceArray');
    } else {
      return;
    }

    cursorStart = this.extractSince(json);
    if (!cursorStart) {
      return;
    }

    cursorIndex = cursorArray.indexOf(cursorStart);
    if (cursorIndex === -1) {
      cursorArray.pushObject(cursorStart);
    } else {
      // drop all cursors after the current one
      cursorArray.splice(cursorIndex + 1, cursorArray.length);
    }

    if (type === FLOW.SurveyedLocale) {
      FLOW.router.surveyedLocaleController.set('sinceArray', cursorArray);
    } else if (type === FLOW.SurveyInstance) {
      FLOW.surveyInstanceControl.set('sinceArray', cursorArray);
    }
  },

  ajax: function (url, type, hash) {
    if (type === 'GET' && url.indexOf('rest/survey_groups/0') >= 0) {
      // Don't fetch the root folder. It doesn't exist.
      return;
    }

    this._super(url, type, hash);
    if (type == "GET") {
      if (url.indexOf('rest/survey_groups') >= 0) {
        FLOW.projectControl.set('isLoading', true);
      }
      FLOW.savingMessageControl.numLoadingChange(1);
    }
  },

  didFindRecord: function (store, type, json, id) {
    this._super(store, type, json, id);
    if (type === FLOW.SurveyGroup) {
      FLOW.projectControl.set('isLoading', false);
    }
    FLOW.savingMessageControl.numLoadingChange(-1);
  },

  didFindAll: function (store, type, json) {
    if (type === FLOW.SurveyGroup) {
      FLOW.projectControl.set('isLoading', false);
    }
    FLOW.savingMessageControl.numLoadingChange(-1);
    this._super(store, type, json);
  },

  didFindQuery: function (store, type, json, recordArray) {
    this._super(store, type, json, recordArray);
    if (type === FLOW.SurveyGroup) {
      FLOW.projectControl.set('isLoading', false);
    }
    FLOW.savingMessageControl.numLoadingChange(-1);
  },

  // adapted from standard ember rest_adapter
  // includes 'bulk' in the POST call, to allign
  // with updateRecords and deleteRecords behaviour.
  createRecords: function (store, type, records) {
    //do not bulk commit when creating questions and question groups
    if (FLOW.questionControl.get('bulkCommit')) {
      this.set('bulkCommit', false);
    }

    if (get(this, 'bulkCommit') === false) {
      return this._super(store, type, records);
    }

    var root = this.rootForType(type),
      plural = this.pluralize(root);

    var data = {};
    data[plural] = [];
    records.forEach(function (record) {
      data[plural].push(this.serialize(record, {
        includeId: true
      }));
    }, this);

    this.ajax(this.buildURL(root, 'bulk'), "POST", {
      data: data,
      context: this,
      success: function (json) {
        this.didCreateRecords(store, type, records, json);
      }
    });
  },


  updateRecords: function(store, type, records) {
    //if updating questions and question groups ordering, enable bulkCommit
    if (FLOW.questionControl.get('bulkCommit')) {
      this.set('bulkCommit', true);
    }
    this._super(store, type, records);
  },

  deleteRecords: function(store, type, records) {
    //do not bulk commit when deleting questions and question groups
    if (FLOW.questionControl.get('bulkCommit')) {
      this.set('bulkCommit', false);
    }
    this._super(store, type, records);
  }
});

});

loader.register('akvo-flow/models/store_def-common', function(require) {
FLOW.store = DS.Store.create({
  revision: 10,
  adapter: DS.FLOWRESTAdapter.create({
    bulkCommit: false,
    namespace: "rest",
    url: window.location.protocol + "//" + window.location.hostname +
         (window.location.port ? ':' + window.location.port : '')
  })
});

DS.JSONTransforms.array = {
  deserialize: function (serialized) {
    return Ember.none(serialized) ? null : serialized;
  },

  serialize: function (deserialized) {
    return Ember.none(deserialized) ? null : deserialized;
  }
};

});

loader.register('akvo-flow/templ-common', function(require) {
var get = Ember.get,
  fmt = Ember.String.fmt;

Ember.View.reopen({
  templateForName: function (name, type) {
    if (!name) {
      return;
    }

    var templates = get(this, 'templates'),
      template = get(templates, name);

    if (!template) {
      try {
        template = require('akvo-flow/templates/' + name);
      } catch (e) {
        throw new Ember.Error(fmt('%@ - Unable to find %@ "%@".', [this, type, name]));
      }
    }

    return template;
  }
});

});

loader.register('akvo-flow/views/maps/map-views-common', function(require) {
FLOW.NavMapsView = FLOW.View.extend({
  templateName: 'navMaps/nav-maps-common',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,
  map: null,
  marker: null,
  polygons: [],
  mapZoomLevel: 0,
  mapCenter: null,
  mediaMarkers: {},
  selectedMediaMarker: {},
  mediaMarkerSelected: {},
  geoModel: null,
  selectedSurvey: null,
  allowFilters: FLOW.Env.mapsProvider && FLOW.Env.mapsProvider === 'cartodb',

  init: function () {
    this._super();
    this.detailsPaneElements = "#pointDetails h2" +
      ", #pointDetails dl" +
      ", #pointDetails img" +
      ", #pointDetails .imgContainer" +
      ", .placeMarkBasicInfo" +
      ", .noDetails";
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
      FLOW.router.mapsController.adaptMap(bestBB, this.map.getZoom());
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
      FLOW.router.mapsController.set('map', this.map);
      this.geoModel = create_geomodel();
      //load points for the visible map
      this.redoMap();
    }

    // add scale indication to map
    L.control.scale({position:'topleft', maxWidth:150}).addTo(this.map);

    this.$('#mapDetailsHideShow').click(function () {
      self.toggleProperty('detailsPaneVisible');
    });

    self.set('detailsPaneVisible', false);

    self.detailsPanelListeners();
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

    this.map = L.map('flowMap', {scrollWheelZoom: true}).setView([26.11598592533351, 1.9335937499999998], 2);

    var bounds = new L.LatLngBounds(this.map.getBounds().getSouthWest(), this.map.getBounds().getNorthEast());

    this.map.options.maxBoundsViscosity = 1.0;
    this.map.options.maxBounds = bounds;
    this.map.options.maxZoom = 18;
    this.map.options.minZoom = 2;

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
    }).addTo(this.map),
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

    L.control.layers(baseLayers).addTo(this.map);

    FLOW.router.mapsController.set('map', this.map);

    this.map.on('click', function(e) {
      self.clearMap(); //remove any previously loaded point data
    });

    this.map.on('zoomend', function() {
      $('body, html, #flowMap').scrollTop(0);
    });
  },

  detailsPanelListeners: function(){
      var self = this;
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

  surveySelection: function () {
      this.clearMap();
      FLOW.router.mapsController.clearSurveyDataLayer();
      if (!Ember.none(this.get('selectedSurvey'))) {
          FLOW.router.mapsController.loadNamedMap(this.selectedSurvey.get('keyId'));
      }
  }.observes('this.selectedSurvey'),

  surveyGroupSelection: function () {
      this.clearMap();
      FLOW.router.mapsController.clearSurveyDataLayer();
  }.observes('FLOW.selectedControl.selectedSurveyGroup'),

  /**
    If a placemark is selected and the details pane is hidden make sure to
    slide out
  */
  handlePlacemarkDetails: function () {
    var details = FLOW.placemarkDetailController.get('content');

    this.showDetailsPane();
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

  clearMap: function() {
    var self = this;
    self.set('detailsPaneVisible', false);
    if (self.marker) {
      self.map.removeLayer(self.marker);
    }

    if (!Ember.empty(self.mediaMarkers)) {
      for (mediaMarker in self.mediaMarkers) {
        self.map.removeLayer(self.mediaMarkers[mediaMarker]);
      }
    }

    if (self.polygons.length > 0) {
      for (var i=0; i<self.polygons.length; i++) {
        self.map.removeLayer(self.polygons[i])
      }
      //restore the previous zoom level and map center
      self.map.setView(self.mapCenter, self.mapZoomLevel);
      self.polygons = [];
    }
  },

  /*Place a marker to highlight clicked point of layer on cartodb map*/
  placeMarker: function(latlng){
      //if there's a previously loaded marker, first remove it
      if (this.marker) {
          this.map.removeLayer(this.marker);
      }

      var markerIcon = new L.Icon({
          iconUrl: 'images/marker.svg',
          iconSize: [10, 10]
      });
      this.marker = new L.marker(FLOW.router.mapsController.get('markerCoordinates'), {icon: markerIcon});
      this.map.addLayer(this.marker);

      this.showDetailsPane();
  }.observes('FLOW.router.mapsController.markerCoordinates'),

  detailsPaneShowHide: function(){
      var button = this.$('#mapDetailsHideShow');
      var display = this.detailsPaneVisible;

      button.html('&lsaquo; ' + Ember.String.loc((display) ? '_hide' : '_show'));

      this.$('#flowMap').animate({
        width: (display) ? '75%' : '99.25%'
      }, 200);
      this.$('#pointDetails').animate({
        width: (display) ? '24.5%' : '0.25%'
      }, 200).css({
        overflow: (display) ? 'auto' : 'scroll-y',
        marginLeft: '-2px'
      });
      this.$(this.detailsPaneElements, '#pointDetails').animate({
        opacity: (display) ? '1' : '0',
        display: (display) ? 'inherit' : 'none'
      });
  }.observes('this.detailsPaneVisible'),

  showDetailsPane: function(){
      if (!this.detailsPaneVisible) {
        this.set('detailsPaneVisible', true);
      }
  }
});

FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailView = Ember.View.extend({
    cartoMaps: FLOW.Env.mapsProvider && FLOW.Env.mapsProvider === 'cartodb'
});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});

FLOW.GeoshapeMapView = FLOW.View.extend({
  templateName: 'navMaps/geoshape-map',
  geoshape: null,

  didInsertElement: function() {
    this.set('geoshape', JSON.parse(this.get('parentView.geoShapeObject')));
    if (this.get('isPolygon') || this.get('isLineString') || this.get('isMultiPoint')) {
      var containerNode = this.get('element').getElementsByClassName('geoshapeMapContainer')[0];
      containerNode.innerHTML = "";
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

});

loader.register('akvo-flow/controllers/controllers-public', function(require) {
// ***********************************************//
//                 controllers
// ***********************************************//
// Define the main application controller. This is automatically picked up by
// the application and initialized.
require('akvo-flow/core-common');
require('akvo-flow/flowenv');
require('akvo-flow/controllers/general-controllers-common');
require('akvo-flow/controllers/maps-controllers-common');

FLOW.ApplicationController = Ember.Controller.extend({});

FLOW.NavMapsController = Ember.Controller.extend();

});

loader.register('akvo-flow/main-public', function(require) {
require('akvo-flow/models/FLOWrest-adapter-v2-common');
require('akvo-flow/models/models-public');
require('akvo-flow/flowenv');
require('akvo-flow/controllers/controllers-public');
require('akvo-flow/views/views-public');
require('akvo-flow/router/router-public');

FLOW.initialize();

});

loader.register('akvo-flow/models/models-public', function(require) {
// ***********************************************//
//                 models and stores
// ***********************************************//
require('akvo-flow/core-common');
require('akvo-flow/models/store_def-common');

FLOW.BaseModel = DS.Model.extend({
  keyId: DS.attr('number'),
  savingStatus: null,

  // this method calls the checkSaving method on the savingMessageControl, which
  // checks if there are any records inflight. If yes, it sets a boolean,
  // so a saving message can be displayed. savingStatus is used to capture the
  // moment that nothing is being saved anymore, but in the previous event it was
  // so we can turn off the saving message.
  anySaving: function () {
    if (this.get('isSaving') || this.get('isDirty') || this.get('savingStatus')) {
      FLOW.savingMessageControl.checkSaving();
    }
    this.set('savingStatus', (this.get('isSaving') || this.get('isDirty')));
  }.observes('isSaving', 'isDirty')

});

FLOW.SurveyGroup = FLOW.BaseModel.extend({
  didDelete: function () {
    FLOW.surveyGroupControl.populate();
  },
  didUpdate: function () {
    FLOW.surveyGroupControl.populate();
  },
  didCreate: function () {
    FLOW.surveyGroupControl.populate();
  },

  description: DS.attr('string', {
    defaultValue: ''
  }),
  name: DS.attr('string', {
    defaultValue: ''
  }),
  createdDateTime: DS.attr('string', {
    defaultValue: ''
  }),
  lastUpdateDateTime: DS.attr('string', {
    defaultValue: ''
  }),
  // the code field is used as name
  code: DS.attr('string', {
    defaultValue: ''
  })
});


// Explicitly avoid to use belongTo and hasMany as
// Ember-Data lacks of partial loading
// https://github.com/emberjs/data/issues/51
FLOW.PlacemarkDetail = FLOW.BaseModel.extend({
  placemarkId: DS.attr('number'),
  collectionDate: DS.attr('number'),
  order: DS.attr('number'),
  questionText: DS.attr('string'),
  metricName: DS.attr('string'),
  stringValue: DS.attr('string'),
  questionType: DS.attr('string')
});

FLOW.Placemark = FLOW.BaseModel.extend({
  latitude: DS.attr('number'),
  longitude: DS.attr('number'),
  count: DS.attr('number'),
  level: DS.attr('number'),
  surveyId: DS.attr('number'),
  detailsId: DS.attr('number'),
  collectionDate: DS.attr('number')
});

});

loader.register('akvo-flow/router/router-public', function(require) {
require('akvo-flow/core-common');

FLOW.Router = Ember.Router.extend({
  enableLogging: true,
  loggedIn: false,
  location: 'none',
  root: Ember.Route.extend({
    doNavMaps: function (router, context) {
      router.transitionTo('navMaps');
    },
    index: Ember.Route.extend({
      route: '/',
      redirectsTo: 'navMaps'
    }),

    // ************************** MAPS ROUTER **********************************
    navMaps: Ember.Route.extend({
      route: '/maps',
      connectOutlets: function (router, context) {
        router.get('applicationController').connectOutlet('navMaps');
      }
    })
  })
});

});

loader.register('akvo-flow/views/maps/map-views-common-public', function(require) {
FLOW.NavMapsView = FLOW.View.extend({
  templateName: 'navMaps/nav-maps-common',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,
  map: null,
  geoModel: null,
  allowFilters: false,

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
      FLOW.router.mapsController.adaptMap(bestBB, this.map.getZoom());
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
    } else {
      // insert the map
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
    }

    // add scale indication to map
    L.control.scale({position:'topleft', maxWidth:150}).addTo(this.map);

    // couple listener to end of zoom or drag
    this.map.on('moveend', function (e) {
      self.redoMap();
    });

    FLOW.router.mapsController.set('map', this.map);
    this.geoModel = create_geomodel();

    //load points for the visible map
    this.redoMap();

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
    button.html('');

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
  }

});


FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailView = Ember.View.extend({});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});

});

loader.register('akvo-flow/views/views-public', function(require) {
// ***********************************************//
//                      Navigation views
// ***********************************************//
/*global tooltip, makePlaceholders */

require('akvo-flow/core-common');
require('akvo-flow/views/maps/map-views-common-public');


FLOW.ApplicationView = Ember.View.extend({
  templateName: 'application/application-public',
});


FLOW.locale = function (i18nKey) {
  return 'Ember.STRINGS._select_survey_group';
  // var i18nValue;
  // try {
  //   i18nValue = Ember.String.loc(i18nKey);
  // }
  // catch (err) {
  //   return i18nKey;
  // }
  // return i18nValue;
};

// ***********************************************//
//                      Handlebar helpers
// ***********************************************//
// localisation helper
Ember.Handlebars.registerHelper('t', function (i18nKey, options) {
  var i18nValue;
  try {
    i18nValue = Ember.String.loc(i18nKey);
  } catch (err) {
    return i18nKey;
  }
  return i18nValue;
});


Ember.Handlebars.registerHelper('tooltip', function (i18nKey) {
  var tooltip;
  try {
    tooltip = Ember.String.loc(i18nKey);
  } catch (err) {
    tooltip = i18nKey;
  }
  tooltip = Handlebars.Utils.escapeExpression(tooltip);
  return new Handlebars.SafeString(
    '<a href="#" class="helpIcon tooltip" title="' + tooltip + '">?</a>'
  );
});

FLOW.renderCaddisflyAnswer = function(json){
  var name = ""
  var imageUrl = ""
  var result = Ember.A();
  if (!Ember.empty(json)){
      try {
          var jsonParsed = JSON.parse(json);

          // get out image url
          if (!Ember.empty(jsonParsed.image)){
            imageUrl = FLOW.Env.photo_url_root + jsonParsed.image.trim();
          }

          // contruct html
          html = "<div><strong>" + name + "</strong></div>"
          html += jsonParsed.result.map(function(item){
                  return "<br><div>" + item.name + " : " + item.value + " " + item.unit + "</div>";
              }).join("\n");
          html += "<br>"
          html += "<div class=\"signatureImage\"><img src=\"" + imageUrl +"\"}} /></div>"
          return html;
      } catch (e) {
          return json;
      }
  } else {
    return "Wrong JSON format";
  }
}

Ember.Handlebars.registerHelper('placemarkDetail', function () {
  var answer, markup, question, cascadeJson, optionJson, cascadeString = "",
  questionType, imageSrcAttr, signatureJson, photoJson;

  question = Ember.get(this, 'questionText');
  answer = Ember.get(this, 'stringValue') || '';
  answer = answer.replace(/\|/g, ' | '); // geo, option and cascade data
  answer = answer.replace(/\//g, ' / '); // also split folder paths
  questionType = Ember.get(this, 'questionType');

  if (questionType === 'CASCADE') {

      if (answer.indexOf("|") > -1) {
        // ignore
      } else {
        cascadeJson = JSON.parse(answer);
        answer = cascadeJson.map(function(item){
          return item.name;
        }).join("|");
      }
  } else if ((questionType === 'VIDEO' || questionType === 'PHOTO') && answer.charAt(0) === '{') {
    photoJson = JSON.parse(answer)
    var mediaAnswer = photoJson.filename;

    var mediaFileURL = FLOW.Env.photo_url_root + mediaAnswer.split('/').pop().replace(/\s/g, '');
    if (questionType == "PHOTO") {
        answer = '<div class=":imgContainer photoUrl:shown:hidden">'
        +'<a class="media" href="'+mediaFileURL+'" target="_blank"><img src="'+mediaFileURL+'" alt=""/></a>'
        +'</div>';
    } else if (questionType == "VIDEO") {
        answer = '<div><div class="media">'+mediaFileURL+'</div><br>'
        +'<a href="'+mediaFileURL+'" target="_blank">'+Ember.String.loc('_open_video')+'</a>'
        +'</div>';
    }
  } else if (questionType === 'OPTION' && answer.charAt(0) === '[') {
    optionJson = JSON.parse(answer);
    answer = optionJson.map(function(item){
      return item.text;
    }).join("|");
  } else if (questionType === 'SIGNATURE') {
    imageSrcAttr = 'data:image/png;base64,';
    signatureJson = JSON.parse(answer);
    answer = signatureJson && imageSrcAttr + signatureJson.image || '';
    answer = answer && '<img src="' + answer + '" />';
    answer = answer && answer + '<div>' + Ember.String.loc('_signed_by') + ':' + signatureJson.name + '</div>' || '';
  } else if (questionType === 'DATE') {
    answer = renderTimeStamp(answer);
  } else if (questionType === 'CADDISFLY'){
    answer = FLOW.renderCaddisflyAnswer(answer)
  }

  markup = '<div class="defListWrap"><dt>' +
    question + ':</dt><dd>' +
    answer + '</dd></div>';

  return new Handlebars.SafeString(markup);
});

/*  Take a timestamp and render it as a date in format
    YYYY-mm-dd */
function renderTimeStamp(timestamp) {
  var d, t, date, month, year;
  t = parseInt(timestamp, 10);
  if (isNaN(t)) {
    return "";
  }

  d = new Date(t);
  if (!d){
	  return "";
  }
  date = d.getDate();
  month = d.getMonth() + 1;
  year = d.getFullYear();

  if (month < 10) {
    monthString = "0" + month.toString();
  } else {
    monthString = month.toString();
  }

  if (date < 10) {
    dateString = "0" + date.toString();
  } else {
    dateString = date.toString();
  }

  return year + "-" + monthString + "-" + dateString;
}

// translates values to labels for languages
Ember.Handlebars.registerHelper('toLanguage', function (value) {
  var label, valueLoc;
  label = "";
  valueLoc = Ember.get(this, value);

  FLOW.languageControl.get('content').forEach(function (item) {
    if (item.get('value') == valueLoc) {
      label = item.get('label');
    }
  });
  return label;
});

// add space to vertical bar helper
Ember.Handlebars.registerHelper('addSpace', function (property) {
  return Ember.get(this, property).replace(/\|/g, ' | ');
});

Ember.Handlebars.registerHelper("getServer", function () {
  var loc = window.location.href,
    pos = loc.indexOf("/admin");
  return loc.substring(0, pos);
});

// Register a Handlebars helper that instantiates `view`.
// The view will have its `content` property bound to the
// helper argument.
FLOW.registerViewHelper = function (name, view) {
  Ember.Handlebars.registerHelper(name, function (property, options) {
    options.hash.contentBinding = property;
    return Ember.Handlebars.helpers.view.call(this, view, options);
  });
};


FLOW.registerViewHelper('date2', Ember.View.extend({
  tagName: 'span',

  template: Ember.Handlebars.compile('{{view.formattedContent}}'),

  formattedContent: (function () {
    var content, d, curr_date, curr_month, curr_year, curr_hour, curr_min, monthString, dateString, hourString, minString;
    content = this.get('content');

    if (content === null) {
      return "";
    }

    d = new Date(parseInt(content, 10));
    curr_date = d.getDate();
    curr_month = d.getMonth() + 1;
    curr_year = d.getFullYear();
    curr_hour = d.getHours();
    curr_min = d.getMinutes();

    if (curr_month < 10) {
      monthString = "0" + curr_month.toString();
    } else {
      monthString = curr_month.toString();
    }

    if (curr_date < 10) {
      dateString = "0" + curr_date.toString();
    } else {
      dateString = curr_date.toString();
    }

    if (curr_hour < 10) {
      hourString = "0" + curr_hour.toString();
    } else {
      hourString = curr_hour.toString();
    }

    if (curr_min < 10) {
      minString = "0" + curr_min.toString();
    } else {
      minString = curr_min.toString();
    }

    return curr_year + "-" + monthString + "-" + dateString + "  " + hourString + ":" + minString;
  }).property('content')
}));


// ********************************************************//
//                      standard views
// ********************************************************//
// TODO check if doing this in View is not impacting performance,
// as some pages have a lot of views (all navigation elements, for example)
// one way could be use an extended copy of view, with the didInsertElement,
// for some of the elements, and not for others.
Ember.View.reopen({
  didInsertElement: function () {
    this._super();
    tooltip();
  }
});

Ember.Select.reopen({
  attributeBindings: ['size']
});

FLOW.FooterView = FLOW.View.extend({
  templateName: 'application/footer-public'
});

});
