import React, { useContext } from 'react';
import DevicesTabContext from '../devices-context';

export default function AddToGroupDialog() {
  const {
    strings,
    devicesGroup,
    showAddToGroupDialogBool,
    cancelAddToGroup,
    addDeviceToGroup,
    dialogGroupSelectionChange,
    selectedDeviceIds,
  } = useContext(DevicesTabContext);

  return (
    <div className={showAddToGroupDialogBool ? `display overlay` : `overlay`}>
      <div className="blanket" />
      <div className="dialogWrap">
        <div className="confirmDialog dialog">
          <h2>{strings.dialogText.addDeviceToGroup}</h2>
          <p className="dialogMsg">{strings.dialogText.chooseGroup}</p>
          <br />
          <select onChange={dialogGroupSelectionChange}>
            <option value={JSON.stringify('')}>{strings.dialogText.selectGroupText}</option>
            {devicesGroup.map(group => (
              <option key={group.keyId} id={group.keyId} value={JSON.stringify(group)}>
                {group.code}
              </option>
            ))}
          </select>
          {/* {{view Ember.Select
              contentBinding="FLOW.deviceGroupControl.contentNoUnassigned"
              selectionBinding="view.selectedDeviceGroup"
              optionLabelPath="content.code"
              optionValuePath="content.keyId"
              prompt=""
              promptBinding="Ember.STRINGS._select_existing_device_group"}} */}
          <div className="buttons menuCentre">
            <ul>
              <li>
                <button
                  type="button"
                  onClick={() => addDeviceToGroup(selectedDeviceIds)}
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
