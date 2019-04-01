FLOW.UserListView = FLOW.View.extend({
  showAddUserBool: false,
  showEditUserBool: false,
  showManageApiKeysBool: false,

  showAddUserDialog() {
    FLOW.editControl.set('newUserName', null);
    FLOW.editControl.set('newEmailAddress', null);

    const userPerm = FLOW.permissionLevelControl.find(item => item.value == 20); // USER

    FLOW.editControl.set('newPermissionLevel', userPerm);

    this.set('showAddUserBool', true);
  },

  doAddUser() {
    let value = null;
    let superAdmin = false;
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
      userName: FLOW.editControl.get('newUserName'),
      emailAddress: Ember.$.trim(FLOW.editControl.get('newEmailAddress').toLowerCase()),
      permissionList: value,
    });

    FLOW.store.commit();
    this.set('showAddUserBool', false);

    if (superAdmin) {
      this.showRoleWarning();
    }
  },

  cancelAddUser() {
    this.set('showAddUserBool', false);
  },

  showEditUserDialog(event) {
    let permission = null;
    FLOW.editControl.set('editUserName', event.context.get('userName'));
    FLOW.editControl.set('editEmailAddress', event.context.get('emailAddress'));
    FLOW.editControl.set('editUserId', event.context.get('keyId'));

    permission = FLOW.permissionLevelControl.find(item => item.value == event.context.get('permissionList'));

    FLOW.editControl.set('editPermissionLevel', permission);
    this.set('showEditUserBool', true);
  },

  doEditUser() {
    let superAdmin = false;
    const user = FLOW.store.find(FLOW.User, FLOW.editControl.get('editUserId'));
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

  cancelEditUser() {
    this.set('showEditUserBool', false);
  },

  showRoleWarning() {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_manage_users_and_user_rights'));
    FLOW.dialogControl.set('message', Ember.String.loc('_cant_set_superadmin'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  showManageApiKeysDialog(event) {
    FLOW.editControl.set('manageAccessKey', event.context.get('accessKey'));
    FLOW.editControl.set('showSecret', false);
    FLOW.editControl.set('manageApiUserId', event.context.get('keyId'));
    this.set('showManageApiKeysBool', true);
  },

  doGenerateNewApiKey() {
    const userId = FLOW.editControl.get('manageApiUserId');

    $.ajax({
      url: `/rest/users/${userId}/apikeys`,
      type: 'POST',
      success(data) {
        const user = FLOW.store.find(FLOW.User, userId);
        const { accessKey } = data.apikeys;
        const { secret } = data.apikeys;

        user.set('accessKey', accessKey);

        FLOW.editControl.set('manageAccessKey', accessKey);
        FLOW.editControl.set('manageSecret', secret);
        FLOW.editControl.set('showSecret', true);
      },
      error() {
        console.error('Could not create apikeys');
      },
    });
  },

  doRevokeApiKey() {
    const userId = FLOW.editControl.get('manageApiUserId');

    $.ajax({
      url: `/rest/users/${userId}/apikeys`,
      type: 'DELETE',
      success() {
        const user = FLOW.store.find(FLOW.User, userId);
        user.set('accessKey', null);

        FLOW.editControl.set('manageAccessKey', null);
        FLOW.editControl.set('manageSecret', null);
        FLOW.editControl.set('showSecret', false);
      },
      error() {
        console.error('Could not delete apikeys.');
      },
    });
  },

  cancelManageApiKeys() {
    this.set('showManageApiKeysBool', false);
  },
});

FLOW.UserView = FLOW.View.extend({
  tagName: 'span',
  deleteUser() {
    const user = FLOW.store.find(FLOW.User, this.content.get('keyId'));
    if (user !== null) {
      user.deleteRecord();
      FLOW.store.commit();
    }
  },
});

FLOW.SingleUserView = FLOW.View.extend({
  tagName: 'td',
  permissionLevel: null,
  roleLabel: null,

  init() {
    let role = null;
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
  },
});
