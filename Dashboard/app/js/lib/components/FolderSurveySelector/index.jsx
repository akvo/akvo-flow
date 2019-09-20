import React from 'react';
import PropTypes from 'prop-types';

export default class FolderSurveySelector extends React.Component {
  state = {
    levels: this.props.levels,
  }

  handleChange = (e) => {
    // pass value back to ember to get new levels
    const { onChange } = this.props;
    const { levels } = this.state;

    const newLevels = onChange(e.target.value);

    if (newLevels) {
      this.setState({ levels: levels.concat(newLevels) });
    }
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

FolderSurveySelector.propTypes = {
  levels: PropTypes.array.isRequired,
  onChange: PropTypes.func.isRequired,
};
