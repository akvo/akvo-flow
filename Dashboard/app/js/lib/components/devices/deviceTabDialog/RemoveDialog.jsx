import React, { useContext } from 'react';
import PropTypes from 'prop-types';

import DevicesTabContext from '../device-context';

export default function RemoveDialog({ warningText, doRemoveFromGroup }) {
  const { showRemoveFromGroupDialogBool, cancelRemoveFromGroup } = useContext(DevicesTabContext);

  return (
    <div className={showRemoveFromGroupDialogBool ? `display overlay` : `overlay`}>
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
