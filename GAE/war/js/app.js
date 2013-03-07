
loader.register('akvo-flow/templates/application/application-public', function(require) {

return Ember.Handlebars.compile("  <header class=\"floats-in top\" id=\"header\" role=\"banner\">\n        <div>\n            <hgroup>\n                <h1>Akvo\n                    <abbr title=\"field level operations watch\">Flow</abbr></h1>\n            </hgroup>\n            <nav id=\"topnav\" role=\"navigation\">\n                {{!view FLOW.NavigationView controllerBinding=\"controller.controllers.navigationController\"}}\n            </nav>\n    \t\t<div class=\"loadSave\">\n\t\t\t\t\t{{#if FLOW.savingMessageControl.areSavingBool}}<div class=\"isSaving\">{{t _saving}}</div>\n\t\t\t{{else}}\n\t\t\t  {{#if FLOW.savingMessageControl.areLoadingBool}}<div class=\"isLoading\">{{t _loading}}</div>\n\t\t\t  {{/if}}\n\t\t\t{{/if}}\n\t\t\t</div>\n            {{view FLOW.HeaderView}}\n            \n        </div>\n    </header>\n    <div id=\"pageWrap\">\n        {{outlet}}\n    </div>\n  \n    <div {{bindAttr class=\"FLOW.dialogControl.showDialog:display :overlay\"}}>\n      <div class=\"blanket\"></div>\n          <div class=\"dialogWrap\">\n        <!-- the dialog contents -->\n        <div class=\"confirmDialog dialog\">\n          <h2>{{FLOW.dialogControl.header}}</h2>\n          <p class=\"dialogMsg\">{{FLOW.dialogControl.message}}</p>\n          <br/><br/>\n          <div class=\"buttons menuCentre\"> \n            <ul>  \n              {{#if FLOW.dialogControl.showOK}} <li><a {{action \"doOK\" target=\"FLOW.dialogControl\"}} class=\"ok smallBtn\">{{t _ok}}</a></li>{{/if}}\n               {{#if FLOW.dialogControl.showCANCEL}} <li><a {{action \"doCANCEL\" target=\"FLOW.dialogControl\"}}} class=\"cancel\">{{t _cancel}}</a></li>{{/if}}\n            </ul>\n          </div>\n        </div>\n      </div>\n    </div>\n\n   {{view FLOW.FooterView}}");

});

loader.register('akvo-flow/templates/application/footer-public', function(require) {

return Ember.Handlebars.compile(" <footer class=\"floats-in bottomPage\" role=\"contentinfo\">\n    <div>\n\t  <nav id=\"footerNav\" class=\"floats-in\">\n\t\t<ul>\n\t\t\t<li><a href=\"http://www.akvo.org/blog/?cat=30\" title=\"Go to News and Software Updates\" target=\"_blank\" > {{t _news_and_software_updates}}</a></li>\n\t\t\t<li><a href=\"http://flowhelp.akvo.org\" title=\"Support\" target=\"_blank\" > {{t _support}}</a></li>\n\t\t\t<li><a href=\"http://flow.readthedocs.org/en/latest/index.html\" title=\"Documentation and User Guides\" target=\"_blank\" >{{t _documentation_and_user_guides}}</a></li>\n\t\t\t<li><a href=\"http://www.akvo.org/web/terms_of_use \" title=\"Terms of Service\" target=\"_blank\" >{{t _terms_of_service}}</a></li>\n\t\t\t<li><a href=\"http://www.akvo.org\" title=\"akvo.org\" target=\"_blank\" class=\"akvoDotOrg\">akvo.org</a></li>\n            <li><a href=\"/admin/logout.html\">{{t _log_out}}</a></li>\n\t\t</ul>\n\t</nav>  \n  </div>\n  <div><small>{{t _copyright}} &copy; 2013 akvo.org</small></div>\n  <p id=\"back-top\"> <a href=\"#header\"><span></span>Back to Top</a> </p>\n</footer>\n");

});

loader.register('akvo-flow/templates/application/header-public', function(require) {

return Ember.Handlebars.compile("<form>\n   <label class=\"languageSelector\"><span class=\"labelText\">{{t _dashboard_language}}:</span> {{view Ember.Select \n      contentBinding=\"FLOW.dashboardLanguageControl.content\" \n      optionLabelPath=\"content.label\" \n      optionValuePath=\"content.value\" \n      selectionBinding=\"FLOW.dashboardLanguageControl.dashboardLanguage\" }}\n   </label>\n</form>\n<section id=\"userLog\">\n  <ul>\n    <li class=\"userLogin\"><a href=\"#\">{{t _login}}</a></li>\n    <li class=\"userRegister\"><a href=\"#\">{{t _register}}</a></li>\n  </ul>\n  <ul>\n    <li></li>\n    <li></li>\n  </ul>\n</section>");

});

loader.register('akvo-flow/templates/navMaps/nav-maps-public', function(require) {

return Ember.Handlebars.compile("<section id=\"main\" class=\"mapFlow floats-in\" role=\"main\">\n  {{! <div id=\"drawHandleWrap\"></div>}}\n  <div id=\"dropdown-holder\">\n    {{#view FLOW.countryView controllerBinding=\"FLOW.countryController\"}}\n      {{#if FLOW.countryController.content}}\n        <label for=\"country\"><span class=\"inlined\">Country:</span>\n          {{view Ember.Select\n          contentBinding=\"FLOW.countryController.content\"\n          valueBinding=\"FLOW.countryController.selected\"\n          optionLabelPath=\"content.label\"\n          selectionBinding=\"FLOW.countryController.country\"}}\n        </label>\n      {{/if}}\n    {{/view}}\n    <div id=\"mapDetailsHideShow\" class=\"drawHandle hideMapD\">{{t _hide}} &rsaquo;</div>\n  </div>\n\n  <div id=\"flowMap\"></div>\n  {{#view FLOW.PlacemarkDetailView controllerBinding=\"FLOW.placemarkDetailController\"}}\n    <div id=\"pointDetails\">\n      {{#if content}}\n        <ul class=\"placeMarkBasicInfo floats-in\">\n          <li>\n            <span>{{t _collected_on}}:</span>\n            <div class=\"placeMarkCollectionDate\">\n              {{date2 FLOW.placemarkController.selected.collectionDate}}\n            </div>\n          </li>\n          <li>\n            <div class=\"placeMarkPointCode\"> \n              {{FLOW.placemarkDetailController.selectedPointCode}}\n            </div>\n          </li>\n        </ul>\n        <div {{bindAttr class=\":imgContainer photoUrl:shown:hidden\"}}>\n          <a {{bindAttr href=\"photoUrl\"}} target=\"_blank\">\n            <img {{bindAttr src=\"photoUrl\"}} alt=\"\">\n          </a>\n        </div>\n        <dl class=\"floats-in\">\n          {{#each content}}\n            {{placemarkDetail}}\n          {{else}}\n            <p class=\"noDetails\">{{t _no_details}}</p>\n          {{/each}}\n        </dl>\n      {{else}}\n        <p class=\"noDetails\">{{t _no_details}}</p>\n      {{/if}}\n      \n    </div>\n  {{/view}}\n  <div id=\"flowMapLegend\">\n    <h1>LEGEND</h1>\n  </div>\n</section>\n\n<style>\n  #pointDetails > dl > div.defListWrap:nth-child(odd) {\n    background-color: rgb(204,214,214);\n  }\n</style>");

});

loader.register('akvo-flow/controllers/controllers-public', function(require) {
// ***********************************************//
//                 controllers
// ***********************************************//
// Define the main application controller. This is automatically picked up by
// the application and initialized.
require('akvo-flow/core-public');
require('akvo-flow/flowenv');
require('akvo-flow/controllers/maps-controllers-public');
require('akvo-flow/controllers/general-controllers-public');

FLOW.ApplicationController = Ember.Controller.extend({
  init: function() {
    this._super();
    Ember.STRINGS = Ember.STRINGS_EN;
  }
});


// Navigation controllers
FLOW.NavigationController = Em.Controller.extend({
  selected: null
});

FLOW.NavMapsController = Ember.Controller.extend();

});

loader.register('akvo-flow/controllers/general-controllers-public', function(require) {
FLOW.dashboardLanguageControl = Ember.Object.create({
  dashboardLanguage: null,

  init: function() {
    var locale;

    this._super();
    locale = localStorage.locale;
    if(typeof locale === 'undefined') {
      this.set('dashboardLanguage', this.content.findProperty('value', 'en'));
    } else {
      this.set('dashboardLanguage', this.content.findProperty('value', locale));
    }
  },

  content: [
    Ember.Object.create({
      label: "English (Default)",
      value: "en"
    }), Ember.Object.create({
      label: "Español",
      value: "es"
    }), Ember.Object.create({
      label: "Français",
      value: "fr"
    })
  ],

  changeLanguage: function() {
    var locale;
    locale = this.dashboardLanguage.get("value");
    localStorage.locale = this.get('dashboardLanguage.value');

    if (locale === 'fr') {
      Ember.set('Ember.STRINGS', Ember.STRINGS_FR);
    } else if (locale === 'es') {
      Ember.set('Ember.STRINGS', Ember.STRINGS_ES);
    } else {
      Ember.set('Ember.STRINGS', Ember.STRINGS_EN);
    }

    // if(locale === "fr") {
    //   Ember.STRINGS = Ember.STRINGS_FR;
    // } else if(locale === "es") {
    //   Ember.STRINGS = Ember.STRINGS_ES;
    // } else {
    //   Ember.STRINGS = Ember.STRINGS_EN;
    // }
  }.observes('this.dashboardLanguage')
});

FLOW.savingMessageControl = Ember.Object.create({
  areSavingBool: false,
  areLoadingBool: false,

  checkSaving: function() {
    if(FLOW.store.defaultTransaction.buckets.inflight.list.get('length') > 0) {
      this.set('areSavingBool', true);
    } else {
      this.set('areSavingBool', false);
    }
  }
});


});

loader.register('akvo-flow/controllers/maps-controllers-public', function(require) {
/*jshint browser:true, jquery:true */
/*global Ember, FLOW */


FLOW.placemarkController = Ember.ArrayController.create({
  content: null,

  // We might be able to remove the buildURL in the REST adapter
  // and use this populate().
  // populate: function (country) {
  //   this.set('content', FLOW.store.findQuery(FLOW.Placemark,
  //     {country: country.get('iso')}));
  // }

  populate: function (country) {
    FLOW.countryController.set('countryCode', country.get('iso'));
    this.set('content', FLOW.store.findAll(FLOW.Placemark));
  }
});


FLOW.placemarkDetailController = Ember.ArrayController.create({
  content: Ember.A(),
  selectedPointCode: null,

  populate: function (placemark) {
    if (placemark && placemark.id) {
      this.set('content', FLOW.store.find(FLOW.PlacemarkDetail, {
        placemarkId: placemark.id
      }));
    } else {
      this.set('content', Ember.A());
    }
  },

  handlePlacemarkSelection: function() {
    var selected = FLOW.placemarkController.get('selected');

    this.populate(selected);
  }.observes('FLOW.placemarkController.selected'),

  photoUrl: function() {
    var photoDetails, photoUrl, rawPhotoUrl;


    if(!this.get('content').get('isLoaded')) {
      return null;
    }

    // filter out details with images
    photoDetails = this.get('content').filter(function (detail) {
      return detail.get('questionType') === 'PHOTO';
    });

    if(Ember.empty(photoDetails)) {
      return null;
    }

    // We only care for the first image
    rawPhotoUrl = photoDetails[0].get('stringValue');
    // Since photos have a leading path from devices that we need to trim
    photoUrl = FLOW.Env.photo_url_root + rawPhotoUrl.split('/').pop();

    return photoUrl;
  }.property('content.isLoaded')

});

FLOW.placemarkDetailPhotoController = Ember.ObjectController.create({
  photo: null
});


FLOW.countryController = Ember.ArrayController.create({
  content: [],
  country: null,
  countryCode: null,

  init: function() {
    this._super();
    if ( !Ember.none(FLOW.Env) && !Ember.none(FLOW.Env.countries) ) {
      this.set('content', this.getContent(FLOW.Env.countries));
    }
  },


  /**

  */
  handleCountrySelection: function () {
    FLOW.placemarkController.populate(this.country);
  }.observes('this.country'),


  getContent: function (countries) {
    var countryList = [];

    countries.sort(function (a, b) {
      if (a.name < b.name) return -1;
      if (a.name > b.name) return 1;
      return 0;
    });

    for (var i = 0; i < countries.length; i++) {
      if ( !Ember.none(countries[i].centroidLat) && !Ember.none(countries[i].centroidLon) ) {
        var zoom = 7; // default zoom level
        if (!Ember.none(countries[i].zoomLevel)) {
          zoom = countries[i].zoomLevel;
        }

        countryList.push(
          Ember.Object.create({
            label: countries[i].name,
            iso: countries[i].isoAlpha2Code,
            lat: countries[i].centroidLat,
            lon: countries[i].centroidLon,
            zoom: zoom
          })
        );
      }
    }
    return countryList;
  }

});
});

loader.register('akvo-flow/core-public', function(require) {
require('akvo-flow/templ-public');
// Ember.LOG_BINDINGS = true;
// Create the application
window.FLOW = Ember.Application.create({
  VERSION: '0.0.1'
});

/* Generic FLOW view that also handles lanague rerenders*/
FLOW.View = Ember.View.extend({
  onLanguageChange: function() {
    this.rerender();
  }.observes('FLOW.dashboardLanguageControl.dashboardLanguage')
});

});

loader.register('akvo-flow/main-public', function(require) {

require('akvo-flow/all_locales');
require('akvo-flow/models/FLOWrest-adapter-v2-public');
require('akvo-flow/models/models-public');

require('akvo-flow/core-public');
require('akvo-flow/flowenv');

// require('akvo-flow/controllers/controllers/maps-controller-public');
require('akvo-flow/controllers/controllers-public');
require('akvo-flow/views/views-public');
require('akvo-flow/router/router-public');

FLOW.initialize();

});

loader.register('akvo-flow/models/FLOWrest-adapter-v2-public', function(require) {
/*global DS*/

DS.FLOWRESTAdapter = DS.RESTAdapter.extend({
  serializer: DS.RESTSerializer.extend({
    primaryKey: function (type) {
      return "keyId";
    },
    keyForAttributeName: function (type, name) {
      return name;
    }
  }),

  buildURL: function (record, suffix) {
    var url;

    url = this._super(record, suffix);
    if (record === 'placemark') {
      return  url + '?country=' + FLOW.countryController.get('countryCode');
    }
    return url;
  },

  sideload: function (store, type, json, root) {
    var msg;
    this._super(store, type, json, root);
    // only change metaControl info if there is actual meta info in the server response
    if (Object.keys(this.extractMeta(json)).length !== 0) {
      FLOW.metaControl.set('since', this.extractMeta(json).since);
      FLOW.metaControl.set('num', this.extractMeta(json).num);

      msg = this.extractMeta(json).message;
      if (msg.indexOf('_') === 0) { // Response is a translatable message
        msg = Ember.String.loc(msg);
      }
      FLOW.metaControl.set('message', msg);

      FLOW.metaControl.set('status', this.extractMeta(json).status);
      FLOW.savingMessageControl.set('areLoadingBool', false);
      FLOW.savingMessageControl.set('areSavingBool', false);

      if (this.extractMeta(json).status === 'failed' || FLOW.metaControl.get('message') !== ''){
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', '' /*Ember.String.loc('_action_failed')*/); //FIXME
        FLOW.dialogControl.set('message', FLOW.metaControl.get('message'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
      }
    }
  },

 ajax: function(url, type, hash) {
   this._super(url, type, hash);
   if (type == "GET"){
     FLOW.savingMessageControl.set('areLoadingBool',true);
   }
 },

didFindRecord: function(store, type, json, id) {
  this._super(store, type, json, id);
  FLOW.savingMessageControl.set('areLoadingBool',false);
},

didFindAll: function(store, type, json) {
  this._super(store, type, json);
  FLOW.savingMessageControl.set('areLoadingBool',false);
},

didFindQuery: function(store, type, json, recordArray) {
  this._super(store, type, json, recordArray);
  FLOW.savingMessageControl.set('areLoadingBool',false);
}


});
});

loader.register('akvo-flow/models/models-public', function(require) {
// ***********************************************//
//                 models and stores
// ***********************************************//
require('akvo-flow/core-public');
require('akvo-flow/models/store_def-public');

FLOW.BaseModel = DS.Model.extend({
  keyId: DS.attr('number'),
  savingStatus: null,

  // this method calls the checkSaving method on the savingMessageControl, which
  // checks if there are any records inflight. If yes, it sets a boolean,
  // so a saving message can be displayed. savingStatus is used to capture the
  // moment that nothing is being saved anymore, but in the previous event it was
  // so we can turn off the saving message.
  anySaving: function() {
    if(this.get('isSaving') || this.get('isDirty') || this.get('savingStatus')) {
      FLOW.savingMessageControl.checkSaving();
    }
    this.set('savingStatus', (this.get('isSaving') || this.get('isDirty')));
  }.observes('isSaving', 'isDirty')

});

FLOW.SurveyGroup = FLOW.BaseModel.extend({
  didDelete: function() {
    FLOW.surveyGroupControl.populate();
  },
  didUpdate: function() {
    FLOW.surveyGroupControl.populate();
  },
  didCreate: function() {
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
  questionText: DS.attr('string'),
  metricName: DS.attr('string'),
  stringValue: DS.attr('string'),
  questionType: DS.attr('string')
});

FLOW.Placemark = FLOW.BaseModel.extend({
  latitude: DS.attr('number'),
  longitude: DS.attr('number'),
  collectionDate: DS.attr('number'),
  markType: DS.attr('string', {
    defaultValue: 'WATER_POINT'
  })
});

});

loader.register('akvo-flow/models/store_def-public', function(require) {
var host = "http://" + window.location.host;
FLOW.store = DS.Store.create({
	revision: 10,
	adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"rest", url:host})
});

DS.JSONTransforms.array = {
  deserialize: function(serialized) {
    return Ember.none(serialized) ? null : serialized;
  },

  serialize: function(deserialized) {
    return Ember.none(deserialized) ? null : deserialized;
  }
};
});

loader.register('akvo-flow/router/router-public', function(require) {
require('akvo-flow/core-public');

FLOW.Router = Ember.Router.extend({
  enableLogging: true,
  loggedIn: false,
  location: 'none',
  root: Ember.Route.extend({
    doNavMaps: function(router, context) {
      router.transitionTo('navMaps');
    },
    index: Ember.Route.extend({
      route: '/',
      redirectsTo: 'navMaps'
    }),

    // ************************** MAPS ROUTER **********************************
    navMaps: Ember.Route.extend({
      route: '/maps',
      connectOutlets: function(router, context) {
        router.get('applicationController').connectOutlet('navMaps');
        router.set('navigationController.selected', 'navMaps');
      }
    })
  })
});
});

loader.register('akvo-flow/templ-public', function(require) {
var get = Ember.get, fmt = Ember.String.fmt;

Ember.View.reopen({
  templateForName: function(name, type) {
    if (!name) { return; }

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

loader.register('akvo-flow/views/maps/map-views-public', function(require) {
/*jshint browser:true, jquery:true, laxbreak:true */
/*global Ember, mxn, FLOW*/
/**
  View that handles map page.
  Definition:
    "placemark" is an FLOW object that represents a single survey point.
    "marker" is a map object that is rendered as a pin. Each marker have
      a placemark counterpart.
**/

FLOW.NavMapsView = Ember.View.extend({
  templateName: 'navMaps/nav-maps-public',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,


  init: function() {
    this._super();
    this.detailsPaneElements = "#pointDetails h2" +
      ", #pointDetails dl" +
      ", #pointDetails img" +
      ", #pointDetails .imgContainer" +
      ", .placeMarkBasicInfo" +
      ", .noDetails";
    this.detailsPaneVisible = true;
  },


  /**
    Create the map once in DOM
  */
  didInsertElement: function() {
    var map = new mxn.Mapstraction('flowMap', 'google', true),
      latLon = new mxn.LatLonPoint(-0.703107, 36.765747),
      self;

    map.addControls({
      pan: true,
      zoom: 'small',
      map_type: true
    });

    map.setCenterAndZoom(latLon, 2);
    map.enableScrollWheelZoom();
    FLOW.placemarkController.set('map', map);

    self = this;
    this.$('#mapDetailsHideShow').click(function () {
      self.handleShowHideDetails();
    });
    // Slide in detailspane after 1 sec
    this.hideDetailsPane(1000);
  },


  /**

  */
  positionMap: function() {
    var country, latLon, map;

    country = FLOW.countryController.get('country');
    map = FLOW.placemarkController.get('map');
    if (!Ember.none(country)) {
      latLon = new mxn.LatLonPoint(country.get('lat'), country.get('lon'));
      map.getMap().clearOverlays();
      map.setCenterAndZoom(latLon, country.get('zoom'));
    }
  }.observes('FLOW.countryController.country'),


  /**
    Populate the map with markers
  */
  populateMap: function() {
    var map;

    if(FLOW.placemarkController.content.get('isUpdating') === false) {
      map = FLOW.placemarkController.get('map');
      FLOW.placemarkController.get('content').forEach(function(placemark) {
        map.addMarker(this.createMarker(placemark));
      }, this);
    }
  }.observes('FLOW.placemarkController.content.isUpdating'),


  /**

  */
  handleShowHideDetails: function () {
    if (this.detailsPaneVisible) {
      this.hideDetailsPane();
    } else {
      this.showDetailsPane();
    }
  },


  /**
    Handle placemark selection
  */
  handlePlacemarkDetails: function() {
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
    Slide in the details pane
  */
  showDetailsPane: function() {
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
    Populates the details pane with data from a placemark
  */
  populateDetailsPane: function (details) {
    var rawImagePath, verticalBars;

    this.set('showDetailsBool', true);
    details.forEach(function(item) {
      rawImagePath = item.get('stringValue');
      verticalBars = rawImagePath.split('|');
      if (verticalBars.length === 4) {
        FLOW.placemarkDetailController.set('selectedPointCode', verticalBars[3]);
      }
    }, this);
  },


  /**
    Slide out details pane
  */
  hideDetailsPane: function(delay) {
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
    });//.css({

    //});
  },


  /**
       Returns a marker(pin on the map) to represent the placemarker
    **/
  createMarker: function(placemark) {
    // Create a marker
    var point = new mxn.LatLonPoint(placemark.get('latitude'),
                                    placemark.get('longitude')),
      marker = new mxn.Marker(point);

    marker.setIcon('images/maps/blueMarker.png');
    marker.placemark = placemark;

    // Add a click handler that handles what happens when marker is clicked
    placemark.addMarkerClickHandler = function(marker) {
      var clickHandler = function(event_name, event_source, event_args) {
        /*jshint unused: true*/
        event_source.placemark.handleClick(event_source.placemark.marker);
        void(event_args); // Until unused:true is honored by JSHint
      };
      marker.click.addHandler(clickHandler);
    };


    /**
        When a marker is clicked we do different thing depending on
        the state of the map. E.g. if the same marker is clicked we deselect
        that marker and no marker is selected.
      **/
    placemark.handleClick = function(marker) {
      var oldSelected;

      marker.placemark.toggleMarker(marker.placemark);

      oldSelected = FLOW.placemarkController.get('selected');
      if(Ember.none(oldSelected)) {
        FLOW.placemarkController.set('selected', placemark);
      } else {
        if(this.marker === oldSelected.marker) {
          FLOW.placemarkController.set('selected', undefined);
        } else {
          oldSelected.toggleMarker(oldSelected);
          FLOW.placemarkController.set('selected', placemark);
        }
      }
    };


    /**
        Toggle between selected and deselected marker.
        In reality there is no toggle but delete and create
      **/
    placemark.toggleMarker = function(placemark) {
      var map = FLOW.placemarkController.get('map');
      var point = new mxn.LatLonPoint(placemark.get('latitude'),
                                      placemark.get('longitude')),
        newMarker = new mxn.Marker(point);

      if(placemark.marker.iconUrl === ('images/maps/blueMarker.png')) {
        newMarker.iconUrl = 'images/maps/redMarker.png' ;
      } else {
        newMarker.iconUrl = 'images/maps/blueMarker.png';
      }

      placemark.addMarkerClickHandler(newMarker);
      map.addMarker(newMarker);
      map.removeMarker(placemark.marker);
      newMarker.placemark = placemark;

      placemark.set('marker', newMarker);
    },

    placemark.addMarkerClickHandler(marker, placemark);
    // Attach the new marker to the placemarker object
    placemark.set('marker', marker);
    return marker;
  }

});

FLOW.countryView = Ember.View.extend({
  // country: null
});

FLOW.PlacemarkDetailView = Ember.View.extend({});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});


});

loader.register('akvo-flow/views/views-public', function(require) {
// ***********************************************//
//                      Navigation views
// ***********************************************//
/*global tooltip, makePlaceholders */

require('akvo-flow/core-public');
require('akvo-flow/views/maps/map-views-public');


FLOW.ApplicationView = Ember.View.extend({
  templateName: 'application/application-public',

  init: function() {
    var locale;

    this._super();

    // If available set language from local storage
    locale = localStorage.locale;
    if(typeof locale === 'undefined') {
      locale = 'en';
    }
    switch(locale) {
    case 'fr':
      Ember.STRINGS = Ember.STRINGS_FR;
      break;
    case 'es':
      Ember.STRINGS = Ember.STRINGS_ES;
      break;
    default:
      Ember.STRINGS = Ember.STRINGS_EN;
      break;
    }
  }
});


FLOW.locale = function(i18nKey) {
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
Ember.Handlebars.registerHelper('t', function(i18nKey, options) {
  var i18nValue;
  try {
    i18nValue = Ember.String.loc(i18nKey);
  }
  catch (err) {
    return i18nKey;
  }
  return i18nValue;
});


Ember.Handlebars.registerHelper('tooltip', function(i18nKey) {
  var tooltip;
  try {
    tooltip = Ember.String.loc(i18nKey);
  }
  catch (err) {
    tooltip = i18nKey;
  }
  return new Handlebars.SafeString(
    '<a href="#" class="helpIcon tooltip" title="' + tooltip + '">?</a>'
  );
});


Ember.Handlebars.registerHelper('placemarkDetail', function () {
  var answer, markup, question;

  question = Ember.get(this, 'questionText');
  answer = Ember.get(this, 'stringValue').replace(/\|/g, ' | ');

  markup = '<div class="defListWrap"><dt>' +
    question + ':</dt><dd>' +
    answer + '</dd></div>';

  return new Handlebars.SafeString(markup);
});


// translates values to labels for languages
Ember.Handlebars.registerHelper('toLanguage', function(value) {
  var label, valueLoc;
  label = "";
  valueLoc = Ember.get(this,value);

  FLOW.languageControl.get('content').forEach(function(item){
    if (item.get('value') == valueLoc) {
      label = item.get('label');
    }
  });
  return label;
});

// translates values to labels for surveyPointTypes
Ember.Handlebars.registerHelper('toPointType', function(value) {
  var label, valueLoc;
  label = "";
  valueLoc = Ember.get(this,value);

  FLOW.surveyPointTypeControl.get('content').forEach(function(item){
    if (item.get('value') == valueLoc) {
      label = item.get('label');
    }
  });
  return label;
});

// translates values to labels for attributeTypes
Ember.Handlebars.registerHelper('toAttributeType', function(value) {
  var label, valueLoc;
  label = "";
  valueLoc = Ember.get(this,value);

  FLOW.attributeTypeControl.get('content').forEach(function(item){
    if (item.get('value') == valueLoc) {
      label = item.get('label');
    }
  });
  return label;
});


// add space to vertical bar helper
Ember.Handlebars.registerHelper('addSpace', function(property) {
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
FLOW.registerViewHelper = function(name, view) {
  Ember.Handlebars.registerHelper(name, function(property, options) {
    options.hash.contentBinding = property;
    return Ember.Handlebars.helpers.view.call(this, view, options);
  });
};


// ********************************************************//
//                      main navigation
// ********************************************************//
FLOW.NavigationView = Em.View.extend({
  templateName: 'application/navigation-public',
  selectedBinding: 'controller.selected',

  onLanguageChange: function() {
    this.rerender();
  }.observes('FLOW.dashboardLanguageControl.dashboardLanguage'),

  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:current navItem'.w(),

    navItem: function() {
      return this.get('item');
    }.property('item').cacheable(),

    isActive: function() {
      return this.get('item') === this.get('parentView.selected');
    }.property('item', 'parentView.selected').cacheable()
  })
});

// ********************************************************//
//                      standard views
// ********************************************************//
// TODO check if doing this in View is not impacting performance,
// as some pages have a lot of views (all navigation elements, for example)
// one way could be use an extended copy of view, with the didInsertElement,
// for some of the elements, and not for others.
Ember.View.reopen({
  didInsertElement: function() {
    this._super();
    tooltip();
  }
});

Ember.Select.reopen({
  attributeBindings: ['size']
});


FLOW.HeaderView = FLOW.View.extend({
  templateName: 'application/header-public'
});

FLOW.FooterView = FLOW.View.extend({
  templateName: 'application/footer-public'
});

});
