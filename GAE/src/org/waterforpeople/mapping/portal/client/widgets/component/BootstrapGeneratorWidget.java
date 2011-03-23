package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.Orientation;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
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
 * 
 * @author Christopher Fagiani
 * 
 */
public class BootstrapGeneratorWidget extends Composite implements ClickHandler {

	private static final String LABEL_STYLE = "input-label-padded";
	private static final int DEFAULT_ITEM_COUNT = 5;
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private ListBox selectionListbox;
	private SurveyServiceAsync surveyService;
	private TextArea dbInstructionArea;
	private CheckBox includeDbScriptBox;
	private Button addSurveyButton;
	private Button generateFileButton;
	private Button removeButton;
	private Panel contentPanel;
	private TextBox notificationEmailBox;
	private SurveySelectionWidget selectionWidget;

	List<SurveyDto> currentSurveyDtoList;

	public BootstrapGeneratorWidget() {
		contentPanel = new VerticalPanel();
		contentPanel.setWidth("800px");
		surveyService = GWT.create(SurveyService.class);
		addSurveyButton = new Button(TEXT_CONSTANTS.addSelected());
		addSurveyButton.addClickHandler(this);
		generateFileButton = new Button(TEXT_CONSTANTS.generate());
		generateFileButton.addClickHandler(this);
		removeButton = new Button(TEXT_CONSTANTS.removeSelected());
		removeButton.addClickHandler(this);
		CaptionPanel selectorPanel = new CaptionPanel(TEXT_CONSTANTS
				.selectSurveyForInclusion());
		HorizontalPanel temp = new HorizontalPanel();
		selectionWidget = new SurveySelectionWidget(Orientation.HORIZONTAL,
				TerminalType.SURVEY);
		temp.add(selectionWidget);
		temp.add(addSurveyButton);
		selectorPanel.add(temp);
		contentPanel.add(selectorPanel);
		CaptionPanel zipPanel = new CaptionPanel(TEXT_CONSTANTS.fileContents());
		selectionListbox = new ListBox(true);
		selectionListbox.setVisibleItemCount(DEFAULT_ITEM_COUNT);
		VerticalPanel zipPanelContent = new VerticalPanel();
		HorizontalPanel selectedSurveyPanel = new HorizontalPanel();
		ViewUtil.installFieldRow(selectedSurveyPanel, TEXT_CONSTANTS
				.selectedSurveys(), selectionListbox, LABEL_STYLE);
		selectedSurveyPanel.add(removeButton);
		zipPanelContent.add(selectedSurveyPanel);
		temp = new HorizontalPanel();
		includeDbScriptBox = new CheckBox();
		includeDbScriptBox.addClickHandler(this);
		ViewUtil.installFieldRow(temp, TEXT_CONSTANTS.includeDB(),
				includeDbScriptBox, LABEL_STYLE);
		zipPanelContent.add(temp);
		dbInstructionArea = new TextArea();
		dbInstructionArea.setVisible(false);
		zipPanelContent.add(dbInstructionArea);
		temp = new HorizontalPanel();
		notificationEmailBox = new TextBox();
		ViewUtil.installFieldRow(temp, TEXT_CONSTANTS.notificationEmail(),
				notificationEmailBox, LABEL_STYLE);
		zipPanelContent.add(temp);
		zipPanelContent.add(generateFileButton);
		zipPanel.add(zipPanelContent);
		contentPanel.add(zipPanel);
		initWidget(contentPanel);
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
			String group = selectionWidget.getSelectedSurveyGroupName();
			List<String> name = selectionWidget.getSelectedSurveyNames();
			List<Long> ids = selectionWidget.getSelectedSurveyIds();
			for (int i = 0; i < name.size(); i++) {
				boolean alreadyThere = false;
				for (int j = 0; j < selectionListbox.getItemCount(); j++) {
					if (selectionListbox.getValue(j).equals(
							ids.get(i).toString())) {
						alreadyThere = true;
						break;
					}
				}
				if (!alreadyThere) {
					selectionListbox.addItem(group + ": " + name.get(i), ids
							.get(i).toString());
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
										TEXT_CONSTANTS.error(), TEXT_CONSTANTS
												.errorTracePrefix()
												+ " "
												+ caught.getLocalizedMessage());
								errDia.showCentered();

							}

							@Override
							public void onSuccess(Void result) {
								MessageDialog dia = new MessageDialog(
										TEXT_CONSTANTS.requestSubmitted(),
										TEXT_CONSTANTS.emailWillBeSent());
								dia.showCentered();
								resetUI();
							}
						});
			} else {
				StringBuilder builder = new StringBuilder(TEXT_CONSTANTS
						.pleaseCorrect()
						+ "<br><ul>");
				for (String e : errors) {
					builder.append("<li>").append(e).append("</li>");
				}
				builder.append("</ul>");
				MessageDialog dia = new MessageDialog(TEXT_CONSTANTS
						.inputError(), builder.toString());
				dia.showCentered();
			}
		}
	}

	private void resetUI() {
		dbInstructionArea.setText("");
		dbInstructionArea.setVisible(false);
		includeDbScriptBox.setValue(false);
		selectionListbox.clear();
		selectionWidget.reset();
		notificationEmailBox.setText("");
	}

	private List<String> validate() {
		List<String> errors = new ArrayList<String>();
		if (selectionListbox.getItemCount() == 0
				&& !ViewUtil.isTextPopulated(dbInstructionArea)) {
			errors.add(TEXT_CONSTANTS.noDBSurveyMandatory());
		}
		if (!ViewUtil.isTextPopulated(notificationEmailBox)) {
			errors.add(TEXT_CONSTANTS.emailMandatory());
		}
		return errors;
	}
}
