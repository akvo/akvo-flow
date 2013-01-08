FLOW.NotificationsView = Ember.View.extend({
  addNotification: function () {

  },

  cancelNotification: function () {

  },

  closeNotifications: function (router,event) {
    FLOW.router.transitionTo('navSurveys.navSurveysEdit.editQuestions');
  },

  removeNotifications: function () {

  }
});