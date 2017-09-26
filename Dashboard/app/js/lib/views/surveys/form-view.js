FLOW.FormView = Ember.View.extend({
	templateName: 'navSurveys/form',
	showFormBasics: false,

	manageTranslations: false,
	manageNotifications: false,

	form: function() {
		return FLOW.selectedControl.get('selectedSurvey');
	}.property('FLOW.selectedControl.selectedSurvey'),

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
	},

	disableFormFields: function () {
		var form = this.get('form');
		return !FLOW.permControl.canEditForm(form);
	}.property('this.form'),

	showFormTranslationsButton: function() {
		var form = this.get('form');
		return FLOW.permControl.canEditForm(form);
	}.property('this.form'),

	showFormDeleteButton: function () {
		var form = this.get('form');
		return FLOW.permControl.canEditForm(form);
	}.property('this.form'),

	showFormPublishButton: function () {
		var form = this.get('form');
		return FLOW.permControl.canEditForm(form);
	}.property('this.form'),
	
	maybeDeleteForm: function () {
		var form = this.get('form');
		fDeleteId = FLOW.selectedControl.selectedSurvey.get('keyId');
		//TODO: This works, but it should be more like the other levels

		$.ajax({
            url: '/rest/surveys/?preflight=delete&surveyId=' + fDeleteId,
            type: 'GET',
            success: function(json) {
            	if (json && json.meta && json.meta.status === 'preflight-delete-survey') {
                    if (json.meta.message === 'can_delete') {
                        // do the deletion
                        FLOW.surveyControl.deleteForm(fDeleteId);
                      } else { //'cannot_delete'
                        FLOW.dialogControl.set('activeAction', 'ignore');
                        FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_form'));
                        FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_form_text'));
                        FLOW.dialogControl.set('showCANCEL', false);
                        FLOW.dialogControl.set('showDialog', true);
                      }
 
            	} else {
            		//TODO: tell user?
            	}
            },
            error: function() {
            	//TODO: tell user?
            	//alert('Checking if form deletable failed; try again.');
            }
          });
		
		
	}
});
