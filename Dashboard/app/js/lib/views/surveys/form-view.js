FLOW.FormView = Ember.View.extend({
	templateName: 'navSurveys/form',
	showFormBasics: false,

	manageTranslations: false,
	manageNotifications: false,



	toggleShowFormBasics: function () {
		this.set('showFormBasics', !this.get('showFormBasics'));
	},

	isNewForm: function() {
		var form = FLOW.selectedControl.get('selectedSurvey');
		return form && form.get('code') == "New Form";
	}.property('FLOW.selectedControl.selectedSurvey'),

	visibleFormBasics: function() {
		return this.get('isNewForm') || this.get('showFormBasics');
	}.property('showFormBasics'),


	doManageTranslations: function() {
		FLOW.translationControl.populate();
		this.set('manageNotifications', false);
		this.set('manageTranslations', true);
	},

	doManageNotifications: function() {
		FLOW.notificationControl.populate();
		this.set('manageTranslations', false);
		this.set('manageNotifications', true);
	}

});
