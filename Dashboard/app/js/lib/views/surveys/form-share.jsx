/* eslint-disable import/no-unresolved */
import React from 'react';
import observe from 'akvo-flow/mixins/observe';
import WebFormShare from 'akvo-flow/components/forms/form-share';

require('akvo-flow/views/react-component');

FLOW.WebFormShareView = FLOW.ReactComponentView.extend(
  observe({
    'FLOW.selectedControl.selectedSurvey.status': 'formValidation',
    'FLOW.selectedControl.selectedSurveyGroup.monitoringGroup': 'formValidation',
    'FLOW.questionControl.content.isLoaded': 'formValidation',
    'FLOW.questionGroupControl.content.isLoaded': 'formValidation',
    'FLOW.surveyControl.webformId': 'renderReactSide',
  }),
  {
    init() {
      this._super();
      this.formValidation = this.formValidation.bind(this);
      this.renderReactSide = this.renderReactSide.bind(this);
      this.getProps = this.getProps.bind(this);
      this.getShareURL = this.getShareURL.bind(this);

      this.valid = false;
    },

    didInsertElement(...args) {
      this._super(...args);
      this.formValidation();
      this.renderReactSide();
    },

    // react side
    renderReactSide() {
      const props = this.getProps();
      this.reactRender(<WebFormShare {...props} />);
    },

    getProps() {
      return {
        strings: {},
        data: {
          valid: this.valid,
          shareUrl:
            FLOW.surveyControl.webformId &&
            `${window.location.origin}/webforms/${FLOW.surveyControl.webformId}`,
        },
        actions: {
          getShareURL: this.getShareURL,
        },
      };
    },

    formValidation() {
      const selectedForm = FLOW.selectedControl.get('selectedSurvey');
      const selectedSurveyGroup = FLOW.selectedControl.get('selectedSurveyGroup');
      const questions = FLOW.questionControl.get('content');
      const questionGroups = FLOW.questionGroupControl.get('content');

      // case 1 is form published?
      const isPublished = selectedForm.get('status') === 'PUBLISHED';

      // case 2 is not monitoring
      const isNotMonitoring = selectedSurveyGroup.get('monitoringGroup') === false;

      // case 3 does not have illegal question type
      const noIllegalQuestion =
        questions.some(question => {
          switch (question.get('type')) {
            case 'CADDISFLY':
              return true;
            case 'SIGNATURE':
              return true;
            case 'GEOSHAPE':
              return true;
            default:
              return false;
          }
        }) === false;

      // case 4 does not have repeated question group
      const noRepeatedQuestionGroup = questionGroups.some(qg => qg.get('repeatable')) === false;

      this.valid = isPublished && isNotMonitoring && noIllegalQuestion && noRepeatedQuestionGroup;
      this.renderReactSide();
    },

    getShareURL() {
      FLOW.surveyControl.webformUrl();
    },
  }
);
