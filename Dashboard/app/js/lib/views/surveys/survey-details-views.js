// ************************ Surveys *************************
FLOW.SurveySidebarView = Ember.View.extend({
	surveyTitle: null,
	surveyDescription: null,
	surveyPointType: null,
	surveySectorType: null,

	init: function() {
		var sectorType = null,
			pointType = null;
		this._super();
		this.set('surveyTitle', FLOW.selectedControl.selectedSurvey.get('name'));
		this.set('surveyDescription', FLOW.selectedControl.selectedSurvey.get('description'));
		this.set('surveyType', FLOW.selectedControl.selectedSurvey.get('pointType'));

		FLOW.surveyPointTypeControl.get('content').forEach(function(item) {
			if(item.get('value') == FLOW.selectedControl.selectedSurvey.get('pointType')) {
				pointType = item;
			}
		});
		this.set('surveyPointType', pointType);

		FLOW.surveySectorTypeControl.get('content').forEach(function(item) {
			if(item.get('value') == FLOW.selectedControl.selectedSurvey.get('sector')) {
				sectorType = item;
			}
		});
		this.set('surveySectorType', sectorType);
	},

	isExistingSurvey: function() {
		return !Ember.none(FLOW.selectedControl.selectedSurvey.get('keyId'));
	}.property('FLOW.selectedControl.selectedSurvey.keyId'),

	isPublished: function() {
		return (FLOW.selectedControl.selectedSurvey.get('status') == 'PUBLISHED');
	}.property('FLOW.selectedControl.selectedSurvey.status'),

	numberQuestions: function () {
		if (Ember.none(FLOW.questionControl.get('filterContent'))){
			return 0;
		}
		return FLOW.questionControl.filterContent.toArray().length;
	}.property('FLOW.questionControl.filterContent.@each'),

	numberQuestionGroups: function () {
		if (Ember.none(FLOW.questionGroupControl.get('content'))){
			return 0;
		}
		return FLOW.questionGroupControl.content.toArray().length;
	}.property('FLOW.questionGroupControl.content.@each'),

	doSaveSurvey: function() {
		var sgId, survey;
		survey = FLOW.selectedControl.get('selectedSurvey');
		survey.set('name', this.get('surveyTitle'));
		survey.set('status','NOT_PUBLISHED');
		survey.set('description', this.get('surveyDescription'));
		if(this.get('surveyPointType') !== null) {
			survey.set('pointType', this.surveyPointType.get('value'));
		}
		if(this.get('surveySectorType') !== null) {
			survey.set('sector', this.surveySectorType.get('value'));
		}
		FLOW.store.commit();
	},

	doPreviewSurvey: function() {
		FLOW.previewControl.set('showPreviewPopup',true);
	},

	doPublishSurvey: function() {
		FLOW.surveyControl.publishSurvey();
	}
});


FLOW.QuestionGroupItemView = Ember.View.extend({
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
		var qgId = this.content.get('id');
		var questionGroup = FLOW.store.find(FLOW.QuestionGroup, qgId);
		questionGroup.set('code', this.get('questionGroupName'));
		FLOW.selectedControl.selectedSurvey.set('status','NOT_PUBLISHED');
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
		FLOW.selectedControl.selectedSurvey.set('status','NOT_PUBLISHED');
		FLOW.store.commit();
	},

	// insert group
	doInsertQuestionGroup: function() {
		var insertAfterOrder;

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
			"order": insertAfterOrder,
			"surveyId": FLOW.selectedControl.selectedSurvey.get('keyId')
		});
		FLOW.selectedControl.selectedSurvey.set('status','NOT_PUBLISHED');
		FLOW.store.commit();
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
				selectedQG.set('order',insertAfterOrder);
				FLOW.selectedControl.selectedSurvey.set('status','NOT_PUBLISHED');
				FLOW.store.commit();
			}
		}

		FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
	},

	// execute group copy to selected location
	// TODO should this copy all questions in the group?
	doQGroupCopyHere: function() {
		var insertAfterOrder;

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
			"surveyId": FLOW.selectedControl.selectedForCopyQuestionGroup.get('surveyId')
		});
		FLOW.selectedControl.selectedSurvey.set('status','NOT_PUBLISHED');
		FLOW.store.commit();
		FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
	}

});