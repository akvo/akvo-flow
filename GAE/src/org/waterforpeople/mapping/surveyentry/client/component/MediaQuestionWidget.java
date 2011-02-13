package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.util.UploadConstants;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
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
	private HorizontalPanel uploadPanel;
	private Button uploadButton;
	private FileUpload upload;
	private Hidden contentType;
	private Image completeIcon;
	private String type;

	public MediaQuestionWidget(QuestionDto q, String type) {
		super(q);
		if("PHOTO".equalsIgnoreCase(type)){
			this.type = "IMAGE";
		}else{
			this.type = type;
		}
	}

	@Override
	protected void constructResponseUi() {
		uploadPanel = new HorizontalPanel();
		uploadButton = new Button("Upload");
		completeIcon = new Image("images/icon-check.gif");
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
		uploadPanel.add(completeIcon);
		getPanel().add(form);

		completeIcon.setVisible(false);

	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	public void captureAnswer() {
		getAnswer().setType(type);
		if (upload.getFilename() != null
				&& upload.getFilename().trim().length() > 0) {
			getAnswer().setValue(upload.getFilename().trim());
		}
	}

	@Override
	protected void resetUi() {
		form.reset();
	}
}
