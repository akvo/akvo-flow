name: Run Automated Tests
description: Shows a Button on Test Case Forms to Run Automated Tests
author: Neha Chriss
version: 1.0
includes: ^cases
excludes: 

js:
$(document).ready(function(){
		var test = $("<a href='#' class='buttonLink'><span>Call Remote Test</span></a>").click(function(){
					$.ajax({
							url: 'https://akvo.testrail.com/index.php?/api/v2/add_result/1',
							dataType: "jsonp",
							jsonpCallback: "callback",
							success: function( testreply ){
								alert("Results obtained.. Posting results...");
								$.ajax({
									url: 'https://akvo.testrail.com/index.php?/api/v2/add_result/1',
									username: "devops@akvo.org",
									password: "R4inDr0p!",
									contentType: "application/json;",
									accepts: "application/json;",
									dataType: "json",
									type: "POST",
									data: '{"status_id": "1"}',
									processData: "false",
									headers:{"Content-Type": "application/json;"},
									beforeSend: function(jqXHR){
										jqXHR.overrideMimeType("application/json;");
										jqXHR.setRequestHeader("Accept", "application/json;");
										},
										success: function( postreply ){
											alert("Results posted.");
										}
								});
					}
			});
			return false;
		});
		$("#sidebar .box:first-child").append(test);
});
