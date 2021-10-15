import React from 'react';
import PropTypes from 'prop-types';
import DevicesGroupList from './DevicesGroupList';
import DevicesList from './DevicesList';
import RemoveDialog from './deviceTabDialog/RemoveDialog';

const DevicesTabContext = React.createContext(null);

export default class DevicesTab extends React.Component {
  state = {
    devices: [...this.props.devices],
    xOffset: 10,
    yOffset: 20,
    showRemoveFromGroupDialogBool: false,
    switchTable: false,
    devicesGroup: [],
  };

  selectDevice(id) {
    const selectedDevice = this.state.devices.find(device => device.keyId === id);
    if (!this.state.devicesGroup.some(device => selectedDevice.deviceGroup === device)) {
      this.setState({ devicesGroup: [...this.state.devicesGroup, selectedDevice.deviceGroup] });
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
    return (
      <DevicesTabContext.Provider value={this.state}>
        <section id="devicesList">
          {this.state.switchTable ? (
            <DevicesGroupList
              devices={this.state.devices}
              selectDevice={this.selectDevice}
              setSwitchTable={() => this.setState({ switchTable: false })}
            />
          ) : (
            <DevicesList
              devices={this.state.devices}
              devicesGroup={this.state.devicesGroup}
              selectDevice={this.selectDevice}
              setSwitchTable={() => this.setState({ switchTable: true })}
              // mouseEnter={mouseEnter}
              // mouseLeave={mouseLeave}
              // mouseMove={mouseMove}
              setShowRemoveFromGroupDialogBool={() => null}
            />
          )}
          <RemoveDialog
            className={this.state.showRemoveFromGroupDialogBool ? `display overlay` : `overlay`}
            cancelRemoveFromGroup={() => this.setState('showRemoveFromGroupDialogBool', false)}
            warningText="Remove devices from device group?"
          />
        </section>
      </DevicesTabContext.Provider>
    );
  }
}

DevicesTab.contextType = DevicesTabContext;
// DevicesTab.propTypes = {
//   devices: PropTypes.array,
// };

// DevicesTab.defaultProps = {
//   devices: [],
// };
