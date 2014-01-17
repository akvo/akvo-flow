FLOW.MonitoringDataTableView = FLOW.View.extend({
	showingDetailsDialog: false,
	showDetailsDialog: function (evt) {
      this.toggleProperty('showingDetailsDialog');
	},
	closeDetailsDialog: function () {
      this.toggleProperty('showingDetailsDialog');
	}
});