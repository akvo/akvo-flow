
function capitaliseFirstLetter(string) {
	if (Ember.empty(string)) return "";
	return string.charAt(0).toUpperCase() + string.slice(1);
}

FLOW.attributeTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: "text",
      value: "String"
    }), Ember.Object.create({
      label: "number",
      value: "Double"
    })
  ]
});

FLOW.attributeControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,

  setFilteredContent: function () {
    this.set('content', FLOW.store.filter(FLOW.Metric, function (item) {
      return true;
    }));
  },

  // load all Survey Groups
  populate: function () {
    FLOW.store.find(FLOW.Metric);
    this.setFilteredContent();
    this.set('sortProperties', ['name']);
    this.set('sortAscending', true);
  },

  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});

FLOW.cascadeResourceControl = Ember.ArrayController.create({
	content:null,
	published:null,
	levelNames:null,
	displayLevelName1: null, displayLevelName2: null, displayLevelName3: null,
	displayLevelNum1: null, displayLevelNum2: null, displayLevelNum3: null,

	populate: function() {
		this.set('content', FLOW.store.find(FLOW.CascadeResource));
		this.set('published',FLOW.store.filter(FLOW.CascadeResource,function(item){
			return item.get('status') === 'PUBLISHED';
		}));
	},

	setLevelNamesArray: function(){
		var i=1, levelNamesArray=[], numLevels;
		numLevels = FLOW.selectedControl.selectedCascadeResource.get('numLevels');

		// store the level names in an array
		FLOW.selectedControl.selectedCascadeResource.get('levelNames').forEach(function(item){
			if (i <= numLevels) {
				levelNamesArray.push(Ember.Object.create({
					levelName: item,
					level:i}));
				i++;
			}
		});
		this.set('levelNames',levelNamesArray);
	},

	setDisplayLevelNames: function(){
		var skip, names, numLevels;
		skip = FLOW.cascadeNodeControl.get('skip');
		names = this.get('levelNames');
		numLevels = FLOW.selectedControl.selectedCascadeResource.get('numLevels');
		this.set('displayLevelName1',names[skip].get('levelName'));
		if (numLevels > 1) {
			this.set('displayLevelName2',names[skip + 1].get('levelName'));
		} else {
			this.set('displayLevelName2',"");
		}
		if (numLevels > 2) {
			this.set('displayLevelName3',names[skip + 2].get('levelName'));
		} else {
			this.set('displayLevelName3',"");
		}
		this.set('displayLevelNum1',skip + 1);
		this.set('displayLevelNum2',skip + 2);
		this.set('displayLevelNum3',skip + 3);
	},

	publish: function(cascadeResourceId){
		FLOW.store.findQuery(FLOW.Action, {
		    action: 'publishCascade',
		    cascadeResourceId: cascadeResourceId
		});
	},

	hasQuestions: function () {
		if (!FLOW.selectedControl.selectedCascadeResource || !FLOW.selectedControl.selectedCascadeResource.get('keyId')) {
			return;
		}
		FLOW.store.findQuery(FLOW.Question, {cascadeResourceId: FLOW.selectedControl.selectedCascadeResource.get('keyId')});
	}.observes('FLOW.selectedControl.selectedCascadeResource'),

	currentStatus: function () {
		// hack to get translation keys, don't delete them
		// {{t _not_published}}
		// {{t _publishing}}
		// {{t _published}}
		var status;
		if (!FLOW.selectedControl.selectedCascadeResource) {
			return '';
		}
		status = ('_' + FLOW.selectedControl.selectedCascadeResource.get('status')).toLowerCase();
		return Ember.String.loc(status);
	}.property('FLOW.selectedControl.selectedCascadeResource'),

	isPublished: function () {
		if (!FLOW.selectedControl.selectedCascadeResource) {
			return false;
		}
		return FLOW.selectedControl.selectedCascadeResource.get('status') === 'PUBLISHED';
	}.property('FLOW.selectedControl.selectedCascadeResource')
});

FLOW.cascadeNodeControl = Ember.ArrayController.create({
	content:null,
	level1:[], level2:[], level3:[], level4:[], level5:[], level6:[], level7:[],
	displayLevel1:[], displayLevel2:[], displayLevel3:[],
	parentNode:[],
	selectedNode:[],
	selectedNodeTrigger: true,
	skip: 0,

	emptyNodes: function(start){
		var i;
		for (i=start ; i < 6 ; i++){
			this.selectedNode[i]=null;
			this.set('level' + i,[]);
		}
	},

	toggleSelectedNodeTrigger:function (){
		this.toggleProperty('selectedNodeTrigger');
	},

	setDisplayLevels: function(){
		this.set('displayLevel1',this.get('level' + (this.get('skip') + 1)));
		this.set('displayLevel2',this.get('level' + (this.get('skip') + 2)));
		this.set('displayLevel3',this.get('level' + (this.get('skip') + 3)));
	},

	populate: function(cascadeResourceId, level, parentNodeId) {
		if (!cascadeResourceId) {
			return;
		}
		this.set('content',FLOW.store.findQuery(FLOW.CascadeNode, {
	        cascadeResourceId: cascadeResourceId,
	        parentNodeId: parentNodeId
	      }));
		this.set('level' + level, FLOW.store.filter(FLOW.CascadeNode, function(item){
			return (item.get('parentNodeId') == parentNodeId && item.get('cascadeResourceId') == cascadeResourceId);
		}));
		this.parentNode[level] = parentNodeId;
		FLOW.cascadeNodeControl.setDisplayLevels();
	},

	addNode: function(cascadeResourceId, level, text, code) {
		var parentNodeId = this.get('parentNode')[level];
		FLOW.store.createRecord(FLOW.CascadeNode, {
			"code": code,
			"name": capitaliseFirstLetter(text),
			"nodeId": null,
			"parentNodeId": parentNodeId,
			"cascadeResourceId": cascadeResourceId
        });
		FLOW.store.commit();
		this.populate(cascadeResourceId, level, parentNodeId);
	},
});


FLOW.surveyInstanceControl = Ember.ArrayController.create({
  sortProperties: ['collectionDate'],
  sortAscending: false,
  selectedSurvey: null,
  content: null,
  sinceArray: [],
  pageNumber: 0,

  populate: function () {
    this.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {}));
  },

  doInstanceQuery: function (surveyId, deviceId, since, beginDate, endDate, submitterName, countryCode, level1, level2) {
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {
      'surveyId': surveyId,
      'deviceId': deviceId,
      'since': since,
      'beginDate': beginDate,
      'endDate': endDate,
      'submitterName': submitterName,
      'countryCode': countryCode,
      'level1': level1,
      'level2': level2
    }));
  },

  contentChanged: function() {
    var mutableContents = [];

    this.get('arrangedContent').forEach(function(item) {
        mutableContents.pushObject(item);
    });

    this.set('currentContents', mutableContents);
  }.observes('content', 'content.isLoaded'),

  removeInstance: function(instance) {
    this.get('currentContents').forEach(function(item, i, currentContents) {
        if (item.get('id') == instance.get('id')) {
            currentContents.removeAt(i, 1);
        }
    });
  },

  allAreSelected: function (key, value) {
    if (arguments.length === 2) {
      this.setEach('isSelected', value);
      return value;
    } else {
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }
  }.property('@each.isSelected'),

  atLeastOneSelected: function () {
    return this.filterProperty('isSelected', true).get('length');
  }.property('@each.isSelected'),

  // fired from tableColumnView.sort
  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});

FLOW.surveyedLocaleControl = Ember.ArrayController.create({
  sortProperties: ['collectionDate'],
  sortAscending: false,
  selectedSurvey: null,
  content: null,
  sinceArray: [],
  pageNumber: 0,

  populate: function () {
    this.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.set('content', FLOW.store.findQuery(FLOW.SurveyedLocale, {}));
  }
});

FLOW.questionAnswerControl = Ember.ArrayController.create({
  content: null,

  doQuestionAnswerQuery: function (surveyInstanceId) {
    this.set('content', FLOW.store.findQuery(FLOW.QuestionAnswer, {
      'surveyInstanceId': surveyInstanceId
    }));
  }
});

FLOW.locationControl = Ember.ArrayController.create({
  selectedCountry: null,
  content:null,
  level1Content:null,
  level2Content:null,
  selectedLevel1: null,
  selectedLevel2: null,

  populateLevel1: function(){
    if (!Ember.none(this.get('selectedCountry')) && this.selectedCountry.get('iso').length > 0){
    this.set('level1Content',FLOW.store.findQuery(FLOW.SubCountry,{
      countryCode:this.selectedCountry.get('iso'),
      level:1,
      parentId:null
      }));
    }
  }.observes('this.selectedCountry'),

 populateLevel2: function(){
    if (!Ember.none(this.get('selectedLevel1')) && this.selectedLevel1.get('name').length > 0){
    this.set('level2Content',FLOW.store.findQuery(FLOW.SubCountry,{
      countryCode:this.selectedCountry.get('iso'),
      level:2,
      parentId:this.selectedLevel1.get('keyId')
      }));
    }
  }.observes('this.selectedLevel1')

});
