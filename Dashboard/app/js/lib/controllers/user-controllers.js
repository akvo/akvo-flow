FLOW.UserListController = Ember.ArrayController.extend({
  sortProperties: null,
  sortAscending: true,
  content: null,
  currentUser: null,
  dataCleaningPaths: null,

  setFilteredContent() {
    this.set('content', FLOW.store.findAll(FLOW.User));
  },

  // load all Survey Groups
  populate() {
    this.setFilteredContent();
    this.set('sortProperties', ['userName']);
    this.set('sortAscending', true);
  },

  getSortInfo() {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
    this.set('selected', FLOW.tableColumnControl.get('selected'));
  },

  /* return all the ancestor paths for a given path */
  ancestorPaths(pathString) {
    if (!pathString) {
      return [];
    }

    const ancestors = [];
    while (pathString) {
      ancestors.push(pathString);
      pathString = pathString.slice(0, pathString.lastIndexOf('/'));
    }
    ancestors.push('/'); // add the root level folder to ancestors list
    return ancestors;
  },
});
