import React from 'react';

// eslint-disable-next-line import/no-unresolved
import FolderSurveySelector from 'akvo-flow/components/FolderSurveySelector';

// eslint-disable-next-line import/no-unresolved
require('akvo-flow/views/react-component');

FLOW.FolderSurveySelectorView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
    this.comparator = this.comparator.bind(this);
    this.folderSurveySelector = this.folderSurveySelector.bind(this);
    this.getLevels = this.getLevels.bind(this);
    this.handleSelect = this.handleSelect.bind(this);

    this.surveyGroups = [];
  },

  didInsertElement(...args) {
    this._super(...args);
    if (FLOW.projectControl.content.isLoaded) {
      FLOW.projectControl.get('content').forEach((item) => {
        this.surveyGroups.push({
          keyId: item.get('keyId'),
          parentId: item.get('parentId'),
          name: item.get('name'),
          published: item.get('published'),
          projectType: item.get('projectType'),
          monitoringGroup: item.get('monitoringGroup'),
          ancestorIds: item.get('ancestorIds'),
        });
      });
      this.folderSurveySelector(0);
    }
  },

  getLevels(parentId = 0) {
    return [
      [{
        keyId: 0,
        parentId: null,
        name: Ember.String.loc('_choose_folder_or_survey'),
      }]
        .concat(this.surveyGroups.filter(sg => sg.parentId == parentId)
          .sort(this.comparator)),
    ];
  },

  handleSelect(parentId) {
    // check if a survey has been selected
    const selectedSG = FLOW.projectControl.get('content').find(sg => sg.get('keyId') == parentId);
    if (selectedSG && selectedSG.get('projectType') !== 'PROJECT_FOLDER') {
      FLOW.selectedControl.set('selectedSurveyGroup', selectedSG);
      return null;
    }

    const newLevels = this.getLevels(parentId);
    return newLevels;
  },

  folderSurveySelector(parentId) {
    // if content isn't loaded, return early
    if (!FLOW.projectControl.content.isLoaded) {
      return;
    }

    const initialLevels = this.getLevels(parentId);

    this.reactRender(
      <FolderSurveySelector
        levels={initialLevels}
        onChange={this.handleSelect}
      />
    );
  },

  comparator(a, b) {
    const nameA = a.name.toUpperCase(); // ignore upper and lowercase
    const nameB = b.name.toUpperCase(); // ignore upper and lowercase
    if (nameA < nameB) {
      return -1;
    }
    if (nameA > nameB) {
      return 1;
    }

    // names must be equal
    return 0;
  },
});
