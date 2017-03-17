/**
  Controllers related to the map tab
  Definition:
    "placemark" is an FLOW object that represents a single survey point.
    "marker" is a map object that is rendered as a pin. Each marker have
      a placemark counterpart.
**/

FLOW.placemarkController = Ember.ArrayController.create({
  content: null,
  map: null,
  geocellCache: [],
  currentGcLevel: null,
  allPlacemarks: null,
  selectedMarker:null,

  populateMap: function () {
    var gcLevel, placemarks, placemarkArray=[];
    if (this.content.get('isLoaded') === true) {
      gcLevel = this.get('currentGcLevel');
      // filter placemarks
      placemarks = FLOW.store.filter(FLOW.Placemark, function(item){
        return item.get('level') == gcLevel;
      });

      placemarks.forEach(function (placemark) {
        marker = this.addMarker(placemark);
        placemarkArray.push(marker);
      }, this);

      if (!Ember.none(this.allPlacemarks)){
        this.allPlacemarks.clearLayers();
      }

      this.allPlacemarks = L.layerGroup(placemarkArray);
      this.allPlacemarks.addTo(this.map);
    }
  }.observes('this.content.isLoaded'),

  adaptMap: function(bestBB, zoomlevel){
    var bbString = "", gcLevel, listToRetrieve = [];

    // determine the geocell cluster level we want to show
    if (zoomlevel < 4){
      gcLevel = 2;
    } else if (zoomlevel < 6) {
      gcLevel = 3;
    } else if (zoomlevel < 8) {
      gcLevel = 4;
    } else if (zoomlevel < 11) {
      gcLevel = 5;
    } else {
      gcLevel = 0;
    }
    this.set('currentGcLevel',gcLevel);
    // on zoomlevel 2, the map repeats itself, leading to wrong results
    // therefore, we force to download the highest level on all the world.
    if (zoomlevel == 2) {
    	bestBB = "0123456789abcdef".split("");
    }

    // see if we already have it in the cache
    // in the cache, we use a combination of geocell and gcLevel requested as the key:
    // for example "af-3", "4ee-5", etc.
    // TODO this is not optimal at high zoom levels, as we will already have loaded the same points on a level before
    for (var i = 0; i < bestBB.length; i++){
      if (this.get('geocellCache').indexOf(bestBB[i]+"-"+gcLevel) < 0 ) {
        // if we don't have it in the cache add it to the list of items to be loaded
    	listToRetrieve.push(bestBB[i]);

    	// now add this key to cache
        this.get('geocellCache').push(bestBB[i] + "-" + gcLevel);
      }
    }

    // pack best bounding box values in a string for sending to the server
    bbString = listToRetrieve.join(',');

    // go get it in the datastore
    // when the points come in, populateMap will trigger and place the points
    if (!Ember.empty(bbString)){
      this.set('content',FLOW.store.findQuery(FLOW.Placemark,
        {bbString: bbString, gcLevel: gcLevel}));
    } else {
    	// we might have stuff in cache, so draw anyway
    	this.populateMap();
    }
  },

  addMarker: function (placemark) {
    var marker;
    if (placemark.get('level') > 0){
    	count = placemark.get('count');
    	if (count == 1) {
    		marker = L.circleMarker([placemark.get('latitude'),placemark.get('longitude')],{
    			radius:7,
    			color:'#d46f12',
    			fillColor:'#edb660',
    			opacity:0.9,
    			fillOpacity:0.7,
    			placemarkId: placemark.get('detailsId'),
    			collectionDate:placemark.get('collectionDate')});
    		marker.on('click', onMarkerClick);
    		return marker;
    	}

      myIcon = L.divIcon({
    	  html: '<div><span>' + count + '</span></div>',
    	  className: 'marker-cluster',
    	  iconSize: new L.Point(40, 40)});

      marker = L.marker([placemark.get('latitude'),placemark.get('longitude')], {
    	  icon: myIcon,
    	  placemarkId: placemark.get('keyId')});
      return marker;
    } else {
    	// if we are here, we are at level 0, and we have normal placemark icons.
    	marker = L.circleMarker([placemark.get('latitude'),placemark.get('longitude')],{
    		radius:7,
    		color:'#d46f12',
    		fillColor:'#edb660',
    		placemarkId: placemark.get('detailsId'),
    		collectionDate:placemark.get('collectionDate')});
		marker.on('click', onMarkerClick);
    	return marker;
    }

    function onMarkerClick(marker){
    	// first deselect others
    	if (!Ember.none(FLOW.placemarkController.get('selectedMarker'))){
    		if (FLOW.placemarkController.selectedMarker.target.options.placemarkId != marker.target.options.placemarkId){
    			FLOW.placemarkController.selectedMarker.target.options.selected = false;
                FLOW.placemarkController.selectedMarker.target.setStyle({color:'#d46f12',
    	    		fillColor:'#edb660'});
    			FLOW.placemarkController.set('selectedMarker',null);
    		}
    	}

    	// now toggle this one
    	if (marker.target.options.selected) {
    		marker.target.setStyle({color:'#d46f12',
    	    		fillColor:'#edb660'});
    		marker.target.options.selected = false;
    		FLOW.placemarkController.set('selectedMarker',null);
    	} else {
    		marker.target.setStyle({color:'#d46f12',
	    		fillColor:'#433ec9'});
    		marker.target.options.selected = true;
    		FLOW.placemarkController.set('selectedMarker',marker);
    	}
    }
  }

});

FLOW.placemarkDetailController = Ember.ArrayController.create({
  content: Ember.A(),
  sortProperties: ['order'],
  sortAscending: true,
  collectionDate: null,

  populate: function (placemarkId) {
	  if (placemarkId) {
		  this.set('content', FLOW.store.findQuery(FLOW.PlacemarkDetail, {
			  placemarkId: placemarkId
		  }));
	  } else {
		  this.set('content', Ember.A());
	  }
  },

  handlePlacemarkSelection: function () {
    var selectedPlacemarkId = null;
    if (!Ember.none(FLOW.placemarkController.get('selectedMarker'))) {
    	selectedPlacemarkId = FLOW.placemarkController.selectedMarker.target.options.placemarkId;
    	this.set('collectionDate',FLOW.placemarkController.selectedMarker.target.options.collectionDate);
    }
    this.populate(selectedPlacemarkId);
  }.observes('FLOW.placemarkController.selectedMarker'),

  photoUrl: function () {
    var photoDetails, photoUrls = [],
      rawPhotoUrl, photoJson;

    if (!this.get('content').get('isLoaded')) {
      return null;
    }

    // filter out details with images
    photoDetails = this.get('content').filter(function (detail) {
      return detail.get('questionType') === 'PHOTO';
    });

    if (Ember.empty(photoDetails)) {
      return null;
    }

    photoDetails.forEach(function (photo) {
      rawPhotoUrl = photo.get('stringValue') || '';
      if (rawPhotoUrl.charAt(0) === '{') {
        photoJson = JSON.parse(rawPhotoUrl);
        rawPhotoUrl = photoJson.filename;
      }
      // Since photos have a leading path from devices that we need to trim
      photoUrls.push(FLOW.Env.photo_url_root + rawPhotoUrl.split('/').pop());
    });

    return Ember.ArrayController.create({
      content: photoUrls
    });
  }.property('content.isLoaded')

});
