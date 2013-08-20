// login module
//

casper.options.verbose = true;
casper.options.logLevel = "debug";
casper.options.javascriptEnabled = true;
casper.options.loadImages = true;
phantom.cookiesEnabled = true;
casper.options.waitTimeout = 5000;

exports.login = function(username, password) {

			casper.test.comment("Login with username \"" + username + "\"");
			var url = 'https://akvoflowsandbox.appspot.com/admin';
			casper.open(url).then(function() {

            	this.capture('screenshots/NavLogin-GAE.png');

				casper.test.assertExists('form#gaia_loginform', 'FLOW GAE Login Found');
	    	   
	    	   	/* return this.waitUntilVisible('form#gaia_login', 
	    	   	  	function then() {
						this.test.assertExists('form#gaia_login', 'FLOW GAE Login Found');
	    	      });
	    	   	*/
			  	
			  	//  this.sendKeys("input[name=username]", username);
			  	//  this.sendKeys("input[name=password]", password);
			   
			 casper.fill('form#gaia_loginform', {
					Email: 'akvoqa',
					Passwd: 'R4inDr0p!'
				}, true);
			
			 return this.waitForResource(this.getCurrentUrl(), function() {
					casper.capture('screenshots/NavAdmin-FLOW.png');
 					}, function() {
 					// page load failed after 5 seconds
					}, 10000);
		

			 // return casper.click('a[type="submit"]');
			 

			// casper.this.getPasses();
            // casper.testrail.postResults();
				// this.sendKeys("input[name=username]", username);
				// this.sendKeys("input[name=password]", password);
				//	return this.click("input[type=submit]");
			// });


});
};


