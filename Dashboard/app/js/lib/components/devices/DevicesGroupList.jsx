import React, { useContext } from 'react';
import DevicesTabContext from './devices-context';
import TABLE_NAMES from './constants';

export default function DevicesGroupList() {
  const {
    strings,
    onSortGroup,
    sortProperties,
    selectGroup,
    devicesGroup,
    tableHeaderClass,
    selectedDeviceGroupIds,
    setCurrentTable,
    onDeleteGroup,
    addNewGroup,
    toggleEditButton,
    selectedEditGroupId,
    renameGroup,
  } = useContext(DevicesTabContext);

  return (
    <>
      <div className="deviceControls">
        <button
          type="button"
          className="btnOutline"
          onClick={() => setCurrentTable(TABLE_NAMES.DEVICES)}
        >
          {strings.navText.manageDevices}
        </button>
        <nav className="dataTabMenu">
          <ul>
            <li>
              <button type="button" onClick={addNewGroup}>
                {strings.navText.newGroup}
              </button>
            </li>
          </ul>
        </nav>
      </div>

      {/* TABLE CONTENTS */}
      <table className="dataTable" id="surveyDataTable">
        <thead>
          <tr>
            <th className="noArrows" />
            <th
              id="device_table_header"
              className={sortProperties.column == 'code' ? tableHeaderClass() : ''}
              onClick={() => onSortGroup('code')}
              onKeyDown={() => onSortGroup('code')}
            >
              {strings.deviceGroup}
            </th>
            <th id="device_table_header" className="noArrows">
              {strings.action}
            </th>
          </tr>
        </thead>
        <tbody>
          {devicesGroup.map(group => {
            const selectedToEdit = selectedEditGroupId === group.keyId;
            return (
              <tr key={group.keyId}>
                <td className="selection">
                  <input
                    type="checkBox"
                    checked={selectedDeviceGroupIds.includes(group.keyId)}
                    onChange={() => selectGroup(group.keyId)}
                  />
                </td>
                <td className="deviceGroup">
                  <button
                    type="button"
                    name={selectedToEdit ? 'save' : 'edit'}
                    id={group.keyId}
                    onClick={toggleEditButton}
                    className={selectedToEdit ? `saveGroupName` : `editGroupName`}
                  >
                    {selectedToEdit ? `Save group name` : `Edit group name`}
                  </button>
                  <input
                    type="text"
                    id={group.keyId}
                    className="editGroupInput"
                    style={{ display: selectedToEdit ? 'block' : 'none' }}
                    defaultValue={group.code}
                    onChange={e => renameGroup({ id: group.keyId, value: e.target.value })}
                  />
                  <span style={{ display: selectedToEdit ? 'none' : 'block' }}>{group.code}</span>
                </td>
                <td>
                  <div
                    onClick={() => onDeleteGroup(group.keyId)}
                    onKeyDown={() => onDeleteGroup(group.keyId)}
                  >
                    {strings.delete}
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </>
  );
}
