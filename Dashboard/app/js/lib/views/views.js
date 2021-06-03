import { isNaN } from 'lodash';
import observe from '../mixins/observe';
import template from '../mixins/template';

// ***********************************************//
//                      Navigation views
// ***********************************************//

require('akvo-flow/core-common');
require('akvo-flow/views/surveys/preview-view');
require('akvo-flow/views/surveys/notifications-view');
require('akvo-flow/views/surveys/translations-view');
require('akvo-flow/views/surveys/survey-group-views');
require('akvo-flow/views/surveys/survey-details-views');
require('akvo-flow/views/surveys/form-view');
require('akvo-flow/views/data/inspect-data-table-views');
require('akvo-flow/views/data/bulk-upload-view');
require('akvo-flow/views/data/bulk-upload-images-view');
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
require('akvo-flow/views/devices/assignments-list-view');
require('akvo-flow/views/devices/assignment-edit-views');
require('akvo-flow/views/devices/survey-bootstrap-view');
require('akvo-flow/views/stats/new-stats');
require('akvo-flow/views/stats/stats-lists');
require('akvo-flow/views/surveys/form-share');
require('akvo-flow/views/surveys/offline-state');

FLOW.ApplicationView = Ember.View.extend(template('application/application'));

FLOW.locale = function() {
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
Ember.Handlebars.registerHelper('t', i18nKey => {
  let i18nValue;
  try {
    i18nValue = Ember.String.loc(i18nKey);
  } catch (err) {
    return i18nKey;
  }
  return i18nValue;
});

Ember.Handlebars.registerHelper('newLines', function(text) {
  let answer = '';
  if (!Ember.none(Ember.get(this, text))) {
    answer = Ember.get(this, text).replace(/\n/g, '<br/>');
  }
  return new Ember.Handlebars.SafeString(answer);
});

Ember.Handlebars.registerHelper('if_blank', function(item) {
  const text = Ember.get(this, item);
  return text && text.replace(/\s/g, '').length
    ? new Ember.Handlebars.SafeString('')
    : new Ember.Handlebars.SafeString(
      '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
    );
});

FLOW.TooltipQuestionMark = Ember.View.extend({
  tagName: 'a',

  template: Ember.Handlebars.compile('?'),

  classNames: ['helpIcon','tooltip'],

  attributeBindings: ['tooltipText:data-title'],

  tooltipText: null,

  eventManager: Ember.Object.create({
    xOffset: 10,

    yOffset: 20,

    mouseEnter: function (e) {
      let tooltipText = $(e.target).attr("data-title");
      $("body").append("<p id='tooltip'>" + tooltipText + "</p>");
      $("#tooltip")
        .css("top", (e.pageY - this.get('xOffset')) + "px")
        .css("left", (e.pageX + this.get('yOffset')) + "px")
        .fadeIn("fast");
    },

    mouseLeave: function () {
      $("#tooltip").remove();
    },

    mouseMove: function (e) {
      $("#tooltip")
        .css("top", (e.pageY - this.get('xOffset')) + "px")
        .css("left", (e.pageX + this.get('yOffset')) + "px")
    },
  }),
});

FLOW.TooltipText = FLOW.TooltipQuestionMark.extend({

  classNames: ['addSurvey', 'noChanges', 'tooltip'],

  i18nTooltipKey: null,

  init() {
    this._super();

    // this class causes the button to be styled in unwanted way
    this.get('classNames').removeObject('helpIcon');

    this.set('tooltipText', Ember.String.loc(this.get('i18nTooltipKey')));
  },
});

Ember.Handlebars.registerHelper('tooltip', function(i18nKey, options) {
  let tooltipText;

  try {
    tooltipText = Ember.String.loc(i18nKey);
  } catch (err) {
    tooltipText = i18nKey;
  }

  options.hash.tooltipText = tooltipText;

  const path = 'FLOW.TooltipQuestionMark';

  return Ember.Handlebars.ViewHelper.helper(this, path, options);
});

FLOW.renderCaddisflyAnswer = function(json) {
  if (!Ember.empty(json)) {
    try {
      const jsonParsed = JSON.parse(json);

      // contruct html
      let html = `<div><strong>${jsonParsed.name}</strong></div>`;
      html += jsonParsed.result
        .map(item => `<br><div>${item.name} : ${item.value} ${item.unit}</div>`)
        .join('\n');
      html += '<br>';

      // get out image url
      if ('image' in jsonParsed) {
        const imageUrl = FLOW.Env.photo_url_root + jsonParsed.image.trim();
        html += `<div class="signatureImage"><img src="${imageUrl}"/></div>`;
      }
      return html;
    } catch (e) {
      return json;
    }
  } else {
    return 'Wrong JSON format';
  }
};

Ember.Handlebars.registerHelper('placemarkDetail', function() {
  const responseType = Ember.get(this, 'type');
  const question = Ember.get(this, 'questionText');
  let answer = Ember.get(this, 'value') || '';
  answer = answer.replace(/\|/g, ' | '); // geo, option and cascade data
  if (responseType !== 'SIGNATURE') {
    answer = answer.replace(/\//g, ' / '); // also split folder paths
    answer = answer.replace(/\\/g, ''); // remove escape characters
  }

  if (responseType === 'CASCADE') {
    if (answer.indexOf('|') > -1) {
      // ignore
    } else if (answer.charAt(0) === '[') {
      const cascadeJson = JSON.parse(answer);
      answer = cascadeJson.map(item => item.name).join('|');
    }
  } else if (
    (responseType === 'VIDEO' || responseType === 'IMAGE') &&
    answer.charAt(0) === '{'
  ) {
    const photoJson = JSON.parse(answer);
    const mediaAnswer = photoJson.filename;

    const mediaFileURL =
      FLOW.Env.photo_url_root +
      mediaAnswer
        .split('/')
        .pop()
        .replace(/\s/g, '');
    if (responseType === 'IMAGE') {
      answer = `${'<div class=":imgContainer photoUrl:shown:hidden">' +
        '<a class="media" data-coordinates=\''}${
        photoJson.location ? answer : ''
      }' href="${mediaFileURL}" target="_blank"><img src="${mediaFileURL}" alt=""/></a><br>${
        photoJson.location
          ? `<a class="media-location" data-coordinates='${answer}'>${Ember.String.loc(
            '_show_photo_on_map'
          )}</a>`
          : ''
      }</div>`;
    } else if (responseType === 'VIDEO') {
      answer =
        `<div><div class="media" data-coordinates='${
          photoJson.location ? answer : ''
        }'><video controls><source src="${mediaFileURL}" type="video/mp4"></video></div><br>` +
        `<a href="${mediaFileURL}" target="_blank">${Ember.String.loc(
          '_open_video'
        )}</a>${
          photoJson.location
            ? `&nbsp;|&nbsp;<a class="media-location" data-coordinates='${answer}'>${Ember.String.loc(
              '_show_photo_on_map'
            )}</a>`
            : ''
        }</div>`;
    }
  } else if (responseType === 'OPTION' && answer.charAt(0) === '[') {
    const optionJson = JSON.parse(answer);
    answer = optionJson.map(item => item.text).join('|');
  } else if (responseType === 'SIGNATURE') {
    const imageSrcAttr = 'data:image/png;base64,';
    const signatureJson = JSON.parse(answer);
    answer = (signatureJson && imageSrcAttr + signatureJson.image) || '';
    answer = answer && `<img src="${answer}" />`;
    answer =
      (answer &&
        `${answer}<div>${Ember.String.loc('_signed_by')}:${
          signatureJson.name
        }</div>`) ||
      '';
  } else if (responseType === 'DATE') {
    answer = FLOW.renderTimeStamp(answer);
  } else if (responseType === 'CADDISFLY') {
    answer = FLOW.renderCaddisflyAnswer(answer);
  } else if (responseType === 'VALUE' && answer.indexOf('features":[') > 0) {
    const geoshapeObject = FLOW.parseJSON(answer, 'features');
    if (geoshapeObject) {
      answer =
        `<div class="geoshape-map" data-geoshape-object='${answer}' style="width:100%; height: 100px; float: left"></div>` +
        `<a style="float: left" class="project-geoshape" data-geoshape-object='${answer}'>${Ember.String.loc(
          '_project_onto_main_map'
        )}</a>`;

      if (
        geoshapeObject.features[0].geometry.type === 'Polygon' ||
        geoshapeObject.features[0].geometry.type === 'LineString' ||
        geoshapeObject.features[0].geometry.type === 'MultiPoint'
      ) {
        answer += `<div style="float: left; width: 100%">${Ember.String.loc(
          '_points'
        )}: ${geoshapeObject.features[0].properties.pointCount}</div>`;
      }

      if (
        geoshapeObject.features[0].geometry.type === 'Polygon' ||
        geoshapeObject.features[0].geometry.type === 'LineString'
      ) {
        answer += `<div style="float: left; width: 100%">${Ember.String.loc(
          '_length'
        )}: ${geoshapeObject.features[0].properties.length}m</div>`;
      }

      if (geoshapeObject.features[0].geometry.type === 'Polygon') {
        answer += `<div style="float: left; width: 100%">${Ember.String.loc(
          '_area'
        )}: ${geoshapeObject.features[0].properties.area}m&sup2;</div>`;
      }
    }
  }

  const markup = `<div class="defListWrap"><h4>${question}:</h4><div>${answer}</div></div>`;

  return new Ember.Handlebars.SafeString(markup);
});

// if there's geoshape, draw it
Ember.Handlebars.registerHelper('drawGeoshapes', function() {
  const responseType = Ember.get(this, 'type');
  const answer = Ember.get(this, 'value');

  if (responseType === 'VALUE' && answer.indexOf('features":[') > 0) {
    setTimeout(() => {
      $('.geoshape-map').each(function(index) {
        FLOW.drawGeoShape(
          $('.geoshape-map')[index],
          $(this).data('geoshape-object')
        );
      });
    }, 500);
  }
});

/*  Take a timestamp and render it as a date in format
    YYYY-mm-dd */
FLOW.renderTimeStamp = function(timestamp) {
  let monthString;
  let dateString;

  const t = parseInt(timestamp, 10);
  if (isNaN(t)) {
    return '';
  }

  const d = new Date(t);
  if (!d) {
    return '';
  }

  const date = d.getDate();
  const month = d.getMonth() + 1;
  const year = d.getFullYear();

  if (month < 10) {
    monthString = `0${month.toString()}`;
  } else {
    monthString = month.toString();
  }

  if (date < 10) {
    dateString = `0${date.toString()}`;
  } else {
    dateString = date.toString();
  }

  return `${year}-${monthString}-${dateString}`;
};

FLOW.renderDate = function(timestamp) {
  if (timestamp) {
    const d = new Date(parseInt(timestamp, 10));
    const curr_date = d.getDate();
    const curr_month = d.getMonth() + 1;
    const curr_year = d.getFullYear();
    const curr_hour = d.getHours();
    const curr_min = d.getMinutes();
    let monthString;
    let dateString;
    let hourString;
    let minString;

    if (curr_month < 10) {
      monthString = `0${curr_month.toString()}`;
    } else {
      monthString = curr_month.toString();
    }

    if (curr_date < 10) {
      dateString = `0${curr_date.toString()}`;
    } else {
      dateString = curr_date.toString();
    }

    if (curr_hour < 10) {
      hourString = `0${curr_hour.toString()}`;
    } else {
      hourString = curr_hour.toString();
    }

    if (curr_min < 10) {
      minString = `0${curr_min.toString()}`;
    } else {
      minString = curr_min.toString();
    }

    return `${curr_year}-${monthString}-${dateString}  ${hourString}:${minString}`;
  }
};

// translates values to labels for languages
Ember.Handlebars.registerHelper('toLanguage', function(value) {
  let label = '';
  const valueLoc = Ember.get(this, value);

  FLOW.languageControl.get('content').forEach(item => {
    if (item.get('value') === valueLoc) {
      label = item.get('label');
    }
  });
  return label;
});

// add space to vertical bar helper
Ember.Handlebars.registerHelper('addSpace', function(property) {
  return Ember.get(this, property).replace(/\|/g, ' | ');
});

// date format helper
Ember.Handlebars.registerHelper('date', function(property) {
  const d = new Date(parseInt(Ember.get(this, property), 10));
  const m_names = [
    'Jan',
    'Feb',
    'Mar',
    'Apr',
    'May',
    'Jun',
    'Jul',
    'Aug',
    'Sep',
    'Oct',
    'Nov',
    'Dec',
  ];

  const curr_date = d.getDate();
  const curr_month = d.getMonth();
  const curr_year = d.getFullYear();
  return `${curr_date} ${m_names[curr_month]} ${curr_year}`;
});

// format used in devices table
Ember.Handlebars.registerHelper('date1', function(property) {
  if (Ember.get(this, property) !== null) {
    return FLOW.renderDate(Ember.get(this, property));
  }
  return '';
});

// format used in devices table
Ember.Handlebars.registerHelper('date3', function(property) {
  if (Ember.get(this, property) !== null) {
    return FLOW.renderTimeStamp(Ember.get(this, property));
  }
});

FLOW.date3 = function(dateString) {
  if (dateString) {
    return FLOW.renderTimeStamp(dateString);
  }
};

FLOW.parseJSON = function(jsonString, property) {
  try {
    const jsonObject = JSON.parse(jsonString);
    if (jsonObject[property].length > 0) {
      return jsonObject;
    }
    return null;
  } catch (e) {
    return null;
  }
};

FLOW.addExtraMapBoxTileLayer = function(baseLayers) {
  if (
    FLOW.Env.extraMapboxTileLayerMapId &&
    FLOW.Env.extraMapboxTileLayerAccessToken &&
    FLOW.Env.extraMapboxTileLayerLabel
  ) {
    const templateURL = `https://{s}.tiles.mapbox.com/v4/${FLOW.Env.extraMapboxTileLayerMapId}/{z}/{x}/{y}.jpg?access_token=${FLOW.Env.extraMapboxTileLayerAccessToken}`;
    const attribution =
      '<a href="http://www.mapbox.com/about/maps/" target="_blank">Terms &amp; Feedback</a>';
    baseLayers[FLOW.Env.extraMapboxTileLayerLabel] = L.tileLayer(templateURL, {
      attribution,
    });
  }
};

FLOW.drawGeoShape = function(containerNode, geoShapeObject) {
  containerNode.style.height = '150px';

  let geoshapeCoordinatesArray;
  const geoShapeObjectType = geoShapeObject.features[0].geometry.type;
  if (geoShapeObjectType === 'Polygon') {
    geoshapeCoordinatesArray =
      geoShapeObject.features[0].geometry.coordinates[0];
  } else {
    geoshapeCoordinatesArray = geoShapeObject.features[0].geometry.coordinates;
  }
  const points = [];

  for (let j = 0; j < geoshapeCoordinatesArray.length; j++) {
    points.push([
      geoshapeCoordinatesArray[j][1],
      geoshapeCoordinatesArray[j][0],
    ]);
  }

  const center = FLOW.getCentroid(points);

  const geoshapeMap = L.map(containerNode, { scrollWheelZoom: false }).setView(
    center,
    2
  );

  geoshapeMap.options.maxZoom = 18;
  geoshapeMap.options.minZoom = 2;
  const mbAttr =
    'Map &copy; 1987-2014 <a href="http://developer.here.com">HERE</a>';
  const mbUrl =
    'https://{s}.{base}.maps.cit.api.here.com/maptile/2.1/maptile/{mapID}/{scheme}/{z}/{x}/{y}/256/{format}?app_id={app_id}&app_code={app_code}';
  const normal = L.tileLayer(mbUrl, {
    scheme: 'normal.day.transit',
    format: 'png8',
    attribution: mbAttr,
    subdomains: '1234',
    mapID: 'newest',
    app_id: FLOW.Env.hereMapsAppId,
    app_code: FLOW.Env.hereMapsAppCode,
    base: 'base',
  }).addTo(geoshapeMap);
  const satellite = L.tileLayer(mbUrl, {
    scheme: 'hybrid.day',
    format: 'jpg',
    attribution: mbAttr,
    subdomains: '1234',
    mapID: 'newest',
    app_id: FLOW.Env.hereMapsAppId,
    app_code: FLOW.Env.hereMapsAppCode,
    base: 'aerial',
  });
  const baseLayers = {
    Normal: normal,
    Satellite: satellite,
  };

  FLOW.addExtraMapBoxTileLayer(baseLayers);

  L.control.layers(baseLayers).addTo(geoshapeMap);

  // Draw geoshape based on its type
  if (geoShapeObjectType === 'Polygon') {
    const geoShapePolygon = L.polygon(points).addTo(geoshapeMap);
    geoshapeMap.fitBounds(geoShapePolygon.getBounds());
  } else if (geoShapeObjectType === 'MultiPoint') {
    const geoShapeMarkersArray = [];
    for (let i = 0; i < points.length; i++) {
      geoShapeMarkersArray.push(L.marker([points[i][0], points[i][1]]));
    }
    const geoShapeMarkers = L.featureGroup(geoShapeMarkersArray).addTo(
      geoshapeMap
    );
    geoshapeMap.fitBounds(geoShapeMarkers.getBounds());
  } else if (geoShapeObjectType === 'LineString') {
    const geoShapeLine = L.polyline(points).addTo(geoshapeMap);
    geoshapeMap.fitBounds(geoShapeLine.getBounds());
  }
};

FLOW.getCentroid = function(arr) {
  return arr.reduce(
    (x, y) => [x[0] + y[0] / arr.length, x[1] + y[1] / arr.length],
    [0, 0]
  );
};

FLOW.reportFilename = function(url) {
  if (!url) {
    return;
  }
  return url
    .split('/')
    .pop()
    .replace(/\s/g, '');
};

FLOW.hasPermission = function(permission) {
  const currentUserPermissions = FLOW.currentUser.get('pathPermissions');
  return Object.keys(currentUserPermissions).reduce(
    (alreadyHasPermission, permissionKey) =>
      alreadyHasPermission ||
      currentUserPermissions[permissionKey].indexOf(permission) > -1,
    false
  );
};

Ember.Handlebars.registerHelper('getServer', () => {
  const loc = window.location.href;
  const pos = loc.indexOf('/admin');
  return loc.substring(0, pos);
});

Ember.Handlebars.registerHelper('sgName', function(property) {
  const sgId = Ember.get(this, property);
  const sg = FLOW.surveyGroupControl.find(
    item => item.get && item.get('keyId') === sgId
  );
  return sg ? sg.get('name') : sgId;
});

Ember.Handlebars.registerHelper('formName', function(property) {
  const formId = Ember.get(this, property);
  let name = '';
  const form = FLOW.Survey.find(formId);
  if (form) {
    name += form.get('name');
  }
  return name;
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

FLOW.registerViewHelper(
  'date2',
  Ember.View.extend({
    tagName: 'span',

    template: Ember.Handlebars.compile('{{view.formattedContent}}'),

    formattedContent: function() {
      let monthString;
      let dateString;
      let hourString;
      let minString;

      const content = this.get('content');

      if (content === null) {
        return '';
      }

      const d = new Date(parseInt(content, 10));
      const curr_date = d.getDate();
      const curr_month = d.getMonth() + 1;
      const curr_year = d.getFullYear();
      const curr_hour = d.getHours();
      const curr_min = d.getMinutes();

      if (curr_month < 10) {
        monthString = `0${curr_month.toString()}`;
      } else {
        monthString = curr_month.toString();
      }

      if (curr_date < 10) {
        dateString = `0${curr_date.toString()}`;
      } else {
        dateString = curr_date.toString();
      }

      if (curr_hour < 10) {
        hourString = `0${curr_hour.toString()}`;
      } else {
        hourString = curr_hour.toString();
      }

      if (curr_min < 10) {
        minString = `0${curr_min.toString()}`;
      } else {
        minString = curr_min.toString();
      }

      return `${curr_year}-${monthString}-${dateString}  ${hourString}:${minString}`;
    }.property('content'),
  })
);

// ********************************************************//
//                      main navigation
// ********************************************************//
FLOW.NavigationView = Ember.View.extend(template('application/navigation'), {
  selectedBinding: 'controller.selected',

  showDevicesButton: Ember.computed(() =>
    FLOW.permControl.get('canManageDevices')
  ).property(),
  showMapsButton: Ember.computed(() => FLOW.Env.showMapsTab).property(
    'FLOW.Env.showMapsTab'
  ),

  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:current navItem'.w(),

    navItem: Ember.computed(function() {
      return this.get('item');
    })
      .property('item')
      .cacheable(),

    isActive: Ember.computed(function() {
      return this.get('item') === this.get('parentView.selected');
    })
      .property('item', 'parentView.selected')
      .cacheable(),

    eventManager: Ember.Object.create({
      click() {
        // Add the active tab as a CSS class to html
        const html = document.querySelector('html');
        html.className = '';
        html.classList.add(FLOW.router.navigationController.selected);
      },
    }),
  }),
});

// ********************************************************//
//                      standard views
// ********************************************************//

Ember.Select.reopen({
  attributeBindings: ['size'],
});

FLOW.DateField = Ember.TextField.extend({
  minDate: true,

  didInsertElement() {
    this._super();

    if (this.get('minDate')) {
      // datepickers with only future dates
      $('#from_date, #from_date02').datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        minDate: new Date(),
        onSelect(selectedDate) {
          $('#to_date, #to_date02').datepicker(
            'option',
            'minDate',
            selectedDate
          );
          FLOW.dateControl.set('fromDate', selectedDate);
        },
      });

      $('#to_date, #to_date02').datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        minDate: new Date(),
        onSelect(selectedDate) {
          $('#from_date, #from_date02').datepicker(
            'option',
            'maxDate',
            selectedDate
          );
          FLOW.dateControl.set('toDate', selectedDate);
        },
      });
    } else {
      // datepickers with all dates
      $('#from_date, #from_date02').datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        onSelect(selectedDate) {
          $('#to_date, #to_date02').datepicker(
            'option',
            'minDate',
            selectedDate
          );
          FLOW.dateControl.set('fromDate', selectedDate);
        },
      });

      $('#to_date, #to_date02').datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        onSelect(selectedDate) {
          $('#from_date, #from_date02').datepicker(
            'option',
            'maxDate',
            selectedDate
          );
          FLOW.dateControl.set('toDate', selectedDate);
        },
      });
    }
  },
});

FLOW.DateField2 = Ember.TextField.extend({
  didInsertElement() {
    this._super();

    this.$().datepicker({
      dateFormat: 'yy-mm-dd',
      defaultDate: new Date(),
      numberOfMonths: 1,
    });
  },
});

// surveys views
FLOW.NavSurveysView = Ember.View.extend(template('navSurveys/nav-surveys'));
FLOW.NavSurveysMainView = Ember.View.extend(
  template('navSurveys/nav-surveys-main')
);

FLOW.NavSurveysEditView = Ember.View.extend(
  template('navSurveys/nav-surveys-edit')
);

FLOW.ManageNotificationsView = Ember.View.extend(
  template('navSurveys/manage-notifications')
);

FLOW.ManageTranslationsView = Ember.View.extend(
  template('navSurveys/manage-translations')
);

FLOW.EditQuestionsView = Ember.View.extend(
  template('navSurveys/edit-questions')
);

// devices views
FLOW.NavDevicesView = Ember.View.extend(template('navDevices/nav-devices'));

FLOW.CurrentDevicesView = FLOW.View.extend(
  template('navDevices/devices-list-tab/devices-list')
);

FLOW.AssignSurveysOverviewView = FLOW.View.extend(
  template('navDevices/assignment-list-tab/assignment-list')
);

FLOW.EditSurveyAssignmentView = Ember.View.extend(
  template('navDevices/assignment-edit-tab/assignment-edit')
);

FLOW.SurveyBootstrapView = FLOW.View.extend(
  template('navDevices/bootstrap-tab/survey-bootstrap')
);

// data views
FLOW.NavDataView = Ember.View.extend(template('navData/nav-data'));

FLOW.InspectDataView = Ember.View.extend(template('navData/inspect-data'));

FLOW.BulkUploadView = Ember.View.extend(template('navData/bulk-upload'));

FLOW.BulkUploadImagesView = Ember.View.extend(template('navData/bulk-upload-images'));

FLOW.CascadeResourcesView = Ember.View.extend(
  template('navData/cascade-resources')
);

FLOW.MonitoringDataView = Ember.View.extend(
  template('navData/monitoring-data')
);

FLOW.ChartReportsView = Ember.View.extend(template('navReports/chart-reports'));

// resources views
FLOW.NavResourcesView = Ember.View.extend(
  template('navResources/nav-resources')
);

// users views
FLOW.NavUsersView = Ember.View.extend(template('navUsers/nav-users'));

// Messages views
FLOW.NavMessagesView = Ember.View.extend(template('navMessages/nav-messages'));

FLOW.HeaderView = FLOW.View.extend(template('application/header-common'));

FLOW.FooterView = FLOW.View.extend(template('application/footer'));

// Stats Views
FLOW.StatsView = FLOW.View.extend(template('navStats/stats-list'));
FLOW.NewStatsView = FLOW.View.extend(template('navStats/new-stats'));

// ********************************************************//
//             Subnavigation for the Data tabs
// ********************************************************//
FLOW.DatasubnavView = FLOW.View.extend(template('navData/data-subnav'), {
  selectedBinding: 'controller.selected',
  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:active'.w(),

    isActive: Ember.computed(function() {
      const parentView = this.get('parentView.selected');
      if (this.get('item') === parentView && (parentView === 'bulkUpload' || parentView === 'bulkUploadImages')) {
        FLOW.uploader.set('bulkUpload', true);
      } else if (this.get('parentView.selected') !== 'bulkUpload') {
        FLOW.uploader.set('bulkUpload', false);
      }
      return this.get('item') === this.get('parentView.selected');
    })
      .property('item', 'parentView.selected')
      .cacheable(),

    showDataCleaningButton: Ember.computed(() =>
      FLOW.permControl.get('canCleanData')
    ).property(),
  }),
});

// ********************************************************//
//             Subnavigation for the Device tabs
// ********************************************************//
FLOW.DevicesSubnavView = FLOW.View.extend(
  template('navDevices/devices-subnav'),
  {
    selectedBinding: 'controller.selected',
    NavItemView: Ember.View.extend({
      tagName: 'li',
      classNameBindings: 'isActive:active'.w(),

      isActive: Ember.computed(function() {
        return this.get('item') === this.get('parentView.selected');
      })
        .property('item', 'parentView.selected')
        .cacheable(),
    }),
  }
);

// ********************************************************//
//             Subnavigation for the Resources tabs
// ********************************************************//
FLOW.ResourcesSubnavView = Ember.View.extend(
  template('navResources/resources-subnav'),
  {
    selectedBinding: 'controller.selected',
    NavItemView: Ember.View.extend({
      tagName: 'li',
      classNameBindings: 'isActive:active'.w(),

      isActive: Ember.computed(function() {
        return this.get('item') === this.get('parentView.selected');
      })
        .property('item', 'parentView.selected')
        .cacheable(),

      showCascadeResourcesButton: Ember.computed(() =>
        FLOW.permControl.get('canManageCascadeResources')
      ).property(),

      showDataApprovalButton: Ember.computed(
        () =>
          FLOW.Env.enableDataApproval &&
          FLOW.permControl.get('canManageDataAppoval')
      ).property(),
    }),
  }
);

// *************************************************************//
//             Generic table column view which supports sorting
// *************************************************************//
FLOW.ColumnView = Ember.View.extend({
  tagName: 'th',
  item: null,
  type: null,

  classNameBindings: ['isActiveAsc:sorting_asc', 'isActiveDesc:sorting_desc'],

  isActiveAsc: Ember.computed(function() {
    return (
      this.get('item') === FLOW.tableColumnControl.get('selected') &&
      FLOW.tableColumnControl.get('sortAscending') === true
    );
  })
    .property(
      'item',
      'FLOW.tableColumnControl.selected',
      'FLOW.tableColumnControl.sortAscending'
    )
    .cacheable(),

  isActiveDesc: Ember.computed(function() {
    return (
      this.get('item') === FLOW.tableColumnControl.get('selected') &&
      FLOW.tableColumnControl.get('sortAscending') === false
    );
  })
    .property(
      'item',
      'FLOW.tableColumnControl.selected',
      'FLOW.tableColumnControl.sortAscending'
    )
    .cacheable(),

  sort() {
    if (this.get('isActiveAsc') || this.get('isActiveDesc')) {
      FLOW.tableColumnControl.toggleProperty('sortAscending');
    } else {
      FLOW.tableColumnControl.set('sortProperties', [this.get('item')]);
      FLOW.tableColumnControl.set('selected', this.get('item'));
    }

    if (this.get('type') === 'device') {
      FLOW.deviceControl.getSortInfo();
    } else if (this.get('type') === 'assignment') {
      FLOW.surveyAssignmentControl.getSortInfo();
    } else if (this.get('type') === 'message') {
      FLOW.messageControl.getSortInfo();
    }
  },
});

const { set } = Ember;
const { get } = Ember;
Ember.RadioButton = Ember.View.extend(
  observe({
    value: 'bindingChanged',
  }),
  {
    title: null,
    checked: false,
    group: 'radio_button',
    disabled: false,

    classNames: ['ember-radio-button'],

    defaultTemplate: Ember.Handlebars.compile(
      '<label><input type="radio" {{ bindAttr disabled="view.disabled" name="view.group" value="view.option" checked="view.checked"}} />{{view.title}}</label>'
    ),

    bindingChanged() {
      if (this.get('option') === get(this, 'value')) {
        this.set('checked', true);
      }
    },

    change() {
      Ember.run.once(this, this._updateElementValue);
    },

    _updateElementValue() {
      const input = this.$('input:radio');
      set(this, 'value', input.attr('value'));
    },
  }
);

FLOW.SelectFolder = Ember.Select.extend(
  observe({
    value: 'onChange',
  }),
  {
    controller: null,

    init() {
      this._super();
      this.set('prompt', Ember.String.loc('_choose_folder_or_survey'));
      this.set('optionLabelPath', 'content.code');
      this.set('optionValuePath', 'content.keyId');
      this.set(
        'controller',
        FLOW.SurveySelection.create({
          selectionFilter: this.get('selectionFilter'),
        })
      );
      this.set(
        'content',
        this.get('controller').getByParentId(this.get('parentId'), {
          monitoringSurveysOnly: this.get('showMonitoringSurveysOnly'),
          dataReadSurveysOnly: this.get('showDataReadSurveysOnly'),
        })
      );
    },

    onChange() {
      const childViews = this.get('parentView').get('childViews');
      const keyId = this.get('value');
      const survey = this.get('controller').getSurvey(keyId);
      const nextIdx = this.get('idx') + 1;
      const monitoringOnly = this.get('showMonitoringSurveysOnly');
      const dataReadOnly = this.get('showDataReadSurveysOnly');
      const filter = this.get('selectionFilter');

      if (nextIdx !== childViews.length) {
        childViews.removeAt(nextIdx, childViews.length - nextIdx);
      }

      if (keyId) {
        // only proceed if a folder/survey is selected
        if (this.get('controller').isSurvey(keyId)) {
          FLOW.selectedControl.set('selectedSurveyGroup', survey);
          if (
            FLOW.Env.enableDataApproval &&
            survey.get('dataApprovalGroupId')
          ) {
            FLOW.router.approvalGroupController.load(
              survey.get('dataApprovalGroupId')
            );
            FLOW.router.approvalStepsController.loadByGroupId(
              survey.get('dataApprovalGroupId')
            );
          }
        } else {
          FLOW.selectedControl.set('selectedSurveyGroup', null);
          childViews.pushObject(
            FLOW.SelectFolder.create({
              parentId: keyId,
              idx: nextIdx,
              showMonitoringSurveysOnly: monitoringOnly,
              showDataReadSurveysOnly: dataReadOnly,
              selectionFilter: filter,
            })
          );
        }
      } else {
        FLOW.selectedControl.set('selectedSurveyGroup', null);
      }
    },
  }
);

FLOW.SurveySelectionView = Ember.ContainerView.extend({
  tagName: 'div',
  classNames: 'modularSelection',
  childViews: [],

  init() {
    this._super();
    this.get('childViews').pushObject(
      FLOW.SelectFolder.create({
        parentId: 0, // start with the root folder
        idx: 0,
        showMonitoringSurveysOnly:
          this.get('showMonitoringSurveysOnly') || false,
        showDataReadSurveysOnly: this.get('showDataReadSurveysOnly') || false,
      })
    );
  },
});

FLOW.DataCleaningSurveySelectionView = Ember.ContainerView.extend({
  tagName: 'div',
  classNames: 'modularSelection',
  childViews: [],

  init() {
    this._super();
    this.get('childViews').pushObject(
      FLOW.SelectFolder.create({
        parentId: 0, // start with the root folder
        idx: 0,
        showMonitoringSurveysOnly:
          this.get('showMonitoringSurveysOnly') || false,
        showDataReadSurveysOnly: this.get('showDataReadSurveysOnly') || false,
        selectionFilter: FLOW.projectControl.dataCleaningEnabled,
      })
    );
  },
});
