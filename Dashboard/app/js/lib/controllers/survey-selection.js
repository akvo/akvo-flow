
FLOW.SurveySelection = Ember.ObjectController.extend({
  surveyGroups: null,

  populate: function() {
    this.surveyGroups = FLOW.store.find(FLOW.SurveyGroup);
  },

  init: function() {
    this._super();
    this.populate();
  },

  getByParentId: function(parentId) {
    return this.get('surveyGroups').filter(function(sg) {
      return sg.get('parentId') === parentId;
    })
  },

  getSurvey: function(keyId) {
    return FLOW.store.find(FLOW.SurveyGroup, keyId);
  },

  isSurvey: function(keyId) {
    return this.getSurvey(keyId).get('projectType') === 'PROJECT';
  },
});
