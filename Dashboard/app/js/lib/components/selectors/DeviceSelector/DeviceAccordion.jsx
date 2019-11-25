import React from 'react';
import PropTypes from 'prop-types';
// eslint-disable-next-line import/no-unresolved
import Checkbox from 'akvo-flow/components/reusable/Checkbox';

export default class DeviceAccordion extends React.Component {
  state = {
    isAccordionOpen: this.props.deviceGroupIsActive || true,
  };

  onAccordionClick = () => {
    const { isAccordionOpen } = this.state;
    this.setState({ isAccordionOpen: !isAccordionOpen });
  };

  getStyleProps = () => {
    const { isAccordionOpen } = this.state;
    const accordionClass = `accordion ${isAccordionOpen ? 'active' : ''}`;
    const fontClass = `fa fa-chevron-${isAccordionOpen ? 'up' : 'down'}`;
    const panelStyle = isAccordionOpen
      ? { display: 'block' }
      : { display: 'none' };

    return {
      accordionClass,
      fontClass,
      panelStyle,
    };
  };

  selectAllDevice = (_, checked) => {
    const { devices, handleSelectDevice } = this.props;

    devices.forEach(device => {
      handleSelectDevice(device.id, checked);
    });
  };

  allDevicesSelected = () => {
    const { selectedDevices, devices } = this.props;
    let allSelected = true;

    // loop through and check if devices is selected
    for (let i = 0; i < devices.length; i++) {
      const device = devices[i];

      if (!selectedDevices.includes(device.id)) {
        allSelected = false;
      }
    }

    return allSelected;
  };

  render() {
    const {
      name,
      devices,
      handleSelectDevice,
      selectedDevices,
      id,
    } = this.props;
    const { accordionClass, fontClass, panelStyle } = this.getStyleProps();

    return (
      <div className="accordion-container">
        <div className={accordionClass} data-testid="accordion">
          <Checkbox
            id={id}
            name={id}
            checked={this.allDevicesSelected()}
            onChange={this.selectAllDevice}
            label=""
          />

          <div
            onClick={this.onAccordionClick}
            onKeyPress={this.onAccordionClick}
          >
            <span>{name}</span>

            <span className={fontClass} />
          </div>
        </div>

        <div className="panel" style={panelStyle} data-testid="panel">
          {devices.map(device => (
            <div key={device.id}>
              <Checkbox
                id={device.id}
                name={device.id}
                checked={selectedDevices.includes(device.id)}
                onChange={handleSelectDevice}
                label={device.name}
              />
            </div>
          ))}
        </div>
      </div>
    );
  }
}

DeviceAccordion.defaultProps = {
  deviceGroupIsActive: true,
};

DeviceAccordion.propTypes = {
  id: PropTypes.string.isRequired,
  deviceGroupIsActive: PropTypes.bool,
  name: PropTypes.string.isRequired,
  devices: PropTypes.array.isRequired,
  handleSelectDevice: PropTypes.func.isRequired,
  selectedDevices: PropTypes.array.isRequired,
};
