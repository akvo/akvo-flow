FLOW.CurrentDevicesTabView = Em.View.extend({
	showDeleteDevicesDialogBool: false,
	showAddToGroupDialogBool: false,
	showManageDeviceGroupsDialogBool: false,
	newDeviceGroupName:null, // bound to devices-list.handlebars
	changedDeviceGroupName:null,
	selectedDeviceGroup:null, // bound to devices-list.handlebars

	showAddToGroupDialog: function(){
		this.set('selectedDeviceGroup',null);
		this.set('showAddToGroupDialogBool',true);
	},

	cancelAddToGroup: function(){
		this.set('showAddToGroupDialogBool',false);
	},

	showManageDeviceGroupsDialog: function(){
		this.set('newDeviceGroupName',null);
		this.set('changedDeviceGroupName',null);
		this.set('selectedDeviceGroup',null);
		this.set('showManageDeviceGroupsDialogBool',true);
	},
	
	cancelManageDeviceGroups: function(){
		this.set('showManageDeviceGroupsDialogBool',false);
	},
	
	assignDisplayNames:function(){
    	if (FLOW.DeviceControl.content.get('isLoaded') === true) {
    		//TODO
    	}
    }.observes('FLOW.DeviceControl.content.isLoaded'),

	doAddToGroup: function(){
		if (this.get('selectedDeviceGroup') !== null) {
			var selectedDeviceGroupId=this.selectedDeviceGroup.get('keyId');
			var selectedDeviceGroupName=this.selectedDeviceGroup.get('code');
			var selectedDevices = FLOW.store.filter(FLOW.Device,function(data){
				if (data.get('isSelected') === true) {return true;} else {return false;}
			});
			selectedDevices.forEach(function(item){
				item.set('deviceGroupName',selectedDeviceGroupName);
				item.set('deviceGroup',selectedDeviceGroupId);
			});
		}
		FLOW.store.commit();
		this.set('showAddToGroupDialogBool',false);
	},

	copyDeviceGroupName:function(){
		if (this.get('selectedDeviceGroup') !== null){
			this.set('changedDeviceGroupName',this.selectedDeviceGroup.get('code'));
		}
	}.observes('this.selectedDeviceGroup'),

	// TODO update device group name in tabel.
	doManageDeviceGroups: function(){
		if (this.get('selectedDeviceGroup') !== null) {
			var selectedDeviceGroupId = this.selectedDeviceGroup.get('keyId');

			// this could have been changed in the UI
			var selectedDeviceGroupName = this.selectedDeviceGroup.get('code');
			var originalSelectedDeviceGroup = FLOW.store.find(FLOW.DeviceGroup,selectedDeviceGroupId);

			if (originalSelectedDeviceGroup.get('code') != this.get('changedDeviceGroupName')) {
				var newName=this.get('changedDeviceGroupName');
				originalSelectedDeviceGroup.set('code',newName);

				allDevices = FLOW.store.filter(FLOW.Device,function(data){return true;});
				allDevices.forEach(function(item){
					console.log('hier ook?');
					//console.log(item.get('deviceGroup'),selectedDeviceGroupId);
					if (parseInt(item.get('deviceGroup')) == selectedDeviceGroupId){
					  item.set('deviceGroupName',newName);
					}
			});

			}

			if (this.get('newDeviceGroupName') !== null) {
				var newDeviceGroup = FLOW.store.createRecord(FLOW.DeviceGroup,{'code':this.get('newDeviceGroupName')});
			}
		}
		this.set('selectedDeviceGroup',null);
		this.set('newDeviceGroupName',null);
		this.set('changedDeviceGroupName',null);

		FLOW.store.commit();
		this.set('showManageDeviceGroupsDialogBool',false);
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
			this.set('showDGSavingDialogBool', false);
		} else {
			this.set('showDGSavingDialogBool', true);
		}
	}.observes('FLOW.deviceGroupControl.allRecordsSaved')
});