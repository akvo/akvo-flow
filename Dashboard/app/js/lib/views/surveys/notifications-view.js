import template from '../../mixins/template';

FLOW.NotificationsView = FLOW.View.extend(template('navSurveys/manage-notifications'), {
  notificationOption: null,
  notificationType: null,
  expiryDate: null,
  notificationDestination: null,
  optionEmpty: false,
  typeEmpty: false,
  destinationEmpty: false,
  dateEmpty: false,

  addNotification() {
    let date;

    this.set('optionEmpty', Ember.none(this.get('notificationOption')));
    this.set('typeEmpty', Ember.none(this.get('notificationType')));
    this.set('destinationEmpty', Ember.none(this.get('notificationDestination')));
    this.set('dateEmpty', Ember.none(this.get('expiryDate')));

    if (Ember.none(this.get('expiryDate'))) {
      date = null;
    } else {
      date = Date.parse(this.get('expiryDate'));
    }
    if (this.get('optionEmpty') || this.get('typeEmpty') || this.get('destinationEmpty') || this.get('dateEmpty')) {
      // do nothing
    } else {
      FLOW.store.createRecord(FLOW.NotificationSubscription, {
        notificationOption: this.notificationOption.get('value'),
        notificationType: this.notificationType.get('value'),
        expiryDate: date,
        notificationDestination: this.get('notificationDestination'),
        notificationMethod: 'EMAIL',
        entityId: FLOW.selectedControl.selectedSurvey.get('keyId'),
      });
      this.set('notificationOption', null);
      this.set('notificationType', null);
      this.set('notificationDestination', null);
      this.set('expiryDate', null);
      FLOW.store.commit();
    }
  },

  cancelNotification() {
    this.set('notificationEvent', null);
    this.set('notificationType', null);
    this.set('notificationDestination', null);
    this.set('expiryDate', null);
  },

  closeNotifications() {
    this.get('parentView').set('manageNotifications', false);
  },

  removeNotification(event) {
    const nDeleteId = event.context.get('keyId');
    const notification = FLOW.store.find(FLOW.NotificationSubscription, nDeleteId);
    notification.deleteRecord();
    FLOW.store.commit();
  },
});
