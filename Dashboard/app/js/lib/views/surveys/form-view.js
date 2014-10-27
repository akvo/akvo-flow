FLOW.FormView = Ember.View.extend({
	templateName: 'navSurveys/form',
	showFormBasics: false,

	toggleShowFormBasics: function () {
		this.set('showFormBasics', !this.get('showFormBasics'));
	}

})
