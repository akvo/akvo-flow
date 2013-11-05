## Data

#### High-level requirements
1. inspect raw data (survey Instances)
2. inspect records (surveyed Locales)
3. bulk upload data
4. Excel data cleaning
5. data cleaning

#### Requirements
###### 1. inspect raw data (survey Instances)
* On a subtab __Inspect Data__, the raw data (surveyInstances) is displayed in a list, with the actions *Edit* and *Delete
* The raw data is ordered by decreasing submitted time
* When *Edit* is clicked, all answers for the given surveyInstance are shown in a popup. All answers can be edited, with the edit action being aware of the question type.
* When *Delete* is clicked, a confirmation dialog is displayed. If confirmed, the surveyInstance will be deleted, and all attached aggregates will be updated.
* Raw data can be filered by survey, submission to and from date, submitter name, device id. 

###### 2. inspect records (surveyed Locales)
* On a subtab __Inspect records__, records (surveyedLocales) are shown in a list, after a monitoring survey group is chosen. 
* On top, there is an action *Detail view*
* When *Details view* is clicked, the currently selected surveyedLocale is shown with all its details, and *previous record* and *next record* actions.
* A record shows the list of surveyInstances that has contributed to it. If a surveyInstance is clicked, the questionAnswers are shown

###### 3. bulk upload data
* On a subtab __Bulk upload data__, a dropdown area is presented where users can drop zipped flow data. Users can also select a file on their computer for upload
* A clear help message is displayed to explain the user what needs to be uploaded (a zip file of the surveyal folder).

###### 4. Excel data cleaning
* On a subtab __Excel data cleaning__, the user can select a surveygroup, survey and language
* When the survey is selected, the user can export a raw data report
* When the survey is selected, the user can import a raw data report that has been cleaned
* The system checks if the uploaded data corresponds to the selected survey.

###### 5. Data cleaning
* On a subtab __Data cleaning__, a survey can be selected, after which the raw data is displayed, with survey instances as rows and questions as columns
* users can hide and show columns
* users can perform actions per column
* actions include search-and-replace, capitalize first word, all lowercase, remove punctuation, etc.
* edit individual fields, with the field being aware of the question type
* The system should save the previous version, and indicate if fields have changed.