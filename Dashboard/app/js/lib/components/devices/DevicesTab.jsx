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
    currentTable: false,
    devices: this.props.devices,
    devicesGroup: this.props.devicesGroup,
    sortAscending: false,
    selectedColumn: null,
    selectedDeviceIds: [],
    selectedDevices: [],
    selectedEditGroupId: null,
    dialogGroupSelection: null,
    showAddToGroupDialogBool: null,
    showRemoveFromGroupDialogBool: false,
    groupToEditId: null,
    newName: null,
  };

  componentDidMount() {
    if (this.state.devicesGroup.length === 0) {
      setTimeout(() => {
        this.setState({
          devicesGroup: FLOW.deviceGroupControl
            .get('content')
            .getEach('_data')
            .getEach('attributes')
            .filter(value => Object.keys(value).length !== 0),
        });
      }, 500);
    }
  }

  setCurrentTable = tableName => {
    this.setState({ currentTable: tableName });
    this.setState({ selectedEditGroupId: null });
    this.setState({ newName: null });
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
  dialogGroupSelectionChange = e => {
    const { code, keyId } = e.target.value.length !== 0 && JSON.parse(e.target.value);
    this.setState({ dialogGroupSelection: { code, keyId } });
  };

  addDeviceToGroup = dev => {
    let devices = [];

    // Find all devices that have the same keyId as in the selectedDeviceIds
    for (let i = 0; i < dev.length; i++) {
      const filterDevices = this.state.devices.find(device => device.keyId === dev[i]);
      devices = [...devices, filterDevices];
      const devicesInGroup = FLOW.store.filter(FLOW.Device, item => item.get('keyId') === dev[i]);
      devicesInGroup.forEach(item => {
        item.set('deviceGroupName', this.state.dialogGroupSelection.code);
        item.set('deviceGroupId', this.state.dialogGroupSelection.keyId);
      });
    }

    // Adding group property to the selected devices
    devices.forEach(item => {
      item.deviceGroupName = this.state.dialogGroupSelection.code;
      item.deviceGroupId = this.state.dialogGroupSelection.keyId;
    });

    // Find all devices that have the same keyId as in the selectedDeviceIds
    for (let i = 0; i < dev.length; i++) {
      const filterDevices = this.state.devices.find(device => device.keyId === dev[i]);
      devices = [...devices, filterDevices];
      const devicesInGroup = FLOW.store.filter(FLOW.Device, item => item.get('keyId') === dev[i]);

      devicesInGroup.forEach(item => {
        item.set('deviceGroupName', this.state.dialogGroupSelection.code);
        item.set('deviceGroup', this.state.dialogGroupSelection.keyId);
      });
    }

    // Adding group property to the selected devices
    devices.map(item => {
      item.deviceGroupName = this.state.dialogGroupSelection.code;
      item.deviceGroup = this.state.dialogGroupSelection.keyId;
      return item;
    });

    FLOW.store.commit();

    this.cancelAddToGroup();
  };

  showAddToGroupDialog = () => {
    this.setState({ showAddToGroupDialogBool: true });
  };

  cancelAddToGroup = () => {
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

  doRemoveFromGroup = dev => {
    let devices = [];

    // Find all devices that have the same keyId as in the selectedDeviceIds
    for (let i = 0; i < dev.length; i++) {
      const filterDevices = this.state.devices.find(device => device.keyId === dev[i]);
      devices = [...devices, filterDevices];
      const devicesInGroup = FLOW.store.filter(FLOW.Device, item => item.get('keyId') === dev[i]);

      devicesInGroup.forEach(item => {
        item.set('deviceGroupName', null);
        item.set('deviceGroupId', null);
      });
    }

    devices.forEach(item => {
      item.deviceGroupName = null;
      item.deviceGroupId = null;
      return item;
    });

    FLOW.store.commit();
    this.cancelRemoveFromGroup();
  };

  // Block and unblock a device
  blockDevice = id => {
    const findDevice = this.state.devices.find(device => device.keyId === id);
    if (findDevice.keyId === id) {
      findDevice.isBlocked = !findDevice.isBlocked;
      this.setState(state => ({ devices: state.devices }));
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
    this.setState({ newName: value });
    this.setState({ groupToEditId: id });
  };

  saveNewName = () => {
    // Update for the database
    if (this.state.newName !== null) {
      const originalSelectedDeviceGroup = FLOW.store.find(
        FLOW.DeviceGroup,
        this.state.groupToEditId
      );

      originalSelectedDeviceGroup.set('code', this.state.newName);

      // Update the device group name in the devices list
      const allDevices = FLOW.store.filter(FLOW.Device, () => true);

      allDevices.forEach(item => {
        if (item.get('deviceGroupId') === this.state.groupToEditId) {
          item.set('deviceGroupName', this.state.newName);
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
      if (findButton.keyId === this.state.selectedEditGroupId) {
        this.setState({ selectedEditGroupId: null });
      } else {
        this.setState({ selectedEditGroupId: findButton.keyId });
      }
      this.saveNewName();
    }
    return this.setState({ devicesToBlock: [...deviceIds, id] });
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

    const filterDevices = this.state.devices.filter(
      device => Number(device.deviceGroup) === this.state.groupToDeleteId
    );

    filterDevices.forEach(item => {
      item.deviceGroupName = null;
      item.deviceGroup = null;
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

  // Sort the groups
  sortGroup = item => {
    this.setState(state => ({ sortAscending: !state.sortAscending }));
    this.setState({ selectedColumn: item });
    return this.state.devicesGroup.sort((a, b) => {
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

  // Sort the devices
  sortDevices = item => {
    this.setState(state => ({ sortAscending: !state.sortAscending }));
    this.setState({ selectedColumn: item });

    return this.state.devices.sort((a, b) => {
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

  addNewGroup = () => {
    FLOW.store.createRecord(FLOW.DeviceGroup, {
      code: 'New group',
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

  renameGroup = ({ id, value }) => {
    // this could have been changed in the UI
    const originalSelectedDeviceGroup = FLOW.store.find(FLOW.DeviceGroup, id);

  saveNewName = () => {
    // Update for the database
    if (this.state.newName !== null) {
      const originalSelectedDeviceGroup = FLOW.store.find(
        FLOW.DeviceGroup,
        this.state.groupToEditId
      );

      originalSelectedDeviceGroup.set('code', this.state.newName);

      // Update the device group name in the devices list
      const allDevices = FLOW.store.filter(FLOW.Device, () => true);

      allDevices.forEach(item => {
        if (parseInt(item.get('deviceGroup'), 10) === this.state.groupToEditId) {
          item.set('deviceGroupName', this.state.newName);
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
      if (findButton.keyId === this.state.selectedEditGroupId) {
        this.setState({ selectedEditGroupId: null });
      } else {
        this.setState({ selectedEditGroupId: findButton.keyId });
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

    const filterDevices = this.state.devices.filter(
      device => device.deviceGroupId === this.state.groupToDeleteId
    );

    filterDevices.forEach(item => {
      item.deviceGroupName = null;
      item.deviceGroupId = null;
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

  // Sort the groups
  sortGroup = item => {
    this.setState(state => ({ sortAscending: !state.sortAscending }));
    this.setState({ selectedColumn: item });
    return this.state.devicesGroup.sort((a, b) => {
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

  // Sort the devices
  sortDevices = item => {
    this.setState(state => ({ sortAscending: !state.sortAscending }));
    this.setState({ selectedColumn: item });

    return this.state.devices.sort((a, b) => {
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
      selectedDevices: this.state.selectedDevices,
      selectedEditGroupId: this.state.selectedEditGroupId,
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
      doRemoveFromGroup: this.doRemoveFromGroup,
      blockDevice: this.blockDevice,
      onSortDevices: this.sortDevices,
      onSortGroup: this.sortGroup,
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
  showRemoveFromGroupDialog: PropTypes.func,
  cancelRemoveFromGroup: PropTypes.func,
  onSortDevices: PropTypes.func,
  strings: PropTypes.object,
  sortProperties: PropTypes.object,
  onSortGroup: PropTypes.func,
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
  selectedDeviceGroup: null,
};
