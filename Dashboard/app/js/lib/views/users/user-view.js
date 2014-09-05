FLOW.UserListView = FLOW.View.extend({
  showAddUserBool: false,
  showEditUserBool: false,
  showManageApiKeysBool: false,

  showAddUserDialog: function () {
    var userPerm;
    FLOW.editControl.set('newUserName', null);
    FLOW.editControl.set('newEmailAddress', null);

    userPerm = FLOW.permissionLevelControl.find(function (item) {
      return item.value == 20; // USER
    });
    FLOW.editControl.set('newPermissionLevel', userPerm);

    this.set('showAddUserBool', true);
  },

  doAddUser: function () {
    var value = null,
      superAdmin = false;
    if (FLOW.editControl.newPermissionLevel !== null) {
      value = FLOW.editControl.newPermissionLevel.value;
    } else {
      value = null;
    }

    if (value === 0) {
      value = 20; // Can't create a Super Admin from UI
      superAdmin = true;
    }

    FLOW.store.createRecord(FLOW.User, {
      "userName": FLOW.editControl.get('newUserName'),
      "emailAddress": Ember.$.trim(FLOW.editControl.get('newEmailAddress').toLowerCase()),
      "permissionList": value
    });

    FLOW.store.commit();
    this.set('showAddUserBool', false);

    if (superAdmin) {
      this.showRoleWarning();
    }

  },

  cancelAddUser: function () {
    this.set('showAddUserBool', false);
  },

  showEditUserDialog: function (event) {
    var permission = null;
    FLOW.editControl.set('editUserName', event.context.get('userName'));
    FLOW.editControl.set('editEmailAddress', event.context.get('emailAddress'));
    FLOW.editControl.set('editUserId', event.context.get('keyId'));

    permission = FLOW.permissionLevelControl.find(function (item) {
      return item.value == event.context.get('permissionList');
    });

    FLOW.editControl.set('editPermissionLevel', permission);
    this.set('showEditUserBool', true);
  },

  doEditUser: function () {
    var user, superAdmin = false;
    user = FLOW.store.find(FLOW.User, FLOW.editControl.get('editUserId'));
    user.set('userName', FLOW.editControl.get('editUserName'));
    user.set('emailAddress', Ember.$.trim(FLOW.editControl.get('editEmailAddress').toLowerCase()));

    if (FLOW.editControl.editPermissionLevel !== null) {
      if (FLOW.editControl.editPermissionLevel.value === 0) {
        superAdmin = true;
        user.set('permissionList', 20); // Can't change to Super Admin
      } else {
        user.set('permissionList', FLOW.editControl.editPermissionLevel.value);
      }
    }

    FLOW.store.commit();
    this.set('showEditUserBool', false);

    if (superAdmin) {
      this.showRoleWarning();
    }
  },

  cancelEditUser: function () {
    this.set('showEditUserBool', false);
  },

  showRoleWarning: function () {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_manage_users_and_user_rights'));
    FLOW.dialogControl.set('message', Ember.String.loc('_cant_set_superadmin'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  showManageApiKeysDialog: function (event) {
    FLOW.editControl.set('manageAccessKey', event.context.get('accessKey'));
    FLOW.editControl.set('showSecret', false);
    FLOW.editControl.set('manageApiUserId', event.context.get('keyId'));
    this.set('showManageApiKeysBool', true);
  },

  doGenerateNewApiKey: function (event) {

    var userId = FLOW.editControl.get('manageApiUserId');

    $.ajax({
      url: '/rest/users/' + userId + '/apikeys',
      type: 'POST',
      success: function(data) {
        var user = FLOW.store.find(FLOW.User, userId);
        var accessKey = data.apikeys.accessKey;
        var secret = data.apikeys.secret;

        user.set('accessKey', accessKey);

        FLOW.editControl.set('manageAccessKey', accessKey);
        FLOW.editControl.set('manageSecret', secret);
        FLOW.editControl.set('showSecret', true);
      },
      error: function() {
        console.error('Could not create apikeys');
      }
    });
  },

  doRevokeApiKey: function(event) {

    var userId = FLOW.editControl.get('manageApiUserId');

    $.ajax({
      url: '/rest/users/' + userId + '/apikeys',
      type: 'DELETE',
      success: function(data) {
        var user = FLOW.store.find(FLOW.User, userId);
        user.set('accessKey', null);

        FLOW.editControl.set('manageAccessKey', null);
        FLOW.editControl.set('manageSecret', null);
        FLOW.editControl.set('showSecret', false);
      },
      error: function() {
        console.error('Could not delete apikeys.')
      }
    });
  },

  cancelManageApiKeys: function() {
    this.set('showManageApiKeysBool', false);
  }
});

FLOW.UserView = FLOW.View.extend({
  tagName: 'span',
  deleteUser: function () {
    var user;
    user = FLOW.store.find(FLOW.User, this.content.get('keyId'));
    if (user !== null) {
      user.deleteRecord();
      FLOW.store.commit();
    }
  }
});

FLOW.SingleUserView = FLOW.View.extend({
  tagName: 'td',
  permissionLevel: null,
  roleLabel: null,

  init: function () {
    var role = null;
    this._super();

    role = FLOW.permissionLevelControl.find(function (item) {
      return item.value == this.content.get('permissionList');
    }, this);


    if (Ember.none(role)) {
      this.set('roleLabel', Ember.String.loc('_please_reset_the_role_for_this_user'));
      this.set('roleClass', 'notFound');
    } else {
      this.set('roleLabel', role.label);
      this.set('roleClass', Ember.String.camelize(role.label));
    }
  }
});
