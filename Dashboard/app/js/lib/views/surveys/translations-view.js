FLOW.TranslationsView = FLOW.View.extend({
  templateName: 'navSurveys/manage-translations',

  saveTranslationsAndClose: function () {
    FLOW.translationControl.saveTranslations();
    this.get('parentView').set('manageTranslations', false);
  },

  closeTranslations: function (router, event) {
    this.get('parentView').set('manageTranslations', false);
  },
});
