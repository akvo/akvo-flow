FLOW.deviceGroupControl = Ember.ArrayController.create({
  content: null,
  contentNoUnassigned: null,

  filterDevices: function () {
    this.set('contentNoUnassigned', FLOW.store.filter(FLOW.DeviceGroup, function (item) {
      return (item.get('keyId') == 1) ? false : true;
    }));
  },

  populate: function () {
    var unassigned;

    // create a special record, which will to be saved to the datastore
    // to represent all devices unassigned to a device group.
    unassigned = FLOW.store.filter(FLOW.DeviceGroup, function (item) {
      return item.get('keyId') == 1;
    });
    if (unassigned.toArray().length === 0) {
      unassigned = FLOW.store.createRecord(FLOW.DeviceGroup, {
        code: Ember.String.loc('_devices_not_in_a_group'),
        keyId: 1
      });
      // prevent saving of this item to the backend
      unassigned.get('stateManager').send('becameClean');
    }
    this.set('content', FLOW.store.find(FLOW.DeviceGroup));
    this.filterDevices();
  }
});

FLOW.deviceControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  selected: null,
  content: null,

  populate: function () {
    this.set('content', FLOW.store.findQuery(FLOW.Device, {}));
    this.set('sortProperties', ['lastPositionDate']);
    this.set('sortAscending', false);
  },

  allAreSelected: function (key, value) {
    if (arguments.length === 2) {
      this.setEach('isSelected', value);
      return value;
    } else {
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }
  }.property('@each.isSelected'),

  atLeastOneSelected: function () {
    return this.filterProperty('isSelected', true).get('length');
  }.property('@each.isSelected'),

  // fired from tableColumnView.sort
  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});


FLOW.devicesInGroupControl = Ember.ArrayController.create({
  content: null,
  sortProperties: ['combinedName'],
  sortAscending: true,
  setDevicesInGroup: function () {
    var deviceGroupId;
    if (FLOW.selectedControl.get('selectedDeviceGroup') && FLOW.selectedControl.selectedDeviceGroup.get('keyId') !== null) {
      deviceGroupId = FLOW.selectedControl.selectedDeviceGroup.get('keyId');

      // 1 means all unassigned devices
      if (deviceGroupId == 1) {
        this.set('content', FLOW.store.filter(FLOW.Device, function (item) {
          return Ember.empty(item.get('deviceGroup'));
        }));
      } else {
        this.set('content', FLOW.store.filter(FLOW.Device, function (item) {
          return parseInt(item.get('deviceGroup'), 10) == deviceGroupId;
        }));
      }
    }
  }.observes('FLOW.selectedControl.selectedDeviceGroup')
});


FLOW.surveyAssignmentControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,

  populate: function () {
    this.set('content', FLOW.store.find(FLOW.SurveyAssignment));
    this.set('sortProperties', ['name']);
    this.set('sortAscending', true);
  },

  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
    this.set('selected', FLOW.tableColumnControl.get('selected'));
  }
});
