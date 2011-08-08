package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyNavigationWidget.MODE;

import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
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

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final String FORM_LABEL_CSS = "form-label";
	private static final String TXT_BOX_CSS = "txt-box";
	private VerticalPanel panel;
	private CaptionPanel nav;
	private SurveyNavigationWidget surveyNavigationWidget;
	private Map<String, Object> bundle;
	private TextBox nameBox;
	private TextBox descriptionBox;
	private TextBox orderBox;
	private SurveyServiceAsync surveyService;
	private SurveyDto surveyDto;
	private SurveyGroupDto groupDto;
	private QuestionGroupDto currentDto;
	private boolean isChanged;
	private PageController controller;

	public QuestionGroupEditWidget(PageController controller) {
		this.controller = controller;
		panel = new VerticalPanel();
		surveyService = GWT.create(SurveyService.class);
		nameBox = new TextBox();
		descriptionBox = new TextBox();
		orderBox = new TextBox();
		orderBox.setReadOnly(true);
		nav = new CaptionPanel(TEXT_CONSTANTS.surveyNavigation());
		panel.add(nav);
		panel.add(buildRow(TEXT_CONSTANTS.name(), nameBox));
		panel.add(buildRow(TEXT_CONSTANTS.description(), descriptionBox));
		panel.add(buildRow(TEXT_CONSTANTS.order(), orderBox));
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
							.getText().trim() : null);

			surveyService.saveQuestionGroup(currentDto, surveyDto.getKeyId(),
					new AsyncCallback<QuestionGroupDto>() {

						@Override
						public void onFailure(Throwable caught) {

							MessageDialog errDia = new MessageDialog(
									TEXT_CONSTANTS.error(), TEXT_CONSTANTS
											.errorTracePrefix()
											+ " "
											+ caught.getLocalizedMessage());
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
					TEXT_CONSTANTS.inputError(),
					TEXT_CONSTANTS.invalidQuestionGroup());
			validationDialog.showRelativeTo(panel);
		}
	}

	@Override
	public void persistContext(String buttonText,CompletionListener listener) {
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
		int order = 1;
		if (currentDto != null) {
			order = currentDto.getOrder();
		} else if (surveyDto.getQuestionGroupList() != null) {
			order = surveyDto.getQuestionGroupList().size() + 1;
		}
		surveyNavigationWidget = new SurveyNavigationWidget(surveyDto, null,
				order, false, MODE.QUESTION_GROUP_EDIT, controller, this);
		nav.add(surveyNavigationWidget);

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
	public void flushContext() {
		// no-op
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
