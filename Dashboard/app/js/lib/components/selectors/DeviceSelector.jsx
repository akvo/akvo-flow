import React from 'react';
import PropTypes from 'prop-types';

// eslint-disable-next-line import/no-unresolved
import Checkbox from 'akvo-flow/components/Checkbox';

export default class DeviceSelector extends React.Component {
  state = {
    isAccordionOpen: false,
  }

  onAccordionClick = () => {
    const { isAccordionOpen } = this.state;
    this.setState({ isAccordionOpen: !isAccordionOpen });
  }

  render() {
    const { isAccordionOpen } = this.state;
    const { deviceGroups, deviceGroupNames, onCheck } = this.props;

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
            >
              {deviceGroupNames[dgId]}
            </div>

            <div className="panel" style={panelStyle}>
              {Object.keys(deviceGroups[dgId]).map(deviceId => (
                <div key={deviceId}>
                  <Checkbox
                    id={deviceId}
                    name={deviceId}
                    checked={deviceGroups[dgId][deviceId].checked}
                    onChange={onCheck}
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
  onCheck: PropTypes.func.isRequired,
};
