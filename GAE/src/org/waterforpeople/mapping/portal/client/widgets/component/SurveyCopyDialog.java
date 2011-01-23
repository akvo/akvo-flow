package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog box used to copy an entire survey to a new survey
 * 
 * @author Christopher Fagiani
 *
 */
public class SurveyCopyDialog extends DialogBox {
	private static final String TITLE = "Copy Survey";
	private HorizontalPanel controlPanel;
	private VerticalPanel mainPanel;
	private ListBox surveyGroupList;
	private TextBox surveyName;
	private SurveyServiceAsync surveyService;
	private CompletionListener listener;
	private Label statusLabel;
	private DockPanel contentPane;
	private SurveyDto surveyDto;
	private Button okButton;
	private Button cancelButton;
	private boolean enabled;

	/**
	 * instantiates and displays the dialog box using the survey in the dto
	 * argument as the survey to copy. Upon load, the list of destination survey
	 * groups will be displayed.
	 * 
	 * @param dto
	 * @param listener
	 */
	public SurveyCopyDialog(SurveyDto dto, CompletionListener listener) {
		setText(TITLE);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		surveyDto = dto;
		this.listener = listener;
		mainPanel = new VerticalPanel();
		surveyService = GWT.create(SurveyService.class);
		statusLabel = new Label("Loading...");
		contentPane = new DockPanel();
		setPopupPosition(Window.getClientWidth() / 4,
				Window.getClientHeight() / 4);
		contentPane.add(statusLabel, DockPanel.CENTER);

		controlPanel = new HorizontalPanel();
		surveyGroupList = new ListBox();
		surveyName = new TextBox();
		mainPanel.add(controlPanel);
		ViewUtil.installFieldRow(controlPanel, "Destination Group",
				surveyGroupList, null);
		ViewUtil.installFieldRow(controlPanel, "New Survey name", surveyName,
				null);
		HorizontalPanel buttonPanel = new HorizontalPanel();

		okButton = new Button("Copy");
		cancelButton = new Button("Cancel");
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		contentPane.add(buttonPanel, DockPanel.SOUTH);
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				performCopy();
			}
		});
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		setWidget(contentPane);
		loadData();
	}

	private void loadData() {
		surveyService.listSurveyGroups(null, false, false, false,
				new AsyncCallback<ArrayList<SurveyGroupDto>>() {

					@Override
					public void onSuccess(ArrayList<SurveyGroupDto> result) {
						if (result != null) {
							for (SurveyGroupDto group : result) {
								surveyGroupList.addItem(group.getDisplayName(),
										group.getKeyId().toString());
								statusLabel.removeFromParent();
								contentPane.add(mainPanel, DockPanel.CENTER);
							}
						} else {
							statusLabel.setText("No survey groups");
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						statusLabel
								.setText("Could not load survey groups. Please close this dialog and try again: "
										+ caught.getLocalizedMessage());

					}
				});
	}

	private void performCopy() {
		disableEnableAll(false);
		statusLabel.setText("Copying Survey...");
		mainPanel.add(statusLabel);
	}

	private void disableEnableAll(boolean isEnabled) {
		enabled = isEnabled;
		cancelButton.setEnabled(isEnabled);
		okButton.setEnabled(isEnabled);
	}

	/**
	 * allow the user to press escape to close
	 */
	@Override
	public boolean onKeyDownPreview(char key, int modifiers) {
		// if (enabled) {
		switch (key) {
		case KeyCodes.KEY_ESCAPE:
			hide();
			return true;
		}
		// }
		return false;
	}

}
