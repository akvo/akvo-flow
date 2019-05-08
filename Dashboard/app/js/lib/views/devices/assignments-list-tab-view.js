FLOW.AssignmentsListTabView = FLOW.View.extend({

  editSurveyAssignment(event) {
    FLOW.selectedControl.set('selectedSurveyAssignment', event.context);
    FLOW.router.transitionTo('navDevices.editSurveysAssignment');
  },

  createNewAssignment() {
    const newAssignment = FLOW.store.createRecord(FLOW.SurveyAssignment, {});
    FLOW.selectedControl.set('selectedSurveyAssignment', newAssignment);
    FLOW.router.transitionTo('navDevices.editSurveysAssignment');
  },
});

FLOW.AssignmentView = FLOW.View.extend({
  tagName: 'span',
  deleteSurveyAssignment() {
    const assignment = FLOW.store.find(FLOW.SurveyAssignment, this.content.get('keyId'));
    if (assignment !== null) {
      assignment.deleteRecord();
      FLOW.store.commit();
    }
  },
});
