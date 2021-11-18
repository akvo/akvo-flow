import React from 'react';
import PropTypes from 'prop-types';
import DevicesGroupList from './DevicesGroupList';
import DevicesList from './DevicesList';
import RemoveDialog from './deviceTabDialog/RemoveDialog';
import DevicesTabContext from './devices-context';
import TABLE_NAMES from './constants';
import AddToGroupDialog from './deviceTabDialog/AddToGroupDialog';
import DeleteGroupDialog from './deviceTabDialog/DeleteGroupDialog';

export default class DevicesTab extends React.Component {
  state = {
    currentTable: TABLE_NAMES.DEVICES,
    devices: this.props.devices,
    devicesGroup: this.props.devicesGroup,
    sortAscending: null,
    selectedColumn: null,
    selectedDeviceIds: [],
    groupToEditButtonId: null,
    dialogGroupSelection: null,
    showAddToGroupDialogBool: null,
    showRemoveFromGroupDialogBool: false,
    groupToEditId: null,
    inputEditGroupValue: null,
  };

  setCurrentTable = tableName => {
    this.setState({ currentTable: tableName });

    // Input value is reseted when switched into Manage devices
    this.setState({ groupToEditButtonId: null });
    this.setState({ inputEditGroupValue: null });
  };

  // DEVICES LIST
  selectDevice = (changedDeviceId, selectedDeviceIds) => {
    if (selectedDeviceIds.includes(changedDeviceId)) {
      const filterDevice = selectedDeviceIds.filter(deviceId => changedDeviceId !== deviceId);
      return this.setState({ selectedDeviceIds: [...filterDevice] });
    }
    return this.setState({ selectedDeviceIds: [...selectedDeviceIds, changedDeviceId] });
  };

  // Get the property of a selected group
  dialogGroupSelectionChange = event => {
    const { code, keyId } = event.target.value.length !== 0 && JSON.parse(event.target.value);
    this.setState({ dialogGroupSelection: { code, keyId } });
  };

  addDeviceToGroup = selectedDeviceIds => {
    const devicesInGroup = FLOW.store.filter(
      FLOW.Device,
      device => selectedDeviceIds.includes(device.get('keyId')) && device
    );

    devicesInGroup.forEach(device => {
      device.set('deviceGroupName', this.state.dialogGroupSelection.code);
      device.set('deviceGroupId', this.state.dialogGroupSelection.keyId);
    });

    this.state.devices.map(device => {
      if (selectedDeviceIds.includes(device.keyId)) {
        device.deviceGroupName = this.state.dialogGroupSelection.code;
        device.deviceGroupId = this.state.dialogGroupSelection.keyId;
      }
      return device;
    });

    FLOW.store.commit();

    // Removes selection
    this.setState({ selectedDeviceIds: [] });

    this.cancelAddToGroup();
  };

  showAddToGroupDialog = () => {
    this.setState({ showAddToGroupDialogBool: true });
  };

  cancelAddToGroup = () => {
    // Reset dropdown
    const select = document.querySelector('#select-group');
    select.value = '';

    this.setState({ dialogGroupSelection: null });
    this.setState({ showAddToGroupDialogBool: false });
  };

  showRemoveFromGroupDialog = () => {
    this.setState({ showRemoveFromGroupDialogBool: true });
  };

  cancelRemoveFromGroup = () => {
    this.setState({ showRemoveFromGroupDialogBool: false });
  };

  removeDeviceFromGroup = selectedDeviceIds => {
    const devicesInGroup = FLOW.store.filter(
      FLOW.Device,
      device => selectedDeviceIds.includes(device.get('keyId')) && device
    );

    devicesInGroup.forEach(device => {
      device.set('deviceGroupName', null);
      device.set('deviceGroupId', null);
    });

    this.state.devices.map(device => {
      if (selectedDeviceIds.includes(device.keyId)) {
        device.deviceGroupName = null;
        device.deviceGroupId = null;
      }
      return device;
    });

    FLOW.store.commit();

    // Removes selection
    this.setState({ selectedDeviceIds: [] });

    this.cancelRemoveFromGroup();
  };

  // Block and unblock a device
  blockDevice = deviceId => {
    const findDevice = this.state.devices.find(device => device.keyId === deviceId);
    if (findDevice.keyId === deviceId) {
      findDevice.isBlocked = !findDevice.isBlocked;

      // Remove the selection
      this.setState(state => ({
        selectedDeviceIds: state.selectedDeviceIds.filter(
          selectedDeviceId => selectedDeviceId !== findDevice.keyId
        ),
      }));
    }
  };

  // DEVICES GROUP LIST
  addNewGroup = () => {
    FLOW.store.createRecord(FLOW.DeviceGroup, {
      code: this.props.strings.newGroup,
    });

    FLOW.store.commit();

    // Use timeout to ensure that keyId is set
    setTimeout(() => {
      this.setState({
        devicesGroup: FLOW.deviceGroupControl
          .get('content')
          .getEach('_data')
          .getEach('attributes')
          .filter(value => Object.keys(value).length !== 0),
      });
    }, 500);
  };

  getGroupNewName = ({ id, value }) => {
    this.setState({ inputEditGroupValue: value });
    this.setState({ groupToEditId: id });
  };

  saveNewName = () => {
    // Update for the database
    if (this.state.inputEditGroupValue !== null) {
      const originalSelectedDeviceGroup = FLOW.store.find(
        FLOW.DeviceGroup,
        this.state.groupToEditId
      );

      originalSelectedDeviceGroup.set('code', this.state.inputEditGroupValue);

      // Update the device group name in the devices list
      const allDevices = FLOW.store.filter(FLOW.Device, () => true);

      allDevices.forEach(device => {
        if (device.get('deviceGroupId') === this.state.groupToEditId) {
          device.set('deviceGroupName', this.state.inputEditGroupValue);
        }
      });

      // Using setTimeout to make sure that the new name is displayed in the UI
      setTimeout(() => {
        this.setState({
          devicesGroup: FLOW.deviceGroupControl
            .get('content')
            .getEach('_data')
            .getEach('attributes')
            .filter(value => Object.keys(value).length !== 0),
        });
      }, 500);
      FLOW.store.commit();
    }
  };

  toggleEditButton = e => {
    const findButton = this.state.devicesGroup.find(group => group.keyId === Number(e.target.id));
    if (findButton) {
      if (findButton.keyId === this.state.groupToEditButtonId) {
        this.setState({ groupToEditButtonId: null });
      } else {
        this.setState({ groupToEditButtonId: findButton.keyId });
      }
      this.saveNewName();
    }
  };

  onDeleteGroup = groupId => {
    this.setState({ isShowDeleteDialog: true });
    this.setState({ groupToDeleteId: groupId });
  };

  cancelDeletingGroup = () => {
    this.setState({ isShowDeleteDialog: false });
  };

  deleteGroupConfirm = () => {
    const devicesGroup = FLOW.store.find(FLOW.DeviceGroup, this.state.groupToDeleteId);

    this.state.devices.map(device => {
      if (this.state.groupToDeleteId === device.deviceGroupId) {
        device.deviceGroupName = null;
        device.deviceGroupId = null;
      }
      return device;
    });

    devicesGroup.deleteRecord();

    FLOW.store.commit();

    this.setState({
      devicesGroup: FLOW.deviceGroupControl
        .get('content')
        .getEach('_data')
        .getEach('attributes')
        .filter(value => Object.keys(value).length !== 0),
    });

    this.cancelDeletingGroup();
  };

  // SORTING
  tableHeaderClass = () => {
    if (this.state.sortAscending) {
      return 'sorting_asc';
    }
    return 'sorting_desc';
  };

  sortTableItem = item => {
    this.setState(state => ({ sortAscending: !state.sortAscending }));
    this.setState({ selectedColumn: item });

    const itemToSort =
      this.state.currentTable === TABLE_NAMES.DEVICES_GROUP
        ? this.state.devicesGroup
        : this.state.devices;

    return itemToSort.sort((a, b) => {
      if (this.state.sortAscending) {
        if (a[this.state.selectedColumn] < b[this.state.selectedColumn]) {
          return -1;
        }
        if (a[this.state.selectedColumn] > b[this.state.selectedColumn]) {
          return 1;
        }
      } else {
        if (b[this.state.selectedColumn] < a[this.state.selectedColumn]) {
          return -1;
        }
        if (b[this.state.selectedColumn] > a[this.state.selectedColumn]) {
          return 1;
        }
      }
      return 0;
    });
  };

  render() {
    const contextData = {
      currentTable: this.state.currentTable,
      strings: this.props.strings,
      devices: this.state.devices,
      devicesGroup: this.state.devicesGroup,
      sortProperties: {
        column: this.state.selectedColumn,
        ascending: this.state.sortAscending,
      },
      selectedDeviceIds: this.state.selectedDeviceIds,
      groupToEditButtonId: this.state.groupToEditButtonId,
      groupToDeleteId: this.state.groupToDeleteId,
      dialogGroupSelection: this.state.dialogGroupSelection,
      showAddToGroupDialogBool: this.state.showAddToGroupDialogBool,
      isShowDeleteDialog: this.state.isShowDeleteDialog,
      showRemoveFromGroupDialogBool: this.state.showRemoveFromGroupDialogBool,

      // Functions
      tableHeaderClass: this.tableHeaderClass,

      // Event handlers
      setCurrentTable: this.setCurrentTable,
      cancelDeletingGroup: this.cancelDeletingGroup,
      showRemoveFromGroupDialog: this.showRemoveFromGroupDialog,
      cancelRemoveFromGroup: this.cancelRemoveFromGroup,
      selectDevice: this.selectDevice,
      deleteGroupConfirm: this.deleteGroupConfirm,
      onDeleteGroup: this.onDeleteGroup,
      addNewGroup: this.addNewGroup,
      toggleEditButton: this.toggleEditButton,
      getGroupNewName: this.getGroupNewName,
      showAddToGroupDialog: this.showAddToGroupDialog,
      cancelAddToGroup: this.cancelAddToGroup,
      addDeviceToGroup: this.addDeviceToGroup,
      dialogGroupSelectionChange: this.dialogGroupSelectionChange,
      removeDeviceFromGroup: this.removeDeviceFromGroup,
      blockDevice: this.blockDevice,
      sortTableItem: this.sortTableItem,
    };

    return (
      <DevicesTabContext.Provider value={contextData}>
        <section id="devicesList">
          {this.state.currentTable === TABLE_NAMES.DEVICES_GROUP ? (
            <DevicesGroupList />
          ) : (
            <DevicesList />
          )}
          <RemoveDialog />
          <AddToGroupDialog />
          <DeleteGroupDialog />
        </section>
      </DevicesTabContext.Provider>
    );
  }
}

DevicesTab.propTypes = {
  devices: PropTypes.array,
  devicesGroup: PropTypes.array,
  showRemoveFromGroupDialogBool: PropTypes.bool,
  strings: PropTypes.object,
  sortProperties: PropTypes.object,
  showRemoveFromGroupDialog: PropTypes.func,
  cancelRemoveFromGroup: PropTypes.func,
  sortTableItem: PropTypes.func,
};

DevicesTab.defaultProps = {
  devices: [],
  devicesGroup: [],
  showRemoveFromGroupDialogBool: false,
  sortProperties: {},
  strings: {},
  showRemoveFromGroupDialog: () => null,
  cancelRemoveFromGroup: () => null,
  sortTableItem: () => null,
};
