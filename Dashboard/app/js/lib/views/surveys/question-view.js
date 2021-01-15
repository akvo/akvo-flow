import { isNaN } from 'lodash';
import observe from '../../mixins/observe';
import template from '../../mixins/template';

function sortByOrder(a, b) {
  return a.get('order') - b.get('order');
}

function getOffset(el) {
  // el.offsetTop only returns the top value relative to the closest el with position relative.
  // to get the value relative to body (or the document), we need to recursively add all the
  // offsets from each parents.
  if (!el) return 0;

  return getOffset(el.offsetParent) + el.offsetTop;
}

FLOW.QuestionView = FLOW.View.extend(
  template('navSurveys/question-view'),
  observe({
    'FLOW.selectedControl.dependentQuestion': 'fillOptionList',
    'this.text': 'validateQuestionObserver',
    'FLOW.questionOptionsControl.emptyOptions': 'validateQuestionObserver',
    'this.tip': 'validateQuestionTooltipObserver',
    'this.variableName': 'validateVariableNameObserver',
    'this.selectedCaddisflyTestBrand': 'brandsObserver',
  }),
  {
    content: null,
    variableName: null,
    text: null,
    tip: null,
    type: null,
    mandatoryFlag: null,
    minVal: null,
    maxVal: null,
    allowSign: null,
    allowDecimal: null,
    allowMultipleFlag: null,
    allowOtherFlag: null,
    allowExternalSources: false,
    localeNameFlag: false,
    localeLocationFlag: false,
    geoLocked: null,
    requireDoubleEntry: null,
    dependentFlag: false,
    dependentQuestion: null,
    includeInMap: null,
    allowPoints: true,
    allowLine: true,
    allowPolygon: true,
    questionValidationFailure: false,
    questionTooltipValidationFailure: false,
    caddisflyResourceUuid: null,
    personalData: null,

    showCaddisflyTests: Ember.computed(() =>
      FLOW.router.caddisflyResourceController.get('testsFileLoaded')
    ).property('FLOW.router.caddisflyResourceController.testsFileLoaded'),

    showMetaConfig: Ember.computed(() => FLOW.Env.showMonitoringFeature).property(
      'FLOW.Env.showMonitoringFeature'
    ),

    amOpenQuestion: Ember.computed(function() {
      const selected = FLOW.selectedControl.get('selectedQuestion');
      if (selected && this.get('content')) {
        const isOpen =
          this.content.get('keyId') == FLOW.selectedControl.selectedQuestion.get('keyId');
        return isOpen;
      }
      return false;
    })
      .property('FLOW.selectedControl.selectedQuestion', 'content.keyId')
      .cacheable(),

    amQuestionPublishingError: Ember.computed(function() {
      if (!FLOW.selectedControl.get('publishingErrors')) { return false; }
      const questionGroupId = this.content._data.attributes.questionGroupId;
      const questionId = this.content._data.attributes.keyId;
      const groupPublishingErrors = FLOW.selectedControl.get('publishingErrors')[questionGroupId];
      return Boolean(groupPublishingErrors && groupPublishingErrors.find(x => x === questionId));
    }).property('FLOW.selectedControl.publishingErrors'),

    isTemplate: Ember.computed(function() {
      const surveyId = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
      return JSON.parse(FLOW.Env.templateIds).indexOf(surveyId) >= 0;
    })
      .property('FLOW.selectedControl.selectedSurveyGroup')
      .cacheable(),

    editable: Ember.computed(function() {
      const immutable = this.get('content').get('immutable');
      const isTemplate = this.get('isTemplate');
      return isTemplate || !immutable;
    })
      .property('this.isTemplate')
      .cacheable(),

    amTextType: Ember.computed(function() {
      if (this.type) {
        return this.type.get('value') == 'FREE_TEXT';
      }
      return false;
    })
      .property('this.type')
      .cacheable(),

    amOptionType: Ember.computed(function() {
      return this.type && this.type.get('value') === 'OPTION';
    }).property('this.type'),

    amNumberType: Ember.computed(function() {
      if (this.type) {
        return this.type.get('value') == 'NUMBER';
      }
      return false;
    })
      .property('this.type')
      .cacheable(),

    amBarcodeType: Ember.computed(function() {
      if (this.type) {
        return this.type.get('value') === 'SCAN';
      }
      return false;
    })
      .property('this.type')
      .cacheable(),

    amFreeTextType: Ember.computed(function() {
      if (this.type) {
        return this.type.get('value') == 'FREE_TEXT';
      }
      return false;
    })
      .property('this.type')
      .cacheable(),

    amGeoType: Ember.computed(function() {
      if (this.type) {
        return this.type.get('value') == 'GEO';
      }
      return false;
    })
      .property('this.type')
      .cacheable(),

    amCascadeType: Ember.computed(function() {
      if (this.type) {
        return this.type.get('value') == 'CASCADE';
      }
      return false;
    })
      .property('this.type')
      .cacheable(),

    hasExtraSettings: Ember.computed(function() {
      if (!Ember.none(this.type)) {
        const val = this.type.get('value');
        return (
          val === 'GEOSHAPE' ||
          val === 'CASCADE' ||
          val === 'NUMBER' ||
          val === 'GEO' ||
          val === 'FREE_TEXT' ||
          val === 'SCAN' ||
          val === 'OPTION' ||
          val === 'CADDISFLY'
        );
      }
    })
      .property('this.type')
      .cacheable(),

    amGeoshapeType: Ember.computed(function() {
      if (this.type) {
        return this.type.get('value') == 'GEOSHAPE';
      }
      return false;
    })
      .property('this.type')
      .cacheable(),

    amDateType: Ember.computed(function() {
      if (this.type) {
        return this.type.get('value') == 'DATE';
      }
      return false;
    })
      .property('this.type')
      .cacheable(),

    amSignatureType: Ember.computed(function() {
      return (
        (this.content && this.content.get('type') === 'SIGNATURE') ||
        (this.type && this.type.get('value') === 'SIGNATURE')
      );
    }).property('this.type'),

    amCaddisflyType: Ember.computed(function() {
      return this.type && this.type.get('value') == 'CADDISFLY';
    })
      .property('this.type')
      .cacheable(),

    showLocaleName: Ember.computed(function() {
      if (!this.type) {
        return false;
      }
      return (
        this.type.get('value') == 'FREE_TEXT' ||
        this.type.get('value') == 'NUMBER' ||
        this.type.get('value') == 'OPTION' ||
        this.type.get('value') == 'SCAN' ||
        this.type.get('value') == 'CASCADE'
      );
    })
      .property('this.type')
      .cacheable(),

    // TODO dependencies
    // TODO options
    doQuestionEdit() {
      if (this.content && (this.content.get('isDirty') || this.content.get('isSaving'))) {
        this.showMessageDialog(
          Ember.String.loc('_question_is_being_saved'),
          Ember.String.loc('_question_is_being_saved_text')
        );
        return;
      }

      this.loadQuestionOptions();

      FLOW.selectedControl.set('selectedQuestion', this.get('content'));
      this.set('variableName', FLOW.selectedControl.selectedQuestion.get('variableName'));
      this.set('text', FLOW.selectedControl.selectedQuestion.get('text'));
      this.set('tip', FLOW.selectedControl.selectedQuestion.get('tip'));
      this.set('mandatoryFlag', FLOW.selectedControl.selectedQuestion.get('mandatoryFlag'));
      this.set('minVal', FLOW.selectedControl.selectedQuestion.get('minVal'));
      this.set('maxVal', FLOW.selectedControl.selectedQuestion.get('maxVal'));
      this.set('allowSign', FLOW.selectedControl.selectedQuestion.get('allowSign'));
      this.set('allowDecimal', FLOW.selectedControl.selectedQuestion.get('allowDecimal'));
      this.set('allowMultipleFlag', FLOW.selectedControl.selectedQuestion.get('allowMultipleFlag'));
      this.set('allowOtherFlag', FLOW.selectedControl.selectedQuestion.get('allowOtherFlag'));
      this.set(
        'allowExternalSources',
        FLOW.selectedControl.selectedQuestion.get('allowExternalSources')
      );
      this.set('localeNameFlag', FLOW.selectedControl.selectedQuestion.get('localeNameFlag'));
      this.set(
        'localeLocationFlag',
        FLOW.selectedControl.selectedQuestion.get('localeLocationFlag')
      );
      this.set('geoLocked', FLOW.selectedControl.selectedQuestion.get('geoLocked'));
      this.set(
        'requireDoubleEntry',
        FLOW.selectedControl.selectedQuestion.get('requireDoubleEntry')
      );
      this.set('includeInMap', FLOW.selectedControl.selectedQuestion.get('includeInMap'));
      this.set('dependentFlag', FLOW.selectedControl.selectedQuestion.get('dependentFlag'));
      this.set('allowPoints', FLOW.selectedControl.selectedQuestion.get('allowPoints'));
      this.set('allowLine', FLOW.selectedControl.selectedQuestion.get('allowLine'));
      this.set('immutable', FLOW.selectedControl.selectedQuestion.get('immutable'));
      this.set('allowPolygon', FLOW.selectedControl.selectedQuestion.get('allowPolygon'));
      this.set('cascadeResourceId', FLOW.selectedControl.selectedQuestion.get('cascadeResourceId'));
      this.set(
        'caddisflyResourceUuid',
        FLOW.selectedControl.selectedQuestion.get('caddisflyResourceUuid')
      );
      this.set('personalData', FLOW.selectedControl.selectedQuestion.get('personalData'));

      FLOW.optionListControl.set('content', []);

      // if the cascadeResourceId is not null, get the resource
      if (!Ember.empty(FLOW.selectedControl.selectedQuestion.get('cascadeResourceId'))) {
        const cascadeResource = FLOW.store.find(
          FLOW.CascadeResource,
          FLOW.selectedControl.selectedQuestion.get('cascadeResourceId')
        );
        FLOW.selectedControl.set('selectedCascadeResource', cascadeResource);
      }

      // reset selected caddisfly resource
      FLOW.selectedControl.set('selectedCaddisflyResource', null);
      // if the caddisflyResourceUuid is not null, get the resource
      if (!Ember.empty(FLOW.selectedControl.selectedQuestion.get('caddisflyResourceUuid'))) {
        const caddResource = FLOW.router.caddisflyResourceController.content.findProperty(
          'uuid',
          FLOW.selectedControl.selectedQuestion.get('caddisflyResourceUuid')
        );
        if (!Ember.empty(caddResource)) {
          this.set(
            'selectedCaddisflyTestSample',
            this.get('caddisflyTestSamples').findProperty('sample', caddResource.get('sample'))
          );
          this.set(
            'selectedCaddisflyTestName',
            this.get('caddisflyTestNames').findProperty('name', caddResource.get('name'))
          );
          this.set(
            'selectedCaddisflyTestBrand',
            this.get('caddisflyTestBrands').find(
              item =>
                item.brand === caddResource.get('brand') &&
                item.model === caddResource.get('model') &&
                item.device === caddResource.get('device')
            )
          );
          FLOW.selectedControl.set('selectedCaddisflyResource', caddResource);
        }
      }
      // if the dependentQuestionId is not null, get the question
      if (!Ember.empty(FLOW.selectedControl.selectedQuestion.get('dependentQuestionId'))) {
        const dependentQuestion = FLOW.store.find(
          FLOW.Question,
          FLOW.selectedControl.selectedQuestion.get('dependentQuestionId')
        );
        const dependentAnswer = FLOW.selectedControl.selectedQuestion.get(
          'dependentQuestionAnswer'
        );

        // if we have found the question, fill the options
        if (dependentQuestion.get('id') !== '0') {
          FLOW.selectedControl.set('dependentQuestion', dependentQuestion);
          this.fillOptionList();

          const dependentAnswerArray = dependentAnswer.split('|');
          // find the answer already set and set it to true in the optionlist
          FLOW.optionListControl.get('content').forEach(item => {
            if (dependentAnswerArray.indexOf(item.get('value')) > -1) {
              item.set('isSelected', true);
            }
          });
        }
      }

      let questionType = null;
      // set the type to the original choice
      FLOW.questionTypeControl.get('content').forEach(item => {
        if (item.get('value') == FLOW.selectedControl.selectedQuestion.get('type')) {
          questionType = item;
        }
      });
      this.set('type', questionType);
    },

    /*
     *  Load the question options for question editing
     */
    loadQuestionOptions() {
      const c = this.content;
      FLOW.questionOptionsControl.set('content', []);
      FLOW.questionOptionsControl.set('questionId', c.get('keyId'));

      const options = FLOW.store.filter(
        FLOW.QuestionOption,
        optionItem => optionItem.get('questionId') === c.get('keyId')
      );

      if (options.get('length')) {
        const optionArray = Ember.A(options.toArray().sort(sortByOrder));
        FLOW.questionOptionsControl.set('content', optionArray);
      } else {
        FLOW.questionOptionsControl.loadDefaultOptions();
      }
    },

    fillOptionList() {
      if (FLOW.selectedControl.get('dependentQuestion')) {
        const dependentQuestion = FLOW.selectedControl.get('dependentQuestion');
        FLOW.optionListControl.set('content', []);
        FLOW.optionListControl.set('currentActive', null);

        const options = FLOW.store.filter(FLOW.QuestionOption, item => {
          if (!Ember.none(FLOW.selectedControl.selectedQuestion)) {
            return item.get('questionId') == dependentQuestion.get('keyId');
          }
          return false;
        });

        const optionArray = options.toArray();
        optionArray.sort((a, b) => a.get('order') - b.get('order'));

        optionArray.forEach(item => {
          FLOW.optionListControl.get('content').push(
            Ember.Object.create({
              isSelected: false,
              value: item.get('text'),
            })
          );
        });
      }
    },

    doCancelEditQuestion() {
      FLOW.selectedControl.set('selectedQuestion', null);

      // scroll to position
      const el = document.querySelector(`[data-id="${this.get('content').get('keyId')}"]`);
      // removing the offset of the fixed topbar (-150px)
      $('body').animate({ scrollTop: getOffset(el) - 150 }, 500);
    },

    doSaveEditQuestion() {
      if (this.variableNameValidationFailure) {
        this.showMessageDialog(
          Ember.String.loc('_variable_name_must_be_valid_and_unique'),
          this.variableNameValidationFailureReason
        );
        return;
      }

      if (this.questionValidationFailure) {
        this.showMessageDialog(
          Ember.String.loc('_question_over_500_chars_header'),
          Ember.String.loc('_question_over_500_chars_text')
        );
        return;
      }

      if (this.questionTooltipValidationFailure) {
        this.showMessageDialog(
          Ember.String.loc('_tooltip_over_500_chars_header'),
          Ember.String.loc('_tooltip_over_500_chars_text')
        );
        return;
      }

      if (this.get('amOptionType')) {
        // save options to the datastore
        FLOW.questionOptionsControl.persistOptions();
      }

      if (
        this.type.get('value') === 'GEOSHAPE' &&
        this.get('allowPoints') === false &&
        this.get('allowLine') === false &&
        this.get('allowPolygon') === false
      ) {
        this.showMessageDialog(
          Ember.String.loc('_no_geoshape_types_header'),
          Ember.String.loc('_no_geoshape_types_text')
        );
        return;
      }

      if (
        this.type.get('value') === 'CASCADE' &&
        Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'))
      ) {
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_cascade_resources'));
        FLOW.dialogControl.set('message', Ember.String.loc('_cascade_select_resource'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
        return false;
      }

      if (this.type.get('value') !== 'NUMBER') {
        this.set('minVal', null);
        this.set('maxVal', null);
        this.set('allowSign', false);
        this.set('allowDecimal', false);
      }
      if (
        this.type.get('value') !== 'GEO' &&
        this.type.get('value') !== 'GEOSHAPE' &&
        this.type.get('value') !== 'SCAN'
      ) {
        this.set('geoLocked', false);
      }

      if (!(this.type.get('value') == 'NUMBER' || this.type.get('value') == 'FREE_TEXT')) {
        this.set('requireDoubleEntry', false);
      }

      if (!(this.type.get('value') == 'CASCADE')) {
        this.set('cascadeResourceId', null);
      }

      if (!(this.type.get('value') == 'CADDISFLY')) {
        this.set('caddisflyResourceUuid', null);
      }

      const path = `${FLOW.selectedControl.selectedSurveyGroup.get(
        'code'
      )}/${FLOW.selectedControl.selectedSurvey.get(
        'name'
      )}/${FLOW.selectedControl.selectedQuestionGroup.get('code')}`;
      FLOW.selectedControl.selectedQuestion.set('variableName', this.get('variableName'));
      FLOW.selectedControl.selectedQuestion.set('text', this.get('text'));
      FLOW.selectedControl.selectedQuestion.set('tip', this.get('tip'));
      FLOW.selectedControl.selectedQuestion.set('mandatoryFlag', this.get('mandatoryFlag'));
      FLOW.selectedControl.selectedQuestion.set('personalData', this.get('personalData'));

      const minVal = Ember.empty(this.get('minVal')) ? null : this.get('minVal');
      const maxVal = Ember.empty(this.get('maxVal')) ? null : this.get('maxVal');
      FLOW.selectedControl.selectedQuestion.set('minVal', minVal);
      FLOW.selectedControl.selectedQuestion.set('maxVal', maxVal);

      FLOW.selectedControl.selectedQuestion.set('path', path);
      FLOW.selectedControl.selectedQuestion.set('allowSign', this.get('allowSign'));
      FLOW.selectedControl.selectedQuestion.set('allowDecimal', this.get('allowDecimal'));
      FLOW.selectedControl.selectedQuestion.set('allowMultipleFlag', this.get('allowMultipleFlag'));
      FLOW.selectedControl.selectedQuestion.set('allowOtherFlag', this.get('allowOtherFlag'));
      FLOW.selectedControl.selectedQuestion.set(
        'localeNameFlag',
        this.get('showLocaleName') && this.get('localeNameFlag')
      );
      FLOW.selectedControl.selectedQuestion.set(
        'localeLocationFlag',
        this.get('amGeoType') && this.get('localeLocationFlag')
      );
      FLOW.selectedControl.selectedQuestion.set('geoLocked', this.get('geoLocked'));
      FLOW.selectedControl.selectedQuestion.set(
        'requireDoubleEntry',
        this.get('requireDoubleEntry')
      );
      FLOW.selectedControl.selectedQuestion.set('includeInMap', this.get('includeInMap'));
      FLOW.selectedControl.selectedQuestion.set('allowPoints', this.get('allowPoints'));
      FLOW.selectedControl.selectedQuestion.set('allowLine', this.get('allowLine'));
      FLOW.selectedControl.selectedQuestion.set('allowPolygon', this.get('allowPolygon'));
      FLOW.selectedControl.selectedQuestion.set('immutable', this.get('immutable'));

      const allowExternalSources =
        this.type.get('value') !== 'FREE_TEXT' ? false : this.get('allowExternalSources');
      FLOW.selectedControl.selectedQuestion.set('allowExternalSources', allowExternalSources);

      let dependentQuestionAnswer = '';
      let first = true;

      FLOW.optionListControl.get('content').forEach(item => {
        if (item.isSelected) {
          if (!first) {
            dependentQuestionAnswer += '|';
          }
          first = false;
          dependentQuestionAnswer += item.value;
        }
      });

      if (this.get('dependentFlag') && dependentQuestionAnswer !== '') {
        FLOW.selectedControl.selectedQuestion.set('dependentFlag', this.get('dependentFlag'));
        FLOW.selectedControl.selectedQuestion.set(
          'dependentQuestionId',
          FLOW.selectedControl.dependentQuestion.get('keyId')
        );
        FLOW.selectedControl.selectedQuestion.set(
          'dependentQuestionAnswer',
          dependentQuestionAnswer
        );
      } else {
        FLOW.selectedControl.selectedQuestion.set('dependentFlag', false);
        FLOW.selectedControl.selectedQuestion.set('dependentQuestionId', null);
        FLOW.selectedControl.selectedQuestion.set('dependentQuestionAnswer', null);
      }

      if (this.get('type')) {
        FLOW.selectedControl.selectedQuestion.set('type', this.type.get('value'));
      }

      // deal with cascadeResource
      if (this.type.get('value') == 'CASCADE') {
        if (!Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'))) {
          FLOW.selectedControl.selectedQuestion.set(
            'cascadeResourceId',
            FLOW.selectedControl.selectedCascadeResource.get('keyId')
          );
        }
      }

      // deal with caddisflyResource
      if (this.type.get('value') == 'CADDISFLY') {
        if (!Ember.empty(FLOW.selectedControl.get('selectedCaddisflyResource'))) {
          FLOW.selectedControl.selectedQuestion.set(
            'caddisflyResourceUuid',
            FLOW.selectedControl.selectedCaddisflyResource.get('uuid')
          );
        }
      }

      FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
      FLOW.store.commit();

      FLOW.selectedControl.set('selectedQuestion', null);
      FLOW.selectedControl.set('dependentQuestion', null);
      FLOW.selectedControl.set('selectedCascadeResource', null);

      // scroll to position
      const el = document.querySelector(`[data-id="${this.get('content').get('keyId')}"]`);
      // removing the offset of the fixed topbar (-150px)
      $('body').animate({ scrollTop: getOffset(el) - 150 }, 500);
    },

    isPartOfMonitoringGroup(questionKeyId) {
      const surveyId = FLOW.store.findById(FLOW.Question, questionKeyId).get('surveyId');
      const surveyGroupId = FLOW.store.findById(FLOW.Survey, surveyId).get('surveyGroupId');
      return FLOW.store.findById(FLOW.SurveyGroup, surveyGroupId).get('monitoringGroup');
    },

    /**
     * Variable name validation
     *
     * A valid variable name must match /^[A-Za-z0-9_\-]*$/. Uniqueness
     * constraints depends on wether the question is part of a
     * monitoring group or not. If the question is part of a
     * monitoring group, uniqueness validation _must_ happen on the
     * server and cover all questions which are part of that group. If
     * not, the uniqueness constraint only covers the survey and can
     * be checked on the client.
     */
    throttleTimer: null,

    validateVariableName(args) {
      const self = this;
      const { selectedQuestion } = FLOW.selectedControl;
      const questionKeyId = selectedQuestion.get('keyId');
      const variableName = this.get('variableName') || '';
      if (FLOW.Env.mandatoryQuestionID && variableName.match(/^\s*$/)) {
        args.failure(Ember.String.loc('_variable_name_mandatory'));
      } else if (!variableName.match(/^[A-Za-z0-9_-]*$/)) {
        args.failure(Ember.String.loc('_variable_name_only_alphanumeric'));
      } else {
        const monitoring = this.isPartOfMonitoringGroup(questionKeyId);
        if (monitoring) {
          clearTimeout(this.throttleTimer);
          this.throttleTimer = setTimeout(() => {
            $.ajax({
              url: `/rest/questions/${questionKeyId}/validate?variableName=${variableName}`,
              type: 'POST',
              success(data) {
                if (data.success) {
                  // check for special characters once more
                  if (!self.get('variableName').match(/^[A-Za-z0-9_-]*$/)) {
                    args.failure(Ember.String.loc('_variable_name_only_alphanumeric'));
                  } else {
                    args.success();
                  }
                } else {
                  args.failure(data.reason);
                }
              },
              error() {
                args.failure(Ember.String.loc('_could_not_validate_variable_name_with_server'));
              },
            });
          }, 1000);
        } else {
          const otherVariableNames = FLOW.store
            .filter(
              FLOW.Question,
              question =>
                selectedQuestion.get('surveyId') === question.get('surveyId') &&
                questionKeyId !== question.get('keyId')
            )
            .map(question => question.get('variableName'))
            .filter(_variableName => _variableName !== '');
          const isUnique = !otherVariableNames.contains(variableName);
          if (isUnique) {
            args.success();
          } else {
            args.failure(Ember.String.loc('_variable_name_not_unique'));
          }
        }
      }
    },

    validateMinAndMax(args) {
      if (this.type.get('value') == 'NUMBER') {
        if (!Ember.empty(this.get('minVal')) && !Ember.empty(this.get('maxVal'))) {
          if (isNaN(this.get('minVal')) || isNaN(this.get('maxVal'))) {
            args.NaNFailure();
            return;
          }
          if (parseFloat(this.get('minVal')) >= parseFloat(this.get('maxVal'))) {
            args.valueFailure();
            return;
          }
        }
      }
      args.success();
    },

    showMessageDialog(header, message) {
      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', header);
      FLOW.dialogControl.set('message', message);
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
    },

    deleteQuestion() {
      const qDeleteId = this.content.get('keyId');

      // check if anything is being saved at the moment
      if (this.checkQuestionsBeingSaved()) {
        this.showMessageDialog(
          Ember.String.loc('_please_wait'),
          Ember.String.loc('_please_wait_until_previous_request')
        );
        return;
      }

      // Check if there is another question that is dependant on this question
      if (this.content.get('type') === 'OPTION') {
        const hasDependant = FLOW.store
          .find(FLOW.Question)
          .some(q => qDeleteId === q.get('dependentQuestionId'));

        if (hasDependant) {
          this.showMessageDialog(
            Ember.String.loc('_cant_delete_question'),
            Ember.String.loc('_another_question_depends_on_this')
          );
          return;
        }
      }

      // check if deleting this question is allowed
      // if successful, the deletion action will be called from DS.FLOWrestadaptor.sideload
      FLOW.store.findQuery(FLOW.Question, {
        preflight: 'delete',
        questionId: qDeleteId,
      });
    },

    checkQuestionsBeingSaved() {
      const question = FLOW.store.filter(FLOW.Question, item => item.get('isSaving'));
      return question.content.length > 0;
    },

    // move question to selected location
    doQuestionMoveHere() {
      let insertAfterOrder;
      let selectedQ;
      const selectedOrder = FLOW.selectedControl.selectedForMoveQuestion.get('order');

      if (this.get('zeroItemQuestion')) {
        insertAfterOrder = 0;
      } else {
        insertAfterOrder = this.content.get('order');
      }

      // check if anything is being saved at the moment
      if (this.checkQuestionsBeingSaved()) {
        this.showMessageDialog(
          Ember.String.loc('_please_wait'),
          Ember.String.loc('_please_wait_until_previous_request')
        );
        return;
      }

      // check to see if we are trying to move the question to another question group
      if (
        FLOW.selectedControl.selectedForMoveQuestion.get('questionGroupId') !=
        FLOW.selectedControl.selectedQuestionGroup.get('keyId')
      ) {
        selectedQ = FLOW.store.find(
          FLOW.Question,
          FLOW.selectedControl.selectedForMoveQuestion.get('keyId')
        );
        if (selectedQ !== null) {
          const qgIdSource = FLOW.selectedControl.selectedForMoveQuestion.get('questionGroupId');
          const qgIdDest = FLOW.selectedControl.selectedQuestionGroup.get('keyId');

          // restore order
          FLOW.questionControl.reorderQuestions(qgIdSource, selectedOrder, 'decrement');
          FLOW.questionControl.reorderQuestions(qgIdDest, insertAfterOrder, 'increment');

          // move question
          selectedQ.set('order', insertAfterOrder + 1);
          selectedQ.set('questionGroupId', qgIdDest);

          FLOW.questionControl.submitBulkQuestionsReorder([qgIdSource, qgIdDest]);
        }
        // if we are not moving to another group, we must be moving inside a group
        // only do something if we are not moving to the same place
      } else if (!(selectedOrder == insertAfterOrder || selectedOrder == insertAfterOrder + 1)) {
        selectedQ = FLOW.store.find(
          FLOW.Question,
          FLOW.selectedControl.selectedForMoveQuestion.get('keyId')
        );
        if (selectedQ !== null) {
          // restore order
          const qgId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
          const questionsInGroup = FLOW.store.filter(
            FLOW.Question,
            item => item.get('questionGroupId') == qgId
          );

          const origOrder = FLOW.selectedControl.selectedForMoveQuestion.get('order');
          const movingUp = origOrder < insertAfterOrder;

          questionsInGroup.forEach(item => {
            const currentOrder = item.get('order');
            if (movingUp) {
              if (currentOrder == origOrder) {
                // move moving item to right location
                selectedQ.set('order', insertAfterOrder);
              } else if (currentOrder > origOrder && currentOrder <= insertAfterOrder) {
                // move item down
                item.set('order', item.get('order') - 1);
              }
              // Moving down
            } else if (currentOrder == origOrder) {
              // move moving item to right location
              selectedQ.set('order', insertAfterOrder + 1);
            } else if (currentOrder < origOrder && currentOrder > insertAfterOrder) {
              // move item up
              item.set('order', item.get('order') + 1);
            }
          });

          FLOW.questionControl.submitBulkQuestionsReorder([qgId]);
        }
      }
      FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
      FLOW.store.commit();
      FLOW.selectedControl.set('selectedForMoveQuestion', null);
    },

    // execute question copy to selected location
    doQuestionCopyHere() {
      let insertAfterOrder;

      if (this.get('zeroItemQuestion')) {
        insertAfterOrder = 0;
      } else {
        insertAfterOrder = this.content.get('order');
      }

      // check if anything is being saved at the moment
      if (this.checkQuestionsBeingSaved()) {
        this.showMessageDialog(
          Ember.String.loc('_please_wait'),
          Ember.String.loc('_please_wait_until_previous_request')
        );
        return;
      }

      const qgId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');

      // restore order
      FLOW.questionControl.reorderQuestions(qgId, insertAfterOrder, 'increment');

      const question = FLOW.selectedControl.get('selectedForCopyQuestion');
      // create copy of Question item in the store
      FLOW.store.createRecord(FLOW.Question, {
        order: insertAfterOrder + 1,
        surveyId: question.get('surveyId'),
        questionGroupId: qgId,
        sourceId: question.get('keyId'),
      });

      FLOW.questionControl.submitBulkQuestionsReorder([qgId]);

      FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
      FLOW.store.commit();
      FLOW.selectedControl.set('selectedForCopyQuestion', null);
    },

    // create new question
    doInsertQuestion() {
      let insertAfterOrder;
      const path = `${FLOW.selectedControl.selectedSurveyGroup.get(
        'code'
      )}/${FLOW.selectedControl.selectedSurvey.get(
        'name'
      )}/${FLOW.selectedControl.selectedQuestionGroup.get('code')}`;

      if (this.get('zeroItemQuestion')) {
        insertAfterOrder = 0;
      } else {
        insertAfterOrder = this.content.get('order');
      }

      // check if anything is being saved at the moment
      if (this.checkQuestionsBeingSaved()) {
        this.showMessageDialog(
          Ember.String.loc('_please_wait'),
          Ember.String.loc('_please_wait_until_previous_request')
        );
        return;
      }

      const qgId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');

      // reorder the rest of the questions
      FLOW.questionControl.reorderQuestions(qgId, insertAfterOrder, 'increment');

      // create new Question item in the store
      FLOW.store.createRecord(FLOW.Question, {
        order: insertAfterOrder + 1,
        type: 'FREE_TEXT',
        path,
        text: Ember.String.loc('_new_question_please_change_name'),
        surveyId: FLOW.selectedControl.selectedSurvey.get('keyId'),
        questionGroupId: qgId,
      });

      FLOW.questionControl.submitBulkQuestionsReorder([qgId]);

      FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
      FLOW.store.commit();
    },

    // true if one question has been selected for Move
    oneSelectedForMove: Ember.computed(() => {
      const selectedForMove = FLOW.selectedControl.get('selectedForMoveQuestion');
      if (selectedForMove) {
        return true;
      }
      return false;
    }).property('FLOW.selectedControl.selectedForMoveQuestion'),

    // true if one question has been selected for Copy
    oneSelectedForCopy: Ember.computed(() => {
      const selectedForCopy = FLOW.selectedControl.get('selectedForCopyQuestion');
      if (selectedForCopy) {
        return true;
      }
      return false;
    }).property('FLOW.selectedControl.selectedForCopyQuestion'),

    // prepare for question copy. Shows 'copy to here' buttons
    doQuestionCopy() {
      FLOW.selectedControl.set('selectedForCopyQuestion', this.get('content'));
      FLOW.selectedControl.set('selectedForMoveQuestion', null);
    },

    // cancel question copy
    doQuestionCopyCancel() {
      FLOW.selectedControl.set('selectedForCopyQuestion', null);
    },

    // prepare for question move. Shows 'move here' buttons
    doQuestionMove() {
      FLOW.selectedControl.set('selectedForMoveQuestion', this.get('content'));
      FLOW.selectedControl.set('selectedForCopyQuestion', null);
    },

    // cancel group move
    doQuestionMoveCancel() {
      FLOW.selectedControl.set('selectedForMoveQuestion', null);
    },

    validateQuestionObserver() {
      this.set(
        'questionValidationFailure',
        (this.text && this.text.length > 500) || !this.text || this.text == ''
      );
      if (this.text && this.text.length > 500) {
        this.set(
          'questionValidationFailureReason',
          Ember.String.loc('_question_over_500_chars_header')
        );
      } else if (!this.text || this.text == '') {
        this.set('questionValidationFailureReason', Ember.String.loc('_question_text_empty'));
      }
    },

    validateQuestionTooltipObserver() {
      this.set('questionTooltipValidationFailure', this.tip != null && this.tip.length > 1500);
    },

    validateVariableNameObserver() {
      const self = this;
      self.validateVariableName({
        success() {
          self.set('variableNameValidationFailure', false);
          self.set('variableNameValidationFailureReason', null);
        },
        failure(msg) {
          self.set('variableNameValidationFailure', true);
          self.set('variableNameValidationFailureReason', msg);
        },
      });
    },

    immutableGroup: Ember.computed(() => {
      return FLOW.selectedControl.selectedQuestionGroup.get('immutable');
    }).property('FLOW.selectedControl.selectedQuestionGroup'),

    showQuestionModifyButtons: Ember.computed(() => {
      const form = FLOW.selectedControl.get('selectedSurvey');
      return FLOW.permControl.canEditForm(form);
    }).property('FLOW.selectedControl.selectedSurvey'),

    caddisflyTestSamples: Ember.computed(function() {
      const tests = FLOW.router.caddisflyResourceController.get('content');
      const distinct = {};
      const testSamples = [];
      FLOW.selectedControl.set('selectedCaddisflyResource', null);
      this.set('selectedCaddisflyTestSample', null);
      tests.forEach(obj => {
        if (!(obj.sample in distinct)) {
          testSamples.push(obj);
        }
        distinct[obj.sample] = 0;
      });
      return this.sortedList(testSamples, 'sample');
    }).property('FLOW.router.caddisflyResourceController.content'),

    caddisflyTestNames: Ember.computed(function() {
      const tests = FLOW.router.caddisflyResourceController.get('content');
      const distinct = {};
      const testNames = [];
      FLOW.selectedControl.set('selectedCaddisflyResource', null);
      this.set('selectedCaddisflyTestName', null);
      if (this.get('selectedCaddisflyTestSample')) {
        const sample = this.get('selectedCaddisflyTestSample');
        const names = tests.filter(item => item.sample === sample.sample);
        names.forEach(obj => {
          if (!(obj.name in distinct)) {
            testNames.push(obj);
          }
          distinct[obj.name] = 0;
        });
      }
      return this.sortedList(testNames, 'name');
    }).property('this.selectedCaddisflyTestSample'),

    caddisflyTestBrands: Ember.computed(function() {
      const tests = FLOW.router.caddisflyResourceController.get('content');
      const distinct = {};
      const testBrands = [];
      FLOW.selectedControl.set('selectedCaddisflyResource', null);
      this.set('selectedCaddisflyTestBrand', null);
      if (this.get('selectedCaddisflyTestName')) {
        const name = this.get('selectedCaddisflyTestName');
        const brands = tests.filter(item => item.sample == name.sample && item.name === name.name);
        brands.forEach(obj => {
          let displayName = 'brand' in obj && obj.brand ? obj.brand : '';
          displayName += 'model' in obj && obj.model ? ` - ${obj.model}` : '';
          displayName += 'device' in obj && obj.device ? ` - ${obj.device}` : '';

          if (!(displayName in distinct)) {
            obj.brandDisplayName = displayName;
            testBrands.push(obj);
          }
          distinct[displayName] = 0;
        });
      } else {
        this.set('selectedCaddisflyTestBrand', null);
      }
      return this.sortedList(testBrands, 'brandDisplayName');
    }).property('this.selectedCaddisflyTestName'),

    caddisflyTestDetails: Ember.computed(function() {
      const tests = FLOW.router.caddisflyResourceController.get('content');
      const distinct = {};
      const testDetails = [];
      if (this.get('selectedCaddisflyTestBrand')) {
        const brands = this.get('selectedCaddisflyTestBrand');
        const details = tests.filter(
          item =>
            item.sample == brands.sample &&
            item.name === brands.name &&
            item.brand === brands.brand &&
            item.model === brands.model &&
            item.device === brands.device
        );
        details.forEach(obj => {
          const { results } = obj;
          let displayName = '';
          for (let i = 0; i < results.length; i++) {
            displayName += 'name' in results[i] ? results[i].name : '';
            displayName += 'chemical' in results[i] ? ` (${results[i].chemical})` : '';
            displayName += 'range' in results[i] ? ` ${results[i].range}` : '';
            displayName += 'unit' in results[i] ? ` ${results[i].unit}` : '';
            displayName += i + 1 < results.length ? ', ' : '';
          }

          const { reagents } = obj;
          for (let i = 0; i < reagents.length; i++) {
            displayName += ` ${reagents[i].code}`;
          }

          if (!(displayName in distinct)) {
            obj.detailsDisplayName = displayName;
            testDetails.push(obj);
          }
          distinct[displayName] = 0;
        });
      }
      return this.sortedList(testDetails, 'detailsDisplayName');
    }).property('this.selectedCaddisflyTestBrand'),

    sortedList(arr, prop) {
      return arr.sort((a, b) => {
        const nameA = a[prop].toUpperCase(); // ignore upper and lowercase
        const nameB = b[prop].toUpperCase(); // ignore upper and lowercase
        if (nameA < nameB) {
          return -1;
        }
        if (nameA > nameB) {
          return 1;
        }

        // names must be equal
        return 0;
      });
    },

    brandsObserver() {
      // needed to disable save button when no resource brand is specified
      FLOW.selectedControl.set('selectedCaddisflyResource', null);
    },
  }
);

/*
 *  View to render the options for an option type question.
 */
FLOW.OptionListView = Ember.CollectionView.extend({
  tagName: 'ul',
  content: null,
  itemViewClass: Ember.View.extend(template('navSurveys/question-option'), {
    topOption: Ember.computed(function() {
      const option = this.get('content');
      if (option) {
        return option.get('order') == 1;
      }
    }).property('content.order'),
    bottomOption: Ember.computed(function() {
      const option = this.get('content');
      const options = FLOW.questionOptionsControl.get('content');
      if (option && options) {
        return option.get('order') == options.get('length');
      }
    }).property('content.order', 'FLOW.questionOptionsControl.content.length'),
  }),
});
