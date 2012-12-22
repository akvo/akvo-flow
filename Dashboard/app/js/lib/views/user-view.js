FLOW.UserListView = Em.View.extend({
  showAddUserBool: false,
  showEditUserBool: false,

  showAddUserDialog: function() {
    FLOW.editControl.set('newUserName', null);
    FLOW.editControl.set('newEmailAddress', null);
    FLOW.editControl.set('newPermissionLevel', null);
    this.set('showAddUserBool', true);
  },

  doAddUser: function() {
    var value = null;
    if(FLOW.editControl.editPermissionLevel !== null) {
      value = FLOW.editControl.newPermissionLevel.get('value');
    } else {
      value = null;
    }

    FLOW.store.createRecord(FLOW.User, {
      "userName": FLOW.editControl.get('newUserName'),
      "emailAddress": FLOW.editControl.get('newEmailAddress'),
      "permissionLevel": value
    });

    FLOW.store.commit();
    this.set('showAddUserBool', false);
  },

  cancelAddUser: function() {
    this.set('showAddUserBool', false);
  },

  showEditUserDialog: function(event) {
    var permission = null;
    FLOW.editControl.set('editUserName', event.context.get('userName'));
    FLOW.editControl.set('editEmailAddress', event.context.get('emailAddress'));
    FLOW.editControl.set('editUserId', event.context.get('keyId'));

    FLOW.permissionLevelControl.get('content').forEach(function(item) {
      if(item.get('value') == event.context.get('permissionList')) {
        permission = item;
      }
    });

    FLOW.editControl.set('editPermissionLevel', permission);
    this.set('showEditUserBool', true);
  },

  doEditUser: function() {
    var user;
    user = FLOW.store.find(FLOW.User, FLOW.editControl.get('editUserId'));
    user.set('userName', FLOW.editControl.get('editUserName'));
    user.set('emailAddress', FLOW.editControl.get('editEmailAddress'));
    
    if(FLOW.editControl.editPermissionLevel !== null) {
      user.set('permissionList', FLOW.editControl.editPermissionLevel.get('value'));
    }

    FLOW.store.commit();
    this.set('showEditUserBool', false);
  },

  cancelEditUser: function() {
    this.set('showEditUserBool', false);
  }
});

FLOW.UserView = Em.View.extend({
  tagName: 'span',
  deleteUser: function() {
    var user;
    user = FLOW.store.find(FLOW.User, this.content.get('keyId'));
    if(user !== null) {
      user.deleteRecord();
      FLOW.store.commit();
    }
  }
});