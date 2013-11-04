## Devices

The data collection is user-centric, so the device is just a tool. Surveys are assigned to users, not devices


#### High-level requirements
1. See device status, including recent activity, app version, users, etc.
2. Send message to device

#### Requirements
###### 1. See device status, including recent activity, app version, users, etc.
* The __Devices__ page shows a list of devices that have been in contact with the dashboard lately
* Devices can be grouped into device groups
* Each device has the actions , *see details* and *block*
* When *details* is clicked, a popup is shown with the details of the device, including: last contact, last known location, users that have logged in, number of surveys submitted, remote exceptions, apk version installed.
* When *block* is clicked, a confirmation popup is shown. If confirmed, it will block new contributions from that device, and block all access to data stored in that device. 
* if a device is blocked, and a mobile user tries to login, a message is displayed informing the mobile user that the phone is blocked, with a 'contact your field manager' message.

###### 2. Send message to device
* On the __Devices__ page, a user can select devices or groups of devices, and apply actions to them. 
* One action is to send a message to a device, which will be displayed when a mobile user logs in.
* If the *Send message* is clicked, a popup is displayed with a text field for the message and a confirmation message.
