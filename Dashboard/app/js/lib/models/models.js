// ***********************************************//
//                 models and stores
// ***********************************************//
import observe from '../mixins/observe';

require('akvo-flow/core-common');
require('akvo-flow/models/store_def-common');

FLOW.BaseModel = DS.Model.extend(
  observe({
    isSaving: 'anySaving',
    isDirty: 'anySaving',
  }),
  {
    keyId: DS.attr('number'),
    savingStatus: null,

    // this method calls the checkSaving method on the savingMessageControl, which
    // checks if there are any records inflight. If yes, it sets a boolean,
    // so a saving message can be displayed. savingStatus is used to capture the
    // moment that nothing is being saved anymore, but in the previous event it was
    // so we can turn off the saving message.
    anySaving() {
      if (this.get('isSaving') || this.get('isDirty') || this.get('savingStatus')) {
        FLOW.savingMessageControl.checkSaving();
      }
      this.set('savingStatus', this.get('isSaving') || this.get('isDirty'));
    },
  }
);

FLOW.CaddisflyTestDefinition = Ember.Object.extend({
  name: null,
  multiParameter: null,
  sample: null,
  device: null,
  brand: null,
  model: null,
  uuid: null,
  results: [],
  reagents: [],
});

FLOW.CascadeResource = FLOW.BaseModel.extend({
  name: DS.attr('string', {
    defaultValue: '',
  }),
  version: DS.attr('number', {
    defaultValue: 1,
  }),
  numLevels: DS.attr('number', {
    defaultValue: 1,
  }),
  status: DS.attr('string', {
    defaultValue: 'NOT_PUBLISHED',
  }),
  levelNames: DS.attr('array', { defaultValue: [] }),
});

FLOW.CascadeNode = FLOW.BaseModel.extend({
  name: DS.attr('string', {
    defaultValue: '',
  }),
  code: DS.attr('string', {
    defaultValue: '',
  }),
  parentNodeId: DS.attr('number', {
    defaultValue: '',
  }),
  cascadeResourceId: DS.attr('number', {
    defaultValue: '',
  }),
});

FLOW.SurveyGroup = FLOW.BaseModel.extend({
  description: DS.attr('string', {
    defaultValue: '',
  }),
  name: DS.attr('string', {
    defaultValue: '',
  }),
  path: DS.attr('string', {
    defaultValue: null,
  }),
  ancestorIds: DS.attr('array', {
    defaultValue: [],
  }),
  createdDateTime: DS.attr('string', {
    defaultValue: '',
  }),
  monitoringGroup: DS.attr('boolean', {
    defaultValue: false,
  }),
  newLocaleSurveyId: DS.attr('number'),
  lastUpdateDateTime: DS.attr('string', {
    defaultValue: '',
  }),
  // the code field is used as name
  code: DS.attr('string', {
    defaultValue: '',
  }),

  parentId: DS.attr('number', {
    defaultValue: null,
  }),

  projectType: DS.attr('string', {
    defaultValue: 'PROJECT',
  }),

  privacyLevel: DS.attr('string', {
    defaultValue: 'PRIVATE',
  }),

  defaultLanguageCode: DS.attr('string', {
    defaultValue: 'en',
  }),

  published: DS.attr('boolean', {
    defaultValue: false,
  }),

  requireDataApproval: DS.attr('boolean', {
    defaultValue: false,
  }),

  dataApprovalGroupId: DS.attr('number', {
    defaultValue: null,
  }),

  surveyList: DS.attr('array', {
    defaultValue: null,
  }),
});

FLOW.Survey = FLOW.BaseModel.extend({
  defaultLanguageCode: DS.attr('string'),
  status: DS.attr('string'),
  sector: DS.attr('string'),
  code: DS.attr('string'),
  requireApproval: DS.attr('string'),
  version: DS.attr('string'),
  description: DS.attr('string'),
  name: DS.attr('string'),
  path: DS.attr('string'),
  ancestorIds: DS.attr('array'),
  pointType: DS.attr('string'),
  surveyGroupId: DS.attr('number'),
  createdDateTime: DS.attr('number'),
  lastUpdateDateTime: DS.attr('number'),

  // This attribute is used for the 'Copy Survey' functionality
  // Most of the times is `null`
  sourceId: DS.attr('number', {
    defaultValue: null,
  }),

  /* computed property that is used in the assignment edit page but never saved to backend.
  it should ideally appear as a view property. to be refactored */
  surveyGroupName: Ember.computed(function() {
    const surveyId = this.get('surveyGroupId');
    const survey = surveyId && FLOW.store.find(FLOW.SurveyGroup, surveyId);
    if (!Ember.empty(survey)) return survey.get('name');
    return '';
  }).property(''),

  allowEdit: Ember.computed(function() {
    return !this.get('isNew') && this.get('status') !== 'COPYING';
  }).property('status', 'isNew'),
});

FLOW.QuestionGroup = FLOW.BaseModel.extend({
  order: DS.attr('number'),
  name: DS.attr('string'),
  path: DS.attr('string'),
  code: DS.attr('string'),
  surveyId: DS.attr('number'),
  status: DS.attr('string'),
  sourceId: DS.attr('number', {
    defaultValue: null,
  }),
  repeatable: DS.attr('boolean', {
    defaultValue: false,
  }),
});

FLOW.Question = FLOW.BaseModel.extend({
  questionOptions: DS.hasMany('FLOW.QuestionOption'),

  allowDecimal: DS.attr('boolean', {
    defaultValue: false,
  }),
  allowMultipleFlag: DS.attr('boolean', {
    defaultValue: false,
  }),
  allowOtherFlag: DS.attr('boolean', {
    defaultValue: false,
  }),
  localeNameFlag: DS.attr('boolean', {
    defaultValue: false,
  }),
  localeLocationFlag: DS.attr('boolean', {
    defaultValue: false,
  }),
  allowSign: DS.attr('boolean', {
    defaultValue: false,
  }),
  geoLocked: DS.attr('boolean', {
    defaultValue: false,
  }),
  requireDoubleEntry: DS.attr('boolean', {
    defaultValue: false,
  }),
  collapseable: DS.attr('boolean', {
    defaultValue: false,
  }),
  immutable: DS.attr('boolean', {
    defaultValue: false,
  }),
  mandatoryFlag: DS.attr('boolean', {
    defaultValue: true,
  }),
  dependentFlag: DS.attr('boolean', {
    defaultValue: false,
  }),
  dependentQuestionAnswer: DS.attr('string'),
  dependentQuestionId: DS.attr('number'),
  maxVal: DS.attr('number', {
    defaultValue: null,
  }),
  minVal: DS.attr('number', {
    defaultValue: null,
  }),
  order: DS.attr('number'),
  cascadeResourceId: DS.attr('number'),
  caddisflyResourceUuid: DS.attr('string'),
  path: DS.attr('string'),
  questionGroupId: DS.attr('number'),
  surveyId: DS.attr('number'),
  variableName: DS.attr('string'),
  metricId: DS.attr('number'),
  text: DS.attr('string'),
  tip: DS.attr('string'),
  type: DS.attr('string', {
    defaultValue: 'FREE_TEXT',
  }),
  // This attribute is used for the 'Copy Survey' functionality
  // Most of the times is `null`
  sourceId: DS.attr('number', {
    defaultValue: null,
  }),
  allowExternalSources: DS.attr('boolean', {
    defaultValue: false,
  }),
  // Geoshape question type options
  allowPoints: DS.attr('boolean', {
    defaultValue: false,
  }),
  allowLine: DS.attr('boolean', {
    defaultValue: false,
  }),
  allowPolygon: DS.attr('boolean', {
    defaultValue: false,
  }),
});

FLOW.QuestionOption = FLOW.BaseModel.extend({
  question: DS.belongsTo('FLOW.Question'),
  order: DS.attr('number'),
  questionId: DS.attr('number'),
  text: DS.attr('string'),
  code: DS.attr('string'),
});

FLOW.DeviceGroup = FLOW.BaseModel.extend({
  code: DS.attr('string', {
    defaultValue: '',
  }),
});

FLOW.Device = FLOW.BaseModel.extend({
  didLoad() {
    let combinedName;
    if (Ember.empty(this.get('deviceIdentifier'))) {
      combinedName = 'no identifer';
    } else {
      combinedName = this.get('deviceIdentifier');
    }
    this.set('combinedName', `${combinedName} ${this.get('phoneNumber')}`);
  },
  esn: DS.attr('string', {
    defaultValue: '',
  }),
  phoneNumber: DS.attr('string', {
    defaultValue: '',
  }),
  deviceIdentifier: DS.attr('string', {
    defaultValue: '',
  }),
  gallatinSoftwareManifest: DS.attr('string'),
  lastKnownLat: DS.attr('number', {
    defaultValue: 0,
  }),
  lastKnownLon: DS.attr('number', {
    defaultValue: 0,
  }),
  lastKnownAccuracy: DS.attr('number', {
    defaultValue: 0,
  }),
  lastPositionDate: DS.attr('number', {
    defaultValue: '',
  }),
  deviceGroup: DS.attr('string', {
    defaultValue: '',
  }),
  deviceGroupName: DS.attr('string', {
    defaultValue: '',
  }),
  isSelected: false,
  combinedName: null,
});

FLOW.SurveyAssignment = FLOW.BaseModel.extend({
  name: DS.attr('string'),
  startDate: DS.attr('number'),
  endDate: DS.attr('number'),
  deviceIds: DS.attr('array'),
  formIds: DS.attr('array'),
  language: DS.attr('string'),
  surveyId: DS.attr('number'),
});

FLOW.SurveyedLocale = DS.Model.extend({
  description: DS.attr('string', {
    defaultValue: '',
  }),
  keyId: DS.attr('number'),
  latitude: DS.attr('number'),
  longitude: DS.attr('number'),
  displayName: DS.attr('string'),
  lastUpdateDateTime: DS.attr('number'),
  surveyGroupId: DS.attr('number'),
  identifier: DS.attr('string'),
  primaryKey: 'keyId',
});

FLOW.DataPointAssignment = FLOW.BaseModel.extend({
  surveyAssignmentId: DS.attr('number'),
  surveyId: DS.attr('number'),
  deviceId: DS.attr('number'),
  dataPointIds: DS.attr('array'),
});

FLOW.Placemark = FLOW.BaseModel.extend({
  latitude: DS.attr('number'),
  longitude: DS.attr('number'),
  count: DS.attr('number'),
  level: DS.attr('number'),
  surveyId: DS.attr('number'),
  collectionDate: DS.attr('number'),
});

FLOW.SurveyInstance = FLOW.BaseModel.extend({
  approvedFlag: DS.attr('string'),
  approximateLocationFlag: DS.attr('string'),
  surveyInstanceId: DS.attr('number'),
  surveyId: DS.attr('number'),
  surveyedLocaleId: DS.attr('number'),
  collectionDate: DS.attr('number'),
  surveyCode: DS.attr('string'),
  submitterName: DS.attr('string'),
  deviceIdentifier: DS.attr('string'),
  surveyedLocaleIdentifier: DS.attr('string'),
  surveyedLocaleDisplayName: DS.attr('string'),
});

FLOW.QuestionAnswer = FLOW.BaseModel.extend({
  value: DS.attr('string'),
  type: DS.attr('string'),
  oldValue: DS.attr('string'),
  surveyId: DS.attr('number'),
  collectionDate: DS.attr('number'),
  surveyInstanceId: DS.attr('number'),
  iteration: DS.attr('number'),
  questionID: DS.attr('string'), // TODO should be number?
  questionText: DS.attr('string'),
});

FLOW.ApprovalGroup = FLOW.BaseModel.extend({
  name: DS.attr('string'),
  ordered: DS.attr('boolean'),
});

FLOW.ApprovalStep = FLOW.BaseModel.extend({
  approvalGroupId: DS.attr('number'),
  order: DS.attr('number'),
  title: DS.attr('string'),
  approverUserList: DS.attr('array', {
    defaultValue: null,
  }),
});

FLOW.DataPointApproval = FLOW.BaseModel.extend({
  surveyedLocaleId: DS.attr('number'),

  approvalStepId: DS.attr('number'),

  approverUserName: DS.attr('string'),

  approvalDate: DS.attr('number'),

  status: DS.attr('string'),

  comment: DS.attr('string'),
});

FLOW.SurveyQuestionSummary = FLOW.BaseModel.extend({
  response: DS.attr('string'),
  count: DS.attr('number'),
  percentage: null,
  questionId: DS.attr('string'),
});

FLOW.User = FLOW.BaseModel.extend({
  userName: DS.attr('string'),
  emailAddress: DS.attr('string'),
  admin: DS.attr('boolean', {
    defaultValue: 0,
  }),
  superAdmin: DS.attr('boolean', {
    defaultValue: 0,
  }),
  permissionList: DS.attr('string'),
});

FLOW.Message = FLOW.BaseModel.extend({
  objectId: DS.attr('number'),
  lastUpdateDateTime: DS.attr('number'),
  userName: DS.attr('string'),
  objectTitle: DS.attr('string'),
  actionAbout: DS.attr('string'),
  shortMessage: DS.attr('string'),
});

FLOW.Action = FLOW.BaseModel.extend({});

FLOW.Translation = FLOW.BaseModel.extend(
  observe({
    'this.keyId': 'didCreateId',
  }),
  {
    didUpdate() {
      FLOW.translationControl.putSingleTranslationInList(
        this.get('parentType'),
        this.get('parentId'),
        this.get('text'),
        this.get('keyId'),
        false
      );
    },

    // can't use this at the moment, as the didCreate is fired before the id is back from the
    // ajax call
    // didCreate: function(){
    //   console.log('didCreate',this.get('keyId'));
    // FLOW.translationControl.putSingleTranslationInList(
    //   this.get('parentType'),
    //   this.get('parentId'),
    //   this.get('text'),
    //   this.get('keyId'),
    //   false
    // );
    // },

    // temporary hack to fire the didCreate event after the keyId is known
    didCreateId() {
      if (!Ember.none(this.get('keyId')) && this.get('keyId') > 0) {
        FLOW.translationControl.putSingleTranslationInList(
          this.get('parentType'),
          this.get('parentId'),
          this.get('text'),
          this.get('keyId'),
          false
        );
      }
    },

    didDelete() {
      FLOW.translationControl.putSingleTranslationInList(
        this.get('parentType'),
        this.get('parentId'),
        null,
        null,
        true
      );
    },

    parentType: DS.attr('string'),
    parentId: DS.attr('string'),
    surveyId: DS.attr('string'),
    questionGroupId: DS.attr('string'),
    text: DS.attr('string'),
    langCode: DS.attr('string'),
  }
);

FLOW.NotificationSubscription = FLOW.BaseModel.extend({
  notificationDestination: DS.attr('string'),
  notificationOption: DS.attr('string'),
  notificationMethod: DS.attr('string'),
  notificationType: DS.attr('string'),
  expiryDate: DS.attr('number'),
  entityId: DS.attr('number'),
});

FLOW.SubCountry = FLOW.BaseModel.extend({
  countryCode: DS.attr('string'),
  level: DS.attr('number'),
  name: DS.attr('string'),
  parentKey: DS.attr('number'),
  parentName: DS.attr('string'),
});

FLOW.Report = FLOW.BaseModel.extend({
  reportType: DS.attr('string'), // DATA_CLEANING/COMPREHENSIVE/...
  formId: DS.attr('number'),
  state: DS.attr('string'), // QUEUED/IN_PROGRESS/FINISHED_SUCCESS/FINISHED_ERROR
  startDate: DS.attr('string'),
  endDate: DS.attr('string'),
  message: DS.attr('string'),
  filename: DS.attr('string'),
  questionId: DS.attr('number'),
  createdDateTime: DS.attr('number'),
  lastUpdateDateTime: DS.attr('number'),
  lastCollectionOnly: DS.attr('boolean', {
    defaultValue: false,
  }),
});
