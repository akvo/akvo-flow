/* jshint strict: false */
/*global CasperError, console, phantom, require*/

//
//// Test - Manage FLOW Dashboard Devices
//// neha@akvo.org


	var utils = require('utils');
	casper.options.verbose = true;
	casper.options.javascriptEnabled = true;
	casper.options.loadImages = true;
	phantom.cookiesEnabled = true;

	var url = 'http://akvoflowsandbox.appspot.com/admin/';
	var ember_xpath = require('casper').selectXPath;

	casper.test.begin('Manage FLOW Devices', function suite(test) {

			var url = 'http://akvoflowsandbox.appspot.com/admin/';

			casper.start(url, function() {
				console.log("Initial Akvo FLOW Login Page");
			    this.test.assertExists('form#gaia_loginform', 'GAE Login Form is Found');
				this.fill('form#gaia_loginform', {
					Email:	'akvoqa@gmail.com',
					Passwd:	'R4inDr0p!'
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
							document.getElementById("ember15980").click();}, 1000);

				this.click('.btnAboveTable a');
						casper.capture('screenshots/devicesManageDevices.png');
			});

		
	casper.then(function() {
				this.thenClick('.navDevices a', function() {
					console.log("Navigate to 'root.navDevices.index' Event");
				this.waitUntilVisible('.tabNav li.active', 
					function then() {
						this.test.assertMatch(this.fetchText('.tabNav li.active'), /\s*Devices list\s*/);
				});

				this.waitUntilVisible('#surveyDataTable td.device',
					// ember_xpath('//*[@id="surveyDataTable"]/tbody/tr[1]/td[3]'),
					function then() {
						casper.capture('screenshots/NavData-SurveyDataTable.png');
					}
				);
	});
	
	casper.then(function() {
				this.test.assertSelectorHasText('a', 'Assignments list');

				this.thenClick(ember_xpath('//a[.="Assignments list"]'), function() {
						this.waitUntilVisible('select.ember-select',
							function then() {
								casper.capture('screenshots/devicesAssignmentList.png', {
									top: 0,
									left: 0,
									width: 1280,
									height: 1024
								});
				});
	});

	casper.run(function() {
			this.test.done();
			casper.test.renderResults(true,0,'deviceTest-results.xml');
		});
