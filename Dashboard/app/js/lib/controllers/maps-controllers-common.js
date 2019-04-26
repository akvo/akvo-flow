import { isNaN } from 'lodash';
import observe from '../mixins/observe';

/**
  Controllers related to the map tab
  Definition:
    "placemark" is an FLOW object that represents a single survey point.
    "marker" is a map object that is rendered as a pin. Each marker have
      a placemark counterpart.
* */

FLOW.MapsController = Ember.ArrayController.extend(observe({
  'this.content.isLoaded': 'populateMap',
}), {
  content: null,
  map: null,
  geocellCache: [],
  currentGcLevel: null,
  allPlacemarks: null,
  selectedMarker: null,
  selectedSI: null,
  surveyDataLayer: null,

  populateMap() {
    if (this.content.get('isLoaded') && FLOW.selectedControl.selectedSurveyGroup) {
      const surveyId = FLOW.selectedControl.selectedSurveyGroup.get('keyId');

      const placemarks = FLOW.store.filter(FLOW.Placemark, item => item.get('surveyId') === surveyId);

      if (!this.allPlacemarks) {
        this.set('allPlacemarks', L.layerGroup());
        this.allPlacemarks.addTo(this.map);
      }

      placemarks.forEach(function (placemark) {
        const marker = this.addMarker(placemark);
        this.allPlacemarks.addLayer(marker);
      }, this);
    }
  },

  adaptMap(bestBB, zoomlevel) {
    let bbString = '';
    const gcLevel = 0;
    const listToRetrieve = [];

    this.set('currentGcLevel', gcLevel);
    // on zoomlevel 2, the map repeats itself, leading to wrong results
    // therefore, we force to download the highest level on all the world.
    if (zoomlevel == 2) {
      bestBB = '0123456789abcdef'.split('');
    }

    for (let i = 0; i < bestBB.length; i++) {
      listToRetrieve.push(bestBB[i]);
    }

    // pack best bounding box values in a string for sending to the server
    bbString = listToRetrieve.join(',');

    // go get it in the datastore
    // when the points come in, populateMap will trigger and place the points
    if (!Ember.empty(bbString)) {
      const requestParams = { bbString, gcLevel };
      if (FLOW.selectedControl.selectedSurveyGroup) {
        requestParams.surveyId = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
      }
      this.set('content', FLOW.store.findQuery(FLOW.Placemark, requestParams));
    } else {
      // we might have stuff in cache, so draw anyway
      this.populateMap();
    }
  },

  addMarker(placemark) {
    let marker;
    let count;

    function onMarkerClick(_marker) {
      // first deselect others
      if (!Ember.none(FLOW.router.mapsController.get('selectedMarker'))) {
        if (FLOW.router.mapsController.selectedMarker.target.options.placemarkId != _marker.target.options.placemarkId) { // eslint-disable-line max-len
          FLOW.router.mapsController.clearMarker();
        }
      }

      // now toggle this one
      _marker.target.setStyle({
        color: '#d46f12',
        fillColor: '#433ec9',
      });
      _marker.target.options.selected = true;
      FLOW.router.mapsController.set('selectedMarker', _marker);
    }

    if (placemark.get('level') > 0) {
      count = placemark.get('count');
      if (count == 1) {
        marker = L.circleMarker([placemark.get('latitude'), placemark.get('longitude')], {
          radius: 7,
          color: '#d46f12',
          fillColor: '#edb660',
          opacity: 0.9,
          fillOpacity: 0.7,
          placemarkId: placemark.get('keyId'),
          collectionDate: placemark.get('collectionDate'),
        });
        marker.on('click', onMarkerClick);
        return marker;
      }

      const myIcon = L.divIcon({
        html: `<div><span>${count}</span></div>`,
        className: 'marker-cluster',
        iconSize: new L.Point(40, 40),
      });

      marker = L.marker([placemark.get('latitude'), placemark.get('longitude')], {
        icon: myIcon,
        placemarkId: placemark.get('keyId'),
      });
      return marker;
    }
    // if we are here, we are at level 0, and we have normal placemark icons.
    marker = L.circleMarker([placemark.get('latitude'), placemark.get('longitude')], {
      radius: 7,
      color: '#d46f12',
      fillColor: '#edb660',
      placemarkId: placemark.get('keyId'),
      collectionDate: placemark.get('collectionDate'),
    });
    marker.on('click', onMarkerClick);
    return marker;
  },

  clearMarker() {
    if (!Ember.none(FLOW.router.mapsController.get('selectedMarker'))) {
      FLOW.router.mapsController.selectedMarker.target.options.selected = false;
      FLOW.router.mapsController.selectedMarker.target.setStyle({
        color: '#d46f12',
        fillColor: '#edb660',
      });
    }
    FLOW.router.mapsController.set('selectedMarker', null);
    FLOW.questionAnswerControl.set('content', null); // clear answers from side bar
    FLOW.placemarkDetailController.set('dataPoint', null); // clear details panel header
    FLOW.placemarkDetailController.set('dataPointCollectionDate', null); // in case previous point's collection date is still cached
    FLOW.placemarkDetailController.set('noSubmissions', false); // can't confirm absence of data if we haven't checked
  },

  formatDate(date) {
    if (date && !isNaN(date.getTime())) {
      return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
    }
    return null;
  },
});

FLOW.placemarkDetailController = Ember.ArrayController.create(observe({
  'FLOW.router.mapsController.selectedMarker': 'mapPointClickHandler',
  'FLOW.surveyInstanceControl.content.isLoaded': 'mapPointRetrieveDetailsHandler',
}), {
  content: Ember.A(),

  dataPoint: null,

  dataPointCollectionDate: null,
  noSubmissions: false,

  dataPointDisplayName: Ember.computed(function () {
    return this.dataPoint && this.dataPoint.get('displayName');
  }).property('this.dataPoint.isLoaded'),

  dataPointIdentifier: Ember.computed(function () {
    return this.dataPoint && this.dataPoint.get('identifier');
  }).property('this.dataPoint.isLoaded'),

  /*
  * Observer that loads a datapoint and its associated details when clicked
  */
  mapPointClickHandler() {
    const mapsController = FLOW.router.get('mapsController');
    if (mapsController && mapsController.get('selectedMarker')) {
      const selectedPlacemarkId = mapsController.selectedMarker.target.options.placemarkId;
      this.set('dataPoint', FLOW.store.find(FLOW.SurveyedLocale, selectedPlacemarkId));
      FLOW.surveyInstanceControl.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {
        surveyedLocaleId: selectedPlacemarkId,
      }));
    }
  },

  /*
  * Observer that retrieves question answers associated with a datapoint
  * when it is clicked on.
  *
  * !!! Only data from the REGISTRATION FORMS is loaded at the moment !!!
  */
  mapPointRetrieveDetailsHandler() {
    const formInstances = FLOW.surveyInstanceControl.content;
    const mapsController = FLOW.router.get('mapsController');

    if (Ember.empty(formInstances)
        || !formInstances.isLoaded
        || !mapsController.get('selectedMarker')) {
      return;
    }

    const survey = FLOW.selectedControl.get('selectedSurvey');
    if (!survey) return;

    const formId = survey.get('keyId');
    const formInstance = formInstances.filterProperty('surveyId', formId).get('firstObject');

    if (formInstance) {
      this.set('noSubmissions', false);
      this.set('dataPointCollectionDate', formInstance.get('collectionDate'));
      FLOW.questionAnswerControl.doQuestionAnswerQuery(formInstance);
    } else {
      this.set('noSubmissions', true);
      this.set('dataPointCollectionDate', null);
    }
  },
});
