import React from 'react';
import PropTypes from 'prop-types';

// eslint-disable-next-line import/no-unresolved
// import Checkbox from 'akvo-flow/components/Checkbox';

export default class FormSelector extends React.Component {
  render() {
    const { forms, onCheck } = this.props;

    return (
      <div>
        {Object.keys(forms).map(formId => (
          <div key={formId}>
            <input
              type="checkbox"
              id={formId}
              name={formId}
              defaultChecked={forms[formId].checked}
              onChange={onCheck}
              className="displayInline"
            />

            <label htmlFor={formId}>
              {forms[formId].name}
            </label>
          </div>
        ))}
      </div>
    );
  }
}

FormSelector.propTypes = {
  forms: PropTypes.any.isRequired,
  onCheck: PropTypes.func.isRequired,
};
