/* jshint strict: false */
/*global CasperError, console, phantom, require*/

//
// Test - Import Cleaned Survey Data
// neha@akvo.org
//
	// var require = patchRequire(require);
	
	//casperjs convention - call utils for phantom extension

	var utils = require('utils');
	var ember_xpath = require('casper').selectXPath;
    // var loginModule = require("./lib/loginGAE.js");
  	var system = require('system');
    
    // var consoleLog = require("./lib/jsConsole.js");
    // var testrailModule = require("./lib/testrailPostResults.js");

	casper.options.verbose = true;
	casper.options.javascriptEnabled = true;
	casper.options.logLevel = "debug";
	casper.options.loadImages = true;
	phantom.cookiesEnabled = true;
	//	casper.options.waitTimeout = 1000000;

    // casper.userAgent('Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36  (KHTML, like Gecko) ');
	// print out all the messages in the headless browser context
	casper.on('remote.message', function(msg) {
		this.echo('remote message caught: ' + msg);
	});

	casper.on("page.error", function(msg, trace) {
		this.echo("Page Error: " + msg, "ERROR");
	});


	casper.test.begin('Import Clean Survey Data', function suite(test) {

			// var url = 'http://uat1.akvoflow.org';

			var url = 'http://akvoflowsandbox.appspot.com/admin';

			  
			casper.start(url, function() {
				console.log("Initial Akvo FLOW Login Page");
			// loginModule.login("akvoqa@gmail.com");
			this.test.assertExists('form#gaia_loginform', 'GAE Login Form is Found');
					this.fill('form#gaia_loginform', {
						Email:	'akvoqa@gmail.com',
						Passwd:	'R4inDr0p!'
						}, true);
			 });

	casper.then(function () {
			casper.capture('screenshots/NavAdmin-capture.png');

			/*
			 var js = this.evaluate(function() {
				return document;
				});

			this.echo(JSON.stringify(js));
           	*/
                    
			this.test.assertVisible('.navSurveys', 'Survey Tab Visible');
			this.test.assertVisible('.navDevices', 'Device Tab Visible');
			this.test.assertVisible('.navData', 'Data Tab Visible');
			this.test.assertVisible('.navReports', 'Reports Tab Visible');
			this.test.assertVisible('.navMaps', 'Maps Tab Visible');
			this.test.assertVisible('.navUsers', 'Users Tab Visible');
			this.test.assertVisible('.navMessages', 'Messages Tab Visible');
			
			this.test.assertTruthy(casper.evaluate(function() {
				return FLOW.router.location.path
			}) === '/surveys/main', 'Successfully Loaded Main Surveys Page');
		 
	});

	casper.then(function() {
			this.thenClick('.navData a', function() {
				console.log("Navigate to 'root.navData.index' Event");
			});
	});

	casper.then(function() {
			this.waitUntilVisible('.tabNav li.active', 
				function then() {
					this.test.assertMatch(this.fetchText('.tabNav li.active'), /\s*Inspect data\s*/);
			});

			casper.capture('screenshots/NavData-SurveyWaitCapture.png');

			this.waitUntilVisible('#surveyDataTable td.device',
				// ember_xpath('//*[@id="surveyDataTable"]/tbody/tr[1]/td[3]'),
				function then() {
					casper.capture('screenshots/NavData-SurveyDataTable.png');
				});
            
			this.waitUntilVisible('.nextBtn a',
				function then() {
					this.test.assertSelectorExists('.nextBtn a', 'Full Survey Table Rendered (Next Button)'); 
            	casper.capture('screenshots/Navdata-SurveyTableNext.png');
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


	casper.then(function () {               
		this.test.assertSelectorHasText('a', 'Data cleaning');

		this.thenClick(ember_xpath('//a[.="Data cleaning"]'), function () {
			console.log("Entering root.navData.dataCleaning");

			this.waitUntilVisible('select.ember-select', 
				// ember_xpath('//a[@class="standardBtn" and .=" Import clean data"]'),
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
				var valSurvey = '2165002';

		 		$('select.ember-select option').each(function(index, option) {
   	 			if ($(this).text() === "Akvo QA") {
				    $('select.ember-select:nth-of-type(1)').val(valSurveyGroup);
		 		    $('select.ember-select:nth-of-type(1)').trigger('change');

		 			console.log("Akvo Survey Group Selected" + " " + valSurveyGroup);
					console.log("el id =" + $('select.ember-select:nth-of-type(1)')[0].id);
					console.log("el id =" + $('select.ember-select:nth-of-type(2)')[0].id);
					console.log("Akvo Survey Selected" + " " + valSurvey);
		 			
		 			// $('select.ember-select:nth-child(3)').val(valSurvey);
		 		    // $('select.ember-select:nth-child(3)').trigger('change');
					// console.log("Akvo Survey Selected" + " " + valSurvey);
		 			return false;
				   }
				});
		});
		
		casper.waitForResource("http://akvoflowsandbox.appspot.com/rest/surveys", function() {
					this.echo('survey has been loaded');
				    this.evaluate( function() {
				    var valSurvey = '2165002';

				    setTimeout(function() {
							console.log("drop down=" + $('select.ember-select:nth-of-type(2)')[0].innerHTML);
							$('select.ember-select:nth-of-type(2)').val(valSurvey);
							$('select.ember-select:nth-of-type(2)').trigger('change');
							return false;
					}, 1); 
                    
					});
				    casper.capture('screenshots/Survey_DropDown.png');
		});
		
		// this.waitForText("QA Eng Survey I", function then() {
	   //  });	
		this.test.assertVisible('select.ember-select:nth-of-type(2)');

		this.evaluate( function() {
			var valSurvey = '2165002';

			$('select.ember-select option').each(function(index, option) {
		 					if ($(this).text() === "QA Eng Survey I") {
		 						$('select.ember-select:nth-of-type(2)').val(valSurvey);
		 						$('select.ember-select:nth-of-type(2)').trigger('change');
		 						console.log("el id =" + $('select.ember-select:nth-of-type(1)')[0].id);
		 						console.log("el id =" + $('select.ember-select:nth-of-type(2)')[0].id);
		 						// console.log("el id =" + $('select.ember-select:nth-of-type(3)')[0].id);
		 						// console.log("Akvo Survey Selected" + " " + valSurvey);
		 						return false;
								}
						});
					});
        
		this.wait(5000, function() {
					casper.capture('screenshots/DataCleaningSelect_DropDown.png');
					this.test.assertVisible(ember_xpath('//*[contains(text()," Raw data report ")]'), 'Raw Data Export Visible');
					this.test.assertVisible(ember_xpath('//*[@id="raw-data-import-file"]'), 'Import Updated Survey Button Visible');
					this.test.assertVisible(ember_xpath('//*[contains(text()," Import clean data")]'), 'Import Clean Data Button Visible');
			});
		});

		casper.then(function() {
					this.test.assertSelectorHasText('a', ' Import clean data');

					var fileName = './surveys/SURVEY_FORM-2205003.xls';

					this.evaluate(function(fileName) {
							__utils__.findOne('input[type="file"]').setAttribute('value',fileName)},{fileName:fileName});
					this.echo('Name='+this.evaluate(function() {
							return __utils__.findOne('input[type="file"]').getAttribute('name')}));
					this.echo('Value='+this.evaluate(function() {
							return __utils__.findOne('input[type="file"]').getAttribute('value')}));

					this.page.uploadFile('input[type="file"]',fileName);
		});

		casper.then(function() {
					this.wait(5000, function() {
					this.click(ember_xpath('//a[@class="standardBtn"]'));
					this.click(ember_xpath('//*[contains(text()," Import clean data")]'));
					});
		});

		casper.then(function() {
					casper.capture('screenshots/FileUploadDialogueFilled.png');
					this.test.assertVisible('.progress-bar', 'Progress Bar Rendered');
					this.waitUntilVisible(ember_xpath('//*[contains(text(), "Upload Complete")]'), function then() {
							console.log("Survey Upload Complete");
					casper.capture('screenshots/UploadCompleteConfirm.png');
					});


	});  

// casper.test.suiteResults.getAllPasses();

// casper.run();

	casper.run(function() {
		this.test.done();
		// casper.test.renderResults(true, 0, 'test-results.xml');
	});
});
