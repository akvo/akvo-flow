// login module
//

exports.login = function(username, password) {
			casper.test.comment("Login in with username \"" + username + "\", password \"" + password + "\"");
			
			casper.start(casper.cli.get("url") + "/login", function() {
	    	    return this.test.assertExists('form#gaia_login', 'FLOW GAE Login Found');
	    	      });
            
	      	casper.then(function() {
				this.fill('form#gaia_loginform', {
					Email: username,
					Password: 'R4inDr0p!'
				}, true);

			casper.this.getPasses();
            // casper.testrail.postResults();
				// this.sendKeys("input[name=username]", username);
				// this.sendKeys("input[name=password]", password);
				//	return this.click("input[type=submit]");
			});

	        return casper.then(function() {});
};
