/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import { groupBy as _groupBy } from 'lodash';
import introJs from 'intro.js';

import FormSection from './screens/FormSection';
import AddDevices from './screens/AddDevices';
import EditDevices from './screens/EditDevices';
import AssignDatapoints from './screens/AssignDatapoints';
import EditDatapoints from './screens/EditDatapoints';
import DevicesSection from './screens/DevicesSection';

import Sidebar from './__partials/Sidebar';
import OnboardingModal from './__partials/OnboardingModal';

import AssignmentsContext from './assignment-context';

export default class AssignmentMain extends React.Component {
  constructor(props) {
    super(props);

    this.introJs = introJs();
    this.introJs.onbeforechange(this.onBeforeIntroChanges);
    this.introJs.setOptions({
      steps: [
        {
          intro: 'Form selection for an assignment is now limited to a single survey',
          element: '#folder-selector',
          position: 'right',
          dynamic: true,
        },
        {
          intro: 'Only publised forms can be selected',
          element: '#form-selector',
          position: 'right',
          dynamic: true,
        },
      ],
    });
  }

  state = {
    currentTab: 'FORMS',
    selectedDeviceId: null,
    modalIsOpen: false,
  };

  componentDidMount() {
    this.toggleModal();
  }

  toggleModal = () => {
    const { modalIsOpen } = this.state;
    this.setState({ modalIsOpen: !modalIsOpen }, () => {
      // if modal is being closed
      if (modalIsOpen) {
        this.introJs.start();
      }
    });
  };

  // intro.js
  onBeforeIntroChanges = () => {
    const intro = this.introJs;
    const currentStepIdx = intro._currentStep;
    const currentStepDynamic = !!intro._options.steps[currentStepIdx].dynamic;

    if (currentStepDynamic) {
      // update element
      const step = intro._options.steps[currentStepIdx];
      const element = document.querySelector(step.element);

      if (element) {
        // const introItem = intro._introItems[currentStepIdx];
        intro._introItems[currentStepIdx].element = element;
        intro._introItems[currentStepIdx].position = step.position || 'auto';
      }
    }
  };

  changeTab = (tab, selectedDeviceId = null) => {
    this.setState({ currentTab: tab, selectedDeviceId }, () => {
      if (tab === 'ASSIGN_DATAPOINTS') {
        // load full details for each datapoint when viewing device datapoints
        this.context.actions.getDeviceDatapoints(selectedDeviceId);
      }
    });
  };

  getDeviceGroups = () => {
    // filter out selected devices
    const { devices, selectedDeviceIds } = this.context.data;

    const selectedDevices = devices.filter(device => selectedDeviceIds.includes(device.id));

    return _groupBy(selectedDevices, device => device.deviceGroup.id);
  };

  render() {
    const { strings, data } = this.context;
    const deviceGroups = this.getDeviceGroups();
    const { currentTab, selectedDeviceId } = this.state;

    return (
      <React.Fragment>
        <div className="assignment-body">
          <Sidebar
            strings={strings}
            data={data}
            deviceGroups={deviceGroups}
            currentTab={currentTab}
            changeTab={this.changeTab}
          />

          <div className="assignment-main">
            {currentTab === 'FORMS' && <FormSection changeTab={this.changeTab} />}
            {currentTab === 'ADD_DEVICE' && <AddDevices changeTab={this.changeTab} />}
            {currentTab === 'EDIT_DEVICE' && <EditDevices changeTab={this.changeTab} />}
            {currentTab === 'DEVICES' && <DevicesSection changeTab={this.changeTab} />}
            {currentTab === 'ASSIGN_DATAPOINTS' && (
              <AssignDatapoints changeTab={this.changeTab} selectedDeviceId={selectedDeviceId} />
            )}
            {currentTab === 'EDIT_DATAPOINTS' && (
              <EditDatapoints changeTab={this.changeTab} selectedDeviceId={selectedDeviceId} />
            )}
          </div>
        </div>

        {/* render modal */}
        <OnboardingModal
          isOpen={this.state.modalIsOpen}
          toggleModal={this.toggleModal}
          strings={strings}
          id="onboardingModal"
        />
      </React.Fragment>
    );
  }
}

AssignmentMain.contextType = AssignmentsContext;
