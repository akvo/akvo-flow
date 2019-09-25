import React from 'react';
import PropTypes from 'prop-types';

// eslint-disable-next-line import/no-unresolved
import Checkbox from 'akvo-flow/components/Checkbox';

export default class FormSelector extends React.Component {
  render() {
    const { forms } = this.props;

    return (
      <div>
        {Object.keys(forms).map(formId => (
          <div key={formId}>
            <Checkbox
              id={formId}
              name={formId}
              checked
            />
          </div>
        ))}
      </div>
    );
  }
}

FormSelector.propTypes = {
  forms: PropTypes.any.isRequired,
  // context: PropTypes.object.isRequired,
};

// export default contextConnect(Context, context => ({ forms: context.forms }))(FormSelector);
