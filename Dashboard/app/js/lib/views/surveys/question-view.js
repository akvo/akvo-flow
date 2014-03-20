FLOW.QuestionView = FLOW.View.extend({
  templateName: 'navSurveys/question-view',
  content: null,
  text: null,
  tip: null,
  type: null,
  mandatoryFlag: null,
  minVal: null,
  maxVal: null,
  allowSign: null,
  allowDecimal: null,
  allowMultipleFlag: null,
  allowOtherFlag: null,
  localeNameFlag:false,
  localeLocationFlag:false,
  geoLocked: null,
  requireDoubleEntry: null,
  dependentFlag: false,
  dependentQuestion: null,
  optionList: null,
  includeInMap: null,
  showAddAttributeDialogBool: false,
  newAttributeName: null,
  newAttributeGroup: null,
  newAttributeType: null,

  init: function () {
    var self, qoList, i;
    qoList = "";
    this._super();
    self = this;
    if (this.content && this.content.get('type') == 'OPTION') {
      options = FLOW.store.filter(FLOW.QuestionOption, function (item) {
        if (!Ember.none(self.content)) {
          return item.get('questionId') == self.content.get('keyId');
        } else {
          return false;
        }
      });
      i = 0;
      optionArray = options.toArray();
      optionArray.sort(function (a, b) {
    	  return a.get('order') - b.get('order');
      });

      optionArray.forEach(function (item) {
        if (i === 0) {
          qoList += item.get('text');
        } else {
          qoList += "\n" + item.get('text');
        }
        i++;
      });
      self.content.set('questionOptionList', qoList);
    }
  },

  amOpenQuestion: function () {
    var selected = FLOW.selectedControl.get('selectedQuestion');
    if (selected && this.get('content')) {
      var isOpen = (this.content.get('keyId') == FLOW.selectedControl.selectedQuestion.get('keyId'));
      return isOpen;
    } else {
      return false;
    }
  }.property('FLOW.selectedControl.selectedQuestion', 'content.keyId').cacheable(),


  amOptionType: function () {
    var options;
    if (this.type) {
      return this.type.get('value') == 'OPTION';
    } else {
      return false;
    }
  }.property('this.type').cacheable(),

  amNumberType: function () {
    if (this.type) {
      return this.type.get('value') == 'NUMBER';
    } else {
      return false;
    }
  }.property('this.type').cacheable(),

  amFreeTextType: function () {
	    if (this.type) {
	      return this.type.get('value') == 'FREE_TEXT';
	    } else {
	      return false;
	    }
	  }.property('this.type').cacheable(),

  amGeoType: function () {
	    var options;
	    if (this.type) {
	      return this.type.get('value') == 'GEO';
	    } else {
	      return false;
	    }
	  }.property('this.type').cacheable(),

  
  amFreetextType: function () {
	    var options;
	    if (this.type) {
	      return this.type.get('value') == 'FREE_TEXT';
	    } else {
	      return false;
	    }
	  }.property('this.type').cacheable(),

  amNumberType: function () {
    if (this.type) {
      return this.type.get('value') == 'NUMBER';
    } else {
      return false;
    }
  }.property('this.type').cacheable(),

  amNoOptionsType: function () {
    var val;
    if (!Ember.none(this.type)) {
      val = this.type.get('value');
      return val == 'PHOTO' || val == 'VIDEO' || val == 'BARCODE';
    }
  }.property('this.type').cacheable(),

  // when we change the question type to GEO, we turn on the
  // localeLocationFLag by default. If we change to something else, we
  // turn the flag of.
  enableLocaleLocation: function() {
	  this.set('localeLocationFlag', this.type.get('value') == 'GEO');
  }.observes('this.type'),
  
  
  // TODO dependencies
  // TODO options
  doQuestionEdit: function () {
    var questionType = null,
      attribute = null,
      dependentQuestion, dependentAnswer, dependentAnswerArray;
    if (this.content && (this.content.get('isDirty') || this.content.get('isSaving'))){
    	 FLOW.dialogControl.set('activeAction', 'ignore');
         FLOW.dialogControl.set('header', Ember.String.loc('_question_is_being_saved'));
         FLOW.dialogControl.set('message', Ember.String.loc('_question_is_being_saved_text'));
         FLOW.dialogControl.set('showCANCEL', false);
         FLOW.dialogControl.set('showDialog', true);
    	return;
    }
    this.init();

    FLOW.selectedControl.set('selectedQuestion', this.get('content'));
    this.set('text', FLOW.selectedControl.selectedQuestion.get('text'));
    this.set('tip', FLOW.selectedControl.selectedQuestion.get('tip'));
    this.set('mandatoryFlag', FLOW.selectedControl.selectedQuestion.get('mandatoryFlag'));
    this.set('minVal', FLOW.selectedControl.selectedQuestion.get('minVal'));
    this.set('maxVal', FLOW.selectedControl.selectedQuestion.get('maxVal'));
    this.set('allowSign', FLOW.selectedControl.selectedQuestion.get('allowSign'));
    this.set('allowDecimal', FLOW.selectedControl.selectedQuestion.get('allowDecimal'));
    this.set('allowMultipleFlag', FLOW.selectedControl.selectedQuestion.get('allowMultipleFlag'));
    this.set('allowOtherFlag', FLOW.selectedControl.selectedQuestion.get('allowOtherFlag'));
    this.set('localeNameFlag', FLOW.selectedControl.selectedQuestion.get('localeNameFlag'));
    this.set('localeLocationFlag', FLOW.selectedControl.selectedQuestion.get('localeLocationFlag'));
    this.set('geoLocked', FLOW.selectedControl.selectedQuestion.get('geoLocked'));
    this.set('requireDoubleEntry', FLOW.selectedControl.selectedQuestion.get('requireDoubleEntry'));
    this.set('includeInMap', FLOW.selectedControl.selectedQuestion.get('includeInMap'));
    this.set('dependentFlag', FLOW.selectedControl.selectedQuestion.get('dependentFlag'));
    this.set('optionList', FLOW.selectedControl.selectedQuestion.get('questionOptionList'));
    FLOW.optionListControl.set('content', []);

    // if the dependentQuestionId is not null, get the question
    if (!Ember.empty(FLOW.selectedControl.selectedQuestion.get('dependentQuestionId'))) {
      dependentQuestion = FLOW.store.find(FLOW.Question, FLOW.selectedControl.selectedQuestion.get('dependentQuestionId'));
      dependentAnswer = FLOW.selectedControl.selectedQuestion.get('dependentQuestionAnswer');

      // if we have found the question, fill the options
      if (dependentQuestion.get('id') !== "0") {
        FLOW.selectedControl.set('dependentQuestion', dependentQuestion);
        this.fillOptionList();

        dependentAnswerArray = dependentAnswer.split('|');
        // find the answer already set and set it to true in the optionlist
        FLOW.optionListControl.get('content').forEach(function (item) {
          if (dependentAnswerArray.indexOf(item.get('value')) > -1) {
            item.set('isSelected', true);
          }
        });
      }
    }

    // set the attribute to the original choice
    FLOW.attributeControl.get('content').forEach(function (item) {
      if (item.get('keyId') == FLOW.selectedControl.selectedQuestion.get('metricId')) {
        attribute = item;
      }
    });
    this.set('attribute', attribute);

    // set the type to the original choice
    FLOW.questionTypeControl.get('content').forEach(function (item) {
      if (item.get('value') == FLOW.selectedControl.selectedQuestion.get('type')) {
        questionType = item;
      }
    });
    this.set('type', questionType);
  },

  fillOptionList: function () {
    var optionList, optionListArray, i, sizeList;
    if (FLOW.selectedControl.get('dependentQuestion') !== null) {
      FLOW.optionListControl.set('content', []);
      FLOW.optionListControl.set('currentActive', null);

      options = FLOW.store.filter(FLOW.QuestionOption, function (item) {
        if (!Ember.none(FLOW.selectedControl.selectedQuestion)) {
          return item.get('questionId') == FLOW.selectedControl.dependentQuestion.get('keyId');
        } else {
          return false;
        }
      });

      optionArray = options.toArray();
      optionArray.sort(function (a, b) {
    	  return a.get('order') - b.get('order');
      });

      optionArray.forEach(function (item) {
        FLOW.optionListControl.get('content').push(Ember.Object.create({
          isSelected: false,
          value: item.get('text')
        }));
      });
    }
  }.observes('FLOW.selectedControl.dependentQuestion'),

  doCancelEditQuestion: function () {
    FLOW.selectedControl.set('selectedQuestion', null);
  },

  doSaveEditQuestion: function () {
    var path, anyActive, first, dependentQuestionAnswer, minVal, maxVal, options, found, optionsToDelete;

    // validation
    if (this.type.get('value') == 'NUMBER') {
      if (!Ember.empty(this.get('minVal')) && !Ember.empty(this.get('maxVal'))) {

        if (isNaN(this.get('minVal')) || isNaN(this.get('maxVal'))) {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_min_max_not_number'));
          FLOW.dialogControl.set('message', Ember.String.loc('_min_max_not_number_message'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
          return;
        }

        if (parseFloat(this.get('minVal')) >= parseFloat(this.get('maxVal'))) {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_min_max_not_correct'));
          FLOW.dialogControl.set('message', Ember.String.loc('_min_larger_than_max_or_equal'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
          return;
        }
      }
    }
    if (this.type.get('value') !== 'NUMBER') {
      this.set('minVal', null);
      this.set('maxVal', null);
      this.set('allowSign', false);
      this.set('allowDecimal', false);
    }
    if (this.type.get('value') !== 'GEO') {
        this.set('geoLocked', false);
    }

    if (!(this.type.get('value') == 'NUMBER' || this.type.get('value') == 'FREE_TEXT')) {
        this.set('requireDoubleEntry', false);
    }
    path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name') + "/" + FLOW.selectedControl.selectedQuestionGroup.get('code');
    FLOW.selectedControl.selectedQuestion.set('text', this.get('text'));
    FLOW.selectedControl.selectedQuestion.set('tip', this.get('tip'));
    FLOW.selectedControl.selectedQuestion.set('mandatoryFlag', this.get('mandatoryFlag'));

    minVal = (Ember.empty(this.get('minVal'))) ? null : this.get('minVal');
    maxVal = (Ember.empty(this.get('maxVal'))) ? null : this.get('maxVal');
    FLOW.selectedControl.selectedQuestion.set('minVal', minVal);
    FLOW.selectedControl.selectedQuestion.set('maxVal', maxVal);

    FLOW.selectedControl.selectedQuestion.set('path', path);
    FLOW.selectedControl.selectedQuestion.set('allowSign', this.get('allowSign'));
    FLOW.selectedControl.selectedQuestion.set('allowDecimal', this.get('allowDecimal'));
    FLOW.selectedControl.selectedQuestion.set('allowMultipleFlag', this.get('allowMultipleFlag'));
    FLOW.selectedControl.selectedQuestion.set('allowOtherFlag', this.get('allowOtherFlag'));
    FLOW.selectedControl.selectedQuestion.set('localeNameFlag', this.get('localeNameFlag'));
    FLOW.selectedControl.selectedQuestion.set('localeLocationFlag', this.get('localeLocationFlag'));
    FLOW.selectedControl.selectedQuestion.set('geoLocked', this.get('geoLocked'));
    FLOW.selectedControl.selectedQuestion.set('requireDoubleEntry', this.get('requireDoubleEntry'));
    FLOW.selectedControl.selectedQuestion.set('includeInMap', this.get('includeInMap'));

    dependentQuestionAnswer = "";
    first = true;

    FLOW.optionListControl.get('content').forEach(function (item) {
      if (item.isSelected) {
        if (!first) {
          dependentQuestionAnswer += "|";
        }
        first = false;
        dependentQuestionAnswer += item.value;
      }
    });

    if (this.get('dependentFlag') && dependentQuestionAnswer !== "") {
      FLOW.selectedControl.selectedQuestion.set('dependentFlag', this.get('dependentFlag'));
      FLOW.selectedControl.selectedQuestion.set('dependentQuestionId', FLOW.selectedControl.dependentQuestion.get('keyId'));
      FLOW.selectedControl.selectedQuestion.set('dependentQuestionAnswer', dependentQuestionAnswer);
    } else {
      FLOW.selectedControl.selectedQuestion.set('dependentFlag', false);
      FLOW.selectedControl.selectedQuestion.set('dependentQuestionId', null);
      FLOW.selectedControl.selectedQuestion.set('dependentQuestionAnswer', null);
    }

    if (this.get('attribute')) {
      FLOW.selectedControl.selectedQuestion.set('metricId', this.attribute.get('keyId'));
    }

    if (this.get('type')) {
      FLOW.selectedControl.selectedQuestion.set('type', this.type.get('value'));
    }


    // deal with saving options
    // the questionOptionList field is created in the init method, and contains the list of options as a string
    // if the list of options is not equal to the edited list, we need to save it
    if (FLOW.selectedControl.selectedQuestion.get('questionOptionList') != this.get('optionList')) {
      options = FLOW.store.filter(FLOW.QuestionOption, function (item) {
        if (!Ember.none(FLOW.selectedControl.selectedQuestion)) {
          return item.get('questionId') == FLOW.selectedControl.selectedQuestion.get('keyId');
        } else {
          return false;
        }
      });
      newOptionStringArray = this.get('optionList').split('\n');
      optionsToDelete = [];

      options.forEach(function (item) {
        optionsToDelete.push(item.get('keyId'));
      });

      order = 1;
      newOptionStringArray.forEach(function (item) {
        found = false;
        // skip empty lines
        if (!Ember.empty(item)) {
          // if there is an existing option with this value, use it and change order if neccessary
          options.forEach(function (optionItem) {
            if (item == optionItem.get('text')) {
              found = true;
              // adapt order if necessary
              if (optionItem.get('order') != order) {
                optionItem.set('order', order);
              }
              // don't delete this one
              optionsToDelete.splice(optionsToDelete.indexOf(optionItem.get('keyId')), 1);
            }
          });
          if (!found) {
            // create new one
            FLOW.store.createRecord(FLOW.QuestionOption, {
              text: item,
              questionId: FLOW.selectedControl.selectedQuestion.get('keyId'),
              order: order
            });
          }
          order++;
        }
      });

      // delete unused questionOptions
      for (var ii = 0; ii < optionsToDelete.length; ii++) {
        opToDel = FLOW.store.find(FLOW.QuestionOption, optionsToDelete[ii]);
        opToDel.deleteRecord();
      }
      FLOW.selectedControl.selectedQuestion.set('questionOptionList', this.get('optionList'));
    }

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedQuestion', null);
    FLOW.selectedControl.set('dependentQuestion', null);
  },

  deleteQuestion: function () {
    var qDeleteId;
    qDeleteId = this.content.get('keyId');

    // check if anything is being saved at the moment
    if (this.checkQuestionsBeingSaved()) {
   	 FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_please_wait'));
        FLOW.dialogControl.set('message', Ember.String.loc('_please_wait_until_previous_request'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
   	 	return;
    } 
   
    // check if deleting this question is allowed
    // if successful, the deletion action will be called from DS.FLOWrestadaptor.sideload
    FLOW.store.findQuery(FLOW.Question, {
      preflight: 'delete',
      questionId: qDeleteId
    });
  },

  checkQuestionsBeingSaved: function () {
	var question;
	question = FLOW.store.filter(FLOW.Question, function(item){
		return item.get('isSaving');
	});
	return question.content.length > 0;
  },
  
  // move question to selected location
  doQuestionMoveHere: function () {
    var selectedOrder, insertAfterOrder, selectedQ, useMoveQuestion;
    selectedOrder = FLOW.selectedControl.selectedForMoveQuestion.get('order');

    if (this.get('zeroItemQuestion')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }
    
    // check if anything is being saved at the moment
     if (this.checkQuestionsBeingSaved()) {
    	 FLOW.dialogControl.set('activeAction', 'ignore');
         FLOW.dialogControl.set('header', Ember.String.loc('_please_wait'));
         FLOW.dialogControl.set('message', Ember.String.loc('_please_wait_until_previous_request'));
         FLOW.dialogControl.set('showCANCEL', false);
         FLOW.dialogControl.set('showDialog', true);
    	 return;
     }
    
    // check to see if we are trying to move the question to another question group
    if (FLOW.selectedControl.selectedForMoveQuestion.get('questionGroupId') != FLOW.selectedControl.selectedQuestionGroup.get('keyId')) {
      selectedQ = FLOW.store.find(FLOW.Question, FLOW.selectedControl.selectedForMoveQuestion.get('keyId'));
      if (selectedQ !== null) {

        // restore order
        qgIdSource = FLOW.selectedControl.selectedForMoveQuestion.get('questionGroupId');
        qgIdDest = FLOW.selectedControl.selectedQuestionGroup.get('keyId');

        questionsInSourceGroup = FLOW.store.filter(FLOW.Question, function (item) {
          return item.get('questionGroupId') == qgIdSource;
        });

        questionsInDestGroup = FLOW.store.filter(FLOW.Question, function (item) {
          return item.get('questionGroupId') == qgIdDest;
        });

        // restore order in source group, where the question dissapears 
        questionsInSourceGroup.forEach(function (item) {
          if (item.get('order') > selectedOrder) {
            item.set('order', item.get('order') - 1);
          }
        });

        // make room in destination group
        questionsInDestGroup.forEach(function (item) {
          if (item.get('order') > insertAfterOrder) {
            item.set('order', item.get('order') + 1);
          }
        });

        // move question
        selectedQ.set('order', insertAfterOrder + 1);
        selectedQ.set('questionGroupId', qgIdDest);

        // recompute questions in groups so we can correct any order problems
        questionsInSourceGroup = FLOW.store.filter(FLOW.Question, function (item) {
          return item.get('questionGroupId') == qgIdSource;
        });

        questionsInDestGroup = FLOW.store.filter(FLOW.Question, function (item) {
          return item.get('questionGroupId') == qgIdDest;
        });

        FLOW.questionControl.restoreOrder(questionsInSourceGroup);
        FLOW.questionControl.restoreOrder(questionsInDestGroup);
      }
    // if we are not moving to another group, we must be moving inside a group
    // only do something if we are not moving to the same place
    } else if (!((selectedOrder == insertAfterOrder) || (selectedOrder == (insertAfterOrder + 1)))) {
      selectedQ = FLOW.store.find(FLOW.Question, FLOW.selectedControl.selectedForMoveQuestion.get('keyId'));
      if (selectedQ !== null) {
        // restore order
        qgId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
        questionsInGroup = FLOW.store.filter(FLOW.Question, function (item) {
          return item.get('questionGroupId') == qgId;
        });

        origOrder = FLOW.selectedControl.selectedForMoveQuestion.get('order');
        movingUp = origOrder < insertAfterOrder;

        questionsInGroup.forEach(function (item) {
          currentOrder = item.get('order');
          if (movingUp) {
            if (currentOrder == origOrder) {
              // move moving item to right location
              selectedQ.set('order', insertAfterOrder);
            } else if ((currentOrder > origOrder) && (currentOrder <= insertAfterOrder)) {
              // move item down
              item.set('order', item.get('order') - 1);
            }
          } else {
            // Moving down
            if (currentOrder == origOrder) {
              // move moving item to right location
              selectedQ.set('order', insertAfterOrder + 1);
            } else if ((currentOrder < origOrder) && (currentOrder > insertAfterOrder)) {
              // move item up
              item.set('order', item.get('order') + 1);
            }
          }
        });

        questionsInGroup = FLOW.store.filter(FLOW.Question, function (item) {
        	return item.get('questionGroupId') == qgId;
       	});

        // restore order in case the order has gone haywire
        FLOW.questionControl.restoreOrder(questionsInGroup);
      	}
    }
    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedForMoveQuestion', null);
  },

  // execute question copy to selected location
  doQuestionCopyHere: function () {
    var insertAfterOrder, path, qgId, questionsInGroup, question;
    //path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name') + "/" + FLOW.selectedControl.selectedQuestionGroup.get('code');

    if (this.get('zeroItemQuestion')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }

    // check if anything is being saved at the moment
    if (this.checkQuestionsBeingSaved()) {
   	 FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_please_wait'));
        FLOW.dialogControl.set('message', Ember.String.loc('_please_wait_until_previous_request'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
   	 	return;
    }

    // restore order
    qgId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
    questionsInGroup = FLOW.store.filter(FLOW.Question, function (item) {
      return item.get('questionGroupId') == qgId;
    });
    // move items up to make space
    questionsInGroup.forEach(function (item) {
      if (item.get('order') > insertAfterOrder) {
        item.set('order', item.get('order') + 1);
      }
    });

    question = FLOW.selectedControl.get('selectedForCopyQuestion');
    // create copy of Question item in the store
    FLOW.store.createRecord(FLOW.Question, {
      "order": insertAfterOrder + 1,
      "surveyId": question.get('surveyId'),
      "questionGroupId": qgId,
      "sourceId":question.get('keyId')
    });

    questionsInGroup = FLOW.store.filter(FLOW.Question, function (item) {
      return item.get('questionGroupId') == qgId;
    });

    // restore order in case the order has gone haywire
    FLOW.questionControl.restoreOrder(questionsInGroup);
    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
    
    FLOW.selectedControl.set('selectedForCopyQuestion', null);
  },

  // create new question
  doInsertQuestion: function () {
    var insertAfterOrder, path, qgId, questionsInGroup;
    path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name') + "/" + FLOW.selectedControl.selectedQuestionGroup.get('code');

    if (this.get('zeroItemQuestion')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }

    // check if anything is being saved at the moment
    if (this.checkQuestionsBeingSaved()) {
   	 FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_please_wait'));
        FLOW.dialogControl.set('message', Ember.String.loc('_please_wait_until_previous_request'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
   	 	return;
    } 
    
    
    // restore order
    qgId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
    questionsInGroup = FLOW.store.filter(FLOW.Question, function (item) {
      return item.get('questionGroupId') == qgId;
    });

    // move items up to make space
    questionsInGroup.forEach(function (item) {
      if (item.get('order') > insertAfterOrder) {
        item.set('order', item.get('order') + 1);
      }
    });

    // create new Question item in the store
    FLOW.store.createRecord(FLOW.Question, {
      "order": insertAfterOrder + 1,
      "type": "FREE_TEXT",
      "path": path,
      "text": Ember.String.loc('_new_question_please_change_name'),
      "surveyId": FLOW.selectedControl.selectedSurvey.get('keyId'),
      "questionGroupId": FLOW.selectedControl.selectedQuestionGroup.get('keyId')
    });

    questionsInGroup = FLOW.store.filter(FLOW.Question, function (item) {
      return item.get('questionGroupId') == qgId;
    });
    // restore order in case the order has gone haywire
    FLOW.questionControl.restoreOrder(questionsInGroup);
    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
  },

  // true if one question has been selected for Move
  oneSelectedForMove: function () {
    var selectedForMove = FLOW.selectedControl.get('selectedForMoveQuestion');
    if (selectedForMove) {
      return true;
    } else {
      return false;
    }
  }.property('FLOW.selectedControl.selectedForMoveQuestion'),

  // true if one question has been selected for Copy
  oneSelectedForCopy: function () {
    var selectedForCopy = FLOW.selectedControl.get('selectedForCopyQuestion');
    if (selectedForCopy) {
      return true;
    } else {
      return false;
    }
  }.property('FLOW.selectedControl.selectedForCopyQuestion'),

  // prepare for question copy. Shows 'copy to here' buttons
  doQuestionCopy: function () {
    FLOW.selectedControl.set('selectedForCopyQuestion', this.get('content'));
    FLOW.selectedControl.set('selectedForMoveQuestion', null);
  },

  // cancel question copy
  doQuestionCopyCancel: function () {
    FLOW.selectedControl.set('selectedForCopyQuestion', null);
  },


  // prepare for question move. Shows 'move here' buttons
  doQuestionMove: function () {
    FLOW.selectedControl.set('selectedForMoveQuestion', this.get('content'));
    FLOW.selectedControl.set('selectedForCopyQuestion', null);
  },

  // cancel group move
  doQuestionMoveCancel: function () {
    FLOW.selectedControl.set('selectedForMoveQuestion', null);
  },
  showAddAttributeDialog: function () {
    this.set('showAddAttributeDialogBool', true);
  },

  doAddAttribute: function () {
    if ((this.get('newAttributeName') !== null) && (this.get('newAttributeType') !== null)) {
      FLOW.store.createRecord(FLOW.Metric, {
        "name": this.get('newAttributeName'),
        "group": this.get('newAttributeGroup'),
        "valueType": this.newAttributeType.get('value')
      });
      FLOW.store.commit();
    }
    this.set('showAddAttributeDialogBool', false);
  },

  cancelAddAttribute: function () {
    this.set('showAddAttributeDialogBool', false);
  }
});
