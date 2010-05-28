package org.waterforpeople.mapping.portal.client.widgets;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * portlet for manual upload of data zip files
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataUploadPortlet extends Portlet implements ClickHandler,
		SubmitCompleteHandler {

	public static final String NAME = "Data Upload";
	public static final String DESCRIPTION = "Manual upload of data zip files";
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private static final String NOTIFICATION_URL = "/processor";
	private static final String UPLOAD_URL = "http://waterforpeople.s3.amazonaws.com/";

	private static final String S3_ID = "1JZZVDSNFFQYF23ZYJ02";
	private static final String DATA_S3_POLICY = "eyJleHBpcmF0aW9uIjogIjIwMTAtMTAtMDJUMDA6MDA6MDBaIiwgICJjb25kaXRpb25zIjogWyAgICAgeyJidWNrZXQiOiAid2F0ZXJmb3JwZW9wbGUifSwgICAgIFsic3RhcnRzLXdpdGgiLCAiJGtleSIsICJkZXZpY2V6aXAvIl0sICAgIHsiYWNsIjogInB1YmxpYy1yZWFkIn0sICAgIHsic3VjY2Vzc19hY3Rpb25fcmVkaXJlY3QiOiAiaHR0cDovL3d3dy5nYWxsYXRpbnN5c3RlbXMuY29tL1N1Y2Nlc3NVcGxvYWQuaHRtbCJ9LCAgICBbInN0YXJ0cy13aXRoIiwgIiRDb250ZW50LVR5cGUiLCAiIl0sICAgIFsiY29udGVudC1sZW5ndGgtcmFuZ2UiLCAwLCAzMTQ1NzI4XSAgXX0=";
	private static final String DATA_S3_SIG = "7/fo9v4qamQJjnbga529k3iZMZE=";
	private static final String S3_DATA_FILE_PATH = "devicezip";
	private static final String DATA_CONTENT_TYPE = "application/zip";
	private static final String IMAGE_S3_POLICY = "eyJleHBpcmF0aW9uIjogIjIwMTAtMTAtMDJUMDA6MDA6MDBaIiwgICJjb25kaXRpb25zIjogWyAgICAgeyJidWNrZXQiOiAid2F0ZXJmb3JwZW9wbGUifSwgICAgIFsic3RhcnRzLXdpdGgiLCAiJGtleSIsICJpbWFnZXMvIl0sICAgIHsiYWNsIjogInB1YmxpYy1yZWFkIn0sICAgIHsic3VjY2Vzc19hY3Rpb25fcmVkaXJlY3QiOiAiaHR0cDovL3d3dy5nYWxsYXRpbnN5c3RlbXMuY29tL1N1Y2Nlc3NVcGxvYWQuaHRtbCJ9LCAgICBbInN0YXJ0cy13aXRoIiwgIiRDb250ZW50LVR5cGUiLCAiIl0sICAgIFsiY29udGVudC1sZW5ndGgtcmFuZ2UiLCAwLCAzMTQ1NzI4XSAgXX0=";
	private static final String IMAGE_S3_SIG = "FXBhybgyBjd+oG++KUlvq1onIDY=";
	private static final String S3_IMAGE_FILE_PATH = "images";
	private static final String IMAGE_CONTENT_TYPE = "image/jpeg";

	private Button submitBtn;
	private FormPanel form;
	private FileUpload upload;
	private Label statusLabel;
	private TextBox phoneNumberBox;
	private Hidden filePath;
	private Hidden s3Policy;
	private Hidden s3Sig;
	private Hidden contentType;

	private VerticalPanel contentPane;

	public DataUploadPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		contentPane = new VerticalPanel();
		Label instructions = new Label(
				"Manual upload of handheld data. You can export the data from the handheld's SD card and upload from your computer");
		contentPane.add(instructions);
		HorizontalPanel phPanel = new HorizontalPanel();
		phPanel.add(new Label("Device Phone #: "));
		phoneNumberBox = new TextBox();		
		phoneNumberBox.setWidth("100px");
		phPanel.add(phoneNumberBox);
		contentPane.add(phPanel);
		
		VerticalPanel tempPanel = new VerticalPanel();
		form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction(UPLOAD_URL);
		form.addSubmitCompleteHandler(this);
		filePath = new Hidden("key");

		tempPanel.add(filePath);
		tempPanel.add(new Hidden("AWSAccessKeyId", S3_ID));
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

		submitBtn = new Button("Upload");
		submitBtn.addClickHandler(this);
		contentPane.add(submitBtn);
		statusLabel = new Label();
		statusLabel.setVisible(false);
		contentPane.add(statusLabel);
		setWidget(contentPane);
	}

	/**
	 * sets the hidden variables on the form so we can do the upload
	 * 
	 * @param path
	 * @param sig
	 * @param policy
	 * @param contentType
	 */
	private void configureForm(String path, String sig, String policy,
			String contentType) {
		filePath.setValue(path + "/${filename}");
		s3Sig.setValue(sig);
		s3Policy.setValue(policy);
		this.contentType.setValue(contentType);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == submitBtn) {
			boolean valid = true;
			if (upload.getFilename() == null
					|| upload.getFilename().trim().length() == 0) {
				valid = false;
			} else if (!(upload.getFilename().toLowerCase().endsWith(".zip")
					|| upload.getFilename().toLowerCase().endsWith(".jpg") || upload
					.getFilename().toLowerCase().endsWith(".jpeg"))) {
				valid = false;
			}
			if (valid) {
				if (upload.getFilename().toLowerCase().endsWith(".zip")) {
					configureForm(S3_DATA_FILE_PATH, DATA_S3_SIG,
							DATA_S3_POLICY, DATA_CONTENT_TYPE);
				} else {
					configureForm(S3_IMAGE_FILE_PATH, IMAGE_S3_SIG,
							IMAGE_S3_POLICY, IMAGE_CONTENT_TYPE);
				}
				submitBtn.setVisible(false);
				statusLabel.setText("Uploading...");
				statusLabel.setVisible(true);
				form.submit();
			} else {
				MessageDialog dia = new MessageDialog("Error",
						"You must specify either a zip file or a jpg");
				dia.showRelativeTo(submitBtn);
			}
		}
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		submitBtn.setVisible(true);
		if (event.getResults() == null) {
			statusLabel.setText("Upload complete.");
			String filename = upload.getFilename();
			// create a form to submit the processing notification. This form
			// MUST be added to the contentPane before it can be submitted
			if (filename != null) {

				if (filename.toLowerCase().trim().endsWith(".zip")) {
					FormPanel tempForm = new FormPanel();
					tempForm.setMethod(FormPanel.METHOD_GET);
					tempForm.setAction(NOTIFICATION_URL);
					VerticalPanel vPanel = new VerticalPanel();
					vPanel.add(new Hidden("action", "submit"));
					vPanel.add(new Hidden("fileName", filename));
					vPanel.add(new Hidden("phoneNumber",phoneNumberBox.getText()));
					tempForm.setWidget(vPanel);
					tempForm.setVisible(false);
					contentPane.add(tempForm);
					tempForm
							.addSubmitCompleteHandler(new SubmitCompleteHandler() {

								@Override
								public void onSubmitComplete(
										SubmitCompleteEvent event) {
									contentPane.remove((Widget) event
											.getSource());
								}
							});
					tempForm.submit();
				}				
			}
		} else {
			statusLabel.setText("Upload error. Please try again.");
		}
	}
}
