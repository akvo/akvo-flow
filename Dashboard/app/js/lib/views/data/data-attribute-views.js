FLOW.ManageAttributesTableView = FLOW.View.extend({
  showAddAttributeDialogBool: false,
  showEditAttributeDialogBool: false,
  newAttributeName: null,
  newAttributeGroup: null,
  newAttributeType: null,

  showAddAttributeDialog: function () {
    this.set('showAddAttributeDialogBool', true);
  },

  doAddAttribute: function () {
    if (this.get('newAttributeName') !== null && this.get('newAttributeType') !== null) {
      FLOW.store.createRecord(FLOW.Metric, {
        "name": this.get('newAttributeName'),
        "group": this.get('newAttributeGroup'),
        "valueType": this.newAttributeType.get('value')
      });
      FLOW.store.commit();
    }
    this.set('showAddAttributeDialogBool', false);
  },

  cancelAddAttribute: function () {
    this.set('showAddAttributeDialogBool', false);
  },

  showEditAttributeDialog: function (event) {
    var attrType = null;

    FLOW.editControl.set('editAttributeName', event.context.get('name'));
    FLOW.editControl.set('editAttributeGroup', event.context.get('group'));
    FLOW.editControl.set('editAttributeId', event.context.get('keyId'));

    FLOW.attributeTypeControl.get('content').forEach(function (item) {
      if (item.get('value') == event.context.get('valueType')) {
        attrType = item;
      }
    });

    FLOW.editControl.set('editAttributeType', attrType);
    this.set('showEditAttributeDialogBool', true);
  },

  doEditAttribute: function () {
    var attribute;
    attribute = FLOW.store.find(FLOW.Metric, FLOW.editControl.get('editAttributeId'));
    attribute.set('name', FLOW.editControl.get('editAttributeName'));
    attribute.set('group', FLOW.editControl.get('editAttributeGroup'));

    if (FLOW.editControl.editAttributeType !== null) {
      attribute.set('valueType', FLOW.editControl.editAttributeType.get('value'));
    }

    FLOW.store.commit();
    this.set('showEditAttributeDialogBool', false);
  },

  cancelEditAttribute: function () {
    this.set('showEditAttributeDialogBool', false);
  }
});

FLOW.AttributeView = FLOW.View.extend({
  tagName: 'span',

  deleteAttribute: function () {
    var attrDeleteId, attribute;
    attrDeleteId = this.content.get('keyId');
    attribute = FLOW.store.find(FLOW.Metric, attrDeleteId);
    attribute.deleteRecord();
    FLOW.store.commit();
  }
});
