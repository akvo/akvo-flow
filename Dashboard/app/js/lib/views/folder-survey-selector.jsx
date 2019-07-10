import React from 'react';

import ChildOption from 'akvo-flow/components/ChildOption';

require('akvo-flow/views/react-component');

FLOW.FolderSurveySelectorView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
    this.handleChange = this.handleChange.bind(this);
    this.folderSurveySelector = this.folderSurveySelector.bind(this);
    this.state = { value: 0 };
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

  folderSurveySelector(parentId) {
    if (!FLOW.projectControl.content.isLoaded) return;
    const levels = [];
    let surveySelected = false;
    // if not root level, generate ancestor selectors
    if (parentId !== 0) {
      const parent = this.surveyGroups.find(sg => sg.keyId == parentId);
      for (let i = 0; i < parent.ancestorIds.length; i++) {
        const level = [{
          keyId: 0,
          parentId: null,
          name: Ember.String.loc('_choose_folder_or_survey'),
        }].concat(this.surveyGroups.filter(sgs => sgs.parentId == parent.ancestorIds[i])
          .sort((a, b) => {
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
          }));
        levels.push(level);
      }
      const selectedSG = FLOW.projectControl.get('content').find(sg => sg.get('keyId') == parentId);
      if (selectedSG && selectedSG.get('projectType') !== 'PROJECT_FOLDER') {
        surveySelected = true;
        FLOW.selectedControl.set('selectedSurveyGroup', selectedSG);
      }
    }

    if (!surveySelected) {
      levels.push([{
        keyId: 0,
        parentId: null,
        name: Ember.String.loc('_choose_folder_or_survey'),
      }].concat(this.surveyGroups.filter(sg => sg.parentId == parentId).sort((a, b) => {
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
      })));
    }

    this.reactRender(
      <div>
        {levels.map(sgs => (
          <select value={this.state.value} onChange={this.handleChange}>
            {sgs.map(sg => (
              <ChildOption key={sg.keyId} name={sg.name} value={sg.keyId} />
            ))}
          </select>
        ))}
      </div>
    );
  },

  handleChange(event) {
    if (event.target.value !== 0) {
      this.folderSurveySelector(event.target.value);
    }
  },
});
