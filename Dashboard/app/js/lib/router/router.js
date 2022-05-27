// ***********************************************//
//                 Router
// ***********************************************//
import { trackPageView } from 'akvo-flow/analytics';

require('akvo-flow/core-common');

FLOW.Router = Ember.Router.extend({
  enableLogging: true,
  loggedIn: false,
  location: 'none',
  // 'hash'or 'none' for URLs

  resetState() {
    // We could have unsaved changes
    FLOW.store.commit();

    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedSurveyGroup', null);
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.selectedControl.set('selectedQuestion', null);
    FLOW.selectedControl.set('selectedCascadeResource', null);
    FLOW.selectedControl.set('cascadeImportNumLevels', null);
    FLOW.selectedControl.set('cascadeImportIncludeCodes', null);
    FLOW.selectedControl.set('surveyAssignmentTransaction', null);
    FLOW.surveyControl.set('content', null);
    FLOW.questionControl.set('OPTIONcontent', null);
    FLOW.metaControl.set('since', null);
  },

  root: Ember.Route.extend({
    doNavSurveys(router) {
      router.transitionTo('navSurveys.index');
    },
    doNavDevices(router) {
      router.transitionTo('navDevices.index');
    },
    doNavData(router) {
      router.transitionTo('navData.index');
    },
    doNavResources(router) {
      router.transitionTo('navResources.index');
    },
    doNavMaps(router) {
      router.transitionTo('navMaps');
    },
    doNavUsers(router) {
      router.transitionTo('navUsers');
    },
    doNavMessages(router) {
      router.transitionTo('navMessages');
    },
    doNavStats(router) {
      router.transitionTo('navStats.index');
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
      redirectsTo: 'navSurveys.index',
    }),

    // ******************* SURVEYS ROUTER ********************
    navSurveys: Ember.Route.extend({
      route: '/surveys',
      connectOutlets(router) {
        router.get('applicationController').connectOutlet('navSurveys');
        router.set('navigationController.selected', 'navSurveys');
      },

      doNewSurvey(router) {
        router.transitionTo('navSurveys.navSurveysNew');
      },

      doEditSurvey(router, event) {
        FLOW.selectedControl.set('selectedSurvey', event.context);
        router.transitionTo('navSurveys.navSurveysEdit.index');
      },

      doSurveysMain(router) {
        FLOW.selectedControl.set('selectedQuestion', null);
        router.transitionTo('navSurveys.navSurveysMain');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'navSurveysMain',
      }),

      navSurveysMain: Ember.Route.extend({
        route: '/main',
        connectOutlets(router) {
          router.get('navSurveysController').connectOutlet({
            name: 'navSurveysMain',
          });
          FLOW.projectControl.populate();
          FLOW.surveyControl.populateAll();
          FLOW.cascadeResourceControl.populate();
          FLOW.projectControl.set('currentProject', null);
          FLOW.projectControl.set('newlyCreated', null);
          FLOW.selectedControl.set('selectedQuestionGroup', null);
          FLOW.selectedControl.set('selectedSurvey', null);
          FLOW.selectedControl.set('selectedQuestion', null);
          FLOW.questionControl.set('OPTIONcontent', null);

          trackPageView('Folder & Survey Page');
        },
      }),

      navSurveysNew: Ember.Route.extend({
        route: '/new',
        connectOutlets(router) {
          const newSurvey = FLOW.store.createRecord(FLOW.Survey, {
            name: '',
            defaultLanguageCode: 'en',
            requireApproval: false,
            status: 'NOT_PUBLISHED',
            surveyGroupId: FLOW.selectedControl.selectedSurveyGroup.get('keyId'),
            version: '1.0',
          });

          FLOW.selectedControl.set('selectedSurvey', newSurvey);
          router.transitionTo('navSurveys.navSurveysEdit.index');
        },
      }),

      navSurveysEdit: Ember.Route.extend({
        route: '/edit',
        connectOutlets(router) {
          router.get('navSurveysController').connectOutlet({
            name: 'navSurveysEdit',
          });
          // all questions should be closed when we enter
          FLOW.selectedControl.set('selectedQuestion', null);
        },

        doEditQuestions(router) {
          router.transitionTo('navSurveys.navSurveysEdit.editQuestions');
        },

        index: Ember.Route.extend({
          route: '/',
          redirectsTo: 'editQuestions',
        }),

        manageNotifications: Ember.Route.extend({
          route: '/notifications',
          connectOutlets(router) {
            router.get('navSurveysEditController').connectOutlet({
              name: 'manageNotifications',
            });
            FLOW.notificationControl.populate();
          },
        }),

        manageTranslations: Ember.Route.extend({
          route: '/translations',
          connectOutlets(router) {
            router.get('navSurveysEditController').connectOutlet({
              name: 'manageTranslations',
            });
            FLOW.translationControl.populate();
          },
        }),

        editQuestions: Ember.Route.extend({
          route: '/questions',
          connectOutlets(router) {
            router.get('navSurveysEditController').connectOutlet({
              name: 'editQuestions',
            });
          },
        }),
      }),
    }),

    //* ********************* DEVICES ROUTER *******************
    navDevices: Ember.Route.extend({
      route: '/devices',
      connectOutlets(router) {
        router.get('applicationController').connectOutlet('navDevices');
        router.set('navigationController.selected', 'navDevices');
      },

      doCurrentDevices(router) {
        router.transitionTo('navDevices.currentDevices');
      },

      doAssignSurveysOverview(router) {
        router.transitionTo('navDevices.assignSurveysOverview');
      },

      doEditSurveysAssignment(router) {
        router.transitionTo('navDevices.editSurveysAssignment');
      },

      doSurveyBootstrap(router) {
        router.transitionTo('navDevices.surveyBootstrap');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'currentDevices',
      }),

      currentDevices: Ember.Route.extend({
        route: '/current-devices',
        connectOutlets(router) {
          router.get('navDevicesController').connectOutlet('currentDevices');
          router.resetState();
          FLOW.deviceGroupControl.populate();
          FLOW.deviceControl.populate();
          router.set('devicesSubnavController.selected', 'currentDevices');

          trackPageView('Devices Page');
        },
      }),

      assignSurveysOverview: Ember.Route.extend({
        route: '/assign-surveys',
        connectOutlets(router) {
          router.get('navDevicesController').connectOutlet('assignSurveysOverview');
          FLOW.surveyAssignmentControl.populate();
          router.set('devicesSubnavController.selected', 'assignSurveys');
          trackPageView('Assignments Page');
        },
      }),

      editSurveysAssignment: Ember.Route.extend({
        route: '/assign-surveys',
        connectOutlets(router) {
          router.get('navDevicesController').connectOutlet('editSurveyAssignment');

          router.set('devicesSubnavController.selected', 'assignSurveys');
        },
      }),

      surveyBootstrap: Ember.Route.extend({
        route: '/manual-transfer',
        connectOutlets(router) {
          router.get('navDevicesController').connectOutlet('surveyBootstrap');
          router.set('devicesSubnavController.selected', 'surveyBootstrap');
          trackPageView('Manual Survey Transfer Page');
        },
      }),
    }),

    // ******************* DATA ROUTER ***********************
    navData: Ember.Route.extend({
      route: '/data',
      connectOutlets(router) {
        router.get('applicationController').connectOutlet('navData');
        router.set('navigationController.selected', 'navData');
      },

      doInspectData(router) {
        router.transitionTo('navData.inspectData');
      },
      doBulkUpload(router) {
        router.transitionTo('navData.bulkUpload');
      },
      doBulkUploadImages(router) {
        router.transitionTo('navData.bulkUploadImages');
      },
      doDataCleaning(router) {
        router.transitionTo('navData.dataCleaning');
      },
      doMonitoringData(router) {
        router.transitionTo('navData.monitoringData');
      },

      doReportsList(router) {
        router.transitionTo('navData.reportsList');
      },

      doExportReports(router) {
        router.transitionTo('navData.exportReports');
      },

      doChartReports(router) {
        router.transitionTo('navData.chartReports');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'inspectData',
      }),

      inspectData: Ember.Route.extend({
        route: '/inspectdata',
        connectOutlets(router) {
          router.get('navDataController').connectOutlet('inspectData');
          router.set('datasubnavController.selected', 'inspectData');
          router.resetState();

          trackPageView('Inspect Data Page');
        },
      }),

      bulkUpload: Ember.Route.extend({
        route: '/bulkupload',
        connectOutlets(router) {
          router.get('navDataController').connectOutlet('bulkUpload');
          router.set('datasubnavController.selected', 'bulkUpload');

          trackPageView('Bulk Upload Data Page');
        },
      }),

      bulkUploadImages: Ember.Route.extend({
        route: '/bulkuploadimages',
        connectOutlets(router) {
          router.get('navDataController').connectOutlet('bulkUploadImages');
          router.set('datasubnavController.selected', 'bulkUploadImages');

          trackPageView('Bulk Upload Images Page');
        },
      }),

      dataCleaning: Ember.Route.extend({
        route: '/datacleaning',
        connectOutlets(router) {
          router.get('navDataController').connectOutlet('dataCleaning');
          router.set('datasubnavController.selected', 'dataCleaning');
          router.resetState();

          trackPageView('Data Cleaning Page');
        },
      }),

      monitoringData: Ember.Route.extend({
        route: '/monitoringdata',
        connectOutlets(router) {
          router.get('navDataController').connectOutlet('monitoringData');
          router.set('datasubnavController.selected', 'monitoringData');
          router.resetState();

          trackPageView('Monitoring Datapoints Page');
        },
      }),

      reportsList: Ember.Route.extend({
        route: '/reportslist',
        connectOutlets(router) {
          // if landing on tab, show reports list first
          router.get('navDataController').connectOutlet('reportsList');
          router.set('datasubnavController.selected', 'exportReports');
          router.resetState();

          trackPageView('Data Exports Page');
        },
      }),

      exportReports: Ember.Route.extend({
        route: '/exportreports',
        connectOutlets(router) {
          router.get('navDataController').connectOutlet('exportReports');
          router.set('datasubnavController.selected', 'exportReports');
          router.resetState();
        },
      }),

      chartReports: Ember.Route.extend({
        route: '/chartreports',
        connectOutlets(router) {
          router.resetState();
          router.get('navDataController').connectOutlet('chartReports');
          router.set('datasubnavController.selected', 'chartReports');

          trackPageView('Data Charts Page');
        },
      }),
    }),

    // ************************** RESOURCES ROUTER **********************************
    navResources: Ember.Route.extend({
      route: '/resources',
      connectOutlets(router) {
        router.get('applicationController').connectOutlet('navResources');
        router.set('navigationController.selected', 'navResources');
      },

      doCascadeResources(router) {
        router.transitionTo('navResources.cascadeResources');
      },

      doDataApproval(router) {
        router.transitionTo('navResources.dataApproval.listApprovalGroups');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'cascadeResources',
      }),

      cascadeResources: Ember.Route.extend({
        route: '/cascaderesources',
        connectOutlets(router) {
          router.get('navResourcesController').connectOutlet('cascadeResources');
          router.set('resourcesSubnavController.selected', 'cascadeResources');
          FLOW.cascadeResourceControl.populate();

          trackPageView('Cascade Resources Page');
        },
      }),

      dataApproval: Ember.Route.extend({
        route: '/dataapproval',

        connectOutlets(router) {
          router.get('navResourcesController').connectOutlet('dataApproval');
          router.set('resourcesSubnavController.selected', 'approvalGroup');

          trackPageView('Data Approval Page');
        },

        doAddApprovalGroup(router) {
          router.get('approvalGroupController').add();
          router.get('approvalStepsController').loadByGroupId();
          router.transitionTo('navResources.dataApproval.editApprovalGroup');
        },

        doEditApprovalGroup(router, event) {
          const groupId = event.context.get('keyId');
          const lastLoadedGroup = router.get('approvalGroupController').get('content');
          if (!lastLoadedGroup || lastLoadedGroup.get('keyId') !== groupId) {
            router.get('approvalGroupController').load(groupId);
            router.get('approvalStepsController').loadByGroupId(groupId);
          }
          router.transitionTo('navResources.dataApproval.editApprovalGroup');
        },

        doSaveApprovalGroup(router) {
          router.get('approvalGroupController').save();
          router.transitionTo('navResources.dataApproval.listApprovalGroups');
        },

        doCancelEditApprovalGroup(router) {
          router.get('approvalGroupController').cancel();
          router.transitionTo('navResources.dataApproval.listApprovalGroups');
        },

        doDeleteApprovalGroup(router, event) {
          const group = event.context;
          router.get('approvalGroupListController').delete(group);
        },

        // default route for dataApproval tab
        listApprovalGroups: Ember.Route.extend({
          route: '/list',

          connectOutlets(router) {
            router.get('dataApprovalController').connectOutlet('approvalMain', 'approvalGroupList');
            const approvalList = router.get('approvalGroupListController');
            if (!approvalList.get('content')) {
              approvalList.set('content', FLOW.ApprovalGroup.find());
            }
          },
        }),

        editApprovalGroup: Ember.Route.extend({
          route: '/approvalsteps',

          connectOutlets(router) {
            router.get('dataApprovalController').connectOutlet('approvalMain', 'approvalGroup');
            router
              .get('approvalGroupController')
              .connectOutlet('approvalStepsOutlet', 'approvalSteps');
          },
        }),
      }),
    }),

    // ************************** MAPS ROUTER **********************************
    navMaps: Ember.Route.extend({
      route: '/maps',
      connectOutlets(router) {
        FLOW.selectedControl.set('selectedSurveyGroup', null);
        router.get('applicationController').connectOutlet('navMaps');
        router.set('navigationController.selected', 'navMaps');

        trackPageView('Maps Page');
      },
    }),

    // ************************** USERS ROUTER **********************************
    navUsers: Ember.Route.extend({
      route: '/users',
      connectOutlets(router) {
        router.get('applicationController').connectOutlet('navUsers');
        router.set('navigationController.selected', 'navUsers');

        trackPageView('Users Page');
      },
    }),

    // ************************** MESSAGES ROUTER **********************************
    navMessages: Ember.Route.extend({
      route: '/users',
      connectOutlets(router) {
        router.get('applicationController').connectOutlet('navMessages');
        router.set('navigationController.selected', 'navMessages');
        FLOW.messageControl.populate();
        router.resetState();

        trackPageView('Messages Page');
      },
    }),

    // ************************** STATS ROUTER **********************************
    navStats: Ember.Route.extend({
      route: '/stats',

      doNewStats(router) {
        router.transitionTo('navStats.newStats');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'statsLists',
      }),

      statsLists: Ember.Route.extend({
        route: '/stats-lists',
        connectOutlets(router) {
          router.get('applicationController').connectOutlet('stats');
          FLOW.router.reportsController.populate();
          router.set('navigationController.selected', 'navStats');

          trackPageView('Stats Page');
        },
      }),

      newStats: Ember.Route.extend({
        route: '/new-stats',
        connectOutlets(router) {
          router.get('applicationController').connectOutlet('newStats');
          router.set('navigationController.selected', 'navStats');
        },
      }),
    }),
  }),
});
