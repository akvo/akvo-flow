// ***********************************************//
//                      views                    
// ***********************************************//

  
FLOW.ApplicationView = Ember.View.extend({  templateName: 'application'   });

// main Navigation with 'active' indication  
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

// subnavigation for the data tab
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


FLOW.QuestionGroupView = Ember.View.extend({
	classNames: 'child-view'.w(),
	selectedBinding:"FLOW.selectedQuestionGroupController.selectedQuestionGroup",
 	content:null,
 	QuestionGroupItemView: Ember.View.extend({
			
			amVisible: function(){
				var id= (this.content.get('keyId') === this.get('parentView.selected.keyId'));
				return id;
			}.property('parentView.selected','content.keyId'),
			
			toggleVisibility:function(){
					if (this.get('amVisible')) {
						this.set('parentView.selected',null)
					}
					else {
						this.set('parentView.selected',this.content);
					}
			},
			
			showHideText: function(){
    		return this.get('amVisible') ? 'Close group' : 'Open group';
  			}.property('amVisible')	
	})	
});



FLOW.QuestionListView = Ember.View.extend({
	content:null,

});

