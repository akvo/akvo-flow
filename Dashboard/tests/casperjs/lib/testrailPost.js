// postResultsTR.js
//
// Posts Test Results to https://akvo.testrail.com
// neha@akvo.org
//
//


exports.postResultsTR = function(){

    var rawResults = require('utils').dump(casper.test.suiteResults.getAllPasses());
	var url = 'https://akvo.testrail.com';

    testResultsObj = JSON.parse(rawResults);

	postParams = { 
		foo: "case_id", 
		bar: testResultsObj
	}; 

	casper.start().then(function() { 
		this.open(url, { 
	    	        method: 'post', 
	    	        data: postParams 
	    	    }) 
	});

    casper.test.done();
};
