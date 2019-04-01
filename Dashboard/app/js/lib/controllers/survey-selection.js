
FLOW.SurveySelection = Ember.ObjectController.extend({
  surveyGroups: null,
  selectionFilter: null,

  populate() {
    const selectionFilter = this.get('selectionFilter');
    if (selectionFilter) {
      this.surveyGroups = FLOW.store.filter(FLOW.SurveyGroup, selectionFilter);
    } else {
      this.surveyGroups = FLOW.store.filter(FLOW.SurveyGroup);
    }
  },

  init() {
    this._super();
    this.populate();
  },

  getByParentId(parentId, filters) {
    return this.get('surveyGroups').filter((sg) => {
      if (filters.monitoringSurveysOnly) {
        return sg.get('parentId') === parentId
          && (sg.get('monitoringGroup') || sg.get('projectType') === 'PROJECT_FOLDER');
      }
      return sg.get('parentId') === parentId;
    }).filter((sg) => {
      // check if user has DATA_READ permissions
      if (filters.dataReadSurveysOnly) {
        return sg.get('parentId') === parentId
          && (FLOW.permControl.userCanViewData(sg) || sg.get('projectType') === 'PROJECT_FOLDER');
      }
      return sg.get('parentId') === parentId;
    }).sort((survey1, survey2) => {
      const s1 = survey1.get('name') || '';
      const s2 = survey2.get('name') || '';

      return s1.toLocaleLowerCase().localeCompare(
        s2.toLocaleLowerCase()
      );
    });
  },

  getSurvey(keyId) {
    const surveyGroups = this.get('surveyGroups').filter(sg => sg.get('keyId') === keyId);

    return surveyGroups[0];
  },

  isSurvey(keyId) {
    return this.getSurvey(keyId).get('projectType') === 'PROJECT';
  },
});
