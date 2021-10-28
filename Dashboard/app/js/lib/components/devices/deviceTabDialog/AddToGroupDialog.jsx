import React, { useContext } from 'react';
import DevicesTabContext from '../devices-context';

export default function AddToGroupDialog() {
  const { strings, devicesGroup, showAddToGroupDialogBool, cancelAddToGroup } = useContext(
    DevicesTabContext
  );
  return (
    <div className={showAddToGroupDialogBool ? `display overlay` : `overlay`}>
      <div className="blanket" />
      <div className="dialogWrap">
        <div className="confirmDialog dialog">
          <h2>{strings.dialogText.addDeviceToGroup}</h2>
          <p className="dialogMsg">{strings.dialogText.chooseGroup}</p>
          <br />
          <select>
            <option value={strings.dialogText.selectGroupText}>
              {strings.dialogText.selectGroupText}
            </option>
            {devicesGroup.map(group => (
              <option key={group.keyId} value={group.code}>
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
                <button type="button" onClick={() => null} className="ok smallBtn">
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
