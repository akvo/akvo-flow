require('akvo-flow/core-common');

FLOW.Router = Ember.Router.extend({
  enableLogging: true,
  loggedIn: false,
  location: 'none',
  root: Ember.Route.extend({
    doNavMaps: function(router, context) {
      router.transitionTo('navMaps');
    },
    index: Ember.Route.extend({
      route: '/',
      redirectsTo: 'navMaps'
    }),

    // ************************** MAPS ROUTER **********************************
    navMaps: Ember.Route.extend({
      route: '/maps',
      connectOutlets: function(router, context) {
        router.get('applicationController').connectOutlet('navMaps');
        router.set('navigationController.selected', 'navMaps');
      }
    })
  })
});