import React from 'react';
import PropTypes from 'prop-types';
import DevicesGroupList from './DevicesGroupList';
import DevicesList from './DevicesList';
import RemoveDialog from './deviceTabDialog/RemoveDialog';
import DevicesTabContext from './devices-context';
import TABLE_NAMES from './constants';

export default class DevicesTab extends React.Component {
  state = {
    devices: this.props.devices,
    currentTable: false,
    devicesGroup: this.props.devicesGroup,
    selectedDeviceIds: [],
    selectedDeviceGroupIds: [],
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
    };
    // FLOW.selectedControl.set('selectedDevice', true);

    return (
      <DevicesTabContext.Provider value={contextData}>
        <section id="devicesList">
          {this.state.currentTable === TABLE_NAMES.DEVICES_GROUP ? (
            <DevicesGroupList />
          ) : (
            <DevicesList />
          )}
          <RemoveDialog warningText={this.props.strings.dialogText.warningText} />
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
};
