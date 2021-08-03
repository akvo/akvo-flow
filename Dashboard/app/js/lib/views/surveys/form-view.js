import template from '../../mixins/template';

FLOW.FormView = Ember.View.extend(template('navSurveys/form'), {
  showFormBasics: false,

  manageTranslations: false,
  manageNotifications: false,

  form: Ember.computed(() => FLOW.selectedControl.get('selectedSurvey')).property('FLOW.selectedControl.selectedSurvey'),

  toggleShowFormBasics() {
    this.set('showFormBasics', !this.get('showFormBasics'));
  },

  isNewForm: Ember.computed(() => {
    const form = FLOW.selectedControl.get('selectedSurvey');
    return form && form.get('code') == 'New Form';
  }).property('FLOW.selectedControl.selectedSurvey'),

  visibleFormBasics: Ember.computed(function () {
    return this.get('isNewForm') || this.get('showFormBasics');
  }).property('showFormBasics'),

  doManageTranslations() {
    FLOW.translationControl.populate();
    this.set('manageNotifications', false);
    this.set('manageTranslations', true);
  },

  doManageNotifications() {
    FLOW.notificationControl.populate();
    this.set('manageTranslations', false);
    this.set('manageNotifications', true);
  },

  disableFormFields: Ember.computed(function () {
    const form = this.get('form');
    return !FLOW.permControl.canEditForm(form);
  }).property('this.form'),

  showFormTranslationsButton: Ember.computed(function () {
    const form = this.get('form');
    return FLOW.permControl.canEditForm(form);
  }).property('this.form'),

  showFormDeleteButton: Ember.computed(function () {
    const form = this.get('form');
    return FLOW.permControl.canEditForm(form);
  }).property('this.form'),

  showFormPublishButton: Ember.computed(function () {
    const form = this.get('form');
    return FLOW.permControl.canEditForm(form);
  }).property('this.form'),

  disableFormPublishButton: Ember.computed(function () {
    const form = this.get('form');
    const questionsLoading = !FLOW.questionControl.content.isLoaded;
    return questionsLoading || form.get('status') === 'PUBLISHED';
  }).property('this.form.status', 'FLOW.questionControl.content.isLoaded'),
});
