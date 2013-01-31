// ************************ Surveys *************************
// FLOW.SurveySidebarView = FLOW.View.extend({
FLOW.SurveySidebarView = FLOW.View.extend({
	surveyTitle: null,
	surveyDescription: null,
	surveyPointType: null,
	language: null,
	isDirty: false,

	init: function() {
		var sectorType = null,
			pointType = null,
			language = null;
		this._super();
		this.set('surveyTitle', FLOW.selectedControl.selectedSurvey.get('name'));
		this.set('surveyDescription', FLOW.selectedControl.selectedSurvey.get('description'));

		FLOW.surveyPointTypeControl.get('content').forEach(function(item) {
			if(item.get('value') == FLOW.selectedControl.selectedSurvey.get('pointType')) {
				pointType = item;
			}
		});
		this.set('surveyPointType', pointType);

		FLOW.languageControl.get('content').forEach(function(item) {
			if(item.get('value') == FLOW.selectedControl.selectedSurvey.get('defaultLanguageCode')) {
				language = item;
			}
		});
		this.set('language', language);
	},

	isExistingSurvey: function() {
		return !Ember.none(FLOW.selectedControl.selectedSurvey.get('keyId'));
	}.property('FLOW.selectedControl.selectedSurvey.keyId'),

	setIsDirty: function() {
		var isDirty, survey;
		survey = FLOW.selectedControl.get('selectedSurvey');
		isDirty = this.get('surveyTitle') != survey.get('name');

		if(!Ember.none(this.get('surveyDescription'))) {
			isDirty = isDirty || this.get('surveyDescription') != survey.get('surveyDescription');
		} else {
			// if we don't have one now, but we had one before, it has also changed
			isDirty = isDirty || !Ember.none(survey.get('surveyDescription'));
		}

		if(!Ember.none(this.get('surveyPointType'))) {
			// if we have a surveyPointType, compare them
			isDirty = isDirty || this.surveyPointType.get('value') != survey.get('pointType');
		} else {
			// if we don't have one now, but we had one before, it has also changed
			// TODO - this breaks when the pointType is an old point Type
			//isDirty = isDirty || !Ember.none(survey.get('pointType'));
		}

		if(!Ember.none(this.get('language'))) {
			isDirty = isDirty || this.language.get('value') != survey.get('defaultLanguageCode');
		} else {
			isDirty = isDirty || !Ember.empty(survey.get('defaultLanguageCode'));
		}
		this.set('isDirty', isDirty);
	},

	isPublished: function() {
		return(FLOW.selectedControl.selectedSurvey.get('status') == 'PUBLISHED');
	}.property('FLOW.selectedControl.selectedSurvey.status'),

	numberQuestions: function() {
		if(Ember.none(FLOW.questionControl.get('filterContent'))) {
			return 0;
		}
		return FLOW.questionControl.filterContent.toArray().length;
	}.property('FLOW.questionControl.filterContent.@each'),

	numberQuestionGroups: function() {
		if(Ember.none(FLOW.questionGroupControl.get('content'))) {
			return 0;
		}
		return FLOW.questionGroupControl.content.toArray().length;
	}.property('FLOW.questionGroupControl.content.@each'),

	doSaveSurvey: function() {
		var survey;
		survey = FLOW.selectedControl.get('selectedSurvey');
		survey.set('name', this.get('surveyTitle'));
		survey.set('code', this.get('surveyTitle'));
		survey.set('status', 'NOT_PUBLISHED');
		survey.set('path', FLOW.selectedControl.selectedSurveyGroup.get('code'));
		survey.set('description', this.get('surveyDescription'));
		if(this.get('surveyPointType') !== null) {
			survey.set('pointType', this.surveyPointType.get('value'));
		} else {
			survey.set('pointType', null);
		}
		if(this.get('language') !== null) {
			survey.set('defaultLanguageCode', this.language.get('value'));
		} else {
			survey.set('defaultLanguageCode', null);
		}
		FLOW.store.commit();
		this.setIsDirty();
	},

	doPreviewSurvey: function() {
		FLOW.previewControl.set('showPreviewPopup', true);
	},

	doPublishSurvey: function() {
		var survey;
		// check if survey has unsaved changes
		survey = FLOW.store.find(FLOW.Survey, FLOW.selectedControl.selectedSurvey.get('keyId'));
		this.setIsDirty();
		if(!Ember.none(survey) && this.get('isDirty')) {
			FLOW.dialogControl.set('activeAction', "ignore");
			FLOW.dialogControl.set('header', Ember.String.loc('_save_before_publishing'));
			FLOW.dialogControl.set('message', Ember.String.loc('_save_before_publishing_text'));
			FLOW.dialogControl.set('showCANCEL', false);
			FLOW.dialogControl.set('showDialog', true);

		} else {
			FLOW.surveyControl.publishSurvey();
			FLOW.dialogControl.set('activeAction', "ignore");
			FLOW.dialogControl.set('header', Ember.String.loc('_survey_published'));
			FLOW.dialogControl.set('message', Ember.String.loc('_survey_published_text'));
			FLOW.dialogControl.set('showCANCEL', false);
			FLOW.dialogControl.set('showDialog', true);
		}
	},

	doSurveysMain: function() {
		var item;
		// if the survey does not have a keyId, it has not been saved, so delete it.
		if(Ember.none(FLOW.selectedControl.selectedSurvey.get('keyId'))) {
			item = FLOW.selectedControl.get('selectedSurvey');
			item.deleteRecord();
		}
		FLOW.router.transitionTo('navSurveys.navSurveysMain');
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

	amVisible: function() {
		var selected, isVis;
		selected = FLOW.selectedControl.get('selectedQuestionGroup');
		if(selected) {

			isVis = (this.content.get('keyId') === FLOW.selectedControl.selectedQuestionGroup.get('keyId'));
			return isVis;
		} else {
			return null;
		}
	}.property('FLOW.selectedControl.selectedQuestionGroup', 'content.keyId').cacheable(),

	toggleVisibility: function() {
		if(this.get('amVisible')) {
			FLOW.selectedControl.set('selectedQuestionGroup', null);
		} else {
			FLOW.selectedControl.set('selectedQuestionGroup', this.content);
		}
	},

	doQGroupNameEdit: function() {
		this.set('questionGroupName', this.content.get('code'));
		this.set('showQGroupNameEditField', true);
	},

	// fired when 'save' is clicked while showing edit group name field. Saves the new group name
	saveQuestionGroupNameEdit: function() {
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
	cancelQuestionGroupNameEdit: function() {
		this.set('questionGroupName', null);
		this.set('showQGroupNameEditField', false);
	},

	// true if one question group has been selected for Move
	oneSelectedForMove: function() {
		var selectedForMove = FLOW.selectedControl.get('selectedForMoveQuestionGroup');
		if(selectedForMove) {
			return true;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedForMoveQuestionGroup'),

	// true if one question group has been selected for Copy
	oneSelectedForCopy: function() {
		var selectedForCopy = FLOW.selectedControl.get('selectedForCopyQuestionGroup');
		if(selectedForCopy) {
			return true;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedForCopyQuestionGroup'),

	// execute group delete
	// TODO should this be allowed when questions are present?
	deleteQuestionGroup: function() {
		var qgDeleteId, questionGroup;
		qgDeleteId = this.content.get('keyId');

		questionGroup = FLOW.store.find(FLOW.QuestionGroup, qgDeleteId);
		questionGroup.deleteRecord();
		FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
		FLOW.store.commit();
	},

	// insert group
	doInsertQuestionGroup: function() {
		var insertAfterOrder, path;
		path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name');

		if(FLOW.selectedControl.selectedSurvey.get('keyId')) {

			if(this.get('zeroItem')) {
				insertAfterOrder = 0;
			} else {
				insertAfterOrder = this.content.get('order');
			}

			// create new QuestionGroup item in the store
			// the insertAfterOrder is inserted here
			// in the server, the proper order of all question groups is re-established
			FLOW.store.createRecord(FLOW.QuestionGroup, {
				"code": "New group - please change name",
				"name": "New group - please change name",
				"order": insertAfterOrder,
				"path": path,
				"surveyId": FLOW.selectedControl.selectedSurvey.get('keyId')
			});
			FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
			FLOW.store.commit();
		} else {
			FLOW.dialogControl.set('activeAction', "ignore");
			FLOW.dialogControl.set('header', Ember.String.loc('_please_save_survey'));
			FLOW.dialogControl.set('message', Ember.String.loc('_please_save_survey_text'));
			FLOW.dialogControl.set('showCANCEL', false);
			FLOW.dialogControl.set('showDialog', true);
		}
	},

	// prepare for group copy. Shows 'copy to here' buttons
	doQGroupCopy: function() {
		FLOW.selectedControl.set('selectedForCopyQuestionGroup', this.content);
		FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
	},


	// cancel group copy
	doQGroupCopyCancel: function() {
		FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
	},


	// prepare for group move. Shows 'move here' buttons
	doQGroupMove: function() {
		FLOW.selectedControl.set('selectedForMoveQuestionGroup', this.content);
		FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
	},

	// cancel group move
	doQGroupMoveCancel: function() {
		FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
	},


	// execture group move to selected location
	doQGroupMoveHere: function() {
		var selectedOrder, insertAfterOrder, selectedQG;
		selectedOrder = FLOW.selectedControl.selectedForMoveQuestionGroup.get('order');

		if(this.get('zeroItem')) {
			insertAfterOrder = 0;
		} else {
			insertAfterOrder = this.content.get('order');
		}

		// only do something if we are not moving to the same place
		if(!((selectedOrder == insertAfterOrder) || (selectedOrder == (insertAfterOrder + 1)))) {
			selectedQG = FLOW.store.find(FLOW.QuestionGroup, FLOW.selectedControl.selectedForMoveQuestionGroup.get('keyId'));
			if(selectedQG !== null) {
				// the insertAfterOrder is inserted here
				// in the server, the proper order of all question groups is re-established
				selectedQG.set('order', insertAfterOrder);
				FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
				FLOW.store.commit();
			}
		}

		FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
	},

	// execute group copy to selected location
	// TODO should this copy all questions in the group?
	doQGroupCopyHere: function() {
		var insertAfterOrder, path;
		path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name');

		if(this.get('zeroItem')) {
			insertAfterOrder = 0;
		} else {
			insertAfterOrder = this.content.get('order');
		}

		// the insertAfterOrder is inserted here
		// in the server, the proper order of all question groups is re-established
		FLOW.store.createRecord(FLOW.QuestionGroup, {
			"description": FLOW.selectedControl.selectedForCopyQuestionGroup.get('description'),
			"order": insertAfterOrder,
			"code": FLOW.selectedControl.selectedForCopyQuestionGroup.get('code'),
			"name": FLOW.selectedControl.selectedForCopyQuestionGroup.get('code'),
			"path": path,
			"surveyId": FLOW.selectedControl.selectedForCopyQuestionGroup.get('surveyId')
		});
		FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
		FLOW.store.commit();
		FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
	}

});