// this function is also present in assignment-edit-views.js, we need to consolidate using moment.js
function formatDate(value) {
  if(!Ember.none(value)) {
    return value.getFullYear() + "/" + (value.getMonth() + 1) + "/" + value.getDate();
  } else return null;
}


FLOW.QuestionAnswerView = Ember.View.extend({
  isTextType: false,
  isOptionType: false,
  isNumberType: false,
  isBarcodeType: false,
  isDateType: false,
  isPhotoType: false,
  optionsList: [],
  content: null,
  optionChoice: null,
  inEditMode: false,
  isNotEditable: false,
  value: null,
  numberValue:null,
  date: null,
  photoUrl:null,

  init: function() {
    this._super();
    this.doInit();
  },

doInit: function() {
    var q, questionId, type;

    // TODO use filter instead: if the question is not yet there, don't do anything
    // it will be picked up later at isLoaded.
    questionId = this.content.get('questionID');
    q = FLOW.store.find(FLOW.Question, questionId);
    type = q.get('type');
    this.set('isTextType', type == 'FREE_TEXT');
    this.set('isOptionType', type == 'OPTION');
    this.set('isNumberType', type == 'NUMBER');
    this.set('isBarcodeType', type == 'BARCODE');
    this.set('isPhotoType', type == 'PHOTO');
    this.set('isDateType', type == 'DATE');
    this.set('isNotEditable', (type == 'GEO' || type == 'PHOTO' || type == 'VIDEO'));

    this.setInitialValue();
  }.observes('FLOW.questionControl.content.isLoaded'),


  setInitialValue: function() {
    var opList, opListArray, i, sizeList, q, questionId, qaValue, choice = null, date;

    questionId = this.content.get('questionID');
    q = FLOW.store.find(FLOW.Question, questionId);

    // set value
    this.set('value', this.content.get('value'));

    if(this.get('isNumberType')) {
      this.set('numberValue',this.content.get('value'));
    }

    if(this.get('isDateType') && !Ember.none(this.content.get('value'))) {
      date = new Date(parseInt(this.content.get('value'),10));
      this.set('date',formatDate(date));
    }
    // fill option list
    if(this.get('isOptionType') && q.get('optionList') !== null) {
      this.set('optionsList', []);
      opList = q.get('optionList');
      opListArray = opList.split('\n');
      sizeList = opListArray.length;

      for(i = 0; i < sizeList; i++) {
        this.get('optionsList').push(Ember.Object.create({
          isSelected: false,
          value: opListArray[i]
        }));
      }
      // set answer
      qaValue = this.content.get('value');
      this.get('optionsList').forEach(function(item) {
        if(item.get('value') == qaValue) {
          choice = item;
        }
      });
      this.set('optionChoice', choice);
    }
    if (this.get('isPhotoType') && !Ember.empty(this.content.get('value'))){
      // Since photos have a leading path from devices that we need to trim
      this.set('photoUrl',FLOW.Env.photo_url_root + this.content.get('value').split('/').pop());
    }
  },

  doEdit: function() {
    this.set('inEditMode', true);
  },

  doCancel: function() {
    // revert answer
    this.setInitialValue();
    this.set('inEditMode', false);
  },

  doSave: function() {
    if(this.get('isDateType')){
      this.content.set('value',Date.parse(this.get('date')));
    } else if (this.get('isOptionType')){
      this.content.set('value',this.optionChoice.get('value'));
    } else if (this.get('isNumberType')){
      this.content.set('value',this.get('numberValue'));
    } else {
      this.content.set('value',this.get('value'));
    }
    FLOW.store.commit();
    this.set('inEditMode', false);
  },

  doValidateNumber: function () {
    // TODO should check for minus sign and decimal point, depending on question setting
    this.set('numberValue',this.get('numberValue').toString().replace(/[^\d.]/g, ""));
  }.observes('this.numberValue')

});