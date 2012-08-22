// ***********************************************//
//                      views                    
// ***********************************************//

  
  FLOW.ApplicationView = Ember.View.extend({  templateName: 'application'   });
  
  FLOW.NavigationView = Em.View.extend({
    templateName: 'navigation',
    selectedBinding: 'controller.selected',
    NavItemView: Ember.View.extend({
        tagName: 'li',
        classNameBindings: 'isActive:current navItem'.w(),
        
        navItem: function() {
            return this.get('item')
        }.property('item').cacheable(),
        
        
        isActive: function() {
            return this.get('item') === this.get('parentView.selected');
        }.property('item', 'parentView.selected').cacheable()
    })
});



FLOW.DatasubnavView = Em.View.extend({
    templateName: 'datasubnav',
    selectedBinding: 'controller.selected',
    NavItemView: Ember.View.extend({
        tagName: 'li',
        classNameBindings: 'isActive:active'.w(),
     
        isActive: function() {
            return this.get('item') === this.get('parentView.selected');
        }.property('item', 'parentView.selected').cacheable()
    })
});
  
  
  FLOW.NavHomeView = Ember.View.extend({  templateName: 'navHome' });

  FLOW.NavSurveysView = Ember.View.extend({templateName: 'navSurveys' });
  
  FLOW.NavDevicesView = Ember.View.extend({ templateName: 'navDevices' });
  
  FLOW.NavDataView = Ember.View.extend({  templateName: 'navData' });
  
  FLOW.InspectDataView = Ember.View.extend({  templateName: 'inspectData' });
  
  FLOW.ImportSurveyView = Ember.View.extend({  templateName: 'importSurvey' });
  
  FLOW.ExcelImportView = Ember.View.extend({ templateName: 'excelImport' });
  
	FLOW.ExcelExportView = Ember.View.extend({ templateName: 'excelExport' });
  
  FLOW.NavReportsView = Ember.View.extend({ templateName: 'navReports' });
   
  FLOW.NavMapsView = Ember.View.extend({  templateName: 'navMaps' });
  
  FLOW.NavUsersView = Ember.View.extend({  templateName: 'navUsers' });
  
  FLOW.NavAdminView = Ember.View.extend({	 templateName: 'navAdmin' });


FLOW.QuestionsInSetView = Ember.View.extend({
    content: null, //question sets
    questionList: null, // questions
    templateName: 'questionTempl',

    filteredQuestions: function() {
        return this.get("questionList").filterProperty('questionSetId', this.content.get('keyId'))
    }.property('questionList.@each').cacheable() 
});





	//	var newrec= FLOW.store.createRecord(FLOW.SurveyGroup,  
  //					{"description":"version 4" , "displayName":"Or this"});
  //	FLOW.store.commit();
   				