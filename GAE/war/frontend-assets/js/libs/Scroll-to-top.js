  // JQuery - Sliding Drop-down  control   /////////
//                            loic@arkkeo.com  /////////


$(document).ready(function(){

	// hide #back-top first
	$("#back-top").hide();
	
	// fade in #back-top
	$(function () {
		$(window).scroll(function () {
			if ($(this).scrollTop() > 300) {
				$('#back-top').fadeIn();
			} else {
				$('#back-top').fadeOut();
			}
		});

		// scroll body to 0px on click
		$('#back-top a').click(function () {
			$('body,html').animate({
				scrollTop: 0
			}, 800);
			return false;
		});
	});


	$("icon-view-btn").click(function() {
		$("#archive-icon-view ul").addClass("icon-view");
		});
});
