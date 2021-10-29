/* eslint-disable react/sort-comp */
/* eslint-disable react/no-unused-state */
import React from 'react';
import PropTypes from 'prop-types';
import DevicesGroupList from './DevicesGroupList';
import DevicesList from './DevicesList';
import RemoveDialog from './deviceTabDialog/RemoveDialog';
import DevicesTabContext from './devices-context';
import TABLE_NAMES from './constants';
import AddToGroupDialog from './deviceTabDialog/AddToGroupDialog';

export default class DevicesTab extends React.Component {
  setCurrentTable = tableName => {
    this.setState({ currentTable: tableName });
  };

  tableHeaderClass = () => {
    if (this.props.sortProperties.ascending) {
      return 'sorting_asc';
    }
    return 'sorting_desc';
  };

  // DEVICES LIST

  selectDevice = (id, deviceIds) => {
    if (deviceIds.some(deviceId => id === deviceId)) {
      const filterDevice = deviceIds.filter(deviceId => id !== deviceId);
      return this.setState({ selectedDeviceIds: [...filterDevice] });
    }
    return this.setState({ selectedDeviceIds: [...deviceIds, id] });
  };

  showAddToGroupDialog = () => {
    this.setState({ showAddToGroupDialogBool: true });
  };

  // Get the property of a selected group
  dialogGroupSelectionChange = e => {
    const { code, keyId } = JSON.parse(e.target.value);
    this.setState({ dialogGroupSelection: { code, keyId } });
  };

  addDeviceToGroup = dev => {
    let devices = [];

    // Find all devices that have the same keyId as in the selectedDeviceIds
    for (let i = 0; i < dev.length; i++) {
      const filterDevices = this.state.devices.find(device => device.keyId === dev[i]);
      devices = [...devices, filterDevices];
    }

    // Adding group property to the selected devices
    devices.map(item => {
      item.deviceGroupName = this.state.dialogGroupSelection.code;
      item.deviceGroup = this.state.dialogGroupSelection.keyId;
      return null;
    });

    FLOW.store.commit();

    this.cancelAddToGroup();
  };

  cancelAddToGroup = () => {
    this.setState({ showAddToGroupDialogBool: false });
  };

  showRemoveFromGroupDialog = () => {
    this.setState({ showRemoveFromGroupDialogBool: true });
  };

  cancelRemoveFromGroup = () => {
    this.setState({ showRemoveFromGroupDialogBool: false });
  };

  doRemoveFromGroup = () => {
    this.cancelRemoveFromGroup();
  };

  // DEVICES GROUP LIST

  selectGroup = id => {
    this.setState(state => {
      if (state.selectedDeviceGroupIds.some(keyId => id === keyId)) {
        const filterGroup = state.selectedDeviceGroupIds.filter(keyId => id !== keyId);
        return { selectedDeviceGroupIds: [...filterGroup] };
      }
      return { selectedDeviceGroupIds: [...state.selectedDeviceGroupIds, id] };
    });
  };

  addNewGroup = () => {
    const newGroup = {
      code: `New group[${[this.state.devicesGroup.length - 1]}]`,
      keyId: Date.now(),
    };

    this.setState(state => ({
      devicesGroup: [...state.devicesGroup, newGroup],
    }));

    FLOW.store.createRecord(FLOW.DeviceGroup, {
      code: `New group[${[this.state.devicesGroup.length - 1]}]`,
      keyId: newGroup.keyId,
      id: newGroup.keyId,
    });
    FLOW.store.commit();
  };

  renameGroup = ({ id, value }) => {
    const findGroup = this.state.devicesGroup.find(group => group.keyId === id);
    const selectedDeviceGroupId = findGroup.keyId;

    // this could have been changed in the UI
    const originalSelectedDeviceGroup = FLOW.store.find(FLOW.DeviceGroup, selectedDeviceGroupId);
    originalSelectedDeviceGroup.set('code', value);
    findGroup.code = value;

    // Update the device group name in the devices list
    const allDevices = FLOW.store.filter(FLOW.Device, () => true);
    allDevices.forEach(item => {
      if (parseInt(item.get('deviceGroup'), 10) == selectedDeviceGroupId) {
        item.set('deviceGroupName', value);
      }
    });

    if (this.state.newDeviceGroupName.length !== 0) {
      FLOW.store.createRecord(FLOW.DeviceGroup, {
        code: this.state.newDeviceGroupName,
      });
    }
    FLOW.store.commit();
  };

  toggleEditButton = e => {
    const findButton = this.state.devicesGroup.find(group => group.keyId === Number(e.target.id));
    if (findButton) {
      if (findButton.keyId === this.state.selectedEditGroupId) {
        this.setState({ selectedEditGroupId: null });
      } else {
        this.setState({ selectedEditGroupId: findButton.keyId });
      }
    }
  };

  state = {
    devices: this.props.devices,
    currentTable: false,
    devicesGroup: this.props.devicesGroup.filter(value => Object.keys(value).length !== 0),
    selectedDeviceIds: [],
    selectedDeviceGroupIds: [],
    newDeviceGroupName: '',
    selectedEditGroupId: null,
    showAddToGroupDialogBool: null,
    selectedDevices: [],
    showRemoveFromGroupDialogBool: false,
    showRemoveFromGroupDialog: this.showRemoveFromGroupDialog,
    cancelRemoveFromGroup: this.cancelRemoveFromGroup,
    onSortDevices: this.props.onSortDevices,
    onSortGroup: this.props.onSortGroup,
    strings: this.props.strings,
    sortProperties: this.props.sortProperties,
    selectDevice: this.selectDevice,
    selectGroup: this.selectGroup,
    tableHeaderClass: this.tableHeaderClass,
    setCurrentTable: this.setCurrentTable,
    onDeleteGroup: this.props.onDeleteGroup,
    addNewGroup: this.addNewGroup,
    toggleEditButton: this.toggleEditButton,
    renameGroup: this.renameGroup,
    showAddToGroupDialog: this.showAddToGroupDialog,
    cancelAddToGroup: this.cancelAddToGroup,
    addDeviceToGroup: this.addDeviceToGroup,
    dialogGroupSelectionChange: this.dialogGroupSelectionChange,
    dialogGroupSelection: null,
    doRemoveFromGroup: this.doRemoveFromGroup,
  };

  render() {
    return (
      <DevicesTabContext.Provider value={this.state}>
        <section id="devicesList">
          {this.state.currentTable === TABLE_NAMES.DEVICES_GROUP ? (
            <DevicesGroupList />
          ) : (
            <DevicesList />
          )}
          <RemoveDialog />
          <AddToGroupDialog />
        </section>
      </DevicesTabContext.Provider>
    );
  }
}

DevicesTab.propTypes = {
  devices: PropTypes.array,
  devicesGroup: PropTypes.array,
  showRemoveFromGroupDialogBool: PropTypes.bool,
  showRemoveFromGroupDialog: PropTypes.func,
  cancelRemoveFromGroup: PropTypes.func,
  onSortDevices: PropTypes.func,
  strings: PropTypes.object,
  sortProperties: PropTypes.object,
  onSortGroup: PropTypes.func,
  onDeleteGroup: PropTypes.func,
};

DevicesTab.defaultProps = {
  devices: [],
  devicesGroup: [],
  showRemoveFromGroupDialogBool: false,
  showRemoveFromGroupDialog: () => null,
  cancelRemoveFromGroup: () => null,
  onSortDevices: () => null,
  onSortGroup: () => null,
  sortProperties: {},
  strings: {},
  onDeleteGroup: () => null,
};
