
function capitaliseFirstLetter(string) {
  if (Ember.empty(string)) return "";
  return string.charAt(0).toUpperCase() + string.slice(1);
}

FLOW.attributeTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: "text",
      value: "String"
    }), Ember.Object.create({
      label: "number",
      value: "Double"
    })
  ]
});

FLOW.attributeControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,

  setFilteredContent: function () {
    this.set('content', FLOW.store.filter(FLOW.Metric, function (item) {
      return true;
    }));
  },

  // load all Survey Groups
  populate: function () {
    if (FLOW.Env.showStatisticsFeature) {
        FLOW.store.find(FLOW.Metric);
    }
    this.setFilteredContent();
    this.set('sortProperties', ['name']);
    this.set('sortAscending', true);
  },

  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});

FLOW.cascadeResourceControl = Ember.ArrayController.create({
  content:null,
  published:null,
  statusUpdateTrigger:false,
  levelNames:null,
  displayLevelName1: null, displayLevelName2: null, displayLevelName3: null,
  displayLevelNum1: null, displayLevelNum2: null, displayLevelNum3: null,
  sortProperties: ['name'],
  sortAscending: true,

  populate: function() {
    this.set('content', FLOW.store.find(FLOW.CascadeResource));
    this.set('published', Ember.ArrayController.create({
      sortProperties: ['name'],
      sortAscending: true,
      content: FLOW.store.filter(FLOW.CascadeResource,function(item){
        return item.get('status') === 'PUBLISHED';
      })
    }));
  },

  setLevelNamesArray: function(){
    var i=1, levelNamesArray=[], numLevels;
    numLevels = FLOW.selectedControl.selectedCascadeResource.get('numLevels');

    // store the level names in an array
    FLOW.selectedControl.selectedCascadeResource.get('levelNames').forEach(function(item){
      if (i <= numLevels) {
        levelNamesArray.push(Ember.Object.create({
          levelName: item,
          level:i}));
        i++;
      }
    });
    this.set('levelNames',levelNamesArray);
  },

  setDisplayLevelNames: function(){
    var skip, names, numLevels;
    skip = FLOW.cascadeNodeControl.get('skip');
    names = this.get('levelNames');
    numLevels = FLOW.selectedControl.selectedCascadeResource.get('numLevels');
    this.set('displayLevelName1',names[skip].get('levelName'));
    if (numLevels > 1) {
      this.set('displayLevelName2',names[skip + 1].get('levelName'));
    } else {
      this.set('displayLevelName2',"");
    }
    if (numLevels > 2) {
      this.set('displayLevelName3',names[skip + 2].get('levelName'));
    } else {
      this.set('displayLevelName3',"");
    }
    this.set('displayLevelNum1',skip + 1);
    this.set('displayLevelNum2',skip + 2);
    this.set('displayLevelNum3',skip + 3);
  },

  publish: function(cascadeResourceId){
    FLOW.store.findQuery(FLOW.Action, {
      action: 'publishCascade',
      cascadeResourceId: cascadeResourceId
    });
  },

  hasQuestions: function () {
    if (!FLOW.selectedControl.selectedCascadeResource || !FLOW.selectedControl.selectedCascadeResource.get('keyId')) {
      return;
    }
    FLOW.store.findQuery(FLOW.Question, {cascadeResourceId: FLOW.selectedControl.selectedCascadeResource.get('keyId')});
  }.observes('FLOW.selectedControl.selectedCascadeResource'),

  triggerStatusUpdate: function(){
    this.toggleProperty('statusUpdateTrigger');
  },

  currentStatus: function () {
    // hack to get translation keys, don't delete them
    // {{t _not_published}}
    // {{t _publishing}}
    // {{t _published}}
    var status;
    if (!FLOW.selectedControl.selectedCascadeResource) {
      return '';
    }
    status = ('_' + FLOW.selectedControl.selectedCascadeResource.get('status')).toLowerCase();
    return Ember.String.loc(status);
  }.property('FLOW.selectedControl.selectedCascadeResource','this.statusUpdateTrigger'),

  isPublished: function () {
    if (!FLOW.selectedControl.selectedCascadeResource) {
      return false;
    }
    return FLOW.selectedControl.selectedCascadeResource.get('status') === 'PUBLISHED';
  }.property('FLOW.selectedControl.selectedCascadeResource','this.statusUpdateTrigger')
});

FLOW.cascadeNodeControl = Ember.ArrayController.create({
  content:null,
  level1:[], level2:[], level3:[], level4:[], level5:[], level6:[], level7:[],
  displayLevel1:[], displayLevel2:[], displayLevel3:[],
  parentNode:[],
  selectedNode:[],
  selectedNodeTrigger: true,
  skip: 0,

  emptyNodes: function(start){
    var i;
    for (i=start ; i < 6 ; i++){
      this.selectedNode[i]=null;
      this.set('level' + i,[]);
    }
  },

  toggleSelectedNodeTrigger:function (){
    this.toggleProperty('selectedNodeTrigger');
  },

  setDisplayLevels: function(){
    this.set('displayLevel1',this.get('level' + (this.get('skip') + 1)));
    this.set('displayLevel2',this.get('level' + (this.get('skip') + 2)));
    this.set('displayLevel3',this.get('level' + (this.get('skip') + 3)));
  },

  populate: function(cascadeResourceId, level, parentNodeId) {
    if (!cascadeResourceId) {
      return;
    }
    this.set('content',FLOW.store.findQuery(FLOW.CascadeNode, {
      cascadeResourceId: cascadeResourceId,
      parentNodeId: parentNodeId
    }));
    this.set('level' + level, FLOW.store.filter(FLOW.CascadeNode, function(item){
      return (item.get('parentNodeId') == parentNodeId && item.get('cascadeResourceId') == cascadeResourceId);
    }));
    this.parentNode[level] = parentNodeId;
    FLOW.cascadeNodeControl.setDisplayLevels();
  },

  addNode: function(cascadeResourceId, level, text, code) {
    var parentNodeId;
    if (level == 1) {
      parentNodeId = 0;
    } else {
      parentNodeId = this.get('parentNode')[level];
    }
    FLOW.store.createRecord(FLOW.CascadeNode, {
      "code": code,
      "name": capitaliseFirstLetter(text),
      "nodeId": null,
      "parentNodeId": parentNodeId,
      "cascadeResourceId": cascadeResourceId
    });
    if (FLOW.selectedControl.selectedCascadeResource.get('status') == 'PUBLISHED'){
      FLOW.selectedControl.selectedCascadeResource.set('status','NOT_PUBLISHED');
      FLOW.cascadeResourceControl.triggerStatusUpdate();
    }
    FLOW.store.commit();
    this.populate(cascadeResourceId, level, parentNodeId);
  },
});


FLOW.surveyInstanceControl = Ember.ArrayController.create({
  sortProperties: ['collectionDate'],
  sortAscending: false,
  selectedSurvey: null,
  content: null,
  sinceArray: [],
  pageNumber: 0,

  populate: function () {
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {}));
  },

  doInstanceQuery: function (surveyInstanceId, surveyId, deviceId, since, beginDate, endDate, submitterName, countryCode, level1, level2) {
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {
      'surveyInstanceId': surveyInstanceId,
      'surveyId': surveyId,
      'deviceId': deviceId,
      'since': since,
      'beginDate': beginDate,
      'endDate': endDate,
      'submitterName': submitterName,
      'countryCode': countryCode,
      'level1': level1,
      'level2': level2
    }));
  },

  contentChanged: function() {
    var mutableContents = [];

    this.get('arrangedContent').forEach(function(item) {
      mutableContents.pushObject(item);
    });

    this.set('currentContents', mutableContents);
  }.observes('content', 'content.isLoaded'),

  removeInstance: function(instance) {
    this.get('currentContents').forEach(function(item, i, currentContents) {
      if (item.get('id') == instance.get('id')) {
        currentContents.removeAt(i, 1);
      }
    });
  },

  allAreSelected: function (key, value) {
    if (arguments.length === 2) {
      this.setEach('isSelected', value);
      return value;
    } else {
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }
  }.property('@each.isSelected'),

  atLeastOneSelected: function () {
    return this.filterProperty('isSelected', true).get('length');
  }.property('@each.isSelected'),

  // fired from tableColumnView.sort
  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});

FLOW.SurveyedLocaleController = Ember.ArrayController.extend({
  sortProperties: ['collectionDate'],
  sortAscending: false,
  selectedSurvey: null,
  sinceArray: [],
  pageNumber: 0,

  populate: function (criteria) {
    this.set('content', FLOW.store.find(FLOW.SurveyedLocale, criteria));
  },

  contentChanged: function() {
    var mutableContents = [];

    this.get('arrangedContent').forEach(function(item) {
      mutableContents.pushObject(item);
    });

    this.set('currentContents', mutableContents);
  },

  removeLocale: function(locale) {
    this.get('currentContents').forEach(function(item, i, currentContents) {
      if (item.get('id') == locale.get('id')) {
        currentContents.removeAt(i, 1);
      }
    });
  },

  /* the current user is able to delete surveyed locales
    stored in the 'content' property of this controller */
  userCanDelete: function() {
    if(this.get('content') === null) {
      return false;
    }
    var surveyedLocale = this.get('content').get('firstObject'); // locale delete only allowed if enabled for the entire monitoring group
    if(surveyedLocale && surveyedLocale.get('surveyGroupId')) {
      return FLOW.surveyGroupControl.userCanDeleteData(surveyedLocale.get('surveyGroupId'));
    }
    return false; // prevents deletion incase no surveyId found
  }.property('content'),
});

FLOW.questionAnswerControl = Ember.ArrayController.create({
  content: null,

  // a computed property that returns a list containing *sub lists*
  // of responses to questions. Each sub list represents a single iteration
  // over a set of responses to questions in a specific question group, ordered
  // by question order. For repeat question groups two adjacent sub lists
  // represent two iterations of responses for that group
  contentByGroup: Ember.computed('content.isLoaded', function(key, value) {
    var content = Ember.get(this, 'content'),
        self = this;
    if (content) {
		var surveyQuestions = FLOW.questionControl.get('content');
		var groups = FLOW.questionGroupControl.get('content');

		var allResponses = [];
		var groupResponses = [];
		var answersInGroup = [];
		var group, groupId, groupName, groupIteration, isRepeatable, questionsInGroup, questionGroupId, questionId;

		for (var i = 0; i < groups.get('length'); i++) {
			group = groups.objectAt(i);
			isRepeatable = group.get('repeatable');
			groupId = group.get('keyId');
			groupName = group.get('name');

			questionsInGroup = surveyQuestions.filterProperty('questionGroupId',groupId);

			for (var j = 0; j < questionsInGroup.get('length'); j++) {
				questionId = questionsInGroup[j].get('keyId').toString();
				answersInGroup = answersInGroup.concat(self.filterProperty('questionID', questionId));
			}

			if (isRepeatable) {
				groupIteration = 0;

				this.splitIterationAnswers(answersInGroup).forEach(function(iterationAnswers){
					if (iterationAnswers && iterationAnswers.length) {
						groupIteration++;
						iterationAnswers.groupName = groupName + " - " + groupIteration;
						groupResponses.push(iterationAnswers);
					}
				});
			} else {
				answersInGroup.groupName = groupName;
				groupResponses.push(answersInGroup);
			}
			allResponses.push(groupResponses);
			groupResponses = [];
			answersInGroup = [];
		}
		return Ember.A(allResponses);
	  }
	return content;
  }),

  /* take a list of question answer objects containing multiple iterations
   * and split into a list of sublists, each sublist containing a single iteration
   * answers group together */
  splitIterationAnswers: function(allAnswersInRepeatGroup){
	var allIterations = [];
	var iteration;

	for(var i = 0; i < allAnswersInRepeatGroup.length; i++) {
		iteration = allAnswersInRepeatGroup[i].get('iteration');
		if (!allIterations[iteration]) {
			allIterations[iteration] = [];
		}
		allIterations[iteration].push(allAnswersInRepeatGroup[i]);
	}
	return allIterations;
  },

  doQuestionAnswerQuery: function (surveyInstanceId) {
    this.set('content', FLOW.store.findQuery(FLOW.QuestionAnswer, {
      'surveyInstanceId': surveyInstanceId
    }));
  },
});

FLOW.locationControl = Ember.ArrayController.create({
  selectedCountry: null,
  content:null,
  level1Content:null,
  level2Content:null,
  selectedLevel1: null,
  selectedLevel2: null,

  populateLevel1: function(){
    if (!Ember.none(this.get('selectedCountry')) && this.selectedCountry.get('iso').length > 0){
    this.set('level1Content',FLOW.store.findQuery(FLOW.SubCountry,{
      countryCode:this.selectedCountry.get('iso'),
      level:1,
      parentId:null
      }));
    }
  }.observes('this.selectedCountry'),

  populateLevel2: function(){
    if (!Ember.none(this.get('selectedLevel1')) && this.selectedLevel1.get('name').length > 0){
    this.set('level2Content',FLOW.store.findQuery(FLOW.SubCountry,{
      countryCode:this.selectedCountry.get('iso'),
      level:2,
      parentId:this.selectedLevel1.get('keyId')
      }));
    }
  }.observes('this.selectedLevel1')

});

FLOW.DataApprovalController = Ember.Controller.extend({});

FLOW.ApprovalGroupListController = Ember.ArrayController.extend({
    /* ---------------------
     * Controller Properties
     * ---------------------
     */
    sortProperties: ['name'],

    /* ---------------------
     * Controller Functions
     * ---------------------
     */

    /*
     * Load the list of approval groups
     */
    load: function () {
        this.set('content', FLOW.ApprovalGroup.find());
    },

    /*
     * Delete an approval group from the list
     */
    delete: function (group) {
        if(!group || !group.get('keyId')) {
            return;
        }

        var groups = this.content;
        var steps = FLOW.ApprovalStep.find({approvalGroupId: group.get('keyId')});
        var stepsController = FLOW.router.get('approvalStepsController');
        steps.on('didLoad', function () {
            steps.forEach(function (step) {
                step.deleteRecord();
            })
            group.deleteRecord();
            FLOW.store.commit();
        })
    },
});

FLOW.ApprovalGroupController = Ember.ObjectController.extend({

    /* ---------------------
     * Controller Properties
     * ---------------------
     */

    /*
     * Transform the `ordered` property on the ApprovalGroup model
     * to a string representation in order to bind successfully to
     * value attribute of the generated <option> entities
     */
    isOrderedApprovalGroup: function (key, value, previousValue) {
        var group = this.content;

        // setter
        if (group && arguments.length > 1) {
            group.set('ordered', value.trim() === "ordered")
        }

        // getter
        if(group && group.get('ordered')) {
            return "ordered";
        } else {
            return "unordered"
        }
    }.property('this.content'),




    /* ---------------------
     * Controller Functions
     * ---------------------
     */

    /*
     * Create a new approval group
     */
    add: function () {
        var group = FLOW.ApprovalGroup.createRecord({
            name: Ember.String.loc('_new_approval_group'),
            ordered: true,
        });

        this.set('content', group);
    },

    /*
     * Load the approval group by groupId
     */
    load: function (groupId) {
        if (groupId) {
            this.set('content', FLOW.ApprovalGroup.find(groupId));
        }
    },

    /*
     * Save an approval group and associated steps
     */
    save: function () {
        var validationError = this.validate();
        if(validationError) {
            return;
        }

        var group = this.content;
        if(group.get('name') !==  group.get('name').trim()) {
            group.set('name', group.get('name').trim());
        }

        FLOW.router.get('approvalStepsController').save(group);
    },

    /*
     * Validate approval group and associated steps
     */
    validate: function () {
        var stepsController = FLOW.router.get('approvalStepsController');
        var error = stepsController.validate();

        var group = this.content;
        if (!group.get('name') || !group.get('name').trim()) {
            error = Ember.String.loc('_blank_approval_group_name');
        }

        if(error) {
            FLOW.dialogControl.set('activeAction', 'ignore');
            FLOW.dialogControl.set('header', Ember.String.loc('_cannot_save'));
            FLOW.dialogControl.set('message', error);
            FLOW.dialogControl.set('showCANCEL', false);
            FLOW.dialogControl.set('showDialog', true);
        }

        return error;
    },

    /*
     * Cancel the editing of an approval group and its related
     * steps
     */
    cancel: function () {
        FLOW.store.get('defaultTransaction').rollback();
    },
});

FLOW.ApprovalStepsController = Ember.ArrayController.extend({

    /* ---------------------
     * Controller Properties
     * ---------------------
     */
    sortProperties: ['order'],

    /* ---------------------
     * Controller Functions
     * ---------------------
     */

    /*
     * Load approval steps for a given approval group
     */
    loadByGroupId: function (groupId) {
        var steps = Ember.A();
        if (groupId) {
            FLOW.ApprovalStep.find({approvalGroupId: groupId}).on('didLoad', function () {
                steps.addObjects(this);
            });
        }
        this.set('content',steps);
    },

    /*
     * Add an approval step for a given approval group
     */
    addApprovalStep: function () {
        var groupId = FLOW.router.get('approvalGroupController').get('content').get('keyId');
        if(!groupId) {
            FLOW.store.commit();
        }
        var steps = this.content;
        var lastStep = steps && steps.get('lastObject');

        // For cases where intermediate steps have been deleted during
        // the creation of an approval group, we do not update the order
        // of subsequent steps but rather ensure that the order of the
        // next step is based on the last step in the approval list. e.g
        // the order property for an approval list could be 0,2,4,6, where
        // steps with order 1,3 and 5 were deleted during group creation
        var newStep = Ember.Object.create({
            approvalGroupId: groupId,
            order: lastStep && lastStep.get('order') + 1 || 0,
            title: null,
        });
        steps.addObject(FLOW.store.createRecord(FLOW.ApprovalStep, newStep));
    },

    /*
     * Validate steps for erroneous input
     */
    validate: function () {
        var steps = this.content;
        var valid = true;

        steps.forEach(function (step) {
            var hasTitle = step.get('title') && step.get('title').trim();
            if(!hasTitle) {
                valid = false;
            }
        });

        var error;
        if (valid) {
            error = '';
        } else {
            error = Ember.String.loc('_blank_approval_step_title');
        }
        return error;
    },

    /*
     * Save approval steps
     */
    save: function(group) {
        var steps = this.content || [];
        steps.forEach(function (step, index) {
            if(step.get('code') && step.get('code').trim()) {
                step.set('code', step.get('code').trim());
            } else {
                step.set('code', null);
            }
            step.set('title', step.get('title').trim());

            if (!step.get('approvalGroupId') && group && group.get('keyId')) {
                step.set('approvalGroupId', group.get('keyId'));
            }
        });

        FLOW.store.commit();
    },

    /*
     * Delete an approval step
     */
    deleteApprovalStep: function (event) {
        var step = event.context;
        var steps = this.content;
        steps.removeObject(step);
        step.deleteRecord();
    },
});

FLOW.DataPointApprovalController = Ember.ArrayController.extend({

    content: Ember.A(),

    /**
     * add an approval element
     */
    add: function (dataPointApproval) {
        var dataPointApprovalList = this.content;
        var approval = FLOW.store.createRecord(FLOW.DataPointApproval, dataPointApproval);
        approval.on('didCreate', function () {
            dataPointApprovalList.addObject(approval);
        });

        FLOW.store.commit();
    },

    /**
     * Update an existing approval element
     */
    update: function (dataPointApproval) {
        var dataPointApprovalList = this.content;
        dataPointApproval.on('didUpdate', function () {
            dataPointApprovalList.addObject(dataPointApproval);
        });
        FLOW.store.commit();
    },

    /**
     * Load approval elements based on the surveyedLocaleId (data point id)
     */
    loadBySurveyedLocaleId: function (surveyedLocaleId) {
        var dataPointApprovalList = this.content;
        var approvals = FLOW.DataPointApproval.find({ surveyedLocaleId: surveyedLocaleId });
        approvals.on('didLoad', function () {
            dataPointApprovalList.addObjects(this);
        });
    },
});
