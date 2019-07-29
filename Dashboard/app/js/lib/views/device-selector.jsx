import React from 'react';

import ChildOption from 'akvo-flow/components/ChildOption';

require('akvo-flow/views/react-component');

FLOW.DeviceSelectorView = FLOW.ReactComponentView.extend({
  init() {
    this._super();
    this.handleChange = this.handleChange.bind(this);
    this.comparator = this.comparator.bind(this);
    this.deviceGroupSelector = this.deviceGroupSelector.bind(this);
    this.state = { value: 0 };
    this.deviceGroups = [];
  },

  didInsertElement(...args) {
    this._super(...args);
    if (FLOW.deviceGroupControl.content.isLoaded) {
      FLOW.deviceGroupControl.get('content').forEach((item) => {
        this.deviceGroups.push({
          keyId: item.get('keyId'),
          name: item.get('displayName'),
        });
      });
      this.deviceGroupSelector(0);
    }
  },

  deviceGroupSelector(deviceGroupId) {
    if (!FLOW.deviceGroupControl.content.isLoaded) return;

    let dgs = [{
      keyId: 0,
      name: Ember.String.loc('_select_device_group'),
    }].concat(this.deviceGroups.sort(this.comparator));

    if (deviceGroupId !== 0) {
      const selectedDG = FLOW.deviceGroupControl.get('content')
        .find(dg => dg.get('keyId') == deviceGroupId);
      if (selectedDG) {
        FLOW.selectedControl.set('selectedDeviceGroup', selectedDG);
      }
    }

    this.reactRender(
      <select value={this.state.value} onChange={this.handleChange}>
        {dgs.map(dg => (
          <ChildOption key={dg.keyId} name={dg.name} value={dg.keyId} />
        ))}
      </select>
    );
  },

  handleChange(event) {
    if (event.target.value !== 0) {
      this.deviceGroupSelector(event.target.value);
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
