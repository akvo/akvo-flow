// ************************ Surveys *************************
// FLOW.SurveySidebarView = FLOW.View.extend({
FLOW.SurveySidebarView = FLOW.View.extend({
  surveyTitle: null,
  surveyDescription: null,
  surveyPointType: null,
  language: null,
  isDirty: false,

  init: function () {
    var pointType = null,
      language = null;
    this._super();
    this.set('surveyTitle', FLOW.selectedControl.selectedSurvey.get('name'));
    this.set('surveyDescription', FLOW.selectedControl.selectedSurvey.get('description'));

    FLOW.surveyPointTypeControl.get('content').forEach(function (item) {
      if (item.get('value') == FLOW.selectedControl.selectedSurvey.get('pointType')) {
        pointType = item;
      }
    });
    this.set('surveyPointType', pointType);
    FLOW.translationControl.get('isoLangs').forEach(function (item) {
      if (item.get('value') == FLOW.selectedControl.selectedSurvey.get('defaultLanguageCode')) {
        language = item;
      }
    });
    this.set('language', language);
  },

  isExistingSurvey: function () {
    return !Ember.none(FLOW.selectedControl.selectedSurvey.get('keyId'));
  }.property('FLOW.selectedControl.selectedSurvey.keyId'),

  setIsDirty: function () {
    var isDirty, survey;
    survey = FLOW.selectedControl.get('selectedSurvey');
    isDirty = this.get('surveyTitle') != survey.get('name');

    if (!Ember.none(this.get('surveyDescription'))) {
      isDirty = isDirty || this.get('surveyDescription') != survey.get('description');
    } else {
      // if we don't have one now, but we had one before, it has also changed
      isDirty = isDirty || !Ember.none(survey.get('surveyDescription'));
    }

    if (!Ember.none(this.get('surveyPointType'))) {
      // if we have a surveyPointType, compare them
      isDirty = isDirty || this.surveyPointType.get('value') != survey.get('pointType');
    } else {
      isDirty = isDirty || this.get('surveyPointType') === null;
      // if we don't have one now, but we had one before, it has also changed
      // TODO - this breaks when the pointType is an old point Type
      //isDirty = isDirty || !Ember.none(survey.get('pointType'));
    }

    if (!Ember.none(this.get('language'))) {
      isDirty = isDirty || this.language.get('value') != survey.get('defaultLanguageCode');
    } else {
      isDirty = isDirty || !Ember.empty(survey.get('defaultLanguageCode'));
    }
    this.set('isDirty', isDirty);
  },

  isPublished: function () {
    return FLOW.selectedControl.selectedSurvey.get('status') == 'PUBLISHED';
  }.property('FLOW.selectedControl.selectedSurvey.status'),

  numberQuestions: function () {
    if (Ember.none(FLOW.questionControl.get('filterContent'))) {
      return 0;
    }
    return FLOW.questionControl.filterContent.toArray().length;
  }.property('FLOW.questionControl.filterContent.@each'),

  numberQuestionGroups: function () {
    if (Ember.none(FLOW.questionGroupControl.get('content'))) {
      return 0;
    }
    return FLOW.questionGroupControl.content.toArray().length;
  }.property('FLOW.questionGroupControl.content.@each'),

  surveyNotComplete: function () {
	 if (Ember.empty(this.get('surveyTitle'))) {
		 FLOW.dialogControl.set('activeAction', 'ignore');
		 FLOW.dialogControl.set('header', Ember.String.loc('_survey_title_not_set'));
		 FLOW.dialogControl.set('message', Ember.String.loc('_survey_title_not_set_text'));
		 FLOW.dialogControl.set('showCANCEL', false);
		 FLOW.dialogControl.set('showDialog', true);
		 return true;
	 }
	 if (Ember.empty(this.get('surveyPointType'))) {
		 FLOW.dialogControl.set('activeAction', 'ignore');
		 FLOW.dialogControl.set('header', Ember.String.loc('_survey_type_not_set'));
		 FLOW.dialogControl.set('message', Ember.String.loc('_survey_type_not_set_text'));
		 FLOW.dialogControl.set('showCANCEL', false);
		 FLOW.dialogControl.set('showDialog', true);
		 return true;
	 }
	 return false;
  },
  
  doManageTranslations: function () {
	// check if we have questions that are still loading
	if (Ember.none(FLOW.questionControl.get('content'))){
	  	FLOW.dialogControl.set('activeAction', "ignore");
	  	FLOW.dialogControl.set('header', Ember.String.loc('_no_questions'));
	  	FLOW.dialogControl.set('message', Ember.String.loc('_no_questions_text'));
	  	FLOW.dialogControl.set('showCANCEL', false);
	  	FLOW.dialogControl.set('showDialog', true);
	    return;
	}
	// check if we have questions that are still loading
	if (!FLOW.questionControl.content.get('isLoaded')){
  		FLOW.dialogControl.set('activeAction', "ignore");
  	    FLOW.dialogControl.set('header', Ember.String.loc('_questions_still_loading'));
  	    FLOW.dialogControl.set('message', Ember.String.loc('_questions_still_loading_text'));
  	    FLOW.dialogControl.set('showCANCEL', false);
  	    FLOW.dialogControl.set('showDialog', true);
  		return;
  	}
	if (this.surveyNotComplete()){
		return;
	}
	// check if we have any unsaved changes
	survey = FLOW.store.find(FLOW.Survey, FLOW.selectedControl.selectedSurvey.get('keyId'));
	this.setIsDirty();
	if (!Ember.none(survey) && this.get('isDirty')) {
	    FLOW.dialogControl.set('activeAction', "ignore");
	    FLOW.dialogControl.set('header', Ember.String.loc('_you_have_unsaved_changes'));
	    FLOW.dialogControl.set('message', Ember.String.loc('_before_translations_save'));
	    FLOW.dialogControl.set('showCANCEL', false);      FLOW.dialogControl.set('showDialog', true);
	    return;
	}
	FLOW.router.transitionTo('navSurveys.navSurveysEdit.manageTranslations');
  },
  
  
  doManageNotifications: function () {
	if (this.surveyNotComplete()){
		return;
	}
	// check if we have any unsaved changes
	survey = FLOW.store.find(FLOW.Survey, FLOW.selectedControl.selectedSurvey.get('keyId'));
	this.setIsDirty();
	if (!Ember.none(survey) && this.get('isDirty')) {
		 FLOW.dialogControl.set('activeAction', "ignore");
		 FLOW.dialogControl.set('header', Ember.String.loc('_you_have_unsaved_changes'));
		 FLOW.dialogControl.set('message', Ember.String.loc('_before_notifications_save'));
		 FLOW.dialogControl.set('showCANCEL', false);      FLOW.dialogControl.set('showDialog', true);
		 return;
	}
	FLOW.router.transitionTo('navSurveys.navSurveysEdit.manageNotifications');
  },
  
  doSaveSurvey: function () {
    var survey;
    if (this.surveyNotComplete()){
		return;
	}
    survey = FLOW.selectedControl.get('selectedSurvey');
    survey.set('name', this.get('surveyTitle'));
    survey.set('code', this.get('surveyTitle'));
    survey.set('status', 'NOT_PUBLISHED');
    survey.set('path', FLOW.selectedControl.selectedSurveyGroup.get('code'));
    survey.set('description', this.get('surveyDescription'));
    if (this.get('surveyPointType') !== null) {
      survey.set('pointType', this.surveyPointType.get('value'));
    } else {
      survey.set('pointType', null);
    }
    if (this.get('language') !== null) {
      survey.set('defaultLanguageCode', this.language.get('value'));
    } else {
      survey.set('defaultLanguageCode', null);
    }
    FLOW.store.commit();
  },

  doPreviewSurvey: function () {
    FLOW.previewControl.set('showPreviewPopup', true);
  },

  doPublishSurvey: function () {
    var survey;
    // validation
    if (this.get('surveyPointType') === null) {
      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', Ember.String.loc('_survey_type_not_set'));
      FLOW.dialogControl.set('message', Ember.String.loc('_survey_type_not_set_text'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
      return;
    }

    // check if survey has unsaved changes
    survey = FLOW.store.find(FLOW.Survey, FLOW.selectedControl.selectedSurvey.get('keyId'));
    this.setIsDirty();
    if (!Ember.none(survey) && this.get('isDirty')) {
      FLOW.dialogControl.set('activeAction', "ignore");
      FLOW.dialogControl.set('header', Ember.String.loc('_you_have_unsaved_changes'));
      FLOW.dialogControl.set('message', Ember.String.loc('_before_publishing_'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);

    } else {
      FLOW.surveyControl.publishSurvey();
      FLOW.dialogControl.set('activeAction', "ignore");
      FLOW.dialogControl.set('header', Ember.String.loc('_publishing_survey'));
      FLOW.dialogControl.set('message', Ember.String.loc('_survey_published_text_'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
    }
  },

  doSurveysMain: function () {
    var item;
    // if the survey does not have a keyId, it has not been saved, so delete it.
    if (Ember.none(FLOW.selectedControl.selectedSurvey.get('keyId'))) {
      item = FLOW.selectedControl.get('selectedSurvey');
      item.deleteRecord();
    }
    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.router.transitionTo('navSurveys.navSurveysMain');
  }
});

FLOW.QuestionGroupItemTranslationView = FLOW.View.extend({
	content: null,
	 // question group content comes through binding in handlebars file
	amVisible: function () {
	  var selected, isVis;
	  selected = FLOW.selectedControl.get('selectedQuestionGroup');
	  if (selected) {
	     isVis = (this.content.get('keyId') === FLOW.selectedControl.selectedQuestionGroup.get('keyId'));
	     return isVis;
	   } else {
	     return null;
	   }
	 }.property('FLOW.selectedControl.selectedQuestionGroup', 'content.keyId').cacheable(),

	toggleVisibility: function () {
	   if (this.get('amVisible')) {
		 // if we have any unsaved translations, do nothing.
		 // a warning will be printed by the check method.
		   console.log('unsaved? ',FLOW.translationControl.unsavedTranslations());
		 if (FLOW.translationControl.unsavedTranslations()){
			 return;
		 }
	     FLOW.selectedControl.set('selectedQuestionGroup', null);
	     // empty translation structures
	   } else {
	     FLOW.selectedControl.set('selectedQuestionGroup', this.content);
	     FLOW.translationControl.loadQuestionGroup(this.content.get('keyId'));
	   }
	}
});


FLOW.QuestionGroupItemView = FLOW.View.extend({
  content: null,
  // question group content comes through binding in handlebars file
  zeroItem: false,
  renderView: false,
  showQGDeletedialog: false,
  showQGroupNameEditField: false,
  questionGroupName: null,

  amVisible: function () {
    var selected, isVis;
    selected = FLOW.selectedControl.get('selectedQuestionGroup');
    if (selected) {

      isVis = (this.content.get('keyId') === FLOW.selectedControl.selectedQuestionGroup.get('keyId'));
      return isVis;
    } else {
      return null;
    }
  }.property('FLOW.selectedControl.selectedQuestionGroup', 'content.keyId').cacheable(),

  toggleVisibility: function () {
    if (this.get('amVisible')) {
      FLOW.selectedControl.set('selectedQuestionGroup', null);
    } else {
      FLOW.selectedControl.set('selectedQuestionGroup', this.content);
    }
  },

  doQGroupNameEdit: function () {
    this.set('questionGroupName', this.content.get('code'));
    this.set('showQGroupNameEditField', true);
  },

  // fired when 'save' is clicked while showing edit group name field. Saves the new group name
  saveQuestionGroupNameEdit: function () {
    var path, qgId, questionGroup;
    qgId = this.content.get('id');
    questionGroup = FLOW.store.find(FLOW.QuestionGroup, qgId);
    path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name');
    questionGroup.set('code', this.get('questionGroupName'));
    questionGroup.set('name', this.get('questionGroupName'));
    questionGroup.set('path', path);

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
    this.set('showQGroupNameEditField', false);
  },

  // fired when 'cancel' is clicked while showing edit group name field. Cancels the edit.
  cancelQuestionGroupNameEdit: function () {
    this.set('questionGroupName', null);
    this.set('showQGroupNameEditField', false);
  },

  // true if one question group has been selected for Move
  oneSelectedForMove: function () {
    var selectedForMove = FLOW.selectedControl.get('selectedForMoveQuestionGroup');
    if (selectedForMove) {
      return true;
    } else {
      return false;
    }
  }.property('FLOW.selectedControl.selectedForMoveQuestionGroup'),

  // true if one question group has been selected for Copy
  oneSelectedForCopy: function () {
    var selectedForCopy = FLOW.selectedControl.get('selectedForCopyQuestionGroup');
    if (selectedForCopy) {
      return true;
    } else {
      return false;
    }
  }.property('FLOW.selectedControl.selectedForCopyQuestionGroup'),

  // execute group delete
  deleteQuestionGroup: function () {
    var qgDeleteId, questionGroup, questionsGroupsInSurvey, sId, qgOrder, questionsInGroup;
    qgDeleteId = this.content.get('keyId');
    sId = this.content.get('surveyId');
    questionGroup = FLOW.store.find(FLOW.QuestionGroup, qgDeleteId);
    qgOrder = questionGroup.get('order');
    questionsInGroup = FLOW.store.filter(FLOW.Question, function (item) {
      return item.get('questionGroupId') == qgDeleteId;
    });

    if (questionsInGroup.get('content').length > 0) {
      FLOW.dialogControl.set('activeAction', "ignore");
      FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_questiongroup'));
      FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_questiongroup_text'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
      return;
    }

    // if we are here, we can safely delete
    questionGroup.deleteRecord();
    // restore order
    questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, function (item) {
      return item.get('surveyId') == sId;
    });

    // restore order
    questionGroupsInSurvey.forEach(function (item) {
      if (item.get('order') > qgOrder) {
        item.set('order', item.get('order') - 1);
      }
    });
    // restore order in case the order has gone haywire
    FLOW.questionControl.restoreOrder(questionGroupsInSurvey);
    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();

  },

  // insert group
  doInsertQuestionGroup: function () {
    var insertAfterOrder, path, sId, questionGroupsInSurvey;
    path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name');
    if (FLOW.selectedControl.selectedSurvey.get('keyId')) {

      if (this.get('zeroItem')) {
        insertAfterOrder = 0;
      } else {
        insertAfterOrder = this.content.get('order');
      }

      // restore order
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, function (item) {
        return item.get('surveyId') == sId;
      });

      // move items up to make space
      questionGroupsInSurvey.forEach(function (item) {
        if (item.get('order') > insertAfterOrder) {
          item.set('order', item.get('order') + 1);
        }
      });

      // create new QuestionGroup item in the store
      FLOW.store.createRecord(FLOW.QuestionGroup, {
        "code": Ember.String.loc('_new_group_please_change_name'),
        "name": Ember.String.loc('_new_group_please_change_name'),
        "order": insertAfterOrder + 1,
        "path": path,
        "surveyId": FLOW.selectedControl.selectedSurvey.get('keyId')
      });

      // get the question groups again, now it contains the new one as well
      questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, function (item) {
        return item.get('surveyId') == sId;
      });

      // restore order in case the order has gone haywire
      FLOW.questionControl.restoreOrder(questionGroupsInSurvey);

      FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
      FLOW.store.commit();
      FLOW.questionGroupControl.setFilteredContent();
    } else {
      FLOW.dialogControl.set('activeAction', "ignore");
      FLOW.dialogControl.set('header', Ember.String.loc('_please_save_survey'));
      FLOW.dialogControl.set('message', Ember.String.loc('_please_save_survey_text'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
    }
  },

  // prepare for group copy. Shows 'copy to here' buttons
  doQGroupCopy: function () {
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', this.content);
    FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
  },


  // cancel group copy
  doQGroupCopyCancel: function () {
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
  },


  // prepare for group move. Shows 'move here' buttons
  doQGroupMove: function () {
    FLOW.selectedControl.set('selectedForMoveQuestionGroup', this.content);
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
  },

  // cancel group move
  doQGroupMoveCancel: function () {
    FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
  },

  // execute group move to selected location
  doQGroupMoveHere: function () {
    var selectedOrder, insertAfterOrder, selectedQG, sId, questionGroupsInSurvey, origOrder, movingUp;
    selectedOrder = FLOW.selectedControl.selectedForMoveQuestionGroup.get('order');

    if (this.get('zeroItem')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }

    // only do something if we are not moving to the same place
    if (!((selectedOrder == insertAfterOrder) || (selectedOrder == (insertAfterOrder + 1)))) {
      selectedQG = FLOW.store.find(FLOW.QuestionGroup, FLOW.selectedControl.selectedForMoveQuestionGroup.get('keyId'));
      if (selectedQG !== null) {

        // selectedQG.set('order', insertAfterOrder + 1);
        // restore order
        sId = FLOW.selectedControl.selectedSurvey.get('keyId');
        questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, function (item) {
          return item.get('surveyId') == sId;
        });

        origOrder = FLOW.selectedControl.selectedForMoveQuestionGroup.get('order');
        movingUp = origOrder < insertAfterOrder;

        questionGroupsInSurvey.forEach(function (item) {
          currentOrder = item.get('order');
          if (movingUp) {
            if (currentOrder == origOrder) {
              // move moving item to right location
              selectedQG.set('order', insertAfterOrder);
            } else if ((currentOrder > origOrder) && (currentOrder <= insertAfterOrder)) {
              // move item down
              item.set('order', item.get('order') - 1);
            }
          } else {
            // Moving down
            if (currentOrder == origOrder) {
              // move moving item to right location
              selectedQG.set('order', insertAfterOrder + 1);
            } else if ((currentOrder < origOrder) && (currentOrder > insertAfterOrder)) {
              // move item up
              item.set('order', item.get('order') + 1);
            }
          }
        });

        // restore order in case the order has gone haywire
        FLOW.questionControl.restoreOrder(questionGroupsInSurvey);

        FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
        FLOW.store.commit();
      }
    }

    FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
  },

  // execute group copy to selected location
  // TODO should this copy all questions in the group?
  doQGroupCopyHere: function () {
    var insertAfterOrder, path, sId, questionGroupsInSurvey;
    path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name');

    if (this.get('zeroItem')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }

    sId = FLOW.selectedControl.selectedSurvey.get('keyId');
    questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, function (item) {
      return item.get('surveyId') == sId;
    });

    // restore order - move items up to make space
    questionGroupsInSurvey.forEach(function (item) {
      if (item.get('order') > insertAfterOrder) {
        item.set('order', item.get('order') + 1);
      }
    });

    FLOW.store.createRecord(FLOW.QuestionGroup, {
      "description": FLOW.selectedControl.selectedForCopyQuestionGroup.get('description'),
      "order": insertAfterOrder + 1,
      "code": FLOW.selectedControl.selectedForCopyQuestionGroup.get('code'),
      "name": FLOW.selectedControl.selectedForCopyQuestionGroup.get('code'),
      "path": path,
      "surveyId": FLOW.selectedControl.selectedForCopyQuestionGroup.get('surveyId')
    });

      // get the question groups again, now it contains the new one as well
      questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, function (item) {
        return item.get('surveyId') == sId;
      });

      // restore order in case the order has gone haywire
      FLOW.questionControl.restoreOrder(questionGroupsInSurvey);

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
  }

});
