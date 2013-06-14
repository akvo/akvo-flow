//
// Test - Import Cleaned Survey Data
// neha@akvo.org
//

var utils = require('utils');

var casper = require('casper').create({
verbose: true,
logLevel: 'debug',
waitTimeout: 50000,
clientScripts: ["includes/jquery.min.js"],
remoteScripts: ['http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js'
	'http://akvoflowsandbox.appspot.com/vendorjs/js/vendor/handlebars-1.0.rc.1.js',
	'http://akvoflowsandbox.appspot.com/vendorjs/js/vendor/ember-1.0.0.pre-2-36.min.js',
	'http://akvoflowsandbox.appspot.com/vendorjs/js/vendor/ember-data-rev10.min.js',
	'http://akvoflowsandbox.appspot.com/vendorjs/js/vendor/jquery-ui-1.8.21.custom.js',
	'http://akvoflowsandbox.appspot.com/vendorjs/js/vendor/jquery.dataTables.js',
	'http://akvoflowsandbox.appspot.com/vendorjs/js/vendor/d3.v2.min.js',
	'http://akvoflowsandbox.appspot.com/vendorjs/js/vendor/resumable.min.js',
	'http://akvoflowsandbox.appspot.com/js/loader.js',
	'http://akvoflowsandbox.appspot.com/ui-strings.js',
	'http://akvoflowsandbox.appspot.com/flowenv.js',
	'http://akvoflowsandbox.appspot.com/currentuser.js',
	'http://akvoflowsandbox.appspot.com/js/app.js',
	'http://akvoflowsandbox.appspot.com/js/flowDashboard.js']
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
var x = require('casper').selectXPath;

casper.start(url, function() {
	console.log("page loaded");
	this.test.assertExists('form#gaia_loginform', 'GAE Login form is found');
	this.fill('form#gaia_loginform', {
		Email:	'nchriss@gmail.com',
		Passwd:	'$0c1o/p4tH'
	}, true);
});

casper.then(function() {
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/jquery-1.8.2.min.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/handlebars-1.0.rc.1.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/ember-1.0.0.pre-2-36.min.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/ember-data-rev10.min.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/jquery-ui-1.8.21.custom.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/jquery.dataTables.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/d3.v2.min.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/resumable.min.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/plugins/loader.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/plugins/flowDashboard.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/GAE/war/admin/js/app.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/GAE/war/admin/js/flowDashboard.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/currentuser.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/flowenv.js');
   	// this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/jquery-1.8.2.min.js');

	this.test.assertExists(x('//*[@id="ember803"]/a'), 'Survey Nav Tab Exists');
	this.test.assertExists(x('//*[@id="ember811"]/a'), 'Devices Nav Tab Exists');
	this.test.assertExists(x('//*[@id="ember819"]/a'), 'Data Nav Tab Exists');
	this.test.assertExists(x('//*[@id="ember827"]/a'), 'Reports Nav Tab Exists');
	this.test.assertExists(x('//*[@id="ember835"]/a'), 'Maps Nav Tab Exists');
	this.test.assertExists(x('//*[@id="ember848"]/a'), 'Users Nav Tab Exists');
	this.test.assertExists(x('//*[@id="ember861"]/a'), 'Messages Nav Tab Exists');
	
	
	
});




casper.then(function () {
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/jquery-1.8.2.min.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/handlebars-1.0.rc.1.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/ember-1.0.0.pre-2-36.min.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/ember-data-rev10.min.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/jquery-ui-1.8.21.custom.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/jquery.dataTables.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/d3.v2.min.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/resumable.min.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/plugins/loader.js');
   	// this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/plugins/flowDashboard.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/GAE/war/admin/js/flowDashboard.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/GAE/war/admin/js/app.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/currentuser.js');
   	this.page.injectJs('/Users/neha/code/akvo/akvo-flow/Dashboard/app/js/vendor/flowenv.js');

	this.thenClick(x('//*[@id="ember819"]/a'), function() {
		console.log("Navigate to 'root.navData.index' Event");
		});

	this.waitFor(function check() {
    this.test.assertVisible(x('//*[@id="ember15236"]/a'));
	});

    this.echo('TabNav Test Block END');
	
    this.capture('shots/pageWrap.png', {
		top: 0,
		left: 0,
		width: 1280,
		height: 1024
	});

});

//casper.then(function() {
//     var ids = $('//*[@id="main"]').children().map(function(n,i) {
//       return n.id;
 //    this.echo('Return set of IDs under #datasection');
  //   });
   //  if (this.test.assertVisible('.tabNav%20floats-in') {
  //  return document.getElementByXPath("//a[text()="Data Cleaning"]");
   //  });
//});
    // },
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
