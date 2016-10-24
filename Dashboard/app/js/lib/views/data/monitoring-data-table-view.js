FLOW.MonitoringDataTableView = FLOW.View.extend({
  showingDetailsDialog: false,
  cursorStart: null,

  pageNumber: function(){
	return FLOW.router.surveyedLocaleController.get('pageNumber');
  }.property('FLOW.router.surveyedLocaleController.pageNumber'),

  showDetailsDialog: function (evt) {
	FLOW.surveyInstanceControl.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {
		surveyedLocaleId: evt.context.get('keyId')
	}));
    this.toggleProperty('showingDetailsDialog');
  },

  showApprovalStatusColumn: function () {
      return FLOW.Env.enableDataApproval;
  }.property(),

  closeDetailsDialog: function () {
    this.toggleProperty('showingDetailsDialog');
  },

  showSurveyInstanceDetails: function (evt) {
    FLOW.questionAnswerControl.doQuestionAnswerQuery(evt.context.get('keyId'));
    $('.si_details').hide();
    $('tr[data-flow-id="si_details_' + evt.context.get('keyId') + '"]').show();
  },

  findSurveyedLocale: function (evt) {
	  var ident = this.get('identifier'),
	      displayName = this.get('displayName'),
	      sgId = FLOW.selectedControl.get('selectedSurveyGroup'),
	      cursorType = FLOW.metaControl.get('cursorType'),
        criteria = {};

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
      FLOW.router.surveyedLocaleController.populate(criteria);
      if(Ember.empty(FLOW.router.userListController.get('content'))) {
          FLOW.router.userListController.set('content', FLOW.User.find());
      }
  },

  doNextPage: function () {
	var cursorArray, cursorStart;
	cursorArray = FLOW.router.surveyedLocaleController.get('sinceArray');
	cursorStart = cursorArray.length > 0 ? cursorArray[cursorArray.length - 1] : null;
	this.set('cursorStart', cursorStart);
    this.findSurveyedLocale();
    FLOW.router.surveyedLocaleController.set('pageNumber', FLOW.router.surveyedLocaleController.get('pageNumber') + 1);
  },

  doPrevPage: function () {
	var cursorArray, cursorStart;
	cursorArray = FLOW.router.surveyedLocaleController.get('sinceArray');
	cursorStart = cursorArray.length - 3 > -1 ? cursorArray[cursorArray.length - 3] : null;
	this.set('cursorStart', cursorStart);
    this.findSurveyedLocale();
    FLOW.router.surveyedLocaleController.set('pageNumber', FLOW.router.surveyedLocaleController.get('pageNumber') - 1);
  },

  hasNextPage: function () {
    return FLOW.metaControl.get('numSLLoaded') == 20;
  }.property('FLOW.metaControl.numSLLoaded'),

  hasPrevPage: function () {
    return FLOW.router.surveyedLocaleController.get('pageNumber');
  }.property('FLOW.router.surveyedLocaleController.pageNumber'),
});

/**
 * View of each row/data point in the monitoring data tab
 */
FLOW.DataPointView = FLOW.View.extend({
    templateName: 'navData/monitoring-data-row',

    approvalStatus: [{label: Ember.String.loc('_pending'), value: 'PENDING'}, { label: Ember.String.loc('_approved'), value: 'APPROVED' },{ label: Ember.String.loc('_rejected'), value: 'REJECTED'}],

    showDataApprovalBlock: false,

    showSurveyedLocaleDeleteButton: function() {
        return FLOW.router.surveyedLocaleController.get('userCanDelete');
    }.property(),

    showApprovalStatusColumn: function () {
        return this.get('parentView').get('showApprovalStatusColumn');
    }.property(),

    dataPointApprovals: function () {
        var approvals = FLOW.router.dataPointApprovalController.get('content');
        if(!approvals) {
            return;
        }

        var surveyedLocaleId = this.content && this.content.get('keyId');
        return approvals.filterProperty('surveyedLocaleId', surveyedLocaleId);
    }.property('FLOW.router.dataPointApprovalController.content.@each'),

    /*
     * Derive the approvalStepId for the next approval (in ordered approvals)
     */
    nextApprovalStepId: function () {
        var approvals = this.get('dataPointApprovals');
        var steps = FLOW.router.approvalStepsController.get('arrangedContent');
        var lastApprovedStepOrder = -1;

        approvals.forEach(function (approval) {
            var step = steps.filterProperty('keyId',
                                approval.get('approvalStepId')).get('firstObject');
            if (approval.get('status') === 'APPROVED' &&
                    step && (step.get('order') > lastApprovedStepOrder)) {
                lastApprovedStepOrder = step.get('order');
            }
        });

        var nextStep = steps.filterProperty('order', ++lastApprovedStepOrder).get('firstObject');
        return nextStep && nextStep.get('keyId');

    // NOTE: below we observe the '@each.approvalDate' in order to be
    // sure that we only recalculate the next step whenever the approval
    //  has been correctly updated on the server side
    }.property('this.dataPointApprovals.@each.approvalDate'),

    /*
     * return true if there are any of the approvals rejected in this set
     */
    hasRejectedApproval: function () {
        var approvals = this.get('dataPointApprovals');
        return !Ember.empty(approvals.filterProperty('status', 'REJECTED'));
    }.property('this.dataPointApprovals'),

    loadDataPointApprovalObserver: function () {
        if(!this.get('showDataApprovalBlock')) {
            return; // do nothing when hiding approval block
        }

        var dataPoint = this.get('content');
        if (dataPoint) {
            FLOW.router.dataPointApprovalController.loadBySurveyedLocaleId(dataPoint.get('keyId'));
        }
    }.observes('this.showDataApprovalBlock'),

    toggleShowDataApprovalBlock: function () {
        this.toggleProperty('showDataApprovalBlock');
    },
});

/**
 * View to render the status of a data point in the approval
 * status cell of each data point / row
 */
FLOW.DataPointApprovalStatusView = FLOW.View.extend({
    content: null,

    latestApprovalStepTitle: function () {
        var nextStepId = this.get('parentView').get('nextApprovalStepId');
        var stepsController = FLOW.router.get('approvalStepsController');
        var step = stepsController.filterProperty('keyId', nextStepId).get('firstObject');
        return step && step.get('title');
    }.property('this.parentView.nextApprovalStepId'),
});

/**
 * The data approval view for each approval step of a data point
 */
FLOW.DataPointApprovalView = FLOW.View.extend({
    step: null,

    dataPoint: null,

    dataPointApproval: function () {
        var approvals = this.get('parentView').get('dataPointApprovals');
        var defaultApproval = Ember.Object.create({ status: null, comment: null});

        if(Ember.empty(approvals)) {
            return defaultApproval;
        }

        var stepId = this.step && this.step.get('keyId');
        var approval = approvals.filterProperty('approvalStepId', stepId).get('firstObject');

        return approval || defaultApproval;
    }.property('this.parentView.dataPointApprovals'),

    isApprovedStep: function () {
        var dataPointApproval = this.get('dataPointApproval');
        return dataPointApproval && dataPointApproval.get('keyId');
    }.property('this.dataPointApproval'),

    /*
     * Enable the approval fields based on whether or not approval steps
     * should be executed in order
     */
    showApprovalFields: function () {
        if(this.get('parentView').get('hasRejectedApproval')) {
            return false;
        }

        var approvalGroup = FLOW.router.approvalGroupController.get('content');
        if(approvalGroup && approvalGroup.get('ordered')) {
            var nextStepId = this.get('parentView').get('nextApprovalStepId');
            if (nextStepId) {
                return this.step.get('keyId') === nextStepId;
            } else {
                return false;
            }
        } else {
            // this is for unordered approval steps. show fields for
            // all steps so that its possible to approve any step
            return true;
        }
    }.property('this.parentView.nextApprovalStepId'),

    /*
     *  Submit data approval properties to controller
     */
    submitDataPointApproval: function (event) {

        var dataPointApproval = this.get('dataPointApproval');
        if(dataPointApproval.get('keyId')) {
            FLOW.router.dataPointApprovalController.update(dataPointApproval);
        } else {
            dataPointApproval.surveyedLocaleId = this.get('dataPoint').get('keyId');
            dataPointApproval.approvalStepId = this.get('step').get('keyId');
            dataPointApproval.approverUserName = null;

            FLOW.router.dataPointApprovalController.add(dataPointApproval);
        }
    },
});