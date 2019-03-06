FLOW.FormView = Ember.View.extend({
	template: Ember.Handlebars.compile(require('templates/navSurveys/form')),
	showFormBasics: false,

	manageTranslations: false,
	manageNotifications: false,

	form: Ember.computed(function() {
		return FLOW.selectedControl.get('selectedSurvey');
	}).property('FLOW.selectedControl.selectedSurvey'),

	toggleShowFormBasics: function () {
		this.set('showFormBasics', !this.get('showFormBasics'));
	},

	isNewForm: Ember.computed(function() {
		var form = FLOW.selectedControl.get('selectedSurvey');
		return form && form.get('code') == "New Form";
	}).property('FLOW.selectedControl.selectedSurvey'),

	visibleFormBasics: Ember.computed(function() {
		return this.get('isNewForm') || this.get('showFormBasics');
	}).property('showFormBasics'),


	doManageTranslations: function() {
		FLOW.translationControl.populate();
		this.set('manageNotifications', false);
		this.set('manageTranslations', true);
	},

	doManageNotifications: function() {
		FLOW.notificationControl.populate();
		this.set('manageTranslations', false);
		this.set('manageNotifications', true);
	},

	disableFormFields: Ember.computed(function () {
		var form = this.get('form');
		return !FLOW.permControl.canEditForm(form);
	}).property('this.form'),

	showFormTranslationsButton: Ember.computed(function() {
		var form = this.get('form');
		return FLOW.permControl.canEditForm(form);
	}).property('this.form'),

	showFormDeleteButton: Ember.computed(function () {
		var form = this.get('form');
		return FLOW.permControl.canEditForm(form);
	}).property('this.form'),

	showFormPublishButton: Ember.computed(function () {
		var form = this.get('form');
		return FLOW.permControl.canEditForm(form);
	}).property('this.form')
});
