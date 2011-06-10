// JavaScript Document
var isIE = false;
var eventType= 'change';
var map;
var initialCountry;
var countryLatLon;
var openWindow = null;
function getCountry() {
	if ($.jqURL.get('country')) {
		initialCountry = $.jqURL.get('country').toUpperCase();
	} else {
		initialCountry = 'MW';
		$("#tabs").tabs("select", 5);
	}
	setCountryLatLon(initialCountry);
}
var loadedMW = new Boolean(false);
var loadedHN = new Boolean(false);
var loadedPE = new Boolean(false);
var loadedRW = new Boolean(false);
var loadedGT = new Boolean(false);
var loadedIN = new Boolean(false);
var loadedSV = new Boolean(false);
var loadedNI = new Boolean(false);
var loadedBO = new Boolean(false);
var loadedDO = new Boolean(false);
var latlngRW = new google.maps.LatLng(-1.7, 30);
var latlngMW = new google.maps.LatLng(-15.79, 35);
var latlngHN = new google.maps.LatLng(15, -88);;
var latlngPE = new google.maps.LatLng(-7.5, -79);;
var latlngGT = new google.maps.LatLng(15, -90);;
var latlngIN = new google.maps.LatLng(21.7, 88.1);;
var latlngSV = new google.maps.LatLng(14, -89);
var latlngNI = new google.maps.LatLng(12.7, -85.7);
var latlngBO = new google.maps.LatLng(-17.648, -65.812);
var latlngDO = new google.maps.LatLng(19.015,-70.729);

function setCountryLatLon(country) {
	if (country == 'MW') {
		countryLatLon = latlngMW;
	} else if (country == 'RW') {
		countryLatLon = latlngRW;
	} else if (country == 'HN') {
		countryLatLon = latlngHN;
	} else if (country == 'PE') {
		countryLatLon = latlngPE;
	} else if (country == 'GT') {
		countryLatLon = latlngGT;
	} else if (country == 'IN') {
		countryLatLon = latlngIN;
	} else if (country == 'SV') {
		countryLatLon = latlngSV;
	} else if (country == 'NI') {
		countryLatLon = latlngNI;
	} else if (country == 'BO') {
		countryLatLon = latlngBO;
	} else if (country == 'DO'){
		countryLatLon= latlngDO;
	}else{
		countryLatLon = new google.maps.LatLng(-15, 35);
		alert('Water For People does not have projects in that country yet. So we will show you our work in Malawi.');
	}

}

function loadCountryData(countryToLoad) {
	if (countryToLoad == 'MW' && loadedMW == false) {
		getPlacemarkInfo('MW');
		loadedMW = true;
	} else if (countryToLoad == 'RW' && loadedRW == false) {
		getPlacemarkInfo('RW');
		loadedRW = true;
	} else if (countryToLoad == 'HN' && loadedHN == false) {
		getPlacemarkInfo('HN');
		loadedHN = true;
	} else if (countryToLoad == 'PE' && loadedPE == false) {
		getPlacemarkInfo('PE');
		loadedPE = true;
	} else if (countryToLoad == 'GT' && loadedGT == false) {
		getPlacemarkInfo('GT');
		loadedGT = true;
	} else if (countryToLoad == 'IN' && loadedIN == false) {
		getPlacemarkInfo('IN');
		loadedIN = true;
	} else if (countryToLoad == 'SV' && loadedSV == false) {
		getPlacemarkInfo('SV');
		loadedSV = true;
	} else if (countryToLoad == 'NI' && loadedNI == false) {
		getPlacemarkInfo('NI');
		loadedNI = true;
	} else if (countryToLoad == 'BO' && loadedBO == false) {
		getPlacemarkInfo('BO');
		loadedBO = true;
	}else if(countryToLoad =='DO' && loadedDO == false){
		getPlacemarkInfo('DO');
		loadedDO = true;
	}
}

function initialize() {
	getCountry();
	var myOptions = {
		zoom : 7,
		center : countryLatLon,
		mapTypeId : google.maps.MapTypeId.ROADMAP
	}
	var isSmartphone = false;
	if (navigator.userAgent.match(/iPhone/i)
			|| navigator.userAgent.match(/iPod/i)
			|| navigator.userAgent.match(/android/i)) {
		isSmartphone = true;
	}else if (navigator.appName == 'Microsoft Internet Explorer'){
		isIE = true;
		eventType = 'click';
	}


	if (isSmartphone) {
		document.getElementById("map_canvas").style.width = '90%';

	}

	map = new google.maps.Map(document.getElementById("map_canvas"),
			myOptions);
	createCountryControl();
	//createChartControl();
	if (!isSmartphone) {
		createLegend();
	}

	google.maps.event.addListener(map, 'bounds_changed', function() {

		var curBounds = map.getBounds();
		var containsRW = curBounds.contains(latlngRW);
		var containsMW = curBounds.contains(latlngMW);
		var containsHN = curBounds.contains(latlngHN);
		var containsIN = curBounds.contains(latlngIN);
		var containsGT = curBounds.contains(latlngGT);
		var containsSV = curBounds.contains(latlngSV);
		var containsPE = curBounds.contains(latlngPE);
		var containsNI = curBounds.contains(latlngNI);
		var containsBO = curBounds.contains(latlngBO);
		var containsDO = curBounds.contains(latlngDO);

		if (containsRW) {				
			loadCountryData('RW');
		}

		if (containsMW) {				
			loadCountryData('MW');
		}

		if (containsHN) {				
			loadCountryData('HN');
		}
		if (containsIN) {				
			loadCountryData('IN');
		}
		if (containsGT) {				
			loadCountryData('GT');
		}
		if (containsSV) {				
			loadCountryData('SV');
		}
		if (containsPE) {				
			loadCountryData('PE');
		}
		if (containsNI) {				
			loadCountryData('NI');
		}
		if (containsBO) {				
			loadCountryData('BO');
		}
		if(containsDO){
			loadCountryData('DO');
		}
	});
	loadCountryData(initialCountry);
};
var markers = [];
function addPlacemark(latitude, longitude, iconUrl, placemarkContents) {
	var infowindow = new google.maps.InfoWindow({
		content : placemarkContents,
		maxWidth : 1000
	});
	var myLatlng = new google.maps.LatLng(latitude, longitude);
	var marker = new google.maps.Marker({
		position : myLatlng,
		map : map,
		title : 'Water For People',
		icon : iconUrl
	});
	google.maps.event.addListener(marker, 'click', function() {
		var infoOptions = {
			maxWidth : 1000
		};
		infowindow.setOptions(infoOptions);
		infowindow.open(map, marker);
	});
};

function makeMarker(latitude, longitude, iconUrl, communityCode, pointType,
		placemarkContents) {
	var markerOptions = {
		map : map,
		position : new google.maps.LatLng(latitude, longitude),
		icon : iconUrl,
		height : 600,
		width : 600,
		title : communityCode
	};
	var marker = new google.maps.Marker(markerOptions);
	markers.push(marker);

	google.maps.event
			.addListener(
					marker,
					'click',
					function(e) {
						var communityCode = marker.title;
						var iconString = marker.icon;
						var url = '/placemarkrestapi?action=getAPDetails&display=external&communityCode='
								+ communityCode
								+ '&pointType='
								+ iconString;
						$.getJSON(url, function(jd) {
							var count = 0;
							$.each(jd.placemarks, function(i, item) {
								if (openWindow != null) {
									openWindow.onRemove();
								}
								var infobox = new SmartInfoWindow({
									position : marker.getPosition(),
									map : map,
									content : item.placemarkContents
								});
								infobox.onAdd = function() {
								    this.createElement();
								    $('.scroll-pane-info').jScrollPane({
										showArrows : true
									});
								};
								openWindow = infobox;
								count++;
							});
							if (count > 0 && jd.cursor != null) {
								getPlacemarkInfo(countryCode, jd.cursor);
							}
						});
					});
}

function clearOverlays() {
	  if (markers) {
	    for (i in markers) {
	      markers[i].setMap(null);
	    }
	  }
	}


function getPlacemarkInfo(countryCode, cursor) {
	var url = '/placemarkrestapi?country=' + countryCode
			+ '&needDetailsFlag=false&maxResults=200';
	if(currSelectedOrg == "wfp"){
		url += '&org=wfp';
	}else if(currSelectedOrg == 'other'){
		url += '&org=other';
	}
	
	if (cursor != null) {
		url += '&cursor=' + cursor;
	}
	$.getJSON(url, function(jd) {
		var count = 0;
		$.each(jd.placemarks, function(i, item) {
			makeMarker(item.latitude, item.longitude, item.iconUrl,
					item.communityCode, item.markType,
					item.placemarkContents);
			count++;
		});
		if (count > 0 && jd.cursor != null) {
			getPlacemarkInfo(countryCode, jd.cursor);
		}
	});
};

function createCountryControl() {
	// Create a div to hold the control.
	var controlDiv = document.createElement('DIV');

	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	controlDiv.style.padding = '5px';

	// Set CSS for the control border
	var controlUI = document.createElement('DIV');
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '2px';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Jumps to a specific country';
	controlDiv.appendChild(controlUI);

	// Set CSS for the control interior
	var controlText = document.createElement('DIV');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '12px';
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = 'Jump to: <select id=countrySel><option></option><option value="BO">Bolivia</option><option value="SV">El Salvador</option><option value="GT">Guatemala</option><option value="HN">Honduras</option><option value="IN">India</option><option value="MW">Malawi</option><option value="NI">Nicaragua</option><option value="PE">Peru</option><option value="RW">Rwanda</option><option value="DO">Dominican Republic</option></select>';
	controlUI.appendChild(controlText);
	map.controls[google.maps.ControlPosition.TOP_RIGHT].push(controlDiv);
	google.maps.event
			.addDomListener(
					controlUI,
					eventType,
					function() {
						if (document.getElementById("countrySel").selectedIndex == 1) {
							map.setCenter(latlngBO);
							map.setZoom(10);
							$("#tabs").tabs("select", 0);
						} else if (document.getElementById("countrySel").selectedIndex == 2) {
							map.setCenter(latlngSV);
							map.setZoom(10);
							$("#tabs").tabs("select", 1);
						} else if (document.getElementById("countrySel").selectedIndex == 3) {
							map.setCenter(latlngGT);
							map.setZoom(10);
							$("#tabs").tabs("select", 2);
						} else if (document.getElementById("countrySel").selectedIndex == 4) {
							map.setCenter(latlngHN);
							map.setZoom(10);
							$("#tabs").tabs("select", 3);
						} else if (document.getElementById("countrySel").selectedIndex == 5) {
							map.setCenter(latlngIN);
							map.setZoom(10);
							$("#tabs").tabs("select", 4);
						} else if (document.getElementById("countrySel").selectedIndex == 6) {
							map.setCenter(latlngMW);
							map.setZoom(10);
							$("#tabs").tabs("select", 5);
						} else if (document.getElementById("countrySel").selectedIndex == 7) {
							map.setCenter(latlngNI);
							map.setZoom(10);
							$("#tabs").tabs("select", 6);
						} else if (document.getElementById("countrySel").selectedIndex == 8) {
							map.setCenter(latlngPE);
							map.setZoom(10);
							$("#tabs").tabs("select", 7);
						} else if (document.getElementById("countrySel").selectedIndex == 9) {
							map.setCenter(latlngRW);
							map.setZoom(10);
							$("#tabs").tabs("select", 8);
						}else if (document.getElementById("countrySel").selectedIndex == 10) {
							map.setCenter(latlngDO);
							map.setZoom(10);
							$("#tabs").tabs("select", 9);
						}
					});

}


function createChartControl() {
	// Create a div to hold the control.
	var controlDiv = document.createElement('DIV');

	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	controlDiv.style.padding = '5px';

	// Set CSS for the control border
	var controlUI = document.createElement('DIV');
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '2px';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Jumps to a specific country';
	controlDiv.appendChild(controlUI);

	// Set CSS for the control interior
	var controlText = document.createElement('DIV');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '12px';
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = 'See summary for: <select id=chartSel name=chartSel><option></option><option value="SV">El Salvador</option><option value="GT">Guatemala</option><option value="HN">Honduras</option><option value="IN">India</option><option value="MW">Malawi</option><option value="NI">Nicaragua</option><option value="PE">Peru</option><option value="RW">Rwanda</option></select>';
	controlUI.appendChild(controlText);
	map.controls[google.maps.ControlPosition.TOP_RIGHT].push(controlDiv);
	google.maps.event.addDomListener(controlUI, 'click', function() {
		var center;
		if (document.getElementById("chartSel").selectedIndex == 1) {
			displayChart("BO", latlngBO);
		} else if (document.getElementById("chartSel").selectedIndex == 2) {
			displayChart("SV", latlngSV);
		} else if (document.getElementById("chartSel").selectedIndex == 3) {
			displayChart("GT", latlngGT);
		} else if (document.getElementById("chartSel").selectedIndex == 4) {
			displayChart("HN", latlngHN);
		} else if (document.getElementById("chartSel").selectedIndex == 5) {
			displayChart("IN", latlngIN);
		} else if (document.getElementById("chartSel").selectedIndex == 6) {
			displayChart("MW", latlngMW);
		} else if (document.getElementById("chartSel").selectedIndex == 7) {
			displayChart("NI", laglngNI);
		} else if (document.getElementById("chartSel").selectedIndex == 8) {
			displayChart("PE", latlngPE);
		} else if (document.getElementById("chartSel").selectedIndex == 9) {
			displayChart("RW", latlngRW);
		} else if(document.getElementById("chartSel").selectedIndex ==10){
			displayChart("DO".latlngDO);
		}
	});
}

function displayChart(countryCode, center) {
	var url = '/charturlrestapi?action=getAPStatus&country=' + countryCode;
	$.getJSON(url, function(jd) {
		if (jd != null) {
			var resultUrl = jd.message;
			if (resultUrl != null && resultUrl.substr(0, 4) == 'http') {
				var infobox = new google.maps.InfoWindow({
					position : center,
					content : '<div><img src="'+jd.message+'"></div>'
				});
				map.setZoom(10);
				infobox.open(map);
			}
		}
	});
}

function createLegend() {
	var controlDiv = document.createElement('DIV');

	// Set CSS styles for the DIV containing the control
	// Setting padding to 5 px will offset the control
	// from the edge of the map
	controlDiv.style.padding = '5px';

	// Set CSS for the control border
	var controlUI = document.createElement('DIV');
	controlUI.style.backgroundColor = 'white';
	controlUI.style.borderStyle = 'solid';
	controlUI.style.borderWidth = '2px';
	controlUI.style.cursor = 'pointer';
	controlUI.style.textAlign = 'center';
	controlUI.title = 'Legend';
	controlDiv.appendChild(controlUI);

	// Set CSS for the control interior
	/*var controlText = document.createElement('DIV');
	controlText.style.fontFamily = 'Arial,sans-serif';
	controlText.style.fontSize = '12px';
	controlText.style.paddingLeft = '4px';
	controlText.style.paddingRight = '4px';
	controlText.innerHTML = '<img height=200 width=200 src=/images/WFPkey.jpg>';
	controlUI.appendChild(controlText);
	map.controls[google.maps.ControlPosition.BOTTOM_LEFT].push(controlDiv);*/
};


$(function() {
	$("#tabs").tabs();
	
	
$('#tabs').bind('tabsselect',function(event,ui){
	jQuery("select#countrySel option[selected]").removeAttr("selected");
	if(ui.index == 0){
		jQuery("select#countrySel option[value='BO']").attr("selected", "selected");
		map.setCenter(latlngBO);
		map.setZoom(10);
	}else if (ui.index == 1){
		jQuery("select#countrySel option[value='SV']").attr("selected", "selected");
		map.setCenter(latlngSV);
		map.setZoom(10);
	}else if (ui.index == 2){
		jQuery("select#countrySel option[value='GT']").attr("selected", "selected");
		map.setCenter(latlngGT);
		map.setZoom(10);
	}else if (ui.index == 3){
		jQuery("select#countrySel option[value='HN']").attr("selected", "selected");
		map.setCenter(latlngHN);
		map.setZoom(10);
	}else if (ui.index == 4){
		jQuery("select#countrySel option[value='IN']").attr("selected", "selected");
		map.setCenter(latlngIN);
		map.setZoom(10);
	}else if (ui.index == 5){
		jQuery("select#countrySel option[value='MW']").attr("selected", "selected");
		if(map!=null){
			map.setCenter(latlngMW);
			map.setZoom(10);
		}
	}else if (ui.index == 6){
		jQuery("select#countrySel option[value='NI']").attr("selected", "selected");
		map.setCenter(latlngNI);
		map.setZoom(10);
	}else if (ui.index == 7){
		jQuery("select#countrySel option[value='PE']").attr("selected", "selected");
		map.setCenter(latlngPE);
		map.setZoom(10);
	}else if (ui.index == 8){
		jQuery("select#countrySel option[value='RW']").attr("selected", "selected");
		map.setCenter(latlngRW);
		map.setZoom(10);
	}else if (ui.index == 9){
		jQuery("select#countrySel option[value='DO']").attr("selected", "selected");
		map.setCenter(latlngDO);
		map.setZoom(10);
	}
	
	
});


	});