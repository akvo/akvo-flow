package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget for editing question group objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionGroupEditWidget extends Composite implements ContextAware,
		ChangeHandler {

	private static final String FORM_LABEL_CSS = "form-label";
	private static final String TXT_BOX_CSS = "txt-box";
	private VerticalPanel panel;
	private Map<String, Object> bundle;
	private TextBox nameBox;
	private TextBox descriptionBox;
	private TextBox orderBox;
	private SurveyServiceAsync surveyService;
	private SurveyDto surveyDto;
	private SurveyGroupDto groupDto;
	private QuestionGroupDto currentDto;
	private boolean isChanged;

	public QuestionGroupEditWidget() {
		panel = new VerticalPanel();
		surveyService = GWT.create(SurveyService.class);
		nameBox = new TextBox();
		descriptionBox = new TextBox();
		orderBox = new TextBox();
		orderBox.setReadOnly(true);
		panel.add(buildRow("Name: ", nameBox));
		panel.add(buildRow("Description: ", descriptionBox));
		panel.add(buildRow("Order: ", orderBox));
		currentDto = null;
		initWidget(panel);
	}

	private Widget buildRow(String label, TextBox box) {
		Label l = new Label();
		l.setText(label);
		l.setStylePrimaryName(FORM_LABEL_CSS);
		box.setStylePrimaryName(TXT_BOX_CSS);
		box.addChangeHandler(this);
		HorizontalPanel row = new HorizontalPanel();
		row.add(l);
		row.add(box);
		return row;
	}

	private void populateWidgets() {
		isChanged = false;
		if (currentDto != null) {
			nameBox.setText(currentDto.getCode());
			descriptionBox.setText(currentDto.getDescription());
			orderBox.setText(currentDto.getOrder() != null ? currentDto
					.getOrder().toString() : null);
		}
	}

	private boolean validateInput() {
		return ViewUtil.isTextPopulated(nameBox);
	}

	/**
	 * validates and saves the group
	 * 
	 * @param listener
	 */
	public void saveSurveyGroup(final CompletionListener listener) {
		if (validateInput()) {
			if (currentDto == null) {
				currentDto = new QuestionGroupDto();
				currentDto.setPath(groupDto.getCode() + "/"
						+ surveyDto.getName());
				currentDto.setSurveyId(surveyDto.getKeyId());
			}
			currentDto.setCode(nameBox.getText().trim());
			currentDto.setName(nameBox.getText().trim());
			currentDto
					.setDescription(descriptionBox.getText() != null ? descriptionBox
							.getText().trim()
							: null);

			surveyService.saveQuestionGroup(currentDto, surveyDto.getKeyId(),
					new AsyncCallback<QuestionGroupDto>() {

						@Override
						public void onFailure(Throwable caught) {

							MessageDialog errDia = new MessageDialog(
									"Could not save question group",
									"There was an error while attempting to save the question group. Please try again. If the problem persists, please contact an administrator");
							errDia.showRelativeTo(panel);
							if (listener != null) {
								listener.operationComplete(false, null);
							}
						}

						@Override
						public void onSuccess(QuestionGroupDto result) {

							currentDto = result;
							if (listener != null) {
								listener.operationComplete(true,
										getContextBundle(true));
							}
						}
					});
		} else {
			MessageDialog validationDialog = new MessageDialog(
					"Invalid Question Group",
					"The question group name must contain at least 1 character.");
			validationDialog.showRelativeTo(panel);
		}
	}

	@Override
	public void persistContext(CompletionListener listener) {
		if (isChanged) {
			saveSurveyGroup(listener);
		} else {
			listener.operationComplete(true, getContextBundle(true));
		}
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		currentDto = (QuestionGroupDto) bundle
				.get(BundleConstants.QUESTION_GROUP_KEY);
		surveyDto = (SurveyDto) bundle.get(BundleConstants.SURVEY_KEY);
		groupDto = (SurveyGroupDto) bundle
				.get(BundleConstants.SURVEY_GROUP_KEY);
		flushContext();
		populateWidgets();
	}

	@Override
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		if (bundle == null) {
			bundle = new HashMap<String, Object>();
		}
		if (doPopulation) {
			bundle.put(BundleConstants.QUESTION_GROUP_KEY, currentDto);
		}
		return bundle;
	}
	
	@Override
	public void flushContext(){
		//no-op
	}
	
	@Override
	public void onChange(ChangeEvent event) {
		if (currentDto != null) {
			if (currentDto.getCode() != null
					&& !currentDto.getCode().equals(nameBox.getText())) {
				isChanged = true;
			} else if (currentDto.getDescription() != null
					&& !currentDto.getDescription().equals(
							descriptionBox.getText())) {
				isChanged = true;
			} else if ((ViewUtil.isTextPopulated(nameBox) && currentDto
					.getCode() == null)
					|| (ViewUtil.isTextPopulated(descriptionBox) && currentDto
							.getDescription() == null)) {
				isChanged = true;
			} else {
				isChanged = false;
			}
		} else {
			// if we haven't saved, set isChanged to true if either field is non
			// empty
			isChanged = (ViewUtil.isTextPopulated(nameBox) || ViewUtil
					.isTextPopulated(descriptionBox));
		}
	}
}
