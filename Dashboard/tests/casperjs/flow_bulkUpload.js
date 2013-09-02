/* jshint strict: false */
/*global CasperError, console, phantom, require*/

//
//// Test - Basic Manage Devices 
//// neha@akvo.org


var utils = require('utils');

var casper = require('casper').create({
verbose: true,
logLevel: 'debug',

// Give waitForResource calls plenty of time to load.
waitTimeout: 50000,
/// clientScripts: ["includes/jquery.min.js"],
//

PageSettings: {
        javascriptEnabled: true,
        loadImages:     true,           // WebPage instance will use these settings
        laodPlugins: false,             // use these settings
        userAgent:      'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36'
        }
});

var url = 'http://akvoflowsandbox.appspot.com/admin/';

casper.start(url, function() {
	        console.log("Initial Akvo FLOW Login Page");
	        this.test.assertExists('form#gaia_loginform', 'GAE Login Form is Found');
	        this.fill('form#gaia_loginform', {
	                   Email:  '',
	                   Passwd: ''
	        }, true);
});

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

	/*  this.body = this.evaluate(function() {
	 *          var rows = $('table#surveyDataTable.dataTable');
	 *              var listings = rows.eq(3).text();
	 *                  var count = rows.eq(4).text();
	 *                       return {
	 *                                   listings: listings,
	 *                                               count: count
	 *                                                        };
	 *                                                        */

	
	setInterval(function () {
			document.getElementById("ember15980").click();}, 1000);

	this.click('.btnAboveTable a');
			casper.capture('screenshots/devicesManageDevices.png');
});

asper.then(function() {
	this.click('.nextBtn a');
	this.waitUntilVisible('#surveyDataTable td.device',
			ember_xpath('//*[@id="surveyDataTable"]/tbody/tr[1]/td[3]'),
					function then() {
			casper.capture('screenshots/NavData-SurveyDataTableNext.png');
				                                 }
				                                                      );
	                                                                });


casper.run();
