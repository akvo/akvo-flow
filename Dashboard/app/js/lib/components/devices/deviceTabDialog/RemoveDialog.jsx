import React from 'react';
import PropTypes from 'prop-types';

export default function RemoveDialog({
  className,
  cancelRemoveFromGroup,
  warningText,
  doRemoveFromGroup,
}) {
  return (
    <div className={className}>
      <div className="blanket" />
      <div className="dialogWrap">
        <div className="confirmDialog dialog">
          <h2>{warningText}</h2>
          <div className="buttons menuCentre">
            <ul>
              <li>
                <button type="button" onClick={doRemoveFromGroup} className="ok smallBtn">
                  OK
                </button>
              </li>
              <li>
                <button type="button" onClick={cancelRemoveFromGroup} className="cancel">
                  Cancel
                </button>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}

RemoveDialog.propTypes = {
  className: PropTypes.string,
  cancelRemoveFromGroup: PropTypes.func,
  doRemoveFromGroup: PropTypes.func,
  warningText: PropTypes.string,
};

RemoveDialog.defaultProps = {
  className: '',
  doRemoveFromGroup: () => null,
  cancelRemoveFromGroup: () => null,
  warningText: '',
};
