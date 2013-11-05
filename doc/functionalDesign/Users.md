## Users

There are two types of users: dashboard users and mobile app users.

#### High-level requirements
1. Edit dashboard users
2. Edit roles and permissions of dashboard users
3. Edit mobile users
4. see mobile users activity
5. assign surveys to mobile users
6. create web authorization

#### Requirements

###### 1. Edit dashboard users
* A subtab __Dashboard users__ shows a list of existing users, including user name, email, permission level, last activity, and the action icons *edit*, and *delete*. 
* An action *Add new user* is displayed at the top
* When *Add new user* is clicked, the user can create a new user.
* When *delete user* is clicked, a confirmation dialog is displayed. If the user clickes 'Ok', the user is deleted.
* When *Edit user* is clicked, the details of that user are displayed, which can then be edited.
* *Email address entry field* - should validate email address format.
* *Username entry field* - should limit users to entering letters and numbers in the ISO basic Latin Alphabet (http://en.wikipedia.org/wiki/ISO_basic_Latin_alphabet), with no spaces, tabs or special characters. Username should be case-insensitive. Character limit for username should be 10. Whitespace should be stripped upon saving.


###### 2. Edit roles and permissions of dashboard users
* A subtab __Manage permissions__ displayes an overview of current permissions, with actions as rows and roles as columns. The user can change permissions
* A subtab __Manage roles__ displayes an overview of current roles. The user can add new roles
* All dashboard users can view current users and their permission levels
* Only Organisation administrators can see [+ Add New Users] button or Action column
* Users should only see the elements that they have permission to interact with according to their permission level.
* The default user level should be User.
* Akvo staff level users (Super Users) should be hidden on the user listing on all pages other than flowakvo.

###### 3. Edit mobile users
* A subtab __Mobile users__ shows a list of existing users, including user name, email, permission level, last activity, and the action icons *edit*, and *delete*. 
* Mobile users can be created in bulk, by clicking 'Add users in bulk'. This shows a popup where a list of user names can be put inside a textfield.
* user names and passwords / pin numbers for mobile users are created by the system. 
* Mobile users can be grouped into user groups
* Mobile users can be selected by group

###### 4. see mobile users activity
* A subtab __User activity__ shows the list of users. Two buttons on top, *Mobile users* and *Dasbhoard users*, toggle between the two.
* For each user, an action *show details* is shown.
* When clicked, it shows a status overview of the user: last contact, total surveys submitted, surveys submitted today, this week, and other useful statistics.

###### 5. assign surveys to mobile users
* A subtab __Survey assignments__ allows users to assign surveys to users
* On the tab, a user can:
	* see which surveys have been assigned to which users
	* select (a group of) users and assign surveys to them
	* determine if users can download data for monitoring features
	
###### 6. create web survey
* On the sidetab __Create web authorization__, a list of existing web authorizations is shown.
* When clicked, users can create a URL that they can share with respondents which allows filling in and submitting the survey through a web interface
* Editable parameters include name (to identify the web authorization with), expiration date (after which the URL is no longer accessible), survey group and survey, choice between anonymous or user-specific, max number of uses.
* Existing web authorizations are displayed in a list
* THe URL sends the user to the specififed FLOW survey in a webpage, where:
	* Question groups appear as tabs
	* Field types should be enforced appropriate to question types 
		* FREE TEXT - string
		* NUMBER - numbers only
		* OPTION - show option dropdown, along with Allow Other and Allow Multiple tick boxes
		* GPS - fields to enter latitude, longitude (required), altitude, unique code, dropdown pick from existing codes, view map to select location
		* PHOTO - browse and upload
		* VIDEO - browse and upload
		* DATE - date picker
		* BARCODE - number field to enter barcode number
	* Form adapts to dependent questions
* Submit screen is the final tab, contains a submit button
* Submit button enforces mandatory questions
* User sees a confirmation pop up when the survey is successfully submitted. Pressing Ok returns them to a blank page inside the authorization URL, with a message that states "Thank you for your submission." And a button "Submit Another" Clicking "Submit Another" takes them to a fresh blank web survey screen if they have not met the max number of uses. If they have met the max number of uses for that authorization, display a pop up that informs them of this.
* Survey data appears with submitter = WEB FORM in Data listing (Data tab)
