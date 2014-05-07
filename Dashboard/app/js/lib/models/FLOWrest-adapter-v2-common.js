/*global DS*/
var get = Ember.get,
  set = Ember.set;

DS.FLOWRESTAdapter = DS.RESTAdapter.extend({
  serializer: DS.RESTSerializer.extend({
    primaryKey: function (type) {
      return "keyId";
    },
    keyForAttributeName: function (type, name) {
      return name;
    }
  }),

  sideload: function (store, type, json, root) {
    var msg, status, metaObj;
    this._super(store, type, json, root);
    // only change metaControl info if there is actual meta info in the server response
    // and if it does not come from a delete action. We detect this by looking if num == null
    metaObj = this.extractMeta(json);
    if (metaObj && !Ember.none(metaObj.message)) {
      if (type == FLOW.SurveyInstance && !Ember.none(this.extractMeta(json).num)) {
        FLOW.metaControl.set('numSILoaded', this.extractMeta(json).num);
        FLOW.metaControl.set('since', this.extractMeta(json).since);
        FLOW.metaControl.set('num', this.extractMeta(json).num);
      }
      msg = this.extractMeta(json).message;
      status = this.extractMeta(json).status;
      keyId = this.extractMeta(json).keyId;

      if (msg.indexOf('_') === 0) { // Response is a translatable message
        msg = Ember.String.loc(msg);
      }
      FLOW.metaControl.set('message', msg);
      FLOW.metaControl.set('status', status);
      FLOW.metaControl.set('keyId', keyId);
      FLOW.savingMessageControl.numLoadingChange(-1);
      FLOW.savingMessageControl.set('areSavingBool', false);

      if (status === 'preflight-delete-question') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.questionControl.deleteQuestion(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_question'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_question_text'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
        }
        return;
      }

      if (status === 'preflight-delete-survey') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.surveyControl.deleteSurvey(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_survey'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_survey_text'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
        }
        return;
      }

      if (status === 'preflight-delete-surveygroup') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.surveyGroupControl.deleteSurveyGroup(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_surveygroup'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_surveygroup_text'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
        }
        return;
      }

      if (this.extractMeta(json).status === 'failed' || FLOW.metaControl.get('message') !== '') {
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', '' /*Ember.String.loc('_action_failed')*/ ); //FIXME
        FLOW.dialogControl.set('message', FLOW.metaControl.get('message'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
      }
    }
  },

  ajax: function (url, type, hash) {
    this._super(url, type, hash);
    if (type == "GET") {
      FLOW.savingMessageControl.numLoadingChange(1);
    }
  },

  didFindRecord: function (store, type, json, id) {
    this._super(store, type, json, id);
    FLOW.savingMessageControl.numLoadingChange(-1);
  },

  didFindAll: function (store, type, json) {
    this._super(store, type, json);
    FLOW.savingMessageControl.numLoadingChange(-1);
  },

  didFindQuery: function (store, type, json, recordArray) {
    this._super(store, type, json, recordArray);
    FLOW.savingMessageControl.numLoadingChange(-1);
  },

  // adapted from standard ember rest_adapter
  // includes 'bulk' in the POST call, to allign
  // with updateRecords and deleteRecords behaviour.
  createRecords: function (store, type, records) {
    if (get(this, 'bulkCommit') === false) {
      return this._super(store, type, records);
    }

    var root = this.rootForType(type),
      plural = this.pluralize(root);

    var data = {};
    data[plural] = [];
    records.forEach(function (record) {
      data[plural].push(this.serialize(record, {
        includeId: true
      }));
    }, this);

    this.ajax(this.buildURL(root, 'bulk'), "POST", {
      data: data,
      context: this,
      success: function (json) {
        this.didCreateRecords(store, type, records, json);
      }
    });
  }
});
