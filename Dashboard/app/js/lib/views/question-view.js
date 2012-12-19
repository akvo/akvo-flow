FLOW.QuestionView = Ember.View.extend({
	templateName: 'navSurveys/question-view',
	content: null,
	questionName: null,
	checkedMandatory: false,
	checkedDependent: false,
	checkedOptionMultiple: false,
	checkedOptionOther: false,
	selectedQuestionType: null,
	selectedOptionEdit: null,

	amOpenQuestion: function() {
		var selected = FLOW.selectedControl.get('selectedQuestion');
		if(selected && this.get('content')) {
			var isOpen = (this.content.get('keyId') == FLOW.selectedControl.selectedQuestion.get('keyId'));
			return isOpen;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedQuestion', 'content.keyId').cacheable(),


	amOptionType: function() {
		if(this.selectedQuestionType) {
			return(this.selectedQuestionType.get('value') == 'option') ? true : false;
		} else {
			return false;
		}
	}.property('this.selectedQuestionType').cacheable(),

	amNumberType: function() {
		if(this.selectedQuestionType) {
			return(this.selectedQuestionType.get('value') == 'number') ? true : false;
		} else {
			return false;
		}
	}.property('this.selectedQuestionType').cacheable(),

	doQuestionEdit: function() {
		FLOW.selectedControl.set('selectedQuestion', this.get('content'));
		this.set('questionName', FLOW.selectedControl.selectedQuestion.get('displayName'));
	},

	doCancelEditQuestion: function() {
		FLOW.selectedControl.set('selectedQuestion', null);
		console.log('canceling edit');
	},

	doSaveEditQuestion: function() {
		console.log('TODO save edit');
	},

	// BROKEN
	//TODO when questionAnswers already exist for a question, deletion should not be possible.
	deleteQuestion: function() {
		var qDeleteOrder, qDeleteId, question, questionGroupId;
		qDeleteId = this.content.get('keyId');

		question = FLOW.store.find(FLOW.Question, qDeleteId);
		question.deleteRecord();
		FLOW.store.commit();
	},

	// move question to selected location
	doQuestionMoveHere: function() {
		var selectedOrder, insertAfterOrder, selectedQ;
		selectedOrder = FLOW.selectedControl.selectedForMoveQuestion.get('order');

		if(this.get('zeroItem')) {
			insertAfterOrder = 0;
		} else {
			insertAfterOrder = this.content.get('order');
		}

		// only do something if we are not moving to the same place
		if(!((selectedOrder == insertAfterOrder) || (selectedOrder == (insertAfterOrder + 1)))) {
			selectedQ = FLOW.store.find(FLOW.Question, FLOW.selectedControl.selectedForMoveQuestion.get('keyId'));
			if(selectedQ !== null) {
				// the insertAfterOrder is inserted here
				// in the server, the proper order of all question groups is re-established
				selectedQ.set('order', insertAfterOrder);
				FLOW.store.commit();
			}
		}

		FLOW.store.commit();
		FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
	},

	// execute question copy to selected location
	doQuestionCopyHere: function() {
		var insertAfterOrder;

		if(this.get('zeroItem')) {
			insertAfterOrder = 0;
		} else {
			insertAfterOrder = this.content.get('order');
		}

		// create copy of Question item in the store
		// the insertAfterOrder is inserted here
		// in the server, the proper order of all question groups is re-established
		FLOW.store.createRecord(FLOW.Question, {
			"description": FLOW.selectedControl.selectedForCopyQuestion.get('description'),
			"order": insertAfterOrder,
			"code": FLOW.selectedControl.selectedForCopyQuestion.get('code'),
			"surveyId": FLOW.selectedControl.selectedForCopyQuestion.get('surveyId'),
			"questionGroupId": FLOW.selectedControl.selectedForCopyQuestion.get('questionGroupId')
		});

		FLOW.store.commit();
		FLOW.selectedControl.set('selectedForCopyQuestion', null);
	},

	// create new question
	doInsertQuestion: function() {
		var insertAfterOrder;

		if(this.get('zeroItemQuestion')) {
			insertAfterOrder = 0;
		} else {
			insertAfterOrder = this.content.get('order');
		}

		// create new Question item in the store
		// the insertAfterOrder is inserted here
		// in the server, the proper order of all question groups is re-established
		FLOW.store.createRecord(FLOW.Question, {
			"order": insertAfterOrder,
			"code": "new question - place change name",
			"surveyId": FLOW.selectedControl.selectedSurvey.get('keyId'),
			"questionGroupId": FLOW.selectedControl.selectedQuestionGroup.get('keyId')
		});

		FLOW.store.commit();
		FLOW.selectedControl.set('selectedForCopyQuestion', null);
	},


	// true if one question has been selected for Move
	oneSelectedForMove: function() {
		var selectedForMove = FLOW.selectedControl.get('selectedForMoveQuestion');
		if(selectedForMove) {
			return true;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedForMoveQuestion'),

	// true if one question has been selected for Copy
	oneSelectedForCopy: function() {
		var selectedForCopy = FLOW.selectedControl.get('selectedForCopyQuestion');
		if(selectedForCopy) {
			return true;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedForCopyQuestion'),

	// prepare for question copy. Shows 'copy to here' buttons
	doQuestionCopy: function() {
		FLOW.selectedControl.set('selectedForCopyQuestion', this.get('content'));
		FLOW.selectedControl.set('selectedForMoveQuestion', null);
	},

	// cancel question copy
	doQuestionCopyCancel: function() {
		FLOW.selectedControl.set('selectedForCopyQuestion', null);
	},


	// prepare for question move. Shows 'move here' buttons
	doQuestionMove: function() {
		FLOW.selectedControl.set('selectedForMoveQuestion', this.get('content'));
		FLOW.selectedControl.set('selectedForCopyQuestion', null);
	},

	// cancel group move
	doQuestionMoveCancel: function() {
		FLOW.selectedControl.set('selectedForMoveQuestion', null);
	}
});


//doQuestionDelete
//doQuestionMove
//doQuestionCopy
//doQuestionEdit
//doInsertQuestion