import React from 'react';
import PropTypes from 'prop-types';

export default class FolderSurveySelector extends React.Component {
  state = {
    levels: [],
  };

  componentDidMount() {
    const { initialSurveyGroup, surveyGroups } = this.props;

    if (initialSurveyGroup) {
      // if initial form is available, generate levels and mark it as selected
      const { parentId } = surveyGroups.find(sg => sg.keyId == initialSurveyGroup);

      this.setState({ levels: this.getLevels(parentId) }, () => {
        this.props.onSelectSurvey(initialSurveyGroup);
      });
    } else {
      this.setState({ levels: this.getLevels(0) });
    }
  }

  getLevels = (parentId = 0) => {
    const { surveyGroups, strings, initialSurveyGroup } = this.props;
    const initialSurvey = surveyGroups.find(sg => sg.keyId == initialSurveyGroup);
    const levels = [];

    // eslint-disable-next-line eqeqeq
    if (parentId != 0) {
      // eslint-disable-next-line eqeqeq
      const parent = surveyGroups.find(sg => sg.keyId == parentId);

      for (let i = 0; i < parent.ancestorIds.length; i++) {
        const parentSurveys = surveyGroups
          .reduce((total, sgs) => {
            // eslint-disable-next-line eqeqeq
            if (sgs.parentId == parent.ancestorIds[i]) {
              return total.concat({
                ...sgs,
                selected: initialSurvey && initialSurvey.ancestorIds.includes(sgs.keyId),
              });
            }

            return total;
          }, [])
          .sort(this.comparator);

        const level = [
          {
            keyId: 0,
            parentId: null,
            name: strings.chooseFolderOrSurvey,
          },
        ].concat(parentSurveys);

        levels.push(level);
      }
    }

    const surveys = surveyGroups
      .reduce((total, sgs) => {
        // eslint-disable-next-line eqeqeq
        if (sgs.parentId == parentId) {
          return total.concat({
            ...sgs,
            selected: initialSurvey && initialSurvey.keyId == sgs.keyId,
          });
        }

        return total;
      }, [])
      .sort(this.comparator);

    levels.push(
      [
        {
          keyId: 0,
          parentId: null,
          name: strings.chooseFolderOrSurvey,
        },
      ]
        // eslint-disable-next-line eqeqeq
        .concat(surveys)
    );

    return levels;
  };

  comparator = (a, b) => {
    const nameA = (a.name || '').toUpperCase(); // ignore upper and lowercase
    const nameB = (b.name || '').toUpperCase(); // ignore upper and lowercase

    if (nameA < nameB) {
      return -1;
    }
    if (nameA > nameB) {
      return 1;
    }

    // names must be equal
    return 0;
  };

  handleChange = e => {
    const parentId = e.target.value;

    // check if a survey has been selected
    // eslint-disable-next-line eqeqeq
    if (!this.props.onSelectSurvey(parentId)) {
      return null;
    }

    const newLevels = this.getLevels(parentId);

    if (newLevels) {
      this.setState({ levels: newLevels });
    }

    return null;
  };

  renderForm = (folderSurveyList, id) => {
    const defaultSurveyGroup = folderSurveyList.find(sgs => sgs.selected === true);

    return (
      <select
        defaultValue={defaultSurveyGroup ? defaultSurveyGroup.keyId : undefined}
        data-testid={`folder-survey-select-${id}`}
        key={id}
        onChange={this.handleChange}
      >
        {folderSurveyList.map(surveyGroup => (
          <option key={surveyGroup.keyId} value={surveyGroup.keyId}>
            {surveyGroup.name}
          </option>
        ))}
      </select>
    );
  };

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

FolderSurveySelector.propTypes = {
  surveyGroups: PropTypes.array.isRequired,
  onSelectSurvey: PropTypes.func.isRequired,
  strings: PropTypes.object.isRequired,
  initialSurveyGroup: PropTypes.any, // eslint-disable-line react/require-default-props
};
