import observe from '../../mixins/observe';
import template from '../../mixins/template';

FLOW.PreviewView = FLOW.View.extend(template('navSurveys/preview-view'), {
  closePreviewPopup() {
    FLOW.previewControl.set('showPreviewPopup', false);
  },

});

FLOW.PreviewQuestionGroupView = FLOW.View.extend({
  QGcontent: null,

  init() {
    this._super();
    const qgId = this.content.get('keyId');
    const QGcontent = FLOW.store.filter(FLOW.Question, item => item.get('questionGroupId') == qgId);

    const tmp = QGcontent.toArray();
    tmp.sort((a, b) => a.get('order') - b.get('order'));
    this.set('QGcontent', tmp);
  },
});

FLOW.PreviewQuestionView = FLOW.View.extend(observe({
  'FLOW.previewControl.changed': 'checkVisibility',
  'this.optionChoice': 'storeOptionChoice',
  'this.answer': 'storeAnswer',
}), {
  isTextType: false,
  isOptionType: false,
  isNumberType: false,
  isPhotoType: false,
  isVideoType: false,
  isBarcodeType: false,
  isGeoType: false,
  isGeoshapeType: false,
  isDateType: false,
  isCascadeType: false,
  levelNameList: [],
  isVisible: true,
  optionsList: [],
  optionChoice: null,
  answer: null,
  latitude: null,
  longitude: null,

  init() {
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
      const qId = this.content.get('keyId');
      const options = FLOW.store.filter(FLOW.QuestionOption, item => item.get('questionId') == qId);

      const optionArray = options.toArray();
      optionArray.sort((a, b) => a.get('order') - b.get('order'));

      const tempList = [];
      optionArray.forEach((item) => {
        tempList.push(Ember.Object.create({
          isSelected: false,
          value: item.get('text'),
        }));
      });

      if (this.content.get('allowOtherFlag')) {
        tempList.push(Ember.Object.create({
          isSelected: false,
          value: Ember.String.loc('_other'),
        }));
      }
      this.set('optionsList', tempList);
    }
    if (this.isCascadeType) {
      const cascade = FLOW.store.find(FLOW.CascadeResource, this.content.get('cascadeResourceId'));
      if (!Ember.empty(cascade)) {
        const cascadeNames = cascade.get('levelNames');
        for (let i = 0; i < cascade.get('numLevels'); i++) {
          this.levelNameList.push(cascadeNames[i]);
        }
      }
    }
  },

  checkVisibility() {
    if (this.content.get('dependentFlag') && this.content.get('dependentQuestionId') !== null) {
      const dependentAnswerArray = this.content.get('dependentQuestionAnswer').split('|');
      if (dependentAnswerArray.indexOf(FLOW.previewControl.answers[this.content.get('dependentQuestionId')]) > -1) {
        this.set('isVisible', true);
      } else {
        this.set('isVisible', false);
      }
    }
  },

  storeOptionChoice() {
    const keyId = this.content.get('keyId');
    FLOW.previewControl.answers[keyId] = this.get('optionChoice');
    FLOW.previewControl.toggleProperty('changed');
  },

  storeAnswer() {
    const keyId = this.content.get('keyId');
    FLOW.previewControl.answers[keyId] = this.get('answer');
  },
});
