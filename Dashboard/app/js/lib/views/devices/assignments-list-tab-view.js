FLOW.AssignmentsListTabView = FLOW.View.extend({

  editSurveyAssignment: function (event) {
    FLOW.selectedControl.set('selectedSurveyAssignment', event.context);
    FLOW.router.transitionTo('navDevices.editSurveysAssignment');
  },

  createNewAssignment: function () {
    var newAssignment;
    newAssignment = FLOW.store.createRecord(FLOW.SurveyAssignment, {});
    FLOW.selectedControl.set('selectedSurveyAssignment', newAssignment);
    FLOW.router.transitionTo('navDevices.editSurveysAssignment');
  }
});

FLOW.AssignmentView = FLOW.View.extend({
  tagName: 'span',
  deleteSurveyAssignment: function () {
    var assignment;
    assignment = FLOW.store.find(FLOW.SurveyAssignment, this.content.get('keyId'));
    if (assignment !== null) {
      assignment.deleteRecord();
      FLOW.store.commit();
    }
  }
});
