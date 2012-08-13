$(document).ready(function() {
						   
	var hash = window.location.hash.substr(1);
	var href = $('nav#topnav li a').each(function(){
		var href = $(this).attr('href');
		if(hash==href.substr(0,href.length-5)){
			var toLoad = hash+'.html #main';
			$('#main').load(toLoad)
		}											
	});

	$('nav#topnav li a').click(function(){
		$('nav#topnav li a').removeClass('current');					  
		$('nav#topnav li a').parent('li').addClass('current');					  
		var toLoad = $(this).attr('href')+' #main';
		$('#main').hide('fast',loadContent);
		window.location.hash = $(this).attr('href').substr(0,$(this).attr('href').length-5);
		function loadContent() {
			$('#main').load(toLoad,'',showNewContent())
		}
		function showNewContent() {
			$('#main').show('normal',hideLoader());
		}
		function hideLoader() {
			$('#load').fadeOut('normal');
		}
		return false;
		
	});

});