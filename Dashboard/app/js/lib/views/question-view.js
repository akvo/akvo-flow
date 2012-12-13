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
		
	doQuestionEdit: function() {
		FLOW.selectedControl.set('selectedQuestion', this.get('content'));
		this.set('questionName',FLOW.selectedControl.selectedQuestion.get('displayName'));
	},
	
	doCancelEditQuestion: function() {
		FLOW.selectedControl.set('selectedQuestion', null);
		console.log('canceling edit');
	},
	
	doSaveEditQuestion: function() {
		console.log('TODO save edit');
	},
	
	//WRONG
	deleteQuestion: function() {
		var qDeleteOrder, qDeleteId, question, questionGroupId;
		qDeleteOrder = this.content.get('order');
		qDeleteId = this.content.get('keyId');

		questionGroupId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
		
		// move items down
		FLOW.store.filter(FLOW.Question,function(data){
			return (data.get('questionGroupId') == questionGroupId);
		}).forEach(function(item){
			var currentOrder=item.get('order');
			
			if (currentOrder>qDeleteOrder){
				item.set('order',item.get('order')-1);
			}
		});
	
		question = FLOW.store.find(FLOW.Question, qDeleteId);
		question.deleteRecord();
		FLOW.store.commit();
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
	
    // prepare for question copy. Shows 'copy to here' buttons
	doQuestionCopy:function(){
		FLOW.selectedControl.set('selectedForCopyQuestion', this.get('content'));
		FLOW.selectedControl.set('selectedForMoveQuestion', null);
	},

	// cancel question copy
	doQuestionCopyCancel:function(){
		FLOW.selectedControl.set('selectedForCopyQuestion', null);
    },


	// prepare for question move. Shows 'move here' buttons
	doQuestionMove:function(){
		FLOW.selectedControl.set('selectedForMoveQuestion', this.get('content'));
		FLOW.selectedControl.set('selectedForCopyQuestion', null);
	},

	// cancel group move
	doQuestionMoveCancel:function(){
		FLOW.selectedControl.set('selectedForMoveQuestion', null);
	}
});


//doQuestionDelete
//doQuestionMove
//doQuestionCopy
//doQuestionEdit
//doInsertQuestion