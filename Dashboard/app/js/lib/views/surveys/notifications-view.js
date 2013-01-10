FLOW.NotificationsView = Ember.View.extend({
  notificationOption: null,
  notificationType: null,
  expiryDate: null,
  notificationDestination: null,
  optionEmpty: false,
  typeEmpty: false,
  destinationEmpty: false,
  dateEmpty: false,

  addNotification: function() {
    if(this.get('notificationOption') === null) {
      this.set('optionEmpty', true);
    }
    if(this.get('notificationType') === null) {
      this.set('typeEmpty', true);
    }
    if(this.get('notificationDestination') === null) {
      this.set('destinationEmpty', true);
    }
    if(this.get('expiryDate') === null) {
      this.set('dateEmpty', true);
    }

    if(this.get('optionEmpty') || this.get('typeEmpty') || this.get('destinationEmpty') || this.get('dateEmpty')) {
      FLOW.store.createRecord(FLOW.NotificationSubscription, {
        "notificationOption": this.notificationOption.get('value'),
        "notificationType": this.notificationType.get('value'),
        "expiryDate": Date.parse(this.get('expiryDate')),
        "notificationDestination": this.get('notificationDestination'),
        "notificationMethod": "EMAIL",
        "entityId": FLOW.selectedControl.selectedSurvey.get('keyId')
      });
      FLOW.store.commit();
    }
  },

  cancelNotification: function() {
    this.set('notificationEvent', null);
    this.set('notificationType', null);
    this.set('notificationDestination', null);
    this.set('expiryDate', null);
  },

  closeNotifications: function(router, event) {
    FLOW.router.transitionTo('navSurveys.navSurveysEdit.editQuestions');
  },

  removeNotification: function(event) {
    var nDeleteId, notification;
    nDeleteId = event.context.get('keyId');

    notification = FLOW.store.find(FLOW.NotificationSubscription, nDeleteId);
    notification.deleteRecord();
    FLOW.store.commit();
  }
});