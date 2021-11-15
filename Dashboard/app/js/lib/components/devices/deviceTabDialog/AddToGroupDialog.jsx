import React, { useContext } from 'react';
import DevicesTabContext from '../devices-context';

export default function AddToGroupDialog() {
  const {
    strings,
    devicesGroup,
    selectedDeviceIds,
    dialogGroupSelection,

    // Event handlers
    showAddToGroupDialogBool,
    cancelAddToGroup,
    addDeviceToGroup,
    dialogGroupSelectionChange,
  } = useContext(DevicesTabContext);

  const buttonCondition =
    dialogGroupSelection !== null &&
    dialogGroupSelection.code !== undefined &&
    dialogGroupSelection.keyId !== undefined &&
    (dialogGroupSelection !== null ||
      dialogGroupSelection.code !== undefined ||
      dialogGroupSelection.keyId !== undefined);

  return (
    <div className={showAddToGroupDialogBool ? `display overlay` : `overlay`}>
      <div className="blanket" />
      <div className="dialogWrap">
        <div className="confirmDialog dialog">
          <h2>{strings.dialogText.addDeviceToGroup}</h2>
          <p className="dialogMsg">{strings.dialogText.chooseGroup}</p>
          <br />
          <select id="select-group" onChange={dialogGroupSelectionChange}>
            <option value="">{strings.dialogText.selectGroupText}</option>
            {devicesGroup.map(group => (
              <option key={group.keyId} id={group.keyId} value={JSON.stringify(group)}>
                {group.code}
              </option>
            ))}
          </select>
          <div className="buttons menuCentre">
            <ul>
              <li>
                <button
                  type="button"
                  onClick={() =>
                    buttonCondition ? addDeviceToGroup(selectedDeviceIds) : cancelAddToGroup()
                  }
                  className="ok smallBtn"
                >
                  {strings.dialogText.save}
                </button>
              </li>
              <li>
                <button type="button" onClick={cancelAddToGroup} className="cancel">
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
