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

