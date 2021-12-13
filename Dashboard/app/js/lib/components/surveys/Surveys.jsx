/* eslint-disable jsx-a11y/anchor-is-valid */
/* eslint-disable jsx-a11y/label-has-associated-control */
import React from 'react';
import PropTypes from 'prop-types';

class Surveys extends React.Component {
  state = {
    currentProject: this.props.currentProject,
    selectedSurvey: this.props.selectedSurvey,
  };

  render() {
    // const strings = {
    //   addNewForm: Ember.String.loc('_add_new_form'),
    //   chooseDataApprovalGroup: Ember.String.loc('_choose_data_approval_group'),
    //   chooseRegistrationForm: Ember.String.loc('_choose_the_registration_form'),
    //   collapse: Ember.String.loc('_collapse'),
    //   enabled: Ember.String.loc('_enabled'),
    //   enableDataApproval: Ember.String.loc('_enable_data_approval'),
    //   enableMonitoringFeatures: Ember.String.loc('_enable_monitoring_features'),
    //   form: Ember.String.loc('_forms'),
    //   hideApproval: Ember.String.loc('_hide_approval'),
    //   id: Ember.String.loc('_id'),
    //   language: Ember.String.loc('_language'),
    //   markAsTemplate: Ember.String.loc('_mark_as_template'),
    //   monitoring: Ember.String.loc('_monitoring'),
    //   noForm: Ember.String.loc('_no_forms_in_this_survey'),
    //   notEnabled: Ember.String.loc('_not_enabled'),
    //   orderedApproval: Ember.String.loc('_ordered_approval'),
    //   responsibleUser: Ember.String.loc('_responsible_users'),
    //   show: Ember.String.loc('_show'),
    //   showApproval: Ember.String.loc('_show_approval'),
    //   surveyBasics: Ember.String.loc('_survey_basics'),
    //   surveyTitle: Ember.String.loc('_survey_title'),
    //   unorderedApproval: Ember.String.loc('_unordered_approval'),
    //   tooltip: {
    //     chooseRegistrationForm: Ember.String.loc('_choose_the_registration_form_tooltip'),
    //     enableDataApproval: Ember.String.loc('_enable_data_approval_tooltip'),
    //     markAsTemplate: Ember.String.loc('_mark_as_template_tooltip'),
    //   },
    // };

    return (
      <div className="floats-in">
        <div id="pageWrap" className="widthConstraint belowHeader">
          <section id="main" className="projectSection floats-in middleSection" role="main">
            <section id="" className="projectDetailsPanel">
              <h2>{this.state.currentProject.name}</h2>
              <ul className="projectSummary">
                <li>
                  {Ember.String.loc('_forms')}
                  <span className="projectForm">{this.props.helperFunctions.formCount()}</span>
                </li>
                {/* Should we display the ID as we do with forms?  */}
                <li className="hidden">
                  {Ember.String.loc('_id')}
                  <span className="projectForm">{this.state.currentProject.keyId}</span>
                </li>
                <li>
                  {Ember.String.loc('_monitoring')}
                  <span className="projectMonitoring">
                    {this.state.currentProject.monitoringGroup
                      ? Ember.String.loc('_enabled')
                      : Ember.String.loc('_not_enabled')}
                  </span>
                </li>
              </ul>
              <section className="projectDetails">
                <h3>{Ember.String.loc('_survey_basics')} </h3>{' '}
                {!this.props.helperFunctions.isNewProject() && (
                  <a
                    onClick={this.props.actions.toggleShowProjectDetails}
                    onKeyDown={this.props.actions.toggleShowProjectDetails}
                    className="button"
                  >
                    {this.props.helperFunctions.visibleProjectBasics()
                      ? Ember.String.loc('_collapse')
                      : Ember.String.loc('_show')}
                  </a>
                )}
                {this.props.helperFunctions.visibleProjectBasics() && (
                  <form
                    className="projectDetailForm"
                    //   {{action 'saveProject' on='submit' target="FLOW.projectControl"}}
                  >
                    <label>{Ember.String.loc('_survey_title')}</label>
                    {/* {{view Ember.TextField id="projectTitle" valueBinding="this.state.currentProject.name" disabledBinding="view.disableFolderSurveyInputField"}} */}
                    <input
                      type="text"
                      id="projectTitle"
                      defaultValue={this.state.currentProject.name}
                    />
                    <ul className="projectSelect floats-in">
                      <li>
                        <label>{Ember.String.loc('_language')}:</label>
                        {/* {{view Ember.Select
                       contentBinding="FLOW.languageControl.content"
                       selectionBinding="view.selectedLanguage"
                       optionLabelPath="content.label"
                       optionValuePath="content.value"
                       disabledBinding="view.disableFolderSurveyInputField"}} */}
                        <select>
                          {FLOW.languageControl.content.map(language => (
                            <option key={language.value}>{language.label}</option>
                          ))}
                        </select>
                      </li>
                    </ul>
                    {this.props.helperFunctions.hasForms() && (
                      <>
                        <label htmlFor="enableMonitoring" className="labelcheckbox">
                          <input type="checkbox" id="enableMonitoring" />
                          {/* {{view Ember.Checkbox checkedBinding="this.state.currentProject.monitoringGroup" id="enableMonitoring" disabledBinding="view.disableFolderSurveyInputField"}} */}
                          {Ember.String.loc('_enable_monitoring_features')}
                        </label>
                        {this.state.currentProject.monitoringGroup && (
                          <>
                            <p className="monitoringHint">
                              {Ember.String.loc('_choose_the_registration_form')}:{' '}
                              <a
                                className="helpIcon tooltip"
                                data-title={Ember.String.loc(
                                  '_choose_the_registration_form_tooltip'
                                )}
                              >
                                ?
                              </a>
                            </p>
                            <select>
                              {this.props.arrangedContent.map(content => (
                                <option key={content.keyId}>{content.code}</option>
                              ))}
                            </select>
                            {/* {{view Ember.Select
                                contentBinding="FLOW.surveyControl.arrangedContent"
                                selectionBinding="view.selectedRegistrationForm"
                                optionLabelPath="content.code"
                                optionValuePath="content.keyId"
                                disabledBinding="view.disableFolderSurveyInputField"}} */}

                            {this.props.showDataApproval && (
                              <>
                                <label htmlFor="enableDataApproval" className="labelcheckbox">
                                  <input type="checkbox" id="enableDataApproval" />
                                  {/* {{view Ember.Checkbox checkedBinding="this.state.currentProject.requireDataApproval" id="enableDataApproval" disabledBinding="view.disableFolderSurveyInputField"}} */}
                                  {Ember.String.loc('_enable_data_approval')}{' '}
                                  <a
                                    className="helpIcon tooltip"
                                    data-title={Ember.String.loc('_enable_data_approval_tooltip')}
                                  >
                                    ?
                                  </a>
                                </label>{' '}
                                {this.props.helperFunctions.showDataApprovalList && (
                                  <>
                                    {/* {{view Ember.Select
                                        contentBinding="FLOW.router.approvalGroupListController.arrangedContent"
                                        optionLabelPath="content.name"
                                        optionValuePath="content.keyId"
                                        selectionBinding="FLOW.projectControl.dataApprovalGroup"
                                        disabledBinding="view.disableFolderSurveyInputField"
                                    promptBinding="Ember.STRINGS._choose_data_approval_group"}} */}
                                    <select>
                                      <option>
                                        {Ember.String.loc('_choose_data_approval_group')}
                                      </option>
                                      {this.props.dataApprovalGroup &&
                                        this.props.dataApprovalGroup.map(dataApproval => (
                                          <option value={dataApproval.keyId}>
                                            {dataApproval.name}
                                          </option>
                                        ))}
                                    </select>
                                    {this.props.showDataApprovalDetails ? (
                                      <>
                                        <div className="hideShow">
                                          <a
                                            onClick={
                                              this.props.actions.toggleShowDataApprovalDetails
                                            }
                                            onKeyDown={
                                              this.props.actions.toggleShowDataApprovalDetails
                                            }
                                          >
                                            {Ember.String.loc('_hide_approval')}
                                          </a>
                                        </div>
                                        {/* {{#view FLOW.SurveyApprovalView controllerBinding="FLOW.router.approvalGroupController"}} */}
                                        <div className="approvalDetail">
                                          <h2>name</h2>
                                          <p>
                                            {/* {{#if ordered}}  */}
                                            {Ember.String.loc(
                                              '_ordered_approval'
                                            )} {/* {{else}} */}{' '}
                                            {Ember.String.loc('_unordered_approval')}
                                            {/* {{/if}} */}
                                          </p>
                                          <ul className="approvalSteps">
                                            {/* {{#each step in FLOW.router.approvalStepsController}} */}
                                            {/* {{#view FLOW.SurveyApprovalStepView stepBinding="step"}} */}
                                            <li>
                                              <h4>{/* {{view.step.title}} */}</h4>{' '}
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
                                                  {/* {{view Ember.Checkbox
                                                                                        checkedBinding="view.isResponsibleUser"}}
                                                                                 {{view.user.userName}} */}
                                                  <input type="checkBox" />
                                                </li>
                                                {/* {{/view}} */}
                                                {/* {{/each}} */}
                                              </ul>
                                            </div>

                                            {/* {{/view}}
                                                 {{/each}}  */}
                                          </ul>
                                        </div>
                                        {/* {{/view}} */}
                                      </>
                                    ) : (
                                      <div className="hideShow">
                                        <a
                                          onClick={this.props.actions.toggleShowDataApprovalDetails}
                                          onKeyDown={
                                            this.props.actions.toggleShowDataApprovalDetails
                                          }
                                        >
                                          {Ember.String.loc('_show_approval')}
                                        </a>
                                      </div>
                                    )}
                                  </>
                                )}
                              </>
                            )}
                          </>
                        )}

                        <br />
                        <label htmlFor="markAsTemplate" className="labelcheckbox">
                          <input type="checkBox" id="markAsTemplate" />
                          {/* {{view Ember.Checkbox checkedBinding="this.state.currentProject.template" id="markAsTemplate" disabledBinding="view.disableFolderSurveyInputField"}}  */}
                          {Ember.String.loc('_mark_as_template')}
                          <a
                            className="helpIcon tooltip"
                            data-title={Ember.String.loc('_mark_as_template_tooltip')}
                          >
                            ?
                          </a>
                        </label>
                      </>
                    )}
                  </form>
                )}
              </section>

              <section className="noFormsContainer">
                {!this.props.helperFunctions.hasForms() && (
                  <ul>
                    <li className="formList">
                      <p className="noForms">{Ember.String.loc('_no_forms_in_this_survey')}</p>
                    </li>
                    {this.props.helperFunctions.showAddNewFormButton() && (
                      <li>
                        <a
                          className="addMenuAction aBtn addNewForm"
                          //    {{action "createForm" target="FLOW.surveyControl"}}
                        >
                          {Ember.String.loc('_add_new_form')}
                        </a>
                      </li>
                    )}
                  </ul>
                )}
              </section>

              <section className="forms">
                {this.props.helperFunctions.hasForms() && (
                  <div id="tabs">
                    {this.state.currentProject.monitoringGroup &&
                      (this.props.helperFunctions.showAddNewFormButton() && (
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
                      ))}
                    <nav className="tabNav floats-in">
                      <ul>
                        {/* NEED TO BE SEPARATED */}
                        {/* {{#view FLOW.FormTabView contentBinding="form"}}  */}
                        {FLOW.surveyControl.orderedForms.map(data => {
                          const form = data._data.attributes;
                          return (
                            <li
                              key={form.keyId}
                              className={this.props.helperFunctions.classProperty(form)}
                            >
                              <a>
                                {/* {{action "selectForm" form target="FLOW.surveyControl"}} */}
                                {form.name}
                              </a>
                            </li>
                          );
                        })}
                        {/* {{/view}} */}
                      </ul>
                    </nav>
                    <section className="formsContainer">
                      <div
                        id="form01"
                        className={this.props.helperFunctions.isPublished() ? 'published' : ''}
                      >
                        <h3>{this.state.selectedSurvey && this.state.selectedSurvey.name}</h3>
                        {/* {{view FLOW.FormView}} */}
                      </div>
                    </section>
                  </div>
                )}
              </section>
            </section>
          </section>
        </div>
      </div>
    );
  }
}

Surveys.propTypes = {
  currentProject: PropTypes.object.isRequired,
  selectedSurvey: PropTypes.object.isRequired,
  helperFunctions: PropTypes.object.isRequired,
  showDataApproval: PropTypes.bool.isRequired,
  showDataApprovalDetails: PropTypes.bool.isRequired,
  arrangedContent: PropTypes.array.isRequired,
  actions: PropTypes.object,
  dataApprovalGroup: PropTypes.array,
};

Surveys.defaultProps = {
  dataApprovalGroup: null,
  actions: () => null,
};

export default Surveys;
