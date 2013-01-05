FLOW.PreviewView = Ember.View.extend({
  templateName: 'navSurveys/preview-view',

  closePreviewPopup: function() {
    FLOW.previewControl.set('showPreviewPopup', false);
  }

});

FLOW.PreviewQuestionGroupView = Ember.View.extend({
  QGcontent: null,

  init: function() {
    var qgId;
    this._super();
    qgId = this.content.get('keyId');
    this.set('QGcontent', FLOW.store.filter(FLOW.Question, function(item) {
      return(item.get('questionGroupId') == qgId);
    }));
  }
});

FLOW.PreviewQuestionView = Ember.View.extend({
  isTextType: false,
  isOptionType: false,
  isNumberType: false,
  isPhotoType: false,
  isVideoType: false,
  isBarcodeType: false,
  isGeoType: false,
  isDateType:false,
  optionsList:[],

  init: function() {
    var opList, opListArray, i, sizeList;
    this._super();

    this.set('isTextType',this.content.get('type') == 'FREE_TEXT');
    this.set('isOptionType',this.content.get('type') == 'OPTION');
    this.set('isNumberType',this.content.get('type') == 'NUMBER');
    this.set('isPhotoType',this.content.get('type') == 'PHOTO');
    this.set('isVideoType',this.content.get('type') == 'VIDEO');
    this.set('isBarcodeType',this.content.get('type') == 'BARCODE');
    this.set('isGeoType',this.content.get('type') == 'GEO');
    this.set('isDateType',this.content.get('type') == 'DATE');

    // fill option list
    if(this.isOptionType && this.content.get('optionList') !== null) {
      this.set('optionsList',[]);
      opList = this.content.get('optionList');
      opListArray = opList.split('\n');
      sizeList = opListArray.length;

      for(i = 0; i < sizeList; i++) {
        this.get('optionsList').push(Ember.Object.create({
          isSelected: false,
          value: opListArray[i]
        }));
      }
    }
  }
});