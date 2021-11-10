import React, { useContext } from 'react';
import DevicesTabContext from '../devices-context';

export default function RemoveDialog() {
  const {
    strings,
    selectedDeviceIds,

    // Event handlers
    doRemoveFromGroup,
    cancelRemoveFromGroup,
    showRemoveFromGroupDialogBool,
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
                <button
                  type="button"
                  onClick={() => doRemoveFromGroup(selectedDeviceIds)}
                  className="ok smallBtn"
                >
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
