FLOW.PreviewView = FLOW.View.extend({
  templateName: 'navSurveys/preview-view',

  closePreviewPopup: function() {
    FLOW.previewControl.set('showPreviewPopup', false);
  }

});

FLOW.PreviewQuestionGroupView = FLOW.View.extend({
  QGcontent: null,

  init: function() {
    var qgId;
    this._super();
    qgId = this.content.get('keyId');
    this.set('QGcontent', FLOW.store.filter(FLOW.Question, function(item) {
      return item.get('questionGroupId') == qgId;
    }));
  }
});

FLOW.PreviewQuestionView = FLOW.View.extend({
  isTextType: false,
  isOptionType: false,
  isNumberType: false,
  isPhotoType: false,
  isVideoType: false,
  isBarcodeType: false,
  isGeoType: false,
  isDateType:false,
  isVisible:true,
  optionsList:[],
  optionChoice:null,
  answer:null,
  latitude:null,
  longitude:null,

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
  },

  checkVisibility: function () {
    var dependentAnswerArray;
    if(this.content.get('dependentFlag') && this.content.get('dependentQuestionId') !== null){
      dependentAnswerArray = this.content.get('dependentQuestionAnswer').split('|');
      if(dependentAnswerArray.indexOf(FLOW.previewControl.answers[this.content.get('dependentQuestionId')]) > -1){
        this.set('isVisible',true);
      } else {
        this.set('isVisible',false);
      }
    }
  }.observes('FLOW.previewControl.changed'),

  storeOptionChoice:function () {
    var keyId;
    keyId = this.content.get('keyId');
    FLOW.previewControl.answers[keyId] = this.get('optionChoice');
    FLOW.previewControl.toggleProperty('changed');
  }.observes('this.optionChoice'),

  storeAnswer:function () {
    var keyId;
    keyId = this.content.get('keyId');
    FLOW.previewControl.answers[keyId] = this.get('answer');
  }.observes('this.answer')
});