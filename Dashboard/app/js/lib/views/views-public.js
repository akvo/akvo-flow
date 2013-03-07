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
