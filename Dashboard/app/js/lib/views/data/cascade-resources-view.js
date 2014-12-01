function capitaliseFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

if (!String.prototype.trim) {
		String.prototype.trim=function(){return this.replace(/^\s+|\s+$/g, '');};
}

FLOW.CascadeResourceView = FLOW.View.extend({
	showNewCascadeField: false,
	cascadeResourceName:null,

	// fired when 'add a cascade resource' is clicked. Displays a text field
	newCascade: function () {
		this.set('cascadeResourceName',null);  
	    this.set('showNewCascadeField', true);
	},

	saveNodes: function(){
		FLOW.store.commit();
	},

	publishResource: function(){
		if (!Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'))){
			FLOW.cascadeResourceControl.publish(FLOW.selectedControl.selectedCascadeResource.get('keyId'));
		}
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
			FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
		}
	}.observes('FLOW.selectedControl.selectedCascadeResource'),

	moveRight: function(){
		var skip = FLOW.cascadeNodeControl.get('skip');
		if (skip > 0) {
			FLOW.cascadeNodeControl.set('skip', skip - 1);
			FLOW.cascadeNodeControl.setDisplayLevels();
		}
	},

	moveLeft: function(){
		var skip = FLOW.cascadeNodeControl.get('skip');
			FLOW.cascadeNodeControl.set('skip', skip + 1);
			FLOW.cascadeNodeControl.setDisplayLevels();
	},

	// fired when 'save' is clicked while showing new cascade text field. Saves new cascade resource to the data store
	saveNewCascadeResource: function () {
		  if (!Ember.empty(this.get('cascadeResourceName').trim())){
			  FLOW.store.createRecord(FLOW.CascadeResource, {
				  "version": 0,
				  "levelNames":["Level 1","Level 2","Level 3","Level 4","Level 5","Level 6","Level 7","Level 8","Level 9","Level 10"],
				  "numLevels": 1,
				  "name": capitaliseFirstLetter(this.get('cascadeResourceName'))
			  });
			  FLOW.store.commit();
		  }
	    this.set('showNewCascadeField', false);
	},

	  // fired when 'cancel' is clicked while showing new group text field in left sidebar. Cancels the new survey group creation
	cancelNewCascadeResource: function () {
	  this.set('cascadeResourceName', null);
	  this.set('showNewCascadeField', false);
	},
});

FLOW.CascadeLevelNameView = FLOW.View.extend({
	tagName: 'th',
	editFieldVisible:false,
	content: null,
	levelName:null,

	showEditField: function(){
		this.set('levelName',this.get('content').get('levelName'));
		this.set('editFieldVisible',true);
	},

	cancelNewLevelName: function(){
		this.set('levelName',null);
		this.set('editFieldVisible',false);
	},

	saveNewLevelName: function(){
		var currList, index, i=1, levelNamesArray=[];
		index = this.content.get('col') + FLOW.cascadeNodeControl.get('skip');
		currList = FLOW.selectedControl.selectedCascadeResource.get('levelNames');
		currList[index-1] = capitaliseFirstLetter(this.get('levelName'));
		FLOW.selectedControl.selectedCascadeResource.set('levelNames',currList);

		// this is needed, as in this version of Ember, changes in an array do 
		// not make an object dirty, apparently
		FLOW.selectedControl.selectedCascadeResource.send('becomeDirty');
		FLOW.store.commit();

		// put the names in the array again.
		FLOW.cascadeResourceControl.setLevelNamesArray();

		this.set('levelName',null);
		this.set('editFieldVisible',false);
	}
});

FLOW.CascadeNodeView = FLOW.View.extend({
	cascadeNodeName: null,
	cascadeNodeCode:null,
	
	showInputField:function(){
		var skip;
		// determines if we should show an input field in this column
		// we do this in column one by default, or if in the previous column a node has been selected
		if (this.get('col') == 1 && FLOW.cascadeNodeControl.get('skip') == 0) {
			return true;
		}
		skip = FLOW.cascadeNodeControl.get('skip');
		return (!Ember.empty(FLOW.cascadeNodeControl.selectedNode[skip + this.get('col') - 1]) && 
				!Ember.empty(FLOW.cascadeNodeControl.selectedNode[skip + this.get('col') - 1].get('keyId')));
	}.property('FLOW.cascadeNodeControl.selectedNodeTrigger').cacheable(),

	addNewNode: function() {
		var newNodeStringArray, level, nodes, exists, item, itemTrim;
		level = this.get('col') + FLOW.cascadeNodeControl.get('skip');
		nodes = FLOW.cascadeNodeControl.get('level' + level);
		item = this.get('cascadeNodeName');
		if (item!= null && item.trim().length > 0) {
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
			FLOW.selectedControl.selectedCascadeResource.set('numLevels',level);
			FLOW.store.commit();
			FLOW.cascadeResourceControl.setLevelNamesArray();
		}
	},
});

FLOW.CascadeNodeItemView = FLOW.View.extend({
	content: null,
	tagName: 'li',
	classNameBindings: 'amSelected:selected'.w(),

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