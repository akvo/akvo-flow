// ***********************************************//
//                      Navigation views
// ***********************************************//
/*global tooltip, makePlaceholders */

require('akvo-flow/core-common');
require('akvo-flow/views/surveys/preview-view');
require('akvo-flow/views/surveys/notifications-view');
require('akvo-flow/views/surveys/translations-view');
require('akvo-flow/views/surveys/survey-group-views');
require('akvo-flow/views/surveys/survey-details-views');
require('akvo-flow/views/surveys/form-view');
require('akvo-flow/views/data/inspect-data-table-views');
require('akvo-flow/views/data/data-attribute-views');
require('akvo-flow/views/data/bulk-upload-view');
require('akvo-flow/views/data/monitoring-data-table-view');
require('akvo-flow/views/data/cascade-resources-view');
require('akvo-flow/views/data/data-approval-views');
require('akvo-flow/views/surveys/question-view');
require('akvo-flow/views/data/question-answer-view');
require('akvo-flow/views/reports/report-views');
require('akvo-flow/views/reports/export-reports-views');
require('akvo-flow/views/maps/map-views-common');
require('akvo-flow/views/messages/message-view');
require('akvo-flow/views/devices/devices-views');
require('akvo-flow/views/devices/assignments-list-tab-view');
require('akvo-flow/views/devices/assignment-edit-views');
require('akvo-flow/views/devices/survey-bootstrap-view');
require('akvo-flow/views/users/user-view');

FLOW.ApplicationView = Ember.View.extend({
  templateName: 'application/application',
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

Ember.Handlebars.registerHelper('newLines', function (text) {
  var answer = "";
  if (!Ember.none(Ember.get(this, text))) {
    answer = Ember.get(this, text).replace(/\n/g, '<br/>');
  }
  return new Handlebars.SafeString(answer);
});


Ember.Handlebars.registerHelper('if_blank', function (item) {
  var text;
  text = Ember.get(this, item);
  return (text && text.replace(/\s/g, "").length) ? new Handlebars.SafeString('') : new Handlebars.SafeString('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
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
    answer = photoJson.filename;
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

// translates values to labels for surveyPointTypes
Ember.Handlebars.registerHelper('toPointType', function (value) {
  var label, valueLoc;
  label = "";
  valueLoc = Ember.get(this, value);

  FLOW.surveyPointTypeControl.get('content').forEach(function (item) {
    if (item.get('value') == valueLoc) {
      label = item.get('label');
    }
  });
  return label;
});

// translates values to labels for attributeTypes
Ember.Handlebars.registerHelper('toAttributeType', function (value) {
  var label, valueLoc;
  label = "";
  valueLoc = Ember.get(this, value);

  FLOW.attributeTypeControl.get('content').forEach(function (item) {
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

// date format helper
Ember.Handlebars.registerHelper("date", function (property) {
  var d = new Date(parseInt(Ember.get(this, property), 10));
  var m_names = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

  var curr_date = d.getDate();
  var curr_month = d.getMonth();
  var curr_year = d.getFullYear();
  return curr_date + " " + m_names[curr_month] + " " + curr_year;
});

// format used in devices table
Ember.Handlebars.registerHelper("date1", function (property) {
  var d, curr_date, curr_month, curr_year, curr_hour, curr_min, monthString, dateString, hourString, minString;
  if (Ember.get(this, property) !== null) {
    d = new Date(parseInt(Ember.get(this, property), 10));
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
  } else {
    return "";
  }
});

// format used in devices table
Ember.Handlebars.registerHelper("date3", function (property) {
  var d, curr_date, curr_month, curr_year, monthString, dateString;
  if (Ember.get(this, property) !== null) {
    return renderTimeStamp(Ember.get(this, property));
  }
});

FLOW.parseJSON = function(jsonString, property) {
  try {
    var jsonObject = JSON.parse(jsonString);
    if (jsonObject[property].length > 0) {
        return jsonObject;
    } else {
      return null;
    }
  } catch (e) {
    return null;
  }
};

FLOW.addExtraMapBoxTileLayer = function(baseLayers) {
  if (FLOW.Env.extraMapboxTileLayerMapId
      && FLOW.Env.extraMapboxTileLayerAccessToken
      && FLOW.Env.extraMapboxTileLayerLabel) {
    var templateURL = 'https://{s}.tiles.mapbox.com/v4/' +
      FLOW.Env.extraMapboxTileLayerMapId +
      '/{z}/{x}/{y}.jpg?access_token=' +
      FLOW.Env.extraMapboxTileLayerAccessToken;
    var attribution = '<a href="http://www.mapbox.com/about/maps/" target="_blank">Terms &amp; Feedback</a>'
    baseLayers[FLOW.Env.extraMapboxTileLayerLabel] = L.tileLayer(templateURL, {
      attribution: attribution
    })
  }
}

FLOW.drawGeoShape = function(containerNode, geoShapeObject){
  containerNode.style.height = "150px";

  var geoshapeCoordinatesArray, geoShapeObjectType = geoShapeObject['features'][0]['geometry']['type'];
  if(geoShapeObjectType === "Polygon"){
    geoshapeCoordinatesArray = geoShapeObject['features'][0]['geometry']['coordinates'][0];
  } else {
    geoshapeCoordinatesArray = geoShapeObject['features'][0]['geometry']['coordinates'];
  }
  var points = [];

  for(var j=0; j<geoshapeCoordinatesArray.length; j++){
    points.push([geoshapeCoordinatesArray[j][1], geoshapeCoordinatesArray[j][0]]);
  }

  var center = FLOW.getCentroid(points);

  var geoshapeMap = L.map(containerNode, {scrollWheelZoom: false}).setView(center, 2);

  geoshapeMap.options.maxZoom = 18;
  geoshapeMap.options.minZoom = 2;
  var mbAttr = 'Map &copy; 1987-2014 <a href="http://developer.here.com">HERE</a>';
  var mbUrl = 'https://{s}.{base}.maps.cit.api.here.com/maptile/2.1/maptile/{mapID}/{scheme}/{z}/{x}/{y}/256/{format}?app_id={app_id}&app_code={app_code}';
  var normal = L.tileLayer(mbUrl, {
    scheme: 'normal.day.transit',
    format: 'png8',
    attribution: mbAttr,
    subdomains: '1234',
    mapID: 'newest',
    app_id: FLOW.Env.hereMapsAppId,
    app_code: FLOW.Env.hereMapsAppCode,
    base: 'base'
  }).addTo(geoshapeMap);
  var satellite  = L.tileLayer(mbUrl, {
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

  FLOW.addExtraMapBoxTileLayer(baseLayers);

  L.control.layers(baseLayers).addTo(geoshapeMap);

  //Draw geoshape based on its type
  if(geoShapeObjectType === "Polygon"){
    var geoShapePolygon = L.polygon(points).addTo(geoshapeMap);
    geoshapeMap.fitBounds(geoShapePolygon.getBounds());
  }else if (geoShapeObjectType === "MultiPoint") {
    var geoShapeMarkersArray = [];
    for (var i = 0; i < points.length; i++) {
      geoShapeMarkersArray.push(L.marker([points[i][0],points[i][1]]));
    }
    var geoShapeMarkers = L.featureGroup(geoShapeMarkersArray).addTo(geoshapeMap);
    geoshapeMap.fitBounds(geoShapeMarkers.getBounds());
  }else if (geoShapeObjectType === "LineString") {
    var geoShapeLine = L.polyline(points).addTo(geoshapeMap);
    geoshapeMap.fitBounds(geoShapeLine.getBounds());
  }
};

FLOW.getCentroid = function (arr) {
  return arr.reduce(function (x,y) {
    return [x[0] + y[0]/arr.length, x[1] + y[1]/arr.length]
  }, [0,0])
}

Ember.Handlebars.registerHelper("getServer", function () {
  var loc = window.location.href,
    pos = loc.indexOf("/admin");
  return loc.substring(0, pos);
});

Ember.Handlebars.registerHelper('sgName', function (property) {
  var sgId = Ember.get(this, property),
      sg = FLOW.surveyGroupControl.find(function (item) {
        return item.get && item.get('keyId') === sgId;
      });
  return sg && sg.get('name') || sgId;
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
//                      main navigation
// ********************************************************//
FLOW.NavigationView = Em.View.extend({
  templateName: 'application/navigation',
  selectedBinding: 'controller.selected',

  showMapsButton: function () {
      return FLOW.Env.showMapsTab;
  }.property('FLOW.Env.showMapsTab'),

  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:current navItem'.w(),

    navItem: function () {
      return this.get('item');
    }.property('item').cacheable(),

    isActive: function () {
      return this.get('item') === this.get('parentView.selected');
    }.property('item', 'parentView.selected').cacheable(),

    showDevicesButton: function () {
      return FLOW.permControl.get('canManageDevices');
    }.property(),

    eventManager: Ember.Object.create({
      click: function(event, clickedView) {

        // Add the active tab as a CSS class to html
        var html = document.querySelector('html');
        html.className = '';
        html.classList.add(FLOW.router.navigationController.selected);
      }
    }),
  }),

});

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


FLOW.DateField = Ember.TextField.extend({
  minDate: true,

  didInsertElement: function () {
    this._super();

    if (this.get('minDate')) {
      // datepickers with only future dates
      $("#from_date").datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        minDate: new Date(),
        onSelect: function (selectedDate) {
          $("#to_date").datepicker("option", "minDate", selectedDate);
          FLOW.dateControl.set('fromDate', selectedDate);
        }
      });

      $("#to_date").datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        minDate: new Date(),
        onSelect: function (selectedDate) {
          $("#from_date").datepicker("option", "maxDate", selectedDate);
          FLOW.dateControl.set('toDate', selectedDate);
        }
      });
    } else {
      // datepickers with all dates
      $("#from_date").datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        onSelect: function (selectedDate) {
          $("#to_date").datepicker("option", "minDate", selectedDate);
          FLOW.dateControl.set('fromDate', selectedDate);
        }
      });

      $("#to_date").datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        onSelect: function (selectedDate) {
          $("#from_date").datepicker("option", "maxDate", selectedDate);
          FLOW.dateControl.set('toDate', selectedDate);
        }
      });
    }
  }
});

FLOW.DateField2 = Ember.TextField.extend({
  didInsertElement: function () {
    this._super();

    this.$().datepicker({
      dateFormat: 'yy-mm-dd',
      defaultDate: new Date(),
      numberOfMonths: 1
    });
  }
});

// home screen view
FLOW.NavHomeView = Ember.View.extend({
  templateName: 'navHome/nav-home'
});

// project views
FLOW.NavProjectsView = Ember.View.extend({
  templateName: 'navProjects/nav-projects-main'
});

// surveys views
FLOW.NavSurveysView = Ember.View.extend({
  templateName: 'navSurveys/nav-surveys'
});
FLOW.NavSurveysMainView = Ember.View.extend({
  templateName: 'navSurveys/nav-surveys-main'
});

FLOW.NavSurveysEditView = Ember.View.extend({
  templateName: 'navSurveys/nav-surveys-edit'
});

FLOW.ManageNotificationsView = Ember.View.extend({
  templateName: 'navSurveys/manage-notifications'
});

FLOW.ManageTranslationsView = Ember.View.extend({
  templateName: 'navSurveys/manage-translations'
});

FLOW.EditQuestionsView = Ember.View.extend({
  templateName: 'navSurveys/edit-questions'
});

// devices views
FLOW.NavDevicesView = Ember.View.extend({
  templateName: 'navDevices/nav-devices'
});

FLOW.CurrentDevicesView = FLOW.View.extend({
  templateName: 'navDevices/devices-list-tab/devices-list'
});

FLOW.AssignSurveysOverviewView = FLOW.View.extend({
  templateName: 'navDevices/assignment-list-tab/assignment-list'
});

FLOW.EditSurveyAssignmentView = Ember.View.extend({
  templateName: 'navDevices/assignment-edit-tab/assignment-edit'
});

FLOW.SurveyBootstrapView = FLOW.View.extend({
  templateName: 'navDevices/bootstrap-tab/survey-bootstrap'
});

// data views
FLOW.NavDataView = Ember.View.extend({
  templateName: 'navData/nav-data'
});

FLOW.InspectDataView = Ember.View.extend({
  templateName: 'navData/inspect-data'
});

FLOW.ManageAttributesView = Ember.View.extend({
  templateName: 'navData/manage-attributes'
});

FLOW.BulkUploadView = Ember.View.extend({
  templateName: 'navData/bulk-upload'
});
FLOW.DataCleaningView = Ember.View.extend({
  templateName: 'navData/data-cleaning'
});

FLOW.CascadeResourcesView = Ember.View.extend({
	  templateName: 'navData/cascade-resources'
});

FLOW.MonitoringDataView = Ember.View.extend({
  templateName: 'navData/monitoring-data'
});

// reports views
FLOW.NavReportsView = Ember.View.extend({
  templateName: 'navReports/nav-reports'
});

FLOW.ExportReportsView = Ember.View.extend({
  templateName: 'navReports/export-reports'
});

FLOW.ChartReportsView = Ember.View.extend({
  templateName: 'navReports/chart-reports'
});

FLOW.StatisticsView = Ember.View.extend({
  templateName: 'navReports/statistics'
});


// applets
FLOW.BootstrapApplet = Ember.View.extend({
  templateName: 'navDevices/bootstrap-tab/applets/bootstrap-applet'
});

FLOW.rawDataReportApplet = Ember.View.extend({
  templateName: 'navReports/applets/raw-data-report-applet'
});

FLOW.comprehensiveReportApplet = Ember.View.extend({
  templateName: 'navReports/applets/comprehensive-report-applet'
});

FLOW.googleEarthFileApplet = Ember.View.extend({
  templateName: 'navReports/applets/google-earth-file-applet'
});

FLOW.surveyFormApplet = Ember.View.extend({
  templateName: 'navReports/applets/survey-form-applet'
});

FLOW.bulkImportApplet = Ember.View.extend({
  templateName: 'navData/applets/bulk-import-applet'
});

FLOW.rawDataImportApplet = Ember.View.extend({
  templateName: 'navData/applets/raw-data-import-applet'
});

// users views
FLOW.NavUsersView = Ember.View.extend({
  templateName: 'navUsers/nav-users'
});

// Messages views
FLOW.NavMessagesView = Ember.View.extend({
  templateName: 'navMessages/nav-messages'
});

// admin views
FLOW.NavAdminView = FLOW.View.extend({
  templateName: 'navAdmin/nav-admin'
});

FLOW.HeaderView = FLOW.View.extend({
  templateName: 'application/header-common'
});

FLOW.FooterView = FLOW.View.extend({
  templateName: 'application/footer'
});

// ********************************************************//
//             Subnavigation for the Data tabs
// ********************************************************//
FLOW.DatasubnavView = FLOW.View.extend({
  templateName: 'navData/data-subnav',
  selectedBinding: 'controller.selected',
  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:active'.w(),

    isActive: function () {
      return this.get('item') === this.get('parentView.selected');
    }.property('item', 'parentView.selected').cacheable(),

    showDataCleaningButton: function () {
        return FLOW.permControl.get('canCleanData');
    }.property(),

    showCascadeResourcesButton: function () {
      return FLOW.permControl.get('canManageCascadeResources');
    }.property(),

    showDataApprovalButton: function () {
        return FLOW.Env.enableDataApproval && FLOW.permControl.get('canManageDataAppoval');
    }.property(),
  })
});

// ********************************************************//
//             Subnavigation for the Device tabs
// ********************************************************//
FLOW.DevicesSubnavView = FLOW.View.extend({
  templateName: 'navDevices/devices-subnav',
  selectedBinding: 'controller.selected',
  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:active'.w(),

    isActive: function () {
      return this.get('item') === this.get('parentView.selected');
    }.property('item', 'parentView.selected').cacheable()
  })
});

// ********************************************************//
//             Subnavigation for the Reports tabs
// ********************************************************//
FLOW.ReportsSubnavView = Em.View.extend({
  templateName: 'navReports/reports-subnav',
  selectedBinding: 'controller.selected',
  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:active'.w(),

    isActive: function () {
      return this.get('item') === this.get('parentView.selected');
    }.property('item', 'parentView.selected').cacheable()
  })
});


// *************************************************************//
//             Generic table column view which supports sorting
// *************************************************************//
FLOW.ColumnView = Ember.View.extend({
  tagName: 'th',
  item: null,
  type: null,

  classNameBindings: ['isActiveAsc:sorting_asc', 'isActiveDesc:sorting_desc'],

  isActiveAsc: function () {
    return this.get('item') === FLOW.tableColumnControl.get('selected') && FLOW.tableColumnControl.get('sortAscending') === true;
  }.property('item', 'FLOW.tableColumnControl.selected', 'FLOW.tableColumnControl.sortAscending').cacheable(),

  isActiveDesc: function () {
    return this.get('item') === FLOW.tableColumnControl.get('selected') && FLOW.tableColumnControl.get('sortAscending') === false;
  }.property('item', 'FLOW.tableColumnControl.selected', 'FLOW.tableColumnControl.sortAscending').cacheable(),

  sort: function () {
    if ((this.get('isActiveAsc')) || (this.get('isActiveDesc'))) {
      FLOW.tableColumnControl.toggleProperty('sortAscending');
    } else {
      FLOW.tableColumnControl.set('sortProperties', [this.get('item')]);
      FLOW.tableColumnControl.set('selected', this.get('item'));
    }

    if (this.get('type') === 'device') {
      FLOW.deviceControl.getSortInfo();
    } else if (this.get('type') === 'assignment') {
      FLOW.surveyAssignmentControl.getSortInfo();
    } else if (this.get('type') === 'attribute') {
      FLOW.attributeControl.getSortInfo();
    } else if (this.get('type') === 'message') {
      FLOW.messageControl.getSortInfo();
    }
  }
});

var set = Ember.set,
  get = Ember.get;
Ember.RadioButton = Ember.View.extend({
  title: null,
  checked: false,
  group: "radio_button",
  disabled: false,

  classNames: ['ember-radio-button'],

  defaultTemplate: Ember.Handlebars.compile('<label><input type="radio" {{ bindAttr disabled="view.disabled" name="view.group" value="view.option" checked="view.checked"}} />{{view.title}}</label>'),

  bindingChanged: function () {
    if (this.get("option") == get(this, 'value')) {
      this.set("checked", true);
    }
  }.observes("value"),

  change: function () {
    Ember.run.once(this, this._updateElementValue);
  },

  _updateElementValue: function () {
    var input = this.$('input:radio');
    set(this, 'value', input.attr('value'));
  }
});

FLOW.SelectFolder = Ember.Select.extend({
  controller: null,

  init: function() {
    this._super();
    this.set('prompt', Ember.String.loc('_choose_folder_or_survey'));
    this.set('optionLabelPath', 'content.code');
    this.set('optionValuePath', 'content.keyId');
    this.set('controller', FLOW.SurveySelection.create({ selectionFilter: this.get('selectionFilter')}));
    this.set('content', this.get('controller').getByParentId(this.get('parentId'), this.get('showMonitoringSurveysOnly')));
  },

  onChange: function() {
    var childViews = this.get('parentView').get('childViews');
    var keyId = this.get('value');
    var survey = this.get('controller').getSurvey(keyId);
    var nextIdx = this.get('idx') + 1;
    var monitoringOnly = this.get('showMonitoringSurveysOnly');
    var filter = this.get('selectionFilter');

    if (nextIdx !== childViews.length) {
      childViews.removeAt(nextIdx, childViews.length - nextIdx);
    }

    if (this.get('controller').isSurvey(keyId)) {
      FLOW.selectedControl.set('selectedSurveyGroup', survey);
      if (FLOW.Env.enableDataApproval && survey.get('dataApprovalGroupId')) {
          FLOW.router.approvalGroupController.load(survey.get('dataApprovalGroupId'));
          FLOW.router.approvalStepsController.loadByGroupId(survey.get('dataApprovalGroupId'));
      }
    } else {
      FLOW.selectedControl.set('selectedSurveyGroup', null);
      childViews.pushObject(FLOW.SelectFolder.create({
        parentId: keyId,
        idx: nextIdx,
        showMonitoringSurveysOnly: monitoringOnly,
        selectionFilter : filter
      }));
    }
  }.observes('value'),
});


FLOW.SurveySelectionView = Ember.ContainerView.extend({
  tagName: 'div',
  classNames: 'modularSelection',
  childViews: [],

  init: function() {
    this._super();
    this.get('childViews').pushObject(FLOW.SelectFolder.create({
      parentId: 0, // start with the root folder
      idx: 0,
      showMonitoringSurveysOnly: this.get('showMonitoringSurveysOnly') || false
    }));
  },
})


FLOW.DataCleaningSurveySelectionView = Ember.ContainerView.extend({
  tagName: 'div',
  classNames: 'modularSelection',
  childViews: [],

  init: function() {
    this._super();
    this.get('childViews').pushObject(FLOW.SelectFolder.create({
      parentId: 0, // start with the root folder
      idx: 0,
      showMonitoringSurveysOnly: this.get('showMonitoringSurveysOnly') || false,
      selectionFilter : FLOW.projectControl.dataCleaningEnabled
    }));
  },
})
