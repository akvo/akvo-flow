import React, { useContext } from 'react';
import PropTypes from 'prop-types';
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
  } = useContext(DevicesTabContext);

  const allGroups = devicesGroup.filter(value => Object.keys(value).length !== 0);

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
              <button type="button" onClick={() => alert('add a group')}>
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
          {allGroups.map(group => (
            <tr key={group.keyId}>
              <td className="selection">
                <input
                  type="checkBox"
                  checked={selectedDeviceGroupIds.includes(group.keyId)}
                  onChange={() => selectGroup(group.keyId)}
                />
              </td>
              <td className="deviceGroup">{group.code}</td>
              <td>
                <div
                  onClick={() => alert(`${group.code} is deleted`)}
                  onKeyDown={() => alert(`${group.code} is deleted`)}
                >
                  {strings.delete}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}

DevicesGroupList.propTypes = {
  selectDevice: PropTypes.func,
  setSwitchTable: PropTypes.func,
};

DevicesGroupList.defaultProps = {
  selectDevice: () => null,
  setSwitchTable: () => null,
};
