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
      this.canAddFormsToAssignment = this.canAddFormsToAssignment.bind(this);
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

      // object wide varaibles
      this.forms = {};
      this.surveyGroups = [];
      this.deviceGroups = {};
      this.deviceGroupNames = {};

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
        durationWarning: Ember.String.loc('_duration_warning'),
        survey: Ember.String.loc('_survey'),
        forms: Ember.String.loc('_forms'),
        enabled: Ember.String.loc('_enabled'),
        formsWarning: Ember.String.loc('_forms_warning'),
        noForms: Ember.String.loc('_no_forms_in_this_survey'),
        devices: Ember.String.loc('_devices'),
        assignmentNamePlaceholder: Ember.String.loc(
          '_enter_a_name_for_this_assignment'
        ),
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
        handleSelectAllDevice: this.handleSelectAllDevice,
      };

      const data = {
        forms: this.forms,
        surveyGroups: this.surveyGroups,
        deviceGroups: this.deviceGroups,
        deviceGroupNames: this.deviceGroupNames,
        activeDeviceGroups: this.activeDeviceGroups,
        initialSurveyGroup: this.initialSurveyGroup,
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
      const devices = [];
      const surveys = [];

      // set devices and surveys
      FLOW.selectedControl.get('selectedDevices').forEach(item => {
        devices.push(item.get('keyId'));
      });

      FLOW.selectedControl.get('selectedSurveys').forEach(item => {
        surveys.push(item.get('keyId'));
      });

      // validate data before continuing
      const isValid = this.validateAssignment({ ...data, devices, surveys });
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

      if (FLOW.selectedControl.get('selectedSurveyGroup')) {
        sa.set(
          'surveyId',
          FLOW.selectedControl.get('selectedSurveyGroup').get('keyId')
        );
      }

      sa.set('formIds', surveys);

      FLOW.store.commit();

      // wait half a second before transitioning back to the assignments list
      setTimeout(() => {
        FLOW.router.transitionTo('navDevices.assignSurveysOverview');
      }, 500);

      return true;
    },

    // setups
    setupControls() {
      FLOW.selectedControl.set('selectedDevices', []);
      FLOW.selectedControl.set('selectedSurveys', []);
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
            FLOW.selectedControl.selectedSurveys.pushObject(form);

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

    setupDevices() {
      if (FLOW.deviceGroupControl.content.isLoaded) {
        FLOW.deviceGroupControl.get('content').forEach(item => {
          this.deviceGroupNames[item.get('keyId')] = item.get('code');
          this.deviceGroups[item.get('keyId')] = {}; // initialize array of devices per group
        });

        if (FLOW.deviceControl.content.isLoaded) {
          if (FLOW.selectedControl.selectedSurveyAssignment.get('deviceIds')) {
            FLOW.selectedControl.selectedSurveyAssignment
              .get('deviceIds')
              .forEach(deviceId => {
                // populate pre-selected devices
                const device = FLOW.Device.find(deviceId);
                if (device && device.get('keyId')) {
                  FLOW.selectedControl.selectedDevices.pushObject(device);
                }
              });
          }

          FLOW.deviceControl.get('content').forEach(device => {
            const checked = this.deviceInAssignment(device.get('keyId'));

            if (checked) {
              this.activeDeviceGroups.add(device.get('deviceGroup') || '1');
            }

            this.deviceGroups[device.get('deviceGroup') || '1'][
              device.get('keyId')
            ] = {
              name: device.get('deviceIdentifier'),
              checked,
            };
          });

          // check if all items in device group is selected
          this.addDevicesCheckedOption();
        }
      }
    },

    // listeners
    detectSurveyLoaded() {
      this.forms = {};

      if (!FLOW.surveyControl.content) return;

      FLOW.surveyControl.content.forEach(form => {
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
      const formsInAssignment = FLOW.selectedControl.selectedSurveyAssignment.get(
        'formIds'
      );
      return formsInAssignment ? formsInAssignment.indexOf(formId) > -1 : false;
    },

    canAddFormsToAssignment() {
      // only allow if form qualifies
      const formsInAssignment = FLOW.selectedControl.selectedSurveyAssignment.get(
        'formIds'
      );
      const selectedSurveyGroupId = FLOW.selectedControl.selectedSurveyGroup.get(
        'keyId'
      );

      if (formsInAssignment && formsInAssignment.length > 0) {
        // get survey id of first form currently in assignment
        const preSelectedSurvey = FLOW.Survey.find(formsInAssignment[0]);
        if (preSelectedSurvey && preSelectedSurvey.get('keyId')) {
          return (
            preSelectedSurvey.get('surveyGroupId') == selectedSurveyGroupId
          );
        }
      }

      return true; // no forms are currently added to the assignment
    },

    validateAssignment(data) {
      const { assignmentName, startDate, endDate, devices, surveys } = data;

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

      if (!devices || !devices.length) {
        this.showPopup(
          Ember.String.loc('_device_not_set'),
          Ember.String.loc('_device_not_set_text')
        );

        return false;
      }

      if (!surveys || !surveys.length) {
        this.showPopup(
          Ember.String.loc('_form_not_set'),
          Ember.String.loc('_form_not_set_text')
        );

        return false;
      }

      return true;
    },

    deviceInAssignment(deviceId) {
      const devicesInAssignment = FLOW.selectedControl.selectedSurveyAssignment.get(
        'deviceIds'
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
    handleFormCheck(e) {
      // only allow a form to be checked if a different survey isn't already selected
      const formId = e.target.name;
      if (this.canAddFormsToAssignment()) {
        this.forms[formId].checked = !this.forms[formId].checked;
      } else {
        // TODO: display error that form cannot be added unless currently added forms are removed
      }

      this.renderReactSide();

      // add/remove form to/from assignment
      if (this.forms[formId].checked) {
        // push survey to FLOW.selectedControl.selectedSurveys
        FLOW.selectedControl.selectedSurveys.pushObject(
          FLOW.Survey.find(formId)
        );
      } else {
        FLOW.selectedControl.selectedSurveys.removeObject(
          FLOW.Survey.find(formId)
        );
      }

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
        // push device to FLOW.selectedControl.selectedDevices
        FLOW.selectedControl.selectedDevices.pushObject(
          FLOW.Device.find(deviceId)
        );
      } else {
        // remove device to FLOW.selectedControl.selectedDevices
        FLOW.selectedControl.selectedDevices.removeObject(
          FLOW.Device.find(deviceId)
        );
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
        FLOW.selectedControl.selectedDevices[
          checked ? 'pushObject' : 'removeObject'
        ](device);

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
  }
);
