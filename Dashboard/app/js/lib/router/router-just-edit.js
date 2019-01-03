// ***********************************************//
//                 Router
// ***********************************************//
require('akvo-flow/core-common');

FLOW.Router = Ember.Router.extend({
  enableLogging: true,
  loggedIn: false,
  location: 'none',
  //'hash'or 'none' for URLs

  resetState: function () {
    // We could have unsaved changes
    FLOW.store.commit();

    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedSurveyGroup', null);
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.selectedControl.set('selectedQuestion', null);
    FLOW.selectedControl.set('selectedCascadeResource', null);
    FLOW.selectedControl.set('cascadeImportNumLevels', null);
    FLOW.selectedControl.set('cascadeImportIncludeCodes', null);
    FLOW.surveyControl.set('content', null);
    FLOW.questionControl.set('OPTIONcontent', null);
    FLOW.metaControl.set('since', null);
  },

  root: Ember.Route.extend({

    // non-working code for transitioning to navHome at first entry of the app
    //    setup: function(router){
    //      router.send("goHome");
    //    },
    //    goHome:function(router){
    //      router.transitionTo('navHome');
    //    },
    index: Ember.Route.extend({
      route: '/',
      redirectsTo: 'navSurveys.index'
    }),

    // ******************* SURVEYS ROUTER ********************
    navSurveys: Ember.Route.extend({
      route: '/surveys',
      connectOutlets: function (router, event) {
        router.get('applicationController').connectOutlet('navSurveys');
        router.set('navigationController.selected', 'navSurveys');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'navSurveysEditMine'
      }),

      navSurveysEditMine: Ember.Route.extend({
        route: '/jump',
        connectOutlets: function (router, event) {
          router.get('navSurveysController').connectOutlet({
            name: 'navSurveysMain'
          });


          var the_url = new URL(window.location.href);
          var survey_id = the_url.searchParams.get("survey_id");
          FLOW.projectControl.set('currentProject', FLOW.store.findById(FLOW.SurveyGroup, survey_id));
          FLOW.surveyControl.populateAll();
          FLOW.cascadeResourceControl.populate();
          FLOW.projectControl.set('newlyCreated', null);

          console.log("current project",FLOW.projectControl.currentProject, FLOW.projectControl.currentProject.get('keyId'));

          FLOW.selectedControl.set('selectedSurveyGroup',FLOW.projectControl.currentProject);

          FLOW.selectedControl.set('selectedQuestion', null);
          FLOW.questionControl.set('OPTIONcontent', null);
          FLOW.selectedControl.set('selectedQuestion', null);
        }}),

      navSurveysEdit: Ember.Route.extend({
        route: '/edit',
        connectOutlets: function (router, event) {
          router.get('navSurveysController').connectOutlet({
            name: 'navSurveysEdit'
          });
        },

        doEditQuestions: function (router, event) {
          router.transitionTo('navSurveys.navSurveysEdit.editQuestions');
        },

        index: Ember.Route.extend({
          route: '/',
          redirectsTo: 'editQuestions'
        }),

        manageNotifications: Ember.Route.extend({
          route: '/notifications',
          connectOutlets: function (router, event) {
            router.get('navSurveysEditController').connectOutlet({
              name: 'manageNotifications'
            });
            FLOW.notificationControl.populate();
          }
        }),

        manageTranslations: Ember.Route.extend({
          route: '/translations',
          connectOutlets: function (router, event) {
                      alert("hisadfdasfwsww");

            router.get('navSurveysEditController').connectOutlet({
              name: 'manageTranslations'
            });
            FLOW.translationControl.populate();
          }
        }),

        editQuestions: Ember.Route.extend({
          route: '/questions',
          connectOutlets: function (router, event) {
            alert("hisadfdasf");
            router.get('navSurveysEditController').connectOutlet({
              name: 'editQuestions'
            });

          }
        })
      })
    })

  })
});
