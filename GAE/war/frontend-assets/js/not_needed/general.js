$(document).ready(function(){
	$('#topnav li', 'menuTopbar a').click(function(e){
		e.preventDefault();
		$('#pageWrap').load( e.target.href + ' #main');
		$('#topnav li').removeClass("current");
		$("this").addClass("current");
	}); 
});