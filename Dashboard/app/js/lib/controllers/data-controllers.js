import observe from '../mixins/observe';

function capitaliseFirstLetter(string) {
  if (Ember.empty(string)) return '';
  return string.charAt(0).toUpperCase() + string.slice(1);
}

FLOW.cascadeResourceControl = Ember.ArrayController.create(
  observe({
    'FLOW.selectedControl.selectedCascadeResource': 'hasQuestions',
  }),
  {
    content: null,
    published: null,
    statusUpdateTrigger: false,
    levelNames: null,
    displayLevelName1: null,
    displayLevelName2: null,
    displayLevelName3: null,
    displayLevelNum1: null,
    displayLevelNum2: null,
    displayLevelNum3: null,
    sortProperties: ['name'],
    sortAscending: true,

    populate() {
      this.set('content', FLOW.store.find(FLOW.CascadeResource));
      this.set(
        'published',
        Ember.ArrayController.create({
          sortProperties: ['name'],
          sortAscending: true,
          content: FLOW.store.filter(
            FLOW.CascadeResource,
            item => item.get('status') === 'PUBLISHED'
          ),
        })
      );
    },

    setLevelNamesArray() {
      let i = 1;
      const levelNamesArray = [];
      const numLevels = FLOW.selectedControl.selectedCascadeResource.get('numLevels');

      // store the level names in an array
      FLOW.selectedControl.selectedCascadeResource.get('levelNames').forEach(item => {
        if (i <= numLevels) {
          levelNamesArray.push(
            Ember.Object.create({
              levelName: item,
              level: i,
            })
          );
          i++;
        }
      });
      this.set('levelNames', levelNamesArray);
    },

    setDisplayLevelNames() {
      const skip = FLOW.cascadeNodeControl.get('skip');
      const names = this.get('levelNames');
      const numLevels = FLOW.selectedControl.selectedCascadeResource.get('numLevels');
      this.set('displayLevelName1', names[skip].get('levelName'));
      if (numLevels > 1) {
        this.set('displayLevelName2', names[skip + 1].get('levelName'));
      } else {
        this.set('displayLevelName2', '');
      }
      if (numLevels > 2) {
        this.set('displayLevelName3', names[skip + 2].get('levelName'));
      } else {
        this.set('displayLevelName3', '');
      }
      this.set('displayLevelNum1', skip + 1);
      this.set('displayLevelNum2', skip + 2);
      this.set('displayLevelNum3', skip + 3);
    },

    publish(cascadeResourceId) {
      FLOW.store.findQuery(FLOW.Action, {
        action: 'publishCascade',
        cascadeResourceId,
      });
    },

    hasQuestions() {
      if (
        !FLOW.selectedControl.selectedCascadeResource ||
        !FLOW.selectedControl.selectedCascadeResource.get('keyId')
      ) {
        return;
      }
      FLOW.store.findQuery(FLOW.Question, {
        cascadeResourceId: FLOW.selectedControl.selectedCascadeResource.get('keyId'),
      });
    },

    triggerStatusUpdate() {
      this.toggleProperty('statusUpdateTrigger');
    },

    currentStatus: Ember.computed(() => {
      // hack to get translation keys, don't delete them
      // {{t _not_published}}
      // {{t _publishing}}
      // {{t _published}}
      if (!FLOW.selectedControl.selectedCascadeResource) {
        return '';
      }
      const status = `_${FLOW.selectedControl.selectedCascadeResource.get('status')}`.toLowerCase();
      return Ember.String.loc(status);
    }).property('FLOW.selectedControl.selectedCascadeResource', 'this.statusUpdateTrigger'),

    isPublished: Ember.computed(() => {
      if (!FLOW.selectedControl.selectedCascadeResource) {
        return false;
      }
      return FLOW.selectedControl.selectedCascadeResource.get('status') === 'PUBLISHED';
    }).property('FLOW.selectedControl.selectedCascadeResource', 'this.statusUpdateTrigger'),
  }
);

FLOW.cascadeNodeControl = Ember.ArrayController.create({
  content: null,
  level1: [],
  level2: [],
  level3: [],
  level4: [],
  level5: [],
  level6: [],
  level7: [],
  displayLevel1: [],
  displayLevel2: [],
  displayLevel3: [],
  parentNode: [],
  selectedNode: [],
  selectedNodeTrigger: true,
  skip: 0,

  emptyNodes(start) {
    let i;
    for (i = start; i < 6; i++) {
      this.selectedNode[i] = null;
      this.set(`level${i}`, []);
    }
  },

  toggleSelectedNodeTrigger() {
    this.toggleProperty('selectedNodeTrigger');
  },

  setDisplayLevels() {
    this.set('displayLevel1', this.get(`level${this.get('skip') + 1}`));
    this.set('displayLevel2', this.get(`level${this.get('skip') + 2}`));
    this.set('displayLevel3', this.get(`level${this.get('skip') + 3}`));
  },

  populate(cascadeResourceId, level, parentNodeId) {
    if (!cascadeResourceId) {
      return;
    }
    this.set(
      'content',
      FLOW.store.findQuery(FLOW.CascadeNode, {
        cascadeResourceId,
        parentNodeId,
      })
    );
    this.set(
      `level${level}`,
      FLOW.store.filter(
        FLOW.CascadeNode,
        item =>
          item.get('parentNodeId') == parentNodeId &&
          item.get('cascadeResourceId') == cascadeResourceId
      )
    );
    this.parentNode[level] = parentNodeId;
    FLOW.cascadeNodeControl.setDisplayLevels();
  },

  addNode(cascadeResourceId, level, text, code) {
    let parentNodeId;
    if (level == 1) {
      parentNodeId = 0;
    } else {
      parentNodeId = this.get('parentNode')[level];
    }
    FLOW.store.createRecord(FLOW.CascadeNode, {
      code,
      name: capitaliseFirstLetter(text),
      nodeId: null,
      parentNodeId,
      cascadeResourceId,
    });
    if (FLOW.selectedControl.selectedCascadeResource.get('status') == 'PUBLISHED') {
      FLOW.selectedControl.selectedCascadeResource.set('status', 'NOT_PUBLISHED');
      FLOW.cascadeResourceControl.triggerStatusUpdate();
    }
    FLOW.store.commit();
    this.populate(cascadeResourceId, level, parentNodeId);
  },
});

FLOW.surveyInstanceControl = Ember.ArrayController.create(
  observe({
    content: 'contentChanged',
    'content.isLoaded': 'contentChanged',
  }),
  {
    sortProperties: ['collectionDate'],
    sortAscending: false,
    selectedSurvey: null,
    content: null,
    sinceArray: [],
    pageNumber: 0,

    populate() {
      this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {}));
    },

    doInstanceQuery(
      surveyInstanceId,
      surveyId,
      deviceId,
      since,
      beginDate,
      endDate,
      submitterName,
      countryCode,
      level1,
      level2
    ) {
      this.set(
        'content',
        FLOW.store.findQuery(FLOW.SurveyInstance, {
          surveyInstanceId,
          surveyId,
          deviceId,
          since,
          beginDate,
          endDate,
          submitterName,
          countryCode,
          level1,
          level2,
        })
      );
    },

    contentChanged() {
      const mutableContents = [];

      this.get('arrangedContent').forEach(item => {
        mutableContents.pushObject(item);
      });

      this.set('currentContents', mutableContents);
    },

    removeInstance(instance) {
      this.get('currentContents').forEach((item, i, currentContents) => {
        if (item.get('id') == instance.get('id')) {
          currentContents.removeAt(i, 1);
        }
      });
    },

    allAreSelected: Ember.computed(function(key, value) {
      if (arguments.length === 2) {
        this.setEach('isSelected', value);
        return value;
      }
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }).property('@each.isSelected'),

    atLeastOneSelected: Ember.computed(function() {
      return this.filterProperty('isSelected', true).get('length');
    }).property('@each.isSelected'),

    // fired from tableColumnView.sort
    getSortInfo() {
      this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
      this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
    },
  }
);

FLOW.SurveyedLocaleController = Ember.ArrayController.extend(
  observe({
    content: 'contentChanged',
    'content.isLoaded': 'contentChanged',
  }),
  {
    sortProperties: ['collectionDate'],
    sortAscending: false,
    selectedSurvey: null,
    sinceArray: [],
    pageNumber: 0,

    populate(criteria) {
      this.set('content', FLOW.store.find(FLOW.SurveyedLocale, criteria));
    },

    contentChanged() {
      const mutableContents = [];

      this.get('arrangedContent').forEach(item => {
        mutableContents.pushObject(item);
      });

      this.set('currentContents', mutableContents);
    },

    removeLocale(locale) {
      this.get('currentContents').forEach((item, i, currentContents) => {
        if (item.get('id') == locale.get('id')) {
          currentContents.removeAt(i, 1);
        }
      });
    },

    /* the current user is able to delete surveyed locales
    stored in the 'content' property of this controller */
    userCanDelete: Ember.computed(function() {
      if (this.get('content') === null) {
        return false;
      }
      const surveyedLocale = this.get('content').get('firstObject'); // locale delete only allowed if enabled for the entire monitoring group
      if (surveyedLocale && surveyedLocale.get('surveyGroupId')) {
        return FLOW.surveyGroupControl.userCanDeleteData(surveyedLocale.get('surveyGroupId'));
      }
      return false; // prevents deletion incase no surveyId found
    }).property('content'),
  }
);

FLOW.dataPointAssignmentControl = Ember.ArrayController.create({
  content: null,

  populate(surveyAssignmentId) {
    this.set('content', FLOW.store.find(FLOW.DataPointAssignment, { surveyAssignmentId }));
  },
});

FLOW.questionAnswerControl = Ember.ArrayController.create({
  content: null,

  // a computed property that returns a list containing *sub lists*
  // of responses to questions. Each sub list represents a single iteration
  // over a set of responses to questions in a specific question group, ordered
  // by question order. For repeat question groups two adjacent sub lists
  // represent two iterations of responses for that group
  contentByGroup: Ember.computed(
    'content.isLoaded',
    'FLOW.questionContol.content.isLoaded',
    function() {
      const content = Ember.get(this, 'content');
      const self = this;
      const questions = FLOW.questionControl.get('content');
      const questionGroups = FLOW.questionGroupControl.get('content');

      if (content && questions && questionGroups) {
        const surveyQuestions = FLOW.questionControl.get('content');
        const groups = FLOW.questionGroupControl.get('content');
        const allResponses = [];
        let groupResponses = [];
        let answersInGroup = [];
        let group;
        let groupId;
        let groupName;
        let groupIteration;
        let isRepeatable;
        let questionsInGroup;
        let questionId;

        for (let i = 0; i < groups.get('length'); i++) {
          group = groups.objectAt(i);
          isRepeatable = group.get('repeatable');
          groupId = group.get('keyId');
          groupName = group.get('name');

          questionsInGroup = surveyQuestions.filterProperty('questionGroupId', groupId);

          for (let j = 0; j < questionsInGroup.get('length'); j++) {
            questionId = questionsInGroup[j].get('keyId').toString();
            answersInGroup = answersInGroup.concat(self.filterProperty('questionID', questionId));
          }

          if (isRepeatable) {
            groupIteration = 0;

            this.splitIterationAnswers(answersInGroup).forEach(iterationAnswers => {
              if (iterationAnswers && iterationAnswers.length) {
                groupIteration++;
                iterationAnswers.groupName = `${groupName} - ${groupIteration}`;
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
    }
  ),

  /* take a list of question answer objects containing multiple iterations
   * and split into a list of sublists, each sublist containing a single iteration
   * answers group together */
  splitIterationAnswers(allAnswersInRepeatGroup) {
    const allIterations = [];
    let iteration;

    for (let i = 0; i < allAnswersInRepeatGroup.length; i++) {
      iteration = allAnswersInRepeatGroup[i].get('iteration');
      if (!allIterations[iteration]) {
        allIterations[iteration] = [];
      }
      allIterations[iteration].push(allAnswersInRepeatGroup[i]);
    }
    return allIterations;
  },

  doQuestionAnswerQuery(surveyInstance) {
    const formId = surveyInstance.get('surveyId');
    const form = FLOW.surveyControl.filter(f => f.get('keyId') === formId).get(0);

    if (formId !== FLOW.selectedControl.selectedSurvey.get('keyId')) {
      FLOW.selectedControl.set('selectedSurvey', form);
    }

    this.set(
      'content',
      FLOW.store.findQuery(FLOW.QuestionAnswer, {
        surveyInstanceId: surveyInstance.get('keyId'),
      })
    );
  },
});

FLOW.locationControl = Ember.ArrayController.create(
  observe({
    'this.selectedCountry': 'populateLevel1',
    'this.selectedLevel1': 'populateLevel2',
  }),
  {
    selectedCountry: null,
    content: null,
    level1Content: null,
    level2Content: null,
    selectedLevel1: null,
    selectedLevel2: null,

    populateLevel1() {
      if (!Ember.none(this.get('selectedCountry')) && this.selectedCountry.get('iso').length > 0) {
        this.set(
          'level1Content',
          FLOW.store.findQuery(FLOW.SubCountry, {
            countryCode: this.selectedCountry.get('iso'),
            level: 1,
            parentId: null,
          })
        );
      }
    },

    populateLevel2() {
      if (!Ember.none(this.get('selectedLevel1')) && this.selectedLevel1.get('name').length > 0) {
        this.set(
          'level2Content',
          FLOW.store.findQuery(FLOW.SubCountry, {
            countryCode: this.selectedCountry.get('iso'),
            level: 2,
            parentId: this.selectedLevel1.get('keyId'),
          })
        );
      }
    },
  }
);

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
  load() {
    this.set('content', FLOW.ApprovalGroup.find());
  },

  /*
   * Delete an approval group from the list
   */
  delete(group) {
    if (!group || !group.get('keyId')) {
      return;
    }

    const steps = FLOW.ApprovalStep.find({ approvalGroupId: group.get('keyId') });
    steps.on('didLoad', () => {
      steps.forEach(step => {
        step.deleteRecord();
      });
      group.deleteRecord();
      FLOW.store.commit();
    });
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
  isOrderedApprovalGroup: Ember.computed(function(key, value) {
    const group = this.content;

    // setter
    if (group && arguments.length > 1) {
      group.set('ordered', value.trim() === 'ordered');
    }

    // getter
    if (group && group.get('ordered')) {
      return 'ordered';
    }
    return 'unordered';
  }).property('this.content'),

  /* ---------------------
   * Controller Functions
   * ---------------------
   */

  /*
   * Create a new approval group
   */
  add() {
    const group = FLOW.ApprovalGroup.createRecord({
      name: Ember.String.loc('_new_approval_group'),
      ordered: true,
    });

    this.set('content', group);
  },

  /*
   * Load the approval group by groupId
   */
  load(groupId) {
    if (groupId) {
      this.set('content', FLOW.ApprovalGroup.find(groupId));
    }
  },

  /*
   * Save an approval group and associated steps
   */
  save() {
    const validationError = this.validate();
    if (validationError) {
      return;
    }

    const group = this.content;
    if (group.get('name') !== group.get('name').trim()) {
      group.set('name', group.get('name').trim());
    }

    FLOW.router.get('approvalStepsController').save(group);
  },

  /*
   * Validate approval group and associated steps
   */
  validate() {
    const stepsController = FLOW.router.get('approvalStepsController');
    let error = stepsController.validate();

    const group = this.content;
    if (!group.get('name') || !group.get('name').trim()) {
      error = Ember.String.loc('_blank_approval_group_name');
    }

    if (error) {
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
  cancel() {
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
  loadByGroupId(groupId) {
    const steps = Ember.A();
    if (groupId) {
      FLOW.ApprovalStep.find({ approvalGroupId: groupId }).on('didLoad', function() {
        steps.addObjects(this);
      });
    }
    this.set('content', steps);
  },

  /*
   * Add an approval step for a given approval group
   */
  addApprovalStep() {
    const groupId = FLOW.router
      .get('approvalGroupController')
      .get('content')
      .get('keyId');
    if (!groupId) {
      FLOW.store.commit();
    }
    const steps = this.content;
    const lastStep = steps && steps.get('lastObject');

    // For cases where intermediate steps have been deleted during
    // the creation of an approval group, we do not update the order
    // of subsequent steps but rather ensure that the order of the
    // next step is based on the last step in the approval list. e.g
    // the order property for an approval list could be 0,2,4,6, where
    // steps with order 1,3 and 5 were deleted during group creation
    const newStep = Ember.Object.create({
      approvalGroupId: groupId,
      order: lastStep ? lastStep.get('order') + 1 : 0,
      title: null,
    });
    steps.addObject(FLOW.store.createRecord(FLOW.ApprovalStep, newStep));
  },

  /*
   * Validate steps for erroneous input
   */
  validate() {
    const steps = this.content;
    let valid = true;

    steps.forEach(step => {
      const hasTitle = step.get('title') && step.get('title').trim();
      if (!hasTitle) {
        valid = false;
      }
    });

    let error;
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
  save(group) {
    const steps = this.content || [];
    steps.forEach(step => {
      if (step.get('code') && step.get('code').trim()) {
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
  deleteApprovalStep(event) {
    const step = event.context;
    const steps = this.content;
    steps.removeObject(step);
    step.deleteRecord();
  },
});

FLOW.DataPointApprovalController = Ember.ArrayController.extend({
  content: Ember.A(),

  /**
   * add an approval element
   */
  add(dataPointApproval) {
    const dataPointApprovalList = this.content;
    const approval = FLOW.store.createRecord(FLOW.DataPointApproval, dataPointApproval);
    approval.on('didCreate', () => {
      dataPointApprovalList.addObject(approval);
    });

    FLOW.store.commit();
  },

  /**
   * Update an existing approval element
   */
  update(dataPointApproval) {
    const dataPointApprovalList = this.content;
    dataPointApproval.on('didUpdate', () => {
      dataPointApprovalList.addObject(dataPointApproval);
    });
    FLOW.store.commit();
  },

  /**
   * Load approval elements based on the surveyedLocaleId (data point id)
   */
  loadBySurveyedLocaleId(surveyedLocaleId) {
    const dataPointApprovalList = this.content;
    const approvals = FLOW.DataPointApproval.find({ surveyedLocaleId });
    approvals.on('didLoad', function() {
      dataPointApprovalList.addObjects(this);
    });
  },
});
