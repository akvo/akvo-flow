import React from 'react';
import DevicesTab from '../../components/devices/DevicesTab';
import observe from '../../mixins/observe';

require('akvo-flow/views/react-component');

FLOW.CurrentDevicesTabView = FLOW.ReactComponentView.extend(
  observe({
    'this.selectedDeviceGroup': 'copyDeviceGroupName',
    'FLOW.deviceControl.content.isLoaded': 'renderReactSide',
    'FLOW.deviceGroupControl.content.@each': 'renderReactSide',
    'FLOW.deviceGroupControl.content.isLoaded': 'renderReactSide',
    'FLOW.dialogControl.delGroupConfirm': 'deleteGroup',
    'this.selectedColumn': 'renderReactSide',
    'this.sortAscending': 'renderReactSide',
    'this.showAddToGroupDialogBool': 'renderReactSide',
    'FLOW.deviceGroupControl.content.content.length': 'renderReactSide',
  }),
  {
    init() {
      this._super();
      this.getProps = this.getProps.bind(this);
      this.renderReactSide = this.renderReactSide.bind(this);
      this.showRemoveFromGroupDialog = this.showRemoveFromGroupDialog.bind(this);
      this.cancelRemoveFromGroup = this.cancelRemoveFromGroup.bind(this);
      this.devicesSort = this.devicesSort.bind(this);
      this.sortedDevices = this.sortedDevices.bind(this);
      this.doAddToGroup = this.doAddToGroup.bind(this);
      this.GroupSort = this.GroupSort.bind(this);
      this.deleteGroupConfirm = this.deleteGroupConfirm.bind(this);
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
        // onDeleteGroup: this.deleteGroupConfirm,
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
    }).property('FLOW.deviceGroupControl.content.@each'),

    editGroupName: false,
    sortAscending: false,
    selectedColumn: null,
    changedDeviceGroupName: null,
    selectedDeviceGroup: null,
    selectedDeviceGroupForDelete: null,
    showDeleteDevicesDialogBool: false,
    showAddToGroupDialogBool: false,
    showRemoveFromGroupDialogBool: false,
    showManageDeviceGroupsDialogBool: false,
    newDeviceGroupName: null,

    showAddToGroupDialog() {
      this.set('selectedDeviceGroup', null);
      this.set('showAddToGroupDialogBool', true);
    },

    showRemoveFromGroupDialog() {
      this.set('showRemoveFromGroupDialogBool', true);
    },

    cancelAddToGroup() {
      this.set('showAddToGroupDialogBool', false);
    },

    showManageDeviceGroupsDialog() {
      this.set('newDeviceGroupName', null);
      this.set('changedDeviceGroupName', null);
      this.set('selectedDeviceGroup', null);
      // this.set('showManageDeviceGroupsDialogBool', true);
    },

    cancelManageDeviceGroups() {
      this.set('showManageDeviceGroupsDialogBool', false);
    },

    doAddToGroup() {
      if (this.get('selectedDeviceGroup') !== null) {
        const selectedDeviceGroupId = this.selectedDeviceGroup.get('keyId');
        const selectedDeviceGroupName = this.selectedDeviceGroup.get('code');
        const selectedDevices = FLOW.store.filter(FLOW.Device, data => {
          if (data.get('isSelected') === true) {
            return true;
          }
          return false;
        });

        selectedDevices.forEach(item => {
          item.set('deviceGroupName', selectedDeviceGroupName);
          item.set('deviceGroup', selectedDeviceGroupId);
        });
      }
      FLOW.store.commit();
      this.set('showAddToGroupDialogBool', false);
    },

    devicesSort(item) {
      this.set('sortAscending', !this.sortAscending);
      this.set('selectedColumn', item);
      this.sortedDevices();
    },

    // Sort the devices
    sortedDevices() {
      return this.get('devices').sort((a, b) => {
        if (this.sortAscending) {
          if (a[this.selectedColumn] < b[this.selectedColumn]) {
            return -1;
          }
          if (a[this.selectedColumn] > b[this.selectedColumn]) {
            return 1;
          }
        } else {
          if (b[this.selectedColumn] < a[this.selectedColumn]) {
            return -1;
          }
          if (b[this.selectedColumn] > a[this.selectedColumn]) {
            return 1;
          }
        }
        return 0;
      });
    },

    GroupSort(item) {
      this.set('sortAscending', !this.sortAscending);
      this.set('selectedColumn', item);
      this.sortedGroup();
    },

    // Sort the Group
    sortedGroup() {
      return this.get('devicesGroup').sort((a, b) => {
        if (this.sortAscending) {
          if (a[this.selectedColumn] < b[this.selectedColumn]) {
            return -1;
          }
          if (a[this.selectedColumn] > b[this.selectedColumn]) {
            return 1;
          }
        } else {
          if (b[this.selectedColumn] < a[this.selectedColumn]) {
            return -1;
          }
          if (b[this.selectedColumn] > a[this.selectedColumn]) {
            return 1;
          }
        }
        return 0;
      });
    },

    // TODO repopulate list after update
    doRemoveFromGroup() {
      const selectedDevices = FLOW.store.filter(FLOW.Device, data => {
        if (data.get('isSelected') === true) {
          return true;
        }
        return false;
      });
      selectedDevices.forEach(item => {
        item.set('deviceGroupName', null);
        item.set('deviceGroup', null);
      });

      FLOW.store.commit();
      this.set('showRemoveFromGroupDialogBool', false);
    },

    cancelRemoveFromGroup() {
      this.set('showRemoveFromGroupDialogBool', false);
    },

    copyDeviceGroupName() {
      if (this.get('selectedDeviceGroup') !== null) {
        this.set('changedDeviceGroupName', this.selectedDeviceGroup.get('code'));
      }
    },

    deleteGroupConfirm(groupId) {
      FLOW.dialogControl.confirm({
        context: FLOW.dialogControl.delDeviceGroup,
        groupId,
      });
    },

    deleteGroup() {
      if (FLOW.dialogControl.delGroupConfirm) {
        const devicesGroup = FLOW.store.find(
          FLOW.DeviceGroup,
          FLOW.dialogControl.get('delGroupId')
        );
        if (devicesGroup) {
          devicesGroup.deleteRecord();
          FLOW.store.commit();
        }
      }
    },

    deleteDeviceGroup() {
      const dgroup = this.get('selectedDeviceGroupForDelete');
      if (dgroup !== null) {
        const devicesInGroup = FLOW.store.filter(
          FLOW.Device,
          item => item.get('deviceGroup') == dgroup.get('keyId')
        );
        devicesInGroup.forEach(item => {
          item.set('deviceGroupName', null);
          item.set('deviceGroup', null);
        });

        FLOW.store.commit();

        dgroup.deleteRecord();
        FLOW.store.commit();
      }
      this.set('showManageDeviceGroupsDialogBool', false);
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
