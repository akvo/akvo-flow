
Ember.Handlebars.registerHelper('t', function(i18nKey, options) {
  return Ember.String.loc(i18nKey);
});

// If locale is not set, use English
if (typeof locale === 'undefined') {
  locale = "en";
}

if (locale === "nl") {
  require('akvo-flow/locale/nl');
} else {
  require('akvo-flow/locale/en');
}