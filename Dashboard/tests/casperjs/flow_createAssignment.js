//
// Test - Import Cleaned Survey Data
// neha@akvo.org
//
// var require = patchRequire(require);

//casperjs convention - call utils for phantom extension

var utils = require('utils');
var ember_xpath = require('casper').selectXPath;
// var loginModule = require('./lib/loginGAE.js');
var system = require('system');

// var consoleLog = require("./lib/jsConsole.js");
// var testrailModule = require("./lib/testrailPostResults.js");

casper.options.verbose = true;
casper.options.javascriptEnabled = true;
casper.options.logLevel = "debug";
casper.options.loadImages = true;
phantom.cookiesEnabled = true;

//  casper.options.waitTimeout = 1000000;

// casper.userAgent('Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36  (KHTML, like Gecko) ');
// print out all the messages in the headless browser context

casper.on('remote.message', function(msg) {
	this.echo('remote message caught: ' + msg);
	});

casper.on("page.error", function(msg, trace) {
	this.echo("Page Error: " + msg, "ERROR");
	});


casper.test.begin('FLOW Create Device Assignments', function suite(test) {
	var url = 'http://akvoflowsandbox.appspot.com/admin';

	casper.start(url, function() {
		 console.log("Initial Akvo FLOW Login Page");
		 // loginModule.login("akvoqa@gmail.com");
		 this.test.assertExists('form#gaia_loginform', 'GAE Login Form is Found');
		 this.fill('form#gaia_loginform', {
		 		Email:  'akvoqa@gmail.com',
		 		Passwd: 'R4inDr0p!'
		 }, true);
	});

	casper.then(function () {
		casper.capture('screenshots/createAssign/postLogin.png');

		this.test.assertVisible('.navSurveys', 'Surveys Tab Visible');
		this.test.assertVisible('.navDevices', 'Devices Tab Visible');
		this.test.assertVisible('.navData', 'Data Tab Visible');
		this.test.assertVisible('.navReports', 'Reports Tab Visible');
		this.test.assertVisible('.navMaps', 'Maps Tab Visible');
		this.test.assertVisible('.navUsers', 'Users Tab Visible');
		this.test.assertVisible('.navMessages', 'Messages Tab Visible');
		
	});    

	casper.then(function () {
		this.thenClick('.navDevices a', function() {
			console.log("Navigate to 'root.navDevices.index' Event");
		});

		this.waitUntilVisible('#surveyDataTable tbody td', function
			then() {
				this.test.assertTruthy(casper.evaluate(function() {
					return FLOW.router.location.path
					}) === '/devices', 'Navigated to Devices Page');
			});	

		this.capture('screenshots/createAssign/navDevices.png'); 

	});

   casper.then(function () {
   	   this.thenClick('#tabs ul li:nth-child(2) a', function() {
   	   	   console.log("Click Assignments List Tab");
		});


   });

   casper.then(function () {
	   this.test.assertVisible('#deviceDataTable td', 'Devices List Visible');
   	   this.thenClick('.btnAboveTable');
   	   casper.waitForResource("http://akvoflowsandbox.appspot.com/rest/survey_groups",
   	   	   function() {
   	   	   	   this.capture('screenshots/createAssign/surveyAssignmentsLoaded.png');
			}
   	   	   );
   	   
   	   var date =new Date();
   	   var tomorrow = new Date(date.getTime() + 24 * 60 * 60 * 1000);
   	   var today = new Date(date.getTime() +0  * 60 * 60 * 1000);

   	   this.fill('#assignSurveys form', {
		   assignmentName: 'BlahBlah',
		   from_date: today,
		   to_date: tomorrow,
	   }, true);

	   this.capture('screenshots/createAssign/assignForm.png');
	

   });

   

   

			
});


casper.run();
