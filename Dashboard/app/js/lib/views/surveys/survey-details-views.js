import observe from '../../mixins/observe';

// ************************ Surveys *************************
FLOW.QuestionGroupItemTranslationView = FLOW.View.extend({
  content: null,
  // question group content comes through binding in handlebars file
  amVisible: Ember.computed(function () {
    const selected = FLOW.selectedControl.get('selectedQuestionGroup');
    if (selected) {
      const isVis = (this.content.get('keyId') === FLOW.selectedControl.selectedQuestionGroup.get('keyId'));
      return isVis;
    }
    return null;
  }).property('FLOW.selectedControl.selectedQuestionGroup', 'content.keyId').cacheable(),

  toggleVisibility() {
    if (this.get('amVisible')) {
      // if we have any unsaved translations, do nothing.
      // a warning will be printed by the check method.
      console.log('unsaved? ', FLOW.translationControl.unsavedTranslations());
      if (FLOW.translationControl.unsavedTranslations()) {
        return;
      }
      FLOW.selectedControl.set('selectedQuestionGroup', null);
      // empty translation structures
    } else {
      FLOW.selectedControl.set('selectedQuestionGroup', this.content);
      FLOW.translationControl.loadQuestionGroup(this.content.get('keyId'));
    }
  },
});


FLOW.QuestionGroupItemView = FLOW.View.extend(observe({
  'this.amCopying': 'pollQuestionGroupStatus',
}), {
  // question group content comes through binding in handlebars file
  zeroItem: false,
  renderView: false,
  showQGDeletedialog: false,
  showQGroupNameEditField: false,
  pollingTimer: null,
  showSaveCancelButton: false,

  amCopying: Ember.computed(function () {
    return this.content.get('status') == 'COPYING';
  }).property('this.content.status'),

  amVisible: Ember.computed(function () {
    const selected = FLOW.selectedControl.get('selectedQuestionGroup');
    if (selected) {
      const isVis = (this.content.get('keyId') === FLOW.selectedControl.selectedQuestionGroup.get('keyId'));
      return isVis;
    }
    return null;
  }).property('FLOW.selectedControl.selectedQuestionGroup', 'content.keyId').cacheable(),

  amQuestionGroupPublishingError: Ember.computed(function () {
    if (!FLOW.selectedControl.get('publishingErrors')) { return false; }
    return Boolean(FLOW.selectedControl.get('publishingErrors')[this.content.get('keyId')]);
  }).property('FLOW.selectedControl.publishingErrors'),

  toggleVisibility() {
    if (this.get('amVisible')) {
      FLOW.selectedControl.set('selectedQuestion', null);
      FLOW.selectedControl.set('selectedQuestionGroup', null);
    } else {
      FLOW.selectedControl.set('selectedQuestionGroup', this.content);
    }
  },

  doQGroupNameEdit() {
    this.set('showQGroupNameEditField', true);
    this.set('showSaveCancelButton', true);
  },

  // fired when 'save' is clicked
  saveQuestionGroup() {
    const qgId = this.content.get('id');
    const questionGroup = FLOW.store.find(FLOW.QuestionGroup, qgId);
    const path = `${FLOW.selectedControl.selectedSurveyGroup.get('code')}/${FLOW.selectedControl.selectedSurvey.get('name')}`;
    questionGroup.set('code', this.content.get('code'));
    questionGroup.set('name', this.content.get('code'));
    questionGroup.set('path', path);
    questionGroup.set('repeatable', this.content.get('repeatable'));
    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');

    this.set('showQGroupNameEditField', false);
    this.set('showSaveCancelButton', false);

    FLOW.store.commit();
    FLOW.surveyControl.validateSurveyToBePublished();
  },

  eventManager: Ember.Object.create({
    click(event, clickedView) {
      if (clickedView.type === 'checkbox') {
        const parentView = clickedView.get('parentView');
        parentView.set('showSaveCancelButton', true);
      }
    },
  }),

  // fired when 'cancel' is clicked while showing edit group name field. Cancels the edit.
  cancelQuestionGroupNameEdit() {
    this.set('showQGroupNameEditField', false);
    this.set('showSaveCancelButton', false);
  },

  // true if one question group has been selected for Move
  oneSelectedForMove: Ember.computed(() => {
    const selectedForMove = FLOW.selectedControl.get('selectedForMoveQuestionGroup');
    const selectedSurvey = FLOW.selectedControl.get('selectedSurvey');

    if (selectedForMove && selectedSurvey) {
      return selectedForMove.get('surveyId') === selectedSurvey.get('keyId');
    }
  }).property('FLOW.selectedControl.selectedForMoveQuestionGroup'),

  // true if one question group has been selected for Copy
  oneSelectedForCopy: Ember.computed(() => {
    const selectedForCopy = FLOW.selectedControl.get('selectedForCopyQuestionGroup');
    const selectedSurvey = FLOW.selectedControl.get('selectedSurvey');

    if (selectedForCopy && selectedSurvey) {
      return selectedForCopy.get('surveyId') === selectedSurvey.get('keyId');
    }
  }).property('FLOW.selectedControl.selectedForCopyQuestionGroup'),

  // execute group delete
  deleteQuestionGroup() {
    const qgId = this.content.get('id');

    // do preflight check if deleting this question group is allowed
    FLOW.store.findQuery(FLOW.QuestionGroup, {
      preflight: 'delete',
      questionGroupId: qgId,
    });
  },

  // insert group
  doInsertQuestionGroup() {
    const path = `${FLOW.selectedControl.selectedSurveyGroup.get('code')}/${FLOW.selectedControl.selectedSurvey.get('name')}`;
    if (FLOW.selectedControl.selectedSurvey.get('keyId')) {
      let insertAfterOrder;
      if (this.get('zeroItem')) {
        insertAfterOrder = 0;
      } else {
        insertAfterOrder = this.content.get('order');
      }

      // restore order
      const sId = FLOW.selectedControl.selectedSurvey.get('keyId');

      // reorder the rest of the question groups
      FLOW.questionGroupControl.reorderQuestionGroups(sId, insertAfterOrder, 'increment');

      // create new QuestionGroup item in the store
      FLOW.store.createRecord(FLOW.QuestionGroup, {
        code: Ember.String.loc('_new_group_please_change_name'),
        name: Ember.String.loc('_new_group_please_change_name'),
        order: insertAfterOrder + 1,
        path,
        status: 'READY',
        surveyId: FLOW.selectedControl.selectedSurvey.get('keyId'),
      });

      FLOW.questionGroupControl.submitBulkQuestionGroupsReorder(sId);

      FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
      FLOW.store.commit();
      FLOW.questionGroupControl.setFilteredContent();
    } else {
      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', Ember.String.loc('_please_save_survey'));
      FLOW.dialogControl.set('message', Ember.String.loc('_please_save_survey_text'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
    }
  },

  // prepare for group copy. Shows 'copy to here' buttons
  doQGroupCopy() {
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', this.content);
    FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
  },


  // cancel group copy
  doQGroupCopyCancel() {
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
  },


  // prepare for group move. Shows 'move here' buttons
  doQGroupMove() {
    FLOW.selectedControl.set('selectedForMoveQuestionGroup', this.content);
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
  },

  // cancel group move
  doQGroupMoveCancel() {
    FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
  },

  // execute group move to selected location
  doQGroupMoveHere() {
    let insertAfterOrder;
    const selectedOrder = FLOW.selectedControl.selectedForMoveQuestionGroup.get('order');

    if (this.get('zeroItem')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }

    // only do something if we are not moving to the same place
    if (!((selectedOrder == insertAfterOrder) || (selectedOrder == (insertAfterOrder + 1)))) {
      const selectedQG = FLOW.store.find(FLOW.QuestionGroup, FLOW.selectedControl.selectedForMoveQuestionGroup.get('keyId'));
      if (selectedQG !== null) {
        // selectedQG.set('order', insertAfterOrder + 1);
        // restore order
        const sId = FLOW.selectedControl.selectedSurvey.get('keyId');
        const questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, item => item.get('surveyId') == sId);

        const origOrder = FLOW.selectedControl.selectedForMoveQuestionGroup.get('order');
        const movingUp = origOrder < insertAfterOrder;

        questionGroupsInSurvey.forEach((item) => {
          const currentOrder = item.get('order');
          if (movingUp) {
            if (currentOrder == origOrder) {
              // move moving item to right location
              selectedQG.set('order', insertAfterOrder);
            } else if ((currentOrder > origOrder) && (currentOrder <= insertAfterOrder)) {
              // move item down
              item.set('order', item.get('order') - 1);
            }
          // Moving down
          } else if (currentOrder == origOrder) {
            // move moving item to right location
            selectedQG.set('order', insertAfterOrder + 1);
          } else if ((currentOrder < origOrder) && (currentOrder > insertAfterOrder)) {
            // move item up
            item.set('order', item.get('order') + 1);
          }
        });

        FLOW.questionGroupControl.submitBulkQuestionGroupsReorder(sId);

        FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
        FLOW.store.commit();
      }
    }

    FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
  },

  // cycle until our local question group has an id
  // when this is done, start monitoring the status of the remote question group
  pollQuestionGroupStatus() {
    const self = this;
    let qgQuery = null;
    if (this.get('amCopying')) {
      qgQuery = setInterval(() => {
        // if the question group has a keyId, we can start polling it remotely
        if (self.content && self.content.get('keyId')) {
          // we have an id and can start polling remotely
          if (self.content.get('status') == 'READY') {
            // load new group's questions and clear interval
            FLOW.store.findQuery(FLOW.Question, {
              questionGroupId: self.content.get('keyId'),
            });
            clearInterval(qgQuery);
          } else {
            FLOW.questionGroupControl.populate();
          }
        }
      }, 5000);
    } else {
      clearInterval(qgQuery);
    }
  },

  // execute group copy to selected location
  doQGroupCopyHere() {
    let insertAfterOrder;
    const path = `${FLOW.selectedControl.selectedSurveyGroup.get('code')}/${FLOW.selectedControl.selectedSurvey.get('name')}`;

    if (this.get('zeroItem')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }

    const sId = FLOW.selectedControl.selectedSurvey.get('keyId');

    // restore order
    FLOW.questionGroupControl.reorderQuestionGroups(sId, insertAfterOrder, 'increment');

    FLOW.store.createRecord(FLOW.QuestionGroup, {
      order: insertAfterOrder + 1,
      code: FLOW.selectedControl.selectedForCopyQuestionGroup.get('code'),
      name: FLOW.selectedControl.selectedForCopyQuestionGroup.get('code'),
      path,
      status: 'COPYING',
      surveyId: FLOW.selectedControl.selectedForCopyQuestionGroup.get('surveyId'),
      sourceId: FLOW.selectedControl.selectedForCopyQuestionGroup.get('keyId'),
      repeatable: FLOW.selectedControl.selectedForCopyQuestionGroup.get('repeatable'),
    });

    FLOW.questionGroupControl.submitBulkQuestionGroupsReorder(sId);

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
  },

  showQuestionGroupModifyButtons: Ember.computed(() => {
    const form = FLOW.selectedControl.get('selectedSurvey');
    return FLOW.permControl.canEditForm(form);
  }).property('FLOW.selectedControl.selectedSurvey'),

  disableQuestionGroupEditing: Ember.computed(() => {
    const form = FLOW.selectedControl.get('selectedSurvey');
    return !FLOW.permControl.canEditForm(form);
  }).property('FLOW.selectedControl.selectedSurvey'),
});
