/* jshint strict: false */
/*global CasperError, console, phantom, require*/

//
// Test - Import Cleaned Survey Data
// neha@akvo.org
//

var utils = require('utils');

var casper = require('casper').create({
verbose: true,
// logLevel: 'debug',
	
// Give waitForResource calls plenty of time to load.
// waitTimeout: 50000,
// clientScripts: ["includes/jquery.min.js"],

PageSettings: {
	javascriptEnabled: true,
	loadImages:	true,		// WebPage instance will use these settings
	laodPlugins: false,		// use these settings
//	userAgent:	'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36'
	}
});

phantom.cookiesEnabled = true;


// print out all the messages in the headless browser context
casper.on('remote.message', function(msg) {
	this.echo('remote message caught: ' + msg);
});

casper.on("page.error", function(msg, trace) {
	this.echo("Page Error: " + msg, "ERROR");
});



var url = 'http://akvoflowsandbox.appspot.com/admin/';
var ember_xpath = require('casper').selectXPath;

// For taking username/password via CLI
// if (system.args.length < 3) {
// console.info("You need to pass in account name, username, password, and path to casperJS as arguments to this code
// phantom.exit();
// }

// var username = system.args[1];
// var password = system.args[2];

screenshotNow = new Date(),
// screenshotDateTime = screenshotNow.getFullYear() + pad(screenshotNow.getMonth() + 1) + pad(screenshotNow.getDate()) + '-' + pad(screenshotNow.getHours()) + pad(screenshotNow.getMinutes()) + pad(screenshotNow.getSeconds()), 
viewport = [
		{
			'name': 'desktop-standard',
			'viewport': {width: 1280, height: 1024}
		}
];

casper.start(url, function() {
	console.log("Initial Akvo FLOW Login Page");
	this.test.assertExists('form#gaia_loginform', 'GAE Login Form is Found');
	this.fill('form#gaia_loginform', {
		Email:	'nchriss@gmail.com',
		Passwd:	'876^5017&'
	}, true);
});


casper.then(function () {
		this.test.assertVisible('.navSurveys', 'Survey Tab Visible');
		this.test.assertVisible('.navDevices', 'Device Tab Visible');
		this.test.assertVisible('.navData', 'Data Tab Visible');
		this.test.assertVisible('.navReports', 'Reports Tab Visible');
		this.test.assertVisible('.navMaps', 'Maps Tab Visible');
		this.test.assertVisible('.navUsers', 'Users Tab Visible');
		this.test.assertVisible('.navMessages', 'Messags Tab Visible');
		this.test.assertVisible('.menuGroup', 'Survey Menu Group Visible');
		
		this.test.assertTruthy(casper.evaluate(function() {
			return FLOW.router.location.path
		}) === '/surveys/main', 'Successfully Loaded Main Surveys Page');
		 
});


			

casper.then(function() {
		this.thenClick('.navData a', function() {
		console.log("Navigate to 'root.navData.index' Event");
    	this.waitUntilVisible('.tabNav li.active', 
    		function then() {
				this.test.assertMatch(this.fetchText('.tabNav li.active'), /\s*Inspect data\s*/);
				});


		this.waitUntilVisible('#surveyDataTable td.device',
	  	 	// ember_xpath('//*[@id="surveyDataTable"]/tbody/tr[1]/td[3]'),
			function then() {
				casper.capture('screenshots/NavData-SurveyDataTable.png');
            }
        );

		this.waitForSelector('.nextBtn a',
            function then() {
            	casper.capture('screenshots/Navdata-SurveyTableNext.png');
            });

		this.test.assertSelectorExists('.nextBtn a', 'Survey Table Next Button Rendered'); 

		});
		
});	  

casper.then(function() {
	   this.click('.nextBtn a');
		this.waitUntilVisible('#surveyDataTable td.device',
	  	 	// ember_xpath('//*[@id="surveyDataTable"]/tbody/tr[1]/td[3]'),
			function then() {
				casper.capture('screenshots/NavData-SurveyDataTableNext.png');
            }
        );
});


casper.then(function() {
	var pElements = document.getElementsByTagName('#device'); // NodeList
	
	for (var i = 0, len = pElements.length; i < len; i = i + 1) {
		   this.echo(pElements[i], + " check this out ", PELEMENT);
		   console.log("HelloHlllo");
	};
});

casper.then(function () {               
	this.test.assertSelectorHasText('a', 'Data cleaning');

	this.thenClick(ember_xpath('//a[.="Data cleaning"]'), function () {
		console.log("Entering root.navData.dataCleaning");

		this.waitUntilVisible('select.ember-select', 

			//ember_xpath('//a[@class="standardBtn" and .=" Import clean data"]'),
			function then() {
                casper.capture('screenshots/NavData-DataCleaning1.png', {
					top: 0,
					left: 0,
					width: 1280,
					height: 1024
				});
			});
		});
	
	this.waitForText("Select survey group", function then() {
		this.test.assertVisible('select.ember-select');
		// this.test.assertVisible('#ember8562', 'Survey Group Drop Down Visible');
		// this.test.assertVisible(ember_xpatch('//*[contains(text(), "Select survey group")]'), 'Select Survey Group Visible');
		this.test.assertSelectorHasText('select.ember-select option', "Select survey group");
		this.test.assertSelectorHasText('select.ember-select:nth-of-type(1)', "Akvo QA");
        this.test.assertSelectorHasText('select.ember-select option', "Select survey");
	});        
});

casper.then(function () {

		// Iterate through select options to find group 'IPE Test', execute click()

		this.evaluate( function() {
				var valSurveyGroup = '2164002';

		 		$('select.ember-select option').each(function(index, option) {
   	 			if ($(this).text() === "Akvo QA") {
				    $('select.ember-select:nth-of-type(1)').val(valSurveyGroup);
		 		    $('select.ember-select:nth-of-type(1)').trigger('change');
		 			console.log("Akvo Survey Group Selected" + " " + valSurveyGroup);
		 			return false;
					}
				});
			});

		this.waitForText("QA Eng Survey I", function then() {
            		this.test.assertVisible('select.ember-select:nth-of-type(2)');
					this.evaluate( function() {
						var valSurvey = '2165002';

		 				$('select.ember-select option').each(function(index, option) {
		 					if ($(this).text() === "QA Eng Survey I") {
		 						$('select.ember-select:nth-of-type(2)').val(valSurvey);
		 						$('select.ember-select:nth-of-type(2)').trigger('change');
		 						console.log("Akvo Survey Selected" + " " + valSurvey);
		 						return false;
					}
				});
		 });
		 });	

		this.wait(5000, function() {
			casper.capture('screenshots/DataCleaningSelect_DropDown.png');
			this.test.assertVisible(ember_xpath('//*[contains(text()," Raw data report ")]'), 'Raw Data Report Button Visible');
			this.test.assertVisible(ember_xpath('//*[@id="raw-data-import-file"]'), 'Raw Data Import File Upload Visible');
			this.test.assertVisible(ember_xpath('//*[contains(text()," Import clean data")]'), 'Import Clean Data Button Visible');
			casper.capture('screenshots/SurveyGroupSelect.png');
		});
});

// Table Listing Test

//casper.then(function() {
  //  this.body = this.evaluate(function() {
   //     var rows = $('table#surveyDataTable.dataTable');
    //    var listings = rows.eq(3).text();
     //   var count = rows.eq(4).text();
      //  return {
       //     listings: listings,
        //    count: count
        // };  

casper.then(function() {
  	// var fileName = 'blah.xlsx'
    var surveyLink = 'a[data-ember-action="348"]'

    this.click('.navSurveys a');

    // this.waitUntilVisible(surveyLink, function then() {
	//			this.click(surveyLink); 
	//				});

    this.capture('screenshots/navSurveys.png');
    this.capture('screenshots/navSurveys-AkvoGroupSurveys.png');
    
});
  	// this.evaluate(function(fileName) {__utils__.findOne('#raw-data-import-file').setAttribute('value',fileName)},{fileName:fileName});
  	// this.echo('Name='+this.evaluate(function() {return __utils__.findOne('input[#raw-data-import-file="file"]').getAttribute('name')}));
  	// this.echo('Value='+this.evaluate(function() {return __utils__.findOne('input[#raw-data-import-file="file"]').getAttribute('value')}));
  	// this.page.uploadFile('input[#raw-data-import-file="file"]',fileName);
  	// this.click('a.standardBtn');

casper.then(function() {
		this.click('.navDevices a');

		this.waitUntilVisible('#surveyDataTable td.EMEI', function then() {
			this.capture('screenshots/devicesTable.png', {
				top: 0, 
				left: 0,
				width: 1280,
				height: 1024 
			});
		});
   		var mngdeviceLink = 'a[id="ember15890"]';

   		if (!this.exists(mngdeviceLink)) return;
		this.click(mngdeviceLink);

		this.evaluate(function(mngdeviceLink) {
			__utils__.findOne(mngdeviceLink).setAttribute("className", "clicked");
		}, mngdeviceLink);

   		this.test.assertVisible(ember_xpath('//*[@id="ember15890"]/a'), 'Manage Device Groups Button Visible');

		setInterval(function () {
			document.getElementById("ember15980").click();}, 1000);

		this.click('.btnAboveTable a');
		casper.capture('screenshots/devicesManageDevices.png');
});

casper.run();
