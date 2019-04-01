import template from '../../mixins/template';

FLOW.TranslationsView = FLOW.View.extend(template('navSurveys/manage-translations'), {
  saveTranslationsAndClose() {
    FLOW.translationControl.saveTranslations();
    this.get('parentView').set('manageTranslations', false);
  },

  closeTranslations() {
    this.get('parentView').set('manageTranslations', false);
  },
});
