import React from 'react';

import ChildOption from 'akvo-flow/components/ChildOption';

require('akvo-flow/views/react-component');
/* eslint-disable jsx-a11y/click-events-have-key-events */
FLOW.DeviceGroupSelectorView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
    this.comparator = this.comparator.bind(this);
    this.state = { value: 0 };
    this.deviceGroupNames = {};
  },

  didInsertElement(...args) {
    this._super(...args);
    let dGs = {};
    if (FLOW.deviceGroupControl.content.isLoaded) {
      FLOW.deviceGroupControl.get('content').forEach((item) => {
        this.deviceGroupNames[item.get('keyId')] = item.get('code');
        dGs[item.get('keyId')] = []; // initialize array of devices per group
      });
      if (FLOW.deviceControl.content.isLoaded) {
        FLOW.deviceControl.get('content').forEach((device) => {
          dGs[device.get('deviceGroup') ? device.get('deviceGroup') : 1].push({
            id: device.get('keyId'),
            name: device.get('deviceIdentifier'),
          });
        });
      }
      this.reactRender(
        <div className="formSelectorList">
          {Object.keys(dGs).map(dgId => (
            <div key={dgId}>
              <div className="accordion" onClick={this.deviceGroupClick}>
                {/* Object values accessible only by sqaure braces */}
                {this.deviceGroupNames[dgId]}
              </div>
              <div className="panel">
                <select multiple className="device-selector">
                  {dGs[dgId].map(device => (
                    <ChildOption key={device.id} name={device.name} value={device.id} />
                  ))}
                </select>
              </div>
            </div>
          ))}
        </div>
      );
    }
  },

  deviceGroupClick(e) {
    /* Toggle between adding and removing the "active" class,
    to highlight the button that controls the panel */
    e.target.classList.toggle('active');

    /* Toggle between hiding and showing the active panel */
    let panel = e.target.nextElementSibling;
    if (panel.style.display === 'block') {
      panel.style.display = 'none';
    } else {
      panel.style.display = 'block';
    }
  },

  comparator(a, b) {
    const nameA = a.name.toUpperCase(); // ignore upper and lowercase
    const nameB = b.name.toUpperCase(); // ignore upper and lowercase
    if (nameA < nameB) {
      return -1;
    }
    if (nameA > nameB) {
      return 1;
    }

    // names must be equal
    return 0;
  },
});
