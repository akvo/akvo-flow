/*
jqURL
by Josh Nathanson

various manipulations on url strings and windows.  
all functions can also take a window object as an argument,
for example {win:opener}
but will default to current window if none is passed.

public functions:

-------------------------
.url({ 
	 win:window object 
	 })
-------------------------
returns the whole url string
like win.location.href

	so if the current window href is "http://www.mysite.com?var1=1&var2=2&var3=3"
	
	$.jqURL.url() returns "http://www.mysite.com?var1=1&var2=2&var3=3"
	
	
------------------------------
.loc(urlstr:string, 
	 { 
	 win:window object, 
	 w:integer, 
	 h:integer, 
	 t:integer,
	 l:integer,
	 wintype:string('_top'[default],'_blank','_parent') )
	})
------------------------------																			 
- directs passed in window to urlstr, which is required
- works like window.location.href = 'myurl'
- but you can also use it to pop open a new window by passing in "_blank" as the wintype
- if popping open a window, defaults to center of screen

	so
	$.jqURL.loc('http://www.google.com',
				{w:200,h:200,wintype:'_blank'});
	would open Google in a new centered 200x200 window
	
	or, locate an url to any named window:
	$.jqURL.loc('http://www.google.com',{ win:mywindow });
	opens Google in mywindow


------------------------------
.qs({ 
	ret:string('string'[default],'object'), 
	win:window object })
------------------------------
returns querystring, either string (pass ret:'string' [default])
or object (pass ret:'object') 

	so if the current window href is "http://www.mysite.com?var1=1&var2=2&var3=3"

	$.jqURL.qs();
	returns "var1=1&var2=2&var3=3"
	
	$.jqURL.qs({ ret:'object' });
	returns Object var1=1,var2=2,var3=3


------------------------------
.strip({ keys:string(list of keys to strip), win:window object })
------------------------------
if passed with no arguments, returns url with '?' and query string removed
if you pass in list of keys, it returns url with the specified key-value pairs removed

	so if the current window href is "http://www.mysite.com?var1=1&var2=2&var3=3"

	$.jqURL.strip();
	will return
	"http://www.mysite.com"
	
	$.jqURL.strip({ keys:'var1,var2' });
	will return
	"http://www.mysite.com?var3=3"
	
	
-------------------------------------
.get(key, {win:window object})
-------------------------------------
returns value of passed in querystring key

	so if the current window href is "http://www.mysite.com?var1=1&var2=2&var3=3"
	$.jqURL.get('var2');
	will return 2

--------------------------------------
.set(hash, {win:window object})
--------------------------------------
returns the window's url, but with the keys/values set in the query string
if the keys already exist, re-sets the value
if they don't exist, they're appended onto the query string

*/

jQuery.jqURL = {

	url : // returns a string
	function(args) {
		args = 
			jQuery.extend({
				win : window
			},
			args);
		return args.win.location.href;
	},
	
	loc : 
	function(urlstr, args) {
		args = 
			jQuery.extend({
				win : window,
				w : 500,
				h : 500,
				wintype : '_top'
			},
			args);
			
		if (!args.t) {
			args.t = screen.height / 2 - args.h / 2;
		}
		if (!args.l) {
			args.l = screen.width / 2 - args.w / 2;
		}
		if (args['wintype'] == '_top') {
			args.win.location.href = urlstr;
		}
		else {			
			open(
			urlstr,
			args['wintype'],
			'width=' + args.w + ',height=' + args.h + ',top=' + args.t + ',left=' + args.l + ',scrollbars,resizable'
			);
		
		}
		return;
	},
	
	qs :
	function(args) {
		args = jQuery.extend({
			ret : 'string',
			win : window
		},
		args);
		
		if (args['ret'] == 'string') {
			return jQuery.jqURL.url({ win:args.win }).split('?')[1];
			}

		else if (args['ret'] == 'object') {
			
			var qsobj = {};
			var thisqs = jQuery.jqURL.url({ win:args.win }).split('?')[1];
			
			if ( thisqs ) {
				var pairs = thisqs.split('&');
				for ( i=0;i<pairs.length;i++ ) {
					var pair = pairs[i].split('=');
					qsobj[pair[0]] = pair[1];
				}
			}
			return qsobj;
		}
	},
	
	strip :
	function(args) {
		args = jQuery.extend({
			keys : '',
			win : window
			},
			args);
		
		if (jQuery.jqURL.url().indexOf('?') == -1) { // no query string found
			return jQuery.jqURL.url({ win:args.win });
		}
		// if no keys passed in, just return url with no querystring
		else if (!args.keys) {
			return jQuery.jqURL.url({ win:args.win }).split('?')[0];
		}
		else { //return stripped url

			var qsobj = jQuery.jqURL.qs({ ret:'object',win:args.win });  // object with key/value pairs		
			var counter = 0;
			var url = jQuery.jqURL.url({ win:args.win }).split('?')[0] + '?';
			var amp = '';
			
			for (var key in qsobj) {
				if (args.keys.indexOf(key) == -1) { 
					// pass test, add this key/value to string
					amp = (counter) ? '&' : '';
					url = url + amp + key + '=' + qsobj[key];
					counter++;
				}
			}
			return url;
		}			
	},
	
	get :
	function(key,args) {
		args = jQuery.extend({
			win : window
			},args);
	
	qsobj =  jQuery.jqURL.qs({ ret:'object', win:args.win });
	return qsobj[key];
	},
	
	set :
	function(hash,args) {
		args = jQuery.extend({
			win : window
			},args);
		
		// get current querystring
		var qsobj =  jQuery.jqURL.qs({ ret:'object',win:args.win });
		
		// add/set values from hash
		for (var i in hash) {
			qsobj[i] = hash[i];
		}
		
		var qstring = '';
		var counter = 0;
		var amp = '';
		
		// turn qsobj into string
		for (var k in qsobj) {
			amp = (counter) ? '&' : '';
			qstring = qstring + amp + k + '=' + qsobj[k];
			counter++;
		}
		return jQuery.jqURL.strip({ win: args.win }) + '?' + qstring;
	}
	
};
