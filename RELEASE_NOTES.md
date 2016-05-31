Akvo FLOW (Field Level Operations Watch) is a system to collect, manage, analyse and display geographically-referenced monitoring and evaluation data.

Read more about [Akvo FLOW](http://www.akvo.org/blog/?p=4836).
Read more about the [Akvo Platform](http://www.akvo.org/blog/?p=4822).

Akvo FLOW Dashboard release notes
----
#1.9.7 - Fashionable Flamingo
Date: 16 May 2016
# New and noteworthy
* Order cascade resources in drop-downs alphabetically to enable easier finding of a cascade resource in the list [#1615]
* Add Bahasa/Indonesian language to dashboard [#1566]
* Geotagging in photos in the dashboard.  We now show the latitude/longitude data for photos that have been geotagged when previewing responses [#1527]

# Resolved issues
* Device identification [#1644]
* Signature in CartoDB maps display incorrect [#1622]
* Update RemoteAPI datascripts to use service accounts [#1620]
* Disable manual input of bacode [#1522]
* Spreadsheet Import converts data to numbers regardless of the type of data expected by the associated question [#923]

#1.9.6.2
Date: 29 April 2016
# Resolved issues
* Dashboard doesn't load for users with uppercase letters in email [#1602]

#1.9.6.1
Date: 22 April 2016
# Resolved issues
* Update Portuguese translations in FLOW dashboard [#1578]

#1.9.6 - Easygoing Elephant
Date: 20 April 2016
# New and noteworthy
* Better timezone handling in export raw data reports. Consistently display dates using ISO 8601 [#257]
* Geotagging in photos: include location information, if available, in media responses [#1527]
* Portuguese translations in FLOW dashboard [#1578]
* Improve performance by dynamically loading languages in dashboard [#1602]
* Switch to Oauth2-based authentication in GAE deployments [#1625]

# Resolved issues
* Ignore blank last cell in raw data reports [#1553]
* Graphical survey summary report gives a decimal in the summary sheet [#1580]
* Update device last contact time to the dashboard [#1585]
* Tasks are unnecessarily rerun for every form response submitted [#1594]
* NPE on non-monitored survey publishing [#1611]

#1.9.5.2
Date: 15 March 2016
# Resolved issues
* API returning JSON encoded response for options whereas the API version is expected to return pipe separated strings [#1589]

#1.9.5.1
Date: 4 March 2016
# Resolved issues
* Fix an equality check that prevented the automated building of datapoint names[#1576]

#1.9.5 - Decubitus Dingo
Date: 3 March 2016
# New and noteworthy
* Enforce the use of SSL via configuration.  Access to all dashboard URLs will now be encrypted. [#1564]

# Resolved issues
* Update device last contact time to the dashboard [#1585]
* Improved French translations for both the dashboard and app [#1579]
* Dynamically build datapoint names[#1576]
* Add 'terrain' baselayer to cartodb maps [#1573]
* Displaying signature responses on cartodb point overlays [#1560]
* Caching of requests causes malfunction with user roles and permissions and reading surveys [#1182]

#1.9.4 - Clamorous Capybara
Date: 6 January 2016

# New and noteworthy
* We now have two new permissions, `management of cascades` and `management of assignments` to enable controlling which users are able manipulate these features on the dashboard [#1497]
* Expanded the rendering of geographic shapes on CartoDB maps to support 'line' and 'points' [#1495]
* Signature question - a new type of question that enables collecting of signatures of respondents to forms or to particular statements in forms [#1453]
* Codes for option questions - we now support defining a code and associating it to a particular option item.  These can be used in reports for data analysis and enables defining more advanced option type questions [#828]

# Resolved issues
* Data cleaning tab fails to load [#1542]
* Data cleaning NUMBER type can result in datastore values in scientific notation [#1534]
* CartoDB maps - render cascade resources pipe separated [#1531]
* Re-ingesting a SurveyInstance sets the wrong creationDateTime [#1530]
* Use correct HTTP methods for data related permissions [#1516]
* Date type not rendering correctly on overlay for Cartodb maps [#1512]
* Copying question groups sometimes fails [#1510]
* Fix scrolling on CartoDB maps [#1475]
* Text in in-line help box for French and Spanish versions does not fit [#1462]
* Minor Issue - Numerical values stored differently on import [#1425]
* Specific users not able to assign permissions to other users [#1416]
* Data export: don't use scientific notations for large numbers [#1381]
* Maps tab does not show the entire map [#1283]


#1.9.3 - Bouncing Badger
Date: 1 December 2015

# New and noteworthy
* Change footer links to new Support page - we have created an all new and improved support self-help page for FLOW and we include the link to this page in the dashboard. #1493]
* Add two new permissions: View data and Edit data - we now make a clear separation between read only and editing permissions when browsing collected data [#1460]
* Export geographic shapes from the dashboard - it is possible to export geographic shapes as a geojson file that can be imported into existing GIS tools that support this format [#1408]
* Geoshapes on dashboard - while browsing data on the dashboard, it is possible to see geoshpaes on a map under the inspect data & monitoring tab [#1370]
* Add codes to cascade questions - this means it is now possible to assign codes to values in cascading questions and view these codes in an exported report to aid with data analysis [#1323]

# Resolved issues
* Handle date questions correctly in raw data imports [#1486]
* Export reports tab - fix the dropdown selection so it does not disappear with scrolling [#1484]
* Enhance answerCreated and answerUpdated events [#1477]
* Change font color on subtabs [#1451]
* Remove column 'form' in Inspect data tab [#1415]
* `Next` button on Data > Monitoring tab does not function [#1299]
* No `Previous` button when browsing responses under the Data tab [#1125]
* A user with view permissions can still edit forms [#1124]
* Date question type not appearing correctly on map questions [#571]


#1.9.2 - Anticipated Alpaca
Date: 4 November 2015

# New and noteworthy
* Add import functionality for [data cleaning repeatable question groups](https://akvoflow.supporthero.io/article/show/data-cleaning-tab) [#1421] and [#269]
* [Visualisation of geoshapes](https://akvoflow.supporthero.io/article/show/maps) responses on the dashboard [#1356]
* Add the ability to [associate codes to cascade question responses](https://akvoflow.supporthero.io/article/show/cascade-resources-tab) [#1323]

# Resolved issues
* User tab does not render in Firefox [#1445]
* `Null` values in the `QuestionAnswerStore` entities cause results to be skipped in report [#1443]
* Question groups duplicated without user involvement [#1417]


#1.9.1.3
Date: 7 October 2015

# Resolved issues
* Enable dependencies to option questions within a repeat question group [#1439]

#1.9.1.1
Date: 7 October 2015

# Resolved issues
* NPE when accessing the SurveyInstance Key before it's saved into the datastore [#1432]

#1.9.1
Date: 6 October 2015

# New and noteworthy
* [Repeatable Question Groups](https://akvoflow.supporthero.io/article/show/creating-and-editing-a-survey) (Grid questions) - enables specification of a set of questions as repeatable i.e. an enumerator can ask these questions multiple times [#269]

# Resolved issues
* Positioning of save button for Question Group name [#1419]
* Folder selection drop down hidden behind calendar elements [#1409]
* Datascript: Convert PG data into events (unified log) [#1392]
* Datascript: FixSurveyedLocale datascript does not check all SurveyedLocales [#1390]
* Datascript: Export event data to Postgres [#1385]
* Request for icon_cal.png returns a 404 [#1376]
* Change data view table from Question ID to question order [#1371]
* Include in-line help in the dashboard [#1332]
* Unify vocabulary throughout the dashboard and app [#1281]
* Buttons `ADD DEVICE TO GROUP & REMOVE DEVICE FROM GROUP` not showing when scrolling [#1273]
* Recover language options (or remove all together) on Data Cleaning tab [#1126]
* Minor UI improvement - Add New Cascade button [#1044]
* Specify that the question ID is optional [#1006]

#1.9.0
Date: 18 August 2015

# New and noteworthy
* Enable the use of CartoDB as provider of maps for the dashboard [#1280]

# Resolved issues
* Append s3 bucket policy in InstanceCreator process [#1361]
* UX Design and CSS styling of data tab search text [#1346]
* Add `mapsProvider` system property [#1297]
* Wrong icon in the previous & next buttons [#1052]
* Minor UI Regressions - Missing images, spacing on edit survey screen, layout on inspect data [#809]

#1.8.9
Date: 28 July 2015

# New and noteworthy
* Improve text wrapping when displaying charts [#450]
* Filter by instance id on data tab [#872]
* Show both number of responses and percentages in charts [#1004]
* Enable 'use as data point name' for cascading questions [#1011]

# Resolved issues
* Replace path with object ids for roles and permissions authorisation [#1215]
* Layout enhancements when editing data points [#1294]
* Breadcrumb rendering issue [#1350]
* Survey copying [#1354]
* Skip QuestionAnswerStore entities referencing invalid questions [#1335]
* Optimize fetching of survey groups [#1359]

#1.8.8
Date: 8 July 2015

# New and noteworthy
* Warn the user when data is submitted for non-existing forms, e.g. forms that have been deleted [#1183]
* Changing 'download app' link on dashboard from old /app to new /app2=.  By default we will now offer the newer app version for the download link and the old version remains accessible via `/app` [#1149]

# Resolved issues
* SurveyedLocale deletion bug [#1329]
* Skip QAS referencing an invalid question [#1307]
* `Manage Device Group` popup not displayed properly [#1210]
* Send events to an event log [#1130]

#1.8.7.1
Date: 11 June 2015

# Resolved issues
* Add check for missing survey when recomputing clusters #1302

#1.8.7
Date: 11 June 2015

# Resolved issues
* Fix geocells computation [#1295]
* Multiple `SurveyedLocale` entities created for the same `surveyedLocaleIdentifier` [#1282]
* `QuestionGroup` does not resolve all dependent questions correctly when copying `Survey` [#1217]
* Bring back google map satelite layer [#919]

#1.8.6.1
Date: 29 May 2015

# New and noteworthy
* Add support for Google Maps layers as well as ability to choose region bias [#919]


#1.8.6
Date: 14 May 2015

# New and noteworthy
* Add support for Tetum language [#1263]

# Resolved issues
* Support JSON as data serialization [#1247]
* Data script for getting the number of data points and submitted forms [#1261]

#1.8.5.2
Date: 8 May 2015

# Resolved issues
* The wrong form is shown as registration form [#1267]

#1.8.5.1
Date: 27 April 2015

# New and noteworthy
* Date filtering on data cleaning tab. When downloading data via the data cleaning tab it’s now possible to select to and from dates [#1160]
* Folders and surveys are now ordered alphabetically in dropdown selections [#1212]
* Dependant questions are now resolved correctly when copying a survey [#1217, #1258]
* The checkbox for using question id in raw data exports has moved under a new “Advanced Settings” section and the text describing the option has been clarified [#1232]
* Comma characters are now disallowed as part of folder, survey and form titles [#1252]

# Resolved issues
* Log data change events [#1130]
* Script to add new permissions for existing users [#1221]
* Improvements to application build and deployments code [#1229]
* Enable form publishing after translation changes [#1231]
* Made the dashboard aware of the current instance application id [#1238]
* Date fields are no longer pre-populated with previous selections in export reports and data cleaning tabs [#1248]
* Ensure that `createdDateTime` and `lastUpdateDateTime` properties are set on copied questions [#1259]

#1.8.4
Date: 7 April 2015

## New and noteworthy

* Enable "All Folders" shortcut for a normal user when coupling a user role with a folder. An administrator who has access to "All Folders" is in turn able to assign the same to another user instead of assigning folders one-by-one as was the case previously [#1197]
* When defining questions and help tooltip text, the user will get a *maximum length* warning if the contents of either one of these fields exceeds 500 characters in length. [#1184]
* A new data deletion permission has been added. The permission to delete data can now be selectively applied per survey or per folder for each user [#1115]
* When copying a question group, we now show UI feedback informing the user whether copying is still going on or has been completed. [#857]

## Resolved issues
* Truncate the display name if it is over 500 characters [#1213]
* Disabeling creation of surveys at the first level of the survey tab [#1190]
* Disable moving questions from one form to the other [#1187]
* Delay cascade version increase until successfully pusblished [#1186]
* Review data processing task failures [#1185]
* Add confirmation to delete button cascade resource [#1170]
* Make manual loading of survey in wrong FLOW app impossible [#1071]
* Fix strange behaviour whilst editing question groups which have been copied within a survey [#857]
* Show devices in alphabetical order in assignment screen [#834]
* Export fails when NUMBER question contains invalid data [#337]

#1.8.3
Date: 12 March 2015

## Resolved issues
* Optimize cascade question deletion [#1177]
* Fix cascade question export bug [#1168]
* Hide Survey ID from dashboard [#1166]
* Fix error when revoking a user's API Key [#1156]
* Update to latest clojurescript & om releases [#1117]
* Refactor projectMigration to a data script [#1150]

#1.8.2.1
Date: 3 March 2015

## Resolved issues
* Fixed bug where deleting a cascadeResource deletes all the cascadeNodes [#1171]

#1.8.2
Date: 20 February 2015

## New and noteworthy
* Data cleaning can be restricted via permissioning [#943]
* Added the ability to filter data by date on the raw data export tab, reports are now also emailed to users [#1103]

## Resolved issues
* Fixed issue where trailing spaces in folder/survey/form names resulted in permissions problems [#1110]
* Trailing spaces in option questions no longer impact dependencies appearing [#1123]
* Permissions are now correctly merged when more than one role is applied to the same path [#1161]

#1.8.1
Date: 02 February 2015

## New and noteworthy
* Implement Geoshape question type [#1012]
* Enable deleting full datapoints and forms inside data point [#1058, #1111]
* Improve selecting survey using hierarchical dropdowns [#1053]
* Enable selection of number questions as display name fields [#924]

## Resolved issues

* Test uniqueness of questionID at save time [#905, #1040]
* Improve deleting data through test harness [#730]
* Fix dependency handling excel export of survey format [#546, #370]
* Display option questions and cascading questions correctly in preview [#486, #1072]
* Cascade resource UI issues (disappearing nodes, unpublish on change, edit level names) [#1102, #1045, #1009]
* Handle Excel import/export correctly when cascading questions are present [#1041]
* Bring back publishing per form instead of per survey [#1007]
* Add missing translations [#1055, #1049]
* Search results should be returned considering the "To Date " to be inclusive [#325]
* Clarify meaning of 'administrator' in UI [#1113]
* Deleting surveyInstance uses wrong id for notifying flow services [#1104]
* Always save email addresses as lowercase [#1098]
* Start local development server on port 8888 similar to Eclipse [#1088]
* Code improvements on RemoteAPI class [#1082]
* Check validity geoJSON with geoJSON linter and QGIS [#1070]
* Change default to sort by user name in users tab [#1067]
* Paging functionality on Users tab malfunctions  [#1065]
* Identifier search not working on monitoring tab [#1059]
* Wrong icon in the previous & next buttons [#1052]
* Include the survey title on exported survey forms [#273]

#1.8.0
Date: 15 December 2014

## New and noteworthy
* User roles and permissions. Define custom roles and permissions, and restrict access to specific folders and surveys [#818, #966, #958, #957, #956]
* Cascading question type. Create questions using hierarchical data, for example describing administrative boundaries [#189, #970]
* Survey folders and structure. Store surveys in a folder structure. A survey can contain multiple forms now [#805, #959]
* Bulk barcode scanning. It is now possible to scan multiple barcodes within a single barcode question [#888]

## Resolved issues
* Handle empty strings in pipe-concatenated geolocation responses [#920]
* Save button in assignment screen not reachable [#915]
* Question ID validation causes attributes to be copied to other questions [#903]
* Missing indexes for displayName and identifier [#900]
* Question name being blanked on hide/show [#896]
* Move dataexport System.out calls to proper logging calls [#884]
* Don't allow a user to delete a question that has dependant questions pointing to it [#853]
* Add GAE remote api tool to /config folder to streamline creation process (& document) [#811]
* Incorrect options visible for certain roles - also crashes browser [#990]
* Change of Translation Placeholders and Text [#989]
* Minor Issues with release/1.8.0 [#960]
* Enable super admin user to list all folders and forms on an instance [#934]

## Known issues

* When user has _read only_ permission and makes a change, the save action is rejected but there is no feedback in the UI [#818]
* When the changes in a form/survey are _autosaved_, the path property is not properly updated [#1019]
* A _RAW DATA_ report containing _cascade_ data, is rejected from import [#1038]
* Question ID uniqueness is no longer respected [#1040]

#1.7.6
Date: 07 November 2014

## New and noteworthy
* New questions are now mandatory by default when editing a survey [#881]
* The name of the submitter of the survey is now returned to the mobile app when syncing data [#878]

## Resolved issues
* Survey Assignment containing deleted survey causing backend failure [#887]
* Handle non-existent surveys within an assignment [#878]
* Explicitly use Locale.US on date formats [#869]
* Legacy Code cleanup [#866]
* Minor UI Fix - Long survey group names overlapping arrows [#858]
* Adding pagination support for the data > monitoring tab [#855]

#1.7.5
Date: 16 October 2014

## New and noteworthy

* External Sources Integration - This new feature enables marking a question as having responses coming from a source of data external to the mobile application, e.g., another application [#793]

## Resolved issues

* XML surveys encoding [#861]
* Survey instances are missing when generating the RAW report [#854]
* Change format of location data column header in questionId [#847]
* Store APK file checksum [#843]
* Uniqueness test for questionID sees other surveys [#842]
* Disallow allow empty-string questionId's [#838]
* Increase the request timeout on report generation [#835]
* The (textual) questionId should be part of QuestionAnswer responses [#831]
* Add scale indicator to map [#829]
* Make S3Util.put more robust [#817]
* Move the 'Question Id' field down in Question editor [#814]
* Incorrect ordering of elements in the display name [#813]
* Remove UploadConstants.properties from copyconfig task [#791]
* Javascript code small cleanup [#788]

#1.7.4.2
Date: 07 October 2014

## Resolved issues

* Include textual question identifier as part of QuestionAnswer responses [#831]

#1.7.4.1
Date: 25 September 2014

## Resolved issues

* Display Name column empty in the RAW DATA report [#797]
* Question id" value when copying questions [#807]

#1.7.4
Date: 25 September 2014

## New and noteworthy

* Enable double entry for number questions [#759] - in survey definitions, it is now possible to also select the double entry verification option for number questions

## Resolved issues

* CreationSurveyId not set on new surveyedLocales [#798]
* Screen does not scroll up [#778]
* Remove hyperlink from survey title [#775]
* Dashboard Translation for Monitoring Features Text [#774]
* Add css generation to the dashboard build process [#768]
* Use Akvo FLOW Favicon [#739]
* Data points show up as "Unknown" [#735]
* Copy functionality on Question Groups [#435]
* Add survey title and question group name to survey xml so their translation can be viewed on the device [#299]

#1.7.3.1
Date: 7 September 2014

## Resolved issues
* Survey responses not processed when a question is deleted from survey definition [#771]

#1.7.3
Date: 5 September 2014

## New and noteworthy
* Implement read API [#717] - Akvo FLOW now has a full read API! Details can be found [here](https://github.com/akvo/akvo-flow/wiki/Akvo-FLOW-API)
* Stylesheet cleaning and refactor [#609, #612] - The user interface has been given a makeover. In addition to various other details, surveys are now displayed in a list, which makes finding them easier.
* Introduce textual question identifier [#650] - A question can now be given a textual identifier, which can be used as a column heading when exporting data, and which is present in the read API. This makes it easier to transfer FLOW data to other databases.

## Resolved issues
* RAW DATA export keeps using "use question id" in Data Cleaning tab [#756]
* Copying a copied survey across instances breaks survey publishing [#744]
* Non-specified surveyGroupId query param in SurveyedLocaleRestService causes a JDOUserException [#727]
* Various small UI changes:
	* Change of wording: "record" => "data point" [#753, #740]
	* Monitoring tab style is broken [#751]
	* Dashboard tooltips are shown twice  [#745]
	* Footer layout breaks using Spanish|French and low resolution [#570]
	* Master language tooltip incorrect [#442]

#1.7.2.2

## Resolved issues

* Deployment enhancements - Instance Configurator [#724]

#1.7.2.1

## Resolved issues

* Data points show up as "Unknown" on non-monitoring groups [#735]

#1.7.2

## New and noteworthy

* The new APK v2.0 is now available in `http://instance.akvoflow.org/app2` [#666]
* Introduced more consistent counts, fixing inaccuracies between the Dashboard's
  chart builder and the _RAW DATA_ report. [#651]
* Revamped the way of transferring data to/from _Object Store_ (Amazon S3) [#690]

## Resolved issues

* Code cleanup - Monitoring related code [#643]
* Adapt _bootstrap_ files to support _Monitoring_ features [#695]
* Spreadsheet importer for the RAW DATA report misidentifies the columns [#697]
* Disallow the usage of commas in _Survey Group_ names [#707]
* Invalid XML characters in survey group name breaks the survey definition [#714]
* signingKey is a required system properties [#725]

#1.7.1

## New and noteworthy

* Added support for storing more than 500 characters in a response [#656]
* Added support for exporting _RAW DATA_ report for monitoring and non-monitoring groups [#653]

## Resolved issues

* Limit the number of retries for queues [#688]
* Use https connections when possible [#680]
* Publish Survey fails for Copied Surveys [#664]
* Submitter name missing in new records [#662]
* Update survey.properties file for the new APK version [#660]
* Code cleanup - Remove custom JSON* classes from the code [#585]
* Dashboard deployment enhancements
  * Upgrade gem bundle for Ember part of Dashboard build [#678]
  * Critical build tasks that fail should also fail the main build process [#675]
  * Dashboard rollback task should also rollback failed backends [#672]
  * Ensure Dashboard build uses Java 7 [#668]
  * Update RubyGems sources for Dashboard build to prevent deprecation warnings [#667]
  * Ember build should ensure bundled rake is used [#665]
  * Ensure Dashboard can be built with GAE SDK 1.9.1 [#483]


#1.7.0.3

## Resolved issues

* Submitter name missing when importing RAW DATA spreadsheet [#662]


#1.7.0.2

## Resolved issues

* Survey response values longer than 500 characters cannot be stored [#656]

#1.7.0.1

## Resolved issues

* Survey response values longer than 500 characters cannot be stored [#656]

#1.7.0

## New and noteworthy

* Monitoring features — Monitoring features enable users to download data on existing points on their phones, and add information to these points. Regular survey forms can be used to register entities, so they can be retrieved later for further data collection. This makes it possible to see how data has changed over time, or to collect new properties of the same entity.


## Resolved issues

* Fix typo in home screen [#615]
* Validate lat/long data while creating geocells [#613]
* SurveyedLocale's lastModified timestamp might not be unique [#605]
* Remove code that sets wrong path [#602]
* Adds lastUpdateDateTime on SurveyedLocale [#591]
* Get collectionDate from surveyInstance as well [#583]
* Add dashes to identifiers created in the server [#582]
* Retrieving Available Surveys for a Device Fails [#578]
* Bulk update monitoring fields on surveyedLocales [#575]
* Enable deletion of entire surveys and question groups [#368]
* Control Mononitoring UI based on config property [#607]

#1.6.17.1

## Resolved issues

* Problems with automatic survey download on _WiFi only_ devices [#588]

#1.6.17

## New and noteworthy

* The Dashboard now has a direct link which lets users download the mobile app, you
  can find it in the footer __"Download FLOW app"__ [#565]
* When showing the details of a _data point_ on the map, the questions
  are sorted based on the survey definition (previously they were sorted
  alphabetically) [#239]

## Resolved issues

* Survey transfer issues [#420]
* Add a survey name to the survey id when copying [#399]
* Removed GWT config file generation from InstanceConfigurator [#453]
* Removing responses under the data tab should update the map [#523]
* Adds URI to download a GPS status app [#572]

#1.6.16

## New and noteworthy

* WiFi only devices (e.g. Tablets with no SIM card) are now
  distinguished by MAC address [#320]

## Resolved issues

* Removed obsolete GWT code [#497]
* When transferring surveys across instances, settings like _other_,
  _allow multiple_ and _dependencies_ are now copied [#504]

## Known issues

* Some survey configuration settings are still missing when copying,
  see [#420]

#1.6.15

## Resolved issues
* Fixed issue of performing the same data request 2 times on
  Survey preview/edit [#527]

#1.6.14.4

## Resolved issues
* Fixed wrong computation of cache value for cluster locations [#519]

#1.6.14.3

## Resolved issues
* Avoid NPE when lastSurveyalInstanceId field is null (as happens on old data) [#516]

#1.6.14.2

## Enhancements
* Show more detailed clusters at a lower zoomlevel [#363]

#1.6.14.1

## Resolved issues
* Compute lat / lon of cluster as average over all containing placemarks [#508]
* Use cluster Id as key id to ensure all clusers have a unique id on all zoomlevels [#509]
* Avoid keyId collision between clusters and individual placemarks [#510]
* Perform adapt cluster data as separate task, to deal with non-availability of memcache [#363]

#1.6.14

## Enhancements
* Implement server-side clustering of markers on maps [#363]

## Resolved issues
* fixed translations in chart builder [#494]
* fixed link to FLOW terms of use [#432]


# 1.6.13

## Enhancements
* Prepare the Dashboard for responding to _Monitoring_ related requests [#487]

## Resolved issues
* Fixed: Deleting an assingment didn't clean all related data [#490]


# 1.6.12

## Enhancements
* Completed translations for all UI elements in French and Spanish. Thanks Iñigo, Iván, Valentin, and Emeline! [#474, #436, #440]
* Add K'iche', the second dominant language in Guatemala, as an available translation language [#473]
* Enhance build process: automatically rebuild dashboard code when updating an instance [#478]

## Resolved issues
* A change in dashboard language now refreshes the whole page, to prevent rendering issues [#474, #163]

# 1.6.11

Release date: 6 March 2014

## Enhancements
* Increase loading speed of translations [#455]

## Resolved issues
* Fixed: Failing uploads due to race condition on bulk upload process in some browsers [#465]
* Fixed: Ignore __MACOSX folders created by MacOS in zip files during bulk upload [#464]
* Fixed: Missing translation key in translation tab [#449]

## Known issues
* When working with translations, if the first question group contains a large number of
  questions (30+) the UI locks up for a few seconds. See [#469]

# 1.6.10

Release Date: 18 February 2014

The enhancements and resolved issues described work with apk version 1.13.0.

## Enhancements
* Automatically download new APK versions (#457). From now on, whenever there is a new version of the FLOW app, the user will be notified and can download and install the new version
* Implement a way of checking if an image is present in the remote object store (#456). When images don't make it to S3, there is now a fallback system that guarantees that images are retrieved from devices.
* Warn user that a survey group and survey have not been selected when trying to upload data (#461)
* Implement double entry functionality (#427)

## Resolved issues
* Fixed: On importing data, information on the original upload is discarded (#458)
* Fixed: Change geonames api call (#454)
* Fixed: Spreadsheet importer needs to handle empty columns (#445)
* Fixed: Remote survey importer fails when there is a large amount of survey groups (#444)
* Fixed: Deprecate the GWT and applet code compilation (#448)
* Fixed: Path to video in raw data export should point to S3 (#462)
* Fixed: When viewing the map, video links are not shown on survey points (#403)

# 1.6.9

Release Date: 08 January 2014

## Enhancements

* Add filter on sub-country regions to data tab and raw data report (#229)
* Show statistics on questions which have a metric (#231)
* Note: Both functionality is only available when the instance is configured to
  show the statistics tab

## Resolved issues

* Fixed: Deprecate the compilation of GWT and applet code (#448)
* Fixed: Remote survey importer fails when there is a large amount of survey groups (#444)

# 1.6.8.1

Release Date: 23 December 2013

## Resolved issues
* Fixed: Set the uuid and userID when importing a file (#446)

# 1.6.8

Release Date: 11 December 2013

## Enhancements
* Increase the default number of points shown on map to 2000 (#441)
* Format survey duration as hh:mm:ss (#439)

##Resolved issues
* Fixed: Implement robots.txt (#437)
* Fixed: Changing dashboard language removes survey map points (# 431)
* Fixed: Copy survey message does not have any value under 'Survey name' (#397)


# 1.6.7.1

Release Date: 20 November 2013

## Resolved issues
* Fix the logic for SurveyedLocale reset (#396)


# 1.6.7

Release Date: 08 November 2013

## Resolved issues
* Fixed: Survey preview number styling looks strange (#401)
* Fixed: Prevent user from going to notifications when there are unsaved changes (#405)
* Fixed: User can create a new survey without completing the title field (#406)
* Fixed: Quick fix - persmission level tool tip has unused "Project Editor" role (#409)
* Fixed: Quick fix - notification email dashboard spelling mistake (#410)
* Fixed: When previewing a large survey it would be nice if the exit button remained visible (#412)
* Fixed: Prevent user from creating empty assignments (#413)
* Fixed: MINOR - UI style issues in header/footer on the dashboard (#415)
* Fixed: Survey status not updated on some survey changes (#422)
* Fixed: Not possible to export data of two surveys consecutively (#423)
* Fixed: Add requirements and design documents to the code base (#424)
* Fixed: Issue with deleting survey groups (#425)
* Fixed: Unpublished copied surveys can appear in the assignment list (#426)

# 1.6.6.2

Release Date: 24 October 2013

## Enhancements
* Make it harder for user to upload data to wrong survey (#408)
* Add ability to lock manual edit of GEO question (#418)

## Resolved issues
* Fixed: Importing a spreadsheet fails silently with a JS error (#416)

# 1.6.6.1

Release Date: 24 October 2013

## Enhancements
* Provide method for bulk change locale type (#396)

## Resolved issues
* Fixed: Chart Builder should clean the previous chart on each question (#365)
* Fixed: Terms of service link broken on FLOW instances (#400)

# 1.6.6

Release Date: 18 October 2013

## Enhancements
Thanks to the efforts of the Water for People team, we now have a complete set of Spanish translations of the dashboard.

## Resolved issues
* Fixed: Chart Builder should clean the previous chart on each question. (#365)
* Fixed: 'No data available' warning still shown when data is available for chart. (#388)
* Fixed: Increase performance of loading questions for charts. (#379)
* Fixed: When showing a surveyGroup with a lot of surveys, scrolling behaviour is strange. (#387)
* Fixed: Make summary count more robust to possible QAS duplicates. (#385)
* Fixed: Trim user email address when creating a new user. (#384, #366)
* Fixed: Make reports support languages other than english. (#381)
* Fixed: Update FLOW logo. (#378)
* Fixed: Move copy survey functionality to backend to avoid timeouts on large surveys. (#377)
* Fixed:  Bring back language dropdown and incorporate new spanish translations. (#376)
* Fixed: Update instance creation templates with latest config. (#374)
* Fixed: Improve efficiency in saving of surveyedLocales. (#373)
* Fixed: Translations does not get copied when we copy a Survey. (#357)
* Fixed: Prevent user from going to translations when there are unsaved changes. (#389)
* Fixed: Better implementation of sort functions. (#394)
* Fixed: Opt in a UUID implementation for `generateUniqueIdentifier` (#391)
* Fixed: Fix faulty sort functions (#394)


# 1.6.5

Release Date: 30 September 2013

## Resolved Issues

* While uploading spreadsheet, the submitter name gets altered (#367)
* Surveys with same response but different UUID getting skipped as duplicate while processing (#369)
* Issue with data Cleaning: Blank responses are getting added as records (#371)

# 1.6.4

Release Date: 17 September 2013

## New Features

* Record the duration of a survey (#356)

# 1.6.3

Release Date: 13 September 2013

## User interface
* Translations are now loaded and edited per question group instead of all at once. This improves behaviour for large surveys with many questions.
* Block moving to the Translation screen when not all questions have been loaded.
* Print a 'no data available for this question' message when a user tries to see a graph for a question which has no data.
* Fix a bug in the question ordering which manifested itself when a user navigates from editing a survey to the survey overview and back, using the 'back to survey overview' button.
* Fix order of questions in survey preview screen
* Improve the loading indicator to not hide when the first query result comes in, but keep a count of all the running requests and hide when all are done.

## Backend
* Repair image path in raw data report for data, which showed the device path when data was collected by a tablet
* Implement administrator tool to bulk delete data through the API
* Fix a bug which caused question options not to be copied when a survey is copied.

# 1.6.2

* Added Casper.js automated dashboard tests
* Implemented testRail environment for keeping track of tests
* Block moving/copying/deleting of questions while a previous request is still busy
* Fix a bug which caused the question order to go wrong

# 1.6.1.3

* Small change to developers access of GAE admin pages

# 1.6.1.2

* Added Casper.js dashboard tests
* Deploy _backends_ to Google App Engine, which handle long requests

# 1.6.1.1

* Allow FLOW developers to use the remote api

# 1.6.1

## Improvements to reports and data files
* Export numbers in Excel reports in number format (double) (#267)
* Fix a bug where data counts on the Cardno dashboard didn't match between charts and exported reports due to duplicated entries in the `QuestionAnswerStore` (#305)
* Allow raw data file export in text file format (.txt) (#312)

## Security and user permissions
* Restrict access to TestHarness servlet to only SuperAdmin role (#310)
* Modify dashboard user permissions to restrict delete data action to Admin level users only (#314)

## Improvements to survey question editing
* Fix a bug where the order of survey questions was not being reliably maintained on the user interface (#211)
* Enable copy and move of questions across question groups (#321)

## Dashboard interface
* Disable "Include DB instructions" from Manual Survey Transfer selection screen in the Devices tab since it's an advanced feature that's not used (#313)
* Hide dashboard language dropdown changer until after Ember refactor (#322)

## Deployments and infrastructure
* Generate Google Web Toolkit code only for webkit browsers (Chrome, Chromium, Safari etc) for emergency support for FLOW 1.0 dashboards to improve deployment compilation speed (#300)

## Misc
* Fixed a bug where a new survey assignment was not storing the IMEI of the device and relying on the phone number (#298)
* Fix a bug where serial number of data records in Inspect Data tab not displayed in the correct sequence (#302)
* Fix a bug where device information was sometimes not sent from the device or recorded on the dashboard in the Devices table (#306)


# 1.6.0
Release Date: 18 July 2013

This is a combined release of the Akvo FLOW Dashboard and Field Survey app whose major feature is survey translations. Survey translations allow users to enter multiple translations for a single FLOW survey so that data collectors in the field can conduct a survey in their local language.

We’ve made 181 languages available in the language list, but any language with a non-Roman alphabet or any language that reads anything other than left-to-right is experimental at this point.

In order to take advantage of the survey translations feature, users must be running both the 1.6.0 Dashboard or higher and the 1.11.0 Field Survey app or higher.

## Survey translations
* Implement survey translations for FLOW Dashboard (#177)

## Interface and usability improvements
* Temporarily hide unused items on Dashboard (#253)
* Revert to creating short survey IDs to adapt to GAE datastore change that started creating very long IDs (#254)
* Fix a bug where Edit data window wasn't loading questions correctly while navigating between records from different surveys (#281)
* Fix a bug where survey groups weren't sorting alphabetically in dropdowns in Devices, Data and Reports tabs (#286)
* Enhance map placemark detail pane to show all available photos for a survey taken at that point (#289)
* Enhance map placemark detail pane to display survey questions in alphabetical order (#291)
* Add version to footer to show user what version the Dashboard is running (#294)

## Deployments and infrastructure
* Upgrade included jar files for Dashboard to GAE SDK 1.8.1 (#274)

## Bug fixes and misc
* Resolve emberjs deprecation warnings on flowaglimmerofhope dashboard (#225)
* Fix a bug where operations on `/survey_instances` endpoint weren't triggering _cache invalidation_ messages to FLOW services (#265)
* Correct the displayed parameter list for InstanceConfigurator utility (#288)


# 1.5.1
Release Date: 4 July 2013

Improvements to data summarization and counting
* Fix defect where data submitted over wifi and the bulk upload was double counted in data summaries (#185)
* Fix a bug where the surveyInstance Count was creating new entities each time instead of checking first whether there was a matching one already existing (#235)
* Fix a bug where spreadsheet import was using different summarization customs than the device, resulting in confusion in the backend for OTHER types (#250)

Improvements in Data tab and Inspect Data table
* Fixing filtering in Inspect Data table - Fix a bug in filtering for Device ID and Submitter in Inspect Data table (#212)
* Fix a paging bug in Inspect Data table where cursor reset to zero (#251)

Improvements to Bulk Upload tool usability
* Prevent bulk upload tool from importing data for a survey that doesn't exist on the Dashboard (#230)
* Warn user if they try to close the browser page while a data bulk upload is in progress; add explanation text on the Bulk Upload page to direct the user not to navigate away during the upload (#201)

Security
* Enable security on REST calls based on API servlet (#256)
* Update FLOW instance configurator to create API key and enable REST security (related to #256) (#272)

Other minor enhancements
* Entering user email addresses - Save email addresses in lowercase when adding or editing a new Dashboard user (#193)
* Fix defect where surveyedLocales remained in the datastore after surveyInstances were deleted (#218)
* Fix a bug where deleting a date in the Edit data window displayed filler text (NaN) in the date field (#236)
* Fix a bug where a large number of survey groups broke the css box for the display and the group names stopped displaying correctly (#242)
* Fix a bug where services.akvoflow.org was dishing up stale reports (#246)
* Fix bug in validation of min/max parameter on type=Number survey questions where string values were being compared instead of int values (#258)
* Increase maximum map place mark points from 200 to 500 to improve map performance (#263)
