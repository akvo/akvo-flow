//
// Test - Import Cleaned Survey Data
// neha@akvo.org
//

var utils = require('utils');

var casper = require('casper').create({
verbose: true,
logLevel: 'debug',
	
// Give waitForResource calls plenty of time to load.
// waitTimeout: 50000,

PageSettings: {
	javascriptEnabled: true,
	loadImages:	true,		// WebPage instance will use these settings
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
	console.log("page loaded");
	this.test.assertExists('form#gaia_loginform', 'GAE Login form is found');
	this.fill('form#gaia_loginform', {
		Email:	'nchriss@gmail.com',
		Passwd:	'876^5017&'
	}, true);
});


casper.then(function () {
	this.thenClick(ember_xpath('//*[@id="ember819"]/a'), function() {
		console.log("Navigate to 'root.navData.index' Event");

    			return this.test.assertVisible(ember_xpath('//*[@id="ember2023"]/a'), 
    			'Ember.root NavdataIndex');	
    				this.wait(50000);
		            this.capture('screenshots/NavDataIndex.png');
					});
			
});	

casper.then(function () {
	this.thenClick(ember_xpath('//*[@id="ember2041"]/a'), function () {
		console.log("Navigate to Data Cleaning Tab");
		this.capture('screenshots/DataCleaning.png', {
			top: 0,
			left: 0,
			width: 1280,
			height: 1024
		});
	});
});

casper.then(function () {
		this.capture('screenshots/Step4.png', {
			top: 0,
			left: 0,
			width: 1280,
			height: 1024
		});         
});
// casper.then(function () {
  //   this.test.assertExists(ember_xpath('//*[@id="ember2029"]/a', 'Manage Attributes Exists'));
	// this.click(ember_xpath('//*[@id="ember2029"]/a');
   //  casper.capture('screenshots/NavDataIndex2.png');
   //  });

//

// casper.then(function () {
//   this.test.assertVisible(ember_xpath('//*[@id="surveyDataTable"]'));
//   this.test.assertVisible(ember_xpath('//*[@id="surveyDataTable"]/thead/tr/th[3]'));
//   return casper.capture('shots/pageWrap.png', {
 //   top: 0,
//   left: 0,
	 //width: 1280,
//    height: 1024
//   });
//});
//
//  Iterate through children that inherit dataSection Class from 

// casper.then(function() {
//     var ids = $('//*[@id="main"]').children().map(function(n,i) {
//       return n.id;
//       this.echo('Return set of IDs under #datasection');
// 	 if (this.test.assertVisible('.tabNav%20floats-in') {
//  	 return document.getElementByXPath();
//    });
// });

  //  function then() {
   // 	console.log("element : ", this.evaluate(function ()
	//	{
	 //   	var el = $("ul li:eq(3)");
	  //  	return el;
	   // }));

    // var url = this.evaluate(function() {
    	// return __utils__.getElementByXPath("//a[text()="Data cleaning"
    
    	// return document.getElementById('watch-related').getElementsByTagName('li');
    // this.clickLabel('Data cleaning', 'a');
    // xpath selector: xpath se
    // //a[text()="Data cleaning"
	
	
   // return this.evaluate(function	
	// return document.querySelectorAll('#ember1965 > a:nth-child(1)').length > 0;

	// step to execute when check is ok
	// this.test.assertExists('#ember1965 > a:nth-child(1)','Cleaning data Tab exists');
	// this.clickLabel('Data cleaning', 'a');
  	// step to execute when check fails	
	// }, function then() { 
	// this.echo('Failed to Navigate to Data Cleaning Tab', 'ERROR');

// Table Listing Test

casper.then(function() {
    this.body = this.evaluate(function() {
        var rows = $('table#surveyDataTable.dataTable');
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
	this.test.assertExists('//*[@id="ember21602"]', 'Dropdown is found');
	return document.querySelector('//*[@id="ember21602"]').value = 12;
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
