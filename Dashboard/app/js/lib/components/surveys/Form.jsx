/* eslint-disable jsx-a11y/anchor-is-valid */
/* eslint-disable jsx-a11y/label-has-associated-control */
import React from 'react';
import PropTypes from 'prop-types';

function Form({ strings, helperFunctions, form, questionCount }) {
  return (
    // {{#with FLOW.selectedControl.selectedSurvey as form}}
    <div id="form01" className="aformContainer">
      <nav className="newSurveyNav">
        <ul>
          {helperFunctions.showFormPublishButton() && (
            <li>
              <a
                className={helperFunctions.disableFormPublishButton() ? 'disabled' : 'btnOutline'}
                //  {{bindAttr className="view.disableFormPublishButton:disabled :btnOutline"}}

                //   {{action "publishSurvey" target="FLOW.surveyControl"}}
                disabled={form.status === 'PUBLISHED'}
              >
                {strings.publish}
              </a>
            </li>
          )}
          <li>
            <a
              className="previewNewSurvey"
              //  {{action "showPreview" target="FLOW.surveyControl"}}
            >
              {strings.preview}
            </a>
          </li>
          {helperFunctions.showFormDeleteButton() && (
            <li>
              <a
                className="deleteSurvey"
                // onClick={this.props.actions.confirm}
                // onKeyDown={this.props.actions.confirm}
                //  {{action confirm FLOW.dialogControl.delForm target="FLOW.dialogControl"}}
              >
                {' '}
                {strings.delete}
              </a>
            </li>
          )}
          {/* {{view FLOW.WebFormShareView}} */}
        </ul>
      </nav>
      <ul className="formSummary">
        <li>
          {strings.version}
          <span className="formVersion">{form.version}</span>
        </li>
        <li>
          <span className="upCase">{strings.id}</span>
          <span className="formID">{form.keyId}</span>
        </li>
        <li>
          {strings.questions}
          <span className="formQuestionCount">{questionCount}</span>
        </li>
      </ul>
      <section className="formDetails">
        <h3>{strings.form_basics}</h3>
        {helperFunctions.visibleFormBasics() ? (
          <>
            {!helperFunctions.isNewForm() && (
              <a
                // {{action "toggleShowFormBasics" target="this"}}
                className="button"
              >
                {strings.collapse}
              </a>
            )}
            <form
              className="surveyDetailForm"
              // {{action 'saveProject' on='submit' target="FLOW.projectControl"}}
            >
              <label>{strings.formTitle}</label>
              <input type="text" disabled={helperFunctions.disableFormFields()} />
              {/* {{view Ember.TextField valueBinding="form.name" disabledBinding="view.disableFormFields"}} */}

              {FLOW.Env.showFormInstanceApiUrl && (
                <>
                  <label>
                    {strings.formApiUrl}

                    <a className="helpIcon tooltip" data-title={strings.tooltip.formApiUrl}>
                      ?
                    </a>
                  </label>
                  <input type="text" disabled />
                </>
              )

              //  {{view Ember.TextField valueBinding="view.apiUrl" disabled="true"}
              }
              <nav className="newSurveyNav">
                {helperFunctions.showFormTranslationsButton() && (
                  <ul className="manageStuff">
                    <li>
                      <a
                        className="btnOutline"
                        // {{action "doManageTranslations" target="this"}}
                      >
                        {strings.manageTranslations}
                      </a>
                    </li>
                    <li>
                      <a
                        className="btnOutline"
                        //  {{action "doManageNotifications" target="this"}}
                      >
                        {strings.manageNotifications}
                      </a>
                    </li>
                  </ul>
                )}
              </nav>
            </form>
          </>
        ) : (
          <a
            // {{action "toggleShowFormBasics" target="this"}}
            className="button"
          >
            {strings.show}
          </a>
        )}
      </section>
      <section className="surveyForm">
        {/* {{#if view.manageTranslations}} */}
        {/* {{view FLOW.TranslationsView}} */}
        {/* {{else}} */}
        {/* {{#if view.manageNotifications}} */}
        {/* {{view FLOW.NotificationsView}} */}
        {/* {{else}} */}
        {/* {{view FLOW.EditQuestionsView}} */}
        {/* {{/if}} */}
        {/* {{/if}} */}
      </section>
    </div>
    // {{/with}}
  );
}

Form.propTypes = {
  strings: PropTypes.object.isRequired,
  form: PropTypes.object,
  helperFunctions: PropTypes.object,
  questionCount: PropTypes.number,
};

Form.defaultProps = {
  form: null,
  helperFunctions: null,
  questionCount: null,
};

export default Form;
