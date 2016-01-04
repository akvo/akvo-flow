function sortByOrder(a , b) {
  return a.get('order') - b.get('order');
}

FLOW.QuestionView = FLOW.View.extend({
  templateName: 'navSurveys/question-view',
  content: null,
  questionId: null,
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
  allowExternalSources: false,
  localeNameFlag:false,
  localeLocationFlag:false,
  geoLocked: null,
  requireDoubleEntry: null,
  dependentFlag: false,
  dependentQuestion: null,
  includeInMap: null,
  showAddAttributeDialogBool: false,
  newAttributeName: null,
  newAttributeGroup: null,
  newAttributeType: null,
  allowPoints: true,
  allowLine: true,
  allowPolygon: true,
  questionValidationFailure: false,
  questionTooltipValidationFailure: false,

  showMetaConfig: function () {
    return FLOW.Env.showMonitoringFeature;
  }.property('FLOW.Env.showMonitoringFeature'),

  amOpenQuestion: function () {
    var selected = FLOW.selectedControl.get('selectedQuestion');
    if (selected && this.get('content')) {
      var isOpen = (this.content.get('keyId') == FLOW.selectedControl.selectedQuestion.get('keyId'));
      return isOpen;
    } else {
      return false;
    }
  }.property('FLOW.selectedControl.selectedQuestion', 'content.keyId').cacheable(),

  amTextType: function () {
    if (this.type) {
      return this.type.get('value') == 'FREE_TEXT';
    } else {
      return false;
    }
  }.property('this.type').cacheable(),

  amOptionType: function () {
    return (this.content && this.content.get('type') === 'OPTION')
            || (this.type && this.type.get('value') === "OPTION");
  }.property('this.type'),

  amNumberType: function () {
    if (this.type) {
      return this.type.get('value') == 'NUMBER';
    } else {
      return false;
    }
  }.property('this.type').cacheable(),

  amBarcodeType: function () {
      if (this.type) {
          return this.type.get('value') === 'SCAN';
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
    if (this.type) {
      return this.type.get('value') == 'GEO';
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

  amCascadeType: function () {
	    if (this.type) {
	      return this.type.get('value') == 'CASCADE';
	    } else {
	      return false;
	    }
	  }.property('this.type').cacheable(),

  amNoOptionsType: function () {
    var val;
    if (!Ember.none(this.type)) {
      val = this.type.get('value');
      return val === 'PHOTO' || val === 'VIDEO' || val === 'DATE' || val === 'SIGNATURE';
    }
  }.property('this.type').cacheable(),

  amGeoshapeType: function () {
	    if (this.type) {
	      return this.type.get('value') == 'GEOSHAPE';
	    } else {
	      return false;
	    }
	  }.property('this.type').cacheable(),

  amDateType: function () {
      if (this.type) {
        return this.type.get('value') == 'DATE';
      } else {
        return false;
      }
    }.property('this.type').cacheable(),    

  amSignatureType: function () {
    return (this.content && this.content.get('type') === 'SIGNATURE')
            || (this.type && this.type.get('value') === 'SIGNATURE');
  }.property('this.type'),

  showLocaleName: function () {
      if (!this.type) {
          return false;
      }
      return this.type.get('value') == 'FREE_TEXT'
          || this.type.get('value') == 'NUMBER'
          || this.type.get('value') == 'OPTION'
          || this.type.get('value') == 'CASCADE';
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
    dependentQuestion, dependentAnswer, dependentAnswerArray,cascadeResource;
    if (this.content && (this.content.get('isDirty') || this.content.get('isSaving'))) {
      this.showMessageDialog(Ember.String.loc('_question_is_being_saved'),
			     Ember.String.loc('_question_is_being_saved_text'));
      return;
    }

    this.loadQuestionOptions();

    FLOW.selectedControl.set('selectedQuestion', this.get('content'));
    this.set('questionId', FLOW.selectedControl.selectedQuestion.get('questionId'));
    this.set('text', FLOW.selectedControl.selectedQuestion.get('text'));
    this.set('tip', FLOW.selectedControl.selectedQuestion.get('tip'));
    this.set('mandatoryFlag', FLOW.selectedControl.selectedQuestion.get('mandatoryFlag'));
    this.set('minVal', FLOW.selectedControl.selectedQuestion.get('minVal'));
    this.set('maxVal', FLOW.selectedControl.selectedQuestion.get('maxVal'));
    this.set('allowSign', FLOW.selectedControl.selectedQuestion.get('allowSign'));
    this.set('allowDecimal', FLOW.selectedControl.selectedQuestion.get('allowDecimal'));
    this.set('allowMultipleFlag', FLOW.selectedControl.selectedQuestion.get('allowMultipleFlag'));
    this.set('allowOtherFlag', FLOW.selectedControl.selectedQuestion.get('allowOtherFlag'));
    this.set('allowExternalSources', FLOW.selectedControl.selectedQuestion.get('allowExternalSources'));
    this.set('localeNameFlag', FLOW.selectedControl.selectedQuestion.get('localeNameFlag'));
    this.set('localeLocationFlag', FLOW.selectedControl.selectedQuestion.get('localeLocationFlag'));
    this.set('geoLocked', FLOW.selectedControl.selectedQuestion.get('geoLocked'));
    this.set('requireDoubleEntry', FLOW.selectedControl.selectedQuestion.get('requireDoubleEntry'));
    this.set('includeInMap', FLOW.selectedControl.selectedQuestion.get('includeInMap'));
    this.set('dependentFlag', FLOW.selectedControl.selectedQuestion.get('dependentFlag'));
    this.set('allowPoints', FLOW.selectedControl.selectedQuestion.get('allowPoints'));
    this.set('allowLine', FLOW.selectedControl.selectedQuestion.get('allowLine'));
    this.set('allowPolygon', FLOW.selectedControl.selectedQuestion.get('allowPolygon'));
    FLOW.optionListControl.set('content', []);

    // if the cascadeResourceId is not null, get the resource
    if (!Ember.empty(FLOW.selectedControl.selectedQuestion.get('cascadeResourceId'))) {
    	cascadeResource = FLOW.store.find(FLOW.CascadeResource,FLOW.selectedControl.selectedQuestion.get('cascadeResourceId'));
    	FLOW.selectedControl.set('selectedCascadeResource', cascadeResource);
    }

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

  /*
   *  Load the question options for question editing
   */
  loadQuestionOptions: function () {
    var c = this.content;
    FLOW.questionOptionsControl.set('content', []);
    FLOW.questionOptionsControl.set('questionId', null);

    options = FLOW.store.filter(FLOW.QuestionOption, function (optionItem) {
        return optionItem.get('questionId') === c.get('keyId');
    });

    if (options.get('length')) {
      optionArray = Ember.A(options.toArray().sort(sortByOrder));
      FLOW.questionOptionsControl.set('content', optionArray);
      FLOW.questionOptionsControl.set('questionId', c.get('keyId'));
    } else {
      FLOW.questionOptionsControl.loadDefaultOptions();
    }
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


  doSaveEditQuestion: function() {
    var path, anyActive, first, dependentQuestionAnswer, minVal, maxVal, options, found, optionsToDelete;

    if (this.questionIdValidationFailure) {
      this.showMessageDialog(Ember.String.loc('_question_id_must_be_valid_and_unique'), this.questionIdValidationFailureReason);
      return;
    }

    if (this.questionValidationFailure) {
        this.showMessageDialog(Ember.String.loc('_question_over_500_chars_header'), Ember.String.loc('_question_over_500_chars_text'));
        return;
      }

    if (this.questionTooltipValidationFailure) {
        this.showMessageDialog(Ember.String.loc('_tooltip_over_500_chars_header'), Ember.String.loc('_tooltip_over_500_chars_text'));
        return;
      }

    if (this.get('amOptionType')) {
      var invalidOptions = FLOW.questionOptionsControl.validateOptions();
      if (invalidOptions) {
        this.showMessageDialog(Ember.String.loc('_invalid_options_header'), invalidOptions);
        return;
      }

      // save options to the datastore
      FLOW.questionOptionsControl.persistOptions();
    }

    if (this.type.get('value') === 'CASCADE' && Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'))) {
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_cascade_resources'));
        FLOW.dialogControl.set('message', Ember.String.loc('_cascade_select_resource'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
        return false;
    }

    if (this.type.get('value') !== 'NUMBER') {
      this.set('minVal', null);
      this.set('maxVal', null);
      this.set('allowSign', false);
      this.set('allowDecimal', false);
    }
    if (this.type.get('value') !== 'GEO' && this.type.get('value') !== 'GEOSHAPE') {
      this.set('geoLocked', false);
    }

    if (!(this.type.get('value') == 'NUMBER' || this.type.get('value') == 'FREE_TEXT')) {
      this.set('requireDoubleEntry', false);
    }
    path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name') + "/" + FLOW.selectedControl.selectedQuestionGroup.get('code');
    FLOW.selectedControl.selectedQuestion.set('questionId', this.get('questionId'));
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
    FLOW.selectedControl.selectedQuestion.set('allowPoints', this.get('allowPoints'));
    FLOW.selectedControl.selectedQuestion.set('allowLine', this.get('allowLine'));
    FLOW.selectedControl.selectedQuestion.set('allowPolygon', this.get('allowPolygon'));

    var allowExternalSources = (this.type.get('value') !== 'FREE_TEXT') ? false : this.get('allowExternalSources');
    FLOW.selectedControl.selectedQuestion.set('allowExternalSources', allowExternalSources);

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

    // deal with cascadeResource
    if (this.type.get('value') == 'CASCADE') {
    	if (!Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'))){
    		FLOW.selectedControl.selectedQuestion.set('cascadeResourceId',
    				FLOW.selectedControl.selectedCascadeResource.get('keyId'));
    	}
    }

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedQuestion', null);
    FLOW.selectedControl.set('dependentQuestion', null);
    FLOW.selectedControl.set('selectedCascadeResource',null);
  },

  isPartOfMonitoringGroup: function(questionKeyId) {
    var surveyId = FLOW.store.findById(FLOW.Question, questionKeyId).get('surveyId');
    var surveyGroupId = FLOW.store.findById(FLOW.Survey, surveyId).get('surveyGroupId');
    return FLOW.store.findById(FLOW.SurveyGroup, surveyGroupId).get('monitoringGroup');
  },

  /**
   * QuestionId validation
   *
   * A valid questionId must match /^[A-Za-z0-9_\-]*$/. Uniqueness
   * constraints depends on wether the question is part of a
   * monitoring group or not. If the question is part of a
   * monitoring group, uniqueness validation _must_ happen on the
   * server and cover all questions which are part of that group. If
   * not, the uniqueness constraint only covers the survey and can
   * be checked on the client.
   */
  throttleTimer: null,

  validateQuestionId: function(args) {
    var selectedQuestion = FLOW.selectedControl.selectedQuestion
    var questionKeyId = selectedQuestion.get('keyId');
    var questionId = this.get('questionId') || "";
    if (FLOW.Env.mandatoryQuestionID && questionId.match(/^\s*$/)) {
      args.failure(Ember.String.loc('_question_id_mandatory'));
    } else if (!questionId.match(/^[A-Za-z0-9_\-]*$/)) {
      args.failure(Ember.String.loc('_question_id_only_alphanumeric'))
    } else {
      var monitoring = this.isPartOfMonitoringGroup(questionKeyId);
      if (monitoring) {
        clearTimeout(this.throttleTimer);
        this.throttleTimer = setTimeout(function () {
          $.ajax({
            url: '/rest/questions/' + questionKeyId + '/validate?questionId=' + questionId,
            type: 'POST',
            success: function(data) {
              if (data.success) {
                args.success();
              } else {
                args.failure(data.reason);
              }
            },
            error: function() {
              args.failure(Ember.String.loc('_could_not_validate_question_id_with_server'));
            }
          });
        }, 1000);
      } else {
        var otherQuestionIds = FLOW.store.filter(FLOW.Question, function(question) {
          var keyId = question.get('keyId');
          return (selectedQuestion.get('surveyId') === question.get('surveyId'))
            && (questionKeyId !== question.get('keyId'));
        }).map(function(question) {
          return question.get('questionId');
        }).filter(function(questionId) {
          return questionId !== "";
        });
        var isUnique = !otherQuestionIds.contains(questionId);
        if (isUnique) {
          args.success();
        } else {
          args.failure('the question id is not unique');
        }
      }
    }
  },

  validateMinAndMax: function(args) {
    if (this.type.get('value') == 'NUMBER') {
      if (!Ember.empty(this.get('minVal')) && !Ember.empty(this.get('maxVal'))) {
        if (isNaN(this.get('minVal')) || isNaN(this.get('maxVal'))) {
	  args.NaNFailure();
	  return;
        } else if (parseFloat(this.get('minVal')) >= parseFloat(this.get('maxVal'))) {
          args.valueFailure();
	  return;
        }
      }
    }
    args.success();
  },

  showMessageDialog: function(header, message) {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', header);
    FLOW.dialogControl.set('message', message);
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  deleteQuestion: function () {
    var qDeleteId;
    qDeleteId = this.content.get('keyId');

    // check if anything is being saved at the moment
    if (this.checkQuestionsBeingSaved()) {
      this.showMessageDialog(Ember.String.loc('_please_wait'),
			     Ember.String.loc('_please_wait_until_previous_request'));
      return;
    }

    // Check if there is another question that is dependant on this question
    if (this.content.get('type') === 'OPTION') {
      var hasDependant = FLOW.store.find(FLOW.Question).some(function (q) {
        return qDeleteId === q.get('dependentQuestionId');
      });

      if (hasDependant) {
        this.showMessageDialog(
          Ember.String.loc('_cant_delete_question'),
          Ember.String.loc('_another_question_depends_on_this'));
        return;
      }
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
      this.showMessageDialog(Ember.String.loc('_please_wait'),
			     Ember.String.loc('_please_wait_until_previous_request'));
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
      this.showMessageDialog(Ember.String.loc('_please_wait'),
			     Ember.String.loc('_please_wait_until_previous_request'));
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
      this.showMessageDialog(Ember.String.loc('_please_wait'),
			     Ember.String.loc('_please_wait_until_previous_request'));
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
  },

  validateQuestionObserver: function(){
      this.set('questionValidationFailure', (this.text != null && this.text.length > 500));
  }.observes('this.text'),

  validateQuestionTooltipObserver: function(){
      this.set('questionTooltipValidationFailure', (this.tip != null && this.tip.length > 500));
  }.observes('this.tip'),

  validateQuestionIdObserver: function() {
    var self = this;
    self.validateQuestionId({
      success: function() {
        self.set('questionIdValidationFailure', false);
        self.set('questionIdValidationFailureReason', null);
      },
      failure: function(msg) {
        self.set('questionIdValidationFailure', true);
        self.set('questionIdValidationFailureReason', msg);
      }
    });
  }.observes('this.questionId'),

  showQuestionModifyButtons: function () {
    var form = FLOW.selectedControl.get('selectedSurvey');
    return FLOW.permControl.canEditForm(form);
  }.property('FLOW.selectedControl.selectedSurvey'),
});

/*
 *  View to render the options for an option type question.
 */
FLOW.OptionListView = Ember.CollectionView.extend({
  tagName: 'ul',
  content: null,
  itemViewClass: Ember.View.extend({
    templateName: 'navSurveys/question-option',
  }),
});
