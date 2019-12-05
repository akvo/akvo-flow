import React from 'react';
// eslint-disable-next-line import/no-unresolved
import AssignmentsEditView from 'akvo-flow/components/devices/AssignmentsEditView';
import observe from '../../mixins/observe';

// eslint-disable-next-line import/no-unresolved
require('akvo-flow/views/react-component');

// utils
FLOW.ArrNoDupe = function(a) {
  let gotIt;
  const templ = {};
  const tempa = Ember.A([]);
  for (let i = 0; i < a.length; i++) {
    templ[a.objectAt(i).clientId] = true;
  }
  const keys = Object.keys(templ);
  for (let j = 0; j < keys.length; j++) {
    gotIt = false;
    for (let i = 0; i < a.length; i++) {
      if (a.objectAt(i).clientId == keys[j] && !gotIt) {
        tempa.pushObject(a.objectAt(i));
        gotIt = true;
      }
    }
  }
  return tempa;
};

FLOW.formatDate = function(value) {
  if (!Ember.none(value)) {
    return `${value.getFullYear()}/${value.getMonth() + 1}/${value.getDate()}`;
  }
  return null;
};

FLOW.AssignmentEditView = FLOW.ReactComponentView.extend(
  observe({
    'FLOW.router.navigationController.selected': 'detectChangeTab',
    'FLOW.router.devicesSubnavController.selected': 'detectChangeTab',
    'FLOW.surveyControl.content.isLoaded': 'detectSurveyLoaded',
  }),
  {
    init() {
      this._super();
      this.setupControls();

      this.getProps = this.getProps.bind(this);
      this.cancelEditSurveyAssignment = this.cancelEditSurveyAssignment.bind(
        this
      );
      this.detectSurveyLoaded = this.detectSurveyLoaded.bind(this);
      this.renderReactSide = this.renderReactSide.bind(this);
      this.handleFormCheck = this.handleFormCheck.bind(this);
      this.validateAssignment = this.validateAssignment.bind(this);
      this.saveSurveyAssignment = this.saveSurveyAssignment.bind(this);
      this.setupForms = this.setupForms.bind(this);
      this.setupSurveyGroups = this.setupSurveyGroups.bind(this);
      this.handleSurveySelect = this.handleSurveySelect.bind(this);
      this.setupDevices = this.setupDevices.bind(this);
      this.deviceInAssignment = this.deviceInAssignment.bind(this);
      this.handleDeviceCheck = this.handleDeviceCheck.bind(this);
      this.handleSelectAllDevice = this.handleSelectAllDevice.bind(this);
      this.addDevicesCheckedOption = this.addDevicesCheckedOption.bind(this);
      this.addDevicesToAssignment = this.addDevicesToAssignment.bind(this);
      this.removeDevicesFromAssignment = this.removeDevicesFromAssignment.bind(
        this
      );

      // object wide varaibles
      this.forms = {};
      this.surveyGroups = [];
      this.devices = [];
      this.deviceGroups = {};
      this.deviceGroupNames = {};

      // selected attributes
      this.selectedDevices = [];
      this.selectedSurveys = [];

      // using Set to avoia duplication
      this.activeDeviceGroups = new Set();
      this.initialSurveyGroup = null;
    },

    didInsertElement(...args) {
      this._super(...args);

      this.setupForms();
      this.setupSurveyGroups();
      this.setupDevices();

      // react render
      this.renderReactSide();
    },

    renderReactSide() {
      const props = this.getProps();
      this.reactRender(<AssignmentsEditView {...props} />);
    },

    getProps() {
      const strings = {
        saveAssignment: Ember.String.loc('_save'),
        settings: Ember.String.loc('_settings'),
        duration: Ember.String.loc('_duration'),
        day: Ember.String.loc('_day'),
        days: Ember.String.loc('_days'),
        durationWarning: Ember.String.loc('_duration_warning'),
        survey: Ember.String.loc('_survey'),
        forms: Ember.String.loc('_forms'),
        enabled: Ember.String.loc('_enabled'),
        formsWarning: Ember.String.loc('_forms_warning'),
        noForms: Ember.String.loc('_no_forms_in_this_survey'),
        devices: Ember.String.loc('_devices'),
        device: Ember.String.loc('_device'),
        selected: Ember.String.loc('_selected'),
        assignmentNamePlaceholder: Ember.String.loc(
          '_enter_a_name_for_this_assignment'
        ),
        chooseFolderOrSurvey: Ember.String.loc('_choose_folder_or_survey'),
      };

      const inputValues = {
        assignmentName: FLOW.selectedControl.selectedSurveyAssignment.get(
          'name'
        ),
        startDate: FLOW.dateControl.fromDate,
        toDate: FLOW.dateControl.toDate,
      };

      const actions = {
        cancelEditSurveyAssignment: this.cancelEditSurveyAssignment,
        handleFormCheck: this.handleFormCheck,
        onSubmit: this.saveSurveyAssignment,
        handleSurveySelect: this.handleSurveySelect,
        handleDeviceCheck: this.handleDeviceCheck,
        addDevicesToAssignment: this.addDevicesToAssignment,
        removeDevicesFromAssignment: this.removeDevicesFromAssignment,
      };

      const data = {
        forms: this.forms,
        devices: this.devices,
        surveyGroups: this.surveyGroups,
        deviceGroups: this.deviceGroups,
        deviceGroupNames: this.deviceGroupNames,
        activeDeviceGroups: this.activeDeviceGroups,
        initialSurveyGroup: this.initialSurveyGroup,
        numberOfForms: this.selectedSurveys.length,
        selectedDeviceIds: this.selectedDevices,
      };

      return {
        strings,
        actions,
        inputValues,
        data,
      };
    },

    cancelEditSurveyAssignment() {
      if (
        Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))
      ) {
        FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
      }
      FLOW.selectedControl.set('selectedSurveyAssignment', null);
      FLOW.router.transitionTo('navDevices.assignSurveysOverview');
    },

    saveSurveyAssignment(data) {
      let endDateParse;
      let startDateParse;

      // set devices and surveys
      const deviceIds = this.selectedDevices;
      const formIds = this.selectedSurveys.map(item => item.get('keyId'));

      // validate data before continuing
      const isValid = this.validateAssignment({ ...data, deviceIds, formIds });
      if (!isValid) {
        return false;
      }

      // set Ember Data
      FLOW.dateControl.set(
        'fromDate',
        FLOW.formatDate(new Date(data.startDate))
      );
      FLOW.dateControl.set('toDate', FLOW.formatDate(new Date(data.endDate)));

      // get assignment
      const sa = FLOW.selectedControl.get('selectedSurveyAssignment');

      // set assignment name
      sa.set('name', data.assignmentName);

      // parse date
      if (!Ember.none(FLOW.dateControl.get('toDate'))) {
        endDateParse = Date.parse(FLOW.dateControl.get('toDate'));
      } else {
        endDateParse = null;
      }

      if (!Ember.none(FLOW.dateControl.get('fromDate'))) {
        startDateParse = Date.parse(FLOW.dateControl.get('fromDate'));
      } else {
        startDateParse = null;
      }

      // set data and language
      sa.set('endDate', endDateParse);
      sa.set('startDate', startDateParse);
      sa.set('language', 'en');

      // set survey group
      if (FLOW.selectedControl.get('selectedSurveyGroup')) {
        sa.set(
          'surveyId',
          FLOW.selectedControl.get('selectedSurveyGroup').get('keyId')
        );
      }

      // set form and devices
      sa.set('formIds', formIds);
      sa.set('deviceIds', deviceIds);

      FLOW.store.commit();

      // wait half a second before transitioning back to the assignments list
      setTimeout(() => {
        FLOW.router.transitionTo('navDevices.assignSurveysOverview');
      }, 500);

      return true;
    },

    // setups
    setupControls() {
      FLOW.selectedControl.set('selectedSurveyGroup', null);
      FLOW.selectedControl.set('selectedDeviceGroup', null);
      FLOW.surveyControl.set('content', null);
      FLOW.devicesInGroupControl.set('content', null);

      let startDate = null;
      let endDate = null;

      if (FLOW.selectedControl.selectedSurveyAssignment.get('startDate') > 0) {
        startDate = new Date(
          FLOW.selectedControl.selectedSurveyAssignment.get('startDate')
        );
      }
      if (FLOW.selectedControl.selectedSurveyAssignment.get('endDate') > 0) {
        endDate = new Date(
          FLOW.selectedControl.selectedSurveyAssignment.get('endDate')
        );
      }
      FLOW.dateControl.set('fromDate', FLOW.formatDate(startDate));
      FLOW.dateControl.set('toDate', FLOW.formatDate(endDate));
    },

    setupForms() {
      if (!FLOW.selectedControl.selectedSurveyAssignment.get('formIds')) {
        return;
      }

      FLOW.selectedControl.selectedSurveyAssignment
        .get('formIds')
        .forEach(formId => {
          const form = FLOW.Survey.find(formId);
          if (form && form.get('keyId')) {
            this.selectedSurveys.push(form);

            // load selected survey group
            this.initialSurveyGroup = form.get('surveyGroupId');

            this.forms[form.get('keyId')] = {
              // also load pre-selected forms
              name: form.get('name'),
              checked: true,
            };
          }
        });
    },

    setupSurveyGroups() {
      if (FLOW.projectControl.content.isLoaded) {
        FLOW.projectControl.get('content').forEach(item => {
          this.surveyGroups.push({
            keyId: item.get('keyId'),
            parentId: item.get('parentId'),
            name: item.get('name'),
            published: item.get('published'),
            projectType: item.get('projectType'),
            monitoringGroup: item.get('monitoringGroup'),
            ancestorIds: item.get('ancestorIds'),
          });
        });
      }
    },

    // listeners
    detectSurveyLoaded() {
      this.forms = {};

      if (!FLOW.surveyControl.content) return;

      // filter to show only published forms here
      FLOW.surveyControl.content
        .filter(form => form.get('status') === 'PUBLISHED')
        .forEach(form => {
          this.forms[form.get('keyId')] = {
            name: form.get('name'),
            checked: this.formInAssignment(form.get('keyId')),
          };
        });

      this.renderReactSide();
    },

    detectChangeTab() {
      if (
        Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))
      ) {
        FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
      }
      FLOW.selectedControl.set('selectedSurveyAssignment', null);
    },

    // helpers
    showPopup(header, message) {
      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', header);
      FLOW.dialogControl.set('message', message);
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
    },

    formInAssignment(formId) {
      const formsInAssignment = this.selectedSurveys.map(item =>
        item.get('id')
      );

      // convert id to string
      return formsInAssignment
        ? formsInAssignment.indexOf(`${formId}`) > -1
        : false;
    },

    shouldRemoveForms() {
      const formsInAssignment = FLOW.selectedControl
        .get('selectedSurveys')
        .map(item => item.get('id'));

      const selectedSurveyGroupId = FLOW.selectedControl.selectedSurveyGroup.get(
        'keyId'
      );

      if (formsInAssignment && formsInAssignment.length > 0) {
        // get survey id of first form currently in assignment
        const preSelectedSurvey = FLOW.Survey.find(formsInAssignment[0]);
        if (preSelectedSurvey && preSelectedSurvey.get('keyId')) {
          return (
            preSelectedSurvey.get('surveyGroupId') != selectedSurveyGroupId
          );
        }
      }

      return false;
    },

    validateAssignment(data) {
      const { assignmentName, startDate, endDate, deviceIds, formIds } = data;

      // validate assignment name
      if (!assignmentName || assignmentName == '') {
        this.showPopup(
          Ember.String.loc('_assignment_name_not_set'),
          Ember.String.loc('_assignment_name_not_set_text')
        );

        return false;
      }

      if (assignmentName.length > 100) {
        this.showPopup(
          Ember.String.loc('_assignment_name_error'),
          Ember.String.loc('_assignment_name_over_100_chars')
        );

        return false;
      }

      // validate dates ==== start date
      if (!startDate || !startDate.length) {
        this.showPopup(
          Ember.String.loc('_date_not_set'),
          Ember.String.loc('_date_not_set_text')
        );

        return false;
      }

      // validate date ==== expire date
      if (!endDate || !endDate.length) {
        this.showPopup(
          Ember.String.loc('_date_not_set'),
          Ember.String.loc('_date_not_set_text')
        );

        return false;
      }

      if (!deviceIds || !deviceIds.length) {
        this.showPopup(
          Ember.String.loc('_device_not_set'),
          Ember.String.loc('_device_not_set_text')
        );

        return false;
      }

      if (!formIds || !formIds.length) {
        this.showPopup(
          Ember.String.loc('_form_not_set'),
          Ember.String.loc('_form_not_set_text')
        );

        return false;
      }

      return true;
    },

    deviceInAssignment(deviceId) {
      const devicesInAssignment = this.selectedDevices.map(item =>
        item.get('keyId')
      );

      return devicesInAssignment
        ? devicesInAssignment.indexOf(deviceId) > -1
        : false;
    },

    addDevicesCheckedOption() {
      // check if all items in device group is selected
      Object.keys(this.deviceGroups).forEach(dgId => {
        const deviceGroupKeys = Object.keys(this.deviceGroups[dgId]).filter(
          deviceId => deviceId != 0
        );

        // get length of all devices in this group
        const numberOfDevices = deviceGroupKeys.length;

        // get length of all selected devices in this group
        const numberOfSelectedDevices = deviceGroupKeys.filter(
          deviceId => this.deviceGroups[dgId][deviceId].checked
        ).length;

        // add select all device option
        this.deviceGroups[dgId] = {
          ...this.deviceGroups[dgId],
          0: {
            name: Ember.String.loc('_select_all_devices'),
            checked:
              numberOfDevices !== 0 &&
              numberOfDevices === numberOfSelectedDevices,
          },
        };
      });
    },

    // handlers
    handleFormCheck(formId) {
      // if checking a form in a new survey, remove all forms
      if (this.shouldRemoveForms()) {
        // remove all currently selected forms
        this.selectedSurveys = [];
      }

      // check form
      this.forms[formId].checked = !this.forms[formId].checked;

      // add/remove form to/from assignment
      if (this.forms[formId].checked) {
        // push survey to selectedSurveys
        this.selectedSurveys.push(FLOW.Survey.find(formId));
      } else {
        this.selectedSurveys.pop(FLOW.Survey.find(formId));
      }

      this.renderReactSide();

      // TODO: load data points in selected form
    },

    handleSurveySelect(parentId) {
      const selectedSG = FLOW.projectControl
        .get('content')
        .find(sg => sg.get('keyId') == parentId);

      if (selectedSG && selectedSG.get('projectType') !== 'PROJECT_FOLDER') {
        FLOW.selectedControl.set('selectedSurveyGroup', selectedSG);
        return false;
      }

      // empty forms when a new folder is picked
      this.forms = {};
      this.selectedSurveys = [];

      this.renderReactSide();
      return true;
    },

    handleDeviceCheck(deviceId, checked, deviceGroupId) {
      // if it's the select all option
      if (deviceId == 0) {
        return this.handleSelectAllDevice(deviceGroupId, checked);
      }

      const device = FLOW.Device.find(deviceId);
      if (checked) {
        // push device to selectedDevices
        this.selectedDevices.push(FLOW.Device.find(deviceId));
      } else {
        // remove device to selectedDevices
        this.selectedDevices.pop(FLOW.Device.find(deviceId));
      }

      // check devices
      this.deviceGroups[device.get('deviceGroup') || '1'][
        device.get('keyId')
      ] = {
        name: device.get('deviceIdentifier'),
        checked,
      };

      // check if all items in device group is selected
      this.addDevicesCheckedOption();

      return this.renderReactSide();
    },

    handleSelectAllDevice(deviceGroupId, checked) {
      const deviceGroup = this.deviceGroups[deviceGroupId];
      const allDevices = Object.keys(deviceGroup)
        .filter(deviceId => deviceId != 0)
        .map(deviceId => FLOW.Device.find(deviceId));

      allDevices.forEach(device => {
        this.selectedDevices[checked ? 'push' : 'pop'](device);

        // check devices
        this.deviceGroups[device.get('deviceGroup') || '1'][
          device.get('keyId')
        ] = {
          name: device.get('deviceIdentifier'),
          checked,
        };
      });

      // mark device group as selected
      this.deviceGroups[deviceGroupId][0].checked = checked;

      // rerender react side
      return this.renderReactSide();
    },

    // handle devices functionality
    setupDevices() {
      if (
        FLOW.deviceGroupControl.content.isLoaded &&
        FLOW.deviceControl.content.isLoaded
      ) {
        this.devices = FLOW.deviceControl.get('content').map(device => {
          const formattedDevice = {
            name: device.get('deviceIdentifier'),
            id: device.get('id'),
            deviceGroup: {
              id: '1',
              name: 'Device not in group',
            },
          };

          if (device.get('deviceGroup')) {
            const deviceGroup = FLOW.DeviceGroup.find(
              device.get('deviceGroup')
            );

            formattedDevice.deviceGroup = {
              id: deviceGroup.get('id'),
              name: deviceGroup.get('code'),
            };
          }

          return formattedDevice;
        });

        // initialize with previous selected devices [if editing survey]
        if (FLOW.selectedControl.selectedSurveyAssignment.get('deviceIds')) {
          FLOW.selectedControl.selectedSurveyAssignment
            .get('deviceIds')
            .forEach(deviceId => {
              // populate pre-selected devices
              const device = FLOW.Device.find(deviceId);
              if (device && device.get('id')) {
                this.selectedDevices.push(device.get('id'));
              }
            });
        }
      }
    },

    addDevicesToAssignment(devices) {
      devices.forEach(deviceId => {
        // populate pre-selected devices
        const device = FLOW.Device.find(deviceId);
        if (device && device.get('id')) {
          this.selectedDevices.push(device.get('id'));
        }
      });

      return this.renderReactSide();
    },

    removeDevicesFromAssignment(devices) {
      devices.forEach(deviceId => {
        // populate pre-selected devices
        const device = FLOW.Device.find(deviceId);
        if (device && device.get('id')) {
          this.selectedDevices = this.selectedDevices.filter(
            d => d !== device.get('id')
          );
        }
      });

      return this.renderReactSide();
    },
  }
);
