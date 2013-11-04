## Surveys 

#### High-level requirements
1. Edit survey groups 
2. Edit surveys
3. Edit question groups
4. Edit questions
5. add translations
6. add help media
7. create and edit monitoring settings
8. view basic survey statistics
9. publish surveys
10. preview surveys
11. create printable survey form
13. create bootstrap file

#### Requirements

###### 1. create, view, edit and delete survey groups, surveys, question groups, questions
* Surveys are ordered in survey groups on a __survey group overview page__. In a sidebar on the left, the surveygroups are shown. When a survey group is selected, the surveys inside are displayed
* Users can create new survey groups, and edit names of existing survey groups
* Surveys in a survey group are displayed in a grid format. For each survey, the title, created date, last modified date, total responses, privacy type, language are shown. Action
* For each survey, four actions are shown: *Edit*, *Delete*, *Preview*, *Copy*

###### 2. Edit surveys
* When a user clicks 'Edit' on the __survey group overview page__, the __survey edit page__ is displayed
* The following survey meta information that can be edited is displayed: title, description, type, master language.
* The following survey meta information that can not be edited is displayed: version number, surveyID, number of question groups, number of questions, publishing status
* In a sidebar on the left, the following actions are displayed: *manage translation*, *manage notifications* (should move to reports), *save*, *preview*, *publish*.
* on the right of the sidebar, a list of question groups is displayed


###### 3. Edit question groups
* Between each question group, a *insert group here* action is dispayed
* For each question group, five actions are displayed: *show questions*, *edit group name*, *delete*, *copy*, *move*. 
* When *Move* is clicked, two actions are show in between each question group: *move group here*, and *cancel*
* When *Copy* is clicked, two actions are shown in between each question group: *paste group here*, and *cancel*
* When *Delete* is clicked, a confim dialog will be displayed. If *ok* is clicked, the system will check if data has been collected for the questions contained in this question group. If so, a dialog will be displayed and the delete will not be done.
* When *Edit group name* is clicked, the name of the question group can be edited
* When *Show question* is clicked, the questions inside the question group are displayed in a list.
* In between each question, a *add new question* action is dispayed
* For each question, four actions are displayed: *edit*, *move*, *copy*, and *delete*.
* When *Move* is clicked, two actions are show in between each question: *move question here*, and *cancel*
* When *Copy* is clicked, two actions are shown in between each question: *paste question here*, and *cancel*
* When *Delete* is clicked, a confim dialog will be displayed. If *ok* is clicked, the system will check if data has been collected for the question. If so, a dialog will be displayed and the delete will not be done.
* When *Edit* is clicked, the question details are displayed.

###### 4. Edit questions
* The user can supply a question text (required) and question help tooltip (optional).
* The user can select a mandatory checkbox, which makes the question mandatory on the device
* The user can choose between these question types: Free text, Option, Number, Geolocation, Photo, Video, Date, Barcode
* Depending on the question type, the following extra options are available:
	* free text - (1) "display in record list on device". When selected, answers for this question will be used as meta data on records
	* option -  (1) a list of options, separated by newlines. (2) checkbox "display in record list on device", (3) checkbox "Allow Multiple". Determines if multiple answers can be selected on the device. (4) checkbox "Allow other". Determines if a user can select 'Other' as an option, and then fill in a text field with a free text answer.
	* number - (1) checkbox "Allow sign". Determines if the answer can contain a minus sign. (2) checkbox "Allow decimal point". Determines if a decimal point can be used. (3) numeric fields "minimum value" and "maximum value". Determine the min and max values of the answer.
	* geolocation - (1) checkbox "Use as the geolocation of the record". Determines if this value will be used as meta data on records.
	* photo, video, date, barcode - no extra options
* a checkbox "Dependent" determines if this question is dependent on another question. If selected, a user can select an option question and answer from a dropdown. On the device, dependent questions are only displayed when the dependency is fulfilled.
* a "save" and "cancel" button are displayed.

###### 5. add translations
* When the user clicks "Manage Translations" on the __survey edit page__, the __transation edit page__ is displayed.
* At the top, the __translation edit page__ displays the default language of the survey (same as the master language), and existing translations.
* Under "Add new translation" a dropdown with all languages is displayed, together with an "Add" button.
* When a user selects a new language and clicks "Add", a new translation is created.
* The current translation in language xxxxxx is displayed as the text "Survey details in xxxxxx"
* By clicking on the languages listed in "Existing translations", the user can switch between translations.
* On the page the survey details are listed in a two-column format, with the orginal on the left, and the translations on the right.
* On top, the survey title and survey description are displayed
* further below, the translations are shown per question group. When the user first lands on the __translation edit page__, only the first group is shown as open. 
* Question groups can be closed and opened, to display only those translations. 
* A "save translations" button is displayed both for the survey title and description section, and for each question group
* For each question group, the question group title is displayed for translation
* For each question, all textual elements of the question, including options, are displayed for translation.
* At the bottom of the page, a "Save and close", and "Close without saving" button are displayed.

###### 6. add help media
* For each question, a user can add help media, which can be text or an image

###### 7. create and edit monitoring settings
* On the __survey edit page__, users can indicate if for this survey group monitoring features are enabled
* If monotoring features are enabled, users can select the survey which can create new records
* Monitoring survey groups are indicated by a blue 'M' before the survey group name, and with an indications on the top of the page.

###### 8. view basic survey statistics
* In the left sidebar, the number of questions and question groups is displayed
* In the left sidebar, the number of surveys collected in total is displayed

###### 9. publish surveys
* In the left sidebar, a "publish" action is displayed
* When clicked, the dashboard requests the backend to publish the survey
* If succesfull, a message is available on the message tab, stating the path to the resulting xml file

###### 10. preview surveys
* In the left sidebar, an action "preview" is displayed
* When clicked a popup window is opened, with a close button on top
* All the questions and question groups are displayed, including options
* The dependency logic works in the preview.

###### 11. create printable survey form
* In the left sidebar, an action "create survey form" is displayed
* When clicked, a dialog is displayed which lets the user select a folder and a name and a confirmation button
* After confirmation, an excel file with all the questions is created and saved in the indicated location.
* The layout of the questions in the excel file makes it easy to fill it in, using it as an offline form. It contains descriptions for the dependent questions.

###### 12. create bootstrap file
* In the left sidebar, an action "create manual survey file" is displayed
* When clicked, a dialog is displayed asking for the target address of the user. As an alternative, it could select a folder on the client computer.
* After confirmation, a zip file containing the survey XML is delivered to the user. 

