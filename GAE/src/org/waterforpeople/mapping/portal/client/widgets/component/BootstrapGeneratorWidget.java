package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Admin widget for generating a bootstrap.zip file and storing it on S3. Users
 * can select any number of surveys and optionally supply a set of db
 * instructions. They will be emailed when the zip file is ready for download.
 * 
 * TODO: refactor survey group/survey loading into a shared component
 * 
 * @author Christopher Fagiani
 * 
 */
public class BootstrapGeneratorWidget extends Composite implements
		ClickHandler, ChangeHandler {

	private static final String LABEL_STYLE = "input-label-padded";
	private static final int DEFAULT_ITEM_COUNT = 5;
	private ListBox surveyGroupListbox;
	private ListBox surveyListbox;
	private ListBox selectionListbox;
	private SurveyServiceAsync surveyService;
	private TextArea dbInstructionArea;
	private CheckBox includeDbScriptBox;
	private Button addSurveyButton;
	private Button generateFileButton;
	private Button removeButton;
	private Panel contentPanel;
	private MessageDialog loadingDialog;
	private TextBox notificationEmailBox;

	List<SurveyDto> currentSurveyDtoList;
	private Map<String, List<SurveyDto>> surveys;

	public BootstrapGeneratorWidget() {
		surveys = new HashMap<String, List<SurveyDto>>();
		contentPanel = new VerticalPanel();
		loadingDialog = new MessageDialog("Loading...", "Please wait.");
		contentPanel.setWidth("800px");
		surveyService = GWT.create(SurveyService.class);
		surveyGroupListbox = new ListBox();
		surveyGroupListbox.addChangeHandler(this);
		surveyListbox = new ListBox(true);
		surveyListbox.setVisibleItemCount(DEFAULT_ITEM_COUNT);
		addSurveyButton = new Button("Add Selected");
		addSurveyButton.addClickHandler(this);
		generateFileButton = new Button("Generate");
		generateFileButton.addClickHandler(this);
		removeButton = new Button("Remove Selected");
		removeButton.addClickHandler(this);
		CaptionPanel selectorPanel = new CaptionPanel(
				"Select Surveys for Inclusion");
		HorizontalPanel temp = new HorizontalPanel();
		ViewUtil.installFieldRow(temp, "Survey Group", surveyGroupListbox,
				LABEL_STYLE);
		ViewUtil.installFieldRow(temp, "Surveys", surveyListbox, LABEL_STYLE);
		temp.add(addSurveyButton);
		selectorPanel.add(temp);
		contentPanel.add(selectorPanel);
		CaptionPanel zipPanel = new CaptionPanel("File Contents");
		selectionListbox = new ListBox(true);
		selectionListbox.setVisibleItemCount(DEFAULT_ITEM_COUNT);
		VerticalPanel zipPanelContent = new VerticalPanel();
		HorizontalPanel selectedSurveyPanel = new HorizontalPanel();
		ViewUtil.installFieldRow(selectedSurveyPanel, "Selected Surveys",
				selectionListbox, LABEL_STYLE);
		selectedSurveyPanel.add(removeButton);
		zipPanelContent.add(selectedSurveyPanel);
		temp = new HorizontalPanel();
		includeDbScriptBox = new CheckBox();
		includeDbScriptBox.addClickHandler(this);
		ViewUtil.installFieldRow(temp, "Include DB Instructions?",
				includeDbScriptBox, LABEL_STYLE);
		zipPanelContent.add(temp);
		dbInstructionArea = new TextArea();
		dbInstructionArea.setVisible(false);
		zipPanelContent.add(dbInstructionArea);
		temp = new HorizontalPanel();
		notificationEmailBox = new TextBox();
		ViewUtil.installFieldRow(temp, "Notification Email:",
				notificationEmailBox, LABEL_STYLE);
		zipPanelContent.add(temp);
		zipPanelContent.add(generateFileButton);
		zipPanel.add(zipPanelContent);
		contentPanel.add(zipPanel);
		initWidget(contentPanel);
		loadSurveyGroups();
	}

	/**
	 * loads the survey groups
	 */
	private void loadSurveyGroups() {
		surveyService.listSurveyGroups("all", false, false, false,
				new AsyncCallback<ArrayList<SurveyGroupDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(
								"Application Error",
								"Cannot load survey groups");
						errDia.showCentered();
					}

					@Override
					public void onSuccess(ArrayList<SurveyGroupDto> result) {
						surveyGroupListbox.addItem("", "");
						if (result != null) {
							int i = 0;
							for (SurveyGroupDto dto : result) {
								surveyGroupListbox.addItem(dto.getCode(), dto
										.getKeyId().toString());
								i++;
							}
						}
						toggleLoading(false);
					}
				});
	}

	private void getSurveys() {
		if (surveyGroupListbox.getSelectedIndex() > 0) {
			final String selectedGroupId = surveyGroupListbox
					.getValue(surveyGroupListbox.getSelectedIndex());

			if (selectedGroupId != null) {
				if (surveys.get(selectedGroupId) != null) {
					populateSurveyList(surveys.get(selectedGroupId));
				} else {
					// Set up the callback object.
					AsyncCallback<ArrayList<SurveyDto>> surveyCallback = new AsyncCallback<ArrayList<SurveyDto>>() {
						public void onFailure(Throwable caught) {
							toggleLoading(false);
							MessageDialog errDia = new MessageDialog(
									"Cannot list surveys",
									"The application encountered an error: "
											+ caught.getLocalizedMessage());
							errDia.showCentered();
						}

						public void onSuccess(ArrayList<SurveyDto> result) {
							if (result != null) {
								surveys.put(selectedGroupId, result);
								populateSurveyList(result);
								toggleLoading(false);
							}
						}
					};
					toggleLoading(true);
					surveyService.listSurveysByGroup(selectedGroupId,
							surveyCallback);
				}
			} else {
				toggleLoading(false);
				MessageDialog errDia = new MessageDialog(
						"Please select a group",
						"You must select a survey group first");
				errDia.showCentered();
			}
		} else {
			surveyListbox.clear();
		}
	}

	/**
	 * shows/hides the loading dialog box
	 * 
	 * @param show
	 */
	private void toggleLoading(boolean show) {
		if (!show) {
			loadingDialog.hide();
		} else {
			loadingDialog.showCentered();
		}
	}

	private void populateSurveyList(List<SurveyDto> surveyItems) {
		surveyListbox.clear();
		currentSurveyDtoList = surveyItems;
		if (surveyItems != null) {
			int i = 0;
			for (SurveyDto survey : surveyItems) {
				surveyListbox.addItem(survey.getName() != null ? survey
						.getName() : "Survey " + survey.getKeyId().toString(),
						survey.getKeyId().toString());

				i++;
			}
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == surveyGroupListbox) {
			getSurveys();
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == includeDbScriptBox) {
			if (includeDbScriptBox.getValue()) {
				dbInstructionArea.setValue("");
				dbInstructionArea.setVisible(true);
			} else {
				dbInstructionArea.setValue("");
				dbInstructionArea.setVisible(false);
			}
		} else if (event.getSource() == addSurveyButton) {
			String group = surveyGroupListbox.getItemText(surveyGroupListbox
					.getSelectedIndex());
			for (int i = 0; i < surveyListbox.getItemCount(); i++) {
				if (surveyListbox.isItemSelected(i)) {
					selectionListbox.addItem(group + ": "
							+ surveyListbox.getItemText(i), surveyListbox
							.getValue(i));
				}
			}
		} else if (event.getSource() == removeButton) {
			List<Integer> victimList = new ArrayList<Integer>();
			for (int i = 0; i < selectionListbox.getItemCount(); i++) {
				if (selectionListbox.isItemSelected(i)) {
					victimList.add(i);
				}
			}
			if (victimList.size() > 0) {
				// remove the items in descending order so we don't have to
				// worry about adjusting indexes as we remove
				Collections.sort(victimList);
				for (int i = victimList.size() - 1; i >= 0; i--) {
					selectionListbox.removeItem(victimList.get(i));
				}
			}
		} else if (event.getSource() == generateFileButton) {

			List<Long> idList = new ArrayList<Long>();
			for (int i = 0; i < selectionListbox.getItemCount(); i++) {
				idList.add(new Long(selectionListbox.getValue(i)));
			}

			List<String> errors = validate();
			if (errors.size() == 0) {
				surveyService.generateBootstrapFile(idList, dbInstructionArea
						.getText(), notificationEmailBox.getText(),
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								MessageDialog errDia = new MessageDialog(
										"Error",
										"Could not submit generation request");
								errDia.showCentered();

							}

							@Override
							public void onSuccess(Void result) {
								MessageDialog dia = new MessageDialog(
										"Request Submitted",
										"An email will be sent when the file is available");
								dia.showCentered();
								resetUI();
							}
						});
			} else {
				StringBuilder builder = new StringBuilder(
						"Please corect the following errors and try again: <br><ul>");
				for (String e : errors) {
					builder.append("<li>").append(e).append("</li>");
				}
				builder.append("</ul>");
				MessageDialog dia = new MessageDialog("Missing data", builder
						.toString());
				dia.showCentered();
			}
		}
	}

	private void resetUI() {
		dbInstructionArea.setText("");
		dbInstructionArea.setVisible(false);
		includeDbScriptBox.setValue(false);
		selectionListbox.clear();
		surveyListbox.clear();
		surveyGroupListbox.setSelectedIndex(0);
		notificationEmailBox.setText("");
	}

	private List<String> validate() {
		List<String> errors = new ArrayList<String>();
		if (selectionListbox.getItemCount() == 0
				&& !ViewUtil.isTextPopulated(dbInstructionArea)) {
			errors
					.add("If no db instructions are included, then at least 1 survey must be selected");
		}
		if (!ViewUtil.isTextPopulated(notificationEmailBox)) {
			errors.add("You must provide a notification email address");
		}
		return errors;
	}
}
