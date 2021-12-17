/* eslint-disable jsx-a11y/label-has-associated-control */
import React from 'react';

class Form extends React.Component {
  render() {
    const strings = {
      publish: Ember.String.loc('_publish'),
      preview: Ember.String.loc('_preview'),
      delete: Ember.String.loc('_delete'),
      version: Ember.String.loc('_version'),
      id: Ember.String.loc('_id'),
      question: Ember.String.loc('_questions'),
      formBasic: Ember.String.loc('_form_basics'),
      collapse: Ember.String.loc('_collapse'),
      formTitle: Ember.String.loc('_form_title'),
      formApiUrl: Ember.String.loc('_form_api_url'),
      manageTranslations: Ember.String.loc('_manage_translations'),
      manageNotifications: Ember.String.loc('_manage_notifications'),
      show: Ember.String.loc('_show'),
      tooltip: {
        formApiUrl: Ember.String.loc('_form_api_url_tooltip'),
      },
    };

    // const helperFunctions = {
    //   showFormPublishButton: this.showFormPublishButton,
    //   disableFormPublishButton: this.disableFormPublishButton,
    //   showFormDeleteButton: this.showFormDeleteButton,
    //   visibleFormBasics: this.visibleFormBasics,
    //   isNewForm: this.isNewForm,
    //   apiUrl: this.apiUrl,
    //   showFormTranslationsButton: this.showFormTranslationsButton,
    //   disableFormFields: this.disableFormFields,
    //   manageTranslations: this.manageTranslations,
    //   manageNotifications: this.manageNotifications,
    //   actions: {
    //     toggleShowFormBasics: this.toggleShowFormBasics,
    //     publishSurvey: this.publishSurvey,
    //     showPreview: this.showPreview,
    //     confirm: this.confirm,
    //     saveProject: this.saveProject,
    //     doManageTranslations: this.doManageTranslations,
    //     doManageNotifications: this.doManageNotifications,
    //   },
    // };

    return (
      // {{#with FLOW.selectedControl.selectedSurvey as form}}
      <div id="form01" className="aformContainer">
        <nav className="newSurveyNav">
          <ul>
            {/* {{#if view.showFormPublishButton}} */}
            <li>
              <button
                //  {{bindAttr className="view.disableFormPublishButton:disabled :btnOutline"}}
                type="button"
                //   {{action "publishSurvey" target="FLOW.surveyControl"}}
                disabled
              >
                {strings.publish}
              </button>
            </li>
            {/* {{/if}} */}
            <li>
              <a
                className="previewNewSurvey"
                //  {{action "showPreview" target="FLOW.surveyControl"}}
              >
                {strings.preview}
              </a>
            </li>
            {/* {{#if view.showFormDeleteButton}} */}
            <li>
              <a
                className="deleteSurvey"
                //  {{action confirm FLOW.dialogControl.delForm target="FLOW.dialogControl"}}
              >
                {' '}
                {strings.delete}
              </a>
            </li>
            {/* {{/if}} */}
            {/* {{view FLOW.WebFormShareView}} */}
          </ul>
        </nav>
        <ul className="formSummary">
          <li>
            {strings.version}
            <span className="formVersion">form.version</span>
          </li>
          <li>
            <span className="upCase">{strings.id}</span>
            <span className="formID">form.keyId</span>
          </li>
          <li>
            {strings.questions}
            <span className="formQuestionCount">FLOW.projectControl.questionCount</span>
          </li>
        </ul>
        <section className="formDetails">
          <h3>{strings.form_basics}</h3>
          {/* {{#if view.visibleFormBasics}} */}
          {/* {{#unless view.isNewForm}} */}
          <a
            // {{action "toggleShowFormBasics" target="this"}}
            className="button"
          >
            {strings.collapse}
          </a>
          {/* {{/unless}} */}
          <form
            className="surveyDetailForm"
            // {{action 'saveProject' on='submit' target="FLOW.projectControl"}}
          >
            <label>{strings.formTitle}</label>
            {/* {{view Ember.TextField valueBinding="form.name" disabledBinding="view.disableFormFields"}} */}
            {/* {{#if FLOW.Env.showFormInstanceApiUrl}} */}
            <label>
              {strings.formApiUrl}

              <a className="helpIcon tooltip" data-title={strings.tooltip.formApiUrl}>
                ?
              </a>
            </label>
            {/* {{view Ember.TextField valueBinding="view.apiUrl" disabled="true"}} */}
            {/* {{/if}} */}
            <nav className="newSurveyNav">
              <ul className="manageStuff">
                {/* {{#if view.showFormTranslationsButton}} */}
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
                {/* {{/if}} */}
              </ul>
            </nav>
          </form>
          {/* {{else}} */}
          <a
            // {{action "toggleShowFormBasics" target="this"}}
            className="button"
          >
            {strings.show}
          </a>
          {/* {{/if}} */}
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
}

export default Form;
