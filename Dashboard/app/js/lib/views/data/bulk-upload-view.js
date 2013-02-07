FLOW.BulkUploadAppletView = FLOW.View.extend({
  showBulkUploadAppletBool: false,
  showBulkUploadApplet: function () {
    if(this.get('showBulkUploadAppletBool')) {
      // re-insert the applet
      this.get('childViews')[0].rerender();
    } else {
      this.set('showBulkUploadAppletBool', true);
    }
  }
});