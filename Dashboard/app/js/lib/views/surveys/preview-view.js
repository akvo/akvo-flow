FLOW.PreviewView = FLOW.View.extend({
  templateName: 'navSurveys/preview-view',
  closePreviewPopup: function () {
    FLOW.previewControl.set('showPreviewPopup', false);
  }

});

FLOW.PreviewQuestionGroupView = FLOW.View.extend({
  QGcontent: null,

  init: function () {
    var qgId,QGcontent;
    this._super();
    qgId = this.content.get('keyId');
    QGcontent = FLOW.store.filter(FLOW.Question, function (item) {
      return item.get('questionGroupId') == qgId;
    });

    tmp = QGcontent.toArray();
    tmp.sort(function(a,b){
    	return a.get('order') - b.get('order');
    });
    this.set('QGcontent',tmp);
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
  isGeoshapeType:false,
  isDateType: false,
  isCascadeType: false,
  levelNameList:[],
  isVisible: true,
  optionsList: [],
  optionChoice: null,
  answer: null,
  latitude: null,
  longitude: null,

  init: function () {
    var opList, opListArray, i, sizeList, qId, tempList, cascadeNames;
    this._super();

    this.set('isTextType', this.content.get('type') == 'FREE_TEXT');
    this.set('isOptionType', this.content.get('type') == 'OPTION');
    this.set('isNumberType', this.content.get('type') == 'NUMBER');
    this.set('isPhotoType', this.content.get('type') == 'PHOTO');
    this.set('isVideoType', this.content.get('type') == 'VIDEO');
    this.set('isBarcodeType', this.content.get('type') == 'BARCODE');
    this.set('isGeoType', this.content.get('type') == 'GEO');
    this.set('isGeoshapeType', this.content.get('type') == 'GEOSHAPE');
    this.set('isDateType', this.content.get('type') == 'DATE');
    this.set('isCascadeType', this.content.get('type') == 'CASCADE');

    // fill option list
    if (this.isOptionType) {
      qId = this.content.get('keyId');
      options = FLOW.store.filter(FLOW.QuestionOption, function (item) {
        return item.get('questionId') == qId;
      });

      optionArray = options.toArray();
      optionArray.sort(function (a, b) {
    	  return a.get('order') - b.get('order');
      });

      tempList = [];
      optionArray.forEach(function (item) {
        tempList.push(Ember.Object.create({
          isSelected: false,
          value: item.get('text')
        }));
      });

      if (this.content.get('allowOtherFlag')) {
        tempList.push(Ember.Object.create({
          isSelected: false,
          value: Ember.String.loc('_other')
        }));
      }
      this.set('optionsList', tempList);
    }
    if (this.isCascadeType) {
    	cascade = FLOW.store.find(FLOW.CascadeResource,this.content.get('cascadeResourceId'));
    	if (!Ember.empty(cascade)){
    		cascadeNames = cascade.get('levelNames');
    		for (i=0 ; i < cascade.get('numLevels'); i++){
    			this.levelNameList.push(cascadeNames[i]);
    		}
    	}
    }
  },

  checkVisibility: function () {
    var dependentAnswerArray;
    if (this.content.get('dependentFlag') && this.content.get('dependentQuestionId') !== null) {
      dependentAnswerArray = this.content.get('dependentQuestionAnswer').split('|');
      if (dependentAnswerArray.indexOf(FLOW.previewControl.answers[this.content.get('dependentQuestionId')]) > -1) {
        this.set('isVisible', true);
      } else {
        this.set('isVisible', false);
      }
    }
  }.observes('FLOW.previewControl.changed'),

  storeOptionChoice: function () {
    var keyId;
    keyId = this.content.get('keyId');
    FLOW.previewControl.answers[keyId] = this.get('optionChoice');
    FLOW.previewControl.toggleProperty('changed');
  }.observes('this.optionChoice'),

  storeAnswer: function () {
    var keyId;
    keyId = this.content.get('keyId');
    FLOW.previewControl.answers[keyId] = this.get('answer');
  }.observes('this.answer')
});
