// ***********************************************//
//                 Router
// ***********************************************//
require('akvo-flow/core');

FLOW.Router = Ember.Router.extend({
  enableLogging: true,
  loggedIn:false,
  location: 'none', //'hash'or 'none' for URLs
  
  root: Ember.Route.extend({
    doNavHome: function(router, context) {
      router.transitionTo('navHome');
    },
    doNavSurveys: function(router, context) {
      router.transitionTo('navSurveys.index');
    },
    doNavDevices: function(router, context) {
      router.transitionTo('navDevices.index');
    },
    doNavData: function(router, context) {
      router.transitionTo('navData.index');
    },
    doNavReports: function(router, context) {
      router.transitionTo('navReports');
    },
    doNavMaps: function(router, context) {
      router.transitionTo('navMaps');
    },
    doNavUsers: function(router, context) {
      router.transitionTo('navUsers');
    },
    doNavAdmin: function(router, context) {
      router.transitionTo('navAdmin');
    },

// non-working code for transitioning to navHome at first entry of the app
//    setup: function(router){
//      router.send("goHome");
//    },
    
//    goHome:function(router){
//      router.transitionTo('navHome');
//    },
    
    index: Ember.Route.extend({
      route: '/',
      redirectsTo: 'navHome'
    }),

// ************************** HOME ROUTER **********************************
    navHome: Ember.Route.extend({
      route: '/',
      connectOutlets: function(router, event) {
        router.get('applicationController').connectOutlet('navHome');
        router.set('navigationController.selected', 'navHome');
      }
    }),

// ******************* SURVEYS ROUTER ********************
    navSurveys: Ember.Route.extend({
      route: '/surveys',
      connectOutlets: function(router, event) {
        router.get('applicationController').connectOutlet('navSurveys');
        router.set('navigationController.selected', 'navSurveys');
      },

      doEditSurvey: function(router, event) {
        FLOW.selectedControl.set('selectedSurvey',event.context);
        router.transitionTo('navSurveys.navSurveysEdit');
      },
      
      doSurveysMain: function(router, event) {
        FLOW.selectedControl.set('selectedQuestion',null);
        router.transitionTo('navSurveys.navSurveysMain');
      },
      
      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'navSurveysMain'
      }),

      navSurveysMain: Ember.Route.extend({
        route: '/main',
        connectOutlets: function(router, event) {
          router.get('navSurveysController').connectOutlet({ name: 'navSurveysMain'});
          FLOW.surveyGroupControl.populate();
        }
      }),
      
      navSurveysEdit: Ember.Route.extend({
        route: '/edit',
        connectOutlets: function(router, event) {
          router.get('navSurveysController').connectOutlet({ name: 'navSurveysEdit'});

          // all questions should be closed when we enter
          FLOW.selectedControl.set('selectedQuestion',null);
          // moet weg
          var sId=FLOW.selectedControl.selectedSurvey.get('keyId');
          FLOW.questionGroupControl.set('content', FLOW.store.find(FLOW.QuestionGroup, {surveyId:sId}));
        }
      })

    }),

//********************** DEVICES ROUTER *******************
    navDevices: Ember.Route.extend({
      route: '/devices',
      connectOutlets: function(router, event) {
        router.get('applicationController').connectOutlet('navDevices');
        router.set('navigationController.selected', 'navDevices');
      },

      doCurrentDevices: function(router, event) {
        router.transitionTo('navDevices.currentDevices');
      },
      
      doAssignSurveysOverview: function(router, event) {
        router.transitionTo('navDevices.assignSurveysOverview');
      },

      doEditSurveysAssignment: function(router, event) {
        router.transitionTo('navDevices.editSurveysAssignment');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'currentDevices'
      }),

      currentDevices: Ember.Route.extend({
        route: '/current-devices',
        connectOutlets: function(router, context) {
          router.get('navDevicesController').connectOutlet('currentDevices');
          FLOW.deviceGroupControl.populate();
          FLOW.deviceControl.populate();
          router.set('devicesSubnavController.selected', 'currentDevices');
        }
      }),

      assignSurveysOverview: Ember.Route.extend({
        route: '/assign-surveys',
        connectOutlets: function(router, context) {
          router.get('navDevicesController').connectOutlet('assignSurveysOverview');
           FLOW.surveyAssignmentControl.populate();
          router.set('devicesSubnavController.selected', 'assignSurveys');
        }
      }),

      editSurveysAssignment: Ember.Route.extend({
        route: '/assign-surveys',
        connectOutlets: function(router, context) {
          router.get('navDevicesController').connectOutlet('editSurveyAssignment');
          router.set('devicesSubnavController.selected', 'editSurveyAssignment');
        }
      })
    }),


// ******************* DATA ROUTER ***********************
    navData: Ember.Route.extend({
      route: '/data',
      connectOutlets: function(router, event) {
        router.get('applicationController').connectOutlet('navData');
        router.set('navigationController.selected', 'navData');
      },

      doInspectData: function(router, event) {
        router.transitionTo('navData.inspectData');
      },
      doImportSurvey: function(router, event) {
        router.transitionTo('navData.importSurvey');
      },
      doExcelImport: function(router, event) {
        router.transitionTo('navData.excelImport');
      },
      doExcelExport: function(router, event) {
        router.transitionTo('navData.excelExport');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'inspectData'
      }),

      inspectData: Ember.Route.extend({
        route: '/inspectdata',
        connectOutlets: function(router, context) {
          router.get('navDataController').connectOutlet('inspectData');
          router.set('datasubnavController.selected', 'inspectData');
          FLOW.surveyGroupControl.populate();
          FLOW.surveyInstanceControl.populate();
        }
      }),

      importSurvey: Ember.Route.extend({
        route: '/importsurvey',
        connectOutlets: function(router, context) {
          router.get('navDataController').connectOutlet('importSurvey');
          router.set('datasubnavController.selected', 'importSurvey');
        }
      }),

      excelImport: Ember.Route.extend({
        route: '/excelimport',
        connectOutlets: function(router, context) {
          router.get('navDataController').connectOutlet('excelImport');
          router.set('datasubnavController.selected', 'excelImport');
        }
      }),

      excelExport: Ember.Route.extend({
        route: '/excelexport',
        connectOutlets: function(router, context) {
          router.get('navDataController').connectOutlet('excelExport');
          router.set('datasubnavController.selected', 'excelExport');

        }
      })
    }),

// ************************** REPORTS ROUTER **********************************
    navReports: Ember.Route.extend({
      route: '/reports',
      connectOutlets: function(router, context) {
        router.get('applicationController').connectOutlet('navReports');
        FLOW.surveyGroupControl.populate();
        FLOW.set('showRawDataReportApplet', false);
        router.set('navigationController.selected', 'navReports');
      }
    }),

// ************************** MAPS ROUTER **********************************
    navMaps: Ember.Route.extend({
      route: '/maps',
      connectOutlets: function(router, context) {
        router.get('applicationController').connectOutlet('navMaps');
        router.set('navigationController.selected', 'navMaps');
      }
    }),
    
// ************************** USERS ROUTER **********************************
    navUsers: Ember.Route.extend({
      route: '/users',
      connectOutlets: function(router, context) {
        router.get('applicationController').connectOutlet('navUsers');
        router.set('navigationController.selected', 'navUsers');
      }
    }),

// ************************** ADMIN ROUTER **********************************
    navAdmin: Ember.Route.extend({
      route: '/admin',
      connectOutlets: function(router, context) {
        router.get('applicationController').connectOutlet('navAdmin');
        router.set('navigationController.selected', 'navAdmin');
      }
    })
  })
});
