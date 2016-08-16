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

function renderCaddisflyAnswer(json){
  name = ""
  imageUrl = ""
  result = Ember.A();
  if (!Ember.empty(json)){
    jsonParsed = JSON.parse(json);

    // get out test name
    if (!Ember.empty(jsonParsed.name)){
      testName =  jsonParsed.name.trim();
    }

    // get out results
    if (jsonParsed.result && !Ember.empty(jsonParsed.result)){
        jsonParsed.result.forEach(function(item){
        newResult = {
          "name":item.name,
          "value":item.value,
          "unit":item.unit,
        };
        result.push(newResult);
      });
    }
    // get out image url
    if (!Ember.empty(jsonParsed.image)){
      imageUrl = FLOW.Env.photo_url_root + jsonParsed.image.trim();
    }

    // contruct html
    html = "<div><strong>" + name + "</strong></div>"
    result.forEach(function(item){
      html +=  "<br><div>" + item.name + " : " + item.value + " " + item.unit + "</div>";})
    html += "<br>"
    html += "<div class=\"signatureImage\"><img src=\"" + imageUrl +"\"}} /></div>"
    return html;
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
    answer = renderCaddisflyAnswer(answer)
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
  if (d) {
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
  } else {
    return "";
  }
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


FLOW.HeaderView = FLOW.View.extend({
  templateName: 'application/header-common'
});

FLOW.FooterView = FLOW.View.extend({
  templateName: 'application/footer-public'
});
