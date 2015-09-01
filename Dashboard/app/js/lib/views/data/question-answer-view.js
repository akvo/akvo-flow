// this function is also present in assignment-edit-views.js, we need to consolidate using moment.js

function formatDate(value) {
  if (!Ember.none(value)) {
    return value.getFullYear() + "/" + (value.getMonth() + 1) + "/" + value.getDate();
  } else return null;
}


FLOW.QuestionAnswerView = Ember.View.extend({
  templateName: 'navData/question-answer',

  isTextType: function(){
    return this.get('questionType') === 'FREE_TEXT' || this.get('questionType') === 'CASCADE';
  }.property('this.questionType'),

  isOptionType: function(){
    return this.get('questionType') === 'OPTION';
  }.property('this.questionType'),

  isNumberType: function(){
    return this.get('questionType') === 'NUMBER';
  }.property('this.questionType'),

  isBarcodeType: function(){
    return this.get('questionType') === 'SCAN';
  }.property('this.questionType'),

  isDateType: function(){
    return this.get('questionType') === 'DATE';
  }.property('this.questionType'),

  isPhotoType: function(){
    return this.get('questionType') === 'PHOTO';
  }.property('this.questionType'),

  isVideoType: function(){
    return this.get('questionType') === 'VIDEO';
  }.property('this.questionType'),

  optionsList: function(){
    var c = this.content;
    if (Ember.none(c)) {
      return [];
    }

    var questionId = c.get('questionID');

    var options = FLOW.store.filter(FLOW.QuestionOption, function (item) {
      return item.get('questionId') === +questionId;
    });

    optionArray = options.toArray();
    optionArray.sort(function (a, b) {
        return a.get('order') - b.get('order');
    });

    tempList = [];
    optionArray.forEach(function (item) {
      tempList.push(item.get('text'));
    });
    return tempList;
  }.property('this.content'),

  content: null,

  inEditMode: false,

  isNotEditable: function(){
    var type = this.get('questionType');
    return (type == 'GEO' || type == 'PHOTO' || type == 'VIDEO' || type == 'GEOSHAPE');
  }.property('this.questionType'),

  date: function(){
    var c = this.content;
    if (this.isDateType && !Ember.empty(c.get('value'))) {
      var date = new Date(parseInt(c.get('value'), 10));
      return formatDate(date);
    }
  }.property('this.content'),

  photoUrl: function(){
    var c = this.content;
    if (!Ember.empty(c.get('value'))) {
      return FLOW.Env.photo_url_root + c.get('value').split('/').pop();
    }
  }.property('this.content,this.isPhotoType,this.isVideoType'),

  questionType: function(){
    if(this.get('question')){
      return this.get('question').get('type');
    }
  }.property('this.question'),

  question: function(){
    var c = this.get('content');
    if (c) {
      var questionId = this.content.get('questionID');
      var q = FLOW.questionControl.findProperty('keyId', +questionId);
      return q;
    }
  }.property('FLOW.questionControl.content'),

  init: function () {
    this._super();
  },

  doEdit: function () {
    this.set('inEditMode', true);
  },

  doCancel: function () {
    this.set('inEditMode', false);
  },

  doSave: function () {

    if (this.get('isDateType')) {
      if (Ember.empty(this.get('date'))) {
        this.content.set('value', null);
      } else {
        var tempDate = null;
        tempDate = Date.parse(this.get('date'));
        if (!isNaN(tempDate)) {
          this.content.set('value', tempDate);
        } else {
          this.content.set('value', null);
        }
      }
    }
    FLOW.store.commit();
    this.set('inEditMode', false);
  },

  doValidateNumber: function () {
    // TODO should check for minus sign and decimal point, depending on question setting
    this.set('numberValue', this.get('numberValue').toString().replace(/[^\d.]/g, ""));
  }.observes('this.numberValue')
});
