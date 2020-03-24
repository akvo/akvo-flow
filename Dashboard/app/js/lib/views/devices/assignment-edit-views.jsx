/* eslint-disable import/no-unresolved */
import React from 'react';
import AssignmentsEditView from 'akvo-flow/components/devices/AssignmentsEditView';
import { formatDate } from 'akvo-flow/utils';
import observe from 'akvo-flow/mixins/observe';

require('akvo-flow/views/react-component');

FLOW.AssignmentEditView = FLOW.ReactComponentView.extend(
  observe({
    'FLOW.router.navigationController.selected': 'detectChangeTab',
    'FLOW.router.devicesSubnavController.selected': 'detectChangeTab',
    'FLOW.surveyControl.content.isLoaded': 'detectSurveyLoaded',
    'FLOW.router.surveyedLocaleController.content.isLoaded': 'detectDatapointsLoaded',
    'searchedDatapoints.isLoaded': 'detectSearchedDatapointLoaded',
    'FLOW.selectedControl.selectedSurveyAssignment.keyId': 'saveDatapoints',
    selectedSurveyGroupId: 'setDatapointEnabled',
  }),
  {
    // init methods
    init() {
      this._super();
      this.setupControls();

      this.getProps = this.getProps.bind(this);
      this.cancelEditSurveyAssignment = this.cancelEditSurveyAssignment.bind(this);

      this.renderReactSide = this.renderReactSide.bind(this);

      this.validateAssignment = this.validateAssignment.bind(this);
      this.saveSurveyAssignment = this.saveSurveyAssignment.bind(this);

      this.setupSurveyGroups = this.setupSurveyGroups.bind(this);
      this.handleSurveySelect = this.handleSurveySelect.bind(this);
      this.setupDevices = this.setupDevices.bind(this);
      this.deviceInAssignment = this.deviceInAssignment.bind(this);
      this.handleDeviceCheck = this.handleDeviceCheck.bind(this);
      this.handleSelectAllDevice = this.handleSelectAllDevice.bind(this);
      this.addDevicesCheckedOption = this.addDevicesCheckedOption.bind(this);
      this.addDevicesToAssignment = this.addDevicesToAssignment.bind(this);
      this.removeDevicesFromAssignment = this.removeDevicesFromAssignment.bind(this);

      // form methods
      this.setupForms = this.setupForms.bind(this);
      this.handleFormCheck = this.handleFormCheck.bind(this);
      this.detectSurveyLoaded = this.detectSurveyLoaded.bind(this);

      // datapoints methods
      this.saveDatapoints = this.saveDatapoints.bind(this);
      this.getDeviceDatapoints = this.getDeviceDatapoints.bind(this);
      this.detectDatapointsLoaded = this.detectDatapointsLoaded.bind(this);
      this.findDatapoints = this.findDatapoints.bind(this);
      this.detectSearchedDatapointLoaded = this.detectSearchedDatapointLoaded.bind(this);
      this.assignDataPointsToDevice = this.assignDataPointsToDevice.bind(this);
      this.getDeviceDatapoints = this.getDeviceDatapoints.bind(this);
      this.removeDatapointsFromAssignments = this.removeDatapointsFromAssignments.bind(this);
      this.setDatapointEnabled = this.setDatapointEnabled.bind(this);
      this.clearSearchedDatapoints = this.clearSearchedDatapoints.bind(this);
      this.assignAllDatapointsToDevice = this.assignAllDatapointsToDevice.bind(this);
      this.unassignAllDatapointsToDevice = this.unassignAllDatapointsToDevice.bind(this);

      // object wide varaibles
      this.forms = {};
      this.surveyGroups = [];
      this.devices = [];
      this.deviceGroups = {};
      this.deviceGroupNames = {};
      this.datapointsResults = [];

      // selected attributes
      this.selectedSurveyGroupId = null;
      this.selectedDevices = [];
      this.selectedFormIds = [];
      this.datapointAssignments = [];

      // global object variables
      this.searchedDatapoints = null;
      this.datapointsEnabled = null;
      this.allDataPointsAssigned = false;
      this.deviceInView = null;
    },

    didInsertElement(...args) {
      this._super(...args);

      this.setupSurveyGroups();
      this.setupForms();

      // load intial forms for selected survey group
      this.handleSurveySelect(this.selectedSurveyGroupId);
      this.setupDevices();

      // react render
      this.renderReactSide();
    },

    // react side
    renderReactSide() {
      const props = this.getProps();
      this.reactRender(<AssignmentsEditView {...props} />);
    },

    getProps() {
      const strings = {
        assignmentNamePlaceholder: Ember.String.loc('_enter_a_name_for_this_assignment'),
        chooseFolderOrSurvey: Ember.String.loc('_choose_folder_or_survey'),
        saveAssignment: Ember.String.loc('_save'),
        duration: Ember.String.loc('_duration'),
        forms: Ember.String.loc('_forms'),
        enabled: Ember.String.loc('_enabled'),
        noForms: Ember.String.loc('_no_forms_in_this_survey'),
        devices: Ember.String.loc('_devices'),
        device: Ember.String.loc('_device'),
        selected: Ember.String.loc('_selected'),
        edit: Ember.String.loc('_edit'),
        add: Ember.String.loc('_add'),
        addDevicesToAssignment: Ember.String.loc('_add_devices_to_assignment'),
        removeDevicesFromAssignment: Ember.String.loc('_remove_devices_from_assignment'),
        addToAssignment: Ember.String.loc('_add_to_assignment'),
        removeFromAssignment: Ember.String.loc('_remove_from_assignment'),
        noDeviceInAssignment: Ember.String.loc('_no_devices_in_assignments'),
        selectAMonitoringSurveyMessage: Ember.String.loc('_please_select_a_monitoring_survey'),
        assignDatapoints: Ember.String.loc('_assign_datapoints'),
        assignDatapointByNameOrId: Ember.String.loc('_assign_datapoint_by_name_or_id'),
        assignAllDatapoint: Ember.String.loc('_assign_all_datapoints'),
        searchDatapointByNameOrId: Ember.String.loc('_search_datapoint_by_name_or_id'),
        datapointAssigned: Ember.String.loc('_datapoints_assigned'),
        editDatapoints: Ember.String.loc('_edit_datapoints'),
        unassign: Ember.String.loc('_unassign'),
        unassignNote: Ember.String.loc('_unassign_all_datapoints_to_assign_by_other_options'),
        allDatapointsAssigned: Ember.String.loc('_all_datapoints_assigned'),
      };

      const inputValues = {
        assignmentName: FLOW.selectedControl.selectedSurveyAssignment.get('name'),
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
        findDatapoints: this.findDatapoints,
        clearSearchedDatapoints: this.clearSearchedDatapoints,
        getDeviceDatapoints: this.getDeviceDatapoints,
        removeDatapointsFromAssignments: this.removeDatapointsFromAssignments,
        assignDataPointsToDevice: this.assignDataPointsToDevice,
        assignAllDatapointsToDevice: this.assignAllDatapointsToDevice,
        unassignAllDatapointsToDevice: this.unassignAllDatapointsToDevice,
      };

      const data = {
        forms: this.forms,
        devices: this.devices,
        datapointsResults: this.datapointsResults,
        surveyGroups: this.surveyGroups,
        deviceGroups: this.deviceGroups,
        deviceGroupNames: this.deviceGroupNames,
        activeDeviceGroups: this.activeDeviceGroups,
        initialSurveyGroup: this.selectedSurveyGroupId,
        numberOfForms: this.selectedFormIds.length,
        selectedDeviceIds: this.selectedDevices,
        datapointsEnabled: this.datapointsEnabled,
        datapointAssignments: this.datapointAssignments,
        allDataPointsAssigned: this.allDataPointsAssigned,
      };

      return {
        strings,
        actions,
        inputValues,
        data,
      };
    },

    // global actions
    cancelEditSurveyAssignment() {
      if (Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))) {
        FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
      }
      FLOW.selectedControl.set('selectedSurveyAssignment', null);
      FLOW.router.transitionTo('navDevices.assignSurveysOverview');
    },

    detectChangeTab() {
      if (Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))) {
        FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
      }
      FLOW.selectedControl.set('selectedSurveyAssignment', null);
    },

    // saving functionality
    saveSurveyAssignment(data) {
      let endDateParse;
      let startDateParse;

      // set devices and surveys
      const deviceIds = this.selectedDevices;
      const formIds = this.selectedFormIds;

      // validate data before continuing
      const isValid = this.validateAssignment({ ...data, deviceIds, formIds });
      if (!isValid) {
        return false;
      }

      // set Ember Data
      FLOW.dateControl.set('fromDate', formatDate(new Date(data.startDate)));
      FLOW.dateControl.set('toDate', formatDate(new Date(data.endDate)));

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
        sa.set('surveyId', FLOW.selectedControl.get('selectedSurveyGroup').get('keyId'));
      }

      // set form and devices
      sa.set('formIds', formIds);
      sa.set('deviceIds', deviceIds);

      FLOW.store.commit();

      return true;
    },

    saveDatapoints() {
      if (!FLOW.selectedControl.selectedSurveyAssignment) return;
      const surveyAssignmentId = FLOW.selectedControl.selectedSurveyAssignment.get('keyId');

      if (!surveyAssignmentId) return;
      const surveyFolderId = FLOW.selectedControl.get('selectedSurveyGroup').get('keyId');

      // create records for each device datapoints
      this.datapointAssignments.forEach(dpAssignment => {
        const data = {
          surveyAssignmentId,
          surveyId: surveyFolderId,
          deviceId: dpAssignment.deviceId,
          dataPointIds: dpAssignment.datapoints.map(dp => {
            // if it's an object get the ID
            if (typeof dp === 'object') {
              return dp.id;
            }

            return dp;
          }),
        };

        if (dpAssignment.id) {
          // find and update old record with data
          const dpAssignmentRecord = FLOW.DataPointAssignment.find(dpAssignment.id);
          dpAssignmentRecord.set('surveyId', surveyFolderId);
          dpAssignmentRecord.set('dataPointIds', data.dataPointIds);
        } else {
          // create new record with data
          FLOW.store.createRecord(FLOW.DataPointAssignment, data);
        }
      });

      FLOW.store.commit();

      // wait half a second before transitioning back to the assignments list
      setTimeout(() => {
        FLOW.router.transitionTo('navDevices.assignSurveysOverview');
      }, 500);
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
        startDate = new Date(FLOW.selectedControl.selectedSurveyAssignment.get('startDate'));
      }
      if (FLOW.selectedControl.selectedSurveyAssignment.get('endDate') > 0) {
        endDate = new Date(FLOW.selectedControl.selectedSurveyAssignment.get('endDate'));
      }
      FLOW.dateControl.set('fromDate', formatDate(startDate));
      FLOW.dateControl.set('toDate', formatDate(endDate));
    },

    // helpers
    showPopup(header, message) {
      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', header);
      FLOW.dialogControl.set('message', message);
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
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
      if (!startDate) {
        this.showPopup(Ember.String.loc('_date_not_set'), Ember.String.loc('_date_not_set_text'));

        return false;
      }

      // validate date ==== expire date
      if (!endDate) {
        this.showPopup(Ember.String.loc('_date_not_set'), Ember.String.loc('_date_not_set_text'));

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
        this.showPopup(Ember.String.loc('_form_not_set'), Ember.String.loc('_form_not_set_text'));

        return false;
      }

      return true;
    },

    deviceInAssignment(deviceId) {
      const devicesInAssignment = this.selectedDevices.map(item => item.get('keyId'));

      return devicesInAssignment ? devicesInAssignment.indexOf(deviceId) > -1 : false;
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
            checked: numberOfDevices !== 0 && numberOfDevices === numberOfSelectedDevices,
          },
        };
      });
    },

    // handlers
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
      this.deviceGroups[device.get('deviceGroup') || '1'][device.get('keyId')] = {
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
        this.deviceGroups[device.get('deviceGroup') || '1'][device.get('keyId')] = {
          name: device.get('deviceIdentifier'),
          checked,
        };
      });

      // mark device group as selected
      this.deviceGroups[deviceGroupId][0].checked = checked;

      // rerender react side
      return this.renderReactSide();
    },

    // handle forms functionality
    setupForms() {
      if (!FLOW.selectedControl.selectedSurveyAssignment.get('formIds')) {
        return;
      }

      // setup forms from the selected survey assignment
      FLOW.selectedControl.selectedSurveyAssignment.get('formIds').forEach(formId => {
        const form = FLOW.Survey.find(formId);

        if (form && form.get('keyId')) {
          this.selectedFormIds.push(form.get('keyId'));

          // load selected survey group
          this.set('selectedSurveyGroupId', form.get('surveyGroupId'));

          this.forms[form.get('keyId')] = {
            // also load pre-selected forms
            name: form.get('name'),
            checked: true,
          };
        }
      });
    },

    detectSurveyLoaded() {
      this.forms = {};

      if (!FLOW.surveyControl.content) return;

      // filter to show only published forms here
      FLOW.surveyControl.content
        .filter(form => form.get('status') === 'PUBLISHED')
        .forEach(form => {
          // load selected survey group
          this.set('selectedSurveyGroupId', form.get('surveyGroupId'));

          this.forms[form.get('keyId')] = {
            name: form.get('name'),
            checked: this.selectedFormIds.includes(form.get('keyId')),
          };
        });

      this.renderReactSide();
    },

    handleFormCheck(formId) {
      // if checking a form in a new survey, remove all forms
      if (this.shouldRemoveForms()) {
        // remove all currently selected forms
        this.selectedFormIds = [];
      }

      // check form
      this.forms[formId].checked = !this.forms[formId].checked;

      // add/remove form to/from assignment
      if (this.forms[formId].checked) {
        // push survey to selectedFormIds
        this.selectedFormIds.push(formId);
      } else {
        // convert to string to check
        this.selectedFormIds = this.selectedFormIds.filter(surveys => `${surveys}` !== formId);
      }

      this.renderReactSide();
    },

    shouldRemoveForms() {
      const formsInAssignment = this.selectedFormIds;

      if (formsInAssignment && formsInAssignment.length > 0) {
        // get survey id of first form currently in assignment
        const preSelectedSurvey = FLOW.Survey.find(formsInAssignment[0]);
        if (preSelectedSurvey && preSelectedSurvey.get('keyId')) {
          return preSelectedSurvey.get('surveyGroupId') !== this.selectedSurveyGroupId;
        }
      }

      return false;
    },

    // handle survey group functionality
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

    handleSurveySelect(parentId) {
      const selectedSG = FLOW.projectControl.get('content').find(sg => sg.get('keyId') == parentId);

      if (selectedSG && selectedSG.get('projectType') !== 'PROJECT_FOLDER') {
        // TODO:: Add confirmation from user
        // empty all currently selected datapoints
        if (this.datapointAssignments.length) {
          this.datapointAssignments = [];
        }

        // using this to trigger an onserver which will load forms and rerender react
        FLOW.selectedControl.set('selectedSurveyGroup', selectedSG);

        return false;
      }

      if (parentId == 0) {
        this.set('selectedSurveyGroupId', null);
        FLOW.selectedControl.set('selectedSurveyGroup', selectedSG);
      }

      // empty forms when a new folder is picked
      this.forms = {};

      this.renderReactSide();
      return true;
    },

    // handle devices functionality
    setupDevices() {
      if (FLOW.deviceGroupControl.content.isLoaded && FLOW.deviceControl.content.isLoaded) {
        this.devices = FLOW.deviceControl
          .get('content')
          .map(device => {
            const formattedDevice = {
              name: device.get('deviceIdentifier'),
              id: device.get('keyId'),
              deviceGroup: {
                id: 1,
                name: 'Device not in group',
              },
            };

            if (device.get('deviceGroup')) {
              const deviceGroup = FLOW.DeviceGroup.find(device.get('deviceGroup'));

              formattedDevice.deviceGroup = {
                id: deviceGroup.get('keyId'),
                name: deviceGroup.get('code'),
              };
            }

            return formattedDevice;
          })
          .sort((a, b) => {
            const nameA = (a.name || '').toUpperCase(); // ignore upper and lowercase
            const nameB = (b.name || '').toUpperCase(); // ignore upper and lowercase

            if (nameA < nameB) {
              return -1;
            }
            if (nameA > nameB) {
              return 1;
            }

            // names must be equal
            return 0;
          });

        // initialize with previous selected devices [if editing survey]
        if (FLOW.selectedControl.selectedSurveyAssignment.get('deviceIds')) {
          FLOW.selectedControl.selectedSurveyAssignment.get('deviceIds').forEach(deviceId => {
            // populate pre-selected devices
            const device = FLOW.Device.find(deviceId);
            if (device && device.get('keyId')) {
              this.selectedDevices.push(device.get('keyId'));
            }
          });
        }
      }
    },

    addDevicesToAssignment(devices) {
      devices.forEach(deviceId => {
        // populate pre-selected devices
        const device = FLOW.Device.find(deviceId);
        if (device && device.get('keyId')) {
          this.selectedDevices.push(device.get('keyId'));
          this.assignAllDatapointsToDevice(device.get('keyId'));
        }
      });
    },

    removeDevicesFromAssignment(devices) {
      devices.forEach(deviceId => {
        // populate pre-selected devices
        const device = FLOW.Device.find(deviceId);
        if (device && device.get('keyId')) {
          this.selectedDevices = this.selectedDevices.filter(d => d !== device.get('keyId'));
        }
      });

      return this.renderReactSide();
    },

    // handle datapoints functionality
    setDatapointEnabled() {
      const surveyGroup = this.surveyGroups.find(sg => sg.keyId === this.selectedSurveyGroupId);

      if (surveyGroup) {
        // if selected survey has monitoring disabled, disable datapoint assignments
        this.datapointsEnabled = surveyGroup.monitoringGroup;
      }
    },

    getDeviceDatapoints(deviceId) {
      const surveyAssignmentId = FLOW.selectedControl.get('selectedSurveyAssignment').get('keyId');

      // if creating a new assignment then no need to make a fetch
      // return early
      if (!surveyAssignmentId) {
        return;
      }

      // if datapoint details is already exist no need to fetch
      if (this.datapointAssignments.find(item => item.deviceId === parseInt(deviceId, 10))) {
        return;
      }

      // assign current `this` to that
      const that = this;

      // get datapoints information for this device
      FLOW.DataPointAssignment.find({ deviceId, surveyAssignmentId }).on('didLoad', function() {
        // we're only expecting one datapoint assignment at max
        const datapointAssignment = this.map(item => ({
          id: item.get('keyId'),
          deviceId: item.get('deviceId'),
          datapoints: item.get('dataPointIds'),
        }))[0];

        if (!datapointAssignment) {
          return;
        }

        // check if all datapoints has been assigned
        if (datapointAssignment.datapoints[0] === 0) {
          // return early and check that all datapoints is set
          const completeDatapointAssignment = {
            ...datapointAssignment,
            allDataPointsAssigned: true,
          };

          // add to assignment
          that.datapointAssignments.push(completeDatapointAssignment);
          that.renderReactSide();

          return;
        }

        // get all datapoints for this assignment
        FLOW.SurveyedLocale.find({ ids: datapointAssignment.datapoints }).on('didLoad', function() {
          // combine data and add to datapoint assignments
          const completeDatapointAssignment = {
            ...datapointAssignment,
            datapoints: this.map(dp => ({
              name: dp.get('displayName'),
              id: dp.get('keyId'),
            })),
          };

          // add to assignment
          that.datapointAssignments.push(completeDatapointAssignment);
          that.renderReactSide();
        });
      });
    },

    detectDatapointsLoaded() {
      const datapoints = FLOW.router.surveyedLocaleController.get('content');

      if (!datapoints.get('length')) {
        return;
      }

      // update datapoints with complete information
      this.datapointAssignments = this.datapointAssignments.map(selectedDp => {
        if (selectedDp.deviceId !== this.deviceInView) {
          return selectedDp;
        }

        // update device
        return {
          ...selectedDp,
          datapoints: datapoints.map(dp => ({
            name: dp.get('displayName'),
            id: dp.get('keyId'),
          })),
        };
      });

      this.renderReactSide();
    },

    findDatapoints(search) {
      // find datapoints in the selected survey group
      const surveyGroupId = FLOW.selectedControl.get('selectedSurveyGroup').get('keyId');
      this.set('searchedDatapoints', FLOW.SurveyedLocale.find({ search, surveyGroupId }));
    },

    detectSearchedDatapointLoaded() {
      this.datapointsResults = this.searchedDatapoints.map(datapoint => {
        return {
          name: datapoint.get('displayName'),
          id: datapoint.get('keyId'),
        };
      });

      this.renderReactSide();
    },

    clearSearchedDatapoints() {
      this.searchedDatapoints = null;
      this.datapointsResults = [];

      this.renderReactSide();
    },

    assignAllDatapointsToDevice(deviceId) {
      const datapointAssignment = this.datapointAssignments.find(sDp => sDp.deviceId === deviceId);

      // check if device already has datapoints
      if (datapointAssignment) {
        datapointAssignment.allDataPointsAssigned = true;
        datapointAssignment.datapoints = [0];
      } else {
        // push new device into selected datapoints
        this.datapointAssignments.push({
          deviceId,
          datapoints: [0],
          allDataPointsAssigned: true,
        });
      }

      this.renderReactSide();
    },

    assignDataPointsToDevice(datapoints, deviceId) {
      const datapointAssignment = this.datapointAssignments.find(sDp => sDp.deviceId === deviceId);

      // check if device already has datapoints
      if (datapointAssignment) {
        datapoints.forEach(dp => {
          // check if datapoints isn't already added to this device
          if (!datapointAssignment.datapoints.find(sDp => sDp.id === dp.id)) {
            // push datapoints to device
            datapointAssignment.datapoints.push(dp);
          }
        });
      } else {
        // push new device into selected datapoints
        this.datapointAssignments.push({
          deviceId,
          datapoints,
        });
      }

      this.renderReactSide();
    },

    unassignAllDatapointsToDevice(deviceId) {
      const datapointAssignment = this.datapointAssignments.find(sDp => sDp.deviceId === deviceId);

      datapointAssignment.allDataPointsAssigned = false;
      datapointAssignment.datapoints = [];

      this.renderReactSide();
    },

    removeDatapointsFromAssignments(datapointIds, deviceId) {
      // create a new datapoint assignment and update the datapoint assignment immutably
      this.datapointAssignments = this.datapointAssignments.map(dpAssignment => {
        // return any datapoint assignment we're not trying to update
        if (dpAssignment.deviceId !== deviceId) {
          return dpAssignment;
        }

        // once we've gotten the datapoint assignment we need
        // remove the selected datapoints from the list
        const dps = dpAssignment.datapoints.filter(dp => {
          // filter out datapoints that's in the array
          return !datapointIds.includes(dp.id);
        });

        // add the updated datapoints to the datapoint assignment
        return {
          ...dpAssignment,
          datapoints: dps,
        };
      });

      this.renderReactSide();
    },
  }
);
