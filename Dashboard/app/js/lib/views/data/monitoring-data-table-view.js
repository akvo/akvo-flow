import observe from '../../mixins/observe';
import template from '../../mixins/template';

FLOW.MonitoringDataTableView = FLOW.View.extend(observe({
  'FLOW.selectedControl.selectedSurveyGroup': 'watchSurveySelection',
}), {
  showingDetailsDialog: false,
  cursorStart: null,
  missingSurvey: false,

  pageNumber: Ember.computed(() => FLOW.router.surveyedLocaleController.get('pageNumber')).property('FLOW.router.surveyedLocaleController.pageNumber'),

  showDetailsDialog(evt) {
    FLOW.surveyInstanceControl.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {
      surveyedLocaleId: evt.context.get('keyId'),
    }));
    this.toggleProperty('showingDetailsDialog');
  },

  showApprovalStatusColumn: Ember.computed(() => FLOW.Env.enableDataApproval).property(),

  closeDetailsDialog() {
    this.toggleProperty('showingDetailsDialog');
  },

  showSurveyInstanceDetails(evt) {
    FLOW.questionAnswerControl.doQuestionAnswerQuery(evt.context);
    $('.si_details').hide();
    $(`tr[data-flow-id="si_details_${evt.context.get('keyId')}"]`).show();
  },

  watchSurveySelection() {
    if (FLOW.selectedControl.get('selectedSurveyGroup') !== null) {
      this.set('missingSurvey', false);
    }
  },

  findSurveyedLocale() {
    const ident = this.get('identifier');
    const displayName = this.get('displayName');
    const sgId = FLOW.selectedControl.get('selectedSurveyGroup');
    const criteria = {};
    // check if the survey is not selected, then highlight the dropdown
    if (FLOW.selectedControl.get('selectedSurveyGroup') === null) {
      this.set('missingSurvey', true);
      return;
    }

    if (ident) {
      criteria.identifier = ident;
    }

    if (displayName) {
      criteria.displayName = displayName;
    }

    if (sgId) {
      criteria.surveyGroupId = sgId.get('keyId');
    }

    if (this.get('cursorStart')) {
      criteria.since = this.get('cursorStart');
    }
    const surveyedLocaleController = FLOW.router.get('surveyedLocaleController');
    surveyedLocaleController.populate(criteria);

    surveyedLocaleController.get('content').on('didLoad', function () {
      const surveyedLocales = this;
      const surveyedLocaleIds = Ember.A();
      surveyedLocales.forEach((item) => {
        surveyedLocaleIds.addObject(item.get('keyId'));
      });
      FLOW.router.dataPointApprovalController.loadBySurveyedLocaleId(surveyedLocaleIds);
    });

    if (Ember.empty(FLOW.router.userListController.get('content'))) {
      FLOW.router.userListController.set('content', FLOW.User.find());
    }
  },

  noResults: Ember.computed(() => {
    const content = FLOW.router.surveyedLocaleController.get('content');
    if (content && content.get('isLoaded')) {
      return content.get('length') === 0;
    }
  }).property('FLOW.router.surveyedLocaleController.content', 'FLOW.router.surveyedLocaleController.content.isLoaded'),

  doNextPage() {
    const cursorArray = FLOW.router.surveyedLocaleController.get('sinceArray');
    const cursorStart = cursorArray.length > 0 ? cursorArray[cursorArray.length - 1] : null;
    this.set('cursorStart', cursorStart);
    this.findSurveyedLocale();
    FLOW.router.surveyedLocaleController.set('pageNumber', FLOW.router.surveyedLocaleController.get('pageNumber') + 1);
  },

  doPrevPage() {
    const cursorArray = FLOW.router.surveyedLocaleController.get('sinceArray');
    const cursorStart = cursorArray.length - 3 > -1 ? cursorArray[cursorArray.length - 3] : null;
    this.set('cursorStart', cursorStart);
    this.findSurveyedLocale();
    FLOW.router.surveyedLocaleController.set('pageNumber', FLOW.router.surveyedLocaleController.get('pageNumber') - 1);
  },

  hasNextPage: Ember.computed(() => FLOW.metaControl.get('numSLLoaded') == 20).property('FLOW.metaControl.numSLLoaded'),

  hasPrevPage: Ember.computed(() => FLOW.router.surveyedLocaleController.get('pageNumber')).property('FLOW.router.surveyedLocaleController.pageNumber'),

  willDestroyElement() {
    FLOW.router.surveyedLocaleController.set('currentContents', null);
    FLOW.metaControl.set('numSLLoaded', null);
    FLOW.router.surveyedLocaleController.set('pageNumber', 0);
  },
});

/**
 * View of each row/data point in the monitoring data tab
 */
FLOW.DataPointView = FLOW.View.extend(template('navData/monitoring-data-row'), observe({
  'this.showDataApprovalBlock': 'loadDataPointApprovalObserver',
}), {
  approvalStatus: [{ label: Ember.String.loc('_pending'), value: 'PENDING' }, { label: Ember.String.loc('_approved'), value: 'APPROVED' }, { label: Ember.String.loc('_rejected'), value: 'REJECTED' }],

  // catering for counter for the data points.
  tagName: 'span',
  content: null,
  pageNumber: 0,
  showDataApprovalBlock: false,

  showApprovalStatusColumn: Ember.computed(function () {
    return this.get('parentView').get('showApprovalStatusColumn');
  }).property(),

  dataPointApprovals: Ember.computed(function () {
    const approvals = FLOW.router.dataPointApprovalController.get('content');
    if (!approvals) {
      return;
    }

    const surveyedLocaleId = this.content && this.content.get('keyId');
    return approvals.filterProperty('surveyedLocaleId', surveyedLocaleId);
  }).property('FLOW.router.dataPointApprovalController.content.@each'),

  /*
     * get the next approval step id
     */
  nextApprovalStepId: Ember.computed(function () {
    return this.get('nextApprovalStep') && this.get('nextApprovalStep').get('keyId');
  }).property('this.nextApprovalStep'),

  /*
     * Derive the next approval step (in ordered approvals)
     */
  nextApprovalStep: Ember.computed(function () {
    let nextStep;
    const approvals = this.get('dataPointApprovals');
    const steps = FLOW.router.approvalStepsController.get('arrangedContent');

    if (Ember.empty(approvals)) {
      return steps && steps.get('firstObject');
    }

    steps.forEach((step) => {
      const approval = approvals.filterProperty('approvalStepId',
        step.get('keyId')).get('firstObject');
      const isPendingStep = !approval || approval.get('status') === 'PENDING';
      const isRejectedStep = approval && approval.get('status') === 'REJECTED';
      if (!nextStep && (isPendingStep || isRejectedStep)) {
        nextStep = step;
      }
    });

    return nextStep;

    // NOTE: below we observe the '@each.approvalDate' in order to be
    // sure that we only recalculate the next step whenever the approval
    //  has been correctly updated on the server side
  }).property('this.dataPointApprovals.@each.approvalDate'),

  /*
     * return true if there are any of the approvals rejected in this set
     */
  hasRejectedApproval: Ember.computed(function () {
    const approvals = this.get('dataPointApprovals');
    return !Ember.empty(approvals.filterProperty('status', 'REJECTED'));
  }).property('this.dataPointApprovals'),

  loadDataPointApprovalObserver() {
    if (!this.get('showDataApprovalBlock')) {
      return; // do nothing when hiding approval block
    }

    const dataPoint = this.get('content');
    if (dataPoint) {
      FLOW.router.dataPointApprovalController.loadBySurveyedLocaleId(dataPoint.get('keyId'));
    }
  },

  toggleShowDataApprovalBlock() {
    this.toggleProperty('showDataApprovalBlock');
  },

  dataPointRowNumber: Ember.computed(function () {
    const pageNumber = FLOW.router.surveyedLocaleController.get('pageNumber');
    return this.get('_parentView.contentIndex') + 1 + 20 * pageNumber;
  }).property(),
});

/**
 * View to render the status of a data point in the approval
 * status cell of each data point / row
 */
FLOW.DataPointApprovalStatusView = FLOW.View.extend({
  content: null,

  dataPointApprovalStatus: Ember.computed(function () {
    const latestApprovalStep = this.get('latestApprovalStep');
    if (!latestApprovalStep) {
      return;
    }

    const dataPointApprovals = this.get('parentView').get('dataPointApprovals');
    const dataPointApproval = dataPointApprovals && dataPointApprovals.filterProperty(
      'approvalStepId',
      latestApprovalStep.get('keyId')
    ).get('firstObject');
    const approvalStepStatus = dataPointApproval ? dataPointApproval.get('status') : Ember.String.loc('_pending');

    return `${latestApprovalStep.get('title')} - ${approvalStepStatus.toUpperCase()}`;
  }).property('this.parentView.nextApprovalStep'),

  /*
     * Derive the latest approval step for a particular data point
     */
  latestApprovalStep: Ember.computed(function () {
    const nextStep = this.get('parentView').get('nextApprovalStep');

    if (nextStep) {
      return nextStep;
    }
    const steps = FLOW.router.approvalStepsController.get('arrangedContent');
    const lastStep = steps && steps.get('lastObject');
    return lastStep;
  }).property('this.parentView.nextApprovalStep'),
});

/**
 * The data approval view for each approval step of a data point
 */
FLOW.DataPointApprovalView = FLOW.View.extend({
  step: null,

  dataPoint: null,

  dataPointApproval: Ember.computed(function () {
    const approvals = this.get('parentView').get('dataPointApprovals');
    const defaultApproval = Ember.Object.create({ status: null, comment: null });

    if (Ember.empty(approvals)) {
      return defaultApproval;
    }

    const stepId = this.step && this.step.get('keyId');
    const approval = approvals.filterProperty('approvalStepId', stepId).get('firstObject');

    return approval || defaultApproval;
  }).property('this.parentView.dataPointApprovals'),

  isApprovedStep: Ember.computed(function () {
    const dataPointApproval = this.get('dataPointApproval');
    return dataPointApproval && dataPointApproval.get('keyId');
  }).property('this.dataPointApproval'),

  /*
     * return the current user's id
     */
  currentUserId: Ember.computed(() => {
    const currentUserEmail = FLOW.currentUser.get('email');
    const userList = FLOW.router.userListController.get('content');
    const currentUser = userList
                            && userList.filterProperty('emailAddress', currentUserEmail).get('firstObject');
    return currentUser && currentUser.get('keyId');
  }).property(),

  /*
     * Enable the approval fields based on whether or not approval steps
     * should be executed in order
     */
  showApprovalFields: Ember.computed(function () {
    if (this.get('parentView').get('hasRejectedApproval')) {
      return false;
    }

    const approvalGroup = FLOW.router.approvalGroupController.get('content');
    const currentUserId = this.get('currentUserId');
    if (approvalGroup && approvalGroup.get('ordered')) {
      const nextStep = this.get('parentView').get('nextApprovalStep');
      if (nextStep) {
        return this.step.get('keyId') === nextStep.get('keyId')
                    && nextStep.get('approverUserList')
                    && nextStep.get('approverUserList').contains(currentUserId);
      }
      return false;
    }
    // this is for unordered approval steps. show fields for
    // all steps so that its possible to approve any step
    return true;
  }).property('this.parentView.nextApprovalStep'),

  /*
     *  Submit data approval properties to controller
     */
  submitDataPointApproval() {
    const dataPointApproval = this.get('dataPointApproval');
    if (dataPointApproval.get('keyId')) {
      FLOW.router.dataPointApprovalController.update(dataPointApproval);
    } else {
      dataPointApproval.surveyedLocaleId = this.get('dataPoint').get('keyId');
      dataPointApproval.approvalStepId = this.get('step').get('keyId');
      dataPointApproval.approverUserName = null;

      FLOW.router.dataPointApprovalController.add(dataPointApproval);
    }
  },
});
