FLOW.QuestionView = Ember.View.extend({
	templateName:'navSurveys/question-view',
	content:null,
	questionName:null,
	checkedMandatory: false,
	checkedDependent: false,
	checkedOptionMultiple:false,
	checkedOptionOther:false,
	selectedQuestionType:null,
	selectedOptionEdit:null,
	oneSelectedForMove:false,
	oneSelectedForCopy:false,
	zeroItem:false,
	
	amOpenQuestion: function() {
		var selected = FLOW.selectedControl.get('selectedQuestion');

		if (selected && this.get('content')) {
			var isOpen = (this.content.get('keyId') == FLOW.selectedControl.selectedQuestion.get('keyId'));
			return isOpen;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedQuestion', 'content.keyId').cacheable(),

	
	amOptionType:function() {
		if (this.selectedQuestionType){ return (this.selectedQuestionType.get('value')=='option') ? true : false;}
		else {return false;}
	}.property('this.selectedQuestionType').cacheable(),
	
	amNumberType:function() {
		if (this.selectedQuestionType){ return (this.selectedQuestionType.get('value')=='number') ? true : false;}
		else {return false;}
	}.property('this.selectedQuestionType').cacheable(),
		
	doEdit: function() {
		FLOW.selectedControl.set('selectedQuestion', this.get('content'));
		this.set('questionName',FLOW.selectedControl.selectedQuestion.get('displayName'));
		
		//FLOW.optionControl.set('editCopy',FLOW.optionControl.get('questionOptionsList'));
	
		//TODO populate selected question type
		//TODO populate tooltip
		//TODO populate question options
		//TODO populate help
		//TODO populate translations
	},
	
	doCancelEditQuestion: function() {
		FLOW.selectedControl.set('selectedQuestion', null);
		console.log('canceling edit');
	},
	
	doSaveEditQuestion: function() {
	},
	
	// true if one question has been selected for Move
	oneSelectedForMove: function() {
		var selectedForMove = FLOW.selectedControl.get('selectedForMoveQuestion');
		if (selectedForMove) {
			return true;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedForMoveQuestion'),

	// true if one question has been selected for Copy
	oneSelectedForCopy: function() {
		var selectedForCopy = FLOW.selectedControl.get('selectedForCopyQuestion');
		if (selectedForCopy) {
			return true;
		} else {
			return false;
		}
	}.property('FLOW.selectedControl.selectedForCopyQuestion'),
	
    // prepare for group copy. Shows 'copy to here' buttons
	doQuestionCopy:function(){
		FLOW.selectedControl.set('selectedForCopyQuestion', this.content);
		FLOW.selectedControl.set('selectedForMoveQuestion', null);
	},


	// cancel group copy
	doQuestionCopyCancel:function(){
		FLOW.selectedControl.set('selectedForCopyQuestion', null);
    },


	// prepare for group move. Shows 'move here' buttons
	doQuestionMove:function(){
		FLOW.selectedControl.set('selectedForMoveQuestion', this.content);
		FLOW.selectedControl.set('selectedForCopyQuestion', null);
	},

	// cancel group move
	doQuestionMoveCancel:function(){
		FLOW.selectedControl.set('selectedForMoveQuestion', null);
	},



	doDelete: function() {
			console.log("doing doDelete");
	}
});