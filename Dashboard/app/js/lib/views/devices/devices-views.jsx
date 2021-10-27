import React from 'react';
import DevicesTab from '../../components/devices/DevicesTab';
import observe from '../../mixins/observe';

require('akvo-flow/views/react-component');

FLOW.CurrentDevicesTabView = FLOW.ReactComponentView.extend(
  observe({
    'FLOW.deviceControl.content.isLoaded': 'renderReactSide',
    'FLOW.deviceGroupControl.content.isLoaded': 'renderReactSide',
  }),
  {
    init() {
      this._super();
      this.getProps = this.getProps.bind(this);
      this.renderReactSide = this.renderReactSide.bind(this);
    },

    didInsertElement(...args) {
      this._super(...args);
    },

    renderReactSide() {
      if (!FLOW.deviceControl.content || !FLOW.deviceControl.content.isLoaded) return;
      const props = this.getProps();
      this.reactRender(<DevicesTab {...props} />);
    },

    getProps() {
      return {
        devices: this.get('devices'),
        devicesGroup: this.get('devicesGroup'),
        doAddToGroup: this.doAddToGroup,
        cancelRemoveFromGroup: this.cancelRemoveFromGroup,
        onSortDevices: this.devicesSort,
        onSortGroup: this.GroupSort,
<<<<<<< HEAD
=======
        onDeleteGroup: this.deleteGroupConfirm,
>>>>>>> 71bba63eb... [#3887] Clean code and refactor
        sortProperties: {
          column: this.selectedColumn,
          ascending: this.sortAscending,
        },
        strings: {
          imeiTooltip: Ember.String.loc('_imei_tooltip'),
          delete: Ember.String.loc('_delete'),
          IMEI: Ember.String.loc('_imei'),
          deviceID: Ember.String.loc('_device_id'),
          deviceGroup: Ember.String.loc('_device_group'),
          lastContact: Ember.String.loc('_last_contact'),
          version: Ember.String.loc('_version'),
          action: Ember.String.loc('_action'),
          navText: {
            manageDeviceGroups: Ember.String.loc('_manage_device_groups'),
            addToDeviceGroup: Ember.String.loc('_add_to_device_group'),
            manageDevices: Ember.String.loc('_devices'),
            newGroup: Ember.String.loc('_new_group'),
            removeFromDeviceGroup: Ember.String.loc('_remove_from_device_group'),
          },
          dialogText: {
            warningText: Ember.String.loc('_remove_devices_from_device_group'),
            selectGroupText: Ember.String.loc('_select_existing_device_group'),
            chooseGroup: Ember.String.loc('_choose_an_existing_device_group_from_the_list'),
            addDeviceToGroup: Ember.String.loc('_add_devices_to_device_group'),
            save: Ember.String.loc('_ok'),
            cancel: Ember.String.loc('_cancel'),
          },
        },
      };
    },

    devices: Ember.computed(() => {
      return FLOW.deviceControl
        .get('content')
        .getEach('_data')
        .getEach('attributes');
    }).property('FLOW.deviceControl.content.isLoaded'),

    devicesGroup: Ember.computed(() => {
      return FLOW.deviceGroupControl
        .get('content')
        .getEach('_data')
        .getEach('attributes')
        .filter(value => Object.keys(value).length !== 0);
    }).property('FLOW.deviceGroupControl.content.isLoaded'),

    devicesSort(item) {
      this.set('sortAscending', !this.sortAscending);
      this.set('selectedColumn', item);
      this.sortedDevices();
    },
  }
);

// TODO not used?
FLOW.SavingDeviceGroupView = FLOW.View.extend(
  observe({
    'FLOW.deviceGroupControl.allRecordsSaved': 'showDGSavingDialog',
  }),
  {
    showDGSavingDialogBool: false,

    showDGSavingDialog() {
      if (FLOW.DeviceGroupControl.get('allRecordsSaved')) {
        this.set('showDGSavingDialogBool', false);
      } else {
        this.set('showDGSavingDialogBool', true);
      }
    },
  }
);
