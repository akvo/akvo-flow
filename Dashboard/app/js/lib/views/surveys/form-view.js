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
		fDeleteId = FLOW.selectedControl.selectedSurvey.get('keyId'); //TODO: Works, but  this more like other levels

		alert("Bravo:" + fDeleteId);
		// check if deleting this form is allowed
		// if successful, the deletion action will be called from DS.FLOWrestadaptor.sideload
		//http://localhost:8888/rest/survey:ember1899>s?preflight=delete&surveyId=146532016
		//FLOW.store.findQuery(form, {
		//	preflight: 'delete',
		//	surveyId: fDeleteId
		//}
		//)
		
		$.ajax({
            url: '/rest/surveys/?preflight=delete&surveyId=' + fDeleteId,
            type: 'GET',
            success: function(json) {
            	//surveys:[]
            	//meta:	Object
            	//since	
            	//num	null
            	//keyId	null
            	//status	preflight-delete-survey
            	//message	cannot_delete
            	if (json && json.meta && json.meta.status === 'preflight-delete-survey') {
                    if (json.meta.message === 'can_delete') {
                        // do the deletion
                      	alert('Echo');
                        FLOW.surveyControl.deleteForm(fDeleteId);
                      } else {
                        FLOW.dialogControl.set('activeAction', 'ignore');
                        FLOW.dialogControl.set('header', 'Cannot delete form');
                        FLOW.dialogControl.set('message', 'There is data collected for the form; you must delete it first from the Data tab.');
                        FLOW.dialogControl.set('showCANCEL', false);
                        FLOW.dialogControl.set('showDialog', true);
                      }
 
            	} else {
            		alert('Foxtrot '+json);
            	}
            },
            error: function() {
              alert('Checking if form deletable failed... try again...');
            }
          });
		
		
	}
});
