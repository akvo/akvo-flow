require('akvo-flow/core-common');

FLOW.Router = Ember.Router.extend({
  enableLogging: true,
  loggedIn: false,
  location: 'none',
  root: Ember.Route.extend({
    doNavMaps(router) {
      router.transitionTo('navMaps');
    },
    index: Ember.Route.extend({
      route: '/',
      redirectsTo: 'navMaps',
    }),

    // ************************** MAPS ROUTER **********************************
    navMaps: Ember.Route.extend({
      route: '/maps',
      connectOutlets(router) {
        router.get('applicationController').connectOutlet('navMaps');
      },
    }),
  }),
});
