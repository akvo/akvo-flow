import React from 'react';
import PropTypes from 'prop-types';
import DevicesGroupList from './DevicesGroupList';
import DevicesList from './DevicesList';
import RemoveDialog from './deviceTabDialog/RemoveDialog';
import DevicesTabContext from './device-context';

export default class DevicesTab extends React.Component {
  state = {
    switchTable: false,
    devicesGroup: [],
  };

  selectDevice(id) {
    const selectedDevice = this.props.devices.find(device => device.keyId === id);

    if (!this.state.devicesGroup.some(device => selectedDevice.deviceGroup === device)) {
      this.setState(prevState => ({
        devicesGroup: [...prevState.devicesGroup, selectedDevice.deviceGroup],
      }));
    } else {
      const filterDevice = this.state.devicesGroup.filter(
        device => device !== selectedDevice.deviceGroup
      );
      this.setState({ devicesGroup: [...filterDevice] });
    }
  }

  render() {
    const contextData = {
      devices: this.props.devices,
      showRemoveFromGroupDialog: this.props.showRemoveFromGroupDialog,
      showRemoveFromGroupDialogBool: this.props.showRemoveFromGroupDialogBool,
      cancelRemoveFromGroup: this.props.cancelRemoveFromGroup,
      strings: this.props.strings,
    };

    return (
      <DevicesTabContext.Provider value={contextData}>
        <section id="devicesList">
          {this.state.switchTable ? (
            <DevicesGroupList
              selectDevice={this.selectDevice}
              setSwitchTable={() => this.setState({ switchTable: false })}
            />
          ) : (
            <DevicesList
              devicesGroup={this.state.devicesGroup}
              selectDevice={this.selectDevice}
              setSwitchTable={() => this.setState({ switchTable: true })}
            />
          )}
          <RemoveDialog warningText={this.props.strings.dialogText.warningText} />
        </section>
      </DevicesTabContext.Provider>
    );
  }
}

// DevicesTab.contextType = DevicesTabContext;
DevicesTab.propTypes = {
  devices: PropTypes.array,
  showRemoveFromGroupDialogBool: PropTypes.bool,
  showRemoveFromGroupDialog: PropTypes.func,
  cancelRemoveFromGroup: PropTypes.func,
  strings: PropTypes.object,
};

DevicesTab.defaultProps = {
  devices: [],
  showRemoveFromGroupDialogBool: false,
  showRemoveFromGroupDialog: () => null,
  cancelRemoveFromGroup: () => null,
  strings: {},
};
