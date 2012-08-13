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

