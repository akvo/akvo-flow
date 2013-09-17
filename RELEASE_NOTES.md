Last changed: 17 September 2013

Akvo FLOW (Field Level Operations Watch) is a system to collect, manage, analyse and display geographically-referenced monitoring and evaluation data.

Read more about [Akvo FLOW](http://www.akvo.org/blog/?p=4836).
Read more about the [Akvo Platform](http://www.akvo.org/blog/?p=4822).

Akvo FLOW Dashboard release notes
----

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

