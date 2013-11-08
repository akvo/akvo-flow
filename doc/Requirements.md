## Purpose
The mission of the Akvo Foundation is to increase the quality of development processes by providing organisations with open source tools for knowledge sharing, online reporting, and data collection. To accomplish this, Akvo is working on an open source suite of tools under the label end-to-end transparency — transparency all the way from the allocation of government ODA funds, down to individual projects, down to the detailed impact data in the field. Akvo FLOW is part of this suite of tools.

Data is a valuable commodity for the organizations working to deliver better services to those who lack them. Having the right data can drive smarter decision-making and make development projects more efficient, more effective, and more appealing to funders.The ultimate goal of Akvo FLOW is providing governments and organizations an open, easy-to-use, affordable way to collect and understand this data. Experience has shown that use of Akvo FLOW significantly reduces errors in data collection, increases the speed and convenience of collection, survey management and data sharing and analysis, as well as increasing transparency in the data flow. 

The outcome we aim for is that using the Akvo FLOW service allows organisations and governments to do their (monitoring) work more efficient, more effective, and more transparent. A second expected outcome is that using Akvo FLOW leads to more transparency in the sector. In the end, the impact of Akvo FLOW is the benefit it brings to the processes and systems it helps manage: pumps that are monitored stay working longer and are reparied earlier, client satisfaction interviews leads to better services in hospitals, open data leads to more public scrutiny and less corruption, etc. 

Expected areas of change for organisations using Akvo FLOW:
* easier data collection, leading to more and high-quality data
* increases of speed of data collection
* accuracy of data collection - reduction of data entry errors
* better usage of data analysis and visualisation for decision making
* More users of collected data
* Easier management of surveys

A quote from Keri Kugler, Monitoring expert at the US-based NGO Water for People: "Using the survey tools, we speak with community members, find out if water service is reliable, whether someone can fix problems, and better understand ongoing issues,” Kugler wrote. “This kind of monitoring is a cornerstone to sustainable water solutions across the developing world."

## Scope
The goal of Akvo FLOW is to provide organisations with an affordable and reliable service to collect, manage, analyse and display geographically-referenced monitoring and evaluation data, offering fast data collection, survey flexibility, analytical tools for data-driven decision making and visual reporting of results.

Due to the wide availability of Android-based devices at low cost, the Akvo FLOW at the moment exclusively runs on Android devices.

FLOW users create surveys that can include text, photos, video, and GPS coordinates. Smartphones can store hundreds of surveys and collect data even where there is no cellular connection. The data automatically gets transmitted once the user has a mobile connection, or can be manually transferred.

## Akvo FLOW overview
The Akvo FLOW system consists of two parts: an online system for managing surveys and data, and an android app. Detailed functional design specifications [are available here](FunctionalDesign.md).

#### Dashboard
The FLOW Dashboard is the web-based location where users access and manage their FLOW data. All the data collected through FLOW is stored and processed in the Dashboard. It is also where users can run reports, create and edit surveys, and edit data. Each organization has its own URL to access its online Dashboard.

The Dashboard is made up a series of pages. Across the top bar there are a series of menu items:
* Surveys - Create, edit and publish surveys
* Devices - Manage the devices connected with your Akvo FLOW Dashboard
* Data - View, edit, import and clean data collected with Akvo FLOW surveys
* Reports - Viewing and exporting data and results from your surveys
* Maps - Shows the surveys collected with a GPS location as points on a map
* Users - Defining users and their permissions
* Messages - Lists messages on your dashboard related to activity with surveys and data processing activity
* Admin - contains activities for super users (typically Akvo staff)

#### FLOW app
The FLOW app is an Android app that runs on phones or tablets. Users can log in to the app, download surveys, fill in surveys, and submit data. Users can also download data, if monitoring features are used.

## Use cases
We have defined a number of user roles, representing ways that people with different roles can interact with the system. Each roles has a detailed description in the form of a persona. The user roles are:
* [Enumerator](useCases/Enumerator.md)
* [Field Manager](useCases/FieldManager.md)
* [Project Manager](useCases/ProjectManager.md)
* [Director](useCases/Director.md)
* [Donor](useCases/Donor.md)
* [External data user](useCases/DataUser.md)

We have also formulated a number of [use case examples](useCases/UserCaseExamples.md).