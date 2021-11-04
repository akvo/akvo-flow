import React from 'react';
import PropTypes from 'prop-types';
import DevicesGroupList from './DevicesGroupList';
import DevicesList from './DevicesList';
import RemoveDialog from './deviceTabDialog/RemoveDialog';
import DevicesTabContext from './devices-context';
import TABLE_NAMES from './constants';
import AddToGroupDialog from './deviceTabDialog/AddToGroupDialog';
import DeleteGroup from './deviceTabDialog/DeleteGroupDialog';

export default class DevicesTab extends React.Component {
  state = {
    groupToDeleteId: null,
    isShowDeleteDialog: false,
    devices: this.props.devices,
    devicesGroup: this.props.devicesGroup,
    blockedDevice: null,
    selectedDeviceIds: [],
    selectedDevices: [],
    newDeviceGroupName: '',
    currentTable: false,
    selectedEditGroupId: null,
    showAddToGroupDialogBool: null,
    dialogGroupSelection: null,
    showRemoveFromGroupDialogBool: false,
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

  // ADD TO GROUP DIALOG
  showAddToGroupDialog = () => {
    this.setState({ showAddToGroupDialogBool: true });
  };

  // Get the property of a selected group
  dialogGroupSelectionChange = e => {
    const { code, keyId } = e.target.value.length !== 0 && JSON.parse(e.target.value);
    this.setState({ dialogGroupSelection: { code, keyId } });
  };

  addDeviceToGroup = dev => {
    if (
      this.state.dialogGroupSelection.code !== undefined &&
      this.state.dialogGroupSelection.keyId !== undefined
    ) {
      let devices = [];

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
    }
    this.cancelAddToGroup();
  };

  // REMOVE FROM GROUP DIALOG
  cancelAddToGroup = () => {
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
      const devicesInGroup = FLOW.store.filter(FLOW.Device, item => item.get('keyId') == dev[i]);

      devicesInGroup.forEach(item => {
        item.set('deviceGroupName', null);
        item.set('deviceGroup', null);
      });
    }

    devices.forEach(item => {
      item.deviceGroupName = null;
      item.deviceGroup = null;
      return item;
    });

    FLOW.store.commit();
    this.cancelRemoveFromGroup();
  };

  // TODO block and unblock devices
  setIsblocked = e => {
    const findDevice = this.state.devices.find(device => device.keyId === Number(e.target.id));
    if (findDevice) {
      if (findDevice.keyId === this.state.blockedDevice) {
        this.setState({ blockedDevice: null });
      } else {
        this.setState({ blockedDevice: findDevice.keyId });
      }
    }
  };

  // DEVICES GROUP LIST
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

    originalSelectedDeviceGroup.set('code', value);

    // Update the device group name in the devices list
    const allDevices = FLOW.store.filter(FLOW.Device, () => true);

    allDevices.forEach(item => {
      if (parseInt(item.get('deviceGroup'), 10) == id) {
        item.set('deviceGroupName', value);
      }
    });

    FLOW.store.commit();

    this.setState({
      devicesGroup: FLOW.deviceGroupControl
        .get('content')
        .getEach('_data')
        .getEach('attributes')
        .filter(group => Object.keys(group).length !== 0),
    });
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

  deleteGroup = groupId => {
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

  render() {
    const contextData = {
      groupToDeleteId: this.state.groupToDeleteId,
      isShowDeleteDialog: this.state.isShowDeleteDialog,
      devices: this.state.devices,
      devicesGroup: this.state.devicesGroup,
      onSortDevices: this.props.onSortDevices,
      onSortGroup: this.props.onSortGroup,
      sortProperties: this.props.sortProperties,
      strings: this.props.strings,
      selectedDeviceIds: this.state.selectedDeviceIds,
      selectedDevices: this.state.selectedDevices,
      newDeviceGroupName: this.state.newDeviceGroupName,
      currentTable: this.state.currentTable,
      selectedEditGroupId: this.state.selectedEditGroupId,
      showAddToGroupDialogBool: this.state.showAddToGroupDialogBool,
      dialogGroupSelection: this.state.dialogGroupSelection,
      showRemoveFromGroupDialogBool: this.state.showRemoveFromGroupDialogBool,
      blockedDevice: this.state.blockedDevice,
      cancelDeletingGroup: this.cancelDeletingGroup,
      showRemoveFromGroupDialog: this.showRemoveFromGroupDialog,
      cancelRemoveFromGroup: this.cancelRemoveFromGroup,
      selectDevice: this.selectDevice,
      tableHeaderClass: this.tableHeaderClass,
      setCurrentTable: this.setCurrentTable,
      deleteGroupConfirm: this.deleteGroupConfirm,
      onDeleteGroup: this.deleteGroup,
      addNewGroup: this.addNewGroup,
      toggleEditButton: this.toggleEditButton,
      renameGroup: this.renameGroup,
      showAddToGroupDialog: this.showAddToGroupDialog,
      cancelAddToGroup: this.cancelAddToGroup,
      addDeviceToGroup: this.addDeviceToGroup,
      dialogGroupSelectionChange: this.dialogGroupSelectionChange,
      doRemoveFromGroup: this.doRemoveFromGroup,
      setIsblocked: this.setIsblocked,
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
          <DeleteGroup />
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
