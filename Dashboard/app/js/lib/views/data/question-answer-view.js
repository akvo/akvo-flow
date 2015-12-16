// this function is also present in assignment-edit-views.js, we need to consolidate using moment.js

function formatDate(date) {
  if (date && !isNaN(date.getTime())) {
    return date.getFullYear() + "/" + (date.getMonth() + 1) + "/" + date.getDate();
  } else return null;
}


FLOW.QuestionAnswerView = Ember.View.extend({

  isTextType: function(){
    return this.get('questionType') === 'FREE_TEXT';
  }.property('this.questionType'),

  isCascadeType: function(){
    return this.get('questionType') === 'CASCADE';
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

  isGeoShapeType: function(){
    return this.get('questionType') === 'GEOSHAPE';
  }.property('this.questionType'),

  nonEditableQuestionTypes: ['GEO', 'PHOTO', 'VIDEO', 'GEOSHAPE'],

  form: function() {
    if (FLOW.selectedControl.get('selectedSurvey')) {
      return FLOW.selectedControl.get('selectedSurvey');
    }
  }.property('FLOW.selectedControl.selectedSurvey'),

  /*
   * Get the full list of options related to a particular option type question
   */
  optionsList: function(){
    var c = this.content;
    if (Ember.none(c) || !this.get('isOptionType')) {
      return Ember.A([]);
    }

    var questionId = c.get('questionID');

    var options = FLOW.store.filter(FLOW.QuestionOption, function (item) {
      return item.get('questionId') === +questionId;
    });

    optionArray = options.toArray();
    optionArray.sort(function (a, b) {
        return a.get('order') - b.get('order');
    });

    tempList = Ember.A([]);
    var obj;
    optionArray.forEach(function (item) {
      obj = Ember.Object.create({
        code : item.get('code'),
        text : item.get('text')
      });
      tempList.push(obj);
    });
    return tempList;
  }.property('this.content,this.isOptionType'),

  content: null,

  inEditMode: false,

  isNotEditable: function() {
    // keep this property to limit template rafactoring
    return !this.get('isEditable');
  }.property('this.isEditable'),

  isEditable: function () {
    var isEditableQuestionType, canEditFormResponses;
    isEditableQuestionType = this.nonEditableQuestionTypes.indexOf(this.get('questionType')) < 0;
    if (!isEditableQuestionType) {
      return false; // no need to check permissions
    }

    canEditFormResponses = FLOW.permControl.canEditResponses(this.get('form'));
    return isEditableQuestionType && canEditFormResponses;
  }.property('this.questionType,this.form'),

  date: null,

  numberValue: null,

  cascadeValue: function(key, value, previousValue){
    var c = this.content;
    // setter
    if (arguments.length > 1) {
      // split according to pipes
      var cascadeNames = value.split("|");
      var cascadeResponse = [];
      var obj = null;
      cascadeNames.forEach(function(item){
        if (item.trim().length > 0) {
          obj = {};
          obj.name = item.trim();
          cascadeResponse.push(obj);
        }
      });

      c.set('value', JSON.stringify(cascadeResponse));
    }

    // getter
    var cascadeString = "", cascadeJson;
    if (c && c.get('value')) {
      if (c.get('value').indexOf("|") > -1) {
        cascadeString += c.get('value');
      } else {
        cascadeJson = JSON.parse(c.get('value'));
        cascadeString = cascadeJson.map(function(item){
          return item.name;
        }).join("|");
      }
      return cascadeString;
    }
    return null;
  }.property('this.content'),

  /*
   *  A view property to support the getting and setting of old and new
   *  format values for OPTION type question items / lists.
   *
   *  The setter takes an object and correctly transforms it into an
   *  array that is later stored in the value property as a string
   *
   *  The getter retrieves the string response from the value property and
   *  transforms it into an Ember array consisting of elements from
   *  the optionsList property.  We return elements from the optionList
   *  in order to handle binding of the view elements to the value. Retrieved
   *  responses could be:
   *   - pipe-separated strings for legacy format e.g. 'text1|text2'
   *   - JSON string in the current format e.g
   *    '[{text: "text with code", code: "code"}]'
   *    '[{text: "only text"}]'
   *  An ARRAY is always returned if the content property of the
   *  view is set.
   */
  optionValue: function (key, value, previousValue) {
    var val, textArray = [], selectedOptions = Ember.A(), c = this.content;
    //setter
    if (c && arguments.length > 1) {
    }

    // getter
    if (c && c.get('value')) {
      val = c.get('value');

      if (val.charAt(0) === '[') {
        // responses in JSON format
        JSON.parse(val).forEach(function (response) {
          this.get('optionsList').forEach(function (optionObj, index) {
            if (response.text === optionObj.get('text') &&
                response.code === optionObj.get('code')) {
              selectedOptions.addObject(optionObj);
            }
          });
        });
      } else {
        // responses in pipe separated format
        val.split("|").forEach(function(item){
          if (item.trim().length > 0) {
            textArray.push(item.trim());
          }
        });

        this.get('optionsList').forEach(function(optionObj, i) {
          if (textArray.indexOf(optionObj.get('text')) > -1) {
            selectedOptions.addObject(optionObj);
          }
        });
      }
      return selectedOptions;
    }
    return null;
  }.property('this.content,this.optionsList'),

  photoUrl: function(){
    var c = this.content;
    if (!Ember.empty(c.get('value'))) {
      return FLOW.Env.photo_url_root + c.get('value').split('/').pop();
    }
  }.property('this.content,this.isPhotoType,this.isVideoType'),

  geoShapeObject: function(){
    var c = this.content;
    if (!Ember.empty(c.get('value'))) {
      return c.get('value');
    }
  }.property('this.content,this.isGeoShapeType'),

  questionType: function(){
    if(this.get('question')){
      return this.get('question').get('type');
    }
  }.property('this.question'),

  question: function(){
    var c = this.get('content');
    if (c) {
      var questionId = c.get('questionID');
      var q = FLOW.questionControl.findProperty('keyId', +questionId);
      return q;
    }
  }.property('FLOW.questionControl.content'),

  init: function () {
    this._super();
  },

  doEdit: function () {
    this.set('inEditMode', true);
    var c = this.content;

    if (this.get('isDateType') && !Ember.empty(c.get('value'))) {
      var d = new Date(+c.get('value')); // need to coerce c.get('value') due to milliseconds
      this.set('date', formatDate(d));
    }

    if (this.get('isNumberType') && !Ember.empty(c.get('value'))) {
      this.set('numberValue', c.get('value'));
    }
  },

  doCancel: function () {
    this.set('inEditMode', false);
  },

  doSave: function () {

    if (this.get('isDateType')) {
      var d = Date.parse(this.get('date'));
      if (isNaN(d) || d < 0) {
        this.content.set('value', null);
      } else {
        this.content.set('value', d);
      }
    }

    if (this.get('isNumberType')) {
      if (isNaN(this.numberValue)) {
        this.content.set('value', null);
      } else {
        this.content.set('value', this.numberValue);
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

FLOW.QuestionAnswerOptionListView = Ember.CollectionView.extend({
  tagName: 'ul',
  content: null,
  itemViewClass: Ember.View.extend({
    template: Ember.Handlebars.compile("{{view.content.text}}")
  })
});

FLOW.QuestionAnswerInspectDataView = FLOW.QuestionAnswerView.extend({
  templateName: 'navData/question-answer',
});
