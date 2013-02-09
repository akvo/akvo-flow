FLOW.deviceGroupControl = Ember.ArrayController.create({
  content: null,

  populate: function() {
    var unassigned;
    this.set('content', FLOW.store.find(FLOW.DeviceGroup));
    unassigned = FLOW.store.createRecord(FLOW.DeviceGroup, {
      code: 'all unassigned devices',
      keyId: '-1'
    });
  }
});

FLOW.deviceControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  selected: null,
  content: null,

  populate: function() {
    this.set('content', FLOW.store.findQuery(FLOW.Device, {}));
    this.set('sortProperties', ['lastPositionDate']);
    this.set('sortAscending', false);
  },

  allAreSelected: function(key, value) {
    if(arguments.length === 2) {
      this.setEach('isSelected', value);
      return value;
    } else {
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }
  }.property('@each.isSelected'),

  atLeastOneSelected: function() {
    return this.filterProperty('isSelected', true).get('length');
  }.property('@each.isSelected'),

  // fired from tableColumnView.sort
  getSortInfo: function() {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});


FLOW.devicesInGroupControl = Ember.ArrayController.create({
  content: null,
  sortProperties: ['phoneNumber'],
  sortAscending: true,
  setDevicesInGroup: function() {
    var deviceGroupId;
    if(FLOW.selectedControl.get('selectedDeviceGroup') && FLOW.selectedControl.selectedDeviceGroup.get('keyId') !== null) {
      deviceGroupId = FLOW.selectedControl.selectedDeviceGroup.get('keyId');
      if(deviceGroupId == -1) {
        this.set('content', FLOW.store.filter(FLOW.Device, function(item) {
          return(Ember.none(item.get('deviceGroup')));
        }));
      } else {
        this.set('content', FLOW.store.filter(FLOW.Device, function(item) {
          return(parseInt(item.get('deviceGroup'), 10) == deviceGroupId);
        }));
      }
    }
  }.observes('FLOW.selectedControl.selectedDeviceGroup')
});


FLOW.surveyAssignmentControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,

  populate: function() {
    this.set('content', FLOW.store.find(FLOW.SurveyAssignment));
    this.set('sortProperties', ['name']);
    this.set('sortAscending', true);
  },

  getSortInfo: function() {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
    this.set('selected', FLOW.tableColumnControl.get('selected'));
  }
});