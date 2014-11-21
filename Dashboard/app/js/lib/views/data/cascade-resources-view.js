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

FLOW.CascadeResourceItemView = FLOW.View.extend({
	tagName: 'li',
	classNameBindings: 'amSelected:current'.w(),

	// true if the cascade resource is selected. Used to set proper display class
	amSelected: function () {
	    var selected = FLOW.selectedControl.get('selectedCascadeResource');
	    if (selected) {
	      var amSelected = (this.content.get('keyId') === FLOW.selectedControl.selectedCascadeResource.get('keyId'));
	      return amSelected;
	    } else {
	      return null;
	    }
	  }.property('FLOW.selectedControl.selectedCascadeResource', 'content').cacheable(),

	makeSelected: function(){
		var i=1, levelNamesArray=[];
		FLOW.selectedControl.set('selectedCascadeResource', this.content);
		FLOW.cascadeNodeControl.emptyNodes(1);
		FLOW.cascadeNodeControl.populate(1,0);
		this.content.get('levelNames').forEach(function(item){
			levelNamesArray.push(Ember.Object.create({
				levelName: item,
				level:i
			}));
			i++;
		});
		FLOW.cascadeResourceControl.set('levelNames',levelNamesArray);
		FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
	}	  
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
		index=this.content.get('level');
		currList = FLOW.selectedControl.selectedCascadeResource.get('levelNames');
		currList[index-1] = this.get('levelName');
		FLOW.selectedControl.selectedCascadeResource.set('levelNames',currList);

		// this is needed, as in this version of Ember, changes in an array do 
		// not make an object dirty, apparently
		FLOW.selectedControl.selectedCascadeResource.send('becomeDirty');
		FLOW.store.commit();

		// put the names in the array again.
		FLOW.selectedControl.selectedCascadeResource.get('levelNames').forEach(function(item){
			levelNamesArray.push(Ember.Object.create({
				levelName: item,
				level:i
			}));
			i++;
		});
		FLOW.cascadeResourceControl.set('levelNames',levelNamesArray);

		this.set('levelName',null);
		this.set('editFieldVisible',false);
	}
});

FLOW.CascadeNodeView = FLOW.View.extend({
	cascadeNodeName: null,
	
	showInputField:function(){
		if (this.get('level') == 1) {
			return !Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'));
		}
		return (!Ember.empty(FLOW.cascadeNodeControl.selectedNode[this.get('level')-1]) && 
				!Ember.empty(FLOW.cascadeNodeControl.selectedNode[this.get('level')-1].get('keyId')) );
	}.property('FLOW.cascadeNodeControl.selectedNodeTrigger').cacheable(),

	addNewNode: function() {
		var newNodeStringArray, level, nodes, exists;
		level = this.get('level');
		nodes = FLOW.cascadeNodeControl.get('level' + level);

		if (!Ember.empty(this.get('cascadeNodeName'))) {
			newNodeStringArray = this.get('cascadeNodeName').split('\n');
		    if (newNodeStringArray.length > 0) {
		    	newNodeStringArray.forEach(function (item) {
		    		if (!Ember.empty(item.trim()) && item.trim().length > 0) {
		    			// check for uniqueness
		    			exists = false;
		    			nodes.forEach(function(node){
		    				if (node.get('name').toLowerCase() == item.trim().toLowerCase()) {
		    					exists = true;
		    				}
		    			});
		    			if (!exists) {
		    				FLOW.cascadeNodeControl.addNode(level,item.trim());
		    			}
		    		}
		    	});
		    }
		    this.set('cascadeNodeName',"");
		}

		// save nodes using bulk commit
		FLOW.store.adapter.set('bulkCommit', true);
		FLOW.store.commit();
		FLOW.store.adapter.set('bulkCommit', false);

		// check if we need to increase the level of items that we use
		// TODO somehow decrease it when a level becomes empty. However, this is hard to check.
		if (level > FLOW.selectedControl.selectedCascadeResource.get('numLevels')){
			FLOW.selectedControl.selectedCascadeResource.set('numLevels',level);
			FLOW.store.commit();
		}
	},

	newNodeEnter: function() {
		if (this.get('cascadeNodeName').indexOf('\n') > -1) {
			this.addNewNode();
		}
	}.observes('this.cascadeNodeName'),
});

FLOW.CascadeNodeItemView = FLOW.View.extend({
	content: null,
	tagName: 'li',
	classNameBindings: 'amSelected:selected'.w(),

	// true if the node group is selected. Used to set proper display class
	amSelected: function () {
	    var selected = FLOW.cascadeNodeControl.get('selectedNode')[this.get('level')];
	    if (selected) {
	      var amSelected = (this.content.get('name') === FLOW.cascadeNodeControl.get('selectedNode')[this.get('level')].get('name'));
	      return amSelected;
	    } else {
	      return false;
	    }
	}.property('FLOW.cascadeNodeControl.selectedNodeTrigger').cacheable(),

	deleteNode:function() {
		FLOW.cascadeNodeControl.emptyNodes(this.get('level') + 1);
		this.get('content').deleteRecord();
		FLOW.store.commit();
	},

	makeSelected: function(){
		var i, level;
		level = this.get('level');
		FLOW.cascadeNodeControl.get('selectedNode')[level] = this.get('content');
		FLOW.cascadeNodeControl.emptyNodes(level + 1);

		if (!Ember.empty(this.content.get('keyId'))){
			FLOW.cascadeNodeControl.populate(level+1,this.content.get('keyId'));
		}
		FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
	}
});