/* eslint-disable jsx-a11y/anchor-is-valid */
/* eslint-disable jsx-a11y/label-has-associated-control */
import React, { useContext } from 'react';
import SurveysContext from './surveys-context';

export default function Survey() {
  const {
    surveyToDisplay,
    currentProject,
    showDataApproval,
    visibleProjectBasics,
    isNewProject,
    toggleShowProjectDetails,
  } = useContext(SurveysContext);

  const languageControl = FLOW.languageControl.content;

  return (
    <section id="main" className="projectSection floats-in middleSection" role="main">
      <section className="projectDetailsPanel">
        <h2>{currentProject.name}</h2>
        <ul className="projectSummary">
          <li>
            {Ember.String.loc('_forms')}
            <span className="projectForm">FLOW.projectControl.formCount</span>
          </li>
          {/* Should we display the ID as we do with forms?  */}
          <li className="hidden">
            {Ember.String.loc('_id')}
            <span className="projectForm">{currentProject.keyId}</span>
          </li>
          <li>
            {Ember.String.loc('_monitoring')}
            <span className="projectMonitoring">
              {currentProject.monitoringGroup
                ? Ember.String.loc('_enabled')
                : Ember.String.loc('_not_enabled')}
            </span>
          </li>
        </ul>
        <section className="projectDetails">
          <h3>{Ember.String.loc('_survey_basics')}</h3>{' '}
          {!isNewProject(currentProject) && (
            <a
              onClick={toggleShowProjectDetails}
              onKeyDown={toggleShowProjectDetails}
              className="button"
            >
              {visibleProjectBasics() ? Ember.String.loc('_collapse') : Ember.String.loc('_show')}
            </a>
          )}
          {visibleProjectBasics() && (
            <form
              className="projectDetailForm"
              onSubmit={() => null}
              //   {{action 'saveProject' on='submit' target="FLOW.projectControl"}}
            >
              <label>{Ember.String.loc('_survey_title')}</label>
              <input type="text" id="projectTitle" defaultValue={currentProject.name} />
              {/* {{view Ember.TextField id="projectTitle" valueBinding="FLOW.projectControl.currentProject.name" disabledBinding="view.disableFolderSurveyInputField"}} */}
              <ul className="projectSelect floats-in">
                <li>
                  <label>{Ember.String.loc('_language')}:</label>
                  <select>
                    {languageControl.map(language => (
                      <option key={language.value} value={language.value}>
                        {language.label}
                      </option>
                    ))}
                  </select>

                  {/* {{view Ember.Select
                              contentBinding="FLOW.languageControl.content"
                              selectionBinding="view.selectedLanguage"
                              optionLabelPath="content.label"
                              optionValuePath="content.value"
                              disabledBinding="view.disableFolderSurveyInputField"}} */}
                </li>
              </ul>
              {/* {{#if FLOW.projectControl.hasForms}} */}
              <label htmlFor="enableMonitoring" className="labelcheckbox">
                <input type="checkbox" id="enableMonitoring" />
                {/* {{view Ember.Checkbox checkedBinding="FLOW.projectControl.currentProject.monitoringGroup" id="enableMonitoring" disabledBinding="view.disableFolderSurveyInputField"}} */}
                {Ember.String.loc('_enable_monitoring_features')}
              </label>
              {currentProject.monitoringGroup && (
                <p className="monitoringHint">
                  {Ember.String.loc('_choose_the_registration_form')}:{' '}
                  <a
                    className="helpIcon tooltip"
                    data-title={Ember.String.loc('_choose_the_registration_form_tooltip')}
                  >
                    ?
                  </a>
                </p>
              )}
              <select>
                <option>Form</option>
              </select>
              {/* {{view Ember.Select
                                       contentBinding="FLOW.surveyControl.arrangedContent"
                                       selectionBinding="view.selectedRegistrationForm"
                                       optionLabelPath="content.code"
                                       optionValuePath="content.keyId"
                                       disabledBinding="view.disableFolderSurveyInputField"}} */}
              {showDataApproval && (
                <>
                  <label htmlFor="enableDataApproval" className="labelcheckbox">
                    {/* {{view Ember.Checkbox checkedBinding="FLOW.projectControl.currentProject.requireDataApproval" id="enableDataApproval" disabledBinding="view.disableFolderSurveyInputField"}} */}
                    {Ember.String.loc('_enable_data_approval')}{' '}
                    <a
                      className="helpIcon tooltip"
                      data-title={Ember.String.loc('_enable_data_approval_tooltip')}
                    >
                      ?
                    </a>
                  </label>{' '}
                  <select>
                    <option value="content.keyId">
                      {Ember.String.loc('_choose_data_approval_group')}
                    </option>
                  </select>
                  {/* {{#if view.showDataApprovalList}}
               {{view Ember.Select
                                               contentBinding="FLOW.router.approvalGroupListController.arrangedContent"
                                               optionLabelPath="content.name"
                                               optionValuePath="content.keyId"
                                               selectionBinding="FLOW.projectControl.dataApprovalGroup"
                                               disabledBinding="view.disableFolderSurveyInputField"
                                               promptBinding="Ember.STRINGS._choose_data_approval_group"}}
        
                                        {{#if view.showDataApprovalDetails}} */}
                </>
              )}
              <div className="hideShow">
                <a>
                  {/* {{action toggleShowDataApprovalDetails target="view"}} */}
                  {Ember.String.loc('_hide_approval')}
                </a>
              </div>
              {/* {{#view FLOW.SurveyApprovalView controllerBinding="FLOW.router.approvalGroupController"}} */}
              <div className="approvalDetail">
                <h2>name</h2>
                <p>
                  {/* {{#if ordered}}  */}
                  {Ember.String.loc('_ordered_approval')}
                  {/* {{else}} */}
                  {Ember.String.loc('_unordered_approval')}
                  {/* {{/if}} */}
                </p>
                <ul className="approvalSteps">
                  {/* {{#each step in FLOW.router.approvalStepsController}} */}
                  {/* {{#view FLOW.SurveyApprovalStepView stepBinding="step"}} */}
                  <li>
                    <h4>{/* {{view.step.title}} */}</h4>
                    <a>
                      {/* {{action toggleShowResponsibleUsers target="view"}} */}
                      {Ember.String.loc('_responsible_users')}
                    </a>
                  </li>
                  {/* {{#if view.showResponsibleUsers}} */}
                  <div>
                    <ul className="responsibleUsers">
                      {/* {{#each user in FLOW.router.userListController}} */}
                      {/* {{#view FLOW.ApprovalResponsibleUserView
                                                                                        userBinding="user"
                                                                                        stepBinding="view.step"}} */}
                      <li>
                        <input type="checkBox" />
                        {/* {{view Ember.Checkbox
                                                                                               checkedBinding="view.isResponsibleUser"}}
                                                                                        {{view.user.userName}} */}
                      </li>
                      {/* {{/view}} */}
                      {/* {{/each}} */}
                    </ul>
                  </div>
                  {/* {{/if}}
                                                            {{/view}}
                                                        {{/each}} */}
                </ul>
              </div>
              {/* {{/view}}
                                        {{else}} */}
              <div className="hideShow">
                <a>
                  {/* {{action toggleShowDataApprovalDetails target="view"}} */}
                  {Ember.String.loc('_show_approval')}
                </a>
              </div>
              {/* {{/if}}
                                    {{/if}}
                                {{/if}}
          }
                                {/* {/if} */}
              <br />
              <label htmlFor="markAsTemplate" className="labelcheckbox">
                <input type="checkBox" id="markAsTemplate" />
                {/* {{view Ember.Checkbox checkedBinding="FLOW.projectControl.currentProject.template" id="markAsTemplate" disabledBinding="view.disableFolderSurveyInputField"}}  */}
                {Ember.String.loc('_mark_as_template')}
                <a
                  className="helpIcon tooltip"
                  data-title={Ember.String.loc('_mark_as_template_tooltip')}
                >
                  ?
                </a>
              </label>
            </form>
          )}
        </section>

        <section className="noFormsContainer">
          {/* {{#unless FLOW.projectControl.hasForms}} */}
          <ul>
            <li className="formList">
              <p className="noForms">{Ember.String.loc('_no_forms_in_this_survey')}</p>
            </li>
            {/* {{#if view.showAddNewFormButton}} */}
            <li>
              <a
                className="addMenuAction aBtn addNewForm"
                //    {{action "createForm" target="FLOW.surveyControl"}}
              >
                {Ember.String.loc('_add_new_form')}
              </a>
            </li>
            {/* {{/if}} */}
          </ul>
          {/* {{/unless}} */}
        </section>

        <section className="forms">
          {/* {{#if FLOW.projectControl.hasForms}} */}
          <div id="tabs">
            {currentProject.monitoringGroup && (
              //     /* {{#if FLOW.projectControl.currentProject.monitoringGroup}} */

              // /* {{#if view.showAddNewFormButton}} */

              <nav className="menuTopbar">
                <ul>
                  <li>
                    <a
                      //   {{action "createForm" target="FLOW.surveyControl"}}
                      className="button addFormBtn"
                    >
                      {Ember.String.loc('_add_new_form')}
                    </a>
                  </li>
                </ul>
              </nav>
            )}
            {/* {{/if}}
                    {{/if}} */}
            <nav className="tabNav floats-in">
              <ul>
                {/* {{#each form in FLOW.surveyControl.orderedForms}}
                              {{#view FLOW.FormTabView contentBinding="form"}} */}
                <li className="aFormTab current">
                  <a>
                    {/* {{action "selectForm" form target="FLOW.surveyControl"}  */}
                    form.name
                  </a>
                </li>
                {/* {{/view}}
                            {{/each}} */}
              </ul>
            </nav>
            <section className="formsContainer">
              <div
                id="form01"
                // {{bindAttr className="view.isPublished:published"}}
              >
                {/* {{#if FLOW.selectedControl.selectedSurvey}} */}
                <h3>
                  {surveyToDisplay.name}
                  {/* {{FLOW.selectedControl.selectedSurvey.name}} */}
                </h3>
                {/* {{view FLOW.FormView}}
                              {{/if}} */}
              </div>
            </section>
          </div>
          {/* {{/if}} */}
        </section>
      </section>
    </section>
  );
}
