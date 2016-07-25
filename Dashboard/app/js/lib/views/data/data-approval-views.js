FLOW.DataApprovalView = Ember.View.extend({
    template: Ember.Handlebars.compile('{{outlet approvalMain}}'),
});

FLOW.ApprovalGroupView = Ember.View.extend({
    templateName: 'navData/data-approval-group'
});


FLOW.ApprovalStepsView = Ember.View.extend({
    templateName: 'navData/data-approval-step',

    approvalGroup: null,

    approvalTypeOptions: [{label: Ember.String.loc('_ordered'), value: true},
                          {label: Ember.String.loc('_unordered'), value: false}],
});