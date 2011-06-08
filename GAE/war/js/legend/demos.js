function loadDemo(path) {

	resetDemos();

	$.get(path, function(data) {
	
		data = data.replace(/<script.*>.*<\/script>/ig,""); // Remove script tags
		data = data.replace(/<\/?link.*>/ig,""); //Remove link tags
		data = data.replace(/<\/?html.*>/ig,""); //Remove html tag
		data = data.replace(/<\/?body.*>/ig,""); //Remove body tag
		data = data.replace(/<\/?head.*>/ig,""); //Remove head tag
		data = data.replace(/<\/?!doctype.*>/ig,""); //Remove doctype
		data = data.replace(/<title.*>.*<\/title>/ig,""); // Remove title tags
		data = data.replace(/<iframe(.+)src=(\"|\')(.+)(\"|\')>/ig, '<iframe$1src="'+'/'+section+'/'+'$3">');; // Change iframe src
		data = data.replace(/<img([^<>]+)src=(\"|\')([^\"\']+)(\"|\')([^<>]+)?>/ig, '<img$1src="'+'/'+section+'/'+'$3" $5/>');; // Change images src
		data = $.trim(data);

		$('style.demo-style').remove();
		$('#demo-frame').empty().html(data);
		$('#demo-frame style').appendTo('head').addClass('demo-style');
		$('#demo-link a').attr('href', path);
		updateDemoNotes();
		updateDemoSource(data);

	});

}

function updateDemoNotes() {

	var notes = $('#demo-frame .demo-description');
	if ($('#demo-notes').length == 0) {
		$('<div id="demo-notes"></div>').insertAfter('#demo-config');
	}
	$('#demo-notes').empty().html(notes.html());
	notes.hide();

}

function updateDemoSource(source) {
	if ($('#demo-source').length == 0) {
		$('<div id="demo-source"><a href="#" class="source-closed">View Source</a><div><pre><code></code></pre></div></div>').insertAfter('#demo-notes');
		$('#demo-source').find("> a").click(function() {
			$(this).toggleClass("source-closed").toggleClass("source-open").next().toggle();
			return false;
		}).end().find("> div").hide();
	}
	$('#demo-source code').empty().text(source);
}

function resetDemos() {
	( $.datepicker && $.datepicker.setDefaults($.extend({showMonthAfterYear: false}, $.datepicker.regional[''])) );
	$(".ui-dialog-content").remove();
}

$(document).ready(function() {

	//Do not run on /demos/ root page.
	if ( (/demos\/(#.*)?$/i).test(window.location) ) {
		return;
	}
	
	//Rewrite and prepare links of right-hand sub navigation
	$('#demo-config-menu a').each(function() {
		$(this).attr('target', 'demo-frame');
		$(this).click(function(e) {

			resetDemos();
			$(this).parents('ul').find('li').removeClass('demo-config-on');
			$(this).parent().addClass('demo-config-on');

			//Set the hash to the actual page without ".html"
			window.location.hash = this.getAttribute('href').match((/\/([^\/\\]+)\.html/))[1];

			loadDemo(this.getAttribute('href'));
			e.preventDefault();

		});
	});
	
	//If a hash is available, select the right-hand menu item and load it
	if(window.location.hash && window.location.href.indexOf('/demos/') != -1) {
		loadHash();
	}

	resetDemos();

	updateDemoNotes();
	updateDemoSource($.trim($('#demo-frame').html()));
	
	//Prepare the option types
	var paramDescriptions = {
		'String': '<pre> "I\'m a String in JavaScript!"\n \'So am I!\'</pre><p>A string in JavaScript is an immutable object that contains none, one or many characters.</p><p>The type of a string is "string".</p><pre> typeof "some string"; // "string"</pre><p class="more">read more about the String type at <a href="http://docs.jquery.com/Types#String">http://docs.jquery.com/Types#String</a></p>',
		'Number': '<pre> 12\n 3.543</pre><p>Numbers in JavaScript are double-precision 64-bit format IEEE 754 values. They are immutable, just as <a href="#String" title="">strings</a>. All operators common in c-based languages are available to work with numbers (+, -, *, /, %, =, +=, -=, *=, /=, ++, --).</p><p>The type of a number is "number".</p><pre> typeof 12 // "number"\n typeof 3.543 // "number"</pre><p class="more">read more about the Number type at <a href="http://docs.jquery.com/Types#Number">http://docs.jquery.com/Types#Number</a></p>',
		'Boolean': '<p>A boolean in JavaScript can be either true or false:</p><pre> if ( true ) console.log("always!")\n if ( false ) console.log("never!")</pre><p>When an option is specified as a boolean, it often looks like this:</p><pre>$("...").somePlugin({\n  hideOnStartup: true,\n  onlyOnce: false\n});</pre>',
		'Object': '<p>Everything in JavaScript is an object, though some are more objective (haha). The easiest way to create an object is the object literal:</p><pre> var x = {};\n var y = {\n   name: "Pete",\n   age: 15\n };</pre><p>The type of an object is "object":</p><pre> typeof {} // "object"</pre><p class="more">read more about the Object type at <a href="http://docs.jquery.com/Types#Object"></a>></p>',
		'Options': '<p>Options in jQuery are plain JavaScript objects. Whenever Options is mentioned as a type, that object and also all of its properties should be optional. There are exceptions where at least one option is required. jQuery\'s most prominent use of Options is its <a href="/Ajax/jQuery.ajax" title="Ajax/jQuery.ajax">AJAX</a>-method. Nearly all jQuery plugins provide an Options-based API: They work without any configuration, but allow the user to specify whatever customization (s)he needs.</p><p>Let\'s look at an example from the form plugin. It allows you to submit a form via AJAX with this simple line of code:</p><pre> $("#myform").ajaxForm();</pre><p>In that mode, it uses the form\'s action-attribute as the AJAX-URL and the form\'s method-attribute to determine whether to GET or POST the form. You can override both defaults by specifying them as options:</p><pre> $("#myform").ajaxForm({\n   url: "mypage.php",\n   type: "POST"\n });</pre>',
		'Array': '<p>Arrays in JavaScript are mutable lists with a few built-in methods. You can define arrays using the array literal:</p><pre> var x = [];\n var y = [1, 2, 3];</pre><p>The type of an array is "object":</p><pre> typeof []; // "object"\n typeof [1, 2, 3]; // "object"</pre><p>Reading and writing elements to an array uses the array-notation:</p><pre> x[0] = 1;\n y[2] // 3</pre><p class="more">read more about the Array type at <a href="http://docs.jquery.com/Types#Array">http://docs.jquery.com/Types#Array</a></p>',
		'Map': '<p>The map type is used by the AJAX function to hold the data of a request.  This type could be a string, an array&lt;form elements&gt;, a jQuery object with form elements or an object with key/value pairs. In the last case, it is possible to assign multiple values to one key by assigning an array.</p><pre>{\'key[]\':[\'valuea\',\'valueb\']}</pre><p>becomes on the server-side (in PHP):</p><pre>$_REQUEST[\'key\'][0]="valuea";\n$_REQUEST[\'key\'][1]="valueb";</pre><p>in Rails or Merb:</p><pre> params[:key] = ["valuea", "valueb"]</pre>',
		'Function': '<p>A function in JavaScript can be either named or anonymous. An anonymous function can be assigned to a variable or passed to a method.</p><pre>function named() {}\nvar handler = function() {}</pre><p>You see a lot of anonymous functions in jQuery code:</p><pre> $(document).ready(function() {});\n $("a").click(function() {});\n $.ajax({\n   url: "someurl.php",\n   success: function() {}\n });</pre><p>The type of a function is "function".<p class="more">read more about the Function type at <a href="http://docs.jquery.com/Types#Function">http://docs.jquery.com/Types#Function</a></p></p>',
		'Callback': '<p>A callback is a plain JavaScript function passed to some method as an argument or option. Some callbacks are just events, called to give the user a chance to react when a certain state is triggered. jQuery\'s event system uses such callbacks everywhere:</p><pre> $("body").click(function(event) {\n   console.log("clicked: " + event.target);\n });</pre><p>Most callbacks provide arguments and a scope. In the event-handler example, the callback is called with one argument, an Event. The scope is set to the handling element, in the above example, document.body.</p><p>Some callbacks are required to return something, others make that return value optional. To prevent a form submission, a submit event handler can return false:</p><pre> $("#myform").submit(function() {\n   return false;\n });</pre><p>Instead of always returning false, the callback could check fields of the form for validity, and return false only when the form is invalid.</p>',
		'Selector': '<p>A selector is used in jQuery to select DOM elements from a DOM document. That document is, in most cases, the DOM document present in all browsers, but can also be a XML document received via AJAX.</p><p>The selectors are a composition of CSS and custom additions. XPath selectors are available as a plugin.</p><p>All selectors available in jQuery are documented on the <a href="/Selectors" title="Selectors">Selectors API page</a>.</p><p>There are lot of plugins that leverage jQuery\'s selectors in other ways. The validation plugin accepts a selector to specify a dependency, whether an input is required or not:</p><pre> emailrules: {\n   required: "#email:filled"\n }\n</pre><p>This would make a checkbox with name "emailrules" required only if the user entered an email address in the email field, selected via its id, filtered via a custom selector ":filled" that the validation plugin provides.</p><p>If Selector is specified as the type of an argument, it accepts everything that the jQuery constructor accepts, eg. Strings, Elements, Lists of Elements.</p>',
		'Event': '<p>jQuery\'s event system normalizes the event object according to W3C standards. The event object is guaranteed to be passed to the event handler (no checks for window.event required). It normalizes the target, relatedTarget, which, metaKey and pageX/Y properties and provides both stopPropagation() and preventDefault() methods.</p><p>Those properties are all documented, and accompanied by examples, on the <a href="http://docs.jquery.com/Events/jQuery.Event" title="Events/jQuery.Event">Event</a> page.</p>',
		'Element': '<p>An element in the Document Object Model (DOM) has attributes, text and children. It provides methods to traverse the parent and children and to get access to its attributes. Due to a lot of flaws in DOM API specifications and implementations, those methods are no fun to use. jQuery provides a wrapper around those elements to help interacting with the DOM. But often enough you will be working directly with DOM elements, or see methods that (also) accept DOM elements as arguments.</p><p>Whenever you use jQuery\'s each-method, the context of your callback is set to a DOM element. That is also the case for event handlers.</p><p>Some properties of DOM elements are quite consistent among browsers. Consider this example of a simple on-blur-validation:</p><pre>$(":text").blur(function() {\n  if(!this.value) {\n   alert("Please enter some text!");\n  }\n});</pre><p>You could replace this.value with $(this).val() to access the value of the text input via jQuery, but in that case you don\'t gain anything.</p>'
	};
	$('#widget-docs dd.option-type').each(function() {
		
		var html = $(this).text();
		var words = $.map(html.split(','), function(n) { return paramDescriptions[$.trim(n)] ? '<span>'+$.trim(n)+'</span>' : $.trim(n); });
		$(this).html(words.join(', '));
		
		$('span', this).click(function() {
			if(paramDescriptions[this.innerHTML]) $('<div>'+paramDescriptions[this.innerHTML]+'</div>').dialog({ open: function() { $(this).parent().attr('id', 'demo-dialog'); }, modal: true, title: this.innerHTML, width: 500 });
		});

	});

	
	$("#widget-docs").tabs();
	$("#widget-docs > div").addClass('clearfix'); //This fixes clearing of containers

	//show details/hide details
	$("#options .options-list, #events .events-list").before('<div class="toggle-docs-links"><a class="toggle-docs-detail" href="#">Show details</a> | <a class="toggle-docs-example" href="#">Hide examples</a></div>');

	$("#methods .methods-list").before('<div class="toggle-docs-links"><a class="toggle-docs-detail" href="#">Show details</a></div>');

	var showExamples = true;
	$(".toggle-docs-detail").toggle(function(e){
		var details = $(this).text("Hide details")
			.parent().next("ul").find("li > div:first-child").addClass("header-open");
		if ( showExamples ) {
			details.nextAll().show();
		} else {
			details.next().show();
		}
		e.preventDefault();
	},function(e){
		$(this).text("Show details")
			.parent().next("ul").find("li > div:first-child").removeClass("header-open")
			.nextAll().hide();
		e.preventDefault();
	});

	$(".toggle-docs-example").click(function(e){
		if ( showExamples ) {
			showExamples = false;
			$(".toggle-docs-example").text("Show examples").parent().next("ul").find("div.header-open ~ .option-examples, div.header-open ~ .event-examples").hide();
		} else {
			showExamples = true;
			$(".toggle-docs-example").text("Hide examples").parent().next("ul").find("div.header-open ~ .option-examples, div.header-open ~ .event-examples").show();
		}
		e.preventDefault();
	});

	//Initially hide all options/methods/events
	$('div.option-description, div.option-examples, div.event-description, div.event-examples, div.method-description, div.method-examples').hide();
	
	//Make list items collapsible
	$('div.option-header h3, div.event-header h3, div.method-header h3').live('click', function() {
		var details = $(this).parent().toggleClass('header-open');
		if ( showExamples ) {
			details.nextAll().toggle();
		} else {
			details.next().toggle();
		}
	});
	
	//Hide theming tabs for interactions that cannot be themed
	(/(draggable|droppable|sortable)$/i).test(section) && $('#widget-docs > ul li:last').hide();

	//Load themeswitcher
	$('#switcher').themeswitcher({loadTheme: 'UI lightness'});
	
	listenToHashChange();

});

$(window).bind('load', function() {
	//If we use it as docs page, go to the selected option
	if(window.location.hash && window.location.href.indexOf('/docs/') != -1) {
		gotoHash();
	}
});

function listenToHashChange() {
	
	var savedHash = window.location.hash;
	
	window.setInterval(function() {
		
		if(savedHash != window.location.hash) {
			savedHash = window.location.hash;
			if(window.location.hash && window.location.href.indexOf('/docs/') != -1)
				gotoHash();
			//Since we have bind click event on demos link and load hash on document.ready
			//if(window.location.hash && window.location.href.indexOf('/demos/') != -1)
			//	loadHash();
		}
		
	},200);
	
}

function loadHash() {
	
	$('#demo-config-menu a').each(function() {
		if(this.getAttribute('href').indexOf('/'+window.location.hash.substr(1)+'.html') != -1) {

			$(this).parents('ul').find('li').removeClass('demo-config-on');
			$(this).parent().addClass('demo-config-on');

			loadDemo(this.getAttribute('href'));
		}
	});
	
}

function gotoHash() {
	
	var hash = window.location.hash.substr(1).split('-');
	var go = hash[1] ? hash[1] : hash[0];
	var resolve = { overview: 0,option: 1,event: 2,method: 3,theming: 4 };

	$("#widget-docs").tabs('select', resolve[hash[0]]);
	var h3 = $("#widget-docs a:contains("+go+")").parent();
	h3.parent().parent().toggleClass("param-open").end().next().toggle();
	
	$(document).scrollTop(h3.parent().effect('highlight', null, 2000).offset().top);
		
}