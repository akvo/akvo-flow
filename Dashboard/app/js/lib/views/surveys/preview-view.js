FLOW.PreviewView = Ember.View.extend({
  templateName: 'navSurveys/preview-view',

  closePreviewPopup:function () {
    FLOW.previewControl.set('showPreviewPopup',false);
  }

});

FLOW.PreviewQuestionGroupView = Ember.View.extend({ 
  templateName:'navSurveys/preview-questiongroup-view',
  QGcontent:null,

  init:function () {
    var qgId;
    this._super();
    qgId = this.content.get('keyId');
    this.set('QGcontent', FLOW.store.filter(FLOW.Question, function(item) {
        return(item.get('questionGroupId') == qgId);
      }));
  }
});
