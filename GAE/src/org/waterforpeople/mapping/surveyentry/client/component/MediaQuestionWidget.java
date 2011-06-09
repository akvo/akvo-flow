package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.client.util.UploadConstants;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * handles photo and video questions by implementing an upload function
 * 
 * @author Christopher Fagiani
 * 
 */
public class MediaQuestionWidget extends QuestionWidget implements
		SubmitCompleteHandler, ClickHandler {

	private static UploadConstants UPLOAD_CONSTANTS = GWT
			.create(UploadConstants.class);

	private FormPanel form;
	private HorizontalPanel contentPanel;
	private Label statusLabel;
	private HorizontalPanel uploadPanel;
	private Button uploadButton;
	private Button resetButton;
	private FileUpload upload;
	private Hidden contentType;
	private Image completeIcon;
	private String type;

	public MediaQuestionWidget(QuestionDto q, QuestionAnswerStoreDto a, String type) {
		super(q,a);
		if ("PHOTO".equalsIgnoreCase(type)) {
			this.type = "IMAGE";
		} else {
			this.type = type;
		}
	}

	/**
	 * sets up the upload form
	 */
	protected void constructForm() {
		uploadPanel = new HorizontalPanel();
		form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction(UPLOAD_CONSTANTS.uploadUrl());
		form.addSubmitCompleteHandler(this);
		Hidden filePath = new Hidden("key", UPLOAD_CONSTANTS.imageS3Path()
				+ "/${filename}");

		uploadPanel.add(filePath);
		uploadPanel.add(new Hidden("AWSAccessKeyId", UPLOAD_CONSTANTS.s3Id()));
		uploadPanel.add(new Hidden("acl", "public-read"));
		uploadPanel.add(new Hidden("success_action_redirect",
				"http://www.gallatinsystems.com/SuccessUpload.html"));
		Hidden s3Policy = new Hidden("policy");
		s3Policy.setValue(UPLOAD_CONSTANTS.imageS3Policy());
		uploadPanel.add(s3Policy);
		Hidden s3Sig = new Hidden("signature");
		s3Sig.setValue(UPLOAD_CONSTANTS.imageS3Sig());
		uploadPanel.add(s3Sig);
		contentType = new Hidden("Content-Type");
		uploadPanel.add(contentType);

		uploadButton = new Button(TEXT_CONSTANTS.upload());
		uploadButton.addClickHandler(this);
		upload = new FileUpload();
		upload.setName("file");
		uploadPanel.add(upload);
		uploadPanel.add(uploadButton);
		form.setWidget(uploadPanel);
		statusLabel.setVisible(false);
		contentPanel.add(form);
		contentPanel.add(completeIcon);
		contentPanel.add(statusLabel);

		completeIcon.setVisible(false);
	}

	@Override
	protected void constructResponseUi() {

		contentPanel = new HorizontalPanel();
		statusLabel = new Label(TEXT_CONSTANTS.uploading());
		uploadButton = new Button(TEXT_CONSTANTS.upload());
		resetButton = new Button(TEXT_CONSTANTS.clear());
		uploadButton.addClickHandler(this);
		resetButton.addClickHandler(this);
		completeIcon = new Image("images/icon-check.gif");
		constructForm();
		if(getAnswer().getKeyId() != null){
			//if we're initializing and key id is not null, pre-populate
			completeIcon.setVisible(true);			
		}
		getPanel().add(contentPanel);
	}

	/**
	 * turn on the completion icon and the reset button
	 */
	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		statusLabel.setVisible(false);
		completeIcon.setVisible(true);
		contentPanel.add(resetButton);
		form.setVisible(true);
	}

	/**
	 * checks the file extension and invokes the upload
	 */
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
					.getFilename().toLowerCase().endsWith(".mp4") || upload
					.getFilename().toLowerCase().endsWith(".flv") || upload
					.getFilename().toLowerCase().endsWith(".3gp"))) {
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
				statusLabel.setVisible(true);
				form.submit();
			} else {
				MessageDialog dia = new MessageDialog(TEXT_CONSTANTS.inputError(),
						TEXT_CONSTANTS.filetypeError());
				dia.showCentered();
			}
		} else if (event.getSource() == resetButton) {
			super.reset();
		}
	}

	public void captureAnswer() {
		getAnswer().setType(type);
		if (completeIcon.isVisible() && upload.getFilename() != null
				&& upload.getFilename().trim().length() > 0) {
			//we only want the filename (not the path)
			String name = upload.getFilename().trim();
			if(name.contains("/")){
				name = name.substring(name.lastIndexOf("/")+1);
			}if(name.contains("\\")){
				name = name.substring(name.lastIndexOf("\\")+1);
			}
			getAnswer().setValue(name);
		}
	}

	@Override
	protected void resetUi() {
		statusLabel.setVisible(false);
		completeIcon.setVisible(false);
		resetPanels();
	}

	/**
	 * clears the content and re-installs the upload panel
	 */
	private void resetPanels() {
		contentPanel.clear();
		uploadPanel.clear();
		constructForm();
	}
}
