import React from 'react';
import Form from '../../components/surveys/Form';

import observe from '../../mixins/observe';

FLOW.FormView = FLOW.ReactComponentView.extend(
  observe({ 'FLOW.projectControl.currentProject': 'renderReactSide' }),
  {
    init() {
      this._super();
      this.getProps = this.getProps.bind(this);
      this.renderReactSide = this.renderReactSide.bind(this);
    },

    didInsertElement(...args) {
      this._super(...args);
      this.renderReactSide();
    },

    renderReactSide() {
      const props = this.getProps();
      this.reactRender(<Form {...props} />);
    },

    getProps() {
     
    },

    showFormBasics: false,

    manageTranslations: false,
    manageNotifications: false,

    form: Ember.computed(() => FLOW.selectedControl.get('selectedSurvey')).property(
      'FLOW.selectedControl.selectedSurvey'
    ),

    toggleShowFormBasics() {
      this.set('showFormBasics', !this.get('showFormBasics'));
    },

    isNewForm: Ember.computed(() => {
      const form = FLOW.selectedControl.get('selectedSurvey');
      return form && form.get('code') == 'New Form';
    }).property('FLOW.selectedControl.selectedSurvey'),

    visibleFormBasics: Ember.computed(function() {
      return this.get('isNewForm') || this.get('showFormBasics');
    }).property('showFormBasics'),

    apiUrl: Ember.computed(function() {
      const form = this.get('form');
      const instanceName = window.location.hostname.split('.')[0];
      return [
        'https://api-auth0.akvo.org/flow/orgs/',
        instanceName,
        '/form_instances?survey_id=',
        form.get('surveyGroupId'),
        '&form_id=',
        form.get('id'),
      ].join('');
    }),

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

    disableFormFields: Ember.computed(function() {
      const form = this.get('form');
      return !FLOW.permControl.canEditForm(form);
    }).property('this.form'),

    showFormTranslationsButton: Ember.computed(function() {
      const form = this.get('form');
      return FLOW.permControl.canEditForm(form);
    }).property('this.form'),

    showFormDeleteButton: Ember.computed(function() {
      const form = this.get('form');
      return FLOW.permControl.canEditForm(form);
    }).property('this.form'),

    showFormPublishButton: Ember.computed(function() {
      const form = this.get('form');
      return FLOW.permControl.canEditForm(form);
    }).property('this.form'),

    disableFormPublishButton: Ember.computed(function() {
      const form = this.get('form');
      const questionsLoading =
        FLOW.questionControl.content && !FLOW.questionControl.content.isLoaded;
      return questionsLoading || form.get('status') === 'PUBLISHED';
    }).property('this.form.status', 'FLOW.questionControl.content.isLoaded'),
  }
);
