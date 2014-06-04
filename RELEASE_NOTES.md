Akvo FLOW (Field Level Operations Watch) is a system to collect, manage, analyse and display geographically-referenced monitoring and evaluation data.

Read more about [Akvo FLOW](http://www.akvo.org/blog/?p=4836).
Read more about the [Akvo Platform](http://www.akvo.org/blog/?p=4822).

Akvo FLOW Dashboard release notes
----
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

