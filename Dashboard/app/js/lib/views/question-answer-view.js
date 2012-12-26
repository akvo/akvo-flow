FLOW.QuestionAnswerView = Ember.View.extend({

  isEditable: function (){
    var type = this.content.get('type');
    return (type == "VALUE");
  }.property()


});