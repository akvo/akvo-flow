/* global DS */
const { get } = Ember;

const makeEventEmitter = () => ({
  emit(event, ...args) {
    let callbacks = this.events[event] || []
    for (let i = 0, length = callbacks.length; i < length; i++) {
      callbacks[i](...args)
    }
  },
  events: {},
  on(event, cb) {
    if (this.events[event]) {
      this.events[event].push(cb);
    } else {
      this.events[event] = [cb];
    }
    return () => {
      this.events[event] = this.events[event] ? this.events[event].filter(i => cb !== i) : null;
    };
  },
})

DS.FLOWRESTAdapter = DS.RESTAdapter.extend({
  init() {
    this._super();
    this.emitter = makeEventEmitter()
  },

  on(event, callback) {
    return this.emitter.on(event, callback)
  },

  serializer: DS.RESTSerializer.extend({
    primaryKey() {
      return 'keyId';
    },
    keyForAttributeName(type, name) {
      return name;
    },
  }),

  sideload(store, type, json, root) {
    this._super(store, type, json, root);

    this.setQueryCursor(type, json);

    // only change metaControl info if there is actual meta info in the server response
    // and if it does not come from a delete action. We detect this by looking if num == null
    const metaObj = this.extractMeta(json);
    if (metaObj && !Ember.none(metaObj.message)) {
      if (
        type == FLOW.SurveyInstance
        || (type == FLOW.SurveyedLocale && !Ember.none(this.extractMeta(json).num))
      ) {
        FLOW.metaControl.set(type == FLOW.SurveyInstance ? 'numSILoaded' : 'numSLLoaded', this.extractMeta(json).num);
        FLOW.metaControl.set('since', this.extractMeta(json).since);
        FLOW.metaControl.set('num', this.extractMeta(json).num);
        FLOW.metaControl.set('cursorType', type);
      }
      let msg = this.extractMeta(json).message;
      const { status } = this.extractMeta(json);
      const { keyId } = this.extractMeta(json);

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

      if (status === 'preflight-delete-questiongroup') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.questionGroupControl.deleteQuestionGroup(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_questiongroup'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_questiongroup_text'));
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
        FLOW.dialogControl.set('header', '' /* Ember.String.loc('_action_failed') */); // FIXME
        FLOW.dialogControl.set('message', FLOW.metaControl.get('message'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
      }
    }
  },

  /*  Process the cursor returned by the query. The cursor is used for pagination requests
      and is based on the type of entities queried */
  setQueryCursor(type, json) {
    let cursorArray;
    if (type === FLOW.SurveyedLocale) {
      cursorArray = FLOW.router.surveyedLocaleController.get('sinceArray');
    } else if (type === FLOW.SurveyInstance) {
      cursorArray = FLOW.surveyInstanceControl.get('sinceArray');
    } else {
      return;
    }

    const cursorStart = this.extractSince(json);
    if (!cursorStart) {
      return;
    }

    const cursorIndex = cursorArray.indexOf(cursorStart);
    if (cursorIndex === -1) {
      cursorArray.pushObject(cursorStart);
    } else {
      // drop all cursors after the current one
      cursorArray.splice(cursorIndex + 1, cursorArray.length);
    }

    if (type === FLOW.SurveyedLocale) {
      FLOW.router.surveyedLocaleController.set('sinceArray', cursorArray);
    } else if (type === FLOW.SurveyInstance) {
      FLOW.surveyInstanceControl.set('sinceArray', cursorArray);
    }
  },

  ajax(url, type, hash) {
    if (type === 'GET' && url.indexOf('rest/survey_groups/0') >= 0) {
      // Don't fetch the root folder. It doesn't exist.
      return;
    }

    this._super(url, type, hash);
    if (type == 'GET') {
      if (url.indexOf('rest/survey_groups') >= 0) {
        FLOW.projectControl.set('isLoading', true);
      }
      FLOW.savingMessageControl.numLoadingChange(1);
    }
  },

  find(store, type, id) {
    const root = this.rootForType(type);

    this.ajax(this.buildURL(root, id), 'GET', {
      success(json) {
        if (type === FLOW.SurveyGroup) {
          if (json.survey_group) {
            this.didFindRecord(store, type, json, id);
          } else {
            // missing survey so no further action
            FLOW.projectControl.set('isLoading', false);
            FLOW.savingMessageControl.numLoadingChange(-1);
          }
        } else {
          this.didFindRecord(store, type, json, id);
        }
      },
      error() {
        // TODO: Handle various error response codes
        if (type === FLOW.SurveyGroup) {
          FLOW.projectControl.set('isLoading', false);
        }
        FLOW.savingMessageControl.numLoadingChange(-1);
      },
    });
  },

  didFindRecord(store, type, json, id) {
    this._super(store, type, json, id);
    if (type === FLOW.SurveyGroup) {
      FLOW.projectControl.set('isLoading', false);
    }
    FLOW.savingMessageControl.numLoadingChange(-1);
  },

  didFindAll(store, type, json) {
    this._super(store, type, json);
    this.emitter.emit('didFindAll', type)
    if (type === FLOW.SurveyGroup) {
      FLOW.projectControl.set('isLoading', false);
    }
    FLOW.savingMessageControl.numLoadingChange(-1);
  },

  didFindQuery(store, type, json, recordArray) {
    this._super(store, type, json, recordArray);
    if (type === FLOW.SurveyGroup) {
      FLOW.projectControl.set('isLoading', false);
    }
    FLOW.savingMessageControl.numLoadingChange(-1);
  },

  // adapted from standard ember rest_adapter
  // includes 'bulk' in the POST call, to allign
  // with updateRecords and deleteRecords behaviour.
  createRecords(store, type, records) {
    // do not bulk commit when creating questions and question groups
    if (FLOW.questionControl.get('bulkCommit')) {
      this.set('bulkCommit', false);
    }

    if (get(this, 'bulkCommit') === false) {
      return this._super(store, type, records);
    }

    const root = this.rootForType(type);
    const plural = this.pluralize(root);

    const data = {};
    data[plural] = [];
    records.forEach(function (record) {
      data[plural].push(this.serialize(record, {
        includeId: true,
      }));
    }, this);

    this.ajax(this.buildURL(root, 'bulk'), 'POST', {
      data,
      context: this,
      success(json) {
        this.didCreateRecords(store, type, records, json);
      },
    });
  },


  updateRecords(store, type, records) {
    // if updating questions and question groups ordering, enable bulkCommit
    if (FLOW.questionControl.get('bulkCommit')) {
      this.set('bulkCommit', true);
    }
    this._super(store, type, records);
  },

  deleteRecords(store, type, records) {
    // do not bulk commit when deleting questions and question groups
    if (FLOW.questionControl.get('bulkCommit')) {
      this.set('bulkCommit', false);
    }
    this._super(store, type, records);
  },
});
