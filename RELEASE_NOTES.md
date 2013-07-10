Last changed: 9 July 2013, co

Akvo FLOW (Field Level Operations Watch) is a system to collect, manage, analyse and display geographically-referenced monitoring and evaluation data.

Read more about [Akvo FLOW](http://www.akvo.org/blog/?p=4836).
Read more about the [Akvo Platform](http://www.akvo.org/blog/?p=4822).

Akvo FLOW Dashboard release notes
----

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

---


This set of notes captures the weekly status of FLOW features and fixes to keep our colleagues and partners better informed about the status of the software as we concentrate on roll-out of 1.5.

* **Completed + Live** means the feature/fix has been tested and deployed to all active FLOW Dashboards.
* **Completed + Pending** means the feature/fix has been done and is either in testing or waiting for deployment.
* **In Progress** means the team is working on it this week.
* **Other Known Issues** means we've heard from you that it's a request or a problem, but we haven't worked it into the priority list yet.
* **Backburner** means we know about it, but have put it aside for the moment while we work on higher priority issues.

They also sometimes contain a head note highlighting something we are working on or a solution to a problem that might be of interest to everyone.

Numbers in parantheses indicate the github issue number related to the item.

2 April 2013, co
---

Some of you will be interested to know that we've been able to diagnose the photo transmission problem that has been bothering many of our users -- photos were not making it to the server although the data were. It came down to a connectivity issue. Essentially, connectivity can appear to be ok, but may actually be inconsistent. The zip files containing the data are fairly small and can slip through, but the photos take a more consistent connection to successfully transmit. It may be entertaining for you to know that Stellan recreated this issue by submitting data from a device he placed inside an antistatic bag. So, as part of issues 182 and 183, we are making some changes to how the app handles photos, and communicates success or failure to the device user which we hope is helpful.

Completed + Live
---
* Nothing new since last week

Completed + Pending 
---
* Improvements to the way we load countries to the maps, making it easier to add a new country (161, 139)

In Progress
---
* New 1.5 Dashboards for ICCO and Mars
* Addressing an issue where Water For People is having trouble publishing surveys from their Dashboard, related to the Google App Engine environment (186)
* New reporting service for data export is receiving further improvements after user testing on the MWA Dashboard, preparing for wider rollout next (164, 179, 181, 184)
* Survey translations - implementation of user interface design (177)
* Updating data summaries (chart builder totals; 158, 185)
* Improvements to Bulk Upload tool (185, 170)
* Improvements to photo transmission from device (182, 183)
* Upgrading our user interface framework to the newest version (154)

Other Known Issues
---
* Household data are showing on public map when they shouldn't be (176)
* Date conflict on Inspect Data filters (178)

Backburner
---
* copying surveys between survey groups (140)
* supporting newer devices with dual or no SD cards
* Orange "Loading" icon persists even if load fails (or is really slow)
* After deleting survey assignment from the Dashboard, the survey still shows in the Field Survey app even after survey reload

26 March 2013, co
---

Completed + Live
---
* Ability to delete single data records from Inspect Data (122)
* Improvements to the way the interface handles mix/max values, decimal points for number questions (166, 168, 169, 175)
* Added link to image in Edit data window that user can open in a new window (165)
* Fixed, Device IDs and submitter names were sometimes not showing in DEVICES tab (131)
* Fixed, user could create a new survey without making the required 'Type' selection (128)
* Fixed, survey questions and question groups were not deleting properly, affecting multiple dependencies and question order (171)
* Fixed, Device list was not loading in Create Assignment selection boxes (173)

Completed + Pending 
---
* New reporting service is in first rounds of testing, for EXPORTING data only (the beginning of the end of the applets!) 

In Progress
---
* Survey translations - implementation of user interface design (177)
* Investigating photo transmission (172)
* Updating data summaries (chart builder totals; 158)
* Improvements to the way we load countries to the maps, making it easier to add a new country (161, 139)
* We have made some improvements and fixed some bugs (159, 160) related to the Bulk Upload tool, but beware if you are using this that some users are still having difficulties, and we are working on those (170)
* Upgrading our user interface framework to the newest version

Other Known Issues
---
* Reporting service errors (164)
* Household data are showing on public map when they shouldn't be (176)

Backburner
---
* Orange "Loading" icon persists even if load fails (or is really slow)
* After deleting survey assignment from the Dashboard, the survey still shows in the Field Survey app even after survey reload

19 March 2013, co
---
We are aware that not being able to delete individual data records is preventing smooth operation in trainings. We are working on implementing this functionality this week. In the mean time, if you need to collect test or training data that you know you will need to delete, please make a copy of the survey within the survey group and use that copy for collecting the test data. Then when partners need to collect the real data, you won't have to deal with deleting records at all. This is actually better practice anyways.

Completed + Live
---
* New deployments of 1.5 to Dutch WASH Alliance, Football for Water, WASH United
* Survey questions can be dependent on multiple responses to a previous question (multiple dependencies; 138)
* Public map is live from xxxx.akvoflow.org (ie mwa.akvoflow.org) (yay!)
* Fix for bugs that prevented dependencies from copying over when you copied a survey, and didn't clear a dependency when you deleted it (146, 155)
* Fix for a bug that didn't publish the correct survey version (150)

Completed + Pending 
---
* New reporting service is in first rounds of testing, for EXPORTING data only (no more applets!) 
* Enforcing min/max values on the device for number questions (144)
* Improvements to the way we load countries to the maps, making it easier to add a new country (161, 139)

In Progress
---
* Ability to delete single data records from Inspect Data (122)
* Survey translations
* Investigating photo transmission
* Updating data summaries (chart builder totals; 158)
* We have made some improvements and fixed some bugs (159, 160) related to the Bulk Upload tool, but beware if you are using this that some users are still having difficulties, and we are working on those (170)
* Improvements to the way the interface handles mix/max values, decimal points for number questions (166, 168, 169)
* Upgrading our user interface framework to the newest version

Other Known Issues
---
* Installing an app from one dashboard over another one from another dashboard still shows the surveys from the old app, and you can still submit data. SOLUTION: if you need to do this, as many of us do, just Uninstall the first app before installing the new one, and you won't see the vestigial surveys. Over time we'll improve this, but for now this is the best practice.

Backburner
---
* Orange "Loading" icon persists even if load fails (or is really slow)
* After deleting survey assignment from the Dashboard, the survey still shows in the Field Survey app even after survey reload

12 March 2013, co
---
One note - we have discovered a problem where points mapped close to country boundaries are not being correctly located in the right country, and are therefore not always showing on the map. We have figured out how to fix this, and will be scheduling this fix for the first week of April, since it will require a short period of downtime which I will contact the relevant partner team members about.

Completed + Live
---
* Adjusted code so that if users press enter between options on Question Edit screen, this does not result in extra lines between options in Preview and on App
* Fix for App home screen layout issues (cutoff halfway down screen)
* Added version number to bootstrap survey zip file
* Deployments of 1.5 to FXB and Congo (now RCLWSC) Dashboards
* New script to automate deployment to all active Dashboards

Competed + Pending
---
* Fix for options and dependencies not saving on Question Edit screen (one fix committed, still testing and confirming)

In Progress
---
* Service to run reports without Java applets
* Display FLOW map in public URL
* Devices IDs not consistently showing in DEVICES tab
* Upgrading our user interface framework ember.js to the newest version
* Implementing ability to delete single raw data records
* Implementing ability to make questions dependent on multiple answers
* Max value not saving in Question edit screen for number questions
* New Dashboards: WASH Alliance, WASH United, Football for Water

Other Known Issues
---
* Transmission of photos - we know about this and will be looking into it as soon as we can
* After deleting survey assignment from the Dashboard, the survey still shows in the Field Survey app even after survey reload
* Totals on Chart Builder not reflecting number of submitted surveys
* Orange "Loading" icon persists even if load fails (or is really slow)

Backburner
---
* Implementing survey translations (dependent on finishing ember.js upgrade)
* Improving geolocation service for mapped points

05 March 2013, co
---

Completed + Live
---
* Increased map placemarks so that 200 load instead of 20, to address Amit's issue that all points aren't showing on the map (training.akvoflow.org)
* Bootstrap file doesn't contain version info (training.akvoflow.org)

Completed + Pending
---
* deployment of 1.5 to salone.akvoflow.org (still waiting for apk)
* Option questions saving with blank options
* Device home screen layout issues
* Extra lines between options when a user presses return when entering options

In Progress
---
* Options and dependencies not saving (mwa.akvoflow.org)
* Deployments of 1.5 to FXB and Congo dashboards
* Display FLOW map in public URL
* Adjust geolocation service to improve display of points close to country boundaries on maps
* Device IDs not consistently showing in DEVICES tab (mwa.akvoflow.org, others)
* New service to get rid of Java applets (will help with slow report running)

Other Known Issues
---
* Transmission of photos - we know about this and will spend more time scoping out some adjustments later this week
* After deleting survey assignment from the Dashboard, the survey still shows in the Field Survey app even after survey reload
* Totals on Chart Builder not reflecting number of submitted surveys
* Orange "Loading" icon persists even if load fails (or is really slow)

Backburner
---
* Max value not saving in Question edit screen for number questions
* Implementing survey translations on Dashboard (leftover from FLOW Classic)
* Implement multiple dependencies for survey questions (leftover from FLOW Classic)
* Implement ability to delete a single raw data record (leftover from FLOW Classic)


Akvo FLOW Field Survey app ver 1.9.36 release notes
---
8 July 2012, co

Overview
----
This release contains improvements to the FLOW Field Survey application related to the storage of files on the device, a new keystore for app generation for easier installation of updates, and a new feature to allow users to scan barcodes from within a survey on the application.

New features & enhancements
----
### New feature: Barcode question type in surveys
A new question type that allows users to scan a barcode from inside their FLOW survey while collecting data in the field. The user selects the question type from the Survey Manager on the Dashboard when creating a survey, and then on the device this question prompts the user to "Scan Barcode," which calls an external barcode scanning app, and then populates the text field with the barcode number. REQUIRES an external barcode scanning app to be installed on the device. Barcode scanning apps that have been tested for this feature are: ZXing and QuickMark.

GitHub issues:
[17](https://github.com/akvo/akvo-flow/issues/17), [59](https://github.com/akvo/akvo-flow/issues/59)

### Enhancement: Enable users to store large numbers of surveys and photos on device SD card
Users can now store very large numbers of survey zip files and photos on their device's SD card. This is necessary when data collectors are going for long periods offline, where surveys must be stored on the devices for later upload. Since there is a limit for the number of files that can be stored in each directory independent of SD card storage capacity, this is achieved using a new file structure that distributes the files into multiple directories, instead of just storing them in the SD card root directory as before.

GitHub issue:
[16](https://https://github.com/akvo/akvo-flow/issues/16)

### Enhancement: New keystore for apk generation to eliminate signature conflicts
Created and distributed a release keystore along with instructions for use so that there is a single signature for the Field Survey application. This solved the problem from previous versions where, when multiple keystores were used to generate the app, users would face a signature conflict when trying to update the app on the device that forced them to uninstall the existing app before installing an update.

GitHub issue:
[21](https://https://github.com/akvo/akvo-flow/issues/21)


Bug fixes
----
### Email address of user not displayed correctly in user management

GitHub issue:
[51](https://https://github.com/akvo/akvo-flow/issues/51)


Known Issues
----
### Compatibility issues with Android OS version 2.1

Barcode Scanning
External barcode scanning apps (QuickMark, ZXing) crash or freeze in Android OS 2.1. As a result, we have not made this feature available for the apk running on 2.1. You can still enter barcodes manually into surveys when a barcode question type is present.

GPS Status App
Cannot reliably launch GPS Status app from inside the Field Survey application running on Android OS 2.1 (Settings > GPS Status).
