import React from 'react';
import Checkbox from 'akvo-flow/components/Checkbox';
import observe from '../mixins/observe';
/* eslint-disable jsx-a11y/label-has-associated-control, jsx-a11y/label-has-for */
require('akvo-flow/views/react-component');

FLOW.FormSelectorView = FLOW.ReactComponentView.extend(observe({
  'FLOW.surveyControl.content.isLoaded': 'listForms',
}), {
  init() {
    this._super();
    this.handleChange = this.handleChange.bind(this);
    this.listForms = this.listForms.bind(this);
    this.formInAssignment = this.formInAssignment.bind(this);
    this.forms = {};
  },

  didInsertElement(...args) {
    this._super(...args);
    FLOW.selectedControl.selectedSurveyAssignment.get('surveys').forEach((formId) => {
      const form = FLOW.Survey.find(formId);
      if (form && form.get('keyId')) {
        FLOW.selectedControl.selectedSurveys.pushObject(form);
        this.forms[form.get('keyId')] = { // also load pre-selected forms
          name: form.get('name'),
          checked: true,
        };
      }
    });
    this.renderFormsList();
  },

  listForms() {
    this.forms = {};
    FLOW.surveyControl.content.forEach((form) => {
      this.forms[form.get('keyId')] = {
        name: form.get('name'),
        checked: this.formInAssignment(form.get('keyId')),
      };
    });
    this.renderFormsList();
  },

  renderFormsList() {
    this.reactRender(
      <div>
        {Object.keys(this.forms).map(form => (
          <label key={form}>
            <Checkbox
              name={form.toString()}
              onChange={this.handleChange}
              checked={this.forms[form].checked}
            />
            {this.forms[form].name}
          </label>
        ))}
        Forms list
      </div>
    );
  },

  formInAssignment(formId) {
    const formsInAssignment = FLOW.selectedControl.selectedSurveyAssignment.get('surveys');
    return formsInAssignment.indexOf(formId) > -1;
  },

  canAddFormsToAssignment() {
    // only allow if form qualifies
    const formsInAssignment = FLOW.selectedControl.selectedSurveyAssignment.get('surveys');
    const selectedSurveyGroupId = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
    if (formsInAssignment.length > 0) {
      // get survey id of first form currently in assignment
      const preSelectedSurvey = FLOW.Survey.find(formsInAssignment[0]);
      if (preSelectedSurvey && preSelectedSurvey.get('keyId')) {
        return preSelectedSurvey.get('surveyGroupId') == selectedSurveyGroupId;
      }
    }
    return true; // no forms are currently added to the assignment
  },

  handleChange(e) {
    // only allow a form to be checked if a different survey isn't already selected
    const formId = e.target.name;
    if (this.canAddFormsToAssignment()) {
      this.forms[formId].checked = !this.forms[formId].checked;
    } else {
      // TODO: display error that form cannot be added unless currently added forms are removed
    }
    this.renderFormsList();
    // add/remove form to/from assignment
    if (this.forms[formId].checked) {
      // push survey to FLOW.selectedControl.selectedSurveys
      FLOW.selectedControl.selectedSurveys.pushObject(FLOW.Survey.find(formId));
    } else {
      FLOW.selectedControl.selectedSurveys.removeObject(FLOW.Survey.find(formId));
    }
    // TODO: load data points in selected form
  },
});
