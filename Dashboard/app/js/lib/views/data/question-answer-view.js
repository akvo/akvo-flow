FLOW.QuestionAnswerView = Ember.View.extend({
  isTextType: false,
  isOptionType: false,
  isNumberType: false,
  isBarcodeType: false,
  isDateType: false,
  optionsList: [],
  content: null,
  optionChoice: null,
  inEditMode:false,

  init: function() {
    this._super();
    this.doInit();
  },

  doInit:function(){
     var opList, opListArray, i, sizeList, q, questionId, qaValue, choice;
   
   // TODO use filter instead: if the question is not yet there, don't do anything
   // it will be picked up later at isLoaded.
    questionId = this.content.get('questionID');
    q = FLOW.store.find(FLOW.Question, questionId);
    this.set('isTextType', q.get('type') == 'FREE_TEXT');
    this.set('isOptionType', q.get('type') == 'OPTION');
    this.set('isNumberType', q.get('type') == 'NUMBER');
    this.set('isBarcodeType', q.get('type') == 'BARCODE');
    this.set('isDateType', q.get('type') == 'DATE');

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
  }.observes('FLOW.questionControl.content.isLoaded'),

  doEdit: function (){
    this.set('inEditMode',true);
  },

  doSave: function (){
    
  },

  doCancel: function (){
    // revert answer
    this.set('inEditMode',false);
  }
});