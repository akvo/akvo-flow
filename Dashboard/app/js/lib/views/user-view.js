FLOW.UserView = Em.View.extend({
  showAddUserBool: false,
  newEmailAddress: null,
  newUserName: null,
  newPermissionLevel: null,

  showAddUserDialog: function(){
    this.set('showAddUserBool',true);
  },

  doAddUser:function (){

  FLOW.store.createRecord(FLOW.User, {
    "userName": this.get('newUserName'),
    "emailAddress": this.get('newEmailAddress')
    // TODO make this work
    // "permissionLevel": FLOW.selectedControl.selectedForCopyQuestion.get('allowSign'),
    });

    FLOW.store.commit();
    this.set('showAddUserBool',false);


  },

  cancelAddUser:function (){
    this.set('showAddUserBool',false);
  }

});