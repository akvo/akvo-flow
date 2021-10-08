import React from 'react';
import PropTypes from 'prop-types';

export default function RemoveDialog({
  className,
  cancelRemoveFromGroup,
  warningText,
  doRemoveFromGroup,
}) {
  return (
    <div
      className={className}
      // {{bindAttr className="view.showRemoveFromGroupDialogBool:display :overlay"}}
    >
      <div className="blanket" />
      <div className="dialogWrap">
        <div className="confirmDialog dialog">
          <h2>
            {/* {{t _remove_devices_from_device_group}} */}
            {warningText}
          </h2>
          <div className="buttons menuCentre">
            <ul>
              <li>
                <button
                  type="button"
                  // {{action "doRemoveFromGroup" target="this"}}
                  onClick={doRemoveFromGroup}
                  className="ok smallBtn"
                >
                  OK
                  {/* {{t _ok}} */}
                </button>
              </li>
              <li>
                <button
                  type="button"
                  onClick={cancelRemoveFromGroup}
                  // {{action "cancelRemoveFromGroup" target="this"}}
                  className="cancel"
                >
                  Cancel
                  {/* {{t _cancel}} */}
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
