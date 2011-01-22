package org.waterforpeople.mapping.portal.client.widgets.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.portal.client.util.UploadConstants;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * Widget that can handle the rendering and update of a QuestionHelpMedia object
 * 
 * @author Christopher Fagiani
 * 
 */
public class HelpMediaWidget extends Composite implements
		SubmitCompleteHandler, ClickHandler, ChangeHandler {

	private static UploadConstants UPLOAD_CONSTANTS = GWT
			.create(UploadConstants.class);

	private HorizontalPanel panel;
	private HorizontalPanel uploadPanel;
	private HorizontalPanel activityPanel;
	private HorizontalPanel textPanel;
	private HorizontalPanel statusPanel;
	private FormPanel form;
	private FileUpload upload;
	private ListBox typeSelector;
	private ListBox activitySelector;
	private TextBox helpText;
	private Hidden contentType;
	private Button uploadButton;
	private QuestionHelpDto helpDto;
	private String uploadedFile;

	public HelpMediaWidget(QuestionHelpDto helpDto) {
		this.helpDto = helpDto;
		panel = new HorizontalPanel();
		typeSelector = new ListBox();
		for (QuestionHelpDto.Type type : QuestionHelpDto.Type.values()) {
			typeSelector.addItem(type.toString(), type.toString());
		}
		typeSelector.addChangeHandler(this);
		ViewUtil.installFieldRow(panel, "Type", typeSelector, null);

		statusPanel = new HorizontalPanel();
		uploadPanel = new HorizontalPanel();
		textPanel = new HorizontalPanel();
		panel.add(textPanel);
		panel.add(statusPanel);
		helpText = new TextBox();
		textPanel.add(helpText);
		activityPanel = new HorizontalPanel();
		activitySelector = new ListBox();
		panel.add(activityPanel);
		activityPanel.setVisible(false);
		installHelpActivities(activitySelector);
		ViewUtil.installFieldRow(activityPanel, "Helper Activity",
				activitySelector, null);

		form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction(UPLOAD_CONSTANTS.uploadUrl());
		form.addSubmitCompleteHandler(this);
		Hidden filePath = new Hidden("key", UPLOAD_CONSTANTS.helpS3Path()
				+ "/${filename}");

		uploadPanel.add(filePath);
		uploadPanel.add(new Hidden("AWSAccessKeyId", UPLOAD_CONSTANTS.s3Id()));
		uploadPanel.add(new Hidden("acl", "public-read"));
		uploadPanel.add(new Hidden("success_action_redirect",
				"http://www.gallatinsystems.com/SuccessUpload.html"));
		Hidden s3Policy = new Hidden("policy");
		s3Policy.setValue(UPLOAD_CONSTANTS.helpS3Policy());
		uploadPanel.add(s3Policy);
		Hidden s3Sig = new Hidden("signature");
		s3Sig.setValue(UPLOAD_CONSTANTS.helpS3Sig());
		uploadPanel.add(s3Sig);
		contentType = new Hidden("Content-Type");
		uploadPanel.add(contentType);

		uploadButton = new Button("Upload");
		uploadButton.addClickHandler(this);
		upload = new FileUpload();
		upload.setName("file");
		uploadPanel.add(upload);
		uploadPanel.add(uploadButton);
		form.setWidget(uploadPanel);
		panel.add(form);
		bindValues();
		initWidget(panel);
	}

	private void bindValues() {
		if (helpDto != null && helpDto.getType() != null) {
			ViewUtil.setListboxSelection(typeSelector, helpDto.getType()
					.toString());
			toggleTypePanels(helpDto.getType().toString());
			if ((QuestionHelpDto.Type.PHOTO == helpDto.getType() || QuestionHelpDto.Type.VIDEO == helpDto
					.getType())
					&& helpDto.getResourceUrl() != null) {
				statusPanel.setVisible(true);
				if (helpDto.getResourceUrl().contains("/")) {
					statusPanel.add(new Label(
							helpDto.getResourceUrl()
									.substring(
											helpDto.getResourceUrl()
													.lastIndexOf("/") + 1)));
				} else {
					statusPanel.add(new Label(helpDto.getResourceUrl()));
				}
			}
			if (helpDto.getText() != null) {
				helpText.setValue(helpDto.getText());
			}
			if (helpDto.getType() == QuestionHelpDto.Type.ACTIVITY) {
				ViewUtil.setListboxSelection(activitySelector, helpDto
						.getResourceUrl());
			}
		}
	}

	public QuestionHelpDto getHelpDto() {
		if (helpDto == null) {
			helpDto = new QuestionHelpDto();
		}
		helpDto.setType(QuestionHelpDto.Type.valueOf(typeSelector
				.getValue(typeSelector.getSelectedIndex())));
		helpDto.setText(helpText.getValue());
		if (QuestionHelpDto.Type.ACTIVITY == helpDto.getType()) {
			helpDto.setResourceUrl(activitySelector.getValue(activitySelector
					.getSelectedIndex()));
		} else {
			helpDto.setResourceUrl(UPLOAD_CONSTANTS.uploadUrl()
					+ UPLOAD_CONSTANTS.helpS3Path() + "/" + uploadedFile);
		}
		return helpDto;
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		statusPanel.clear();
		uploadedFile = upload.getFilename();
		if (uploadedFile != null) {
			if (uploadedFile.contains("\\")) {
				uploadedFile = uploadedFile.substring(uploadedFile
						.lastIndexOf("\\") + 1);
			}
			if (uploadedFile.contains("/")) {
				uploadedFile = uploadedFile.substring(uploadedFile
						.lastIndexOf("/") + 1);
			}
		}
		statusPanel.add(new Label("Uploaded " + uploadedFile));
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == uploadButton) {
			boolean valid = true;
			if (upload.getFilename() == null
					|| upload.getFilename().trim().length() == 0) {
				valid = false;
			} else if (!(upload.getFilename().toLowerCase().endsWith(".mpeg")
					|| upload.getFilename().toLowerCase().endsWith(".jpg")
					|| upload.getFilename().toLowerCase().endsWith(".jpeg") || upload
					.getFilename().toLowerCase().endsWith(".mp4"))) {
				valid = false;
			}
			if (valid) {
				if (upload.getFilename().toLowerCase().endsWith(".jpg")
						|| upload.getFilename().toLowerCase().endsWith(".jpeg")) {
					contentType.setValue(UPLOAD_CONSTANTS.imageContentType());
				} else {
					contentType.setValue(UPLOAD_CONSTANTS.videoContentType());
				}

				uploadPanel.setVisible(false);
				statusPanel.setVisible(true);
				statusPanel.clear();
				statusPanel.add(new Label("Uploading..."));
				form.submit();
			} else {
				MessageDialog dia = new MessageDialog("Error",
						"You must specify either a jpg or mp4 file");
				dia.showCentered();
			}
		}
	}

	private void toggleTypePanels(String type) {
		statusPanel.clear();
		statusPanel.setVisible(false);
		if (QuestionHelpDto.Type.TEXT.toString().equals(type)) {
			uploadPanel.setVisible(false);
			activityPanel.setVisible(false);

		} else if (QuestionHelpDto.Type.ACTIVITY.toString().equals(type)) {
			uploadPanel.setVisible(false);
			activityPanel.setVisible(true);
		} else {
			uploadPanel.setVisible(true);
			activityPanel.setVisible(false);
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == typeSelector) {
			toggleTypePanels(typeSelector.getValue(typeSelector
					.getSelectedIndex()));
		}
	}

	private void installHelpActivities(ListBox box) {
		box.addItem("waterflowcalculator", "waterflowcalculator");
		box.addItem("nearbypoint", "nearbypoint");
	}
}
