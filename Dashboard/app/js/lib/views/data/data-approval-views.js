import template from '../../mixins/template';

FLOW.DataApprovalView = Ember.View.extend({
  /*
     * This is a wrapper template for the data approval tab.
     * It provides a container view for the two views,
     *  1. list approvals
     *  2. edit approval steps
     * that shall be displayed under the Data > Data Approval tab
     * It has been created to avoid mixing the data approval views
     * and controllers with the data tab views and controllers
     */
  template: Ember.Handlebars.compile('{{outlet approvalMain}}'),
});

FLOW.ApprovalGroupListView = Ember.View.extend(template('navData/data-approval-group-list'));

FLOW.ApprovalGroupView = Ember.View.extend(template('navData/data-approval-group'), {

  approvalTypeOptions: [{ label: Ember.String.loc('_ordered'), optionValue: 'ordered' },
    { label: Ember.String.loc('_unordered'), optionValue: 'unordered' }],
});


FLOW.ApprovalStepsView = Ember.View.extend(template('navData/data-approval-steps'));
