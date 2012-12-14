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

	doSaveSurvey: function() {
		var sgId, survey;
		console.log(this.get('surveySectorType'));
		sgId = FLOW.selectedControl.selectedSurvey.get('id');
		survey = FLOW.store.find(FLOW.Survey, sgId);
		survey.set('name', this.get('surveyTitle'));
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
		console.log("TODO: implement preview survey");
	},

	doPublishSurvey: function() {
		console.log("TODO: implement publish survey");
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
	// WRONG
	deleteQuestionGroup: function() {
		var qgDeleteOrder, qgDeleteId, questionGroup, surveyId;
		qgDeleteOrder = this.content.get('order');
		qgDeleteId = this.content.get('keyId');
		surveyId = FLOW.selectedControl.selectedSurvey.get('keyId');

		// move items down
		FLOW.store.filter(FLOW.QuestionGroup, function(data) {
			return(data.get('surveyId') == surveyId);
		}).forEach(function(item) {
			var currentOrder = item.get('order');

			if(currentOrder > qgDeleteOrder) {
				item.set('order', item.get('order') - 1);
			}
		});

		questionGroup = FLOW.store.find(FLOW.QuestionGroup, qgDeleteId);
		questionGroup.deleteRecord();
		FLOW.store.commit();
	},

	// insert group
	doInsertQuestionGroup: function() {
		var insertAfterOrder, surveyId;

		if(this.get('zeroItem')) {
			insertAfterOrder = 0;
		} else {
			insertAfterOrder = this.content.get('order');
		}
		surveyId = FLOW.selectedControl.selectedSurvey.get('keyId');
		console.log('surveyId:', surveyId);
		// move up to make space
		FLOW.store.filter(FLOW.QuestionGroup, function(data) {
			return(data.get('surveyId') == surveyId);
		}).forEach(function(item) {
			console.log(item.get('keyId'), item.get('order'));
			var currentOrder = item.get('order');
			if(currentOrder > insertAfterOrder) {
				item.set('order', item.get('order') + 1);
			}
		});
		// create new QuestionGroup item in the store
		var newRec = FLOW.store.createRecord(FLOW.QuestionGroup, {
			"code": "New group - please change name",
			"order": insertAfterOrder + 1,
			"surveyId": FLOW.selectedControl.selectedSurvey.get('keyId')
		});
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
		var selectedOrder = FLOW.selectedControl.selectedForMoveQuestionGroup.get('order');
		var insertAfterOrder;
		var movingUp = false;

		if(this.get('zeroItem')) {
			insertAfterOrder = 0;
		} else {
			insertAfterOrder = this.content.get('order');
		}

		// moving to the same place => do nothing
		if((selectedOrder == insertAfterOrder) || (selectedOrder == (insertAfterOrder + 1))) {} else {
			// determine if the item is moving up or down
			movingUp = (selectedOrder < insertAfterOrder);

			FLOW.questionGroupControl.get('content').forEach(function(item) {
				var currentOrder = item.get('order');

				// item moving up
				if(movingUp) {
					// if outside of change region, do not move
					if((currentOrder < selectedOrder) || (currentOrder > insertAfterOrder)) {}

					// move moving item to right location
					else if(currentOrder == selectedOrder) {
						item.set('order', insertAfterOrder);
					}

					// move rest down
					else {
						item.set('order', item.get('order') - 1);
					}
				}

				// item moving down
				else {
					if((currentOrder <= insertAfterOrder) || (currentOrder > selectedOrder)) {} else if(currentOrder == selectedOrder) {
						item.set('order', insertAfterOrder + 1);
					} else {
						item.set('order', item.get('order') + 1);
					}
				}
			}); // end of forEach
		} // end of top else
		FLOW.store.commit();
		FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
	},

	// execute group copy to selected location
	doQGroupCopyHere: function() {

		var selectedOrder = FLOW.selectedControl.selectedForCopyQuestionGroup.get('order');
		var insertAfterOrder;

		if(this.get('zeroItem')) {
			insertAfterOrder = 0;
		} else {
			insertAfterOrder = this.content.get('order');
		}

		// move up to make space
		FLOW.questionGroupControl.get('content').forEach(function(item) {
			var currentOrder = item.get('order');
			if(currentOrder > insertAfterOrder) {
				item.set('order', item.get('order') + 1);
			}
		}); // end of forEach
		// create copy of QuestionGroup item in the store
		var newRec = FLOW.store.createRecord(FLOW.QuestionGroup, {
			"description": FLOW.selectedControl.selectedForCopyQuestionGroup.get('description'),
			"order": insertAfterOrder + 1,
			"code": FLOW.selectedControl.selectedForCopyQuestionGroup.get('code'),
			"surveyId": FLOW.selectedControl.selectedForCopyQuestionGroup.get('surveyId'),
			"displayName": FLOW.selectedControl.selectedForCopyQuestionGroup.get('displayName')
		});

		FLOW.store.commit();
		FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
	}

});