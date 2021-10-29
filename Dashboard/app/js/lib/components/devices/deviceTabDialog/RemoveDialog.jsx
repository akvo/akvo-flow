import React, { useContext } from 'react';
import PropTypes from 'prop-types';
import DevicesTabContext from '../devices-context';

export default function RemoveDialog() {
  const {
    showRemoveFromGroupDialogBool,
    cancelRemoveFromGroup,
    doRemoveFromGroup,
    strings,
  } = useContext(DevicesTabContext);

  return (
    <div className={showRemoveFromGroupDialogBool ? 'display overlay' : 'overlay'}>
      <div className="blanket" />
      <div className="dialogWrap">
        <div className="confirmDialog dialog">
          <h2>{strings.dialogText.warningText}</h2>
          <div className="buttons menuCentre">
            <ul>
              <li>
                <button type="button" onClick={doRemoveFromGroup} className="ok smallBtn">
                  {strings.dialogText.save}
                </button>
              </li>
              <li>
                <button type="button" onClick={cancelRemoveFromGroup} className="cancel">
                  {strings.dialogText.cancel}
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
