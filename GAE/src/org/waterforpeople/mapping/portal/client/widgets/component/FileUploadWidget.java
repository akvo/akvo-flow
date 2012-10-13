/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */
package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.app.gwt.client.util.UploadConstants;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * reusable widget for uploading a single file to S3
 * 
 * @author Christopher Fagiani
 * 
 */
public class FileUploadWidget extends Composite implements ClickHandler,
		SubmitCompleteHandler {

	private static UploadConstants UPLOAD_CONSTANTS = GWT
			.create(UploadConstants.class);

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private Panel contentPane;
	private Button submitBtn;
	private FormPanel form;
	private FileUpload upload;
	private Label statusLabel;
	private Hidden filePath;
	private Hidden s3Policy;
	private Hidden s3Sig;
	private Hidden contentType;
	private FileUploadHandler uploadHandler;
	private String[] acceptableExtensions;

	public FileUploadWidget(FileUploadHandler handler,
			String... validExtensions) {
		contentPane = new VerticalPanel();
		uploadHandler = handler;
		acceptableExtensions = validExtensions;

		VerticalPanel tempPanel = new VerticalPanel();
		form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction(UPLOAD_CONSTANTS.uploadUrl());
		form.addSubmitCompleteHandler(this);
		filePath = new Hidden("key");

		tempPanel.add(filePath);
		tempPanel.add(new Hidden("AWSAccessKeyId", UPLOAD_CONSTANTS.s3Id()));
		tempPanel.add(new Hidden("acl", "public-read"));
		tempPanel.add(new Hidden("success_action_redirect",
				"http://www.gallatinsystems.com/SuccessUpload.html"));
		s3Policy = new Hidden("policy");
		tempPanel.add(s3Policy);
		s3Sig = new Hidden("signature");
		tempPanel.add(s3Sig);
		contentType = new Hidden("Content-Type");
		tempPanel.add(contentType);

		form.setWidth("275px");

		upload = new FileUpload();
		upload.setName("file");
		tempPanel.add(upload);
		form.setWidget(tempPanel);
		contentPane.add(form);

		submitBtn = new Button(TEXT_CONSTANTS.upload());
		submitBtn.addClickHandler(this);
		contentPane.add(submitBtn);
		statusLabel = new Label();
		statusLabel.setVisible(false);
		contentPane.add(statusLabel);
		initWidget(contentPane);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == submitBtn) {
			boolean valid = validateFile(upload.getFilename());
			if (upload.getFilename() == null
					|| upload.getFilename().trim().length() == 0) {
				valid = false;
			} else if (!(upload.getFilename().toLowerCase().endsWith(".zip")
					|| upload.getFilename().toLowerCase().endsWith(".jpg") || upload
					.getFilename().toLowerCase().endsWith(".jpeg"))) {
				valid = false;
			}
			if (valid) {
				configureForm(upload.getFilename().trim());
				submitBtn.setVisible(false);
				statusLabel.setText(TEXT_CONSTANTS.uploading());
				statusLabel.setVisible(true);
				form.submit();
			} else {
				MessageDialog dia = new MessageDialog(TEXT_CONSTANTS.error(),
						TEXT_CONSTANTS.filetypeError());
				dia.showCentered();
			}
		}
	}

	protected boolean validateFile(String file) {
		boolean valid = true;
		if (file == null || file.trim().length() == 0) {
			valid = false;
		} else if (acceptableExtensions != null
				&& acceptableExtensions.length > 0) {
			String trimmedName = file.trim().toLowerCase();
			valid = false;
			for (String ext : acceptableExtensions) {
				if (trimmedName.endsWith(ext.trim().toLowerCase())) {
					valid = true;
					break;
				}
			}
		}
		return valid;
	}

	/**
	 * sets the hidden variables on the form so we can do the upload
	 * 
	 * @param path
	 * @param sig
	 * @param policy
	 * @param contentType
	 */
	private void configureForm(String fileName) {
		Map<String, String> uploadMap = new HashMap<String, String>();
		uploadHandler.configureUploadMap(uploadMap, fileName);
		filePath.setValue(uploadMap.get(FileUploadHandler.PATH)
				+ "/${filename}");
		s3Sig.setValue(uploadMap.get(FileUploadHandler.SIG));
		s3Policy.setValue(uploadMap.get(FileUploadHandler.POLICY));
		this.contentType
				.setValue(uploadMap.get(FileUploadHandler.CONTENT_TYPE));
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		submitBtn.setVisible(true);
		if (event.getResults() == null) {
			statusLabel.setText(TEXT_CONSTANTS.uploadComplete());
			// delegate to the client class so they can take any required
			// action
			uploadHandler.onSubmitSuccess(event, upload.getFilename());

		} else {
			statusLabel.setText(TEXT_CONSTANTS.uploadError());
		}
	}

	public interface FileUploadHandler {
		public static final String PATH = "path";
		public static final String CONTENT_TYPE = "contentType";
		public static final String SIG = "sig";
		public static final String POLICY = "policy";

		public void onSubmitSuccess(SubmitCompleteEvent event, String fileName);

		public void configureUploadMap(Map<String, String> uploadMap,
				String fileName);
	}

}
