function capitaliseFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

if (!String.prototype.trim) {
		String.prototype.trim=function(){return this.replace(/^\s+|\s+$/g, '');};
}



FLOW.CascadeResourceView = FLOW.View.extend({
	showNewCascadeField: false,
	cascadeResourceName:null,
	showImportDialog:false,

    didInsertElement : function() {
      FLOW.uploader.registerEvents();
    },

    showMessage : function(header, msg) {
		FLOW.dialogControl.set('activeAction', 'ignore');
		FLOW.dialogControl.set('header', header);
		FLOW.dialogControl.set('message', msg);
		FLOW.dialogControl.set('showCANCEL', false);
		FLOW.dialogControl.set('showDialog', true);
	},

    importFile : function() {
		var file = $('#cascade-resource-file')[0],
		    numLevels = FLOW.selectedControl.get('cascadeImportNumLevels');

		if (!numLevels || +numLevels === 0) {
			this.showMessage(Ember.String.loc('_import_cascade_file'), Ember.String.loc('_import_cascade_number_levels'));
			return;
		}

		if (!file || file.files.length === 0) {
			this.showMessage(Ember.String.loc('_import_cascade_file'), Ember.String.loc('_import_select_cascade_file'));
			return;
		}
		FLOW.uploader.addFile(file.files[0]);
		FLOW.uploader.upload();
	},

	// fired when 'add a cascade resource' is clicked. Displays a text field
	newCascade: function () {
		FLOW.selectedControl.set('cascadeImportNumLevels', null);
		FLOW.selectedControl.set('cascadeImportIncludeCodes', null);
		this.set('importIncludeCodes', null);
	    this.set('showNewCascadeField', true);
	},

	saveNodes: function(){
		FLOW.store.commit();
	},

	showImportCascade: function(){
		this.toggleProperty('showImportDialog');
	},

	hideImportCascade: function(){
		FLOW.selectedControl.set('cascadeImportNumLevels', null);
		FLOW.selectedControl.set('cascadeImportIncludeCodes', null);
		this.toggleProperty('showImportDialog');
	},

	publishResource: function(){
		if (!Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'))){
			FLOW.cascadeResourceControl.publish(FLOW.selectedControl.selectedCascadeResource.get('keyId'));
			this.showMessage(Ember.String.loc('_cascade_resources'), Ember.String.loc('_cascade_resource_published_text'));
		}
	},

	deleteResource: function (e) {
		var resource = FLOW.selectedControl.selectedCascadeResource,
		    keyId = resource.get('keyId'),
            questions = FLOW.store.filter(FLOW.Question, function (item) {
              return item.get('cascadeResourceId') === keyId;
            });

		if (questions.get('length') > 0) {
			this.showMessage(Ember.String.loc('_cascade_resources'), Ember.String.loc('_cannot_delete_cascade'));
			return;
		}
		resource.deleteRecord();
		FLOW.store.commit();
		FLOW.selectedControl.set('selectedCascadeResource', null);
	},

	// adds a level to the hierarchy
	addLevel: function(){
		var selectedCascade = FLOW.selectedControl.selectedCascadeResource,
		    numLevels = (selectedCascade && selectedCascade.get('numLevels') + 1) || 0,
		    nameLevels = (selectedCascade && selectedCascade.get('levelNames')) || [];

		if (!selectedCascade) {
			return;
		}

		nameLevels.push('Level ' + numLevels);

		selectedCascade.set('numLevels', numLevels);
		selectedCascade.set('levelNames', nameLevels);
		FLOW.store.commit();
		FLOW.cascadeResourceControl.setLevelNamesArray();
		FLOW.cascadeResourceControl.setDisplayLevelNames();
	},

	oneSelected: function(){
		return !Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'));
	}.property('FLOW.selectedControl.selectedCascadeResource').cacheable(),

	resourceSelected: function(){
		if (!Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'))){
			var i=1, levelNamesArray=[];
			FLOW.cascadeNodeControl.emptyNodes(1);
			FLOW.cascadeNodeControl.populate(FLOW.selectedControl.selectedCascadeResource.get('keyId'),1,0);
			FLOW.cascadeResourceControl.setLevelNamesArray();
			FLOW.cascadeNodeControl.set('skip',0);
			FLOW.cascadeNodeControl.setDisplayLevels();
			FLOW.cascadeResourceControl.setDisplayLevelNames();
			FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
		}
	}.observes('FLOW.selectedControl.selectedCascadeResource'),

	// fired when 'save' is clicked while showing new cascade text field. Saves new cascade resource to the data store
	saveNewCascadeResource: function () {
		var casc;
		  if (!Ember.empty(this.get('cascadeResourceName').trim())){
			  casc = FLOW.store.createRecord(FLOW.CascadeResource, {
				  "version": 0,
				  "levelNames":["Level 1"],
				  "numLevels": 1,
				  "name": capitaliseFirstLetter(this.get('cascadeResourceName'))
			  });
			  FLOW.store.commit();
			  FLOW.selectedControl.set('selectedCascadeResource',casc);
		  }
	    this.set('showNewCascadeField', false);
	    this.set('cascadeResourceName',null);
	},

	  // fired when 'cancel' is clicked while showing new group text field in left sidebar. Cancels the new survey group creation
	cancelNewCascadeResource: function () {
	  this.set('cascadeResourceName', null);
	  this.set('showNewCascadeField', false);
	},

	hideColumn2: function () {
		var cascade = FLOW.selectedControl.selectedCascadeResource;
		if (!cascade) {
			return false;
		}
		console.log('hideColumn2', cascade.get('numLevels') < 2);
		return cascade.get('numLevels') < 2;
	}.property('FLOW.selectedControl.selectedCascadeResource', 'FLOW.selectedControl.selectedCascadeResource.numLevels'),

	hideColumn3: function() {
		var cascade = FLOW.selectedControl.selectedCascadeResource;
		if (!cascade) {
			return false;
		}
		console.log('hideColumn3', cascade.get('numLevels') < 3);
		return cascade.get('numLevels') < 3;
	}.property('FLOW.selectedControl.selectedCascadeResource', 'FLOW.selectedControl.selectedCascadeResource.numLevels')
});

FLOW.CascadeSecondNavView = FLOW.View.extend({
	tagName: 'li',
	content: null,
	classNameBindings: 'display:disable'.w(),

	display: function(){
		if (this.get('dir') == "up"){
			return !this.get('showGoUpLevel');
		} else return !this.get('showGoDownLevel');
	}.property('this.showGoUpLevel','this.showGoUpLevel'),

	showGoUpLevel: function(){
		return FLOW.selectedControl.selectedCascadeResource && (FLOW.cascadeNodeControl.get('skip') + 3 < FLOW.selectedControl.selectedCascadeResource.get('numLevels'));
	}.property('FLOW.cascadeNodeControl.skip', 'FLOW.selectedControl.selectedCascadeResource'),

	showGoDownLevel: function(){
		return FLOW.cascadeNodeControl.get('skip') > 0;
	}.property('FLOW.cascadeNodeControl.skip','FLOW.selectedControl.selectedCascadeResource'),

	goUpLevel: function(){
		FLOW.cascadeNodeControl.set('skip', FLOW.cascadeNodeControl.get('skip') + 1);
		FLOW.cascadeNodeControl.setDisplayLevels();
		FLOW.cascadeResourceControl.setDisplayLevelNames();
		FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
	},

	goDownLevel: function(){
		FLOW.cascadeNodeControl.set('skip', FLOW.cascadeNodeControl.get('skip') - 1);
		FLOW.cascadeNodeControl.setDisplayLevels();
		FLOW.cascadeResourceControl.setDisplayLevelNames();
		FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
	},
});

FLOW.CascadeLevelBreadcrumbView = FLOW.View.extend({
	tagName: 'li',
	content: null,
	classNameBindings: 'offscreen:offScreen'.w(),

	offscreen: function() {
		var skip = FLOW.cascadeNodeControl.get('skip');
		var level = this.content.get('level');
		return ((level < skip + 1) || (level > skip + 3));
	}.property('FLOW.cascadeNodeControl.skip', 'FLOW.selectedControl.selectedCascadeResource'),

	adaptColView: function(){
		var skip = FLOW.cascadeNodeControl.get('skip');
		var level = this.content.get('level');
		// if the level is already visible, do nothing
		if (level > skip && level < skip + 4) {
			return;
		}

		// clicked level lies on the left
		if (level < skip + 1) {
			FLOW.cascadeNodeControl.set('skip', level - 1);
			FLOW.cascadeNodeControl.setDisplayLevels();
			FLOW.cascadeResourceControl.setDisplayLevelNames();
			FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
		}

		// clicked level lies on the right
		if (level > skip + 3) {
			FLOW.cascadeNodeControl.set('skip', level - 3);
			FLOW.cascadeNodeControl.setDisplayLevels();
			FLOW.cascadeResourceControl.setDisplayLevelNames();
			FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
		}
	},
});

FLOW.CascadeLevelNameView = FLOW.View.extend({
	editFieldVisible:false,
	content: null,
	levelName:null,

	showEditField: function(){
		this.set('levelName',this.get('origLevelName'));
		this.set('editFieldVisible',true);
	},

	cancelNewLevelName: function(){
		this.set('levelName',null);
		this.set('editFieldVisible',false);
	},

	saveNewLevelName: function(){
		var currList, index, i=1, levelNamesArray=[];
		index = this.get('col') + FLOW.cascadeNodeControl.get('skip');
		currList = FLOW.selectedControl.selectedCascadeResource.get('levelNames');
		currList[index-1] = capitaliseFirstLetter(this.get('levelName'));
		FLOW.selectedControl.selectedCascadeResource.set('levelNames',currList);

		// this is needed, as in this version of Ember, changes in an array do
		// not make an object dirty, apparently
		FLOW.selectedControl.selectedCascadeResource.send('becomeDirty');
		FLOW.store.commit();

		// put the names in the array again.
		FLOW.cascadeResourceControl.setLevelNamesArray();
		FLOW.cascadeResourceControl.setDisplayLevelNames();

		this.set('levelName',null);
		this.set('editFieldVisible',false);
	}
});

FLOW.CascadeNodeView = FLOW.View.extend({
	cascadeNodeName: null,
	cascadeNodeCode:null,

	showInputField:function(){
		var skip = FLOW.cascadeNodeControl.get('skip');

		// determines if we should show an input field in this column
		// we do this in column one by default, or if in the previous column a node has been selected
		if (this.get('col') === 1 && skip === 0) {
			return true;
		}
		return (!Ember.empty(FLOW.cascadeNodeControl.selectedNode[skip + this.get('col') - 1]) &&
				!Ember.empty(FLOW.cascadeNodeControl.selectedNode[skip + this.get('col') - 1].get('keyId')));
	}.property('FLOW.cascadeNodeControl.selectedNodeTrigger').cacheable(),

	addNewNode: function() {
		var newNodeStringArray, level, nodes, exists, item, itemTrim, levelNames;
		level = this.get('col') + FLOW.cascadeNodeControl.get('skip');
		nodes = FLOW.cascadeNodeControl.get('level' + level);
		item = this.get('cascadeNodeName');
		if (item !== null && item.trim().length > 0) {
			exists = false;
			itemTrim = item.trim().toLowerCase();
			nodes.forEach(function(node){
				if (node.get('name').toLowerCase() == itemTrim) {
					exists = true;
				}
			});
			if (!exists) {
				FLOW.cascadeNodeControl.addNode(FLOW.selectedControl.selectedCascadeResource.get('keyId'),
						level, item.trim(), this.get('cascadeNodeCode'));
			}
		}
		this.set('cascadeNodeName',"");
		this.set('cascadeNodeCode',"");

		// check if we need to increase the level of items that we use
		// TODO somehow decrease it when a level becomes empty. However, this is hard to check.
		if (level > FLOW.selectedControl.selectedCascadeResource.get('numLevels')){
			levelNames = FLOW.selectedControl.selectedCascadeResource.get('levelNames');
			levelNames.push('Level ' + level);
			FLOW.selectedControl.selectedCascadeResource.set('numLevels', level);
			FLOW.selectedControl.selectedCascadeResource.set('levelNames', levelNames);
			FLOW.store.commit();
			FLOW.cascadeResourceControl.setLevelNamesArray();
		}
	},
});

FLOW.CascadeNodeItemView = FLOW.View.extend({
	content: null,
	tagName: 'li',
	classNameBindings: 'amSelected:selected'.w(),
	showEditNodeFlag: false,
	newCode:null,
	newName:null,

	// true if the node group is selected. Used to set proper display class
	amSelected: function () {
	    var selected = FLOW.cascadeNodeControl.get('selectedNode')[this.get('col') + FLOW.cascadeNodeControl.get('skip')];
	    if (selected) {
	      var amSelected = (this.content.get('name') === FLOW.cascadeNodeControl.get('selectedNode')[this.get('col') + FLOW.cascadeNodeControl.get('skip')].get('name'));
	      return amSelected;
	    } else {
	      return false;
	    }
	}.property('FLOW.cascadeNodeControl.selectedNodeTrigger').cacheable(),

	deleteNode:function() {
		FLOW.cascadeNodeControl.emptyNodes(this.get('col') + FLOW.cascadeNodeControl.get('skip') + 1);
		this.get('content').deleteRecord();
		FLOW.store.commit();
	},

	showEditNodeField: function(){
		this.set('showEditNode', true);
		this.set('newCode',this.content.get('code'));
		this.set('newName',this.content.get('name'));
	},

	cancelEditNode: function(){
		this.set('showEditNode', false);
		this.set('newCode',null);
		this.set('newName',null);
	},

	saveEditNode: function(){
		if (Ember.empty(this.get('newName')) ||this.get('newName').trim().length === 0) {
			this.cancelEditNode();
			return;
		}
		this.content.set('name',capitaliseFirstLetter(this.get('newName')));
		this.content.set('code',this.get('newCode'));
		FLOW.store.commit();
		this.cancelEditNode();
	},

	makeSelected: function(){
		var i, level;
		level = this.get('col') + FLOW.cascadeNodeControl.get('skip');
		FLOW.cascadeNodeControl.get('selectedNode')[level] = this.get('content');
		FLOW.cascadeNodeControl.emptyNodes(level + 1);

		if (!Ember.empty(this.content.get('keyId'))){
			FLOW.cascadeNodeControl.populate(FLOW.selectedControl.selectedCascadeResource.get('keyId'),
					level + 1, this.content.get('keyId'));
		}
		FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
	}
});