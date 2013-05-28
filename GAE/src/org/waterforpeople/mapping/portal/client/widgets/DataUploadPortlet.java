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

package org.waterforpeople.mapping.portal.client.widgets;

import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.app.gwt.client.util.UploadConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.FileUploadWidget;
import org.waterforpeople.mapping.portal.client.widgets.component.FileUploadWidget.FileUploadHandler;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * portlet for manual upload of data zip files
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataUploadPortlet extends Portlet implements FileUploadHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private static UploadConstants UPLOAD_CONSTANTS = GWT
			.create(UploadConstants.class);

	public static final String NAME = TEXT_CONSTANTS.uploadPortletTitle();

	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private static final String NOTIFICATION_URL = "/processor";
	// NOTE: the S3 policies expire on 10/2/2013
	private static final String DATA_S3_POLICY = UPLOAD_CONSTANTS
			.surveyDataS3Policy();
	private static final String DATA_S3_SIG = UPLOAD_CONSTANTS
			.surveyDataS3Sig();

	private static final String S3_DATA_FILE_PATH = UPLOAD_CONSTANTS
			.surveyDataS3Path();

	private static final String DATA_CONTENT_TYPE = "application/zip";
	private static final String IMAGE_S3_POLICY = UPLOAD_CONSTANTS
			.imageS3Policy();
	private static final String IMAGE_S3_SIG = UPLOAD_CONSTANTS.imageS3Sig();

	private static final String S3_IMAGE_FILE_PATH = UPLOAD_CONSTANTS
			.imageS3Path();
	private static final String IMAGE_CONTENT_TYPE = "image/jpeg";

	private TextBox phoneNumberBox;
	private VerticalPanel contentPane;
	private FileUploadWidget uploadWidget;

	public DataUploadPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		contentPane = new VerticalPanel();
		Label instructions = new Label(
				TEXT_CONSTANTS.uploadPortletInstructions());
		contentPane.add(instructions);
		HorizontalPanel phPanel = new HorizontalPanel();
		phPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.devicePhoneNumber()));
		phoneNumberBox = new TextBox();
		phoneNumberBox.setWidth("100px");
		phPanel.add(phoneNumberBox);
		contentPane.add(phPanel);
		uploadWidget = new FileUploadWidget(this, "zip", "jpg", "jpeg");
		contentPane.add(uploadWidget);
		setWidget(contentPane);
	}

	@Override
	public String getName() {
		return TEXT_CONSTANTS.uploadPortletTitle();
	}

	@Override
	public void onSubmitSuccess(SubmitCompleteEvent event, String filename) {
		// create a form to submit the processing notification. This form
		// MUST be added to the contentPane before it can be submitted
		if (filename != null) {

			if (filename.toLowerCase().trim().endsWith(".zip")) {
				if (filename.contains("/")) {
					filename = filename
							.substring(filename.lastIndexOf("/") + 1);
				}
				if (filename.contains("\\")) {
					filename = filename
							.substring(filename.lastIndexOf("\\") + 1);
				}
				FormPanel tempForm = new FormPanel();
				tempForm.setMethod(FormPanel.METHOD_GET);
				tempForm.setAction(NOTIFICATION_URL);
				VerticalPanel vPanel = new VerticalPanel();
				vPanel.add(new Hidden("action", "submit"));
				vPanel.add(new Hidden("fileName", filename));
				vPanel.add(new Hidden("phoneNumber", phoneNumberBox.getText()));
				tempForm.setWidget(vPanel);
				tempForm.setVisible(false);
				contentPane.add(tempForm);
				tempForm.addSubmitCompleteHandler(new SubmitCompleteHandler() {

					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						contentPane.remove((Widget) event.getSource());
					}
				});
				tempForm.submit();
			}
		}
	}

	@Override
	public void configureUploadMap(Map<String, String> uploadMap,
			String fileName) {
		if (fileName.toLowerCase().endsWith(".zip")) {
			uploadMap.put(FileUploadHandler.PATH, S3_DATA_FILE_PATH);
			uploadMap.put(FileUploadHandler.SIG, DATA_S3_SIG);
			uploadMap.put(FileUploadHandler.POLICY, DATA_S3_POLICY);
			uploadMap.put(FileUploadHandler.CONTENT_TYPE, DATA_CONTENT_TYPE);
		} else {
			uploadMap.put(FileUploadHandler.PATH, S3_IMAGE_FILE_PATH);
			uploadMap.put(FileUploadHandler.SIG, IMAGE_S3_SIG);
			uploadMap.put(FileUploadHandler.POLICY, IMAGE_S3_POLICY);
			uploadMap.put(FileUploadHandler.CONTENT_TYPE, IMAGE_CONTENT_TYPE);
		}

	}

	@Override
	public boolean isReadyToUpload() {
		return true;
	}
}
