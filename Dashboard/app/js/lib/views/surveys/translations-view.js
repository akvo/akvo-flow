import template from '../../mixins/template';

FLOW.TranslationsView = FLOW.View.extend(template('navSurveys/manage-translations'), {
  saveTranslationsAndClose: function () {
    FLOW.translationControl.saveTranslations();
    this.get('parentView').set('manageTranslations', false);
  },

  closeTranslations: function (router, event) {
    this.get('parentView').set('manageTranslations', false);
  },
});
