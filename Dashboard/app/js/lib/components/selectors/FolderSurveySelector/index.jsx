import React from 'react';

export default class FolderSurveySelector extends React.Component {
  state = {
    surveyGroups: [],
    levels: [],
  }

  componentDidMount() {
    const surveyGroups = [];

    if (FLOW.projectControl.content.isLoaded) {
      FLOW.projectControl.get('content').forEach((item) => {
        surveyGroups.push({
          keyId: item.get('keyId'),
          parentId: item.get('parentId'),
          name: item.get('name'),
          published: item.get('published'),
          projectType: item.get('projectType'),
          monitoringGroup: item.get('monitoringGroup'),
          ancestorIds: item.get('ancestorIds'),
        });
      });
    }

    this.setState({ surveyGroups }, () => {
      this.setState({ levels: this.getLevels() });
    });
  }

  getLevels = (parentId = 0) => {
    const { surveyGroups } = this.state;
    const levels = [];

    if (parentId !== 0) {
      // eslint-disable-next-line eqeqeq
      const parent = surveyGroups.find(sg => sg.keyId == parentId);
      for (let i = 0; i < parent.ancestorIds.length; i++) {
        const level = [{
          keyId: 0,
          parentId: null,
          name: Ember.String.loc('_choose_folder_or_survey'),
          // eslint-disable-next-line eqeqeq
        }].concat(surveyGroups.filter(sgs => sgs.parentId == parent.ancestorIds[i])
          .sort(this.comparator));

        levels.push(level);
      }
    }

    levels.push([{
      keyId: 0,
      parentId: null,
      name: Ember.String.loc('_choose_folder_or_survey'),
    }]
      // eslint-disable-next-line eqeqeq
      .concat(surveyGroups.filter(sg => sg.parentId == parentId)
        .sort(this.comparator)));

    return levels;
  }

  comparator = (a, b) => {
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
  }

  handleChange = (e) => {
    // const { levels } = this.state;
    const parentId = e.target.value;

    // check if a survey has been selected
    // eslint-disable-next-line eqeqeq
    const selectedSG = FLOW.projectControl.get('content').find(sg => sg.get('keyId') == parentId);
    if (selectedSG && selectedSG.get('projectType') !== 'PROJECT_FOLDER') {
      FLOW.selectedControl.set('selectedSurveyGroup', selectedSG);
      return null;
    }

    const newLevels = this.getLevels(parentId);

    if (newLevels) {
      this.setState({ levels: newLevels });
    }

    return null;
  }

  renderForm = (folderSurveyList, id) => (
    <select data-testid={`folder-survey-select-${id}`} key={id} onChange={this.handleChange}>
      {folderSurveyList.map(surveyGroup => (
        <option key={surveyGroup.keyId} value={surveyGroup.keyId}>
          {surveyGroup.name}
        </option>
      ))}
    </select>
  )

  render() {
    const { levels } = this.state;

    return (
      <div>
        {/* foreach levels in array render a new select form */}
        {levels.map(this.renderForm)}
      </div>
    );
  }
}

FolderSurveySelector.propTypes = {};
