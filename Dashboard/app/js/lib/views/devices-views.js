FLOW.DevicesTableHeaderView = Em.View.extend({
	templateName: 'navDevices/current-devices-table-header',
	tagName:'tr',
	//selectedBinding:'controller.selected',
	
	NavItemView: Ember.View.extend({
		tagName: 'th',
		item:null,
		
		classNameBindings: ['isActiveAsc:sorting_asc','isActiveDesc:sorting_desc'],
		
		isActiveAsc: function() {
			return (this.get('item') === FLOW.deviceControl.get('selected'))&&(FLOW.deviceControl.get('sortAscending')===true);
		}.property('item', 'FLOW.deviceControl.selected','FLOW.deviceControl.sortAscending').cacheable(),

		isActiveDesc: function() {
			return (this.get('item') === FLOW.deviceControl.get('selected'))&&(FLOW.deviceControl.get('sortAscending')===false);
		}.property('item', 'FLOW.deviceControl.selected','FLOW.deviceControl.sortAscending').cacheable(),


		sort:function(){
			if ((this.get('isActiveAsc'))||(this.get('isActiveDesc'))) {
				FLOW.deviceControl.toggleProperty('sortAscending');
			}
			else {
				FLOW.deviceControl.set('sortProperties',[this.get('item')]);
				FLOW.deviceControl.set('selected',this.get('item'));
			}
		}
	})
});


FLOW.CurrentDevicesTabView = Em.View.extend({
	showDeleteDevicesdialog: function(){
		console.log("show dialog");
	},

	doDeleteDevices: function(){

	}
});
