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
	
	// BROKEN
	//TODO when questionAnswers already exist for a question, deletion should not be possible.
	// at the moment, the deletion is fired and fails, but the order changes are also fired.
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

	// move question to selected location
    doQuestionMoveHere:function(){
		var selectedOrder, insertAfterOrder, movingUp;
		selectedOrder = FLOW.selectedControl.selectedForMoveQuestion.get('order');
		movingUp=false;

		if (this.get('zeroItem')) {insertAfterOrder=0;} else {insertAfterOrder=this.content.get('order');}

		// moving to the same place => do nothing
		if ((selectedOrder==insertAfterOrder)||(selectedOrder==(insertAfterOrder+1))){}
		else {
			// determine if the item is moving up or down
			movingUp = (selectedOrder<insertAfterOrder);
		
			FLOW.questionControl.get('content').forEach(function(item){
				var currentOrder=item.get('order');

				// item moving up
				if (movingUp) {
					// if outside of change region, do not move
					if ((currentOrder<selectedOrder) || (currentOrder>insertAfterOrder)){ }

					// move moving item to right location
					else if (currentOrder==selectedOrder) {	item.set('order',insertAfterOrder); }
					
					// move rest down
					else { item.set('order',item.get('order')-1); }
				}
				// item moving down
				else {
					if ((currentOrder<=insertAfterOrder) || (currentOrder>selectedOrder)){ }
					else if (currentOrder==selectedOrder) {	item.set('order',insertAfterOrder+1); }
					else {	item.set('order',item.get('order')+1); }
				}
			}); // end of forEach
		}
		
		FLOW.store.commit();
		FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
	},

	// TODO
	// execute question copy to selected location
	doQuestionCopyHere:function(){
		var selectedOrder, insertAfterOrder, newRec, currentOrder;
		selectedOrder = FLOW.selectedControl.selectedForCopyQuestion.get('order');
	

		if (this.get('zeroItem')) {insertAfterOrder=0;} else {insertAfterOrder=this.content.get('order');}

		// move up to make space
		FLOW.questionControl.get('content').forEach(function(item){
			var currentOrder=item.get('order');
			if (currentOrder>insertAfterOrder) {
				item.set('order',item.get('order')+1);
			}
		}); // end of forEach
	
		// create copy of QuestionGroup item in the store
		newRec = FLOW.store.createRecord(FLOW.Question,{
			"description": FLOW.selectedControl.selectedForCopyQuestion.get('description'),
			"order":insertAfterOrder+1,
			"code":FLOW.selectedControl.selectedForCopyQuestion.get('code'),
			"surveyId":FLOW.selectedControl.selectedForCopyQuestion.get('surveyId'),
			"displayName":FLOW.selectedControl.selectedForCopyQuestion.get('displayName')});
		
		FLOW.store.commit();
		FLOW.selectedControl.set('selectedForCopyQuestion', null);
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