import React from 'react';
import PropTypes from 'prop-types';
import DevicesGroupList from './DevicesGroupList';
import DevicesList from './DevicesList';
import RemoveDialog from './deviceTabDialog/RemoveDialog';
import DevicesTabContext from './devices-context';
import TABLE_NAMES from './constants';
import AddToGroupDialog from './deviceTabDialog/AddToGroupDialog';

export default class DevicesTab extends React.Component {
  state = {
    devices: this.props.devices,
    currentTable: false,
    devicesGroup: this.props.devicesGroup.filter(value => Object.keys(value).length !== 0),
    selectedDeviceIds: [],
    selectedDeviceGroupIds: [],
    newDeviceGroupName: '',
    selectedEditGroupId: null,
    showAddToGroupDialogBool: null,
  };

  selectDevice = id => {
    this.setState(state => {
      if (state.selectedDeviceIds.some(deviceId => id === deviceId)) {
        const filterDevice = state.selectedDeviceIds.filter(deviceId => id !== deviceId);
        return { selectedDeviceIds: [...filterDevice] };
      }
      return { selectedDeviceIds: [...state.selectedDeviceIds, id] };
    });
  };

  selectGroup = id => {
    this.setState(state => {
      if (state.selectedDeviceGroupIds.some(keyId => id === keyId)) {
        const filterGroup = state.selectedDeviceGroupIds.filter(keyId => id !== keyId);
        return { selectedDeviceGroupIds: [...filterGroup] };
      }
      return { selectedDeviceGroupIds: [...state.selectedDeviceGroupIds, id] };
    });
  };

  tableHeaderClass = () => {
    if (this.props.sortProperties.ascending) {
      return 'sorting_asc';
    }
    return 'sorting_desc';
  };

  setCurrentTable = tableName => {
    this.setState({ currentTable: tableName });
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
    const findGroup = this.state.devicesGroup.find(group => group.keyId == id);
    const selectedDeviceGroupId = findGroup.keyId;
    // this could have been changed in the UI
    findGroup.code = value;
    const originalSelectedDeviceGroup = FLOW.store.find(FLOW.DeviceGroup, selectedDeviceGroupId);
    originalSelectedDeviceGroup.set('code', findGroup.code);

    // Update the device group name in the devices list
    const allDevices = FLOW.store.filter(FLOW.Device, () => true);
    allDevices.forEach(item => {
      if (parseInt(item.get('deviceGroup'), 10) == selectedDeviceGroupId) {
        item.set('deviceGroupName', findGroup.code);
      }
    });

    if (this.state.newDeviceGroupName.length !== 0) {
      FLOW.store.createRecord(FLOW.DeviceGroup, {
        code: this.state.newDeviceGroupName,
      });
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
    }
  };

  showAddToGroupDialog = () => {
    this.setState({ showAddToGroupDialogBool: true });
  };

  cancelAddToGroup = () => {
    this.setState({ showAddToGroupDialogBool: false });
  };

  render() {
    const contextData = {
      devices: this.state.devices,
      devicesGroup: this.state.devicesGroup,
      showRemoveFromGroupDialogBool: this.props.showRemoveFromGroupDialogBool,
      showRemoveFromGroupDialog: this.props.showRemoveFromGroupDialog,
      cancelRemoveFromGroup: this.props.cancelRemoveFromGroup,
      onSortDevices: this.props.onSortDevices,
      onSortGroup: this.props.onSortGroup,
      strings: this.props.strings,
      sortProperties: this.props.sortProperties,
      selectDevice: this.selectDevice,
      selectGroup: this.selectGroup,
      selectedDeviceIds: this.state.selectedDeviceIds,
      selectedDeviceGroupIds: this.state.selectedDeviceGroupIds,
      tableHeaderClass: this.tableHeaderClass,
      currentTable: this.state.currentTable,
      setCurrentTable: this.setCurrentTable,
      onDeleteGroup: this.props.onDeleteGroup,
      addNewGroup: this.addNewGroup,
      toggleEditButton: this.toggleEditButton,
      selectedEditGroupId: this.state.selectedEditGroupId,
      renameGroup: this.renameGroup,
      showAddToGroupDialogBool: this.state.showAddToGroupDialogBool,
      showAddToGroupDialog: this.showAddToGroupDialog,
      cancelAddToGroup: this.cancelAddToGroup,
    };

    return (
      <DevicesTabContext.Provider value={contextData}>
        <section id="devicesList">
          {this.state.currentTable === TABLE_NAMES.DEVICES_GROUP ? (
            <DevicesGroupList />
          ) : (
            <DevicesList />
          )}
          <RemoveDialog warningText={this.props.strings.dialogText.warningText} />
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
  doAddToGroup: PropTypes.func,
  onSortGroup: PropTypes.func,
  selectedDeviceGroup: PropTypes.bool,
  onDeleteGroup: PropTypes.func,
  addNewGroup: PropTypes.func,
  toggleEditButton: PropTypes.func,
  selectedEditGroupId: PropTypes.number,
  renameGroup: PropTypes.func,
};

DevicesTab.defaultProps = {
  devices: [],
  devicesGroup: [],
  showRemoveFromGroupDialogBool: false,
  showRemoveFromGroupDialog: () => null,
  cancelRemoveFromGroup: () => null,
  onSortDevices: () => null,
  doAddToGroup: () => null,
  onSortGroup: () => null,
  sortProperties: {},
  strings: {},
  selectedDeviceGroup: null,
  onDeleteGroup: () => null,
  addNewGroup: () => null,
  toggleEditButton: () => null,
  selectedEditGroupId: 0,
  renameGroup: () => null,
};
