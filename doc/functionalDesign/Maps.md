## Maps

#### High-level requirements
1. Display maps
2. Filter data
3. Export maps

#### Requirements
###### 1. Display maps


###### 2. Filter data


###### 3. Export maps




















Summary: requirement for FLOW 1.5 is to replicate the current 1.0 functionality in the new user interface, with minor alterations to the point type data model and better user-facing integration of the metrics into map display of survey data.

Requirements
------------
**1.5**
^^^^^^^
	* Map displays in Maps tab on Dashboard so that Dashboard users can view map data from within the Dashboard page, and also displays on a publicly-accessible URL so that organizations can share their map with anyone with access to a web browser
	* Map data must also be exportable to a Google Earth file (.kmz)
	* Map displays all surveyed points where point type = access point (formerly waterpoint), and public institution; map does not display data with point type = household
	* Surveyed point = surveyedLocale or accessPoint, survey must have contained "geo" question type in order for a surveyedLocale to be created
	* Map points are displayed using Akvo-designed icons that denote point type
	* Map displays a legend explaining point icons
	* When clicked by user, each point displays a window with a photo of the point, a set of basic information and selection of survey data as determined by the user-defined metrics (more detail below)
	* Map contains dropdown with list of all countries with data for that Dashboard so that users can easily navigate between countries where they have FLOW data
	* Map data are displayed on Google Map platform
	* Map has a footer that contains name of organization(s) that collected the data, link to akvo.org or "powered by Akvo" (need to ask Thomas about this)
	* Water For People map already has styling and we should carry that through into 1.5 on their public map

Non-requirements 
----------------
ie, things we will not do in this version

**1.5**
^^^^^^^

	* Points are not color coded or otherwise scored or categorized beyond point type
	* No zoom levels on maps
	* No point groupings or data aggregation
	
Details
-------

Country Selector Dropdown
^^^^^^^^^^^^^^^^^^^^^^^^^

**Summary**: FLOW Dashboard map needs a dropdown with list of all countries with data for that Dashboard so that users can easily navigate between countries where they have FLOW data.

**Requirements**
	* Dropdown contains all the countries with data available on that instance. 
	* When user selects a country, they should land on the country centroid (can be manually set) at a zoom level appropriate to see the whole country, or most of it, in the map view.
	* Countries in dropdown should be listed in alpha order.
	* List of countries in dropdown should be able to update and re-order after a page refresh or at some reasonable interval when new data from new countries are added.

**Questions**
	* What would make sense as a default landing point?


Data Window
^^^^^^^^^^^
**Requirements**
	* Point Code (surveyedLocale or accessPoint Unique Code)
	* Submission date (YYYY-MM-DD)
	* One photo, able to display landscape or portrait
	* Photo should link to URL that users can open in new browser window, eg http://waterforpeople.s3.amazonaws.com/images/wfpPhoto6308908093912.jpg
	* If there is no image available for the point, we need a generic placeholder image
	* Remainder of the data displayed in the window is determined by the metrics the user has selected. If user has not selected any metrics to display, there are no other data displayed in the window.


To Come:

Mapping platform requirements

Point types - currently waterpoint, sanitation point, public institution, household; should be access point, public institution (ie school, clinic), and household

Next step is to have a sector tag (ie water, sanitation, health, food security etc)

Metrics

