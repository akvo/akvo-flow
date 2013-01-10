FLOW.AssignmentsListTabView = Em.View.extend({

  editSurveyAssignment: function(event) {
    FLOW.selectedControl.set('selectedSurveyAssignment',event.context);
    FLOW.router.transitionTo('navDevices.assignSurveysOverview');
  }

});

FLOW.AssignmentView = Em.View.extend({
 tagName: 'span',
  deleteSurveyAssignment: function() {
    var assignment;
    assignment = FLOW.store.find(FLOW.SurveyAssignment, this.content.get('keyId'));
    if(assignment !== null) {
      assignment.deleteRecord();
      FLOW.store.commit();
    }
  }
});

