//
// Test - Import Cleaned Survey Data
// neha@akvo.org
//

var casper = require('casper').create({
verbose: true,
logLevel: 'debug',
PageSettings: {
	loadImages:	false,		// WebPage instance will use these settings
	laodPlugins: false,		// use these settings
	userAgent:	'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36'
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

casper.start(url, function() {
	console.log("page loaded");
	this.test.assertExists('form#gaia_loginform', 'GAE Login form is found');
	this.fill('form#gaia_loginform', {
		Email:	'',
		Passwd:	''
	}, true);
	
});

casper.then(function() {
	this.test.assertExists('.ember-view.navData', 'The NavData element exists');
	this.click('.ember-view.navData');
	this.waitFor(function check() {
		return this.evaluate(function() {
		return document.querySelectorAll('#ember1965 > a:nth-child(1)').length > 0;
	});
	// step to execute when check is ok
	this.test.assertExists('#ember1965','Cleaning data Tab exists');
	this.clickLabel('Data cleaning', 'a');
  	// step to execute when check fails	
	}, function then() { 
	this.echo('Failed to Navigate to Data Cleaning Tab', 'ERROR');
    });
});	

// Table Listing Test
casper.then(function() {
    data.body = this.evaluate(function() {
        var rows = $('#content table:first tbody tr');
        var listings = rows.eq(3).text();
        var count = rows.eq(4).text();
        return {
            listings: listings,
            count: count
        };  
    });
	this.echo(data.body.listings); 
	// Select Akvo Group from DropDown Menu
	this.evaluate(function() {
	this.test.assertExists('select[#ember6356]', 'Dropdown is found');
    	document.querySelector('select[#ember6556]').value = 12;
	});
});


casper.then(function() {
  	this.evaluate(function(fileName) {__utils__.findOne('input[#raw-data-import-file]="file"]').setAttribute('value',fileName)},{fileName:fileName});
  	this.echo('Name='+this.evaluate(function() {return __utils__.findOne('input[#raw-data-import-file="file"]').getAttribute('name')}));
  	this.echo('Value='+this.evaluate(function() {return __utils__.findOne('input[#raw-data-import-file="file"]').getAttribute('value')}));
  	this.page.uploadFile('input[#raw-data-import-file="file"]',fileName);
  	this.click('a.standardBtn');

});

casper.run();
