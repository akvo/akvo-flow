// this function is also present in assignment-edit-views.js, we need to consolidate using moment.js

function formatDate(date) {
  if (date && !isNaN(date.getTime())) {
    return date.getFullYear() + "/" + (date.getMonth() + 1) + "/" + date.getDate();
  } else return null;
}

function sortByOrder(a , b) {
  return a.get('order') - b.get('order');
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

  isSignatureType: function(){
    return this.get('questionType') === 'SIGNATURE' || (this.content && this.content.get('type') === 'SIGNATURE');
  }.property('this.questionType'),

  isCaddisflyType: function(){
	    return this.get('questionType') === 'CADDISFLY' || (this.content && this.content.get('type') === 'CADDISFLY');
	}.property('this.questionType'),

  nonEditableQuestionTypes: ['GEO', 'PHOTO', 'VIDEO', 'GEOSHAPE', 'SIGNATURE','CADDISFLY'],

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
        code : item.get('code') && item.get('code').trim(),
        text : item.get('text').trim(),
        order: item.get('order')
      });
      tempList.push(obj);
    });

    // add other option if enabed
    // we assume codes are all or nothing
    var setOptionCodes = tempList.get('firstObject').get('code');
    if (this.get('isOtherOptionEnabled')) {
      tempList.push(Ember.Object.create({
        code: setOptionCodes ? "OTHER" : null, // OTHER is default code
        otherText: null,
        text: function () {
          var suffix = this.get('otherText') ? this.get('otherText') : Ember.String.loc('_other_option_specify');
          return Ember.String.loc('_other') + ": " + suffix;
        }.property('this.otherText'),
        order: tempList.get('length'),
        isOther: true,
      }));
    }
    return tempList;
  }.property('this.content'),

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

  /*
   *  Extract base64 signature image data and convert to src attribute for
   *  HTML img tag
   */
  signatureImageSrc: function () {
    var c = this.content, srcAttr = 'data:image/png;base64,', signatureJson;
    if (c && c.get('value')) {
      signatureJson = JSON.parse(c.get('value'));
      return srcAttr + signatureJson.image;
    }
    return null
  }.property('this.content'),

  /*
   * Extract signatory from signature response
   */
  signatureSignatory: function () {
    var c = this.content, signatureJson;
    if (c && c.get('value')) {
      signatureJson = JSON.parse(c.get('value'));
      return signatureJson.name.trim();
    }
    return null;
  }.property('this.content'),

  /*
  * parse the caddisfly test JSON result
  */
  parseTestJson: function(){
	  var c = this.content, testJson, newResult, image;
  	result=Ember.A();
  	if (c && c.get('value')) {
  	  testJson = JSON.parse(c.get('value'));
  	  if (testJson.result && !Ember.empty(testJson.result)){
 		    testJson.result.forEach(function(item){
  			  newResult = {"name":item.name,
  				  "value":item.value,
  				  "unit":item.unit,
  	  		};
  		  	result.push(newResult);
  		  });
  	  }
    }
    this.set('testResult',result);
  },

  /*
   * Get out the caddisfly test name
  */
  testName: function(){
  	var c = this.content, testJson;
  	if (c && c.get('value')) {
  	  testJson = JSON.parse(c.get('value'));
  	  if (!Ember.empty(testJson.result))
  	  {
  		  this.parseTestJson();
  	  }
  	  if (!Ember.empty(testJson.name)){
  		  return testJson.name.trim();
  	  }
  	}
    return null;
  }.property('this.content'),

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

  /* object properties to include when transforming selected options
   * to string to store in the datastore
   */
  optionValueProperties: ['code', 'text', 'isOther'],

  /*
   *  An Ember array consisting of selected elements from the optionsList.
   *  This is later serialised into a string response for the datastore.
   */
  selectedOptionValues: null,

  /*
   *  A view property to set and retrieve the selectedOptionValues array
   *
   *  At first load, the getter block uses the string response from the datastore
   *  to create an Ember array consisting of the corresponding elements from the
   *  optionsList property, i.e, selected elements of the optionsList.
   *
   *  Retrieved string responses could be:
   *   - pipe-separated strings for legacy format e.g. 'text1|text2'
   *   - JSON string in the current format e.g
   *    '[{text: "text with code", code: "code"}]'
   *    '[{text: "only text"}]'
   */
  optionValue: function (key, value, previousValue) {
    var valueArray = [], selectedOptions = Ember.A(), c = this.content, isOtherEnabled;

    // setter
    if (arguments.length > 1) {
      this.set('selectedOptionValues', value);
    }

    // getter
    // initial selection setup
    if (!this.get('selectedOptionValues')) {
      this.set('selectedOptionValues', this.parseOptionsValueString(c.get('value')));
    }
    return this.get('selectedOptionValues');
  }.property('this.selectedOptionValues'),

  /*
   *  Used to parse a provided string response from the value property of an option question.
   *  Returns an Ember array consisting of the corresponding elements from the optionsList
   *  property, i.e, selected elements of the optionsList.
   *
   *  The string responses could be:
   *   - pipe-separated strings for legacy format e.g. 'text1|text2'
   *   - JSON string in the current format e.g
   *    '[{text: "text with code", code: "code"}]'
   *    '[{text: "only text"}]'
   */
  parseOptionsValueString: function (optionsValueString) {
    if (!optionsValueString) {
      return Ember.A();
    }

    var selectedOptions = Ember.A();
    var optionsList = this.get('optionsList');
    var isOtherEnabled = this.get('isOtherOptionEnabled');

    if (optionsValueString.charAt(0) === '[') {
      // responses in JSON format
      JSON.parse(optionsValueString).forEach(function (response) {
        optionsList.forEach(function (optionObj) {
          if (response.text === optionObj.get('text') &&
              response.code == optionObj.get('code')) { // '==' because codes could be undefined or null
            selectedOptions.addObject(optionObj);
          }

          // add other
          if (response.isOther && optionObj.get('isOther') && isOtherEnabled) {
            optionObj.set('otherText', response.text);
            selectedOptions.addObject(optionObj);
          }
        });
      });
    } else {
      // responses in pipe separated format
      optionsValueString.split("|").forEach(function(item, textIndex, textArray){
        var text = item.trim(), isLastItem = textIndex === textArray.length - 1;
        if (text.length > 0) {
          optionsList.forEach(function(optionObj) {
            var optionIsIncluded = optionObj.get('text') && optionObj.get('text') === text;
            if (optionIsIncluded) {
              selectedOptions.addObject(optionObj);
            }

            // add other
            if (!optionIsIncluded && optionObj.get('isOther') && isOtherEnabled && isLastItem) {
              optionObj.set('otherText', text);
              selectedOptions.addObject(optionObj);
            }
          });
        }
      });
    }

    return selectedOptions.sort(sortByOrder);
  },

  /*
   *  A property to enable setting and getting of the selected element
   *  of a single-select option question.
   *
   */
  singleSelectOptionValue: function (key, value, previousValue) {
    var selectedOptions, c = this.content;

    // setter
    if (c && arguments.length > 1) {
      selectedOptions = Ember.A();
      selectedOptions.push(value);
      this.set('optionValue', selectedOptions);
    }

    // getter
    var options = this.get('optionValue');
    if (options && options.get('length') === 1) {
      return options.get('firstObject');
    }
    return null;
  }.property('this.optionValue'),

  /*
   *  A property to enable setting and getting of the selected element
   *  of a multi-select option question.
   *
   */
  multiSelectOptionValue: function (key, value, previousValue) {
    var c = this.content;

    // setter
    if (c && arguments.length > 1) {
      this.set('optionValue', value);
    }

    // getter
    return this.get('optionValue');
  }.property('this.optionValue'),

  isMultipleSelectOption: function () {
    return this.get('isOptionType') && this.get('question').get('allowMultipleFlag');
  }.property('this.isOptionType'),

  isOtherOptionEnabled: function () {
    return this.get('isOptionType') && this.get('question').get('allowOtherFlag');
  }.property('this.isOptionType'),

  isOtherOptionSelected: function () {
    var selectedOption = this.get('optionValue') && this.get('optionValue').get('lastObject');
    return selectedOption && selectedOption.get('isOther');
  }.property('this.optionValue'),

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
  }.property('this.content'),

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

    if (this.get('isOptionType')) {
      var responseArray = [];
      this.get('selectedOptionValues').forEach(function(option){
        var obj = {};
        if (option.get('code')) {
          obj.code = option.get('code');
        }
        if (option.get('isOther')) {
          obj.isOther = option.get('isOther');
          obj.text = option.get('otherText');
        } else {
          obj.text = option.get('text');
        }
        responseArray.push(obj);
      });
      this.content.set('value', JSON.stringify(responseArray));
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

/**
 * Multi select option editing view that renders question options with the allow
 * multiple option selected.  The selection property should be a set of options
 * that are considered to be selected (checked).  Whenever each options checkbox
 * is modified, the set bound to the selection property is updated to add or remove
 * the item.
 *
 * The inline view template displays the selected option's text with its corresponding
 * checkbox and if the 'allow other' flag is set on the question, it displays in addition
 * a text box to enable editing the other response text.
 */
FLOW.QuestionAnswerMultiOptionEditView = Ember.CollectionView.extend({
  tagName: 'ul',
  content: null,
  selection: null,
  itemViewClass: Ember.View.extend({
    template: Ember.Handlebars.compile('{{view Ember.Checkbox checkedBinding="view.isSelected"}} {{view.content.text}}'),
    isSelected: function(key, checked, previousValue) {
      var selectedOptions = this.get('parentView').get('selection');
      var newSelectedOptions = Ember.A();

      // setter
      if (arguments.length > 1) {
        if (checked) {
          selectedOptions.addObject(this.content);
        } else {
          selectedOptions.removeObject(this.content);
        }
        selectedOptions.forEach(function(option){
          newSelectedOptions.push(option);
        });

        // Using a copy of the selected options 'newSelectedOptions' in order
        // to trigger the setter property of the object bound to 'parentView.selection'
        this.set('parentView.selection', newSelectedOptions);
      }

      // getter
      return selectedOptions && selectedOptions.contains(this.content);
    }.property('this.content'),
  })
});

FLOW.QuestionAnswerInspectDataView = FLOW.QuestionAnswerView.extend({
  templateName: 'navData/question-answer',
});
