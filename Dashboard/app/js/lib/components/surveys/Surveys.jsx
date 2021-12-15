/* eslint-disable jsx-a11y/anchor-is-valid */
/* eslint-disable jsx-a11y/label-has-associated-control */
import React from 'react';
import PropTypes from 'prop-types';

class Surveys extends React.Component {
  state = {
    currentProject: this.props.currentProject,
    selectedSurvey: this.props.selectedSurvey,
  };

  toggleMonitoringGroup = () => {
    this.setState(state => ({
      currentProject: {
        ...state.currentProject,
        monitoringGroup: !state.currentProject.monitoringGroup,
      },
    }));
  };

  toggleDataApproval = () => {
    this.setState(state => ({
      currentProject: {
        ...state.currentProject,
        requireDataApproval: !state.currentProject.requireDataApproval,
      },
    }));
  };

  toggleTemplate = () => {
    this.setState(state => ({
      currentProject: {
        ...state.currentProject,
        template: !state.currentProject.template,
      },
    }));
  };

  getSurveyTitle = inputValue => {
    if (inputValue.length > 0) {
      this.setState(state => ({
        currentProject: {
          ...state.currentProject,
          code: inputValue,
          name: inputValue,
          path: `${FLOW.projectControl.currentProjectPath}/${inputValue}`,
        },
      }));
    }
  };

  render() {
    return (
      <div className="floats-in">
        <div id="pageWrap" className="widthConstraint belowHeader">
          <section id="main" className="projectSection floats-in middleSection" role="main">
            <section id="" className="projectDetailsPanel">
              <h2>{this.state.currentProject.name}</h2>
              <ul className="projectSummary">
                <li>
                  {this.props.strings.form}
                  <span className="projectForm">{this.props.helperFunctions.formCount()}</span>
                </li>
                {/* Should we display the ID as we do with forms?  */}
                <li className="hidden">
                  {this.props.strings.id}
                  <span className="projectForm">{this.state.currentProject.keyId}</span>
                </li>
                <li>
                  {this.props.strings.monitoring}
                  <span className="projectMonitoring">
                    {this.state.currentProject.monitoringGroup
                      ? this.props.strings.enabled
                      : this.props.strings.notEnabled}
                  </span>
                </li>
              </ul>
              <section className="projectDetails">
                <h3>{this.props.strings.surveyBasics} </h3>{' '}
                {!this.props.helperFunctions.isNewProject() && (
                  <a
                    onClick={this.props.actions.toggleShowProjectDetails}
                    onKeyDown={this.props.actions.toggleShowProjectDetails}
                    className="button"
                  >
                    {this.props.helperFunctions.visibleProjectBasics()
                      ? this.props.strings.collapse
                      : this.props.strings.show}
                  </a>
                )}
                {this.props.helperFunctions.visibleProjectBasics() && (
                  <form
                    className="projectDetailForm"
                    //   {{action 'saveProject' on='submit' target="FLOW.projectControl"}}
                  >
                    <label>{this.props.strings.surveyTitle}</label>
                    <input
                      type="text"
                      id="projectTitle"
                      defaultValue={this.state.currentProject.name}
                      disabled={this.props.helperFunctions.disableFolderSurveyInputField()}
                      onChange={e => this.getSurveyTitle(e.target.value)}
                    />
                    <ul className="projectSelect floats-in">
                      <li>
                        <label>{this.props.strings.language}:</label>
                        {/* {{view Ember.Select
                       contentBinding="FLOW.languageControl.content"
                       selectionBinding="view.selectedLanguage"
                       optionLabelPath="content.label"
                       optionValuePath="content.value"
                       disabledBinding="view.disableFolderSurveyInputField"}} */}
                        <select
                          disabled={this.props.helperFunctions.disableFolderSurveyInputField()}
                        >
                          {this.props.languages.map(language => (
                            <option key={language.value}>{language.label}</option>
                          ))}
                        </select>
                      </li>
                    </ul>
                    {this.props.helperFunctions.hasForms() && (
                      <>
                        <label htmlFor="enableMonitoring" className="labelcheckbox">
                          <input
                            type="checkbox"
                            id="enableMonitoring"
                            checked={this.state.currentProject.monitoringGroup}
                            disabled={this.props.helperFunctions.disableFolderSurveyInputField()}
                            onChange={this.toggleMonitoringGroup}
                          />
                          {this.props.strings.enableMonitoringFeatures}
                        </label>
                        {this.state.currentProject.monitoringGroup && (
                          <>
                            <p className="monitoringHint">
                              {this.props.strings.chooseRegistrationForm}:{' '}
                              <a
                                className="helpIcon tooltip"
                                data-title={this.props.strings.tooltip.chooseRegistrationForm}
                              >
                                ?
                              </a>
                            </p>
                            <select
                              disabled={this.props.helperFunctions.disableFolderSurveyInputField()}
                            >
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
                                  <input
                                    type="checkbox"
                                    id="enableDataApproval"
                                    disabled={this.props.helperFunctions.disableFolderSurveyInputField()}
                                    onChange={this.toggleDataApproval}
                                  />
                                  {this.props.strings.enableDataApproval}{' '}
                                  <a
                                    className="helpIcon tooltip"
                                    data-title={this.props.strings.tooltip.enableDataApproval}
                                  >
                                    ?
                                  </a>
                                </label>{' '}
                                {this.state.currentProject.requireDataApproval && (
                                  <>
                                    {/* {{view Ember.Select
                                        contentBinding="FLOW.router.approvalGroupListController.arrangedContent"
                                        optionLabelPath="content.name"
                                        optionValuePath="content.keyId"
                                        selectionBinding="FLOW.projectControl.dataApprovalGroup"
                                        disabledBinding="view.disableFolderSurveyInputField"
                                    promptBinding="Ember.STRINGS._choose_data_approval_group"}} */}
                                    <select
                                      disabled={this.props.helperFunctions.disableFolderSurveyInputField()}
                                    >
                                      <option>{this.props.strings.chooseDataApprovalGroup}</option>
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
                                            {this.props.strings.hideApproval}
                                          </a>
                                        </div>

                                        <div className="approvalDetail">
                                          <h2>
                                            {this.props.approvalGroups &&
                                              this.props.approvalGroups.name}
                                          </h2>
                                          <p>
                                            {this.props.approvalGroups &&
                                            this.props.approvalGroups.ordered
                                              ? this.props.strings.orderedApproval
                                              : this.props.strings.unorderedApproval}
                                          </p>
                                          {/* CAN BE SEPARATED */}
                                          <ul className="approvalSteps">
                                            {this.props.approvalSteps &&
                                              this.props.approvalSteps.map(step => (
                                                <>
                                                  <li>
                                                    <h4>{step && step.title}</h4>{' '}
                                                    <a
                                                      onClick={
                                                        this.props.actions
                                                          .toggleShowResponsibleUsers
                                                      }
                                                      onKeyDown={
                                                        this.props.actions
                                                          .toggleShowResponsibleUsers
                                                      }
                                                    >
                                                      {this.props.strings.responsibleUser}
                                                    </a>
                                                  </li>
                                                  {this.props.showResponsibleUsers && (
                                                    <div>
                                                      <ul className="responsibleUsers">
                                                        {this.props.userList.map(user => (
                                                          // {{#view FLOW.ApprovalResponsibleUserView
                                                          //                                userBinding="user"
                                                          //                                stepBinding="view.step"}}
                                                          <li>
                                                            <input
                                                              type="checkBox"
                                                              onChange={() =>
                                                                this.props.actions.isResponsibleUser(
                                                                  user.keyId,
                                                                  user
                                                                )
                                                              }
                                                              // view.user.userName
                                                            />
                                                          </li>
                                                        ))}
                                                      </ul>
                                                    </div>
                                                  )}
                                                </>
                                              ))}
                                          </ul>
                                        </div>
                                      </>
                                    ) : (
                                      <div className="hideShow">
                                        <a
                                          onClick={this.props.actions.toggleShowDataApprovalDetails}
                                          onKeyDown={
                                            this.props.actions.toggleShowDataApprovalDetails
                                          }
                                        >
                                          {this.props.strings.showApproval}
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
                          <input
                            type="checkBox"
                            id="markAsTemplate"
                            disabled={this.props.helperFunctions.disableFolderSurveyInputField()}
                            onChange={this.toggleTemplate}
                          />
                          {this.props.strings.markAsTemplate}
                          <a
                            className="helpIcon tooltip"
                            data-title={this.props.strings.tooltip.markAsTemplate}
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
                      <p className="noForms">{this.props.strings.noForm}</p>
                    </li>
                    {this.props.helperFunctions.showAddNewFormButton() && (
                      <li>
                        <a
                          className="addMenuAction aBtn addNewForm"
                          onClick={this.props.actions.createForm}
                          onKeyDown={this.props.actions.createForm}
                        >
                          {this.props.strings.addNewForm}
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
                                onClick={this.props.actions.createForm}
                                onKeyDown={this.props.actions.createForm}
                                className="button addFormBtn"
                              >
                                {this.props.strings.addNewForm}
                              </a>
                            </li>
                          </ul>
                        </nav>
                      ))}
                    <nav className="tabNav floats-in">
                      <ul>
                        {/* Form list */}
                        {this.props.orderedForms.map(project => {
                          const form = project._data.attributes;
                          const formKeyId = form.keyId !== undefined ? form.keyId : 1;
                          return (
                            <li
                              key={formKeyId}
                              className={this.props.helperFunctions.classProperty(form)}
                            >
                              <a
                                onClick={() => this.props.actions.selectForm(project)}
                                onKeyDown={() => this.props.actions.selectForm(project)}
                              >
                                {form.name}
                              </a>
                            </li>
                          );
                        })}
                        {/* Form list */}
                      </ul>
                    </nav>
                    <section className="formsContainer">
                      <div
                        id="form01"
                        className={this.props.helperFunctions.isPublished() ? 'published' : ''}
                      >
                        <h3>{this.state.selectedSurvey && this.state.selectedSurvey.name}</h3>
                        {/* FORM */}
                        {/* {{view FLOW.FormView}} */}

                        {/* FORM */}
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
  strings: PropTypes.object.isRequired,
  step: PropTypes.object,
  approvalGroups: PropTypes.object,
  currentProject: PropTypes.object.isRequired,
  selectedSurvey: PropTypes.object,
  orderedForms: PropTypes.object,
  helperFunctions: PropTypes.object.isRequired,
  showDataApproval: PropTypes.bool.isRequired,
  showDataApprovalDetails: PropTypes.bool.isRequired,
  showResponsibleUsers: PropTypes.bool.isRequired,
  userList: PropTypes.array,
  arrangedContent: PropTypes.array.isRequired,
  approvalSteps: PropTypes.array,
  actions: PropTypes.object,
  dataApprovalGroup: PropTypes.array,
  languages: PropTypes.array.isRequired,
};

Surveys.defaultProps = {
  step: null,
  userList: null,
  approvalSteps: null,
  approvalGroups: null,
  selectedSurvey: null,
  orderedForms: null,
  dataApprovalGroup: null,
  actions: () => null,
};

export default Surveys;
