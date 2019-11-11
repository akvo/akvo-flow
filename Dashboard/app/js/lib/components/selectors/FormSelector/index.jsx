/* eslint-disable import/no-unresolved */
import React from 'react';
import PropTypes from 'prop-types';
import Switch from 'akvo-flow/components/reusable/SwitchBox';

export default class FormSelector extends React.Component {
  render() {
    const { forms, onCheck } = this.props;

    return (
      <div>
        {Object.keys(forms).map(formId => (
          <div className="form" key={formId}>
            <label htmlFor={formId}>{forms[formId].name}</label>

            <Switch
              id={formId}
              name={formId}
              checked={forms[formId].checked}
              onChange={onCheck}
            />
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
