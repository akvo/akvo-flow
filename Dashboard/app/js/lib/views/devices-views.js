FLOW.CurrentDevicesTabView = Em.View.extend({
	showDeleteDevicesDialogBool: false,
	showAddToGroupDialogBool: false,
	newDeviceGroupName:null, // bound to devices-list.handlebars
	selectedDeviceGroup:null, // bound to devices-list.handlebars

	showAddToGroupDialog: function(){
		this.set('showAddToGroupDialogBool',true);
	},

	doAddToGroup: function(){
	  if (this.get('selectedDeviceGroup') !== null) {
	  		console.log(this.get('selectedDeviceGroup'));
			var selectedDeviceGroupId=this.selectedDeviceGroup.get('keyId');
			var selectedDevices = FLOW.store.filter(FLOW.Device,function(data){
				if (data.get('isSelected') === true) {return true;} else {return false;}
			});
			selectedDevices.forEach(function(item){
				console.log('setting device group',selectedDeviceGroupId, ' on ', item.get('phoneNumber'));
				item.set('deviceGroup',selectedDeviceGroupId);
			});
		}
		FLOW.store.commit();
		this.set('showAddToGroupDialogBool',false);
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


FLOW.SavingDeviceGroupView = Ember.View.extend({
	showDGSavingDialogBool:false,

	showDGSavingDialog:function(){
		if (FLOW.DeviceGroupControl.get('allRecordsSaved')){
			this.set('showDGSavingDialogBool', false)
		} else {
			this.set('showDGSavingDialogBool', true)
		}
	}.observes('FLOW.deviceGroupControl.allRecordsSaved')
});