// postResultsTR.js
//
// Posts Test Results to https://akvo.testrail.com
// neha@akvo.org
//
//


// exports.postResultsTR = function(){

require('utils').dump(casper.test.suiteResults.getAllPasses());
// this.echo(typeof rawResults);
casper.test.done();
	// var url = 'https://akvo.testrail.com';

    // testResultsObj = JSON.parse(rawResults);

	/* postParams = { 
	  foo: "case_id", 
		bar: testResultsObj
	}; 

	casper.start().then(function() { 
		this.open(url, { 
	    	        method: 'post', 
	    	        data: postParams 
	    	    }) 
	});
   */

//    casper.test.done();
// };
