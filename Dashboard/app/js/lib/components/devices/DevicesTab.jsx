import React from 'react';
import PropTypes from 'prop-types';
import DevicesGroupList from './DevicesGroupList';
import DevicesList from './DevicesList';
import RemoveDialog from './deviceTabDialog/RemoveDialog';
import DevicesTabContext from './device-context';

export default class DevicesTab extends React.Component {
  state = {
    // xOffset: 10,
    // // yOffset: 20,
    // showRemoveFromGroupDialogBool: false,
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

  // const mouseEnter = function(e) {
  //   const tooltipText = $(e.target).attr('data-title');
  //   $('body').append(`<p id='tooltip'>${tooltipText}</p>`);
  //   $('#tooltip')
  //     .css('top', `${e.pageY - xOffset}px`)
  //     .css('left', `${e.pageX + yOffset}px`)
  //     .fadeIn('fast');
  // },

  // const mouseLeave = function() {
  //   $('#tooltip').remove();
  // },

  // const mouseMove = function(e) {
  //   $('#tooltip')
  //     .css('top', `${e.pageY - xOffset}px`)
  //     .css('left', `${e.pageX + yOffset}px`);
  // },

  render() {
    // console.log(this.props.showRemoveFromGroupDialogBool, this.props, 'show dialog');
    const contextData = {
      devices: this.props.devices,
      showRemoveFromGroupDialog: this.props.showRemoveFromGroupDialog,
      showRemoveFromGroupDialogBool: this.props.showRemoveFromGroupDialogBool,
      cancelRemoveFromGroup: this.props.cancelRemoveFromGroup,
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
              // mouseEnter={mouseEnter}
              // mouseLeave={mouseLeave}
              // mouseMove={mouseMove}
            />
          )}
          <RemoveDialog warningText="Remove devices from device group?" />
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
};

DevicesTab.defaultProps = {
  devices: [],
  showRemoveFromGroupDialogBool: false,
  showRemoveFromGroupDialog: () => null,
  cancelRemoveFromGroup: () => null,
};
