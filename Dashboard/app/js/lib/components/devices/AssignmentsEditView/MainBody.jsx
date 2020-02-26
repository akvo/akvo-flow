/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';

// screens
import FormSection from './screens/FormSection';
import DevicesSection from './DevicesSection';
import AddDevices from './screens/AddDevices';
import EditDevices from './screens/EditDevices';
import AssignDatapoints from './screens/AssignDatapoints';
import EditDatapoints from './screens/EditDatapoints';

import Sidebar from './__partials/Sidebar';
import { TabRouter, TabRoute } from './__partials/TabRouter';
import AssignmentsContext from './assignment-context';

export default class AssignmentMain extends React.Component {
  render() {
    return (
      <TabRouter>
        <div className="assignment-body">
          <Sidebar />

          <div className="assignment-main">
            <TabRoute Component={FormSection} path="" />
            <TabRoute Component={DevicesSection} path="DEVICE" />
            <TabRoute Component={AddDevices} path="ADD_DEVICES" />
            <TabRoute Component={EditDevices} path="EDIT_DEVICES" />
            <TabRoute Component={AssignDatapoints} path="ASSIGN_DATAPOINTS" />
            <TabRoute Component={EditDatapoints} path="EDIT_DATAPOINTS" />
          </div>
        </div>
      </TabRouter>
    );
  }
}

AssignmentMain.contextType = AssignmentsContext;
