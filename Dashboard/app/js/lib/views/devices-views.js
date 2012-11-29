FLOW.CurrentDevicesTabView = Em.View.extend({
	showDeleteDevicesDialogBool: false,
	showAddToGroupDialogBool: false,
	newDeviceGroupName:null, // bound to devices-list.handlebars
	selectedDeviceGroup:null, // bound to devices-list.handlebars

	showAddToGroupDialog: function(){
		this.set('showAddToGroupDialogBool',true);
	},

	doAddToGroup: function(){
		this.set('showAddToGroupDialogBool',false);
		console.log(this.get('newDeviceGroupName'));
		console.log(this.get('selectedDeviceGroup'));
		//if ()
	//	new name has preference
	},

	cancelAddToGroup: function(){
		this.set('showAddToGroupDialogBool',false);
	},


	showDeleteDevicesDialog: function(){
		console.log("show delete devices dialog");
	},

	doDeleteDevices: function(){

	},

	cancelDeleteDevices: function(){

	}
});
