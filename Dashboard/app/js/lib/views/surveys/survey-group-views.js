function capitaliseFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

if (!String.prototype.trim) {
		String.prototype.trim=function(){return this.replace(/^\s+|\s+$/g, '');};
	}

// displays survey groups in left sidebar
FLOW.SurveyGroupMenuItemView = FLOW.View.extend({
  content: null,
  tagName: 'li',
  classNameBindings: 'amSelected:current'.w(),
  monitoringGroup: false,
  
  // true if the survey group is selected. Used to set proper display class
  amSelected: function () {
    var selected = FLOW.selectedControl.get('selectedSurveyGroup');
    if (selected) {
      var amSelected = (this.content.get('keyId') === FLOW.selectedControl.selectedSurveyGroup.get('keyId'));
      return amSelected;
    } else {
      return null;
    }
  }.property('FLOW.selectedControl.selectedSurveyGroup', 'content').cacheable(),

  // fired when a survey group is clicked
  makeSelected: function () {
    FLOW.selectedControl.set('selectedSurveyGroup', this.content);
  }
});

// displays single survey in content area of survey group page
// doEditSurvey is defined in the Router. It transfers to the nav-surveys-edit handlebar view
FLOW.SurveyGroupSurveyView = FLOW.View.extend({
  
	// fired when 'preview survey' is clicked in the survey item display
  previewSurvey: function () {
    FLOW.selectedControl.set('selectedSurvey', this.content);
  },

  showPreview: function () {
	if (FLOW.questionControl.content.get('isLoaded')) {
		FLOW.previewControl.set('showPreviewPopup', true);
	}
  }.observes('FLOW.questionControl.content.isLoaded'),
  
  // fired when 'delete survey' is clicked in the survey item display
  deleteSurvey: function () {
    var sId = this.content.get('id');
    var survey = FLOW.store.find(FLOW.Survey, sId);

    // do preflight check if deleting this survey is allowed
    // if successful, the deletion action will be called from DS.FLOWrestadaptor.sideload
    FLOW.store.findQuery(FLOW.Survey, {
      preflight: 'delete',
      surveyId: sId
    });
  },

  // fired when 'inspect data' is clicked in the survey item display
  inspectData: function () {
    console.log("TODO inspect Data");
  }

});

// handles all survey-group interaction elements on survey group page
FLOW.SurveyGroupMainView = FLOW.View.extend({
  showEditField: false,
  showNewGroupField: false,
  surveyGroupName: null,
  showSGDeleteDialog: false,
  showSGDeleteNotPossibleDialog: false,
  showCopySurveyDialogBool: false,
  newSurveyName: null,
  monitoringGroup:false,

  // true if at least one survey group is active
  oneSelected: function () {
    var selected = FLOW.selectedControl.get('selectedSurveyGroup');
    if (selected) {
      return true;
    } else {
      return false;
    }
  }.property('FLOW.selectedControl.selectedSurveyGroup'),
  
  initVars: function () {
	  if (!Ember.none('FLOW.selectedControl.selectedSurveyGroup')) {
		  this.set('monitoringGroup',FLOW.selectedControl.selectedSurveyGroup.get('monitoringGroup'));
	  }
  }.observes('FLOW.selectedControl.selectedSurveyGroup', 'FLOW.selectedControl.selectedSurvey'),

  // fired when 'edit name' is clicked, shows edit field to change survey group name
  editSurveyGroupName: function () {
    this.set('surveyGroupName', FLOW.selectedControl.selectedSurveyGroup.get('code'));
    this.set('showEditField', true);
  },

  // fired when 'save' is clicked while showing edit group name field. Saves the new group name
  saveSurveyGroupNameEdit: function () {
    if (!Ember.empty(this.get('surveyGroupName').trim())){
    	var sgId = FLOW.selectedControl.selectedSurveyGroup.get('id');
    	var surveyGroup = FLOW.store.find(FLOW.SurveyGroup, sgId);
    	surveyGroup.set('code', capitaliseFirstLetter(this.get('surveyGroupName')));
    	surveyGroup.set('name', capitaliseFirstLetter(this.get('surveyGroupName')));
    	FLOW.store.commit();
    	FLOW.selectedControl.set('selectedSurveyGroup', FLOW.store.find(FLOW.SurveyGroup, sgId));
    } else {
    	this.cancelSurveyGroupNameEdit();
    }
    this.set('showEditField', false);
  },

  // fired when 'cancel' is clicked while showing edit group name field. Cancels the edit.
  cancelSurveyGroupNameEdit: function () {
    this.set('surveyGroupName', FLOW.selectedControl.selectedSurveyGroup.get('code'));
    this.set('showEditField', false);
  },
  
  // fired when 'add a group' is clicked. Displays a new group text field in the left sidebar
  addGroup: function () {
    FLOW.selectedControl.set('selectedSurveyGroup', null);
    this.set('surveyGroupName', null);
    this.set('showNewGroupField', true);
  },

  deleteSurveyGroup: function () {
    var sgId = FLOW.selectedControl.selectedSurveyGroup.get('id');
    var surveyGroup = FLOW.store.find(FLOW.SurveyGroup, sgId);

    // do preflight check if deleting this survey is allowed
    // if successful, the deletion action will be called from DS.FLOWrestadaptor.sideload
    FLOW.store.findQuery(FLOW.SurveyGroup, {
      preflight: 'delete',
      surveyGroupId: sgId
    });
  },

  // fired when 'save' is clicked while showing new group text field in left sidebar. Saves new survey group to the data store
  saveNewSurveyGroupName: function () {
	  if (!Ember.empty(this.get('surveyGroupName').trim())){
		  FLOW.store.createRecord(FLOW.SurveyGroup, {
			  "code": capitaliseFirstLetter(this.get('surveyGroupName')),
			  "name": capitaliseFirstLetter(this.get('surveyGroupName'))
		  });
		  FLOW.store.commit();
	  }
    this.set('showNewGroupField', false);
  },

  // fired when 'cancel' is clicked while showing new group text field in left sidebar. Cancels the new survey group creation
  cancelNewSurveyGroupName: function () {
    this.set('surveyGroupName', null);
    this.set('showNewGroupField', false);
  },

  makeMonitorGroup: function () {
	  this.set('monitoringGroup', true);
	  FLOW.selectedControl.selectedSurveyGroup.set('monitoringGroup', true);
	  FLOW.store.commit();
  },

  saveNewLocaleSurveyIdChoice: function () {
	  var newLocaleSurvey = FLOW.surveyControl.get('newLocaleSurvey');
	  if (!Ember.none(newLocaleSurvey) && !Ember.none(newLocaleSurvey.get('keyId'))){
		  FLOW.selectedControl.selectedSurveyGroup.set('newLocaleSurveyId', newLocaleSurvey.get('keyId'));
		  FLOW.store.commit();
	  }
  }.observes('FLOW.surveyControl.newLocaleSurvey'),

  showCopySurveyDialog: function (event) {
    FLOW.selectedControl.set('selectedForCopySurvey', event.context);
    this.set('showCopySurveyDialogBool', true);
    this.set('newSurveyName', event.context.get('name') + " (copy)");
    this.set('selectedSurveyGroup', FLOW.selectedControl.get('selectedSurveyGroup'));
  },

  copySurvey: function () {
    var source = FLOW.selectedControl.selectedForCopySurvey;

    FLOW.store.createRecord(FLOW.Survey, {
      sourceId: source.get('id'),
      surveyGroupId: this.selectedSurveyGroup.get('keyId'),
      code: this.get('newSurveyName'),
      name: this.get('newSurveyName'),
      createdDateTime: source.get('createdDateTime'),
      lastUpdateDateTime: source.get('lastUpdateDateTime'),
      pointType: source.get('pointType'),
      defaultLanguageCode: source.get('defaultLanguageCode'),
      instanceCount: 0
    });

    FLOW.store.commit();
    FLOW.selectedControl.set('selectedForCopySurvey', null);
    this.set('showCopySurveyDialogBool', false);

    FLOW.dialogControl.set('activeAction', "ignore");
    FLOW.dialogControl.set('header', Ember.String.loc('_copying_survey'));
    FLOW.dialogControl.set('message', Ember.String.loc('_copying_published_text_'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  cancelMoveSurvey: function () {
    FLOW.selectedControl.set('selectedForCopySurvey', null);
    this.set('showCopySurveyDialogBool', false);
  }
});

FLOW.JavascriptSurveyGroupListView = FLOW.View.extend({
  didInsertElement: function () {
    var menuHeight, scroll;
    this._super();
    $('.scrollUp').addClass("FadeIt");
    $('.scrollUp').click(function (e) {
      e.preventDefault();
      menuHeight = $('.menuGroupWrap').height();
      scroll = $('.menuGroupWrap').scrollTop();
      $('.scrollDown').removeClass("FadeIt");
      $('.menuGroupWrap').animate({
        'scrollTop': scroll - 72
      }, 155);

      //the value used for scroll is the old one
      if (scroll < 73) {
        $('.scrollUp').addClass("FadeIt");
      }
    });
    $('.scrollDown').click(function (e) {
      e.preventDefault();
      menuHeight = $('.menuGroupWrap').height();
      scroll = $('.menuGroupWrap').scrollTop();
      $('.scrollUp').removeClass("FadeIt");
      $('.menuGroupWrap').animate({
        'scrollTop': scroll + 72
      }, 155);
    });
  },

  checkHeight: function () {
    var scroll;

    if (FLOW.surveyGroupControl.content.content.length < 10) {
      $('.scrollDown').addClass("FadeIt");
      $('.scrollUp').addClass("FadeIt");
    } else {
      scroll = $('.menuGroupWrap').scrollTop();
      $('.scrollDown').removeClass("FadeIt");
      if (scroll < 73) {
        $('.scrollUp').addClass("FadeIt");
      } else {
        $('.scrollUp').removeClass("FadeIt");
      }
    }
  }.observes('FLOW.surveyGroupControl.content.content.length')
});
