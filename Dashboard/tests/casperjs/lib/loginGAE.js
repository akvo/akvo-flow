// login module
//

// patching phantomjs require to allow casper modules using thei full name
var require = patchRequire(require);
var fs = require('fs');

casper.options.verbose = true;
casper.options.logLevel = "debug";
casper.options.javascriptEnabled = true;
casper.options.loadImages = true;
phantom.cookiesEnabled = true;
casper.options.waitTimeout = 5000;
casper.options.clientScripts.push("includes/jquery-1.8.2.min.js"); 

exports.login = function(username, password) {

			casper.test.comment("Login with username \"" + username + "\"");
			var url = 'https://akvoflowsandbox.appspot.com/admin';


            var gmailCred = {
            		'Passwd': fs.read('./lib/creds/akvoqa_gmail')
            	};
			
			// closure to capture the file information.
			
			casper.open(url).then(function() {

			this.capture('screenshots/NavLogin-GAE.png');

			casper.test.assertExists('form#gaia_loginform', 'FLOW GAE Login Found');
	    	   
			  	//  this.sendKeys("input[name=username]", username);
			  	//  this.sendKeys("input[name=password]", password);
			   
			 casper.fill('form#gaia_loginform', {
					Email: 'akvoqa@gmail.com',
					gmailCred
				}, true);

			 //POST to GAE
			
			 return this.waitForResource(this.getCurrentUrl(), function() {
					casper.capture('screenshots/NavAdmin-FLOW.png');
 					}, function() {
 					// page load failed after 5 seconds
					}, 2000);
		


			// casper.this.getPasses();
            // casper.testrail.postResults();
				// this.sendKeys("input[name=username]", username);
				// this.sendKeys("input[name=password]", password);
				//	return this.click("input[type=submit]");
			// });


		});
};


