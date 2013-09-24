var casper = require('casper').create({
  verbose: true,
  logLevel: 'debug',
  pageSettings: {
	userName:'devops@akvo.org',
	password:'R4inDr0p!',
    loadImages: true,
    loadPlugins: true
  }
});

var colorizer = require('colorizer').create('Colorizer');


casper.on('resource.requested', function(request) {
  this.echo(colorizer.colorize("SENDING REQUEST #" + request.id + " TO " + request.url, "PARAMETER"));
  this.echo(JSON.stringify(request, null, 4));
});



casper.on('resource.received', function(resource) {
  this.echo(JSON.stringify(resource, null, 4));
});


// casper.setHttpAuth('devops','pass');

// encoded_userName=encodeURIComponent('devops@akvo.org');
// console.log(encoded_userName);

// var url = "https://devops@akvo.org:R4inDr0p!@akvo.testrail.com/index.php?/api/v2/get_case/1";

// var url = "https://"+encoded_userName+":R4inDr0p!@akvo.testrail.com/index.php?/api/v2/get_case/1";

var url = "https://akvo.testrail.com/index.php?/api/v2/get_case/1";

console.log(url);

var echoCurrentPage = function() {
    	  this.echo(colorizer.colorize("[Current Page]", "INFO") + this.getTitle() + " : " + this.getCurrentUrl());  
	};

var headers = {
	method: 'get',
	headers: { 'content-type': 'application/json'}
};

// }).then(function(response) {
//	echoCurrentPage.call(this);
//	this.debugPage();
// });



// casper.open(url, headers).then(function(response) {
//	echoCurrentPage.call(this);
//	this.debugPage();  
// });

casper.start();
// casper.setHttpAuth('devops@akvo.org','R4inDr0p!');

casper.thenOpen(url, headers).then(function(response) {
	echoCurrentPage.call(this);
	this.debugPage();
});


casper.run();
