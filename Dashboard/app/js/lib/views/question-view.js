FLOW.QuestionView = Ember.View.extend({
	templateName: 'navSurveys/question-view',
	content: null,
	text: null,
	tip: null,
	type: null,
	mandatoryFlag: null,
	minVal: null,
	maxVal: null,
	allowSign: null,
	allowDecimalPoint: null,
	allowMultipleFlag: null,
	allowOtherFlag: null,
	dependentFlag: null,
	dependentQuestion: null,
	optionList: null,

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
		if(this.type) {
			return(this.type.get('value') == 'OPTION') ? true : false;
		} else {
			return false;
		}
	}.property('this.type').cacheable(),

	amNumberType: function() {
		if(this.type) {
			return(this.type.get('value') == 'NUMBER') ? true : false;
		} else {
			return false;
		}
	}.property('this.type').cacheable(),

	// TODO dependencies
	// TODO options
	doQuestionEdit: function() {
		var questionType;

		FLOW.selectedControl.set('selectedQuestion', this.get('content'));
		this.set('text', FLOW.selectedControl.selectedQuestion.get('text'));
		this.set('tip', FLOW.selectedControl.selectedQuestion.get('tip'));
		this.set('mandatoryFlag', FLOW.selectedControl.selectedQuestion.get('mandatoryFlag'));
		this.set('minVal', FLOW.selectedControl.selectedQuestion.get('minVal'));
		this.set('maxVal', FLOW.selectedControl.selectedQuestion.get('maxVal'));
		this.set('allowSign', FLOW.selectedControl.selectedQuestion.get('allowSign'));
		this.set('allowDecimalPoint', FLOW.selectedControl.selectedQuestion.get('allowDecimalPoint'));
		this.set('allowMultipleFlag', FLOW.selectedControl.selectedQuestion.get('allowMultipleFlag'));
		this.set('allowOtherFlag', FLOW.selectedControl.selectedQuestion.get('allowOtherFlag'));
		this.set('dependentFlag', FLOW.selectedControl.selectedQuestion.get('dependentFlag'));
		this.set('optionList', FLOW.selectedControl.selectedQuestion.get('optionList'));

		FLOW.questionTypeControl.get('content').forEach(function(item) {
			if(item.get('value') == FLOW.selectedControl.selectedQuestion.get('type')) {
				questionType = item;
			}
		});
		this.set('type', questionType);
	},

	fillOptionList: function () {
		var optionList,optionListArray, i, sizeList;
		if (FLOW.selectedControl.get('dependentQuestion') !== null){
			FLOW.optionListControl.set('content',[]);
			optionList = FLOW.selectedControl.dependentQuestion.get('optionList');
			optionListArray = optionList.split('\n');
			sizeList = optionListArray.length;
			for (i=0 ; i< sizeList ; i++){
				FLOW.optionListControl.get('content').push(Ember.Object.create({isSelected: false, value: optionListArray[i]}));
			}
		}
	}.observes('FLOW.selectedControl.dependentQuestion'),

	doCancelEditQuestion: function() {
		FLOW.selectedControl.set('selectedQuestion', null);
	},

	doSaveEditQuestion: function() {
		FLOW.selectedControl.selectedQuestion.set('text', this.get('text'));
		FLOW.selectedControl.selectedQuestion.set('tip', this.get('tip'));
		FLOW.selectedControl.selectedQuestion.set('mandatoryFlag', this.get('mandatoryFlag'));
		FLOW.selectedControl.selectedQuestion.set('minVal', this.get('minVal'));
		FLOW.selectedControl.selectedQuestion.set('maxVal', this.get('maxVal'));
		FLOW.selectedControl.selectedQuestion.set('allowSign', this.get('allowSign'));
		FLOW.selectedControl.selectedQuestion.set('allowDecimalPoint', this.get('allowDecimalPoint'));
		FLOW.selectedControl.selectedQuestion.set('allowMultipleFlag', this.get('allowMultipleFlag'));
		FLOW.selectedControl.selectedQuestion.set('allowOtherFlag', this.get('allowOtherFlag'));
		if(this.get('dependentFlag')) {
			FLOW.selectedControl.selectedQuestion.set('dependentFlag', this.get('dependentFlag'));
		}
		FLOW.selectedControl.selectedQuestion.set('type', this.type.get('value'));
		FLOW.selectedControl.selectedQuestion.set('optionList', this.get('optionList'));
		FLOW.store.commit();
		FLOW.selectedControl.set('selectedQuestion', null);
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

		if(this.get('zeroItemQuestion')) {
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

		if(this.get('zeroItemQuestion')) {
			insertAfterOrder = 0;
		} else {
			insertAfterOrder = this.content.get('order');
		}

		// create copy of Question item in the store
		// the insertAfterOrder is inserted here
		// in the server, the proper order of all question groups is re-established
		FLOW.store.createRecord(FLOW.Question, {
			"tip": FLOW.selectedControl.selectedForCopyQuestion.get('tip'),
			"mandatoryFlag": FLOW.selectedControl.selectedForCopyQuestion.get('mandatoryFlag'),
			"allowSign": FLOW.selectedControl.selectedForCopyQuestion.get('allowSign'),
			"allowDecimalPoint": FLOW.selectedControl.selectedForCopyQuestion.get('allowDecimalPoint'),
			"allowMultipleFlag": FLOW.selectedControl.selectedForCopyQuestion.get('allowMultipleFlag'),
			"allowOtherFlag": FLOW.selectedControl.selectedForCopyQuestion.get('allowOtherFlag'),
			"dependentFlag": false,
			"maxVal": FLOW.selectedControl.selectedForCopyQuestion.get('maxVal'),
			"minVal": FLOW.selectedControl.selectedForCopyQuestion.get('minVal'),
			"type": FLOW.selectedControl.selectedForCopyQuestion.get('type'),
			"order": insertAfterOrder,
			"text": FLOW.selectedControl.selectedForCopyQuestion.get('text'),
			"optionList": FLOW.selectedControl.selectedForCopyQuestion.get('optionList'),
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
			"type": "FREE_TEXT",
			"text": "new question - please change name",
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