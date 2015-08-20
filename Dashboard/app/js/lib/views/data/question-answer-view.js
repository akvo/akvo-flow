// this function is also present in assignment-edit-views.js, we need to consolidate using moment.js

function formatDate(value) {
  if (!Ember.none(value)) {
    return value.getFullYear() + "/" + (value.getMonth() + 1) + "/" + value.getDate();
  } else return null;
}


FLOW.QuestionAnswerView = Ember.View.extend({
  isTextType: false,
  isOptionType: false,
  isMultipleOptionType: false,
  isNumberType: false,
  isBarcodeType: false,
  isGeoshapeType: false,
  isDateType: false,
  isPhotoType: false,
  isVideoType: false,
  optionsList: [],
  content: null,
  optionChoice: null,
  inEditMode: false,
  isNotEditable: false,
  value: null,
  displayValue: null,
  numberValue: null,
  date: null,
  photoUrl: null,

  init: function () {
    this._super();
    this.doInit();
  },

  doInit: function () {
    var q, questionId, type;

    // TODO use filter instead: if the question is not yet there, don't do anything
    // it will be picked up later at isLoaded.
    questionId = this.content.get('questionID');
    q = FLOW.store.find(FLOW.Question, questionId);
    type = q.get('type');
    multipleFlag = q.get('allowMultipleFlag');
    this.set('isTextType', type == 'FREE_TEXT' || type === 'CASCADE');
    this.set('isOptionType', type == 'OPTION');
    this.set('isMultipleOptionType', multipleFlag === true);
    this.set('isNumberType', type == 'NUMBER');
    this.set('isBarcodeType', type == 'BARCODE');
    this.set('isPhotoType', type == 'PHOTO');
    this.set('isVideoType', type == 'VIDEO');
    this.set('isDateType', type == 'DATE');
    this.set('isNotEditable', (type == 'GEO' || type == 'PHOTO' || type == 'VIDEO' || type == 'GEOSHAPE'));

    this.setInitialValue();
  }.observes('FLOW.questionControl.content.isLoaded'),


  setInitialValue: function () {
    var opList, opListArray, i, sizeList, q, questionId, qaValue, choice = null,
      date;

    questionId = this.content.get('questionID');
    q = FLOW.store.find(FLOW.Question, questionId);

    // set value
    this.set('value', this.content.get('value'));

    if (this.get('isNumberType')) {
      this.set('numberValue', this.content.get('value'));
    }

    if (this.get('isDateType') && !Ember.none(this.content.get('value'))) {
      date = new Date(parseInt(this.content.get('value'), 10));
      this.set('date', formatDate(date));
    }
    if (this.get('isOptionType') && !Ember.none(this.content.get('value'))) {
      options = FLOW.store.filter(FLOW.QuestionOption, function (item) {
        return item.get('questionId') == questionId;
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
      this.set('optionsList', tempList);

      // set answer

      if (!this.get('isMultipleOptionType')) {
        qaValue = this.content.get('value');
        this.get('optionsList').forEach(function (item) {
          if (item.get('value') == qaValue) {
            choice = item;
          }
        });
        this.set('optionChoice', choice);
      } else {

        /*
        Values for multiple-option questions are stored as pipe-separated strings.
        Add spaces on either side of the pipes to the display value to make it
        easier to read.
        */

        this.content.set('displayValue', this.content.get('value').replace(/\|/g, ' | '));         

        /* 
        Set the initial value for multiple-option questions.
        Iterate through the optionsList array and mark each
        option we have stored.
        */
        tempList = [];
        qaValue = [];
        qaValue = this.content.get('value').split('|');
        this.get('optionsList').forEach(function (item) {
          var isSelected = false;
          qaValue.forEach(function (qaItem) {
            if (item.get('value') === qaItem.toString()) {
              isSelected = true;
            }
          });
          tempList.push(Ember.Object.create({
            isSelected: isSelected,
            value: item.get('value'),
          }));         
        });  
        this.set('optionsList', tempList);
      }
    }
    if ((this.get('isPhotoType') || this.get('isVideoType')) && !Ember.empty(this.content.get('value'))) {
      // Since photos have a leading path from devices that we need to trim
      this.set('photoUrl', FLOW.Env.photo_url_root + this.content.get('value').split('/').pop());
    }
  },

  doEdit: function () {
    this.set('inEditMode', true);
  },

  doCancel: function () {
    // revert answer
    this.setInitialValue();
    this.set('inEditMode', false);
  },

  doSave: function () {
    var value, valueArr, tempDate = null;
    if (this.get('isDateType')) {
      if (Ember.empty(this.get('date'))) {
        this.content.set('value', null);
      } else {
        tempDate = Date.parse(this.get('date'));
        if (!isNaN(tempDate)) {
          this.content.set('value', tempDate);
        } else {
          this.content.set('value', null);
        }
      }
    } else if (this.get('isOptionType')) {
      if (this.get('isMultipleOptionType')) {
        valueArr = [];
        this.get('optionsList').forEach( function(item) {
          if (item.get('isSelected')) {
            valueArr.push(item.get('value'));
          }
        });
        value = valueArr.join('|');
        this.content.set('value', value);
        this.content.set('displayValue', value.replace(/\|/g, ' | '));
      } else {
        this.content.set('value', this.optionChoice.get('value'));
      }
    } else if (this.get('isNumberType')) {
      this.content.set('value', this.get('numberValue'));
    } else {
      this.content.set('value', this.get('value'));
    }
    FLOW.store.commit();
    this.set('inEditMode', false);
  },

  doValidateNumber: function () {
    // TODO should check for minus sign and decimal point, depending on question setting
    this.set('numberValue', this.get('numberValue').toString().replace(/[^\d.]/g, ""));
  }.observes('this.numberValue')

});
