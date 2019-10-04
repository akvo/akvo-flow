import React from 'react';
import PropTypes from 'prop-types';

// eslint-disable-next-line import/no-unresolved
import Checkbox from 'akvo-flow/components/Checkbox';

export default class DeviceSelector extends React.Component {
  state = {
    isAccordionOpen: this.props.deviceIsChecked,
  }

  onAccordionClick = () => {
    const { isAccordionOpen } = this.state;
    this.setState({ isAccordionOpen: !isAccordionOpen });
  }

  render() {
    const { isAccordionOpen } = this.state;
    const { deviceGroups, deviceGroupNames, handleDeviceCheck } = this.props;

    const accordionClass = `accordion ${isAccordionOpen && 'active'}`;
    const panelStyle = isAccordionOpen ? { display: 'block' } : { display: 'none' };

    return (
      <div>
        {Object.keys(deviceGroups).map(dgId => (
          <div key={dgId}>
            <div
              className={accordionClass}
              onClick={this.onAccordionClick}
              onKeyPress={this.onAccordionClick}
              data-testid="accordion"
            >
              {deviceGroupNames[dgId]}
            </div>

            <div className="panel" style={panelStyle} data-testid="panel">
              {Object.keys(deviceGroups[dgId]).map(deviceId => (
                <div key={deviceId}>
                  <Checkbox
                    id={deviceId}
                    name={deviceId}
                    checked={deviceGroups[dgId][deviceId].checked}
                    onChange={handleDeviceCheck}
                  />

                  <label id={deviceId} htmlFor={deviceId}>
                    {deviceGroups[dgId][deviceId].name}
                  </label>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    );
  }
}

DeviceSelector.propTypes = {
  deviceGroups: PropTypes.object.isRequired,
  deviceGroupNames: PropTypes.object.isRequired,
  deviceIsChecked: PropTypes.bool.isRequired,
  handleDeviceCheck: PropTypes.func.isRequired,
};
