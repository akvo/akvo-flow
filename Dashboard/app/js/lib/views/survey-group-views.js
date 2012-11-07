// survey group views

FLOW.SurveyGroupMenuItemView = Ember.View.extend({
	content: null,
	tagName: 'li',
	classNameBindings: 'amSelected:current'.w(),
	
	// true if the survey group is selected. Used to set proper display class
	amSelected: function() {
		var selected = FLOW.selectedControl.get('selectedSurveyGroup');
		if (selected) {
			var amSelected = (this.content.get('keyId') === FLOW.selectedControl.selectedSurveyGroup.get('keyId'));
			return amSelected;
		} else {
			return null;
		}
	}.property('FLOW.selectedControl.selectedSurveyGroup', 'content').cacheable(),
	
	// fired when a survey group is clicked
	makeSelected: function() {
			FLOW.selectedControl.set('selectedSurveyGroup', this.content);
	}
});

FLOW.SurveyGroupSurveyView = Ember.View.extend({
	//doEditSurvey is defined in the Router. It transfers to the nav-surveys-edit handlebar view

	// fired when 'preview survey' is clicked in the survey item display
	previewSurvey: function() {
			console.log("TODO preview Survey");
	},
	
	// fired when 'delete survey' is clicked in the survey item display
	deleteSurvey: function() {
			console.log("TODO delete Survey");
	},
	
	// fired when 'inspect data' is clicked in the survey item display
	inspectData: function() {
			console.log("TODO inspect Data");
	}
});

FLOW.SurveyGroupMainView = Ember.View.extend({
	
	showEditField: false,
	showNewGroupField:false,
	surveyGroupName:null,
	showSGDeleteDialogue:false,
	showSGDeleteNotPossibleDialogue:false,
	
	// true if at least one survey group is active
	oneSelected: function() {
		var selected = FLOW.selectedControl.get('selectedSurveyGroup');
		if (selected) {
			return true;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedSurveyGroup'),
	
	// fired when 'edit name' is clicked, shows edit field to change survey group name
	editSurveyGroupName: function() {
		this.set('surveyGroupName',FLOW.selectedControl.selectedSurveyGroup.get('code'));
		this.set('showEditField',true);
	},
	
	// fired when 'save' is clicked while showing edit group name field. Saves the new group name
	saveSurveyGroupNameEdit: function() {
		var sgId=FLOW.selectedControl.selectedSurveyGroup.get('id');
		var surveyGroup=FLOW.store.find(FLOW.SurveyGroup, sgId);
		surveyGroup.set('code',this.get('surveyGroupName'));
		FLOW.store.commit();
		FLOW.selectedControl.set('selectedSurveyGroup',FLOW.store.find(FLOW.SurveyGroup, sgId));
		this.set('showEditField',false);
	},
	
	// fired when 'cancel' is clicked while showing edit group name field. Cancels the edit.
	cancelSurveyGroupNameEdit: function() {
		this.set('surveyGroupName',FLOW.selectedControl.selectedSurveyGroup.get('code'));
		this.set('showEditField',false);
	},

	
	// fired when 'add a group' is clicked. Displays a new group text field in the left sidebar
	addGroup: function() {
		FLOW.selectedControl.set('selectedSurveyGroup',null);
		this.set('surveyGroupName',null);
		this.set('showNewGroupField',true);
	},

    // show delete SurveyGroup dialog
	showSGroupDeleteDialog:function(){
		// check if there are surveys in the the datastore (this is also checked at the server)
		var surveys=FLOW.store.filter(FLOW.Survey,function(data,sgId) {
			var sgId=FLOW.selectedControl.selectedSurveyGroup.get('id');
   			if (data.get('surveyGroupId') == sgId) { 
   				return true; }
		});

		// if there are surveys in this group, display 'please remove surveys first'
		if (surveys.get('content').length > 0) { 
			this.set('showSGDeleteNotPossibleDialogue',true);
		} else {

			// else display 'are you sure you want to delete'
			this.set('showSGDeleteDialogue',true);
		}
	},

	// cancel survey group delete
	cancelSGroupDelete:function(){
		this.set('showSGDeleteDialogue',false);
		this.set('showSGDeleteNotPossibleDialogue',false);
	},

	// delete survey group
	doSGroupDelete:function(){
		var sgId=FLOW.selectedControl.selectedSurveyGroup.get('id');
		var surveyGroup=FLOW.store.find(FLOW.SurveyGroup, sgId);
		surveyGroup.deleteRecord();
		FLOW.store.commit();
		this.set('showSGDeleteDialogue',false);
		// TODO refresh list of survey groups

	},

	// fired when 'save' is clicked while showing new group text field in left sidebar. Saves new survey group to the data store
	saveNewSurveyGroupName: function() {
			var newSG = FLOW.store.createRecord(FLOW.SurveyGroup,{
				"code":this.get('surveyGroupName')
			});
			FLOW.store.commit();
			FLOW.surveyGroupControl.set('content',FLOW.store.find(FLOW.SurveyGroup, {}));
			this.set('showNewGroupField',false);
	},
	
	// fired when 'cancel' is clicked while showing new group text field in left sidebar. Cancels the new survey group creation
	cancelNewSurveyGroupName: function() {
			this.set('surveyGroupName',null);
			this.set('showNewGroupField',false);
	},
	
	// fired when 'create a new survey' is clicked in the top bar. 
	createSurvey: function() {
			console.log("TODO create Survey");		
	},
	
});