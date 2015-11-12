FLOW.userControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,
  currentUser: null,
  dataCleaningPaths: null,

  setFilteredContent: function () {
    this.set('content', FLOW.store.findAll(FLOW.User));
  },

  // load all Survey Groups
  populate: function () {
    this.setFilteredContent();
    this.set('sortProperties', ['userName']);
    this.set('sortAscending', true);
  },

  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
    this.set('selected', FLOW.tableColumnControl.get('selected'));
  },

  currentUserPathPermissions: function() {
    return FLOW.currentUser.get('pathPermissions');
  },

  /* return all the ancestor paths for a given path */
  ancestorPaths: function(pathString) {
    if(!pathString) {
        return [];
    }

    var ancestors = [];
    while(pathString) {
        ancestors.push(pathString);
        pathString = pathString.slice(0, pathString.lastIndexOf("/"));
    }
    ancestors.push("/"); // add the root level folder to ancestors list
    return ancestors;
  },

  /* query based on survey (group) ancestorIds whether a user has
  permissions for data deletion */

  canDeleteData: function(ancestorIds) {
    var pathPermissions = this.currentUserPathPermissions();
    var canDelete = false;
    ancestorIds.forEach(function(id){
        if(id in pathPermissions && pathPermissions[id].indexOf("DATA_DELETE") > -1) {
            canDelete = true;
        }
    });
    return canDelete;
  },
});
